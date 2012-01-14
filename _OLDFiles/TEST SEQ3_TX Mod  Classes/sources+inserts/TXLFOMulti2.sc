// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLFOMulti2 : TXModuleBase {		 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=150;
	classvar	<guiWidth=450;
	classvar	<guiLeft=150;
	classvar	<guiTop=300;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "LFO";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Freq", 1, "modFreq", 0],
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
	var holdControlSpec;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["out", 0, 0],
		["freq", 0.5, defLagTime],
		["freqMin", 0.01, defLagTime],
		["freqMax", 100, defLagTime],
		["modFreq", 0, defLagTime],
	]; 
	arrOptions = [0, 0];
	arrOptionData = [
		TXLFO.arrOptionData,
		TXLFO.arrLFOOutputRanges,
	];
	synthDefFunc = { arg out, freq, freqMin, freqMax, modFreq = 0;
		var outFreq, outFunction, rangeFunction, outSignal;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		// select function based on arrOptions
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		rangeFunction = arrOptionData.at(1).at(arrOptions.at(1)).at(1);
		outSignal = rangeFunction.value(TXClean.kr(outFunction.value(outFreq)));
		Out.kr(out, outSignal);
	};
	holdControlSpec = ControlSpec(0.001, 100, \exp, 0, 1, units: " Hz");
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Frequency", holdControlSpec, "freq", "freqMin", "freqMax",
			nil, TXLFO.arrLFOFreqRanges], 
		["SpacerLine", 2], 
		["SynthOptionPopupPlusMinus", "Waveform", arrOptionData, 0], 
		["SpacerLine", 2], 
		["SynthOptionPopupPlusMinus", "Output range", arrOptionData, 1], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

