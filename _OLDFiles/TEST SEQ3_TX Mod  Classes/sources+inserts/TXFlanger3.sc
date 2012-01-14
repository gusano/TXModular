// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXFlanger3 : TXModuleBase {		// Flanger module 

	//	Notes:
	//	This is a Flanger which can be set to any time up to 0.5 secs.
	//	This version uses flange freq instead of flange time

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
	classvar	<maxDelaytime = 0.5;	//	delay time up to 0.5 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Flanger";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Flange Freq", 1, "modFreq", 0],
		["Feedback", 1, "modFeedback", 0],
		["LFO rate", 1, "modLFOFreq", 0],
		["LFO depth", 1, "modLFODepth", 0],
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
	var holdControlSpec, holdControlSpec2, arrFreqRanges;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumDelay", 0, \ir],
		["freq", 0.5, defLagTime],
		["freqMin", 0.midicps, defLagTime],
		["freqMax", 127.midicps, defLagTime],
		["feedback", 0.1, defLagTime],
		["feedbackMin", 0.01, defLagTime],
		["feedbackMax", 1.0, defLagTime],
		["lfoFreq", 0.25, defLagTime],
		["lfoFreqMin", 0.01, defLagTime],
		["lfoFreqMax", 20, defLagTime],
		["lfoDepth", 0.1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modFreq", 0, defLagTime],
		["modFeedback", 0, defLagTime],
		["modLFOFreq", 0, defLagTime],
		["modLfoDepth", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [TXLFO.arrOptionData];
	synthDefFunc = { 
		arg in, out, bufnumDelay, freq=0.1, freqMin, freqMax, feedback, feedbackMin, 
			feedbackMax, lfoFreq, lfoFreqMin, lfoFreqMax, lfoDepth, wetDryMix, 
			modFreq, modFeedback, modLFOFreq, modLfoDepth, modWetDryMix;
		var outLfo, outLFOFreq, outLfoDepth, outFunction, outVolRamp;
		var input, outSound, delaytime, feedbackVal, decaytime, mixCombined;
		outLFOFreq = ( (lfoFreqMax/lfoFreqMin) ** ((lfoFreq + modLFOFreq).max(0.001).min(1)) ) * lfoFreqMin;
		outLfoDepth = (lfoDepth + modLfoDepth).max(0).min(1);
		// select function based on arrOptions
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outLfo = outFunction.value(outLFOFreq) * outLfoDepth;
		input = InFeedback.ar(in,1);
		delaytime =(
			((freqMax/freqMin) ** ((freq + modFreq + outLfo).max(0.0001).min(1)) ) * freqMin
		).reciprocal;
		feedbackVal = feedbackMin + ( (feedbackMax-feedbackMin) * (feedback + modFeedback).max(0).min(1) );
		decaytime = 0.1 + (delaytime * (1 + (128 * feedbackVal)) );
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outSound = BufCombC.ar(bufnumDelay, input, delaytime, decaytime, mixCombined,
			input * (1-mixCombined));
		outVolRamp = EnvGen.kr(Env.new([0, 0, 1], [0.1, 0.1]), 1);
		// use tanh as a limiter to stop blowups
		Out.ar(out, outVolRamp * LeakDC.ar(outSound.tanh, 0.995));
	};
	holdControlSpec = ControlSpec.new(0.midicps, 127.midicps, \exp );
	holdControlSpec2 = ControlSpec.new(0.01, 1.0, \exp );
	arrFreqRanges = [
		["Presets: ", [40, 127.midicps]],
		["MIDI Note Range 8.17 - 12543 hz", [0.midicps, 127.midicps]],
		["Wide range 40 - 8k hz", [40, 8000]],
		["Low range 40 - 250 hz", [40, 250]],
		["Mid range 100 - 2k hz", [100, 2000]],
		["High range 1k - 6k hz", [1000, 6000]],
	];
	guiSpecArray = [
		["TXMinMaxFreqNoteSldr", "Flange freq", holdControlSpec,"freq", "freqMin", "freqMax", nil, arrFreqRanges], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Feedback", holdControlSpec2, "feedback", "feedbackMin", "feedbackMax"], 
		["SpacerLine", 4], 
		["SynthOptionPopup", "Waveform", arrOptionData, 0], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "LFO rate", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
			"lfoFreq", "lfoFreqMin", "lfoFreqMax", nil, TXLFO.arrLFOFreqRanges], 
		["SpacerLine", 4], 
		["EZslider", "LFO depth", ControlSpec(0, 1), "lfoDepth"],
		["SpacerLine", 4], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

