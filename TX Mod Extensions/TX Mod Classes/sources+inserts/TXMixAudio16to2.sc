// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMixAudio16to2 : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiWidth=642;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Mix Audio 16-2";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Level 1+2", 1, "modLevel1", 0],
		["Level 3+4", 1, "modLevel2", 0],
		["Level 5+6", 1, "modLevel3", 0],
		["Level 7+8", 1, "modLevel4", 0],
		["Level 9+10", 1, "modLevel5", 0],
		["Level 11+12", 1, "modLevel6", 0],
		["Level 13+14", 1, "modLevel7", 0],
		["Level 15+16", 1, "modLevel8", 0],
		["Output Level", 1, "modOutLevel", 0],
	];	
	arrAudSCInBusSpecs = [ 
		 ["Inputs 1+2", 2, "input1"],
		 ["Inputs 3+4", 2, "input2"],
		 ["Inputs 5+6", 2, "input3"],
		 ["Inputs 7+8", 2, "input4"],
		 ["Inputs 9+10", 2, "input5"],
		 ["Inputs 11+12", 2, "input6"],
		 ["Inputs 13+14", 2, "input7"],
		 ["Inputs 15+16", 2, "input8"],
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
		["input1", 0, 0],
		["input2", 0, 0],
		["input3", 0, 0],
		["input4", 0, 0],
		["input5", 0, 0],
		["input6", 0, 0],
		["input7", 0, 0],
		["input8", 0, 0],
		["out", 0, 0],
		["level1", 0.5, defLagTime],
		["level2", 0.5, defLagTime],
		["level3", 0.5, defLagTime],
		["level4", 0.5, defLagTime],
		["level5", 0.5, defLagTime],
		["level6", 0.5, defLagTime],
		["level7", 0.5, defLagTime],
		["level8", 0.5, defLagTime],
		["outLevel", 0.5, defLagTime],
		["modLevel1", 0, defLagTime],
		["modLevel2", 0, defLagTime],
		["modLevel3", 0, defLagTime],
		["modLevel4", 0, defLagTime],
		["modLevel5", 0, defLagTime],
		["modLevel6", 0, defLagTime],
		["modLevel7", 0, defLagTime],
		["modLevel8", 0, defLagTime],
		["modOutLevel", 0, defLagTime],
	]; 
	synthDefFunc = { arg input1, input2, input3, input4, input5, input6, input7, input8, 
			out, level1, level2, level3, level4, level5, level6, level7, level8, outLevel, 
			modLevel1, modLevel2, modLevel3, modLevel4, modLevel5, modLevel6, modLevel7,
			modLevel8, modOutLevel;
		var holdInput1, holdInput2, holdInput3, holdInput4, holdInput5, holdInput6, holdInput7, 
			holdInput8, holdOutLevel;
		holdInput1 = InFeedback.ar(input1, 2) * (level1 + modLevel1).max(0).min(1);
		holdInput2 = InFeedback.ar(input2, 2) * (level2 + modLevel2).max(0).min(1);
		holdInput3 = InFeedback.ar(input3, 2) * (level3 + modLevel3).max(0).min(1);
		holdInput4 = InFeedback.ar(input4, 2) * (level4 + modLevel4).max(0).min(1);
		holdInput5 = InFeedback.ar(input5, 2) * (level5 + modLevel5).max(0).min(1);
		holdInput6 = InFeedback.ar(input6, 2) * (level6 + modLevel6).max(0).min(1);
		holdInput7 = InFeedback.ar(input7, 2) * (level7 + modLevel7).max(0).min(1);
		holdInput8 = InFeedback.ar(input8, 2) * (level8 + modLevel8).max(0).min(1);
		holdOutLevel = (outLevel + modOutLevel).max(0).min(1);
		Out.ar(out, holdOutLevel * Mix.new([holdInput1, holdInput2, holdInput3, holdInput4, 
			holdInput5, holdInput6, holdInput7, holdInput8]));
	};
	guiSpecArray = [
		["MixerLevel", "Level 1+2", ControlSpec(0, 1), "level1"],
		["MixerLevel", "Level 3+4", ControlSpec(0, 1), "level2"],
		["MixerLevel", "Level 5+6", ControlSpec(0, 1), "level3"],
		["MixerLevel", "Level 7+8", ControlSpec(0, 1), "level4"],
		["MixerLevel", "Level 9+10", ControlSpec(0, 1), "level5"],
		["MixerLevel", "Level 11+12", ControlSpec(0, 1), "level6"],
		["MixerLevel", "Level 13+14", ControlSpec(0, 1), "level7"],
		["MixerLevel", "Level 15+16", ControlSpec(0, 1), "level8"],
		["SpacerLine", 20], 
		["EZslider", "Output Level", ControlSpec(0, 1), "outLevel"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

