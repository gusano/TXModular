// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSmooth2 : TXModuleBase {		// Lag module 

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
	defaultName = "Smooth";
	moduleRate = "control";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Time 1", 1, "modLag", 0],
		["Time 2", 1, "modLag2", 0]
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
		["lag2", 0.5, defLagTime],
		["lag2Min", 0.01, defLagTime], 
		["lag2Max", 1, defLagTime],
		["modLag", 0, defLagTime],
		["modLag2", 0, defLagTime],
	]; 
	arrOptions = [1];
	arrOptionData = [
		[
			["Linear - use time 1 for up and down smoothing", 
				{arg input, lagtime; Ramp.kr(input, lagtime); }
			],
			["Exp 1 - use time 1 for up and down smoothing", 
				{arg input, lagtime; Lag.kr(input, lagtime); }
			],
			["Exp 2 - use time 1 for up and down smoothing", 
				{arg input, lagtime; Lag2.kr(input, lagtime); }
			],
			["Exp 3 - use time 1 for up and down smoothing", 
				{arg input, lagtime; Lag3.kr(input, lagtime); }
			],
			["Exp 1 - use time 1 for up, time 2 for down smoothing", 
				{arg input, lagtime, lagtime2; LagUD.kr(input, lagtime, lagtime2); }
			],
			["Exp 2 - use time 1 for up, time 2 for down smoothing", 
				{arg input, lagtime, lagtime2; Lag2UD.kr(input, lagtime, lagtime2); }
			],
			["Exp 3 - use time 1 for up, time 2 for down smoothing", 
				{arg input, lagtime, lagtime2; Lag3UD.kr(input, lagtime, lagtime2); }
			],
		]
	];
	synthDefFunc = { arg in, out, lag, lagMin, lagMax, lag2, lag2Min, lag2Max, modLag, modLag2;
		var inSignal, lagtime, lagtime2, outFunction, outSignal;
		inSignal = TXClean.kr(In.kr(in,1));
		lagtime = ( (lagMax/lagMin) ** ((lag + modLag).max(0.0001).min(1)) ) * lagMin;
		lagtime2 = ( (lag2Max/lag2Min) ** ((lag2 + modLag2).max(0.0001).min(1)) ) * lag2Min;
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outSignal = outFunction.value(inSignal, lagtime, lagtime2);
		Out.kr(out, TXClean.kr(outSignal));
	};
	holdControlSpec = ControlSpec(0.0001, 30, \exp, 0, 1, units: " secs");
	guiSpecArray = [
		["SynthOptionPopupPlusMinus", "Smoothing", arrOptionData, 0], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Time 1", holdControlSpec, "lag", "lagMin", "lagMax"], 
		["SpacerLine", 4], 
		["TXMinMaxSliderSplit", "Time 2", holdControlSpec, "lag2", "lag2Min", "lag2Max"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

