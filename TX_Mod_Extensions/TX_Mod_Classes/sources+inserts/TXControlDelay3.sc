// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXControlDelay3 : TXModuleBase {		// delay module 

	//	Notes:
	//	This is a delay which can be set to any time up to 16 secs.
	//	This version uses BufCombC

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
	classvar	<maxDelaytime = 6;	//	delay time up to 6 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Control Delay";
	moduleRate = "control";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Delay Time", 1, "modDelay", 0],
		["Wet-Dry Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumDelay", defSampleRate * maxDelaytime, 1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	var holdControlSpec, holdControlSpec2;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	holdControlSpec = ControlSpec.new(10, 6000, \exp );
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumDelay", 0, \ir],
		["delay", holdControlSpec.unmap(1000), defLagTime],
		["delayMin", 10, defLagTime],
		["delayMax", 6000, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelay, delay=0.1, delayMin, delayMax, wetDryMix, modDelay=0, modWetDryMix=0;
		var input, outSignal, delaytime, decaytime, mixCombined;
		input = In.kr(in,1);
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay).max(0.0001).min(1)) ) * delayMin / 1000;
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		//	CombC.kr(in, maxdelaytime, delaytime, decaytime, mul, add)
		outSignal = BufDelayC.kr(bufnumDelay, input, delaytime, mixCombined, 
			input * (1-mixCombined));
		Out.kr(out, TXClean.kr(outSignal));
	};
	guiSpecArray = [
		["TextBarLeft", "Delay time shown in ms and bpm", 200],
		["Spacer", 3], 
		["ActionButton", "time x 2", {this.delayTimeMultiply(2);}, 60], 
		["ActionButton", "time x 3", {this.delayTimeMultiply(3);}, 60], 
		["ActionButton", "time / 2", {this.delayTimeMultiply(0.5);}, 60], 
		["ActionButton", "time / 3", {this.delayTimeMultiply(1/3);}, 60], 
		["NextLine"],
		["TXTimeBpmMinMaxSldr", "Delay time", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

delayTimeMultiply { arg argMultiplyValue;
	var currentTime, minTime, maxTime, holdControlSpec, newTime;
	minTime = this.getSynthArgSpec("delayMin");
	maxTime = this.getSynthArgSpec("delayMax");
	holdControlSpec = ControlSpec.new(minTime, maxTime, \exp);
	currentTime = holdControlSpec.map(this.getSynthArgSpec("delay"));
	newTime = currentTime * argMultiplyValue;
	if (argMultiplyValue < 1, {
		if ( newTime >= minTime, {
			this.setSynthValue("delay", holdControlSpec.unmap(newTime));
		});
	},{
		if ( newTime <= maxTime, {
			this.setSynthValue("delay", holdControlSpec.unmap(newTime));
		});
	});
	system.flagGuiIfModDisplay(this);
}
}

