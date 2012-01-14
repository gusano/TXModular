// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXDelay3 : TXModuleBase {		// Delay 

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
		["Freeze", 1, "modFreeze", 0],
		["Smooth time", 1, "modSmoothTime", 0],
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
		["freeze", 0, 0],
		["smoothTime", 0.2, 0],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modFeedback", 0, defLagTime],
		["modFreeze", 0, 0],
		["modSmoothTime", 0, 0],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelay, delay=0.1, delayMin, delayMax, feedback, feedbackMin, feedbackMax, 
			freeze, smoothTime, wetDryMix, modDelay=0, modFeedback=0, modFreeze=0,
			modSmoothTime=0, modWetDryMix=0;
		var input, outSound, delaytime, feedbackVal, decaytime, mixCombined, freezeCombined, 
			invLagFreeze, smoothTimeCombined;
		freezeCombined = (freeze + modFreeze).max(0).min(1).round(1);
		invLagFreeze = 1 - Lag.kr(freezeCombined, 0.1);
		smoothTimeCombined = (smoothTime + modSmoothTime).max(0).min(1);
		input = TXClean.ar(TXEnvPresets.startEnvFunc.value * InFeedback.ar(in,1));
		delaytime = Lag2.kr(
			(( (delayMax/delayMin) ** ((delay + modDelay).max(0.0001).min(1)) ) * delayMin / 1000),
			smoothTimeCombined
		);
		feedbackVal = feedbackMin + ( (feedbackMax-feedbackMin) * (feedback + modFeedback).max(0).min(1) );
		decaytime = (( (100/delaytime) ** feedbackVal.sin) * delaytime)
			+ (freezeCombined * 3600); // if freeze is on add 60 minutes to decaytime  
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outSound = BufCombC.ar(bufnumDelay, input * invLagFreeze, delaytime, decaytime, mixCombined, 
			input * (1-mixCombined));
		Out.ar(out, TXClean.ar(outSound));
	};
	holdControlSpec = ControlSpec.new(10, 1000 * maxDelaytime, \exp );
	holdControlSpec2 = ControlSpec.new(0.001, 1.0, \lin );
	guiSpecArray = [
		["TextBarLeft", "Note - delay times shown in ms and bpm", 250],
		["NextLine"],
		["TXTimeBpmMinMaxSldr", "Delay time", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["DividingLine"], 
		["EZslider", "Smooth time", ControlSpec(0, 1), "smoothTime"], 
		["DividingLine"], 
		["TXMinMaxSliderSplit", "Feedback", holdControlSpec2, "feedback", "feedbackMin", "feedbackMax"], 
		["DividingLine"], 
		["WetDryMixSlider"], 
		["DividingLine"], 
		["TXCheckBox", "Freeze", "freeze"], 
		["Spacer", 20], 
		["ActionButtonDark", "Clear", {this.clearBuffers}], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

