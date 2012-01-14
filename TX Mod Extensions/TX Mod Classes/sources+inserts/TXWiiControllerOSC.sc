// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWiiControllerOSC : TXModuleBase {

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
	
	var	oscControlRoutine;
	var	<>oscString;
	var	oscResponder;
	var testTime = 0, testMin=10000, testMax = -10000; // for testing

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Wii Ctrl OSC";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrWiiNames = [
		" Select..." ,
		" Wii pitch - tilt forward/back" ,
		" Wii roll - tilt left/right" ,
		" Wii yaw - spin clockwise/anti-clockwise" ,
		" Wii acceleration" ,
		" Wii ir X" ,
		" Wii ir Y" ,
		" Wii button A" ,
		" Wii button B" ,
		" Wii button Up" ,
		" Wii button Down" ,
		" Wii button Left" ,
		" Wii button Right" ,
		" Wii button Minus" ,
		" Wii button Plus" ,
		" Wii button Home" ,
		" Wii button 1" ,
		" Wii button 2" ,
		" Nunchuk pitch - tilt forward/back" ,
		" Nunchuk roll - tilt left/right" ,
		" Nunchuk yaw - rotate clockwise/anti-clockwise" ,
		" Nunchuk acceleration" ,
		" Nunchuk joystick X" ,
		" Nunchuk joystick Y" ,
		" Nunchuk button Z" ,
		" Nunchuk button C" ,
	];
	arrOscStrings = [
		"dummy" ,
		"/wii/1/accel/pry" ,
		"/wii/1/accel/pry" ,
		"/wii/1/accel/pry" ,
		"/wii/1/accel/pry" ,
		"/wii/1/ir" ,
		"/wii/1/ir" ,
		"/wii/1/button/A" ,
		"/wii/1/button/B" ,
		"/wii/1/button/Up" ,
		"/wii/1/button/Down" ,
		"/wii/1/button/Left" ,
		"/wii/1/button/Right" ,
		"/wii/1/button/Minus" ,
		"/wii/1/button/Plus" ,
		"/wii/1/button/Home" ,
		"/wii/1/button/1" ,
		"/wii/1/button/2" ,
		"/wii/1/nunchuk/accel/pry" ,
		"/wii/1/nunchuk/accel/pry" ,
		"/wii/1/nunchuk/accel/pry" ,
		"/wii/1/nunchuk/accel/pry" ,
		"/wii/1/nunchuk/joy" ,
		"/wii/1/nunchuk/joy" ,
		"/wii/1/nunchuk/button/Z" ,
		"/wii/1/nunchuk/button/C" ,
	];
	arrOscArgOffsets = [0, 0,1,2,3, 0,1, 0,  0,  0,  0,  0,  0,  0,  0,  0, 
		 0,  0, 0,1,2,3, 0,1,  0,  0];
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
		["DividingLine"], 
		["TXStaticText", "Please note:", "OSCulator should be open with document WiiToTX1"],
		["DividingLine"], 
		["TXPopupAction", "Wii control", arrWiiNames, "wiiControl", { arg view; this.setOscString(view.value); 

//testing xxxxx
testMin=10000;
testMax = -10000;
		
		}],		
		["DividingLine"], 
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
	var indexOffset, holdControlVal;
	indexOffset = arrOscArgOffsets.at(this.getSynthArgSpec("wiiControl") ? 0);
	//	stop any previous responder 
	this.oscControlDeactivate;
	oscResponder = OSCresponderNode(nil, oscString.asSymbol, { arg time, responder, msg;

//	For testing  - post details
//
//	//	if (msg.at(0).asString.keep("/wii/irdata".size) == "/wii/irdata", {
//		
//		//	[time, responder, msg].round(0.01).postln;
//	
//			if (msg.at(1) > testMax, {
//				testMax = msg.at(1);
//				(msg.at(0) ++ "    Max: " ++ testMax.round(0.01).asString).postln;
//			});
//			if (msg.at(1) < testMin, {
//				testMin = msg.at(1);
//				(msg.at(0) ++ "    Min: " ++ testMin.round(0.01).asString).postln;
//			});
//		
//	//		if (testTime < time.round(0.25), {
//	//			"TXWiiController : ".postln;
//	//			[time.round, responder, msg.round(0.01)].postln;
//	//			testTime = time.round(0.25);
//	//		});
//	//	
//	//	});

		holdControlVal = msg.at(1+indexOffset);
		//  if ir and value X or Y is 1, ignore
		if (((msg.at(0).asString.keep("/wii/1/ir".size) == "/wii/1/ir")
			and: (holdControlVal == 1)
		).not, {
			// set the Bus value
		 	if ( (outBus.class == Bus) and: (holdControlVal.isNumber), {
		 		outBus.value_(holdControlVal.max(-1).min(1)); 
		 	});
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

