// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXReverb2 : TXModuleBase {		

	//	Notes:
	//based on SuperCollider 2 revereb adaptations by Nick Collins 
	///4 comb followed by 2 allpass- basic Schroeder reverb
	//see Curtis Roads CM Tutorial pp 481 
	//relatively prime delay times
	//order 10 mS small room - roomsize = 0.2
	//50 mS large - roomsize = 1

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

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Reverb";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrCtlSCInBusSpecs = [ 
		["Room size", 1, "modDelay", 0],
		["Decay", 1, "modFeedback", 0],
		["Damping", 1, "modFreq", 0],
		["Dry-Wet Mix", 1, "modWetDryMix", 0]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ 
		["bufnumComb1", defSampleRate * 0.05, 1] ,  
		["bufnumComb2", defSampleRate * 0.05, 1] ,  
		["bufnumComb3", defSampleRate * 0.05, 1] ,  
		["bufnumComb4", defSampleRate * 0.05, 1] ,  
		["bufnumAllPass1", defSampleRate * 0.01, 1] ,
		["bufnumAllPass2", defSampleRate * 0.01, 1] ,
		["bufnumAllPass3", defSampleRate * 0.01, 1] ,
		["bufnumAllPass4", defSampleRate * 0.01, 1] ,
	];
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
		["bufnumComb1", 0, \ir],
		["bufnumComb2", 0, \ir],
		["bufnumComb3", 0, \ir],
		["bufnumComb4", 0, \ir],
		["bufnumAllPass1", 0, \ir],
		["bufnumAllPass2", 0, \ir],
		["bufnumAllPass3", 0, \ir],
		["bufnumAllPass4", 0, \ir],
		["delay", 0.5, defLagTime],
		["delayMin", 0.1, defLagTime],
		["delayMax", 1, defLagTime],
		["feedback", 0.25, defLagTime],
		["feedbackMin", 0.01, defLagTime],
		["feedbackMax", 1.0, defLagTime],
		["freq", 3000, defLagTime],
		["wetDryMix", 1.0, defLagTime],
		["modDelay", 0, defLagTime],
		["modFeedback", 0, defLagTime],
		["modFreq", 0, defLagTime],
		["modWetDryMix", 0, defLagTime],
	]; 
	synthDefFunc = { 
		arg in, out, bufnumComb1, bufnumComb2, bufnumComb3, bufnumComb4, bufnumAllPass1, bufnumAllPass2, bufnumAllPass3, bufnumAllPass4, 
			delay=0.1, delayMin, delayMax, feedback, feedbackMin, 
			feedbackMax, freq, wetDryMix, modDelay=0, modFeedback=0, modFreq=0, modWetDryMix=0;
		var input, outSound, combBufs, allPassBufs, roomsize, feedbackVal, combdelays, sumfreq, mixCombined;
		input = TXClean.ar(InFeedback.ar(in,1));
		roomsize = ((delayMax/delayMin) ** ((delay + modDelay).max(0.1).min(1)) ) * delayMin;
		feedbackVal = feedbackMin + ( (feedbackMax-feedbackMin) * (feedback + modFeedback).max(0).min(1) );
		sumfreq = ( (100) ** ((freq + modFreq).max(0.001).min(1)) ) * 200;
		mixCombined = (wetDryMix + modWetDryMix).max(0).min(1);

		//coprime delay times
		combdelays = ([0.0499,0.0431,0.0373,0.0311]);
		// combs
		combBufs = [bufnumComb1, bufnumComb2, bufnumComb3, bufnumComb4];
		outSound = Mix.arFill(4,{arg i; BufCombC.ar(combBufs.at(i), input, roomsize * combdelays.at(i),
				 4 * roomsize * feedbackVal, 0.25)});
		// 2 allpass in series
// now using 4 for smoother reverb
//		2.do({ arg i; 
//			outSound = AllpassC.ar(outSound,0.01,[0.005,0.004].at(i),[0.05,0.07].at(i));
//		});
		// 4 allpass in series
		allPassBufs = [bufnumAllPass1, bufnumAllPass2, bufnumAllPass3, bufnumAllPass4];
		4.do({ arg i; 
			outSound = BufAllpassC.ar(allPassBufs.at(i), outSound,[0.005,0.004,0.0055,0.0045].at(i),[0.05,0.07,0.055,0.075].at(i));
		});
		// damping filter
		outSound = LPF.ar(outSound, sumfreq);
		outSound = (outSound  * mixCombined) + (input * (1-mixCombined));
		Out.ar(out, TXClean.ar(outSound));
	};
	holdControlSpec = ControlSpec.new(0.1, 1, \exp );
	holdControlSpec2 = ControlSpec.new(0.01, 1.0, \exp );
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Room size", holdControlSpec, "delay", "delayMin", "delayMax"], 
		["SpacerLine", 4],
		["TXMinMaxSliderSplit", "Decay", holdControlSpec2, "feedback", "feedbackMin", "feedbackMax"], 
		["SpacerLine", 4],
		["EZsliderUnmapped", "Damping freq", ControlSpec(100, 20000, \exp, 0, 1), "freq"], 
		["SpacerLine", 4],
		["WetDryMixSlider"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

