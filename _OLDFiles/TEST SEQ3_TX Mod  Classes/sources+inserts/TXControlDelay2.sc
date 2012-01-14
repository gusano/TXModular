// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXControlDelay2 : TXModuleBase {		// delay module 

	//	Notes:
	//	This is a delay which can be set to any time up to 16 secs.
	//	
	//	

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=200;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	
	classvar	<maxDelaytime = 66;	//	delay time up to 6 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Control Delay";
	moduleRate = "control";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Delay Time", 1, "modDelay", 0],
		["Wet-Dry Mix", 1, "modWetDryMix", 0]
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
	var holdControlSpec, holdControlSpec2;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["delay", 0.5, defLagTime],
		["delayMin", 10, defLagTime],
		["delayMax", 6000, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, delay=0.1, delayMin, delayMax, wetDryMix, modDelay=0, modWetDryMix=0;
		var input, outSound, delaytime, decaytime, mixCombined;
		input = In.kr(in,1);
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay).max(0.0001).min(1)) ) * delayMin / 1000;
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		//	CombC.kr(in, maxdelaytime, delaytime, decaytime, mul, add)
		outSound = DelayC.kr(input, maxDelaytime, delaytime, mixCombined, 
			input * (1-mixCombined));
		Out.kr(out, outSound);
	};
	holdControlSpec = ControlSpec.new(10, 6000, \exp );
	guiSpecArray = [
		["TXTimeBpmMinMaxSldr", "Delay ms/bpm", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["WetDryMixSlider"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

