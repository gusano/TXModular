// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXWaveSynth6 : TXModuleBase {

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

	var	displayOption;
	var ratioView;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Wave Synth";
	moduleRate = "audio";
	moduleType = "groupsource";
	arrCtlSCInBusSpecs = [ 		
		["Modify 1", 1, "modModify1", 0],
		["Modify 2", 1, "modModify2", 0],
		["Pitch bend", 1, "modPitchbend", 0],
		["Delay", 1, "modDelay", 0],
		["Attack", 1, "modAttack", 0],
		["Decay", 1, "modDecay", 0],
		["Sustain level", 1, "modSustain", 0],
		["Sustain time", 1, "modSustainTime", 0],
		["Release", 1, "modRelease", 0],
		["Curve", 1, "modCurve", 0],
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	autoModOptions = false;
	displayOption = "showWaveform";
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
		["modify1", 0.5, defLagTime],
		["modify1Min", 0, defLagTime],
		["modify1Max", 1, defLagTime],
		["modify2", 0.5, defLagTime],
		["modify2Min", 0, defLagTime],
		["modify2Max", 1, defLagTime],
		["level", 0.5, \ir],
		["envtime", 0, \ir],
		["delay", 0, \ir],
		["attack", 0.005, \ir],
		["decay", 0.15, \ir],
		["sustain", 1, \ir],
		["sustainTime", 1, \ir],
		["release", 0.1, \ir],
		["curve", 0, \ir],
		["timeMultiply", 1, defLagTime],
		["intKey", 0, \ir],
		["modPitchbend", 0, defLagTime],
		["modModify1", 0, defLagTime],
		["modModify2", 0, defLagTime],
		["modDelay", 0, \ir],
		["modAttack", 0, \ir],
		["modDecay", 0, \ir],
		["modSustain", 0, \ir],
		["modSustainTime", 0, \ir],
		["modRelease", 0, \ir],
		["modCurve", 0, \ir],
		["modTimeMultiply", 0, \ir],
  	]; 
	arrOptions = [0,0];
	arrOptionData = [TXWaveForm.arrOptionData, TXIntonation.arrOptionData];
	synthDefFunc = { 
		arg out, gate, note, velocity, keytrack, transpose, pitchbend, pitchbendMin, pitchbendMax, 
			modify1, modify1Min, modify1Max, modify2, modify2Min, modify2Max, 
			level, envtime=0, delay, attack, decay, sustain, sustainTime, release, 
			curve, timeMultiply, intKey, modPitchbend, modModify1, modModify2, modDelay, modAttack, modDecay, 
			modSustain, modSustainTime, modRelease, modCurve, modTimeMultiply;
		var outEnv, intonationFunc, outFreq, pbend, outFunction, outWave, curveAdjusted, 
			mod1, mod2, del, att, dec, sus, sustime, rel, timeMult;
		mod1 = modify1Min + ((modify1Max - modify1Min) * (modify1 + modModify1).max(0).min(1));
		mod2 = modify2Min + ((modify2Max - modify2Min) * (modify2 + modModify2).max(0).min(1));
		pbend = pitchbendMin + ((pitchbendMax - pitchbendMin) * (pitchbend + modPitchbend).max(0).min(1));
		del = (delay + modDelay).max(0).min(1);
		att = (attack + modAttack).max(0.001).min(1);
		dec = (decay + modDecay).max(0.001).min(1);
		sus = (sustain + modSustain).max(0).min(1);
		sustime = (sustainTime + modSustainTime).max(0.001).min(1);
		rel = (release + modRelease).max(0.01).min(1);
		curveAdjusted = ((curve) + (modCurve * 10)).max(-10).min(10);
		timeMult = (timeMultiply + modTimeMultiply).max(0.001).min(20);
		outEnv = EnvGen.kr(
			Env.dadsr(del, att * timeMult, dec* timeMult, sus, rel* timeMult, 1, curveAdjusted), 
			gate, 
			doneAction: 2
		);
		intonationFunc = arrOptionData.at(1).at(arrOptions.at(1)).at(1);
		outFreq = (intonationFunc.value((note + transpose), intKey) * keytrack) + ((48 + transpose).midicps * (1-keytrack));
		outFunction = arrOptionData.at(0).at(arrOptions.at(0)).at(1);
		outWave = outFunction.value(
			outFreq *  (2 ** (pbend /12)), 
			mod1, 
			mod2
		);
		// amplitude is vel *  0.00315 approx. == 1 / 127
		Out.ar(out, outEnv * outWave * level * (velocity * 0.007874));
	};
	this.buildGuiSpecArray;
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.midiNoteInit;
	//	load the synthdef and create the Group for synths to belong to
	this.loadDefAndMakeGroup;
}

buildGuiSpecArray {
	guiSpecArray = [
		["SpacerLine", 6], 
		["ActionButton", "Waveform", {displayOption = "showWaveform"; 
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
	if (displayOption == "showWaveform", {
		guiSpecArray = guiSpecArray ++[
		["SynthOptionPopup", "Waveform", arrOptionData, 0], 
		["TXMinMaxSliderSplit", "Modify 1", \unipolar, "modify1", "modify1Min", "modify1Max"], 
		["TXMinMaxSliderSplit", "Modify 2", \unipolar, "modify2", "modify2Min", "modify2Max"], 
		["DividingLine"],
		["EZslider", "Level", ControlSpec(0, 1), "level"], 
		["DividingLine"], 
		];
	});
	if (displayOption == "showMIDI", {
		guiSpecArray = guiSpecArray ++[
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
			["TXMinMaxSliderSplit", "Pitch bend", ControlSpec(-48, 48), "pitchbend", "pitchbendMin", "pitchbendMax"], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showEnv", {
		guiSpecArray = guiSpecArray ++[
			["TextBar", "Envelope", 80, 20], 
			["NextLine"], 
			["EZslider", "Pre-Delay", ControlSpec(0, 1), "delay"], 
			["EZslider", "Attack*", ControlSpec(0, 1), "attack"], 
			["EZslider", "Decay*", ControlSpec(0, 1), "decay"], 
			["EZslider", "Sustain level", ControlSpec(0, 1), "sustain"], 
			["EZslider", "Sustain time*", ControlSpec(0, 1), "sustainTime"], 
			["EZslider", "Release*", ControlSpec(0, 1), "release"], 
			["EZsliderUnmapped", "* Curve", ControlSpec(-10, 10, step: 1), "curve"], 
			["EZslider", "* Time Scale", ControlSpec(0.001, 20), "timeMultiply"], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showIntonation", {
		guiSpecArray = guiSpecArray ++[
			["SynthOptionPopup", "Intonation", arrOptionData, 1, nil, {arg view; this.updateIntString(view.value)}], 
			["TXStaticText", "Note ratios", {TXIntonation.arrScalesText.at(arrOptions.at(1));}, {arg view; ratioView = view}],
			["TXPopupAction", "Key / root", ["C", "C#", "D", "D#", "E","F", "F#", "G", "G#", "A", "A#", "B"], "intKey", nil, 140], 
		];
	});
	if (displayOption == "showModOptions", {
		guiSpecArray = guiSpecArray ++[
			["ModulationOptions"]
		];
	});

}

updateIntString{arg argIndex; 
	ratioView.string = TXIntonation.arrScalesText.at(argIndex);
}

}

