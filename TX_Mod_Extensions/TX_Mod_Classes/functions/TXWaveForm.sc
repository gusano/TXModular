// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXWaveForm {		// Waveform module 

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

*arrAllData {
	^ [
		["Sine wave", 
			{arg outFreq, waveModify1, waveModify2; SinOsc.ar(outFreq); },
			"Modify 1 is not used. Modify 2 is not used."
		],
		["Chorusing sine wave - M1", 
			{arg outFreq, waveModify1, waveModify2; Mix.ar(SinOsc.ar(outFreq * (1 + (waveModify1 * [-0.02,0.02]) ) )); },
			"Mix of 2 detuned sine waves. Modify 1 sets the depth of detune."
			+ "Modify 2 is not used."
		],
		["Sine -> Sawtooth wave - M1", 
			{arg outFreq, waveModify1, waveModify2; 
			(((1-waveModify1) * SinOsc.ar(outFreq)) + (waveModify1 * Saw.ar(outFreq))); },
			"Modify 1 crossfades between the waveforms."
			+ "Modify 2 is not used."
		],
		["Sine -> Square wave - M1", 
			{arg outFreq, waveModify1, waveModify2; 
			(((1-waveModify1) * SinOsc.ar(outFreq)) + (waveModify1 * Pulse.ar(outFreq, 0.5))); },
			"Modify 1 crossfades between the waveforms."
			+ "Modify 2 is not used."
		],
		["Sawtooth wave", 
			{arg outFreq, waveModify1, waveModify2; Saw.ar(outFreq); },
			"Modify 1 is not used. Modify 2 is not used."
		],
		["SyncSaw wave - M1", 
			{arg outFreq, waveModify1, waveModify2; SyncSaw.ar(outFreq, outFreq*(1 + (1.5*waveModify1))); },
			"Sawtooth with a hard-synced second waveform. Modify 1 is the frequency of the second waveform."
			+ "Modify 2 is not used."
		],
		["Sawtooth -> Square wave - M1", 
			{arg outFreq, waveModify1, waveModify2; 
			(((1-waveModify1) * Saw.ar(outFreq)) + (waveModify1 * Pulse.ar(outFreq, 0.5))); },
			"Modify 1 crossfades between the waveforms."
			+ "Modify 2 is not used."
		],
		["Sawtooth wave with flattened harmonics - M1",
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
					SinOsc.ar(outFreq * 
							[1, 2-(waveModify1*0.03), 3-(waveModify1*0.06), 4-(waveModify1*0.09), 
								5-(waveModify1*0.12), 6-(waveModify1*0.15)]
					,0
					, 1.0/[1,2,3,4,5,6]
					);
				); },
			"Modify 1 sets the amount of flattening."
			+ "Modify 2 is not used."
		],
		["Sawtooth wave with sharpened harmonics - M1",
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
					SinOsc.ar(
						outFreq * 
							[1, 2+(waveModify1*0.03), 3+(waveModify1*0.06), 4+(waveModify1*0.09), 
								5+(waveModify1*0.12), 6+(waveModify1*0.15)]
						,0
						, 1.0/[1,2,3,4,5,6]
						);
					); },
			"Modify 1 sets the amount of sharpening."
			+ "Modify 2 is not used."
		],
		["Square wave", 
			{arg outFreq, waveModify1, waveModify2; Pulse.ar(outFreq, 0.5); },
			"Modify 1 is not used. Modify 2 is not used."
		],
		["Pulse wave - M1", 
			{arg outFreq, waveModify1, waveModify2; Pulse.ar(outFreq, 0.5+(waveModify1*0.47)); },
			"Modify 1 sets the width of the pulse."
			+ "Modify 2 is not used."
		],
		["Square wave with flattened harmonics - M1",
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
							SinOsc.ar(outFreq * 
										[1, 3-(waveModify1*0.03), 5-(waveModify1*0.06), 7-(waveModify1*0.09), 
											9-(waveModify1*0.12), 11-(waveModify1*0.15)]
								,0
								, 1.0/[1,3,5,7,9,11]
								);
							); },
			"Modify 1 sets the amount of flattening."
			+ "Modify 2 is not used."
		],
		["Square wave with sharpened harmonics - M1",
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
					SinOsc.ar(outFreq * 
								[1, 3+(waveModify1*0.03), 5+(waveModify1*0.06), 7+(waveModify1*0.09), 
									9+(waveModify1*0.12), 11+(waveModify1*0.15)]
						,0
						, 1.0/[1,3,5,7,9,11]
						);
					); },
			"Modify 1 sets the amount of sharpening."
			+ "Modify 2 is not used."
		],

		//	Formant.ar(fundfreq, formfreq, widthfreq, mul, add)
		["Formant osc 1 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*(1+(9*waveModify1)),outFreq); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Pulse width frequency is same as fundamental frequency."
			+ "Modify 1 sets the formant frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 2 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*(1+(9*waveModify1)),outFreq*2); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Pulse width frequency is twice the fundamental frequency."
			+ "Modify 1 sets the formant frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 3 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*(1+(9*waveModify1)),outFreq*4); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Pulse width frequency is 4 times the fundamental frequency."
			+ "Modify 1 sets the formant frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 4 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*(1+(9*waveModify1)),outFreq*8); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Pulse width frequency is 8 times the fundamental frequency."
			+ "Modify 1 sets the formant frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 5 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*2,outFreq*(1+(8*waveModify1))); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Formant frequency is twice the fundamental frequency."
			+ "Modify 1 sets the pulse width frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 6 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*4,outFreq*(1+(8*waveModify1))); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Formant frequency is 4 times the fundamental frequency."
			+ "Modify 1 sets the pulse width frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 7 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*8,outFreq*(1+(16*waveModify1))); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Formant frequency is 8 times the fundamental frequency."
			+ "Modify 1 sets the pulse width frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 8 - M1", 
			{arg outFreq, waveModify1, waveModify2; Formant.ar(outFreq,outFreq*16,outFreq*(1+(16*waveModify1))); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Formant frequency is 16 times the fundamental frequency."
			+ "Modify 1 sets the pulse width frequency."
			+ "Modify 2 is not used."
		],
		["Formant osc 9 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				Formant.ar(outFreq,outFreq*(1+(9*waveModify1)),outFreq*(1+(15*waveModify2))); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Formant frequency is 8 times the fundamental frequency."
			+ "Modify 1 sets the formant frequency."
			+ "Modify 2 sets the pulse width frequency."
		],
		["Formant osc 10 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				Formant.ar(outFreq,outFreq*(10-(9*waveModify1)),outFreq*(1+(15*waveModify2))); },
			"Generates a set of harmonics around a formant frequency at a given fundamental frequency,"
			+ "with pulse width frequency affecting the bandwidth of the formant."
			+ "Formant frequency is 8 times the fundamental frequency."
			+ "Modify 1 sets the formant frequency, with the direction reversed - going from high to low."
			+ "Modify 2 sets the pulse width frequency."
		],
		["White Noise", 
			{arg outFreq, waveModify1, waveModify2; WhiteNoise.ar; },
			"Noise where frequency spectrum has equal power at all frequencies."
			+ "Modify 1 is not used. Modify 2 is not used."
		],
		["Pink Noise", 
			{arg outFreq, waveModify1, waveModify2; PinkNoise.ar; },
			"Noise where frequency spectrum falls off in power by 3 dB per octave."
			+ "This gives equal power over the span of each octave."
			+ "It has more lower frequencies than white noise."
			+ "Modify 1 is not used. Modify 2 is not used."
		],
		["Brown Noise", 
			{arg outFreq, waveModify1, waveModify2; BrownNoise.ar; },
			"Noise where frequency spectrum falls off in power by 6 dB per octave. "
			+ "It has more lower frequencies than white and pink noise."
			+ "Modify 1 is not used. Modify 2 is not used."
		],
		["Clip Noise", 
			{arg outFreq, waveModify1, waveModify2; ClipNoise.ar; },
			"Noise with maximum energy. "
			+ "Modify 1 is not used. Modify 2 is not used."
		],
		["Crackle - M1", 
			{arg outFreq, waveModify1, waveModify2; Crackle.ar(0.8 + (waveModify1*1.2)); },
			"Noise generator based on chaos algorithm. "
			+ "Modify 1 sets the depth of chaos. Modify 2 is not used."
		],
		["Dust - M1", 
			{arg outFreq, waveModify1, waveModify2; Dust2.ar(1 + (waveModify1*1000)); },
			"Noise based on random impulses. "
			+ "Modify 1 sets the density of the dust. Modify 2 is not used."
		],
		["Gray Noise", 
			{arg outFreq, waveModify1, waveModify2; GrayNoise.ar; },
			"Varying noise where frequency spectrum is emphasized towards lower frequencies. "
			+ "Modify 1 is not used. Modify 2 is not used."
		],
		["Vowel - Man 1 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2, hold3, hold4;
				hold1 = [270, 2300, 3000];		// vowel in BEAT
				hold2 = [400, 2000, 2550];		// vowel in BIT
				hold3 = [730, 1100, 2450];		// vowel in PART
				hold4 = [300, 850, 2250];		// vowel in BOOK
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						(((hold2*waveModify1)+(hold1*(1-waveModify1)))*(1-waveModify2)) +
							(((hold3*waveModify1)+(hold4*(1-waveModify1)))*(waveModify2)),
						outFreq)); },
			"Vowel sounds from formants based on a man's voice and the vowel sounds in BEAT, BIT, PART and BOOK."
			+ "Modify 1 & Modify 2 together control the mix between the vowel sounds."
		],
		["Vowel - Man 2 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2, hold3, hold4;
				hold1 = [640, 1200, 240];		// vowel in BUT
				hold2 = [440, 1000, 2250];		// vowel in BOOT
				hold3 = [660, 1700, 2400];		// vowel in BAT
				hold4 = [530, 1850, 2500];		// vowel in BET
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						(((hold2*waveModify1)+(hold1*(1-waveModify1)))*(1-waveModify2)) +
							(((hold3*waveModify1)+(hold4*(1-waveModify1)))*(waveModify2)),
						outFreq)); },
			"Vowel sounds from formants based on a man's voice and the vowel sounds in BUT, BOOT, BAT and BET."
			+ "Modify 1 & Modify 2 together control the mix between the vowel sounds."
		],
		["Vowel - Woman 1 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2, hold3, hold4;
				hold1 = [590, 900, 2700];		// vowel in POT
				hold2 = [430, 2500, 3100];		// vowel in BIT
				hold3 = [600, 2350, 3000];		// vowel in BET
				hold4 = [850, 1200, 2800];		// vowel in PART
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						(((hold2*waveModify1)+(hold1*(1-waveModify1)))*(1-waveModify2)) +
							(((hold3*waveModify1)+(hold4*(1-waveModify1)))*(waveModify2)),
						outFreq)); },
			"Vowel sounds from formants based on a woman's voice and the vowel sounds in POT, BIT, BET and PART."
			+ "Modify 1 & Modify 2 together control the mix between the vowel sounds."
		],
		["Vowel - Woman 2 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2, hold3, hold4;
				hold1 = [370, 950, 2650];		// vowel in BOOK
				hold2 = [500, 1650, 1950];		// vowel in PERT
				hold3 = [860, 2050, 2850];		// vowel in BAT
				hold4 = [470, 1150, 2650];		// vowel in BOOT
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						(((hold2*waveModify1)+(hold1*(1-waveModify1)))*(1-waveModify2)) +
							(((hold3*waveModify1)+(hold4*(1-waveModify1)))*(waveModify2)),
						outFreq)); },
			"Vowel sounds from formants based on a woman's voice and the vowel sounds in BOOK, PERT, BAT and BOOT."
			+ "Modify 1 & Modify 2 together control the mix between the vowel sounds."
		],
		["Vowel - Child 1 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2, hold3, hold4;
				hold1 = [1030, 1350, 3200];		// vowel in PART
				hold2 = [700, 2600, 3550];		// vowel in BET
				hold3 = [680, 1050, 3200];		// vowel in POT
				hold4 = [530, 2750, 3600];		// vowel in BIT
				1/3 * 
					Mix.ar(
						Formant.ar(
						outFreq,
						(((hold2*waveModify1)+(hold1*(1-waveModify1)))*(1-waveModify2)) +
							(((hold3*waveModify1)+(hold4*(1-waveModify1)))*(waveModify2)),
						outFreq)); },
			"Vowel sounds from formants based on a child's voice and the vowel sounds in PART, BET, POT and BIT."
			+ "Modify 1 & Modify 2 together control the mix between the vowel sounds."
		],
		["Vowel - Child 2 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2, hold3, hold4;
				hold1 = [370, 3200, 3700];		// vowel in BEAT
				hold2 = [1000, 2300, 3300];		// vowel in BAT
				hold3 = [850, 1600, 3350];		// vowel in BUT
				hold4 = [560, 1650, 2150];		// vowel in PERT
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						(((hold2*waveModify1)+(hold1*(1-waveModify1)))*(1-waveModify2)) +
							(((hold3*waveModify1)+(hold4*(1-waveModify1)))*(waveModify2)),
						outFreq)); },
			"Vowel sounds from formants based on a child's voice and the vowel sounds in BEAT, BAT, BUT and PERT."
			+ "Modify 1 & Modify 2 together control the mix between the vowel sounds."
		],
		["Vowel - SizeMorph 1 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2;
				hold1 = [640, 1200, 240];		// vowel in BUT
				hold2 = [300, 850, 2250];		// vowel in BOOK
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						((hold2 * waveModify1) + (hold1 * (1-waveModify1))) * (0.5 + (waveModify2 * 4)),
						outFreq * 2
						)); },
			"Vowel sounds from formants."
			+ "Modify 1 sets the mix between the vowel sound in BUT and BOOK."
			+ "Modify 2 shifts the formant frequencies."
		],
		["Vowel - SizeMorph 2 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2;
				hold1 = [400, 2000, 2550];		// vowel in BIT
				hold2 = [660, 1700, 2400];		// vowel in BAT
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						((hold2 * waveModify1) + (hold1 * (1-waveModify1))) * (0.5 + (waveModify2 * 4)),
						outFreq * 2
						)); },
			"Vowel sounds from formants."
			+ "Modify 1 sets the mix between the vowel sound in BUT and BOOK."
			+ "Modify 2 shifts the formant frequencies."
		],
		["Vowel - SizeMorph 3 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2;
				hold1 = [440, 1000, 2250];		// vowel in BOOT
				hold2 = [730, 1100, 2450];		// vowel in PART
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						((hold2 * waveModify1) + (hold1 * (1-waveModify1))) * (0.5 + (waveModify2 * 4)),
						outFreq * 2
						)); },
			"Vowel sounds from formants."
			+ "Modify 1 sets the mix between the vowel sound in BOOT and PART."
			+ "Modify 2 shifts the formant frequencies."
		],
		["Vowel - SizeMorph 4 - M1 & M2",
			{arg outFreq, waveModify1, waveModify2; 
				var hold1, hold2;
				hold1 = [270, 2300, 3000];		// vowel in BEAT
				hold2 = [490, 1350, 1700];		// vowel in PERT
				1/3 * Mix.ar(
					Formant.ar(
						outFreq,
						((hold2 * waveModify1) + (hold1 * (1-waveModify1))) * (0.5 + (waveModify2 * 4)),
						outFreq * 2
						)); },
			"Vowel sounds from formants."
			+ "Modify 1 sets the mix between the vowel sound in BEAT and PERT."
			+ "Modify 2 shifts the formant frequencies."
		],
		["FM 0 - 2C.2M, freq ratios 2 & 1.5 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(PMOsc.ar(outFreq, outFreq*[2, 1.5], [waveModify1*2pi,waveModify2*2pi], 0)); },
			"Frequency modulation using sine waves."
			+ "Has a mix of 2 carriers each with a modulator."
			+ "Modify 1 sets the depth of modulation using 2:1 frequency ratio."
			+ "Modify 2 sets the depth of modulation using 1.5:1 frequency ratio."
		],
		["FM 1 - 2C.2M, freq ratios 1.5 & 3.5 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(PMOsc.ar(outFreq, outFreq*[1.5, 3.5], [waveModify1*2pi,waveModify2*2pi], 0)); },
			"Frequency modulation using sine waves."
			+ "Has a mix of 2 carriers each with a modulator."
			+ "Modify 1 sets the depth of modulation using 2:1 frequency ratio."
			+ "Modify 2 sets the depth of modulation using 1.5:1 frequency ratio."
		],
		["FM 2 - 2C.2M, freq ratios 1.333 & 1.666 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(PMOsc.ar(outFreq, outFreq*[4/3, 5/5], [waveModify1*2pi,waveModify2*2pi], 0)); },
			"Frequency modulation using sine waves."
			+ "Has a mix of 2 carriers each with a modulator."
			+ "Modify 1 sets the depth of modulation using 1.333:1 frequency ratio."
			+ "Modify 2 sets the depth of modulation using 1.666:1 frequency ratio."
		],
		["FM 3 - 2C.2M, freq ratios 1.25 & 1.75 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(PMOsc.ar(outFreq, outFreq*[5/4, 7/4], [waveModify1*2pi,waveModify2*2pi], 0)); },
			"Frequency modulation using sine waves."
			+ "Has a mix of 2 carriers each with a modulator."
			+ "Modify 1 sets the depth of modulation using 1.25:1 frequency ratio."
			+ "Modify 2 sets the depth of modulation using 1.75:1 frequency ratio."
		],
		["FM 4 - 1C.2M, freq ratios 2 & 1.5 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * PMOsc.ar(outFreq, outFreq * 2, waveModify1*2pi, SinOsc.ar(outFreq * 1.5, 0, waveModify2*2pi)); },
			"Frequency modulation using sine waves."
			+ "Has 1 carrier being modulated by 2 modulators."
			+ "Modify 1 sets the depth of modulation using 2:1 frequency ratio."
			+ "Modify 2 sets the depth of modulation using 1.5:1 frequency ratio."
		],
		["FM 5 - 1C.2M, freq ratios 1.5 & 3.5 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * PMOsc.ar(outFreq, outFreq * 1.5, waveModify1*2pi, SinOsc.ar(outFreq * 3.5, 0, waveModify2*2pi)); },
			"Frequency modulation using sine waves."
			+ "Has 1 carrier being modulated by a modulator with a 1.5:1 frequency ratio,"
			+ "which is being modulated by another modulator with a 3.5:1 frequency ratio."
			+ "Modify 1 sets the depth of modulation using 1.5:1 ratio."
			+ "Modify 2 sets the depth of modulation using 3.5:1 ratio."
		],
		["FM 6 - 1C.2M, freq ratios 1.25 & 2.25 - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * PMOsc.ar(outFreq, outFreq * 1.25, waveModify1*2pi, SinOsc.ar(outFreq * 2.25, 0, waveModify2*2pi)); },
			"Frequency modulation using sine waves."
			+ "Has 1 carrier being modulated by a modulator with a 1.25:1 frequency ratio,"
			+ "which is being modulated by another modulator with a 2.25:1 frequency ratio."
			+ "Modify 1 sets the depth of modulation using 1.25:1 ratio."
			+ "Modify 2 sets the depth of modulation using 2.25:1 ratio."
		],
		["FM 7 - 1C.1M, variable freq - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * PMOsc.ar(outFreq, outFreq*(1 + (waveModify2*1.5)), waveModify1*2pi, 0); },
			"Frequency modulation using sine waves."
			+ "Has 1 carrier being modulated by a modulator."
			+ "Modify 1 sets the frequency ratio of the modulator."
			+ "Modify 2 sets the depth of modulation."
		],
		["FM 8 - 1C.2M, variable freq - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * PMOsc.ar(outFreq, outFreq * (0.5 + (waveModify1*1.5)), pi, 
			SinOsc.ar(outFreq * (0.25 + (waveModify2*3)), 0, 1)); },
			"Frequency modulation using sine waves."
			+ "Has 1 carrier being modulated by a modulator with a variable frequency ratio,"
			+ "which is being modulated by another modulator with a variable frequency ratio."
			+ "This has a shallower modulation than FM 9."
			+ "Modify 1 sets the frequency ratio of the first modulator."
			+ "Modify 2 sets the frequency ratio of the second modulator."
		],
		["FM 9 - 1C.2M, variable freq - M1 & M2", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * PMOsc.ar(outFreq, outFreq * (0.5 + (waveModify1*1.5)), 2pi, 
			SinOsc.ar(outFreq * (0.25 + (waveModify2*3)), 0, 1)); },
			"Frequency modulation using sine waves."
			+ "Has 1 carrier being modulated by a modulator with a variable frequency ratio,"
			+ "which is being modulated by another modulator with a variable frequency ratio."
			+ "This has a deeper modulation than FM 8."
			+ "Modify 1 sets the frequency ratio of the first modulator."
			+ "Modify 2 sets the frequency ratio of the second modulator."
		],
		["Golden Mean Harmonics - M1", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
					SinOsc.ar(outFreq * 
						(1.61803 ** [0,1,2,3,4,5])
						,0
						,[1, waveModify1*0.618, waveModify1*0.381, waveModify1*0.236, 
							waveModify1*0.145, waveModify1*0.09]
						);
					); },
			"Generates a set of harmonics based on the frequency ratio of the Golden Mean which is approx. 1.61803:1."
			+ "Modify 1 sets the proportion of harmonics added."
			+ "Modify 2 is not used."
		],
		["Drum Harmonics - M1", 
			{arg outFreq, waveModify1, waveModify2; 
				1/5 * Mix.ar(
					SinOsc.ar(outFreq * [1, 1.593, 2.136, 2.296, 2.653]
						,0
						,[1, waveModify1/1.2, waveModify1/1.3, waveModify1/1.4, waveModify1/1.5]
						);
					); },
			"Generates a set of harmonics based on the vibration of a drum,"
			+ "Modify 1 sets the proportion of harmonics added."
			+ "Modify 2 is not used."
		],
		["Cymbal Harmonics - M1", 
			{arg outFreq, waveModify1, waveModify2; 
				1/5 * Mix.ar(
					SinOsc.ar(outFreq * [1, 2.092, 3.427, 3.910, 6.067]
						,0
						,[1, waveModify1/1.2, waveModify1/1.3, waveModify1/1.4, waveModify1/1.5]
						);
					); },
			"Generates a set of harmonics based on the vibration of a cymbal,"
			+ "Modify 1 sets the proportion of harmonics added."
			+ "Modify 2 is not used."
		],
		["Root 2 Harmonics - M1", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
					SinOsc.ar(outFreq * (1.41421 ** [0,1,2,3,4,5])
						,0
						, [1] ++ (waveModify1 * (0.75 ** [1,2,3,4,5]))
						);
					); },
			"Generates a set of harmonics based on the frequency ratio of the square root of 2 which is approx. 1.61803:1."
			+ "Modify 1 sets the proportion of harmonics added."
			+ "Modify 2 is not used."
		],
		["Root 3 Harmonics - M1", 
			{arg outFreq, waveModify1, waveModify2; 
				1/3 * Mix.ar(
					SinOsc.ar(outFreq * (1.73205 ** [0,1,2,3,4,5])
						,0
						, [1] ++ (waveModify1 * (0.75 ** [1,2,3,4,5]))
						);
					); },
			"Generates a set of harmonics based on the frequency ratio of the square root of 3 which is approx. 1.73205:1."
			+ "Modify 1 sets the proportion of harmonics added."
			+ "Modify 2 is not used."
		],
		["Blip - M1", 
			{arg outFreq, waveModify1, waveModify2; Blip.ar(outFreq, ControlSpec(1,60).map(waveModify1));},
			"Blip - Band limited impulse oscillator."
			+ "Modify 1 sets the number of harmonics."
			+ "Modify 2 is not used."
		],
	];
}

}

