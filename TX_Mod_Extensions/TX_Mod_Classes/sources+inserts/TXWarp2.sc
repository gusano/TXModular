// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWarp2 : TXModuleBase {		// Warp module 

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
	
	var arrCurveValues;
	var arrSlotData;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Warp";
	moduleRate = "control";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumWarp", 128, 1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

*arrOutputRanges {
	^ [
		["Presets: ", [0, 1]],
		["Full range -1 to 1", [-1, 1]],
		["Half range -0.5 to 0.5", [-0.5, 0.5]],
		["Positive range 0 to 1", [0, 1]],
		["Negative range -1 to 0", [-1, 0]],
	];
}

init {arg argInstName;
	//	set  class specific instance variables
	extraLatency = 0.05;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumWarp", 0, \ir],
		["inputMin", 0, defLagTime], 
		["inputMax", 1, defLagTime],
		["outputMin", 0, defLagTime], 
		["outputMax", 1, defLagTime],
		
		// N.B. the args below aren't used in the synthdef, just stored here for convenience
		["gridRows", 2],
		["gridCols", 2],		
	]; 
	synthDefFunc = { arg in, out, bufnumWarp, inputMin, inputMax, outputMin, outputMax;
		var inSignal, inLimit, scaleWarp, invWarp, curveWarp, outWarp;
		 
		inSignal = TXClean.kr(In.kr(in,1));

		// apply limits
		inLimit = inSignal.max(inputMin).min(inputMax);	
		// scale to limits
		scaleWarp = (inLimit - inputMin)  / (inputMax - inputMin);	
		// apply curve by indexing into buffer
		curveWarp =  BufRd.kr(1, bufnumWarp, scaleWarp * 127, 0, 0);

		// map to o/p range
		outWarp = outputMin + (curveWarp * (outputMax - outputMin));

		Out.kr(out, TXClean.kr(outWarp));
	};
	guiSpecArray = [
		["TXRangeSlider", "Input range", \bipolar, "inputMin", "inputMax", nil, 
			this.class.arrOutputRanges], 
		["SpacerLine", 2], 
		["TXCurveDraw", "Warp curve", {arrCurveValues}, 
			{arg view; arrCurveValues = view.value; arrSlotData = view.arrSlotData; 
				this.bufferStore(view.value);}, 
			{arrSlotData}, "Warp", nil, nil, nil, "gridRows", "gridCols" ], 
		["NextLine",], 
		["ActionButton", "Rebuild curve by mirroring ", {this.runMirror}, 234],
		["ActionButton", "Rebuild curve by mirroring & inverting", 
			{this.runMirrorInvert}, 234],
		["SpacerLine", 2], 
		["TXNumberPlusMinus", "Grid rows", ControlSpec(1, 99), "gridRows", {system.showView}],
		["TXNumberPlusMinus", "Grid columns", ControlSpec(1, 99), "gridCols", {system.showView}],
		["SpacerLine", 2], 
		["TXRangeSlider", "Output range", \bipolar, "outputMin", "outputMax", nil, this.class.arrOutputRanges], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make the buffer, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
	//	initialise buffer to linear curve
	arrCurveValues = Array.newClear(128).seriesFill(0, 1/127);
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

runMirror {
	var holdArray, startVal, midArray, endVal, holdSignal;
	holdArray = arrCurveValues.deepCopy;
	startVal = holdArray.first.asArray;
	midArray = holdArray.drop(2).drop(-2);
	midArray = midArray.clump(2).collect({arg item, i; item.sum/2;});
	endVal = holdArray.last.asArray;
	holdArray = startVal ++ midArray ++ endVal;
	holdArray = holdArray ++ holdArray.copy.reverse;
	holdSignal = Signal.newFrom(holdArray);
	arrCurveValues = Array.newFrom(holdSignal);
	this.bufferStore(arrCurveValues);
	system.showView;
}

runMirrorInvert {
	var holdArray, startVal, midArray, endVal, holdSignal;
	holdArray = arrCurveValues.deepCopy;
	startVal = holdArray.first.asArray;
	midArray = holdArray.drop(2).drop(-2);
	midArray = midArray.clump(2).collect({arg item, i; item.sum/2;});
	endVal = holdArray.last.asArray;
	holdArray = startVal ++ midArray ++ endVal;
	holdArray = (holdArray ++ holdArray.copy.reverse.neg).normalize(0,1);
	holdSignal = Signal.newFrom(holdArray);
	arrCurveValues = Array.newFrom(holdSignal);
	this.bufferStore(arrCurveValues);
	system.showView;
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

