// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAnalyser2 : TXModuleBase {		// Audio In module 

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
	classvar	holdControlSpec;

	var	displayOption;
	var  holdTrigID, holdTrigID2, holdTrigID3, holdOSCResp, holdOSCResp2, holdOSCResp3;
	var	holdFlatnessMin, holdFlatnessMinView, holdFlatnessMax, holdFlatnessMaxView;
	var	holdPercentileMin, holdPercentileMinView, holdPercentileMax, holdPercentileMaxView;
	var	holdCentroidMin, holdCentroidMinView, holdCentroidMax, holdCentroidMaxView;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	holdControlSpec = ControlSpec(60, 22000, 'lin', 1, 0);
	defaultName = "Analyser";
	moduleRate = "control";
	moduleType = "source";
	arrAudSCInBusSpecs = [ 
		 ["Audio in", 1, "audioIn"]
	];	
	arrCtlSCInBusSpecs = [];	
	noOutChannels = 5;
	arrOutBusSpecs = [ 
		["Amplitude", [0]],
		["Flatness", [1]],
		["Percentile", [2]],
		["Centroid", [3]],
		["Onsets", [4]],
	];	
//	arrBufferSpecs = [ ["bufnumFFT", 2048, 1]];
	arrBufferSpecs = [ ["bufnumFFT", 1024, 1]];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	displayOption = "showInfo1";
	holdTrigID = UniqueID.next;
	holdTrigID2 = UniqueID.next;
	holdTrigID3 = UniqueID.next;
	holdPercentileMin = 999999;
	holdPercentileMax = 0;
	holdCentroidMin = 999999;
	holdCentroidMax = 0;
	holdFlatnessMin = 1;
	holdFlatnessMax = 0;
	
	arrSynthArgSpecs = [
		["out", 0, 0],
		["bufnumFFT", 0, 0],
		["audioIn", 0, 0],
		["flatnessMin", 0, 0], 
		["flatnessMax", 1, 0],
		["percentileMin", 60, 0], 
		["percentileMax", 22000, 0],
		["centroidMin", 60, 0], 
		["centroidMax", 22000, 0],
		["threshold", 0.5, 0],
	]; 

	arrOptions = [1, 1, 1, 18, 3, 1, 0, 0, 0, 0, 0, 0];
	arrOptionData = [
		[
			// 0 - spectral flatness detection on/off
			["Off", { 0; }],
			["On", {arg chain, flatnessMin, flatnessMinMax;
				SpecFlatness.kr(chain).max(flatnessMin).min(flatnessMinMax)}],
		],
		[
			// 1 - spectral percentile detection on/off
			["Off", { 0; }],
			["On", {arg chain, percentileMin, percentileMax, percentile; SpecPcile.kr(chain, 
				percentile, 1).max(percentileMin).min(percentileMax)}],
		],
		[
			// 2 - spectral centroid detection on/off
			["Off", { 0; }],
			["On", {arg chain, centroidMin, centroidMax; SpecCentroid.kr(chain).max(centroidMin).min(centroidMax)}],
		],
		[
			// 3 - percentile value
			["5 %", 0.5 ],
			["10 %", 0.1 ],
			["15 %", 0.15 ],
			["20 %", 0.2 ],
			["25 %", 0.25 ],
			["30 %", 0.3 ],
			["35 %", 0.35 ],
			["40 %", 0.4 ],
			["45 %", 0.45 ],
			["50 %", 0.5 ],
			["55 %", 0.55 ],
			["60 %", 0.6 ],
			["65 %", 0.65 ],
			["70 %", 0.7 ],
			["75 %", 0.75 ],
			["80 %", 0.8 ],
			["85 %", 0.85 ],
			["90 %", 0.9 ],
			["95 % - default", 0.95 ],
		],
		[
			// 4 - onset detection type
			["power", \power  ],
			["magsum", \magsum  ],
			["complex ", \complex  ],
			["rcomplex - default ", \rcomplex  ],
			["phase", \phase  ],
			["wphase", \wphase  ],
			["mkl ", \mkl  ],
		],
		[
			// 5 - onset detection on/off
			["Off", { 0; }],
			["On", {arg chain, threshold, type; Onsets.kr(chain, threshold, type)}],
		],
		[
			// 6 - spectral percentile analysis on/off
			["Off", { 0; }],
			["On", {arg chain, percentile, ampGate; 
				var freq;
				freq = SpecPcile.kr(chain, percentile, 1).max(60).min(22000);
				SendTrig.kr((Impulse.kr(10) * ampGate), holdTrigID, freq);
			}],

		],
		[
			// 7 - spectral centroid analysis on/off
			["Off", { 0; }],
			["On", {arg chain, ampGate; 
				var freq;
				freq = SpecCentroid.kr(chain).max(60).min(22000);
				SendTrig.kr((Impulse.kr(10) * ampGate), holdTrigID2, freq);
			}],
		],
		[
			// 8 - spectral percentile update function
			["Off", {  }],
			["On", {this.copyMinMaxPercentile; }],
		],
		[
			// 9 - spectral centroid update function
			["Off", {  }],
			["On", {this.copyMinMaxCentroid;}],
		],
		[
			// 10 - flatness analysis on/off
			["Off", { 0; }],
			["On", {arg chain, ampGate; 
				var holdFlatness;
				holdFlatness = SpecFlatness.kr(chain).max(0).min(1);
				SendTrig.kr((Impulse.kr(10) * ampGate), holdTrigID3, holdFlatness);
			}],
		],
		[
			// 11 - flatness update function
			["Off", {  }],
			["On", {this.copyMinMaxFlatness; }],
		],
	];

	synthDefFunc = { arg out, bufnumFFT, audioIn, flatnessMin, flatnessMinMax, percentileMin, percentileMax,
			centroidMin, centroidMax, threshold;
		var lagtime, imp;
		var in, chain, flatness, percentileVal, centroid, percentile, percentileOut, 
			centroidOut, amplitude, ampGate, detectType, onsets;
		var	flatnessAnalysisFunc, percentileAnalysisFunc, centroidAnalysisFunc;
		
		in = InFeedback.ar(audioIn,1);
		
		amplitude = Amplitude.kr(in, 0.01, 0.01);
		ampGate = (amplitude - 0.02).ceil;

		chain = FFT(bufnumFFT, in);
		
		// select function based on arrOptions
		flatness = Gate.kr(this.getSynthOption(0).value(chain, flatnessMin, flatnessMinMax), ampGate);
		percentileVal = this.getSynthOption(3);
		percentile = this.getSynthOption(1).value(chain, percentileMin, percentileMax, percentileVal);  
		centroid = this.getSynthOption(2).value(chain, centroidMin, centroidMax);

		// send current flatness, percentile & centroid if requested
		flatnessAnalysisFunc = this.getSynthOption(10);
		flatnessAnalysisFunc.value(chain, ampGate);
		percentileAnalysisFunc = this.getSynthOption(6);
		percentileAnalysisFunc.value(chain, percentileVal, ampGate);
		centroidAnalysisFunc = this.getSynthOption(7);
		centroidAnalysisFunc.value(chain, ampGate);
		
		percentileOut = Gate.kr(log(percentile/percentileMin) / log(percentileMax / percentileMin), ampGate);
		centroidOut = Gate.kr(log(centroid/centroidMin) / log(centroidMax / centroidMin), ampGate);


		detectType = this.getSynthOption(4);
		onsets =  this.getSynthOption(5).value(chain, threshold, detectType);


		Out.kr(out, [amplitude, flatness, percentileOut, centroidOut, onsets]);
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["SynthOptionCheckBox", "Spectral Flatness", arrOptionData, 0], 
		["EZNumber", "Flatness min", ControlSpec(0, 1), "flatnessMin", nil, 80, 66],
		["EZNumber", "Flatness max", ControlSpec(0, 1), "flatnessMax", nil, 80, 66],
		["SynthOptionCheckBox", "Detect min & max", arrOptionData, 10, 150], 
		["SynthOptionCheckBox", "Automatically copy detected values to min/max", arrOptionData, 11], 
		["SynthOptionCheckBox", "Spectral Percentile", arrOptionData, 1], 
		["SynthOptionPopup", "Percentile", arrOptionData, 3, 290], 
		["EZNumber", "Min freq", holdControlSpec, "percentileMin", nil, 80, 66],
		["EZNumber", "Max freq", holdControlSpec, "percentileMax", nil, 80, 66],
		["SynthOptionCheckBox", "Detect min & max", arrOptionData, 6, 150], 
		["SynthOptionCheckBox", "Automatically copy detected values to min/max", arrOptionData, 8], 
		["SynthOptionCheckBox", "Spectral Centroid", arrOptionData, 2], 
		["EZNumber", "Centroid min", holdControlSpec, "centroidMin", nil, 80, 66],
		["EZNumber", "Centroid max", holdControlSpec, "centroidMax", nil, 80, 66],
		["SynthOptionCheckBox", "Detect min & max", arrOptionData, 7, 150], 
		["SynthOptionCheckBox", "Automatically copy detected values to min/max", arrOptionData, 9], 
		["SynthOptionCheckBox", "Onset Detection", arrOptionData, 5], 
		["EZslider", "Threshold", ControlSpec(0, 1), "threshold"],
		["SynthOptionPopup", "Detection type", arrOptionData, 4, 250], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
	this.oscActivate;
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Controls 1", {displayOption = "showInfo1"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showEnv")], 
		["Spacer", 3], 
		["ActionButton", "Controls 2", {displayOption = "showInfo2"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showMIDI")], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showInfo1", {
		guiSpecArray = guiSpecArray ++[
		["TextBar", "To save CPU, please turn off unused analysis processes:", 350],
		["DividingLine"], 
		["SpacerLine", 4], 

		["SynthOptionCheckBox", "Spectral Flatness", arrOptionData, 0], 
		["NextLine"], 
		["EZNumber", "Flatness min", ControlSpec(0, 1), "flatnessMin", nil, 80, 66],
		["EZNumber", "Flatness max", ControlSpec(0, 1), "flatnessMax", nil, 80, 66],
		["ActionButton", "Restore Defaults", {
				this.setSynthValue("flatnessMin", 0); 
				this.setSynthValue("flatnessMax", 1); 
				system.flagGuiUpd;
			}, 120, TXColour.white, TXColour.sysDeleteCol], 

		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Detect min & max", arrOptionData, 10, 150], 
		["ActionButton", "Copy detected values to min/max", 
			{this.copyMinMaxFlatness}, 200, TXColor.white, TXColor.sysGuiCol2],
		["NextLine"], 
		["TXStaticText", "Detected min", {holdFlatnessMin.asString}, 
			{arg view; holdFlatnessMinView = view.textView;}, 150], 
		["TXStaticText", "Detected max", {holdFlatnessMax.asString}, 
			{arg view; holdFlatnessMaxView = view.textView; this.resetMinMaxFlatnessDisplay;}, 150], 
		["ActionButton", "Reset", 
			{holdFlatnessMin = 1; holdFlatnessMax = 0; this.resetMinMaxFlatnessDisplay;}, 
			120, TXColor.white, TXColor.sysDeleteCol],
		["NextLine"], 
		["SynthOptionCheckBox", "Automatically copy detected values to min/max", arrOptionData, 11, nil, 
			{this.copyMinMaxFlatness}], 

		["DividingLine"], 
		["SpacerLine", 4], 

		["SynthOptionCheckBox", "Spectral Percentile", arrOptionData, 1], 
		["NextLine"], 
		["SynthOptionPopup", "Percentile", arrOptionData, 3, 290], 
		["NextLine"], 
		["EZNumber", "Min freq", holdControlSpec, "percentileMin", nil, 80, 66],
		["EZNumber", "Max freq", holdControlSpec, "percentileMax", nil, 80, 66],
		["ActionButton", "Restore Defaults", {
				this.setSynthValue("percentileMin", 60); 
				this.setSynthValue("percentileMax", 22000); 
				this.setSynthOption(4, 0.95); 
				system.flagGuiUpd;
			}, 120, TXColour.white, TXColour.sysDeleteCol], 

		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Detect min & max", arrOptionData, 6, 150], 
		["ActionButton", "Copy detected values to min/max", 
			{this.copyMinMaxPercentile}, 200, TXColor.white, TXColor.sysGuiCol2],
		["NextLine"], 
		["TXStaticText", "Detected min", {holdPercentileMin.asString}, 
			{arg view; holdPercentileMinView = view.textView;}, 150], 
		["TXStaticText", "Detected max", {holdPercentileMax.asString}, 
			{arg view; holdPercentileMaxView = view.textView; this.resetMinMaxPercentileDisplay;}, 150], 
		["ActionButton", "Reset", 
			{holdPercentileMin = 999999; holdPercentileMax = 0; this.resetMinMaxPercentileDisplay;}, 
			120, TXColor.white, TXColor.sysDeleteCol],
		["NextLine"], 
		["SynthOptionCheckBox", "Automatically copy detected values to min/max", arrOptionData, 8, nil, 
			{this.copyMinMaxPercentile}], 

		["DividingLine"], 
		["SpacerLine", 4], 
	];
	});
	if (displayOption == "showInfo2", {
		guiSpecArray = guiSpecArray ++[
		["TextBar", "To save CPU, please turn off unused analysis processes:", 350],
		["DividingLine"], 
		["SpacerLine", 4], 

		["SynthOptionCheckBox", "Spectral Centroid", arrOptionData, 2], 
		["NextLine"], 
		["EZNumber", "Centroid min", holdControlSpec, "centroidMin", nil, 80, 66],
		["EZNumber", "Centroid max", holdControlSpec, "centroidMax", nil, 80, 66],
		["ActionButton", "Restore Defaults", {
				this.setSynthValue("centroidMin", 60); 
				this.setSynthValue("centroidMax", 22000); 
				system.flagGuiUpd;
			}, 120, TXColour.white, TXColour.sysDeleteCol], 

		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Detect min & max", arrOptionData, 7, 150], 
		["ActionButton", "Copy detected values to min/max", 
			{this.copyMinMaxCentroid}, 200, TXColor.white, TXColor.sysGuiCol2],
		["NextLine"], 
		["TXStaticText", "Detected min", {holdCentroidMin.asString}, 
			{arg view; holdCentroidMinView = view.textView;}, 150], 
		["TXStaticText", "Detected max", {holdCentroidMax.asString}, 
			{arg view; holdCentroidMaxView = view.textView; this.resetMinMaxCentroidDisplay;}, 150], 
		["ActionButton", "Reset", 
			{holdCentroidMin = 999999; holdCentroidMax = 0; this.resetMinMaxCentroidDisplay;}, 
			120, TXColor.white, TXColor.sysDeleteCol],
		["NextLine"], 
		["SynthOptionCheckBox", "Automatically copy detected values to min/max", arrOptionData, 9, nil, 
			{this.copyMinMaxCentroid}], 

		["DividingLine"], 
		["SpacerLine", 4], 

		["SynthOptionCheckBox", "Onset Detection", arrOptionData, 5], 
		["NextLine"], 
		["EZslider", "Threshold", ControlSpec(0, 1), "threshold"],
		["NextLine"], 
		["SynthOptionPopup", "Detection type", arrOptionData, 4, 250], 
		["ActionButton", "Restore Defaults", {
				this.setSynthValue("threshold", 0.5); 
				this.setSynthOption(5, \rcomplex ); 
				system.flagGuiUpd;
			}, 120, TXColour.white, TXColour.sysDeleteCol], 
		["DividingLine"], 

		];
	});
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
}

oscActivate {
	//	remove any previous OSCresponderNodes and add new
	this.oscDeactivate;
	holdOSCResp = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == holdTrigID, {this.setMinMaxPercentile(msg[3])} );
	}).add;
	holdOSCResp2 = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == holdTrigID2, {this.setMinMaxCentroid(msg[3])} );
	}).add;
	holdOSCResp3 = OSCresponderNode(system.server.addr,'/tr',{ arg time,responder,msg;
		if (msg[2] == holdTrigID3, {this.setMinMaxFlatness(msg[3])} );
	}).add;
}

oscDeactivate { 
	//	remove responder
	if (holdOSCResp.class == OSCresponderNode, {holdOSCResp.remove});
	if (holdOSCResp2.class == OSCresponderNode, {holdOSCResp2.remove});
}

setMinMaxFlatness {arg argFlatness;
	var holdFlatness = argFlatness;
	if (holdFlatness.notNil, {
		if (holdFlatness < holdFlatnessMin, {
			holdFlatnessMin = holdFlatness;
			if ((system.isModDisplay(this) == true) and: (displayOption == "showInfo1"), {
				{holdFlatnessMinView.string = holdFlatnessMin.asString;}.defer;
			});
		});
		if (holdFlatness > holdFlatnessMax, {
			holdFlatnessMax = holdFlatness;
			if ((system.isModDisplay(this) == true) and: (displayOption == "showInfo1"), {
				{holdFlatnessMaxView.string = holdFlatnessMax.asString;}.defer;
			});
		});
		// run update function
		this.getSynthOption(11).value;
	});
}


setMinMaxPercentile {arg argPercentile;
	var holdPercentile = argPercentile;
	if (holdPercentile.notNil, {
		if (holdPercentile < holdPercentileMin, {
			holdPercentileMin = holdPercentile.round(1);
			if ((system.isModDisplay(this) == true) and: (displayOption == "showInfo1"), {
				{holdPercentileMinView.string = holdPercentileMin.asString;}.defer;
			});
		});
		if (holdPercentile > holdPercentileMax, {
			holdPercentileMax = holdPercentile.round(1);
			if ((system.isModDisplay(this) == true) and: (displayOption == "showInfo1"), {
				{holdPercentileMaxView.string = holdPercentileMax.asString;}.defer;
			});
		});
		// run update function
		this.getSynthOption(8).value;
	});
}

setMinMaxCentroid {arg argCentroid;
	var holdCentroid = argCentroid;
	if (holdCentroid.notNil, {
		if (holdCentroid < holdCentroidMin, {
			holdCentroidMin = holdCentroid.round(1);
			if ((system.isModDisplay(this) == true) and: (displayOption == "showInfo2"), {
				{holdCentroidMinView.string = holdCentroidMin.asString;}.defer;
			});
		});
		if (holdCentroid > holdCentroidMax, {
			holdCentroidMax = holdCentroid.round(1);
			if ((system.isModDisplay(this) == true) and: (displayOption == "showInfo2"), {
				{holdCentroidMaxView.string = holdCentroidMax.asString;}.defer;
			});
		});
		// run update function
		this.getSynthOption(9).value;
	});
}

resetMinMaxFlatnessDisplay {arg argFlatness;
	{	if (holdFlatnessMin == 1, {holdFlatnessMinView.string = " ";}); 
		if (holdFlatnessMax == 0, {holdFlatnessMaxView.string = " ";});
	}.defer;
}


resetMinMaxPercentileDisplay {arg argPercentile;
	{	if (holdPercentileMin == 999999, {holdPercentileMinView.string = " ";}); 
		if (holdPercentileMax == 0, {holdPercentileMaxView.string = " ";});
	}.defer;
}
 
resetMinMaxCentroidDisplay {arg argCentroid;
	{	if (holdCentroidMin == 999999, {holdCentroidMinView.string = " ";}); 
		if (holdCentroidMax == 0, {holdCentroidMaxView.string = " ";});
	}.defer;
}
 
copyMinMaxFlatness {
	if (holdFlatnessMin < 1, {
		this.setSynthValue("flatnessMin", ControlSpec(0, 1).constrain(holdFlatnessMin));
	});
	if (holdFlatnessMax > 0, {
		this.setSynthValue("flatnessMax", ControlSpec(0, 1).constrain(holdFlatnessMax));
	});
	system.flagGuiUpd;
}

copyMinMaxPercentile {
	if (holdPercentileMin < 999999, {
		this.setSynthValue("percentileMin", holdControlSpec.constrain(holdPercentileMin));
	});
	if (holdPercentileMax > 0, {
		this.setSynthValue("percentileMax", holdControlSpec.constrain(holdPercentileMax));
	});
	system.flagGuiUpd;
}

copyMinMaxCentroid {
	if (holdCentroidMin < 999999, {
		this.setSynthValue("centroidMin", holdControlSpec.constrain(holdCentroidMin));
	});
	if (holdCentroidMax > 0, {
		this.setSynthValue("centroidMax", holdControlSpec.constrain(holdCentroidMax));
	});
	system.flagGuiUpd;
}

}

