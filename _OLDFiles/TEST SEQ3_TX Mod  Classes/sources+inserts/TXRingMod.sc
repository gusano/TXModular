// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXRingMod : TXModuleBase {		// Lag module 

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
		 ["Modulator", 1, "inmodulator"]
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
	]; 
	synthDefFunc = { arg in, inmodulator, out;
		var outSound;
		// ring modulate 2 inputs - input bus * modulator bus
		outSound = InFeedback.ar(in,1)	* InFeedback.ar(inmodulator,1); 
		Out.ar(out, outSound);
	};
	guiSpecArray = [
		//	no controls needed for this module
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

