// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXCompander2 : TXModuleBase {		// Compander module 

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
	classvar	<guiWidth=500;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Compander";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["threshold", 1, "modThreshold", 0],
		["output gain", 1, "modOutGain", 0]
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
 		["modThreshold", 0, defLagTime],
		["modOutGain", 0, defLagTime],
	]; 
	synthDefFunc = { arg in, out, threshold, expanderRatio, expanderRatioMin, expanderRatioMax,
			compressorRatio, compressorRatioMin, compressorRatioMax, attack, release, outGain, 
			modThreshold = 0, modOutGain = 0;
		var input, thresh, thresholdSum, outGainSum, compRatio, expRatio;
		compRatio = compressorRatioMin + ( (compressorRatioMax - compressorRatioMin) * compressorRatio);
		expRatio = expanderRatioMin + ( (expanderRatioMax - expanderRatioMin) * expanderRatio);
		input = InFeedback.ar(in,1);
		thresholdSum = (threshold + modThreshold).max(0.001).min(1);
		outGainSum = (outGain + modOutGain).max(0).min(10);
		Out.ar(out, Compander.ar(input, input, thresholdSum, expRatio, 1/compRatio, 
			attack, release, outGainSum));
	};
	guiSpecArray = [
		["TXPresetPopup", "Presets", 
			TXCompPresets.arrCompPresets(this).collect({arg item, i; item.at(0)}), 
			TXCompPresets.arrCompPresets(this).collect({arg item, i; item.at(1)})
		],
		["TXMinMaxSliderSplit", "Expand ratio", ControlSpec(0.1, 10), 
			"expanderRatio", "expanderRatioMin", "expanderRatioMax"], 
		["EZslider", "Threshold", ControlSpec(0.001, 1, \amp), "threshold"],
		["TXMinMaxSliderSplit", "Comp. ratio", ControlSpec(0.1, 10), 
			"compressorRatio", "compressorRatioMin", "compressorRatioMax"], 
		["EZslider", "Clamp time", ControlSpec(0.001, 1), "attack"],
		["EZslider", "Relax time", ControlSpec(0.001, 1), "release"],
		["EZslider", "Out gain", ControlSpec(0, 10), "outGain"],
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

