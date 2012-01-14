// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAmpFollower2 : TXModuleBase {		// Audio In module 

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
	defaultName = "Amp Follower";
	moduleRate = "control";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Audio in", 1, "audioIn"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Attack/ decay", 1, "modLag", 0]
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
		["lag", 0.05, defLagTime],
		["lagMin", 0.01, defLagTime], 
		["lagMax", 1, defLagTime],
		["modLag", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioIn, lag, lagMin, lagMax, modLag;
		var lagtime;
		lagtime = lagMin + ((lagMax - lagMin) * (lag + modLag).max(0.001).min(1));
		Out.kr(out, 
			Lag.kr(			// lag the input 
				Amplitude.kr(InFeedback.ar(audioIn,1), 0.01, 0.01),
				lagtime		//  lag time
			)
		);
	};
	holdControlSpec = ControlSpec(0.01, 10, \lin, 0, 1, units: " secs");
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Attack /Decay", holdControlSpec, "lag", "lagMin", "lagMax"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

