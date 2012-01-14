// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXEnvDADSR2 : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=450;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;

	var	displayOption;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Env. DADSR";
	moduleRate = "control";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Delay", 1, "modDelay", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain level", 1, "modSustain", 0],
		["Sustain time", 1, "modSustainTime", 0],
		["Release", 1, "modRelease", 0],
		["Time scale", 1, "modTimeMultiply", 0],
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
	autoModOptions = false;
	displayOption = "showEnv";
	arrSynthArgSpecs = [
		["out", 0, 0],
		["gate", 1, 0],
		["note", 0, \ir],
		["velocity", 0, \ir],
		["envtime", 0, \ir],
		["delay", 0, \ir],
		["attack", 0.005, \ir],
		["decay", 0.15, \ir],
		["sustain", 1, \ir],
		["sustainTime", 1, \ir],
		["release", 0.1, \ir],
		["timeMultiply", 1, defLagTime],
		["velocityScaling", 1, \ir],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],
		["modTimeMultiply", 0, \ir],
  	]; 
	arrOptions = [0,0];
	arrOptionData = [
		[	
			["linear", 'linear'],
//invalid		["exponential", 'exponential'],
			["sine", 'sine'],
			["welch", 'welch'],
//invalid		["step", 'step'],
			["slope +10 ", 10],
			["slope +9 ", 9],
			["slope +8 ", 8],
			["slope +7 ", 7],
			["slope +6 ", 6],
			["slope +5 ", 5],
			["slope +4 ", 4],
			["slope +3 ", 3],
			["slope +2 ", 2],
			["slope +1 ", 1],
			["slope -1", -1],
			["slope -2 ", -2],
			["slope -3 ", -3],
			["slope -4 ", -4],
			["slope -5 ", -5],
			["slope -6 ", -6],
			["slope -7 ", -7],
			["slope -8 ", -8],
			["slope -9 ", -9],
			["slope -10 ", -10]
		],
		[	
			["Sustain", 
				{arg del, att, dec, sus, rel, envCurve; 
					Env.dadsr(del, att, dec, sus, rel, 1, envCurve);
				}
			],
			["Fixed Length", 
				{arg del, att, dec, sus, rel, envCurve; 
					Env.new([0, 0, 1, sus, 0], [del, att, dec, rel], envCurve, nil);
				}
			]
		],
	];
	synthDefFunc = { 
		arg out, gate, note, velocity, envtime=0, delay, attack, decay, 
			sustain, sustainTime, release, timeMultiply, velocityScaling, modDelay, 
			modAttack, modDecay, modSustain, modSustainTime, modRelease, modTimeMultiply;
		var outEnv, outFreq, outFunction, outWave, del, att, dec, sus, sustime, rel, timeMult, envCurve, envFunction;
		del = (delay + modDelay).max(0).min(1);
		att = (attack + modAttack).max(0.001).min(1);
		dec = (decay + modDecay).max(0.001).min(1);
		sus = (sustain + modSustain).max(0).min(1);
		sustime = (sustainTime + modSustainTime).max(0.001).min(1);
		rel = (release + modRelease).max(0.001).min(1);
		timeMult = (timeMultiply + modTimeMultiply).max(0.001).min(20);
		envCurve = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		envFunction = this.getSynthOption(1);
		outEnv = EnvGen.kr(
			envFunction.value(del, att * timeMult, dec* timeMult, sus, rel* timeMult, envCurve),
			gate, 
			doneAction: 2
		);
		// amplitude is vel *  0.00315 approx. == 1 / 127
		Out.kr(out, outEnv * ((velocity * 0.007874) + (1-velocityScaling)).min(1));
	};
	this.buildGuiSpecArray;
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	load the synthdef and create the Group for synths to belong to
	this.loadDefAndMakeGroup;
}

buildGuiSpecArray {
	guiSpecArray = [
		["SpacerLine", 6], 
		["ActionButton", "Envelope", {displayOption = "showEnv"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["Spacer", 3], 
		["ActionButton", "MIDI", {displayOption = "showMIDI"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["Spacer", 3], 
		["ActionButton", "Modulation options", {displayOption = "showModOptions"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showEnv", {
		guiSpecArray = guiSpecArray ++[
			["TextBar", "Envelope", 80, 20], 
			["NextLine"], 
			["EZslider", "Pre-Delay", ControlSpec(0, 1), "delay"], 
			["EZslider", "Attack*", ControlSpec(0, 1), "attack"], 
			["EZslider", "Decay*", ControlSpec(0, 1), "decay"], 
			["EZslider", "Sustain level", ControlSpec(0, 1), "sustain"], 
			["EZslider", "Sustain time*", ControlSpec(0, 1), "sustainTime"], 
			["EZslider", "Release*", ControlSpec(0, 1), "release"], 
			["EZslider", "* Time Scale", ControlSpec(0.1, 20, \exp), "timeMultiply"], 
			["SynthOptionPopup", "Curve", arrOptionData, 0, 200, {system.showView;}], 
			["NextLine"], 
			["SynthOptionPopup", "Env. Type", arrOptionData, 1, 200], 
			["Spacer", 4], 
			["ActionButton", "Plot", {this.envPlot;}],
			["SpacerLine", 6], 
			["TXCheckBox", "Scale level to velocity", "velocityScaling"], 
		];
	});
	if (displayOption == "showMIDI", {
		guiSpecArray = guiSpecArray ++[
			["MIDIListenCheckBox"], 
			["NextLine"], 
			["MIDIChannelSelector"], 
			["NextLine"], 
			["MIDINoteSelector"], 
			["NextLine"], 
			["MIDIVelSelector"], 
		];
	});
	if (displayOption == "showModOptions", {
		guiSpecArray = guiSpecArray ++[
			["ModulationOptions"]
		];
	});

}
envPlot {
	var del, att, dec, sus, rel, timeMult, envCurve;
	del = this.getSynthArgSpec("delay");
	att = this.getSynthArgSpec("attack");
	dec = this.getSynthArgSpec("decay");
	sus = this.getSynthArgSpec("sustain");
	rel = this.getSynthArgSpec("release");
	timeMult = this.getSynthArgSpec("timeMultiply");
	envCurve = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
	Env.dadsr(del, att * timeMult, dec* timeMult, sus, rel* timeMult, 1, envCurve).plot;
}

}

