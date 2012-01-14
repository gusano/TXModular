// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSimpleMIDISynth3 : TXModuleBase {

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

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Mini-Synth";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Modify 1", 1, "modModify1", 0],
		["Modify 2", 1, "modModify2", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain", 1, "modSustain", 0],
		["Release", 1, "modRelease", 0],
		["Curve", 1, "modCurve", 0],
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["out", 0, 0],
		["gate", 1, 0],
		["note", 0, 0],
		["velocity", 0, 0],
		["transpose", 0, 0],
		["modify1", 0.5, defLagTime],
		["modify2", 0.5, defLagTime],
		["attack", 0.005, \ir],
		["decay", 0.15, \ir],
		["sustain", 1, \ir],
		["release", 0.25, \ir],
		["curve", 0, \ir],
		["modModify1", 0, defLagTime],
		["modModify2", 0, defLagTime],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modRelease", 0, \ir],
		["modCurve", 0, \ir],
  	]; 
	arrOptions = [0];
	arrOptionData = [TXWaveForm.arrOptionData];
	synthDefFunc = { arg out, gate, note, velocity, transpose, modify1, modify2, attack, decay, sustain, release, 
		curve, modModify1, modModify2, modAttack, modDecay, modSustain, modRelease, modCurve ;
		var outEnv, outFreq, outFunction, outWave, curveAdjusted, mod1, mod2;
		curveAdjusted = ( (curve + modCurve).max(0).min(1) - 0.5) * 20;
		mod1 = (modify1 + modModify1).max(0).min(1);
		mod2 = (modify2 + modModify2).max(0).min(1);
		outEnv = EnvGen.kr(
			Env.adsr(5 * (attack + modAttack).max(0).min(1), 5 * (decay + modDecay).max(0).min(1), 
				(sustain + modSustain).max(0).min(1), 5 * (release + modRelease).max(0).min(1), 
				1, 
				curveAdjusted
			), 
			gate, 
			doneAction: 2
		); 
		outFreq = (note + transpose).max(0).min(127).midicps;
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outWave = outFunction.value(
			outFreq, 
			(modify1 + modModify1).max(0).min(1), 
			(modify2 + modModify2).max(0).min(1)
		);
		// amplitude is vel *  0.00315 approx. == 1 / 127 * 0.4
		Out.ar(out, outEnv * outWave * (velocity * 0.00315));
	};
	guiSpecArray = [
		["NextLine"], 
		["SynthOptionPopup", "Waveform", arrOptionData, 0], 
		["EZslider", "Modify 1", \unipolar, "modify1"], 
		["EZslider", "Modify 2", \unipolar, "modify2"], 
		["DividingLine"], 
		["EZsliderUnmapped", "Attack", ControlSpec(0, 5), "attack"], 
		["EZsliderUnmapped", "Decay", ControlSpec(0, 5), "decay"], 
		["EZslider", "Sustain", ControlSpec(0, 1), "sustain"], 
		["EZsliderUnmapped", "Release", ControlSpec(0, 5), "release"], 
		["EZsliderUnmapped", "Curve", ControlSpec(-10, 10, step: 1), "curve"], 
		["DividingLine"], 
		["MIDIChannelSelector"], 
		["Transpose"], 
		["DividingLine"], 
	];

	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	load the synthdef and create the Group for synths to belong to
	this.loadDefAndMakeGroup;
}

}

