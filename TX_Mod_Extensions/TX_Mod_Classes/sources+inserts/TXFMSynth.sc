// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

/*

NOTE - THIS MODULE WAS ABANDONED DUE TO UNWIELDY SIZE (69% CPU!)

*/

TXFMSynth : TXModuleBase {

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
	classvar	arrMMSourceNames, arrMMDestNames, arrMMScaleNames;

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
	defaultName = "FM Synth";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Pitch bend", 1, "modPitchbend", 0],
		["Delay", 1, "modDelay", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain level", 1, "modSustain", 0],
		["Sustain time", 1, "modSustainTime", 0],
		["Release", 1, "modRelease", 0],
		["Delay2", 1, "modDelay2", 0],
		["Attack2", 1, "modAttack2", 0],
		["Decay2", 1, "modDecay2", 0],
		["Sustain level2", 1, "modSustain2", 0],
		["Sustain time2", 1, "modSustainTime2", 0],
		["Release2", 1, "modRelease2", 0],
		["LFO 1 Freq", 1, "modFreqLFO1", 0],
		["LFO 1 Fade-in", 1, "modFadeInLFO1", 0],
		["LFO 2 Freq", 1, "modFreqLFO2", 0],
		["LFO 2 Fade-in", 1, "modFadeInLFO2", 0],
 		["Slider 1", 1, "modSlider1", 0],
 		["Slider 2", 1, "modSlider2", 0],
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	timeSpec = ControlSpec(0.001, 20);
	arrMMSourceNames = ["Set Source...", "Note", "Velocity", "Amp Env", "Env 2", "Env 3", 
// testing xxxx - removed for now
//	"Env 4", 
		"LFO 1", "LFO 2", "Random val 1", "Random val 2", "Offset val", "Slider 1", "Slider 2"];
	arrMMDestNames = ["Set Dest...", "Pitchbend", "Level"
		"Op 1 Ratio", "Op 1 Phase", "Op 1 Amp", 
		"Op 2 Ratio", "Op 2 Phase", "Op 2 Amp", 
		"Op 3 Ratio", "Op 3 Phase", "Op 3 Amp", 
		"Op 4 Ratio", "Op 4 Phase", "Op 4 Amp", 
		"Op 5 Ratio", "Op 5 Phase", "Op 5 Amp", 
		"Op 6 Ratio", "Op 6 Phase", "Op 6 Amp", 	
		"ModFM_11", "ModFM_12", "ModFM_13", "ModFM_14", "ModFM_15", "ModFM_16", 
		"ModFM_21", "ModFM_22", "ModFM_23", "ModFM_24", "ModFM_25", "ModFM_26", 
		"ModFM_31", "ModFM_32", "ModFM_33", "ModFM_34", "ModFM_35", "ModFM_36", 
		"ModFM_41", "ModFM_42", "ModFM_43", "ModFM_44", "ModFM_45", "ModFM_46", 
		"ModFM_51", "ModFM_52", "ModFM_53", "ModFM_54", "ModFM_55", "ModFM_56", 
		"ModFM_61", "ModFM_62", "ModFM_63", "ModFM_64", "ModFM_65", "ModFM_66", 
	];
	arrMMScaleNames = ["...", "Note", "Velocity", "Slider 1", "Slider 2"];

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

		["envtime2", 0, \ir],
		["delay2", 0, \ir],
		["attack2", 0.005, \ir],
		["attackMin2", 0, \ir],
		["attackMax2", 5, \ir],
		["decay2", 0.05, \ir],
		["decayMin2", 0, \ir],
		["decayMax2", 5, \ir],
		["sustain2", 1, \ir],
		["sustainTime2", 0.2, \ir],
		["sustainTimeMin2", 0, \ir],
		["sustainTimeMax2", 5, \ir],
		["release2", 0.01, \ir],
		["releaseMin2", 0, \ir],
		["releaseMax2", 5, \ir],

		["envtime3", 0, \ir],
		["delay3", 0, \ir],
		["attack3", 0.005, \ir],
		["attackMin3", 0, \ir],
		["attackMax3", 5, \ir],
		["decay3", 0.05, \ir],
		["decayMin3", 0, \ir],
		["decayMax3", 5, \ir],
		["sustain3", 1, \ir],
		["sustainTime3", 0.2, \ir],
		["sustainTimeMin3", 0, \ir],
		["sustainTimeMax3", 5, \ir],
		["release3", 0.01, \ir],
		["releaseMin3", 0, \ir],
		["releaseMax3", 5, \ir],

// testing xxxx - removed for now
//		["envtime4", 0, \ir],
//		["delay4", 0, \ir],
//		["attack4", 0.005, \ir],
//		["attackMin4", 0, \ir],
//		["attackMax4", 5, \ir],
//		["decay4", 0.05, \ir],
//		["decayMin4", 0, \ir],
//		["decayMax4", 5, \ir],
//		["sustain4", 1, \ir],
//		["sustainTime4", 0.2, \ir],
//		["sustainTimeMin4", 0, \ir],
//		["sustainTimeMax4", 5, \ir],
//		["release4", 0.01, \ir],
//		["releaseMin4", 0, \ir],
//		["releaseMax4", 5, \ir],

		["intKey", 0, \ir],
		["freqLFO1", 0.5, defLagTime],
		["freqLFO1Min", 0.01, defLagTime],
		["freqLFO1Max", 100, defLagTime],
		["lfo1FadeIn", 0, \ir],
		["lfo1FadeInMin", 0.01, \ir],
		["lfo1FadeInMax", 5, \ir],
		["freqLFO2", 0.5, defLagTime],
		["freqLFO2Min", 0.01, defLagTime],
		["freqLFO2Max", 100, defLagTime],
		["lfo2FadeIn", 0, \ir],
		["lfo2FadeInMin", 0.01, \ir],
		["lfo2FadeInMax", 5, \ir],
		["noteModMin", 0, defLagTime],
		["noteModMax", 127, defLagTime],
		["velModMin", 0, defLagTime],
		["velModMax", 127, defLagTime],
		["slider1", 0, defLagTime],
		["slider2", 0, defLagTime],

		["i_Source0", 0, \ir],
		["i_Dest0", 0, \ir],
		["mmValue0", 0, defLagTime],
		["i_Scale0", 0, \ir],
		["i_Source1", 0, \ir],
		["i_Dest1", 0, \ir],
		["mmValue1", 0, defLagTime],
		["i_Scale1", 0, \ir],
		["i_Source2", 0, \ir],
		["i_Dest2", 0, \ir],
		["mmValue2", 0, defLagTime],
		["i_Scale2", 0, \ir],
		["i_Source3", 0, \ir],
		["i_Dest3", 0, \ir],
		["mmValue3", 0, defLagTime],
		["i_Scale3", 0, \ir],
		["i_Source4", 0, \ir],
		["i_Dest4", 0, \ir],
		["mmValue4", 0, defLagTime],
		["i_Scale4", 0, \ir],
		["i_Source5", 0, \ir],
		["i_Dest5", 0, \ir],
		["mmValue5", 0, defLagTime],
		["i_Scale5", 0, \ir],
		["i_Source6", 0, \ir],
		["i_Dest6", 0, \ir],
		["mmValue6", 0, defLagTime],
		["i_Scale6", 0, \ir],
		["i_Source7", 0, \ir],
		["i_Dest7", 0, \ir],
		["mmValue7", 0, defLagTime],
		["i_Scale7", 0, \ir],
		["i_Source8", 0, \ir],
		["i_Dest8", 0, \ir],
		["mmValue8", 0, defLagTime],
		["i_Scale8", 0, \ir],
		["i_Source9", 0, \ir],
		["i_Dest9", 0, \ir],
		["mmValue9", 0, defLagTime],
		["i_Scale9", 0, \ir],

/*
		["i_Source10", 0, \ir],
		["i_Dest10", 0, \ir],
		["mmValue10", 0, defLagTime],
		["i_Scale10", 0, \ir],
		["i_Source11", 0, \ir],
		["i_Dest11", 0, \ir],
		["mmValue11", 0, defLagTime],
		["i_Scale11", 0, \ir],
		["i_Source12", 0, \ir],
		["i_Dest12", 0, \ir],
		["mmValue12", 0, defLagTime],
		["i_Scale12", 0, \ir],
		["i_Source13", 0, \ir],
		["i_Dest13", 0, \ir],
		["mmValue13", 0, defLagTime],
		["i_Scale13", 0, \ir],
		["i_Source14", 0, \ir],
		["i_Dest14", 0, \ir],
		["mmValue14", 0, defLagTime],
		["i_Scale14", 0, \ir],
		["i_Source15", 0, \ir],
		["i_Dest15", 0, \ir],
		["mmValue15", 0, defLagTime],
		["i_Scale15", 0, \ir],
		["i_Source16", 0, \ir],
		["i_Dest16", 0, \ir],
		["mmValue16", 0, defLagTime],
		["i_Scale16", 0, \ir],
*/

		["modPitchbend", 0, defLagTime],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],

		["modDelay2", 0, \ir],
		["modAttack2", 0, \ir],
		["modDecay2", 0, \ir],
		["modSustain2", 0, \ir],
		["modSustainTime2", 0, \ir],
		["modRelease2", 0, \ir],

		["modFreqLFO1", 0, defLagTime],
		["modFadeInLFO1", 0, defLagTime],
		["modFreqLFO2", 0, defLagTime],
		["modFadeInLFO2", 0, defLagTime],
 		["modSlider1", 0, defLagTime],
 		["modSlider2", 0, defLagTime],
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
			envtime2, delay2, attack2, attackMin2, attackMax2, decay2, decayMin2, decayMax2, sustain2, 
			sustainTime2, sustainTimeMin2, sustainTimeMax2, release2, releaseMin2, releaseMax2, 
			envtime3, delay3, attack3, attackMin3, attackMax3, decay3, decayMin3, decayMax3, sustain3, 
			sustainTime3, sustainTimeMin3, sustainTimeMax3, release3, releaseMin3, releaseMax3, 
// testing xxxx - removed for now
//			envtime4, delay4, attack4, attackMin4, attackMax4, decay4, decayMin4, decayMax4, sustain4, 
//			sustainTime4, sustainTimeMin4, sustainTimeMax4, release4, releaseMin4, releaseMax4, 
			intKey, 
			freqLFO1, freqLFO1Min, freqLFO1Max, lfo1FadeIn, lfo1FadeInMin, lfo1FadeInMax,
			freqLFO2, freqLFO2Min, freqLFO2Max,  lfo2FadeIn, lfo2FadeInMin, lfo2FadeInMax,
			velModMin, velModMax,noteModMin, noteModMax, slider1, slider2,
			
			i_Source0, i_Dest0, mmValue0, i_Scale0, i_Source1, i_Dest1, mmValue1, i_Scale1, 
			i_Source2, i_Dest2, mmValue2, i_Scale2, 
			i_Source3, i_Dest3, mmValue3, i_Scale3, i_Source4, i_Dest4, mmValue4, i_Scale4, 
			i_Source5, i_Dest5, mmValue5, i_Scale5, 
			i_Source6, i_Dest6, mmValue6, i_Scale6, i_Source7, i_Dest7, mmValue7, i_Scale7, 
			i_Source8, i_Dest8, mmValue8, i_Scale8, i_Source9, i_Dest9, mmValue9, i_Scale9, 
/*
			i_Source10, i_Dest10, mmValue10, i_Scale10, i_Source11, i_Dest11, mmValue11, i_Scale11, 
			i_Source12, i_Dest12, mmValue12, i_Scale12, i_Source13, i_Dest13, mmValue13, i_Scale13, 
			i_Source14, i_Dest14, mmValue14, i_Scale14, i_Source15, i_Dest15, mmValue15, i_Scale15, 
			i_Source16, i_Dest16, mmValue16, i_Scale16, 
*/
			modPitchbend,  
			modDelay, modAttack, modDecay, modSustain, modSustainTime, modRelease, 
			modDelay2, modAttack2, modDecay2, modSustain2, modSustainTime2, modRelease2, 
			modFreqLFO1, modFadeInLFO1, modFreqLFO2, modFadeInLFO2, modSlider1, modSlider2;

		var outEnv, envFunction, envCurve, 
			outEnv2, envFunction2, envCurve2, envGenFunction2,
			outEnv3, envFunction3, envCurve3, envGenFunction3,
			outEnv4, envFunction4, envCurve4, envGenFunction4,
			intonationFunc, outFreq, pbend, 
			mmOp1FreqRatio, mmOp1Phase, mmOp1Amp, 
			mmOp2FreqRatio, mmOp2Phase, mmOp2Amp, 
			mmOp3FreqRatio, mmOp3Phase, mmOp3Amp, 
			mmOp4FreqRatio, mmOp4Phase, mmOp4Amp, 
			mmOp5FreqRatio, mmOp5Phase, mmOp5Amp, 
			mmOp6FreqRatio, mmOp6Phase, mmOp6Amp, 
			mmModFM_11, mmModFM_12, mmModFM_13, mmModFM_14, mmModFM_15, mmModFM_16, 
			mmModFM_21, mmModFM_22, mmModFM_23, mmModFM_24, mmModFM_25, mmModFM_26, 
			mmModFM_31, mmModFM_32, mmModFM_33, mmModFM_34, mmModFM_35, mmModFM_36, 
			mmModFM_41, mmModFM_42, mmModFM_43, mmModFM_44, mmModFM_45, mmModFM_46, 
			mmModFM_51, mmModFM_52, mmModFM_53, mmModFM_54, mmModFM_55, mmModFM_56, 
			mmModFM_61, mmModFM_62, mmModFM_63, mmModFM_64, mmModFM_65, mmModFM_66, 
						arrFMCtls, arrFMMods, arrFMLevels, outFM,
//			waveFunction, outWave, 
			del, att, dec, sus, sustime, rel, 
			del2, att2, dec2, sus2, sustime2, rel2, 
			del3, att3, dec3, sus3, sustime3, rel3, 
			del4, att4, dec4, sus4, sustime4, rel4, 
			lfo1Function, outLFO1, lfo2Function, outLFO2, lfo1FadeInTime, lfo1FadeInCurve, 
			lfo2FadeInTime, lfo2FadeInCurve, 
			randomValue1, randomValue2, 
			timeControlSpec, sumVelocity, sourceIndexArray, destIndexArray, scaleIndexArray, 
			sourceArray, destArray, mmValueArray, scaleArray, sumSlider1, sumSlider2,
			dummyVal, mmPbend = 0, mmLevel = 0,  
			arrAllDestModulations, noteModulation, velModulation;

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

		del2 = (delay2 + modDelay2).max(0).min(1);
		att2 = (attackMin2 + ((attackMax2 - attackMin2) * (attack2 + modAttack2))).max(0.001).min(20);
		dec2 = (decayMin2 + ((decayMax2 - decayMin2) * (decay2 + modDecay2))).max(0.001).min(20);
		sus2 = (sustain2 + modSustain2).max(0).min(1);
		sustime2 = (sustainTimeMin2 + 
			((sustainTimeMax2 - sustainTimeMin2) * (sustainTime2 + modSustainTime2))).max(0.001).min(20);
		rel2 = (releaseMin2 + ((releaseMax2 - releaseMin2) * (release2 + modRelease2))).max(0.001).min(20);
		envCurve2 = this.getSynthOption(9);
		envFunction2 = this.getSynthOption(10);
		envGenFunction2 = this.getSynthOption(11);
		outEnv2 = envGenFunction2.value(envFunction2, del2, att2, dec2, sus2, sustime2, rel2, envCurve2, gate);

		del3 = delay3;
		att3 = (attackMin3 + ((attackMax3 - attackMin3) * attack3)).max(0.001).min(20);
		dec3 = (decayMin3 + ((decayMax3 - decayMin3) * decay3)).max(0.001).min(20);
		sus3 = (sustain3).max(0).min(1);
		sustime3 = (sustainTimeMin3 + 
			((sustainTimeMax3 - sustainTimeMin3) * sustainTime3)).max(0.001).min(20);
		rel3 = (releaseMin3 + ((releaseMax3 - releaseMin3) * release3)).max(0.001).min(20);
		envCurve3 = this.getSynthOption(12);
		envFunction3 = this.getSynthOption(13);
		envGenFunction3 = this.getSynthOption(14);
		outEnv3 = envGenFunction3.value(envFunction3, del3, att3, dec3, sus3, sustime3, rel3, envCurve3, gate);

// testing xxxx - removed for now
//		del4 = (delay4);
//		att4 = (attackMin4 + ((attackMax4 - attackMin4) * (attack4))).max(0.001).min(20);
//		dec4 = (decayMin4 + ((decayMax4 - decayMin4) * (decay4))).max(0.001).min(20);
//		sus4 = (sustain4).max(0).min(1);
//		sustime4 = (sustainTimeMin4 + 
//			((sustainTimeMax4 - sustainTimeMin4) * (sustainTime4))).max(0.001).min(20);
//		rel4 = (releaseMin4 + ((releaseMax4 - releaseMin4) * (release4))).max(0.001).min(20);
//		envCurve4 = this.getSynthOption(15);
//		envFunction4 = this.getSynthOption(16);
//		envGenFunction4 = this.getSynthOption(17);
//		outEnv4 = envGenFunction4.value(envFunction4, del4, att4, dec4, sus4, sustime4, rel4, envCurve4, gate);

		lfo1FadeInTime = (lfo1FadeInMin + ((lfo1FadeInMax - lfo1FadeInMin) * (lfo1FadeIn))).max(0.001).min(20);
		lfo1FadeInCurve = XLine.kr(0.01, 1, lfo1FadeInTime); 
		lfo1Function =  this.getSynthOption(1);
		outLFO1 = lfo1FadeInCurve * 
			lfo1Function.value(this.getSynthOption(2), this.getSynthOption(3), 
				freqLFO1, freqLFO1Min, freqLFO1Max, modFreqLFO1);

		lfo2FadeInTime = (lfo2FadeInMin + ((lfo2FadeInMax - lfo2FadeInMin) * (lfo2FadeIn + modFadeInLFO2))).max(0.001).min(20);
		lfo2FadeInCurve = XLine.kr(0.01, 1, lfo2FadeInTime);
		lfo2Function =  this.getSynthOption(4);
		outLFO2 = lfo2FadeInCurve * 
			lfo2Function.value(this.getSynthOption(5), this.getSynthOption(6), 
				freqLFO2, freqLFO2Min, freqLFO2Max, modFreqLFO2);
		
		sumSlider1 = (slider1 + modSlider1).max(0).min(1);
		sumSlider2 = (slider2 + modSlider2).max(0).min(1);

		randomValue1 = Rand(0, 1);
		randomValue2 = Rand(0, 1);
		
		noteModulation = note.max(noteModMin).min(noteModMax) -  noteModMin / (noteModMax - noteModMin);
		velModulation = velocity.max(velModMin).min(velModMax) - velModMin / (velModMax - velModMin);

		sourceArray = [0, noteModulation, velModulation, outEnv, outEnv2, outEnv3, 
// testing xxxx - removed for now
//		outEnv4, 
			outLFO1, outLFO2, randomValue1, randomValue2, 1, sumSlider1, sumSlider2];
		destArray = [
			dummyVal, mmPbend, mmLevel, 
			mmOp1FreqRatio, mmOp1Phase, mmOp1Amp, 
			mmOp2FreqRatio, mmOp2Phase, mmOp2Amp, 
			mmOp3FreqRatio, mmOp3Phase, mmOp3Amp, 
			mmOp4FreqRatio, mmOp4Phase, mmOp4Amp, 
			mmOp5FreqRatio, mmOp5Phase, mmOp5Amp, 
			mmOp6FreqRatio, mmOp6Phase, mmOp6Amp, 
			mmModFM_11, mmModFM_12, mmModFM_13, mmModFM_14, mmModFM_15, mmModFM_16, 
			mmModFM_21, mmModFM_22, mmModFM_23, mmModFM_24, mmModFM_25, mmModFM_26, 
			mmModFM_31, mmModFM_32, mmModFM_33, mmModFM_34, mmModFM_35, mmModFM_36, 
			mmModFM_41, mmModFM_42, mmModFM_43, mmModFM_44, mmModFM_45, mmModFM_46, 
			mmModFM_51, mmModFM_52, mmModFM_53, mmModFM_54, mmModFM_55, mmModFM_56, 
			mmModFM_61, mmModFM_62, mmModFM_63, mmModFM_64, mmModFM_65, mmModFM_66, 
		];
		scaleArray = [1, noteModulation, velModulation, sumSlider1, sumSlider2];
		sourceIndexArray = ["i_Source0", "i_Source1", "i_Source2", "i_Source3", "i_Source4", "i_Source5",
			"i_Source6", "i_Source7", "i_Source8", "i_Source9", 
/*
			"i_Source10", "i_Source11", "i_Source12", "i_Source13", "i_Source14", "i_Source15", "i_Source16"
*/
			]
			.collect({arg item, i; this.getSynthArgSpec(item);});
		destIndexArray = ["i_Dest0", "i_Dest1", "i_Dest2", "i_Dest3", "i_Dest4", "i_Dest5", "i_Dest6", "i_Dest7", 
			"i_Dest8", "i_Dest9"]
			.collect({arg item, i; this.getSynthArgSpec(item);});
		mmValueArray = [mmValue0, mmValue1, mmValue2, mmValue3, mmValue4, mmValue5, mmValue6, mmValue7, 
			mmValue8, mmValue9];
		scaleIndexArray = ["i_Scale0", "i_Scale1", "i_Scale2", "i_Scale3", "i_Scale4", "i_Scale5",
			"i_Scale6", "i_Scale7", "i_Scale8", "i_Scale9"]
			.collect({arg item, i; this.getSynthArgSpec(item);});

		// build mod matrix modulations
		arrAllDestModulations = destArray.collect ({arg item, i;
				var arrModulations;
				arrModulations = [];
				sourceIndexArray.do({arg itemSourceInd, j; 
					if (destIndexArray[j] == i, {
						arrModulations = arrModulations.add(sourceArray[itemSourceInd] * (mmValueArray[j] / 100)
							 * scaleArray[scaleIndexArray[j].asInteger]  
					)});
				});
				(arrModulations ?  [0]).sum ;   
			});

		dummyVal = arrAllDestModulations[0];
		mmPbend = arrAllDestModulations[1];
		mmLevel = arrAllDestModulations[2];
		mmOp1FreqRatio  = arrAllDestModulations[3];
		mmOp1Phase  = arrAllDestModulations[4];
		mmOp1Amp  = arrAllDestModulations[5];
		mmOp2FreqRatio  = arrAllDestModulations[6];
		mmOp2Phase  = arrAllDestModulations[7];
		mmOp2Amp  = arrAllDestModulations[8];
		mmOp3FreqRatio  = arrAllDestModulations[9];
		mmOp3Phase  = arrAllDestModulations[10];
		mmOp3Amp  = arrAllDestModulations[11];
		mmOp4FreqRatio  = arrAllDestModulations[12];
		mmOp4Phase  = arrAllDestModulations[13];
		mmOp4Amp  = arrAllDestModulations[14];
		mmOp5FreqRatio  = arrAllDestModulations[15];
		mmOp5Phase  = arrAllDestModulations[16];
		mmOp5Amp  = arrAllDestModulations[17];
		mmOp6FreqRatio  = arrAllDestModulations[18];
		mmOp6Phase  = arrAllDestModulations[19];
		mmOp6Amp  = arrAllDestModulations[20];
		mmModFM_11 = arrAllDestModulations[21];
		mmModFM_12 = arrAllDestModulations[22];
		mmModFM_13 = arrAllDestModulations[23];
		mmModFM_14 = arrAllDestModulations[24];
		mmModFM_15 = arrAllDestModulations[25];
		mmModFM_16 = arrAllDestModulations[26];
		mmModFM_21 = arrAllDestModulations[27];
		mmModFM_22 = arrAllDestModulations[28];
		mmModFM_23 = arrAllDestModulations[29];
		mmModFM_24 = arrAllDestModulations[30];
		mmModFM_25 = arrAllDestModulations[31];
		mmModFM_26 = arrAllDestModulations[32];
		mmModFM_31 = arrAllDestModulations[33];
		mmModFM_32 = arrAllDestModulations[34];
		mmModFM_33 = arrAllDestModulations[35];
		mmModFM_34 = arrAllDestModulations[36];
		mmModFM_35 = arrAllDestModulations[37];
		mmModFM_36 = arrAllDestModulations[38];
		mmModFM_41 = arrAllDestModulations[39];
		mmModFM_42 = arrAllDestModulations[40];
		mmModFM_43 = arrAllDestModulations[41];
		mmModFM_44 = arrAllDestModulations[42];
		mmModFM_45 = arrAllDestModulations[43];
		mmModFM_46 = arrAllDestModulations[44];
		mmModFM_51 = arrAllDestModulations[45];
		mmModFM_52 = arrAllDestModulations[46];
		mmModFM_53 = arrAllDestModulations[47];
		mmModFM_54 = arrAllDestModulations[48];
		mmModFM_55 = arrAllDestModulations[49];
		mmModFM_56 = arrAllDestModulations[50];
		mmModFM_61 = arrAllDestModulations[51];
		mmModFM_62 = arrAllDestModulations[52];
		mmModFM_63 = arrAllDestModulations[53];
		mmModFM_64 = arrAllDestModulations[54];
		mmModFM_65 = arrAllDestModulations[55];
		mmModFM_66 = arrAllDestModulations[56];

		pbend = pitchbendMin + ((pitchbendMax - pitchbendMin) 
				* (pitchbend + modPitchbend + mmPbend).max(0).min(1));

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
//			[(mmOp1FreqRatio + op1FreqRatio) * outFreq, mmOp1Phase, mmOp1Amp, ],
//			[(mmOp2FreqRatio + op2FreqRatio) * outFreq, mmOp2Phase, mmOp2Amp, ],
////			[(mmOp3FreqRatio + op3FreqRatio) * outFreq, mmOp3Phase, mmOp3Amp, ],   /// why doesn't this one work????
//			[(mmOp4FreqRatio + op4FreqRatio) * outFreq, mmOp4Phase, mmOp4Amp, ],
//			[(mmOp5FreqRatio + op5FreqRatio) * outFreq, mmOp5Phase, mmOp5Amp, ],
//			[(mmOp6FreqRatio + op6FreqRatio) * outFreq, mmOp6Phase, mmOp6Amp, ],
//		[XLine.kr(300, 1200, 2), 0, Line.kr(1, 0, 3)],
//		[XLine.kr(600, 300, 2), 0, Line.kr(1, 0, 3)],
//		[311, 0, 1],
//		[702, 0, Line.kr(0, 1, 1)],
//		[1100, 0, 1],
//		[1010, 0, 1],

		];
		arrFMMods = [
			[modFM_11 + mmModFM_11, modFM_12 + mmModFM_12, 
				modFM_13 + mmModFM_13, modFM_14 + mmModFM_14, 
				modFM_15 + mmModFM_15, modFM_16 + mmModFM_16, ],
			[modFM_21 + mmModFM_21, modFM_22 + mmModFM_22, 
				modFM_23 + mmModFM_23, modFM_24 + mmModFM_24, 
				modFM_25 + mmModFM_25, modFM_26 + mmModFM_26, ],
			[modFM_31 + mmModFM_31, modFM_32 + mmModFM_32, 
				modFM_33 + mmModFM_33, modFM_34 + mmModFM_34, 
				modFM_35 + mmModFM_35, modFM_36 + mmModFM_36, ],
			[modFM_41 + mmModFM_41, modFM_42 + mmModFM_42, 
				modFM_43 + mmModFM_43, modFM_44 + mmModFM_44, 
				modFM_45 + mmModFM_45, modFM_46 + mmModFM_46, ],
			[modFM_51 + mmModFM_51, modFM_52 + mmModFM_52, 
				modFM_53 + mmModFM_53, modFM_54 + mmModFM_54, 
				modFM_55 + mmModFM_55, modFM_56 + mmModFM_56, ],
			[modFM_61 + mmModFM_61, modFM_62 + mmModFM_62, 
				modFM_63 + mmModFM_63, modFM_64 + mmModFM_64, 
				modFM_65 + mmModFM_65, modFM_66 + mmModFM_66, ],

//  these should be limited to range 0-1 but graphdef limit reached
//			[(modFM_11 + mmModFM_11).max(0).min(1), (modFM_12 + mmModFM_12).max(0).min(1), 
//				(modFM_13 + mmModFM_13).max(0).min(1), (modFM_14 + mmModFM_14).max(0).min(1), 
//				(modFM_15 + mmModFM_15).max(0).min(1), (modFM_16 + mmModFM_16).max(0).min(1), ],
//			[(modFM_21 + mmModFM_21).max(0).min(1), (modFM_22 + mmModFM_22).max(0).min(1), 
//				(modFM_23 + mmModFM_23).max(0).min(1), (modFM_24 + mmModFM_24).max(0).min(1), 
//				(modFM_25 + mmModFM_25).max(0).min(1), (modFM_26 + mmModFM_26).max(0).min(1), ],
//			[(modFM_31 + mmModFM_31).max(0).min(1), (modFM_32 + mmModFM_32).max(0).min(1), 
//				(modFM_33 + mmModFM_33).max(0).min(1), (modFM_34 + mmModFM_34).max(0).min(1), 
//				(modFM_35 + mmModFM_35).max(0).min(1), (modFM_36 + mmModFM_36).max(0).min(1), ],
//			[(modFM_41 + mmModFM_41).max(0).min(1), (modFM_42 + mmModFM_42).max(0).min(1), 
//				(modFM_43 + mmModFM_43).max(0).min(1), (modFM_44 + mmModFM_44).max(0).min(1), 
//				(modFM_45 + mmModFM_45).max(0).min(1), (modFM_46 + mmModFM_46).max(0).min(1), ],
//			[(modFM_51 + mmModFM_51).max(0).min(1), (modFM_52 + mmModFM_52).max(0).min(1), 
//				(modFM_53 + mmModFM_53).max(0).min(1), (modFM_54 + mmModFM_54).max(0).min(1), 
//				(modFM_55 + mmModFM_55).max(0).min(1), (modFM_56 + mmModFM_56).max(0).min(1), ],
//			[(modFM_61 + mmModFM_61).max(0).min(1), (modFM_62 + mmModFM_62).max(0).min(1), 
//				(modFM_63 + mmModFM_63).max(0).min(1), (modFM_64 + mmModFM_64).max(0).min(1), 
//				(modFM_65 + mmModFM_65).max(0).min(1), (modFM_66 + mmModFM_66).max(0).min(1), ],

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

		sumVelocity = ((velocity * 0.007874) + mmLevel).max(0).min(1);
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

		["SynthOptionCheckBox", "Envelope 2", arrOptionData, 14, 250], 
		["EZslider", "Pre-Delay 2", ControlSpec(0,1), "delay2", {this.updateEnvView2;}], 
		["TXMinMaxSliderSplit", "Attack 2", timeSpec, "attack2", "attackMin2", "attackMax2",
			{this.updateEnvView2;}], 
		["TXMinMaxSliderSplit", "Decay 2", timeSpec, "decay2", "decayMin2", "decayMax2",{this.updateEnvView2;}], 
		["EZslider", "Sustain level 2", ControlSpec(0, 1), "sustain2", {this.updateEnvView2;}], 
		["TXMinMaxSliderSplit", "Sustain time 2", timeSpec, "sustainTime2", "sustainTimeMin2", 
			"sustainTimeMax2",{this.updateEnvView2;}], 
		["TXMinMaxSliderSplit", "Release 2", timeSpec, "release2", "releaseMin2", "releaseMax2",
			{this.updateEnvView2;}], 
		["SynthOptionPopup", "Curve 2", arrOptionData, 4, 150, {system.showView;}], 
		["SynthOptionPopup", "Env. Type 2", arrOptionData, 5, 180], 
		["commandAction", "Plot envelope 2", {this.envPlot2;}],

		["SynthOptionCheckBox", "LFO 1", arrOptionData, 8, 250], 
		["TXMinMaxSliderSplit", "LFO 1 Freq", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
			"freqLFO1", "freqLFO1Min", "freqLFO1Max", nil, TXLFO.arrLFOFreqRanges], 
		["SynthOptionPopup", "LFO 1 Waveform", arrOptionData, 9], 
		["SynthOptionPopup", "LFO 1 Output range", arrOptionData, 10], 

		["SynthOptionCheckBox", "LFO 2", arrOptionData, 11, 250], 
		["NextLine"], 
		["TXMinMaxSliderSplit", "LFO 2 Freq", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
			"freqLFO2", "freqLFO2Min", "freqLFO2Max", nil, TXLFO.arrLFOFreqRanges], 
		["SynthOptionPopup", "LFO 2 Waveform", arrOptionData, 12], 
		["SynthOptionPopup", "LFO 2 Output range", arrOptionData, 13], 
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
		["ActionButton", "Envelope 2", {displayOption = "showEnv2"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showEnv2")], 
		["Spacer", 3], 
		["ActionButton", "LFO 1/2", {displayOption = "showLFO"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showLFO")], 
		["Spacer", 3], 
		["ActionButton", "Mod Matrix", {displayOption = "showModMatrix"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showModMatrix")], 
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


	
// xxxxx	 update buildguispecs
//			["TextViewDisplay", {TXWaveForm.arrDescriptions.at(arrOptions.at(0).asInteger);}],
//			["DividingLine"],
// xxxxx	 update buildguispecs
// xxxxx	for 6 operators add freq ratio, phase, amp, modIndex(x6), outLevel





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
	if (displayOption == "showEnv2", {
		guiSpecArray = guiSpecArray ++[
			["SynthOptionCheckBox", "Envelope 2", arrOptionData, 11, 250], 
			["NextLine"], 
			["TXPresetPopup", "Env presets", 
				TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(0)}), 
				TXEnvPresets.arrEnvPresets2(this, 4, 5).collect({arg item, i; item.at(1)})
			],
			["TXEnvDisplay", {this.envViewValues2;}, {arg view; envView2 = view;}],
			["NextLine"], 
			["EZslider", "Pre-Delay", ControlSpec(0,1), "delay2", {this.updateEnvView2;}], 
			["TXMinMaxSliderSplit", "Attack", timeSpec, "attack2", "attackMin2", "attackMax2",{this.updateEnvView2;}], 
			["TXMinMaxSliderSplit", "Decay", timeSpec, "decay2", "decayMin2", "decayMax2",{this.updateEnvView2;}], 
			["EZslider", "Sustain level", ControlSpec(0, 1), "sustain2", {this.updateEnvView2;}], 
			["TXMinMaxSliderSplit", "Sustain time", timeSpec, "sustainTime2", "sustainTimeMin2", 
				"sustainTimeMax2",{this.updateEnvView2;}], 
			["TXMinMaxSliderSplit", "Release", timeSpec, "release2", "releaseMin2", "releaseMax2",{this.updateEnvView2;}], 
			["NextLine"], 
			["SynthOptionPopup", "Curve", arrOptionData, 9, 150, {system.showView;}], 
			["SynthOptionPopup", "Env. Type", arrOptionData, 10, 180], 
			["Spacer", 4], 
			["ActionButton", "Plot", {this.envPlot2;}],
		];
	});
	if (displayOption == "showLFO", {
		guiSpecArray = guiSpecArray ++[
			["SynthOptionCheckBox", "LFO 1", arrOptionData, 1, 250], 
			["NextLine"], 
			["TXMinMaxSliderSplit", "Fade-in time", ControlSpec(0.01, 20, \exp), 
				"lfo1FadeIn", "lfo1FadeInMin", "lfo1FadeInMax"], 
			["TXMinMaxSliderSplit", "Frequency", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
				"freqLFO1", "freqLFO1Min", "freqLFO1Max", nil, TXLFO.arrLFOFreqRanges], 
			["SynthOptionPopup", "Waveform", arrOptionData, 2], 
			["SynthOptionPopup", "Output range", arrOptionData, 3], 
			["SpacerLine", 4], 
			["DividingLine"], 
			["SpacerLine", 4], 
			["SynthOptionCheckBox", "LFO 2", arrOptionData, 4, 250], 
			["NextLine"], 
			["TXMinMaxSliderSplit", "Fade-in time", ControlSpec(0.01, 20, \exp), 
				"lfo2FadeIn", "lfo2FadeInMin", "lfo2FadeInMax"], 
			["TXMinMaxSliderSplit", "Frequency", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
				"freqLFO2", "freqLFO2Min", "freqLFO2Max", nil, TXLFO.arrLFOFreqRanges], 
			["SynthOptionPopup", "Waveform", arrOptionData, 5], 
			["SynthOptionPopup", "Output range", arrOptionData, 6], 
		];
	});
	if (displayOption == "showModMatrix", {
		guiSpecArray = guiSpecArray ++[
			["NoteRangeSelector", "Note range", "noteModMin", "noteModMax"],
			["TXRangeSlider", "Vel range", ControlSpec(0, 127, step: 1), "velModMin", "velModMax"],
			["DividingLine"],
			["SpacerLine", 2], 
			["EZslider", "Slider 1", ControlSpec(0,1), "slider1"], 
			["EZslider", "Slider 2", ControlSpec(0,1), "slider2"], 
			["DividingLine"],
			["SpacerLine", 2], 
			["TextBarLeft", "  Source", 100],
			["TextBarLeft", "  Destination", 100],
			["TextBarLeft", "  Modulation amount", 154],
			["TextBarLeft", "  Scale", 80],
			["SpacerLine", 4],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source0", "i_Dest0", "mmValue0", arrMMScaleNames, "i_Scale0"], 
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source1", "i_Dest1", "mmValue1", arrMMScaleNames, "i_Scale1"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source2", "i_Dest2", "mmValue2", arrMMScaleNames, "i_Scale2"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source3", "i_Dest3", "mmValue3", arrMMScaleNames, "i_Scale3"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source4", "i_Dest4", "mmValue4", arrMMScaleNames, "i_Scale4"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source5", "i_Dest5", "mmValue5", arrMMScaleNames, "i_Scale5"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source6", "i_Dest6", "mmValue6", arrMMScaleNames, "i_Scale6"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source7", "i_Dest7", "mmValue7", arrMMScaleNames, "i_Scale7"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source8", "i_Dest8", "mmValue8", arrMMScaleNames, "i_Scale8"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source9", "i_Dest9", "mmValue9", arrMMScaleNames, "i_Scale9"],
/*
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source10", "i_Dest10", "mmValue10", arrMMScaleNames, "i_Scale10"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source11", "i_Dest11", "mmValue11", arrMMScaleNames, "i_Scale11"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source12", "i_Dest12", "mmValue12", arrMMScaleNames, "i_Scale12"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source13", "i_Dest13", "mmValue13", arrMMScaleNames, "i_Scale13"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source14", "i_Dest14", "mmValue14", arrMMScaleNames, "i_Scale14"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source15", "i_Dest15", "mmValue15", arrMMScaleNames, "i_Scale15"],
			["ModMatrixRowScale", arrMMSourceNames, arrMMDestNames, "i_Source16", "i_Dest16", "mmValue16", arrMMScaleNames, "i_Scale16"],
*/
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

envPlot2 {
	var del, att, dec, sus, sustime, rel, envCurve;
	del = this.getSynthArgSpec("delay2");
	att = this.getSynthArgSpec("attack2");
	dec = this.getSynthArgSpec("decay2");
	sus = this.getSynthArgSpec("sustain2");
	sustime = this.getSynthArgSpec("sustainTime2");
	rel = this.getSynthArgSpec("release2");
	envCurve = this.getSynthOption(9);
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

envViewValues2 {
	var attack, attackMin, attackMax, decay, decayMin, decayMax, sustain;
	var sustainTime, sustainTimeMin, sustainTimeMax, release, releaseMin, releaseMax;
	var del, att, dec, sus, sustime, rel;
	var arrTimesNorm, arrTimesNormedSummed;

	del = this.getSynthArgSpec("delay2");
	attack = this.getSynthArgSpec("attack2");
	attackMin = this.getSynthArgSpec("attackMin2");
	attackMax = this.getSynthArgSpec("attackMax2");
	att = attackMin + ((attackMax - attackMin) * attack);
	decay = this.getSynthArgSpec("decay2");
	decayMin = this.getSynthArgSpec("decayMin2");
	decayMax = this.getSynthArgSpec("decayMax2");
	dec = decayMin + ((decayMax - decayMin) * decay);
	sus = this.getSynthArgSpec("sustain2");
	sustainTime = this.getSynthArgSpec("sustainTime2");
	sustainTimeMin = this.getSynthArgSpec("sustainTimeMin2");
	sustainTimeMax = this.getSynthArgSpec("sustainTimeMax2");
	sustime = sustainTimeMin + ((sustainTimeMax - sustainTimeMin) * sustainTime);
	release = this.getSynthArgSpec("release2");
	releaseMin = this.getSynthArgSpec("releaseMin2");
	releaseMax = this.getSynthArgSpec("releaseMax2");
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

updateEnvView2 {
	if (envView2.class == SCEnvelopeView, {
		if (envView2.notClosed, {
			6.do({arg i;
				envView2.setEditable(i, true);
			});
			envView2.value = this.envViewValues2;
			6.do({arg i;
				envView2.setEditable(i, false);
			});
		});
	});
}

}

