// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXNotchPhaserSt : TXModuleBase {		// Phaser module 

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

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Notch Phaser St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Centre freq", 1, "modFFreq", 0],
		["Resonance", 1, "modRes", 0],
		["LFO rate", 1, "modFreq", 0],
		["LFO depth", 1, "modLFODepth", 0],
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
	var holdControlSpec, holdControlSpec2, arrFreqRanges;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["fFreq", 0.5, defLagTime],
		["fFreqMin", 100, defLagTime],
		["fFreqMax", 10000, defLagTime],
		["res", 0.5, defLagTime],
		["resMin", 0, defLagTime],
		["resMax", 1.0, defLagTime],
		["freq", 0.4, defLagTime],
		["freqMin", 0.01, defLagTime],
		["freqMax", 100, defLagTime],
		["lfoDepth", 0.3, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modFFreq", 0, defLagTime],
		["modRes", 0, defLagTime],
		["modFreq", 0, defLagTime],
		["modLfoDepth", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	arrOptions = [0, 2];
	arrOptionData = [
		TXLFO.arrOptionData,
		[	
			["1 layer", 1],
			["2 layers", 2],
			["3 layers", 3],
			["4 layers", 4],
			["5 layers", 5],
		],

	];
	synthDefFunc = { 
		arg in, out, fFreq, fFreqMin, fFreqMax, res, resMin, 
			resMax, freq, freqMin, freqMax, lfoDepth, wetDryMix, 
			modFFreq, modRes, modFreq, modLfoDepth, modWetDryMix;
		var outLfo, outFreq, outLfoDepth, outFunction, phaseFreq;
		var input, outSound, resVal, mixCombined, filterFunc, numLayers;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outLfoDepth = (lfoDepth + modLfoDepth).max(0).min(1);
		// select function based on arrOptions
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outLfo = outFunction.value(outFreq) * outLfoDepth;
		input = TXClean.ar(InFeedback.ar(in,2));
		phaseFreq =( (fFreqMax/fFreqMin) ** ((fFreq + modFFreq + [outLfo, outLfo.neg]).max(0).min(1)) ) * fFreqMin;
		resVal = resMin + ( (resMax - resMin) * (res + modRes).max(0).min(1) );
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		//	BRF.ar(in, freq, rq, mul, add);
		filterFunc = {arg argIn, argFactor; 
			BRF.ar(argIn, phaseFreq * [1, 1.5, 0.75, 1.25, 0.875].at(argFactor), (1 - resVal));
		};
		numLayers = this.getSynthOption(1);
		outSound = input;
		numLayers.do({ arg item, i;
			var holdSound;
			holdSound = filterFunc.value(outSound, i);
			outSound = holdSound;
		});
		outSound = outSound.madd(mixCombined/2, input * (1-mixCombined));


//		outSound = Mix.new(BRF.ar(input, [phaseFreq * 2, phaseFreq, phaseFreq/2], (1 - resVal)))
//			.madd(mixCombined, input * (mixCombined + (1-mixCombined)));
//		outSound = (BRF.ar(input, phaseFreq, (1 - resVal)))
//			.madd(mixCombined/2, input * ((mixCombined/2) + (1-mixCombined)));
//		outSound = (BRF.ar(BRF.ar(BRF.ar(input, phaseFreq/2, (1 - resVal)), phaseFreq, (1 - resVal)), phaseFreq/1.5, (1 - resVal)))
//			outSound.madd(mixCombined/2, input * (1-mixCombined));

		// use tanh as a limiter
		Out.ar(out, TXClean.ar(outSound.tanh));
	};
	holdControlSpec = ControlSpec(60, 10000, \exp );
	arrFreqRanges = [
		["Presets: ", [100, 100]],
		["Full range 100 - 10000 hz", [100, 10000]],
		["Low range 100 - 1000 hz", [100, 1000]],
		["Medium range 500 - 5000 hz", [500, 5000]],
		["High range 1000 - 10000 hz", [1000, 10000]],
	];
	guiSpecArray = [
		["SynthOptionPopupPlusMinus", "No. layers", arrOptionData, 1, 300], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Centre freq", ControlSpec(60, 10000, \exp ),"fFreq", "fFreqMin", "fFreqMax", 
			nil, arrFreqRanges], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Resonance", ControlSpec(0, 1), "res", "resMin", "resMax"], 
		["SpacerLine", 4], 
		["SynthOptionPopupPlusMinus", "Waveform", arrOptionData, 0], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "LFO rate", ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz"), 
			"freq", "freqMin", "freqMax", nil, TXLFO.arrLFOFreqRanges], 
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

