// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXFrontScreen {	// Front Screen gui  

classvar	<>system;	    			// system
classvar <>arrWidgets;	
classvar arrWidgetClasses;
classvar currAddWidgetInd;
classvar <>currWidgetInd;
classvar holdWidgetPropertyList;
classvar holdWidgetClass;
classvar screen;
classvar screenWidth;
classvar screenHeight;
classvar screenColour;
classvar gridSize;
classvar arrFonts;
classvar lockWidgets;
classvar arrKeyDownFunctions;
classvar arrLayers;	
classvar arrMidiRoutines;
classvar arrMidiResponders;
classvar layerNo = 0;	
classvar layerName = " ";	
classvar holdWidgetWidth;
classvar holdWidgetHeight;
classvar clipboard1, clipboard2, clipboard3, clipboard4;

*initClass{
	// initialise class variables
	arrWidgetClasses = [          // <--------- list of  widget classes  
		TXWActionButton,
		TXWCheckBox,
		TXWLabelBox,
		TXWNotesBox, 
		TXWNumberBox,
		TXWPopup,
		TXWSlider,
		TXWSliderV,
		TXWSliderNo,
		TXWSliderNoV,
		TXW2DSlider,
		TXWTextDisplayBox,
		TXWTextEditBox,
	];
	holdWidgetWidth = 100; // initialise default width and heigth
	holdWidgetHeight = 20; 
	clipboard1 = clipboard2 = clipboard3 = 0;
	clipboard4 = "(text)";
	
	currAddWidgetInd = 0;
	currWidgetInd = 0;
	layerNo = 0;	
	layerName = " ";
	this.initArrLayers;
	this.initArrWidgets;
	arrMidiRoutines = [];
	arrMidiResponders = [];
	screenWidth = 1000;
	screenHeight = 550;
	gridSize = 10;
	lockWidgets = 1;
	screenColour = TXColor.sysInterface.copy;
	arrFonts = ["Arial", "Arial-Black", "AmericanTypewriter", "AndaleMono", "Baskerville", "BigCaslon",
		 "BrushScriptMT", "Cochin", "ComicSansMS", "Copperplate",  "Courier", "CourierNewPS", "Didot", 
		 "Futura", "Georgia", "GillSans", "Helvetica", "HelveticaNeue", "Herculanum", "Impact", 
		 "MarkerFelt-Thin", "MarkerFelt-Wide", "Monaco", "Optima", "Palatino", "Papyrus", "Symbol", 
		 "TechnoRegular", "Times", "TimesNewRomanPS", "TrebuchetMS","Verdana", "VT100", "VT100-Bold", 
		 "Webdings", "ZapfDingbatsITC", "Zapfino" 
	];
	arrKeyDownFunctions = [];
} // end of class method initClass

*initArrWidgets{
	TXWidget.resetWidgetID;
	// default is 1 textbox
	arrWidgets = [TXWLabelBox.new(0.05, 0.95, 40, 250, ["Screen 1", nil, nil, 16])];
} 

*initArrLayers{
	var holdArrWidgets, holdScreenColour, holdLayer;
	arrLayers = [];
	20.do({ arg item, i;
		TXWidget.resetWidgetID;
		holdScreenColour = TXColor.sysInterface.copy;
		// default is 1 textbox
		holdArrWidgets = [TXWLabelBox.new(0.05, 0.95, 40, 250, 
			["Screen " ++ (i+1).asString, nil, nil, 16])];
		holdLayer = [
			holdArrWidgets.collect({arg item, i; [item.class.asSymbol, item.getPropertyList]; }), 
			holdScreenColour.asArray,
			" ",
			TXWidget.holdNextWidgetID
		];
		arrLayers = arrLayers.add(holdLayer);
	});
} 

*saveData{
	^[	
		TXWidget.holdNextWidgetID, 
		arrWidgets.collect({arg item, i; [item.class.asSymbol, item.getPropertyList]; }),
		screenColour.asArray,
		arrLayers,
		layerNo,
		layerName,
		gridSize,
		screenWidth,
		screenHeight
	];
} 

*templateSaveData{
	^[
		"TXScreenTemplateSaveData",
		[	
			TXWidget.holdNextWidgetID, 
			arrWidgets.collect({arg item, i; [item.class.asSymbol, item.getTemplatePropertyList]; }),
			screenColour.asArray,
			nil,
			nil,
			layerName,
			gridSize,
			screenWidth,
			screenHeight
		]
	];
} 

*loadData{ arg arrData;
	if (arrData.isNil, {^0});
//	arrData = arrData.deepCopy;
	// create widgets
	arrWidgets = arrData.at(1).collect({arg item, i; item.at(0).asClass.new; });
	// load data
	arrWidgets.do({arg item, i; item.setPropertyList(arrData.at(1).at(i).at(1)); });
	// set data
	TXWidget.holdNextWidgetID = arrData.at(0);
	if (arrData.at(2).notNil, {screenColour = Color.fromArray(arrData.at(2));});
	if (arrData.at(4).notNil, {layerNo = arrData.at(4).deepCopy; });
	if (layerNo.isNil or: layerNo.isInteger.not, {layerNo = 0;});
	if (arrData.at(3).notNil, {
		arrLayers = arrData.at(3).deepCopy; 
	},{
		this.storeCurrentLayer;   // if nil, store current layer from arrWidgets
	});
	if (arrData.at(5).notNil, {layerName = arrData.at(5).copy; }, {layerName = " ";});
	if (arrData.at(6).notNil, {gridSize = arrData.at(6).copy; }, {gridSize = 10;});
	if (arrData.at(7).notNil, {screenWidth = arrData.at(7).copy; }, {screenWidth = 1000;});
	if (arrData.at(8).notNil, {screenHeight = arrData.at(8).copy; }, {screenHeight = 550;});
	^arrWidgets.size;
} 

*templateLoadData { arg arrData;
	// error check
	if (arrData.class != Array, {
		TXInfoScreen.new("Error: invalid data. cannot load.");   
		^0;
	});	
	if (arrData.at(0) != "TXScreenTemplateSaveData", {
		TXInfoScreen.new("Error: File is not a Screen Template. Cannot load.");   
		^0;
	});	
	this.loadData(arrData.at(1));
}
*storeCurrentLayer {
	var holdLayer;
	holdLayer = [
		arrWidgets.collect({arg item, i; [item.class.asSymbol, item.getPropertyList]; }),
		screenColour.asArray,
		layerName,
		TXWidget.holdNextWidgetID
	];
	// store layer 
	arrLayers.put(layerNo, holdLayer.deepCopy);
}

*loadLayer { arg argLayerNo;
	var holdLayer;
	// deactivate any midi and keydown functions
	this.midiDeActivate;
	this.keyDownDeActivate;
	// load new layer
	layerNo = argLayerNo;
	holdLayer = arrLayers.at(argLayerNo).deepCopy;
	// create widgets
	arrWidgets = holdLayer.at(0).collect({arg item, i; item.at(0).asClass.new; });
	// load data
	arrWidgets.do({arg item, i; item.setPropertyList(holdLayer.at(0).at(i).at(1)); });
	screenColour = Color.fromArray(holdLayer.at(1));
	layerName = holdLayer.at(2).copy;
	TXWidget.holdNextWidgetID = holdLayer.at(3).copy;
}

*getLayerName { arg argLayerNo;
	var holdLayer, layerName;
	holdLayer = arrLayers.at(argLayerNo);
	layerName = holdLayer.at(2).copy
	^layerName;
}

*storeCurrLoadNewLayer { arg newLayerNo;
	this.storeCurrentLayer;
	layerNo = newLayerNo; 
	this.loadLayer(layerNo);
	// update variable
	currWidgetInd = 0;
}
*storeCurrLoadNextLayer {
	var newLayerNo;
	newLayerNo = (layerNo + 1);
	if (newLayerNo < arrLayers.size, {this.storeCurrLoadNewLayer(newLayerNo);});
}
*storeCurrLoadPrevLayer {
	var newLayerNo;
	newLayerNo = (layerNo - 1); 
	if (newLayerNo > -1,  {this.storeCurrLoadNewLayer(newLayerNo);});
}

*overwriteCurrFromLayer { arg newLayerNo;
	var holdLayerNo;
	holdLayerNo = layerNo;
	this.loadLayer(newLayerNo);
	layerNo = holdLayerNo; 
	this.storeCurrentLayer;
	// update variable
	currWidgetInd = 0;
}

*copyDisplayProperties {
	^#[\height, \width, \backgroundAsArgs,
	\string, \stringColorAsArgs, \font, \fontSize,
	\knobColourAsArgs, \thumbSize, \colourReverse
	];
}

*copyAllProperties {
	^this.copyDisplayProperties ++
	#[\arrActions, 
	\midiListen, \midiNote, \midiCCNo, \midiMinChannel, \midiMaxChannel,
	\keyListen, \keyChar, \arrActions2, \midiCCNo2, 
	];
}

*checkDeletions{
	arrWidgets.do({ arg item, i;
		var holdModuleID, holdModule;
		if (item.properties.includes(\arrActions), {
			item.arrActions.do({ arg action, i;
				holdModuleID = action.at(0);
				holdModule = system.getModuleFromID(holdModuleID);
				if ((holdModule != 0) and: (holdModuleID != 99), {
					// if module is being deleted, reset widget action
					if ((holdModule.toBeDeletedStatus == true) or:(holdModule.deletedStatus == true), {
						action.put(0, 99);
						action.put(1, 0);
						if (action.size<8, {action = action.addAll([nil, nil, nil, nil, nil, nil])
						},{
							action.put(7, nil);
						});
					}); 
				}); 
			});
		});
		if (item.properties.includes(\arrActions2), {
			item.arrActions2.do({ arg action, i;
				holdModuleID = action.at(0);
				holdModule = system.getModuleFromID(holdModuleID);
				if ((holdModule != 0) and: (holdModuleID != 99), {
					// if module is being deleted, reset widget action
					if ((holdModule.toBeDeletedStatus == true) or:(holdModule.deletedStatus == true), {
						action.put(0, 99);
						action.put(1, 0);
						if (action.size<8, {action = action.addAll([nil, nil, nil, nil, nil, nil])
						},{
							action.put(7, nil);
						});
					}); 
				}); 
			});
		});
	});
} 

*registerMidiRoutine {arg argRoutine;
	// add to array   
	arrMidiRoutines = arrMidiRoutines.add(argRoutine);
}

*registerMidiResponder {arg argResponder;
	// add to array   
	arrMidiResponders = arrMidiResponders.add(argResponder);
}

*midiDeActivate{
	// stop all midi routines  
	arrMidiRoutines.do({ arg item, i;
		item.stop;
	});
	arrMidiRoutines = [];
	// stop all midi Responders  
	arrMidiResponders.do({ arg item, i;
		item.remove;
	});
	arrMidiResponders = [];
}

*addKeyDownActionFunction { arg argFunction;
	// add function to array  
	arrKeyDownFunctions = arrKeyDownFunctions.add(argFunction);
}

*keyDownDeActivate {
	// remove all functions  
	arrKeyDownFunctions = [];
}

*runKeyDownActionFunctions { arg char, modifiers, unicode, keycode;
	// run all functions  
	arrKeyDownFunctions.do({arg item, i; item.value(char, modifiers, unicode, keycode)});
}

*fitAllWidgetsToGrid{
	// set all widgets  
	arrWidgets.do({ arg item, i;
		item.fitToGrid(gridSize, screenWidth, screenHeight);
	});
}

*loadScreenTemplate {
	var newPath, newFile, newData;
	CocoaDialog.getPaths({ arg paths;
		newPath = paths.at(0);
		newFile = File(newPath,"r");
		newData = thisProcess.interpreter.executeFile(newPath);
		newFile.close;
		this.templateLoadData(newData);
		// update view
		system.showView;
	});
}

*saveScreenTemplate {
	var newPath, newFile, newData;
	CocoaDialog.savePanel({ arg path;
		newPath = path;
		newData = this.templateSaveData;
		newFile = File(newPath,"w");
		newFile << "#" <<< newData << "\n";
		//	use file as an io stream
		//	<<< means store the compile string of the object
		//	<< means store a print string of the object
		newFile.close;
	});
}

*deleteWidgetAtInd { arg currWidgetInd;
	arrWidgets.removeAt(currWidgetInd);
	if (arrWidgets.size == 0, {
		this.initArrWidgets;
	});
} 

*deleteHighlitWidgets {
	var holdInds = [];
	arrWidgets.do({arg item, ind; 
		if (item.highlight == true, {
			holdInds = holdInds.add(ind);
		});
	});
	holdInds.sort.reverse.do({arg holdInd; 
		this.deleteWidgetAtInd(holdInd);
	});
}

*resetCurrWidgetInd {
	currWidgetInd = arrWidgets.size - 1;
	// update view
	system.flagGuiUpd;
}

*makeGui{ arg window, viewMode;

	var frontView, fromTop, fromLeft, layoutView, arrLayoutXvals, arrLayoutYvals, arrWidgetTexts, addWidgetPopupView;
	var lockWidgetsCheckbox, currWidgetIDPopupView, currWidgetNameText, currGridSizePopupView;
	var currWidgetHeight, currWidgetWidth, currWidgetFromLeft, currWidgetFromTop, arrSizes;
	var arrScreenSizes, screenWidthBox, screenHeightBox, offsetHeight;
	var currWidgetProperties, propertiesBox, actionsBox, actionCount, newWidgetHeight, newWidgetWidth;
	var notesBox, notesView, updateButton, colourPickerButton, boxColourBox;
	var screenColourPopup, screenColourRed, screenColourGreen, screenColourBlue, screenColourAlpha, screenColourBox;
	var backgroundPopup, backgroundRed, backgroundGreen, backgroundBlue, backgroundAlpha;
	var colourRevCheckbox, colourSwapButton, clearTextButton, knobColourBox, textColourBox;
	var knobColourPopup, knobColourRed, knobColourGreen, knobColourBlue, knobColourAlpha, knobWidthBox, numberSizeBox;	var rotateButton, labelText, fontPopup, fontSizeBox, holdView;
	var midiListenCheckbox, midiNoteBox, midiNotePopup, midiCCNoBox, midiCCNoBox2;
	var midiChannelMinPopup, midiChannelMaxPopup, keyListenCheckbox, keyTextField;
	var copyPropertiesButton, pastePropertiesButton, selectActionsButton;
	var layerBar, layerPopupView, layerNameText, replaceLayerActions, replaceLayerPopupView;
	var snapshotPopupView, snapshotPopupWidth, snapshotPopupItems, snapshotNameText, holdActionText;

	// deactivate any midi and keydown functions
	TXFrontScreen.midiDeActivate;
	TXFrontScreen.keyDownDeActivate;
	
	// set variables 
	fromTop = 65;
	fromLeft = 7;
	// width & height choices
	arrSizes = [" ", 1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 
		100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 
		200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 
		300, 320, 340, 360, 380, 400, 420, 440, 460, 480, 500];  
	arrScreenSizes = [" ",  480, 500, 550, 600, 640, 700, 720, 768, 800, 900, 1000, 1024, 
		1100, 1152, 1200, 1280, 1300, 1400, 1440, 1500, 1600, 1700, 1800, 1900, 2000 ];  

//  ================== ================== ================== ================== ==================  

	if (viewMode == "Run interface", {
		// create layer bar  
		layerBar = CompositeView(window, Rect(0, 0, 1000, 30));
		layerBar.decorator = FlowLayout(layerBar.bounds);
		// popup - current layer  
		layerPopupView = PopUpMenu(layerBar, Rect(0, 30, 380, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(
//				(1 .. 20).collect({arg item, i; "Screen " ++ item.asString})
				arrLayers.collect({arg item, i; 
					 "Screen " ++ (i + 1).asString ++ " - " ++ item.at(2)
				})
			)
			.action_({arg view; 
				this.storeCurrLoadNewLayer(view.value);
				// update variables
				currWidgetInd = 0;
//				layerNameText.string = layerName;
				// update view
				system.showView;
			});
		layerPopupView.value = layerNo;

//		// text - current layer name 
//		layerNameText = StaticText(layerBar, Rect(0, 0, 300, 20))
//			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
//			.align_(\left);
//		layerNameText.string = layerName;
		
		// add spacer
		layerBar.decorator.shift(40, 0);

		// set popup width
		if ( (system.snapshotNo == 0) or: system.snapshotIsEmpty(system.snapshotNo).not, {  
			snapshotPopupWidth = 424;
		},{
			snapshotPopupWidth = 130;
		});
		// set popup items
		snapshotPopupItems = ["Load Snapshot ..."] 
			++ (1 .. 99).collect({arg item, i;
			 	var holdName;
			 	holdName = system.getSnapshotName(item);
			 	if ( (system.snapshotNo == 0) or: system.snapshotIsEmpty(system.snapshotNo), { 
				 	if (holdName == "", {holdName = "Empty"});
				});
			 	"Snapshot " ++ item.asString ++ ": " ++ holdName;
			 });
		// popup - current snapshot  
		snapshotPopupView = PopUpMenu(layerBar, Rect(0, 0, snapshotPopupWidth, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(snapshotPopupItems)
			.action_({arg view; 
				system.loadSnapshot(view.value);
				// update view
				system.showView;
			});
		snapshotPopupView.value = system.snapshotNo;

		// if a real snapshot is selected
		if (system.snapshotNo > 0, {  
			if (system.snapshotIsEmpty(system.snapshotNo), {  
	
				// text field - current snapshot name 
				snapshotNameText = TextField(layerBar, Rect(0, 0, 300, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
					.align_(\left);
	
				// button - save snapshot	  
				Button(layerBar, Rect(0, 0, 100, 20))
					.states_([["Save snapshot", TXColor.white, TXColor.sysGuiCol1]])
					.action_({
						var holdName;
						holdName = snapshotNameText.string;
						// save snapshot
						system.saveCurrentSnapshot(holdName);
						// update view
						system.showView;
					});
			},{
				// button - Delete snapshot	  
				Button(layerBar, Rect(0, 0, 60, 20))
					.states_([["Overwrite", TXColor.white, TXColor.sysGuiCol2]])
					.action_({
						// delete 
						system.overwriteCurrentSnapshot;
						// update view
						system.showView;
					});
				// button - Delete snapshot	  
				Button(layerBar, Rect(0, 0, 60, 20))
					.states_([["Delete", TXColor.white, TXColor.sysDeleteCol]])
					.action_({
						// delete 
						system.deleteCurrentSnapshot;
						// update view
						system.showView;
					});
			});
		});

		// draw screen  
		screen = CompositeView(window, Rect(4, 30, screenWidth, screenHeight)).background_(screenColour);
		// build gui for all widgets  
		arrWidgets.do({ arg item, i;
			item.buildGui(screen, 0, 0, screenWidth, screenHeight);
		});
	});
	
//  ================== ================== ================== ================== ==================  

	if (viewMode == "Design layout", {

		// create layer bar  
		layerBar = CompositeView(window, Rect(0, 0, 1400, 30));
		layerBar.decorator = FlowLayout(layerBar.bounds);

		// popup - current layer  
		layerPopupView = PopUpMenu(layerBar, Rect(0, 0, 80, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_((1 .. 20).collect({arg item, i; "Screen " ++ item.asString}))
			.action_({arg view; 
				this.storeCurrLoadNewLayer(view.value);
				// update variables
				currWidgetInd = 0;
				// update view
				system.showView;
			});
		layerPopupView.value = layerNo;

		// text  
		StaticText(layerBar, Rect(0, 0, 15, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\center)
			.string_("W");
			
		// numberbox - screen width
		screenWidthBox = NumberBox(layerBar, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				screenWidth = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		screenWidthBox.value = screenWidth;

		// popup - width choices
		PopUpMenu(layerBar, Rect(0, 0, 14, 20))
			.items_(arrScreenSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {screenWidthBox.valueAction = arrScreenSizes.at(view.value);});
			});

		// text  
		StaticText(layerBar, Rect(0, 0, 15, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\center)
			.string_("H");
			
		// numberbox - screen height
		screenHeightBox = NumberBox(layerBar, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				screenHeight = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		screenHeightBox.value = screenHeight;

		// popup - height choices
		PopUpMenu(layerBar, Rect(0, 0, 14, 20))
			.items_(arrScreenSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {screenHeightBox.valueAction = arrScreenSizes.at(view.value);});
			});

		// add spacer
		layerBar.decorator.shift(10, 0);

		// popup - current widget id  
		currWidgetIDPopupView = PopUpMenu(layerBar, Rect(0, 0, 80, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(arrWidgets.collect({arg item, i; "Widget " ++ item.widgetID.asString}))
			.action_({arg view; 
				currWidgetInd = view.value; 
				currWidgetNameText.string = arrWidgets.at(view.value).class.widgetName;
				// update view
				system.showView;
			});
		currWidgetIDPopupView.value = currWidgetInd?0;
		// add screen update function
		system.addScreenUpdFunc(
			[currWidgetIDPopupView], 
			{ arg argArray;
				var currWidgetIDPopupView = argArray.at(0);
				currWidgetIDPopupView.valueAction_(currWidgetInd ? 0);
			}
		);

		// text - current widget name 
		currWidgetNameText = StaticText(layerBar, Rect(0, 0, 100, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\left);
		if (arrWidgets.notNil, {currWidgetNameText.string = arrWidgets.at(currWidgetInd).class.widgetName;});

		// text  
		StaticText(layerBar, Rect(0, 0, 15, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\center)
			.string_("W");
			
		// numberbox - current widget width
		currWidgetWidth = NumberBox(layerBar, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				arrWidgets.at(currWidgetInd).width = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currWidgetWidth.value = arrWidgets.at(currWidgetInd).width;
		// add screen update function
		system.addScreenUpdFunc(
			[currWidgetWidth], 
			{ arg argArray;
				var currWidgetWidth = argArray.at(0);
				currWidgetWidth.value_(arrWidgets.at(currWidgetInd ? 0).width);
			}
		);

		// popup - width choices
		PopUpMenu(layerBar, Rect(0, 0, 14, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {currWidgetWidth.valueAction = arrSizes.at(view.value);});
			});

		// text  
		StaticText(layerBar, Rect(0, 0, 15, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\center)
			.string_("H");
			
		// numberbox - current widget height
		currWidgetHeight = NumberBox(layerBar, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				arrWidgets.at(currWidgetInd).height = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currWidgetHeight.value = arrWidgets.at(currWidgetInd).height;
		// add screen update function
		system.addScreenUpdFunc(
			[currWidgetHeight], 
			{ arg argArray;
				var currWidgetHeight = argArray.at(0);
				currWidgetHeight.value_(arrWidgets.at(currWidgetInd ? 0).height);
			}
		);

		// popup - height choices
		PopUpMenu(layerBar, Rect(0, 0, 14, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {currWidgetHeight.valueAction = arrSizes.at(view.value);});
			});

		// add spacer
		layerBar.decorator.shift(10, 0);

		// text  
		StaticText(layerBar, Rect(0, 0, 30, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\right)
			.string_("Grid");
			
		// popup - current grid size
		currGridSizePopupView = PopUpMenu(layerBar, Rect(0, 0, 40, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_([1,3,5,10,20,30,40,60,80,100].collect({arg item; item.asString}))
			.action_({arg view; 
				gridSize = [1,3,5,10,20,30,40,60,80,100].at(view.value); 
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currGridSizePopupView.value = [1,3,5,10,20,30,40,60,80,100].indexOf(gridSize);

		// add spacer
		layerBar.decorator.shift(10, 0);

		// button - Add new Source widget	  
		Button(layerBar, Rect(0, 0, 95, 20))
			.states_([["Add widget", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				// add widget
				arrWidgets = arrWidgets.add(arrWidgetClasses.at(addWidgetPopupView.value)
					.new(nil, nil, holdWidgetHeight, holdWidgetWidth));
				// update variables
				currWidgetInd = arrWidgets.size - 1;
				// update view
				system.showView;
			});
		
		// popup - new widget  
		arrWidgetTexts = arrWidgetClasses.collect({arg item, i; item.widgetName});
		addWidgetPopupView = PopUpMenu(layerBar, Rect(0, 0, 120, 20))
			.background_(TXColor.paleYellow2).stringColor_(TXColor.sysGuiCol1)
			.items_(arrWidgetTexts)
			.action_({arg view; 
				currAddWidgetInd = view.value;
				addWidgetPopupView.dragLabel_(arrWidgetTexts[addWidgetPopupView.value.asInteger]);
			})
			.beginDragAction_({arrWidgetClasses[addWidgetPopupView.value]
				.new(nil, nil, holdWidgetHeight, holdWidgetWidth);})
		;
		addWidgetPopupView.value = currAddWidgetInd;
		addWidgetPopupView.dragLabel_(arrWidgetTexts[addWidgetPopupView.value.asInteger]);
		
		// text  
		StaticText(layerBar, Rect(0, 0, 15, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\center)
			.string_("W");
			
		// numberbox - add widget width
		newWidgetWidth = NumberBox(layerBar, Rect(0, 0, 35, 20))
			.action_({arg view; 
				holdWidgetWidth = view.value;
			});
		newWidgetWidth.value = holdWidgetWidth;

		// popup - width choices
		PopUpMenu(layerBar, Rect(0, 0, 15, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {newWidgetWidth.valueAction = arrSizes.at(view.value);});
			});

		// text  
		StaticText(layerBar, Rect(0, 0, 15, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\center)
			.string_("H");
			
		// numberbox - add widget height
		newWidgetHeight = NumberBox(layerBar, Rect(0, 0, 35, 20))
			.action_({arg view; 
				holdWidgetHeight = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		newWidgetHeight.value = holdWidgetHeight;

		// popup - height choices
		PopUpMenu(layerBar, Rect(0, 0, 15, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {newWidgetHeight.valueAction = arrSizes.at(view.value);});
			});
		
		// add spacer
		layerBar.decorator.shift(10, 0);

		// button - Delete widgets	  
		Button(layerBar, Rect(0, 0, 140, 20))
			.states_([["Delete selected widgets", TXColor.white, TXColor.sysDeleteCol]])
			.action_({
				// delete widget
				this.deleteHighlitWidgets;
				// update variables
				currWidgetInd = 0;
				// update view
				system.showView;
			});

		// check for empty arrWidgets
		if (arrWidgets.size == 0, {this.class.initArrWidgets});

/* EnvelopeView replaced by TXInterfaceLayoutView
		// create EnvelopeView
		layoutView = EnvelopeView(window, Rect(0, 30, screenWidth, screenHeight))
			.drawLines_(false)
			.selectionColor_(Color.white)
			.background_(screenColour)
			.drawRects_(true)
			.thumbSize_(5)
			.value_([Array.fill(arrWidgets.size, 0.1), Array.fill(arrWidgets.size, 0.1)])
		;
		// display boxes to represent widgets 
		arrWidgets.do({arg item, i;
			layoutView 
				.setThumbWidth(i, item.width)
				.setThumbHeight(i, item.height)
				.setString(i, "W " ++ item.widgetID.asString)
			;
			if (i == currWidgetInd, {
				layoutView.setFillColor(i, Color.white)
			}, {
				layoutView.setFillColor(i, Color.grey(0.6))
			});
			arrLayoutXvals = arrLayoutXvals.add(item.layoutX);
			arrLayoutYvals = arrLayoutYvals.add(item.layoutY);
		});
		// update value  
		 layoutView.value_([arrLayoutXvals, arrLayoutYvals].asFloat);
		// view action  
		layoutView.mouseUpAction = ({arg view; 
			var arrLayoutXvals, arrLayoutYvals;
			arrLayoutXvals = view.value.at(0);
			arrLayoutYvals = view.value.at(1);
			arrWidgets.do({arg item, i;
				item.layoutX = arrLayoutXvals.at(i);
				item.layoutY = arrLayoutYvals.at(i);
				item.fitToGrid(gridSize, screenWidth, screenHeight);
			});
			// update variables & view
			if (view.index.isPositive, {
				currWidgetInd = view.index; 
				system.showView;
			});
		});
*/

		// create TXInterfaceLayoutView
		layoutView = TXInterfaceLayoutView(window, Rect(4, 30, screenWidth, screenHeight), arrWidgets);
		layoutView.highlightActionFunc = {arg widget; currWidgetInd = arrWidgets.indexOf(widget);};
		layoutView.mouseUpActionFunc = {system.flagGuiUpd};
		layoutView.gridStep = gridSize;
		// help texts
		StaticText(window, Rect(4, 40 + screenHeight, 1000, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleYellow)
			.align_(\left)
			.string_(" Click on a single widget and grab its corners to change its size. "
				++ "Use Shift key with mouse clicks to add to group of currently selected widgets. ");
		StaticText(window, Rect(4, 65 + screenHeight, 1000, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleYellow)
			.align_(\left)
			.string_(" Use Command key with mouse drag to add a widget from the yellow popup menu above, "
				++ "or to clone currently selected widgets. ");

		// text label for clipboard items
		StaticText(window, Rect(10 + screenWidth, 60, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Clipboards");
		// number boxes  
		 NumberBox(window, Rect(10 + screenWidth, 90, 80, 20))
		.action_({arg view; clipboard1 = view.value;})
		.value_(clipboard1);
		 NumberBox(window, Rect(10 + screenWidth, 120, 80, 20))
		.action_({arg view; clipboard2 = view.value;})
		.value_(clipboard2);
		 NumberBox(window, Rect(10 + screenWidth, 150, 80, 20))
		.action_({arg view; clipboard3 = view.value;})
		.value_(clipboard3);
//		 TextField(window, Rect(10 + screenWidth, 180, 80, 20))
//		.action_({arg view; clipboard4 = view.string;})
//		.value_(clipboard4);

	}); // end of if viewMode == "Design layout" ================== ================== ==================  


	if (viewMode == "GUI properties", {
		currWidgetInd = currWidgetInd ? 0;
		// create layer bar  
		layerBar = CompositeView(window, Rect(4, 0, 1000, 30));
		layerBar.decorator = FlowLayout(layerBar.bounds);
		layerBar.decorator.shift(-4,0);
		// popup - current layer  
		layerPopupView = PopUpMenu(layerBar, Rect(0, 0, 80, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_((1 .. 20).collect({arg item, i; "Screen " ++ item.asString}))
			.action_({arg view; 
				this.storeCurrLoadNewLayer(view.value);
				// update variables
				currWidgetInd = 0;
				// update view
				system.showView;
			});
		layerPopupView.value = layerNo;

		
		// label -  name 
		StaticText(layerBar, Rect(0, 0, 50, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\right)
			.string_("Name: " );
		
		// text - current layer name 
		layerNameText = TextField(layerBar, Rect(0, 0, 300, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\left)
			.action_({arg view; 
				layerName = view.string; 
				this.storeCurrentLayer;
			});
		layerNameText.string = layerName ;

		// add spacer
		layerBar.decorator.shift(100, 0);

		// popup - replace layer  
		replaceLayerActions = 
			[ ["Copy or load another screen ... ", {}] ]
			++ (1 .. 20).collect({arg item, i; 
					["Copy Screen " ++ item.asString, {this.overwriteCurrFromLayer(item-1);}]; 
				})
			++ [	["Load screen template from disk", {this.loadScreenTemplate; }], 
				["Save screen template to disk", {this.saveScreenTemplate}]
			];
		replaceLayerPopupView = PopUpMenu(layerBar, Rect(0, 0, 300, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(replaceLayerActions.collect({arg item, i; item.at(0)}))
			.action_({arg view; 
				replaceLayerActions.at(view.value).at(1).value;
				// update variables
				currWidgetInd = 0;
				// update view
				system.showView;
			});

		// show widget properties for editing
		currWidgetProperties = arrWidgets.at(currWidgetInd).properties;

		// make box to display	
		propertiesBox = CompositeView(window,Rect(4, 30, 245, 570))
			.background_(TXColor.sysInterface);
		propertiesBox.decorator = FlowLayout(propertiesBox.bounds);

		// checkbox Lock widget positions
		lockWidgetsCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 235, 20),
			"Lock widget positions", 
			TXColour.blue, TXColor.white, 
			TXColour.white, TXColor.blue);
		lockWidgetsCheckbox.action = {arg view; 
			lockWidgets = view.value;
		};
		lockWidgetsCheckbox.value = lockWidgets ? 0;

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Screen Colour");

		// screenColourbox 
		screenColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
		screenColourBox.background_(screenColour);
		screenColourBox.beginDragAction_({ arg view, x, y;
			view.dragLabel_("Colour");
			screenColourBox.background;
	 	});
		screenColourBox.canReceiveDragHandler = {
			SCView.currentDrag.isKindOf( Color )
		};
		screenColourBox.receiveDragHandler = {
			var holdDragObject;
			holdDragObject = SCView.currentDrag;
			screenColour = holdDragObject;
			// update view
			system.showView;
		};

		// colourPickerButton			
		colourPickerButton = Button(propertiesBox, 45 @ 20)
		.states_([["Picker", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			TXColour.showPicker;
		});
		// popup - screenColour presets
		screenColourPopup = PopUpMenu(propertiesBox, Rect(0, 0, 60, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(["Presets"] ++ TXColour.colourNames)
			.action_({arg view; 
				if (view.value > 0, {
					screenColour = 
						TXColour.perform(TXColour.colourNames.at(view.value - 1).asSymbol).copy;
					// update view
					system.showView;
				});
			});

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.pink4)
			.align_(\centre)
			.string_("R");
			
		// numberbox - screenColour red
		screenColourRed = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				screenColour.red = view.value /255;
				// update view
				system.showView;
			});
		screenColourRed.value = (screenColour.red * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleGreen)
			.align_(\centre)
			.string_("G");
			
		// numberbox - screenColour green
		screenColourGreen = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				screenColour.green = view.value /255;
				// update view
				system.showView;
			});
		screenColourGreen.value = (screenColour.green * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue)
			.align_(\centre)
			.string_("B");
		// numberbox - screenColour blue
		screenColourBlue = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				screenColour.blue = view.value /255;
				// update view
				system.showView;
			});
		screenColourBlue.value = (screenColour.blue * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 35, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Alpha");
		// numberbox - screenColour alpha
		screenColourAlpha = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				screenColour.alpha = view.value /255;
				// update view
				system.showView;
			});
		screenColourAlpha.value = (screenColour.alpha * 255).round;
		
		// draw line & go to next line
		propertiesBox.decorator.shift(0, 5);
		StaticText(propertiesBox, 240 @ 1).background_(TXColor.sysGuiCol1);
		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 100, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Current widget");
		// popup - current widget id  
		currWidgetIDPopupView = PopUpMenu(propertiesBox, Rect(0, 0, 60, 20))
			.background_(TXColor.white).stringColor_(TXColor.black)
			.items_(arrWidgets.collect({arg item, i; "W " ++ item.widgetID.asString}))
			.action_({arg view; 
				currWidgetInd = view.value; 
				currWidgetNameText.string = arrWidgets.at(view.value).class.widgetName;
				// update view
				system.showView;
			});
		currWidgetIDPopupView.value = currWidgetInd;

		// button - Delete widget	  
		Button(propertiesBox, Rect(0, 0, 60, 20))
			.states_([["Delete", TXColor.white, TXColor.sysDeleteCol]])
			.action_({
				// delete widget
				this.deleteWidgetAtInd(currWidgetInd);
				// update variables
				currWidgetInd = 0;
				// update view
				system.showView;
			});

		// text - current module name 
		currWidgetNameText = StaticText(propertiesBox, Rect(0, 0, 205, 20))
			.stringColor_(TXColour.black).background_(TXColor.white)
			.align_(\left);
		currWidgetNameText.string = arrWidgets.at(currWidgetInd).class.widgetName;

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 50, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Width");
			
		// numberbox - current module width
		currWidgetWidth = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value
					.max(arrWidgets.at(currWidgetInd).widthMin)
					.min(arrWidgets.at(currWidgetInd).widthMax);
				arrWidgets.at(currWidgetInd).width = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currWidgetWidth.value = arrWidgets.at(currWidgetInd).width;

		// popup - width choices
		PopUpMenu(propertiesBox, Rect(0, 0, 15, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {currWidgetWidth.valueAction = arrSizes.at(view.value);});
			});

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 50, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Height");
			
		// numberbox - current module height
		currWidgetHeight = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value
					.max(arrWidgets.at(currWidgetInd).heightMin)
					.min(arrWidgets.at(currWidgetInd).heightMax);
				arrWidgets.at(currWidgetInd).height = view.value;
				this.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currWidgetHeight.value = arrWidgets.at(currWidgetInd).height;

		// popup - height choices
		PopUpMenu(propertiesBox, Rect(0, 0, 15, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {currWidgetHeight.valueAction = arrSizes.at(view.value);});
			});
// make for all widgets
//		// check properties
//		if ((arrWidgets.at(currWidgetInd).class == TXWSlider)
//			or: (arrWidgets.at(currWidgetInd).class == TXWSliderV)
//			or: (arrWidgets.at(currWidgetInd).class == TXWLabelBox)
//			or: (arrWidgets.at(currWidgetInd).class == TXWNotesBox)
//			or: (arrWidgets.at(currWidgetInd).class == TXWActionButton)
//			or: (arrWidgets.at(currWidgetInd).class == TXW2DSlider)
//			or: (arrWidgets.at(currWidgetInd).class == TXWNumberBox)
//			, {

			// go to next line
			propertiesBox.decorator.nextLine;
			// button swap width and height
			rotateButton = Button(propertiesBox, Rect(0, 0, 240, 20))
				.states_([["Swap width and height", 
					TXColor.white, TXColor.blue]]);
			rotateButton.action = {arg view; 
				var holdHeight, holdWidth;
				holdHeight = arrWidgets.at(currWidgetInd).width;
				holdWidth = arrWidgets.at(currWidgetInd).height;
				arrWidgets.at(currWidgetInd).height = holdHeight;
				arrWidgets.at(currWidgetInd).width = holdWidth;
				// update view
				system.showView;
			};
//		});
		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 60, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("From left");
			
		// numberbox - current module from left
		currWidgetFromLeft = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(screenWidth - arrWidgets.at(currWidgetInd).width);
				arrWidgets.at(currWidgetInd).fromLeft_(view.value, screenWidth);
				// update view
				system.showView;
			});
		currWidgetFromLeft.value = arrWidgets.at(currWidgetInd).fromLeft(screenWidth).asInteger;

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 60, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("From top");
			
		// numberbox - current module from top
		currWidgetFromTop = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(screenHeight - arrWidgets.at(currWidgetInd).height);
				arrWidgets.at(currWidgetInd).fromTop_(view.value, screenHeight);
				// update view
				system.showView;
			});
		currWidgetFromTop.value = arrWidgets.at(currWidgetInd).fromTop(screenHeight).asInteger;

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;


		// text label  
		StaticText(propertiesBox, Rect(0, 0, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Box colour");
		// boxColourBox 
		boxColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
		boxColourBox.background_(arrWidgets.at(currWidgetInd).background);
		boxColourBox.beginDragAction_({ arg view, x, y;
			view.dragLabel_("Colour");
			boxColourBox.background;
	 	});
		boxColourBox.canReceiveDragHandler = {
			SCView.currentDrag.isKindOf( Color )
		};
		boxColourBox.receiveDragHandler = {
			var holdDragObject;
			holdDragObject = SCView.currentDrag;
			boxColourBox.background_(holdDragObject);
			backgroundRed.value = (boxColourBox.background.red * 255).round;
			backgroundGreen.value = (boxColourBox.background.green * 255).round;
			backgroundBlue.value = (boxColourBox.background.blue * 255).round;
			arrWidgets.at(currWidgetInd).background = holdDragObject;
		};

		// colourPickerButton			
		colourPickerButton = Button(propertiesBox, 45 @ 20)
		.states_([["Picker", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			TXColour.showPicker;
		});
			
		// popup - background presets
		backgroundPopup = PopUpMenu(propertiesBox, Rect(0, 0, 60, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(["Presets"] ++ TXColour.colourNames)
			.action_({arg view; 
				if (view.value > 0, {
					arrWidgets.at(currWidgetInd).background = 
						TXColour.perform(TXColour.colourNames.at(view.value - 1).asSymbol).copy;
					// update view
					system.showView;
				});
			});

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.pink4)
			.align_(\centre)
			.string_("R");
			
		// numberbox - background red
		backgroundRed = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				arrWidgets.at(currWidgetInd).background.red = view.value /255;
				// update view
				system.showView;
			});
		backgroundRed.value = (arrWidgets.at(currWidgetInd).background.red * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleGreen)
			.align_(\centre)
			.string_("G");
			
		// numberbox - background green
		backgroundGreen = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				arrWidgets.at(currWidgetInd).background.green = view.value /255;
				// update view
				system.showView;
			});
		backgroundGreen.value = (arrWidgets.at(currWidgetInd).background.green * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue)
			.align_(\centre)
			.string_("B");
		// numberbox - background blue
		backgroundBlue = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				arrWidgets.at(currWidgetInd).background.blue = view.value /255;
				// update view
				system.showView;
			});
		backgroundBlue.value = (arrWidgets.at(currWidgetInd).background.blue * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 35, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Alpha");
		// numberbox - background alpha
		backgroundAlpha = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				arrWidgets.at(currWidgetInd).background.alpha = view.value /255;
				// update view
				system.showView;
			});
		backgroundAlpha.value = (arrWidgets.at(currWidgetInd).background.alpha * 255).round;
		
		// ==========================================================================================
		
		// check properties
		if ((arrWidgets.at(currWidgetInd).class == TXWSlider)
			or: (arrWidgets.at(currWidgetInd).class == TXWSliderV)
			or: (arrWidgets.at(currWidgetInd).class == TXWSliderNo)
			or: (arrWidgets.at(currWidgetInd).class == TXWSliderNoV)
			or: (arrWidgets.at(currWidgetInd).class == TXW2DSlider)
		,{

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
	
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 80, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Knob/Text colour");
			// knobColourBox 
			knobColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
			knobColourBox.background_(arrWidgets.at(currWidgetInd).knobColour);
			knobColourBox.beginDragAction_({ arg view, x, y;
				view.dragLabel_("Colour");
				knobColourBox.background;
		 	});
			knobColourBox.canReceiveDragHandler = {
				SCView.currentDrag.isKindOf( Color )
			};
			knobColourBox.receiveDragHandler = {
				var holdDragObject;
				holdDragObject = SCView.currentDrag;
				knobColourBox.background_(holdDragObject);
				knobColourRed.value = (knobColourBox.background.red * 255).round;
				knobColourGreen.value = (knobColourBox.background.green * 255).round;
				knobColourBlue.value = (knobColourBox.background.blue * 255).round;
				arrWidgets.at(currWidgetInd).knobColour = holdDragObject;
			};
	
			// colourPickerButton			
			colourPickerButton = Button(propertiesBox, 45 @ 20)
			.states_([["Picker", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				TXColour.showPicker;
			});
				
			// popup - background presets
			backgroundPopup = PopUpMenu(propertiesBox, Rect(0, 0, 60, 20))
				.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
				.items_(["Presets"] ++ TXColour.colourNames)
				.action_({arg view; 
					if (view.value > 0, {
						arrWidgets.at(currWidgetInd).knobColour = TXColour.perform(
							TXColour.colourNames.at(view.value - 1).asSymbol).copy;
						// update view
						system.showView;
					});
				});
	
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.pink4)
				.align_(\centre)
				.string_("R");
				
			// numberbox - knobColour red
			knobColourRed = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					arrWidgets.at(currWidgetInd).knobColour.red = view.value /255;
					// update view
					system.showView;
				});
			knobColourRed.value = (arrWidgets.at(currWidgetInd).knobColour.red * 255).round;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleGreen)
				.align_(\centre)
				.string_("G");
				
			// numberbox - knobColour green
			knobColourGreen = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					arrWidgets.at(currWidgetInd).knobColour.green = view.value /255;
					// update view
					system.showView;
				});
			knobColourGreen.value = (arrWidgets.at(currWidgetInd).knobColour.green * 255).round;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue)
				.align_(\centre)
				.string_("B");
			// numberbox - knobColour blue
			knobColourBlue = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					arrWidgets.at(currWidgetInd).knobColour.blue = view.value /255;
					// update view
					system.showView;
				});
			knobColourBlue.value = (arrWidgets.at(currWidgetInd).knobColour.blue * 255).round;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 35, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Alpha");
			// numberbox - knobColour alpha
			knobColourAlpha = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					arrWidgets.at(currWidgetInd).knobColour.alpha = view.value /255;
					// update view
					system.showView;
				});
			knobColourAlpha.value = (arrWidgets.at(currWidgetInd).knobColour.alpha * 255).round;
			
			// go to next line
			propertiesBox.decorator.nextLine;
			// colour swap button
			colourSwapButton = Button(propertiesBox, Rect(0, 0, 240, 20))
				.states_([["Swap knob/text colour and box colour", 
					TXColor.white, TXColor.blue]]);
			colourSwapButton.action = {arg view; 
				var holdKnobColor2, holdBackground2;
				holdKnobColor2 = arrWidgets.at(currWidgetInd).background;
				holdBackground2 = arrWidgets.at(currWidgetInd).knobColour;
				arrWidgets.at(currWidgetInd).knobColour = holdKnobColor2;
				arrWidgets.at(currWidgetInd).background = holdBackground2;
				// update view
				system.showView;
			};
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
	
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Knob size");
				
			// numberbox - knob width
			knobWidthBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(0).min(600);
					arrWidgets.at(currWidgetInd).thumbSize = view.value;
					// update view
					system.showView;
				});
			knobWidthBox.value = arrWidgets.at(currWidgetInd).thumbSize ;

			// check properties
			if ((arrWidgets.at(currWidgetInd).class == TXWSliderNo)
				or: (arrWidgets.at(currWidgetInd).class == TXWSliderNoV)
				, {
	
				// spacer 
				propertiesBox.decorator.shift(10, 0);
	
				// text label  
				StaticText(propertiesBox, Rect(0, 0, 70, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
					.align_(\left)
					.string_("Number size");
					
				// numberbox - number size
				numberSizeBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.action_({arg view; 
						view.value = view.value.max(0).min(600);
						arrWidgets.at(currWidgetInd).numberSize = view.value;
						// update view
						system.showView;
					});
				numberSizeBox.value = arrWidgets.at(currWidgetInd).numberSize ;
	
			});
		});

		// ==========================================================================================
		
		// check properties
		if (arrWidgets.at(currWidgetInd).properties.includes(\string) and: 
			(arrWidgets.at(currWidgetInd).class != TXWNotesBox), {
		
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 150, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Text to be displayed");
			// clear text button
			clearTextButton = Button(propertiesBox, Rect(0, 0, 60, 20))
				.states_([["Clear text", 
					TXColor.white, TXColor.sysDeleteCol]])
				.action = {arg view; 
					arrWidgets.at(currWidgetInd).string = "";
					// update view
					system.showView;
				};
			// text box
			labelText = TextField(propertiesBox, Rect(0, 0, 240, 20))
				.action_({arg view; 
					arrWidgets.at(currWidgetInd).string = view.string;
				})
				.align_(\left);
			labelText.string = arrWidgets.at(currWidgetInd).string;
		});
		
		// ==========================================================================================
		
		// check properties
		if (arrWidgets.at(currWidgetInd).properties.includes(\stringColorAsArgs), {
		
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 80, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Text colour");
			// textColourBox 
			textColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
			textColourBox.background_(arrWidgets.at(currWidgetInd).stringColor);
			textColourBox.beginDragAction_({ arg view, x, y;
				view.dragLabel_("Colour");
				textColourBox.background;
		 	});
			textColourBox.canReceiveDragHandler = {
				SCView.currentDrag.isKindOf( Color )
			};
			textColourBox.receiveDragHandler = {
				var holdDragObject;
				holdDragObject = SCView.currentDrag;
				textColourBox.background_(holdDragObject);
				arrWidgets.at(currWidgetInd).stringColor = holdDragObject;
			};
	
			// colourPickerButton			
			colourPickerButton = Button(propertiesBox, 45 @ 20)
			.states_([["Picker", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				TXColour.showPicker;
			});
				
			// popup - background presets
			backgroundPopup = PopUpMenu(propertiesBox, Rect(0, 0, 60, 20))
				.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
				.items_(["Presets"] ++ TXColour.colourNames)
				.action_({arg view; 
					if (view.value > 0, {
						arrWidgets.at(currWidgetInd).stringColor = TXColour.perform(
							TXColour.colourNames.at(view.value - 1).asSymbol).copy;
						// update view
						system.showView;
					});
				});

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 60, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Font");
				
			// popup - font
			fontPopup = PopUpMenu(propertiesBox, Rect(105, 30, 140, 20))
				.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
				.items_(arrFonts)
				.action_({arg view; 
					if (view.value > 0, {
						arrWidgets.at(currWidgetInd).font = arrFonts.at(view.value);
					});
				});
			fontPopup.value = arrFonts.indexOf(arrWidgets.at(currWidgetInd).font) ? 0;

			// numberbox - font size
			fontSizeBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(2).min(200);
					arrWidgets.at(currWidgetInd).fontSize = view.value;
					// update view
					system.showView;
				});
			fontSizeBox.value = arrWidgets.at(currWidgetInd).fontSize;

			// go to next line
			propertiesBox.decorator.nextLine;
			// colour swap button
			colourSwapButton = Button(propertiesBox, Rect(0, 0, 240, 20))
				.states_([["Swap text colour and box colour", 
					TXColor.white, TXColor.blue]]);
			colourSwapButton.action = {arg view; 
				var holdStringColor2, holdBackground2;
				holdStringColor2 = arrWidgets.at(currWidgetInd).background;
				holdBackground2 = arrWidgets.at(currWidgetInd).stringColor;
				arrWidgets.at(currWidgetInd).stringColor = holdStringColor2;
				arrWidgets.at(currWidgetInd).background = holdBackground2;
				// update view
				system.showView;
			};
		});

		// ==========================================================================================
		
		if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {

			// midi note trigger

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Midi Trigger");
				
			// checkbox midi Listen
			midiListenCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 40, 20),
				" ", TXColour.black, TXColor.white, 
				TXColour.black, TXColor.white, 4);
			midiListenCheckbox.action = {arg view; 
				arrWidgets.at(currWidgetInd).midiListen = view.value;
			};
			midiListenCheckbox.value = arrWidgets.at(currWidgetInd).midiListen ? 0;
			
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 40, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Note");
				
			// numberbox - midi Note
			midiNoteBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					view.value = view.value.max(0).min(127);
					arrWidgets.at(currWidgetInd).midiNote = view.value;
					midiNotePopup.value = view.value;
					// update view
					system.showView;
				});
			midiNoteBox.value = arrWidgets.at(currWidgetInd).midiNote ? 0;

			// popup - midi note text
			midiNotePopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((0 .. 127).collect({arg item, i; TXGetMidiNoteString.new(item)});)
				.background_(TXColor.white)
				.action_({arg view; 
					midiNoteBox.valueAction = view.value;
				});
			midiNotePopup.value = arrWidgets.at(currWidgetInd).midiNote ? 0;

			//  go to next line
			propertiesBox.decorator.nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Channels ");
				
			// numberbox - midi channel min
			midiChannelMinPopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((1..16).collect({arg item, i; item.asString}))
				.background_(TXColor.white)
				.action_({arg view; 
					arrWidgets.at(currWidgetInd).midiMinChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMinPopup.value = (arrWidgets.at(currWidgetInd).midiMinChannel ? 1) - 1;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
				.align_(\centre)
				.string_("-");
				
			// numberbox - midi channel max
			midiChannelMaxPopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((1..16).collect({arg item, i; item.asString}))
				.background_(TXColor.white)
				.action_({arg view; 
					arrWidgets.at(currWidgetInd).midiMaxChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMaxPopup.value = (arrWidgets.at(currWidgetInd).midiMaxChannel ? 1) - 1;
			
			// key down trigger

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Key Trigger");
				
			// checkbox key Listen
			keyListenCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 40, 20),
				" ", TXColour.black, TXColor.white, 
				TXColour.black, TXColor.white, 4);
			keyListenCheckbox.action = {arg view; 
				arrWidgets.at(currWidgetInd).keyListen = view.value;
			};
			keyListenCheckbox.value = arrWidgets.at(currWidgetInd).keyListen ? 0;
			
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 40, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Key");
				
			// text field - key
			keyTextField = TextField(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					if (view.string.size == 0, {view.string = "";});
					view.string = view.string.at(0);
					arrWidgets.at(currWidgetInd).keyChar = view.string;
					// update view
					system.showView;
				});
			keyTextField.string = arrWidgets.at(currWidgetInd).keyChar ? "";

		});

		// ==========================================================================================
		
		// check properties
		if ((arrWidgets.at(currWidgetInd).class == TXWSlider)
			or: (arrWidgets.at(currWidgetInd).class == TXWSliderV)
			or: (arrWidgets.at(currWidgetInd).class == TXWSliderNo)
			or: (arrWidgets.at(currWidgetInd).class == TXWSliderNoV)
			or: (arrWidgets.at(currWidgetInd).class == TXW2DSlider)
		,{
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 31, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Midi");
				
			// checkbox midi Listen
			midiListenCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 35, 20),
				" ", TXColour.black, TXColor.white, 
				TXColour.black, TXColor.white, 4);
			midiListenCheckbox.action = {arg view; 
				arrWidgets.at(currWidgetInd).midiListen = view.value;
			};
			midiListenCheckbox.value = arrWidgets.at(currWidgetInd).midiListen ? 0;
			
			if (arrWidgets.at(currWidgetInd).class == TXW2DSlider,{
				// text label  
				StaticText(propertiesBox, Rect(0, 0, 80, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
					.align_(\left)
					.string_("Controller nos");
					
				// numberbox - midi controller no
				midiCCNoBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.action_({arg view; 
						view.value = view.value.max(0).min(127);
						arrWidgets.at(currWidgetInd).midiCCNo = view.value;
						// update view
						system.showView;
					});
				midiCCNoBox.value = arrWidgets.at(currWidgetInd).midiCCNo ? 0;
				// numberbox - midi controller no 2
				midiCCNoBox2 = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.action_({arg view; 
						view.value = view.value.max(0).min(127);
						arrWidgets.at(currWidgetInd).midiCCNo2 = view.value;
						// update view
						system.showView;
					});
				midiCCNoBox2.value = arrWidgets.at(currWidgetInd).midiCCNo2 ? 0;
			},{
				// text label  
				StaticText(propertiesBox, Rect(0, 0, 80, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
					.align_(\left)
					.string_("Controller no");
					
				// numberbox - midi controller no
				midiCCNoBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.action_({arg view; 
						view.value = view.value.max(0).min(127);
						arrWidgets.at(currWidgetInd).midiCCNo = view.value;
						// update view
						system.showView;
					});
				midiCCNoBox.value = arrWidgets.at(currWidgetInd).midiCCNo ? 0;
			});

			//  go to next line
			propertiesBox.decorator.nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\left)
				.string_("Channels ");
				
			// numberbox - midi channel min
			midiChannelMinPopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((1..16).collect({arg item, i; item.asString}))
				.background_(TXColor.white)
				.action_({arg view; 
					arrWidgets.at(currWidgetInd).midiMinChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMinPopup.value = (arrWidgets.at(currWidgetInd).midiMinChannel ? 1) - 1;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
				.align_(\centre)
				.string_("-");
				
			// numberbox - midi channel max
			midiChannelMaxPopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((1..16).collect({arg item, i; item.asString}))
				.background_(TXColor.white)
				.action_({arg view; 
					arrWidgets.at(currWidgetInd).midiMaxChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMaxPopup.value = (arrWidgets.at(currWidgetInd).midiMaxChannel ? 16) - 1;
			
		});

		// ==========================================================================================
		
		if (arrWidgets.at(currWidgetInd).class == TXWCheckBox, {
			// go to next line
			propertiesBox.decorator.nextLine;
			// checkbox reverse colours
			colourRevCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 240, 20),
				"Reverse colours when switched on", 
				TXColour.blue, TXColor.white, 
				TXColour.white, TXColor.blue);
			colourRevCheckbox.action = {arg view; 
				arrWidgets.at(currWidgetInd).colourReverse = view.value;
			};
			colourRevCheckbox.value = arrWidgets.at(currWidgetInd).colourReverse ? 0;
			
		});

		// ==========================================================================================

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// copy properties button
		copyPropertiesButton = Button(propertiesBox, Rect(0, 0, 100, 20))
			.states_([["Copy properties", 
				TXColor.white, TXColor.bluegreen]]);
		copyPropertiesButton.action = {arg view; 
			holdWidgetClass = arrWidgets.at(currWidgetInd).class.asSymbol;
			holdWidgetPropertyList = arrWidgets.at(currWidgetInd).getPropertyList;
		};
		// paste properties button
		pastePropertiesButton = Button(propertiesBox, Rect(0, 0, 100, 20))
			.states_([["Paste properties", 
				TXColor.white, TXColor.bluegreen]]);
		pastePropertiesButton.action = {arg view; 
			var selectedPropertyList;
			// only paste relevent properties
			if (holdWidgetClass.notNil, {
				// if classes match, copy all properties, else only display properties
				if (holdWidgetClass == arrWidgets.at(currWidgetInd).class.asSymbol, {
					selectedPropertyList = holdWidgetPropertyList.select({arg item, i;
						this.copyAllProperties.includes(item.at(0));
					});
				},{
					selectedPropertyList = holdWidgetPropertyList.select({arg item, i;
						this.copyDisplayProperties.includes(item.at(0));
					});
				});
				arrWidgets.at(currWidgetInd).setPropertyList(selectedPropertyList.deepCopy);
			});
			
			// update view
			system.showView;
		};
		// ==========================================================================================
		
		// check properties
		if (arrWidgets.at(currWidgetInd).class == TXWNotesBox, {
		
			// make box to display notes
			notesBox = CompositeView(window,Rect(254, (screenHeight * 0.75) + 35, 750, 150))
				.background_(TXColor.sysInterface);
			notesBox.decorator = FlowLayout(notesBox.bounds);
		

			// text label  
			StaticText(notesBox, Rect(0, 0, 400, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\centre)
				.string_("Enter notes text below, then press button to store notes:");

			// update button
			updateButton = Button(notesBox, Rect(0, 0, 100, 20))
				.states_([ ["Store Notes", TXColor.white, TXColor.sysEditCol] ]);
			updateButton.action = {arg view; 
				arrWidgets.at(currWidgetInd).string = notesView.string;
				// update view
				system.showView;
			};

			// go to next line
			notesBox.decorator.nextLine;

			// notes
			notesView = TextView(notesBox,Rect(0,0, 700,110))
				.hasVerticalScroller_(true)
				.background_(Color.white)
//				.autohidesScrollers_(true)
				.enterInterpretsSelection_(false)
				.string_(arrWidgets.at(currWidgetInd).string)
				;
		});
		// ==========================================================================================
		
		if (arrWidgets.at(currWidgetInd).properties.includes(\arrActions), {
		
			// make box to display actions
			actionsBox = CompositeView(window,Rect(254, (screenHeight * 0.75) + 35, 750, 150));
			actionsBox.decorator = FlowLayout(actionsBox.bounds);
		
			// text label  
			if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
				// select actions button
				selectActionsButton = Button(actionsBox, Rect(0, 0, 76, 20))
					.states_([
						["1-5", TXColor.white, TXColor.blue],
						["6-10", TXColor.white, TXColor.blue]
					]);
				selectActionsButton.action = {arg view; 
					arrWidgets.at(currWidgetInd).showActions6to10 = view.value;
					// update view
					system.showView;
				};
				selectActionsButton.value = arrWidgets.at(currWidgetInd).showActions6to10 ? 0;
				StaticText(actionsBox, Rect(0, 0, 70, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
					.align_(\centre)
					.string_("Module");
			},{	
				if (arrWidgets.at(currWidgetInd).class == TXW2DSlider, {
					// x-y axis button
					selectActionsButton = Button(actionsBox, Rect(0, 0, 76, 20))
						.states_([
							["x-Axis", TXColor.white, TXColor.blue],
							["y-Axis", TXColor.white, TXColor.blue]
						]);
					selectActionsButton.action = {arg view; 
						arrWidgets.at(currWidgetInd).showYAxis = view.value;
						// update view
						system.showView;
					};
					selectActionsButton.value = arrWidgets.at(currWidgetInd).showYAxis ? 0;
					StaticText(actionsBox, Rect(0, 0, 70, 20))
						.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
						.align_(\centre)
						.string_("Module");
				},{	
					StaticText(actionsBox, Rect(0, 0, 150, 20))
						.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
						.align_(\centre)
						.string_("Module");
				});
			});

			// text label  
			holdView = StaticText(actionsBox, Rect(0, 0, 300, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\centre);
			if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
				holdView.string_("Action");
			},{
				holdView.string_("Parameter to update");
			});

			// text label  
			holdView = StaticText(actionsBox, Rect(0, 0, 250, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
				.align_(\centre);
			if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
				holdView.string_("Value settings");
			},{
				holdView.string_("Extra parameter settings");
			});

			// up to 5 actions can be defined  
			if (arrWidgets.at(currWidgetInd).class == TXWTextDisplayBox, {
				actionCount = 1;
			},{
				actionCount = 5;
			});
			
			actionCount.do({ arg item, i;
				var arrModules, modulePopup, arrActionItems, arrLegacyActionItems, actionPopup;
				var holdModuleID, holdModule;
				var holdControlSpec1, holdControlSpec2, holdControlSpec3, holdControlSpec4, holdArrActionSpecs;
				var val1NumberBox, val1Slider, val2NumberBox, val3NumberBox, val4NumberBox, valPopup; 
				var valCheckbox, valTextbox, holdArrActions;

				holdArrActions = arrWidgets.at(currWidgetInd).arrActions;
				if (arrWidgets.at(currWidgetInd).class == TXW2DSlider, {
					if (arrWidgets.at(currWidgetInd).showYAxis == 1, {
						holdArrActions = arrWidgets.at(currWidgetInd).arrActions2;
					});
				});
				if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
					if (arrWidgets.at(currWidgetInd).showActions6to10 == 1, {
						holdArrActions = arrWidgets.at(currWidgetInd).arrActions2;
					});
				});

				// go to next line
				actionsBox.decorator.nextLine;

				// popup - module
				if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
					arrModules = system.arrWidgetActionModules;
				},{
					arrModules = system.arrWidgetValueActionModules;
//						.select({arg item, i; item.arrActionSpecs
//							.select({arg action, i; action.guiObjectType 
//								== arrWidgets.at(currWidgetInd).guiObjectType}).size > 0;  });
				});
				modulePopup = PopUpMenu(actionsBox, Rect(0, 0, 150, 20))
					.background_(TXColor.white).stringColor_(TXColor.black)
					.items_(arrModules.collect({arg item, i; item.instName;}))
					.action_({arg view; 
						var holdAction;
						holdArrActions.at(i)
							.put(0, arrModules.at(view.value).moduleID);
						holdArrActions.at(i).put(1, 0);
						if (holdArrActions.at(i).size<8, {
							holdAction = holdArrActions.at(i).deepCopy;
							holdAction = holdAction.addAll([nil, nil, nil, nil, nil, nil]);
							holdArrActions.put(i, holdAction.deepCopy);
						},{
							holdArrActions.at(i).put(7, nil);
						});
						// update view
						system.showView;
					});
				holdModuleID = holdArrActions.at(i).at(0);
				holdModule = system.getModuleFromID(holdModuleID);
				if (holdModule == 0, {holdModule = system});
				modulePopup.value = arrModules.indexOf(holdModule) ? 0;
				
				// popup - action
				if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
					holdArrActionSpecs = holdModule.arrActionSpecs;
					arrActionItems = holdArrActionSpecs
						.collect({arg item, i; item.actionName;});
					arrLegacyActionItems = holdArrActionSpecs .select({arg item, i; item.legacyType == 1})
						.collect({arg item, i; item.actionName;});

				},{
					holdArrActionSpecs = holdModule.arrActionSpecs
						.select({arg action, i;
						 (action.actionType == \valueAction) 
							and: (action.guiObjectType == arrWidgets.at(currWidgetInd).guiObjectType);});
					arrActionItems = holdArrActionSpecs
						.collect({arg item, i; item.actionName;});
					arrLegacyActionItems = holdModule.arrActionSpecs .select({arg item, i; item.legacyType == 1})
						.select({arg action, i;
						 (action.actionType == \valueAction) 
							and: (action.guiObjectType == arrWidgets.at(currWidgetInd).guiObjectType);})
						.collect({arg item, i; item.actionName;});
				});
				actionPopup = PopUpMenu(actionsBox, Rect(0, 0, 300, 20))
					.background_(TXColor.white).stringColor_(TXColor.black)
					.items_(arrActionItems)
					.action_({arg view; 
						var holdAction;
						// popup value and text are stored
						holdArrActions.at(i).put(1, view.value);
						if (holdArrActions.at(i).size<8, {
							holdAction = holdArrActions.at(i).deepCopy;
							holdAction = holdAction.addAll([nil, nil, nil, nil, nil, nil]);
							holdArrActions.put(i, holdAction.deepCopy);
						});
						holdArrActions.at(i).put(7, arrActionItems.at(view.value));
						// default argument values are stored
						// arg 1
						if (holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 0, {
							holdArrActions.at(i).put(2, 
								holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(0).value.default);
						});
						// arg 2
						if (holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 1, {
							holdArrActions.at(i).put(3, 
								holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(1).value.default);
						});
						// arg 3
						if (holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 2, {
							holdArrActions.at(i).put(4, 
								holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(2).value.default);
						});
						// update view
						system.showView;
					});
				// if text found, match action string with text, else use numerical value
				if (holdArrActions.at(i).at(7).notNil, {
					actionPopup.value = arrActionItems.indexOfEqual(holdArrActions.at(i).at(7)) ? 0;
				},{
					holdActionText = arrLegacyActionItems.at(holdArrActions.at(i).at(1) ? 0);
					actionPopup.value = arrActionItems.indexOfEqual(holdActionText) ? 0;
				});
	
				// check for no actions
				if (holdArrActionSpecs.size > 0, {		
					// action widget, show value settings
					if (arrWidgets.at(currWidgetInd).class == TXWActionButton, {
						// if only 1 controlspec is given, then create slider 
						if (holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size == 1, {
						// slider - value 1
							holdControlSpec1 = 
								holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(0);
							val1Slider = Slider(actionsBox, Rect(0, 0, 175, 20))
							.action_({arg view; 
								holdArrActions.at(i)
									.put(2, holdControlSpec1.value.map(view.value));
								if (val1NumberBox.class == NumberBox.redirectClass, 
									{val1NumberBox.value = holdControlSpec1.value.map(view.value);})
							});
							if (holdControlSpec1.value.step != 0, {
								val1Slider.step = (holdControlSpec1.value.step 
									/ (holdControlSpec1.value.maxval - holdControlSpec1.value.minval));
							});
							val1Slider.value = holdControlSpec1.value.unmap(
								holdArrActions.at(i).at(2) ? 0);
						});
						// if object type is number
						if (holdModule.arrActionSpecs.at(actionPopup.value).guiObjectType == \number, {
							// if at least 1 controlspec is given, then create numberbox
							if (holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 0, {
								holdControlSpec1 =
									 holdModule.arrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(0);
								val1NumberBox = NumberBox(actionsBox, Rect(0, 0, 60, 20))
								.action_({arg view; 
									view.value = holdControlSpec1.value.constrain(view.value);
									holdArrActions.at(i).put(2, view.value);
									if (val1Slider.class == Slider.redirectClass, 
										{val1Slider.value = holdControlSpec1.value.unmap(view.value);})
								});
								val1NumberBox.value = holdControlSpec1.value.constrain(
									holdArrActions.at(i).at(2) ? holdControlSpec1.value.default);
							});
						});
						// popup 
						if (holdModule.arrActionSpecs.at(actionPopup.value).guiObjectType == \popup, {
							valPopup = PopUpMenu(actionsBox, Rect(0, 0, 250, 20))
								.stringColor_(TXColour.black).background_(TXColor.white);
							valPopup.items = 
								holdModule.arrActionSpecs.at(actionPopup.value).getItemsFunction.value;
							valPopup.action = {arg view; 
								holdArrActions.at(i).put(2, view.value);
							};
							valPopup.value = holdArrActions.at(i).at(2) ? 0;
						});
	
						// checkbox 
						if (holdModule.arrActionSpecs.at(actionPopup.value).guiObjectType == \checkbox, {
							valCheckbox = TXCheckBox(actionsBox, Rect(0, 0, 60, 20),
								" ", TXColour.black, TXColor.white, 
								TXColour.black, TXColor.white, 7);
							valCheckbox.action = {arg view; 
								holdArrActions.at(i).put(2, view.value);
							};
							valCheckbox.value = holdArrActions.at(i).at(2) ? 0;
						});
	
						// textbox 
						if (holdModule.arrActionSpecs.at(actionPopup.value).guiObjectType == \textedit, {
							valTextbox = TextField(actionsBox, Rect(0, 0, 250, 20),
								" ", TXColour.black, TXColor.white, 
								TXColour.black, TXColor.white, 4);
							valTextbox.action = {arg view; 
								holdArrActions.at(i).put(2, view.string);
							};
							valTextbox.string = holdArrActions.at(i).at(2) ? 0;
						});
	
					}); // end of if ...TXWActionButton
					// if more than 1 control spec given, then create extra numberbox
					if (holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 1, {
					// numberbox - value 2
						holdControlSpec2 = 
							holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(1);
						val2NumberBox = NumberBox(actionsBox, Rect(0, 0, 60, 20))
						.action_({arg view; 
							view.value = holdControlSpec2.value.constrain(view.value);
							holdArrActions.at(i).put(3, view.value);
						});
						if (holdArrActions.at(i).at(3).notNil, {
							val2NumberBox.value = holdControlSpec2.value.constrain(
								holdArrActions.at(i).at(3));
							holdArrActions.at(i).put(3, val2NumberBox.value);
						},{
							val2NumberBox.value = holdControlSpec2.default;
							holdArrActions.at(i).put(3, holdControlSpec2.default);
	
	
						});
					});
					// numberbox - value 3
					// if more than 2 controlspecs given, then create extra numberbox
					if (holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 2, {
						holdControlSpec3 = 
							holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(2);
						val3NumberBox = NumberBox(actionsBox, Rect(0, 0, 60, 20))
						.action_({arg view; 
							view.value = holdControlSpec3.value.constrain(view.value);
							holdArrActions.at(i).put(4, view.value);
						});
						if (holdArrActions.at(i).at(4).notNil, {
							val3NumberBox.value = holdControlSpec3.value.constrain(
								holdArrActions.at(i).at(4));
							holdArrActions.at(i).put(4, val3NumberBox.value);
						},{
							val3NumberBox.value = holdControlSpec3.default;
							holdArrActions.at(i).put(4, holdControlSpec3.default);
						});
					});
					// numberbox - value 4
					// if more than 3 controlspecs given, then create extra numberbox
					if (holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 3, {
						holdControlSpec4 = 
							holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(3);
						val4NumberBox = NumberBox(actionsBox, Rect(0, 0, 60, 20))
						.action_({arg view; 
							view.value = holdControlSpec4.value.constrain(view.value);
							holdArrActions.at(i).put(5, view.value);
						});
						if (holdArrActions.at(i).at(5).notNil, {
							val4NumberBox.value = holdControlSpec4.value.constrain(
								holdArrActions.at(i).at(5));
							holdArrActions.at(i).put(5, val4NumberBox.value);
						},{
							val4NumberBox.value = holdControlSpec4.default;
							holdArrActions.at(i).put(5, holdControlSpec4.default);
						});
					});
	
			}); // end of if holdArrActionSpecs.size > 0

			}); // end of actionCount.do

		});  //end of if ...\arrActions

		// ==========================================================================================
		
		// create EnvelopeView - but three quarters size
		layoutView = EnvelopeView(window, Rect(254, 30, screenWidth * 0.75, screenHeight * 0.75))
		.drawLines_(false)
		.selectionColor_(Color.white)
		.background_(screenColour)
		.drawRects_(true)
		.thumbSize_(5)
		.value_([Array.fill(arrWidgets.size, 0.1), Array.fill(arrWidgets.size, 0.1)])
		;
		// check for empty arrWidgets
		if (arrWidgets.size == 0, {this.class.initArrWidgets});
		// display boxes to represent widgets 
		arrWidgets.do({arg item, i;
			layoutView 
				.setThumbWidth(i, item.width * 0.75)
				.setThumbHeight(i, item.height * 0.75)
				.setString(i, "W " ++ item.widgetID.asString)
			;
			if (i == currWidgetInd, {
				layoutView.setFillColor(i, Color.white)
			}, {
				layoutView.setFillColor(i, Color.grey(0.6))
			});
			arrLayoutXvals = arrLayoutXvals.add(item.layoutX);
			arrLayoutYvals = arrLayoutYvals.add(item.layoutY);
		});
		// update value  
		 layoutView.value_([arrLayoutXvals, arrLayoutYvals].asFloat);

		// if locked, don't allow edits on this screen  
		// view action  
		layoutView.mouseUpAction = ({arg view; 
			var arrLayoutXvals, arrLayoutYvals;
			// if locked, don't update values
			if ((lockWidgets == 0), {
				arrLayoutXvals = view.value.at(0);
				arrLayoutYvals = view.value.at(1);
				arrWidgets.do({arg item, i;
					item.layoutX = arrLayoutXvals.at(i);
					item.layoutY = arrLayoutYvals.at(i);
					item.fitToGrid(gridSize, screenWidth, screenHeight);
				});
			});
			// update variables & view  
			if (view.index.isPositive, {
				currWidgetInd = view.index; 
				system.showView;
			});
		});

		// text label for clipboard items
		offsetHeight = (screenHeight * 0.75) + 40;
		StaticText(window, Rect(1014, offsetHeight, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.sysLabelBackground)
			.align_(\left)
			.string_("Clipboards");
		// number boxes  
		 NumberBox(window, Rect(1014, offsetHeight + 30, 80, 20))
		.action_({arg view; clipboard1 = view.value;})
		.value_(clipboard1);
		 NumberBox(window, Rect(1014, offsetHeight + 60, 80, 20))
		.action_({arg view; clipboard2 = view.value;})
		.value_(clipboard2);
		 NumberBox(window, Rect(1014, offsetHeight + 90, 80, 20))
		.action_({arg view; clipboard3 = view.value;})
		.value_(clipboard3);
//		 TextField(window, Rect(1010, offsetHeight + 120, 80, 20))
//		.action_({arg view; clipboard4 = view.string;})
//		.value_(clipboard4);

	}); // end of if viewMode == "GUI properties"

} // end of class method makeGui


}	// end of class


