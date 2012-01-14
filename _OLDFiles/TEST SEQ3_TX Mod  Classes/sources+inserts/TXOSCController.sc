// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXOSCController : TXModuleBase {

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
		["SpacerLine", 4], 
		["TXStaticText", "Please note:", 
			"The Network Port for receiving OSC messages is  " ++ NetAddr.langPort.asString],
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

