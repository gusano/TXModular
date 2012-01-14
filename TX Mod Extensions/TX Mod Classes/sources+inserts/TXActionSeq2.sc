// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXActionSeq2 : TXModuleBase {		// Action Sequencer module 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiWidth=1080;
	classvar	<seqLatency = 0.2;	// all sequencers should use same latency for server timing. keep as small as poss.
	classvar	defaultActionStep;
	
	var		<seqClock; 		// clock for sequencer
	var		<seqCurrentStep;
	var		<seqRunning = false;
	var 		holdCurrTime;
	var		holdDeltaTime;
	var		<>jumpStep;
	

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

*stopAllSequencers {
	 arrInstances.do({ arg item, i;
	 	item.stopSequencer;
	 });
} 

////////////////////////////////////

init {arg argInstName;

//	n.b. this module is using arrSynthArgSpecs just as a place to store variables for use with guiSpecArray
//  it takes advantage of the  gui objects saving values to arrSynthArgSpecs as well as it being already
//   saved and loaded with other data
//	it is only for (very lazy!) convenience, since no synths are used by this module - unlike most of the others 

	arrSynthArgSpecs = [
		["seqStartStep", 1],
		["displayFirstStep", 0],
		["syncStart", 1],
		["muteSeq", 0],
		["timeLock", 1],
		["bpm", 120],
		["oldBpm", 120],
		["beatsPerBar", 4],
		["arrActionSteps", defaultActionStep.deepCopy.dup.collect({arg item, i; item.put(12, i+1); item.put(13, i + 1001;); })],
		["holdNextStepID", 1003],
	]; 
		
	seqCurrentStep = 0;
	seqRunning = false;
		
	guiSpecTitleArray = [
		["TitleBar"], 
		["Spacer", 3], 
		["ActionButtonBig", "Start", {this.startSequencer}, 50, nil, TXColor.sysGuiCol2], 
		["Spacer", 3], 
		["ActionButtonBig", "Stop", {this.stopSequencer}, 50, nil, TXColor.sysGuiCol2], 
		["Spacer", 3], 
		["SeqSyncStartCheckBox"], 
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
		["SeqNavigationButtons", {this.getSynthArgSpec("arrActionSteps").size;},  "displayFirstStep"],
		["Spacer", 20], 
		["TXNumberPlusMinus", "Start playing from step", ControlSpec(1, 999, 'lin', 1, 0), "seqStartStep", 
			{this.checkStartStep; system.showView;}, nil, 140, 40],
		["Spacer", 10], 
		["EZNumber", "BPM", ControlSpec(1, 999), "bpm", {arg view; this.bpmUpdated(view.value); system.showView;}],
		["EZNumber", "Beats per bar", ControlSpec(1, 999), "beatsPerBar", {system.showView;}],
		["TXCheckBox", "Keep times locked when BPM changes", "timeLock", nil, 250],
		["DividingLine", 990], 
		["TXActionSteps", {this.getSynthArgSpec("arrActionSteps");}, 
			{arg argArrActionSteps;  this.setSynthArgSpec("arrActionSteps", argArrActionSteps);},
			{this.getSynthArgSpec("displayFirstStep");}, 
			{this.getSynthArgSpec("bpm");}, 
			{this.getSynthArgSpec("beatsPerBar");}, 
			{this.getNextStepID;}, 
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
			{this.checkStartStep; system.showView;}, nil, 140, 40],
		["EZNumber", "BPM", ControlSpec(1, 999), "bpm", {arg view; this.bpmUpdated(view.value); }],
		["EZNumber", "Beats per bar", ControlSpec(1, 999), "beatsPerBar"],
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
}
////////////////////////////////////

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

// n.b. do we need to allow for latency? what about with servers or with other sequencers
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


			// go to next step
			if (jumpStep.notNil, {
				seqCurrentStep = (jumpStep - 1).max(0).asInteger;
				holdCurrTime = holdArrActionSteps.at(jumpStep).at(9);
				jumpStep = nil;
			},{
				seqCurrentStep = (seqCurrentStep + 1);
			});

			// if past end stop sequencer
			if (seqCurrentStep > (holdArrActionSteps.size - 1), { 
				this.stopSequencer;
				holdDeltaTime = nil;
			},{
				//schedule next event by subtracting old time from new 
				holdDeltaTime = (holdArrActionSteps.at(seqCurrentStep).at(9) - holdCurrTime).max(0);
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

}

