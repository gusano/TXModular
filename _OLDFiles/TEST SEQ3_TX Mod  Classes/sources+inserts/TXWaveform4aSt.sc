// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWaveform4aSt : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=350;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Waveform St";
	moduleRate = "audio";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Modify 1", 1, "modChange1", 0],
		["Modify 2", 1, "modChange2", 0],
		["Frequency", 1, "modFreq", 0],
		["Beats frequency", 1, "modBeatsFreq", 0],
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

*arrFreqRanges {
	^ [
		["Presets: ", [40, 127.midicps]],
		["MIDI Note Range 8.17 - 12543 hz", [0.midicps, 127.midicps]],
		["Full Audible range 40 - 20k hz", [40, 20000]],
		["Wide range 40 - 8k hz", [40, 8000]],
		["Low range 40 - 250 hz", [40, 250]],
		["Mid range 100 - 2k hz", [100, 2000]],
		["High range 1k - 6k hz", [1000, 6000]],
	];
}
init {arg argInstName;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["out", 0, 0],
		["freq", 0.5, defLagTime],
		["freqMin", 0.midicps, defLagTime],
		["freqMax", 127.midicps, defLagTime],
		["beatsFreq", 0.2, defLagTime],
		["beatsFreqMin", 0, defLagTime],
		["beatsFreqMax", 10, defLagTime],
		["change1", 0.5, defLagTime],
		["change1Min", 0, defLagTime],
		["change1Max", 1, defLagTime],
		["change2", 0.5, defLagTime],
		["change2Min", 0, defLagTime],
		["change2Max", 1, defLagTime],
		["level", 0.5, defLagTime],
		["modFreq", 0, defLagTime],
		["modBeatsFreq", 0, defLagTime],
		["modChange1", 0, defLagTime],
		["modChange2", 0, defLagTime],
	]; 
	arrOptions = [0];
	arrOptionData = [TXWaveForm.arrOptionData];
	synthDefFunc = { arg out, freq, freqMin, freqMax, beatsFreq, beatsFreqMin, beatsFreqMax,
		change1, change1Min, change1Max, change2, change2Min, change2Max, level, 
		modFreq, modBeatsFreq, modChange1, modChange2;
		var outFreq, outBeatsFreq, outFunction, outChange1, outChange2, outVol;
		outFreq = ( (freqMax/freqMin) ** ((freq + modFreq).max(0.001).min(1)) ) * freqMin;
		outBeatsFreq = beatsFreqMin + ((beatsFreqMax - beatsFreqMin) * (beatsFreq + modBeatsFreq).max(0).min(1));
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outChange1 = change1Min + ((change1Max - change1Min) * (change1 + modChange1).max(0).min(1));
		outChange2 = change2Min + ((change2Max - change2Min) * (change2 + modChange2).max(0).min(1));
		outVol = EnvGen.kr(Env.new([0, 0, level], [0.1,0.1]), 1);
		// use TXClean to stop blowups
		Out.ar(out, [
			TXClean.ar(outVol * outFunction.value(outFreq + (outBeatsFreq / 2), outChange1, outChange2)),
			TXClean.ar(outVol * outFunction.value(outFreq - (outBeatsFreq / 2), outChange1, outChange2)),
		]);
	};
	guiSpecArray = [
		["SynthOptionPopupPlusMinus", "Waveform", arrOptionData, 0, nil, {system.flagGuiUpd}], 
		["TextViewDisplay", {TXWaveForm.arrDescriptions.at(arrOptions.at(0).asInteger);}],
		["TXMinMaxSliderSplit", "Modify 1", \unipolar, "change1", "change1Min", "change1Max"], 
		["TXMinMaxSliderSplit", "Modify 2", \unipolar, "change2", "change2Min", "change2Max"], 
		["DividingLine"],
		["TXMinMaxFreqNoteSldr", "Freq", ControlSpec(0.midicps, 20000, \exponential), 
			"freq", "freqMin", "freqMax", nil, this.class.arrFreqRanges], 
		["TXMinMaxSliderSplit", "Beats Freq", ControlSpec(0,100), 
			"beatsFreq", "beatsFreqMin", "beatsFreqMax"], 
		["DividingLine"],
		["EZslider", "Level", ControlSpec(0, 1), "level"], 
		["DividingLine"],
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
}

}

