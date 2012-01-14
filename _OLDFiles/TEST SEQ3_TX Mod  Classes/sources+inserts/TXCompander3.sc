// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXCompander3 : TXModuleBase {		// Compander module 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<arrBufferSpecs;
	classvar	<guiHeight=300;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Compander";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrAudSCInBusSpecs = [ 
		 ["Side-chain", 1, "sideChain"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Threshold", 1, "modThreshold", 0],
		["Output Gain", 1, "modOutGain", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
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
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["sideChain", 0, 0],
		["out", 0, 0],
		["threshold", 0.5, defLagTime],
		["expanderRatio", 0.09091, defLagTime],
		["expanderRatioMin", 0.1, defLagTime],
		["expanderRatioMax", 10, defLagTime],
		["compressorRatio", 0.09091, defLagTime],
		["compressorRatioMin", 0.1, defLagTime],
		["compressorRatioMax", 10, defLagTime],
		["attack", 0.01, defLagTime],
		["release", 0.01, defLagTime],
		["outGain", 1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
 		["modThreshold", 0, defLagTime],
		["modOutGain", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [
		[
			["use main input signal to control compander", {arg input, sideChain; input;}],
			["use side-chain signal to control compander", {arg input, sideChain; InFeedback.ar(sideChain,1);}],
		]
	];
	synthDefFunc = { arg in, sideChain, out, threshold, expanderRatio, expanderRatioMin, expanderRatioMax,
			compressorRatio, compressorRatioMin, compressorRatioMax, attack, release, outGain, wetDryMix, 
			modThreshold = 0, modOutGain = 0, modWetDryMix=0;
		var input, thresh, thresholdSum, outGainSum, compRatio, expRatio, controlSignal, mixCombined, outSound;
		compRatio = compressorRatioMin + ( (compressorRatioMax - compressorRatioMin) * compressorRatio);
		expRatio = expanderRatioMin + ( (expanderRatioMax - expanderRatioMin) * expanderRatio);
		input = InFeedback.ar(in,1);
		thresholdSum = (threshold + modThreshold).max(0.001).min(1);
		outGainSum = (outGain + modOutGain).max(0).min(10);
		controlSignal = arrOptionData.at(0).at(arrOptions.at(0)).at(1)
			.value(input, sideChain);
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outSound = Compander.ar(input, controlSignal, thresholdSum, expRatio, 1/compRatio, 
			attack, release, outGainSum);
		Out.ar(out, (outSound * mixCombined) + (input * (1-mixCombined)));
	};
	guiSpecArray = [
		["SynthOptionPopup", "Control signal", arrOptionData, 0], 
		["EZslider", "Threshold", ControlSpec(0.001, 1, \amp), "threshold"],
		["DividingLine"], 
		["SpacerLine", 2], 
		["TXPresetPopup", "Presets", 
			TXCompPresets.arrCompPresets(this).collect({arg item, i; item.at(0)}), 
			TXCompPresets.arrCompPresets(this).collect({arg item, i; item.at(1)})
		],
		["TXMinMaxSliderSplit", "Expand ratio", ControlSpec(0.1, 10), 
			"expanderRatio", "expanderRatioMin", "expanderRatioMax"], 
		["TXMinMaxSliderSplit", "Comp. ratio", ControlSpec(0.1, 10), 
			"compressorRatio", "compressorRatioMin", "compressorRatioMax"], 
		["EZslider", "Clamp time", ControlSpec(0.001, 1), "attack"],
		["EZslider", "Relax time", ControlSpec(0.001, 1), "release"],
		["DividingLine"], 
		["SpacerLine", 2], 
		["EZslider", "Out gain", ControlSpec(0, 10), "outGain"],
		["DividingLine"], 
		["SpacerLine", 2], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

