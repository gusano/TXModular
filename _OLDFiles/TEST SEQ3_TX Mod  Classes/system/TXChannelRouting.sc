// Copyright (C) 2010  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXChannelRouting {	// Channel Routing  

classvar	<>system;	    			// parent system - set by parent
classvar <arrChannels;  		// array of channels created
classvar <>startChannel;   		// starting channel for view
classvar chanWidth = "Narrow";	// channel width
classvar arrControls;			// gui controls
classvar <>arrControlVals; 		// gui control values - (no longer used)
classvar multiWindow=false;		// whether multi-window mode is on
classvar <>displayModule; 		// shows current module who's gui will be shown in single window mode
classvar <>displayChannel; 		// shows current channel who's gui will be highlighted
classvar <>showModuleBox;		// whether module box is shown
classvar <>showChannelType = "all";
classvar popNewModuleInd;
classvar popNewChannelInd;
classvar channelsVisibleOrigin;

*initClass{
	arrChannels = [];		
	startChannel = 0;
	chanWidth = "Narrow";	
	arrControls = nil;
	multiWindow = false;
	displayModule = nil;
	showModuleBox = false;
	channelsVisibleOrigin = Point.new(0,0);
} 

////////////////////////////////////////////////////////////////////////////////////

*saveData {	
	// this method returns an array of all data for saving with various components:
	// 0- string "TXModuleSaveData", 1- module class, 2- arrControlVals, 3- arrAllChannelData
	var arrData, arrAllChannelData;
	// collect saveData from  all modules 
	arrAllChannelData = arrChannels.collect({ arg item, i; item.saveData; });
	arrData = ["TXModuleSaveData", this.class.asString, arrControlVals, arrAllChannelData]; 
	^arrData;
}

*loadData { arg arrData, holdLoadQueue, holdLastChanCondition;   
	// this method updates all data by loading arrData. format:
	// 0- string "TXModuleSaveData", 1- module class, 2- arrControlVals, 3- arrAllChannelData
	var arrAllChannelData, holdSourceModuleID, holdSourceModule, newChannel;
	// error check
	if (arrData.class != Array, {
		TXInfoScreen.new("Error: invalid data. cannot load.");   
		^0;
	});	
	if (arrData.at(1) != this.class.asString, {
		TXInfoScreen.new("Error: invalid data class. cannot load.");   
		^0;
	});	
	// reset variable
	showChannelType = "all";	
	arrChannels = [];
	// assign values
//	arrControlVals = arrData.at(2).copy;	// no longer used
	arrAllChannelData = arrData.at(3).deepCopy;

	Routine.run {
		var holdCondition;

		// for each saved channel - recreate module, add to arrSystemModules and run loadData
		arrAllChannelData.do({ arg item, i;
			var holdChanCondition;

			// add condition to load queue
			holdChanCondition = holdLoadQueue.addCondition;
			// pause
			holdChanCondition.wait;
			// pause
			system.server.sync;
			
			// get source module data
			holdSourceModuleID = item.at(4).at(0);
			holdSourceModule = system.getModuleFromID(holdSourceModuleID);
			// create new instance of channel 
			newChannel = TXChannel.new(holdSourceModule);
			// add module to array 
			arrChannels = arrChannels.add(newChannel);
			// load ModuleID into new module
			newChannel.loadModuleID(item);

			// remove condition from load queue
			holdLoadQueue.removeCondition(holdChanCondition);
		});
		// load data into all channels
		arrAllChannelData.do({ arg item, i;
			var holdChanCondition;

			// add condition to load queue
			holdChanCondition = holdLoadQueue.addCondition;
			// pause
			holdChanCondition.wait;
			// pause
			system.server.sync;

			arrChannels.at(i).loadData(item);

			// remove condition from load queue
			holdLoadQueue.removeCondition(holdChanCondition);
		});

		// add condition to load queue
		holdCondition = holdLoadQueue.addCondition;
		// pause
		holdCondition.wait;
		// pause
		system.server.sync;

		// remove condition from load queue
		holdLoadQueue.removeCondition(holdCondition);

		arrChannels.do({ arg item, i;
			var holdChanCondition;

			// add condition to load queue
			holdChanCondition = holdLoadQueue.addCondition;
			// pause
			holdChanCondition.wait;
			// pause
			system.server.sync;

			// reactivate channel
			item.reactivate;

			// remove condition from load queue
			holdLoadQueue.removeCondition(holdChanCondition);
		});

		// add last condition to load queue
		holdLoadQueue.addCondition(holdLastChanCondition);

		// recreate view
		system.showView;
	};
}

////////////////////////////////////////////////////////////////////////////////////

*arrShowChannels{ 
	var holdArrShowChannels;
	//	create array of channels to be shown
	if (showChannelType == "all", {
		holdArrShowChannels = arrChannels;
	},{
		holdArrShowChannels = arrChannels.select({ arg item, i; item.channelRate == showChannelType; });
	});
	^holdArrShowChannels;
}

////////////////////////////////////////////////////////////////////////////////////

*addChannel{ arg argModule;
	var newChannel;
	if (system.server.serverRunning.not, {
		TXInfoScreen.new("Error: Server not running");   
		^0;
	});
	newChannel = TXChannel.new(argModule);
	arrChannels = arrChannels.add(newChannel);
	displayChannel = newChannel;
	// set position
	TXSignalFlow.setPositionNear(newChannel, argModule);
	// make sure new channel is displayed
	if ( (showChannelType == "control") and: (newChannel.channelRate ==  "audio"), {showChannelType = "all";});
	if ( (showChannelType == "audio") and: (newChannel.channelRate ==  "control"), {showChannelType = "all";});
} 

*checkDeletions {
	// reset variable 
	if (displayModule.notNil, {
		if (displayModule.deletedStatus == true, {
			displayModule = nil;
		});
	});	
	// run deletions check in all channels
	arrChannels.do({ arg item, i;  item.checkDeletions; });
	// delete any channels in  arrChannels marked for deletion
	arrChannels.do({ arg item, i;  
		if (item.toBeDeletedStatus==true, {item.deleteChannel}); 
	});
	// recreate arrChannels without deleted ones
	arrChannels = arrChannels.select({ arg item, i; item.deletedStatus == false; });
	// reorder channels
	this.reorderChannelSynths;
}

*checkRebuilds {
	// run rebuilds check in all channels
	arrChannels.do({ arg item, i;  item.checkDeletions; });
	// reorder channels
	this.reorderChannelSynths;
}

*deleteAllChannels {
	// reset variables 
	showModuleBox = false;
	displayModule = nil;	
	startChannel = 0;
	// delete all channels
	arrChannels.do({ arg item, i;  item.deleteChannel; });
	// recreate arrChannels without deleted ones
	arrChannels = [];
	// recreate TXChannel.arrInstances without deleted ones
	TXChannel.arrInstances = TXChannel.arrInstances.select({ arg item, i; item.deletedStatus == false; });
}

*checkChannelsDest { arg argModule, argOptionNo;
	// run channel dest check on all channels
	arrChannels.do({ arg item, i; 
		if (item.destModule == argModule, {
			if (item.destBusNo == argOptionNo, {
				item.deactivate;
				item.chanError = 
					"error: Modulation needs to be switched on for this bus "
					++ "in Modulation Options of destination module";
			});
		});
	});
}

*display{ arg argModule;
	if (argModule.class.moduleType == "bus" or: (argModule.class.moduleType == "channel"), {
/* removed for now
			displayModule = nil;
			showModuleBox = false;
			// update view
			system.showView;
*/
	},{
		if (multiWindow == false, {
			displayModule = argModule;
			showModuleBox = true;
			// update view
			system.showView;
		}, {
			argModule.openGui;
		});
	});
} 

*sortChannels { arg sortOption = 0;
	// sort channels 
	arrChannels.sort({ arg a, b;   a.sortKey(sortOption) <= b.sortKey(sortOption);}); 
	// reset startChannel
	startChannel = 0;
}

*reorderChannelSynths {
	// wait for server sync
	Routine.run {
		system.server.sync;
		arrChannels.do({ arg item, i;   
			item.sendSynthToTail;
		}); 
	};
}

////////////////////////////////////////////////////////////////////////////////////


*setStartChannel { arg argStartChannelNo;
	var arrChannelNos;
	if (this.arrShowChannels.size > 0, {
		// set startChannel
		arrChannelNos = this.arrShowChannels.collect({arg item, i; item.channelNo});
		startChannel = arrChannelNos.indexOf(argStartChannelNo.nearestInList(arrChannelNos));
	});
	// update view
	system.showView;
}

*getStartChannel {
	var arrChannelNos, holdStartChannel;
	holdStartChannel = 1;
	if (this.arrShowChannels.size > 0, {
		// set startChannel
		arrChannelNos = this.arrShowChannels.collect({arg item, i; item.channelNo});
		holdStartChannel = arrChannelNos.at(startChannel) ? 0;
	});
	^holdStartChannel;
}

////////////////////////////////////////////////////////////////////////////////////

*makeGui{ arg parent;
	var popNewModule, btnAddModule, chkAutoOpen, chkAutoRun;
	var popNewChannel, btnAddChannel, popDisplayMod;
	var popWidthOption, popDisplayOption, popSortOption, btnSortChannels, popMultiWindow, btnHideModule;
	var maxChannels, totalChannels, arrSelectedChannels;
	var arrAllPossSourceActClasses, arrAllPossSourceActNames, arrAllSourceModules, arrAllSourceModNames, 		arrAllSourceModsBusses, arrAllSourceModBusNames; 
	var channelsScrollView;
	var arrAllSourceActionModules, arrAllSourceActionModNames;
	var modListBox, listModules, listViewModules;
	var numStartChannel;
	var channelBox;

	//	initialise variables
	arrControls = [];
	startChannel = startChannel.min(this.arrShowChannels.size-1).max(0);

	// create array of names of all possible source modules. 
	arrAllPossSourceActClasses = system.arrAllPossModules
		.select({ arg item, i; 
			(item.moduleType == "source") 
				or: (item.moduleType == "groupsource") 
				or: (item.moduleType == "action"); 
		});  // only show source, groupsource or action modules
	arrAllPossSourceActNames = arrAllPossSourceActClasses.collect({ arg item, i; 
		var holdType;
		if (item.moduleType == "action", {
			holdType = "action";
		},{
			holdType = item.moduleRate;
		});
		item.defaultName + " ["++ holdType ++ "]"; });

	// create array of names of all system's source modules. 
	arrAllSourceModules = 	system.arrSystemModules
		.select({ arg item, i; 
			(item.class.moduleType == "source") 
			or: 
			(item.class.moduleType == "groupsource") 
			or: 
			(item.class.moduleType == "insert") ;    // allow inserts to be sources for channels
		 })
		.sort({ arg a, b; 
			a.instName < b.instName 
		});
	arrAllSourceModNames = arrAllSourceModules.collect({arg item, i;  item.instName; });

	// create array of names of all system's source, insert & action modules. 
	arrAllSourceActionModules = system.arrSystemModules
		.select({ arg item, i; 
			(item.class.moduleType == "source") 
			or: 
			(item.class.moduleType == "groupsource") 
			or: 
			(item.class.moduleType == "action") 
			or: 
			(item.class.moduleType == "insert") ;
		 })
		.sort({ arg a, b; 
			a.instName < b.instName 
		});
	arrAllSourceActionModNames = arrAllSourceActionModules.collect({arg item, i;  item.instName; });
	// create array of names of all system's source modules and busses. 
	arrAllSourceModsBusses = (
		arrAllSourceModules
		++ system.arrFXSendBusses	// array of FX send busses
		++ system.arrAudioAuxBusses	// array of Audio Aux busses 
		++ system.arrControlAuxBusses	// array of Control Aux busses 
		++ system.arrMainOutBusses	// array of Main Out busses 
	);
	arrAllSourceModBusNames = arrAllSourceModsBusses.collect({arg item, i;  item.instName; });

//	// spacing
//	parent.decorator.shift(0, 5);

	// popup - new module  
	popNewModule = PopUpMenu(parent, 200 @ 24).background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	popNewModule.items = ["Select module before adding ..."] ++ arrAllPossSourceActNames;
	popNewModule.action = {|view|
		// store current data 
		popNewModuleInd = view.value;
	};
	popNewModule.value = popNewModuleInd ? 0;
	arrControls = arrControls.add(popNewModule);

	// button - Add new Source module	  
	btnAddModule = Button(parent, 140 @ 24);
	btnAddModule.states = [["<- Add new module", TXColor.white, TXColor.sysGuiCol1]];
	btnAddModule.action = {
		var newModuleClass, newModule;
		// first item has no effect
		if ( popNewModule.value > 0, {
			// set new module class
			newModuleClass = arrAllPossSourceActClasses.at(popNewModule.value - 1);
			// ask system to add new module
			newModule = system.addModule(newModuleClass);
			// set startChannel 
// removed	startChannel = (this.arrShowChannels.size - 1).max(0);
			displayModule = newModule;	
			showModuleBox = true;
			// scroll to end
			this.setScrollToEndChannel;
			// update view
			system.showView;
		});
	};

	// spacing
	parent.decorator.shift(17, 0);

	// button - hide module box
	if (showModuleBox == true, {
		btnHideModule = Button(parent, 106 @ 24);
		btnHideModule.states = [["Hide Module Box", TXColor.white, TXColor.sysGuiCol2]];
		btnHideModule.action = {
			if ( showModuleBox == true, {
				// reset variables
				showModuleBox = false;
				displayModule = nil;	
				// update view
				system.showView;
			});
		};
		arrControls = arrControls.add(btnHideModule);
	}, {
		// spacer 
		StaticText(parent, 106 @ 24);
	});

	// spacing
	parent.decorator.shift(17, 0);

// AutoRun checkbox removed for now
//	// checkbox - automatically run module when  added
//	chkAutoRun = TXCheckBox (parent, 90 @ 24, "Auto Run", TXColour.sysGuiCol1, TXColour.white, 
//		TXColour.sysGuiCol1, TXColour.white);
//	if (system.autoRun == true, {
//		chkAutoRun.value_(1);
//	}, {
//		chkAutoRun.value_(0);
//	});
//	chkAutoRun.action = {|view|
//		// store current data to arrControlVals
//		arrControlVals.put(arrControls.indexOf(view), TXViewHolder.getData(view));
//		system.autoRun = (TXViewHolder.getData(view) == [1]);
//	};
//	arrControls = arrControls.add(chkAutoRun);
	
	// popup - new channel  
	popNewChannel = PopUpMenu(parent, 180 @ 24).background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	popNewChannel.items = ["Select channel source ..."] ++ arrAllSourceModBusNames;
	popNewChannel.action = {|view|
		// store current data 
		popNewChannelInd  = view.value;
	};
	popNewChannel.value = popNewChannelInd ? 0;
	arrControls = arrControls.add(popNewChannel);

	// button - add new Channel  
	btnAddChannel = Button(parent, 210 @ 24);
	btnAddChannel.states = [["<- Add new channel with this source", TXColor.white, TXColor.sysGuiCol1]];
	btnAddChannel.action = {
		// first item has no effect
		if (popNewChannel.value > 0, {
		// add new channel from source module
			this.addChannel(arrAllSourceModsBusses.at(popNewChannel.value - 1));
			// update variable
//	// removed	startChannel = startChannel.max(this.arrShowChannels.size-6).min(this.arrShowChannels.size-1).max(0);//
//			// reset variable 
//			displayModule = nil;	
			// scroll to end
			this.setScrollToEndChannel;
			// update view
			system.showView;
		});
	};

// Removed for now
//	// popup - open modules in multiple windows  
//	popMultiWindow = PopUpMenu(parent, 110 @ 24).background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
//	popMultiWindow.items = [ 
//		"Single Window",
//		"Multiple Windows" 
//	];
//	popMultiWindow.action = {|view|
//		// store current data to arrControlVals
//		arrControlVals.put(arrControls.indexOf(view), TXViewHolder.getData(view));
//		// store current value to classvar
//		if (view.value == 0, {multiWindow = false}, {multiWindow = true});
//		// update view
//		system.showView;
//	};
//	arrControls = arrControls.add(popMultiWindow);
	
/* no longer needed --------
	// spacing
	parent.decorator.shift(0, 5);

	// spacer line	
	parent.decorator.nextLine;

	// popup - display module selector  
	popDisplayMod = PopUpMenu(parent, 250 @ 24)
		.background_(TXColor.white.blend(TXColor.sysGuiCol2, 0.1)).stringColor_(TXColor.sysGuiCol1);
	popDisplayMod.items = ([ "All Modules "] ++ arrAllSourceActionModNames)
		.collect({ arg item, i; "Module Display: " ++ item;});
	popDisplayMod.action = {|view|
		// set variable 
		if (view.value == 0, {
			displayModule = nil;
		}, {
			displayModule = arrAllSourceActionModules.at(view.value-1)
		});
		// update view
		system.showView;
	};
	popDisplayMod.value = ((arrAllSourceActionModules.indexOf(displayModule) ? -1) + 1);
*/


	// spacer 
	StaticText(parent, 20 @ 20);

	// popup - display option selector  
	popDisplayOption  = PopUpMenu(parent, 220 @ 24)
		.background_(TXColor.white.blend(TXColor.sysGuiCol2, 0.1)).stringColor_(TXColor.sysGuiCol1);
	popDisplayOption.items = ["Show Audio & Control Channels", 
		"Show Audio Channels Only", 
		"Show Control Channels Only"
	];
	popDisplayOption.action = {|view|
			// store current value to classvar and reset startChannel
		if (view.value == 0, {showChannelType = "all"; startChannel = 0; });
		if (view.value == 1, {showChannelType = "audio"; startChannel = 0; });
		if (view.value == 2, {showChannelType = "control"; startChannel = 0; });
		// update view
		system.showView;
	};
	// update value	
	popDisplayOption.value = ['all', 'audio', 'control'].indexOf(showChannelType.asSymbol);
	arrControls = arrControls.add(popDisplayOption);

/*
// starting channel removed when scrollview added 
//
	// numberbox starting channel
	numStartChannel = TXScrollNumBox(parent, 30 @ 24, ControlSpec(1, (this.arrShowChannels.size).max(1)))
		.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
		.inc_(1.0);
	numStartChannel.action_({arg view; this.setStartChannel(view.value.asInteger)});
	numStartChannel.value = this.getStartChannel;
	
	// navigation buttons
	[ ["|<", -300], ["<<<", -7], ["<<", -3], ["<", -1], [">", 1], [">>", 3], [">>>", 7], [">|", 300] ]
		.do({ arg item, i;
			Button(parent, 30 @ 24)
				.states_([[item.at(0), TXColor.white, TXColor.sysGuiCol1]])
				.action_({
					// adjust startChannel
					startChannel = (startChannel + item.at(1)).min(this.arrShowChannels.size-1).max(0);
					// update view
					system.showView;
				});
	});
	// spacer 
	StaticText(parent, 20 @ 20);
*/

/*
// width option removed for now - add later if necessary
//
	// popup - width option selector  
	popWidthOption = PopUpMenu(parent, 70 @ 24).background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	popWidthOption.items = ["Narrow", "Wide"];
	popWidthOption.action = {|view|
		// store current data to arrControlVals
		arrControlVals.put(arrControls.indexOf(view), TXViewHolder.getData(view));
		// adjust startChannel
		chanWidth = popWidthOption.items.at(popWidthOption.value);
		// update view
		system.showView;
	};
	arrControls = arrControls.add(popWidthOption);


// sort option removed for now - add later if necessary
//
	// spacer 
	StaticText(parent, 14 @ 20);
	// popup - sort order selector  
	popSortOption = PopUpMenu(parent, 310 @ 24).background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	popSortOption.items = [
		"Order: Channel No.", 
		"Order: Control by Source, Audio by Source", 
		"Order: Control by Source, Audio by Destination", 
		"Order: Control by Destination, Audio by Source", 
		"Order: Control by Destination, Audio by Destination"
	];
	popSortOption.action = {|view|
		// store current data to arrControlVals
		arrControlVals.put(arrControls.indexOf(view), TXViewHolder.getData(view));
		// run action
		btnSortChannels.doAction;
	};
	arrControls = arrControls.add(popSortOption);
	// button - sort  channels 
	btnSortChannels = Button(parent, 60 @ 24);
	btnSortChannels.states = [["Sort", TXColor.white, TXColor.sysGuiCol1]];
	btnSortChannels.action = {
		// sort channels 
		this.sortChannels(popSortOption.value);
		// update view
		system.showView;
	};
	arrControls = arrControls.add(btnSortChannels);
*/

	// spacer 
	StaticText(parent, 14 @ 20);

	// spacing
	parent.decorator.shift(0, 5);

	// spacer line	
	parent.decorator.nextLine;

	// list of all modules	
	modListBox =  CompositeView(parent,Rect(0,0, 153, 570));  
	modListBox.background = TXColour.sysChannelAudio;
	modListBox.decorator = FlowLayout(modListBox.bounds);
	listModules = [ "System Modules: "] ++ arrAllSourceActionModNames;
	listViewModules = ListView(modListBox, 145 @ 562)
			.items_(listModules)
			.background_(TXColor.white)
			.stringColor_(TXColor.sysGuiCol1)
			.hiliteColor_(TXColor.sysGuiCol1)
			.action_({|view|
				// set variable 
				if (view.value == 0, {
					displayModule = nil;
					showModuleBox = false;
				}, {
					displayModule = arrAllSourceActionModules.at(view.value-1);
					showModuleBox = true;
				});
				// update view
				{system.showView}.defer;
			});
	
	if ( displayModule.notNil, {
		listViewModules.value_(arrAllSourceActionModNames.indexOf(displayModule.instName)?0 + 1);
	},{
		listViewModules.value_(0);
	});

	// decorator shift	
	parent.decorator.shift(10, 0);

	// display module gui
	if ( (multiWindow == false) and: (showModuleBox == true), {
		if ( displayModule.notNil, {
			// display module
			displayModule.openGui(parent);		 
		},{	 
			TextView(parent,Rect(0,0, 500, 570))
				.background_(TXColor.sysModuleWindow);
		});
		// decorator shift	
		parent.decorator.shift(10, 0);
	});

	if ( (multiWindow == false) and: (showModuleBox == true), {
		maxChannels = 4;		// max no of channels to be displayed at a time 
	},{	
		maxChannels = 8;		// max no of channels to be displayed at a time 
	});
	totalChannels = (this.arrShowChannels.size - startChannel).min(maxChannels);

	// if channels to show, show channel row titles first
	if (totalChannels > 0, {
		TXChannel.makeTitleGui(parent);
	});
	
	// show channels	

/* NEW CODE FOR CHANNEL SCROLLING */
	channelsScrollView = ScrollView(parent, Rect(0, 0, 4 + (maxChannels * 132), 585))
		.hasBorder_(false).autoScrolls_(false);
	channelsScrollView.action = {arg view; channelsVisibleOrigin = view.visibleOrigin; };
	channelBox = CompositeView(channelsScrollView, Rect(0, 0, 4 + ((this.arrShowChannels.size+2) * 132), 570));
	channelBox.decorator = FlowLayout(channelBox.bounds);
	channelBox.decorator.margin.x = 0;
	channelBox.decorator.margin.y = 0;
	channelBox.decorator.reset;

	totalChannels = this.arrShowChannels.size;
	totalChannels.do({ arg item, i;
		this.arrShowChannels.at(i).makeChannelGui(channelBox);
	});
	
	this.setScrollToCurrentChannel(maxChannels);	
	channelsScrollView.visibleOrigin = channelsVisibleOrigin;

/* OLD CODE
	totalChannels = (this.arrShowChannels.size - startChannel);
	totalChannels.do({ arg item, i;
		this.arrShowChannels.at(startChannel+i).makeChannelGui(parent);
	});
*/

} 

*setScrollToCurrentChannel { arg maxChannels;
	var	holdIndex, curChannelLeft; 
	// if current channel not visible then move origin
	if (displayChannel.notNil, {
		holdIndex = this.arrShowChannels.indexOf(displayChannel);
		if (holdIndex.notNil, {
			curChannelLeft = 4 + (holdIndex * 132);
			if ( (curChannelLeft < channelsVisibleOrigin.x) or: 
				(curChannelLeft > (channelsVisibleOrigin.x +((maxChannels-1) * 132))) , {
					channelsVisibleOrigin.x = curChannelLeft;
				channelsVisibleOrigin = Point.new(curChannelLeft, 0);
			});
		});
	});
}

*setScrollToEndChannel {
	if (this.arrShowChannels.size > 3, {
		channelsVisibleOrigin = Point.new((this.arrShowChannels.size-3).max(0) * 132, 0);
	});
}

}


