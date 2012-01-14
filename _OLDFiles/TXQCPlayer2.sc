// Copyright (C) 2008  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXQCPlayer2 : TXModuleBase {		// Quartz Composition Player

/*     CODING NOTES
       ============

//testing xxxx - 
//


QC Player - bugs issues:

- min and max aren't working properly

- show window co-ords on screen -default 0, 0 - which are updated when you drag the window, but also can be changed by hand

- when rebuilding the QC screen, need to remember window co-ords - e.g. if on 2nd monitor

- give user choice of borderless window (in same place as current window)

- change TXBuildActions to allow for :
["TXQCArgGui", item.asString, arrPStrings.at(i), arrQCArgData, i, {this.sendArgValue(i);}]

- colours needs to show a coloured box in window

- when pausing the synth, also pause the QC Comp (using .stop/ .start)

-------=-=-=-=-==----=-=-=-=-==----=-=-=-=-==----=-=-=-=-==----=-=-=-=-==----=-=-=-=-==----=-=-=-=-==-


order -
in order for action names to be valid always, need to force user to set QC composition before anything else. Then once set, cannot change without deleting module first.

Another Alternative?
could go for a different approach, using multiple synths that get created only when a QC input is numeric and therefore needs all the mod inputs and oscResponders.
the oscresponder should have them update the QC composition directly. 
for non numeric inputs, they are sent only when a value is changed either by gui widget action or by another action. 

Rough spec
this needs to be able to open any QC file and display it on a window of any size. 
It needs to address up to 80 parameters, including enables, with changable control specs for every parameter, with defaults of: initVal: 0, minVal: -1, maxVal: 1
Needs to be able to send floats, ints, booleans, colours, strings
*/
/*
From SCQuartzComposerView help:
QC compositions have typed input and output ports which can be accessed from within SC lang using keys which you specify within the composition. Instances of Float, Integer, Color, and Boolean (true and false) are supported SC objects for input and output. (Images are not supported at this time.) Arrays or IdentityDictionaries ('structures' in QC terminology) containing these types are also supported. N.B. Due to the way that structures are stored within a composition, structure outputs are always IdentityDictionaries. See the structure example below for more detail.
*/


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
	var arrInputs, arrSendTrigIDs, arrQCArgData, arrNumArgNames;
	var displayOption;
	var holdScreenSizes;
	var holdScreenSizeTexts;
	var holdSSPresetActions;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "QC Player";
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
	//  array of :  argDataType, argStringVal, argMin, argMax, argHue, argSaturation, argBrightness, argAlpha, argNumVal
	//  argDataType can be: [0.notPassed, 1.float, 2.integer, 3.string, 4.booleanNum(0/1), 5.colour(HSBA)], 

	arrSendTrigIDs = [];
	// create unique ids
	maxParameters.do({arg item, i;
		arrSendTrigIDs = arrSendTrigIDs.add(UniqueID.next);
	});
	screenWidth = 1024;
	screenHeight = 768;

	arrSynthArgSpecs = 
		[	["out", 0, 0],
			["i_screenWidth", 1024, 0],
			["i_screenHeight", 768, 0],
			["i_maxFPS", 0, 0],
			["t_sendVals", 0, 0],
		]
		++ arrActivePStrings.collect({arg item, i; [item, 0, 0]})
		++ arrPStrings.collect({arg item, i; [item, 0, defLagTime]})
		++ arrModPStrings.collect({arg item, i; [item, 0, defLagTime]});

	synthDefFunc = { 
		arg out, i_screenWidth, i_screenHeight, i_maxFPS, t_sendVals,
i_activep000, i_activep001, i_activep002, i_activep003, i_activep004, i_activep005, i_activep006, i_activep007, i_activep008, i_activep009, i_activep010, i_activep011, i_activep012, i_activep013, i_activep014, i_activep015, i_activep016, i_activep017, i_activep018, i_activep019, i_activep020, i_activep021, i_activep022, i_activep023, i_activep024, i_activep025, i_activep026, i_activep027, i_activep028, i_activep029, i_activep030, i_activep031, i_activep032, i_activep033, i_activep034, i_activep035, i_activep036, i_activep037, i_activep038, i_activep039, i_activep040, i_activep041, i_activep042, i_activep043, i_activep044, i_activep045, i_activep046, i_activep047, i_activep048, i_activep049, i_activep050, i_activep051, i_activep052, i_activep053, i_activep054, i_activep055, i_activep056, i_activep057, i_activep058, i_activep059, i_activep060, i_activep061, i_activep062, i_activep063, i_activep064, i_activep065, i_activep066, i_activep067, i_activep068, i_activep069, i_activep070, i_activep071, i_activep072, i_activep073, i_activep074, i_activep075, i_activep076, i_activep077, i_activep078, i_activep079,
p000, p001, p002, p003, p004, p005, p006, p007, p008, p009, p010, p011, p012, p013, p014, p015, p016, p017, p018, p019, p020, p021, p022, p023, p024, p025, p026, p027, p028, p029, p030, p031, p032, p033, p034, p035, p036, p037, p038, p039, p040, p041, p042, p043, p044, p045, p046, p047, p048, p049, p050, p051, p052, p053, p054, p055, p056, p057, p058, p059, p060, p061, p062, p063, p064, p065, p066, p067, p068, p069, p070, p071, p072, p073, p074, p075, p076, p077, p078, p079, 
modp000, modp001, modp002, modp003, modp004, modp005, modp006, modp007, modp008, modp009, modp010, modp011, modp012, modp013, modp014, modp015, modp016, modp017, modp018, modp019, modp020, modp021, modp022, modp023, modp024, modp025, modp026, modp027, modp028, modp029, modp030, modp031, modp032, modp033, modp034, modp035, modp036, modp037, modp038, modp039, modp040, modp041, modp042, modp043, modp044, modp045, modp046, modp047, modp048, modp049, modp050, modp051, modp052, modp053, modp054, modp055, modp056,  modp057, modp058, modp059, modp060, modp061, modp062, modp063, modp064, modp065, modp066, modp067, modp068, modp069, modp070, modp071, modp072, modp073, modp074, modp075, modp076, modp077, modp078, modp079;

	   	var arrParmArgs, arrModParmArgs, arrActiveArgs;
	   	var imp;
	   	var arrParmSums, arrTrigs, arrSendTrigs;
	   	
	   	imp = Impulse.kr(20);

	   	arrParmArgs = [
p000, p001, p002, p003, p004, p005, p006, p007, p008, p009, p010, p011, p012, p013, p014, p015, p016, p017, p018, p019, p020, p021, p022, p023, p024, p025, p026, p027, p028, p029, p030, p031, p032, p033, p034, p035, p036, p037, p038, p039, p040, p041, p042, p043, p044, p045, p046, p047, p048, p049, p050, p051, p052, p053, p054, p055, p056, p057, p058, p059, p060, p061, p062, p063, p064, p065, p066, p067, p068, p069, p070, p071, p072, p073, p074, p075, p076, p077, p078, p079];
	   
	   arrModParmArgs = [
modp000, modp001, modp002, modp003, modp004, modp005, modp006, modp007, modp008, modp009, modp010, modp011, modp012, modp013, modp014, modp015, modp016, modp017, modp018, modp019, modp020, modp021, modp022, modp023, modp024, modp025, modp026, modp027, modp028, modp029, modp030, modp031, modp032, modp033, modp034, modp035, modp036, modp037, modp038, modp039, modp040, modp041, modp042, modp043, modp044, modp045, modp046, modp047, modp048, modp049, modp050, modp051, modp052, modp053, modp054, modp055, modp056,  modp057, modp058, modp059, modp060, modp061, modp062, modp063, modp064, modp065, modp066, modp067, modp068, modp069, modp070, modp071, modp072, modp073, modp074, modp075, modp076, modp077, modp078, modp079];
	   
	   arrActiveArgs = [
i_activep000, i_activep001, i_activep002, i_activep003, i_activep004, i_activep005, i_activep006, i_activep007, i_activep008, i_activep009, i_activep010, i_activep011, i_activep012, i_activep013, i_activep014, i_activep015, i_activep016, i_activep017, i_activep018, i_activep019, i_activep020, i_activep021, i_activep022, i_activep023, i_activep024, i_activep025, i_activep026, i_activep027, i_activep028, i_activep029, i_activep030, i_activep031, i_activep032, i_activep033, i_activep034, i_activep035, i_activep036, i_activep037, i_activep038, i_activep039, i_activep040, i_activep041, i_activep042, i_activep043, i_activep044, i_activep045, i_activep046, i_activep047, i_activep048, i_activep049, i_activep050, i_activep051, i_activep052, i_activep053, i_activep054, i_activep055, i_activep056, i_activep057, i_activep058, i_activep059, i_activep060, i_activep061, i_activep062, i_activep063, i_activep064, i_activep065, i_activep066, i_activep067, i_activep068, i_activep069, i_activep070, i_activep071, i_activep072, i_activep073, i_activep074, i_activep075, i_activep076, i_activep077, i_activep078, i_activep079];
	   

		arrParmSums = arrParmArgs.collect({arg item, i; 

//removed 
//			 arrActiveArgs.at(i) * 
			 (item + arrModParmArgs.at(i)).max(0).min(1);
		});

		arrTrigs = arrParmSums.collect({arg item, i; 
			 Trig.kr(t_sendVals + (imp * HPZ1.kr(item).abs), 0.01);
		});

		arrSendTrigs = arrTrigs.collect({arg item, i; 
			SendTrig.kr(item, arrSendTrigIDs.at(i), arrParmSums.at(i));
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
			["ActionButton", "*", {displayOption = "show0"; 
				this.buildGuiSpecArray; system.showView;}, 30, 
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

				["TXStaticText", "QC File", {this.qcFileName.keep(-50)}, {arg view; qcFileNameView = view.textView}], 
				["DividingLine"], 
				["EZNumber", "Screen width", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenWidth", 
					{arg view; screenWidth = view.value; this.resetScreenSize; this.oscActivate;}],
				["EZNumber", "Screen height", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenHeight", 
					{arg view; screenHeight = view.value; this.resetScreenSize; this.oscActivate;}],
	 			["TXPresetPopup", "Presets", holdScreenSizeTexts, holdSSPresetActions, 20],
				["NextLine"], 
				["EZNumber", "Max FPS", ControlSpec(0, 80, 'lin', 1, 0), "i_maxFPS", 
					{arg view; this.setFPS(view.value);}],
				["TextBarLeft", "set to 0 for unrestricted Frames Per Second", 250],
				["NextLine"], 
				["ActionButton", "Show Video Screen", {this.rebuildQCScreen; this.sendCurrentValues;}, 200], 
				["DividingLine"], 
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
	var holdSpecs;
	
	holdSpecs = [
		["commandAction", "Show Video Screen", {this.rebuildQCScreen;this.sendCurrentValues;}],
		["EZNumber", "Screen width", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenWidth", 
			{arg view; screenWidth = view.value; this.resetScreenSize; this.oscActivate;}],
		["EZNumber", "Screen height", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenHeight", 
			{arg view; screenHeight = view.value; this.resetScreenSize; this.oscActivate;}],
 			["TXPresetPopup", "Presets", holdScreenSizeTexts, holdSSPresetActions, 20],
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
		holdSpecs = holdSpecs.add( ["TXQCArgGui", item.asString, arrPStrings.at(i), arrQCArgData, i, 
			{this.sendArgValue(i);}])
	});
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
}

showQCScreen { arg argPath;
	if ( (qcFileName == "" and: argPath.isNil).not, {   // only show screen if valid path
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
				
				if (argPath.notNil, {
					holdQCView.path = argPath;
				},{
					holdQCView.path = qcFileName;
				});
				holdQCView.start;
				arrInputs = holdQCView.inputKeys;
				this.buildGuiSpecArray;

				// replace names in arrCtlSCInBusChoices
				arrInputs.do({arg item, i; this.arrCtlSCInBusChoices.at(i).put(0, item.asString);});
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
}

resetScreenSize { 
	screenWidth = this.getSynthArgSpec("i_screenWidth");
	screenHeight = this.getSynthArgSpec("i_screenHeight");
	this.rebuildQCScreen;
	this.sendCurrentValues;
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
								if (holdArgType == 4, {
									holdVal =  msg[3];
								},{
									holdVal =  holdMin + (msg[3] * (holdMax - holdMin));
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
	^[arrQCArgData, qcFileName];
}

loadExtraData {arg argData;  // override default method
	arrQCArgData = argData.at(0); 
	qcFileName = argData.at(1); 
	this.buildGuiSpecArray;
	this.oscActivate;
	{this.resetScreenSize;}.defer(0.2);
//	{this.setSynthValue("t_sendVals", 1);}.defer(0.4);  // trigger synth to send values
	{this.sendCurrentValues;}.defer(0.6);
}

deleteModuleExtraActions {     
	//	remove responders
	this.oscDeactivate;
	if (holdQCWindow.notNil) {
		// if window exists close it
		holdQCWindow.close;
	};
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
		{this.setSynthValue(arrPStrings.at(i), 1);}.defer(0.35 + (i * 0.0));
	});
//	arrInputs.do({arg item, i; 
//		{this.setSynthValue(arrPStrings.at(i), 0);}.defer(0.4 + (i * 0.0));
//		});
//	
	arrInputs.do({arg item, i; 
		{	this.setSynthValue(arrPStrings.at(i), arrQCArgData.at(i).at(8));}.defer(0.45 + (i * 0.30));
		});
		
	
//	arrInputs.do({arg item, i; {this.sendArgValue(i);}.defer(0.4 + (i * 0.05));});
	
}

sendArgValue {arg argIndex;
	var holdArgs, holdVal;
	var argDataType, argStringVal, argMin, argMax, argHue, argSaturation, argBrightness, argAlpha, argNumVal;
	holdArgs = arrQCArgData.at(argIndex);
	argDataType = holdArgs.at(0);
	argStringVal = holdArgs.at(1);
	argMin = holdArgs.at(2);
	argMax = holdArgs.at(3);
	argHue = holdArgs.at(4);
	argSaturation = holdArgs.at(5);
	argBrightness = holdArgs.at(6);
	argAlpha = holdArgs.at(7);
	argNumVal = holdArgs.at(8);

	//  array of :  argDataType, argStringVal, argMin, argMax, argHue, argSaturation, argBrightness, argAlpha
	//  argDataType can be: [0.notPassed, 1.float, 2.integer, 3.string, 4.booleanNum(0/1), 5.colour(HSBA)], 

	if (argDataType == 1, {
		holdVal = argMin + (argNumVal * (argMax - argMin));
		this.setSynthValue(arrPStrings.at(argIndex), argNumVal)
	});
	if (argDataType == 2, {
		holdVal = argMin + (argNumVal * argMax);
		this.setSynthValue(arrPStrings.at(argIndex), argNumVal)
	});

	if (argDataType == 3, {
		holdVal = argStringVal;
		holdQCView.setInputValue(arrInputs.at(argIndex), holdVal);   
	});
	if (argDataType == 4, {
		holdVal = argNumVal;
		this.setSynthValue(arrPStrings.at(argIndex), argNumVal)
	});
	if (argDataType == 5, {
		holdVal = Color.hsv(argHue, argSaturation, argBrightness, argAlpha);
		holdQCView.setInputValue(arrInputs.at(argIndex), holdVal);   
	});

}

}


