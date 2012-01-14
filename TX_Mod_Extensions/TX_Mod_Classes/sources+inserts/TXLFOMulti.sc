// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLFOMulti : TXModuleBase {		// Audio In module 

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
	arrSynthArgSpecs = [
		["out", 0, 0],
		["freq", 0.5, defLagTime],
		["freqMin", 0.01, defLagTime],
		["freqMax", 100, defLagTime],
		["modFreq", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [
		[	["Sine", {arg lfoFreq; SinOsc.kr(lfoFreq, 0).range(0, 1)}],
			["Square", {arg lfoFreq; LFPulse.kr(lfoFreq).range(0, 1)}],
			["Triangular", {arg lfoFreq; LFTri.kr(lfoFreq).range(0, 1)}],
			["Sawtooth", {arg lfoFreq; LFSaw.kr(lfoFreq).range(0, 1)}],
			["Sawtooth reversed", {arg lfoFreq; (1-(LFSaw.kr(lfoFreq)).range(0, 1))}],
			["Noise - stepped", {arg lfoFreq; LFDNoise0.kr(lfoFreq).range(0, 1)}],
			["Noise - smooth linear", {arg lfoFreq; LFDNoise1.kr(lfoFreq).range(0, 1)}],
			["Noise - smooth cubic", {arg lfoFreq; LFDNoise3.kr(lfoFreq).range(0, 1)}],
			["Noise - clipped", {arg lfoFreq; LFDClipNoise.kr(lfoFreq).range(0, 1)}],
		],
	];
	synthDefFunc = { arg out, freq, freqMin, freqMax, modFreq = 0;
		var outFreq, outFunction;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		// select function based on arrOptions
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		Out.kr(out, outFunction.value(outFreq));
	};
	holdControlSpec = ControlSpec(0.01, 100, \exp, 0, 1, units: " Hz");
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Freq", holdControlSpec, "freq", "freqMin", "freqMax"], 
		["SynthOptionPopup", "Waveform", arrOptionData, 0], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

