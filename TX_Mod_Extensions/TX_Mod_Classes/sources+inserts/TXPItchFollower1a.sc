// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXPItchFollower1a : TXModuleBase {		//  

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

	var  holdTrigID, holdOSCResp;
	var	holdCurrPitch1, holdCurrPitch2, holdCurrPitch3, holdCurrPitch4, holdCurrPitch5, holdCurrPitch6;
	var	holdCurrPitchView1, holdCurrPitchView2, holdCurrPitchView3, holdCurrPitchView4, holdCurrPitchView5, holdCurrPitchView6;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "PItch Follower";
	moduleRate = "control";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Audio in", 1, "audioIn"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Follow time", 1, "modLag", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["PItch Out", [0]],
		["Pitch Found", [1]],
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	holdTrigID = UniqueID.next;
	holdCurrPitch1 = 0;
	holdCurrPitch2 = 0;
	holdCurrPitch3 = 0;
	holdCurrPitch4 = 0;
	holdCurrPitch5 = 0;
	holdCurrPitch6 = 0;
	arrSynthArgSpecs = [
		["out", 0, 0],
		["audioIn", 0, 0],
		["tr_sendPitch", 0, 0],
		["i_minFreq", 60.0, \ir],
		["i_maxFreq", 4000.0, \ir],
		["lag", 0.01010101010101, defLagTime], // = ControlSpec(10, 1000).unmap(20)
		["lagMin", 10, defLagTime], 
		["lagMax", 1000, defLagTime],
		["modLag", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioIn, tr_sendPitch, i_minFreq, i_maxFreq, lag, lagMin, lagMax, modLag;
		var input, lagtime, freq, hasFreq, freqOut;
		input = InFeedback.ar(audioIn,1);
		lagtime = lagMin + ((lagMax - lagMin) * (lag + modLag).max(0.001).min(1));
		# freq, hasFreq = 		
			Pitch.kr(input, i_minFreq, i_minFreq, i_maxFreq, ampThreshold: 0.02);
		freqOut = log(freq/i_minFreq) / log(i_maxFreq / i_minFreq);
		SendTrig.kr(tr_sendPitch, holdTrigID, freq); // sends current pitch if requested
		Out.kr(out, [
			Lag.kr(			// lag the input 
				freqOut,
				lagtime/1000		//  lag time - divide to get seconds
			),
			Lag.kr(			// lag and round the input 
				hasFreq,
				lagtime/1000		//  lag  time - divide to get seconds
			).round,
		]);
	};
	guiSpecArray = [
		[	"ActionButton", "Set controls to default values", {
				this.setSynthArgSpec("i_minFreq", 60);
				this.setSynthArgSpec("i_maxFreq", 4000); 
				this.setSynthArgSpec("lag", 0.01010101010101); 
				this.setSynthArgSpec("lagMin", 10); 
				this.setSynthArgSpec("lagMax", 1000);
			}, 
			250
		],
		["NextLine"],
		["TXRangeSlider", "Min-max Freq", ControlSpec(40.0, 5000.0, 'exp'), "i_minFreq", "i_maxFreq", 
			{this.checkMinMaX; this.rebuildSynth}, 
			[
				["Presets: ", {}],
				["Piano: Midi 21 - 108", [21, 108].midicps;],
				["Bass Guitar: Midi 24 - 60", [24, 60].midicps;],
				["Harp: Midi 24 - 103", [24, 103].midicps;],
				["Double Bass: Midi 28 - 67", [28, 67].midicps;],
				["Harpsichord: Midi 29 - 89", [29, 89].midicps;],
				["Bassoon: Midi 34 - 75", [34, 75].midicps;],
				["French Horn: Midi 34 - 77", [34, 77].midicps;],
				["Organ: Midi 36 - 96", [36, 96].midicps;],
				["Cello: Midi 36 - 76", [36, 76].midicps;],
				["Timpani: Midi 40 - 55", [40, 55].midicps;],
				["Guitar: Midi 40 - 76", [40, 76].midicps;],
				["Male Voice: Midi 41 - 72", [41, 72].midicps;],
				["Clarinet: Midi 50 - 94", [50, 94].midicps;],
				["Vibraphone: Midi 53 - 89", [53, 89].midicps;],
				["Female Voice: Midi 52 - 84", [52, 84].midicps;],
				["Violin: Midi 55 - 103", [55, 103].midicps;],
				["Trumpet: Midi 55 - 82", [55, 82].midicps;],
				["Oboe: Midi 58 - 91", [58, 91].midicps;],
				["Flute: Midi 60 - 96", [60, 96].midicps;],
				["Piccolo: Midi 74 - 102", [74, 102].midicps;],
			]
		],
		["NextLine"],
		["TXMinMaxSliderSplit", "Follow time", this.getLagControlSpec, "lag", "lagMin", "lagMax"], 
		["DividingLine"],
		["NextLine"],
		["TXStaticText", "Current Pitch", {holdCurrPitch6}, {arg view; holdCurrPitchView6 = view.textView}, 200], 
		["ActionButton", "Get Current Pitch", 
			{this.moduleNode.set("tr_sendPitch", 1); {this.moduleNode.set("tr_sendPitch", 0);}.defer(0.2)}, 
			150],
		["NextLine"],
		["TXStaticText", "Old Pitch 1", {holdCurrPitch5}, {arg view; holdCurrPitchView5 = view.textView}, 200], 
		["ActionButton", "Reset", {this.resetPitchDisplay}, 150, TXColor.white, TXColor.sysDeleteCol],
		["NextLine"],
		["TXStaticText", "Old Pitch 2", {holdCurrPitch4}, {arg view; holdCurrPitchView4 = view.textView}, 200], 
		["NextLine"],
		["TXStaticText", "Old Pitch 3", {holdCurrPitch3}, {arg view; holdCurrPitchView3 = view.textView}, 200], 
		["NextLine"],
		["TXStaticText", "Old Pitch 4", {holdCurrPitch2}, {arg view; holdCurrPitchView2 = view.textView}, 200], 
		["NextLine"],
		["TXStaticText", "Old Pitch 5", {holdCurrPitch1}, {arg view; holdCurrPitchView1 = view.textView}, 200], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
	this.oscActivate;
}

getLagControlSpec {
	^ ControlSpec(1, 10000);
 }
getFreqControlSpec {
	^ [this.getSynthArgSpec("i_minFreq"), this.getSynthArgSpec("i_maxFreq"), 'exp'].asSpec;
 }
 
oscActivate {
	//	remove any previous OSCresponderNodes and add new
	this.oscDeactivate;
	holdOSCResp = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == holdTrigID, {this.setCurrPitch(msg[3])} );
	}).add;
}

oscDeactivate { 
	//	remove responder
	if (holdOSCResp.class == OSCresponderNode, {holdOSCResp.remove});
}

deleteModuleExtraActions {     
	//	remove responders
	this.oscDeactivate;
}

setCurrPitch {arg argPitch;
	holdCurrPitch1 = holdCurrPitch2;
	holdCurrPitch2 = holdCurrPitch3;
	holdCurrPitch3 = holdCurrPitch4;
	holdCurrPitch4 = holdCurrPitch5;
	holdCurrPitch5 = holdCurrPitch6;
	holdCurrPitch6 = argPitch.round(0.1);
	{holdCurrPitchView1.string = holdCurrPitch1.asString;}.defer;
	{holdCurrPitchView2.string = holdCurrPitch2.asString;}.defer;
	{holdCurrPitchView3.string = holdCurrPitch3.asString;}.defer;
	{holdCurrPitchView4.string = holdCurrPitch4.asString;}.defer;
	{holdCurrPitchView5.string = holdCurrPitch5.asString;}.defer;
	{holdCurrPitchView6.string = holdCurrPitch6.asString;}.defer;
}
 
resetPitchDisplay {arg argPitch;
	holdCurrPitch1 = 0;
	holdCurrPitch2 = 0;
	holdCurrPitch3 = 0;
	holdCurrPitch4 = 0;
	holdCurrPitch5 = 0;
	holdCurrPitch6 = 0;
	{holdCurrPitchView1.string = 0.asString;}.defer;
	{holdCurrPitchView2.string = 0.asString;}.defer;
	{holdCurrPitchView3.string = 0.asString;}.defer;
	{holdCurrPitchView4.string = 0.asString;}.defer;
	{holdCurrPitchView5.string = 0.asString;}.defer;
	{holdCurrPitchView6.string = 0.asString;}.defer;
}
 
checkMinMaX {     
	var holdMin, holdMax;
	holdMin = this.getSynthArgSpec("i_minFreq");
	holdMax = this.getSynthArgSpec("i_maxFreq");
	if (holdMin > holdMax, {
		this.setSynthArgSpec("i_minFreq", holdMax);
		this.setSynthArgSpec("i_maxFreq", holdMin);
		system.flagGuiUpd;
	});
}

		  
			

}

