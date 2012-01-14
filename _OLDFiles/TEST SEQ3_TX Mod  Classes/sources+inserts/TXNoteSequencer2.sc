// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXNoteSequencer2 : TXModuleBase {		// Sequencer module 

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
	var		<>noteOutModule4;
	var		<>noteOutModule5;
	var		<>noteOutModule6;
	var		seqRecordStep = 0;
	var		<>arrSlots;
	var		arrParmNames;
	var		<>parmIndex = 0;
	var		<>displayOctave = 4;
	var		<arrProcessSpecs;	// used for processing
	var		arrProcSelNames;
	var		arrProcSynthArgNames;
	var		arrProcSelGroupNames;
	var		arrProcGroupSynthArgNames;
	var 		loTargetIndex, hiTargetIndex, targetSize; 
	var		slotClipboard;
	var		arrSeqRangePresets;
	var		arrBPMRangePresets;
	var		arrStepNoteLenPresets;
	var		holdKeybOctaves;
	var 		arrScrollViewsH, arrScrollViewsV, arrScrollViewsVH;
	var 		holdVisibleOrigin;

*initClass {
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Note Sequencer";
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

////////////////////////////////////////////////////////////////////////////

init {arg argInstName;
	var holdControlSpecBPM;

//	n.b. this module is using arrSynthArgSpecs just as a place to store variables for use with guiSpecArray
//  it takes advantage of the  gui objects saving values to arrSynthArgSpecs as well as it being already
//   saved and loaded with other data
//	it is only for (very lazy!) convenience, since no synths are used by this module - unlike most of the others 

	//	set  class specific instance variables
	holdVisibleOrigin = Point.new(0,480);
	arrSeqRangePresets = [
		["Presets: ", []],
		["1 - 64", [1, 64]],
		["1 - 4", [1, 4]],
		["1 - 6", [1, 6]],
		["1 - 8", [1, 8]],
		["1 - 9", [1, 9]],
		["1 - 12", [1, 12]],
		["1 - 16", [1, 16]],
		["1 - 24", [1, 24]],
		["1 - 32", [1, 32]],
		["5 - 64", [5, 64]],
		["9 - 64", [9, 64]],
		["17 - 32", [17, 32]],
		["17 - 64", [17, 64]],
		["33 - 48", [33, 48]],
		["49 - 64", [49, 64]],
	];
	arrBPMRangePresets = [
		["Presets: ", []],
		["1 - 300", [1, 300]],
		["60 - 170", [60, 170]],
		["60 - 90", [60, 90]],
		["70 - 100", [70, 100]],
		["80 - 110", [80, 110]],
		["90 - 120", [90, 120]],
		["100 - 130", [100, 130]],
		["110 - 140", [110, 140]],
		["120 - 150", [120, 150]],
		["130 - 160", [120, 160]],
		["140 - 170", [140, 170]],
		["150 - 180", [150, 180]],
		["160 - 190", [160, 190]],
	];
	arrStepNoteLenPresets = [
		["Select preset to set step & notes length values", []],
		["   16 | 1", [16, 1]],
		["   12 | 1", [12, 1]],
		["   9  | 1", [9, 1]],
		["   8  | 1", [8, 1]],
		["   7  | 1", [7, 1]],
		["   6  | 1", [6, 1]],
		["   5  | 1", [5, 1]],
		["   4  | 1", [4, 1]],
		["   3  | 1", [3, 1]],
		["   2  | 1", [2, 1]],
		["   1  | 1", [1, 1]],
		["   1  | 2", [1, 2]],
		["   1  | 3", [1, 3]],
		["   1  | 4", [1, 4]],
		["   1  | 5", [1, 5]],
		["   1  | 6", [1, 6]],
		["   1  | 7", [1, 7]],
		["   1  | 8", [1, 8]],
		["   1  | 9", [1, 9]],
		["   1  | 12", [1, 12]],
		["   1  | 16", [1, 16]],
	];
	this.initProcessSpecs;
	arrSlots = Array.fill(100, nil);
	arrSynthArgSpecs = [
		["seqBPM", 0.3979933], 		// default is set for 120 bpm
		["seqBPMMin", 1], 		
		["seqBPMMax", 300], 		
		["seqStartStep", 1],
		["seqEndStep", 16],
		[ "displayFirstStep", 0],
// not used now
//		["seqResetStep", 64],
		["syncStart", 1],
		["syncStop", 1],
		["seqNoteBase", 48],
 
 		["arrNotes", Array.fill(64, 0)],
		["arrRandOctaves", Array.fill(64, 0)],
		["arrVelocities", Array.fill(64, 100)],
		["arrRandVelocities", Array.fill(64, 0)],
		["velMin", 0],
		["velMax", 100],
		["arrStepLengthsX", Array.fill(64, 1)],
		["arrStepLengthsY", Array.fill(64, 2)],
		["arrNoteLengthsX", Array.fill(64, 1)],
		["arrNoteLengthsY", Array.fill(64, 2)],
		["arrProbabilities", Array.fill(64, 100)],
		["arrOnOffSteps", Array.fill(64, 1)],
		["arrDelays", Array.fill(64, 0)],
		["arrRandDelays", Array.fill(64, 0)],
		["arrGroup1s", Array.fill(64, 0)],
		["arrGroup2s", Array.fill(64, 0)],
		["arrGroup3s", Array.fill(64, 0)],
		["arrGroup4s", Array.fill(64, 0)],
		["arrGroup5s", Array.fill(64, 0)],
		["arrGroup6s", Array.fill(64, 0)],
		["arrGroup7s", Array.fill(64, 0)],
		["arrGroup8s", Array.fill(64, 0)],

		["noteOutModuleID1", nil],
		["noteOutModuleID2", nil],
		["noteOutModuleID3", nil],
		["noteOutModuleID4", nil],
		["noteOutModuleID5", nil],
		["noteOutModuleID6", nil],

		["midiLearn", 0],
		["showRandOctBars", 0],
		["showVelocityBars", 1],
		["showRandVelBars", 1],
		["showProbabilityBars", 1],
		["showDelayBars", 1],
		["showRandDelayBars", 1],
 		["description", ""],
 		["chainStartStep", 1],
		["chainEndStep", 1],
		["chainCurrentStep", 1],
		["chainLoop", 1],
		["arrChainSlots", Array.fill(100, 0)],
		["displayFirstChainStep", 0],
 		["slotNo", 0],
 		["displayOption", "showGlobal"],
		["nextPatternInd", 1],
		["muteSeq", 0],
		["randTrigger", 0],
		["randomStepOrder", 0],
		["stepOrderArr", Array.series(64)],
		["stepNoteLenPresetInd", 0],

		["processTypeInd", 0],
		["procTargStart", 1],
		["procTargEnd", 64],
		["procCopyTargStart", 17],
		["procCopyTargEnd", 64],
		["procSourceStart", 1],
		["procSourceEnd", 16],
		["procShiftSteps", 1],
		["procOctTranspMin", 0],
		["procOctTranspMax", 0],
		["procScaleTypeInd", 0],
		["procScaleRoot", 48],
		["procNoteOrderInd", 0],
		["procTranspose", 1],

 		["procSelOnOffSteps", 1],
		["procSelNotes", 1],
		["procSelRandOctaves", 1],
		["procSelVelocities", 1],
		["procSelRandVelocities", 1],
		["procSelStepLengthsX", 1],
		["procSelStepLengthsY", 1],
		["procSelNoteLengthsX", 1],
		["procSelNoteLengthsY", 1],
		["procSelProbabilities", 1],
		["procSelDelays", 1],
		["procSelRandDelays", 1],
		["procSelGroup1s", 1],
		["procSelGroup2s", 1],
		["procSelGroup3s", 1],
		["procSelGroup4s", 1],
		["procSelGroup5s", 1],
		["procSelGroup6s", 1],
		["procSelGroup7s", 1],
		["procSelGroup8s", 1],
		
		["procRandOnProb", 50],
		["procRandNoteMin", 24],
		["procRandNoteMax", 108],
		["procRandVelMin", 0],
		["procRandVelMax", 100],
		["procRandStepXMin", 1],
		["procRandStepXMax", 4],
		["procRandStepYMin", 1],
		["procRandStepYMax", 4],
		["procRandNoteXMin", 1],
		["procRandNoteXMax", 4],
		["procRandNoteYMin", 1],
		["procRandNoteYMax", 4],
		["procRandProbMin", 0],
		["procRandProbMax", 100],
		["procRandDelayMin", 0],
		["procRandDelayMax", 100],
	]; 
		
	seqCurrentStep = 0;
	seqRunning = false;
// not used now
//	seqResetCounter = 0;
		
	holdControlSpecBPM = ControlSpec(1, 999);
	guiSpecTitleArray = [
		["TitleBar"], 
		["ActionButtonBig", "Start", {this.startSequencer}, 50, nil, TXColor.sysGuiCol2], 
		["ActionButtonBig", "Stop", {this.stopSequencer}, 50, nil, TXColor.sysGuiCol2], 
		["SeqSyncStartCheckBox"], 
		["SeqSyncStopCheckBox"], 
		["HelpButton"], 
		["DeleteModuleButton"], 
		["ModuleActionPopup"], 
		["NextLine"], 
		["ModuleInfoTxt"], 
		["SpacerLine", 2], 
	];
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Start Sequencer", {this.startSequencer;}],
		["commandAction", "Stop Sequencer", {this.stopSequencer;}],
		["TXMinMaxSliderSplit", "BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax",
			{arg view; this.setTempo(view.value/60);}], 
		["TXCheckBox", "Midi learn notes and velocities", "midiLearn", {arg view; this.midiLearn(view.value);}, 450],
		["EZNumber", "Note base", ControlSpec(0, 127, 'lin', 1, 48), "seqNoteBase"],		
		["TXCheckBox", "Mute output", "muteSeq", nil, 450],
		["TXCheckBox", "Use Random trigger - based on BPM", "randTrigger", nil, 450],
		["TXCheckBox", "Randomise step order every cycle", "randomStepOrder", {this.rebuildStepOrderArr;}, 450],

		["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Play steps", 
			{this.updateSlot; system.flagGuiIfModDisplay(this);}],
		["TXRangeSlider", "Vel range", ControlSpec(0, 100), "velMin", "velMax", {this.updateSlot;}],
		["TXNumberPlusMinus", "Pattern slot", ControlSpec(0, 99, step: 1), "slotNo", 
			{this.setSlotData(arrSlots.at(this.getSynthArgSpec("slotNo"))); 
				this.updateCurrentChainStep;
				system.showViewIfModDisplay(this);
			}, 
			[-10,-1,1,10],
			nil,
			nil,
			false  // don't allow scrolling
		],
		["valueActionCheckBox", "Set Group Step on-off : value + group", 
			[ControlSpec(0, 1, step: 1), ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrOnOffSteps");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrOnOffSteps");}],
		["valueActionNumber", "Set Group Note: value, group", [ControlSpec(0, 127, step: 1), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrNotes");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrNotes");}],
		["valueActionNumber", "Set Group Rand octave: value, group", [ControlSpec(-4, 4, step: 1),
			 ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrRandOctaves");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrRandOctaves");}],
		["valueActionNumber", "Set Group Velocity: value, group", [ControlSpec(0, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrVelocities");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrVelocities");}],
		["valueActionNumber", "Set Group Rand vel: value, group", [ControlSpec(-99, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrRandVelocities");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrRandVelocities");}],
		["valueActionNumber", "Set Group Step length X: value, group", [ControlSpec(0.1, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrStepLengthsX");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrStepLengthsX");}],
		["valueActionNumber", "Set Group Step length Y: value, group", [ControlSpec(0.1, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrStepLengthsY");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrStepLengthsY");}],
		["valueActionNumber", "Set Group Note length X: value, group", [ControlSpec(0.1, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrNoteLengthsX");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrNoteLengthsX");}],
		["valueActionNumber", "Set Group Note length Y: value, group", [ControlSpec(0.1, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrNoteLengthsY");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrNoteLengthsY");}],
		["valueActionNumber", "Set Group Probability: value, group", [ControlSpec(0, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrProbabilities");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrProbabilities");}],
		["valueActionNumber", "Set Group Delay: value, group", [ControlSpec(0, 100), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrDelays");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrDelays");}],
		["valueActionNumber", "Set Group Rand delay: value, group", [ControlSpec(-99, 100, default: 0), 
			ControlSpec(1, 8, step: 1, default: 1)],
			{arg argGroup; this.getSynthArgValByGroup(argGroup, "arrRandDelays");}, 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrRandDelays");}],

	// commandAction 
		// arguments- index1 is action name, index2 is action function, 
		//   index3 is array of controlspec functions
		["commandAction", "Step on-off: value + group", 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrOnOffSteps");}, 
			[ControlSpec(0, 1, step: 1), ControlSpec(1, 8, step: 1, default: 1)],],
		["commandAction", "Set Group Note: value, group", 
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrNotes");}, 
			[ControlSpec(0, 127, step: 1), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Rand octave: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrRandOctaves");}, 
			[ControlSpec(-4, 4, step: 1), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Velocity: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrVelocities");}, 
			[ControlSpec(0, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Rand vel: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrRandVelocities");}, 
			[ControlSpec(-99, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Step length X: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrStepLengthsX");}, 
			[ControlSpec(0.1, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Step length Y: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrStepLengthsY");}, 
			[ControlSpec(0.1, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Note length X: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrNoteLengthsX");}, 
			[ControlSpec(0.1, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Note length Y: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrNoteLengthsY");}, 
			[ControlSpec(0.1, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Probability: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrProbabilities");}, 
			[ControlSpec(0, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Delay: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrDelays");}, 
			[ControlSpec(0, 100), ControlSpec(1, 8, step: 1, default: 1)]],
		["commandAction", "Set Group Rand delay: value, group",
			{arg argVal, argGroup; this.setSynthArgValByGroup(argVal, argGroup, "arrRandDelays");}, 
			[ControlSpec(-99, 100), ControlSpec(1, 8, step: 1, default: 1)]],
	]);	
	this.buildGuiSpecArray;
	//	use base class initialise 
	this.baseInit(this, argInstName);
}

////////////////////////////////////////////////////////////////////////////

buildGuiSpecArray {
	// reset variables
	this. resetScrollViewArrays;
	
	guiSpecArray = [
		["ActionButton", "Global", {this.setSynthArgSpec("displayOption", "showGlobal"); 
			this.buildGuiSpecArray; system.showView;}, 105, 
			TXColor.white, this.getButtonColour(this.getSynthArgSpec("displayOption") == "showGlobal")], 
		["Spacer", 3], 
		["ActionButton", "Piano Roll", {this.setSynthArgSpec("displayOption", "showPianoroll"); 
			this.buildGuiSpecArray; system.showView;}, 105, 
			TXColor.white, this.getButtonColour(this.getSynthArgSpec("displayOption") == "showPianoroll")], 
		["Spacer", 3], 
		["ActionButton", "Groups", {this.setSynthArgSpec("displayOption", "showGroups"); 
			this.buildGuiSpecArray; system.showView;}, 105, 
			TXColor.white, this.getButtonColour(this.getSynthArgSpec("displayOption") == "showGroups")], 
		["Spacer", 3], 
		["ActionButton", "Patterns", {this.setSynthArgSpec("displayOption", "showPatterns"); 
			this.buildGuiSpecArray; system.showView;}, 105, 
			TXColor.white, this.getButtonColour(this.getSynthArgSpec("displayOption") == "showPatterns")], 
		["Spacer", 3], 
		["ActionButton", "Processes", {this.setSynthArgSpec("displayOption", "showProcesses"); 
			this.buildGuiSpecArray; system.showView;}, 105, 
			TXColor.white, this.getButtonColour(this.getSynthArgSpec("displayOption") == "showProcesses")], 
//		["DividingLine", 500], 
//		["SpacerLine", 1], 
	];
	if (this.getSynthArgSpec("displayOption") == "showGlobal", {
		guiSpecArray = guiSpecArray ++[
//			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["SeqScrollStep", 64, {arg view; this.addScrollViewH(view);}, 
				{arg view; this.updateScrollOrigin(view.visibleOrigin)}],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep", 
				{"Pattern" + this.getSynthArgSpec("slotNo");}, "seqStartStep", "seqEndStep"], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Play steps", 
				{this.updateSlot; system.flagGuiIfModDisplay(this);}, nil, arrSeqRangePresets],
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXMinMaxSliderSplit", "BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax",
				{arg view; this.setTempo(view.value/60);}, arrBPMRangePresets ], 
			["Spacer", 3], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TextBarLeft", "Modules to be triggered directly by the sequencer:"],
			["SeqSelect3GroupModules", "noteOutModule1", "noteOutModule2", "noteOutModule3", 
				"noteOutModuleID1", "noteOutModuleID2", "noteOutModuleID3"], 
			["SeqSelect3GroupModules", "noteOutModule4", "noteOutModule5", "noteOutModule6", 
				"noteOutModuleID4", "noteOutModuleID5", "noteOutModuleID6"], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXCheckBox", "Mute output (sequencer still runs but nothing is output)", "muteSeq", nil, 450],
			["TXCheckBox", "Use Random trigger - based on BPM", "randTrigger", nil, 450],
			["TXCheckBox", "Randomise step order every cycle", "randomStepOrder", {this.rebuildStepOrderArr;}, 450],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showPianoroll", {
		arrParmNames = ["notes", "velocity", "random velocity", "step, note length", "probability", 
			"delay", "random delay", "random octave"
	//		"chords, scales" // add in future - see below
			];
		if (parmIndex == 0, {holdKeybOctaves = 3;}, {holdKeybOctaves = 2;});
		guiSpecArray = guiSpecArray ++[
//			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],			["SeqScrollStep", 64, {arg view; this.addScrollViewH(view);}, 
				{arg view; this.updateScrollOrigin(view.visibleOrigin)}],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 64, {this.updateSlot;}, "displayFirstStep", 
				{arg view; this.addScrollViewH(view);}],
			["MidiNoteText", "seqNoteBase", "arrNotes", 64, "displayFirstStep", 
				{"Pattern" + this.getSynthArgSpec("slotNo");}, "seqStartStep", "seqEndStep", 
					{arg view; this.addScrollViewH(view);}], 
			["TXMidiNoteKeybGrid", "seqNoteBase", "arrNotes", 64, "displayFirstStep", {this.updateSlot;}, 
				{this.displayOctave.asInteger}, {arg argOctave; this.displayOctave_(argOctave)},
				holdKeybOctaves, arrParmNames, {this.parmIndex.asInteger}, 
				{arg argIndex; this.parmIndex_(argIndex); this.buildGuiSpecArray; system.showView;},
				{arg arrViews; this.addScrollViewV(arrViews[0]); this.addScrollViewVH(arrViews[1]);},
				{arg view; this.updateScrollOrigin(view.visibleOrigin)}
			 ], 
		];
		guiSpecArray = guiSpecArray ++[
			[
			//	["NextLine"],
				["TextBar", "MIDI Learn", 80, nil, nil, nil, \right],
				["TXCheckBox", "MIDI learn notes/velocities - starting at step 1", 
					"midiLearn", {arg view; this.midiLearn(view.value);}, 320],
			],
			[
				["TXMultiSliderNo", "Velocity", ControlSpec(0, 100), "arrVelocities", 64, {this.updateSlot;}, 
					"showVelocityBars", "displayFirstStep", nil, nil, {arg view; this.addScrollViewH(view);}],
				["TXRangeSlider", "Vel o/p range", ControlSpec(0, 100), "velMin", "velMax", {this.updateSlot;}],
			],
			[
				["TXMultiSliderNo", "Rand vel", ControlSpec(-99, 100), "arrRandVelocities", 64, {this.updateSlot;}, 
					"showRandVelBars", "displayFirstStep", nil, nil, {arg view; this.addScrollViewH(view);}],
			],
			[
				["TXMultiNumber", "Step length X", ControlSpec(0.1, 100), "arrStepLengthsX", 64, 
					{this.updateSlot;}, "displayFirstStep", {arg view; this.addScrollViewH(view);}],
				["TXMultiNumber", " (in beats) / Y", ControlSpec(0.1, 100), "arrStepLengthsY", 64, 
					{this.updateSlot;}, "displayFirstStep", {arg view; this.addScrollViewH(view);}],
				["SpacerLine", 1], 
				["TXMultiNumber", "Note length X", ControlSpec(0.1, 100), "arrNoteLengthsX", 64, 
					{this.updateSlot;}, "displayFirstStep", {arg view; this.addScrollViewH(view);}],
				["TXMultiNumber", " (in beats) / Y", ControlSpec(0.1, 100), "arrNoteLengthsY", 64, 
					{this.updateSlot;}, "displayFirstStep", {arg view; this.addScrollViewH(view);}],
				["SpacerLine", 1], 
				["TXPopupAction", "Presets", arrStepNoteLenPresets.collect({arg item, i; item.at(0);}), 
					"stepNoteLenPresetInd", 
					{this.runStepNoteLenPreset; system.showView;}, 400],
			],
			[
				["TXMultiSliderNo", "Probability", ControlSpec(0, 100), "arrProbabilities", 64, {this.updateSlot;},
					 "showProbabilityBars", "displayFirstStep", nil, nil, {arg view; this.addScrollViewH(view);}],
			],
			[
				["TXMultiSliderNo", "Delays", ControlSpec(0, 100), "arrDelays", 64, {this.updateSlot;},
					 "showDelayBars", "displayFirstStep", nil, nil, {arg view; this.addScrollViewH(view);}],
				["TextBarLeft", "Note: maximum delay is 50% of step length"],
			],
			[
				["TXMultiSliderNo", "Rand delay", ControlSpec(-99, 100), "arrRandDelays", 64, {this.updateSlot;}, 
					 "showRandDelayBars", "displayFirstStep", nil, nil, {arg view; this.addScrollViewH(view);}],
				["TextBarLeft", "Note: maximum delay is 50% of step length"],
			],
			[
				["TXMultiSliderNo", "Rand octave", ControlSpec(-4, 4, step: 1), "arrRandOctaves", 64, {this.updateSlot;},
					"showRandOctBars", "displayFirstStep", 1, nil, {arg view; this.addScrollViewH(view);}],
			],
// add in future - so that scales can be shown on midi keyboard
//			[
//				["TXPopupAction", "Chord/ scale", TXScale.arrScaleNames, "xxxxxxx", nil, 400], 
//				["NextLine"],
//				["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", "F#", "G", "G#", "A", "A#", "B"], 
//					"yyyyyyyyy", nil, 140], 
//			],
		].at(parmIndex);
	});

//////////////////////////////////////////////////////////////////////////////// 

	if (this.getSynthArgSpec("displayOption") == "showGroups", {
		guiSpecArray = guiSpecArray ++[
			["SeqScrollStep", 64, {arg view; this.addScrollViewH(view);}, 
				{arg view; this.updateScrollOrigin(view.visibleOrigin)}],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep", 
				{"Pattern" + this.getSynthArgSpec("slotNo");}, "seqStartStep", "seqEndStep"], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXMultiSwitch", "Group 1", "arrGroup1s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 2", "arrGroup2s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 3", "arrGroup3s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 4", "arrGroup4s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 5", "arrGroup5s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 6", "arrGroup6s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 7", "arrGroup7s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
			["TXMultiSwitch", "Group 8", "arrGroup8s", 16, {this.updateSlot;}, "displayFirstStep"],
			["DividingLine", 500], 
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showPatterns", {
		guiSpecArray = guiSpecArray ++[
			["SeqScrollStep", 64, {arg view; this.addScrollViewH(view);}, 
				{arg view; this.updateScrollOrigin(view.visibleOrigin)}],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep", 
				{"Pattern" + this.getSynthArgSpec("slotNo");}, "seqStartStep", "seqEndStep"], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Play steps", 
				{this.updateSlot; system.flagGuiIfModDisplay(this);}, nil, arrSeqRangePresets],
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXMinMaxSliderSplit", "BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax",
				{arg view; this.setTempo(view.value/60);}, arrBPMRangePresets ], 
			["Spacer", 3], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXTextBox", "Pattern desc.", "description", nil, 350], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXNumberPlusMinus", "Pattern slot", ControlSpec(0, 99, step: 1), "slotNo", 
				{this.setSlotData(arrSlots.at(this.getSynthArgSpec("slotNo"))); 
					this.updateCurrentChainStep;
					system.showViewIfModDisplay(this);				}, 
				[-10,-1,1,10]],
			["ActionButton", "Copy pattern", {slotClipboard = this.getSlotData.deepCopy;}, 80],
			["ActionButton", "Paste pattern", {if (slotClipboard.notNil, 
				{this.setSlotData(slotClipboard.deepCopy)}); this.updateSlot; system.showView;}, 80],
			["ActionButtonDark", "Reset all values", {this.resetPattern; this.updateSlot;}, 100], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXPopupAction", "Next pattern", ["None", "Repeat current pattern", "Use pattern chain"],
				 "nextPatternInd", {this.buildGuiSpecArray; system.showView;}, 350],
		];
		// if relevant add chain gui specs
		if (this.getSynthArgSpec("nextPatternInd") == 2, {
			guiSpecArray = guiSpecArray ++ [
				["NextLine"], 
				["TextBarLeft", "Pattern Chain: press a chain step to display pattern in slot"],
				["SpacerLine", 1], 
				["SeqPlayRange", "chainStartStep", "chainEndStep", "chainLoop", 64, "Chain steps", 
					{this.setSynthArgSpec("displayFirstChainStep", 0); this.constrainChainStep; system.showView; },
					false, 
				],
				["NextLine"], 
				["SpacerLine", 1], 
				["SeqSelectChainStep", 16,"displayFirstChainStep", "chainCurrentStep", "chainStartStep", 
					"chainEndStep", "arrChainSlots", {this.updateSlotNo; system.showView;}
				],
			];
		});
	});
	if (this.getSynthArgSpec("displayOption") == "showProcesses", {
		guiSpecArray = guiSpecArray ++[
			["SeqScrollStep", 64, {arg view; this.addScrollViewH(view);}, 
				{arg view; this.updateScrollOrigin(view.visibleOrigin)}],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep", 
				{"Pattern" + this.getSynthArgSpec("slotNo");}, "seqStartStep", "seqEndStep"], 
			["DividingLine", 500], 
			["SpacerLine", 1], 
			["TXPopupAction", "Process", arrProcessSpecs.collect({arg item, i; item.at(0);}), "processTypeInd", 
				{this.buildGuiSpecArray; system.showView;}, 350],
			["ActionButton", "Run Process", {arrProcessSpecs.at(this.getSynthArgSpec("processTypeInd")).at(1).value; 
				this.updateSlot; system.showView;}, 90, nil, TXColour.sysGuiCol2],
			["ActionButton", "Show All Processes", {this.setSynthArgSpec("processTypeInd", 0);
				this.buildGuiSpecArray; system.showView;}, 130, nil, TXColour.sysGuiCol1],
			["SpacerLine", 1], 
			["NextLine"], 
		];
		// add processing gui specs
		guiSpecArray = guiSpecArray ++ arrProcessSpecs.at(this.getSynthArgSpec("processTypeInd")).at(2);
	});
}

openGui{ arg argParent; 			 // override base class
	this.resetScrollViewArrays;
	//	use base class initialise 
	this.baseOpenGui(this, argParent);
	this.updateScrollOrigin(holdVisibleOrigin);
}
	
//////////////////////////////////// scroll view syncing

resetScrollViewArrays { 
	arrScrollViewsH = [];
	arrScrollViewsV = [];
	arrScrollViewsVH = [];
}

addScrollViewH { arg view;
	arrScrollViewsH = arrScrollViewsH.asArray.add(view);
}

addScrollViewV { arg view;
	arrScrollViewsV = arrScrollViewsV.asArray.add(view);
}

addScrollViewVH { arg view;
	arrScrollViewsVH = arrScrollViewsVH.asArray.add(view);
}

updateScrollOrigin {arg argOrigin;
	holdVisibleOrigin = argOrigin;
	arrScrollViewsH.do({arg item, i; item.visibleOrigin = (argOrigin.x @ item.visibleOrigin.y); item.refresh;});
	arrScrollViewsV.do({arg item, i; item.visibleOrigin = (item.visibleOrigin.x @ argOrigin.y); item.refresh;});
	arrScrollViewsVH.do({arg item, i; item.visibleOrigin = (argOrigin.x @ argOrigin.y);  item.refresh;});
}

//////////////////////////////////// 


runStepNoteLenPreset {
	var presetIndex, arrNewVals;
	presetIndex = this.getSynthArgSpec("stepNoteLenPresetInd") ? 0;
	if (presetIndex > 0, {
		arrNewVals = arrStepNoteLenPresets.at(presetIndex).at(1);
		this.setSynthArgSpec("arrStepLengthsX", arrNewVals.at(0).dup(64));
		this.setSynthArgSpec("arrStepLengthsY", arrNewVals.at(1).dup(64));
		this.setSynthArgSpec("arrNoteLengthsX", arrNewVals.at(0).dup(64));
		this.setSynthArgSpec("arrNoteLengthsY", arrNewVals.at(1).dup(64));
		this.updateSlot;
		this.setSynthArgSpec("stepNoteLenPresetInd", 0);
	});
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
}

getCurrentBpm {
	^  (this.getSynthArgSpec("seqBPM") 
		* (this.getSynthArgSpec("seqBPMMax") - this.getSynthArgSpec("seqBPMMin")) 
	+ this.getSynthArgSpec("seqBPMMin"));
}

startSequencer { 
	var 	holdStepNo, nextStepSize, envSize, outRandOcts, outAdjust, outNote, outVelMin, outVelMax, outVel, 
		outEnvTime, outDelay, randDelay, stepDelay;
	var holdChainCurrentStep, stopSeqFlag, holdSlotNo;
	// stop any old sequence running
	this.stopSequencer;
	if (	deletedStatus != true, {
		// reset variables
		this.resetSequencer;
		this.rebuildStepOrderArr;
		// start tempo clock and play sequence
		seqClock = TempoClock.new(
			this.getCurrentBpm / 60;
		); 
		seqRunning = true;
		seqClock.schedAbs(seqClock.elapsedBeats,{	
			// get values
			holdStepNo = this.getSynthArgSpec("stepOrderArr").at(seqCurrentStep);
			outRandOcts = this.getSynthArgSpec("arrRandOctaves").at(holdStepNo).asInteger;
			if (outRandOcts.isPositive, 
				{outRandOcts = outRandOcts + 1; outAdjust = 0;}, 
				{outRandOcts = outRandOcts - 1; outAdjust = 12;});
			outRandOcts = outRandOcts.rand;
			outNote = this.getSynthArgSpec("seqNoteBase")
				+ this.getSynthArgSpec("arrNotes").at(holdStepNo)
				+ (outRandOcts * 12) + outAdjust;
			outVel = this.getSynthArgSpec("arrVelocities").at(holdStepNo)
				+ this.getSynthArgSpec("arrRandVelocities").at(holdStepNo).rand; 
			outVel = outVel.max(0).min(100);
			outVelMin = this.getSynthArgSpec("velMin");
			outVelMax = this.getSynthArgSpec("velMax");
			outVel = outVelMin + ((outVel/100) * (outVelMax-outVelMin));
			outVel = outVel * 127/ 100;
			// if random triggering, randomise stepsize, and don't use step delay
			if (this.getSynthArgSpec("randTrigger") == 0, {
				nextStepSize = (this.getSynthArgSpec("arrStepLengthsX").at(holdStepNo))
					/ (this.getSynthArgSpec("arrStepLengthsY").at(holdStepNo));
				randDelay = this.getSynthArgSpec("arrRandDelays").at(holdStepNo).rand;
				stepDelay = this.getSynthArgSpec("arrDelays").at(holdStepNo);
				outDelay = (stepDelay + randDelay).max(0).min(100)  * nextStepSize * (60/ this.getCurrentBpm) / 200;
			},{
				nextStepSize = (seqClock.beatDur * 2).rand;
				outDelay = 0;
			});
			envSize = (this.getSynthArgSpec("arrNoteLengthsX").at(holdStepNo))
				/ (this.getSynthArgSpec("arrNoteLengthsY").at(holdStepNo));
			outEnvTime = envSize * seqClock.beatDur;
			
			// make the noise
			if (	// if step is to be played 
				this.getSynthArgSpec("arrOnOffSteps").at(holdStepNo) == 1
					// and the fates allow it
					and: ((this.getSynthArgSpec("arrProbabilities").at(holdStepNo)/100 - rand(1.0)).ceil == 1)
					// and sequencer not muted
					and: (this.getSynthArgSpec("muteSeq") == 0),
			{ 
				// use bundle to allow for latency
				system.server.makeBundle(seqLatency + outDelay, { 
					if (noteOutModule1.notNil, {noteOutModule1.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule2.notNil, {noteOutModule2.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule3.notNil, {noteOutModule3.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule4.notNil, {noteOutModule4.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule5.notNil, {noteOutModule5.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule6.notNil, {noteOutModule6.createSynthNote(outNote, outVel, outEnvTime)});
					// write note & velocity as control values to output busses
					outBus.setn([(outNote/127).max(0).min(1), outVel/127]);
				});
			});
			// go to next step
			seqCurrentStep = (seqCurrentStep + 1);

	// not used now
	//		seqResetCounter = (seqResetCounter+1);

			// if past end, go to start step if looped, use chain if requested, or else stop sequencer
			if (seqCurrentStep > (this.getSynthArgSpec("seqEndStep") - 1), { 

				// if nextPatternInd is 0 stop sequencer
				if ((this.getSynthArgSpec("nextPatternInd") == 0), {
					this.stopSequencer;
				});

				// rebuild step order
				this.rebuildStepOrderArr;
				
				// if nextPatternInd is 1 loop current pattern
				if ((this.getSynthArgSpec("nextPatternInd") == 1), {
					seqCurrentStep = this.getSynthArgSpec("seqStartStep") - 1;
				});
				
				// if nextPatternInd is 2 get next pattern in pattern chain
				if ((this.getSynthArgSpec("nextPatternInd") == 2), {
					stopSeqFlag = false;
					// go to next chain step
					holdChainCurrentStep = this.getSynthArgSpec("chainCurrentStep");
					holdChainCurrentStep = holdChainCurrentStep + 1;
					this.setSynthArgSpec("chainCurrentStep", holdChainCurrentStep);
					// if end of chain 
					if (this.getSynthArgSpec("chainCurrentStep") > this.getSynthArgSpec("chainEndStep"), {
						if (this.getSynthArgSpec("chainLoop") == 1, {
							this.setSynthArgSpec("chainCurrentStep",this.getSynthArgSpec("chainStartStep"));
						}, {
							stopSeqFlag = true;
						});
					});
					if (stopSeqFlag == false, {
						// load new slot
						holdSlotNo = this.getSynthArgSpec("arrChainSlots").at(this.getSynthArgSpec("chainCurrentStep") - 1);
						this.setSynthArgSpec("slotNo", holdSlotNo);
						this.setSlotData(arrSlots.at(holdSlotNo));
						seqCurrentStep = this.getSynthArgSpec("seqStartStep") - 1;

						//	gui update
					//	this.buildGuiSpecArray;
						system.showViewIfModDisplay(this);
					}, {
						this.stopSequencer;
					});
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

syncStopSequencer { 
	// if syncStop is 1 then stop sequencer
	if (this.getSynthArgSpec("syncStop") == 1, {
		this.stopSequencer;
	});
} 

resetSequencer { 
	var holdSlotNo;
	// reset variables
	// if nextPatternInd is 2 (i.e. it is using the pattern chain) get first pattern in chain
	if ((this.getSynthArgSpec("nextPatternInd") == 2), {
		// set chain step to start
		this.setSynthArgSpec("chainCurrentStep",this.getSynthArgSpec("chainStartStep"));
		// load new slot
		holdSlotNo = this.getSynthArgSpec("arrChainSlots").at(this.getSynthArgSpec("chainCurrentStep") - 1);
		this.setSlotData(arrSlots.at(holdSlotNo));
		seqCurrentStep = this.getSynthArgSpec("seqStartStep") - 1;
	});
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
	var holdBPM;
	if (argData.notNil, {
		arrSlots = argData.deepCopy;
		// set BPM
		holdBPM =  ControlSpec(this.getSynthArgSpec("seqBPMMin"), this.getSynthArgSpec("seqBPMMax"))
			.map(this.getSynthArgSpec("seqBPM"));
		this.setTempo(holdBPM/60);
	});
}

getSlotData {	
	^["seqStartStep", "seqEndStep", "seqNoteBase", "arrNotes", "arrRandOctaves", 
		"arrVelocities", "arrRandVelocities", "arrStepLengthsX", "arrStepLengthsY", "arrNoteLengthsX", "arrNoteLengthsY", 
		"arrProbabilities", "arrDelays", "arrRandDelays", "arrOnOffSteps", "description"
	].collect({ arg item, i;
		this.getSynthArgSpec(item);
	});
}

setSlotData {arg argSlotData;
	if (argSlotData.notNil, {
		["seqStartStep", "seqEndStep", "seqNoteBase", "arrNotes", "arrRandOctaves", 
			"arrVelocities", "arrRandVelocities", "arrStepLengthsX", "arrStepLengthsY", "arrNoteLengthsX", "arrNoteLengthsY",
			"arrProbabilities", "arrDelays", "arrRandDelays", "arrOnOffSteps", "description"
		].do({ arg item, i;
			this.setSynthArgSpec(item, argSlotData.at(i));
		});
	}, {
		this.resetPattern;
	});
}

updateSlot {	
	arrSlots.put(this.getSynthArgSpec("slotNo"), this.getSlotData.deepCopy);
}

updateSlotNo {
	var holdSlotNo;	
	holdSlotNo = this.getSynthArgSpec("arrChainSlots").at(this.getSynthArgSpec("chainCurrentStep") - 1);
	this.setSynthArgSpec("slotNo", holdSlotNo);
	this.setSlotData(arrSlots.at(holdSlotNo));
}

updateCurrentChainStep {	
	var holdSlotNo, holdArrChainSlots;
	holdSlotNo = this.getSynthArgSpec("slotNo");
	holdArrChainSlots = this.getSynthArgSpec("arrChainSlots");
	holdArrChainSlots.put(this.getSynthArgSpec("chainCurrentStep") - 1, holdSlotNo);
	this.setSynthArgSpec("arrChainSlots",  holdArrChainSlots);
}

resetPattern {
	[	["seqStartStep", 1],
		["seqEndStep", 16],
		["seqNoteBase", 48],
 		["arrNotes", Array.fill(64, 0)],
		["arrRandOctaves", Array.fill(64, 0)],
		["arrVelocities", Array.fill(64, 100)],
		["arrRandVelocities", Array.fill(64, 0)],
		["arrStepLengthsX", Array.fill(64, 1)],
		["arrStepLengthsY", Array.fill(64, 2)],
		["arrNoteLengthsX", Array.fill(64, 1)],
		["arrNoteLengthsY", Array.fill(64, 2)],
		["arrProbabilities", Array.fill(64, 100)],
		["arrOnOffSteps", Array.fill(64, 1)],
		["arrDelays", Array.fill(64, 0)],
		["arrRandDelays", Array.fill(64, 0)],
 		["description", ""],
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
 	holdID = this.getSynthArgSpec("noteOutModuleID4");
	if (holdID.notNil, {noteOutModule4 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("noteOutModuleID5");
	if (holdID.notNil, {noteOutModule5 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("noteOutModuleID6");
	if (holdID.notNil, {noteOutModule6 = system.getModuleFromID(holdID)});
} 

midiLearn { arg argSwitch = 0;
	// stop any previous routine 
 	if (midiNoteOnResp.class == NoteOnResponder, {
 		midiNoteOnResp.remove; 
 	});
	// if requested start responder  
	if (argSwitch == 1, {
		// reset variable
		seqRecordStep = 0;
		// start responder 
		midiNoteOnResp = NoteOnResponder ({  |src, chan, num, vel|
			var holdArrNotes, holdArrVelocities, holdSeqNoteBase;
			//	check whether to store 
			if (	(chan >= (midiMinChannel-1)) and: (chan <= (midiMaxChannel-1)), {
				holdArrNotes = this.getSynthArgSpec("arrNotes");
				holdArrVelocities = this.getSynthArgSpec("arrVelocities");
				holdSeqNoteBase  = this.getSynthArgSpec("seqNoteBase");
				// store note and velocity
				holdArrNotes.put(seqRecordStep, num - holdSeqNoteBase);
				holdArrVelocities.put(seqRecordStep, vel * 100/127);
				this.setSynthArgSpec("arrNotes", holdArrNotes);
				this.setSynthArgSpec("arrVelocities", holdArrVelocities);
				// go to next step
				seqRecordStep = seqRecordStep + 1;
				if (seqRecordStep > 64, {seqRecordStep = 0;});
			});
			// refresh view
			system.showViewIfModDisplay(this);
		});
	}, {
		// refresh view
		system.showViewIfModDisplay(this);
	});
} 

rebuildStepOrderArr { 
	var rangeStart, rangeEnd, loRangeIndex, hiRangeIndex, rangeSize, randOrder, wholeArr, partArr;
	// scramble if selected
	if (	this.getSynthArgSpec("randomStepOrder") == 1, {
		rangeStart = this.getSynthArgSpec("seqStartStep");
		rangeEnd = this.getSynthArgSpec("seqEndStep");
		loRangeIndex = min(rangeStart, rangeEnd) - 1;
		hiRangeIndex = max(rangeStart, rangeEnd) - 1;
		rangeSize = hiRangeIndex - loRangeIndex + 1;
		wholeArr = Array.series(64);
		randOrder = Array.series(rangeSize).scramble;
		rangeSize.do({ arg i;
			wholeArr.put(loRangeIndex + i, randOrder.at(i));
		});
	},{
		wholeArr = Array.series(64);
	});
	this.setSynthArgSpec("stepOrderArr", wholeArr);
}

////////////////////////////////////
getSynthArgValByGroup {arg argGroup, argSynthArgName;
	var groupName, groupArray, synthArgArray, firstGpMembInd, firstGpMembSynthArg;
	groupName = ["arrGroup1s", "arrGroup2s", "arrGroup3s", "arrGroup4s", 
		"arrGroup5s", "arrGroup6s", "arrGroup7s", "arrGroup8s"]
		.clipAt(argGroup-1);
	groupArray = this.getSynthArgSpec(groupName);
	// find first step in group
	firstGpMembInd = groupArray.indexOfEqual(1);
	// if  not nil return value of first step in group, else return nil
	if (firstGpMembInd.notNil, {
		synthArgArray = this.getSynthArgSpec(argSynthArgName);
		firstGpMembSynthArg = synthArgArray.at(firstGpMembInd);
	});
	^firstGpMembSynthArg;
}

setSynthArgValByGroup {arg argVal, argGroup, argSynthArgName;
	var groupName, groupArray, synthArgArray, firstGpMembInd, firstGpMembSynthArg;
	groupName = ["arrGroup1s", "arrGroup2s", "arrGroup3s", "arrGroup4s", 
		"arrGroup5s", "arrGroup6s", "arrGroup7s", "arrGroup8s"]
		.clipAt(argGroup-1);
	groupArray = this.getSynthArgSpec(groupName);
	synthArgArray = this.getSynthArgSpec(argSynthArgName).deepCopy;
	// set value of all steps in group
	groupArray.do({arg item, i;
		if (item == 1, {
			synthArgArray.put(i, argVal);
		});
	});
	// update synth arg spec
	this.setSynthArgSpec(argSynthArgName, synthArgArray);
}


////////////////////////////////////

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
		if (noteOutModule4.notNil, {
			if (noteOutModule4.deletedStatus == true, {
				noteOutModule4 = nil;
				this.setSynthArgSpec("noteOutModuleID4", nil);
			});
		});
		if (noteOutModule5.notNil, {
			if (noteOutModule5.deletedStatus == true, {
				noteOutModule5 = nil;
				this.setSynthArgSpec("noteOutModuleID5", nil);
			});
		});
		if (noteOutModule6.notNil, {
			if (noteOutModule6.deletedStatus == true, {
				noteOutModule6 = nil;
				this.setSynthArgSpec("noteOutModuleID6", nil);
			});
		});
}

constrainChainStep {
	var holdCurrent, holdStart, holdEnd;	
	// contrain current chain step between start and end steps
	holdCurrent = this.getSynthArgSpec("chainCurrentStep");
	holdStart = this.getSynthArgSpec("chainStartStep");
	holdEnd = this.getSynthArgSpec("chainEndStep");
	this.setSynthArgSpec("chainCurrentStep", holdCurrent.max(holdStart).min(holdEnd));
}

////////////////////////////////////

initProcessSpecs {
	var parmSelectGui, parmGroupSelectGui;
	arrProcSelNames = ["procSelOnOffSteps", "procSelNotes", "procSelRandOctaves", "procSelVelocities", 
		"procSelRandVelocities", "procSelStepLengthsX", "procSelStepLengthsY", 
		"procSelNoteLengthsX", "procSelNoteLengthsY", "procSelProbabilities", "procSelDelays", "procSelRandDelays",
		"procSelGroup1s", "procSelGroup2s", "procSelGroup3s", "procSelGroup4s", 
		"procSelGroup5s", "procSelGroup6s", "procSelGroup7s", "procSelGroup8s", 
	];
	arrProcSynthArgNames = ["arrOnOffSteps", "arrNotes",  "arrRandOctaves",  "arrVelocities", 
		"arrRandVelocities",  "arrStepLengthsX",  "arrStepLengthsY", 
		"arrNoteLengthsX",  "arrNoteLengthsY",  "arrProbabilities", "arrDelays", "arrRandDelays", 
		"arrGroup1s", "arrGroup2s", "arrGroup3s", "arrGroup4s", "arrGroup5s", 
		"arrGroup6s", "arrGroup7s", "arrGroup8s"
	];
	parmSelectGui = [
		["DividingLine", 500],
		["SpacerLine", 1], 
		["TextBar", "Select which parameters should be processed:", 304],
		["ActionButton", "All Off", {
			arrProcSelNames.do({ arg item, i;
				this.setSynthArgSpec(item, 0);
			});
			system.showView;
			}, 
			71
		],
		["ActionButton", "All On", {
			arrProcSelNames.do({ arg item, i;
				this.setSynthArgSpec(item, 1);
			});
			system.showView;
			}, 
			71
		],
		["NextLine"],
		["TXCheckBox", "On/Off Steps", "procSelOnOffSteps", nil, 120],
 		["TXCheckBox", "Notes", "procSelNotes", nil, 120],
		["TXCheckBox", "Rand Octaves", "procSelRandOctaves", nil, 120],
		["TXCheckBox", "Velocities", "procSelVelocities", nil, 120],
		["TXCheckBox", "Rand Velocities", "procSelRandVelocities", nil, 120],
		["TXCheckBox", "Step Lengths X", "procSelStepLengthsX", nil, 120],
		["TXCheckBox", "Step Lengths Y", "procSelStepLengthsY", nil, 120],
		["TXCheckBox", "Note Lengths X", "procSelNoteLengthsX", nil, 120],
		["TXCheckBox", "Note Lengths Y", "procSelNoteLengthsY", nil, 120],
		["TXCheckBox", "Probabilities", "procSelProbabilities", nil, 120],
		["TXCheckBox", "Delays", "procSelDelays", nil, 120],
		["TXCheckBox", "Rand delays", "procSelRandDelays", nil, 120],
		["TXCheckBox", "Group 1", "procSelGroup1s", nil, 120],
		["TXCheckBox", "Group 2", "procSelGroup2s", nil, 120],
		["TXCheckBox", "Group 3", "procSelGroup3s", nil, 120],
		["TXCheckBox", "Group 4", "procSelGroup4s", nil, 120],
		["TXCheckBox", "Group 5", "procSelGroup5s", nil, 120],
		["TXCheckBox", "Group 6", "procSelGroup6s", nil, 120],
		["TXCheckBox", "Group 7", "procSelGroup7s", nil, 120],
		["TXCheckBox", "Group 8", "procSelGroup8s", nil, 120],
	];
	arrProcessSpecs = [
		["select a process...", 
			{ }, 
			["Copy and paste steps once","Copy and paste steps repeatedly","Shift steps left","Shift steps right ",
			"Randomise step order","Reverse step order","Constrain notes to chord, mode, scale",
			"Generate notes from chord, mode, scale","Generate notes from selected range","Transpose notes upwards",
			"Transpose notes downwards","Randomise step on-off values","Randomise velocity values",
			"Randomise step length X values","Randomise step length Y values","Randomise note length X values",
			"Randomise note length Y values","Randomise probability values","Randomise delay values"
			].collect({ arg item, i;
				["ActionButton", item, 
					{this.setSynthArgSpec("processTypeInd", i + 1); this.buildGuiSpecArray; system.showView;}, 
					260, TXColor.white, TXColor.sysGuiCol2 
				]
			}),
		], 
		["Copy and paste steps once", 
			{ 	var sourceStart, sourceEnd, loSourceIndex, hiSourceIndex, wholeArr, sourceArr, changeArr;
				this.prepareCopyProcess;
				sourceStart = this.getSynthArgSpec("procSourceStart");
				sourceEnd = this.getSynthArgSpec("procSourceEnd");
				loSourceIndex = min(sourceStart, sourceEnd) - 1;
				hiSourceIndex = max(sourceStart, sourceEnd) - 1;
				arrProcSelNames.do({arg item, i;
					// if parameter selected for processing 
					if (this.getSynthArgSpec(item) == 1, {
						wholeArr = this.getSynthArgSpec(arrProcSynthArgNames.at(i));
						sourceArr = wholeArr.copyRange(loSourceIndex.asInteger, hiSourceIndex.asInteger);
						changeArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
						if (sourceArr.size > targetSize, {
							targetSize.do({ arg i;
								changeArr.put(i, sourceArr.at(i));
							});
						},{
							sourceArr.size.do({ arg i;
								changeArr.put(i, sourceArr.at(i));
							});
						});
						this.updateProcTarget(arrProcSynthArgNames.at(i), changeArr);					});
				});
			}, 
			[
				["TXRangeSlider", "Copy steps", ControlSpec(1, 64, step:1), "procSourceStart", "procSourceEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procCopyTargStart", "procCopyTargEnd", 
					nil, arrSeqRangePresets],
			] ++ parmSelectGui,
		], 
		["Copy and paste steps repeatedly", 
			{ 	var sourceStart, sourceEnd, loSourceIndex, hiSourceIndex, wholeArr, sourceArr, changeArr;
				this.prepareCopyProcess;
				sourceStart = this.getSynthArgSpec("procSourceStart");
				sourceEnd = this.getSynthArgSpec("procSourceEnd");
				loSourceIndex = min(sourceStart, sourceEnd) - 1;
				hiSourceIndex = max(sourceStart, sourceEnd) - 1;
				arrProcSelNames.do({arg item, i;
					// if parameter selected for processing 
					if (this.getSynthArgSpec(item) == 1, {
						wholeArr = this.getSynthArgSpec(arrProcSynthArgNames.at(i));
						sourceArr = wholeArr.copyRange(loSourceIndex.asInteger, hiSourceIndex.asInteger);
						changeArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
						targetSize.do({ arg i;
							changeArr.put(i, sourceArr.wrapAt(i));
						});
						this.updateProcTarget(arrProcSynthArgNames.at(i), changeArr);					});
				});
			}, 
			[
				["TXRangeSlider", "Copy steps", ControlSpec(1, 64, step:1), "procSourceStart", "procSourceEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procCopyTargStart", "procCopyTargEnd", 
					nil, arrSeqRangePresets],
			] ++ parmSelectGui,
		], 
		["Shift steps left", 
			{ 	var wholeArr, changeArr;
				this.prepareProcess;
				arrProcSelNames.do({arg item, i;
					// if parameter selected for processing 
					if (this.getSynthArgSpec(item) == 1, {
						wholeArr = this.getSynthArgSpec(arrProcSynthArgNames.at(i));
						changeArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
						changeArr = changeArr.rotate(this.getSynthArgSpec("procShiftSteps").neg.asInteger);
						this.updateProcTarget(arrProcSynthArgNames.at(i), changeArr);					});
				});
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["EZslider", "No. steps", ControlSpec(1, 64, step:1), "procShiftSteps"],
			] ++ parmSelectGui,
		], 
		["Shift steps right ", 
			{ 	var wholeArr, changeArr;
				this.prepareProcess;
				arrProcSelNames.do({arg item, i;
					// if parameter selected for processing 
					if (this.getSynthArgSpec(item) == 1, {
						wholeArr = this.getSynthArgSpec(arrProcSynthArgNames.at(i));
						changeArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
						changeArr = changeArr.rotate(this.getSynthArgSpec("procShiftSteps").asInteger);
						this.updateProcTarget(arrProcSynthArgNames.at(i), changeArr);					});
				});
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["EZslider", "No. steps", ControlSpec(1, 64, step:1), "procShiftSteps"],
			] ++ parmSelectGui,
		], 
		["Randomise step order", 
			{ 	var wholeArr, partArr, changeArr, randOrder;
				this.prepareProcess;
				randOrder = Array.series(targetSize).scramble;
				arrProcSelNames.do({arg item, i;
					// if parameter selected for processing 
					if (this.getSynthArgSpec(item) == 1, {
						wholeArr = this.getSynthArgSpec(arrProcSynthArgNames.at(i));
						partArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
						changeArr = [];
						targetSize.do({arg item, i;
							changeArr = changeArr.add(partArr.at(randOrder.at(i)));
						});
						this.updateProcTarget(arrProcSynthArgNames.at(i), changeArr);					});
				});
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
			] ++ parmSelectGui,
		], 
		["Reverse step order", 
			{ 	var wholeArr, partArr, changeArr;
				this.prepareProcess;
				arrProcSelNames.do({arg item, i;
					// if parameter selected for processing 
					if (this.getSynthArgSpec(item) == 1, {
						wholeArr = this.getSynthArgSpec(arrProcSynthArgNames.at(i));
						partArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
						changeArr = partArr.reverse;
						this.updateProcTarget(arrProcSynthArgNames.at(i), changeArr);
					});
				});
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
			] ++ parmSelectGui,
		], 
		["Constrain notes to chord, mode, scale", 
			{ 	var wholeArr, partArr, changeArr, arrScaleSpec, scaleRoot, seqNoteBase, arrScaleNotes;
				this.prepareProcess;
				wholeArr = this.getSynthArgSpec("arrNotes");
				partArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
				arrScaleSpec = TXScale.arrScaleNotes.at(this.getSynthArgSpec("procScaleTypeInd"));
				scaleRoot = this.getSynthArgSpec("procScaleRoot");
				seqNoteBase = this.getSynthArgSpec("seqNoteBase");
				13.do({arg octave;
					arrScaleSpec.do({arg item, i;
						arrScaleNotes = arrScaleNotes.add(((octave-1) * 12) + scaleRoot + item);
					});
				});
				arrScaleNotes = arrScaleNotes.select({arg item, i; ((item > -1) and: (item < 128)); });
				changeArr = partArr.collect({arg item, i;
					var holdInd;
					// find index of closest valid note
					holdInd = arrScaleNotes.indexIn(item + seqNoteBase);
					arrScaleNotes.at(holdInd) - seqNoteBase;
				});
				this.updateProcTarget("arrNotes", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXPopupAction", "Chord/ scale", TXScale.arrScaleNames, "procScaleTypeInd", nil, 400], 
				["NextLine"],
				["SpacerLine", 1], 
				["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", "F#", "G", "G#", "A", "A#", "B"], 
					"procScaleRoot", nil, 140], 
			],
		], 
		["Generate notes from chord, mode, scale", 
			{ 	var changeArr, arrScaleSpec, scaleRoot, seqNoteBase, noteMin, noteMax, noteOrderInd, arrScaleNotes;
				this.prepareProcess;
				arrScaleSpec = TXScale.arrScaleNotes.at(this.getSynthArgSpec("procScaleTypeInd"));
				scaleRoot = this.getSynthArgSpec("procScaleRoot");
				seqNoteBase = this.getSynthArgSpec("seqNoteBase");
				noteMin = this.getSynthArgSpec("procRandNoteMin");
				noteMax = this.getSynthArgSpec("procRandNoteMax");
				noteOrderInd = this.getSynthArgSpec("procNoteOrderInd");
				13.do({arg octave;
					arrScaleSpec.do({arg item, i;
						arrScaleNotes = arrScaleNotes.add(((octave-1) * 12) + scaleRoot + item);
					});
				});
				arrScaleNotes = arrScaleNotes.select({arg item, i; ((item >= noteMin) and: (item <= noteMax)); });
				if (noteOrderInd == 3, {arrScaleNotes = arrScaleNotes.reverse; });
				if (noteOrderInd == 4, {arrScaleNotes = arrScaleNotes.mirror1; });
				if (noteOrderInd == 5, {arrScaleNotes = arrScaleNotes.reverse.mirror1; });
				targetSize.do({ arg i;
					changeArr = changeArr.add(arrScaleNotes.wrapAt(i));
				});
				if (noteOrderInd == 0, {changeArr = changeArr.scramble; });
				if (noteOrderInd == 1, {
					changeArr = Array.rand(targetSize, 0, targetSize-1).collect({arg item, i;
						changeArr.at(item);
					}); 
				});
				changeArr = changeArr - seqNoteBase;
				this.updateProcTarget("arrNotes", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXPopupAction", "Chord/ scale", TXScale.arrScaleNames, "procScaleTypeInd", nil, 400], 
				["NextLine"],
				["SpacerLine", 1], 
				["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", "F#", "G", "G#", "A", "A#", "B"], 
					"procScaleRoot", nil, 140], 
				["NextLine"],
				["SpacerLine", 1], 
				["TXPopupAction", "Note order", [ "random order", "random selection & order", 
					"forwards", "backwards","forwards-backwards","backwards-forwards"], "procNoteOrderInd", nil, 300], 
				["NextLine"],
				["SpacerLine", 1], 
				["TXNoteRangeSlider", "Note range", "procRandNoteMin", "procRandNoteMax", nil, true],
			],
		], 
		["Generate notes from selected range", 
			{ 	var seqNoteBase, noteOrderInd, arrScaleNotes;
				var sourceStart, sourceEnd, loSourceIndex, hiSourceIndex, wholeArr, sourceArr, changeArr;
				var octTranspMin, octTranspMax, loOct, hiOct;
				
				this.prepareProcess;
				sourceStart = this.getSynthArgSpec("procSourceStart");
				sourceEnd = this.getSynthArgSpec("procSourceEnd");
				loSourceIndex = min(sourceStart, sourceEnd) - 1;
				hiSourceIndex = max(sourceStart, sourceEnd) - 1;
				seqNoteBase = this.getSynthArgSpec("seqNoteBase");
				noteOrderInd = this.getSynthArgSpec("procNoteOrderInd");
				octTranspMin = this.getSynthArgSpec("procOctTranspMin");
				octTranspMax = this.getSynthArgSpec("procOctTranspMax");
				loOct = min(octTranspMin, octTranspMax);
				hiOct = max(octTranspMin, octTranspMax);
				wholeArr = this.getSynthArgSpec("arrNotes");
				sourceArr = wholeArr.copyRange(loSourceIndex.asInteger, hiSourceIndex.asInteger);
				((hiOct - loOct) + 1).do({arg octave;
					sourceArr.do({arg item, i;
						arrScaleNotes = arrScaleNotes.add(((loOct + octave) * 12) + item);
					});
				});
				arrScaleNotes = (arrScaleNotes + seqNoteBase).select({arg item, i; ((item > -1) and: (item < 128)); });
				if (noteOrderInd == 3, {arrScaleNotes = arrScaleNotes.reverse; });
				if (noteOrderInd == 4, {arrScaleNotes = arrScaleNotes ++ arrScaleNotes.reverse; });
				if (noteOrderInd == 5, {arrScaleNotes = arrScaleNotes.reverse ++ arrScaleNotes; });
				targetSize.do({ arg i;
					changeArr = changeArr.add(arrScaleNotes.wrapAt(i));
				});
				if (noteOrderInd == 0, {changeArr = changeArr.scramble; });
				if (noteOrderInd == 1, {
					changeArr = Array.rand(targetSize, 0, targetSize-1).collect({arg item, i;
						changeArr.at(item);
					}); 
				});
				changeArr = changeArr - seqNoteBase;
				this.updateProcTarget("arrNotes", changeArr);			}, 
			[
				["TXRangeSlider", "Copy steps", ControlSpec(1, 64, step:1), "procSourceStart", "procSourceEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Oct Transpose", ControlSpec(-8, 8, step:1), "procOctTranspMin", "procOctTranspMax"],
				["NextLine"],
				["SpacerLine", 1], 
				["TXPopupAction", "Note order", [ "random order", "random selection & order", 
					"forwards", "backwards","forwards-backwards","backwards-forwards"], 
					"procNoteOrderInd", nil, 300], 
			],
		], 
		["Transpose notes upwards", 
			{ 	var wholeArr, partArr, changeArr, seqNoteBase, seqNoteTranspose;
				this.prepareProcess;
				wholeArr = this.getSynthArgSpec("arrNotes");
				partArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
				seqNoteBase = this.getSynthArgSpec("seqNoteBase");
				seqNoteTranspose = this.getSynthArgSpec("procTranspose");
				changeArr = (partArr + seqNoteBase + seqNoteTranspose).min(127) - seqNoteBase;
				this.updateProcTarget("arrNotes", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["EZslider", "No. semitones", ControlSpec(1, 64, step:1), "procTranspose"],
			],
		], 
		["Transpose notes downwards", 
			{ 	var wholeArr, partArr, changeArr, seqNoteBase, seqNoteTranspose;
				this.prepareProcess;
				wholeArr = this.getSynthArgSpec("arrNotes");
				partArr = wholeArr.copyRange(loTargetIndex.asInteger, hiTargetIndex.asInteger);
				seqNoteBase = this.getSynthArgSpec("seqNoteBase");
				seqNoteTranspose = this.getSynthArgSpec("procTranspose");
				changeArr = (partArr + seqNoteBase - seqNoteTranspose).max(0) - seqNoteBase;
				this.updateProcTarget("arrNotes", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["EZslider", "No. semitones", ControlSpec(1, 64, step:1), "procTranspose"],
			],
		], 
		["Randomise step on-off values", 
			{ 	var changeArr, onProb;
				this.prepareProcess;
				onProb = this.getSynthArgSpec("procRandOnProb");
				changeArr = Array.fill(targetSize,{[1,0].wchoose([onProb, (100-onProb)] / 100);});
				this.updateProcTarget("arrOnOffSteps", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["EZslider", "On probability", ControlSpec(0, 100, step:1), "procRandOnProb"],
			],
		], 
		["Randomise velocity values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandVelMin");
				maxVal = this.getSynthArgSpec("procRandVelMax");
				changeArr = Array.rand(targetSize, minVal, maxVal);
				this.updateProcTarget("arrVelocities", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Vel range", ControlSpec(0, 100, step:1), "procRandVelMin", "procRandVelMax"],
			],
		], 
		["Randomise step length X values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandStepXMin");
				maxVal = this.getSynthArgSpec("procRandStepXMax");
				changeArr = Array.rand(targetSize, minVal.asInteger, maxVal.asInteger);
				this.updateProcTarget("arrStepLengthsX", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "X range", ControlSpec(1, 64, step: 1), "procRandStepXMin", "procRandStepXMax"],
			],
		], 
		["Randomise step length Y values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandStepYMin");
				maxVal = this.getSynthArgSpec("procRandStepYMax");
				changeArr = Array.rand(targetSize, minVal.asInteger, maxVal.asInteger);
				this.updateProcTarget("arrStepLengthsY", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Y range", ControlSpec(1, 64, step: 1), "procRandStepYMin", "procRandStepYMax"],
			],
		], 
		["Randomise note length X values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandNoteXMin");
				maxVal = this.getSynthArgSpec("procRandNoteXMax");
				changeArr = Array.rand(targetSize, minVal.asInteger, maxVal.asInteger);
				this.updateProcTarget("arrNoteLengthsX", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "X range", ControlSpec(1, 64, step: 1), "procRandNoteXMin", "procRandNoteXMax"],
			],
		], 
		["Randomise note length Y values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandNoteYMin");
				maxVal = this.getSynthArgSpec("procRandNoteYMax");
				changeArr = Array.rand(targetSize, minVal.asInteger, maxVal.asInteger);
				this.updateProcTarget("arrNoteLengthsY", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Y range", ControlSpec(1, 64, step: 1), "procRandNoteYMin", "procRandNoteYMax"],
			],
		], 
		["Randomise probability values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandProbMin");
				maxVal = this.getSynthArgSpec("procRandProbMax");
				changeArr = Array.rand(targetSize, minVal, maxVal);
				this.updateProcTarget("arrProbabilities", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Prob range", ControlSpec(0, 100, step:1), "procRandProbMin", "procRandProbMax"],
			],
		], 
		["Randomise delay values", 
			{ 	var changeArr, minVal, maxVal;
				this.prepareProcess;
				minVal = this.getSynthArgSpec("procRandDelayMin");
				maxVal = this.getSynthArgSpec("procRandDelayMax");
				changeArr = Array.rand(targetSize, minVal, maxVal);
				this.updateProcTarget("arrDelays", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd", 
					nil, arrSeqRangePresets],
				["NextLine"],
				["SpacerLine", 1], 
				["TXRangeSlider", "Delay range", ControlSpec(0, 100, step:1), "procRandDelayMin", "procRandDelayMax"],
			],
		], 

	]; 
}

prepareProcess { 
	var targStart, targEnd;
	targStart = this.getSynthArgSpec("procTargStart");
	targEnd = this.getSynthArgSpec("procTargEnd");
	loTargetIndex = min(targStart, targEnd) - 1;
	hiTargetIndex = max(targStart, targEnd) - 1;
	targetSize = hiTargetIndex - loTargetIndex + 1;
}

prepareCopyProcess { 
	// processes which copy use different target start & end variables to other processes
	var targStart, targEnd;
	targStart = this.getSynthArgSpec("procCopyTargStart");
	targEnd = this.getSynthArgSpec("procCopyTargEnd");
	loTargetIndex = min(targStart, targEnd) - 1;
	hiTargetIndex = max(targStart, targEnd) - 1;
	targetSize = hiTargetIndex - loTargetIndex + 1;
}

updateProcTarget {arg argSynthArgString, argChangeArray; 
	var outArray;
	outArray = this.getSynthArgSpec(argSynthArgString);
	targetSize.do({ arg i;
		outArray.put(loTargetIndex + i, argChangeArray.at(i));
	});
	this.setSynthArgSpec(argSynthArgString, outArray);
}

rebuildSynth { 
	// override base class method
}

}

