// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXVocoderFX : TXModuleBase {		// Vocoder FX module 

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
	
*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "VocoderFX";
	moduleRate = "audio";
	moduleType = "insert";
	noInChannels = 1;			
	arrAudSCInBusSpecs = [ 
		 ["Modulator", 1, "inmodulator"]
	];	
	arrCtlSCInBusSpecs = [ 
		["stretch", 1, "modStretch", 0],
		["shift", 1, "modShift", 0],
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
		["stretch", 0.5, defLagTime],
		["stretchMin", 0.25, defLagTime],
		["stretchMax", 4, defLagTime],
		["shift", 0.5, defLagTime],
		["shiftMin", -50,  defLagTime],
		["shiftMax", 50, defLagTime],
		["modStretch", 0, defLagTime],
		["modShift", 0, defLagTime],
	]; 
	// vocode 2 inputs 
	synthDefFunc = { arg in, inmodulator, out, bufnumCarrier, bufnumModulator, stretch, stretchMin, stretchMax, 
			shift = 0.5, shiftMin= -50, shiftMax= 50,  modStretch = 0.0, modShift = 0.0;
		var outSound, chain, fftCarrier, fftModulator, sumStretch, sumShift;
		sumStretch = ( (stretchMax/ stretchMin) ** ((stretch + modStretch).max(0.001).min(1)) ) * stretchMin;
		sumShift =  shiftMin + ( (shiftMax - shiftMin) * (shift + modShift).max(0).min(1) );
		fftCarrier = FFT(bufnumCarrier, InFeedback.ar(in,1));
		fftModulator = FFT(bufnumModulator, InFeedback.ar(inmodulator,1));
//		fftModulator = PV_BinShift(bufnumModulator, sumStretch, sumShift-1);
		fftModulator = PV_BinShift(fftModulator, sumStretch, sumShift-1);
		chain = PV_MagMul(fftCarrier, fftModulator);
		Out.ar(out, 
			IFFT(chain) // inverse FFT
		);
	};
	guiSpecArray = [
		["TXMinMaxSliderSplit", "Stretch", ControlSpec(0.25, 4, \exp, 0, 1), "stretch", "stretchMin", "stretchMax"], 
		["TXMinMaxSliderSplit", "Shift", ControlSpec(-50, 50, default: 0), "shift", "shiftMin", "shiftMax"], 
	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
}

}

