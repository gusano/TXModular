// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMidSideEncoder : TXModuleBase {		 

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
	defaultName = "M-S Encoder";
	moduleRate = "audio";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Audio in L + R", 2, "audioIn"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Left level", 1, "modLeftLvl", 0], 
		["Right level", 1, "modRightLvl", 0] 
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Mid signal", [0]], 
		["Side signal", [1]] 
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["out", nil, 0],
		["audioIn", 0, 0],
		["leftLevel", 1.0, defLagTime],
		["rightLevel", 1.0, defLagTime],
		["modLeftLvl", 0, defLagTime],
		["modRightLvl", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioIn, leftLevel, rightLevel, modLeftLvl, modRightLvl;
		var arrInputs, holdLeft, holdRight, holdMid, holdSide;
		arrInputs = InFeedback.ar(audioIn,2);
		holdLeft = arrInputs[0]  * (leftLevel + modLeftLvl).max(0).min(1);
		holdRight = arrInputs[1]  * (rightLevel + modRightLvl).max(0).min(1);
		holdMid = holdLeft + holdRight;
		holdSide = holdLeft - holdRight;
		Out.ar(out, [holdMid, holdSide]);
	};
	guiSpecArray = [
		["SpacerLine", 4], 
		["EZSlider", "Left level", \unipolar,"leftLevel"], 
		["SpacerLine", 4], 
		["EZSlider", "Right level", \unipolar,"rightLevel"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

