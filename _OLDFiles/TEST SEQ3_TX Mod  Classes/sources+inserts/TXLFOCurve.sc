// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLFOCurve : TXModuleBase {		// Same as TXLFOCurve but with a longer curve 

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
	defaultName = "LFO Curve";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Frequency", 1, "modFreq", 0],
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
		["freq", 0.5, defLagTime],
		["freqMin", 0.01, defLagTime],
		["freqMax", 100, defLagTime],
		["modFreq", 0, defLagTime],
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
	synthDefFunc = { arg out, bufnumCurve, freq, freqMin, freqMax, modFreq = 0;
		var outFreq, outCurve, rangeFunction, outSignal;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outCurve = BufRd.kr(1, bufnumCurve, 
			Phasor.kr(0, outFreq * ControlDur.ir * 700, 0, 700));

		// select function based on arrOptions
		rangeFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outSignal = rangeFunction.value(outCurve);
		Out.kr(out, outSignal);
	};
	holdControlSpec = ControlSpec(0.001, 100, \exp, 0, 1, units: " Hz");
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Frequency", holdControlSpec, "freq", "freqMin", "freqMax",
			nil, TXLFO.arrLFOFreqRanges], 
		["SpacerLine", 2], 
		["TXCurveDraw", "LFO curve", {arrCurveValues}, 
			{arg view; arrCurveValues = view.value; arrSlotData = view.arrSlotData; 
				this.bufferStore(view.value);}, 
			{arrSlotData}, "LFO", 700, 340, nil, "gridRows", "gridCols"], 
		["ActionButton", "Rebuild curve by mirroring current values", {this.runMirror}, 350],
		["Spacer", 10], 
		["ActionButton", "Rebuild curve by mirroring & inverting current values", 
			{this.runMirrorInvert}, 350],
		["SpacerLine", 2], 
		["SynthOptionPopupPlusMinus", "Output range", arrOptionData, 0, 350], 
		["Spacer", 10], 
		["TXNumberPlusMinus", "Grid rows", ControlSpec(1, 99), "gridRows", {system.showView}],
		["Spacer", 10], 
		["TXNumberPlusMinus", "Grid columns", ControlSpec(1, 99), "gridCols", {system.showView}],
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make the buffer, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
	//	initialise buffer to linear curve
	arrCurveValues = Array.newClear(700).seriesFill(0, 1/699);
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

