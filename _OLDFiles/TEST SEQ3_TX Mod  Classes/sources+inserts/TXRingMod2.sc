// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXRingMod2 : TXModuleBase {		// Lag module 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=100;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "RingMod";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrAudSCInBusSpecs = [ 
		["Modulator", 1, "inmodulator"],
	];	
	arrCtlSCInBusSpecs = [ 
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
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["inmodulator", 0, 0],
		["out", 0, 0],
		["wetDryMix", 1.0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { arg in, inmodulator, out, wetDryMix, modWetDryMix;
		var input, outSound, mixCombined;
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		input = TXClean.ar(InFeedback.ar(in,1));
		// ring modulate 2 inputs - input bus * modulator bus
		outSound = (input * InFeedback.ar(inmodulator,1) * mixCombined) 
			+ (input * (1-mixCombined));
		Out.ar(out, TXClean.ar(outSound));
	};
	guiSpecArray = [
		["WetDryMixSlider"]
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

