// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
// Thanks to Josh Parmenter on the SuperCollider list for the AmpSim code

TXAmpSim : TXModuleBase { 

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
	defaultName = "Amp Sim";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Tube factor", 1, "modTube", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
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
	var controlSpec;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["tube", 15, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modTube", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	controlSpec = ControlSpec.new(1, 50);
	
	synthDefFunc = { 
		arg in, out, tube, wetDryMix, modTube, modWetDryMix;
		var input, outSound, tubeCombined, mixCombined;
		var limit = 0.9;

		input = InFeedback.ar(in,1);

		tubeCombined = controlSpec.map (
			(controlSpec.unmap(tube) + modTube).max(0).min(1)
		);
		
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);

		outSound = Limiter.ar((((input*tubeCombined).exp - (input* tubeCombined * -1.2).exp)/
			((input*tubeCombined).exp + (input * tubeCombined * -1.0).exp)) /tubeCombined, 
			limit, 0.01);

		Out.ar(out, (outSound * mixCombined) + (input * (1-mixCombined)));
	};
	guiSpecArray = [
		["EZslider", "Tube factor", controlSpec, "tube"], 
		["SpacerLine", 2], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

