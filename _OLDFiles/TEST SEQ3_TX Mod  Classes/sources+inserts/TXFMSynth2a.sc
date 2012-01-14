// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

/*

NOTE - THIS IS A CUT-DOWN VERSION

*/

TXFMSynth2a : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=450;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	timeSpec;
	classvar	arrFreqRangePresets;

	var 	displayOption;
	var 	displayOperator;
	var 	ratioView;
	var	envView, envView2;
	var 	<>testMIDINote = 69;
	var 	<>testMIDIVel = 100;
	var 	<>testMIDITime = 1;
	var	arrEnvPresetNames, arrEnvPresetActions;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "FM Synth2a";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Pitch bend", 1, "modPitchbend", 0],
		["Op1 Freq Ratio", 1, "mmOp1FreqRatio", 0],
		["Op1 Phase",1, "mmOp1Phase", 0],
		["Op1 Amp",1, "mmOp1Amp", 0],
		["Op2 Freq Ratio", 1, "mmOp2FreqRatio", 0],
		["Op2 Phase",1, "mmOp2Phase", 0],
		["Op2 Amp",1, "mmOp2Amp", 0],
		["Op3 Freq Ratio", 1, "mmOp3FreqRatio", 0],
		["Op3 Phase",1, "mmOp3Phase", 0],
		["Op3 Amp",1, "mmOp3Amp", 0],
		["Op4 Freq Ratio", 1, "mmOp4FreqRatio", 0],
		["Op4 Phase",1, "mmOp4Phase", 0],
		["Op4 Amp",1, "mmOp4Amp", 0],
		["Op5 Freq Ratio", 1, "mmOp5FreqRatio", 0],
		["Op5 Phase",1, "mmOp5Phase", 0],
		["Op5 Amp",1, "mmOp5Amp", 0],
		["Op6 Freq Ratio", 1, "mmOp6FreqRatio", 0],
		["Op6 Phase",1, "mmOp6Phase", 0],
		["Op6 Amp",1, "mmOp6Amp", 0],
		["Delay", 1, "modDelay", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain level", 1, "modSustain", 0],
		["Sustain time", 1, "modSustainTime", 0],
		["Release", 1, "modRelease", 0],

	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	timeSpec = ControlSpec(0.001, 20);

} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	displayOption = "showOperators";
	displayOperator = "showOp1";
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
		["level", 0.5, defLagTime],

		["op1FreqRatio", 1, defLagTime],
		["op1Phase", 0, defLagTime],
		["op1Amp", 1, defLagTime],
		["op2FreqRatio", 1, defLagTime],
		["op2Phase", 0, defLagTime],
		["op2Amp", 1, defLagTime],
		["op3FreqRatio", 1, defLagTime],
		["op3Phase", 0, defLagTime],
		["op3Amp", 1, defLagTime],
		["op4FreqRatio", 1, defLagTime],
		["op4Phase", 0, defLagTime],
		["op4Amp", 1, defLagTime],
		["op5FreqRatio", 1, defLagTime],
		["op5Phase", 0, defLagTime],
		["op5Amp", 1, defLagTime],
		["op6FreqRatio", 1, defLagTime],
		["op6Phase", 0, defLagTime],
		["op6Amp", 1, defLagTime],
		["modFM_11", 0, defLagTime],
		["modFM_12", 0, defLagTime],
		["modFM_13", 0, defLagTime],
		["modFM_14", 0, defLagTime],
		["modFM_15", 0, defLagTime],
		["modFM_16", 0, defLagTime],
		["modFM_21", 0, defLagTime],
		["modFM_22", 0, defLagTime],
		["modFM_23", 0, defLagTime],
		["modFM_24", 0, defLagTime],
		["modFM_25", 0, defLagTime],
		["modFM_26", 0, defLagTime],
		["modFM_31", 0, defLagTime],
		["modFM_32", 0, defLagTime],
		["modFM_33", 0, defLagTime],
		["modFM_34", 0, defLagTime],
		["modFM_35", 0, defLagTime],
		["modFM_36", 0, defLagTime],
		["modFM_41", 0, defLagTime],
		["modFM_42", 0, defLagTime],
		["modFM_43", 0, defLagTime],
		["modFM_44", 0, defLagTime],
		["modFM_45", 0, defLagTime],
		["modFM_46", 0, defLagTime],
		["modFM_51", 0, defLagTime],
		["modFM_52", 0, defLagTime],
		["modFM_53", 0, defLagTime],
		["modFM_54", 0, defLagTime],
		["modFM_55", 0, defLagTime],
		["modFM_56", 0, defLagTime],
		["modFM_61", 0, defLagTime],
		["modFM_62", 0, defLagTime],
		["modFM_63", 0, defLagTime],
		["modFM_64", 0, defLagTime],
		["modFM_65", 0, defLagTime],
		["modFM_66", 0, defLagTime],

		["outLevelOp1", 1, defLagTime],
		["outLevelOp2", 0, defLagTime],
		["outLevelOp3", 0, defLagTime],
		["outLevelOp4", 0, defLagTime],
		["outLevelOp5", 0, defLagTime],
		["outLevelOp6", 0, defLagTime],
		
		["envtime", 0, \ir],
		["delay", 0, \ir],
		["attack", 0.005, \ir],
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

		["modPitchbend", 0, defLagTime],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],

//		["modDelay2", 0, \ir],
//		["modAttack2", 0, \ir],
//		["modDecay2", 0, \ir],
//		["modSustain2", 0, \ir],
//		["modSustainTime2", 0, \ir],
//		["modRelease2", 0, \ir],

		["mmOp0FreqRatio", 0, defLagTime],
		["mmOp0Phase", 0, defLagTime],
		["mmOp0Amp", 0, defLagTime],
		["mmOp2FreqRatio", 0, defLagTime],
		["mmOp2Phase", 0, defLagTime],
		["mmOp2Amp", 0, defLagTime],
		["mmOp3FreqRatio", 0, defLagTime],
		["mmOp3Phase", 0, defLagTime],
		["mmOp3Amp", 0, defLagTime],
		["mmOp4FreqRatio", 0, defLagTime],
		["mmOp4Phase", 0, defLagTime],
		["mmOp4Amp", 0, defLagTime],
		["mmOp5FreqRatio", 0, defLagTime],
		["mmOp5Phase", 0, defLagTime],
		["mmOp5Amp", 0, defLagTime],
		["mmOp6FreqRatio", 0, defLagTime],
		["mmOp6Phase", 0, defLagTime],
		["mmOp6Amp", 0, defLagTime],
		
	]; 
	arrOptions = 0! 18;
	arrOptionData = [
		TXIntonation.arrOptionData,
		[
			["Off", { 0; }],
			["On", TXLFO.lfoFunction],
		],
		TXLFO.arrOptionData,
		TXLFO.arrLFOOutputRanges,
		[
			["Off", { 0; }],
			["On", TXLFO.lfoFunction],
		],
		TXLFO.arrOptionData,
		TXLFO.arrLFOOutputRanges,
		// amp env
		TXEnvLookup.arrSlopeOptionData,
		TXEnvLookup.arrSustainOptionData,
		// env 2
		TXEnvLookup.arrSlopeOptionData,
		TXEnvLookup.arrSustainOptionData,
		[	["Off", { 0; }],
			["On", {arg envFunction, del, att, dec, sus, sustime, rel, envCurve, gate;  
				EnvGen.ar(
					envFunction.value(del, att, dec, sus, sustime, rel, envCurve),
					gate, 
					doneAction: 0
				);
			}],
		],
		// env 3
		TXEnvLookup.arrSlopeOptionData,
		TXEnvLookup.arrSustainOptionData,
		[	["Off", { 0; }],
			["On", {arg envFunction, del, att, dec, sus, sustime, rel, envCurve, gate;  
				EnvGen.ar(
					envFunction.value(del, att, dec, sus, sustime, rel, envCurve),
					gate, 
					doneAction: 0
				);
			}],
		],
		// env 4
		TXEnvLookup.arrSlopeOptionData,
		TXEnvLookup.arrSustainOptionData,
		[	["Off", { 0; }],
			["On", {arg envFunction, del, att, dec, sus, sustime, rel, envCurve, gate;  
				EnvGen.ar(
					envFunction.value(del, att, dec, sus, sustime, rel, envCurve),
					gate, 
					doneAction: 0
				);
			}],
		],
	];
	synthDefFunc = { 
		arg out, gate, note, velocity, keytrack, transpose, pitchbend, pitchbendMin, pitchbendMax, 
			level, 

			op1FreqRatio, op1Phase, op1Amp, 
			op2FreqRatio, op2Phase, op2Amp, 
			op3FreqRatio, op3Phase, op3Amp, 
			op4FreqRatio, op4Phase, op4Amp, 
			op5FreqRatio, op5Phase, op5Amp, 
			op6FreqRatio, op6Phase, op6Amp, 
			modFM_11, modFM_12, modFM_13, modFM_14, modFM_15, modFM_16, 
			modFM_21, modFM_22, modFM_23, modFM_24, modFM_25, modFM_26, 
			modFM_31, modFM_32, modFM_33, modFM_34, modFM_35, modFM_36, 
			modFM_41, modFM_42, modFM_43, modFM_44, modFM_45, modFM_46, 
			modFM_51, modFM_52, modFM_53, modFM_54, modFM_55, modFM_56, 
			modFM_61, modFM_62, modFM_63, modFM_64, modFM_65, modFM_66, 
			outLevelOp1, outLevelOp2, outLevelOp3, outLevelOp4, outLevelOp5, outLevelOp6,

			envtime, delay, attack, attackMin, attackMax, decay, decayMin, decayMax, sustain, 
			sustainTime, sustainTimeMin, sustainTimeMax, release, releaseMin, releaseMax, 
			intKey, 
			
			modPitchbend,  
			modDelay, modAttack, modDecay, modSustain, modSustainTime, modRelease, 
//			modDelay2, modAttack2, modDecay2, modSustain2, modSustainTime2, modRelease2,

			mmOp1FreqRatio, mmOp1Phase, mmOp1Amp, 
			mmOp2FreqRatio, mmOp2Phase, mmOp2Amp, 
			mmOp3FreqRatio, mmOp3Phase, mmOp3Amp, 
			mmOp4FreqRatio, mmOp4Phase, mmOp4Amp, 
			mmOp5FreqRatio, mmOp5Phase, mmOp5Amp, 
			mmOp6FreqRatio, mmOp6Phase, mmOp6Amp;

		var outEnv, envFunction, envCurve, 
//			outEnv2, envFunction2, envCurve2, envGenFunction2,
//			outEnv3, envFunction3, envCurve3, envGenFunction3,
//			outEnv4, envFunction4, envCurve4, envGenFunction4,
			intonationFunc, outFreq, pbend, 
			arrFMCtls, arrFMMods, arrFMLevels, outFM,
			del, att, dec, sus, sustime, rel, 
			del2, att2, dec2, sus2, sustime2, rel2, 
			del3, att3, dec3, sus3, sustime3, rel3, 
			del4, att4, dec4, sus4, sustime4, rel4, 
			lfo1Function, outLFO1, lfo2Function, outLFO2, lfo1FadeInTime, lfo1FadeInCurve, 
			lfo2FadeInTime, lfo2FadeInCurve, 
			randomValue1, randomValue2, 
			timeControlSpec, sumVelocity, sourceIndexArray, destIndexArray, scaleIndexArray; 

		del = (delay + modDelay).max(0).min(1);
		att = (attackMin + ((attackMax - attackMin) * (attack + modAttack))).max(0.001).min(20);
		dec = (decayMin + ((decayMax - decayMin) * (decay + modDecay))).max(0.001).min(20);
		sus = (sustain + modSustain).max(0).min(1);
		sustime = (sustainTimeMin + 
			((sustainTimeMax - sustainTimeMin) * (sustainTime + modSustainTime))).max(0.001).min(20);
		rel = (releaseMin + ((releaseMax - releaseMin) * (release + modRelease))).max(0.001).min(20);
		envCurve = this.getSynthOption(7);
		envFunction = this.getSynthOption(8);
		outEnv = EnvGen.ar(
			envFunction.value(del, att, dec, sus, sustime, rel, envCurve),
			gate, 
			doneAction: 2
		);


		pbend = pitchbendMin + ((pitchbendMax - pitchbendMin) 
				* (pitchbend + modPitchbend).max(0).min(1));

		intonationFunc = this.getSynthOption(0);
		outFreq = (intonationFunc.value(
			(note + transpose), intKey) * keytrack) + ((48 + transpose).midicps * (1-keytrack));

//		waveFunction = {arg outFreq, waveModify1, waveModify2; 
//			(((1-waveModify1) * SinOsc.ar(outFreq)) + (waveModify1 * Pulse.ar(outFreq, 0.5))); };
//		outWave = waveFunction.value(
//			outFreq *  (2 ** (pbend /12)), 
//			0.5, 
//			0.5
//		);
				

		arrFMCtls = [

			[ (mmOp1FreqRatio + op1FreqRatio) * outFreq, 
			(mmOp1Phase + op1Phase).max(0).min(1),(mmOp1Amp + op1Amp).max(0).min(1),],
			[ (mmOp2FreqRatio + op2FreqRatio) * outFreq, 
			(mmOp2Phase + op2Phase).max(0).min(1),(mmOp2Amp + op2Amp).max(0).min(1),],
			[ (mmOp3FreqRatio + op3FreqRatio) * outFreq, 
			(mmOp3Phase + op3Phase).max(0).min(1),(mmOp3Amp + op3Amp).max(0).min(1),],
			[ (mmOp4FreqRatio + op4FreqRatio) * outFreq, 
			(mmOp4Phase + op4Phase).max(0).min(1),(mmOp4Amp + op4Amp).max(0).min(1),],
			[ (mmOp5FreqRatio + op5FreqRatio) * outFreq, 
			(mmOp5Phase + op5Phase).max(0).min(1),(mmOp5Amp + op5Amp).max(0).min(1),],
			[ (mmOp6FreqRatio + op6FreqRatio) * outFreq, 
			(mmOp6Phase + op6Phase).max(0).min(1),(mmOp6Amp + op6Amp).max(0).min(1),],

// testing xxxx 
//		[XLine.kr(300, 1200, 2), 0, Line.kr(1, 0, 3)],
//		[XLine.kr(600, 300, 2), 0, Line.kr(1, 0, 3)],
//		[311, 0, 1],
//		[702, 0, Line.kr(0, 1, 1)],
//		[1100, 0, 1],
//		[1010, 0, 1],

		];
		arrFMMods = [
			[ modFM_11, modFM_12, modFM_13, modFM_14, modFM_15, modFM_16, ],
			[ modFM_21, modFM_22, modFM_23, modFM_24, modFM_25, modFM_26, ],
			[ modFM_31, modFM_32, modFM_33, modFM_34, modFM_35, modFM_36, ],
			[ modFM_41, modFM_42, modFM_43, modFM_44, modFM_45, modFM_46, ],
			[ modFM_51, modFM_52, modFM_53, modFM_54, modFM_55, modFM_56, ],
			[ modFM_61, modFM_62, modFM_63, modFM_64, modFM_65, modFM_66, ],

// testing xxxx 
//		[Line.kr(0, 0.001, 4), Line.kr(0.1, 0, 4), 0, 0, 0.1, 0.1],
//		[Line.kr(0, 6, 4), 0, 0, 0, 0, 0],
//		[1, 0.5, 0.01, 0, 0, 0],
//		[1, 0.5, 0.5, 0.01, 0, 0],
//		[0, 0, 0, 0, 0, 0],
//		[0, 0, 0, 0, 0, 0]

		];

		arrFMLevels = [outLevelOp1, outLevelOp2, outLevelOp3, outLevelOp4, outLevelOp5, outLevelOp6];
		
		outFM = Mix.new(FM7.ar(arrFMCtls, arrFMMods) * arrFMLevels);

		sumVelocity = ((velocity * 0.007874)).max(0).min(1);
		// amplitude is vel *  0.007874 approx. == 1 / 127

// testing xxxx - level doesn't seem to be working now
//		Out.ar(out, outEnv * outFM * level * sumVelocity);
		Out.ar(out, outEnv * outFM  );

	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
	
// xxxxx	 update buildactionspecs
// xxxxx	 update buildactionspecs
// xxxxx	 update buildactionspecs

		["TestNoteVals"], 
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
		["SynthOptionPopup", "Intonation", arrOptionData, 1, nil, 
			{arg view; this.updateIntString(view.value)}], 
		["TXStaticText", "Note ratios", 
			{TXIntonation.arrScalesText.at(arrOptions.at(1));}, 
				{arg view; ratioView = view}],
		["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", 
			"F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 140], 

		["EZslider", "Pre-Delay", ControlSpec(0,1), "delay", {this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Attack", timeSpec, "attack", "attackMin", "attackMax",{this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Decay", timeSpec, "decay", "decayMin", "decayMax",{this.updateEnvView;}], 
		["EZslider", "Sustain level", ControlSpec(0, 1), "sustain", {this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTime", "sustainTimeMin", 
			"sustainTimeMax",{this.updateEnvView;}], 
		["TXMinMaxSliderSplit", "Release", timeSpec, "release", "releaseMin", "releaseMax",{this.updateEnvView;}], 
		["SynthOptionPopup", "Curve", arrOptionData, 2, 200, {system.showView;}], 
		["SynthOptionPopup", "Env. Type", arrOptionData, 3, 200], 
		["commandAction", "Plot amp envelope", {this.envPlot;}],

	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	load the synthdef and create the Group for synths to belong to
	this.loadDefAndMakeGroup;
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Operators", {displayOption = "showOperators"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showOperators")], 
		["Spacer", 3], 
		["ActionButton", "MIDI/ Note", {displayOption = "showMIDI"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showMIDI")], 
		["Spacer", 3], 
		["ActionButton", "Amp Envelope", {displayOption = "showEnv"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showEnv")], 
		["Spacer", 3], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	
	if (displayOption == "showOperators", {
		guiSpecArray = guiSpecArray ++[

			["EZslider", "Main level", ControlSpec(0, 1), "level"], 
			["SpacerLine", 3], 

			["ActionButton", "Op 1", {displayOperator = "showOp1"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOperator == "showOp1")], 
			["Spacer", 3], 
			["ActionButton", "Op 2", {displayOperator = "showOp2"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOperator == "showOp2")], 
			["Spacer", 3], 
			["ActionButton", "Op 3", {displayOperator = "showOp3"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOperator == "showOp3")], 
			["Spacer", 3], 
			["ActionButton", "Op 4", {displayOperator = "showOp4"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOperator == "showOp4")], 
			["Spacer", 3], 
			["ActionButton", "Op 5", {displayOperator = "showOp5"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOperator == "showOp5")], 
			["Spacer", 3], 
			["ActionButton", "Op 6", {displayOperator = "showOp6"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOperator == "showOp6")], 
			["SpacerLine", 3], 
		];

		if (displayOperator == "showOp1", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op1FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op1Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op1Amp"], 
				["EZslider", "Mod Index Op 1", ControlSpec(0,1), "modFM_11"], 
				["EZslider", "Mod Index Op 2", ControlSpec(0,1), "modFM_12"], 
				["EZslider", "Mod Index Op 3", ControlSpec(0,1), "modFM_13"], 
				["EZslider", "Mod Index Op 4", ControlSpec(0,1), "modFM_14"], 
				["EZslider", "Mod Index Op 5", ControlSpec(0,1), "modFM_15"], 
				["EZslider", "Mod Index Op 6", ControlSpec(0,1), "modFM_16"], 
				["EZslider", "Level", ControlSpec(0,1), "outLevelOp1"], 
			];
		});
		if (displayOperator == "showOp2", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op2FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op2Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op2Amp"], 
				["EZslider", "Mod Index Op 1", ControlSpec(0,1), "modFM_21"], 
				["EZslider", "Mod Index Op 2", ControlSpec(0,1), "modFM_22"], 
				["EZslider", "Mod Index Op 3", ControlSpec(0,1), "modFM_23"], 
				["EZslider", "Mod Index Op 4", ControlSpec(0,1), "modFM_24"], 
				["EZslider", "Mod Index Op 5", ControlSpec(0,1), "modFM_25"], 
				["EZslider", "Mod Index Op 6", ControlSpec(0,1), "modFM_26"], 
				["EZslider", "Level", ControlSpec(0,1), "outLevelOp2"], 
			];
		});
		if (displayOperator == "showOp3", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op3FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op3Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op3Amp"], 
				["EZslider", "Mod Index Op 1", ControlSpec(0,1), "modFM_31"], 
				["EZslider", "Mod Index Op 2", ControlSpec(0,1), "modFM_32"], 
				["EZslider", "Mod Index Op 3", ControlSpec(0,1), "modFM_33"], 
				["EZslider", "Mod Index Op 4", ControlSpec(0,1), "modFM_34"], 
				["EZslider", "Mod Index Op 5", ControlSpec(0,1), "modFM_35"], 
				["EZslider", "Mod Index Op 6", ControlSpec(0,1), "modFM_36"], 
				["EZslider", "Level", ControlSpec(0,1), "outLevelOp3"], 
			];
		});
		if (displayOperator == "showOp4", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op4FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op4Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op4Amp"], 
				["EZslider", "Mod Index Op 1", ControlSpec(0,1), "modFM_41"], 
				["EZslider", "Mod Index Op 2", ControlSpec(0,1), "modFM_42"], 
				["EZslider", "Mod Index Op 3", ControlSpec(0,1), "modFM_43"], 
				["EZslider", "Mod Index Op 4", ControlSpec(0,1), "modFM_44"], 
				["EZslider", "Mod Index Op 5", ControlSpec(0,1), "modFM_45"], 
				["EZslider", "Mod Index Op 6", ControlSpec(0,1), "modFM_46"], 
				["EZslider", "Level", ControlSpec(0,1), "outLevelOp4"], 
			];
		});
		if (displayOperator == "showOp5", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op5FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op5Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op5Amp"], 
				["EZslider", "Mod Index Op 1", ControlSpec(0,1), "modFM_51"], 
				["EZslider", "Mod Index Op 2", ControlSpec(0,1), "modFM_52"], 
				["EZslider", "Mod Index Op 3", ControlSpec(0,1), "modFM_53"], 
				["EZslider", "Mod Index Op 4", ControlSpec(0,1), "modFM_54"], 
				["EZslider", "Mod Index Op 5", ControlSpec(0,1), "modFM_55"], 
				["EZslider", "Mod Index Op 6", ControlSpec(0,1), "modFM_56"], 
				["EZslider", "Level", ControlSpec(0,1), "outLevelOp5"], 
			];
		});
		if (displayOperator == "showOp6", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op6FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op6Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op6Amp"], 
				["EZslider", "Mod Index Op 1", ControlSpec(0,1), "modFM_61"], 
				["EZslider", "Mod Index Op 2", ControlSpec(0,1), "modFM_62"], 
				["EZslider", "Mod Index Op 3", ControlSpec(0,1), "modFM_63"], 
				["EZslider", "Mod Index Op 4", ControlSpec(0,1), "modFM_64"], 
				["EZslider", "Mod Index Op 5", ControlSpec(0,1), "modFM_65"], 
				["EZslider", "Mod Index Op 6", ControlSpec(0,1), "modFM_66"], 
				["EZslider", "Level", ControlSpec(0,1), "outLevelOp6"], 
			];
		});
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
//			["TestNoteVals"], 
			["SynthOptionPopup", "Intonation", arrOptionData, 0, 250, 
				{arg view; this.updateIntString(view.value)}], 
			["Spacer", 10], 
			["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", 
				"F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 120], 
			["NextLine"], 
			["TXStaticText", "Note ratios", 
				{TXIntonation.arrScalesText.at(arrOptions.at(1));}, 
				{arg view; ratioView = view}],
			["DividingLine"], 
			["MIDIKeyboard", {arg note; this.createSynthNote(note, testMIDIVel, testMIDITime);}, 5, 60, nil, 36], 
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
			["SynthOptionPopup", "Curve", arrOptionData, 7, 150, {system.showView;}], 
			["SynthOptionPopup", "Env. Type", arrOptionData, 8, 180], 
			["Spacer", 4], 
			["ActionButton", "Plot", {this.envPlot;}],
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
	^[testMIDINote, testMIDIVel, testMIDITime];
}

loadExtraData {arg argData;  // override default method
	testMIDINote = argData.at(0);
	testMIDIVel = argData.at(1);
	testMIDITime = argData.at(2);
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
	envCurve = this.getSynthOption(7);
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

