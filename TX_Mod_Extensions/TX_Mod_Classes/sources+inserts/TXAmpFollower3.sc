// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAmpFollower3 : TXModuleBase {		// Audio In module 

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
	defaultName = "Amp Follower";
	moduleRate = "control";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Audio in", 1, "audioIn"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Follow time", 1, "modLag", 0]
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
		["audioIn", 0, 0],
		["lag", ControlSpec(10, 1000).unmap(20), defLagTime],
		["lagMin", 10, defLagTime], 
		["lagMax", 1000, defLagTime],
		["modLag", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioIn, lag, lagMin, lagMax, modLag;
		var lagtime;
		lagtime = lagMin + ((lagMax - lagMin) * (lag + modLag).max(0.001).min(1));
		Out.kr(out, 
			Lag.kr(			// lag the input 
				A2K.kr(Amplitude.ar(InFeedback.ar(audioIn,1), 0.01, 0.01)),
				lagtime/1000		//  lag time - divide to get seconds
			)
		);
	};
	holdControlSpec = ControlSpec(1, 10000);
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Follow time", holdControlSpec, "lag", "lagMin", "lagMax"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

