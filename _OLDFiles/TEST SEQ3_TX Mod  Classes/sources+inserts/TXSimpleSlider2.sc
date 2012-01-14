// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSimpleSlider2 : TXModuleBase {

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
	
	var	midiControlRoutine;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Simple Slider";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [
		["Value", 1, "modSliderVal", 0],
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
		["sliderVal", 0, 0],
		["sliderValMin", 0, 0],
		["sliderValMax", 1, 0],
		["modSliderVal", 0, 0],
	]; 
	synthDefFunc = { 
		arg out, sliderVal, sliderValMin, sliderValMax, modSliderVal;
		var sliderValCombined;
		sliderValCombined = sliderValMin + ((sliderValMax - sliderValMin) * (sliderVal + modSliderVal).max(0).min(1));
		Out.kr(out, sliderValCombined);
	};
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Value", nil.asSpec, "sliderVal", "sliderValMin", "sliderValMax"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

