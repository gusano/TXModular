// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXVocoder : TXModuleBase {		// Vocoder module 

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=100;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	<arrBufferSpecs;
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Vocoder";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrAudSCInBusSpecs = [ 
		 ["Modulator", 1, "inmodulator"]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumCarrier", 2048,1],  ["bufnumModulator", 2048,1] ];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	extraLatency = 0.2;	// allow extra time when recreating
	arrSynthArgSpecs = [
		["in", 0, 0],
		["inmodulator", 0, 0],
		["out", 0, 0],
		["bufnumCarrier", 0, 0],
		["bufnumModulator", 0, 0],
	]; 
	// vocode 2 inputs 
	synthDefFunc = { arg in, inmodulator, out, bufnumCarrier, bufnumModulator;
		var outSound, chain, fftCarrier, fftModulator;
		fftCarrier = FFT(bufnumCarrier, InFeedback.ar(in,1));
		fftModulator = FFT(bufnumModulator, InFeedback.ar(inmodulator,1));
		chain = PV_MagMul(fftCarrier,fftModulator);
		Out.ar(out, 
			IFFT(chain) // inverse FFT
		);
	};
	guiSpecArray = [
		//	no controls needed for this module
	];
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

