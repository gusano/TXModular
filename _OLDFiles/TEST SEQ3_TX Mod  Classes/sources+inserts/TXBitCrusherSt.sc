// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXBitCrusherSt : TXModuleBase { 

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

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Bit Crusher St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Sample rate", 1, "modSamplerate", 0],
		["Bit size", 1, "modBitSize", 0],
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
	var controlSpecSample, controlSpecBit;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["inGain", 3, defLagTime],
		["outGain", 0.5, defLagTime],
		["samplerate", 11025, defLagTime],
		["bitSize", 8, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modSamplerate", 0, defLagTime],
		["modBitSize", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	controlSpecSample = ControlSpec.new(100, 22050, \exp );
	controlSpecBit = ControlSpec.new(1, 24, \exp );
	
	synthDefFunc = { 
		arg in, out, inGain, outGain, samplerate, bitSize, wetDryMix, modSamplerate, modBitSize, modWetDryMix;
		var input, outSound, samplerateCombined, bitSizeCombined, mixCombined;
		var downsamp, bitRedux;

		input = InFeedback.ar(in,2);

		samplerateCombined = controlSpecSample.map(
			(controlSpecSample.unmap(samplerate) + modSamplerate).max(0).min(1)
		);
		bitSizeCombined = controlSpecBit.map ( 
			(controlSpecBit.unmap(bitSize) + modBitSize).max(0).min(1)
		);

		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);

		downsamp = Latch.ar(inGain * input, Impulse.ar(samplerateCombined));
		bitRedux = downsamp.round(0.5 ** bitSizeCombined);

		outSound = input * (1-bitRedux);
		Out.ar(out, (outGain * outSound * mixCombined) + (input * (1-mixCombined)));
	};
	guiSpecArray = [
		["DividingLine"], 
		["SpacerLine", 2], 
		["EZslider", "In Gain", ControlSpec(0, 10), "inGain"],
		["DividingLine"], 
		["SpacerLine", 2], 
		["EZslider", "Sample rate", controlSpecSample, "samplerate"], 
		["EZslider", "Bit size", controlSpecBit, "bitSize"], 
		["DividingLine"], 
		["SpacerLine", 2], 
		["EZslider", "Out Gain", ControlSpec(0, 1), "outGain"],
		["DividingLine"], 
		["SpacerLine", 2], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

