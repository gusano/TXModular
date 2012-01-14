// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXQCPlayer : TXModuleBase {		// Quartz Composition Player

/*
this needs to be able to open any QC file and display it on a window of any size. 
It needs to address up to 200 parameters, including enables, with changable control specs for every parameter, with defaults of: initVal: 0, minVal: -1, maxVal: 1
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
	
	var	p01, p02, p03, p04, p05, p06, p07, p08, p09, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, 
		p21, p22, p23, p24, p25, p26, p27, p28, p29, p30, p31, p32, p33, p34, p35, p36, p37, p38, p39, p40;
	var	holdQCWindow, holdQCView;
	var	screenWidth, screenHeight;
	var arrResps, arrInputs, arrSendTrigIDs, arrQCArgData, arrNumArgNames;
	var displayOption;
	var holdScreenSizes;
	var holdScreenSizeTexts;
	var holdSSPresetActions;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "QC Particles";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 2;			
	arrCtlSCInBusSpecs = [ 		
		["p01", 1, "modp01", 0],
		["p02", 1, "modp02", 0],
		["p03", 1, "modp03", 0],
		["p04", 1, "modp04", 0],
		["p05", 1, "modp05", 0],
		["p06", 1, "modp06", 0],
		["p07", 1, "modp07", 0],
		["p08", 1, "modp08", 0],
		["p09", 1, "modp09", 0],
		["p10", 1, "modp10", 0],
		["p11", 1, "modp11", 0],
		["p12", 1, "modp12", 0],
		["p13", 1, "modp13", 0],
		["p14", 1, "modp14", 0],
		["p15", 1, "modp15", 0],
		["p16", 1, "modp16", 0],
		["p17", 1, "modp17", 0],
		["p18", 1, "modp18", 0],
		["p19", 1, "modp19", 0],
		["p20", 1, "modp20", 0],
		["p21", 1, "modp21", 0],
		["p22", 1, "modp22", 0],
		["p23", 1, "modp23", 0],
		["p24", 1, "modp24", 0],
		["p25", 1, "modp25", 0],
		["p26", 1, "modp26", 0],
		["p27", 1, "modp27", 0],
		["p28", 1, "modp28", 0],
		["p29", 1, "modp29", 0],
		["p30", 1, "modp30", 0],
		["p31", 1, "modp31", 0],
		["p32", 1, "modp32", 0],
		["p33", 1, "modp33", 0],
		["p34", 1, "modp34", 0],
		["p35", 1, "modp35", 0],
		["p36", 1, "modp36", 0],
		["p37", 1, "modp37", 0],
		["p38", 1, "modp38", 0],
		["p39", 1, "modp39", 0],
		["p40", 1, "modp40", 0],		
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

	arrQCArgData = [0, "", 0, 0, 1, 0.5, 0.5, 0.5, 1].dup(40);   
	//  array of : [argDataType [0.notPassed, 1.float, 2.integer, 3.string, 4.booleanVal(0/1), 5.colour(HSBA)], 
	//        argStringVal, argNumVal, argMin, argMax, argHue, argSaturation, argBrightness, argAlpha

	arrResps = [p01, p02, p03, p04, p05, p06, p07, p08, p09, p10, p11, p12, p13, p14, p15, 
	p16, p17, p18, p19, p20, p21, p22, p23, p24, p25, p26, p27, p28, p29, p30, p31, p32, 
	p33, p34, p35, p36, p37, p38, p39, p40];

	arrInputs = ['p01', 'p02', 'p03', 'p04', 'p05', 'p06', 'p07', 'p08', 'p09', 'p10', 'p11', 
	'p12', 'p13', 'p14', 'p15', 'p16', 'p17', 'p18', 'p19', 'p20', 'p21', 'p22', 'p23', 'p24', 
	'p25', 'p26', 'p27', 'p28', 'p29', 'p30', 'p31', 'p32', 'p33', 'p34', 'p35', 'p36', 'p37', 
	'p38', 'p39', 'p40'];

/* xxxxx
	N.B. arrInputs should be set when qtz file has been opened and its inputs queried.

*/
	arrSendTrigIDs = [];
	// create unique ids
	40.do({arg item, i;
		arrSendTrigIDs = arrSendTrigIDs.add(UniqueID.next);
	});

	screenWidth = 1024;
	screenHeight = 768;

	arrSynthArgSpecs = [
		["out", 0, 0],
		["active", 1, 0],
		["i_screenWidth", 1024, 0],
		["i_screenHeight", 768, 0],

		["p01", 0, defLagTime],
		["p02", 0, defLagTime],
		["p03", 0, defLagTime],
		["p04", 0, defLagTime],
		["p05", 0, defLagTime],
		["p06", 0, defLagTime],
		["p07", 0, defLagTime],
		["p08", 0, defLagTime],
		["p09", 0, defLagTime],
		["p10", 0, defLagTime],
		["p11", 0, defLagTime],
		["p12", 0, defLagTime],
		["p13", 0, defLagTime],
		["p14", 0, defLagTime],
		["p15", 0, defLagTime],
		["p16", 0, defLagTime],
		["p17", 0, defLagTime],
		["p18", 0, defLagTime],
		["p19", 0, defLagTime],
		["p20", 0, defLagTime],
		["p21", 0, defLagTime],
		["p22", 0, defLagTime],
		["p23", 0, defLagTime],
		["p24", 0, defLagTime],
		["p25", 0, defLagTime],
		["p26", 0, defLagTime],
		["p27", 0, defLagTime],
		["p28", 0, defLagTime],
		["p29", 0, defLagTime],
		["p30", 0, defLagTime],
		["p31", 0, defLagTime],
		["p32", 0, defLagTime],
		["p33", 0, defLagTime],
		["p34", 0, defLagTime],
		["p35", 0, defLagTime],
		["p36", 0, defLagTime],
		["p37", 0, defLagTime],
		["p38", 0, defLagTime],
		["p39", 0, defLagTime],
		["p40", 0, defLagTime],

		["modp01", 0, defLagTime],
		["modp02", 0, defLagTime],
		["modp03", 0, defLagTime],
		["modp04", 0, defLagTime],
		["modp05", 0, defLagTime],
		["modp06", 0, defLagTime],
		["modp07", 0, defLagTime],
		["modp08", 0, defLagTime],
		["modp09", 0, defLagTime],
		["modp10", 0, defLagTime],
		["modp11", 0, defLagTime],
		["modp12", 0, defLagTime],
		["modp13", 0, defLagTime],
		["modp14", 0, defLagTime],
		["modp15", 0, defLagTime],
		["modp16", 0, defLagTime],
		["modp17", 0, defLagTime],
		["modp18", 0, defLagTime],
		["modp19", 0, defLagTime],
		["modp20", 0, defLagTime],
		["modp21", 0, defLagTime],
		["modp22", 0, defLagTime],
		["modp23", 0, defLagTime],
		["modp24", 0, defLagTime],
		["modp25", 0, defLagTime],
		["modp26", 0, defLagTime],
		["modp27", 0, defLagTime],
		["modp28", 0, defLagTime],
		["modp29", 0, defLagTime],
		["modp30", 0, defLagTime],
		["modp31", 0, defLagTime],
		["modp32", 0, defLagTime],
		["modp33", 0, defLagTime],
		["modp34", 0, defLagTime],
		["modp35", 0, defLagTime],
		["modp36", 0, defLagTime],
		["modp37", 0, defLagTime],
		["modp38", 0, defLagTime],
		["modp39", 0, defLagTime],
		["modp40", 0, defLagTime],
	]; 
	
	synthDefFunc = { 
		arg out, active, i_screenWidth, i_screenHeight, p01, p02, p03, p04, p05, p06, p07, p08, p09, p10, p11, 
			p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22, p23, p24, p25, p26, p27, p28, p29, 
			p30, p31, p32, p33, p34, p35, p36, p37, p38, p39, p40, 
			modp01, modp02, modp03, modp04, modp05, modp06, modp07, modp08, modp09, modp10, modp11, modp12, 
			modp13, modp14, modp15, modp16, modp17, modp18, modp19, modp20, modp21, modp22, modp23, modp24, 
			modp25, modp26, modp27, modp28, modp29, modp30, modp31, modp32, modp33, modp34, modp35, modp36, 
			modp37, modp38, modp39, modp40;
	   var imp, trig01, trig02, trig03, trig04, trig05, trig06, trig07, trig08, trig09, trig10, trig11, 
		   	trig12, trig13, trig14, trig15, trig16, trig17, trig18, trig19, trig20, trig21, trig22, trig23, 
		   	trig24, trig25, trig26, trig27, trig28, trig29, trig30, trig31, trig32, trig33, trig34, trig35, 
		   	trig36, trig37, trig38, trig39, trig40; 
	   var holdp01, holdp02, holdp03, holdp04, holdp05, holdp06, holdp07, holdp08, holdp09, holdp10, holdp11, 
	  	 	holdp12, holdp13, holdp14, holdp15, holdp16, holdp17, holdp18, holdp19, holdp20, holdp21, holdp22,
	  	 	 holdp23,  holdp24, holdp25, holdp26, holdp27, holdp28, holdp29, holdp30, holdp31, holdp32, 
	  	 	 holdp33, holdp34, holdp35, holdp36, holdp37, holdp38, holdp39, holdp40;

	   imp = Impulse.kr(20) * active;
	   
	   holdp01 = (p01 + modp01).max(0).min(1);
	   holdp02 = (p02 + modp02).max(0).min(1);
	   holdp03 = (p03 + modp03).max(0).min(1);
	   holdp04 = (p04 + modp04).max(0).min(1);
	   holdp05 = (p05 + modp05).max(0).min(1);
	   holdp06 = (p06 + modp06).max(0).min(1);
	   holdp07 = (p07 + modp07).max(0).min(1);
	   holdp08 = (p08 + modp08).max(0).min(1);
	   holdp09 = (p09 + modp09).max(0).min(1);
	   holdp10 = (p10 + modp10).max(0).min(1);
	   holdp11 = (p11 + modp11).max(0).min(1);
	   holdp12 = (p12 + modp12).max(0).min(1);
	   holdp13 = (p13 + modp13).max(0).min(1);
	   holdp14 = (p14 + modp14).max(0).min(1);
	   holdp15 = (p15 + modp15).max(0).min(1);
	   holdp16 = (p16 + modp16).max(0).min(1);
	   holdp17 = (p17 + modp17).max(0).min(1);
	   holdp18 = (p18 + modp18).max(0).min(1);
	   holdp19 = (p19 + modp19).max(0).min(1);
	   holdp20 = (p20 + modp20).max(0).min(1);
	   holdp21 = (p21 + modp21).max(0).min(1);
	   holdp22 = (p22 + modp22).max(0).min(1);
	   holdp23 = (p23 + modp23).max(0).min(1);
	   holdp24 = (p24 + modp24).max(0).min(1);
	   holdp25 = (p25 + modp25).max(0).min(1);
	   holdp26 = (p26 + modp26).max(0).min(1);
	   holdp27 = (p27 + modp27).max(0).min(1);
	   holdp28 = (p28 + modp28).max(0).min(1);
	   holdp29 = (p29 + modp29).max(0).min(1);
	   holdp30 = (p30 + modp30).max(0).min(1);
	   holdp31 = (p31 + modp31).max(0).min(1);
	   holdp32 = (p32 + modp32).max(0).min(1);
	   holdp33 = (p33 + modp33).max(0).min(1);
	   holdp34 = (p34 + modp34).max(0).min(1);
	   holdp35 = (p35 + modp35).max(0).min(1);
	   holdp36 = (p36 + modp36).max(0).min(1);
	   holdp37 = (p37 + modp37).max(0).min(1);
	   holdp38 = (p38 + modp38).max(0).min(1);
	   holdp39 = (p39 + modp39).max(0).min(1);
	   holdp40 = (p40 + modp40).max(0).min(1);
	   

	   trig01 = Trig.kr(imp * HPZ1.kr(holdp01).abs, 0.01);
	   trig02 = Trig.kr(imp * HPZ1.kr(holdp02).abs, 0.01);
	   trig03 = Trig.kr(imp * HPZ1.kr(holdp03).abs, 0.01);
	   trig04 = Trig.kr(imp * HPZ1.kr(holdp04).abs, 0.01);
	   trig05 = Trig.kr(imp * HPZ1.kr(holdp05).abs, 0.01);
	   trig06 = Trig.kr(imp * HPZ1.kr(holdp06).abs, 0.01);
	   trig07 = Trig.kr(imp * HPZ1.kr(holdp07).abs, 0.01);
	   trig08 = Trig.kr(imp * HPZ1.kr(holdp08).abs, 0.01);
	   trig09 = Trig.kr(imp * HPZ1.kr(holdp09).abs, 0.01);
	   trig10 = Trig.kr(imp * HPZ1.kr(holdp10).abs, 0.01);
	   trig11 = Trig.kr(imp * HPZ1.kr(holdp11).abs, 0.01);
	   trig12 = Trig.kr(imp * HPZ1.kr(holdp12).abs, 0.01);
	   trig13 = Trig.kr(imp * HPZ1.kr(holdp13).abs, 0.01);
	   trig14 = Trig.kr(imp * HPZ1.kr(holdp14).abs, 0.01);
	   trig15 = Trig.kr(imp * HPZ1.kr(holdp15).abs, 0.01);
	   trig16 = Trig.kr(imp * HPZ1.kr(holdp16).abs, 0.01);
	   trig17 = Trig.kr(imp * HPZ1.kr(holdp17).abs, 0.01);
	   trig18 = Trig.kr(imp * HPZ1.kr(holdp18).abs, 0.01);
	   trig19 = Trig.kr(imp * HPZ1.kr(holdp19).abs, 0.01);
	   trig20 = Trig.kr(imp * HPZ1.kr(holdp20).abs, 0.01);
	   trig21 = Trig.kr(imp * HPZ1.kr(holdp21).abs, 0.01);
	   trig22 = Trig.kr(imp * HPZ1.kr(holdp22).abs, 0.01);
	   trig23 = Trig.kr(imp * HPZ1.kr(holdp23).abs, 0.01);
	   trig24 = Trig.kr(imp * HPZ1.kr(holdp24).abs, 0.01);
	   trig25 = Trig.kr(imp * HPZ1.kr(holdp25).abs, 0.01);
	   trig26 = Trig.kr(imp * HPZ1.kr(holdp26).abs, 0.01);
	   trig27 = Trig.kr(imp * HPZ1.kr(holdp27).abs, 0.01);
	   trig28 = Trig.kr(imp * HPZ1.kr(holdp28).abs, 0.01);
	   trig29 = Trig.kr(imp * HPZ1.kr(holdp29).abs, 0.01);
	   trig30 = Trig.kr(imp * HPZ1.kr(holdp30).abs, 0.01);
	   trig31 = Trig.kr(imp * HPZ1.kr(holdp31).abs, 0.01);
	   trig32 = Trig.kr(imp * HPZ1.kr(holdp32).abs, 0.01);
	   trig33 = Trig.kr(imp * HPZ1.kr(holdp33).abs, 0.01);
	   trig34 = Trig.kr(imp * HPZ1.kr(holdp34).abs, 0.01);
	   trig35 = Trig.kr(imp * HPZ1.kr(holdp35).abs, 0.01);
	   trig36 = Trig.kr(imp * HPZ1.kr(holdp36).abs, 0.01);
	   trig37 = Trig.kr(imp * HPZ1.kr(holdp37).abs, 0.01);
	   trig38 = Trig.kr(imp * HPZ1.kr(holdp38).abs, 0.01);
	   trig39 = Trig.kr(imp * HPZ1.kr(holdp39).abs, 0.01);
	   trig40 = Trig.kr(imp * HPZ1.kr(holdp40).abs, 0.01);

	   SendTrig.kr(trig01, arrSendTrigIDs.at(0), holdp01);
	   SendTrig.kr(trig02, arrSendTrigIDs.at(1), holdp02);
	   SendTrig.kr(trig03, arrSendTrigIDs.at(2), holdp03);
	   SendTrig.kr(trig04, arrSendTrigIDs.at(3), holdp04);
	   SendTrig.kr(trig05, arrSendTrigIDs.at(4), holdp05);
	   SendTrig.kr(trig06, arrSendTrigIDs.at(5), holdp06);
	   SendTrig.kr(trig07, arrSendTrigIDs.at(6), holdp07);
	   SendTrig.kr(trig08, arrSendTrigIDs.at(7), holdp08);
	   SendTrig.kr(trig09, arrSendTrigIDs.at(8), holdp09);
	   SendTrig.kr(trig10, arrSendTrigIDs.at(9), holdp10);
	   SendTrig.kr(trig11, arrSendTrigIDs.at(10), holdp11);
	   SendTrig.kr(trig12, arrSendTrigIDs.at(11), holdp12);
	   SendTrig.kr(trig13, arrSendTrigIDs.at(12), holdp13);
	   SendTrig.kr(trig14, arrSendTrigIDs.at(13), holdp14);
	   SendTrig.kr(trig15, arrSendTrigIDs.at(14), holdp15);
	   SendTrig.kr(trig16, arrSendTrigIDs.at(15), holdp16);
	   SendTrig.kr(trig17, arrSendTrigIDs.at(16), holdp17);
	   SendTrig.kr(trig18, arrSendTrigIDs.at(17), holdp18);
	   SendTrig.kr(trig19, arrSendTrigIDs.at(18), holdp19);
	   SendTrig.kr(trig20, arrSendTrigIDs.at(19), holdp20);
	   SendTrig.kr(trig21, arrSendTrigIDs.at(20), holdp21);
	   SendTrig.kr(trig22, arrSendTrigIDs.at(21), holdp22);
	   SendTrig.kr(trig23, arrSendTrigIDs.at(22), holdp23);
	   SendTrig.kr(trig24, arrSendTrigIDs.at(23), holdp24);
	   SendTrig.kr(trig25, arrSendTrigIDs.at(24), holdp25);
	   SendTrig.kr(trig26, arrSendTrigIDs.at(25), holdp26);
	   SendTrig.kr(trig27, arrSendTrigIDs.at(26), holdp27);
	   SendTrig.kr(trig28, arrSendTrigIDs.at(27), holdp28);
	   SendTrig.kr(trig29, arrSendTrigIDs.at(28), holdp29);
	   SendTrig.kr(trig30, arrSendTrigIDs.at(29), holdp30);
	   SendTrig.kr(trig31, arrSendTrigIDs.at(30), holdp31);
	   SendTrig.kr(trig32, arrSendTrigIDs.at(31), holdp32);
	   SendTrig.kr(trig33, arrSendTrigIDs.at(32), holdp33);
	   SendTrig.kr(trig34, arrSendTrigIDs.at(33), holdp34);
	   SendTrig.kr(trig35, arrSendTrigIDs.at(34), holdp35);
	   SendTrig.kr(trig36, arrSendTrigIDs.at(35), holdp36);
	   SendTrig.kr(trig37, arrSendTrigIDs.at(36), holdp37);
	   SendTrig.kr(trig38, arrSendTrigIDs.at(37), holdp38);
	   SendTrig.kr(trig39, arrSendTrigIDs.at(38), holdp39);
	   SendTrig.kr(trig40, arrSendTrigIDs.at(39), holdp40);

	   // Note this synth doesn't need to write to the output bus
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Show Video Screen", {this.rebuildQCScreen;}],

	holdScreenSizes = [ [640, 480], [720, 480], [800, 500], [800, 600], [1024, 640], [1024, 768], 
		[1152, 720], [1280, 800], [1440, 900]
	];
	holdScreenSizeTexts = holdScreenSizes.collect({arg item, i; item.at(0).asString + "X" + item.at(1).asString});
	
	holdSSPresetActions = holdScreenSizes.collect({arg item, i;
		{	this.setSynthArgSpec("i_screenWidth", item.at(0));
			this.setSynthArgSpec("i_screenHeight", item.at(1));
			this.resetScreenSize; 
			this.oscActivate;
		}
	});

		["EZNumber", "Screen width", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenWidth", 
			{this.resetScreenSize; this.oscActivate;}],
		["EZNumber", "Screen height", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenHeight", 
			{this.resetScreenSize; this.oscActivate;}],
		["TXPresetPopup", "Presets", holdScreenSizeTexts, holdSSPresetActions, 150],
		["EZslider", "P01", ControlSpec(0,1), "p01"],
// <------------------- need to replace this TXQCArgGui controls 1-10, 11-20, etc. --------->
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

		["EZNumber", "Screen width", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenWidth", 
			{this.resetScreenSize; this.oscActivate;}],
		["EZNumber", "Screen height", ControlSpec(0, 10000, 'lin', 1, 0), "i_screenHeight", 
			{this.resetScreenSize; this.oscActivate;}],
		["NextLine"], 
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
		["Spacer", 3], 
		["ActionButton", "settings 4", {displayOption = "show4"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "show4")], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "show1", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "P01", ControlSpec(0,1), "p01"],

		// synth args: 0-"TXQCArgGui", 1-parmIndex, 2-arrayOfData, arrayOfNumArgNames
		["TXQCArgGui", 1, arrQCArgData, arrNumArgNames, ],   
		
		
// <------------------- need to replace this TXQCArgGui controls 1-10, 11-20, etc. --------->
		
// N.B. TXQCArgGui needs to be written


		];
	});
	if (displayOption == "show2", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "P11", ControlSpec(0,1), "p11"],
		];
	});
	if (displayOption == "show3", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "P21", ControlSpec(0,1), "p21"],
		];
	});
	if (displayOption == "show4", {
		guiSpecArray = guiSpecArray ++[
		["EZslider", "P31", ControlSpec(0,1), "p31"],
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
			holdQCView.path = this.class.filenameSymbol.asString.dirname ++ "/Particles2.qtz";
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
	screenWidth = this.getSynthArgSpec("i_screenWidth");
	screenHeight = this.getSynthArgSpec("i_screenWidth");
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

extraSaveData {	
	^arrQCArgData;
}

loadExtraData {arg argData;  // override default method
	arrQCArgData = argData; 
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


