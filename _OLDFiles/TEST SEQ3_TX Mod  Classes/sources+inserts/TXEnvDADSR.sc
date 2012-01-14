// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXEnvDADSR : TXModuleBase {

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
		["Curve", 1, "modCurve", 0],
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
		["curve", 0, \ir],
		["timeMultiply", 1, defLagTime],
		["velocityScaling", 1, \ir],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],
		["modCurve", 0, \ir],
		["modTimeMultiply", 0, \ir],
  	]; 
	synthDefFunc = { 
		arg out, gate, note, velocity, envtime=0, delay, attack, decay, 
			sustain, sustainTime, release, curve, timeMultiply, velocityScaling, modDelay, 
			modAttack, modDecay, modSustain, modSustainTime, modRelease, modCurve, modTimeMultiply;
		var outEnv, outFreq, outFunction, outWave, curveAdjusted, del, att, dec, sus, sustime, rel, timeMult;
		del = (delay + modDelay).max(0).min(1);
		att = (attack + modAttack).max(0.001).min(1);
		dec = (decay + modDecay).max(0.001).min(1);
		sus = (sustain + modSustain).max(0).min(1);
		sustime = (sustainTime + modSustainTime).max(0.001).min(1);
		rel = (release + modRelease).max(0.001).min(1);
		curveAdjusted = ((curve) + (modCurve * 10)).max(-10).min(10);
		timeMult = (timeMultiply + modTimeMultiply).max(0.001).min(20);

//		if (envtime == 0, {
//			outEnv = EnvGen.kr(
//				Env.dadsr(del, att * timeMult, dec* timeMult, sus, rel* timeMult, 1, curveAdjusted), 
//				gate, 
//				doneAction: 2
//			);
//		},{
//			outEnv = EnvGen.kr(
//				Env.new(
//					// levels
//					[ 0, 0, 1, sus, sus, 0 ],
//					// times
//					[ del, att * timeMult, dec * timeMult, sustime * timeMult,  rel * timeMult]
//						* envtime, 
//					// curve
//					curveAdjusted 		
//				),
//				gate, 
//				doneAction: 2
//			); 
//		});

		outEnv = EnvGen.kr(
			Env.dadsr(del, att * timeMult, dec* timeMult, sus, rel* timeMult, 1, curveAdjusted), 
			gate, 
			doneAction: 2
		);

		// amplitude is vel *  0.00315 approx. == 1 / 127
		Out.kr(out, outEnv * ((velocity * 0.007874) + (1-velocityScaling)).min(1));
	};
	guiSpecArray = [
		["DividingLine"], 
		["EZslider", "Pre-Delay", ControlSpec(0, 1), "delay"], 
		["EZslider", "Attack*", ControlSpec(0, 1), "attack"], 
		["EZslider", "Decay*", ControlSpec(0, 1), "decay"], 
		["EZslider", "Sustain level", ControlSpec(0, 1), "sustain"], 
		["EZslider", "Sustain time*", ControlSpec(0, 1), "sustainTime"], 
		["EZslider", "Release*", ControlSpec(0, 1), "release"], 
		["EZsliderUnmapped", "* Curve", ControlSpec(-10, 10, step: 1), "curve"], 
		["EZslider", "* Time Scale", ControlSpec(0.001, 20), "timeMultiply"], 
		["DividingLine"], 
		["MIDIListenCheckBox"], 
		["NextLine"], 
		["MIDIChannelSelector"], 
		["NextLine"], 
		["MIDINoteSelector"], 
		["NextLine"], 
		["MIDIVelSelector"], 
		["DividingLine"], 
		["TXCheckBox", "Scale level to velocity", "velocityScaling"], 
		["DividingLine"], 
	];

	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	load the synthdef and create the Group for synths to belong to
	this.loadDefAndMakeGroup;
}

}

