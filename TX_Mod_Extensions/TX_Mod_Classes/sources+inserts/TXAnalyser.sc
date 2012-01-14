// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAnalyser : TXModuleBase {		// Audio In module 

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

	var  holdTrigID, holdTrigID2, holdOSCResp, holdOSCResp2;
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
		["Flatness", [0]],
		["Percentile", [1]],
		["Centroid", [2]],
		["Amplitude", [3]],
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
	holdTrigID = UniqueID.next;
	holdTrigID2 = UniqueID.next;
	holdPercentileMin = 999999;
	holdPercentileMax = 0;
	holdCentroidMin = 999999;
	holdCentroidMax = 0;
	
	arrSynthArgSpecs = [
		["out", 0, 0],
		["bufnumFFT", 0, 0],
		["audioIn", 0, 0],
		["sendPercentile", 0, 0],
		["sendCentroid", 0, 0],
		["percentileMin", 60, 0], 
		["percentileMax", 22000, 0],
		["centroidMin", 60, 0], 
		["centroidMax", 22000, 0],
		["threshold", 0.5, 0],
	]; 

	arrOptions = [1, 1, 1, 1, 18, 3, 1];
	arrOptionData = [
		[
			["Off", { 0; }],
			["On", {arg chain; SpecFlatness.kr(chain)}],
		],
		[
			["Off", { 0; }],
			["On", {arg chain, percentileMin, percentileMax, percentile; SpecPcile.kr(chain, 
				percentile, 1).max(percentileMin).min(percentileMax)}],
		],
		[
			["Off", { 0; }],
			["On", {arg chain, centroidMin, centroidMax; SpecCentroid.kr(chain).max(centroidMin).min(centroidMax)}],
		],
		[
			["Off", { 0; }],
			["On", {arg in; Amplitude.kr(in, 0.01, 0.01)}],
		],
		[
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
			["power", \power  ],
			["magsum", \magsum  ],
			["complex ", \complex  ],
			["rcomplex - default ", \rcomplex  ],
			["phase", \phase  ],
			["wphase", \wphase  ],
			["mkl ", \mkl  ],
		],
		[
			["Off", { 0; }],
			["On", {arg chain, threshold, type; Onsets.kr(chain, threshold, type)}],
		],
	];

	synthDefFunc = { arg out, bufnumFFT, audioIn, sendPercentile, sendCentroid, percentileMin, percentileMax,
			centroidMin, centroidMax, threshold;
		var lagtime, imp;
		var in, chain, flatness, percentileVal, centroid, percentile, percentileOut, 
			centroidOut, amplitude, detectType, onsets;
		
		in = InFeedback.ar(audioIn,1);
		
		chain = FFT(bufnumFFT, in);
		
		// select function based on arrOptions
		flatness = this.getSynthOption(0).value(chain);

		percentileVal = this.getSynthOption(4);
		percentile = this.getSynthOption(1).value(chain, percentileMin, percentileMax, percentileVal);  
		centroid = this.getSynthOption(2).value(chain, centroidMin, centroidMax);

		// send current percentile & centroid if requested
		imp = Impulse.kr(10);
		SendTrig.kr((sendPercentile * imp), holdTrigID, percentile); 
		SendTrig.kr((sendCentroid * imp), holdTrigID2, centroid); 
		
		percentileOut = log(percentile/percentileMin) / log(percentileMax / percentileMin);
		centroidOut = log(centroid/centroidMin) / log(centroidMax / centroidMin);

		amplitude = this.getSynthOption(3).value(in);

		detectType = this.getSynthOption(5);
		onsets =  this.getSynthOption(6).value(chain, threshold, detectType);

		Out.kr(out, [flatness, percentileOut, centroidOut, amplitude, onsets]);
	};
	guiSpecArray = [
		["SpacerLine", 2], 
		["TextBar", "To save CPU, please turn off unused analysis processes:", 350],
		["DividingLine"], 
		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Spectral Flatness", arrOptionData, 0], 
		["DividingLine"], 
		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Spectral Percentile", arrOptionData, 1], 
		["NextLine"], 
		["SynthOptionPopup", "Percentile", arrOptionData, 4, 290], 
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
		["TXCheckBox", "Detect min & max", "sendPercentile", 200],
		["ActionButton", "Copy detected values to min/max", {this.copyMinMaxPercentile}, 200, TXColor.white, TXColor.sysGuiCol2],
		["NextLine"], 
		["TXStaticText", "Detected min", {holdPercentileMin.asString}, 
			{arg view; holdPercentileMinView = view.textView;}, 150], 
		["TXStaticText", "Detected max", {holdPercentileMax.asString}, 
			{arg view; holdPercentileMaxView = view.textView; this.resetMinMaxPercentileDisplay;}, 150], 
		["ActionButton", "Reset", {holdPercentileMin = 999999; holdPercentileMax = 0; this.resetMinMaxPercentileDisplay;}, 
			120, TXColor.white, TXColor.sysDeleteCol],

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
		["TXCheckBox", "Detect min & max", "sendCentroid", 200],
		["ActionButton", "Copy detected values to min/max", {this.copyMinMaxCentroid}, 200, TXColor.white, TXColor.sysGuiCol2],
		["NextLine"], 
		["TXStaticText", "Detected min", {holdCentroidMin.asString}, 
			{arg view; holdCentroidMinView = view.textView;}, 150], 
		["TXStaticText", "Detected max", {holdCentroidMax.asString}, 
			{arg view; holdCentroidMaxView = view.textView; this.resetMinMaxCentroidDisplay;}, 150], 
		["ActionButton", "Reset", {holdCentroidMin = 999999; holdCentroidMax = 0; this.resetMinMaxCentroidDisplay;}, 
			120, TXColor.white, TXColor.sysDeleteCol],

		["DividingLine"], 
		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Amplitude Follow", arrOptionData, 3], 
		["DividingLine"], 
		["SpacerLine", 4], 
		["SynthOptionCheckBox", "Onset Detection", arrOptionData, 6], 
		["NextLine"], 
		["EZslider", "Threshold", ControlSpec(0, 1), "threshold"],
		["NextLine"], 
		["SynthOptionPopup", "Detection type", arrOptionData, 5, 250], 
		["ActionButton", "Restore Defaults", {
				this.setSynthValue("threshold", 0.5); 
				this.setSynthOption(5, \rcomplex ); 
				system.flagGuiUpd;
			}, 120, TXColour.white, TXColour.sysDeleteCol], 
		["DividingLine"], 

	];
	arrActionSpecs = this.buildActionSpecs(guiSpecArray);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	make buffers, load the synthdef and create the synth
	this.makeBuffersAndSynth(arrBufferSpecs);
	this.oscActivate;
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
}

oscDeactivate { 
	//	remove responder
	if (holdOSCResp.class == OSCresponderNode, {holdOSCResp.remove});
	if (holdOSCResp2.class == OSCresponderNode, {holdOSCResp2.remove});
}

setMinMaxPercentile {arg argPercentile;
	var holdPercentile = argPercentile;
	if (holdPercentile.notNil, {
		if (holdPercentile < holdPercentileMin, {
			holdPercentileMin = holdPercentile.round(0.1);
			if (system.isModDisplay(this) == true, {
				{holdPercentileMinView.string = holdPercentileMin.asString;}.defer;
			});
		});
		if (holdPercentile > holdPercentileMax, {
			holdPercentileMax = holdPercentile.round(0.1);
			if (system.isModDisplay(this) == true, {
				{holdPercentileMaxView.string = holdPercentileMax.asString;}.defer;
			});
		});
	});
}

setMinMaxCentroid {arg argCentroid;
	var holdCentroid = argCentroid;
	if (holdCentroid.notNil, {
		if (holdCentroid < holdCentroidMin, {
			holdCentroidMin = holdCentroid.round(0.1);
			if (system.isModDisplay(this) == true, {
				{holdCentroidMinView.string = holdCentroidMin.asString;}.defer;
			});
		});
		if (holdCentroid > holdCentroidMax, {
			holdCentroidMax = holdCentroid.round(0.1);
			if (system.isModDisplay(this) == true, {
				{holdCentroidMaxView.string = holdCentroidMax.asString;}.defer;
			});
		});
	});
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
		this.setSynthValue("CentroidMin", holdControlSpec.constrain(holdCentroidMin));
	});
	if (holdCentroidMax > 0, {
		this.setSynthValue("CentroidMax", holdControlSpec.constrain(holdCentroidMax));
	});
	system.flagGuiUpd;
}

}

