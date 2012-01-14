// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXPItchFollower : TXModuleBase {		//  

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
	arrSynthArgSpecs = [
		["out", 0, 0],
		["audioIn", 0, 0],
		["i_minFreq", 60.0, \ir],
		["i_maxFreq", 4000.0, \ir],
		["lag", 0.01010101010101, defLagTime], // = ControlSpec(10, 1000).unmap(20)
		["lagMin", 10, defLagTime], 
		["lagMax", 1000, defLagTime],
		["modLag", 0, defLagTime],
	]; 
	synthDefFunc = { arg out, audioIn, i_minFreq, i_maxFreq, lag, lagMin, lagMax, modLag;
		var input, lagtime, freq, hasFreq, freqOut;
		input = InFeedback.ar(audioIn,1);
		lagtime = lagMin + ((lagMax - lagMin) * (lag + modLag).max(0.001).min(1));
		# freq, hasFreq = 		
			Pitch.kr(input, i_minFreq, i_minFreq, i_maxFreq, ampThreshold: 0.02);
		freqOut = log(freq/i_minFreq) / log(i_maxFreq / i_minFreq);
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
		["TXRangeSlider", "Min-max Freq", ControlSpec(40.0, 5000.0, 'exp'), "i_minFreq", "i_maxFreq", {this.rebuildSynth}, [
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
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

getLagControlSpec {
	^ ControlSpec(1, 10000);
 }
getFreqControlSpec {
	^ [this.getSynthArgSpec("i_minFreq"), this.getSynthArgSpec("i_maxFreq"), 'exp'].asSpec;
 }}

