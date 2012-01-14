// Copyright (C) 2008  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXQCPlayer3 : TXModuleBase {		// Quartz Composition Player

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
	classvar	arrPStrings, arrModPStrings, arrActivePStrings;
	classvar	maxParameters = 80;	// allow for 80 input parameters for a QC composition
	
	var	<>qcFileNameView;
	var	<>qcFileName = "";
	var	arrPResponders;
	var	holdQCWindow, holdQCView;
	var	screenWidth, screenHeight;
	var	screenPosX, screenPosY;
	var arrInputs, arrSendTrigIDs, arrQCArgData, arrNumArgNames;
	var displayOption;
	var holdScreenSizes;
	var holdScreenSizeTexts;
	var holdSSPresetActions;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Quartz Player";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 2;
	
	arrPStrings = maxParameters.collect({arg item, i; "p" ++ ("00" ++ item.asString).keep(-3)});
	arrModPStrings = arrPStrings.collect({arg item, i; "mod" ++ item});
	arrActivePStrings = arrPStrings.collect({arg item, i; "i_active" ++ item});
	arrCtlSCInBusSpecs = arrModPStrings.collect({arg item, i; ["(not used", 1, item, 0]});

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
	displayOption = "show0";
	arrInputs = [];

	arrQCArgData = [0, "", 0, 1, 0.5, 0.5, 0.5, 1, 0].dup(maxParameters);   
	//  array of :  argDataType, argStringVal, argMin, argMax, argRed, argGreen, argBlue, argAlpha, argNumVal
	//  argDataType can be: [0.notPassed, 1.float, 2.integer, 3.string, 4.booleanNum(0/1), 5.colour(HSBA)], 

	arrSendTrigIDs = [];
	// create unique ids
	maxParameters.do({arg item, i;
		arrSendTrigIDs = arrSendTrigIDs.add(UniqueID.next);
	});
	screenWidth = 1024;
	screenHeight = 768;
	screenPosX = 0;
	screenPosY = 580;

	arrSynthArgSpecs = 
		[	["out", 0, 0],
			["i_screenWidth", 1024, 0],
			["i_screenHeight", 768, 0],
			["i_posX", 0, 0],
			["i_posY", 60, 0],
			["i_maxFPS", 0, 0],
			["i_border", 1, 0],
		]
		++ arrActivePStrings.collect({arg item, i; [item, 0, 0]})
		++ arrPStrings.collect({arg item, i; [item, 0, 0]})
		++ arrModPStrings.collect({arg item, i; [item, 0, 0]});

	synthDefFunc = { 
		arg out, i_screenWidth, i_screenHeight, i_posX, i_posY, i_maxFPS, i_border, 
i_activep000, i_activep001, i_activep002, i_activep003, i_activep004, i_activep005, i_activep006, i_activep007, i_activep008, i_activep009, i_activep010, i_activep011, i_activep012, i_activep013, i_activep014, i_activep015, i_activep016, i_activep017, i_activep018, i_activep019, i_activep020, i_activep021, i_activep022, i_activep023, i_activep024, i_activep025, i_activep026, i_activep027, i_activep028, i_activep029, i_activep030, i_activep031, i_activep032, i_activep033, i_activep034, i_activep035, i_activep036, i_activep037, i_activep038, i_activep039, i_activep040, i_activep041, i_activep042, i_activep043, i_activep044, i_activep045, i_activep046, i_activep047, i_activep048, i_activep049, i_activep050, i_activep051, i_activep052, i_activep053, i_activep054, i_activep055, i_activep056, i_activep057, i_activep058, i_activep059, i_activep060, i_activep061, i_activep062, i_activep063, i_activep064, i_activep065, i_activep066, i_activep067, i_activep068, i_activep069, i_activep070, i_activep071, i_activep072, i_activep073, i_activep074, i_activep075, i_activep076, i_activep077, i_activep078, i_activep079,
p000, p001, p002, p003, p004, p005, p006, p007, p008, p009, p010, p011, p012, p013, p014, p015, p016, p017, p018, p019, p020, p021, p022, p023, p024, p025, p026, p027, p028, p029, p030, p031, p032, p033, p034, p035, p036, p037, p038, p039, p040, p041, p042, p043, p044, p045, p046, p047, p048, p049, p050, p051, p052, p053, p054, p055, p056, p057, p058, p059, p060, p061, p062, p063, p064, p065, p066, p067, p068, p069, p070, p071, p072, p073, p074, p075, p076, p077, p078, p079, 
modp000, modp001, modp002, modp003, modp004, modp005, modp006, modp007, modp008, modp009, modp010, modp011, modp012, modp013, modp014, modp015, modp016, modp017, modp018, modp019, modp020, modp021, modp022, modp023, modp024, modp025, modp026, modp027, modp028, modp029, modp030, modp031, modp032, modp033, modp034, modp035, modp036, modp037, modp038, modp039, modp040, modp041, modp042, modp043, modp044, modp045, modp046, modp047, modp048, modp049, modp050, modp051, modp052, modp053, modp054, modp055, modp056,  modp057, modp058, modp059, modp060, modp061, modp062, modp063, modp064, modp065, modp066, modp067, modp068, modp069, modp070, modp071, modp072, modp073, modp074, modp075, modp076, modp077, modp078, modp079;

	   	var arrParmArgs, arrModParmArgs, arrActiveArgs;
	   	var arrParmSums, arrTrigs, arrSendTrigs, imp;

	   	arrParmArgs = [
p000, p001, p002, p003, p004, p005, p006, p007, p008, p009, p010, p011, p012, p013, p014, p015, p016, p017, p018, p019, p020, p021, p022, p023, p024, p025, p026, p027, p028, p029, p030, p031, p032, p033, p034, p035, p036, p037, p038, p039, p040, p041, p042, p043, p044, p045, p046, p047, p048, p049, p050, p051, p052, p053, p054, p055, p056, p057, p058, p059, p060, p061, p062, p063, p064, p065, p066, p067, p068, p069, p070, p071, p072, p073, p074, p075, p076, p077, p078, p079];
	   
	   arrModParmArgs = [
modp000, modp001, modp002, modp003, modp004, modp005, modp006, modp007, modp008, modp009, modp010, modp011, modp012, modp013, modp014, modp015, modp016, modp017, modp018, modp019, modp020, modp021, modp022, modp023, modp024, modp025, modp026, modp027, modp028, modp029, modp030, modp031, modp032, modp033, modp034, modp035, modp036, modp037, modp038, modp039, modp040, modp041, modp042, modp043, modp044, modp045, modp046, modp047, modp048, modp049, modp050, modp051, modp052, modp053, modp054, modp055, modp056,  modp057, modp058, modp059, modp060, modp061, modp062, modp063, modp064, modp065, modp066, modp067, modp068, modp069, modp070, modp071, modp072, modp073, modp074, modp075, modp076, modp077, modp078, modp079];
	   
	   arrActiveArgs = [
i_activep000, i_activep001, i_activep002, i_activep003, i_activep004, i_activep005, i_activep006, i_activep007, i_activep008, i_activep009, i_activep010, i_activep011, i_activep012, i_activep013, i_activep014, i_activep015, i_activep016, i_activep017, i_activep018, i_activep019, i_activep020, i_activep021, i_activep022, i_activep023, i_activep024, i_activep025, i_activep026, i_activep027, i_activep028, i_activep029, i_activep030, i_activep031, i_activep032, i_activep033, i_activep034, i_activep035, i_activep036, i_activep037, i_activep038, i_activep039, i_activep040, i_activep041, i_activep042, i_activep043, i_activep044, i_activep045, i_activep046, i_activep047, i_activep048, i_activep049, i_activep050, i_activep051, i_activep052, i_activep053, i_activep054, i_activep055, i_activep056, i_activep057, i_activep058, i_activep059, i_activep060, i_activep061, i_activep062, i_activep063, i_activep064, i_activep065, i_activep066, i_activep067, i_activep068, i_activep069, i_activep070, i_activep071, i_activep072, i_activep073, i_activep074, i_activep075, i_activep076, i_activep077, i_activep078, i_activep079];
	   

		arrParmSums = arrParmArgs.collect({arg item, i; 
			 arrActiveArgs.at(i) * (item + arrModParmArgs.at(i)).max(0).min(1);
		});

		imp = (1 - Impulse.kr(20)); 
		
		arrTrigs = arrParmSums.collect({arg item, i; 
			 Trig.kr(imp * HPZ1.kr(item).abs, 0.02);
		});

		arrSendTrigs = arrTrigs.collect({arg item, i; 
			SendTrig.kr(Impulse.kr(1) + item, arrSendTrigIDs.at(i), arrParmSums.at(i));
		});
		
	   // Note this synth doesn't need to write to the output bus
	};
	// End of synth def function

	this.buildGuiSpecArray;
	holdScreenSizes = [ [640, 480], [720, 480], [800, 500], [800, 600], [1024, 640], [1024, 768], 
		[1152, 720], [1280, 800], [1440, 900]
	];
	holdScreenSizeTexts = holdScreenSizes.collect({arg item, i; item.at(0).asString + "X" + item.at(1).asString});
	
	holdSSPresetActions = holdScreenSizes.collect({arg item, i;
		{	this.setSynthArgSpec("i_screenWidth", item.at(0));
			this.setSynthArgSpec("i_screenHeight", item.at(1));
			screenWidth = item.at(0);
			screenHeight = item.at(1);
			this.resetScreenSize; 
			this.oscActivate;
		}
	});
	this.setActionSpecs;

	// initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
	this.resetScreenSize;
	this.oscActivate;

}

buildGuiSpecArray {
	if (qcFileName == "", {
		guiSpecArray = [
			["TextBar", "Please select Quartz Composer file to be displayed", 450],
			["SpacerLine", 6], 
			["ActionButtonBig", "Open file", {this.openQCComp; system.showView}], 
		];
	}, {
		guiSpecArray = [
			["SpacerLine", 6], 
			["ActionButton", "Global", {displayOption = "show0"; 
				this.buildGuiSpecArray; system.showView;}, 60, 
				TXColor.white, this.getButtonColour(displayOption == "show0")], 
			["Spacer", 3], 
			["ActionButton", "1", {displayOption = "show1"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show1")], 
			["Spacer", 3], 
			["ActionButton", "2", {displayOption = "show2"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show2")], 
			["Spacer", 3], 
			["ActionButton", "3", {displayOption = "show3"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show3")], 
			["Spacer", 3], 
			["ActionButton", "4", {displayOption = "show4"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show4")], 
			["Spacer", 3], 
			["ActionButton", "5", {displayOption = "show5"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show5")], 
			["Spacer", 3], 
			["ActionButton", "6", {displayOption = "show6"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show6")], 
			["Spacer", 3], 
			["ActionButton", "7", {displayOption = "show7"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show7")], 
			["Spacer", 3], 
			["ActionButton", "8", {displayOption = "show8"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show8")], 
			["Spacer", 3], 
			["ActionButton", "9", {displayOption = "show9"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show9")], 
			["Spacer", 3], 
			["ActionButton", "10", {displayOption = "show10"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
				TXColor.white, this.getButtonColour(displayOption == "show10")], 
			["DividingLine"], 
			["SpacerLine", 6], 
		];
		if (displayOption == "show0", {
			guiSpecArray = guiSpecArray ++[

				["ActionButtonBig", "Show Quartz Window", {this.rebuildQCScreen;{this.checkQCFileExists;}.defer(0.6);}, 150], 
				["ActionButtonBig", "Close Quartz Window", 
					{{if (holdQCWindow.notNil) {holdQCWindow.close};}.defer}, 150, nil, TXColour.sysDeleteCol], 
				["DividingLine"], 
				["SpacerLine", 2], 
				["TXStaticText", "Quartz File", {this.qcFileName.keep(-50)}, {arg view; qcFileNameView = view.textView}], 
				["SpacerLine", 2], 
				["ActionButtonDark", "Replace current Quartz file", {
					// close current window first
					if (holdQCWindow.notNil) {holdQCWindow.close};
					this.openQCComp; 
					system.showView;
					}, 160], 
				["DividingLine"], 
				["SpacerLine", 2], 
				["EZNumber", "Window width", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenWidth", 
					{arg view; screenWidth = view.value; this.resetScreenSize; this.oscActivate;}],
				["EZNumber", "Wind. height", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenHeight", 
					{arg view; screenHeight = view.value; this.resetScreenSize; this.oscActivate;}],
	 			["TXPresetPopup", "Presets", holdScreenSizeTexts, holdSSPresetActions, 110],
				["DividingLine"], 
				["SpacerLine", 2], 
				["EZNumber", "Wind. pos X", ControlSpec(0, 10000, 'lin', 1, 0), "i_posX", 
					{arg view; screenPosX = view.value; this.resetScreenSize; this.oscActivate;}],
				["EZNumber", "Wind. pos Y", ControlSpec(0, 10000, 'lin', 1, 0), "i_posY", 
					{arg view; screenPosY = view.value; this.resetScreenSize; this.oscActivate;}],
				["SpacerLine", 2], 
				["ActionButton", "Sample Current Position", {
						var holdRect;
						if (holdQCWindow.notNil, {
							holdRect = holdQCWindow.bounds;
							screenPosX = holdRect.left; 
							screenPosY = holdRect.top;
							this.setSynthValue("i_posX", screenPosX); 
							this.setSynthValue("i_posY", screenPosY);
							system.flagGuiUpd;
						});
					}, 160], 
				["ActionButton", "Default Window Position", {arg view; 
						screenPosX = 0; screenPosY = 60; 
						this.setSynthValue("i_posX", 0); this.setSynthValue("i_posY", 60);
						this.resetScreenSize; this.oscActivate;
						system.flagGuiUpd;
					}, 160, TXColor.white, TXColor.sysGuiCol2], 
				["DividingLine"], 
				["SpacerLine", 2], 
				["EZNumber", "Max FPS", ControlSpec(0, 80, 'lin', 1, 0), "i_maxFPS", 
					{arg view; this.setFPS(view.value);}],
				["TextBarLeft", "set to 0 for unrestricted Frames Per Second", 250],
				["DividingLine"], 
				["SpacerLine", 2], 
				["TXCheckBox", "Visible border", "i_border", {this.rebuildQCScreen; }],
				["DividingLine"], 
				["SpacerLine", 2], 
			];
		});
	
			// add controls for valid inputs
	
			// TXQCArgGuiarguments: 
			// 	index1 is text
			//	index2 is synth arg name to be updated for the number
			// 	index3 is array of all module arguments
			// 	index4 is argument index no
			// 	index5 is set argument value function
			// e.g. ["TXQCArgGui", "Particle Hue", "p003", arrQCArgData, 4, setArgValFunc],

		if (displayOption == "show1", {
			(0..7).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, {this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show2", {
			(8..15).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show3", {
			(16..23).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show4", {
			(24..31).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show5", {
			(32..39).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show6", {
			(40..47).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show7", {
			(48..55).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show8", {
			(56..63).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show9", {
			(64..71).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
		if (displayOption == "show10", {
			(72..79).do({arg item, i; 
				if (item < arrInputs.size, {
					guiSpecArray = guiSpecArray.add( ["TXQCArgGui", arrInputs.at(item).asString, arrPStrings.at(item), 
						arrActivePStrings.at(item), arrQCArgData, item, 
						{this.sendArgValue(item);}])
				});
			});
		});
	});

}

setActionSpecs {
	var arrSpecs;
	
	arrSpecs = [
		["commandAction", "Show Window", {this.rebuildQCScreen;}],
		["EZNumber", "Window width", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenWidth", 
			{arg view; screenWidth = view.value; this.resetScreenSize; this.oscActivate;}],
		["EZNumber", "Wind. height", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenHeight", 
			{arg view; screenHeight = view.value; this.resetScreenSize; this.oscActivate;}],
 		["TXPresetPopup", "Presets", holdScreenSizeTexts, holdSSPresetActions, 20],
		["EZNumber", "Wind. Pos X", ControlSpec(0, 10000, 'lin', 1, 0), "i_posX", 
			{arg view; screenPosX = view.value; this.resetScreenSize; this.oscActivate;}],
		["EZNumber", "Wind. Pos Y", ControlSpec(0, 10000, 'lin', 1, 0), "i_posY", 
			{arg view; screenPosY = view.value; this.resetScreenSize; this.oscActivate;}],
		["commandAction", "Sample Current Position", {
			var holdRect;
			if (holdQCWindow.notNil, {
				holdRect = holdQCWindow.bounds;
				screenPosX = holdRect.left; 
				screenPosY = holdRect.top;
				system.flagGuiUpd;
			});
		}],
		["commandAction", "Default Window Position", {arg view; 
			screenPosX = 0; screenPosY = 60; 
			this.setSynthValue("i_posX", 0); this.setSynthValue("i_posY", 60);
			this.resetScreenSize; this.oscActivate;
			this.sendCurrentValues;
			system.flagGuiUpd;
		}],
		["TXCheckBox", "Visible border", "i_border", {this.rebuildQCScreen; }],
	];
	
			// TXQCArgGuiarguments: 
		// 	index1 is text
		//	index2 is synth arg name to be updated for the number
		//	index3 is synth arg name to be updated for the active number setting
		// 	index4 is array of all module arguments
		// 	index5 is argument index no
		// 	index6 is set argument value function
		// e.g. ["TXQCArgGui", "Particle Hue", "p003", arrQCArgData, 4, setArgValFunc],
	arrInputs.do({arg item, i; 
		arrSpecs = arrSpecs.add( ["TXQCArgGui", arrInputs.at(i).asString, arrPStrings.at(i), 
						arrActivePStrings.at(i), arrQCArgData, i, {this.sendArgValue(i);}]);
	});
	arrActionSpecs = this.buildActionSpecs(arrSpecs);
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
}

showQCScreen { arg argPath;
	var holdBorder, holdPath;
	if ( (qcFileName == "" and: argPath.isNil).not, {   // only show screen if valid path
		{
			//	check if window exists 
			if (holdQCWindow.isNil) {

				if (argPath.notNil, {
					holdPath = argPath;
				},{
					holdPath = qcFileName;
				});
				if (File.exists(holdPath), {
					// make window
					if (this.getSynthArgSpec("i_border") == 1, {holdBorder = true;}, {holdBorder = false;});
					holdQCWindow= SCWindow(" ", Rect(screenPosX, screenPosY, screenWidth, screenHeight), border: holdBorder).front; 
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
				
					holdQCView.path = holdPath;
					holdQCView.start;
					arrInputs = holdQCView.inputKeys;
					this.buildGuiSpecArray;
					this.setActionSpecs;
					// replace names in arrCtlSCInBusChoices
					arrInputs.do({arg item, i; this.arrCtlSCInBusChoices.at(i).put(0, item.asString);});
				});
			}{
				// if window exists bring to front
				holdQCWindow.front;
			};
		}.defer;
	});
}

rebuildQCScreen { 
	{	//	check if window exists 
		if (holdQCWindow.notNil) {holdQCWindow.close};
	}.defer;
	{
		this.showQCScreen;
	}.defer(0.2);
	{
		this.sendCurrentValues;
	}.defer(0.6);
}

resetScreenSize { 
	screenWidth = this.getSynthArgSpec("i_screenWidth");
	screenHeight = this.getSynthArgSpec("i_screenHeight");
	this.rebuildQCScreen;
}

oscActivate {
	{ // defer to allow for screen building
		//	remove any previous OSCresponderNodes and add new
		this.oscDeactivate;
		arrInputs.do({arg item, i;
			var newResp, holdArgType, holdMin, holdMax, holdVal;
			
			//	only build responders for active inputs
			holdArgType = arrQCArgData.at(i).at(0);
			
			if ( (holdArgType == 1) or: (holdArgType == 2) or: (holdArgType == 4), {
				newResp = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
						if (msg[2] == arrSendTrigIDs.at(i),{
							{	holdMin = arrQCArgData.at(i).at(2);
								holdMax = arrQCArgData.at(i).at(3);
								if (holdArgType == 1, {
									holdVal =  holdMin + (msg[3] * (holdMax - holdMin));
								});
								if (holdArgType == 2, {
									holdVal =  (holdMin + (msg[3] * (holdMax - holdMin))).round;
								});
								if (holdArgType == 4, {
									// convert to value to boolean
									holdVal =  (msg[3].round > 0 );
								});
								if (holdQCView.notNil,  {
									holdQCView.setInputValue(arrInputs.at(i), holdVal);
								});
							}.defer;
						});
					}).add;
				arrPResponders = arrPResponders.add(newResp);
			});
		});	
	}.defer(0.5);
}

oscDeactivate { 
	//	remove responders 
	arrPResponders.do({arg item, i;
		item.remove;
	});
}

extraSaveData {	
	^[arrQCArgData, qcFileName, screenWidth, screenHeight, screenPosX, screenPosY];
}

loadExtraData {arg argData;  // override default method
	arrQCArgData = argData.at(0); 
	qcFileName = argData.at(1); 
	screenWidth = argData.at(2); 
	screenHeight = argData.at(3); 
	screenPosX = argData.at(4); 
	screenPosY = argData.at(5); 
	this.buildGuiSpecArray;
	this.setActionSpecs;
	this.oscActivate;
	{this.resetScreenSize;}.defer(0.5);
	{this.checkQCFileExists;}.defer(0.6);
}

deleteModuleExtraActions {     
	//	remove responders
	this.oscDeactivate;
	if (holdQCWindow.notNil) {
		// if window exists close it
		holdQCWindow.close;
	};
}

checkQCFileExists {
	if (File.exists(qcFileName).not, {
		// if invalid filename
		TXInfoScreen.new(
			"Error: Could not open Quartz Composer file: ",
			arrInfoLines: ["File not found:  " ++ qcFileName]
		);
	});
}

openQCComp {	
	var firstPath;
	// get path/filename
	CocoaDialog.getPaths({ arg paths;
		firstPath = paths.at(0);
		// check for valid file
		if (this.isValidQCFile(firstPath), {
			// assign name
			qcFileName = firstPath;
			if (qcFileNameView.notNil, {
				if (qcFileNameView.notClosed, {qcFileNameView.string = qcFileName;});
			});
			// reset various things
			this.buildGuiSpecArray;
			this.setActionSpecs;
			system.showView;
			this.showQCScreen(firstPath);
		}); 
	});
}

isValidQCFile {arg argPath;
	var extension;
	extension = argPath.splitext.at(1);
	^((extension.size == 3) and: extension.containsi("qtz"));
}

setFPS {arg argFPS;
	holdQCView.maxFPS_(argFPS);
}

sendCurrentValues {
	arrInputs.do({arg item, i; 
		{this.setSynthValue(arrPStrings.at(i), 1);}.defer(0.35);
	});
//	arrInputs.do({arg item, i; 
//		{this.setSynthValue(arrPStrings.at(i), 0);}.defer(0.4);
//		});
//	
	{ 	arrInputs.do({arg item, i; 
			var holdMin, holdMax, holdNum;
				holdMin = arrQCArgData.at(i).at(2);
				holdMax = arrQCArgData.at(i).at(3);
				holdNum = arrQCArgData.at(i).at(8);
				this.setSynthValue(arrPStrings.at(i), [holdMin, holdMax].asSpec.unmap(holdNum));
		});
	}.defer(0.5);
		
	{	arrInputs.do({arg item, i; this.sendArgValue(i);});
	}.defer(0.6);
}

sendArgValue {arg argIndex;
	var holdArgs, holdVal;
	var argDataType, argStringVal, argMin, argMax, argRed, argGreen, argBlue, argAlpha, argNumVal;
	holdArgs = arrQCArgData.at(argIndex);
	argDataType = holdArgs.at(0);
	argStringVal = holdArgs.at(1);
	argMin = holdArgs.at(2);
	argMax = holdArgs.at(3);
	argRed = holdArgs.at(4);
	argGreen = holdArgs.at(5);
	argBlue = holdArgs.at(6);
	argAlpha = holdArgs.at(7);
	argNumVal = holdArgs.at(8);

	//  array of :  argDataType, argStringVal, argMin, argMax, argRed, argGreen, argBlue, argAlpha
	//  argDataType can be: [0.notPassed, 1.float, 2.integer, 3.string, 4.booleanNum(0/1), 5.colour(HSBA)], 

	if (argDataType == 1, {
		this.setSynthValue(arrPStrings.at(argIndex), [argMin, argMax].asSpec.unmap(argNumVal));
	});
	if (argDataType == 2, {
		this.setSynthValue(arrPStrings.at(argIndex), [argMin, argMax].asSpec.unmap(argNumVal.asInteger));
	});

	if ( (argDataType == 3) or: (argDataType == 6) or: (argDataType == 7), {
		holdVal = argStringVal;
		holdQCView.setInputValue(arrInputs.at(argIndex), holdVal);   
	});
	if (argDataType == 4, {
		this.setSynthValue(arrPStrings.at(argIndex), argNumVal);
	});
	if (argDataType == 5, {
		holdVal = Color.new(argRed, argGreen, argBlue, argAlpha);
		try {
			holdQCView.setInputValue(arrInputs.at(argIndex), holdVal);   
		};
	});
}

runAction {
	super.runAction;
	if (qcFileName != "", {
		holdQCView.start;
	});
}

pauseAction {
	super.pauseAction;
	if (qcFileName != "", {
		holdQCView.stop;
	});
}

}


