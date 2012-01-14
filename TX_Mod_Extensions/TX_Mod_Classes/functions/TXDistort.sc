// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXDistort {		// Distortion function module 

*initClass{
	//	
	// set class specific variables
	//	
} 

*arrOptionData {
	^ [
		["Distort", 
			{arg inSound; inSound.distort; }
		],
		["Distort3", 
			{arg inSound; inSound.distort.distort.distort; }
		],
		["Softclip", 
			{arg inSound; inSound.softclip; }
		],
		["Softclip3", 
			{arg inSound; inSound.softclip.softclip.softclip; }
		],
		["Clip", 
			{arg inSound; inSound.clip2(1); }
		],
		["Fold", 
			{arg inSound; inSound.fold2(1); }
		],
		["Wrap", 
			{arg inSound; inSound.wrap2(1); }
		],
		["Max", 
			{arg inSound; inSound.max(0); }
		],
		["Squared", 
			{arg inSound; inSound.squared; }
		],
		["Cubed", 
			{arg inSound; inSound.cubed; }
		],
		// Waveshape - phase modulate 0 Hz sine oscillator
		["Waveshape with Depth", 
			{arg inSound, inDepth; 
				SinOsc.ar(0, inSound * (1 + (inDepth * (8pi-1))) ); 
			}
		],
		["Waveshape 2 with Depth", 
		/* SC Code from batuhan@batuhanbozkurt.com on SC list:
		From DSP code by Partice Tarrabia and Bram de Jong
		http://www.musicdsp.org/archive.php?classid=4#203  */
			{arg inSound, inDepth; 
			var scaleDepth, depthRatio;
			scaleDepth = (inDepth * 1.99) - 1;
			depthRatio = 2 * scaleDepth / (1 - scaleDepth);
			(1 + depthRatio) * inSound / (1 + (depthRatio * inSound.abs));
			}
		],
		["Inside Out", 
			{arg inSound; InsideOut.ar(inSound); }
		],
	];
}

}
