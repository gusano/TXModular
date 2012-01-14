// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXTrigImpulse : TXModuleBase {

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
	classvar	timeSpec;

	var	displayOption;
	var	envView;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Trigger Impulse";
	moduleRate = "control";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	timeSpec = ControlSpec(0.001, 20);
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	autoModOptions = false;
	arrSynthArgSpecs = [
		["out", 0, 0],
  	]; 
	synthDefFunc = { 
		arg out;
		var outEnv;
//		outEnv = Line.kr(1, 1, 0.1, doneAction: 2);
		outEnv = EnvGen.kr(Env.adsr(0.01, 0.01, 0, 0.01), 0.01, doneAction: 2);
		Out.kr(out, outEnv);
	};
	guiSpecArray = [
		["MIDIListenCheckBox"], 
		["SpacerLine", 4], 
		["MIDIChannelSelector"], 
		["SpacerLine", 4], 
		["MIDINoteSelector"], 
		["SpacerLine", 4], 
		["MIDIVelSelector"], 
		["SpacerLine", 4], 
		["ActionButton", "Trigger", {this.createSynthNote(60, 100, 0.05);}, 
			200, TXColor.white, TXColor.sysGuiCol2],
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.setMonophonic;	// monophonic by default
	this.midiNoteInit;
	//	load the synthdef and create the Group for synths to belong to
	this.loadDefAndMakeGroup;
}

}

