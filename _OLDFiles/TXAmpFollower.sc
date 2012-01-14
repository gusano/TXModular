// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAmpFollower : TXModuleBase {		// Audio In module 

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
		["attack", 0, "modAttack", 0],
		["release", 0, "modRelease", 0],
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
		["audioIn", 0, 0],
		["attack", 0.01, defLagTime],
		["release", 0.01, defLagTime],
		["modAttack", 0, defLagTime],
		["modRelease", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioIn, attack, release, modAttack, modRelease;
		var att, rel;
		att = (attack + modAttack).max(0.001).min(1);
		rel = (release + modRelease).max(0.001).min(1);
		Out.kr(out, Amplitude.kr(InFeedback.ar(audioIn,1), att, rel));
	};
	guiSpecArray = [
		["EZSlider", "Attack", ControlSpec(0.001,1), "attack"], 
		["EZSlider", "Release", ControlSpec(0.001,1), "release"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

