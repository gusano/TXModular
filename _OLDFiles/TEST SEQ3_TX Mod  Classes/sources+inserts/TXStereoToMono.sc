// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXStereoToMono : TXModuleBase {		// delay stereo 

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
	
	classvar	<maxDelaytime = 16;	//	maximum delay time in secs up to 16 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Stereo to mono";
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
	holdControlSpec = ControlSpec.new(10, 1000 * maxDelaytime, \exp );
	holdControlSpec2 = ControlSpec.new(0.001, 1.0, \exp );
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

