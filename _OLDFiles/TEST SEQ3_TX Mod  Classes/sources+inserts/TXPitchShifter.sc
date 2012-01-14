// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXPitchShifter : TXModuleBase {		

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
	defaultName = "Pitch Shifter";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Pitch ratio", 1, "modRatio", 0],
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
		["out", 0, 0],
		["ratio", 0.75/3.75, defLagTime],
		["ratioMin", 0.25, defLagTime],
		["ratioMax", 4, defLagTime],
		["transpose", 0, 0],
		["randPitch", 0, defLagTime],
		["randTime", 0.004, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modRatio", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	// filter input
	arrOptions = [2];
	arrOptionData = [[["0.05 s", 0.05], ["0.1 s", 0.1], ["0.15 s", 0.15], ["0.2 s", 0.2], ["0.25 s", 0.25], 
		["0.3 s", 0.3]]];
	synthDefFunc = {
		 arg in, out, ratio, ratioMin, ratioMax, transpose, wetDryMix, randPitch,
			 randTime, modRatio, modWetDryMix;
		var input, grainSize, outWet, outTranspose, ratioCombined, mixCombined, outSound;
		input = TXClean.ar(InFeedback.ar(in,1));
		grainSize = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		ratioCombined = ratioMin + ((ratioMax - ratioMin) * (ratio + modRatio).max(0).min(1));
		outTranspose = 2 ** (transpose/12);
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outWet = PitchShift.ar(input, grainSize, ratioCombined * outTranspose, randPitch, randTime * grainSize);
		outSound = (outWet * mixCombined) + (input * (1-mixCombined));
		Out.ar(out, TXClean.ar(outSound));
	};
	guiSpecArray = [
		["SynthOptionPopup", "Grain size", arrOptionData, 0], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Pitch ratio", ControlSpec(0.25, 4), "ratio", "ratioMin", "ratioMax"], 
		["SpacerLine", 4], 
		["Transpose"], 
		["SpacerLine", 4], 
		["EZSlider", "Random pitch", \unipolar, "randPitch"], 
		["SpacerLine", 4], 
		["EZSlider", "Random time", \unipolar, "randTime"], 
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

