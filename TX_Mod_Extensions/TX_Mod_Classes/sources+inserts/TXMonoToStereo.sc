// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMonoToStereo : TXModuleBase {		

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
	defaultName = "Mono to Stereo";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["L-R Pan", 1, "modLeftRightPan", 0]
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

init {arg argInstName;
	var holdControlSpec, holdControlSpec2;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["leftRightPan", 0.5, defLagTime],
		["leftRightPanMin", 0.0, defLagTime],
		["leftRightPanMax", 1.0, defLagTime],
		["modLeftRightPan", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, leftRightPan, leftRightPanMin, leftRightPanMax, modLeftRightPan=0;
		var input, outSound, lrPanCombined;
		input = InFeedback.ar(in,1);
		lrPanCombined = leftRightPanMin + 
			((leftRightPanMax - leftRightPanMin) * (leftRightPan + modLeftRightPan).max(0).min(1));
		outSound = Pan2.ar(input, lrPanCombined.madd(2,-1));
		Out.ar(out, outSound);
	};
	guiSpecArray = [
		["TXMinMaxSliderSplit", "L-R Pan", \unipolar, "leftRightPan", "leftRightPanMin", "leftRightPanMax", 
			nil, 
			[	["Presets:", []], ["Full Stereo", [0, 1]], ["Half Stereo", [0.25, 0.75]], ["Centre Only", [0.5, 0.5]], 
				["Left Half", [0, 0.5]], ["Left Bias", [0, 0.75]], ["Right Bias", [0.25, 1]], ["Right Half", [0.5, 1]]
			]
		], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

