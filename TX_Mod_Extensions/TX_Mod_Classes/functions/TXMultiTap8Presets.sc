// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXMultiTap8Presets {

// Note this is for 8-tap presets. See class TXMultiTapPresets for 4-tap presets.

*initClass{
	//	
	// set class specific variables
	//	
} 

*arrPanPresets { arg argModule;
	^[
		[	"Mono",
			{
				argModule.setSynthValue("tapPan1", 0.5);
				argModule.setSynthValue("tapPan2", 0.5);
				argModule.setSynthValue("tapPan3", 0.5);
				argModule.setSynthValue("tapPan4", 0.5);
				argModule.setSynthValue("tapPan5", 0.5);
				argModule.setSynthValue("tapPan6", 0.5);
				argModule.setSynthValue("tapPan7", 0.5);
				argModule.setSynthValue("tapPan8", 0.5);
			},
		],
		[	"Left to Right",
			{
				argModule.setSynthValue("tapPan1", 0);
				argModule.setSynthValue("tapPan2", 0.1429);
				argModule.setSynthValue("tapPan3", 0.2857);
				argModule.setSynthValue("tapPan4", 0.4286);
				argModule.setSynthValue("tapPan5", 0.5714);
				argModule.setSynthValue("tapPan6", 0.7143);
				argModule.setSynthValue("tapPan7", 0.8571);
				argModule.setSynthValue("tapPan8", 1);
			},
		],
		[	"Right to Left",
			{
				argModule.setSynthValue("tapPan1", 1);
				argModule.setSynthValue("tapPan2", 0.8571);
				argModule.setSynthValue("tapPan3", 0.7143);
				argModule.setSynthValue("tapPan4", 0.5714);
				argModule.setSynthValue("tapPan5", 0.4286);
				argModule.setSynthValue("tapPan6", 0.2857);
				argModule.setSynthValue("tapPan7", 0.1429);
				argModule.setSynthValue("tapPan8", 0);
			},
		],
		[	"Widening",
			{
				argModule.setSynthValue("tapPan1", 0.375);
				argModule.setSynthValue("tapPan2", 0.625);
				argModule.setSynthValue("tapPan3", 0.25);
				argModule.setSynthValue("tapPan4", 0.75);
				argModule.setSynthValue("tapPan5", 0.125);
				argModule.setSynthValue("tapPan6", 0.875);
				argModule.setSynthValue("tapPan7", 0);
				argModule.setSynthValue("tapPan8", 1);
			},
		],
		[	"Narrowing",
			{
				argModule.setSynthValue("tapPan1", 1);
				argModule.setSynthValue("tapPan2", 0);
				argModule.setSynthValue("tapPan3", 0.875);
				argModule.setSynthValue("tapPan4", 0.125);
				argModule.setSynthValue("tapPan5", 0.75);
				argModule.setSynthValue("tapPan6", 0.25);
				argModule.setSynthValue("tapPan7", 0.625);
				argModule.setSynthValue("tapPan8", 0.375);
			},
		],
		[	"Randomise",
			{
				argModule.setSynthValue("tapPan1", 1.0.rand);
				argModule.setSynthValue("tapPan2", 1.0.rand);
				argModule.setSynthValue("tapPan3", 1.0.rand);
				argModule.setSynthValue("tapPan4", 1.0.rand);
				argModule.setSynthValue("tapPan5", 1.0.rand);
				argModule.setSynthValue("tapPan6", 1.0.rand);
				argModule.setSynthValue("tapPan7", 1.0.rand);
				argModule.setSynthValue("tapPan8", 1.0.rand);
			},
		],
	];
}

*arrLevelPresets { arg argModule;
	^[
		[	"Full volume",
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 1);
				argModule.setSynthValue("tapLevel3", 1);
				argModule.setSynthValue("tapLevel4", 1);
				argModule.setSynthValue("tapLevel5", 1);
				argModule.setSynthValue("tapLevel6", 1);
				argModule.setSynthValue("tapLevel7", 1);
				argModule.setSynthValue("tapLevel8", 1);
			},
		],
		[	"Linear fade-out", 
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 0.875);
				argModule.setSynthValue("tapLevel3", 0.75);
				argModule.setSynthValue("tapLevel4", 0.625);
				argModule.setSynthValue("tapLevel5", 0.5);
				argModule.setSynthValue("tapLevel6", 0.375);
				argModule.setSynthValue("tapLevel7", 0.25);
				argModule.setSynthValue("tapLevel8", 0.125);
			},
		],
		[	"Accelerating fade-out",
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 0.907);
				argModule.setSynthValue("tapLevel3", 0.809);
				argModule.setSynthValue("tapLevel4", 0.701);
				argModule.setSynthValue("tapLevel5", 0.581);
				argModule.setSynthValue("tapLevel6", 0.446);
				argModule.setSynthValue("tapLevel7", 0.286);
				argModule.setSynthValue("tapLevel8", 0.077);
			},
		],
		[	"Decelerating fade-out",
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 0.765625);
				argModule.setSynthValue("tapLevel3", 0.5625);
				argModule.setSynthValue("tapLevel4", 0.390625);
				argModule.setSynthValue("tapLevel5", 0.25);
				argModule.setSynthValue("tapLevel6", 0.140625);
				argModule.setSynthValue("tapLevel7", 0.0625);
				argModule.setSynthValue("tapLevel8", 0.015625);
			},
		],
		[	"Linear fade-in",
			{
				argModule.setSynthValue("tapLevel1", 0.125);
				argModule.setSynthValue("tapLevel2", 0.25);
				argModule.setSynthValue("tapLevel3", 0.375);
				argModule.setSynthValue("tapLevel4", 0.5);
				argModule.setSynthValue("tapLevel5", 0.625);
				argModule.setSynthValue("tapLevel6", 0.75);
				argModule.setSynthValue("tapLevel7", 0.875);
				argModule.setSynthValue("tapLevel8", 1);
			},
		],
		[	"Accelerating fade-in",
			{
				argModule.setSynthValue("tapLevel1", 0.015625);
				argModule.setSynthValue("tapLevel2", 0.0625);
				argModule.setSynthValue("tapLevel3", 0.140625);
				argModule.setSynthValue("tapLevel4", 0.25);
				argModule.setSynthValue("tapLevel5", 0.390625);
				argModule.setSynthValue("tapLevel6", 0.5625);
				argModule.setSynthValue("tapLevel7", 0.765625);
				argModule.setSynthValue("tapLevel8", 1);
			},
		],
		[	"Decelerating fade-in",
			{
				argModule.setSynthValue("tapLevel1", 0.077);
				argModule.setSynthValue("tapLevel2", 0.286);
				argModule.setSynthValue("tapLevel3", 0.446);
				argModule.setSynthValue("tapLevel4", 0.581);
				argModule.setSynthValue("tapLevel5", 0.701);
				argModule.setSynthValue("tapLevel6", 0.809);
				argModule.setSynthValue("tapLevel7", 0.907);
				argModule.setSynthValue("tapLevel8", 1);
			},
		],
		[	"Randomise",
			{
				argModule.setSynthValue("tapLevel1", 1.0.rand);
				argModule.setSynthValue("tapLevel2", 1.0.rand);
				argModule.setSynthValue("tapLevel3", 1.0.rand);
				argModule.setSynthValue("tapLevel4", 1.0.rand);
				argModule.setSynthValue("tapLevel5", 1.0.rand);
				argModule.setSynthValue("tapLevel6", 1.0.rand);
				argModule.setSynthValue("tapLevel7", 1.0.rand);
				argModule.setSynthValue("tapLevel8", 1.0.rand);
			},
		],
	];
}

*arrRatioPresets { arg argModule;
	^[
		[	"Straight",
			{
				argModule.setSynthValue("tapRatio1", 0.125);
				argModule.setSynthValue("tapRatio2", 0.25);
				argModule.setSynthValue("tapRatio3", 0.375);
				argModule.setSynthValue("tapRatio4", 0.5);
				argModule.setSynthValue("tapRatio5", 0.625);
				argModule.setSynthValue("tapRatio6", 0.75);
				argModule.setSynthValue("tapRatio7", 0.875);
				argModule.setSynthValue("tapRatio8", 1);
			},
		],
		[	"Swung 3",
			{
				argModule.setSynthValue("tapRatio1", 0.1667);
				argModule.setSynthValue("tapRatio2", 0.25);
				argModule.setSynthValue("tapRatio3", 0.4167);
				argModule.setSynthValue("tapRatio4", 0.5);
				argModule.setSynthValue("tapRatio5", 0.6667);
				argModule.setSynthValue("tapRatio6", 0.75);
				argModule.setSynthValue("tapRatio7", 0.9167);
				argModule.setSynthValue("tapRatio8", 1);
			},
		],
		[	"Swung 4",
			{
				argModule.setSynthValue("tapRatio1", 0.1875);
				argModule.setSynthValue("tapRatio2", 0.25);
				argModule.setSynthValue("tapRatio3", 0.4375);
				argModule.setSynthValue("tapRatio4", 0.5);
				argModule.setSynthValue("tapRatio5", 0.6875);
				argModule.setSynthValue("tapRatio6", 0.75);
				argModule.setSynthValue("tapRatio7", 0.9375);
				argModule.setSynthValue("tapRatio8", 1);
			},
		],
		[	"Randomise",
			{
				argModule.setSynthValue("tapRatio1", 1.0.rand);
				argModule.setSynthValue("tapRatio2", 1.0.rand);
				argModule.setSynthValue("tapRatio3", 1.0.rand);
				argModule.setSynthValue("tapRatio4", 1.0.rand);
				argModule.setSynthValue("tapRatio5", 1.0.rand);
				argModule.setSynthValue("tapRatio6", 1.0.rand);
				argModule.setSynthValue("tapRatio7", 1.0.rand);
				argModule.setSynthValue("tapRatio8", 1.0.rand);
			},
		],
	];
}

}

/*
CODE TO GENERATE PRESET VALUES
7.do({arg item, i; ((i+1) / 7).round(0.0001).postln; });

8.do({arg item, i; ((i+1) / 8).round(0.0001).postln; });

8.do({arg item, i; ((i+1) / 8).round(0.0001).squared.postln; });
8.do({arg item, i; ( ((i+1) / 8) ** 0.5 ).round(0.0001).postln; });

8.collect({arg item, i; ((i+1) / 8).squared.round(0.0001) }).postln;


( // RATIO straight
var a, b, c, d, e;
8.do({arg item, i; a = a.add(((i+1) / 8)); });
a.round(0.0001).postln;
" ";
)

( // RATIO swung 3
var a, b, c, d, e;
8.do({arg item, i; 
	b = ((i+1) / 8);
	if (i%2 == 0, {b = b * (4/3); });
	a = a.add(b); 
});
a.round(0.0001).postln;
" ";
)

( // RATIO swung 4
var a, b, c, d, e;
8.do({arg item, i; 
	b = ((i+1) / 8);
	if (i%2 == 0, {b = b * (3/2); });
	a = a.add(b); 
});
a.round(0.0001).postln;
" ";
)

*/


