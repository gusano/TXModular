// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSamplePlayerSt5 : TXModuleBase {

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
	classvar	<guiHeight=450;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	timeSpec;
	
	var <>sampleNo = 0;
	var sampleFileName = "";
	var showWaveform = 0;
	var sampleNumChannels = 0;
	var sampleFreq = 440;
	var displayOption;
	var ratioView;
	var	envView;
	var <>testMIDINote = 69;
	var <>testMIDIVel = 100;
	var <>testMIDITime = 1;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Sample Player St";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Sample Start", 1, "modStart", 0],
		["Sample End", 1, "modEnd", 0],
		["Sample Reverse", 1, "modReverse", 0],
		["Pitch bend", 1, "modPitchbend", 0],
		["Delay", 1, "modDelay", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain level", 1, "modSustain", 0],
		["Sustain time", 1, "modSustainTime", 0],
		["Release", 1, "modRelease", 0],
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrBufferSpecs = [ ["bufnumSample", 2048, 2] ];
	timeSpec = ControlSpec(0.001, 20);
} // end of method initClass

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	displayOption = "showSample";
	arrSynthArgSpecs = [
		["out", 0, 0],
		["gate", 1, 0],
		["note", 0, 0],
		["velocity", 0, 0],
		["keytrack", 1, \ir],
		["transpose", 0, \ir],
		["pitchbend", 0.5, defLagTime],
		["pitchbendMin", -2, defLagTime],
		["pitchbendMax", 2, defLagTime],
		["bufnumSample", 0, \ir],
		["sampleNo", 0, \ir],
		["sampleFreq", 440, \ir],
		["start", 0, defLagTime],
		["end", 1, defLagTime],
		["reverse", 0, defLagTime],
		["level", 0.5, defLagTime],
		["envtime", 0, \ir],
		["delay", 0, \ir],
		["attack", 0.001, \ir],
		["attackMin", 0, \ir],
		["attackMax", 5, \ir],
		["decay", 0.05, \ir],
		["decayMin", 0, \ir],
		["decayMax", 5, \ir],
		["sustain", 1, \ir],
		["sustainTime", 0.2, \ir],
		["sustainTimeMin", 0, \ir],
		["sustainTimeMax", 5, \ir],
		["release", 0.01, \ir],
		["releaseMin", 0, \ir],
		["releaseMax", 5, \ir],
		["intKey", 0, \ir],
		["modStart", 0, defLagTime],
		["modEnd", 0, defLagTime],
		["modReverse", 0, defLagTime],
		["modPitchbend", 0, defLagTime],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],
  	]; 
  	// create looping option
	arrOptions = [0,0,0,0];
	arrOptionData = [
		[	["Single shot", 
				{arg outRate, bufnumSample, start, end; 
					BufRd.ar(2, bufnumSample, 
						(Sweep.ar(1, outRate * BufSampleRate.kr(bufnumSample))
							+ (((start * outRate.sign.max(0)) + (end * outRate.sign.neg.max(0))) * BufFrames.kr(bufnumSample))
						)
						.min(end * BufFrames.kr(bufnumSample))
						.max(start * BufFrames.kr(bufnumSample))
						,0
					);
				}
			],
			["Looped", 
				{arg outRate, bufnumSample, start, end; 
					BufRd.ar(2, bufnumSample, 
						Phasor.ar(0, outRate * BufRateScale.kr(bufnumSample), start * BufFrames.kr(bufnumSample), 
							end * BufFrames.kr(bufnumSample))
					);
				}
			],
//			["X-Fade Looped", 
//				{arg outRate, bufnumSample, start, end; 
//				Mix.new(
//					BufRd.ar(2, bufnumSample, 
//						Phasor.ar(1, outRate * BufRateScale.kr(0), start * BufFrames.kr(0), end * BufFrames.kr(0), 
//							[start, (end-start)/2] * BufFrames.kr(0))
//						
//					) * SinOsc.kr(outRate * BufRateScale.kr(0), [0,pi/2]).abs;
//				)}
//			]
		],
		// Intonation
		TXIntonation.arrOptionData,
		[	
			["linear", 'linear'],
//invalid		["exponential", 'exponential'],
			["sine", 'sine'],
			["welch", 'welch'],
//invalid		["step", 'step'],
			["slope +10 ", 10],
			["slope +9 ", 9],
			["slope +8 ", 8],
			["slope +7 ", 7],
			["slope +6 ", 6],
			["slope +5 ", 5],
			["slope +4 ", 4],
			["slope +3 ", 3],
			["slope +2 ", 2],
			["slope +1 ", 1],
			["slope -1", -1],
			["slope -2 ", -2],
			["slope -3 ", -3],
			["slope -4 ", -4],
			["slope -5 ", -5],
			["slope -6 ", -6],
			["slope -7 ", -7],
			["slope -8 ", -8],
			["slope -9 ", -9],
			["slope -10 ", -10]
		],
		[	
			["Sustain", 
				{arg del, att, dec, sus, sustime, rel, envCurve; 
					Env.dadsr(del, att, dec, sus, rel, 1, envCurve);
				}
			],
			["Fixed Length", 
				{arg del, att, dec, sus, sustime, rel, envCurve; 
					Env.new([0, 0, 1, sus, sus, 0], [del, att, dec, sustime, rel], envCurve, nil);
				}
			]
		],
	];
	synthDefFunc = { 
		arg out, gate, note, velocity, keytrack, transpose, pitchbend, pitchbendMin, pitchbendMax, 
			bufnumSample, sampleNo, sampleFreq, start, end, reverse, level, 
			envtime=0, delay, attack, attackMin, attackMax, decay, decayMin, decayMax, sustain, 
			sustainTime, sustainTimeMin, sustainTimeMax, release, releaseMin, releaseMax, intKey, 
			modStart, modEnd, modReverse, modPitchbend, modDelay, modAttack, modDecay, 
			modSustain, modSustainTime, modRelease;
		var outEnv, envFunction, outFreq, intonationFunc, pbend, outRate, outFunction, outSample, envCurve, sStart, sEnd, rev, 
			del, att, dec, sus, sustime, rel;
		
		sStart = (start + modStart).max(0).min(1);
		sEnd = (end + modEnd).max(0).min(1);
		rev = (reverse + modReverse).max(0).min(1);
		del = (delay + modDelay).max(0).min(1);
		att = (attackMin + ((attackMax - attackMin) * (attack + modAttack))).max(0.001).min(20);
		dec = (decayMin + ((decayMax - decayMin) * (decay + modDecay))).max(0.001).min(20);
		sus = (sustain + modSustain).max(0).min(1);
		sustime = (sustainTimeMin + 
			((sustainTimeMax - sustainTimeMin) * (sustainTime + modSustainTime))).max(0.001).min(20);
		rel = (releaseMin + ((releaseMax - releaseMin) * (release + modRelease))).max(0.001).min(20);
		envCurve = this.getSynthOption(2);
		envFunction = this.getSynthOption(3);
		outEnv = EnvGen.kr(
			envFunction.value(del, att, dec, sus, sustime, rel, envCurve),
			gate, 
			doneAction: 2
		);
		intonationFunc = this.getSynthOption(1);
		outFreq = (intonationFunc.value((note + transpose), intKey) * keytrack) + ((sampleFreq.cpsmidi 
			+ transpose).midicps * (1-keytrack));
		pbend = pitchbendMin + ((pitchbendMax - pitchbendMin) * (pitchbend + modPitchbend).max(0).min(1));
		outRate = ((outFreq *  (2 ** (pbend /12))) / sampleFreq) * (rev-0.5).neg.sign;
		outFunction = this.getSynthOption(0);
		outSample = outFunction.value(outRate, bufnumSample, sStart, sEnd) * level * 2;
		// amplitude is vel *  0.00315 approx. == 1 / 127
		Out.ar(out, outEnv * outSample * (velocity * 0.007874));
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Plot envelope", {this.envPlot;}],
		// array of sample filenames - beginning with blank sample  - only show stereo files
		["TXPopupAction", "Stereo sample", {["No Sample"]++system.sampleBankStereo
		.collect({arg item, i; 
	//		item.at(0).keep(-50);
			item.at(0).basename;
		})},
			"sampleNo", { arg view; this.sampleNo = view.value; this.loadSample(view.value); }
		], 
		["TXRangeSlider", "Play Range", ControlSpec(0, 1), "start", "end"], 
		["SynthOptionPopup", "Loop type", arrOptionData, 0, 210], 
		["TXCheckBox", "Reverse", "reverse"], 
		["EZslider", "Level", ControlSpec(0, 1), "level"], 
		["MIDIListenCheckBox"], 
		["MIDIChannelSelector"], 
		["MIDINoteSelector"], 
		["MIDIVelSelector"], 
		["TXCheckBox", "Keyboard tracking", "keytrack"], 
		["Transpose"], 
		["TXMinMaxSliderSplit", "Pitch bend", 
			ControlSpec(-48, 48), "pitchbend", "pitchbendMin", "pitchbendMax"], 
		["PolyphonySelector"],
		["TestNoteVals"], 
		["TXEnvDisplay", {this.envViewValues;}, {arg view; envView = view;}],
		["EZslider", "Pre-Delay", ControlSpec(0,1), "delay", {this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Attack", timeSpec, "attack", "attackMin", "attackMax",{this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Decay", timeSpec, "decay", "decayMin", "decayMax",{this.updateEnvView;}], 
		["EZslider", "Sustain level", ControlSpec(0, 1), "sustain", {this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTime", "sustainTimeMin", 
			"sustainTimeMax",{this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Release", timeSpec, "release", "releaseMin", "releaseMax",{this.updateEnvView;}], 
		["SynthOptionPopup", "Curve", arrOptionData, 2, 200, {system.showView;}], 
		["SynthOptionPopup", "Env. Type", arrOptionData, 3, 200], 
		["SynthOptionPopup", "Intonation", arrOptionData, 1, nil, 
			{arg view; this.updateIntString(view.value)}], 
		["TXStaticText", "Note ratios", 
			{TXIntonation.arrScalesText.at(arrOptions.at(1));}, 
				{arg view; ratioView = view}],
		["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", 
			"F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 140], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	make buffers, load the synthdef and create the Group for synths to belong to
	this.makeBuffersAndGroup(arrBufferSpecs);
} // end of method init

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Sample", {displayOption = "showSample"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showSample")],
		["Spacer", 3], 
		["ActionButton", "MIDI/ Note", {displayOption = "showMIDI"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showMIDI")], 
		["Spacer", 3], 
		["ActionButton", "Envelope", {displayOption = "showEnv"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showEnv")], 
		["NextLine"], 
		["ActionButton", "Intonation", {displayOption = "showIntonation"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showIntonation")], 
		["Spacer", 3], 
		["ActionButton", "Play test note", {this.createSynthNote(testMIDINote, testMIDIVel, testMIDITime);}, 
			130, TXColor.white, TXColor.sysGuiCol2], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showSample", {
		guiSpecArray = guiSpecArray ++[
			// array of sample filenames - beginning with blank sample  - only show stereo files
			["TXPopupAction", "Stereo sample", {["No Sample"]++system.sampleBankStereo
			.collect({arg item, i; 
		//		item.at(0).keep(-50);
				item.at(0).basename;
			})},
				"sampleNo", { arg view; 
					this.sampleNo = view.value; 
					this.loadSample(view.value); 
					{system.showView;}.defer(0.2);   //  refresh view 
				}
			], 
			["Spacer", 80], 
			["ActionButton", "Add Samples to Sample Bank", {TXBankBuilder2.addSampleDialog("Sample")}, 200], 
			["ActionButton", "Show", {showWaveform = 1; system.showView;}, 80, TXColor.white, TXColor.sysGuiCol2], 
			["ActionButton", "Hide", {showWaveform = 0; system.showView;}, 80, TXColor.white, TXColor.sysDeleteCol], 
			["NextLine"], 
			["TXSoundFileViewRange", {sampleFileName}, "start", "end", nil, {showWaveform}], 
			["SynthOptionPopup", "Loop type", arrOptionData, 0, 210], 
			["TXCheckBox", "Reverse", "reverse"], 
			["NextLine"], 
			["EZslider", "Level", ControlSpec(0, 1), "level"], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showEnv", {
		guiSpecArray = guiSpecArray ++[
			["TXPresetPopup", "Env presets", 
				TXEnvPresets.arrEnvPresets(this, 2, 3).collect({arg item, i; item.at(0)}), 
				TXEnvPresets.arrEnvPresets(this, 2, 3).collect({arg item, i; item.at(1)})
			],
			["TXEnvDisplay", {this.envViewValues;}, {arg view; envView = view;}],
			["NextLine"], 
			["EZslider", "Pre-Delay", ControlSpec(0,1), "delay", {this.updateEnvView;}], 
			["TXMinMaxSliderSplit", "Attack", timeSpec, "attack", "attackMin", "attackMax",{this.updateEnvView;}], 
			["TXMinMaxSliderSplit", "Decay", timeSpec, "decay", "decayMin", "decayMax",{this.updateEnvView;}], 
			["EZslider", "Sustain level", ControlSpec(0, 1), "sustain", {this.updateEnvView;}], 
			["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTime", "sustainTimeMin", 
				"sustainTimeMax",{this.updateEnvView;}], 
			["TXMinMaxSliderSplit", "Release", timeSpec, "release", "releaseMin", "releaseMax",{this.updateEnvView;}], 
			["NextLine"], 
			["SynthOptionPopup", "Curve", arrOptionData, 2, 200, {system.showView;}], 
			["NextLine"], 
			["SynthOptionPopup", "Env. Type", arrOptionData, 3, 200], 
			["Spacer", 4], 
			["ActionButton", "Plot", {this.envPlot;}],
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
			["TXCheckBox", "Keyboard tracking", "keytrack"], 
			["DividingLine"], 
			["Transpose"], 
			["DividingLine"], 
			["TXMinMaxSliderSplit", "Pitch bend", 
				ControlSpec(-48, 48), "pitchbend", "pitchbendMin", "pitchbendMax"], 
			["DividingLine"], 
			["PolyphonySelector"], 
			["DividingLine"], 
			["TestNoteVals"], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showIntonation", {
		guiSpecArray = guiSpecArray ++[
			["SynthOptionPopup", "Intonation", arrOptionData, 1, nil, 
				{arg view; this.updateIntString(view.value)}], 
			["TXStaticText", "Note ratios", 
				{TXIntonation.arrScalesText.at(arrOptions.at(1));}, 
				{arg view; ratioView = view}],
			["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", 
				"F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 140], 
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
	^[sampleNo, sampleFileName, sampleNumChannels, sampleFreq, testMIDINote, testMIDIVel, testMIDITime];
}

loadExtraData {arg argData;  // override default method
	sampleNo = argData.at(0);
	sampleFileName = argData.at(1);
	sampleNumChannels = argData.at(2);
	sampleFreq = argData.at(3);
	testMIDINote = argData.at(4);
	testMIDIVel = argData.at(5);
	testMIDITime = argData.at(6);
	this.loadSample(sampleNo);
}

loadSample { arg argIndex; // method to load samples into buffer
	var holdBuffer, holdSampleInd, holdModCondition;
	Routine.run {
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		// pause
		system.server.sync;
//		0.1.wait;
		// first reset play range
		this.resetPlayRange;
		if (argIndex == 0, {
			// if argIndex is 0, clear the current buffer & filename
			buffers.at(0).zero;
			sampleFileName = "";
			sampleNumChannels = 0;
			sampleFreq = 440;
			// store Freq to synthArgSpecs
			this.setSynthArgSpec("sampleFreq", sampleFreq);
		},{
			// otherwise,  try to load sample.  if it fails, display error message and clear
			holdSampleInd = (argIndex - 1).min(system.sampleBankStereo.size-1);
			holdBuffer = Buffer.read(system.server, system.sampleBankStereo.at(holdSampleInd).at(0), 
				action: { arg argBuffer; 
					{
					//	if file loaded ok
						if (argBuffer.notNil, {
							this.setSynthArgSpec("bufnumSample", argBuffer.bufnum);
							sampleFileName = system.sampleBankStereo.at(holdSampleInd).at(0);
							sampleNumChannels = argBuffer.numChannels;
							sampleFreq = system.sampleBankStereo.at(holdSampleInd).at(1);
							// store Freq to synthArgSpecs
							this.setSynthArgSpec("sampleFreq", sampleFreq);
						},{
							buffers.at(0).zero;
							sampleFileName = "";
							sampleNumChannels = 0;
							sampleFreq = 440;
							// store Freq to synthArgSpecs
							this.setSynthArgSpec("sampleFreq", sampleFreq);
							TXInfoScreen.new("Invalid Sample File" 
							  ++ system.sampleBankStereo.at(holdSampleInd).at(0));
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
} // end of method loadSample

resetPlayRange {
	this.setSynthArgSpec("start", 0);
	this.setSynthArgSpec("end", 1);
}

updateIntString{arg argIndex; 
	if (ratioView.notNil, {
		if (ratioView.notClosed, {
			ratioView.string = TXIntonation.arrScalesText.at(argIndex);
		});
	});
}

envPlot {
	var del, att, dec, sus, sustime, rel, envCurve;
	del = this.getSynthArgSpec("delay");
	att = this.getSynthArgSpec("attack");
	dec = this.getSynthArgSpec("decay");
	sus = this.getSynthArgSpec("sustain");
	sustime = this.getSynthArgSpec("sustainTime");
	rel = this.getSynthArgSpec("release");
	envCurve = this.getSynthOption(2);
	Env.new([0, 0, 1, sus, sus, 0], [del, att, dec, sustime, rel], envCurve, nil).plot;
}

envViewValues {
	var attack, attackMin, attackMax, decay, decayMin, decayMax, sustain;
	var sustainTime, sustainTimeMin, sustainTimeMax, release, releaseMin, releaseMax;
	var del, att, dec, sus, sustime, rel;
	var arrTimesNorm, arrTimesNormedSummed;

	del = this.getSynthArgSpec("delay");
	attack = this.getSynthArgSpec("attack");
	attackMin = this.getSynthArgSpec("attackMin");
	attackMax = this.getSynthArgSpec("attackMax");
	att = attackMin + ((attackMax - attackMin) * attack);
	decay = this.getSynthArgSpec("decay");
	decayMin = this.getSynthArgSpec("decayMin");
	decayMax = this.getSynthArgSpec("decayMax");
	dec = decayMin + ((decayMax - decayMin) * decay);
	sus = this.getSynthArgSpec("sustain");
	sustainTime = this.getSynthArgSpec("sustainTime");
	sustainTimeMin = this.getSynthArgSpec("sustainTimeMin");
	sustainTimeMax = this.getSynthArgSpec("sustainTimeMax");
	sustime = sustainTimeMin + ((sustainTimeMax - sustainTimeMin) * sustainTime);
	release = this.getSynthArgSpec("release");
	releaseMin = this.getSynthArgSpec("releaseMin");
	releaseMax = this.getSynthArgSpec("releaseMax");
	rel = releaseMin + ((releaseMax - releaseMin) * release);

	arrTimesNorm = [0, del, att, dec, sustime, rel].normalizeSum;
	arrTimesNorm.size.do({ arg i;
		arrTimesNormedSummed = arrTimesNormedSummed.add(arrTimesNorm.copyRange(0, i).sum);
	});
	^[arrTimesNormedSummed, [0, 0, 1, sus, sus, 0]].asFloat;
}

updateEnvView {
	if (envView.class == SCEnvelopeView, {
		if (envView.notClosed, {
			6.do({arg i;
				envView.setEditable(i, true);
			});
			envView.value = this.envViewValues;
			6.do({arg i;
				envView.setEditable(i, false);
			});
		});
	});
}

}

