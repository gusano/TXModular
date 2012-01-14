// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXDistortion2St : TXModuleBase {		// Distortion module 

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
	defaultName = "Distortion St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Depth", 1, "modDepth", 0],
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
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["inGain", 1, defLagTime],
		["depth", 0.5, defLagTime],
		["outGain", 1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDepth", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	// filter input
	arrOptions = [0];
	arrOptionData = [TXDistort.arrOptionData];
	synthDefFunc = { arg in, out, inGain, depth, outGain, wetDryMix, modDepth=0.0, modWetDryMix=0.0;
		var input, outFunction, outDistorted, outClean, depthCombined, mixCombined;
		input = InFeedback.ar(in,2);
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		depthCombined = (depth + modDepth).max(0).min(1);
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outDistorted = outGain * outFunction.value(inGain * input, depthCombined);
		Out.ar(out, (outDistorted * mixCombined) + (input * (1-mixCombined)) );
	};
	guiSpecArray = [
		["SynthOptionPopup", "Distortion", arrOptionData, 0], 
		["SpacerLine", 4], 
		["EZslider", "In Gain", ControlSpec(0, 10), "inGain"],
		["SpacerLine", 4], 
		["EZslider", "Depth", ControlSpec(0, 1), "depth"],
		["SpacerLine", 4], 
		["EZslider", "Out Gain", ControlSpec(0, 1), "outGain"],
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

