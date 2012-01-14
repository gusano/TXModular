// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXPVProcess {		// PV Process module 

/* NOTES 
1. See implementation comments at end of document 
2. To create a list of process names use:
	TXPVProcess.arrProcessNames.do({arg item, i; item.postln;});
*/

*initClass{
	//	
	// set class specific variables
	//	
} 

*arrOptionData {
	^this.arrAllData.collect({arg item, i; [item.at(0), item.at(1)];});
} 

*arrDescriptions {
	^this.arrAllData.collect({arg item, i; item.at(2);});
} 

*arrProcessNames {
	^this.arrAllData.collect({arg item, i; item.at(0);});
} 

*arrAllData {
/* ------------ Template: ----------------------
["PV_xxx", 
{arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_xxx(ZZZZZZZZZZZZ);},
"YYYYYDESCRIPTION
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used."
],
----------------------------------- */
^ [ 
[ "Unprocessed", {arg chainA, chainB, fftSize, modify1, modify2, modify3;  chainA;}, "Not processed
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Bin Wipe Down - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_BinWipe(chainA, chainB, modify1.neg);}, "Bin Wipe Down - Copies low bins of main input and high bins of the side chain, downwards wipe.
Modify 1 - wipe - replace main input with bins from side chain from the top down.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Bin Wipe Up - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_BinWipe(chainA, chainB, modify1);}, "Bin Wipe Up - Copies low bins of main input and high bins of the side chain, upwards wipe.
Modify 1 - wipe - replace main input with bins from side chain from the bottom up.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "BrickWall High Pass", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_BrickWall(chainA, modify1);}, "BrickWall High Pass - Clears FFT bins below a cutoff point.
Modify 1 - wipe - like a high pass filter, clearing bins from the bottom up.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "BrickWall Low Pass", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_BrickWall(chainA, modify1.neg);}, "BrickWall Low Pass - Clears FFT bins above a cutoff point.
Modify 1 - wipe - like a low pass filter, clearing bins from the top down.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Comb Filter", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_RectComb(chainA, modify1 * 50, modify2, modify3);}, "Makes a series of gaps in a spectrum.
Modify 1 - numTeeth - number of teeth in the comb (0-50).
Modify 2 - phase - starting phase of comb pulse.
Modify 3 - width - pulse width of comb." ], 
[ "Comb Integrater - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_RectComb(chainA, modify1 * 50, modify2, modify3);}, "Alternates blocks of FFT bins between the main and side chain inputs.
Modify 1 - numTeeth - number of teeth in the comb (0-50).
Modify 2 - phase - starting phase of comb pulse.
Modify 3 - width - pulse width of comb." ], 
[ "Common Magnitudes - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_CommonMag(chainA, chainB, modify1 * 50, modify2);}, "Returns magnitudes common to main & side chain inputs within a tolerance level.
Modify 1 - tolerance - set value higher to include more bins.
Modify 2 - remove - level for uncommon magnitudes (set to 0 to completely remove them).
Modify 3 - not used." ], 
[ "Conformal Map", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_ConformalMap(chainA, modify1, modify2);}, "Applies the conformal mapping z -> (z-a)/(1-za*) to the FFT bins. (i.e. makes a transformation of the complex plane).
Modify 1 - real part of a used in the mapping.
Modify 2 - imaginary part of a used in the mapping.
Modify 3 - not used." ], 
[ "Cutoff Low Pass - High Pass", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Cutoff(chainA, modify1);}, "Clears bins above or below a cutoff point.  
Modify 1 - buffer - fft buffer.
Modify 2 - wipe - can range between -1 and +1. if wipe == 0.5 then there is no effect.
if  wipe > 0.5 then it acts like a high pass filter, clearing bins from the bottom up. 
if  wipe < 0.5 then it acts like a low pass filter, clearing bins from the top down.
Modify 3 - not used." ], 
[ "Diffuser", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Diffuser(chainA, modify1-0.5);}, "Adds a different constant random phase shift to each bin. The trigger will select a new set of random phases.
Modify 1 - trigger - selects a new set of random phases every time it goes above 0.5.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Disintegrate", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_RandComb(chainA, modify1, modify2-0.5);}, "Randomly clear FFT bins.
Modify 1 - wipe - clears bins from input in a random order as wipe goes from 0 to 1.
Modify 2 - trigger - selects a new random order every time it goes above 0.5.
Modify 3 - not used." ], 
[ "Even Bins Only", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_EvenBin(chainA);}, "Use the even numbered bins in the FFT buffer
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Freeze Spectrum", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Freeze(chainA, modify1-0.5);}, "Freezes magnitudes of FFT bins at current levels.  
Modify 1 - trigger - Freezes magnitudes every time it goes above 0.5.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Invert Magnitudes", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Invert(chainA);}, "Invert the magnitudes of the FFT bins.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Local Maximum", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_LocalMax(chainA, modify1 * 50);}, "Passes only FFT bins whose magnitude is above a threshold and above their nearest neighbors.
Modify 1 - threshold.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Above Threshold", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagAbove(chainA, modify1 * 50);}, "Passes only FFT bins whose magnitude is above a threshold.
Modify 1 - threshold.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Below Threshold", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagBelow(chainA, modify1 * 50);}, "Passes only FFT bins whose magnitude is below a threshold.
Modify 1 - threshold.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Clip To Threshold", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagClip(chainA, modify1 * 50);}, "Clips bin magnitudes to a maximum threshold.
Modify 1 - threshold.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Compare Maximum - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Max(chainA, chainB);}, "Compares the main and side chain inputs, and outputs FFT bins with the maximum magnitude.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Compare Minimum - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Min(chainA, chainB);}, "Compares the main and side chain inputs, and outputs FFT bins with the minimum magnitude.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Divide - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagDiv(chainA, chainB);}, "Divides magnitudes of the main and side chain inputs and keeps the phases of the main input.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Exponential", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagExp(chainA);}, "Converts FFT magnitudes to their exponential values. It is best to use this in combination with Magnitude Log or else levels could become very high.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Freeze", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagFreeze(chainA, modify1-0.5);}, "Freezes magnitudes at current levels every time trigger goes above 0.5.
Modify 1 - trigger - Freezes magnitudes every time it goes above 0.5.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Log", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagLog(chainA);}, "Converts FFT magnitudes to their log values. Use in combination with Magnitude Exponential.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Multiply", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagMulAdd(chainA, modify1 * 10);}, "Scaling of FFT magnitudes
Modify 1 - scaling - from 0 up to 10 times the original magnitude.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Multiply - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagMul(chainA, chainB);}, "Multiplies magnitudes of the main and side chain inputs and keeps the phases of the main input.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Noise", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagNoise(chainA);}, "Magnitudes are multiplied with noise.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Smear", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagSmear(chainA, modify1 * 100);}, "Average a bin's magnitude with its neighbors.
Modify 1 - width - how many neighbors to include in the averaging.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Smooth", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagSmooth(chainA, modify1);}, "Smooth FFT magnitudes over time
Modify 1 - smoothing - from 0 (no smoothing occurs) to 1 ('infinite' smoothing, magnitudes never change).
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Squared", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagSquared(chainA);}, "Squares the magnitudes and renormalizes to previous peak. This makes weak FFT bins weaker.
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Magnitude Stretch & Shift", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	var holdStretch, holdShift;
	holdStretch = [0.25, 4, \exponential].asSpec.map(modify1);
	holdShift = [-128, 128].asSpec.map(modify2);
	PV_MagShift(chainA, holdStretch, holdShift);}, "Stretch and shift the positions of only the magnitude of the FFT bins. 
Modify 1 - stretch - scale bin location by factor (range 0.25 - 4).
Modify 2 - shift - add an offset to bin position (range -+128).
Modify 3 - not used." ], 
[ "Multiply Common Magnitudes - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_CommonMul(chainA, chainB, modify1 * 50, modify2);}, "Returns magnitudes common to main & side chain inputs within a tolerance level.
Modify 1 - tolerance - set value higher to include more bins.
Modify 2 - remove-  level for uncommon magnitudes (set to 0 to completely remove them).
Modify 3 - not used." ], 
[ "Noise Keep Using Magnitudes", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_NoiseSynthF(chainA, modify1 * (22050 / fftSize));}, "Use only FFT bins that are unstable (based on magnitudes)
Modify 1 - threshold - a higher threshold means less bins will be heard.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Noise Keep Using Phases", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_NoiseSynthP(chainA, modify1 * (22050 / fftSize));}, "Use only FFT bins that are unstable (based on phases)
Modify 1 - threshold - a higher threshold means less bins will be heard.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Noise Remove Using Magnitudes", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_PartialSynthF(chainA, modify1 * (22050 / fftSize));}, "Use only FFT bins that are stable (based on magnitudes of bins)
Modify 1 - threshold - a higher threshold means less bins will be heard.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Noise Remove Using Phases", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_PartialSynthP(chainA, modify1 * (22050 / fftSize));}, "Use only FFT bins that are stable (based on phases)
Modify 1 - threshold - a higher threshold means less bins will be heard.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Odd Bins Only", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_OddBin(chainA);}, "Use the odd numbered bins in the FFT buffer
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Phase Shift 270", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_PhaseShift270(chainA);}, "Shift phase of all FFT bins by 270 degrees
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Phase Shift 90", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_PhaseShift90(chainA);}, "Shift phase of all FFT bins by 90 degrees
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Phase Shift Variable", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_PhaseShift(chainA, modify1 * 2pi);}, "Shift phase of all FFT bins
Modify 1 - phase shift (0-360).
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Random Crossfade - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_RandWipe(chainA, chainB, modify1, modify2-0.5);}, "Cross fades between main and side chain inputs by copying FFT bins in a random order.
Modify 1 - wipe - copies more bins from side chain input in a random order as wipe goes from 0 to 1.
Modify 2 - trigger - selects a new random order every time it goes above 0.5.
Modify 3 - not used." ], 
[ "Randomize Bins", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_BinScramble(chainA, modify1, modify2, modify3-0.5);}, "Randomizes the order of the FFT bins.
Modify 1 - wipe - scrambles more bins as it moves from zero to one.
Modify 2 - width - the maximum randomized distance of a bin from its original location in the spectrum.
Modify 3 - trigger - selects a new random order every time it goes above 0.5." ], 
[ "Scale & Shift Bins", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	var holdStretch, holdShift;
	holdStretch = [0.25, 4, \exponential].asSpec.map(modify1);
	holdShift = [-128, 128].asSpec.map(modify2);
	PV_BinShift(chainA, holdStretch, holdShift);}, "Scale and shift the positions of the FFT bins.
Modify 1 - stretch - scale bin location by factor (range 0.25 - 4).
Modify 2 - shift - add an offset to bin position (range -+128).
Modify 3 - not used." ], 
[ "Soft Wipe - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_SoftWipe(chainA, chainB, modify1);}, "Combine low and high bins from main and side chain inputs.
Modify 1 - wipe - from main input to side chain input. if wipe == 0.5 then the main input is heard.
if  wipe > 0.5 then it begins replacing with bins from side chain from the bottom up. 
if  wipe < 0.5 then it begins replacing with bins from side chain from the top down.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Spectral Addition - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Add(chainA, chainB);}, "Spectral addition - adds main input to side chain input
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Spectral Compression", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Compander(chainA, modify1 * 80, (1 + (modify2 * 9)).reciprocal);}, "applies compression to individual magnitudes.
Modify 1 - threshold - magnitudes above the threshold are compressed.
Modify 2 - compression ration (0 - no compression, 0.5, some compression, 1 - limiting.
Modify 3 - not used." ], 
[ "Spectral Expansion", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Compander(chainA, modify1 * 80, 1, (1 + (modify2 * 20)).reciprocal);}, "applies expansion to individual magnitudes.
Modify 1 - threshold - magnitudes above the threshold are compressed.
Modify 2 - expannsion ration (0 - no expannsion, 0.5, some expannsion, 1 - gating.
Modify 3 - not used." ], 
[ "Spectral Gate", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagGate(chainA, modify1 * 100, modify2);}, "reduces magnitudes below threshold
Modify 1 - threshold - a higher threshold means more bins will be reduced.
Modify 2 - remove - level for lower magnitudes.
Modify 3 - not used." ], 
[ "Spectral Gate Inverted", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagGate(chainA, modify1 * -100, modify2);}, "reduces magnitudes above threshold
Modify 1 - threshold - a higher threshold means less bins will be reduced.
Modify 2 - remove - level for uncommon magnitudes (set to 0 to completely remove them).
Modify 3 - not used." ], 
[ "Spectral Morph - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Morph(chainA, chainB, modify1);}, "one kind of spectral morphing from main input to side chain input.
Modify 1 - morph - from main input to side chain input.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Spectral Multiply - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Mul(chainA, chainB)}, "Complex multiplication of the main and side chain inputs
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Spectral Subtract - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagSubtract(chainA, chainB);}, "Spectral subtract (difference of magnitudes) - side chain magnitudes are subtracted from main input 
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Spectral Subtract Zero-limited - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagSubtract(chainA, chainB, 1);}, "Spectral subtract zero-limited (difference of magnitudes) - side chain magnitudes are subtracted from main input. The magnitudes are hard-limited to zero, never going lower. 
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Spectral XFade - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_Morph(chainA, chainB, modify1);}, "One kind of spectral crossfade from main input to side chain input.
Modify 1 - fade - from main input to side chain input.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Strongest Bins", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MaxMagN(chainA, (1 - modify1) * (fftSize-1));}, "Use the strongest FFT bins
Modify 1 - threshold - a higher threshold means less bins will be heard.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Subtract Magnitudes - uses side chain audio", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MagMinus(chainA, chainB, modify1);}, "Subtract magnitudes of side chain input from main input.
Modify 1 - remove - scales the removal process.
Modify 2 - not used.
Modify 3 - not used." ], 
[ "Weekest Bins", {arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_MinMagN(chainA, (1 - modify1) * (fftSize-1));}, "Use the weakest FFT bins
Modify 1 - threshold - a higher threshold means less bins will be heard.
Modify 2 - not used.
Modify 3 - not used." ] 
]


/* ------------ Template: ----------------------
["PV_xxx", 
{arg chainA, chainB, fftSize, modify1, modify2, modify3; 
	PV_xxx(ZZZZZZZZZZZZ);},
"YYYYYDESCRIPTION
Modify 1 - not used.
Modify 2 - not used.
Modify 3 - not used."
],
----------------------------------- */

}

}
/*
======= Nov 2009: NOTE These PV Processes are NOT included in this class so far =======

----NOT included From main classes:-----
FFT	Fast Fourier Transform
IFFT	Inverse Fast Fourier Transform
PV_Copy	copy an FFT buffer
PV_CopyPhase	copy magnitudes and phases
PV_HainsworthFoote
PV_JensenAndersen

----NOT included From SCPlugins:-----
PVFile
PVInfo
PVSynth
PV_BinBufRd
PV_BinDelay
PV_BinPlayBuf
PV_BufRd
PV_FreqBuffer
PV_MagBuffer
PV_MagMap  - this needs a signal array to act as the curve
PV_PlayBuf
PV_RecordBuf
PV_Whiten

=========== Processes included in this class: =========== 

----From main classes:-----
PV_Add	complex addition
PV_BinScramble	scramble bins
PV_BinShift	shift and stretch bin position
PV_BinWipe	combine low and high bins from two inputs
PV_BrickWall	zero bins
PV_ConformalMap	complex plane attack 
PV_Diffuser	random phase shifting
PV_LocalMax	pass bins which are a local maximum
PV_MagAbove	pass bins above a threshold
PV_MagBelow	pass bins below a threshold
PV_MagClip	clip bins to a threshold
PV_MagFreeze	freeze magnitudes
PV_MagMul	multiply magnitudes
PV_MagDiv	division of magnitudes
PV_MagNoise	multiply magnitudes by noise
PV_MagShift	shift and stretch magnitude bin position
PV_MagSmear	average magnitudes across bins
PV_MagSquared	square magnitudes
PV_Max	maximum magnitude
PV_Min	minimum magnitude
PV_Mul	complex multiply
PV_PhaseShift	shift phase of all bins
PV_PhaseShift270	shift phase by 270 degrees
PV_PhaseShift90	shift phase by 90 degrees
PV_RandComb	pass random bins
PV_RandWipe	crossfade in random bin order
PV_RectComb	make gaps in spectrum
PV_RectComb2	make gaps in spectrum
----From SCPlugins:-----	
PV_EvenBin
PV_Freeze
PV_Invert
PV_MaxMagN
PV_MinMagN
PV_NoiseSynthF
PV_NoiseSynthP
PV_PartialSynthF
PV_PartialSynthP
PV_MagExp
PV_MagLog
PV_MagMulAdd
PV_MagSmooth
PV_MagSubtract
PV_CommonMag
PV_CommonMul
PV_Compander
PV_Cutoff
PV_MagGate
PV_MagMinus
PV_Morph
PV_SoftWipe
PV_XFade
*/


