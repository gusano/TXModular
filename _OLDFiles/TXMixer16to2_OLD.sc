// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMixer16to2_OLD : TXModuleBase {

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
	defaultName = "Mixer 16-2";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Pan 1+2", 1, "modPan1", 0],
		["Pan 3+4", 1, "modPan2", 0],
		["Pan 5+6", 1, "modPan3", 0],
		["Pan 7+8", 1, "modPan4", 0],
		["Pan 9+10", 1, "modPan5", 0],
		["Pan 11+12", 1, "modPan6", 0],
		["Pan 13+14", 1, "modPan7", 0],
		["Pan 15+16", 1, "modPan8", 0],
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
		["pan1", 0.5, defLagTime],
		["pan2", 0.5, defLagTime],
		["pan3", 0.5, defLagTime],
		["pan4", 0.5, defLagTime],
		["pan5", 0.5, defLagTime],
		["pan6", 0.5, defLagTime],
		["pan7", 0.5, defLagTime],
		["pan8", 0.5, defLagTime],
		["modLevel1", 0, defLagTime],
		["modLevel2", 0, defLagTime],
		["modLevel3", 0, defLagTime],
		["modLevel4", 0, defLagTime],
		["modLevel5", 0, defLagTime],
		["modLevel6", 0, defLagTime],
		["modLevel7", 0, defLagTime],
		["modLevel8", 0, defLagTime],
		["modOutLevel", 0, defLagTime],
		["modPan1", 0, defLagTime],
		["modPan2", 0, defLagTime],
		["modPan3", 0, defLagTime],
		["modPan4", 0, defLagTime],
		["modPan5", 0, defLagTime],
		["modPan6", 0, defLagTime],
		["modPan7", 0, defLagTime],
		["modPan8", 0, defLagTime],
	]; 
	synthDefFunc = { arg input1, input2, input3, input4, input5, input6, input7, input8, 
			out, level1, level2, level3, level4, level5, level6, level7, level8, outLevel, 
			pan1, pan2, pan3, pan4, pan5, pan6, pan7, pan8, 
			modLevel1, modLevel2, modLevel3, modLevel4, modLevel5, modLevel6, modLevel7, modLevel8, 
			modOutLevel, modPan1, modPan2, modPan3, modPan4, modPan5, modPan6, modPan7, modPan8;
		var holdInput1, holdInput2, holdInput3, holdInput4, holdInput5, holdInput6, holdInput7, 
			holdInput8, holdOutLevel,
			holdPan1, holdPan2, holdPan3, holdPan4, holdPan5, holdPan6, holdPan7, holdPan8;
		holdInput1 = InFeedback.ar(input1, 2) * (level1 + modLevel1).max(0).min(1);
		holdInput2 = InFeedback.ar(input2, 2) * (level2 + modLevel2).max(0).min(1);
		holdInput3 = InFeedback.ar(input3, 2) * (level3 + modLevel3).max(0).min(1);
		holdInput4 = InFeedback.ar(input4, 2) * (level4 + modLevel4).max(0).min(1);
		holdInput5 = InFeedback.ar(input5, 2) * (level5 + modLevel5).max(0).min(1);
		holdInput6 = InFeedback.ar(input6, 2) * (level6 + modLevel6).max(0).min(1);
		holdInput7 = InFeedback.ar(input7, 2) * (level7 + modLevel7).max(0).min(1);
		holdInput8 = InFeedback.ar(input8, 2) * (level8 + modLevel8).max(0).min(1);
		holdOutLevel = (outLevel + modOutLevel).max(0).min(1);
		holdPan1 = (pan1 + modPan1).max(0).min(1);
		holdPan2 = (pan2 + modPan2).max(0).min(1);
		holdPan3 = (pan3 + modPan3).max(0).min(1);
		holdPan4 = (pan4 + modPan4).max(0).min(1);
		holdPan5 = (pan5 + modPan5).max(0).min(1);
		holdPan6 = (pan6 + modPan6).max(0).min(1);
		holdPan7 = (pan7 + modPan7).max(0).min(1);
		holdPan8 = (pan8 + modPan8).max(0).min(1);
		Out.ar(out, holdOutLevel * Mix.ar([
			[holdInput1[0] * (1-holdPan1), holdInput1[1] * holdPan1],
			[holdInput2[0] * (1-holdPan2), holdInput2[1] * holdPan2],
			[holdInput3[0] * (1-holdPan3), holdInput3[1] * holdPan3],
			[holdInput4[0] * (1-holdPan4), holdInput4[1] * holdPan4],
			[holdInput5[0] * (1-holdPan5), holdInput5[1] * holdPan5],
			[holdInput6[0] * (1-holdPan6), holdInput6[1] * holdPan6],
			[holdInput7[0] * (1-holdPan7), holdInput7[1] * holdPan7],
			[holdInput8[0] * (1-holdPan8), holdInput8[1] * holdPan8],
		]));
	};
	guiSpecArray = [
		["MixerPan", "Pan 1+2", ControlSpec(0, 1), "pan1"],
		["MixerPan", "Pan 3+4", ControlSpec(0, 1), "pan2"],
		["MixerPan", "Pan 5+6", ControlSpec(0, 1), "pan3"],
		["MixerPan", "Pan 7+8", ControlSpec(0, 1), "pan4"],
		["MixerPan", "Pan 9+10", ControlSpec(0, 1), "pan5"],
		["MixerPan", "Pan 11+12", ControlSpec(0, 1), "pan6"],
		["MixerPan", "Pan 13+14", ControlSpec(0, 1), "pan7"],
		["MixerPan", "Pan 15+16", ControlSpec(0, 1), "pan8"],
		["SpacerLine", 20], 
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

