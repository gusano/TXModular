// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSampleHold : TXModuleBase {		 

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
	defaultName = "Sample & Hold";
	moduleRate = "control";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Trigger", 1, "extTrigger", nil]
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
		["t_trig", 0, 0],
		["extTrigger", 0, 0],
	]; 
	arrOptions = [0];
	arrOptionData = [
		[
			["Sample & Hold", 
				{arg inSignal, inTrigger; Latch.kr(inSignal, inTrigger); }
			],
			["Gate & Hold", 
				{arg inSignal, inTrigger; Gate.kr(inSignal, inTrigger); }
			],
		]
	];
	synthDefFunc = { arg in, out, t_trig, extTrigger;
		var inSignal, outFunction, outSignal;
		inSignal = TXClean.kr(In.kr(in,1));
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outSignal = outFunction.value(inSignal, t_trig + extTrigger);
		Out.kr(out, TXClean.kr(outSignal));
	};
	holdControlSpec = ControlSpec(0.0001, 30, \exp, 0, 1, units: " secs");
	guiSpecArray = [
		["SynthOptionPopup", "Type", arrOptionData, 0], 
		["SpacerLine", 2], 
		["ActionButton", "Trigger", {this.moduleNode.set("t_trig", 1);}, 
			200, TXColor.white, TXColor.sysGuiCol2] 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

