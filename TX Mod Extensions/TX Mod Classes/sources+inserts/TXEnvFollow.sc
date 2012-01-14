// Copyright (C) 2011  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXEnvFollow : TXModuleBase {		 

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
	defaultName = "Env Follow";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrAudSCInBusSpecs = [ 
		["Side-chain", 1, "insidechain"],
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
		["insidechain", 0, 0],
		["out", 0, 0],
		["wetDryMix", 1.0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { arg in, insidechain, out, wetDryMix, modWetDryMix;
		var input, sidechain, outSound, mixCombined;
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		input = TXClean.ar(InFeedback.ar(in,1));
		sidechain = TXClean.ar(InFeedback.ar(insidechain,1));
		outSound = (Balance.ar(input, sidechain) * mixCombined) 
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

