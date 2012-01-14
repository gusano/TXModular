// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

/*

NOTE - THIS VERSION USES 6 OPTIONAL ENVELOPES

*/

TXFMSynth2b : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiWidth=500;
	classvar	timeSpec;
	classvar	arrFreqRangePresets;

	var 	displayOption;
	var 	displayOperator;
	var 	ratioView;
	var	envView, opEnvView1, opEnvView2, opEnvView3, opEnvView4, opEnvView5, opEnvView6;
	var 	<>testMIDINote = 69;
	var 	<>testMIDIVel = 100;
	var 	<>testMIDITime = 1;
	var	arrEnvPresetNames, arrEnvPresetActions;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "FM Synth2b";
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

		["envtimeOp1", 0, \ir],
		["delayOp1", 0, \ir],
		["attackOp1", 0.005, \ir],
		["attackMinOp1", 0, \ir],
		["attackMaxOp1", 5, \ir],
		["decayOp1", 0.05, \ir],
		["decayMinOp1", 0, \ir],
		["decayMaxOp1", 5, \ir],
		["sustainOp1", 1, \ir],
		["sustainTimeOp1", 0.2, \ir],
		["sustainTimeMinOp1", 0, \ir],
		["sustainTimeMaxOp1", 5, \ir],
		["releaseOp1", 0.01, \ir],
		["releaseMinOp1", 0, \ir],
		["releaseMaxOp1", 5, \ir],
		["envtimeOp2", 0, \ir],
		["delayOp2", 0, \ir],
		["attackOp2", 0.005, \ir],
		["attackMinOp2", 0, \ir],
		["attackMaxOp2", 5, \ir],
		["decayOp2", 0.05, \ir],
		["decayMinOp2", 0, \ir],
		["decayMaxOp2", 5, \ir],
		["sustainOp2", 1, \ir],
		["sustainTimeOp2", 0.2, \ir],
		["sustainTimeMinOp2", 0, \ir],
		["sustainTimeMaxOp2", 5, \ir],
		["releaseOp2", 0.01, \ir],
		["releaseMinOp2", 0, \ir],
		["releaseMaxOp2", 5, \ir],
		["envtimeOp3", 0, \ir],
		["delayOp3", 0, \ir],
		["attackOp3", 0.005, \ir],
		["attackMinOp3", 0, \ir],
		["attackMaxOp3", 5, \ir],
		["decayOp3", 0.05, \ir],
		["decayMinOp3", 0, \ir],
		["decayMaxOp3", 5, \ir],
		["sustainOp3", 1, \ir],
		["sustainTimeOp3", 0.2, \ir],
		["sustainTimeMinOp3", 0, \ir],
		["sustainTimeMaxOp3", 5, \ir],
		["releaseOp3", 0.01, \ir],
		["releaseMinOp3", 0, \ir],
		["releaseMaxOp3", 5, \ir],
		["envtimeOp4", 0, \ir],
		["delayOp4", 0, \ir],
		["attackOp4", 0.005, \ir],
		["attackMinOp4", 0, \ir],
		["attackMaxOp4", 5, \ir],
		["decayOp4", 0.05, \ir],
		["decayMinOp4", 0, \ir],
		["decayMaxOp4", 5, \ir],
		["sustainOp4", 1, \ir],
		["sustainTimeOp4", 0.2, \ir],
		["sustainTimeMinOp4", 0, \ir],
		["sustainTimeMaxOp4", 5, \ir],
		["releaseOp4", 0.01, \ir],
		["releaseMinOp4", 0, \ir],
		["releaseMaxOp4", 5, \ir],
		["envtimeOp5", 0, \ir],
		["delayOp5", 0, \ir],
		["attackOp5", 0.005, \ir],
		["attackMinOp5", 0, \ir],
		["attackMaxOp5", 5, \ir],
		["decayOp5", 0.05, \ir],
		["decayMinOp5", 0, \ir],
		["decayMaxOp5", 5, \ir],
		["sustainOp5", 1, \ir],
		["sustainTimeOp5", 0.2, \ir],
		["sustainTimeMinOp5", 0, \ir],
		["sustainTimeMaxOp5", 5, \ir],
		["releaseOp5", 0.01, \ir],
		["releaseMinOp5", 0, \ir],
		["releaseMaxOp5", 5, \ir],
		["envtimeOp6", 0, \ir],
		["delayOp6", 0, \ir],
		["attackOp6", 0.005, \ir],
		["attackMinOp6", 0, \ir],
		["attackMaxOp6", 5, \ir],
		["decayOp6", 0.05, \ir],
		["decayMinOp6", 0, \ir],
		["decayMaxOp6", 5, \ir],
		["sustainOp6", 1, \ir],
		["sustainTimeOp6", 0.2, \ir],
		["sustainTimeMinOp6", 0, \ir],
		["sustainTimeMaxOp6", 5, \ir],
		["releaseOp6", 0.01, \ir],
		["releaseMinOp6", 0, \ir],
		["releaseMaxOp6", 5, \ir],

		["intKey", 0, \ir],

		["modPitchbend", 0, defLagTime],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],

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
	arrOptions = 0! 27;
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
		// op env 1
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
		// op env 2
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
		// op env 3
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
		// op env 4
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
		// op env 5
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
		// op env 6
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

			envtimeOp1, delayOp1, attackOp1, attackMinOp1, attackMaxOp1, 
			decayOp1, decayMinOp1, decayMaxOp1, sustainOp1, 
			sustainTimeOp1, sustainTimeMinOp1, sustainTimeMaxOp1, releaseOp1, releaseMinOp1, releaseMaxOp1, 
			envtimeOp2, delayOp2, attackOp2, attackMinOp2, attackMaxOp2, 
			decayOp2, decayMinOp2, decayMaxOp2, sustainOp2, 
			sustainTimeOp2, sustainTimeMinOp2, sustainTimeMaxOp2, releaseOp2, releaseMinOp2, releaseMaxOp2, 
			envtimeOp3, delayOp3, attackOp3, attackMinOp3, attackMaxOp3, 
			decayOp3, decayMinOp3, decayMaxOp3, sustainOp3, 
			sustainTimeOp3, sustainTimeMinOp3, sustainTimeMaxOp3, releaseOp3, releaseMinOp3, releaseMaxOp3, 
			envtimeOp4, delayOp4, attackOp4, attackMinOp4, attackMaxOp4, 
			decayOp4, decayMinOp4, decayMaxOp4, sustainOp4, 
			sustainTimeOp4, sustainTimeMinOp4, sustainTimeMaxOp4, releaseOp4, releaseMinOp4, releaseMaxOp4, 
			envtimeOp5, delayOp5, attackOp5, attackMinOp5, attackMaxOp5, 
			decayOp5, decayMinOp5, decayMaxOp5, sustainOp5, 
			sustainTimeOp5, sustainTimeMinOp5, sustainTimeMaxOp5, releaseOp5, releaseMinOp5, releaseMaxOp5, 
			envtimeOp6, delayOp6, attackOp6, attackMinOp6, attackMaxOp6, 
			decayOp6, decayMinOp6, decayMaxOp6, sustainOp6, 
			sustainTimeOp6, sustainTimeMinOp6, sustainTimeMaxOp6, releaseOp6, releaseMinOp6, releaseMaxOp6, 

			intKey, 
			
			modPitchbend,  
			modDelay, modAttack, modDecay, modSustain, modSustainTime, modRelease, 

			mmOp1FreqRatio, mmOp1Phase, mmOp1Amp, 
			mmOp2FreqRatio, mmOp2Phase, mmOp2Amp, 
			mmOp3FreqRatio, mmOp3Phase, mmOp3Amp, 
			mmOp4FreqRatio, mmOp4Phase, mmOp4Amp, 
			mmOp5FreqRatio, mmOp5Phase, mmOp5Amp, 
			mmOp6FreqRatio, mmOp6Phase, mmOp6Amp;

		var outEnv, envFunction, envCurve, 
			outEnvOp1, envFunctionOp1, envCurveOp1, envGenFunctionOp1,
			outEnvOp2, envFunctionOp2, envCurveOp2, envGenFunctionOp2,
			outEnvOp3, envFunctionOp3, envCurveOp3, envGenFunctionOp3,
			outEnvOp4, envFunctionOp4, envCurveOp4, envGenFunctionOp4,
			outEnvOp5, envFunctionOp5, envCurveOp5, envGenFunctionOp5,
			outEnvOp6, envFunctionOp6, envCurveOp6, envGenFunctionOp6,
			intonationFunc, outFreq, pbend, 
			arrFMCtls, arrFMMods, arrFMLevels, outFM,
			del, att, dec, sus, sustime, rel, 
			delOp1, attOp1, decOp1, susOp1, sustimeOp1, relOp1, 
			delOp2, attOp2, decOp2, susOp2, sustimeOp2, relOp2, 
			delOp3, attOp3, decOp3, susOp3, sustimeOp3, relOp3, 
			delOp4, attOp4, decOp4, susOp4, sustimeOp4, relOp4, 
			delOp5, attOp5, decOp5, susOp5, sustimeOp5, relOp5, 
			delOp6, attOp6, decOp6, susOp6, sustimeOp6, relOp6, 
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

		delOp1 = delayOp1;
		attOp1 = (attackMinOp1 + ((attackMaxOp1 - attackMinOp1) * attackOp1)).max(0.001).min(20);
		decOp1 = (decayMinOp1 + ((decayMaxOp1 - decayMinOp1) * decayOp1)).max(0.001).min(20);
		susOp1 = (sustainOp1).max(0).min(1);
		sustimeOp1 = (sustainTimeMinOp1 + 
			((sustainTimeMaxOp1 - sustainTimeMinOp1) * sustainTimeOp1)).max(0.001).min(20);
		relOp1 = (releaseMinOp1 + ((releaseMaxOp1 - releaseMinOp1) * releaseOp1)).max(0.001).min(20);
		envCurveOp1 = this.getSynthOption(9);
		envFunctionOp1 = this.getSynthOption(10);
		envGenFunctionOp1 = this.getSynthOption(11);
		outEnvOp1 = envGenFunctionOp1.value(envFunctionOp1, delOp1, attOp1, decOp1, susOp1, sustimeOp1, 
			relOp1, envCurveOp1, gate);

		delOp2 = delayOp2;
		attOp2 = (attackMinOp2 + ((attackMaxOp2 - attackMinOp2) * attackOp2)).max(0.001).min(20);
		decOp2 = (decayMinOp2 + ((decayMaxOp2 - decayMinOp2) * decayOp2)).max(0.001).min(20);
		susOp2 = (sustainOp2).max(0).min(1);
		sustimeOp2 = (sustainTimeMinOp2 + 
			((sustainTimeMaxOp2 - sustainTimeMinOp2) * sustainTimeOp2)).max(0.001).min(20);
		relOp2 = (releaseMinOp2 + ((releaseMaxOp2 - releaseMinOp2) * releaseOp2)).max(0.001).min(20);
		envCurveOp2 = this.getSynthOption(12);
		envFunctionOp2 = this.getSynthOption(13);
		envGenFunctionOp2 = this.getSynthOption(14);
		outEnvOp2 = envGenFunctionOp2.value(envFunctionOp2, delOp2, attOp2, decOp2, susOp2, sustimeOp2, 
			relOp2, envCurveOp2, gate);

		delOp3 = delayOp3;
		attOp3 = (attackMinOp3 + ((attackMaxOp3 - attackMinOp3) * attackOp3)).max(0.001).min(20);
		decOp3 = (decayMinOp3 + ((decayMaxOp3 - decayMinOp3) * decayOp3)).max(0.001).min(20);
		susOp3 = (sustainOp3).max(0).min(1);
		sustimeOp3 = (sustainTimeMinOp3 + 
			((sustainTimeMaxOp3 - sustainTimeMinOp3) * sustainTimeOp3)).max(0.001).min(20);
		relOp3 = (releaseMinOp3 + ((releaseMaxOp3 - releaseMinOp3) * releaseOp3)).max(0.001).min(20);
		envCurveOp3 = this.getSynthOption(15);
		envFunctionOp3 = this.getSynthOption(16);
		envGenFunctionOp3 = this.getSynthOption(17);
		outEnvOp3 = envGenFunctionOp3.value(envFunctionOp3, delOp3, attOp3, decOp3, susOp3, sustimeOp3, 
			relOp3, envCurveOp3, gate);

		delOp4 = delayOp4;
		attOp4 = (attackMinOp4 + ((attackMaxOp4 - attackMinOp4) * attackOp4)).max(0.001).min(20);
		decOp4 = (decayMinOp4 + ((decayMaxOp4 - decayMinOp4) * decayOp4)).max(0.001).min(20);
		susOp4 = (sustainOp4).max(0).min(1);
		sustimeOp4 = (sustainTimeMinOp4 + 
			((sustainTimeMaxOp4 - sustainTimeMinOp4) * sustainTimeOp4)).max(0.001).min(20);
		relOp4 = (releaseMinOp4 + ((releaseMaxOp4 - releaseMinOp4) * releaseOp4)).max(0.001).min(20);
		envCurveOp4 = this.getSynthOption(18);
		envFunctionOp4 = this.getSynthOption(19);
		envGenFunctionOp4 = this.getSynthOption(20);
		outEnvOp4 = envGenFunctionOp4.value(envFunctionOp4, delOp4, attOp4, decOp4, susOp4, sustimeOp4, 
			relOp4, envCurveOp4, gate);

		delOp5 = delayOp5;
		attOp5 = (attackMinOp5 + ((attackMaxOp5 - attackMinOp5) * attackOp5)).max(0.001).min(20);
		decOp5 = (decayMinOp5 + ((decayMaxOp5 - decayMinOp5) * decayOp5)).max(0.001).min(20);
		susOp5 = (sustainOp5).max(0).min(1);
		sustimeOp5 = (sustainTimeMinOp5 + 
			((sustainTimeMaxOp5 - sustainTimeMinOp5) * sustainTimeOp5)).max(0.001).min(20);
		relOp5 = (releaseMinOp5 + ((releaseMaxOp5 - releaseMinOp5) * releaseOp5)).max(0.001).min(20);
		envCurveOp5 = this.getSynthOption(21);
		envFunctionOp5 = this.getSynthOption(22);
		envGenFunctionOp5 = this.getSynthOption(23);
		outEnvOp5 = envGenFunctionOp5.value(envFunctionOp5, delOp5, attOp5, decOp5, susOp5, sustimeOp5, 
			relOp5, envCurveOp5, gate);

		delOp6 = delayOp6;
		attOp6 = (attackMinOp6 + ((attackMaxOp6 - attackMinOp6) * attackOp6)).max(0.001).min(20);
		decOp6 = (decayMinOp6 + ((decayMaxOp6 - decayMinOp6) * decayOp6)).max(0.001).min(20);
		susOp6 = (sustainOp6).max(0).min(1);
		sustimeOp6 = (sustainTimeMinOp6 + 
			((sustainTimeMaxOp6 - sustainTimeMinOp6) * sustainTimeOp6)).max(0.001).min(20);
		relOp6 = (releaseMinOp6 + ((releaseMaxOp6 - releaseMinOp6) * releaseOp6)).max(0.001).min(20);
		envCurveOp6 = this.getSynthOption(24);
		envFunctionOp6 = this.getSynthOption(25);
		envGenFunctionOp6 = this.getSynthOption(26);
		outEnvOp6 = envGenFunctionOp6.value(envFunctionOp6, delOp6, attOp6, decOp6, susOp6, sustimeOp6, 
			relOp6, envCurveOp6, gate);

		pbend = pitchbendMin + ((pitchbendMax - pitchbendMin) 
				* (pitchbend + modPitchbend).max(0).min(1));

		intonationFunc = this.getSynthOption(0);
		outFreq = (intonationFunc.value(
			(note + transpose), intKey) * keytrack) + ((48 + transpose).midicps * (1-keytrack));

		arrFMCtls = [

// testing xxxx 
/*			[ (mmOp1FreqRatio + op1FreqRatio) * outFreq, 
			(mmOp1Phase + op1Phase).max(0).min(1),(mmOp1Amp + op1Amp + outEnvOp1).max(0).min(1),],
			[ (mmOp2FreqRatio + op2FreqRatio) * outFreq, 
			(mmOp2Phase + op2Phase).max(0).min(1),(mmOp2Amp + op2Amp + outEnvOp2).max(0).min(1),],
			[ (mmOp3FreqRatio + op3FreqRatio) * outFreq, 
			(mmOp3Phase + op3Phase).max(0).min(1),(mmOp3Amp + op3Amp + outEnvOp3).max(0).min(1),],
			[ (mmOp4FreqRatio + op4FreqRatio) * outFreq, 
			(mmOp4Phase + op4Phase).max(0).min(1),(mmOp4Amp + op4Amp + outEnvOp4).max(0).min(1),],
			[ (mmOp5FreqRatio + op5FreqRatio) * outFreq, 
			(mmOp5Phase + op5Phase).max(0).min(1),(mmOp5Amp + op5Amp + outEnvOp5).max(0).min(1),],
			[ (mmOp6FreqRatio + op6FreqRatio) * outFreq, 
			(mmOp6Phase + op6Phase).max(0).min(1),(mmOp6Amp + op6Amp + outEnvOp6).max(0).min(1),],
*/
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
		["ActionButton", "Operator envs", {displayOption = "showOperatorEnvs"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showOperatorEnvs")], 
		["Spacer", 3], 
		["ActionButton", "MIDI/ Note", {displayOption = "showMIDI"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showMIDI")], 
		["Spacer", 3], 
		["ActionButton", "Amp Envelope", {displayOption = "showAmpEnv"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showAmpEnv")], 
		["DividingLine"], 
		["SpacerLine", 2], 
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
				["EZslider", "Mod Op 1", ControlSpec(0,1), "modFM_11"], 
				["EZslider", "Mod Op 2", ControlSpec(0,1), "modFM_12"], 
				["EZslider", "Mod Op 3", ControlSpec(0,1), "modFM_13"], 
				["EZslider", "Mod Op 4", ControlSpec(0,1), "modFM_14"], 
				["EZslider", "Mod Op 5", ControlSpec(0,1), "modFM_15"], 
				["EZslider", "Mod Op 6", ControlSpec(0,1), "modFM_16"], 
				["EZslider", "Out level", ControlSpec(0,1), "outLevelOp1"], 
			];
		});
		if (displayOperator == "showOp2", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op2FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op2Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op2Amp"], 
				["EZslider", "Mod Op 1", ControlSpec(0,1), "modFM_21"], 
				["EZslider", "Mod Op 2", ControlSpec(0,1), "modFM_22"], 
				["EZslider", "Mod Op 3", ControlSpec(0,1), "modFM_23"], 
				["EZslider", "Mod Op 4", ControlSpec(0,1), "modFM_24"], 
				["EZslider", "Mod Op 5", ControlSpec(0,1), "modFM_25"], 
				["EZslider", "Mod Op 6", ControlSpec(0,1), "modFM_26"], 
				["EZslider", "Out level", ControlSpec(0,1), "outLevelOp2"], 
			];
		});
		if (displayOperator == "showOp3", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op3FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op3Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op3Amp"], 
				["EZslider", "Mod Op 1", ControlSpec(0,1), "modFM_31"], 
				["EZslider", "Mod Op 2", ControlSpec(0,1), "modFM_32"], 
				["EZslider", "Mod Op 3", ControlSpec(0,1), "modFM_33"], 
				["EZslider", "Mod Op 4", ControlSpec(0,1), "modFM_34"], 
				["EZslider", "Mod Op 5", ControlSpec(0,1), "modFM_35"], 
				["EZslider", "Mod Op 6", ControlSpec(0,1), "modFM_36"], 
				["EZslider", "Out level", ControlSpec(0,1), "outLevelOp3"], 
			];
		});
		if (displayOperator == "showOp4", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op4FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op4Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op4Amp"], 
				["EZslider", "Mod Op 1", ControlSpec(0,1), "modFM_41"], 
				["EZslider", "Mod Op 2", ControlSpec(0,1), "modFM_42"], 
				["EZslider", "Mod Op 3", ControlSpec(0,1), "modFM_43"], 
				["EZslider", "Mod Op 4", ControlSpec(0,1), "modFM_44"], 
				["EZslider", "Mod Op 5", ControlSpec(0,1), "modFM_45"], 
				["EZslider", "Mod Op 6", ControlSpec(0,1), "modFM_46"], 
				["EZslider", "Out level", ControlSpec(0,1), "outLevelOp4"], 
			];
		});
		if (displayOperator == "showOp5", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op5FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op5Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op5Amp"], 
				["EZslider", "Mod Op 1", ControlSpec(0,1), "modFM_51"], 
				["EZslider", "Mod Op 2", ControlSpec(0,1), "modFM_52"], 
				["EZslider", "Mod Op 3", ControlSpec(0,1), "modFM_53"], 
				["EZslider", "Mod Op 4", ControlSpec(0,1), "modFM_54"], 
				["EZslider", "Mod Op 5", ControlSpec(0,1), "modFM_55"], 
				["EZslider", "Mod Op 6", ControlSpec(0,1), "modFM_56"], 
				["EZslider", "Out level", ControlSpec(0,1), "outLevelOp5"], 
			];
		});
		if (displayOperator == "showOp6", {
			guiSpecArray = guiSpecArray ++[
				["EZslider", "Freq ratio", ControlSpec(0.125, 8), "op6FreqRatio"], 
				["EZslider", "Phase", ControlSpec(0,1), "op6Phase"], 
				["EZslider", "Amplitude", ControlSpec(0,1), "op6Amp"], 
				["EZslider", "Mod Op 1", ControlSpec(0,1), "modFM_61"], 
				["EZslider", "Mod Op 2", ControlSpec(0,1), "modFM_62"], 
				["EZslider", "Mod Op 3", ControlSpec(0,1), "modFM_63"], 
				["EZslider", "Mod Op 4", ControlSpec(0,1), "modFM_64"], 
				["EZslider", "Mod Op 5", ControlSpec(0,1), "modFM_65"], 
				["EZslider", "Mod Op 6", ControlSpec(0,1), "modFM_66"], 
				["EZslider", "Out level", ControlSpec(0,1), "outLevelOp6"], 
			];
		});
	});
	if (displayOption == "showOperatorEnvs", {
		guiSpecArray = guiSpecArray ++[
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
				["SynthOptionCheckBox", "Op Env 1", arrOptionData, 11, 250], 
				["NextLine"], 
				["TXPresetPopup", "Env presets", 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
				],
				["TXEnvDisplay", {this.opEnvViewValues(1);}, {arg view; opEnvView1 = view;}],
				["NextLine"], 
				["EZslider", "Pre-Delay", ControlSpec(0,1), "delayOp1", {this.updateOpEnvView(1);}], 
				["TXMinMaxSliderSplit", "Attack", timeSpec, "attackOp1", "attackMinOp1", "attackMaxOp1",{this.updateOpEnvView(1);}], 
				["TXMinMaxSliderSplit", "Decay", timeSpec, "decayOp1", "decayMinOp1", "decayMaxOp1",{this.updateOpEnvView(1);}], 
				["EZslider", "Sustain level", ControlSpec(0, 1), "sustainOp1", {this.updateOpEnvView(1);}], 
				["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTimeOp1", "sustainTimeMinOp1", 
					"sustainTimeMaxOp1",{this.updateOpEnvView(1);}], 
				["TXMinMaxSliderSplit", "Release", timeSpec, "releaseOp1", "releaseMinOp1", "releaseMaxOp1",{this.updateOpEnvView(1);}], 
				["NextLine"], 
				["SynthOptionPopup", "Curve", arrOptionData, 9, 150, {system.showView;}], 
				["SynthOptionPopup", "Env. Type", arrOptionData, 10, 180], 
			];
		});
		if (displayOperator == "showOp2", {
			guiSpecArray = guiSpecArray ++[	
				["SynthOptionCheckBox", "Op Env 2", arrOptionData, 14, 250], 
				["NextLine"], 
				["TXPresetPopup", "Env presets", 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
				],
				["TXEnvDisplay", {this.opEnvViewValues(2);}, {arg view; opEnvView2 = view;}],
				["NextLine"], 
				["EZslider", "Pre-Delay", ControlSpec(0,1), "delayOp2", {this.updateOpEnvView(2);}], 
				["TXMinMaxSliderSplit", "Attack", timeSpec, "attackOp2", "attackMinOp2", "attackMaxOp2",{this.updateOpEnvView(2);}], 
				["TXMinMaxSliderSplit", "Decay", timeSpec, "decayOp2", "decayMinOp2", "decayMaxOp2",{this.updateOpEnvView(2);}], 
				["EZslider", "Sustain level", ControlSpec(0, 1), "sustainOp2", {this.updateOpEnvView(2);}], 
				["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTimeOp2", "sustainTimeMinOp2", 
					"sustainTimeMaxOp2",{this.updateOpEnvView(2);}], 
				["TXMinMaxSliderSplit", "Release", timeSpec, "releaseOp2", "releaseMinOp2", "releaseMaxOp2",{this.updateOpEnvView(2);}], 
				["NextLine"], 
				["SynthOptionPopup", "Curve", arrOptionData, 12, 150, {system.showView;}], 
				["SynthOptionPopup", "Env. Type", arrOptionData, 13, 180], 
			];
		});
		if (displayOperator == "showOp3", {
			guiSpecArray = guiSpecArray ++[	
				["SynthOptionCheckBox", "Op Env 3", arrOptionData, 17, 250], 
				["NextLine"], 
				["TXPresetPopup", "Env presets", 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
				],
				["TXEnvDisplay", {this.opEnvViewValues(3);}, {arg view; opEnvView3 = view;}],
				["NextLine"], 
				["EZslider", "Pre-Delay", ControlSpec(0,1), "delayOp3", {this.updateOpEnvView(3);}], 
				["TXMinMaxSliderSplit", "Attack", timeSpec, "attackOp3", "attackMinOp3", "attackMaxOp3",{this.updateOpEnvView(3);}], 
				["TXMinMaxSliderSplit", "Decay", timeSpec, "decayOp3", "decayMinOp3", "decayMaxOp3",{this.updateOpEnvView(3);}], 
				["EZslider", "Sustain level", ControlSpec(0, 1), "sustainOp3", {this.updateOpEnvView(3);}], 
				["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTimeOp3", "sustainTimeMinOp3", 
					"sustainTimeMaxOp3",{this.updateOpEnvView(3);}], 
				["TXMinMaxSliderSplit", "Release", timeSpec, "releaseOp3", "releaseMinOp3", "releaseMaxOp3",{this.updateOpEnvView(3);}], 
				["NextLine"], 
				["SynthOptionPopup", "Curve", arrOptionData, 15, 150, {system.showView;}], 
				["SynthOptionPopup", "Env. Type", arrOptionData, 16, 180], 
			];
		});
		if (displayOperator == "showOp4", {
			guiSpecArray = guiSpecArray ++[	
				["SynthOptionCheckBox", "Op Env 4", arrOptionData, 20, 250], 
				["NextLine"], 
				["TXPresetPopup", "Env presets", 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
				],
				["TXEnvDisplay", {this.opEnvViewValues(4);}, {arg view; opEnvView4 = view;}],
				["NextLine"], 
				["EZslider", "Pre-Delay", ControlSpec(0,1), "delayOp4", {this.updateOpEnvView(4);}], 
				["TXMinMaxSliderSplit", "Attack", timeSpec, "attackOp4", "attackMinOp4", "attackMaxOp4",{this.updateOpEnvView(4);}], 
				["TXMinMaxSliderSplit", "Decay", timeSpec, "decayOp4", "decayMinOp4", "decayMaxOp4",{this.updateOpEnvView(4);}], 
				["EZslider", "Sustain level", ControlSpec(0, 1), "sustainOp4", {this.updateOpEnvView(4);}], 
				["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTimeOp4", "sustainTimeMinOp4", 
					"sustainTimeMaxOp4",{this.updateOpEnvView(4);}], 
				["TXMinMaxSliderSplit", "Release", timeSpec, "releaseOp4", "releaseMinOp4", "releaseMaxOp4",{this.updateOpEnvView(4);}], 
				["NextLine"], 
				["SynthOptionPopup", "Curve", arrOptionData, 8, 150, {system.showView;}], 
				["SynthOptionPopup", "Env. Type", arrOptionData, 19, 180], 
			];
		});
		if (displayOperator == "showOp5", {
			guiSpecArray = guiSpecArray ++[	
				["SynthOptionCheckBox", "Op Env 5", arrOptionData, 23, 250], 
				["NextLine"], 
				["TXPresetPopup", "Env presets", 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
				],
				["TXEnvDisplay", {this.opEnvViewValues(5);}, {arg view; opEnvView5 = view;}],
				["NextLine"], 
				["EZslider", "Pre-Delay", ControlSpec(0,1), "delayOp5", {this.updateOpEnvView(5);}], 
				["TXMinMaxSliderSplit", "Attack", timeSpec, "attackOp5", "attackMinOp5", "attackMaxOp5",{this.updateOpEnvView(5);}], 
				["TXMinMaxSliderSplit", "Decay", timeSpec, "decayOp5", "decayMinOp5", "decayMaxOp5",{this.updateOpEnvView(5);}], 
				["EZslider", "Sustain level", ControlSpec(0, 1), "sustainOp5", {this.updateOpEnvView(5);}], 
				["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTimeOp5", "sustainTimeMinOp5", 
					"sustainTimeMaxOp5",{this.updateOpEnvView(5);}], 
				["TXMinMaxSliderSplit", "Release", timeSpec, "releaseOp5", "releaseMinOp5", "releaseMaxOp5",{this.updateOpEnvView(5);}], 
				["NextLine"], 
				["SynthOptionPopup", "Curve", arrOptionData, 21, 150, {system.showView;}], 
				["SynthOptionPopup", "Env. Type", arrOptionData, 22, 180], 
			];
		});
		if (displayOperator == "showOp6", {
			guiSpecArray = guiSpecArray ++[	
				["SynthOptionCheckBox", "Op Env 6", arrOptionData, 26, 250], 
				["NextLine"], 
				["TXPresetPopup", "Env presets", 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
					TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
				],
				["TXEnvDisplay", {this.opEnvViewValues(6);}, {arg view; opEnvView6 = view;}],
				["NextLine"], 
				["EZslider", "Pre-Delay", ControlSpec(0,1), "delayOp6", {this.updateOpEnvView(6);}], 
				["TXMinMaxSliderSplit", "Attack", timeSpec, "attackOp6", "attackMinOp6", "attackMaxOp6",{this.updateOpEnvView(6);}], 
				["TXMinMaxSliderSplit", "Decay", timeSpec, "decayOp6", "decayMinOp6", "decayMaxOp6",{this.updateOpEnvView(6);}], 
				["EZslider", "Sustain level", ControlSpec(0, 1), "sustainOp6", {this.updateOpEnvView(6);}], 
				["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTimeOp6", "sustainTimeMinOp6", 
					"sustainTimeMaxOp6",{this.updateOpEnvView(6);}], 
				["TXMinMaxSliderSplit", "Release", timeSpec, "releaseOp6", "releaseMinOp6", "releaseMaxOp6",{this.updateOpEnvView(6);}], 
				["NextLine"], 
				["SynthOptionPopup", "Curve", arrOptionData, 24, 150, {system.showView;}], 
				["SynthOptionPopup", "Env. Type", arrOptionData, 25, 180], 
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
	if (displayOption == "showAmpEnv", {
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

opEnvViewValues {arg opNo;
	var attack, attackMin, attackMax, decay, decayMin, decayMax, sustain;
	var sustainTime, sustainTimeMin, sustainTimeMax, release, releaseMin, releaseMax;
	var del, att, dec, sus, sustime, rel;
	var arrTimesNorm, arrTimesNormedSummed;

	del = this.getSynthArgSpec("delayOp" ++ opNo.asString);
	attack = this.getSynthArgSpec("attackOp" ++ opNo.asString);
	attackMin = this.getSynthArgSpec("attackMinOp" ++ opNo.asString);
	attackMax = this.getSynthArgSpec("attackMaxOp" ++ opNo.asString);
	att = attackMin + ((attackMax - attackMin) * attack);
	decay = this.getSynthArgSpec("decayOp" ++ opNo.asString);
	decayMin = this.getSynthArgSpec("decayMinOp" ++ opNo.asString);
	decayMax = this.getSynthArgSpec("decayMaxOp" ++ opNo.asString);
	dec = decayMin + ((decayMax - decayMin) * decay);
	sus = this.getSynthArgSpec("sustainOp" ++ opNo.asString);
	sustainTime = this.getSynthArgSpec("sustainTimeOp" ++ opNo.asString);
	sustainTimeMin = this.getSynthArgSpec("sustainTimeMinOp" ++ opNo.asString);
	sustainTimeMax = this.getSynthArgSpec("sustainTimeMaxOp" ++ opNo.asString);
	sustime = sustainTimeMin + ((sustainTimeMax - sustainTimeMin) * sustainTime);
	release = this.getSynthArgSpec("releaseOp" ++ opNo.asString);
	releaseMin = this.getSynthArgSpec("releaseMinOp" ++ opNo.asString);
	releaseMax = this.getSynthArgSpec("releaseMaxOp" ++ opNo.asString);
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

updateOpEnvView {arg opNo;
	var arrViews;
	arrViews = [opEnvView1, opEnvView2, opEnvView3, opEnvView4, opEnvView5, opEnvView6];
	if (arrViews.at(opNo - 1).class == SCEnvelopeView, {
		if (arrViews.at(opNo - 1).notClosed, {
			6.do({arg i;
				arrViews.at(opNo - 1).setEditable(i, true);
			});
			arrViews.at(opNo - 1).value = this.opEnvViewValues(opNo);
			6.do({arg i;
				arrViews.at(opNo - 1).setEditable(i, false);
			});
		});
	});
}

}

