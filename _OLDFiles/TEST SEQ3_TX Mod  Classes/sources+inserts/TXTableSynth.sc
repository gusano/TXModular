// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXTableSynth : TXModuleBase {

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
	classvar	<guiHeight=450;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	timeSpec;

	var	displayOption;
	var ratioView;
	var	envView;
	var arrWaveSpecs;
	var arrSlotData;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Table Synth";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Table position", 1, "modTablePosition", 0],
		["Pitch bend", 1, "modPitchbend", 0],
		["Delay", 1, "modDelay", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain level", 1, "modSustain", 0],
		["Sustain time", 1, "modSustainTime", 0],
		["Release", 1, "modRelease", 0],
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
	arrBufferSpecs = [ ["bufnumFirstTable", 1024, 1, 8] ]; // allocate 8 consecutive mono buffers
	timeSpec = ControlSpec(0.001, 20, \db);
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	autoModOptions = false;
	displayOption = "showWavetables";
	arrSynthArgSpecs = [
		["out", 0, 0],
		["gate", 1, 0],
		["note", 0, 0],
		["velocity", 0, 0],
		["keytrack", 1, \ir],
		["transpose", 0, \ir],
		["pitchbend", 0.5, defLagTime],
		["pitchbendMin", -2, defLagTime],
		["pitchbendMax", 2, defLagTime],
		["bufnumFirstTable", 0, \ir],
		["tablePosition", 0, defLagTime],
		["tablePositionMin", 0, defLagTime],
		["tablePositionMax", 16, defLagTime],
		["level", 0.5, \ir],
		["envtime", 0, \ir],
		["delay", 0, \ir],
		["attack", 0.005, \ir],
		["decay", 0.15, \ir],
		["sustain", 1, \ir],
		["sustainTime", 1, \ir],
		["release", 0.1, \ir],
		["intKey", 0, \ir],
		["modPitchbend", 0, defLagTime],
		["modTablePosition", 0, defLagTime],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],
  	]; 
	arrOptions = [0,0,0,0];
	arrOptionData = [
		[	
			["None", {arg val; val;}],
			["Step size 0.2", {arg val; Lag.kr(val.round(0.2), 0.03);}],
			["Step size 0.25", {arg val; Lag.kr(val.round(0.25), 0.03);}],
			["Step size 0.333", {arg val; Lag.kr(val.round(0.333333333), 0.03);}],
			["Step size 0.5", {arg val; Lag.kr(val.round(0.5), 0.03);}],
			["Step size 1", {arg val; Lag.kr(val.round(1), 0.03);}],
		],
		TXIntonation.arrOptionData,
		[	
			["linear", 'linear'],
//invalid		["exponential", 'exponential'],
			["sine", 'sine'],
			["welch", 'welch'],
//invalid		["step", 'step'],
			["slope +10 ", 10],
			["slope +9 ", 9],
			["slope +8 ", 8],
			["slope +7 ", 7],
			["slope +6 ", 6],
			["slope +5 ", 5],
			["slope +4 ", 4],
			["slope +3 ", 3],
			["slope +2 ", 2],
			["slope +1 ", 1],
			["slope -1", -1],
			["slope -2 ", -2],
			["slope -3 ", -3],
			["slope -4 ", -4],
			["slope -5 ", -5],
			["slope -6 ", -6],
			["slope -7 ", -7],
			["slope -8 ", -8],
			["slope -9 ", -9],
			["slope -10 ", -10]
		],
		[	
			["Sustain", 
				{arg del, att, dec, sus, sustime, rel, envCurve; 
					Env.dadsr(del, att, dec, sus, rel, 1, envCurve);
				}
			],
			["Fixed Length", 
				{arg del, att, dec, sus, sustime, rel, envCurve; 
					Env.new([0, 0, 1, sus, sus, 0], [del, att, dec, sustime, rel], envCurve, nil);
				}
			]
		],
	];
	synthDefFunc = { 
		arg out, gate, note, velocity, keytrack, transpose, pitchbend, pitchbendMin, pitchbendMax, 
			bufnumFirstTable, tablePosition, tablePositionMin, tablePositionMax,  
			level, envtime=0, delay, attack, decay, sustain, sustainTime, release, 
			intKey, modPitchbend, modTablePosition, modDelay, modAttack, modDecay, 
			modSustain, modSustainTime, modRelease;
		var outEnv, envFunction, stepFunc, intonationFunc, outFreq, pbend, outWave, envCurve, 
			tablePos, del, att, dec, sus, sustime, rel;
		tablePos = tablePositionMin + ((tablePositionMax - tablePositionMin) * (tablePosition + modTablePosition).max(0).min(1));
		pbend = pitchbendMin + ((pitchbendMax - pitchbendMin) * (pitchbend + modPitchbend).max(0).min(1));
		del = (delay + modDelay).max(0).min(1);
		att = (attack + timeSpec.map(modAttack) ).max(0.001).min(20);
		dec = (decay + timeSpec.map(modDecay)).max(0.001).min(20);
		sus = (sustain + modSustain).max(0).min(1);
		sustime = (sustainTime + timeSpec.map(modSustainTime)).max(0.001).min(20);
		rel = (release + timeSpec.map(modRelease)).max(0.01).min(20);
		envCurve = this.getSynthOption(2);
		envFunction = this.getSynthOption(3);
		outEnv = EnvGen.kr(
			envFunction.value(del, att, dec, sus, sustime, rel, envCurve),
			gate, 
			doneAction: 2
		);
		stepFunc = this.getSynthOption(0);
		intonationFunc = this.getSynthOption(1);
		outFreq = (intonationFunc.value((note + transpose), intKey) * keytrack) + ((48 + transpose).midicps * (1-keytrack));
		outWave = VOsc.ar(bufnumFirstTable + (stepFunc.value(tablePos) - 1).max(0.0001).min(6.999), outFreq * (2 ** (pbend /12)));
		// amplitude is vel *  0.00315 approx. == 1 / 127
		Out.ar(out, outEnv * outWave * level * (velocity * 0.007874));
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["commandAction", "Plot envelope", {this.envPlot;}],
		["TXMinMaxSliderSplit", "Table position", ControlSpec(1, 8), 
			"tablePosition", "tablePositionMin", "tablePositionMax"], 
		["SynthOptionPopup", "Stepping", arrOptionData, 0, 200], 
		["TXWaveTableSpecs", "Wavetables", {arrWaveSpecs}, 
			{arg view; arrWaveSpecs = view.value; 
				arrSlotData = view.arrSlotData; this.updateBuffers(view.value);}, 
			{arrSlotData}], 
		["EZslider", "Level", ControlSpec(0, 1), "level"], 
		["MIDIListenCheckBox"], 
		["MIDIChannelSelector"], 
		["MIDINoteSelector"], 
		["MIDIVelSelector"], 
		["TXCheckBox", "Keyboard tracking", "keytrack"], 
		["Transpose"], 
		["TXMinMaxSliderSplit", "Pitch bend", 
			ControlSpec(-48, 48), "pitchbend", "pitchbendMin", "pitchbendMax"], 
		["TXEnvDisplay", {this.envViewValues;}, {arg view; envView = view;}],
		["EZslider", "Pre-Delay", ControlSpec(0,1), "delay", {this.updateEnvView;}], 
		["EZslider", "Attack", timeSpec, "attack", {this.updateEnvView;}], 
		["EZslider", "Decay", timeSpec, "decay", {this.updateEnvView;}], 
		["EZslider", "Sustain level", ControlSpec(0, 1), "sustain", {this.updateEnvView;}], 
		["EZslider", "Sustain time", timeSpec, "sustainTime", {this.updateEnvView;}], 
		["EZslider", "Release", timeSpec, "release", {this.updateEnvView;}], 
		["SynthOptionPopup", "Curve", arrOptionData, 2, 200, {system.showView;}], 
		["SynthOptionPopup", "Env. Type", arrOptionData, 3, 200], 
		["SynthOptionPopup", "Intonation", arrOptionData, 1, nil, 
			{arg view; this.updateIntString(view.value)}], 
		["TXStaticText", "Note ratios", 
			{TXIntonation.arrScalesText.at(arrOptions.at(1));}, 
				{arg view; ratioView = view}],
		["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", 
			"F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 140], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	make buffers, load the synthdef and create the Group for synths to belong to
	this.makeBuffersAndGroup(arrBufferSpecs);
	//	initialise buffers 
	8.do({arg item, i;
		arrWaveSpecs = arrWaveSpecs.add(
			(	(Harmonics(32).decay .copyRange(0, i * 3)  
					++ Array.newClear(32).fill(0)
				).copyRange(0, 31) 
			).max(0).min(1)
		); 
	});
	Routine.run {
		var holdModCondition;
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		system.server.sync;
		// update buffers
		this.updateBuffers(arrWaveSpecs);
		// remove condition from load queue
		system.holdLoadQueue.removeCondition(holdModCondition);
	};
	//	initialise slots 
	arrSlotData = arrWaveSpecs.dup(5);
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Wavetables", {displayOption = "showWavetables"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["Spacer", 3], 
		["ActionButton", "MIDI/ Note", {displayOption = "showMIDI"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["Spacer", 3], 
		["ActionButton", "Envelope", {displayOption = "showEnv"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["NextLine"], 
		["ActionButton", "Intonation", {displayOption = "showIntonation"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["Spacer", 3], 
		["ActionButton", "Modulation options", {displayOption = "showModOptions"; 
			this.buildGuiSpecArray; system.showView;}, 130], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showWavetables", {
		guiSpecArray = guiSpecArray ++[
		["TXMinMaxSliderSplit", "Table position", ControlSpec(1, 8), 
			"tablePosition", "tablePositionMin", "tablePositionMax"], 
		["NextLine"], 
		["SynthOptionPopup", "Stepping", arrOptionData, 0, 200], 
		["DividingLine"], 
		["NextLine"], 
		["TXWaveTableSpecs", "Wavetables", {arrWaveSpecs}, 
			{arg view; arrWaveSpecs = view.value; 
				arrSlotData = view.arrSlotData; this.updateBuffers(view.value);}, 
			{arrSlotData}], 
		];
	});
	if (displayOption == "showMIDI", {
		guiSpecArray = guiSpecArray ++[
			["EZslider", "Level", ControlSpec(0, 1), "level"], 
			["DividingLine"], 
			["MIDIListenCheckBox"], 
			["NextLine"], 
			["MIDIChannelSelector"], 
			["NextLine"], 
			["MIDINoteSelector"], 
			["NextLine"], 
			["MIDIVelSelector"], 
			["DividingLine"], 
			["TXCheckBox", "Keyboard tracking", "keytrack"], 
			["DividingLine"], 
			["Transpose"], 
			["DividingLine"], 
			["TXMinMaxSliderSplit", "Pitch bend", 
				ControlSpec(-48, 48), "pitchbend", "pitchbendMin", "pitchbendMax"], 
			["DividingLine"], 
			["PolyphonySelector"], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showEnv", {
		guiSpecArray = guiSpecArray ++[
			["TextBar", "Envelope", 80, 20], 
			["TXEnvDisplay", {this.envViewValues;}, {arg view; envView = view;}],
			["NextLine"], 
			["EZslider", "Pre-Delay", ControlSpec(0,1), "delay", {this.updateEnvView;}], 
			["EZslider", "Attack", timeSpec, "attack", {this.updateEnvView;}], 
			["EZslider", "Decay", timeSpec, "decay", {this.updateEnvView;}], 
			["EZslider", "Sustain level", ControlSpec(0, 1), "sustain", {this.updateEnvView;}], 
			["EZslider", "Sustain time", timeSpec, "sustainTime", {this.updateEnvView;}], 
			["EZslider", "Release", timeSpec, "release", {this.updateEnvView;}], 
			["NextLine"], 
			["SynthOptionPopup", "Curve", arrOptionData, 2, 200, {system.showView;}], 
			["NextLine"], 
			["SynthOptionPopup", "Env. Type", arrOptionData, 3, 200], 
			["Spacer", 4], 
			["ActionButton", "Plot", {this.envPlot;}],
		];
	});
	if (displayOption == "showIntonation", {
		guiSpecArray = guiSpecArray ++[
			["SynthOptionPopup", "Intonation", arrOptionData, 1, nil, 
				{arg view; this.updateIntString(view.value)}], 
			["TXStaticText", "Note ratios", 
				{TXIntonation.arrScalesText.at(arrOptions.at(1));}, 
					{arg view; ratioView = view}],
			["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", 
				"F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 140], 
		];
	});
	if (displayOption == "showModOptions", {
		guiSpecArray = guiSpecArray ++[
			["ModulationOptions"]
		];
	});

}

updateBuffers { arg arrSpecs;
	if (arrSpecs.notNil, {
	// generate wavetables in buffers
		buffers.do({arg item, i;
			item
			.sine1(
				arrSpecs.at(i)
				// first harmonic is > 0 as all zero's would crash method
				.max([0.0001] ++ Array.newClear(31).fill(0))    
			);
		});
	});
}

extraSaveData { // override default method
	^[arrWaveSpecs, arrSlotData];
}

loadExtraData {arg argData;  // override default method
	arrWaveSpecs = argData.at(0);
	Routine.run {
		var holdModCondition;
		// add condition to load queue
		holdModCondition = system.holdLoadQueue.addCondition;
		// pause
		holdModCondition.wait;
		system.server.sync;
		// update buffers
		this.updateBuffers(arrWaveSpecs);
		// remove condition from load queue
		system.holdLoadQueue.removeCondition(holdModCondition);
	};
	arrSlotData = argData.at(1);
}
updateIntString{arg argIndex; 
	if (ratioView.notNil, {
		if (ratioView.notClosed, {
			ratioView.string = TXIntonation.arrScalesText.at(argIndex);
		});
	});
}

envPlot {
	var del, att, dec, sus, sustime, rel, envCurve;
	del = this.getSynthArgSpec("delay");
	att = this.getSynthArgSpec("attack");
	dec = this.getSynthArgSpec("decay");
	sus = this.getSynthArgSpec("sustain");
	sustime = this.getSynthArgSpec("sustainTime");
	rel = this.getSynthArgSpec("release");
	envCurve = this.getSynthOption(2);
	Env.new([0, 0, 1, sus, sus, 0], [del, att, dec, sustime, rel], envCurve, nil).plot;
}

envViewValues {
	var del, att, dec, sus, sustime, rel;
	var arrTimesNorm, arrTimesNormedSummed;
	del = this.getSynthArgSpec("delay");
	att = this.getSynthArgSpec("attack");
	dec = this.getSynthArgSpec("decay");
	sus = this.getSynthArgSpec("sustain");
	sustime = this.getSynthArgSpec("sustainTime");
	rel = this.getSynthArgSpec("release");
	arrTimesNorm = [0, del, att, dec, sustime, rel].normalizeSum;
	arrTimesNorm.size.do({ arg i;
		arrTimesNormedSummed = arrTimesNormedSummed.add(arrTimesNorm.copyRange(0, i).sum);
	});
	^[arrTimesNormedSummed, [0, 0, 1, sus, sus, 0]].asFloat;
}

updateEnvView {
	if (envView.class == SCEnvelopeView, {
		if (envView.notClosed, {
			6.do({arg i;
				envView.setEditable(i, true);
			});
			envView.value = this.envViewValues;
			6.do({arg i;
				envView.setEditable(i, false);
			});
		});
	});
}

}

