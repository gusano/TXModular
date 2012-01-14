// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXNormalizerSt : TXModuleBase {		// Distortion module 

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
	classvar	<guiHeight=300;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Normalizer St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["input gain", 1, "modInLevel", 0],
		["level", 1, "modOutLevel", 0]
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
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["inLevel", 1, defLagTime],
		["outLevel", 0.5, defLagTime],
		["modInLevel", 0, defLagTime],
 		["modOutLevel", 0, defLagTime],
	]; 
	arrOptions = [2];
	arrOptionData = [ [
		["2 ms", 0.002],
		["6 ms", 0.006],
		["10 ms - default", 0.01],
		["20 ms", 0.02],
	] ];
	synthDefFunc = { arg in, out, inLevel, outLevel, modInLevel, modOutLevel;
		var input, outLevelSum, lookahead, outLimiter;
		input = InFeedback.ar(in,2);
		outLevelSum = (outLevel + modOutLevel).max(0).min(1);
		lookahead = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outLimiter = Normalizer.ar(inLevel * input, outLevelSum, lookahead);
		Out.ar(out, outLimiter);
	};
	guiSpecArray = [
		["SynthOptionPopup", "Lookahead", arrOptionData, 0], 
		["SpacerLine", 4], 
		["EZslider", "Input Gain", ControlSpec(0, 3), "inLevel"],
		["SpacerLine", 4], 
		["EZslider", "Level", ControlSpec(0, 1), "outLevel"],
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

