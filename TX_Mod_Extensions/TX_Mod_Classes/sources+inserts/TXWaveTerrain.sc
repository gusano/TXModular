// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWaveTerrain : TXModuleBase {

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
	classvar	<arrBufferSpecs;

	var	arrOrbitPresets;
	var	arrTerrainPresets;
	var terrainCodeString;
	var orbitCodeString;
	var displayOption;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Wave Terrain";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Frequency", 1, "modFreq", 0],
		["Modify 1", 1, "modChange1", 0],
		["Modify 2", 1, "modChange2", 0],
		["Modify 3", 1, "modChange3", 0],
		["Modify 4", 1, "modChange4", 0],
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumTerrain", (256 * 256), 1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	create presets 
	arrTerrainPresets =[
		[	"Default",
			{this.evaluateTerrainCode(
"// Terrain - Default
{arg x, y; (x-y) * (x-1) * (x+1) * (y-1) * (y+1); }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Experimental 1",
			{this.evaluateTerrainCode(
"// Terrain - Experimental 1
{arg x, y; 2*(((x)**2) + ((abs(sin(10*y)))**(1/3)))-1; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Experimental 2",
			{this.evaluateTerrainCode(
"// Terrain - Experimental 2
{arg x, y; (((cos(5*x+1.7))**3) - ((abs(sin(23*y)))**(1/3))); }"); 
			this.buildGuiSpecArray; system.showView;},
		],
	];
	arrOrbitPresets =[
		[	"Ellipse",
			{this.evaluateOrbitCode(
"// Orbit - Ellipse
// uses Frequency & Modify 1 & 2
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * cos(t); 
	y = mod2.max(0.001) * sin(t); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Astroid",
			{this.evaluateOrbitCode(
"// Orbit - Astroid
// uses Frequency & Modify 1 & 2
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * cos(cos(cos(t))); 
	y = mod2.max(0.001) * sin(sin(sin(t))); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Spiral",
			{this.evaluateOrbitCode(
"// Orbit - Spiral
// uses Frequency & Modify 1
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * t * cos(t); 
	y = mod1.max(0.001) * t * sin(t); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Mix 1",
			{this.evaluateOrbitCode(
"// Orbit - Mix 1
// uses Frequency & Modify 1 & 2 & 3 & 4
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * sin(mod3 * t);  
	y = mod2.max(0.001) * sin(mod4 * t); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Mix 2",
			{this.evaluateOrbitCode(
"// Orbit - Mix 2
// uses Frequency & Modify 1 & 2 & 3 & 4
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * cos(mod3 * t); 
	y = mod2.max(0.001) * cos(mod4 * t); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Mix 3",
			{this.evaluateOrbitCode(
"// Orbit - Mix 3
// uses Frequency & Modify 1
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * sin(t); 
	y = (sin(t) + 1) * tan (t); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
		[	"Mix 4",
			{this.evaluateOrbitCode(
"// Orbit - Mix 4
// uses Frequency & Modify 1 & 2 & 3 & 4
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.clip(0.02,0.99) * sin(mod3.clip(0.01,0.99) * t) 
		* cos(mod4.clip(0.01,0.99) * t);  
	y = mod2.clip(0.02,0.99) * sin(mod3.clip(0.01,0.99) * t) 
		* sin(mod4.clip(0.01,0.99) * t); 
	[x,y]; }"); 
			this.buildGuiSpecArray; system.showView;},
		],
	];
	//	set  class specific instance variables
	displayOption = "showControls";

	terrainCodeString = "// Terrain - Default
{arg x, y; (x-y) * (x-1) * (x+1) * (y-1) * (y+1); }";

	orbitCodeString = "// Orbit - Ellipse
// uses Frequency & Modify 1 & 2
{ arg freq, mod1, mod2, mod3, mod4;
	var x, y, t;
	t = Saw.ar(freq).range(0, 2pi);
	x = mod1.max(0.001) * cos(t); 
	y = mod2.max(0.001) * sin(t); 
	[x,y]; }";

	arrSynthArgSpecs = [
		["out", 0, 0],
		["bufnumTerrain", 0, \ir],
		["freq", 0.5, defLagTime],
		["freqMin", 0.midicps, defLagTime],
		["freqMax", 127.midicps, defLagTime],
		["change1", 0.5, defLagTime],
		["change1Min", 0, defLagTime],
		["change1Max", 1, defLagTime],
		["change2", 0.5, defLagTime],
		["change2Min", 0, defLagTime],
		["change2Max", 1, defLagTime],
		["change3", 0.5, defLagTime],
		["change3Min", 0, defLagTime],
		["change3Max", 1, defLagTime],
		["change4", 0.5, defLagTime],
		["change4Min", 0, defLagTime],
		["change4Max", 1, defLagTime],
		["modFreq", 0, defLagTime],
		["modChange1", 0, defLagTime],
		["modChange2", 0, defLagTime],
		["modChange3", 0, defLagTime],
		["modChange4", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg out, bufnumTerrain, freq, freqMin, freqMax, 
			change1, change1Min, change1Max, change2, change2Min, change2Max,
			change3, change3Min, change3Max, change4, change4Min, change4Max, 
			modFreq = 0, modChange1 = 0, modChange2 = 0, modChange3 = 0, modChange4 = 0;
		var outFreq, outChange1, outChange2, outChange3, outChange4, outXY;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outChange1 = change1Min + ((change1Max - change1Min) * (change1 + modChange1).max(0).min(1));
		outChange2 = change2Min + ((change2Max - change2Min) * (change2 + modChange2).max(0).min(1));
		outChange3 = change3Min + ((change3Max - change3Min) * (change3 + modChange3).max(0).min(1));
		outChange4 = change4Min + ((change4Max - change4Min) * (change4 + modChange4).max(0).min(1));
		outXY = orbitCodeString.compile.value.value(outFreq, outChange1, outChange2, outChange3, outChange4);
		// use TXClean to stop blowups
		Out.ar(out, TXClean.ar(
			LeakDC.ar(WaveTerrain.ar(bufnumTerrain, outXY.at(0), outXY.at(1), 256, 256), 0.995);
		));
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["TXMinMaxSliderSplit", "Modify 1", \unipolar, "change1", "change1Min", "change1Max"], 
		["TXMinMaxSliderSplit", "Modify 2", \unipolar, "change2", "change2Min", "change2Max"], 
		["TXMinMaxSliderSplit", "Modify 3", \unipolar, "change3", "change3Min", "change3Max"], 
		["TXMinMaxSliderSplit", "Modify 4", \unipolar, "change4", "change4Min", "change4Max"], 
	]);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
	Routine.run {
		var holdModCondition;
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		system.server.sync;
		// buffer store
		this.bufferStore;
		// remove condition from load queue
		system.holdLoadQueue.removeCondition(holdModCondition);
	};
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Orbit Controls", {displayOption = "showControls"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showControls")], 
		["Spacer", 3], 
		["ActionButton", "Orbit", {displayOption = "showOrbit"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showOrbit")], 
		["Spacer", 3], 
		["ActionButton", "Terrain", {displayOption = "showTerrain"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showTerrain")], 
		["Spacer", 3], 
		["SpacerLine", 4], 
	];
	if (displayOption == "showControls", {
		guiSpecArray = guiSpecArray ++[
			["TXMinMaxFreqNoteSldr", "Freq", ControlSpec(0.midicps, 20000, \exponential), 
				"freq", "freqMin", "freqMax", nil, TXWaveform5.arrFreqRanges], 
			["SpacerLine", 4], 
			["TXMinMaxSliderSplit", "Modify 1", \unipolar, "change1", "change1Min", "change1Max"], 
			["SpacerLine", 4], 
			["TXMinMaxSliderSplit", "Modify 2", \unipolar, "change2", "change2Min", "change2Max"], 
			["SpacerLine", 4], 
			["TXMinMaxSliderSplit", "Modify 3", \unipolar, "change3", "change3Min", "change3Max"], 
			["SpacerLine", 4], 
			["TXMinMaxSliderSplit", "Modify 4", \unipolar, "change4", "change4Min", "change4Max"], 
		];
	});
	if (displayOption == "showOrbit", {
		guiSpecArray = guiSpecArray ++[	
			["TXPresetPopup", "Presets", 
				arrOrbitPresets.collect({arg item, i; item.at(0)}), 
				arrOrbitPresets.collect({arg item, i; item.at(1)})
			],
			["SpacerLine", 1], 
			["TextViewDisplay", "Orbit Coding Notes: The Supercollider 3 code below should be a function which is passed Frequency & Modify 1-4, and returns an array of audio rate UGens as X & Y values between 0 and 1.", 400, 50, "Notes"],
			["TextViewCompile", orbitCodeString, {arg argText; this.evaluateOrbitCode(argText);}, 400, 300],
		];
	});
	if (displayOption == "showTerrain", {
		guiSpecArray = guiSpecArray ++[	
			["TXPresetPopup", "Presets", 
				arrTerrainPresets.collect({arg item, i; item.at(0)}), 
				arrTerrainPresets.collect({arg item, i; item.at(1)})
			],
			["SpacerLine", 1], 
			["TextViewDisplay", "Terrain Coding Notes: The Supercollider 3 code below should be a function which is passed arguments of X value and Y value, and returns a single sample value between 0 and 1.", 400, 50, "Notes"],
			["TextViewCompile", terrainCodeString, {arg argText; this.evaluateTerrainCode(argText);}, 400, 300],
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

evaluateTerrainCode {arg argText; 
	var compileResult, holdText;
	holdText = argText.deepCopy;
	compileResult = holdText.compile;
	if (compileResult.isNil, {
		TXInfoScreen.new("ERROR: code will not compile - see post window ");
	},{
		terrainCodeString = holdText;
		this.bufferStore;
	});
	this.rebuildSynth;
}

evaluateOrbitCode {arg argText; 
	var compileResult, holdText;
	holdText = argText.copy;
	compileResult = holdText.compile;
	if (compileResult.isNil, {
		TXInfoScreen.new("ERROR: code will not compile - see post window ");
	},{
		orbitCodeString = holdText;
		this.bufferStore;
	});
	this.rebuildSynth;
}

bufferStore { 
	var holdArray, funcNormaliseAbsMax;
	var width= 256; //= num cols
	var height=256; //=num rows, though indexing bottom to top. i.e., standard Cartesian co-ords
	var holdCodeFunction;
		
	holdCodeFunction = terrainCodeString.compile;
	holdArray = Array.fill(width * height,{
		arg i; 
		var xnow, ynow, x, y; 
		xnow = i%width;
		ynow = (i-xnow).div(width);	
		x = xnow/width;
		y = ynow/height;	
		holdCodeFunction.value.value(x,y);
	});
	// make array bipolar
	funcNormaliseAbsMax = {	
		arg inputArray;
		var maxVal;
		maxVal = inputArray.abs.reduce({ |a, b| max(a, b) });
		inputArray/maxVal;
	};
	holdArray = funcNormaliseAbsMax.value(holdArray);
	buffers.at(0).loadCollection(holdArray); 
}

extraSaveData { // override default method
	^[terrainCodeString, orbitCodeString];
}

loadExtraData {arg argData;  // override default method
	terrainCodeString = argData.at(0);
	orbitCodeString = argData.at(1);
}

}

