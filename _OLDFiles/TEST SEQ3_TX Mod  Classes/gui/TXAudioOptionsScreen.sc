// Copyright (C) 2008  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAudioOptionsScreen {		// Information Screen module 

classvar system, holdPath, holdAudioDevice, holdBufferSize, holdSampleRate, holdFileData, holdFile, w;

*makeWindow{ arg argSystem, holdServerOptions, argConfirmFunction;

	var txtTitle, btnHelp, popAudioDevice, popBufferSize, popSampleRate;
	var btnConfirm, btnCancel, validData;
	
	system = argSystem;
	holdPath = PathName.new("~/Library/Application\ Support/TXModular/TXModSettings.tx");

	// if TXModular directory doesn't exist, create it.
	if (holdPath.pathOnly.isFolder.not, {
		holdPath.pathOnly.makeDir(false);
	});

	if (File.exists(holdPath.fullPath),  {
		// if file TXMODSettings.tx  exists, update values. 
		holdFileData = thisProcess.interpreter.executeFile(holdPath.fullPath);
		if (holdFileData.class == Array, {
			holdAudioDevice = holdFileData[1][0];
			holdBufferSize = holdFileData[1][1];
			holdSampleRate = holdFileData[1][2];
			validData = true;
		});
	});
	if (validData != true,  {
		// if file TXMODSettings.tx  doesn't exist, create it with default values. 
		holdAudioDevice = nil;
		holdBufferSize = nil;
		holdSampleRate = nil;
		this.saveSettingsToFile;
	});

	holdServerOptions.device = holdAudioDevice;
	holdServerOptions.hardwareBufferSize = holdBufferSize;
	holdServerOptions.sampleRate = holdSampleRate;
	
	w = Window("TX Modular System", Rect(10, 400, 800, 500));
	w.front;
	w.view.decorator = FlowLayout(w.view.bounds);
	w.view.background = TXColor.sysMainWindow;
	w.view.decorator.shift(30, 30);


	// system title	
	txtTitle = StaticText(w, 400 @ 55)
			.string_("TX Modular " ++ system.systemVersion ++ " - Audio Setup")
			.font_(Font.new("Helvetica-Bold",24))
			.align_(\centre)
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1);


	// button - help 
	w.view.decorator.shift(40, 10);
	btnHelp = Button(w, 40 @ 27);
	btnHelp.states = [["Help", TXColor.white, TXColor.sysHelpCol]];
	btnHelp.action = {
		"TX_0 TX Modular Help".openHelpFile;

	};

	// popup - Audio device  
	w.view.decorator.nextLine;
	w.view.decorator.shift(30, 40);

	StaticText(w, 160 @ 27)
		.string_("Audio Device")
		.align_(\centre)
		.background_(TXColor.white)
		.stringColor_(TXColor.sysGuiCol1);

	popAudioDevice = PopUpMenu(w, 160 @ 27)
		.background_(TXColor.sysGuiCol2).stringColor_(TXColor.white);
	popAudioDevice.items = ["Default"] 
		++ TXAudioDevices.devices.collect({arg item, i; 
			item.replace("(", "[")   
			.replace(")", "]");
		});
	popAudioDevice.action = {|view|
		var arrDevices;
		arrDevices = [nil] ++ TXAudioDevices.devices;
		holdAudioDevice = arrDevices.at(view.value);
		holdServerOptions.device = holdAudioDevice;
		this.saveSettingsToFile;
	};
	popAudioDevice.value = ([nil] ++ TXAudioDevices.devices).indexOfEqual(holdAudioDevice) ? 0;

	// popup - Buffer size  
	w.view.decorator.nextLine;
	w.view.decorator.shift(30, 30);

	StaticText(w, 160 @ 27)
		.string_("Audio Device Buffer Size ")
		.align_(\centre)
		.background_(TXColor.white)
		.stringColor_(TXColor.sysGuiCol1);

	popBufferSize = PopUpMenu(w, 160 @ 27)
		.background_(TXColor.sysGuiCol2).stringColor_(TXColor.white);
	popBufferSize.items = ["Default","2048", "1024", "512", "256", "128", "64", "32"];
	popBufferSize.action = {|view|
		var arrBufferSizes;
		arrBufferSizes = [nil, 2048, 1024, 512, 256, 128, 64, 32] ;
		holdBufferSize = arrBufferSizes.at(view.value);
		holdServerOptions.hardwareBufferSize = holdBufferSize;
		this.saveSettingsToFile;
	};
	popBufferSize.value = [nil, 2048, 1024, 512, 256, 128, 64, 32].indexOfEqual(holdBufferSize) ? 0;

	// notes  
	w.view.decorator.shift(20, 0);
	StaticText(w, 300 @ 27)
		.string_(" only use settings suitable for your sound card")
		.align_(\centre)
		.background_(TXColor.grey(0.8))
		.stringColor_(TXColor.sysGuiCol1);

	// popup - sample rate  
	w.view.decorator.nextLine;
	w.view.decorator.shift(30, 30);

	StaticText(w, 160 @ 27)
		.string_("Audio Device Sample Rate ")
		.align_(\centre)
		.background_(TXColor.white)
		.stringColor_(TXColor.sysGuiCol1);

	popSampleRate = PopUpMenu(w, 160 @ 27)
		.background_(TXColor.sysGuiCol2).stringColor_(TXColor.white);
	popSampleRate.items = ["Default", "8000", "11025", "22050", "32000", "44100", 
		"48000", "88200", "96000", "176400", "192000" ];
	popSampleRate.action = {|view|
		var arrSampleRates;
		arrSampleRates = [nil, 8000, 11025, 22050, 32000, 44100, 48000, 88200, 96000, 176400, 192000] ;
		holdSampleRate = arrSampleRates.at(view.value);
		holdServerOptions.sampleRate = holdSampleRate;
		this.saveSettingsToFile;
	};
	popSampleRate.value = [nil, 8000, 11025, 22050, 32000, 44100, 48000, 88200, 96000, 176400, 192000]
		.indexOfEqual(holdSampleRate) ? 0;

	// notes  
	w.view.decorator.shift(20, 0);
	StaticText(w, 300 @ 27)
		.string_(" only use settings suitable for your sound card")
		.align_(\centre)
		.background_(TXColor.grey(0.8))
		.stringColor_(TXColor.sysGuiCol1);
	// confirm button 
	w.view.decorator.nextLine;
	w.view.decorator.shift(30, 60);
	btnConfirm = Button(w, 160 @ 50)
		.states = [["Start Audio Engine", TXColor.white, TXColor.sysGuiCol1]];
	btnConfirm.action = { 
		this.close; 
		// run confirm function
		argConfirmFunction.value;
	};

	// quit button 
	w.view.decorator.shift(60, 0);
	btnCancel = Button(w, 160 @ 50)
		.states = [["Quit", TXColor.white, TXColor.sysDeleteCol]];
	btnCancel.action = {
		system.audioSetupQuit = true;
		this.close;
	};	
}

*saveSettingsToFile {
		holdFileData = ["TXModSystemSystemSettingsData", [holdAudioDevice, holdBufferSize, holdSampleRate] ];
		holdFile = File(holdPath.fullPath,"w");
		holdFile << "#" <<< holdFileData << "\n";
		//	use file as an io stream
		//	<<< means store the compile string of the object
		//	<< means store a print string of the object
		holdFile.close;
}

*close {		//	close window 
	// if standalone system then quit completely else just close TX
	if (system.txStandAlone == 1, {
		0.exit;
	},{
		if (w.isClosed.not, {w.close});
	});
}

}
