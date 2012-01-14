// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXFlanger2St : TXModuleBase {		// stereo Flanger module 

	//	Notes:
	//	This is a Flanger which can be set to any time up to 0.5 secs.
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
	classvar	<guiWidth=500;
	classvar	<arrBufferSpecs;
	classvar	<maxDelaytime = 0.5;	//	delay time up to 0.5 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Flanger St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Flange Time", 1, "modDelay", 0],
		["Feedback", 1, "modFeedback", 0],
		["LFO rate", 1, "modFreq", 0],
		["LFO depth", 1, "modLFODepth", 0],
		["Stereo depth", 1, "modStereoDepth", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrBufferSpecs = [ 
		["bufnumDelayL", defSampleRate * maxDelaytime/2, 1], ["bufnumDelayR", defSampleRate * maxDelaytime/2, 1],
		["bufnumDelayL2", defSampleRate * maxDelaytime, 1], ["bufnumDelayR2", defSampleRate * maxDelaytime, 1] 
	];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	var holdControlSpec, holdControlSpec2, arrTimeRanges;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumDelayL", 0, \ir],
		["bufnumDelayR", 0, \ir],
		["bufnumDelayL2", 0, \ir],
		["bufnumDelayR2", 0, \ir],
		["delay", 0.5, defLagTime],
		["delayMin", 0.1, defLagTime],
		["delayMax", 100, defLagTime],
		["feedback", 0.1, defLagTime],
		["feedbackMin", 0.01, defLagTime],
		["feedbackMax", 1.0, defLagTime],
		["freq", 0.5, defLagTime],
		["freqMin", 0.01, defLagTime],
		["freqMax", 20, defLagTime],
		["lfoDepth", 0.1, defLagTime],
		["stereoDepth", 0.5, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modFeedback", 0, defLagTime],
		["modFreq", 0, defLagTime],
		["modLfoDepth", 0, defLagTime],
		["modStereoDepth", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [TXLFO.arrOptionData];
	synthDefFunc = { 
		arg in, out, bufnumDelayL, bufnumDelayR, bufnumDelayL2, bufnumDelayR2, delay=0.1, delayMin, delayMax, feedback, feedbackMin, 
			feedbackMax, freq, freqMin, freqMax, lfoDepth, stereoDepth, wetDryMix, 
			modDelay, modFeedback, modFreq, modLfoDepth, modStereoDepth, modWetDryMix;
		var outLfo, outFreq, outLfoDepth, outFunction, outVolRamp;
		var inputL, inputR, inputLDelayed, inputRDelayed, outSound, delaytime, feedbackVal, decaytime, stereoDepthComb, mixCombined;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outLfoDepth = (lfoDepth + modLfoDepth).max(0).min(1);
		// select function based on arrOptions
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outLfo = outFunction.value(outFreq) * outLfoDepth;
		inputL = TXClean.ar(InFeedback.ar(in,1));
		inputR = TXClean.ar(InFeedback.ar(in+1,1));
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay + outLfo).max(0.0001).min(1)) ) * delayMin / 1000;
		feedbackVal = feedbackMin + ( (feedbackMax-feedbackMin) * (feedback + modFeedback).max(0).min(1) );
		decaytime = 0.1 + (delaytime * (1 + (128 * feedbackVal)) );
		stereoDepthComb =  (stereoDepth + modStereoDepth).max(0.001).min(1);
		inputLDelayed = BufDelayC.ar(bufnumDelayL, inputL, stereoDepthComb/2 * ((0.5 * outLfoDepth) - outLfo) * (delaytime/2));
		inputRDelayed = BufDelayC.ar(bufnumDelayR, inputR, stereoDepthComb/2 * (outLfo - (0.5 * outLfoDepth)) * (delaytime/2));
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		//	CombC.ar(in, maxdelaytime, delaytime, decaytime, mul, add)
		outSound = BufCombC.ar([bufnumDelayL2, bufnumDelayR2], [inputLDelayed, inputRDelayed], delaytime, decaytime, mixCombined, 
			[inputL, inputR] * (1-mixCombined));
		outVolRamp = EnvGen.kr(Env.new([0, 0, 1], [0.1,0.1]), 1);
		// use tanh as a limiter to stop blowups
		Out.ar(out, TXClean.ar(outVolRamp * outSound.tanh));
	};
	holdControlSpec = ControlSpec.new(0.1, 100, \exp );
	holdControlSpec2 = ControlSpec.new(0.01, 1.0, \exp );
	arrTimeRanges = [
		["Presets: ", [0.1, 100]],
		["Full range 0.1 - 100 ms", [0.1, 100]],
		["Low range 0.1 - 10 ms", [0.1, 10]],
		["Medium range 1 - 30 ms", [1, 30]],
		["High range 10 - 100 ms", [10, 100]],
	];
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Flange time", holdControlSpec,"delay", "delayMin", "delayMax", nil, arrTimeRanges], 
		["TXMinMaxSliderSplit", "Feedback", holdControlSpec2, "feedback", "feedbackMin", "feedbackMax"], 
		["SynthOptionPopupPlusMinus", "Waveform", arrOptionData, 0], 
		["TXMinMaxSliderSplit", "LFO rate", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
			"freq", "freqMin", "freqMax",nil, TXLFO.arrLFOFreqRanges], 
		["EZslider", "LFO depth", ControlSpec(0, 1), "lfoDepth"],
		["EZslider", "Stereo depth", ControlSpec(0, 1), "stereoDepth"],
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

