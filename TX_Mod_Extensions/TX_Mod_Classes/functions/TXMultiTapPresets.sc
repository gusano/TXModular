// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXMultiTapPresets {

// Note this is for 4-tap presets. See class TXMultiTap8Presets for 8-tap presets.

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
			},
		],
		[	"Left to Right",
			{
				argModule.setSynthValue("tapPan1", 0);
				argModule.setSynthValue("tapPan2", 0.333);
				argModule.setSynthValue("tapPan3", 0.666);
				argModule.setSynthValue("tapPan4", 1);
			},
		],
		[	"Right to Left",
			{
				argModule.setSynthValue("tapPan1", 1);
				argModule.setSynthValue("tapPan2", 0.666);
				argModule.setSynthValue("tapPan3", 0.333);
				argModule.setSynthValue("tapPan4", 0);
			},
		],
		[	"Widening",
			{
				argModule.setSynthValue("tapPan1", 0.75);
				argModule.setSynthValue("tapPan2", 0.25);
				argModule.setSynthValue("tapPan3", 1);
				argModule.setSynthValue("tapPan4", 0);
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
			},
		],
		[	"Linear fade-out",
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 0.75);
				argModule.setSynthValue("tapLevel3", 0.5);
				argModule.setSynthValue("tapLevel4", 0.25);
			},
		],
		[	"Accelerating fade-out",
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 0.8333);
				argModule.setSynthValue("tapLevel3", 0.5);
				argModule.setSynthValue("tapLevel4", 0.13333);
			},
		],
		[	"Decelerating fade-out",
			{
				argModule.setSynthValue("tapLevel1", 1);
				argModule.setSynthValue("tapLevel2", 0.6666);
				argModule.setSynthValue("tapLevel3", 0.5);
				argModule.setSynthValue("tapLevel4", 0.333);
			},
		],
		[	"Linear fade-in",
			{
				argModule.setSynthValue("tapLevel1", 0.25);
				argModule.setSynthValue("tapLevel2", 0.5);
				argModule.setSynthValue("tapLevel3", 0.75);
				argModule.setSynthValue("tapLevel4", 1);
			},
		],
		[	"Accelerating fade-in",
			{
				argModule.setSynthValue("tapLevel1", 0.13333);
				argModule.setSynthValue("tapLevel2", 0.5);
				argModule.setSynthValue("tapLevel3", 0.8333);
				argModule.setSynthValue("tapLevel4", 1);
			},
		],
		[	"Decelerating fade-in",
			{
				argModule.setSynthValue("tapLevel1", 0.333);
				argModule.setSynthValue("tapLevel2", 0.5);
				argModule.setSynthValue("tapLevel3", 0.6666);
				argModule.setSynthValue("tapLevel4", 1);
			},
		],
		[	"Varying 1",
			{
				argModule.setSynthValue("tapLevel1", 0.333);
				argModule.setSynthValue("tapLevel2", 1);
				argModule.setSynthValue("tapLevel3", 0.25);
				argModule.setSynthValue("tapLevel4", 0.5);
			},
		],
		[	"Varying 2",
			{
				argModule.setSynthValue("tapLevel1", 0.25);
				argModule.setSynthValue("tapLevel2", 0.6);
				argModule.setSynthValue("tapLevel3", 0.9);
				argModule.setSynthValue("tapLevel4", 0.2);
			},
		],
	];
}

*arrRatioPresets { arg argModule;
	^[
		[	"Straight",
			{
				argModule.setSynthValue("tapRatio1", 0.25);
				argModule.setSynthValue("tapRatio2", 0.5);
				argModule.setSynthValue("tapRatio3", 0.75);
				argModule.setSynthValue("tapRatio4", 1);
			},
		],
		[	"Swung 3",
			{
				argModule.setSynthValue("tapRatio1", 0.33333333);
				argModule.setSynthValue("tapRatio2", 0.5);
				argModule.setSynthValue("tapRatio3", 0.83333333);
				argModule.setSynthValue("tapRatio4", 1);
			},
		],
		[	"Swung 4",
			{
				argModule.setSynthValue("tapRatio1", 0.375);
				argModule.setSynthValue("tapRatio2", 0.5);
				argModule.setSynthValue("tapRatio3", 0.875);
				argModule.setSynthValue("tapRatio4", 1);
			},
		],
	];
}

}

