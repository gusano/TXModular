// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXFilterExtSt : TXModuleBase {		// Filter module 

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
	classvar	arrFreqRangePresets;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Filter St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Frequency", 1, "modfreq", 0],
		["Resonance", 1, "modres", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrFreqRangePresets = TXFilter.arrFreqRanges;
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["freq", 0.5, defLagTime],
		["freqMin",40, defLagTime],
		["freqMax", 20000, defLagTime],
		["res", 0.5, defLagTime],
		["resMin", 0,  defLagTime],
		["resMax", 1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modfreq", 0, defLagTime],
		["modres", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	// filter input
	arrOptions = [0];
	arrOptionData = [TXFilter.arrOptionData];
	synthDefFunc = { arg in, out, freq, freqMin, freqMax, 
			res, resMin, resMax, wetDryMix, modfreq = 0.0, modres = 0.0, modWetDryMix = 0.0;
		var input, outFunction, outFilter, outClean, sumfreq, sumres, mixCombined;
		input = InFeedback.ar(in,2);
		sumfreq = ( (freqMax/ freqMin) ** ((freq + modfreq).max(0.001).min(1)) ) * freqMin;
		sumres =  resMin + ( (resMax - resMin) * (res + modres).max(0).min(1) );
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outFilter = outFunction.value(
			input, 
			sumfreq, 
			(1 - sumres) // invert
		);
		// use tanh as a limiter to stop blowups
		Out.ar(out, (outFilter.tanh * mixCombined) + (input * (1-mixCombined)) );
	};
	guiSpecArray = [
		["SynthOptionPopup", "Filter", arrOptionData, 0], 
		["TXMinMaxSliderSplit", "Frequency", ControlSpec(0.midicps, 20000, \exponential), 
			"freq", "freqMin", "freqMax", nil, arrFreqRangePresets], 
		["TXMinMaxSliderSplit", "Resonance", ControlSpec(0, 1), "res", "resMin", "resMax"], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

