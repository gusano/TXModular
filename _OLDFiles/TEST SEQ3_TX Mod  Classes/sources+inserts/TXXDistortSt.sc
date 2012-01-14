// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXXDistortSt : TXModuleBase { 

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

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "X Distort St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["X Amp", 1, "modXAmp", 0],
		["Smoothing", 1, "modSmoothing", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
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
		["xamp", 0.5, defLagTime],
		["xampMin", 0, defLagTime],
		["xampMax", 1.0, defLagTime],
		["smoothing", 0.5, defLagTime],
		["smoothingMin", 0, defLagTime],
		["smoothingMax", 1.0, defLagTime],
		["outGain", 1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modXAmp", 0, defLagTime],
		["modSmoothing", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	
	synthDefFunc = { 
		arg in, out, xamp, xampMin, xampMax, smoothing, smoothingMin, smoothingMax, 
			outGain, wetDryMix, modXAmp, modSmoothing, modWetDryMix;
		var input, outSound, xampVal, smoothingVal, mixCombined;

		input = InFeedback.ar(in,2);

		xampVal = xampMin + ( (xampMax-xampMin) * (xamp + modXAmp).max(0).min(1) );
		smoothingVal = smoothingMin + ( (smoothingMax-smoothingMin) 
			* (smoothing + modSmoothing).max(-1).min(1) );
		
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);

		outSound = outGain * CrossoverDistortion.ar(input, xampVal, smoothingVal);

		outSound = LeakDC.ar(outSound, 0.995);

		Out.ar(out, (outSound * mixCombined) + (input * (1-mixCombined)));
	};
	guiSpecArray = [
		["TXMinMaxSliderSplit", "X Amp", ControlSpec.new(0.0, 1.0, \lin ), 
			"xamp", "xampMin", "xampMax"], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Smoothing", ControlSpec.new(-0.0, 1.0, \lin ), 
			"smoothing", "smoothingMin", "smoothingMax"], 
		["SpacerLine", 4], 
		["EZslider", "Out Gain", ControlSpec(0, 1), "outGain"],
		["SpacerLine", 4], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

