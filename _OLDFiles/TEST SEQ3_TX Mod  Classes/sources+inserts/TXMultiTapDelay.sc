// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMultiTapDelay : TXModuleBase {		// MultiTap Delay 

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
	classvar	<guiHeight=200;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	<arrBufferSpecs;
	
	classvar	<maxDelaytime = 16;	//	delay time up to 16 secs.

	var	displayOption;
	var holdControlSpec;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "MultiTap Delay";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Delay Time", 1, "modDelay", 0],
		["Tap 1 Level", 1, "modTapLevel1", 0],
		["Tap 2 Level", 1, "modTapLevel2", 0],
		["Tap 3 Level", 1, "modTapLevel3", 0],
		["Tap 4 Level", 1, "modTapLevel4", 0],
		["Tap 1 Pan", 1, "modTapPan1", 0],
		["Tap 2 Pan", 1, "modTapPan2", 0],
		["Tap 3 Pan", 1, "modTapPan3", 0],
		["Tap 4 Pan", 1, "modTapPan4", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 2;
	arrOutBusSpecs = [ 
		["Out L + R", [0,1]], 
		["Out L only", [0]], 
		["Out R only", [1]] 
	];	
	arrBufferSpecs = [ ["bufnumDelay", defSampleRate * maxDelaytime, 1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	displayOption = "showTaps";
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumDelay", 0, \ir],
		["delay", 0.75, defLagTime],
		["delayMin", 10, defLagTime],
		["delayMax", 1000 * maxDelaytime, defLagTime],
		["tapRatio1", 0.25, defLagTime],
		["tapLevel1", 0.8, defLagTime],
		["tapRatio2", 0.5, defLagTime],
		["tapLevel2", 0.6, defLagTime],
		["tapRatio3", 0.75, defLagTime],
		["tapLevel3", 0.45, defLagTime],
		["tapRatio4", 1, defLagTime],
		["tapLevel4", 0.3, defLagTime],
		["tapPan1", 0.25, defLagTime],
		["tapPan2", 0.75, defLagTime],
		["tapPan3", 0, defLagTime],
		["tapPan4", 1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modTapLevel1", 0, defLagTime],
		["modTapLevel2", 0, defLagTime],
		["modTapLevel3", 0, defLagTime],
		["modTapLevel4", 0, defLagTime],
		["modTapPan1", 0, defLagTime],
		["modTapPan2", 0, defLagTime],
		["modTapPan3", 0, defLagTime],
		["modTapPan4", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelay, delay, delayMin, delayMax,  
			tapRatio1, tapLevel1, tapRatio2, tapLevel2, tapRatio3, tapLevel3, tapRatio4, tapLevel4, 
			tapPan1, tapPan2, tapPan3, tapPan4, wetDryMix, modDelay, 
			modTapLevel1, modTapLevel2, modTapLevel3, modTapLevel4, 
			modTapPan1, modTapPan2, modTapPan3, modTapPan4, modWetDryMix;
		var input, outSound, delaytime, totTapLevel1, totTapLevel2, totTapLevel3, totTapLevel4, 
			totTapPan1, totTapPan2, totTapPan3, totTapPan4, totMix, totFrames, bufPhasor, tap1, tap2, tap3, tap4;
		input = TXClean.ar(TXEnvPresets.startEnvFunc.value * InFeedback.ar(in,1));
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay).max(0.0001).min(1)) ) * delayMin / 1000;
		totTapLevel1 = (tapLevel1 + modTapLevel1).max(0).min(1);
		totTapLevel2 = (tapLevel2 + modTapLevel2).max(0).min(1);
		totTapLevel3 = (tapLevel3 + modTapLevel3).max(0).min(1);
		totTapLevel4 = (tapLevel4 + modTapLevel4).max(0).min(1);
		totTapPan1 = (tapPan1 + modTapPan1).max(0).min(1).madd(2, -1);  // Pan goes from -1 to 1
		totTapPan2 = (tapPan2 + modTapPan2).max(0).min(1).madd(2, -1);
		totTapPan3 = (tapPan3 + modTapPan3).max(0).min(1).madd(2, -1);
		totTapPan4 = (tapPan4 + modTapPan4).max(0).min(1).madd(2, -1);
		totMix = (wetDryMix + modWetDryMix).max(0).min(1);
		totFrames = defSampleRate * maxDelaytime;
		bufPhasor = Phasor.ar(0, BufRateScale.ir(bufnumDelay), 0, BufFrames.ir(bufnumDelay), 0);
		BufWr.ar(input, bufnumDelay, bufPhasor);
		tap1 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio1 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan1, totTapLevel1); 
		tap2 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio2 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan2, totTapLevel2); 
		tap3 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio3 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan3, totTapLevel3); 
		tap4 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio4 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan4, totTapLevel4); 
		outSound = Mix.new([([input, input] * (1-totMix)), ((tap1  + tap2 + tap3 + tap4) * totMix)]);
		Out.ar(out, TXClean.ar(outSound));
	};
	holdControlSpec = ControlSpec.new(10, 1000 * maxDelaytime, \exp );
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

buildGuiSpecArray {
	guiSpecArray = [];
	if (displayOption == "showTaps", {
		guiSpecArray = guiSpecArray ++[
		["TextBarLeft", "Note - delay time shown in ms and bpm", 250],
		["ActionButtonDark", "Clear delay", {this.clearBuffers}], 
		["NextLine"],
		["TXTimeBpmMinMaxSldr", "Delay time", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["DividingLine"],
		["TXPresetPopup", "Ratio presets", 
			TXMultiTapPresets.arrRatioPresets(this).collect({arg item, i; item.at(0)}), 
			TXMultiTapPresets.arrRatioPresets(this).collect({arg item, i; item.at(1)})
		],
		["TXFraction", "Tap 1 Ratio", ControlSpec(0, 1), "tapRatio1"],
		["NextLine"],
		["TXFraction", "Tap 2 Ratio", ControlSpec(0, 1), "tapRatio2"],
		["NextLine"],
		["TXFraction", "Tap 3 Ratio", ControlSpec(0, 1), "tapRatio3"],
		["NextLine"],
		["TXFraction", "Tap 4 Ratio", ControlSpec(0, 1), "tapRatio4"],
		["DividingLine"],
		["TXPresetPopup", "Level presets", 
			TXMultiTapPresets.arrLevelPresets(this).collect({arg item, i; item.at(0)}), 
			TXMultiTapPresets.arrLevelPresets(this).collect({arg item, i; item.at(1)})
		],
		["EZslider", "Tap 1 Level", ControlSpec(0, 1), "tapLevel1"],
		["EZslider", "Tap 2 Level", ControlSpec(0, 1), "tapLevel2"],
		["EZslider", "Tap 3 Level", ControlSpec(0, 1), "tapLevel3"],
		["EZslider", "Tap 4 Level", ControlSpec(0, 1), "tapLevel4"],
		["DividingLine"],
		["TXPresetPopup", "Pan presets", 
			TXMultiTapPresets.arrPanPresets(this).collect({arg item, i; item.at(0)}), 
			TXMultiTapPresets.arrPanPresets(this).collect({arg item, i; item.at(1)})
		],
		["EZslider", "Tap 1 Pan", ControlSpec(0, 1), "tapPan1"],
		["EZslider", "Tap 2 Pan", ControlSpec(0, 1), "tapPan2"],
		["EZslider", "Tap 3 Pan", ControlSpec(0, 1), "tapPan3"],
		["EZslider", "Tap 4 Pan", ControlSpec(0, 1), "tapPan4"],
		["DividingLine"],
		["WetDryMixSlider"], 
	];
	});
}

}

