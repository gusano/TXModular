// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXDelay3St : TXModuleBase {		// delay stereo 

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
		["Freeze", 1, "modFreeze", 0],
		["Smooth time", 1, "modSmoothTime", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrBufferSpecs = [ 
		["bufnumDelayL", defSampleRate * maxDelaytime, 1], 
		["bufnumDelayR", defSampleRate * maxDelaytime, 1] ];
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
		["bufnumDelayR", 1, \ir],
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
		["freeze", 0, 0],
		["smoothTime", 0.2, 0],
		["wetDryMix", 1.0, defLagTime],
		["modDelayL", 0, defLagTime],
		["modDelayR", 0, defLagTime],
		["modFeedbackL", 0, defLagTime],
		["modFeedbackR", 0, defLagTime],
		["modFreeze", 0, 0],
		["modSmoothTime", 0, 0],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelayL, bufnumDelayR, delayL, delayMinL, delayMaxL, delayR, delayMinR, delayMaxR, 
			feedbackL, feedbackMinL, feedbackMaxL, feedbackR, feedbackMinR, feedbackMaxR, 
			freeze, smoothTime, wetDryMix, modDelayL=0, modDelayR=0, modFeedbackL=0, modFeedbackR=0, 
			modFreeze=0, modSmoothTime=0, modWetDryMix=0;
		var inputL, inputR, outSound, delaytimeL, feedbackValL, decaytimeL, delaytimeR, feedbackValR, decaytimeR, 
			mixCombined, freezeCombined, invLagFreeze, smoothTimeCombined;
		freezeCombined = (freeze + modFreeze).max(0).min(1).round(1);
		invLagFreeze = 1 - Lag.kr(freezeCombined, 0.1);
		smoothTimeCombined = (smoothTime + modSmoothTime).max(0).min(1);
		inputL = TXClean.ar(TXEnvPresets.startEnvFunc.value * InFeedback.ar(in,1));
		inputR = TXClean.ar(TXEnvPresets.startEnvFunc.value * InFeedback.ar(in+1,1));
		delaytimeL = Lag2.kr(
			(( (delayMaxL/delayMinL) ** ((delayL + modDelayL).max(0.0001).min(1)) ) * delayMinL / 1000),
			smoothTimeCombined
		);
		delaytimeR = Lag2.kr(
			(( (delayMaxR/delayMinR) ** ((delayR + modDelayR).max(0.0001).min(1)) ) * delayMinR / 1000),
			smoothTimeCombined
		);
		feedbackValL = feedbackMinL + ( (feedbackMaxL-feedbackMinL) * (feedbackL + modFeedbackL).max(0).min(1) );
		feedbackValR = feedbackMinR + ( (feedbackMaxR-feedbackMinR) * (feedbackR + modFeedbackR).max(0).min(1) );
		decaytimeL = (( (100/delaytimeL) ** feedbackValL.sin) * delaytimeL)
			+ (freezeCombined * 3600); // if freeze is on add 60 minutes to decaytimeL  
		decaytimeR = (( (100/delaytimeR) ** feedbackValR.sin) * delaytimeR)
			+ (freezeCombined * 3600); // if freeze is on add 60 minutes to decaytimeR  
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outSound = [
			BufCombC.ar(bufnumDelayL, inputL * invLagFreeze, delaytimeL, decaytimeL, mixCombined, 
			inputL * (1-mixCombined)),
			BufCombC.ar(bufnumDelayR, inputR * invLagFreeze, delaytimeR, decaytimeR, mixCombined, 
			inputR * (1-mixCombined))
		];
		Out.ar(out, TXClean.ar(outSound));
	};
	holdControlSpec = ControlSpec.new(10, 1000 * maxDelaytime, \exp );
	holdControlSpec2 = ControlSpec.new(0.001, 1.0, \lin );
	guiSpecArray = [
		["TextBarLeft", "Note - delay times shown in ms and bpm", 250],
		["NextLine"],
		["TXTimeBpmMinMaxSldr", "Del. time L", holdControlSpec, "delayL", "delayMinL", "delayMaxL"], 
		["TXTimeBpmMinMaxSldr", "Del. time R", holdControlSpec, "delayR", "delayMinR", "delayMaxR"], 
		["DividingLine"], 
		["EZslider", "Smooth time", ControlSpec(0, 1), "smoothTime"], 
		["DividingLine"], 
		["TXMinMaxSliderSplit", "Feedback L", holdControlSpec2, "feedbackL", "feedbackMinL", "feedbackMaxL"], 
		["TXMinMaxSliderSplit", "Feedback R ", holdControlSpec2, "feedbackR", "feedbackMinR", "feedbackMaxR"], 
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

