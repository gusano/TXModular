// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAudioTrigger2 : TXModuleBase {		// Audio In module 

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
	classvar	<guiLeft=150;
	classvar	<guiTop=300;

	var	<>noteOutModule1ID;
	var	<>noteOutModule2ID;
	var	<>noteOutModule3ID;
	var	<>noteOutModule4ID;
	var	<>noteOutModule5ID;
	var	<>noteOutModule6ID;
	var	<>noteOutModule1;
	var	<>noteOutModule2;
	var	<>noteOutModule3;
	var	<>noteOutModule4;
	var	<>noteOutModule5;
	var	<>noteOutModule6;
	var	oscResponder;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Audio Trigger";
	moduleRate = "control";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Audio in", 1, "audioIn"]
	];	
	arrCtlSCInBusSpecs = [ 
		["Threshold", 1, "modThreshold", 0]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Level Out", [0]]
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	var holdControlSpec;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["out", 0, 0],
		["audioIn", 0, 0],
		["threshold", 0.05, defLagTime],
		["trigHoldTime", 0.1, defLagTime],
		["modThreshold", 0, defLagTime],
		["i_noteOutModuleID1", nil, 0],
		["i_noteOutModuleID2", nil, 0],
		["i_noteOutModuleID3", nil, 0],
		["i_noteOutModuleID4", nil, 0],
		["i_noteOutModuleID5", nil, 0],
		["i_noteOutModuleID6", nil, 0],
		["i_gateTime", 0.1, 0],
		["i_note", 60, 0],
		["i_level", 50, 0],
	]; 
		
	synthDefFunc = { 
		arg out, audioIn, threshold, trigHoldTime, modThreshold, i_noteOutModuleID1, i_noteOutModuleID2, 
			i_noteOutModuleID3, i_noteOutModuleID4, i_noteOutModuleID5, i_noteOutModuleID6, i_gateTime, i_note, i_level;
		var threshlevel, holdTrig, holdAmp;
		threshlevel = (threshold + modThreshold).max(0.001).min(1);
		holdAmp = Amplitude.kr(InFeedback.ar(audioIn,1), 0.01, 0.01);
		holdTrig = Trig.ar((holdAmp - threshlevel).max(0), trigHoldTime);
		SendTrig.ar(holdTrig, 0, 1);
		Out.kr(out, holdAmp);
	};
	holdControlSpec = ControlSpec(0.01, 10, \lin, 0, 1, units: " secs");
	guiSpecArray = [
		["TextBarLeft", "Modules to be triggered:"],
		["SeqSelect3GroupModules", "noteOutModule1", "noteOutModule2", nil, 
			"i_noteOutModuleID1", "i_noteOutModuleID2", nil], 
		["NextLine"], 
		["SeqSelect3GroupModules", "noteOutModule3", "noteOutModule4", nil, 
			"i_noteOutModuleID3", "i_noteOutModuleID4", nil], 
		["NextLine"], 
		["SeqSelect3GroupModules", "noteOutModule5", "noteOutModule6", nil, 
			"i_noteOutModuleID5", "i_noteOutModuleID6", nil], 
		["DividingLine"], 
		["EZslider", "Trig Threshold", ControlSpec(0.001, 1), "threshold"], 
		["EZslider", "Reload time", ControlSpec(0.01, 1), "trigHoldTime"], 
		["DividingLine"], 
		["MIDINote", "Trig note", "i_note"],
		["NextLine"], 
		["EZslider", "Gate time", ControlSpec(0.05, 1), "i_gateTime"], 
		["EZslider", "Output Level", ControlSpec(1, 100), "i_level"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
	this.oscControlActivate;
}

*restoreAllOutputs {
	 arrInstances.do({ arg item, i;
	 	item.restoreOutputs;
	 });
} 

restoreOutputs {
 	var holdID;
 	holdID = this.getSynthArgSpec("i_noteOutModuleID1");
	if (holdID.notNil, {noteOutModule1 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("i_noteOutModuleID2");
	if (holdID.notNil, {noteOutModule2 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("i_noteOutModuleID3");
	if (holdID.notNil, {noteOutModule3 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("i_noteOutModuleID4");
	if (holdID.notNil, {noteOutModule4 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("i_noteOutModuleID5");
	if (holdID.notNil, {noteOutModule5 = system.getModuleFromID(holdID)});
 	holdID = this.getSynthArgSpec("i_noteOutModuleID6");
	if (holdID.notNil, {noteOutModule6 = system.getModuleFromID(holdID)});
} 

checkDeletions {	
	// check if any note out modules are going to be deleted - if so remove them as outputs
		if (noteOutModule1.notNil, {
			if (noteOutModule1.deletedStatus == true, {
				noteOutModule1 = nil;
				this.setSynthArgSpec("i_noteOutModuleID1", nil);
			});
		});
		if (noteOutModule2.notNil, {
			if (noteOutModule2.deletedStatus == true, {
				noteOutModule2 = nil;
				this.setSynthArgSpec("i_noteOutModuleID2", nil);
			});
		});
		if (noteOutModule3.notNil, {
			if (noteOutModule3.deletedStatus == true, {
				noteOutModule3 = nil;
				this.setSynthArgSpec("i_noteOutModuleID3", nil);
			});
		});
		if (noteOutModule4.notNil, {
			if (noteOutModule4.deletedStatus == true, {
				noteOutModule4 = nil;
				this.setSynthArgSpec("i_noteOutModuleID4", nil);
			});
		});
		if (noteOutModule5.notNil, {
			if (noteOutModule5.deletedStatus == true, {
				noteOutModule5 = nil;
				this.setSynthArgSpec("i_noteOutModuleID5", nil);
			});
		});
		if (noteOutModule6.notNil, {
			if (noteOutModule6.deletedStatus == true, {
				noteOutModule6 = nil;
				this.setSynthArgSpec("i_noteOutModuleID6", nil);
			});
		});
}

loadExtraData {arg argData;  // override default method
	this.oscControlActivate;
}

oscControlActivate { 
	//	stop any previous responder 
	this.oscControlDeactivate;
	oscResponder = OSCresponderNode(nil, '/tr', { arg time, responder, msg;
		var holdNodeID, outNote, outVel, outEnvTime;

//		For testing  - post details
//		"TXAudioTrigger : ".postln;
//		[time, responder, msg].postln;
	
		holdNodeID = msg.at(1);
		outNote = this.getSynthArgSpec("i_note");
		outVel = (this.getSynthArgSpec("i_level") / 100) * 127;
		outEnvTime = this.getSynthArgSpec("i_gateTime");
		
		if (holdNodeID == moduleNode.nodeID, {
				if (noteOutModule1.notNil, {noteOutModule1.createSynthNote(outNote, outVel, outEnvTime, 0)});
				if (noteOutModule2.notNil, {noteOutModule2.createSynthNote(outNote, outVel, outEnvTime, 0)});
				if (noteOutModule3.notNil, {noteOutModule3.createSynthNote(outNote, outVel, outEnvTime, 0)});
				if (noteOutModule4.notNil, {noteOutModule4.createSynthNote(outNote, outVel, outEnvTime, 0)});
				if (noteOutModule5.notNil, {noteOutModule5.createSynthNote(outNote, outVel, outEnvTime, 0)});
				if (noteOutModule6.notNil, {noteOutModule6.createSynthNote(outNote, outVel, outEnvTime, 0)});
		});
	}).add;
}

oscControlDeactivate { 
	if (oscResponder.notNil, {
		oscResponder.remove;
	});
}

}

