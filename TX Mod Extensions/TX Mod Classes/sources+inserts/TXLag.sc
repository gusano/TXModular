// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLag : TXModuleBase {		// Lag module 

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
	defaultName = "Lag";
	moduleRate = "control";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Lag time", 1, "modLag", 0]
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
		["in", 0, 0],
		["out", 0, 0],
		["lag", 0.5, defLagTime],
		["lagMin", 0.01, defLagTime], 
		["lagMax", 1, defLagTime],
		["modLag", 0, defLagTime],
	]; 
	synthDefFunc = { arg in, out, lag, lagMin, lagMax, modLag = 0;
		var lagtime;
		lagtime = ( (lagMax/lagMin) ** ((lag + modLag).max(0.001).min(1)) ) * lagMin;
		Out.kr(out, 
			Lag.kr(			// lag the input 
				In.kr(in,1),	// input bus
				lagtime		//  lag time
			)
			);
	};
	holdControlSpec = ControlSpec(0.0001, 30, \exp, 0, 1, units: " secs");
	guiSpecArray = [
		["TextBarLeft", "Exponential smoothing of a control signal"],
		["DividingLine"], 
		["TXMinMaxSliderSplit", "Lag Time", holdControlSpec, "lag", "lagMin", "lagMax"], 
		["DividingLine"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

