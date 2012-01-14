// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXAudioIn4 : TXModuleBase {		// Audio In module 

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
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Audio Inputs";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Left level", 1, "modLeftLvl", 0], 
		["Right level", 1, "modRightLvl", 0] 
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	guiHeight=150;
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["out", nil, 0],
		["leftLevel", 1.0, defLagTime],
		["rightLevel", 1.0, defLagTime],
		["modLeftLvl", 0, defLagTime],
		["modRightLvl", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [
		[	["Audio Inputs: 1 & 2", {AudioIn.ar([1, 2]); }],
			["Audio Inputs: 3 & 4", {AudioIn.ar([3, 4]); }],
			["Audio Inputs: 5 & 6", {AudioIn.ar([5, 6]); }],
			["Audio Inputs: 7 & 8", {AudioIn.ar([7, 8]); }],
		],
	];
	synthDefFunc = { arg out, leftLevel, rightLevel, modLeftLvl, modRightLvl;
		var inputArr;
		inputArr = arrOptionData.at(0).at(arrOptions.at(0)).at(1).value;
		Out.ar(out, inputArr.at(0) * (leftLevel + modLeftLvl).max(0).min(1) );
		Out.ar(out+1, inputArr.at(1) * (rightLevel + modRightLvl).max(0).min(1) );
	};
	guiSpecArray = [
		["SynthOptionPopup", "Inputs", arrOptionData, 0], 
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

