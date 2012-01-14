// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXQCParticles : TXModuleBase {		// Quartz Composition

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
	var	pEnableResp;
	var	pXposResp;
	var	pYposResp;
	var	pZposResp;
	var	pMinSizeResp;
	var	pMaxSizeResp;
	var	pSizeDeltaResp;
	var	pOpacityDeltaResp;
	var	pAttractionResp;
	var	pGravityResp;
	var	pHueResp;
	var	pSaturationResp;
	var	pBrightnessResp;
	var	holdQCWindow, holdQCView;
	var	screenWidth, screenHeight;
	var arrResps, arrInputs, arrSendTrigIDs;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	holdScreenSizes = [ [640, 480], [720, 480], [800, 500], [800, 600], [1024, 640], [1024, 768], 
		[1152, 720], [1280, 800], [1440, 900]
	];
	defaultName = "QC Particles";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 		
		["Box Hue", 1, "modHue", 0],
		["Box Saturation", 1, "modSaturation", 0],
		["Box Brightness", 1, "modBrightness", 0],
		["Particles on", 1, "modpEnable", 0],
		["X Position", 1, "modpXpos", 0],
		["Y Position", 1, "modpYpos", 0],
		["Z Position", 1, "modpZpos", 0],
		["Min Size", 1, "modpMinSize", 0],
		["Max Size", 1, "modpMaxSize", 0],
		["Size Delta", 1, "modpSizeDelta", 0],
		["Opacity Delta", 1, "modpOpacityDelta", 0],
		["Attraction", 1, "modpAttraction", 0],
		["Gravity", 1, "modpGravity", 0],
		["Part Hue", 1, "modpHue", 0],
		["Part Saturation", 1, "modpSaturation", 0],
		["Part Brightness", 1, "modpBrightness", 0],
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
	arrResps = [hueResp, saturationResp, brightnessResp, pEnableResp,pXposResp, pYposResp, pZposResp, pMinSizeResp, 
		pMaxSizeResp, pSizeDeltaResp, pOpacityDeltaResp, pAttractionResp, pGravityResp, pHueResp, 
		pSaturationResp, pBrightnessResp];
	arrInputs = ['hue', 'saturation', 'brightness', 'pEnable', 'pXpos', 'pYpos', 'pZpos', 'pMinSize', 
		'pMaxSize', 'pSizeDelta', 'pOpacityDelta', 'pAttraction', 'pGravity', 'pHue', 
		'pSaturation', 'pBrightness'];
	arrSendTrigIDs = [];
	// create unique ids
	16.do({arg item, i;
		arrSendTrigIDs = arrSendTrigIDs.add(UniqueID.next);
	});
	screenWidth = 1024;
	screenHeight = 640;
	arrSynthArgSpecs = [
		["out", 0, 0],
		["active", 1, 0],
		["hue", 0, defLagTime],
		["saturation", 0, defLagTime],
		["brightness", 0, defLagTime],
		["pEnable", 1, 0],
		["pXpos", 0, defLagTime],
		["pYpos", 0, defLagTime],
		["pZpos", 0, defLagTime],
		["pMinSize", 0.1, defLagTime],
		["pMaxSize", 0.3, defLagTime],
		["pSizeDelta", -0.1, defLagTime],
		["pOpacityDelta", 0, defLagTime],
		["pAttraction", 0, defLagTime],
		["pGravity", 0, defLagTime],
		["pHue", 0.5, defLagTime],
		["pSaturation", 0.5, defLagTime],
		["pBrightness", 0.5, defLagTime],
		["modHue", 0, defLagTime],
		["modSaturation", 0, defLagTime],
		["modBrightness", 0, defLagTime],
		["modpEnable", 0, defLagTime],
		["modpXpos", 0, defLagTime],
		["modpYpos", 0, defLagTime],
		["modpZpos", 0, defLagTime],
		["modpMinSize", 0, defLagTime],
		["modpMaxSize", 0, defLagTime],
		["modpSizeDelta", 0, defLagTime],
		["modpOpacityDelta", 0, defLagTime],
		["modpAttraction", 0, defLagTime],
		["modpGravity", 0, defLagTime],
		["modpHue", 0, defLagTime],
		["modpSaturation", 0, defLagTime],
		["modpBrightness", 0, defLagTime],
		["i_screenSize", 4, 0],
	]; 

	synthDefFunc = { 
		arg out, active, hue, saturation, brightness, pEnable, pXpos, pYpos, pZpos, pMinSize, pMaxSize, 
			pSizeDelta, pOpacityDelta, pAttraction, pGravity, pHue, pSaturation, pBrightness, modHue, modSaturation, 
			modBrightness, modpEnable, modpXpos, modpYpos, modpZpos, modpMinSize, modpMaxSize, modpSizeDelta, 
			modpOpacityDelta, modpAttraction, modpGravity, modpHue, modpSaturation, modpBrightness, i_screenSize;
	   var imp, trig1, trig2, trig3, trig4, trig5, trig6, trig7, trig8, trig9, trig10, trig11, trig12, trig13, 
	   		trig14, trig15, trig16; 
	   var holdHue, holdSaturation, holdBrightness, holdpEnable, holdpXpos, holdpYpos, holdpZpos, 
	   		holdpMinSize, holdpMaxSize, holdpSizeDelta, holdpOpacityDelta, holdpAttraction, holdpGravity,
	   		holdpHue, holdpSaturation, holdpBrightness;

	   imp = Impulse.kr(20) * active;
	   holdHue = (hue + modHue).max(0).min(1);
	   holdSaturation = (saturation + modSaturation).max(0).min(1);
	   holdBrightness = (brightness + modBrightness).max(0).min(1);
	   holdpEnable = (pEnable + modpEnable).max(0).min(1);
	   holdpXpos = (pXpos + modpXpos).max(-1).min(1);
	   holdpYpos = (pYpos + modpYpos).max(-1).min(1);
	   holdpZpos = (pZpos + modpZpos).max(-1).min(1);
	   holdpMinSize = (pMinSize + modpMinSize).max(0).min(1);
	   holdpMaxSize = (pMaxSize + modpMaxSize).max(0).min(1);
	   holdpSizeDelta = (pSizeDelta + modpSizeDelta).max(-1).min(1);
	   holdpOpacityDelta = (pOpacityDelta + modpOpacityDelta).max(-1).min(1);
	   holdpAttraction = (pAttraction + modpAttraction).max(-1).min(1);
	   holdpGravity = (pGravity + modpGravity).max(-1).min(1);
	   holdpHue = (pHue + modpHue).max(0).min(1);
	   holdpSaturation = (pSaturation + modpSaturation).max(0).min(1);
	   holdpBrightness = (pBrightness + modpBrightness).max(0).min(1);

	   trig1 = Trig.kr(imp * HPZ1.kr(holdHue).abs, 0.01); 
	   trig2 = Trig.kr(imp * HPZ1.kr(holdSaturation).abs, 0.01); 
	   trig3 = Trig.kr(imp * HPZ1.kr(holdBrightness).abs, 0.01); 
	   trig4 = Trig.kr(imp * HPZ1.kr(holdpEnable).abs, 0.01); 
	   trig5 = Trig.kr(imp * HPZ1.kr(holdpXpos).abs, 0.01); 
	   trig6 = Trig.kr(imp * HPZ1.kr(holdpYpos).abs, 0.01); 
	   trig7 = Trig.kr(imp * HPZ1.kr(holdpZpos).abs, 0.01); 
	   trig8 = Trig.kr(imp * HPZ1.kr(holdpMinSize).abs, 0.01); 
	   trig9 = Trig.kr(imp * HPZ1.kr(holdpMaxSize).abs, 0.01); 
	   trig10 = Trig.kr(imp * HPZ1.kr(holdpSizeDelta).abs, 0.01); 
	   trig11 = Trig.kr(imp * HPZ1.kr(holdpOpacityDelta).abs, 0.01); 
	   trig12 = Trig.kr(imp * HPZ1.kr(holdpAttraction).abs, 0.01); 
	   trig13 = Trig.kr(imp * HPZ1.kr(holdpGravity).abs, 0.01); 
	   trig14 = Trig.kr(imp * HPZ1.kr(holdpHue).abs, 0.01); 
	   trig15 = Trig.kr(imp * HPZ1.kr(holdpSaturation).abs, 0.01); 
	   trig16 = Trig.kr(imp * HPZ1.kr(holdpBrightness).abs, 0.01); 

	   SendTrig.kr(trig1, arrSendTrigIDs.at(0), holdHue);
	   SendTrig.kr(trig2, arrSendTrigIDs.at(1), holdSaturation);
	   SendTrig.kr(trig3, arrSendTrigIDs.at(2), holdBrightness);
	   SendTrig.kr(trig4, arrSendTrigIDs.at(3), holdpEnable);
	   SendTrig.kr(trig5, arrSendTrigIDs.at(4), holdpXpos);
	   SendTrig.kr(trig6, arrSendTrigIDs.at(5), holdpYpos);
	   SendTrig.kr(trig7, arrSendTrigIDs.at(6), holdpZpos);
	   SendTrig.kr(trig8, arrSendTrigIDs.at(7), holdpMinSize);
	   SendTrig.kr(trig9, arrSendTrigIDs.at(8), holdpMaxSize);
	   SendTrig.kr(trig10, arrSendTrigIDs.at(9), holdpSizeDelta);
	   SendTrig.kr(trig11, arrSendTrigIDs.at(10), holdpOpacityDelta);
	   SendTrig.kr(trig12, arrSendTrigIDs.at(11), holdpAttraction);
	   SendTrig.kr(trig13, arrSendTrigIDs.at(12), holdpGravity);
	   SendTrig.kr(trig14, arrSendTrigIDs.at(13), holdpHue);
	   SendTrig.kr(trig15, arrSendTrigIDs.at(14), holdpSaturation);
	   SendTrig.kr(trig16, arrSendTrigIDs.at(15), holdpBrightness);

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
		["EZslider", "Box hue", ControlSpec(0,1), "hue"],
		["EZslider", "Box satur.", ControlSpec(0,1), "saturation"],
		["EZslider", "Box br'ness", ControlSpec(0,1), "brightness"],
//		["TXCheckBox", "Particles on", "pEnable"],
//		["NextLine"], 
		["EZslider", "X position", ControlSpec(-1,1), "pXpos"],
		["EZslider", "Y position", ControlSpec(-1,1), "pYpos"],
		["EZslider", "Z position", ControlSpec(-1,1), "pZpos"],
		["EZslider", "Min size", ControlSpec(0,1), "pMinSize"],
		["EZslider", "Max size", ControlSpec(0,1), "pMaxSize"],
		["EZslider", "Size delta", ControlSpec(-1,1), "pSizeDelta"],
		["EZslider", "Opacity delta", ControlSpec(-1,1), "pOpacityDelta"],
		["EZslider", "Attraction", ControlSpec(-1,1), "pAttraction"],
		["EZslider", "Gravity", ControlSpec(-1,1), "pGravity"],
		["EZslider", "Part hue", ControlSpec(0,1), "pHue"],
		["EZslider", "Part sat.", ControlSpec(0,1), "pSaturation"],
		["EZslider", "Part br'ness", ControlSpec(0,1), "pBrightness"],

		["DividingLine"], 
	];
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Show Video Screen", {this.rebuildQCScreen;}],
		["TXPopupAction", "Screen Size", arrItems, "i_screenSize", {this.resetScreenSize; this.oscActivate;}, 200], 
		["TXCheckBox", "Active", "active"],
		["EZslider", "Box Hue", ControlSpec(0,1), "hue"],
		["EZslider", "Box Saturation", ControlSpec(0,1), "saturation"],
		["EZslider", "Box Brightness", ControlSpec(0,1), "brightness"],
//		["TXCheckBox", "Particles on", "pEnable"],
		["EZslider", "X position", ControlSpec(-1,1), "pXpos"],
		["EZslider", "Y position", ControlSpec(-1,1), "pYpos"],
		["EZslider", "Z position", ControlSpec(-1,1), "pZpos"],
		["EZslider", "Min Size", ControlSpec(0,1), "pMinSize"],
		["EZslider", "Max Size", ControlSpec(0,1), "pMaxSize"],
		["EZslider", "Size Delta", ControlSpec(-1,1), "pSizeDelta"],
		["EZslider", "Opacity Delta", ControlSpec(-1,1), "pOpacityDelta"],
		["EZslider", "Attraction", ControlSpec(-1,1), "pAttraction"],
		["EZslider", "Gravity", ControlSpec(-1,1), "pGravity"],
		["EZslider", "Part Hue", ControlSpec(0,1), "pHue"],
		["EZslider", "Part Saturation", ControlSpec(0,1), "pSaturation"],
		["EZslider", "Part Brightness", ControlSpec(0,1), "pBrightness"],
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
			SCButton(holdQCWindow, Rect(2, 2, 30, 20))
				.states_([["<-", Color.white, Color.grey(0.1)]])
				.action_({system.windowToFront});
			// make background
			SCStaticText(holdQCWindow, Rect(0 ,0, 1440, 900)).background_(TXColor.black);
			// make Quartz
			holdQCView = SCQuartzComposerView(holdQCWindow, Rect(20 ,25, screenWidth-40, screenHeight-50));
			holdQCView.path = this.class.filenameSymbol.asString.dirname ++ "/Particles.qtz";
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
	this.sendCurrentValues;
}

sendCurrentValues { 
	{
		arrInputs.do({arg item, i;
			holdQCView.setInputValue(item, this.getSynthArgSpec(item.asString));
		});
	}.defer(0.4);
}

oscActivate {
	//	remove any previous OSCresponderNodes and add new
	this.oscDeactivate;
	arrResps.do({arg item, i;
		item = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
			if (msg[2] == arrSendTrigIDs.at(i),{
				{	if (holdQCView.notNil,  {
						holdQCView.setInputValue(arrInputs.at(i), msg[3]);
					});
				}.defer;
			});
		}).add;
	});
}

oscDeactivate { 
	//	remove responders 
	arrResps.do({arg item, i;
		item.remove;
	});
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


