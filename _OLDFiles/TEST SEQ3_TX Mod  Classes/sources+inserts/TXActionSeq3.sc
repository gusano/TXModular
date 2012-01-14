// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXActionSeq3 : TXModuleBase {		// Action Sequencer module 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=150;
	classvar	<guiWidth=1080;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	<seqLatency = 0.1;	// all seqs should use same small latency for server timing 
	classvar	defaultActionStep;
	
	var		<seqClock; 		// clock for sequencer
	var		<seqCurrentStep;
	var		<seqRunning = false;
	var 		holdCurrTime;
	var		holdDeltaTime, extraDeltaTime;
	var		<>jumpStep;
	var 		holdVisibleOrigin;
	var		holdScrollView;
	var		holdTapTime, newTapTime;
	var 		currentStepID;
	var		<>runningStatus;
	var		<>runningStatusView;

*initClass {
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Action Sequencer";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 0;
	noOutChannels = 0;
	defaultActionStep = [99,0,0,0,0,0,0, nil, 0, 0.0, 1, 100, 1, 1001];
		// actionStep.at(0) is ModuleID
		// actionStep.at(1) is Action Index
		// actionStep.at(2) is value 1
		// actionStep.at(3) is value 2
		// actionStep.at(4) is value 3
		// actionStep.at(5) is value 4
		// actionStep.at(6) is not used
		// actionStep.at(7) is Action Text
		// actionStep.at(8) is Select switch
		// actionStep.at(9) is Time
		// actionStep.at(10) is On switch
		// actionStep.at(11) is Probablity
		// actionStep.at(12) is Step No.
		// actionStep.at(13) is Step ID
} 

*new { arg argInstName;
	 ^super.new.init(argInstName);
} 

*restoreAllOutputs {
	 arrInstances.do({ arg item, i;
	 	item.restoreOutputs;
	 });
} 

*syncStartAllSequencers {
	 arrInstances.do({ arg item, i;
	 	item.syncStartSequencer;
	 });
} 

*syncStopAllSequencers {
	 arrInstances.do({ arg item, i;
	 	item.syncStopSequencer;
	 });
} 

*stopAllSequencers {
	 arrInstances.do({ arg item, i;
	 	item.stopSequencer;
	 });
} 

////////////////////////////////////

init {arg argInstName;

	holdVisibleOrigin = Point.new(0,0);

//	n.b. this module is using arrSynthArgSpecs just as a place to store variables for use with guiSpecArray
//  it takes advantage of the  gui objects saving values to arrSynthArgSpecs as well as it being already
//   saved and loaded with other data
//	it is only for (very lazy!) convenience, since no synths are used by this module - unlike most of the others 

	arrSynthArgSpecs = [
		["seqStartStep", 1],
		["displayFirstStep", 0],
		["syncStart", 1],
		["syncStop", 1],
		["muteSeq", 0],
		["timeLock", 1],
		["bpm", 120],
		["oldBpm", 120],
		["beatsPerBar", 4],
		["arrActionSteps", 
			defaultActionStep.deepCopy.dup
				.collect({arg item, i; item.put(12, i+1); item.put(13, i + 1001;); })],
		["holdNextStepID", 1003],
		["autoTapTempo", 0],
	]; 
		
	seqCurrentStep = 0;
	seqRunning = false;
	this.updateRunningStatus(" STOPPED");
		
	guiSpecTitleArray = [
		["TitleBar"], 
		["Spacer", 3], 
		["ActionButtonBig", "Start", {this.startSequencer}, 50, nil, TXColor.sysGuiCol2], 
		["Spacer", 3], 
		["ActionButtonBig", "Stop", {this.stopSequencer}, 50, nil, TXColor.sysGuiCol2], 
		["Spacer", 3], 
		["TXStaticText", "Status", {this.runningStatus}, 
			{arg view; runningStatusView = view.textView}, 130, 50, TXColor.paleYellow2], 
		["Spacer", 3], 
		["SeqSyncStartCheckBox"], 
		["Spacer", 3], 
		["SeqSyncStopCheckBox"], 
		["Spacer", 10], 
		["TXCheckBox", "Mute all actions", "muteSeq", nil, 200],
		["Spacer", 10], 
		["HelpButton"], 
		["Spacer", 3], 
		["DeleteModuleButton"], 
		["Spacer", 3], 
		["ModuleActionPopup"], 
		["NextLine"], 
		["ModuleInfoTxt"], 
		["SpacerLine", 2], 
	];
	guiSpecArray = [
	// not needed with scrolling
	//	["SeqNavigationButtons", {this.getSynthArgSpec("arrActionSteps").size;},  "displayFirstStep"],
	//	["Spacer", 20], 
		["TXNumberPlusMinus", "Start playing from step", ControlSpec(1, 999, 'lin', 1, 0), 
			"seqStartStep", {this.checkStartStep; system.showView(this);}, nil, 135, 35],
		["Spacer", 10], 
		["EZNumber", "BPM", ControlSpec(1, 999), "bpm", 
			{arg view; this.bpmUpdated(view.value); system.showView(this);}, 50, 50],
		["TapTempoButton", {arg argTempo; this.useTapTempo(argTempo);}],
		["TXCheckBox", "Auto copy tap tempo ", "autoTapTempo", nil, 140],
		["Spacer", 10], 
		["EZNumber", "Beats per bar", ControlSpec(1, 999), "beatsPerBar", {system.showView(this);}, 80, 30],
		["Spacer", 10], 
		["TXCheckBox", "Lock step times when BPM changes", "timeLock", nil, 230],
		["DividingLine", 1060], 
		["TXActionSteps", {this.getSynthArgSpec("arrActionSteps");}, 
			{arg argArrActionSteps;  this.setSynthArgSpec("arrActionSteps", argArrActionSteps);},
			{this.getSynthArgSpec("displayFirstStep");}, 
			{this.getSynthArgSpec("bpm");}, 
			{this.getSynthArgSpec("beatsPerBar");}, 
			{this.getNextStepID;}, 
			{arg view; holdScrollView = view;},
			{arg view; holdVisibleOrigin = view.visibleOrigin; },
			{currentStepID;},
			{arg stepID;  currentStepID = stepID;}
		],
	];
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Start Sequencer", {this.startSequencer;}],
		["commandAction", "Stop Sequencer", {this.stopSequencer;}],
		["commandAction", "Jump to step ID", {arg argStepID; this.jumpToStepID(argStepID);}, 
			[{ControlSpec(1001, this.getSynthArgSpec("holdNextStepID") - 1, step: 1)}],
		],
//		["SeqSyncStartCheckBox"], 
//		["TXCheckBox", "Mute output", "muteSeq", nil, 450],
		["TXNumberPlusMinus", "Start playing from step", ControlSpec(1, 999, 'lin', 1, 0), "seqStartStep", 
			{this.checkStartStep; }, nil, 140, 40],
		["EZNumber", "BPM", ControlSpec(1, 999), "bpm", {arg view; this.bpmUpdated(view.value); }],
		["commandAction", "Tap Tempo", {this.actionTapTempo;}],
		["TXCheckBox", "Auto copy tap tempo ", "autoTapTempo", nil, 180],
		["EZNumber", "Beats per bar", ControlSpec(1, 999), "beatsPerBar"],
		["TXCheckBox", "Lock step times when BPM changes", "timeLock", nil, 270],
		["TXStaticText", "Status", {this.runningStatus}, 
			{arg view; runningStatusView = view.textView}, 150], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
}
////////////////////////////////////

openGui{ arg argParent; 			 // override base class
	//	use base class initialise 
	this.baseOpenGui(this, argParent);
	holdScrollView.visibleOrigin = (holdVisibleOrigin);
}
	
getNextStepID {
	var outStepID;
	outStepID = this.getSynthArgSpec("holdNextStepID");
	 this.setSynthArgSpec("holdNextStepID", outStepID + 1);
	^outStepID;
}

jumpToStepID {arg argStepID;
	var arrSteps, holdStep;
	arrSteps = this.getSynthArgSpec("arrActionSteps");
	arrSteps.do({ arg item, i;
		if (item.at(13) == argStepID, { holdStep = i + 1;});
	});
	if (holdStep.notNil, {
		this.jumpStep = holdStep;
	});
}
////////////////////////////////////

checkStartStep {
	var holdStartStep;
	// check start step is within valid range
	holdStartStep = this.getSynthArgSpec("seqStartStep");
	holdStartStep = holdStartStep.min(this.getSynthArgSpec("arrActionSteps").size).max(1);
	this.setSynthArgSpec("seqStartStep", holdStartStep);
}

startSequencer { arg startStep;
	var 	holdArrActionSteps;

	// stop any old sequence running
	this.stopSequencer;
	if (	deletedStatus != true, {
		// reset variables
		this.resetSequencer(startStep);
			this.updateRunningStatus(" RUNNING");
		// start tempo clock and play sequence
		seqClock = TempoClock.new(1); 
		seqRunning = true;
		seqClock.schedAbs(seqClock.elapsedBeats,{	
			holdArrActionSteps = this.getSynthArgSpec("arrActionSteps");
			holdCurrTime = holdArrActionSteps.at(seqCurrentStep).at(9);
			// if step is switched on
			if ( (holdArrActionSteps.at(seqCurrentStep).at(10) == 1)
				// and the fates allow it
				and: ((holdArrActionSteps.at(seqCurrentStep).at(11)/100 - rand(1.0)).ceil == 1)
				// and sequencer not muted
				and: (this.getSynthArgSpec("muteSeq") == 0),
			{ 
				// run action
				this.performAction(holdArrActionSteps.at(seqCurrentStep));
			});

// n.b. allow for latency? what about with servers or with other sequencers
// see old code from seq:
//
//			if (	// if step is to be played 
//				this.getSynthArgSpec("arrOnOffSteps").at(seqCurrentStep) == 1
//					// and the fates allow it
//					and: ((this.getSynthArgSpec("arrProbabilities").at(seqCurrentStep)/100 - rand(1.0)).ceil == 1)
//					// and sequencer not muted
//					and: (this.getSynthArgSpec("muteSeq") == 0),
//			{ 
//				// use bundle to allow for latency
//	--->			system.server.makeBundle(seqLatency + outDelay, { 
//					if (noteOutModule1.notNil, {noteOutModule1.createSynthNote(outNote, outVel, outEnvTime)});
//					if (noteOutModule2.notNil, {noteOutModule2.createSynthNote(outNote, outVel, outEnvTime)});
//					if (noteOutModule3.notNil, {noteOutModule3.createSynthNote(outNote, outVel, outEnvTime)});
//					if (noteOutModule4.notNil, {noteOutModule4.createSynthNote(outNote, outVel, outEnvTime)});
//					if (noteOutModule5.notNil, {noteOutModule5.createSynthNote(outNote, outVel, outEnvTime)});
//					if (noteOutModule6.notNil, {noteOutModule6.createSynthNote(outNote, outVel, outEnvTime)});
//					// write note & velocity as control values to output busses
//					outBus.setn([(outNote/127).max(0).min(1), outVel/127]);
//				});
//			});

			extraDeltaTime = 0;
			// go to next step
			if (jumpStep.notNil, {
				// if jumping to a previous step , and time is same, add extra time to prevent blowup
				if ( ((jumpStep - 1) < seqCurrentStep) and: (holdCurrTime == holdArrActionSteps.at(jumpStep-1).at(9)), 
					{extraDeltaTime = 0.1;
				});
				seqCurrentStep = (jumpStep - 1).max(0).asInteger;
				holdCurrTime = holdArrActionSteps.at(jumpStep-1).at(9);
				jumpStep = nil;
			},{
				seqCurrentStep = (seqCurrentStep + 1);
			});

			// if past end stop sequencer
			if (seqCurrentStep > (holdArrActionSteps.size - 1), { 
				this.stopSequencer;
				holdDeltaTime = nil;
			},{
				//schedule next event by subtracting old time from new add extra time
				holdDeltaTime = (holdArrActionSteps.at(seqCurrentStep).at(9) - holdCurrTime).max(0);
				//  add extra time
				holdDeltaTime = holdDeltaTime + extraDeltaTime;
			});
			holdDeltaTime;
		});
	});
} 

syncStartSequencer { 
	// if syncStart is 1 then start sequencer
	if (this.getSynthArgSpec("syncStart") == 1, {
		this.startSequencer;
	});
} 

syncStopSequencer { 
	// if syncStop is 1 then stop sequencer
	if (this.getSynthArgSpec("syncStop") == 1, {
		this.stopSequencer;
	});
} 

resetSequencer { arg stepNo;
	// reset variables
	seqCurrentStep = (stepNo ?? this.getSynthArgSpec("seqStartStep")) - 1;
} 

stopSequencer { 
	// stop tempo clock 
	if (seqRunning == true, {
		seqClock.stop;
	});
	seqRunning = false;
	this.updateRunningStatus(" STOPPED");
} 

updateRunningStatus { arg argStatus;
	runningStatus = argStatus; 
	{
		if (runningStatusView.notNil, {
			if (runningStatusView.notClosed, {
				runningStatusView.string = runningStatus;
			});
		});
	}.defer;
} 

performAction {arg argActionStep;
	var holdModuleID, holdModule, holdActionInd, holdArrActionItems, holdActionText, 
		holdAction, holdVal1, holdVal2, holdVal3, holdVal4, holdLatency, holdGuiUpd;
	holdModuleID = argActionStep.at(0);
	holdActionInd = argActionStep.at(1);
	holdVal1 = argActionStep.at(2);
	holdVal2 = argActionStep.at(3);
	holdVal3 = argActionStep.at(4);
	holdVal4 = argActionStep.at(5);
	holdActionText = argActionStep.at(7);
	holdGuiUpd = argActionStep.at(8);
	holdModule = system.getModuleFromID(holdModuleID);
	if (holdModule != 0, {
		holdArrActionItems = holdModule.arrActionSpecs.collect({arg item, i; item.actionName;});
		// if text found, match action string with text, else use numerical value
		if (holdActionText.notNil, {
			holdActionInd = holdArrActionItems.indexOfEqual(holdActionText) ? holdActionInd;
			holdAction = holdModule.arrActionSpecs.at(holdActionInd);
		},{
			// if text not found, use number but only select older actions with legacyType == 1
			holdAction = holdModule.arrActionSpecs.select({arg item, i; item.legacyType == 1}).at(holdActionInd);
		});

		// if module is another sequencer, don't use latency (since they already use latency)
		if ((system.arrAllPossCurSeqModules ++ system.arrAllPossOldSeqModules).indexOfEqual(holdModule.class).notNil, {
			holdLatency = 0;
		},{
			holdLatency = seqLatency;
		});
		// use bundle to allow for latency
		system.server.makeBundle(seqLatency, { 
			// if action type is commandAction then value it with arguments
			if (holdAction.actionType == \commandAction, {
				holdAction.actionFunction.value(holdVal1, holdVal2, holdVal3, holdVal4);
			});
			// if action type is valueAction then value it with arguments
			if (holdAction.actionType == \valueAction, {
				holdAction.setValueFunction.value(holdVal1, holdVal2, holdVal3, holdVal4);
			});

			// gui update
			if (holdGuiUpd == 1, {
				system.flagGuiUpd;
			});
		});
	});
}

////////////////////////////////////

bpmUpdated { arg argBpm;
	var holdArrActionSteps;
	// if timelock is on, update times based on new bpm
	if (this.getSynthArgSpec("timeLock") == 0, {
		holdArrActionSteps = this.getSynthArgSpec("arrActionSteps").do({ arg item, i;
			var oldTime, newTime;
			oldTime = item.at(9);
			newTime = oldTime * (this.getSynthArgSpec("oldBpm") / argBpm);
			item.put(9, newTime);
		});
		this.setSynthArgSpec("arrActionSteps", holdArrActionSteps)
	});
	this.setSynthArgSpec("oldBpm", argBpm);
}

restoreOutputs {
// 	dummy method - not used 
} 

checkDeletions {	
// 	dummy method - not used 
}

rebuildSynth { 
	// override base class method
}

useTapTempo {arg argTempo;
	var autoBPM;
	autoBPM = this.getSynthArgSpec("autoTapTempo");
	if (autoBPM == 1,{
		if ((argTempo >= 1) and: (argTempo <= 999),{
			this.setSynthArgSpec("bpm", argTempo);
			this.bpmUpdated(argTempo);
			system.flagGuiIfModDisplay(this);
		});
	});
}

actionTapTempo {	// tap tempo function used by module action
	var holdBPM;
	if (newTapTime.isNil, {
		newTapTime = Main.elapsedTime
	}, {
		holdTapTime = Main.elapsedTime;
		holdBPM = 60 / (holdTapTime - newTapTime);
		newTapTime = holdTapTime;
		this.useTapTempo(holdBPM);
	});
}

loadExtraData {
	this.stopSequencer;
}

}

