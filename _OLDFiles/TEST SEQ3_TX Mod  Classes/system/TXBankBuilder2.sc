// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXBankBuilder2 {	// Channel Routing  

classvar	<>system;	    			// system

// testing xxx - are the following needed??? 
classvar arrControls;			// gui controls
classvar <>arrControlVals; 		// gui control values

classvar	displayBankType, sampleBank, loopBank, holdBank, showWaveform;

classvar path, txtQuestion, errorScreen;
classvar strBankType, strNoElements, strNo, btnChangeBank;
classvar arrSaveData, bankFile, arrOutput;
classvar btnSaveBank, btnOpenBank, btnEmptyBank, btnAddSamples, btnPlaySample;
classvar btnStopPlay, btnDeleteSample, btnShowWaveform, btnHideWaveform;
classvar strSampleLoop, numSampleNo, signalViewSample, strFileNameTxt, strSampleFileName;
classvar strFreqOrBeatsTxt, strFreqOrBeats, numNewFreqOrBeats, popupMidiNote;
classvar strBPMtxt, strBPM, strLength, strNumChannels;
classvar btnSamplePlus, btnSampleMinus, btnChangeValue, sldVolume, holdSampleView;
classvar specNewFreqOrBeats, holdSynth, holdBuffer, soundFileView, strListTitle, listSamples, listViewSamples;

*initClass{
	displayBankType = "Sample";
	sampleBank = [];
	loopBank = [];
	holdBank = [];
	showWaveform = 0;
}

*sampleBank{ 
	// get bank 
	^sampleBank;
}

*sampleBank_ { arg argBank;
	var newBank; 
	// set bank 
	if (argBank.notNil, {
		newBank = this.checkSndFilePaths(argBank, "Sample");
		sampleBank = newBank;
	});
}

*loopBank{ 
	// get bank 
	^loopBank;
}

*loopBank_ { arg argBank;
	var newBank; 
	// set bank 
	if (argBank.notNil, {
		newBank = this.checkSndFilePaths(argBank, "Loop");
		loopBank = newBank;
	});
}

*makeSampleGui{arg argView;  this.makeGui(argView, "Sample"); }

*makeLoopGui{arg argView;  this.makeGui(argView, "Loop"); }

*makeGui{ arg argView, argDisplayBankType;
	 var parent;

	// initialise variables
	displayBankType = argDisplayBankType ?? displayBankType;
	if (displayBankType == "Sample", {
		holdBank = sampleBank;
	}, {
		holdBank = loopBank;
	});
	
//	argView.decorator.shift(4, 0);
	parent = CompositeView(argView, Rect(0,0, 1060, 600)).background_(TXColor.sysModuleWindow);
	parent.decorator = FlowLayout(parent.bounds);

	if (displayBankType == "Sample", {
		specNewFreqOrBeats = [1, 2000, \linear, 0].asSpec;
	}, {	
		specNewFreqOrBeats = [1, 128, \linear, 1].asSpec;
	});	

	// spacing
//	parent.decorator.shift(0, 10);
	parent.decorator.nextLine;
	// bank type
	strBankType = StaticText(parent, 300 @ 40)
			.align_(\centre)
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);
	if (GUI.current.asSymbol == \SwingGUI, {
		strBankType.font_(JFont.new("Helvetica-Bold",18));
	},{
		strBankType.font_(Font.new("Helvetica-Bold",18));
	});

	if (displayBankType == "Sample", {
		strBankType.string = "SAMPLE BANK";
	}, {
		strBankType.string = "LOOP BANK";
	});

	// spacing
	parent.decorator.shift(10, 0);

	// button
//	btnChangeBank = Button(parent, 200 @ 24);
//	if (displayBankType == "Sample", {
//		btnChangeBank.states = [["Switch to LOOP BANK", TXColor.white, TXColor.sysGuiCol3]];
//		btnChangeBank.action = {displayBankType = "Loop"; system.showView;};
//	},{
//		btnChangeBank.states = [["Switch to SAMPLE BANK", TXColor.white, TXColor.sysGuiCol3]];
//		btnChangeBank.action = {displayBankType = "Sample"; system.showView;};
//	});

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;

	// divider 
	StaticText(parent, 1050 @ 2) .string_("") .background_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;

	// no. samples
	strNoElements = StaticText(parent, 200 @ 24)
		.string_("Total no. of Samples: 0")
		.background_(TXColor.white)
		.stringColor_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(10, 0);
	// button
	btnAddSamples = Button(parent, 150 @ 24);
	if (displayBankType == "Sample", {
		btnAddSamples.states = [["Add Samples to Bank", TXColor.white, TXColor.sysGuiCol1]];
	}, {
		btnAddSamples.states = [["Add Loops to Bank", TXColor.white, TXColor.sysGuiCol1]];
	});

	// spacing
	parent.decorator.shift(10, 0);
	// button
	btnOpenBank = Button(parent, 150 @ 24).states = [["Load Bank", TXColor.white, TXColor.sysGuiCol2]];
	// spacing
	parent.decorator.shift(10, 0);
	// button
	btnSaveBank = Button(parent, 150 @ 24).states = [["Save Bank", TXColor.white, TXColor.sysGuiCol2]];
	// spacing
	parent.decorator.shift(10, 0);
	// button
	btnEmptyBank = Button(parent, 150 @ 24).states = [["Empty Bank", TXColor.white, TXColor.sysDeleteCol]];

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;
	// divider 
	StaticText(parent, 1050 @ 2) .string_("") .background_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;

	// sample selection 
	strListTitle = StaticText(parent, 150 @ 24)
			.string_("Select:")
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);
	if (displayBankType == "Sample", {
		strListTitle.string = "Select Sample:";
	},{
		strListTitle.string = "Select Loop:";
	});
	listSamples = holdBank.collect({ arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0);
	});
	listViewSamples = ListView(parent, 850 @ 200)
			.items_(listSamples ? [""])
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1)
			.action = { arg view;
				numSampleNo.value = view.value;
				numSampleNo.doAction;
			};

	// spacing
	parent.decorator.shift(0, -20);
	parent.decorator.nextLine;

	// button
	btnShowWaveform = Button(parent, 60 @ 24).states = [["Show", TXColor.white, TXColor.sysGuiCol2]];
	// spacing
	parent.decorator.shift(10, 0);
	// button
	btnHideWaveform = Button(parent, 60 @ 24).states = [["Hide", TXColor.white, TXColor.sysDeleteCol]];

	// spacing
	parent.decorator.nextLine;

	// show soundfile
	soundFileView = SoundFileView.new(parent, 1040 @ 100)
		.gridOn_(false).timeCursorOn_(false)
		.waveColors_(Color.blue(alpha: 1.0) ! 8)
		.background_(Color(0.65,0.65,0.95));

	// spacing
	parent.decorator.shift(0, 10);
	parent.decorator.nextLine;

	// button
	btnPlaySample = Button(parent, 200 @ 24);
	if (displayBankType == "Sample", {
		btnPlaySample.states = [["Play Sample", TXColor.white, TXColor.sysGuiCol1]];
	},{
		btnPlaySample.states = [["Play Loop", TXColor.white, TXColor.sysGuiCol1]];
	});

	// spacing
	parent.decorator.shift(10, 0);
	// button
	btnStopPlay = Button(parent, 200 @ 24).states = [["Stop Playing", TXColor.white, TXColor.sysGuiCol1]];
	// spacing
	parent.decorator.shift(10, 0);
	// volume slider
	sldVolume = EZSlider(parent, 190 @ 24, "Volume", labelWidth: 45, numberWidth: 40)
		.setColors(TXColor.white, TXColor.sysGuiCol1)
		.value_(0.5);

	// spacing
	parent.decorator.shift(20, 0);
	// button
	btnDeleteSample = Button(parent, 200 @ 24);
	if (displayBankType == "Sample", {
		btnDeleteSample.states = [["Remove Sample from Bank", TXColor.white, TXColor.sysDeleteCol]];
	}, {
		btnDeleteSample.states = [["Remove Loop from Bank", TXColor.white, TXColor.sysDeleteCol]];
	});
	
	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;
	// divider 
	StaticText(parent, 1050 @ 2) .string_("") .background_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;
//	// spacing
//	parent.decorator.shift(0, 10);
//	parent.decorator.nextLine;
	// sample no. 
	strSampleLoop = StaticText (parent, 80 @ 24)
		.background_(TXColor.white)
		.stringColor_(TXColor.sysGuiCol1);
	if (displayBankType == "Sample", {
		strSampleLoop.string = "Sample no.";
	}, {
		strSampleLoop.string = "Loop no.";
	});
	numSampleNo = NumberBox (parent, 30 @ 24).value_(0);
	btnSampleMinus = Button(parent, 20 @ 24).states = [["-", TXColor.white, TXColor.sysGuiCol1]];
	btnSamplePlus = Button(parent, 20 @ 24).states = [["+", TXColor.white, TXColor.sysGuiCol1]];

	// file name
	strFileNameTxt = StaticText(parent, 100 @ 24)
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);
	if (displayBankType == "Sample", {
		strFileNameTxt.string =  "Sample File";
	}, {
		strFileNameTxt.string =  "Loop File";
	});
	strSampleFileName = StaticText(parent, 475 @ 24)
			.string_(" ")
		//	.align_(\right)
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1)
			.font_(Font("Gill Sans", 11));

	// spacing
	parent.decorator.shift(5, 0);
	// length 
	StaticText(parent, 80 @ 24)
			.string_("Length (ms)")
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);
	strLength = StaticText(parent, 50 @ 24)
			.string_("")
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(5, 0);
	// no. channels 
	StaticText(parent, 80 @ 24)
			.string_("No. Channels")
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);
	strNumChannels = StaticText(parent, 50 @ 24)
			.string_("")
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(0, 10);
	parent.decorator.nextLine;

	// frequency/ no. beats
	strFreqOrBeatsTxt = StaticText(parent, 116 @ 24)
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);
	if (displayBankType == "Sample", {
		strFreqOrBeatsTxt.string =  "Frequency";
	}, {
		strFreqOrBeatsTxt.string =  "Total Beats";
	});
	strFreqOrBeats = StaticText(parent, 80 @ 24)
			.string_("")
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(10, 0);
	// set frequency/ no. Beats
	btnChangeValue = Button(parent, 146 @ 24);
	if (displayBankType == "Sample", {
		btnChangeValue.states = [["Set Frequency to:", TXColor.white, TXColor.sysGuiCol1]]
	}, {
		btnChangeValue.states = [["Set Total Beats to:", TXColor.white, TXColor.sysGuiCol1]]
	});
	numNewFreqOrBeats = NumberBox (parent, 50 @ 24);

	if (displayBankType == "Sample", {
		popupMidiNote = PopUpMenu(parent, 50 @ 24)
			.stringColor_(TXColor.sysGuiCol1).background_(TXColor.white);
		popupMidiNote.items = ["notes"] 
			++ 103.collect({arg item; TXGetMidiNoteString(item + 24);});
		popupMidiNote.action = {
			if (popupMidiNote.value > 0, {
				numNewFreqOrBeats.value = (popupMidiNote.value + 23).midicps.round(0.01);
			});
		};
		popupMidiNote.value = 46;
	});

	// spacing
	parent.decorator.shift(10, 0);

	if (displayBankType == "Loop", {
		// bpm text (only for loops)
		strBPMtxt = StaticText(parent, 120 @ 24)
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
		strBPMtxt.string =  "Equivalent BPM: ";
		strBPM = StaticText(parent, 76 @ 24).string_(" ")
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	}); // end of if banktype == "Loop"

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;

	// divider 
	StaticText(parent, 1050 @ 2) .string_("") .background_(TXColor.sysGuiCol1);

	// spacing
	parent.decorator.shift(0, 5);
	parent.decorator.nextLine;

	////////////////////////////////////////////////////////////////////////////////////////
	// DEFINE GUI ACTIONS 

	btnOpenBank.action = 
		{var errFound, errorScreen, fileType;
		var newPath, newFile, newData;
		errFound = 0;
		if (displayBankType == "Sample", {
			fileType = "SampleBank";
		},{
			fileType = "LoopBank";
		});
		CocoaDialog.getPaths({ arg paths;
			newPath = paths.at(0);
			newFile = File(newPath,"r");
			newData = thisProcess.interpreter.executeFile(newPath);
			newFile.close;
			if (newData.notNil, {
				if (newData.isArray and: (newData.size > 1) and: (newData.at(0)==fileType),{
					holdBank = holdBank.asArray ++ newData.at(1).deepCopy;
					holdBank = this.checkSndFilePaths(holdBank, displayBankType);
					this.updateBank;
				},{
					errFound = 1;
				});
			},{
				errFound = 1;
			});
			this.sizeChange;
		});
		// Show error screen if error found
		if (errFound==1, {
			errorScreen = TXInfoScreen.new("ERROR: invalid " ++ fileType ++ ": " ++ newFile.path);		});
		this.sizeChange;
	};			
	btnSaveBank.action = 
		{
		var newPath, newFile, newData;
		if (holdBank.size > 0, {
			arrSaveData = holdBank;

			arrOutput = [displayBankType++"Bank", arrSaveData];

			Dialog.savePanel({ arg path;
				newPath = path;
				newData = arrOutput;
				newFile = File(newPath,"w");
				newFile << "#" <<< newData << "\n";
				//	use file as an io stream
				//	<<< means store the compile string of the object
				//	<< means store a print string of the object
				newFile.close;
			});

		});
	};
	
	btnEmptyBank.action = 
		{
		holdBank = [];
		this.updateBank;
		this.sizeChange;
		// recreate view
		system.showView;
		};
	
	btnAddSamples.action = {this.addSampleDialog(displayBankType); };

	btnDeleteSample.action = 
		{
			var newBank;
				if (holdBank.size > 0, {
				holdBank.do({ arg item, i;
					if (i != numSampleNo.value, {newBank = newBank.add(item); });
					});
				holdBank = newBank;
				this.updateBank;
				this.sizeChange;
			});
		};	

	btnShowWaveform.action = {showWaveform = 1; system.showView;};
	btnHideWaveform.action = {showWaveform = 0; system.showView;};

	numSampleNo.action = 
		{arg view;
			listViewSamples.value = view.value;
			this.loadSample;
		};			

	btnSamplePlus.action = 
		{
		numSampleNo.value = (numSampleNo.value + 1).min(holdBank.size-1).max(0);
		numSampleNo.doAction;
		};			

	btnSampleMinus.action = 
		{
		numSampleNo.value = (numSampleNo.value - 1).max(0);
		numSampleNo.doAction;
		};			
	
	btnChangeValue.action = 
		{if (holdBank.notEmpty, {
			holdBank.at(numSampleNo.value.asInteger).put(1, numNewFreqOrBeats.value);
			this.updateBank;
			this.loadSample;
			});
		};			

	numNewFreqOrBeats.action = { |view| view.value = specNewFreqOrBeats.constrain(view.value)};

	btnPlaySample.action = {	
		// release any synth running
		btnStopPlay.doAction;
		if (holdBank.notEmpty and: holdBuffer.notNil, { 
			holdSynth = { sldVolume.value * 
				PlayBuf.ar(1, holdBuffer.bufnum, BufRateScale.kr(holdBuffer.bufnum)) }.play;
		});
	};			

	btnStopPlay.action = 
		{if (holdSynth.notNil, {
				holdSynth.free;
				holdSynth = nil;
				});
		};			
	
	// initialise
	this.sizeChange;
	
} // end of class method makeGui

*addSampleDialog { arg argBankType = "Sample";
	var holdString;
		// get path/filenames
		Dialog.getPaths({ arg paths;
			var validPaths, validPathsNumChannels, invalidPaths;
			// check validity of pathnames
			paths.do({ arg item, i;
				var holdFile;
				holdFile = SoundFile.new;
				if (holdFile.openRead(item), {
					validPaths = validPaths.add(item);
					validPathsNumChannels = validPathsNumChannels.add(holdFile.numChannels);
				},{
					invalidPaths = invalidPaths.add(item);
				});
				holdFile.close;
			});
			if (invalidPaths.notNil, {
				TXInfoScreen.new(
					"Error: the following are not valid sound files.",
					arrInfoLines: invalidPaths
				);
			});

			if (argBankType == "Sample", {
				holdBank = sampleBank 
				 ++ validPaths.collect({ arg item, i;  [item, 440, validPathsNumChannels.at(i), true]; });
				holdString = "Total no. of  Samples:  " ++ holdBank.size.asString;
			},{
				holdBank = loopBank 
				 ++ validPaths.collect({ arg item, i;  [item, 4, validPathsNumChannels.at(i), true]; });				 holdString = "Total no. of  Loops:  " ++ holdBank.size.asString;
			});
			this.updateBank;
			if ((system.currentWindowString == "Sample bank") or:
				(system.currentWindowString == "Loop bank"),
			{	
				strNoElements.string = holdString;
//				this.sizeChange;
//				this.loadSample;
//			},{
			});
			// recreate view
			system.showView;
		}, nil, 1024);
}

*updateBank { 	// method to be run whenever holdBank is updated
	if (displayBankType == "Sample", {
		sampleBank = holdBank;
	}, {
		loopBank = holdBank;
	});
} // end of class method updateBank

*sizeChange { 	// method to be run whenever size changes
	if (holdBank.size == 0, {
		numSampleNo.value =  0;
		 // for samples
		if (displayBankType == "Sample", {
			strNoElements.string = "Total no. of Samples:  0";
			numNewFreqOrBeats.value = 440;
			popupMidiNote.value = 46;
		}, { // else for loops
			strNoElements.string = "Total no. of Loops:  0";
			numNewFreqOrBeats.value = 1;
		}); // end of if banktype == "Sample"
	}, { // load sample/loop details for current sample number
		numSampleNo.value = numSampleNo.value.min(holdBank.size-1);
		if (displayBankType == "Sample", {
				strNoElements.string = "Total no. of Samples:  " ++ holdBank.size.asString;
		}, { // else for loops
			strNoElements.string = "Total no. of Loops:  " ++ holdBank.size.asString;
		}); // end of if banktype == "Sample"
	}); // end of if holdBank.isEmpty
	listSamples = holdBank.collect({ arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0);
	});
	listViewSamples.items = listSamples ? [""];
	listViewSamples.value = numSampleNo.value;
	this.loadSample;	// load sample into memory and update gui
} 		// end of class method sizeChange

*loadSample { 			// method to load samples into buffer
	if (holdBank.isNil or: holdBank.isEmpty or: {holdBank.at(numSampleNo.value).at(3) == false}, {
		strSampleFileName.string =  "";
		strFreqOrBeats.string = "";
		strLength.string = "";
		strNumChannels.string = "";
		if (displayBankType == "Loop", {strBPM.string =  ""; });
	},{
		numSampleNo.value = numSampleNo.value.min(holdBank.size-1).max(0);
		holdBuffer = Buffer.read(system.server,holdBank.at(numSampleNo.value.asInteger).at(0), action: { arg buffer; 
			{
			//	if file loaded ok
				if (buffer.notNil, {
					strSampleFileName.string =  holdBank.at(numSampleNo.value.asInteger).at(0).asString;
					soundFileView.soundfile = SoundFile.new(strSampleFileName.string);
					if (showWaveform == 1, {
						soundFileView.readWithTask( block: 1);
					});
					strFreqOrBeats.string = holdBank.at(numSampleNo.value.asInteger).at(1).asString;
					strLength.string = ((buffer.numFrames / buffer.sampleRate).round(0.0001) * 1000).asString;
					strNumChannels.string = buffer.numChannels.asString;
					if (displayBankType == "Loop", {
						strBPM.string =  
							((60 * buffer.sampleRate * holdBank.at(numSampleNo.value.asInteger).at(1))
								/ buffer.numFrames ).round(0.01).asString;
					});
				},{
					strSampleFileName.string = holdBank.at(numSampleNo.value.asInteger).at(0) ++ "-  NOT FOUND";
					strLength.string = "";
					strNumChannels.string = "";
					if (displayBankType == "Loop", {strBPM.string =  ""; });
				});
			}.defer;	// defer because gui process
		});
		if (holdBuffer.isNil, {
			TXInfoScreen.new("Invalid Sample File" ++ holdBank.at(numSampleNo.value.asInteger).at(0));
		});
	});

}	// end of class method loadSample 
	
*checkSndFilePaths { arg argBank, bankTypeString, showMessages = true;
	var newBank, holdSoundFile, invalidPaths;
	newBank = argBank.deepCopy;
	newBank = newBank.collect({ arg item, i;
		var newData;
		holdSoundFile = SoundFile.new;
		if (holdSoundFile.openRead(item.at(0)), {
			newData = item.keep(3).add(true);
		},{
			newData = item.keep(3).add(false);
			invalidPaths = invalidPaths.add(item.at(0));
		});
		holdSoundFile.close;
		newData;
	});
	if (invalidPaths.notNil and: (showMessages == true), {
		TXInfoScreen.new(
			bankTypeString ++ " Bank Error - the following are not valid sound files:",
			arrInfoLines: invalidPaths
		);
	});
	^newBank;
}	// end of class method checkSndFilePaths

}	// end of class


