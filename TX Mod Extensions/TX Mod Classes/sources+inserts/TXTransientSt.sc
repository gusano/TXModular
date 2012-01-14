// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXTransientSt : TXModuleBase {		// Distortion module 

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
	classvar	<guiWidth=500;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Transient St";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 
		["Up Slope", 1, "modUpFreq", 0],
		["Down Slope", 1, "modDownFreq", 0]
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
		["upFreq", 0.5, defLagTime],
		["upFreqMin", 20, defLagTime],
		["upFreqMax", 20000, defLagTime],
		["downFreq", 0.5, defLagTime],
		["downFreqMin", 20, defLagTime],
		["downFreqMax", 20000, defLagTime],
		["modUpFreq", 0, defLagTime],
 		["modDownFreq", 0, defLagTime],
	]; 
	synthDefFunc = { arg in, out, upFreq, upFreqMin, upFreqMax, downFreq, downFreqMin, downFreqMax,
		modUpFreq, modDownFreq;
		var input, upFreqSum, downFreqSum, outSlew;
		input = InFeedback.ar(in,2);
		upFreqSum = ( (upFreqMax/upFreqMin) ** ((upFreq + modUpFreq).max(0.001).min(1)) ) * upFreqMin;
		downFreqSum = ( (downFreqMax/downFreqMin) ** ((downFreq + modDownFreq).max(0.001).min(1)) ) * downFreqMin;
		outSlew = Slew.ar(input, upFreqSum, downFreqSum);
		Out.ar(out, outSlew);
	};
	guiSpecArray = [
		["TextBarLeft", "Transient shaper - limits the slope of a signal"],
		["TXMinMaxSliderSplit", "Up Slope", ControlSpec(20, 20000, \exponential), 
			"upFreq", "upFreqMin", "upFreqMax"], 
		["TXMinMaxSliderSplit", "Down Slope", ControlSpec(20, 20000, \exponential), 
			"downFreq", "downFreqMin", "downFreqMax"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

