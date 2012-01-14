// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXBinauralBeater : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=100;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Binaural Beats";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Base frequency", 1, "modBassFreq", 0],
		["Beats frequency", 1, "modBeatsFreq", 0],
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

*arrFreqRanges {
	^ [
		["Presets: ", [40, 127.midicps]],
		["Full Audible range 40 - 20k hz", [40, 127.midicps]],
		["MIDI Note Range - 8.17 - 12543 hz", [0.midicps, 127.midicps]],
		["Wide range 40 - 8k hz", [40, 8000]],
		["Low range 40 - 250 hz", [40, 250]],
		["Mid range 100 - 2k hz", [100, 2000]],
		["High range 1k - 6k hz", [1000, 6000]],
	];
}
init {arg argInstName;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["out", 0, 0],
		["bassFreq", 0.5, defLagTime],
		["bassFreqMin", 0.midicps, defLagTime],
		["bassFreqMax", 127.midicps, defLagTime],
		["beatsFreq", 0.5, defLagTime],
		["beatsFreqMin", 0, defLagTime],
		["beatsFreqMax", 20, defLagTime],
		["modBassFreq", 0, defLagTime],
		["modBeatsFreq", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg out, bassFreq, bassFreqMin, bassFreqMax, beatsFreq, beatsFreqMin, beatsFreqMax, 
			modBassFreq, modBeatsFreq;
		var outSound, outBassFreq, outBeatsFreq, outFreqArr;
		outBassFreq = ( (bassFreqMax/bassFreqMin) ** 
			((bassFreq + modBassFreq).max(0.001).min(1)) ) * bassFreqMin;
		outBeatsFreq = ( (beatsFreqMax/beatsFreqMin) ** 
			((beatsFreq + modBeatsFreq).max(0.001).min(1)) ) * beatsFreqMin;
		outFreqArr = [outBassFreq + (outBeatsFreq / 2), outBassFreq - (outBeatsFreq / 2)];
		outSound = SinOsc.ar(outFreqArr); 
		Out.ar(out, outSound);
	};
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Bass Freq", ControlSpec(0.midicps, 127.midicps, \exponential), 
			"bassFreq", "bassFreqMin", "bassFreqMax", nil, this.class.arrFreqRanges], 
		["TXMinMaxSliderSplit", "Beats Freq", ControlSpec(0, 100, \exponential), 
			"beatsFreq", "beatsFreqMin", "beatsFreqMax"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

