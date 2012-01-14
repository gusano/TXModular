// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXEnvCurve : TXModuleBase {		// Audio In module 

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
	classvar	<guiWidth=950;
	classvar	<guiLeft=150;
	classvar	<guiTop=300;
	classvar	<arrBufferSpecs;
	
	var arrCurveValues;
	var arrSlotData;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Env Curve";
	moduleRate = "control";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumCurve", 700, 1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	var holdControlSpec;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["out", 0, 0],
		["bufnumCurve", 0, \ir],
		["note", 0, \ir],
		["velocity", 0, \ir],
		["envTotalTime", 3.0, \ir],
		["velocityScaling", 1, \ir],
		// N.B. the args below aren't used in the synthdef, just stored here for convenience
		["gridRows", 2],
		["gridCols", 2],
	]; 
	arrOptions = [0];
	arrOptionData = [
		[	["Positive only: 0 to 1", {arg input; input}],
			["Positive & Negative: -1 to 1", {arg input; (input * 2) - 1}],
			["Positive & Negative: -0.5 to 0.5", {arg input; input - 0.5}],
		],
	];
	synthDefFunc = { 
		arg out, bufnumCurve, note, velocity, envTotalTime, velocityScaling;
		var outCurve, rangeFunction, outSignal, endPointAdjust;
		// adjust endpoint so BufRd doesn't go back to start of buffer
		endPointAdjust = 699/700;
		outCurve = BufRd.kr(1, bufnumCurve, 
			Line.kr(0, 700, envTotalTime, endPointAdjust, doneAction: 2));
		// select function based on arrOptions
		rangeFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		// amplitude is vel *  0.007874 approx. == 1 / 127
		outSignal = rangeFunction.value(
			outCurve * ((velocity * 0.007874) + (1-velocityScaling)).min(1)
		);
		Out.kr(out, outSignal);
	};
	holdControlSpec = ControlSpec(0.001, 100, \exp, 0, 1, units: " Hz");
	guiSpecArray = [
		["TXCurveDraw", "Env curve", {arrCurveValues}, 
			{arg view; arrCurveValues = view.value; arrSlotData = view.arrSlotData; 
				this.bufferStore(view.value);}, 
			{arrSlotData}, nil, 700, 250, "Sine", "gridRows", "gridCols"], 
		["SpacerLine", 2], 
		["EZNumber", "Env Time", ControlSpec(0.01, 1600, \exp), "envTotalTime"],
		["Spacer", 20], 
		["ActionButton", "Trigger Envelope", {this.createSynthNote(60, 100, 0.1);}, 
			150, TXColor.white, TXColor.sysGuiCol2],
		["Spacer", 20], 
		["TXNumberPlusMinus", "Grid rows", ControlSpec(1, 99), "gridRows", {system.showView}],
		["TXNumberPlusMinus", "Grid columns", ControlSpec(1, 99), "gridCols", {system.showView}],
//		["DividingLine"], 
		["SpacerLine", 2], 
		["SynthOptionPopupPlusMinus", "Output range", arrOptionData, 0, 350], 
		["DividingLine"], 
		["SpacerLine", 2], 
		["MIDIListenCheckBox"], 
		["NextLine"], 
		["MIDIChannelSelector"], 
		["NextLine"], 
		["MIDINoteSelector"], 
		["NextLine"], 
		["MIDIVelSelector"], 
		["DividingLine"], 
		["SpacerLine", 2], 
		["TXCheckBox", "Scale level to velocity", "velocityScaling"], 
		["Spacer", 4], 
		["PolyphonySelector"], 
		["DividingLine"], 
	];
	arrActionSpecs = this.buildActionSpecs(
		[["commandAction", "Trigger Envelope", {this.createSynthNote(60, 100, 0.1);}]]
			++ guiSpecArray.deepCopy
	);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.setMonophonic;	// monophonic by default
	this.midiNoteInit;
	//	make the buffer, load the synthdef and create the group
	this.makeBuffersAndGroup(arrBufferSpecs);
	//	initialise buffer 
	arrCurveValues = Signal.sineFill(700, [1.0],[1.5pi]).collect({arg item, i; (item.value + 1) * 0.5;});

	Routine.run {
		var holdModCondition;
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		system.server.sync;
		// buffer store
		this.bufferStore(arrCurveValues);
		// remove condition from load queue
		system.holdLoadQueue.removeCondition(holdModCondition);
	};
	//	initialise slots to linear curves
	arrSlotData = arrCurveValues.dup(5);
}


bufferStore { arg argArray;
	buffers.at(0).sendCollection(argArray); 
}

extraSaveData { // override default method
	^[arrCurveValues, arrSlotData];
}

loadExtraData {arg argData;  // override default method
	arrCurveValues = argData.at(0);
	Routine.run {
		var holdModCondition;
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		system.server.sync;
		// buffer store
		this.bufferStore(arrCurveValues);
		// remove condition from load queue
		system.holdLoadQueue.removeCondition(holdModCondition);
	};
	arrSlotData = argData.at(1);
}

}

