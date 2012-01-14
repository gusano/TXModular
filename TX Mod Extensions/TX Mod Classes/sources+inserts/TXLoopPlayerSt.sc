// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLoopPlayerSt : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<arrBufferSpecs;
	classvar	<guiWidth=500;
	
	var <>loopNo = 0;
	var loopFileName = "";
	var showWaveform = 0;
	var loopNumChannels = 0;
	var loopTotalBeats = 1;
	var loopOriginalBPM = 0;
	var displayOption;
	var <>testMIDINote = 69;
	var <>testMIDIVel = 127
	;
	var <>testMIDITime = 8;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Loop Player St";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Loop Start", 1, "modStart", 0],
		["Loop End", 1, "modEnd", 0],
		["Loop Reverse", 1, "modReverse", 0],
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrBufferSpecs = [ ["bufnumLoop", 2048, 2] ];
} // end of method initClass

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	displayOption = "showLoop";
	arrSynthArgSpecs = [
		["out", 0, 0],
		["gate", 1, 0],
		["note", 0, 0],
		["velocity", 0, 0],
		["seqBPM", 0.3979933, \ir], 		// default BPM is set for 120 bpm
		["seqBPMMin", 1, \ir], 		
		["seqBPMMax", 300, \ir], 	
		["speedFactor", 0, \ir],
		["bufnumLoop", 0, \ir],
		["loopNo", 0, \ir],
		["loopTotalBeats", 1, \ir],
		["start", 0, defLagTime],
		["end", 1, defLagTime],
		["reverse", 0, defLagTime],
		["level", 0.5, \ir],
		["envtime", 0, \ir],
		["modStart", 0, defLagTime],
		["modEnd", 0, defLagTime],
		["modReverse", 0, defLagTime],
  	]; 
  	// create looping option
	arrOptions = [0];
	arrOptionData = [
		[	
			["Looped", 
				{arg outRate, bufnumLoop, start, end; 
					BufRd.ar(2, bufnumLoop, 
						Phasor.ar(0, outRate * BufRateScale.kr(bufnumLoop), 
							start * BufFrames.kr(bufnumLoop), end * BufFrames.kr(bufnumLoop)),
						0
					);
				}
			],
			["Single shot", 
				{arg outRate, bufnumLoop, start, end; 
					BufRd.ar(2, bufnumLoop, 
						(Sweep.ar(1, outRate * BufSampleRate.kr(bufnumLoop))
							+ (((start * outRate.sign.max(0)) + (end * outRate.sign.neg.max(0))) 
								* BufFrames.kr(bufnumLoop))
						)
						.min(end * BufFrames.kr(bufnumLoop))
						.max(start * BufFrames.kr(bufnumLoop))
						,0
					);
				}
			]
		]
	];
	synthDefFunc = { 
		arg out, gate, note, velocity, seqBPM, seqBPMMin, seqBPMMax, speedFactor, bufnumLoop, 
			loopNo, loopTotalBeats, start, end, reverse, level, 
			envtime=0, modStart, modEnd, modReverse;
		var outEnv, originalBPM, outBPM, outRate, outFunction, outLoop, sStart, sEnd, rev;
		
		sStart = (start + modStart).max(0).min(1);
		sEnd = (end + modEnd).max(0).min(1);
		rev = (reverse + modReverse).max(0).min(1);
		outEnv = EnvGen.ar(
			Env.new([0,1,1,0],[0.01,1,0.0001],'linear', 2),
			gate, 
			doneAction: 2
		);

		originalBPM = 60  * loopTotalBeats / BufDur.kr(bufnumLoop);
		outBPM =  seqBPMMin + (seqBPM * (seqBPMMax - seqBPMMin));
		outRate =  (2 ** speedFactor) * (outBPM / originalBPM) * (rev-0.5).neg.sign;
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outLoop = outFunction.value(outRate, bufnumLoop, sStart, sEnd) * level * 2;
		// amplitude is vel *  0.00315 approx. == 1 / 127
		OffsetOut.ar(out, outEnv * outLoop * (velocity * 0.007874));
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		// array of loop filenames - beginning with blank loop  - only show stereo files
		["TXPopupAction", "Loop", {["No Loop"]++system.loopFilesStereo
		.collect({arg item, i; 
	//		item.at(0).keep(-50);
			item.at(0).basename;
		})},
			"loopNo", { arg view; this.loopNo = view.value; this.loadLoop(view.value); }
		], 
		["Spacer", 80], 
		["NextLine"], 
		["TXFraction", "Loop start", ControlSpec(0, 1), "start"], 
		["TXFraction", "Loop end", ControlSpec(0, 1), "end"], 
		["SynthOptionPopup", "Loop type", arrOptionData, 0, 210], 
		["TXCheckBox", "Reverse", "reverse"], 
		["DividingLine"], 
		["EZslider", "Level", ControlSpec(0, 1), "level"], 
		["DividingLine"], 
		["TXMinMaxSliderSplit", "Play BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax"], 
		["TXNumberPlusMinus", "Speed factor", ControlSpec(-5, 5, 'lin', 1, 0), "speedFactor"], 
		["DividingLine"], 
		["MIDIListenCheckBox"], 
		["NextLine"], 
		["MIDIChannelSelector"], 
		["NextLine"], 
		["MIDINoteSelector"], 
		["NextLine"], 
		["MIDIVelSelector"], 
		["DividingLine"], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	make buffers, load the synthdef and create the Group for synths to belong to
	this.makeBuffersAndGroup(arrBufferSpecs);
} // end of method init

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Loop", {displayOption = "showLoop"; 
			this.buildGuiSpecArray; system.showView;}, 110, 
			TXColor.white, this.getButtonColour(displayOption == "showLoop")], 
		["Spacer", 3], 
		["ActionButton", "MIDI/ Test", {displayOption = "showMIDI"; 
			this.buildGuiSpecArray; system.showView;}, 110, 
			TXColor.white, this.getButtonColour(displayOption == "showMIDI")], 
		["Spacer", 3], 
		["ActionButton", "Play test loop", {this.createSynthNote(testMIDINote, testMIDIVel, testMIDITime);}, 
			110, TXColor.white, TXColor.sysGuiCol2], 
		["Spacer", 3], 
		["ActionButton", "Stop test loop", {this.allNotesOff;}, 
			110, TXColor.white, TXColor.sysDeleteCol], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showLoop", {
		guiSpecArray = guiSpecArray ++[
			["TXPopupAction", 
				"Stereo loop", {["No Loop"]++system.loopFilesStereo
				.collect({arg item, i; 
			//		item.at(0).keep(-50);
					item.at(0).basename;
				})},
				"loopNo", { arg view; 
					this.loopNo = view.value; 
					this.loadLoop(view.value); 
					{system.showView;}.defer(0.1);	//  refresh view 
				}
			], 
			["TextBarLeft", "Loopbank Settings: ", 140],
			["TextBarLeft", {"Original BPM = " ++ loopOriginalBPM.round(0.01).asString}, 140],
			["TextBarLeft", {"Total beats = " ++ loopTotalBeats.asString}, 140],
			["ActionButton", "Add Loops to Loop Bank", {TXBankBuilder2.addSampleDialog("Loop")}, 200], 
			["ActionButton", "Show", {showWaveform = 1; system.showView;}, 80, TXColor.white, TXColor.sysGuiCol2], 
			["ActionButton", "Hide", {showWaveform = 0; system.showView;}, 80, TXColor.white, TXColor.sysDeleteCol], 
			["NextLine"], 
			["TXSoundFileViewFraction", {loopFileName}, "start", "end", nil, {showWaveform}], 
			["SynthOptionPopup", "Loop type", arrOptionData, 0, 210], 
			["TXCheckBox", "Reverse", "reverse"], 
			["DividingLine"], 
			["EZslider", "Level", ControlSpec(0, 1), "level"], 
			["DividingLine"], 
			["TXMinMaxSliderSplit", "Play BPM", ControlSpec(1, 900), "seqBPM", "seqBPMMin", "seqBPMMax"], 
			["TXNumberPlusMinus", "Speed factor", ControlSpec(-5, 5, 'lin', 1, 0), "speedFactor"], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showMIDI", {
		guiSpecArray = guiSpecArray ++[
			["MIDIListenCheckBox"], 
			["NextLine"], 
			["MIDIChannelSelector"], 
			["NextLine"], 
			["MIDINoteSelector"], 
			["NextLine"], 
			["MIDIVelSelector"], 
			["DividingLine"], 
			["TestLoopVals"], 
			["DividingLine"], 
		];
	});
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
}

extraSaveData { // override default method
	^[loopNo, loopFileName, loopNumChannels, loopTotalBeats];
}

loadExtraData {arg argData;  // override default method
	loopNo = argData.at(0);
	loopFileName = argData.at(1);
	loopNumChannels = argData.at(2);
	loopTotalBeats = argData.at(3);
	this.loadLoop(loopNo);
}

loadLoop { arg argIndex; // method to load loops into buffer
	var holdBuffer, holdLoopInd, holdModCondition;
	Routine.run {
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		// pause
		system.server.sync;
		if (argIndex == 0, {
			// if argIndex is 0, clear the current buffer & filename
			buffers.at(0).zero;
			loopFileName = "";
			loopNumChannels = 0;
			loopTotalBeats = 1;
			loopOriginalBPM = 0;
			// store Total Beats to synthArgSpecs
			this.setSynthArgSpec("loopTotalBeats", loopTotalBeats);
		},{
			// otherwise,  try to load loop.  if it fails, display error message and clear
			holdLoopInd = (argIndex - 1).min(system.loopFilesStereo.size-1);
			holdBuffer = Buffer.read(system.server, system.loopFilesStereo.at(holdLoopInd).at(0), 
				action: { arg argBuffer; 
					{
					//	if file loaded ok
						if (argBuffer.notNil, {
							this.setSynthArgSpec("bufnumLoop", argBuffer.bufnum);
							loopFileName = system.loopFilesStereo.at(holdLoopInd).at(0);
							loopNumChannels = argBuffer.numChannels;
							loopTotalBeats = system.loopFilesStereo.at(holdLoopInd).at(1);
							loopOriginalBPM = (60 * argBuffer.sampleRate * loopTotalBeats)/ argBuffer.numFrames; 
							// store Total Beats to synthArgSpecs
							this.setSynthArgSpec("loopTotalBeats", loopTotalBeats);
						},{
							buffers.at(0).zero;
							loopFileName = "";
							loopNumChannels = 0;
							loopTotalBeats = 1;
							loopOriginalBPM = 0;
							// store Total Beats to synthArgSpecs
							this.setSynthArgSpec("loopTotalBeats", loopTotalBeats);
							TXInfoScreen.new("Invalid Loop File" 
							  ++ system.loopFilesStereo.at(holdLoopInd).at(0));
						});
					}.defer;	// defer because gui process
				},
				// pass buffer number
				bufnum: buffers.at(0).bufnum
			);
		});
		// remove condition from load queue
		system.holdLoadQueue.removeCondition(holdModCondition);
	}; // end of Routine.run
} // end of method loadLoop

}

