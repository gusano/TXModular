// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSequencer2 : TXModuleBase {		// Sequencer module 

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
	classvar	<guiWidth=600;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	<seqLatency = 0.2;	// all sequencers should use same latency for server timing. keep as small as poss.
	
	var		<seqClock; 		// clock for sequencer
	var		<seqCurrentStep;
	var		<seqRunning = false;
// not used now
//	var		<seqResetCounter;
	var		<>noteOutModule1;
	var		<>noteOutModule2;
	var		<>noteOutModule3;
	var		seqRecordStep = 0;
	var		<>arrSlots;

*initClass {
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Sequencer";
	moduleRate = "control";
	moduleType = "source";
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Note Out", [0]],
		["Velocity Out", [1]],
	];	
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

init {arg argInstName;
	var holdControlSpecBPM;

//	n.b. this module is using arrSynthArgSpecs just as a place to store variables for use with guiSpecArray
//  it takes advantage of the  gui objects saving values to arrSynthArgSpecs as well as it being already
//   saved and loaded with other data
//	it is only for (very lazy!) convenience, since no synths are used by this module - unlike most of the others 

	//	set  class specific instance variables
	arrSlots = Array.fill(127, nil);
	arrSynthArgSpecs = [
		["seqBPM", 0.3979933], 		// default is set for 120 bpm
		["seqBPMMin", 1], 		
		["seqBPMMax", 300], 		
		["seqStartStep", 1],
		["seqEndStep", 16],
// not used now
//		["seqResetStep", 16],
		["syncStart", 1],
		["seqAutoLoop", 1],
		["seqNoteBase", 48],
 		["arrNotes", Array.fill(16, 0)],
		["arrRandOctaves", Array.fill(16, 0)],
		["arrVelocities", Array.fill(16, 100)],
		["arrRandVelocities", Array.fill(16, 0)],
		["velMin", 0],
		["velMax", 100],
		["arrStepLengthsX", Array.fill(16, 1)],
		["arrStepLengthsY", Array.fill(16, 2)],
		["arrProbabilities", Array.fill(16, 100)],
		["arrOnOffSteps", Array.fill(16, 1)],
		["noteOutModuleID1", nil],
		["noteOutModuleID2", nil],
		["noteOutModuleID3", nil],
		["midiLearn", 0],
		["showVelocityBars", 0],
		["showProbabilityBars", 1],
 		["description", ""],
 		["slotNo", 0],
 		["nextSlotNo", nil],
 		["displayOption", "showNotes"],
	]; 
		
	seqCurrentStep = 0;
	seqRunning = false;
// not used now
//	seqResetCounter = 0;
		
	holdControlSpecBPM = ControlSpec(1, 999);
	guiSpecTitleArray = [
		["TitleBar"], 
		["Spacer", 3], 
		["ActionButtonBig", "Start", {this.startSequencer}], 
		["Spacer", 3], 
		["ActionButtonBig", "Stop", {this.stopSequencer}], 
		["Spacer", 3], 
		["SeqSyncStartCheckBox"], 
		["Spacer", 3], 
		["DeleteModuleButton"], 
		["SpacerLine", 2], 
	];
	this.buildGuiSpecArray;
	//	use base class initialise 
	this.baseInit(this, argInstName);
}

buildGuiSpecArray {
	guiSpecArray = [
		["DividingLine"], 
		["SeqSelect3GroupModules", "noteOutModule1", "noteOutModule2", "noteOutModule3", 
			"noteOutModuleID1", "noteOutModuleID2", "noteOutModuleID3"], 
		["SpacerLine", 6], 
		["DividingLine"], 
		["TXMinMaxSliderSplit", "BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax",
			{arg view; this.setTempo(view.value/60);}], 
//		["SpacerLine", 6], 
		["DividingLine"], 
		["SpacerLine", 6], 
		["TXCheckBox", "Midi Learn Notes and Velocities", "midiLearn", {arg view; this.midiLearn(view.value);}, 250],
		["Spacer", 3], 
		["ActionButton", "Refresh Screen", {system.showView;}, 110], 
		["Spacer", 3], 
		["ActionButtonDark", "Reset All Values", {this.resetPattern; }, 150], 
		["SpacerLine", 6], 
		["ActionButton", "Notes", {this.setSynthArgSpec("displayOption", "showNotes"); 
			this.buildGuiSpecArray; system.showView;}, 100], 
		["Spacer", 3], 
		["ActionButton", "Velocities", {this.setSynthArgSpec("displayOption", "showVelocities"); 
			this.buildGuiSpecArray; system.showView;}, 100], 
		["Spacer", 3], 
		["ActionButton", "Step Lengths", {this.setSynthArgSpec("displayOption", "showStepLengths"); 
			this.buildGuiSpecArray; system.showView;}, 100], 
		["Spacer", 3], 
		["ActionButton", "Probabilities", {this.setSynthArgSpec("displayOption", "showProbabilities"); 
			this.buildGuiSpecArray; system.showView;}, 100], 
		["Spacer", 3], 
		["ActionButton", "Patterns", {this.setSynthArgSpec("displayOption", "showPatterns"); 
			this.buildGuiSpecArray; system.showView;}, 100], 
//		["SpacerLine", 6], 
		["DividingLine"], 
		["SeqStepNoTxt", 16],
		["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16],
		["SeqPlayRange", "seqStartStep", "seqEndStep", "seqAutoLoop", 16],
//		["SpacerLine", 6], 
	];
	if (this.getSynthArgSpec("displayOption") == "showNotes", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
//			["SeqStepNoTxt", 16],
			["MidiNoteRow", "seqNoteBase", "arrNotes", 16], 
			["DividingLine"], 
			["TXMultiNumber", "Rand octave", ControlSpec(-8, 8, step: 1), "arrRandOctaves", 16],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showVelocities", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
//			["SeqStepNoTxt", 16],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16], 
			["RefreshButton"],
			["DividingLine"], 
			["TXMultiSliderNo", "Velocity", ControlSpec(0, 100), "arrVelocities", 16, nil, "showVelocityBars"],
			["DividingLine"], 
			["TXMultiNumber", "Rand vel", ControlSpec(-99, 100), "arrRandVelocities", 16],
			["DividingLine"], 
			["TXRangeSlider", "Vel range", ControlSpec(0, 100), "velMin", "velMax"],
			["RefreshButton"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showStepLengths", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
//			["SeqStepNoTxt", 16],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16], 
			["DividingLine"], 
			["TXMultiNumber", "Step length X", ControlSpec(0.1, 999), "arrStepLengthsX", 16],
			["TXMultiNumber", " (in beats) / Y", ControlSpec(0.1, 999), "arrStepLengthsY", 16],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showProbabilities", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
//			["SeqStepNoTxt", 16],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16], 
			["DividingLine"], 
			["TXMultiSliderNo", "Probability", ControlSpec(0, 100), "arrProbabilities", 16, nil, "showProbabilityBars"],
			["RefreshButton"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showPatterns", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["TXSlotGui", {this.arrSlots}, {this.getSynthArgSpec("slotNo")}, 
				{arg slotNo; this.setSynthArgSpec("slotNo", slotNo)}, 
				{this.getSlotData}, {arg slotData; this.setSlotData(slotData)}, 
				{this.getSynthArgSpec("nextSlotNo")}, {arg nextNo; this.setSynthArgSpec("nextSlotNo", nextNo)}], 
			["NextLine"], 
			["TXTextBox", "Pattern desc.", "description", nil, 350], 
		];
	});
}

startSequencer { 
	var nextStepSize, outRandOcts, outAdjust, outNote, outVelMin, outVelMax, outVel, outEnvTime, holdNextNo;
	// stop any old sequence running
	this.stopSequencer;
	if (	deletedStatus != true, {
		// reset variables
		this.resetSequencer;
		// start tempo clock and play sequence
		seqClock = TempoClock.new(
			(this.getSynthArgSpec("seqBPM") 
			* (this.getSynthArgSpec("seqBPMMax") - this.getSynthArgSpec("seqBPMMin")) 
			+ this.getSynthArgSpec("seqBPMMin")
			)/ 60
		); 
		seqRunning = true;
		seqClock.schedAbs(seqClock.elapsedBeats,{	
			// get values
			outRandOcts = this.getSynthArgSpec("arrRandOctaves").at(seqCurrentStep).asInteger;
			if (outRandOcts.isPositive, 
				{outRandOcts = outRandOcts + 1; outAdjust = 0;}, 
				{outRandOcts = outRandOcts - 1; outAdjust = 12;});
			outRandOcts = outRandOcts.rand;
			outNote = this.getSynthArgSpec("seqNoteBase")
				+ this.getSynthArgSpec("arrNotes").at(seqCurrentStep)
				+ (outRandOcts * 12) + outAdjust;
			outVel = this.getSynthArgSpec("arrVelocities").at(seqCurrentStep)
				+ this.getSynthArgSpec("arrRandVelocities").at(seqCurrentStep).rand; 
			outVel = outVel.max(0).min(100);
			outVelMin = this.getSynthArgSpec("velMin");
			outVelMax = this.getSynthArgSpec("velMax");
			outVel = outVelMin + ((outVel/100) * (outVelMax-outVelMin));
			outVel = outVel * 127/ 100;
			nextStepSize = (this.getSynthArgSpec("arrStepLengthsX").at(seqCurrentStep))
				/ (this.getSynthArgSpec("arrStepLengthsY").at(seqCurrentStep));
			outEnvTime = nextStepSize * seqClock.beatDur;
			// make the noise
			if (	// if step is to be played 
				this.getSynthArgSpec("arrOnOffSteps").at(seqCurrentStep) == 1
					// and the fates allow it
					and: ((this.getSynthArgSpec("arrProbabilities").at(seqCurrentStep)/100 - rand(1.0)).ceil == 1),
			{ 
				// use bundle to allow for latency
				system.server.makeBundle(seqLatency, { 
					if (noteOutModule1.notNil, {noteOutModule1.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule2.notNil, {noteOutModule2.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule3.notNil, {noteOutModule3.createSynthNote(outNote, outVel, outEnvTime)});
					// write note & velocity as control values to output busses
					outBus.setn([(outNote/127).max(0).min(1), outVel/127]);
				});
			});
			// go to next step
			seqCurrentStep = (seqCurrentStep+1);
	// not used now
	//		seqResetCounter = (seqResetCounter+1);
			// if past end, go to start step if auto loop on, or else stop sequencer
			if (seqCurrentStep > (this.getSynthArgSpec("seqEndStep") - 1), { 
				holdNextNo = this.getSynthArgSpec("nextSlotNo");
				if (holdNextNo.notNil, {
					this.setSlotData(arrSlots.at(holdNextNo));
				});
				if ((this.getSynthArgSpec("seqAutoLoop") == 1) or: holdNextNo.notNil, {
					seqCurrentStep = this.getSynthArgSpec("seqStartStep") - 1;
				},{
					this.stopSequencer;
				});
				if (holdNextNo.notNil, {
					this.setSynthArgSpec("nextSlotNo", nil);
				});
			});
	// not used now
	//		// check for reset
	//		if (seqResetCounter > this.getSynthArgSpec("seqResetStep"), { 
	//			// reset variables
	//			this.resetSequencer;
	//		});
			nextStepSize;	//schedule next event in beats 
		});
	});
} 

syncStartSequencer { 
	// if syncStart is 1 then start sequencer
	if (this.getSynthArgSpec("syncStart") == 1, {
		this.startSequencer;
	});
} 

resetSequencer { 
	// reset variables
	seqCurrentStep = this.getSynthArgSpec("seqStartStep") - 1;
// not used now
//	seqResetCounter = 0;
} 

stopSequencer { 
	// stop tempo clock 
	if (seqRunning == true, {
		seqClock.stop;
	});
	seqRunning = false;
} 

setTempo { arg argTempo;
	if (argTempo.notNil and: (seqRunning == true), {
		if (argTempo > 0 , { seqClock.tempo = argTempo; });
	});
} 

extraSaveData {	
	^arrSlots;
}

loadExtraData {arg argData;
	if (argData.notNil, {
		arrSlots = argData.deepCopy;
	});
}

getSlotData {	
	^["seqStartStep", "seqEndStep", "seqAutoLoop", "seqNoteBase", "arrNotes", "arrRandOctaves", 
		"arrVelocities", "arrRandVelocities", "arrStepLengthsX", "arrStepLengthsY", 
		"arrProbabilities", "arrOnOffSteps", "description"
	].collect({ arg item, i;
		this.getSynthArgSpec(item);
	});
}

setSlotData {arg argSlotData;
	if (argSlotData.notNil, {
		["seqStartStep", "seqEndStep", "seqAutoLoop", "seqNoteBase", "arrNotes", "arrRandOctaves", 
			"arrVelocities", "arrRandVelocities", "arrStepLengthsX", "arrStepLengthsY", 
			"arrProbabilities", "arrOnOffSteps", "description"
		].do({ arg item, i;
			this.setSynthArgSpec(item, argSlotData.at(i));
		});
	}, {
		this.resetPattern;
	});
	// refresh view
	system.showView;
}

resetPattern {
	[	["seqStartStep", 1],
		["seqEndStep", 16],
		["seqAutoLoop", 1],
		["seqNoteBase", 48],
 		["arrNotes", Array.fill(16, 0)],
		["arrRandOctaves", Array.fill(16, 0)],
		["arrVelocities", Array.fill(16, 100)],
		["arrRandVelocities", Array.fill(16, 0)],
		["arrStepLengthsX", Array.fill(16, 1)],
		["arrStepLengthsY", Array.fill(16, 2)],
		["arrProbabilities", Array.fill(16, 100)],
		["arrOnOffSteps", Array.fill(16, 0)],
 		["description", ""]
 	].do({ arg item, i;
 		this.setSynthArgSpec(item.at(0), item.at(1));
 	});
	// refresh view
	system.showView;
} 

restoreOutputs {
 	var holdID;
 	holdID = this.getSynthArgSpec("noteOutModuleID1");
	if (holdID.notNil, {noteOutModule1 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("noteOutModuleID2");
	if (holdID.notNil, {noteOutModule2 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("noteOutModuleID3");
	if (holdID.notNil, {noteOutModule3 = system.getModuleFromID(holdID)});
} 

midiLearn { arg argSwitch = 0;
	// stop any previous routine 
 	if (midiNoteOnRoutine.class == Routine, {
 		midiNoteOnRoutine.stop; 
 	});
	// if requested start new routine 
	if (argSwitch == 1, {
		// reset variable
		seqRecordStep = 0;
		// start routine
		midiNoteOnRoutine = Routine({
			var event, holdArrNotes, holdArrVelocities, holdSeqNoteBase;
			loop {
				event = MIDIIn.waitNoteOn(nil, ((midiMinChannel-1)..(midiMaxChannel-1)) );
				holdArrNotes = this.getSynthArgSpec("arrNotes");
				holdArrVelocities = this.getSynthArgSpec("arrVelocities");
				holdSeqNoteBase  = this.getSynthArgSpec("seqNoteBase");
				// store note and velocity
				holdArrNotes.put(seqRecordStep, event.note - holdSeqNoteBase);
				holdArrVelocities.put(seqRecordStep, event.veloc * 100/127);
				this.setSynthArgSpec("arrNotes", holdArrNotes);
				this.setSynthArgSpec("arrVelocities", holdArrVelocities);
				// go to next step
				seqRecordStep = seqRecordStep + 1;
				if (seqRecordStep > 16, {seqRecordStep = 0;});
			}
		}).play;
	}, {
		// refresh view
		system.showView;
	});
} 

checkDeletions {	
	// check if any note out modules are going to be deleted - if so remove them as outputs
		if (noteOutModule1.notNil, {
			if (noteOutModule1.deletedStatus == true, {
				noteOutModule1 = nil;
				this.setSynthArgSpec("noteOutModuleID1", nil);
			});
		});
		if (noteOutModule2.notNil, {
			if (noteOutModule2.deletedStatus == true, {
				noteOutModule2 = nil;
				this.setSynthArgSpec("noteOutModuleID2", nil);
			});
		});
		if (noteOutModule3.notNil, {
			if (noteOutModule3.deletedStatus == true, {
				noteOutModule3 = nil;
				this.setSynthArgSpec("noteOutModuleID3", nil);
			});
		});
}

}

