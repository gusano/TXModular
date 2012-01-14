// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXBPM : TXModuleBase {		// Audio In module 

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
	classvar	<guiLeft=100;
	classvar	<guiTop=300;

	//////////////////////////////////////////////////////////////////////////////////////////////
	//	N.B. -  this is a temporary version of BPM
	//	- it doesn't have modBPM working - it only uses the sliders bpm value
	//	- the gui needs to be changed to use TXMinMaxSlider which isn't finished yet.
	//	
	//			see comments below
	/////////////////////////////////////////////////////////////////////////////////////////////

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "BPM Trigger";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["BPM", 0, "modBPM", 0],
		["Phase", 0, "modPhase", 0]
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
		["bpm", 0.5, defLagTime],
		["bpmMin", 20, defLagTime],
		["bpmMax", 180, defLagTime],
		["phase", 0, defLagTime],
		["modBPM", 0, defLagTime],
		["modPhase", 0, defLagTime],
	]; 
	synthDefFunc = { arg out = 0, bpm = 0.5, bpmMin=20, bpmMax=180, phase=0, modBPM = 0, modPhase=0;
			Out.kr(out, Impulse.kr(bpm, (phase+modPhase).min(1).max(0)));
		};
	holdControlSpec = ControlSpec(1, 999);
	guiSpecArray = [
		["TXMinMaxSliderSplit", "BPM", holdControlSpec, "bpm", "bpmMin", "bpmMax"], 
		["EZSlider", "Phase", \unipolar, "phase"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

