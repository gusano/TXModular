// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXFlangerSt : TXModuleBase {		// stereo Flanger module 

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
	
	classvar	<maxDelaytime = 0.5;	//	delay time up to 0.5 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Flanger st.";
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
		["delay", 0.5, defLagTime],
		["delayMin", 0.1, defLagTime],
		["delayMax", 100, defLagTime],
		["feedback", 0.1, defLagTime],
		["feedbackMin", 0.01, defLagTime],
		["feedbackMax", 1.0, defLagTime],
		["freq", 0.5, defLagTime],
		["freqMin", 0.01, defLagTime],
		["freqMax", 100, defLagTime],
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
	arrOptionData = [
		[	["Sine", {arg lfoFreq; SinOsc.kr(lfoFreq, 0, 0.5, 0.5)}],
			["Square", {arg lfoFreq; LFPulse.kr(lfoFreq, 0.5)}],
			["Triangular", {arg lfoFreq; LFTri.kr(lfoFreq, 0.5, 0.5)}],
			["Sawtooth", {arg lfoFreq; LFSaw.kr(lfoFreq, 0.5, 0.5)}],
			["Sawtooth reversed", {arg lfoFreq; (1-LFSaw.kr(lfoFreq, 0.5, 0.5))}],
			["Noise - stepped", {arg lfoFreq; LFNoise0.kr(lfoFreq, 0.5, 0.5)}],
			["Noise - smooth linear", {arg lfoFreq; LFNoise1.kr(lfoFreq, 0.5, 0.5)}],
			["Noise - smooth quadratic", {arg lfoFreq; LFNoise2.kr(lfoFreq, 0.5, 0.5)}],
			["Noise - clipped", {arg lfoFreq; LFClipNoise.kr(lfoFreq, 0.5, 0.5)}],
		],
	];
	synthDefFunc = { 
		arg in, out, delay=0.1, delayMin, delayMax, feedback, feedbackMin, 
			feedbackMax, freq, freqMin, freqMax, lfoDepth, stereoDepth, wetDryMix, 
			modDelay, modFeedback, modFreq, modLfoDepth, modStereoDepth, modWetDryMix;
		var outLfo, outFreq, outLfoDepth, outFunction;
		var inputL, inputR, inputLDelayed, inputRDelayed, outSound, delaytime, feedbackVal, decaytime, stereoDepthComb, mixCombined;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outLfoDepth = (lfoDepth + modLfoDepth).max(0).min(1);
		// select function based on arrOptions
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outLfo = outFunction.value(outFreq) * outLfoDepth;
		inputL = InFeedback.ar(in,1);
		inputR = InFeedback.ar(in+1,1);
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay + outLfo).max(0.0001).min(1)) ) * delayMin / 1000;
		feedbackVal = feedbackMin + ( (feedbackMax-feedbackMin) * (feedback + modFeedback).max(0).min(1) );
		decaytime = 0.1 + (delaytime * (1 + (128 * feedbackVal)) );
		stereoDepthComb =  (stereoDepth + modStereoDepth).max(0.001).min(1);
		inputLDelayed = DelayC.ar(inputL, maxDelaytime/2, stereoDepthComb/2 * ((0.5 * outLfoDepth) - outLfo) * (delaytime/2));
		inputRDelayed = DelayC.ar(inputR, maxDelaytime/2, stereoDepthComb/2 * (outLfo - (0.5 * outLfoDepth)) * (delaytime/2));
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		//	CombC.ar(in, maxdelaytime, delaytime, decaytime, mul, add)
		outSound = CombC.ar([inputLDelayed, inputRDelayed], maxDelaytime, delaytime, decaytime, mixCombined, 
			[inputL, inputR] * (1-mixCombined));
		Out.ar(out, outSound);
	};
	holdControlSpec = ControlSpec.new(0.1, 100, \exp );
	holdControlSpec2 = ControlSpec.new(0.01, 1.0, \exp );
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Flange time", holdControlSpec,"delay", "delayMin", "delayMax"], 
		["TXMinMaxSliderSplit", "Feedback", holdControlSpec2, "feedback", "feedbackMin", "feedbackMax"], 
		["SynthOptionPopup", "Waveform", arrOptionData, 0], 
		["TXMinMaxSliderSplit", "LFO rate", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), "freq", "freqMin", "freqMax"], 
		["EZslider", "LFO depth", ControlSpec(0, 1), "lfoDepth"],
		["EZslider", "Stereo depth", ControlSpec(0, 1), "stereoDepth"],
		["WetDryMixSlider"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

