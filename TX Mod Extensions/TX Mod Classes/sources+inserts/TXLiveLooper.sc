// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLiveLooper : TXModuleBase {		// Live Looper 

	//	Notes:
	//	This is a live looping tool which can be set to any time up to 16 secs.
	//	
	//	

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<arrBufferSpecs;
	classvar	<guiWidth=500;
	
	classvar	<maxLoopTime = 17;	//	loop time up to 16 secs.

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Live Looper";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Loop Time", 1, "modLoopTime", 0],
		["Input Level", 1, "modInLevel", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumLoop", defSampleRate * maxLoopTime, 1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	var holdControlSpec, holdControlSpec2;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["out", 0, 0],
		["bufnumLoop", 0, \ir],
		["loopTime", 0.25, defLagTime],
		["loopTimeMin", 1000, defLagTime],
		["loopTimeMax", 1000 * maxLoopTime, defLagTime],
		["speedFactor", 0.5, defLagTime],
		["speedFactorMin", 0.5, defLagTime],
		["speedFactorMax", 2, defLagTime],
		["inLevel", 0.5, 0.1],	// set lag time to 0.1 to stop clicks when freezing
		["feedback", 0.75, 0.1],	// set lag time to 0.1 to stop clicks when freezing
		["wetDryMix", 1.0, defLagTime],
		["modLoopTime", 0, defLagTime],
		["modSpeedFactor", 0, defLagTime],
		["modInLevel", 0, defLagTime],
		["modFeedback", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumLoop, loopTime=0.1, loopTimeMin, loopTimeMax, 
			speedFactor, speedFactorMin, speedFactorMax, inLevel, feedback,
			wetDryMix, modLoopTime=0, modSpeedFactor=0, modInLevel=0, modFeedback=0, modWetDryMix=0;
		var input, outSound, loopTimeCombined, speedFactorVal, inLevelCombined, feedbackCombined, mixCombined, loopOut, loopOut2;
		input = InFeedback.ar(in,1);

		loopTimeCombined = (loopTimeMin + ( (loopTimeMax-loopTimeMin) * (loopTime + modLoopTime).max(0).min(1) ) )/ 1000;
//		loopTimeCombined =( (loopTimeMax/loopTimeMin) ** ((loopTime + modLoopTime).max(0.0001).min(1)) ) * loopTimeMin / 1000;

		speedFactorVal =( (speedFactorMax/speedFactorMin) ** ((speedFactor + modSpeedFactor).max(0.0001).min(1)) ) * speedFactorMin;
//		speedFactorVal = speedFactorMin + ( (speedFactorMax-speedFactorMin) * (speedFactor + modSpeedFactor).max(0).min(1) );

		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);
		inLevelCombined = (inLevel + modInLevel).max(0).min(1);
		feedbackCombined = (feedback + modFeedback).max(0).min(1);

//		RecordBuf.ar(input, bufnumLoop, 0, oldNewMix, (1 - oldNewMix), 1, 1, t_reset);
//		loopOut = PlayBuf.ar(1, bufnumLoop, speedFactorVal, t_reset, 0, 1);

		loopOut = BufRd.ar(1, bufnumLoop, 
			Phasor.ar(0, speedFactorVal * BufRateScale.kr(bufnumLoop), 
				0, (loopTimeCombined / (maxLoopTime)) * BufFrames.kr(bufnumLoop)),
			0, 4
		);
			
		BufWr.ar( ( (input * inLevelCombined) + (loopOut * feedbackCombined) ), bufnumLoop, 
			Phasor.ar(0, speedFactorVal * BufRateScale.kr(bufnumLoop), 
				0, (loopTimeCombined / maxLoopTime) * BufFrames.kr(bufnumLoop)),
			0
		);
		outSound = (loopOut * mixCombined) + (input * (1-mixCombined));
		Out.ar(out, outSound);
	};
	holdControlSpec = ControlSpec.new(100, 1000 * maxLoopTime);
	holdControlSpec2 = ControlSpec.new(0.1, 10 , \exp );
	guiSpecArray = [
		["TXTimeBpmMinMaxSldr", "Time ms/bpm", holdControlSpec, "loopTime", "loopTimeMin", "loopTimeMax"], 
		["EZslider", "Input Level", ControlSpec(0, 1), "inLevel"], 
		["EZslider", "Feedback", ControlSpec(0, 1), "feedback"], 
		["TXMinMaxSliderSplit", "Speed factor", holdControlSpec2, "speedFactor", "speedFactorMin", "speedFactorMax"], 
		["WetDryMixSlider"], 
		["NextLine"], 
		["ActionButtonBig", "Freeze Loop", {this.freezeLoop}], 
		["ActionButtonDarkBig", "Clear Loop", {this.clearLoop}], 
		["NextLine"], 
	];
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Freeze Loop", {this.freezeLoop;}],
		["commandAction", "Clear Loop", {this.clearLoop;}],
		["TXTimeBpmMinMaxSldr", "Loop time ms + bpm", holdControlSpec, "loopTime", "loopTimeMin", "loopTimeMax"], 
		["EZslider", "Input Level", ControlSpec(0, 1), "inLevel"], 
		["EZslider", "Feedback", ControlSpec(0, 1), "feedback"], 
		["TXMinMaxSliderSplit", "Speed factor", holdControlSpec2, "speedFactor", "speedFactorMin", "speedFactorMax"], 
		["WetDryMixSlider"], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

clearLoop{ 
	this.buffers.at(0).zero;
	system.showViewIfModDisplay(this);
} 

freezeLoop{ 
	this.setSynthValue("inLevel", 0);
	this.setSynthValue("feedback", 1);
	system.showViewIfModDisplay(this);
} 

}

