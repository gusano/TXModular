// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSystem1 {		// system module 1  

/*	NOTE: use the following code to show all class methods:

	TXSystem1.class.dumpMethodList;
*/
////////////////////////////////////////////////////////////////////////////////////
//	define class variables:
	
classvar	<systemVersion = "081";	// version of the system

classvar mainWindowWidth = 1360;	// width and height of main window
classvar mainWindowHeight = 760;
 
classvar	<arrAllPossModules;		// array of all possible modules. 
classvar	<arrAllPossCurSeqModules;	// array of all possible current sequencer modules. 
classvar	<arrAllPossOldSeqModules;	// array of all possible older sequencer modules (for old builds)
classvar	<arrSystemModules;			// array of modules in system. 
classvar	<arrModuleClipboards;		// array of modules clipboards for copying/pasting. 

classvar	<arrMainOutBusses;		// array of main out busses 
classvar	<arrFXSendBusses;		// array of FX send busses
classvar	<arrAudioAuxBusses;	// array of Audio Aux busses 
classvar	<arrControlAuxBusses;	// array of Control Aux busses 
classvar	<arrAllBusses;		// single array of all busses used

classvar <server;				// server variable
classvar holdServerOptions;		// to hold server options
classvar holdNextModuleID;		// used to give each module a unique ID
classvar <moduleID = 99;		// system itself has a moduleID of 99;
classvar w;
classvar <groupModules, <groupChannels;  // groups for order of execution
classvar <>autoOpen = false;
classvar <>autoRun = true;
classvar <>latency = 0.1;		// general latency time in seconds to allow server to do something
classvar closingDown = false;	// only used when closing down
classvar showWindow;			// for gui
classvar viewBox;				// for gui
classvar headerBox;			// for gui
classvar stTextFileName;		// for gui
classvar holdFileName = "";		// for gui
classvar <arrScreenUpdFuncs;	// for gui
classvar screenChanged = false;	// for gui
classvar screenRebuild = false;	// for gui
classvar screenUpdRoutine;		// for gui
classvar holdBootSeconds;		// for gui
classvar notes0, notes1, notes2, notes3, notes4, notes5, notes6, notes7;   // for notes view gui
classvar <instName = "System";	// system name
classvar <arrActionSpecs;		// specs for system actions available to widgets
classvar <guiSpecTitleArray; 	
classvar <>showFrontScreen;
classvar holdBackWindow = "Modules";
classvar holdFrontWindow = "Run Interface";
classvar <arrSnapshots;			// array of system snapshots
classvar <>snapshotNo = 0;	
classvar <>snapshotName = "";	
classvar <holdLoadQueue;			
classvar <defSampleRate = 44100;	//	default sample rate. (also set in TXModuleBase)
classvar <txStandAlone = 0;		
classvar arrMeters;			// holds any meters
classvar <>arrModulesForMeters;	// holds all modules that could be displayed on meters

////////////////////////////////////////////////////////////////////////////////////
// Define class methods

*initClass{
	holdBootSeconds = Main.elapsedTime;
} 
// start the system
	
*startCocoa { 
	this.start;
} 

/* NOTE: The Swing version of the TX Modular is not currently working: 
*startSwing { 
	this.start(0,1);
} 
*/

*start { arg argStandAlone = 0, argSwing = 0; 		
	
	if (argStandAlone == 1, {txStandAlone = 1}); // for running as system as a standalone

	if (argSwing == 1, {
		GUI.swing; // use swing in subsequent GUI creation procedures 	
	}, {
		GUI.cocoa; // use cocoa in subsequent GUI creation procedures 
	}); 
	
	// start scan	
//	TXAudioDevices.scan;	// not currently used

	// create Load Queue
	holdLoadQueue = TXLoadQueue.new;
	// start window
	showWindow = "Modules";
	showFrontScreen = false;
	// create array of all possible modules  
	arrAllPossModules = [

		// required system modules
		TXChannel,
		TXBusMainOuts,
		TXBusFXSend,
		TXBusAudioAux,
		TXBusControlAux,
//
//	N.B. ADD ANY NEW MODULES INTO THIS LIST:  --->
//
		// other modules in ALPHA order, by their defaultName name
		TXActionSeq3,
		TXActionSlider,
		TXAmpFollower3,
		TXAmpSim,
		TXAmpSimSt,
		TXAnalyser3,
		TXAnimateCode2,
		TXAudioIn4,
		TXAudioTrigger3,
		TXBitCrusher2, 
		TXBitCrusher2St, 
		TXChorus,
		TXChorusSt,
		TXCodeInsertAu,
		TXCodeInsertAuSt,
		TXCodeInsertCtrl,
		TXCodeSourceAu,
		TXCodeSourceAuSt,
		TXCodeSourceCtrl,
		TXCompander3,
		TXCompander3St,
		TXControlDelay4,
		TXCyclOSCCol,
		TXCyclOSCGrey,
		TXDCRemove,
		TXDCRemoveSt,
		TXDelay4,
		TXDelay4St,
		TXDisintegrator,
		TXDisintegratorSt,
		TXDistortion2, 
		TXDistortion2St,
		TXEnvCurve,
		TXEnvDADSR4,
		TXEnv16Stage,
		TXEQGraphic,
		TXEQGraphicSt,
		TXEQPara,
		TXEQParaSt,
		TXEQShelf,
		TXEQShelfSt,
		TXFilePlayer6,   
		TXFilePlayer6St,
		TXFileRecorder2,
		TXFileRecorder2St,
		TXFilterExt2,
		TXFilterExt2St,
		TXFilterSynth,
		TXFlanger3,
		TXFlanger3St,
		TXFMSynth4,
		TXGain,
		TXGainSt,
		TXGranulator,
		TXGroupMorph,
		TXHarmoniser,
		TXLFOMulti2,
		TXLFOCurve,
		TXLimiter,
		TXLimiterSt,
		TXLiveLooper2,
		TXLoopPlayer,
		TXLoopPlayerSt2,
		TXMIDIController2,
		TXMIDIControlOut3,
		TXMIDINote2,
		TXMIDIOut,
		TXMIDIPitchbend2,
		TXMIDIVelocity2,
		TXMultiTapDelay2,
		TXNoiseWhitePink,
		TXNormalizer,
		TXNormalizerSt,
		TXNotchPhaser,
		TXNotchPhaserSt,
		TXNoteSequencer3,
		TXOSCController,	
		TXOSCControlOut,
		TXOSCOut, 
		TXOSCTrigger, 
		TXPerlinNoise,
		TXPhaser,
		TXPItchFollower4,
		TXPitchShifter, 
		TXPitchShifterSt,
		TXPingPong,
		TXPingPongSt,
		TXPluckSynth,
		TXQCParticles2,
		TXQCPlayer4,
		TXReverb2,
		TXReverbSt2,
		TXReverbA,
		TXReverbF,
		TXReverbFSt,
		TXReverbG,
		TXRingMod2,
		TXSampleHold,
		TXSamplePlayer5a,
		TXSamplePlayerSt6,
		TXSimpleSlider2,
		TXSlope,
		TXSmooth2,
		TXSpectralFX,
		TXStereoToMono2,
		TXStereoWidth,
		TXTableSynth4,
		TXTransient2,
		TXTransient2St,
		TXTrigImpulse,
		TXVocoder2,
		TXVocoderFX2,
		TXVosim,
		TXWarp2,
		TXWaveform5,
		TXWaveform5St,
		TXWaveshaper,
		TXWaveshaperSt,
		TXWaveSynth8,
		TXWaveSynthPlus2,
		TXWaveTerrain,		TXWiiController,
		TXWiiControllerOSC2,
		TXWiiTrigger,
		TXWiiTrigOSC2,
		TXXDistort, 
		TXXDistortSt,
		TXWXFader2to1,
		TXWXFader4to2,
	];
	if (argSwing == 1, {
		// if swing remove certain modules 	
		arrAllPossModules.remove(TXQCParticles2);
		arrAllPossModules.remove(TXQCPlayer4);
	}); 
	// create arrays of all possible old and current sequencer modules
	arrAllPossOldSeqModules = [	// kept for instruments saved using earlier builds
		TXSequencer4,
		TXSequencer5,
		TXSequencer6,
		TXNoteSequencer,
		TXNoteSequencer2,
		TXActionSeq,
		TXActionSeq2,
	];
	arrAllPossCurSeqModules = [
		TXActionSeq3,
		TXNoteSequencer3,
	];
	autoOpen = false;
	autoRun = true;
	closingDown = false;	
	
	// initialise
	notes0 = " ";
	notes1 = " ";
	notes2 = " ";
	notes3 = " ";
	notes4 = " ";
	notes5 = " ";
	notes6 = " ";
	notes7 = " ";
	arrSnapshots  = Array.newClear(100);
	snapshotNo = 0;	
	snapshotName = "";	
	arrModuleClipboards = Dictionary.new;
	
	// assign  server
//	if (txStandAlone == 1, {
//		server = Server.internal;
//	},{//		server = Server.local;
//	});	// servernow always internal
	server = Server.internal;	
	Server.default = server;
	server.quit;

	// Create a new instance of ServerOptions
	holdServerOptions = ServerOptions.new;
	holdServerOptions.numOutputBusChannels = 16;
	holdServerOptions.memSize = 8192* 8;
	holdServerOptions.sampleRate = defSampleRate;
	holdServerOptions.sampleRate = defSampleRate;
	server.options = holdServerOptions;

	server.waitForBoot({		// wait for server to boot before running other actions
		// use Routine to pause - for safety
		Routine.run {
			// set server.latency
			server.latency = latency;	
			// pause longer if standalone
			if (txStandAlone == 1, {
				0.5.wait;
			},{
				0.1.wait;
			});
			
			{ // defer function
			// set class variables
			guiSpecTitleArray = [
				["xxxxx"]    // dummy  used to override behaviour in TXBuildActions
			];
			arrActionSpecs = TXBuildActions.from( this, [
				["commandAction", "Refresh screen", {this.flagGuiUpd;}],
				["commandAction", "Sync Start", {this.syncStart;}],
				["commandAction", "Sync Stop", {this.syncStop;}],
				["commandAction", "Stop all syncable modules", 
					{this.stopAllSyncModules; this.showView;}],
				["commandAction", "Panic - stop all notes", {this.allNotesOff;}],
				["commandAction", "Show Screen 1", {
					{TXFrontScreen.storeCurrLoadNewLayer(0); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 2", {
					{TXFrontScreen.storeCurrLoadNewLayer(1); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 3", {
					{TXFrontScreen.storeCurrLoadNewLayer(2); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 4", {
					{TXFrontScreen.storeCurrLoadNewLayer(3); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 5", {
					{TXFrontScreen.storeCurrLoadNewLayer(4); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 6", {
					{TXFrontScreen.storeCurrLoadNewLayer(5); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 7", {
					{TXFrontScreen.storeCurrLoadNewLayer(6); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 8", {
					{TXFrontScreen.storeCurrLoadNewLayer(7); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 9", {
					{TXFrontScreen.storeCurrLoadNewLayer(8); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 10", {
					{TXFrontScreen.storeCurrLoadNewLayer(9); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 11", {
					{TXFrontScreen.storeCurrLoadNewLayer(10); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 12", {
					{TXFrontScreen.storeCurrLoadNewLayer(11); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 13", {
					{TXFrontScreen.storeCurrLoadNewLayer(12); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 14", {
					{TXFrontScreen.storeCurrLoadNewLayer(13); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 15", {
					{TXFrontScreen.storeCurrLoadNewLayer(14); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 16", {
					{TXFrontScreen.storeCurrLoadNewLayer(15); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 17", {
					{TXFrontScreen.storeCurrLoadNewLayer(16); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 18", {
					{TXFrontScreen.storeCurrLoadNewLayer(17); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 19", {
					{TXFrontScreen.storeCurrLoadNewLayer(18); this.showView;}.defer(0.2)}],
				["commandAction", "Show Screen 20", {
					{TXFrontScreen.storeCurrLoadNewLayer(19); this.showView;}.defer(0.2)}],
				["commandAction", "Show Next Screen", {
					{TXFrontScreen.storeCurrLoadNextLayer; this.showView;}.defer(0.2)}],
				["commandAction", "Show Previous Screen", {
					{TXFrontScreen.storeCurrLoadPrevLayer; this.showView;}.defer(0.2)}],
				["EmptyValueAction", \popup], 
				["TXPopupAction", "Snapshot", 
					{["Load Snapshot ..."] ++ 
						(1 .. 99).collect({arg item, i;
					 	var holdName;
					 	holdName = this.getSnapshotName(item);
						if (holdName == "", {holdName = "EMPTY"});
					 	"Snapshot " ++ item.asString ++ ": " ++ holdName;
					 });},
					"Snapshot", 
					{arg view; 
						this.loadSnapshot(view.value);
					}
				], 
				["EmptyValueAction", \number], 
				["EmptyValueAction", \checkbox], 
				["EmptyValueAction", \text], 
				["EmptyValueAction", \textedit], 
			]);	
			// start MIDI
			// Swing check				
		//	if (GUI.current.asSymbol !== \SwingGUI, {
				this.midiConnect;
		//	});

			// initialise  
			holdNextModuleID = 100;
			arrSystemModules = [];
			// create groups
			groupModules = Group.tail(server);
			groupChannels = Group.tail(server);
		
			// initialise all module classes
			arrAllPossModules.do({ arg item, i;
				item.initClass;
				item.system = this;
				if (item.moduleType == "channel", 
					{item.group = groupChannels;}, 
					{item.group = groupModules;}
				); 
			});
			// initialise other classes
			TXModGui.initClass;
			TXModGui.system = this;
			TXSeqGui.initClass;
			TXSeqGui.system = this;
			TXBankBuilder2.system = this;
			TXBankBuilder2.initClass;
			TXChannelRouting.system = this;
			TXChannelRouting.initClass;
			TXSignalFlow.system = this;
			TXSignalFlow.initClass;
			TXChannel.systemInit;
			TXGuiBuild2.system = this;
			TXWidget.system = this;
			TXWidget.initClass;
			TXFrontScreen.system = this;
			TXFrontScreen.initClass;
			// create stereo Main Outs Busses
			arrMainOutBusses = 
				["Main Outs"] 
				.collect({ arg item, i;
					TXBusMainOuts.new(item);
				});
			// create mono FX Send Busses
			arrFXSendBusses = 
				["FX Send 1", "FX Send 2", "FX Send 3", "FX Send 4"]
				.collect({ arg item, i;
					TXBusFXSend.new(item);
				});
			// create Audio Aux Busses
			arrAudioAuxBusses = 
				["Audio Aux 1+2", "Audio Aux 3+4", "Audio Aux 5+6", 
					"Audio Aux 7+8", "Audio Aux 9+10"]
				.collect({ arg item, i;
					TXBusAudioAux.new(item);
				});
			// create Audio Aux Busses
			arrControlAuxBusses = 
				["Control Aux 1", "Control Aux 2", "Control Aux 3", 
					"Control Aux 4", "Control Aux 5", 
					"Control Aux 6", "Control Aux 7", "Control Aux 8", 
					"Control Aux 9", "Control Aux 10"]
				.collect({ arg item, i;
					TXBusControlAux.new(item);
				});
			// create arrAllBusses
			arrAllBusses = arrMainOutBusses ++ arrFXSendBusses ++ arrAudioAuxBusses 
				++ arrControlAuxBusses;
			// Initialise module layout x/y positions of all busses
			this.initBusPositions;
			// create gui
			this.makeGui;	
		}.defer;
		
			// pause
			0.1.wait;
			
			// create routine to keep screen up to date
			screenUpdRoutine = Routine { arg inval;
				loop {
					// run all the screen update functions
					this.runScreenUpdFuncs;
					0.1.wait;	// update every xxx secs
				};
			}.play;
		};	// end of Routine.run
	});	// end of server.waitForBoot

}	// end of *start method

///  SYNC START AND STOP SYSTEM /////////////////////////////////////


*syncStart{
	this.syncStartSequencers;
	this.syncStartRecorders;
	this.syncStartPlayers;
} 

*syncStop{
	this.syncStopSequencers;
	this.syncStopRecorders;
	this.syncStopPlayers;
} 

*stopAllSyncModules {
	this.stopAllSequencers;
	this.stopAllRecorders;
	this.stopAllPlayers;
} 
/////// called by above 3: //////

*syncStartSequencers{
	(this.arrAllPossCurSeqModules ++ this.arrAllPossOldSeqModules).do ({ arg item, i;
		item.syncStartAllSequencers;
	});
} 
*syncStopSequencers{
	(this.arrAllPossCurSeqModules ++ this.arrAllPossOldSeqModules).do ({ arg item, i;
		try{item.syncStopAllSequencers;};
	});
} 
*stopAllSequencers{
	(this.arrAllPossCurSeqModules ++ this.arrAllPossOldSeqModules).do ({ arg item, i;
		item.stopAllSequencers;
	});
} 
*syncStartRecorders{
	TXFileRecorder.syncStartAllRecorders;
	TXFileRecorderSt.syncStartAllRecorders;
	TXFileRecorder2.syncStartAllRecorders;
	TXFileRecorder2St.syncStartAllRecorders;
} 
*syncStopRecorders{
	TXFileRecorder2.syncStopAllRecorders;
	TXFileRecorder2St.syncStopAllRecorders;
} 
*stopAllRecorders {
	TXFileRecorder2.stopAllRecorders;
	TXFileRecorder2St.stopAllRecorders;
} 
*syncStartSeqsAndRecorders{
	this.syncStartSequencers;
	this.syncStartRecorders;
} 
*syncStartPlayers{
	TXFilePlayer3.syncStartAllPlayers;
	TXFilePlayer3St.syncStartAllPlayers;
	TXFilePlayer4.syncStartAllPlayers;   
	TXFilePlayer4St.syncStartAllPlayers;
	TXFilePlayer5.syncStartAllPlayers;   
	TXFilePlayer5St.syncStartAllPlayers;
	TXFilePlayer6.syncStartAllPlayers;   
	TXFilePlayer6St.syncStartAllPlayers;
} 
*syncStopPlayers{
	TXFilePlayer3.syncStopAllPlayers;
	TXFilePlayer3St.syncStopAllPlayers;
	TXFilePlayer4.syncStopAllPlayers;   
	TXFilePlayer4St.syncStopAllPlayers;
	TXFilePlayer5.syncStopAllPlayers;   
	TXFilePlayer5St.syncStopAllPlayers;
	TXFilePlayer6.syncStopAllPlayers;   
	TXFilePlayer6St.syncStopAllPlayers;
} 
*stopAllPlayers {
	TXFilePlayer3.stopAllPlayers;
	TXFilePlayer3St.stopAllPlayers;
	TXFilePlayer4.stopAllPlayers;   
	TXFilePlayer4St.stopAllPlayers;
	TXFilePlayer5.stopAllPlayers;   
	TXFilePlayer5St.stopAllPlayers;
	TXFilePlayer6.stopAllPlayers;   
	TXFilePlayer6St.stopAllPlayers;
}
////////////////////////////////////////////////////////////////////////////////////
*setModuleClipboard { arg moduleClass, argData;
	arrModuleClipboards.put(moduleClass.asString, argData);
} 

*getModuleClipboard { arg moduleClass;
	^arrModuleClipboards.at(moduleClass.asString).deepCopy;
} 
////////////////////////////////////////////////////////////////////////////////////

*getSynthArgSpec {arg argString;
	// Dummy method - here just used for snapshot no
	if (argString == "Snapshot", {^snapshotNo});
	^0;
}
*setSynthValue {	
	// Dummy method
	^0;
}

////////////////////////////////////////////////////////////////////////////////////

*midiConnect {	
	var inPorts = 8;
	var outPorts = 8;
	MIDIClient.init(inPorts,outPorts);			// explicitly intialize the client
	inPorts.do({ arg i; 
		MIDIIn.connect(i, MIDIClient.sources.at(i));
	});
}

////////////////////////////////////////////////////////////////////////////////////

*arrWidgetActionModules {	
	// returns all modules including system itself that can perform actions for widgets
	// only selects modules where size of arrActionSpecs > 0
	^([TXSystem1] ++ arrSystemModules ++ TXChannelRouting.arrChannels).select({arg item, i; item.arrActionSpecs.size > 0; });
}

*arrWidgetValueActionModules {	
	// returns all modules including system itself that can perform value actions for widgets 
	// value actions are for continuous controls
	// only selects modules where size of arrActionSpecs > 0
	^([TXSystem1] ++ arrSystemModules ++ TXChannelRouting.arrChannels).select({arg item, i; item.arrActionSpecs.size > 0; })
		.select({arg item, i; item.arrActionSpecs.select({arg action, i; action.actionType == \valueAction; }).size > 0;  });
}
////////////////////////////////////////////////////////////////////////////////////

*makeGui {	
	var cmdPeriodFunc, holdScreenHeight, holdScreenWidth;
	
	holdScreenHeight = Window.screenBounds.height;
	holdScreenWidth = Window.screenBounds.width;

	w = Window("TX Modular System", 
		Rect(0, holdScreenHeight - mainWindowHeight - 10, mainWindowWidth, mainWindowHeight), 
		scroll: true);
	w.front;
	w.view.decorator = FlowLayout(w.view.bounds);
	w.view.background = TXColor.sysMainWindow;
	w.acceptsMouseOver = true;
	
	this.showView;
	
	// cmd-period action       -------to be added if needed--------
//	cmdPeriodFunc = {
		// add any actions here
//	 };
//	CmdPeriod.add(cmdPeriodFunc);
	
	// when window closes remove cmdPeriodFunc.
	w.onClose = {
	//	CmdPeriod.remove(cmdPeriodFunc);
		
		// run method
		this.closeSystem;
	};
}

*currentWindowString {
	if (showFrontScreen == false, {
		^holdBackWindow;
	},{
		^holdFrontWindow;
	});
}

/// SAVE AND LOAD SYSTEM /////////////////////////////////////////////////////////////////////////////////

*saveData {
	// this method returns an array of all data for saving with various components:
	// 0- string "TXSystemSaveData", 1- this class, 2- holdNextModuleID, 3- arrAllModulesData, 
	// 4- channelRoutingData, 5- arrBusData, 6 - sampleBank, 7 - loopBank, 8 - notes
	// 9- arrFrontScreenData, 10- arrSnapshots, 11- arrModuleLayoutData,
	var arrData, arrAllModulesData, channelRoutingData, arrBusData, arrNotes, arrFrontScreenData, arrModuleLayoutData;
	// collect saveData from  all modules 
	arrAllModulesData = arrSystemModules.collect({ arg item, i; item.saveData; });
	// collect data from TXChannelRouting 
	channelRoutingData = TXChannelRouting.saveData;
	// collect bus data  
	arrBusData = arrAllBusses.collect({ arg item, i; [item.moduleID, item.posX, item.posY]}); 
	// collect notes  
	arrNotes = [notes0, notes1, notes2, notes3, notes4, notes5, notes6, notes7];
	// collect front screen data  
	arrFrontScreenData = TXFrontScreen.saveData;
	// collect module layout data  
	arrModuleLayoutData = TXSignalFlow.saveData;
	// build output
	arrData = ["TXSystemSaveData", this.class.asString, holdNextModuleID, arrAllModulesData, 
		channelRoutingData, arrBusData, this.sampleBank, this.loopBank, arrNotes, arrFrontScreenData, arrSnapshots, 
		arrModuleLayoutData]; 
	^arrData;
}

*loadData { arg arrData;   
	// this method updates all data by loading arrData. format:
	// 0- string "TXSystemSaveData", 1- this class, 2- holdNextModuleID, 3- arrAllModulesData, 
	// 4- channelRoutingData, 5- arrBusData, 6 - sampleBank, 7 - loopBank, 8 - notes
	// 9- arrFrontScreenData, 10- arrSnapshots, 11- arrModuleLayoutData,
	var arrAllModulesData, channelRoutingData, newModule, holdAutoOpen, holdModuleClass, 
		arrBusData, arrNotes, arrFrontScreenData, windowLoading, arrModuleLayoutData;
	// error check
	if (arrData.class != Array, {
		TXInfoScreen.new("Error: invalid data. cannot load.");   
		^0;
	});	
	if (arrData.at(1) != this.class.asString, {
		TXInfoScreen.new("Error: invalid data class. cannot load.");   
		^0;
	});	
	
	// delete all modules in system
	this.emptySystem;

	// show loading window
	windowLoading = TXInfoScreen.new(
		"LOADING ... (this can take more than 10 secs)", 0, TXColour.orange);   

	// temporarily store and turn off system.autoOpen while building system
	holdAutoOpen = autoOpen;
	autoOpen = false;

	// assign variables
	holdNextModuleID = arrData.at(2).copy;
	arrAllModulesData = arrData.at(3).deepCopy;
	channelRoutingData = arrData.at(4).deepCopy;
	arrBusData = arrData.at(5).deepCopy;
	this.sampleBank = arrData.at(6).deepCopy;
	this.loopBank = arrData.at(7).deepCopy;
	arrNotes  = arrData.at(8) ? Array.newClear(8);
	arrFrontScreenData = arrData.at(9).deepCopy;
	arrSnapshots  = arrData.at(10).deepCopy ? Array.newClear(100);
	arrModuleLayoutData = arrData.at(11).deepCopy;
	// first reset layout positions of all busses
	this.initBusPositions;
	if (arrModuleLayoutData.notNil, 
		{TXSignalFlow.loadData(arrModuleLayoutData); 
	});
	// set notes
	notes0 = arrNotes.at(0);
	notes1 = arrNotes.at(1);
	notes2 = arrNotes.at(2);
	notes3 = arrNotes.at(3);
	notes4 = arrNotes.at(4);
	notes5 = arrNotes.at(5);
	notes6 = arrNotes.at(6);
	notes7 = arrNotes.at(7);
	// set bus data
	arrAllBusses.do({ arg item, i; 
		var holdData;
		holdData = arrBusData.at(i).asArray;
		item.moduleID = holdData.at(0);
		if (holdData.at(1).notNil, {
			item.posX = holdData.at(1);
		});
		if (holdData.at(2).notNil, {
			item.posY = holdData.at(2);
		});
	});
	// use routine to pause between modules
	Routine.run {
		var holdCondition, holdChanLoadQueue, holdLastChanCondition, totalWidgets;
		
		// for each saved module - recreate module, add to arrSystemModules and run loadData
		arrAllModulesData.do({ arg item, i;
			var holdModCondition;
			
			// add condition to load queue
			holdModCondition = holdLoadQueue.addCondition;
			// pause
			holdModCondition.wait;
			// pause
			this.server.sync;
			// run the Module's new method to get new instance of module 
			holdModuleClass = item.at(1).interpret;
			newModule = holdModuleClass.new;
			// pause
			this.server.sync;
			// extra pause time
			newModule.extraLatency.wait;
			// add module to array of system modules
			arrSystemModules = arrSystemModules.add(newModule);
			// pause
			this.server.sync;
			// extra pause time
			newModule.extraLatency.wait;
			// load data into new module
			newModule.loadModuleID(item);
			newModule.loadData(item);

			// remove condition from load queue
			holdLoadQueue.removeCondition(holdModCondition);
		});

		// add condition to load queue
		holdCondition = holdLoadQueue.addCondition;
		// pause
		holdCondition.wait;
		// pause
		this.server.sync;
//		1.wait;
		// remove condition from load queue
		holdLoadQueue.removeCondition(holdCondition);

		// add Load Queue for channels
		holdChanLoadQueue = holdLoadQueue;

		// create condition to go to end of channel load queue
		holdLastChanCondition = Condition.new(false);

		// load data to channelRouting & create channels
		TXChannelRouting.loadData(channelRoutingData, holdChanLoadQueue, holdLastChanCondition);
		
		// pause
		holdLastChanCondition.wait;
		// pause
		this.server.sync;
		// remove condition from load queue
		holdLoadQueue.removeCondition(holdLastChanCondition);

		// restore all sequencer outputs
		(this.arrAllPossCurSeqModules ++ this.arrAllPossOldSeqModules).do ({ arg item, i;
			item.restoreAllOutputs;
		});
		// restore other outputs - on legacy modules
		TXAudioTrigger.restoreAllOutputs;
		TXAudioTrigger2.restoreAllOutputs;
	
		// restore system.autoOpen to original value
		autoOpen = holdAutoOpen;

		// add condition to load queue
		holdCondition = holdLoadQueue.addCondition;
		// pause
		holdCondition.wait;
		// pause
		this.server.sync;

		// remove condition from load queue
		holdLoadQueue.removeCondition(holdCondition);

		// load data to front screen
		totalWidgets = TXFrontScreen.loadData(arrFrontScreenData);
		if (totalWidgets > 1, {showFrontScreen = true; showWindow = "Run Interface";});

		// add condition to load queue
		holdCondition = holdLoadQueue.addCondition;
		// pause
		holdCondition.wait;

		// close loading window
		windowLoading.close;   

		// remove condition from load queue
		holdLoadQueue.removeCondition(holdCondition);

		// automatically set position data if missing 
		if (arrModuleLayoutData.isNil, {
			TXSignalFlow.rebuildPositionData; 
		});
		// update view
		this.showView;
	};
}

////////  SNAPSHOT SYSTEM  ////////////////////////////////////////////////////////////

*getSnapshotName { arg argSnapshotNo;   
	var holdName, holdSnapshot;
	holdSnapshot = arrSnapshots.at(argSnapshotNo.asInteger);
	if (holdSnapshot.notNil, {holdName = holdSnapshot.at(0);}, {holdName = "";});
	^holdName;
}

*snapshotIsEmpty { arg argSnapshotNo;   
	var holdSnapshot;
	holdSnapshot = arrSnapshots.at(argSnapshotNo.asInteger);
	if (holdSnapshot.isNil, {^true}, {^false});
}

*saveCurrentSnapshot { arg argSnapshotName;   
	var holdSnapshot;
	snapshotName = argSnapshotName ? "";
	if (snapshotName == "", {snapshotName = "Snapshot " ++ snapshotNo.asString;});
	holdSnapshot = [snapshotName, this.saveSnapshotData].deepCopy;
	arrSnapshots.put(snapshotNo, holdSnapshot);
}

*overwriteCurrentSnapshot {    
	var holdSnapshot;
	holdSnapshot = [snapshotName, this.saveSnapshotData].deepCopy;
	arrSnapshots.put(snapshotNo, holdSnapshot);
}

*saveSnapshotData {
	var arrData, arrAllModulesData, channelRoutingData;
	// collect saveData from  all modules 
	arrAllModulesData = arrSystemModules.collect({ arg item, i; item.saveData; });
	// collect data from TXChannelRouting 
	channelRoutingData = TXChannelRouting.saveData;
	// build output
	arrData = [nil, nil, nil, arrAllModulesData, channelRoutingData]; 
	^arrData;
}

*deleteCurrentSnapshot {    
	arrSnapshots.put(snapshotNo, nil);
	snapshotName = "";
}

*loadSnapshot { arg argSnapshotNo;   
	var holdSnapshot;
	holdSnapshot = arrSnapshots.at(argSnapshotNo.asInteger);
	snapshotNo = argSnapshotNo;
	if (holdSnapshot.isNil, {
		snapshotName = "";
	}, {
		snapshotName = holdSnapshot.at(0);
		this.loadSnapshotData(holdSnapshot.at(1));
	});
}

*loadSnapshotData { arg arrData;   
	// this method updates all data by loading arrData. format:
	// 0- string "TXSystemSaveData", 1- this class, 2- holdNextModuleID, 3- arrAllModulesData, 
	// 4- channelRoutingData, 5- arrBusData, 6 - sampleBank, 7 - loopBank, 8 - notes
	var arrAllModulesData, channelRoutingData, arrAllChannelData;
	
	arrAllModulesData = arrData.at(3).deepCopy;
	channelRoutingData = arrData.at(4).deepCopy;
	arrAllChannelData = channelRoutingData.at(3).deepCopy;

	// for each saved module -  run loadData
	arrAllModulesData.do({ arg item, i;
		var holdModule;
		// try to get module from ID. if found load data
		holdModule = this.getModuleFromID(item.at(2));
		if (holdModule != 0, {holdModule.loadData(item);});

	});
	// for each saved channel -  run loadData
	arrAllChannelData.do({ arg item, i;
		var holdModule, holdArrSynthArgSpecs;
		// try to get module from ID. if found load synth args
		holdModule = this.getModuleFromID(item.at(2));
		if (holdModule != 0, {
			holdArrSynthArgSpecs = item.at(9).deepCopy;
			holdArrSynthArgSpecs.do({ arg holdSynthArgSpec, i;
				holdModule.setSynthValue(holdSynthArgSpec.at(0),holdSynthArgSpec.at(1));
			
			});
		});
	});

	// update view
	this.showView;

} // end of method loadSnapshotData

////////////////////////////////////////////////////////////////////////////////////

*emptySystem { 
	// stop all sequencers in system
	this.stopAllSyncModules;
	// clear system clock
	SystemClock.clear;
	// deactivate any midi and keydown functions
	TXFrontScreen.midiDeActivate;
	TXFrontScreen.keyDownDeActivate;
	// delete all channels in  arrChannels in TXChannelRouting
	TXChannelRouting.deleteAllChannels;
	// delete all current modules in system
	arrSystemModules.do({ arg item, i; item.deleteModule; });
	TXBankBuilder2.initClass;
	TXWidget.initClass;
	TXFrontScreen.initClass;
	TXSeqGui.initClass;
	TXModGui.initClass;
	snapshotNo = 0;	
	snapshotName = "";	
}

////////////////////////////////////////////////////////////////////////////////////

*addModule {arg argModClass;
	var newModule, moduleIndex;
	if (server.serverRunning.not, {
		TXInfoScreen.new("Error: Server not running");   
		^0;
	});
	// run the Module's new method to get new instance of module 
	newModule = argModClass.new;
	// add module to array of system modules
	arrSystemModules = arrSystemModules.add(newModule);
	// if module type is source or groupsource or insert
	if ( (argModClass.moduleType == "source") or: (argModClass.moduleType == "groupsource") 
			or: (argModClass.moduleType == "insert"), {
		// set module index no in arrSystemModules
		moduleIndex = arrSystemModules.size - 1;
	});
	// if module type is Source or groupsource then add channel to routing
	if ((argModClass.moduleType == "source") or: (argModClass.moduleType == "groupsource"), {
		// set position
		TXSignalFlow.setPosition(newModule);
		TXChannelRouting.addChannel(newModule); 
	});
	if (argModClass.moduleType == "action", {
		// set position
		TXSignalFlow.setPosition(newModule);
	});
	^newModule;
}

*nextModuleID {
	var outModuleID;
	outModuleID = holdNextModuleID;
	 holdNextModuleID = holdNextModuleID + 1;
	^outModuleID;
}

*getModuleFromID {arg argModuleID;
	([this] ++ arrSystemModules ++ arrAllBusses ++ TXChannelRouting.arrChannels).do({ arg item, i;
		if (item.moduleID == argModuleID, { ^item; });
	});
	^0;
}

*checkDeletions {
	// if not closing down update screen
	if (closingDown == false, {
		// check TXChannelRouting for deletion effects
		TXChannelRouting.checkDeletions;   
		// check TXModGui for deletion effects
		TXModGui.checkDeletions;   
		// run checkDeletions method on all system modules
		arrSystemModules.do({ arg item, i; item.checkDeletions; });
		// run checkDeletions method on all frontscreen widgets
		TXFrontScreen.checkDeletions;
		// delete any modules in arrSystemModules newly marked for deletion
		arrSystemModules.do({ arg item, i;  
			if (item.toBeDeletedStatus==true, {
				item.deleteModule
			}); 
		});
		// recreate arrSystemModules without deleted ones
		arrSystemModules = arrSystemModules.select({ arg item, i; item.deletedStatus == false; });
		//  update screen
		this.showView;
	});
}


*rebuildAllModules {    
	// this method rebuilds all modules in case of module crashes:
	arrSystemModules.do({ arg item, i;
			item.rebuildSynth;
	});
}
	
*checkRebuilds {
		// check TXChannelRouting for rebuild effects
		TXChannelRouting.checkRebuilds;   
}

*checkChannelsDest { arg argModule, argOptionNo;
	// check TXChannelRouting for destination change
	TXChannelRouting.checkChannelsDest(argModule, argOptionNo);   
	//  update screen
	this.showView;
}

*closeSystem {
	// if standalone system then quit completely
	if (txStandAlone == 1, {
		0.exit;
	},{
		// set variables
		closingDown = true;
		holdFileName = "";
		// stop meters
		arrMeters.do({arg item, i; item.quit;});
		// stop routine
		screenUpdRoutine.stop;
		// stop sequencers
		this.stopAllSyncModules;
		// clear system clock
		SystemClock.clear;
		// close all modules
		arrSystemModules.do({ arg item, i; item.deleteModule; });
	//	server.freeAll; 
		server.quit;
	});
}
*allNotesOff{
	// run method on all modules
	arrSystemModules.do({ arg item, i; item.allNotesOff; });
	// clear system clock
	SystemClock.clear;
}

*initBusPositions {
	var holdBusses;
	// Initialise module layout x/y positions of all busses
	holdBusses = arrFXSendBusses ++ arrAudioAuxBusses ++ arrMainOutBusses ++ arrControlAuxBusses;
	holdBusses.do({arg item, i; item.posX = 750; item.posY = (i+1) * 50;});
}

/// SAMPLES AND LOOPS //////////////////////////////////////////////////////////////////////////

*sampleBank{
	// get bank 
	^TXBankBuilder2.sampleBank;
}

*sampleBank_ { arg argBank;
	// set bank 
	if (argBank.notNil, {
		TXBankBuilder2.sampleBank = argBank;
	});
}

*sampleBankMono{
	// get bank 
	^TXBankBuilder2.sampleBank.select({arg item, i; item.at(2) == 1;});
}

*sampleBankStereo{
	// get bank 
	^TXBankBuilder2.sampleBank.select({arg item, i; item.at(2) == 2;});
}

*sampleBankFileNames{
	// get bank 
	^this.sampleBank.collect({arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0).basename;
	});
}

*sampleBankMonoFileNames{
	// get bank 
	^this.sampleBankMono.collect({arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0).basename;
	});
}

*sampleBankStereoFileNames{
	// get bank 
	^this.sampleBankStereo.collect({arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0).basename;
	});
}
*loopBank{
	// get bank 
	^TXBankBuilder2.loopBank;
}

*loopBank_ { arg argBank;
	// set bank 
	if (argBank.notNil, {
		TXBankBuilder2.loopBank = argBank;
	});
}

*loopBankMono{
	// get bank 
	^TXBankBuilder2.loopBank.select({arg item, i; item.at(2) == 1;});
}

*loopBankStereo{
	// get bank 
	^TXBankBuilder2.loopBank.select({arg item, i; item.at(2) == 2;});
}

*loopBankFileNames{
	// get bank 
	^this.loopBank.collect({arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0).basename;
	});
}

*loopBankMonoFileNames{
	// get bank 
	^this.loopBankMono.collect({arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0).basename;
	});
}

*loopBankStereoFileNames{
	// get bank 
	^this.loopBankStereo.collect({arg item, i; 
		var errorText; 
		errorText = "";
		if (item.at(3) == false, {
			errorText = "** INVALID FILE: "; 
		});
		errorText ++ item.at(0).basename;
	});
}

////// SCREEN UPDATES //////////////////////////////////////////////////////////////////////////////

*windowToFront {
	{w.front}.defer;
}

*addScreenUpdFunc { arg argArray, argScreenUpdFunc;
	// passed argument array and function to be valued
	arrScreenUpdFuncs = arrScreenUpdFuncs.add([argArray, argScreenUpdFunc]);
} 

*runScreenUpdFuncs {
	if ((Main.elapsedTime - holdBootSeconds) > 0.5, {
		if (screenRebuild == true, {
			{ // defer function
				this.showViewAction;
				holdBootSeconds = Main.elapsedTime;
			}.defer;
		}, {
			if (screenChanged == true, {
				{ // defer function
					arrScreenUpdFuncs.do({arg item, i; 
						var arrArgs = item.at(0);
						var holdFunc = item.at(1);
						holdFunc.value(arrArgs);
					});
				}.defer;
			});
		});
		screenRebuild = false;
		screenChanged = false;
	});
} 
*flagGuiUpd {
	screenChanged = true;
}

*clearScreenUpdFuncs {
	arrScreenUpdFuncs = [];
} 

*flagGuiIfModDisplay {	arg argModule;
	// if argModule is currently being displayed on screen, then rebuild view
	if ((showWindow == "Modules") and: (TXChannelRouting.displayModule == argModule), 
		{this.flagGuiUpd});
}

*showViewIfModDisplay {	arg argModule;
	// if argModule is currently being displayed on screen, then rebuild view
	if ((showWindow == "Modules") and: (TXChannelRouting.displayModule == argModule), 
		{this.showView});
}

*isModDisplay {	arg argModule;
	var returnVal = false;
	// if argModule is currently being displayed on screen, then return true
	if ((showWindow == "Modules") and: (TXChannelRouting.displayModule == argModule), 
		{returnVal = true;});
	^returnVal;
}

*showView {	// this schedules the view for update the view selected
	screenRebuild = true;
}

///////////////////////////////////////////////////////////////////////////////////
*showViewAction {	// this creates the view selected
	var txtTitle, buttonLabels;	
	var btnHelp, btnLoadSystem, btnSaveSystem, btnNewSystem, btnRebuildSystem, btnCloseSystem;
	var btnAllNotesOff, popNewModule, btnAddModule, btnFrontBack, frontText, backText;
	var frontColour, backColour, frontTextColour, backTextColour;
	var popMeters, holdMeter, txtArrow, btnTestNote;

	// if not closing down update screen
	if (closingDown == false, {
		// wait for server sync
		Routine.run {
			server.sync;
			{	// defer function
				// clear array
				this.clearScreenUpdFuncs;
				// deactivate any midi and keydown functions
				TXFrontScreen.midiDeActivate;
				TXFrontScreen.keyDownDeActivate;
				// set globalKeyDownAction
				View.globalKeyDownAction = { arg view,char,modifiers,unicode,keycode;
					TXFrontScreen.runKeyDownActionFunctions (char,modifiers,unicode,keycode);
				};
				// clear boxes
				if (headerBox.notNil, {
					if (headerBox.notClosed, {this.deferRemoveView(headerBox)});
				});
				if (viewBox.notNil, {
 					if (viewBox.notClosed, {this.deferRemoveView(viewBox)});
				});

				w.view.decorator.reset;
				w.refresh;

				// prepare to display header
				headerBox = CompositeView(w,Rect(0,0,1420,75));
				headerBox.decorator = FlowLayout(headerBox.bounds);
			
				// system title	
				txtTitle = StaticText(headerBox, 140 @ 26)
						.string_("TX Modular " ++ systemVersion)
						.align_(\centre)
						.background_(TXColor.white)
						.stringColor_(TXColor.sysGuiCol1);
				if (GUI.current.asSymbol == \SwingGUI, {
					txtTitle.font_(JFont.new("Helvetica-Bold",16)) ;
				},{
					txtTitle.font_(Font.new("Helvetica-Bold",16)) ;
				});

				// space
				headerBox.decorator.shift(4, 0);

				// Row of system buttons	
				// button - help 
				btnHelp = Button(headerBox, 40 @ 27);
				btnHelp.states = [["Help", TXColor.white, TXColor.sysHelpCol]];
				btnHelp.action = {
					"TX_0 TX Modular Help".openHelpFile;

				};
				// button - Open file 
				btnLoadSystem = Button(headerBox, 70 @ 27);
				btnLoadSystem.states = [["Open File", TXColor.white, TXColor.sysGuiCol2]];
				btnLoadSystem.action = {
					var newPath, newFile, newString, newData;
					if (server.serverRunning.not, {server.boot}); 
					Dialog.getPaths({ arg paths;
						newPath = paths.at(0);
					//	newFile = File(newPath,"r");
					//	newString = newFile.readAllString;
					//	newFile.close;
						newData = thisProcess.interpreter.executeFile(newPath);
						holdFileName = "    File name: " ++ newPath;
						this.loadData(newData);
					});
				};
				// button - save file 
				btnSaveSystem = Button(headerBox, 70 @ 27);
				btnSaveSystem.states = [["Save File", TXColor.white, TXColor.sysGuiCol2]];
				btnSaveSystem.action = {
					var newPath, newFile, newData;
					Dialog.savePanel({ arg path;
						newPath = path;
						newData = this.saveData;
						newFile = File(newPath,"w");
						newFile << "#" <<< newData << "\n";
						//	use file as an io stream
						//	<<< means store the compile string of the object
						//	<< means store a print string of the object
						newFile.close;
						stTextFileName.string = "    File name: " ++ newPath.copy;
						holdFileName = "    File name: " ++ newPath.copy;
					});
				};
				// button - rebuild system 
				btnRebuildSystem = Button(headerBox, 90 @ 27);
				btnRebuildSystem.states = [["Rebuild System", TXColor.white, TXColor.sysDeleteCol]];
				btnRebuildSystem.action = {
					this.rebuildAllModules;
				};
				// button - clear system 
				btnNewSystem = Button(headerBox, 90 @ 27);
				btnNewSystem.states = [["Clear System", TXColor.white, TXColor.sysDeleteCol]];
				btnNewSystem.action = {
					// confirm before action
					TXInfoScreen.newConfirmWindow(
						{
							this.emptySystem;
							holdFileName = " ";
							// reset layout positions of all busses
							this.initBusPositions;
							// update view
							this.showView;
						},
						"Are you sure you want to clear the system?"
					);
				};

				// button - close 
				btnCloseSystem = Button(headerBox, 40 @ 27);
				btnCloseSystem.states = [["Quit", TXColor.white, TXColor.sysDeleteCol]];
				btnCloseSystem.action = {
					// confirm before action
					TXInfoScreen.newConfirmWindow(
						{
							// if standalone system then quit completely else just close TX
							if (txStandAlone == 1, {
								0.exit;
							},{
								// deactivate any midi and keydown functions
								TXFrontScreen.midiDeActivate;
								TXFrontScreen.keyDownDeActivate;
								w.close;
							});
						},
						"Are you sure you want to quit?"
					);
				};
				// button - sync start 
				Button(headerBox, 70 @ 27)
				.states_([
					["Sync Start", TXColor.white, TXColor.sysGuiCol2]
				])
				.action_({|view|
					// run action function
					this.syncStart;
				});
				// button - sync stop 
				Button(headerBox, 70 @ 27)
				.states_([
					["Sync Stop", TXColor.white, TXColor.sysGuiCol2]
				])
				.action_({|view|
					// run action function
					this.syncStop;
				});
				// button - stop all sequencers 
				Button(headerBox, 60 @ 27)
				.states_([
					["Stop All", TXColor.white, TXColor.sysGuiCol2]
				])
				.action_({|view|
					// run action function
					this.stopAllSyncModules;
					this.showView;
				});
				// button - Panic! - all notes off
				btnAllNotesOff = Button(headerBox, 120 @ 27)
					.states_([["Panic! All Notes Off", TXColor.white, TXColor.sysDeleteCol]])
					.action_({ this.allNotesOff; });

				// test note button
				btnTestNote = Button(headerBox, 90 @ 27);
				btnTestNote.states = [["Play Test Note", TXColor.white, TXColor.sysGuiCol2]];
				btnTestNote.action = {
					{ (EnvGen.kr(Env.sine(2,1), 1.0, doneAction: 2) 
						* Saw.ar(440, 0.1))!8 
					}.play;
				};

				// popup - Meters  
				popMeters = PopUpMenu(headerBox, 60 @ 27)
					.background_(TXColor.sysGuiCol2).stringColor_(TXColor.white);
				arrModulesForMeters = arrSystemModules.select({arg item, i; item.class.noOutChannels > 0 ;})
					.sort({arg item1, item2; item2.instName > item1.instName;}); 
				popMeters.items = ["Meters"] 
					++ arrModulesForMeters.collect({ arg item, i; item.instName; })
					++ [	"Audio Out 1+2", "Audio Out 3+4", "Audio Out 5+6", "Audio Out 7+8",  
						"Audio Out 9+10", "Audio Out 11+12", "Audio Out 13+14", "Audio Out 15+16",
						"Audio Out 1-4", "Audio Out 1-8", "Audio Out 1-16",
						"Audio In 1+2", "Audio In 3+4", "Audio In 5+6", "Audio In 7+8", 
						"Audio In 1-4", "Audio In 1-8",
					] ++ (arrAudioAuxBusses ++ arrFXSendBusses ++ arrControlAuxBusses)
						.collect({ arg item, i; item.instName; });
				popMeters.action = {|view|
					var arrAllBusArrays, arrBusses, meterRate, arrBusRates, holdMethod;

					arrAllBusArrays = [ [] ]
					++ arrModulesForMeters.collect({
						arg item, i;
						var holdIndex, outArray;
						holdIndex = item.outBus.index;
						item.class.noOutChannels.do({arg item, i;
							outArray = outArray.add(holdIndex + i + 1);
						});
						outArray;
					})
					++ [
						[1,2], [3,4], [5,6], [7,8],  
						[9,10], [11,12], [13,14], [15,16],
						(1..4), (1..8),(1..16),
						[1,2], [3,4], [5,6], [7,8], 
						(1..4), (1..8),
					] 
					++ (arrAudioAuxBusses ++ arrFXSendBusses ++ arrControlAuxBusses)
						.collect({ arg item, i; item.arrOutBusChoices.at(0).at(1) + 1; });	
					arrBusses = arrAllBusArrays.at(view.value);

					arrBusRates = [\audio]
						++ arrModulesForMeters.collect({ arg item, i; item.class.moduleRate; })
						++ (\audio ! 17) 
						++ (arrAudioAuxBusses ++ arrFXSendBusses ++ arrControlAuxBusses)
							.collect({ arg item, i; item.class.moduleRate; });

					meterRate = arrBusRates.at(view.value);

					if (arrBusses.size > 0, {
						case
							{ view.value > (17 + arrModulesForMeters.size) } {
								holdMeter = TXMeter.perform(
									'new', arrBusses-1, nil, nil, 10 @ 80, 
										popMeters.items.at(view.value), meterRate
								);
							}
							{ view.value > (11 + arrModulesForMeters.size) } {
								holdMeter = TXMeter.perform(
									'input', arrBusses-1, nil, 10 @ 80, 
										popMeters.items.at(view.value)
								);
							}
							{ view.value > arrModulesForMeters.size } {
								holdMeter = TXMeter.perform(
									'output', arrBusses-1, nil, 10 @ 80, 
										popMeters.items.at(view.value)
								);
							}
							{ view.value > 0 } {
								holdMeter = TXMeter.perform(
									'new', arrBusses-1, nil, nil, 10 @ 80, 
										popMeters.items.at(view.value), meterRate
								);
							}
						;
						// defer to allow meter synth to be built:
						if (holdMeter.notNil, {
							{holdMeter.autoreset = 3.0; holdMeter.rate = 20;}.defer(0.5); });
						arrMeters = arrMeters.add(holdMeter);
					});

					popMeters.value = 0;
				};


				// spacing
				headerBox.decorator.nextLine;
				headerBox.decorator.shift(0, 6);

/* OLD CODE:
				headerBox.decorator.shift(10,-40);
				// spacing
				headerBox.decorator.reset;
				headerBox.decorator.shift(194, 36);

				// window buttons 
				if (showFrontScreen == false, {
					frontText = "Show Interface";
					frontTextColour = TXColor.white;
					frontColour = TXColor.sysGuiCol1;
					backText = "Show System";
					backTextColour = TXColor.sysGuiCol1;
					backColour = TXColor.white;
				},{
					frontText = "Show Interface";
					frontTextColour = TXColor.sysGuiCol1;
					frontColour = TXColor.white;
					backText = "Show System";
					backTextColour = TXColor.white;
					backColour = TXColor.sysGuiCol1;
				});
				// Back screen button
				btnFrontBack = Button(headerBox, 110 @ 20);
				btnFrontBack.states = [[backText, backTextColour, backColour]];
				btnFrontBack.action = {
					showFrontScreen = false;
					showWindow = holdBackWindow;
					this.showView;
				};
				// spacing
			//	headerBox.decorator.shift(10,0);
				// Front screen button
				btnFrontBack = Button(headerBox, 110 @ 20);
				btnFrontBack.states = [[frontText, frontTextColour, frontColour]];
				btnFrontBack.action = {
					showFrontScreen = true;
					showWindow = holdFrontWindow;
					this.showView;
				};
				
				// spacing
				headerBox.decorator.shift(10, 0);

				// arrow
				txtArrow = StaticText(headerBox, 25 @ 20)
					.string_("-->")
//					.backColor_(TXColor.white)
					.background_(TXColor.white)
					.stringColor_(TXColor.sysGuiCol1)
					.align_('center');

				if (showFrontScreen == false, {
					buttonLabels = ["Modules", "Signal Flow", "Sample Bank", 
						"Loop Bank", "Notes"];
				},{
					buttonLabels = ["Run Interface", "Design Layout", "GUI Properties"];
				});

				// spacing
				headerBox.decorator.shift(10, 0);
				// display window buttons 
				buttonLabels.do({arg item, i;
					var holdButton, holdBoxColour, holdTextColour;
					if (showWindow == item, {
						holdBoxColour = TXColor.white;
						holdTextColour = TXColor.sysGuiCol1;
					},{
						holdBoxColour = TXColor.sysGuiCol1;
						holdTextColour = TXColor.white;
					});
					holdButton = Button(headerBox, 110 @ 20);
					holdButton.states = [[item, holdTextColour, holdBoxColour]];
					holdButton.action = {
						showWindow = item;
						if (showFrontScreen == false, {
							holdBackWindow = item;
						},{
							holdFrontWindow = item;
						});
						this.showView;
					};
				});	
*/				

// NEW CODE - 

				// text
				StaticText(headerBox, 90 @ 24)
					.string_("SYSTEM:")
					.background_(TXColor.sysLabelBackground)
					.stringColor_(TXColor.white)
					.font_(Font.new("Helvetica", 13))
					.align_('center');

				// display system buttons 
				buttonLabels = ["Modules", "Signal Flow", "Sample Bank", 
						"Loop Bank", "Notes"];
				buttonLabels.do({arg item, i;
					var holdButton, holdBoxColour, holdTextColour;
					if (showWindow == item, {
						holdBoxColour = TXColor.white;
						holdTextColour = TXColor.sysGuiCol1;
					},{
						holdBoxColour = TXColor.sysGuiCol1;
						holdTextColour = TXColor.white;
					});
					holdButton = Button(headerBox, 100 @ 24);
					holdButton.states = [[item, holdTextColour, holdBoxColour]];
					holdButton.action = {
						showWindow = item;
						showFrontScreen = false;
						this.showView;
					};
				});	

				// spacing
				headerBox.decorator.shift(36, 0);
				// text
				StaticText(headerBox, 100 @ 24)
					.string_("INTERFACE:")
					.background_(TXColor.sysLabelBackground)
					.stringColor_(TXColor.white)
					.font_(Font.new("Helvetica", 13))
					.align_('center');

				// display Interface buttons 
				buttonLabels = ["Run Interface", "Design Layout", "GUI Properties"];
				buttonLabels.do({arg item, i;
					var holdButton, holdBoxColour, holdTextColour;
					if (showWindow == item, {
						holdBoxColour = TXColor.white;
						holdTextColour = TXColor.sysInterfaceButton;
					},{
						holdBoxColour = TXColor.sysInterfaceButton;
						holdTextColour = TXColor.white;
					});
					holdButton = Button(headerBox, 100 @ 24);
					holdButton.states = [[item, holdTextColour, holdBoxColour]];
					holdButton.action = {
						showWindow = item;
						showFrontScreen = true;
						this.showView;
					};
				});	

// NEW CODE - END
				// spacing
				headerBox.decorator.nextLine;
				headerBox.decorator.shift(0, 4);
				// static text - file name
				stTextFileName = StaticText(headerBox, 1060 @ 24)
						.background_(TXColor.sysInterface)
					//	.align_(\right)
						.string_(holdFileName)
						.stringColor_(TXColor.white);
//				if (GUI.current.asSymbol == \SwingGUI, {
//					stTextFileName .font_(JFont("Gill Sans", 11));
//				},{
//					stTextFileName .font_(Font("Gill Sans", 11));
//				});
				// spacing	
				headerBox.decorator.nextLine;
			
				// spacing	
				w.view.decorator.shift(0, 20);
				// create viewBox to display selected window
				viewBox = CompositeView(w, Rect(0, 0, 2200, 900));
				if (showFrontScreen == false, {
					viewBox.decorator = FlowLayout(viewBox.bounds);
				});
				if (showWindow == "Modules", {TXChannelRouting.makeGui(viewBox);});
//	OLD			if (showWindow == "Modules", {TXModGui.makeGui(viewBox);});
//	OLD			if (showWindow == "Sequencers", {TXSeqGui.makeGui(viewBox);});
				if (showWindow == "Signal Flow", {TXSignalFlow.makeGui(viewBox);});
				if (showWindow == "Sample Bank", {TXBankBuilder2.makeSampleGui(viewBox);});
				if (showWindow == "Loop Bank", {TXBankBuilder2.makeLoopGui(viewBox);});
				if (showWindow == "Notes", {this.guiViewNotes});
				if (showFrontScreen == true, {TXFrontScreen.makeGui(viewBox, showWindow)});

			}.defer;
	
		};
	});
}

*deferRemoveView {arg holdView; 
	if (holdView.notNil, {
		if (holdView.notClosed, {
			holdView.visible_(false); 
			holdView.focus(false); 
			{holdView.remove}.defer(1);
		});
	});
}

///////////////////////////////////////////////////////////////////////////////////

*guiViewNotes {
	var noteView;
	//  spacer
	viewBox.decorator.shift(40,0);
	// create note fields with titles
	noteView =  CompositeView(viewBox,Rect(0,0,900,500));
	noteView.decorator = FlowLayout(noteView.bounds);
	//  spacer
	noteView.decorator.shift(0,50);
	// main title
	StaticText(noteView, 200 @ 30).string_("NOTES FOR THIS SYSTEM").align_(\centre)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	//  spacer
	noteView.decorator.shift(164, 0);
	//  display Network port info 
	StaticText(noteView, 396 @ 30)
		.string_("The Network Port for receiving OSC messages is  " ++ NetAddr.langPort.asString)
		.align_(\centre) .background_(TXColor.paleYellow2) .stringColor_(TXColor.sysGuiCol1);
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,20);
	//  display all notes 
	StaticText(noteView, 60 @ 24).string_("Notes 1").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes0)
		.action_({arg view; notes0 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 2").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes1)
		.action_({arg view; notes1 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 3").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes2)
		.action_({arg view; notes2 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 4").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes3)
		.action_({arg view; notes3 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 5").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes4)
		.action_({arg view; notes4 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 6").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes5)
		.action_({arg view; notes5 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 7").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes6)
		.action_({arg view; notes6 = view.value;});
	noteView.decorator.nextLine;
	//  spacer
	noteView.decorator.shift(0,5);
	// title
	StaticText(noteView, 60 @ 24).string_("Notes 8").align_(\right)
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	TextField(noteView, 700 @ 24)
		.string_(notes7)
		.action_({arg view; notes7 = view.value;});
	noteView.decorator.nextLine;
}

}

  