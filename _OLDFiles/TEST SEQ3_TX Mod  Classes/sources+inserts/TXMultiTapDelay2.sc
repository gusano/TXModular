// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXMultiTapDelay2 : TXModuleBase {		// MultiTap Delay 

	//	Notes:
	//	This is a delay which can be set to any time up to 5 minutes.
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
	
	var	displayOption;
	var	holdControlSpec;
	var	holdTapTime, newTapTime;

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
		["Tap 5 Level", 1, "modTapLevel5", 0],
		["Tap 6 Level", 1, "modTapLevel6", 0],
		["Tap 7 Level", 1, "modTapLevel7", 0],
		["Tap 8 Level", 1, "modTapLevel8", 0],
		["Tap 1 Pan", 1, "modTapPan1", 0],
		["Tap 2 Pan", 1, "modTapPan2", 0],
		["Tap 3 Pan", 1, "modTapPan3", 0],
		["Tap 4 Pan", 1, "modTapPan4", 0],
		["Tap 5 Pan", 1, "modTapPan5", 0],
		["Tap 6 Pan", 1, "modTapPan6", 0],
		["Tap 7 Pan", 1, "modTapPan7", 0],
		["Tap 8 Pan", 1, "modTapPan8", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
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

init {arg argInstName;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	displayOption = "showTaps";
	arrOptions = [0];
	arrOptionData = [
		[	
			["15 seconds", 15],
			["30 seconds", 30],
			["1 minute", 60],
			["2 minutes", 120],
			["3 minutes", 180],
			["4 minutes", 240],
			["5 minutes", 300],
		],
	];
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumDelay", 0, \ir],
		["delay", 0.75, defLagTime],
		["delayMin", 10, defLagTime],
		["delayMax", 1000 * this.getMaxDelaytime, defLagTime],
		["tapRatio1", 0.125, defLagTime],
		["tapLevel1", 1, defLagTime],
		["tapRatio2", 0.25, defLagTime],
		["tapLevel2", 0.875, defLagTime],
		["tapRatio3", 0.375, defLagTime],
		["tapLevel3", 0.75, defLagTime],
		["tapRatio4", 0.5, defLagTime],
		["tapLevel4", 0.625, defLagTime],
		["tapRatio5", 0.625, defLagTime],
		["tapLevel5", 0.5, defLagTime],
		["tapRatio6", 0.75, defLagTime],
		["tapLevel6", 0.375, defLagTime],
		["tapRatio7", 0.875, defLagTime],
		["tapLevel7", 0.25, defLagTime],
		["tapRatio8", 1, defLagTime],
		["tapLevel8", 0.125, defLagTime],
		["tapPan1", 0.375, defLagTime],
		["tapPan2", 0.625, defLagTime],
		["tapPan3", 0.25, defLagTime],
		["tapPan4", 0.75, defLagTime],
		["tapPan5", 0.125, defLagTime],
		["tapPan6", 0.875, defLagTime],
		["tapPan7", 0, defLagTime],
		["tapPan8", 1, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modTapLevel1", 0, defLagTime],
		["modTapLevel2", 0, defLagTime],
		["modTapLevel3", 0, defLagTime],
		["modTapLevel4", 0, defLagTime],
		["modTapLevel5", 0, defLagTime],
		["modTapLevel6", 0, defLagTime],
		["modTapLevel7", 0, defLagTime],
		["modTapLevel8", 0, defLagTime],
		["modTapPan1", 0, defLagTime],
		["modTapPan2", 0, defLagTime],
		["modTapPan3", 0, defLagTime],
		["modTapPan4", 0, defLagTime],
		["modTapPan5", 0, defLagTime],
		["modTapPan6", 0, defLagTime],
		["modTapPan7", 0, defLagTime],
		["modTapPan8", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
		// N.B. arg below not used in synthdef, just kept here for convenience
		["autoTapTempo", 0, \ir],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumDelay, delay, delayMin, delayMax,  
			tapRatio1, tapLevel1, tapRatio2, tapLevel2, tapRatio3, tapLevel3, tapRatio4, tapLevel4, 
			tapRatio5, tapLevel5, tapRatio6, tapLevel6, tapRatio7, tapLevel7, tapRatio8, tapLevel8, 
			tapPan1, tapPan2, tapPan3, tapPan4, tapPan5, tapPan6, tapPan7, tapPan8, wetDryMix, modDelay, 
			modTapLevel1, modTapLevel2, modTapLevel3, modTapLevel4, 
			modTapLevel5, modTapLevel6, modTapLevel7, modTapLevel8, 
			modTapPan1, modTapPan2, modTapPan3, modTapPan4, modTapPan5, modTapPan6, modTapPan7, modTapPan8,
			modWetDryMix;
		var input, outSound, delaytime, totTapLevel1, totTapLevel2, totTapLevel3, totTapLevel4,
			totTapLevel5, totTapLevel6, totTapLevel7, totTapLevel8, 
			totTapPan1, totTapPan2, totTapPan3, totTapPan4, 
			totTapPan5, totTapPan6, totTapPan7, totTapPan8, 
			totMix, totFrames, bufPhasor, tap1, tap2, tap3, tap4, tap5, tap6, tap7, tap8;
		input = TXClean.ar(TXEnvPresets.startEnvFunc.value * InFeedback.ar(in,1));
		delaytime =( (delayMax/delayMin) ** ((delay + modDelay).max(0.0001).min(1)) ) * delayMin / 1000;
		totTapLevel1 = (tapLevel1 + modTapLevel1).max(0).min(1);
		totTapLevel2 = (tapLevel2 + modTapLevel2).max(0).min(1);
		totTapLevel3 = (tapLevel3 + modTapLevel3).max(0).min(1);
		totTapLevel4 = (tapLevel4 + modTapLevel4).max(0).min(1);
		totTapLevel5 = (tapLevel5 + modTapLevel5).max(0).min(1);
		totTapLevel6 = (tapLevel6 + modTapLevel6).max(0).min(1);
		totTapLevel7 = (tapLevel7 + modTapLevel7).max(0).min(1);
		totTapLevel8 = (tapLevel8 + modTapLevel8).max(0).min(1);
		totTapPan1 = (tapPan1 + modTapPan1).max(0).min(1).madd(2, -1);  // Pan goes from -1 to 1
		totTapPan2 = (tapPan2 + modTapPan2).max(0).min(1).madd(2, -1);
		totTapPan3 = (tapPan3 + modTapPan3).max(0).min(1).madd(2, -1);
		totTapPan4 = (tapPan4 + modTapPan4).max(0).min(1).madd(2, -1);
		totTapPan5 = (tapPan5 + modTapPan5).max(0).min(1).madd(2, -1);
		totTapPan6 = (tapPan6 + modTapPan6).max(0).min(1).madd(2, -1);
		totTapPan7 = (tapPan7 + modTapPan7).max(0).min(1).madd(2, -1);
		totTapPan8 = (tapPan8 + modTapPan8).max(0).min(1).madd(2, -1);
		totMix = (wetDryMix + modWetDryMix).max(0).min(1);
		totFrames = defSampleRate * this.getMaxDelaytime;
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
		tap5 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio5 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan5, totTapLevel5); 
		tap6 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio6 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan6, totTapLevel6); 
		tap7 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio7 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan7, totTapLevel7); 
		tap8 = Pan2.ar(BufRd.ar(1, bufnumDelay, (bufPhasor - Lag.kr(tapRatio8 * delaytime * defSampleRate, 0.1))
			.wrap(0, totFrames-1)),totTapPan8, totTapLevel8); 
		outSound = Mix.new([([input, input] * (1-totMix)), 
			((tap1  + tap2 + tap3 + tap4 + tap5 + tap6 + tap7 + tap8) * totMix)]);
		Out.ar(out, TXClean.ar(outSound));
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["SynthOptionPopupPlusMinus", "Maximum time", arrOptionData, 0, 200,
			{this.buildGuiSpecArray;
			system.showViewIfModDisplay(this);
			this.makeBuffersAndSynth(this.getArrBufferSpecs);
			}
		], 
		["ActionButtonDark", "Clear delay", {this.clearBuffers}], 
		["TXTimeBpmMinMaxSldr", "Delay time", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["TXPresetPopup", "Ratio presets", 
			TXMultiTap8Presets.arrRatioPresets(this).collect({arg item, i; item.at(0)}), 
			TXMultiTap8Presets.arrRatioPresets(this).collect({arg item, i; item.at(1)})
		],
		["commandAction", "Tap Tempo", {this.actionTapTempo;}],
		["TXCheckBox", "Auto copy tap tempo to delay bpm ", "autoTapTempo", nil, 230],
		["TXFraction", "Tap 1 Ratio", ControlSpec(0, 1), "tapRatio1"],
		["TXFraction", "Tap 2 Ratio", ControlSpec(0, 1), "tapRatio2"],
		["TXFraction", "Tap 3 Ratio", ControlSpec(0, 1), "tapRatio3"],
		["TXFraction", "Tap 4 Ratio", ControlSpec(0, 1), "tapRatio4"],
		["TXFraction", "Tap 5 Ratio", ControlSpec(0, 1), "tapRatio5"],
		["TXFraction", "Tap 6 Ratio", ControlSpec(0, 1), "tapRatio6"],
		["TXFraction", "Tap 7 Ratio", ControlSpec(0, 1), "tapRatio7"],
		["TXFraction", "Tap 8 Ratio", ControlSpec(0, 1), "tapRatio8"],
		["DividingLine"],
		["TXPresetPopup", "Level presets", 
			TXMultiTap8Presets.arrLevelPresets(this).collect({arg item, i; item.at(0)}), 
			TXMultiTap8Presets.arrLevelPresets(this).collect({arg item, i; item.at(1)})
		],
		["WetDryMixSlider"], 
		["EZslider", "Tap 1 Level", ControlSpec(0, 1), "tapLevel1"],
		["EZslider", "Tap 2 Level", ControlSpec(0, 1), "tapLevel2"],
		["EZslider", "Tap 3 Level", ControlSpec(0, 1), "tapLevel3"],
		["EZslider", "Tap 4 Level", ControlSpec(0, 1), "tapLevel4"],
		["EZslider", "Tap 5 Level", ControlSpec(0, 1), "tapLevel5"],
		["EZslider", "Tap 6 Level", ControlSpec(0, 1), "tapLevel6"],
		["EZslider", "Tap 7 Level", ControlSpec(0, 1), "tapLevel7"],
		["EZslider", "Tap 8 Level", ControlSpec(0, 1), "tapLevel8"],
		["DividingLine"],
		["TXPresetPopup", "Pan presets", 
			TXMultiTap8Presets.arrPanPresets(this).collect({arg item, i; item.at(0)}), 
			TXMultiTap8Presets.arrPanPresets(this).collect({arg item, i; item.at(1)})
		],
		["EZslider", "Tap 1 Pan", ControlSpec(0, 1), "tapPan1"],
		["EZslider", "Tap 2 Pan", ControlSpec(0, 1), "tapPan2"],
		["EZslider", "Tap 3 Pan", ControlSpec(0, 1), "tapPan3"],
		["EZslider", "Tap 4 Pan", ControlSpec(0, 1), "tapPan4"],
		["EZslider", "Tap 5 Pan", ControlSpec(0, 1), "tapPan5"],
		["EZslider", "Tap 6 Pan", ControlSpec(0, 1), "tapPan6"],
		["EZslider", "Tap 7 Pan", ControlSpec(0, 1), "tapPan7"],
		["EZslider", "Tap 8 Pan", ControlSpec(0, 1), "tapPan8"],
	]);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(this.getArrBufferSpecs);
}

buildGuiSpecArray {
	holdControlSpec = ControlSpec.new(10, 1000 * this.getMaxDelaytime, \exp );
	guiSpecArray = [
		["ActionButton", "Taps", {displayOption = "showTaps"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showTaps")], 
		["Spacer", 3], 
		["ActionButton", "Levels", {displayOption = "showLevels"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showLevels")], 
		["Spacer", 3], 
		["ActionButton", "Pans", {displayOption = "showPans"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showPans")], 
		["Spacer", 3], 
		["SpacerLine", 6], 
	];
	
	if (displayOption == "showTaps", {
		guiSpecArray = guiSpecArray ++[	
			["SynthOptionPopupPlusMinus", "Maximum time", arrOptionData, 0, 200,
				{this.buildGuiSpecArray;
				system.showViewIfModDisplay(this);
				this.makeBuffersAndSynth(this.getArrBufferSpecs);
				}
			], 
			["SpacerLine", 4], 
			["TapTempoButton", {arg argTempo; this.useTapTempo(argTempo);}],
			["Spacer", 10], 
			["TXCheckBox", "Auto copy tap tempo to delay bpm ", "autoTapTempo", nil, 230],
			["SpacerLine", 4], 
			["TextBarLeft", "Delay time shown in ms and bpm", 200],
			["Spacer", 3], 
			["ActionButton", "time x 2", {this.delayTimeMultiply(2);}, 60], 
			["ActionButton", "time x 3", {this.delayTimeMultiply(3);}, 60], 
			["ActionButton", "time / 2", {this.delayTimeMultiply(0.5);}, 60], 
			["ActionButton", "time / 3", {this.delayTimeMultiply(1/3);}, 60], 
			["NextLine"],
			["TXTimeBpmMinMaxSldr", "Delay time", holdControlSpec, "delay", "delayMin", "delayMax"], 
			["SpacerLine", 4], 
			["TXPresetPopup", "Ratio presets", 
				TXMultiTap8Presets.arrRatioPresets(this).collect({arg item, i; item.at(0)}), 
				TXMultiTap8Presets.arrRatioPresets(this).collect({arg item, i; item.at(1)})
			],
			["SpacerLine", 2], 
			["TXFraction", "Tap 1 Ratio", ControlSpec(0, 1), "tapRatio1"],
			["NextLine"],
			["TXFraction", "Tap 2 Ratio", ControlSpec(0, 1), "tapRatio2"],
			["NextLine"],
			["TXFraction", "Tap 3 Ratio", ControlSpec(0, 1), "tapRatio3"],
			["NextLine"],
			["TXFraction", "Tap 4 Ratio", ControlSpec(0, 1), "tapRatio4"],
			["NextLine"],
			["TXFraction", "Tap 5 Ratio", ControlSpec(0, 1), "tapRatio5"],
			["NextLine"],
			["TXFraction", "Tap 6 Ratio", ControlSpec(0, 1), "tapRatio6"],
			["NextLine"],
			["TXFraction", "Tap 7 Ratio", ControlSpec(0, 1), "tapRatio7"],
			["NextLine"],
			["TXFraction", "Tap 8 Ratio", ControlSpec(0, 1), "tapRatio8"],
			["SpacerLine", 4], 
			["WetDryMixSlider"], 
			["SpacerLine", 4], 
			["ActionButtonDark", "Clear delay", {this.clearBuffers}], 
		];
	});
	if (displayOption == "showLevels", {
		guiSpecArray = guiSpecArray ++[	
			["TXPresetPopup", "Level presets", 
				TXMultiTap8Presets.arrLevelPresets(this).collect({arg item, i; item.at(0)}), 
				TXMultiTap8Presets.arrLevelPresets(this).collect({arg item, i; item.at(1)})
			],
			["SpacerLine", 4], 
//			["EZslider", "Tap 1 Level", ControlSpec(0, 1), "tapLevel1"],
//			["EZslider", "Tap 2 Level", ControlSpec(0, 1), "tapLevel2"],
//			["EZslider", "Tap 3 Level", ControlSpec(0, 1), "tapLevel3"],
//			["EZslider", "Tap 4 Level", ControlSpec(0, 1), "tapLevel4"],
//			["EZslider", "Tap 5 Level", ControlSpec(0, 1), "tapLevel5"],
//			["EZslider", "Tap 6 Level", ControlSpec(0, 1), "tapLevel6"],
//			["EZslider", "Tap 7 Level", ControlSpec(0, 1), "tapLevel7"],
//			["EZslider", "Tap 8 Level", ControlSpec(0, 1), "tapLevel8"],
			["TXMultiSliderNoGroup", "Tap Levels", ControlSpec(0, 1, step: 0.001), ["tapLevel1", "tapLevel2",
				"tapLevel3", "tapLevel4", "tapLevel5", "tapLevel6", "tapLevel7", "tapLevel8"], nil, 300],
		];
	});
	if (displayOption == "showPans", {
		guiSpecArray = guiSpecArray ++[
			["TXPresetPopup", "Pan presets", 
				TXMultiTap8Presets.arrPanPresets(this).collect({arg item, i; item.at(0)}), 
				TXMultiTap8Presets.arrPanPresets(this).collect({arg item, i; item.at(1)})
			],
			["SpacerLine", 4], 
//			["EZslider", "Tap 1 Pan", ControlSpec(0, 1), "tapPan1"],
//			["EZslider", "Tap 2 Pan", ControlSpec(0, 1), "tapPan2"],
//			["EZslider", "Tap 3 Pan", ControlSpec(0, 1), "tapPan3"],
//			["EZslider", "Tap 4 Pan", ControlSpec(0, 1), "tapPan4"],
//			["EZslider", "Tap 5 Pan", ControlSpec(0, 1), "tapPan5"],
//			["EZslider", "Tap 6 Pan", ControlSpec(0, 1), "tapPan6"],
//			["EZslider", "Tap 7 Pan", ControlSpec(0, 1), "tapPan7"],
//			["EZslider", "Tap 8 Pan", ControlSpec(0, 1), "tapPan8"],
			["TXMultiSliderNoGroup", "Tap Pans", ControlSpec(0, 1, step: 0.001), ["tapPan1", "tapPan2",
				"tapPan3", "tapPan4", "tapPan5", "tapPan6", "tapPan7", "tapPan8"], nil, 300],
			["SpacerLine", 2], 
			["TextBar", "0 is panned left, 1 is panned right", 250]
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

getMaxDelaytime {
	^arrOptionData.at(0).at(arrOptions.at(0)).at(1);
}
	
getArrBufferSpecs {
	arrBufferSpecs = [ ["bufnumDelay", defSampleRate * this.getMaxDelaytime, 1] ];
	^arrBufferSpecs;
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

useTapTempo {arg argTempo;
	var holdDelay, autoBPM, minDelay, maxDelay;
	holdDelay = 60000/argTempo;
	autoBPM = this.getSynthArgSpec("autoTapTempo");
	minDelay = this.getSynthArgSpec("delayMin");
	maxDelay = this.getSynthArgSpec("delayMax");
	if (autoBPM == 1,{
		if ((holdDelay >= minDelay) and: (holdDelay <= maxDelay),{
			this.setSynthArgSpec("delay", ControlSpec(minDelay, maxDelay, \exp).unmap(holdDelay));
			system.flagGuiIfModDisplay(this);
		});
	});
}

actionTapTempo {	// tap tempo function used by module action
	var holdBPM;
	if (newTapTime.isNil, {
		newTapTime = Main.elapsedTime
	}, {
		holdTapTime = Main.elapsedTime;
		holdBPM = 60 / (holdTapTime - newTapTime);
		newTapTime = holdTapTime;
		this.useTapTempo(holdBPM);
	});
}

loadExtraData {arg argData;
	this.buildGuiSpecArray;
	system.showViewIfModDisplay(this);
	{this.makeBuffersAndSynth(this.getArrBufferSpecs);}.defer(2);
}

}

