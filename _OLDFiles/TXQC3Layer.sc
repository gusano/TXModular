// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXQC3Layer : TXModuleBase {		// Quartz Composition

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
	classvar	holdScreenSizeTexts;
	
	var	hueResp;
	var	saturationResp;
	var	brightnessResp;
	var	pMinSizeResp;
	var	pMaxSizeResp;
	var	pSizeDeltaResp;
	var	pAttractionResp;
	var	pGravityResp;
	var	pEnableResp;
	var	pHueResp;
	var	pSaturationResp;
	var	pBrightnessResp;
	var	pLifetimeResp;
	var	pOpacityDeltaResp;
	var	pXMinVelResp;
	var	pXMaxVelResp;
	var	pYMinVelResp;
	var	pYMaxVelResp;
	var	pZMinVelResp;
	var	pZMaxVelResp;
	var	pXposResp;
	var	pYposResp;
	var	sScaleResp;
	var	sHueResp;
	var	sSaturationResp;
	var	sBrightnessResp;
	var	sAlphaResp;
	var	pAlphaResp;
	var	lHueResp;
	var	lSaturationResp;
	var	lLuminosityResp;
	var	lAlphaResp;
	var	lLengthResp;
	var	lAttractionYResp;
	var	sXposResp;
	var	sYposResp;
	
	var	holdQCWindow, holdQCView;
	var	screenWidth, screenHeight;
	var arrResps, arrInputs, arrSendTrigIDs;
	var	displayOption;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	holdScreenSizes = [ [640, 480], [720, 480], [800, 500], [800, 600], [1024, 640], [1024, 768], 
		[1152, 720], [1280, 800], [1440, 900]
	];
	holdScreenSizeTexts = holdScreenSizes.collect({arg item, i; item.at(0).asString + "X" + item.at(1).asString});
	defaultName = "QC 3-Layer";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 		

		["Screen Hue", 1, "modhue", 0],
		["Screen Saturation", 1, "modsaturation", 0],
		["Screen Brightness", 1, "modbrightness", 0],
		["Particles on", 1, "modpEnable", 0],
		["Part X Pos", 1, "modpXpos", 0],
		["Part Y Pos", 1, "modpYpos", 0],
		["Min Size", 1, "modpMinSize", 0],
		["Max Size", 1, "modpMaxSize", 0],
		["Size Delta", 1, "modpSizeDelta", 0],
		["Opacity Delta", 1, "modpOpacityDelta", 0],
		["Attraction", 1, "modpAttraction", 0],
		["Gravity", 1, "modpGravity", 0],
		["Part Hue", 1, "modpHue", 0],
		["Part Saturation", 1, "modpSaturation", 0],
		["Part Brightness", 1, "modpBrightness", 0],
		["Part Alpha", 1, "modpAlpha", 0],
		["X vel min", 1, "modpXMinVel", 0],
		["X vel max", 1, "modpXMaxVel", 0],
		["Y vel min", 1, "modpYMinVel", 0],
		["Y vel max", 1, "modpYMaxVel", 0],
		["Z vel min", 1, "modpZMinVel", 0],
		["Z vel max", 1, "modpZMaxVel", 0],
		["Lifetime", 1, "modpLifetime", 0],

		["Sphere Scale", 1, "modsScale", 0],
		["Sphere Hue", 1, "modsHue", 0],
		["Sphere Saturation", 1, "modsSaturation", 0],
		["Sphere Brightness", 1, "modsBrightness", 0],
		["Sphere Alpha", 1, "modsAlpha", 0],
		["Line Hue", 1, "modlHue", 0],
		["Line Saturation", 1, "modlSaturation", 0],
		["Line Brightness", 1, "modlLuminosity", 0],
		["Line Alpha", 1, "modlAlpha", 0],
		["Line Length ", 1, "modlLength", 0],
		["Line Attract Y", 1, "modlAttractionY", 0],
		["Sphere X Pos", 1, "modsXpos", 0],
		["Sphere Y Pos", 1, "modsYpos", 0],
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
	displayOption = "show1";
	arrResps = [ hueResp, saturationResp, brightnessResp, pMinSizeResp, pMaxSizeResp, pSizeDeltaResp, pAttractionResp, 
		pGravityResp, pEnableResp, pHueResp, pSaturationResp, pBrightnessResp, pLifetimeResp, pOpacityDeltaResp, pXMinVelResp,
		 pXMaxVelResp, pYMinVelResp, pYMaxVelResp, pZMinVelResp, pZMaxVelResp, pXposResp, pYposResp, sScaleResp, sHueResp, 
		 sSaturationResp, sBrightnessResp, sAlphaResp, pAlphaResp, lHueResp, lSaturationResp, lLuminosityResp, lAlphaResp, 
		 lLengthResp, lAttractionYResp, sXposResp, sYposResp];
	arrInputs = ['hue', 'saturation', 'brightness', 'pMinSize', 'pMaxSize', 'pSizeDelta', 'pAttraction', 'pGravity', 'pEnable', 
		'pHue', 'pSaturation', 'pBrightness', 'pLifetime', 'pOpacityDelta', 'pXMinVel', 'pXMaxVel', 'pYMinVel', 'pYMaxVel', 
		'pZMinVel', 'pZMaxVel', 'pXpos', 'pYpos', 'sScale', 'sHue', 'sSaturation', 'sBrightness', 'sAlpha', 'pAlpha', 
		'lHue', 'lSaturation', 'lLuminosity', 'lAlpha', 'lLength', 'lAttractionY', 'sXpos', 'sYpos'];
	arrSendTrigIDs = [];
	// create unique ids
	36.do({arg item, i;
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
		["pMinSize", 0.1, defLagTime],
		["pMaxSize", 0.3, defLagTime],
		["pSizeDelta", -0.1, defLagTime],
		["pAttraction", 0, defLagTime],
		["pGravity", 0, defLagTime],
		["pEnable", 1, defLagTime],
		["pHue", 0, defLagTime],
		["pSaturation", 0, defLagTime],
		["pBrightness", 0, defLagTime],
		["pLifetime", 0, defLagTime],
		["pOpacityDelta", 0, defLagTime],
		["pXMinVel",  -0.3, defLagTime],
		["pXMaxVel", 0.3, defLagTime],
		["pYMinVel",  -0.3, defLagTime],
		["pYMaxVel", 0.3, defLagTime],
		["pZMinVel",  -0.3, defLagTime],
		["pZMaxVel", 0.3, defLagTime],
		["pXpos", 0.5, defLagTime],
		["pYpos", 0.5, defLagTime],
		["sScale", 0, defLagTime],
		["sHue", 0.5, defLagTime],
		["sSaturation", 0.5, defLagTime],
		["sBrightness", 0.5, defLagTime],
		["sAlpha", 1, defLagTime],
		["pAlpha", 1, defLagTime],
		["lHue", 0.2, defLagTime],
		["lSaturation", 0.5, defLagTime],
		["lLuminosity", 0.5, defLagTime],
		["lAlpha", 1, defLagTime],
		["lLength", 0.5, defLagTime],
		["lAttractionY", 0.1, defLagTime],
		["sXpos", 0.5, defLagTime],
		["sYpos", 0.5, defLagTime],

		["modhue", 0, defLagTime],
		["modsaturation", 0, defLagTime],
		["modbrightness", 0, defLagTime],
		["modpMinSize", 0, defLagTime],
		["modpMaxSize", 0, defLagTime],
		["modpSizeDelta", 0, defLagTime],
		["modpAttraction", 0, defLagTime],
		["modpGravity", 0, defLagTime],
		["modpEnable", 0, defLagTime],
		["modpHue", 0, defLagTime],
		["modpSaturation", 0, defLagTime],
		["modpBrightness", 0, defLagTime],
		["modpLifetime", 0, defLagTime],
		["modpOpacityDelta", 0, defLagTime],
		["modpXMinVel", 0, defLagTime],
		["modpXMaxVel", 0, defLagTime],
		["modpYMinVel", 0, defLagTime],
		["modpYMaxVel", 0, defLagTime],
		["modpZMinVel", 0, defLagTime],
		["modpZMaxVel", 0, defLagTime],
		["modpXpos", 0, defLagTime],
		["modpYpos", 0, defLagTime],
		["modsScale", 0, defLagTime],
		["modsHue", 0, defLagTime],
		["modsSaturation", 0, defLagTime],
		["modsBrightness", 0, defLagTime],
		["modsAlpha", 0, defLagTime],
		["modpAlpha", 0, defLagTime],
		["modlHue", 0, defLagTime],
		["modlSaturation", 0, defLagTime],
		["modlLuminosity", 0, defLagTime],
		["modlAlpha", 0, defLagTime],
		["modlLength", 0, defLagTime],
		["modlAttractionY", 0, defLagTime],
		["modsXpos", 0, defLagTime],
		["modsYpos", 0, defLagTime],

		["i_screenSize", 4, 0],
	]; 

	synthDefFunc = { 
		arg out, active, hue, saturation, brightness, pMinSize, pMaxSize, pSizeDelta, pAttraction, pGravity, pEnable, pHue, 
			pSaturation, pBrightness, pLifetime, pOpacityDelta, pXMinVel, pXMaxVel, pYMinVel, pYMaxVel, pZMinVel, pZMaxVel, 
			pXpos, pYpos, sScale, sHue, sSaturation, sBrightness, sAlpha, pAlpha, lHue, lSaturation, lLuminosity, lAlpha, 
			lLength, lAttractionY, sXpos, sYpos,
			modhue, modsaturation, modbrightness, modpMinSize, modpMaxSize, modpSizeDelta, modpAttraction, modpGravity, modpEnable, 
			modpHue, modpSaturation, modpBrightness, modpLifetime, modpOpacityDelta, modpXMinVel, modpXMaxVel, modpYMinVel, 
			modpYMaxVel, modpZMinVel, modpZMaxVel, modpXpos, modpYpos, modsScale, modsHue, modsSaturation, modsBrightness, 
			modsAlpha, modpAlpha, modlHue, modlSaturation, modlLuminosity, modlAlpha, modlLength, modlAttractionY, modsXpos, 
			modsYpos,
			i_screenSize;
	   var imp, trig1, trig2, trig3, trig4, trig5, trig6, trig7, trig8, trig9, trig10, trig11, trig12, trig13, 
	   		trig14, trig15, trig16, trig17, trig18, trig19, trig20, trig21, trig22, trig23, trig24, trig25, trig26, trig27, 
	   		trig28, trig29, trig30, trig31, trig32, trig33, trig34, trig35, trig36, trig37, trig38, trig39; 
	   var holdhue, holdsaturation, holdbrightness, holdpMinSize, holdpMaxSize, holdpSizeDelta, holdpAttraction, holdpGravity, holdpEnable, 
			holdpHue, holdpSaturation, holdpBrightness, holdpLifetime, holdpOpacityDelta, holdpXMinVel, holdpXMaxVel, holdpYMinVel, 
			holdpYMaxVel, holdpZMinVel, holdpZMaxVel, holdpXpos, holdpYpos, holdsScale, holdsHue, holdsSaturation, holdsBrightness, 
			holdsAlpha, holdpAlpha, holdlHue, holdlSaturation, holdlLuminosity, holdlAlpha, holdlLength, holdlAttractionY, holdsXpos, 
			holdsYpos;

	   imp = Impulse.kr(20) * active;

	   holdhue = (hue + modhue).max(0).min(1);
	   holdsaturation = (saturation + modsaturation).max(0).min(1);
	   holdbrightness = (brightness + modbrightness).max(0).min(1);
	   holdpMinSize = (pMinSize + modpMinSize).max(0).min(1);
	   holdpMaxSize = (pMaxSize + modpMaxSize).max(0).min(1);
	   holdpSizeDelta = (pSizeDelta + modpSizeDelta).max(-1).min(1);
	   holdpAttraction = (pAttraction + modpAttraction).max(-1).min(1);
	   holdpGravity = (pGravity + modpGravity).max(-1).min(1);
	   holdpEnable = (pEnable + modpEnable).max(0).min(1);
	   holdpHue = (pHue + modpHue).max(0).min(1);
	   holdpSaturation = (pSaturation + modpSaturation).max(0).min(1);
	   holdpBrightness = (pBrightness + modpBrightness).max(0).min(1);
	   holdpLifetime = (pLifetime + modpLifetime).max(0).min(1);
	   holdpOpacityDelta = (pOpacityDelta + modpOpacityDelta).max(-1).min(1);
	   holdpXMinVel = (pXMinVel + modpXMinVel).max(-1).min(1);
	   holdpXMaxVel = (pXMaxVel + modpXMaxVel).max(-1).min(1);
	   holdpYMinVel = (pYMinVel + modpYMinVel).max(-1).min(1);
	   holdpYMaxVel = (pYMaxVel + modpYMaxVel).max(-1).min(1);
	   holdpZMinVel = (pZMinVel + modpZMinVel).max(-1).min(1);
	   holdpZMaxVel = (pZMaxVel + modpZMaxVel).max(-1).min(1);
	   holdpXpos = (pXpos + modpXpos).max(0).min(1);
	   holdpYpos = (pYpos + modpYpos).max(0).min(1);
	   holdsScale = (sScale + modsScale).max(0).min(1);
	   holdsHue = (sHue + modsHue).max(0).min(1);
	   holdsSaturation = (sSaturation + modsSaturation).max(0).min(1);
	   holdsBrightness = (sBrightness + modsBrightness).max(0).min(1);
	   holdsAlpha = (sAlpha + modsAlpha).max(0).min(1);
	   holdpAlpha = (pAlpha + modpAlpha).max(0).min(1);
	   holdlHue = (lHue + modlHue).max(0).min(1);
	   holdlSaturation = (lSaturation + modlSaturation).max(0).min(1);
	   holdlLuminosity = (lLuminosity + modlLuminosity).max(0).min(1);
	   holdlAlpha = (lAlpha + modlAlpha).max(0).min(1);
	   holdlLength = (lLength + modlLength).max(0).min(1);
	   holdlAttractionY = (lAttractionY + modlAttractionY).max(0).min(1);
	   holdsXpos = (sXpos + modsXpos).max(0).min(1);
	   holdsYpos = (sYpos + modsYpos).max(0).min(1);

	   trig1 = Trig.kr(imp * HPZ1.kr(holdhue).abs, 0.01); 
	   trig2 = Trig.kr(imp * HPZ1.kr(holdsaturation).abs, 0.01); 
	   trig3 = Trig.kr(imp * HPZ1.kr(holdbrightness).abs, 0.01); 
	   trig4 = Trig.kr(imp * HPZ1.kr(holdpMinSize).abs, 0.01); 
	   trig5 = Trig.kr(imp * HPZ1.kr(holdpMaxSize).abs, 0.01); 
	   trig6 = Trig.kr(imp * HPZ1.kr(holdpSizeDelta).abs, 0.01); 
	   trig7 = Trig.kr(imp * HPZ1.kr(holdpAttraction).abs, 0.01); 
	   trig8 = Trig.kr(imp * HPZ1.kr(holdpGravity).abs, 0.01); 
	   trig9 = Trig.kr(imp * HPZ1.kr(holdpEnable).abs, 0.01); 
	   trig10 = Trig.kr(imp * HPZ1.kr(holdpHue).abs, 0.01); 
	   trig11 = Trig.kr(imp * HPZ1.kr(holdpSaturation).abs, 0.01); 
	   trig12 = Trig.kr(imp * HPZ1.kr(holdpBrightness).abs, 0.01); 
	   trig13 = Trig.kr(imp * HPZ1.kr(holdpLifetime).abs, 0.01); 
	   trig14 = Trig.kr(imp * HPZ1.kr(holdpOpacityDelta).abs, 0.01); 
	   trig15 = Trig.kr(imp * HPZ1.kr(holdpXMinVel).abs, 0.01); 
	   trig16 = Trig.kr(imp * HPZ1.kr(holdpXMaxVel).abs, 0.01); 
	   trig17 = Trig.kr(imp * HPZ1.kr(holdpYMinVel).abs, 0.01); 
	   trig18 = Trig.kr(imp * HPZ1.kr(holdpYMaxVel).abs, 0.01); 
	   trig19 = Trig.kr(imp * HPZ1.kr(holdpZMinVel).abs, 0.01); 
	   trig20 = Trig.kr(imp * HPZ1.kr(holdpZMaxVel).abs, 0.01); 
	   trig21 = Trig.kr(imp * HPZ1.kr(holdpXpos).abs, 0.01); 
	   trig22 = Trig.kr(imp * HPZ1.kr(holdpYpos).abs, 0.01); 
	   trig23 = Trig.kr(imp * HPZ1.kr(holdsScale).abs, 0.01); 
	   trig24 = Trig.kr(imp * HPZ1.kr(holdsHue).abs, 0.01); 
	   trig25 = Trig.kr(imp * HPZ1.kr(holdsSaturation).abs, 0.01); 
	   trig26 = Trig.kr(imp * HPZ1.kr(holdsBrightness).abs, 0.01); 
	   trig27 = Trig.kr(imp * HPZ1.kr(holdsAlpha).abs, 0.01); 
	   trig28 = Trig.kr(imp * HPZ1.kr(holdpAlpha).abs, 0.01); 
	   trig29 = Trig.kr(imp * HPZ1.kr(holdlHue).abs, 0.01); 
	   trig30 = Trig.kr(imp * HPZ1.kr(holdlSaturation).abs, 0.01); 
	   trig31 = Trig.kr(imp * HPZ1.kr(holdlLuminosity).abs, 0.01); 
	   trig32 = Trig.kr(imp * HPZ1.kr(holdlAlpha).abs, 0.01); 
	   trig33 = Trig.kr(imp * HPZ1.kr(holdlLength).abs, 0.01); 
	   trig34 = Trig.kr(imp * HPZ1.kr(holdlAttractionY).abs, 0.01); 
	   trig35 = Trig.kr(imp * HPZ1.kr(holdsXpos).abs, 0.01); 
	   trig36 = Trig.kr(imp * HPZ1.kr(holdsYpos).abs, 0.01);

	   SendTrig.kr(trig1, arrSendTrigIDs.at(0), holdhue);
	   SendTrig.kr(trig2, arrSendTrigIDs.at(1), holdsaturation);
	   SendTrig.kr(trig3, arrSendTrigIDs.at(2), holdbrightness);
	   SendTrig.kr(trig4, arrSendTrigIDs.at(3), holdpMinSize);
	   SendTrig.kr(trig5, arrSendTrigIDs.at(4), holdpMaxSize);
	   SendTrig.kr(trig6, arrSendTrigIDs.at(5), holdpSizeDelta);
	   SendTrig.kr(trig7, arrSendTrigIDs.at(6), holdpAttraction);
	   SendTrig.kr(trig8, arrSendTrigIDs.at(7), holdpGravity);
	   SendTrig.kr(trig9, arrSendTrigIDs.at(8), holdpEnable);
	   SendTrig.kr(trig10, arrSendTrigIDs.at(9), holdpHue);
	   SendTrig.kr(trig11, arrSendTrigIDs.at(10), holdpSaturation);
	   SendTrig.kr(trig12, arrSendTrigIDs.at(11), holdpBrightness);
	   SendTrig.kr(trig13, arrSendTrigIDs.at(12), holdpLifetime);
	   SendTrig.kr(trig14, arrSendTrigIDs.at(13), holdpOpacityDelta);
	   SendTrig.kr(trig15, arrSendTrigIDs.at(14), holdpXMinVel);
	   SendTrig.kr(trig16, arrSendTrigIDs.at(15), holdpXMaxVel);
	   SendTrig.kr(trig17, arrSendTrigIDs.at(16), holdpYMinVel);
	   SendTrig.kr(trig18, arrSendTrigIDs.at(17), holdpYMaxVel);
	   SendTrig.kr(trig19, arrSendTrigIDs.at(18), holdpZMinVel);
	   SendTrig.kr(trig20, arrSendTrigIDs.at(19), holdpZMaxVel);
	   SendTrig.kr(trig21, arrSendTrigIDs.at(20), holdpXpos);
	   SendTrig.kr(trig22, arrSendTrigIDs.at(21), holdpYpos);
	   SendTrig.kr(trig23, arrSendTrigIDs.at(22), holdsScale);
	   SendTrig.kr(trig24, arrSendTrigIDs.at(23), holdsHue);
	   SendTrig.kr(trig25, arrSendTrigIDs.at(24), holdsSaturation);
	   SendTrig.kr(trig26, arrSendTrigIDs.at(25), holdsBrightness);
	   SendTrig.kr(trig27, arrSendTrigIDs.at(26), holdsAlpha);
	   SendTrig.kr(trig28, arrSendTrigIDs.at(27), holdpAlpha);
	   SendTrig.kr(trig29, arrSendTrigIDs.at(28), holdlHue);
	   SendTrig.kr(trig30, arrSendTrigIDs.at(29), holdlSaturation);
	   SendTrig.kr(trig31, arrSendTrigIDs.at(30), holdlLuminosity);
	   SendTrig.kr(trig32, arrSendTrigIDs.at(31), holdlAlpha);
	   SendTrig.kr(trig33, arrSendTrigIDs.at(32), holdlLength);
	   SendTrig.kr(trig34, arrSendTrigIDs.at(33), holdlAttractionY);
	   SendTrig.kr(trig35, arrSendTrigIDs.at(34), holdsXpos);
	   SendTrig.kr(trig36, arrSendTrigIDs.at(35), holdsYpos);
	   

	   // Note this synth doesn't need to write to the output bus
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Show Video Screen", {this.rebuildQCScreen;}],
		["TXPopupAction", "Screen Size", holdScreenSizeTexts, "i_screenSize", {this.resetScreenSize; this.oscActivate;}, 200], 
		["TXCheckBox", "Active", "active"],
		["EZslider", "Screen Hue", ControlSpec(0,1), "hue"],
		["EZslider", "Screen Saturation", ControlSpec(0,1), "saturation"],
		["EZslider", "Screen Brightness", ControlSpec(0,1), "brightness"],
//		["TXCheckBox", "Particles on", "pEnable"],
		["EZslider", "X position", ControlSpec(0,1), "pXpos"],
		["EZslider", "Y position", ControlSpec(0,1), "pYpos"],
//		["EZslider", "Z position", ControlSpec(-1,1), "pZpos"],
		["EZslider", "Min Size", ControlSpec(0,1), "pMinSize"],
		["EZslider", "Max Size", ControlSpec(0,1), "pMaxSize"],
		["EZslider", "Size Delta", ControlSpec(-1,1), "pSizeDelta"],
		["EZslider", "Opacity Delta", ControlSpec(-1,1), "pOpacityDelta"],
		["EZslider", "Attraction", ControlSpec(-1,1), "pAttraction"],
		["EZslider", "Gravity", ControlSpec(-1,1), "pGravity"],
		["EZslider", "Part Hue", ControlSpec(0,1), "pHue"],
		["EZslider", "Part Saturation", ControlSpec(0,1), "pSaturation"],
		["EZslider", "Part Brightness", ControlSpec(0,1), "pBrightness"],
		["EZslider", "Part Alpha", ControlSpec(0,1), "pAlpha"],
		["EZslider", "X vel min", ControlSpec(-1,1), "pXMinVel"],
		["EZslider", "X vel max", ControlSpec(-1,1), "pXMaxVel"],
		["EZslider", "Y vel min", ControlSpec(-1,1), "pYMinVel"],
		["EZslider", "Y vel max", ControlSpec(-1,1), "pYMaxVel"],
		["EZslider", "Z vel min", ControlSpec(-1,1), "pZMinVel"],
		["EZslider", "Z vel max", ControlSpec(-1,1), "pZMaxVel"],
		["EZslider", "Lifetime", ControlSpec(0,1), "pLifetime"],
		
		["EZslider", "Sphere X pos", ControlSpec(0,1), "sXpos"],
		["EZslider", "Sphere Y pos", ControlSpec(0,1), "sYpos"],
		["EZslider", "Sphere Scale ", ControlSpec(0,1), "sScale"],
		["EZslider", "Sphere Hue", ControlSpec(0,1), "sHue"],
		["EZslider", "Sphere Saturation", ControlSpec(0,1), "sSaturation"],
		["EZslider", "Sphere Brightness", ControlSpec(0,1), "sBrightness"],
		["EZslider", "Sphere Alpha", ControlSpec(0,1), "sAlpha"],
		["EZslider", "Lines Hue", ControlSpec(0,1), "lHue"],
		["EZslider", "Lines Saturation", ControlSpec(0,1), "lSaturation"],
		["EZslider", "Lines Brightness", ControlSpec(0,1), "lLuminosity"],
		["EZslider", "Lines Alpha", ControlSpec(0,1), "lAlpha"],
		["EZslider", "Lines Length", ControlSpec(0,1), "lLength"],
		["EZslider", "Lines Attract Y", ControlSpec(0,1), "lAttractionY"],
		
	]);
	// initialise 
	this.baseInit(this, argInstName);
	this.oscActivate;
	this.resetScreenSize;

	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

buildGuiSpecArray {
	guiSpecArray = [
		["TXPopupAction", "Screen Size", holdScreenSizeTexts, "i_screenSize", {this.resetScreenSize; this.oscActivate;}, 200], 
		["ActionButton", "Show Video Screen", {this.rebuildQCScreen;}, 200], 
//		["NextLine"], 
//		["TXCheckBox", "Active", "active"],
		["SpacerLine", 6], 
		["ActionButton", "settings 1", {displayOption = "show1"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "show1")], 
		["Spacer", 3], 
		["ActionButton", "settings 2", {displayOption = "show2"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "show2")], 
		["Spacer", 3], 
		["ActionButton", "settings 3", {displayOption = "show3"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "show3")], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "show1", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "Screen hue", ControlSpec(0,1), "hue"],
		["EZslider", "Screen satur", ControlSpec(0,1), "saturation"],
		["EZslider", "Scr'n br'ness", ControlSpec(0,1), "brightness"],
		["DividingLine"], 
//		["TXCheckBox", "Particles on", "pEnable"],
//		["NextLine"], 
		["EZslider", "X position", ControlSpec(-1,1), "pXpos"],
		["EZslider", "Y position", ControlSpec(-1,1), "pYpos"],
		["EZslider", "Z position", ControlSpec(-1,1), "pZpos"],
		["DividingLine"], 
		["EZslider", "Min size", ControlSpec(0,1), "pMinSize"],
		["EZslider", "Max size", ControlSpec(0,1), "pMaxSize"],
		["DividingLine"], 
		["EZslider", "Size delta", ControlSpec(-1,1), "pSizeDelta"],
		["EZslider", "Opacity delta", ControlSpec(-1,1), "pOpacityDelta"],
		];
	});
	if (displayOption == "show2", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "Lifetime", ControlSpec(0,1), "pLifetime"],
		["DividingLine"], 
		["EZslider", "Part hue", ControlSpec(0,1), "pHue"],
		["EZslider", "Part sat.", ControlSpec(0,1), "pSaturation"],
		["EZslider", "Part br'ness", ControlSpec(0,1), "pBrightness"],
		["EZslider", "Part alpha", ControlSpec(0,1), "pAlpha"],
		["DividingLine"], 
		["EZslider", "Attraction", ControlSpec(-1,1), "pAttraction"],
		["EZslider", "Gravity", ControlSpec(-1,1), "pGravity"],
		["DividingLine"], 
		["EZslider", "X vel min", ControlSpec(-1,1), "pXMinVel"],
		["EZslider", "X vel max", ControlSpec(-1,1), "pXMaxVel"],
		["EZslider", "Y vel min", ControlSpec(-1,1), "pYMinVel"],
		["EZslider", "Y vel max", ControlSpec(-1,1), "pYMaxVel"],
		["EZslider", "Z vel min", ControlSpec(-1,1), "pZMinVel"],
		["EZslider", "Z vel max", ControlSpec(-1,1), "pZMaxVel"],
		];
	});

	if (displayOption == "show3", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "Sphere X pos", ControlSpec(0,1), "sXpos"],
		["EZslider", "Sphere Y pos", ControlSpec(0,1), "sYpos"],
		["EZslider", "Sphere scale ", ControlSpec(0,1), "sScale"],
		["EZslider", "Sphere hue", ControlSpec(0,1), "sHue"],
		["EZslider", "Sphere sat", ControlSpec(0,1), "sSaturation"],
		["EZslider", "Sph. br'ness", ControlSpec(0,1), "sBrightness"],
		["EZslider", "Sphere alpha", ControlSpec(0,1), "sAlpha"],
		["EZslider", "Lines hue", ControlSpec(0,1), "lHue"],
		["EZslider", "Lines sat", ControlSpec(0,1), "lSaturation"],
		["EZslider", "Lines br'ness", ControlSpec(0,1), "lLuminosity"],
		["EZslider", "Lines alpha", ControlSpec(0,1), "lAlpha"],
		["EZslider", "Lines length", ControlSpec(0,1), "lLength"],
		["EZslider", "Lines attract Y", ControlSpec(0,1), "lAttractionY"],
		];
	});
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
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
			holdQCView.path = this.class.filenameSymbol.asString.dirname ++ "/QCTX3Layer.qtz";
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


