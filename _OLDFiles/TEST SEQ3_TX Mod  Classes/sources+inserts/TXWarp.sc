// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWarp : TXModuleBase {		// Warp module 

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
	classvar	<arrBufferSpecs;
	
	var arrCurveValues;

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

init {arg argInstName;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumWarp", 0, \ir],
		["inputMin", 0, defLagTime], 
		["inputMax", 1, defLagTime],
		["outputMin", 0, defLagTime], 
		["outputMax", 1, defLagTime],
	]; 
	synthDefFunc = { arg in, out, bufnumWarp, inputMin, inputMax, outputMin, outputMax;
		var inLimit, scaleWarp, invWarp, curveWarp, outWarp;

		// apply limits
		inLimit = In.kr(in,1).max(inputMin).min(inputMax);	
		// scale to limits
		scaleWarp = (inLimit - inputMin)  / (inputMax - inputMin);	
		// apply curve by indexing into buffer
		curveWarp =  BufRd.kr(1, bufnumWarp, scaleWarp * 127, 0, 0);

		// map to o/p range
		outWarp = outputMin + (curveWarp * (outputMax - outputMin));

		Out.kr(out, outWarp);
	};
	guiSpecArray = [
		["TXRangeSlider", "Input range", \bipolar, "inputMin", "inputMax"], 
		["NextLine"], 
		["TXCurveDraw", "Warp curve", {arrCurveValues}, {arg view; arrCurveValues = view.value; this.bufferStore(view.value);}], 
		["TXRangeSlider", "Output range", \bipolar, "outputMin", "outputMax"], 
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make the buffer, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
	//	initialise buffer to linear morph
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
}

bufferStore { arg argArray;
	buffers.at(0).sendCollection(argArray); 
}

extraSaveData { // override default method
	^[arrCurveValues];
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
}

}

