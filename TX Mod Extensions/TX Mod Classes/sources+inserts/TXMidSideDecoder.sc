// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMidSideDecoder : TXModuleBase {		 

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
	defaultName = "M-S Decoder";
	moduleRate = "audio";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Mid signal", 1, "audioInMid"],
		 ["Side signal", 1, "audioInSide"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Mid level", 1, "modMidLvl", 0], 
		["Side level", 1, "modSideLvl", 0] 
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
		["out", nil, 0],
		["audioInMid", 0, 0],
		["audioInSide", 0, 0],
		["midLvl", 1.0, defLagTime],
		["sideLvl", 1.0, defLagTime],
		["modMidLvl", 0, defLagTime],
		["modSideLvl", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioInMid, audioInSide, midLvl, sideLvl, modMidLvl, modSideLvl;
		var holdLeft, holdRight, holdMid, holdSide;
		holdMid = InFeedback.ar(audioInMid,1) * (midLvl + modMidLvl).max(0).min(1);
		holdSide = InFeedback.ar(audioInSide,1) * (sideLvl + modSideLvl).max(0).min(1);
		holdLeft = holdMid + holdSide;
		holdRight = holdMid - holdSide;
		Out.ar(out, [holdLeft, holdRight]);
	};
	guiSpecArray = [
		["SpacerLine", 4], 
		["EZSlider", "Mid level", \unipolar,"midLvl"], 
		["SpacerLine", 4], 
		["EZSlider", "Side level", \unipolar,"sideLvl"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

