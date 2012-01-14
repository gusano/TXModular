// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXDelay2 : TXModuleBase {		// Delay 

	//	Notes:
	//	This is a delay which can be set to any time up to 16 secs.
	//	This version uses BufCombC

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
	classvar	<arrBufferSpecs;
	
	classvar	<maxDelaytime = 16;	//	delay time up to 16 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Delay";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Delay Time", 1, "modDelay", 0],
		["Feedback", 1, "modFeedback", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumDelay", defSampleRate * maxDelaytime, 1] ];
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
		["bufnumDelay", 0, \ir],
		["delay", 0.5, defLagTime],
		["delayMin", 10, defLagTime],
		["delayMax", 1000 * maxDelaytime, defLagTime],
		["feedback", 0.1, defLagTime],
		["feedbackMin", 0.001, defLagTime],
		["feedbackMax", 1.0, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modFeedback", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelay, delay=0.1, delayMin, delayMax, feedback, feedbackMin, 
			feedbackMax, wetDryMix, modDelay=0, modFeedback=0, modWetDryMix=0;
		var input, outSound, delaytime, feedbackVal, decaytime, mixCombined;
		input = InFeedback.ar(in,1);
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay).max(0.0001).min(1)) ) * delayMin / 1000;
		feedbackVal = feedbackMin + ( (feedbackMax-feedbackMin) * (feedback + modFeedback).max(0).min(1) );
		decaytime = delaytime * (1+(64 * feedbackVal / (0.5+delaytime)));
	//	decaytime = 0.1 + (delaytime * (1 + (128 * feedbackVal)) );
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		//	CombC.ar(in, maxdelaytime, delaytime, decaytime, mul, add)
		outSound = BufCombC.ar(bufnumDelay, input, delaytime, decaytime, mixCombined, 
			input * (1-mixCombined));
		Out.ar(out, outSound);
	};
	holdControlSpec = ControlSpec.new(10, 1000 * maxDelaytime, \exp );
	holdControlSpec2 = ControlSpec.new(0.001, 1.0, \exp );
	guiSpecArray = [
		["TextBarLeft", "Note - delay times shown in ms and bpm", 250],
		["NextLine"],
		["TXTimeBpmMinMaxSldr", "Delay", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["TXMinMaxSliderSplit", "Feedback", holdControlSpec2, "feedback", "feedbackMin", "feedbackMax"], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

