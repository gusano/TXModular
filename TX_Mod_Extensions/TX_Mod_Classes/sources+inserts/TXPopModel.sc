// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXPopModel : TXModuleBase {

/*	NEEDS TO BE WRITTEN

NOTES:

control outputs could based on populations of all 3 groups (normalised), with trigger outputs for arrivals/departures of any of the 3 groups. 
a clock speed would set the rate of interaction. all events occur over time, with other timed-based changes such as fish growing bigger, birth and death rates.
random events such as illnesses in fisherman (with spread rates and recovery times), or the birth of twins in fish, should be included to add fluctuations to populations.


BIRTH AND DEATH RATE EQUATIONS

-From http://en.wikipedia.org/wiki/Population_dynamics:
If N1 is the number of individuals at time 1 then
	N1 = N0 + B - D + I - E
where N0 is the number of individuals at time 0, B is the number of individuals born, D the number that died, I the number that immigrated, and E the number that emigrated between time 0 and time 1.

-From: http://en.wikipedia.org/wiki/Nurgaliev%27s_law:
In population dynamics, Nurgaliev's equation says 
	dn/ dt = an**2 - bn
where 'n' is the size of a population, a is a half of the average probability of a birth of a male (the same for females) of a potential arbitrary parents pair within a year, b is an average probability of a death of a person within a year.
The first term is twice proportional to the half of population (number of males and number of females). The second term is responsible for death rate and has clear and precise sense—in-average-constant distribution of death rate on age scale (babies are at risk at birth, the middle aged are at risk of trauma, old men become ill). It is known to demographers, for example, that the probability of death within the first year of a life is precisely equal to similar probability for the 55th year of a life. Thus, in the given model the average person dies under the same law as an unstable atomic nucleus decays.





*/







//    OLD CODE BELOW FROM OSC Controller: --------------------------------------


	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=200;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	
	var	oscControlRoutine;
	var	<>oscString;
	var	oscResponder;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "OSC Controller";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [];	
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
		["OSCString", "/example/text", 0],
	]; 
	synthDefFunc = { arg out, controller=0;
		var mixOut=0;
		Out.ar(out, mixOut);
	};
	guiSpecArray = [
		["OSCString"], 
		["DividingLine"], 
		["TXStaticText", "Please note:", "OSC messages must have Port set to 57120"],
	];
	arrActionSpecs = this.buildActionSpecs([
		["OSCString"], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.oscControlActivate;
}

runAction {this.oscControlActivate}   //	override base class

pauseAction {this.oscControlDeactivate}   //	override base class

extraSaveData {	
	^oscString;
}
loadExtraData {arg argData;  // override default method
	oscString = argData; 
	this.oscControlActivate;
}

oscControlActivate { 
	//	stop any previous responder 
	this.oscControlDeactivate;
	oscResponder = OSCresponderNode(nil, oscString.asSymbol, { arg time, responder, msg;

//	For testing  - post details
//	"TXOSCController : ".postln;
//	[time, responder, msg].postln;

		// set the Bus value
	 	if ( (outBus.class == Bus) and: (msg.at(1).isNumber), {
	 		outBus.value_(msg.at(1).max(-1).min(1)); 
	 	});
	}).add;
}

oscControlDeactivate { 
	if (oscResponder.notNil, {
		oscResponder.remove;
	});
 }

deleteModuleExtraActions {     
	this.oscControlDeactivate;
}

}

