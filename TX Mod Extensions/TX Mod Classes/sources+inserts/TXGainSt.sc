// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXGainSt : TXModuleBase {		// Compander module 

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
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Gain St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Output gain", 1, "modOutGain", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName, arrPresets;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["outGain", 1.0, defLagTime],
		["outGainMin", 0.0, defLagTime],
		["outGainMax", 1.0, defLagTime],
		["phaseInvert", 0, defLagTime],
		["modOutGain", 0, defLagTime],
		["modPhaseInvert", 0, defLagTime],
	]; 
	synthDefFunc = { arg in, out, outGain, outGainMin, outGainMax, phaseInvert, 
			modOutGain, modPhaseInvert;
		var input, outGainCombined;
		input = InFeedback.ar(in,2) * (1 - (2 * phaseInvert));
		outGainCombined = outGainMin + ( (outGainMax - outGainMin) * (outGain + modOutGain).max(0).min(1));
		Out.ar(out, input * outGainCombined);
	};
	arrPresets = [
		["Presets: ", [0, 1]],
		["0 - 1", [0, 1]],
		["0 - 2", [0, 2]],
		["0 - 5", [0, 5]],
		["0 - 10", [0, 10]],
		["1 - 2", [1, 2]],
		["1 - 5", [1, 5]],
		["1 - 10", [1, 10]],
	];
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Out gain", ControlSpec(0, 10), 
			"outGain", "outGainMin", "outGainMax", nil, arrPresets], 
		["SpacerLine", 4], 
		["TextBar", "Phase ", nil, nil, nil, nil, \right],
		["TXCheckBox", "Invert", "phaseInvert"],
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

