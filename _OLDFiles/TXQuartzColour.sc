// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXQuartzColour : TXModuleBase {		// Quartz Composition 1

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
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	holdScreenSizes;
	
	var	hueResp;
	var	brightnessResp;
	var	saturationResp;
	var	sendTrigIDHue, sendTrigIDSaturation, sendTrigIDBrightness;
	var	holdQCWindow, holdQCView;
	var	screenWidth, screenHeight;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	holdScreenSizes = [ [640, 480], [720, 480], [800, 500], [800, 600], [1024, 640], [1024, 768], 
		[1152, 720], [1280, 800], [1440, 900]
	];
	defaultName = "QC colour";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 		
		["Hue", 1, "modHue", 0],
		["Saturation", 1, "modSaturation", 0],
		["Brightness", 1, "modBrightness", 0],
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
	var arrItems;
	//	set  class specific instance variables
	// create unique ids
	sendTrigIDHue = UniqueID.next;
	sendTrigIDSaturation = UniqueID.next;
	sendTrigIDBrightness = UniqueID.next;
	screenWidth = 1024;
	screenHeight = 640;
	arrSynthArgSpecs = [
		["out", 0, 0],
		["active", 1, 0],
		["hue", 0, defLagTime],
		["saturation", 0, defLagTime],
		["brightness", 0, defLagTime],
		["modHue", 0, defLagTime],
		["modSaturation", 0, defLagTime],
		["modBrightness", 0, defLagTime],
		["i_screenSize", 4, 0],
	]; 
	synthDefFunc = { 
		arg out, active, hue, saturation, brightness, modHue, modSaturation, modBrightness, i_screenSize;
	   var imp, trig1, trig2, trig3, holdHue, holdSaturation, holdBrightness;
	   imp = Impulse.kr(20) * active;
	   holdHue = (hue + modHue).max(0).min(1);
	   holdSaturation = (saturation + modSaturation).max(0).min(1);
	   holdBrightness = (brightness + modBrightness).max(0).min(1);
	   trig1 = Trig.kr(imp * HPZ1.kr(holdHue).abs, 0.01); 
	   trig2 = Trig.kr(imp * HPZ1.kr(holdSaturation).abs, 0.01); 
	   trig3 = Trig.kr(imp * HPZ1.kr(holdBrightness).abs, 0.01); 
	   SendTrig.kr(trig1, sendTrigIDHue, holdHue);
	   SendTrig.kr(trig2, sendTrigIDSaturation, holdSaturation);
	   SendTrig.kr(trig3, sendTrigIDBrightness, holdBrightness);
	   // Note this synth doesn't need to write to the output bus
	};
	arrItems = holdScreenSizes.collect({arg item, i; item.at(0).asString + "X" + item.at(1).asString});
	guiSpecArray = [
		["ActionButton", "Show Video Screen", {this.rebuildQCScreen;}, 200], 
		["NextLine"], 
		["TXPopupAction", "Screen Size", arrItems, "i_screenSize", {this.resetScreenSize; this.oscActivate;}, 200], 
		["NextLine"], 
		["TXCheckBox", "Active", "active"],
		["DividingLine"], 
		["EZslider", "Hue", ControlSpec(0,1), "hue"],
		["EZslider", "Saturation", ControlSpec(0,1), "saturation"],
		["EZslider", "Brightness", ControlSpec(0,1), "brightness"],
		["DividingLine"], 
	];
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Show Video Screen", {this.rebuildQCScreen;}],
		["TXPopupAction", "Screen Size", arrItems, "i_screenSize", {this.resetScreenSize; this.oscActivate;}, 200], 
		["TXCheckBox", "Active", "active"],
		["EZslider", "Hue", ControlSpec(0,1), "hue"],
		["EZslider", "Saturation", ControlSpec(0,1), "saturation"],
		["EZslider", "Brightness", ControlSpec(0,1), "brightness"],
	]);
	// initialise 
	this.baseInit(this, argInstName);
	this.oscActivate;
	this.resetScreenSize;
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

showQCScreen { 
	{
		//	check if window exists 
		if (holdQCWindow.isNil) {
			// make window
			holdQCWindow= SCWindow(" ", Rect(0, 580, screenWidth, screenHeight)).front; 
			holdQCWindow.onClose_({
				holdQCWindow = nil;
				holdQCView = nil;
			}); 
			// make Button
			SCButton(holdQCWindow, Rect(20, 2, 40, 20))
				.states_([["<-", Color.white, Color.grey(0.1)]])
				.action_({system.windowToFront});
//			// make background
			SCStaticText(holdQCWindow, Rect(0 ,0, 1440, 900)).background_(TXColor.black);
			// make Quartz
			holdQCView = SCQuartzComposerView(holdQCWindow, Rect(20 ,25, screenWidth-40, screenHeight-50));
			holdQCView.path = this.class.filenameSymbol.asString.dirname ++ "/SimpleColour.qtz";
			holdQCView.start;
		}{
			// if window exists bring to front
			holdQCWindow.front;
		};
	}.defer;
}

rebuildQCScreen { 
	{	//	check if window exists 
		if (holdQCWindow.notNil) {holdQCWindow.close};
	}.defer;
	{
		this.showQCScreen;
	}.defer(0.1);
}

resetScreenSize { 
	screenWidth = holdScreenSizes.at(this.getSynthArgSpec("i_screenSize")).at(0);
	screenHeight = holdScreenSizes.at(this.getSynthArgSpec("i_screenSize")).at(1);
	this.rebuildQCScreen;
}

oscActivate {
	//	remove any previous OSCresponderNodes and add new
	this.oscDeactivate;
	hueResp = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == sendTrigIDHue,{
			{	if (holdQCView.notNil,  {
					holdQCView.hue =  msg[3];
				});
			}.defer;
		});
	}).add;
	saturationResp = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == sendTrigIDSaturation,{
			{	if (holdQCView.notNil,  {
					holdQCView.saturation =  msg[3];
				});
			}.defer;
		});
	}).add;
	brightnessResp = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == sendTrigIDBrightness,{
			{	if (holdQCView.notNil,  {
					holdQCView.brightness =  msg[3];
				});
			}.defer;
		});
	}).add;
}

oscDeactivate { 
	//	remove responders 
	hueResp.remove;
	brightnessResp.remove;
	saturationResp.remove;
}

loadExtraData {
	this.resetScreenSize;
}

deleteModuleExtraActions {     
	//	remove responders
	this.oscDeactivate;
	if (holdQCWindow.notNil) {
		// if window exists close it
		holdQCWindow.close;
	};
}

}


