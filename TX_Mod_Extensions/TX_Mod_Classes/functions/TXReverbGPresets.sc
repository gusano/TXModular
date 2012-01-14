// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXReverbGPresets {

*initClass{
	//	
	// set class specific variables
	//	
} 

*arrPresets { arg argModule;
	^[
		[	"Bathroom - as send effect",
			{	argModule.arrOptions = [1];
				argModule.setSynthValue("reverbTime", 0.0505);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.48);
				argModule.setSynthValue("damping", 0.38);
				argModule.setSynthValue("inLevel", 0);
				argModule.setSynthValue("earlyLevel", -5.dbamp);
				argModule.setSynthValue("tailLevel", -7.dbamp);
			},
		],
		[	"Bathroom - as insert effect",
			{	argModule.arrOptions = [1];
				argModule.setSynthValue("reverbTime", 0.0505);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.48);
				argModule.setSynthValue("damping", 0.38);
				argModule.setSynthValue("inLevel", -6.dbamp);
				argModule.setSynthValue("earlyLevel", -11.dbamp);
				argModule.setSynthValue("tailLevel", -13.dbamp);
			},
		],
		[	"Living room - as send effect",
			{	argModule.arrOptions = [2];
				argModule.setSynthValue("reverbTime", 0.1151);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.95);
				argModule.setSynthValue("damping", 0.9);
				argModule.setSynthValue("inLevel", 0);
				argModule.setSynthValue("earlyLevel", -5.dbamp);
				argModule.setSynthValue("tailLevel", -7.dbamp);
			},
		],
		[	"Living room - as insert effect",
			{	argModule.arrOptions = [2];
				argModule.setSynthValue("reverbTime", 0.1151);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.95);
				argModule.setSynthValue("damping", 0.9);
				argModule.setSynthValue("inLevel", -3.dbamp);
				argModule.setSynthValue("earlyLevel", -15.dbamp);
				argModule.setSynthValue("tailLevel", -17.dbamp);
			},
		],
		[	"Church - as send effect",
			{	argModule.arrOptions = [4];
				argModule.setSynthValue("reverbTime", 0.47979);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.19);
				argModule.setSynthValue("damping", 0.59);
				argModule.setSynthValue("inLevel", 0);
				argModule.setSynthValue("earlyLevel", -5.dbamp);
				argModule.setSynthValue("tailLevel", -7.dbamp);
			},
		],
		[	"Church - as insert effect",
			{	argModule.arrOptions = [4];
				argModule.setSynthValue("reverbTime", 0.47979);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.19);
				argModule.setSynthValue("damping", 0.59);
				argModule.setSynthValue("inLevel", -3.dbamp);
				argModule.setSynthValue("earlyLevel", -9.dbamp);
				argModule.setSynthValue("tailLevel", -11.dbamp);
			},
		],
		[	"Cathedral - as send effect",
			{	argModule.arrOptions = [6];
				argModule.setSynthValue("reverbTime", 0.5959);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.34);
				argModule.setSynthValue("damping", 0.9);
				argModule.setSynthValue("inLevel", 0);
				argModule.setSynthValue("earlyLevel", -7.dbamp);
				argModule.setSynthValue("tailLevel", -5.dbamp);
			},
		],
		[	"Cathedral - as insert effect",
			{	argModule.arrOptions = [6];
				argModule.setSynthValue("reverbTime", 0.5959);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 10.0);
				argModule.setSynthValue("inDamping", 0.34);
				argModule.setSynthValue("damping", 0.9);
				argModule.setSynthValue("inLevel", -3.dbamp);
				argModule.setSynthValue("earlyLevel", -11.dbamp);
				argModule.setSynthValue("tailLevel", -9.dbamp);
			},
		],
		[	"Canyon - as send effect",
			{	argModule.arrOptions = [7];
				argModule.setSynthValue("reverbTime", 1);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 100.0);
				argModule.setSynthValue("inDamping", 0.51);
				argModule.setSynthValue("damping", 0.57);
				argModule.setSynthValue("inLevel", 0);
				argModule.setSynthValue("earlyLevel", -9.dbamp);
				argModule.setSynthValue("tailLevel", -3.dbamp);
			},
		],
		[	"Canyon - as insert effect",
			{	argModule.arrOptions = [7];
				argModule.setSynthValue("reverbTime", 1);
				argModule.setSynthValue("reverbTimeMin", 0.1);
				argModule.setSynthValue("reverbTimeMax", 100.0);
				argModule.setSynthValue("inDamping", 0.51);
				argModule.setSynthValue("damping", 0.57);
				argModule.setSynthValue("inLevel", -5.dbamp);
				argModule.setSynthValue("earlyLevel", -26.dbamp);
				argModule.setSynthValue("tailLevel", -20.dbamp);
			},
		],
	];
}

}

