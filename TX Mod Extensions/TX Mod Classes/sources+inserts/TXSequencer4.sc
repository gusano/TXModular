// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSequencer4 : TXModuleBase {		// Sequencer module 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiWidth=600;
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
	var		<arrProcessSpecs;	// used for processing
	var		arrProcSelNames;
	var		arrProcSynthArgNames;
	var 		loTargetIndex, hiTargetIndex, targetSize; 
	var		slotClipboard;

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
		["noteOutModuleID1", nil],
		["noteOutModuleID2", nil],
		["noteOutModuleID3", nil],
		["midiLearn", 0],
		["showVelocityBars", 0],
		["showProbabilityBars", 1],
		["showDelayBars", 0],
 		["description", ""],
 		["chainStartStep", 1],
		["chainEndStep", 1],
		["chainCurrentStep", 1],
		["chainLoop", 1],
		["arrChainSlots", Array.fill(100, 0)],
		["displayFirstChainStep", 0],
 		["slotNo", 0],
 		["displayOption", "showNotes"],
		["nextPatternInd", 1],

		["processTypeInd", 0],
		["procTargStart", 1],
		["procTargEnd", 64],
		["procSourceStart", 1],
		["procSourceEnd", 1],
		["procShiftSteps", 0],
		["procOctTranspMin", 0],
		["procOctTranspMax", 0],
		["procScaleTypeInd", 0],
		["procScaleRoot", 48],
		["procNoteOrderInd", 0],
		["procTranspose", 0],
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
		
		["procRandOnProb", 50],
		["procRandNoteMin", 0],
		["procRandNoteMax", 127],
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
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Start Sequencer", {this.startSequencer;}],
		["commandAction", "Stop Sequencer", {this.stopSequencer;}],
		["SeqSyncStartCheckBox"], 
		["TXMinMaxSliderSplit", "BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax",
			{arg view; this.setTempo(view.value/60);}], 
		["TXCheckBox", "Midi learn notes and velocities", "midiLearn", {arg view; this.midiLearn(view.value);}, 250],

		["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
		["TXRangeSlider", "Vel range", ControlSpec(0, 100), "velMin", "velMax", {this.updateSlot;}],
		["TXNumberPlusMinus", "Pattern slot", ControlSpec(0, 99, step: 1), "slotNo", 
			{this.setSlotData(arrSlots.at(this.getSynthArgSpec("slotNo"))); 
				this.updateCurrentChainStep;
				system.showViewIfModDisplay(this);
			}, 
			[-10,-1,1,10]
		],


// ------------------------------------------------------------- amendments, amendments, amendments, -----------------------
//	ADD commandAction DEFINITIONS HERE - such as:
//	- go to step N
//	- transpose sequence by +/- N semitones



	]);	
	this.buildGuiSpecArray;
	//	use base class initialise 
	this.baseInit(this, argInstName);
}

buildGuiSpecArray {
	guiSpecArray = [
		["DividingLine"], 
		["SeqSelect3GroupModules", "noteOutModule1", "noteOutModule2", "noteOutModule3", 
			"noteOutModuleID1", "noteOutModuleID2", "noteOutModuleID3"], 
		["SpacerLine", 3], 
//		["DividingLine"], 
		["TXMinMaxSliderSplit", "BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax",
			{arg view; this.setTempo(view.value/60);}], 
//		["DividingLine"], 
		["SpacerLine", 3], 
		["TXCheckBox", "Midi learn notes and velocities", "midiLearn", {arg view; this.midiLearn(view.value);}, 250],
		["Spacer", 3], 
		["ActionButton", "Refresh screen", {system.showView;}, 110], 
		["Spacer", 3], 
		["ActionButtonDark", "Reset all values", {this.resetPattern; }, 150], 
		["SpacerLine", 3], 
		["ActionButton", "Notes", {this.setSynthArgSpec("displayOption", "showNotes"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
		["Spacer", 3], 
		["ActionButton", "Velocities", {this.setSynthArgSpec("displayOption", "showVelocities"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
		["Spacer", 3], 
		["ActionButton", "Step/note lengths", {this.setSynthArgSpec("displayOption", "showStepNoteLengths"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
		["Spacer", 3], 
		["ActionButton", "Probabilities", {this.setSynthArgSpec("displayOption", "showProbabilities"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
		["NextLine"], 
		["ActionButton", "Delays", {this.setSynthArgSpec("displayOption", "showDelays"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
		["Spacer", 3], 
		["ActionButton", "Patterns", {this.setSynthArgSpec("displayOption", "showPatterns"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
		["Spacer", 3], 
		["ActionButton", "Processes", {this.setSynthArgSpec("displayOption", "showProcesses"); 
			this.buildGuiSpecArray; system.showView;}, 105], 
	];
	if (this.getSynthArgSpec("displayOption") == "showNotes", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["MidiNoteRow", "seqNoteBase", "arrNotes", 16, "displayFirstStep", {this.updateSlot;}], 
			["DividingLine"], 
			["TXMultiNumber", "Rand octave", ControlSpec(-8, 8, step: 1), "arrRandOctaves", 16, 
				{this.updateSlot;}, "displayFirstStep"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showVelocities", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep"], 
			["RefreshButton"],
			["DividingLine"], 
			["TXMultiSliderNo", "Velocity", ControlSpec(0, 100), "arrVelocities", 16, {this.updateSlot;}, 
				"showVelocityBars", "displayFirstStep"],
			["DividingLine"], 
			["TXMultiNumber", "Rand vel", ControlSpec(-99, 100), "arrRandVelocities", 16, 
				{this.updateSlot;}, "displayFirstStep"],
			["DividingLine"], 
			["TXRangeSlider", "Vel range", ControlSpec(0, 100), "velMin", "velMax", {this.updateSlot;}],
			["RefreshButton"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showStepNoteLengths", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep"], 
			["DividingLine"], 
			["TXMultiNumber", "Step length X", ControlSpec(0.1, 999), "arrStepLengthsX", 16, 
				{this.updateSlot;}, "displayFirstStep"],
			["TXMultiNumber", " (in beats) / Y", ControlSpec(0.1, 999), "arrStepLengthsY", 16, 
				{this.updateSlot;}, "displayFirstStep"],
			["DividingLine"], 
			["TXMultiNumber", "Note length X", ControlSpec(0.1, 999), "arrNoteLengthsX", 16, 
				{this.updateSlot;}, "displayFirstStep"],
			["TXMultiNumber", " (in beats) / Y", ControlSpec(0.1, 999), "arrNoteLengthsY", 16, 
				{this.updateSlot;}, "displayFirstStep"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showProbabilities", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep"], 
			["DividingLine"], 
			["TXMultiSliderNo", "Probability", ControlSpec(0, 100), "arrProbabilities", 16, {this.updateSlot;},
				 "showProbabilityBars", "displayFirstStep"],
			["RefreshButton"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showDelays", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep"], 
			["RefreshButton"],
			["DividingLine"], 
			["TXMultiSliderNo", "Delays", ControlSpec(0, 100), "arrDelays", 16, {this.updateSlot;},
				 "showDelayBars", "displayFirstStep"],
			["TXMultiNumber", "Rand delay", ControlSpec(-99, 100), "arrRandDelays", 16, 
				{this.updateSlot;}, "displayFirstStep"],
			["NextLine"], 
			["TextBarLeft", "Note: maximum delay is 50% of step length"],
			["NextLine"], 
			["RefreshButton"],
		];
	});
	if (this.getSynthArgSpec("displayOption") == "showPatterns", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["NextLine"], 
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep"], 
//			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["TXTextBox", "Pattern desc.", "description", nil, 350], 
			["DividingLine"], 
			["TXNumberPlusMinus", "Pattern slot", ControlSpec(0, 99, step: 1), "slotNo", 
				{this.setSlotData(arrSlots.at(this.getSynthArgSpec("slotNo"))); 
					this.updateCurrentChainStep;
					system.showViewIfModDisplay(this);				}, 
				[-10,-1,1,10]],
			["ActionButton", "Copy pattern", {slotClipboard = this.getSlotData.deepCopy;}, 80],
			["ActionButton", "Paste pattern", {if (slotClipboard.notNil, 
				{this.setSlotData(slotClipboard)}); this.updateSlot; system.showView;}, 80],
			["ActionButtonDark", "Reset all values", {this.resetPattern; this.updateSlot;}, 120], 
			["DividingLine"], 
			["TXPopupAction", "Next pattern", ["None", "Repeat current pattern", "Use pattern chain"], "nextPatternInd", 
				{this.buildGuiSpecArray; system.showView;}, 350],
		];
		// if relevant add chain gui specs
		if (this.getSynthArgSpec("nextPatternInd") == 2, {
			guiSpecArray = guiSpecArray ++ [
				["NextLine"], 
				["TextBarLeft", "Pattern Chain: press a chain step to display pattern in slot"],
				["SeqPlayRange", "chainStartStep", "chainEndStep", "chainLoop", 64, "Chain steps", 
					{this.setSynthArgSpec("displayFirstChainStep", 0); this.constrainChainStep; system.showView; },
					false
				],
				["NextLine"], 
				["SeqSelectChainStep", 16,"displayFirstChainStep", "chainCurrentStep", "chainStartStep", 
					"chainEndStep", "arrChainSlots", {this.updateSlotNo; system.showView;}
				],
				["NextLine"], 
				["RefreshButton"],
			];
		});
	});
	if (this.getSynthArgSpec("displayOption") == "showProcesses", {
		guiSpecArray = guiSpecArray ++[
			["DividingLine"], 
			["SeqSelectFirstDisplayStep", 16, "displayFirstStep", 64],
			["NextLine"], 
			["MidiNoteText", "seqNoteBase", "arrNotes", 16, "displayFirstStep"], 
//			["TXMultiSwitch", "Step on/off", "arrOnOffSteps", 16, {this.updateSlot;}, "displayFirstStep"],
			["SeqPlayRange", "seqStartStep", "seqEndStep", nil, 64, "Pattern steps", {this.updateSlot;}],
			["DividingLine"], 
			["TXPopupAction", "Process", arrProcessSpecs.collect({arg item, i; item.at(0);}), "processTypeInd", 
				{this.buildGuiSpecArray; system.showView;}, 350],
			["ActionButton", "RUN", {arrProcessSpecs.at(this.getSynthArgSpec("processTypeInd")).at(1).value; 
				this.updateSlot; system.showView;}, 70],
			["NextLine"], 
		];
		// add processing gui specs
		guiSpecArray = guiSpecArray ++ arrProcessSpecs.at(this.getSynthArgSpec("processTypeInd")).at(2);
	});
}

startSequencer { 
	var nextStepSize, envSize, outRandOcts, outAdjust, outNote, outVelMin, outVelMax, outVel, 
		outEnvTime, outDelay, randDelay, stepDelay;
	var holdChainCurrentStep, stopSeqFlag, holdSlotNo;
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
			envSize = (this.getSynthArgSpec("arrNoteLengthsX").at(seqCurrentStep))
				/ (this.getSynthArgSpec("arrNoteLengthsY").at(seqCurrentStep));
			outEnvTime = envSize * seqClock.beatDur;
			randDelay = this.getSynthArgSpec("arrRandDelays").at(seqCurrentStep).rand;
			stepDelay = this.getSynthArgSpec("arrDelays").at(seqCurrentStep);
			outDelay = (stepDelay + randDelay).max(0).min(100)  * nextStepSize * seqClock.beatDur / 200;
			// make the noise
			if (	// if step is to be played 
				this.getSynthArgSpec("arrOnOffSteps").at(seqCurrentStep) == 1
					// and the fates allow it
					and: ((this.getSynthArgSpec("arrProbabilities").at(seqCurrentStep)/100 - rand(1.0)).ceil == 1),
			{ 
				// use bundle to allow for latency
				system.server.makeBundle(seqLatency + outDelay, { 
					if (noteOutModule1.notNil, {noteOutModule1.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule2.notNil, {noteOutModule2.createSynthNote(outNote, outVel, outEnvTime)});
					if (noteOutModule3.notNil, {noteOutModule3.createSynthNote(outNote, outVel, outEnvTime)});
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

// removed - can cause crashes if gui is being edited
//						// display new pattern 
//						system.showViewIfModDisplay(this);
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
	if (argData.notNil, {
		arrSlots = argData.deepCopy;
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
	arrSlots.put(this.getSynthArgSpec("slotNo"), this.getSlotData);
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
				if (seqRecordStep > 64, {seqRecordStep = 0;});
			}
		}).play;
	}, {
		// refresh view
		system.showViewIfModDisplay(this);
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

constrainChainStep {
	var holdCurrent, holdStart, holdEnd;	
	// contrain current chain step between start and end steps
	holdCurrent = this.getSynthArgSpec("chainCurrentStep");
	holdStart = this.getSynthArgSpec("chainStartStep");
	holdEnd = this.getSynthArgSpec("chainEndStep");
	this.setSynthArgSpec("chainCurrentStep", holdCurrent.max(holdStart).min(holdEnd));
}

initProcessSpecs {
	var parmSelectGui;
	arrProcSelNames = ["procSelOnOffSteps", "procSelNotes", "procSelRandOctaves", "procSelVelocities", 
		"procSelRandVelocities", "procSelStepLengthsX", "procSelStepLengthsY", 
		"procSelNoteLengthsX", "procSelNoteLengthsY", "procSelProbabilities", "procSelDelays", "procSelRandDelays",
	];
	arrProcSynthArgNames = ["arrOnOffSteps", "arrNotes",  "arrRandOctaves",  "arrVelocities", 
		"arrRandVelocities",  "arrStepLengthsX",  "arrStepLengthsY", 
		"arrNoteLengthsX",  "arrNoteLengthsY",  "arrProbabilities", "arrDelays", "arrRandDelays", 
	];
	parmSelectGui = [
		["DividingLine"],
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
		["TXCheckBox", "On/Off Steps", "procSelOnOffSteps"],
 		["TXCheckBox", "Notes", "procSelNotes"],
		["TXCheckBox", "Rand Octaves", "procSelRandOctaves"],
		["NextLine"],
		["TXCheckBox", "Velocities", "procSelVelocities"],
		["TXCheckBox", "Rand Velocities", "procSelRandVelocities"],
		["TXCheckBox", "Step Lengths X", "procSelStepLengthsX"],
		["NextLine"],
		["TXCheckBox", "Step Lengths Y", "procSelStepLengthsY"],
		["TXCheckBox", "Note Lengths X", "procSelNoteLengthsX"],
		["TXCheckBox", "Note Lengths Y", "procSelNoteLengthsY"],
		["NextLine"],
		["TXCheckBox", "Probabilities", "procSelProbabilities"],
		["TXCheckBox", "Delays", "procSelDelays"],
		["TXCheckBox", "Rand delays", "procSelRandDelays"],
	];
	arrProcessSpecs = [
		["select a process...", 
			{ }, 
			[],
		], 
		["Copy and paste steps once", 
			{ 	var sourceStart, sourceEnd, loSourceIndex, hiSourceIndex, wholeArr, sourceArr, changeArr;
				this.prepareProcess;
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
				["TXRangeSlider", "Copy steps", ControlSpec(1, 64, step:1), "procSourceStart", "procSourceEnd"],
			] ++ parmSelectGui,
		], 
		["Copy and paste steps repeatedly", 
			{ 	var sourceStart, sourceEnd, loSourceIndex, hiSourceIndex, wholeArr, sourceArr, changeArr;
				this.prepareProcess;
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
				["TXRangeSlider", "Copy steps", ControlSpec(1, 64, step:1), "procSourceStart", "procSourceEnd"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
				["TXPopupAction", "Chord/ scale", TXScale.arrScaleNames, "procScaleTypeInd", nil, 400], 
				["NextLine"],
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
				this.updateProcTarget("arrNotes", changeArr);
			}, 
			[
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
				["TXPopupAction", "Chord/ scale", TXScale.arrScaleNames, "procScaleTypeInd", nil, 400], 
				["NextLine"],
				["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", "F#", "G", "G#", "A", "A#", "B"], 
					"procScaleRoot", nil, 140], 
				["NextLine"],
				["TXPopupAction", "Note order", [ "random order", "random selection & order", 
					"forwards", "backwards","forwards-backwards","backwards-forwards"], "procNoteOrderInd", nil, 300], 
				["NextLine"],
				["TXNoteRangeSlider", "Note range", "procRandNoteMin", "procRandNoteMax", nil, true],
				["RefreshButton"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
				["TXRangeSlider", "Source steps", ControlSpec(1, 64, step:1), "procSourceStart", "procSourceEnd"],
				["NextLine"],
				["TXRangeSlider", "Oct Transpose", ControlSpec(-8, 8, step:1), "procOctTranspMin", "procOctTranspMax"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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
				["TXRangeSlider", "Target steps", ControlSpec(1, 64, step:1), "procTargStart", "procTargEnd"],
				["NextLine"],
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

updateProcTarget {arg argSynthArgString, argChangeArray; 
	var outArray;
	outArray = this.getSynthArgSpec(argSynthArgString);
	targetSize.do({ arg i;
		outArray.put(loTargetIndex + i, argChangeArray.at(i));
	});
	this.setSynthArgSpec(argSynthArgString, outArray);
}

}

