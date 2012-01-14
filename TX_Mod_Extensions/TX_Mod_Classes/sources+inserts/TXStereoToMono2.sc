// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXStereoToMono2 : TXModuleBase {		

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
	defaultName = "Stereo to Mono";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["L-R Balance", 1, "modLeftRightBalance", 0]
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
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["leftRightBalance", 0.5, defLagTime],
		["modLeftRightBalance", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, leftRightBalance, modLeftRightBalance=0;
		var inputL, inputR, outSound, lrBalCombined;
		inputL = InFeedback.ar(in,1);
		inputR = InFeedback.ar(in+1,1);
		lrBalCombined = (leftRightBalance + modLeftRightBalance).max(0).min(1);
		outSound = Mix.new([inputL * (1-lrBalCombined), inputR * lrBalCombined]);
		Out.ar(out, outSound);
	};
	guiSpecArray = [
		["EZSlider", "L-R balance", \unipolar,"leftRightBalance"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

