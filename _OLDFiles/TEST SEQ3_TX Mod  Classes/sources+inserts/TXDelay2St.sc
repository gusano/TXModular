// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXDelay2St : TXModuleBase {		// delay stereo 

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
	classvar	<maxDelaytime = 16;	//	maximum delay time in secs up to 16 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Delay St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Delay Time L", 1, "modDelayL", 0],
		["Delay Time R", 1, "modDelayR", 0],
		["Feedback L", 1, "modFeedbackL", 0],
		["Feedback R", 1, "modFeedbackR", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrBufferSpecs = [ ["bufnumDelayL", defSampleRate * maxDelaytime, 1], ["bufnumDelayR", defSampleRate * maxDelaytime, 1] ];
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
		["bufnumDelayL", 0, \ir],
		["bufnumDelayR", 0, \ir],
		["delayL", 0.5, defLagTime],
		["delayMinL", 10, defLagTime],
		["delayMaxL", 1000 * maxDelaytime, defLagTime],
		["delayR", 0.5, defLagTime],
		["delayMinR", 10, defLagTime],
		["delayMaxR", 1000 * maxDelaytime, defLagTime],
		["feedbackL", 0.1, defLagTime],
		["feedbackMinL", 0.001, defLagTime],
		["feedbackMaxL", 1.0, defLagTime],
		["feedbackR", 0.1, defLagTime],
		["feedbackMinR", 0.001, defLagTime],
		["feedbackMaxR", 1.0, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelayL", 0, defLagTime],
		["modDelayR", 0, defLagTime],
		["modFeedbackL", 0, defLagTime],
		["modFeedbackR", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelayL, bufnumDelayR, delayL, delayMinL, delayMaxL, delayR, delayMinR, delayMaxR, 
			feedbackL, feedbackMinL, feedbackMaxL, feedbackR, feedbackMinR, feedbackMaxR, 
			wetDryMix, modDelayL=0, modDelayR=0, modFeedbackL=0, modFeedbackR=0, modWetDryMix=0;
		var inputL, inputR, outSound, delaytimeL, feedbackValL, decaytimeL, delaytimeR, feedbackValR, decaytimeR, mixCombined;
		// use tanh as a limiter to stop blowups
		inputL = InFeedback.ar(in,1);
		inputR = InFeedback.ar(in+1,1);
		delaytimeL =( (delayMaxL/delayMinL) ** ((delayL + modDelayL).max(0.0001).min(1)) ) * delayMinL / 1000;
		feedbackValL = feedbackMinL + ( (feedbackMaxL-feedbackMinL) * (feedbackL + modFeedbackL).max(0).min(1) );
		decaytimeL = delaytimeL * (1+(64 * feedbackValL / (0.5+delaytimeL)));
		delaytimeR =( (delayMaxR/delayMinR) ** ((delayR + modDelayR).max(0.0001).min(1)) ) * delayMinR / 1000;
		feedbackValR = feedbackMinR + ( (feedbackMaxR-feedbackMinR) * (feedbackR + modFeedbackR).max(0).min(1) );
		decaytimeR = delaytimeR * (1+(64 * feedbackValR / (0.5+delaytimeR)));
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outSound = [
			BufCombC.ar(bufnumDelayL, inputL, delaytimeL, decaytimeL, mixCombined, 
			inputL * (1-mixCombined))
		,
			BufCombC.ar(bufnumDelayR, inputR, delaytimeR, decaytimeR, mixCombined, 
			inputR * (1-mixCombined))
		];
		// use tanh as a limiter to stop blowups
		Out.ar(out, outSound);
	};
	holdControlSpec = ControlSpec.new(10, 1000 * maxDelaytime, \exp );
	holdControlSpec2 = ControlSpec.new(0.001, 1.0, \exp );
	guiSpecArray = [
		["TextBarLeft", "Note - delay times shown in ms and bpm", 250],
		["NextLine"],
		["TXTimeBpmMinMaxSldr", "Delay L", holdControlSpec, "delayL", "delayMinL", "delayMaxL"], 
		["TXMinMaxSliderSplit", "Feedback L", holdControlSpec2, "feedbackL", "feedbackMinL", "feedbackMaxL"], 
		["DividingLine"], 
		["TXTimeBpmMinMaxSldr", "Delay R", holdControlSpec, "delayR", "delayMinR", "delayMaxR"], 
		["TXMinMaxSliderSplit", "Feedback R ", holdControlSpec2, "feedbackR", "feedbackMinR", "feedbackMaxR"], 
		["DividingLine"], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

