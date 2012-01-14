// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWaveform4 : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiWidth=500;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Waveform";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Modify 1", 1, "modChange1", 0],
		["Modify 2", 1, "modChange2", 0],
		["Frequency", 1, "modFreq", 0]
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
		["out", 0, 0],
		["freq", 0.5, defLagTime],
		["freqMin", 20, defLagTime],
		["freqMax", 20000, defLagTime],
		["change1", 0, defLagTime],
		["change1Min", 0, defLagTime],
		["change1Max", 1, defLagTime],
		["change2", 0, defLagTime],
		["change2Min", 0, defLagTime],
		["change2Max", 1, defLagTime],
		["modFreq", 0, defLagTime],
		["modChange1", 0, defLagTime],
		["modChange2", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [TXWaveForm.arrOptionData];
	synthDefFunc = { arg out, freq, freqMin, freqMax, change1, change1Min, change1Max, change2, 
		change2Min, change2Max, modFreq = 0, modChange1 = 0, modChange2 = 0;
		var outFreq, outFunction, outChange1, outChange2;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outChange1 = change1Min + ((change1Max - change1Min) * (change1 + modChange1).max(0).min(1));
		outChange2 = change2Min + ((change2Max - change2Min) * (change2 + modChange2).max(0).min(1));
		Out.ar(out, outFunction.value(outFreq, outChange1, outChange2));
	};
	guiSpecArray = [
		["SynthOptionPopup", "Waveform", arrOptionData, 0], 
		["DividingLine"],
		["TXMinMaxSliderSplit", "Modify 1", \unipolar, "change1", "change1Min", "change1Max"], 
		["TXMinMaxSliderSplit", "Modify 2", \unipolar, "change2", "change2Min", "change2Max"], 
		["DividingLine"],
		["TXMinMaxSliderSplit", "Freq", \freq, "freq", "freqMin", "freqMax"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

