// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWiiController : TXModuleBase {

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
	classvar	arrOscStrings;
	classvar	arrOscArgOffsets;
	classvar	arrWiiNames;
//	classvar testTime = 0, testMin = 10000, testMax = -10000; // used for testing o/p ranges
	
	var	oscControlRoutine;
	var	<>oscString;
	var	oscResponder;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Wii Ctrl Darwiin";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrWiiNames = [
		" Select..." ,
		" Wii Accleration" ,
		" Wii Roll left-right" ,
		" Wii Pitch front-back" ,
		" Wii irdata 1 X left-right" ,
		" Wii irdata 1 Y up-down" ,
		" Wii irdata 2 X left-right" ,
		" Wii irdata 2 Y up-down" ,
		" Wii irdata 3 X left-right" ,
		" Wii irdata 3 Y up-down" ,
		" Wii irdata 4 X left-right" ,
		" Wii irdata 4 Y up-down" ,
		" Wii Button A" ,
		" Wii Button B" ,
		" Wii Button UP" ,
		" Wii Button DOWN" ,
		" Wii Button LEFT" ,
		" Wii Button RIGHT" ,
		" Wii Button MINUS" ,
		" Wii Button PLUS" ,
		" Wii Button HOME" ,
		" Wii Button 1" ,
		" Wii Button 2" ,
		" Nunchuk Joystick left-right" ,
		" Nunchuk Joystick up-down" ,
		" Nunchuk Accleration" ,
		" Nunchuk Roll left-right" ,
		" Nunchuk Pitch front-back " ,
		" Nunchuk Button Z" ,
		" Nunchuk Button C" ,
	];
	arrOscStrings = [
		"dummy" ,
		"/wii/acc" ,
		"/wii/orientation" ,
		"/wii/orientation" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/irdata" ,
		"/wii/button/a" ,
		"/wii/button/b" ,
		"/wii/button/up" ,
		"/wii/button/down" ,
		"/wii/button/left" ,
		"/wii/button/right" ,
		"/wii/button/minus" ,
		"/wii/button/plus" ,
		"/wii/button/home" ,
		"/wii/button/one" ,
		"/wii/button/two" ,
		"/nunchuk/joystick" ,
		"/nunchuk/joystick" ,
		"/nunchuk/acc" ,
		"/nunchuk/orientation" ,
		"/nunchuk/orientation" ,
		"/nunchuk/button/z" ,
		"/nunchuk/button/c" ,
	];
	arrOscArgOffsets = [0, 0, 0,1, 0,1,3,4,6,7,9,10, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0,1, 0, 0,1, 0, 0];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	arrSynthArgSpecs = [
		["out", 0, 0],
		["wiiControl", 0, 0],
		["oscString", "/example/text", 0],
	]; 
	synthDefFunc = { arg out, wiiControl, oscString;
		var mixOut=0;
		Out.ar(out, mixOut);
	};
	guiSpecArray = [
		["TXStaticText", "Please note:", 
			"DarwiinRemote OSC Preferences - set Port to  " ++ NetAddr.langPort.asString],
		["SpacerLine", 4], 
		["TXPopupAction", "Wii control", arrWiiNames, "wiiControl", 
			{ arg view; this.setOscString(view.value); this.resetTestValues}],		
	];
	arrActionSpecs = this.buildActionSpecs([
		["TXPopupAction", "Wii control", arrWiiNames, "wiiControl", { arg view; this.setOscString(view.value); }],
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

setOscString { arg argWiiControl;
	var holdOscString;
	holdOscString = arrOscStrings. at(argWiiControl);
	// set current value in module
	this.oscString = holdOscString;
	// store current data to synthArgSpecs
	this.setSynthArgSpec("oscString", holdOscString);
	// activate osc responder
	this.oscControlActivate;
}

oscControlActivate { 
	var indexOffset, holdControlVal, arrValues;
	indexOffset = arrOscArgOffsets.at(this.getSynthArgSpec("wiiControl") ? 0);
	//	stop any previous responder 
	this.oscControlDeactivate;
	oscResponder = OSCresponderNode(nil, oscString.asSymbol, { arg time, responder, msg;

//	For testing  - post details
//
//		if (msg.at(1) > testMax, {
//			[time, msg].round(0.01).postln;
//			testMax = msg.at(1);
//			(msg.at(0) ++ "    Max: " ++ testMax.round(0.01).asString).postln;
//		});
//		if (msg.at(1) < testMin, {
//			[time, msg].round(0.01).postln;
//			testMin = msg.at(1);
//			(msg.at(0) ++ "    Min: " ++ testMin.round(0.01).asString).postln;
//		});
//	
//		if (testTime < time.round(1), {
//			[time, msg].round(0.01).postln;
//			"TXWiiController : ".postln;
//			[time.round, msg.round(0.01)].postln;
//			testTime = time.round(1);
//		});
//

		holdControlVal = msg.at(1+indexOffset);

		// scale all values first to range 0 - +1
		if (msg.at(0).asString.keep("/wii/orientation".size) == "/wii/orientation", {
			holdControlVal = ((holdControlVal / 150) + 1 ) / 2;
		});
		if (msg.at(0).asString.keep("/nunchuk/orientation".size) == "/nunchuk/orientation", {
			holdControlVal = ((holdControlVal / 150) + 1 ) / 2;
		});
		// acceleration gets merged to 1 positive value
		if (msg.at(0).asString.keep("/wii/acc".size) == "/wii/acc", {
			holdControlVal =	[msg.at(1), msg.at(2), msg.at(3)]
				.collect({arg item, i; ((item/250) - 0.5).abs})
				.sum;
		});
		if (msg.at(0).asString.keep("/nunchuk/acc".size) == "/nunchuk/acc", {
			holdControlVal =	[msg.at(1), msg.at(2), msg.at(3)]
				.collect({arg item, i; ((item/250) - 0.5).abs})
				.sum;
		});
		// irdata - values of 1 are ignored
		if (msg.at(0).asString.keep("/wii/irdata".size) == "/wii/irdata", {
			if (holdControlVal == 1, {
				holdControlVal = nil;
			});
		});
		if (msg.at(0).asString.keep("/nunchuk/joystick".size) == "/nunchuk/joystick", {
			holdControlVal = ((holdControlVal) + 1 ) / 2;
		});

		// set the Bus value
	 	if ( (outBus.class == Bus) and: (holdControlVal.isNumber), {
	 		outBus.value_(holdControlVal.max(-1).min(1)); 
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

//resetTestValues {
//	testMin = 10000;
//	testMax = -10000;
//} 

}

