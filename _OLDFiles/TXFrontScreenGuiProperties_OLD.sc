// Copyright (C) 2011  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXFrontScreenGuiProperties {	// Front Screen gui  

*makeGui{ arg window, system, classData, arrSizes;

	var fromTop, fromLeft, layoutView, arrLayoutXvals, arrLayoutYvals;
	var layerBar, layerPopupView, layerNameText, replaceLayerActions, replaceLayerPopupView;
	var currWidgetProperties, propertiesBox, actionsBox, actionCount; 
	var lockWidgetsCheckbox;
	var screenColourPopup, screenColourRed, screenColourGreen, screenColourBlue;
	var screenColourAlpha, screenColourBox;
	var notesBox, notesView, updateButton, colourPickerButton, boxColourBox;
	var currWidgetIDPopupView, currWidgetNameText;
	var currWidgetHeight, currWidgetWidth, currWidgetFromLeft, currWidgetFromTop;
	var holdActionText, holdActionType;
	var backgroundPopup, backgroundRed, backgroundGreen, backgroundBlue, backgroundAlpha;
	var colourRevCheckbox, colourSwapButton, clearTextButton, knobColourBox, textColourBox;
	var knobColourRed, knobColourGreen, knobColourBlue, knobColourAlpha, knobWidthBox, numberSizeBox;
	var rotateButton, labelText, fontPopup, fontSizeBox, holdView;
	var midiListenCheckbox, midiNoteBox, midiNotePopup, midiCCNoBox, midiCCNoBox2, midiLearnCheckbox;
	var midiChannelMinPopup, midiChannelMaxPopup, keyListenCheckbox, keyTextField;
	var copyPropertiesButton, pastePropertiesButton, selectActionsButton;
	var displayModePopupView, displayModeNumberView, displayModeItems;
	var imageNameText, addImageButton, delImageButton;
	var offsetHeight;

	holdActionType = "valueAction";
	fromTop = 65;
	fromLeft = 7;
		// set variables  
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWActionButton
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet 
				and: {TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex > 4})
		, {
			holdActionType = "commandAction";
		});
		TXFrontScreen.currWidgetInd = TXFrontScreen.currWidgetInd ? 0;
		// create layer bar  
		layerBar = CompositeView(window, Rect(4, 0, 1400, 30));
		layerBar.decorator = FlowLayout(layerBar.bounds);
		layerBar.decorator.shift(-4,0);
		// popup - current layer  
		layerPopupView = PopUpMenu(layerBar, Rect(0, 0, 80, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_((1 .. 20).collect({arg item, i; "Screen " ++ item.asString}))
			.action_({arg view; 
				TXFrontScreen.storeCurrLoadNewLayer(view.value);
				// update variables
				TXFrontScreen.currWidgetInd = 0;
				system.addHistoryEvent;
				// update view
				system.showView;
			});
		layerPopupView.value = classData.layerNo;
		
		// label -  name 
		StaticText(layerBar, Rect(0, 0, 50, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\right)
			.string_("Name: " );
		
		// text - current layer name 
		layerNameText = TextField(layerBar, Rect(0, 0, 250, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\left)
			.action_({arg view; 
				classData.layerName = view.string; 
				TXFrontScreen.storeCurrentLayer;
			});
		layerNameText.string = classData.layerName ;
		// label - background image 
		StaticText(layerBar, Rect(0, 0, 90, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\right)
			.string_("Screen image");
		// text - image file name 
		imageNameText = StaticText(layerBar, Rect(0, 0, 300, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\left);
		imageNameText.string = (classData.imageFileNames[classData.layerNo] ? " ").keep(60);

		// button - add image
		addImageButton = Button(layerBar, Rect(0, 0, 80, 20));
		addImageButton.states = [
			["Add Image", TXColor.white, TXColour.sysGuiCol1]
		];
		addImageButton.action = {TXFrontScreen.addImageDialog;};

		// button - delete image
		delImageButton = Button(layerBar, Rect(0, 0, 80, 20));
		delImageButton.states = [
			["Delete Image", TXColor.white, TXColour.black]
		];
		delImageButton.action = {
			classData.imageFileNames[classData.layerNo] = nil;
			classData.holdImages[classData.layerNo] = nil;
			// recreate view
			system.showView;
		};
		// label -  image mode
		StaticText(layerBar, Rect(0, 0, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\right)
			.string_("Image mode");
		//display mode 
		displayModeItems = [
			"0 - off - image not shown",
			"1 - fix left, fix top - default",
			"2 - tile horizontally, fix top",
			"3 - fix right, fix top",
			"4 - fix left, tile vertically",
			"5 - tile horizontally, tile vertically",
			"6 - fix right, tile vertically",
			"7 - fix left, fix bottom",
			"8 - tile horizontally, fix bottom",
			"9 - fix right, fix bottom",
			"10 - stretch horizontally & vertically to fit",
			"11 - center horizontally , center vertically & scale",
			"12 - center horizontally , fix top",
			"13 - center horizontally , fix bottom",
			"14 - fix left, center vertically",
			"15 - fix right, center vertically",
			"16 - center horizontally, center vertically - no scale",
		];
		// number box - display mode 
		displayModeNumberView = NumberBox(layerBar, Rect(0, 0, 20, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.scroll_(false)
			.action_({arg view; 
				var holdValue;
				holdValue = view.value.clip(0,16);
				classData.displayModeIndices[classData.layerNo] = holdValue;
				// update view
				system.showView;
			});
		displayModeNumberView.value = classData.displayModeIndices[classData.layerNo];
		// popup - display mode 
		displayModePopupView = PopUpMenu(layerBar, Rect(0, 0, 20, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(displayModeItems)
			.action_({arg view; 
				classData.displayModeIndices[classData.layerNo] = view.value;
				// update view
				system.showView;
			});
		displayModePopupView.value = classData.displayModeIndices[classData.layerNo];

		// add spacer
		layerBar.decorator.shift(10, 0);

		// popup - replace layer  
		replaceLayerActions = 
			[ ["Copy or load another screen ... ", {}] ]
			++ (1 .. 20).collect({arg item, i; 
					["Copy Screen " ++ item.asString, {TXFrontScreen.overwriteCurrFromLayer(item-1);}]; 
				})
			++ [	["Load screen template from disk", {TXFrontScreen.loadScreenTemplate; }], 
				["Save screen template to disk", {TXFrontScreen.saveScreenTemplate}]
			];
		replaceLayerPopupView = PopUpMenu(layerBar, Rect(0, 0, 200, 20))
			.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
			.items_(replaceLayerActions.collect({arg item, i; item.at(0)}))
			.action_({arg view; 
				replaceLayerActions.at(view.value).at(1).value;
				// update variables
				TXFrontScreen.currWidgetInd = 0;
				// update view
				system.showView;
			});
		replaceLayerPopupView.value = 0;

		// show widget properties for editing
		currWidgetProperties = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).properties;

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
			classData.lockWidgets = view.value;
		};
		lockWidgetsCheckbox.value = classData.lockWidgets ? 0;

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 85, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Screen colour");

		// screenColourbox 
		screenColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 35, 20));
		screenColourBox.background_(classData.screenColour);
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
			classData.screenColour = holdDragObject;
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
					classData.screenColour = 
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
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				classData.screenColour.red = view.value /255;
				// update view
				system.showView;
			});
		screenColourRed.value = (classData.screenColour.red * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleGreen)
			.align_(\centre)
			.string_("G");
			
		// numberbox - screenColour green
		screenColourGreen = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				classData.screenColour.green = view.value /255;
				// update view
				system.showView;
			});
		screenColourGreen.value = (classData.screenColour.green * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue)
			.align_(\centre)
			.string_("B");
		// numberbox - screenColour blue
		screenColourBlue = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				classData.screenColour.blue = view.value /255;
				// update view
				system.showView;
			});
		screenColourBlue.value = (classData.screenColour.blue * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 35, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Alpha");
		// numberbox - screenColour alpha
		screenColourAlpha = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				classData.screenColour.alpha = view.value /255;
				// update view
				system.showView;
			});
		screenColourAlpha.value = (classData.screenColour.alpha * 255).round;
		
		// draw line & go to next line
		propertiesBox.decorator.shift(0, 5);
		StaticText(propertiesBox, 240 @ 1).background_(TXColor.sysGuiCol1);
		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 100, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Current widget");
		// popup - current widget id  
		currWidgetIDPopupView = PopUpMenu(propertiesBox, Rect(0, 0, 60, 20))
			.background_(TXColor.white).stringColor_(TXColor.black)
			.items_(TXFrontScreen.arrWidgets.collect({arg item, i; "W " ++ item.widgetID.asString}))
			.action_({arg view; 
				TXFrontScreen.currWidgetInd = view.value; 
				currWidgetNameText.string = TXFrontScreen.arrWidgets.at(view.value).class.widgetName;
				// update view
				system.showView;
			});
		currWidgetIDPopupView.value = TXFrontScreen.currWidgetInd;

		// button - Delete widget	  
		Button(propertiesBox, Rect(0, 0, 60, 20))
			.states_([["Delete", TXColor.white, TXColor.sysDeleteCol]])
			.action_({
				// delete widget
				TXFrontScreen.deleteWidgetAtInd(TXFrontScreen.currWidgetInd);
				// update variables
				TXFrontScreen.currWidgetInd = 0;
				// update view
				system.showView;
			});

		// text - current module name 
		currWidgetNameText = StaticText(propertiesBox, Rect(0, 0, 205, 20))
			.stringColor_(TXColour.black).background_(TXColor.white)
			.align_(\left);
		currWidgetNameText.string = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class.widgetName;

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 50, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Width");
			
		// numberbox - current module width
		currWidgetWidth = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value
					.max(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).widthMin)
					.min(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).widthMax);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).width_(view.value, classData.screenWidth);
				TXFrontScreen.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currWidgetWidth.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).width;

		// popup - width choices
		PopUpMenu(propertiesBox, Rect(0, 0, 15, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {currWidgetWidth.valueAction = arrSizes.at(view.value);});
			});

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 50, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Height");
			
		// numberbox - current module height
		currWidgetHeight = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value
					.max(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).heightMin)
					.min(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).heightMax);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).height_(view.value, classData.screenHeight);
				TXFrontScreen.fitAllWidgetsToGrid;
				// update view
				system.showView;
			});
		currWidgetHeight.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).height;

		// popup - height choices
		PopUpMenu(propertiesBox, Rect(0, 0, 15, 20))
			.items_(arrSizes.collect({arg item, i; item.asString});)
			.background_(TXColor.white)
			.action_({arg view; 
				if (view.value > 0, {currWidgetHeight.valueAction = arrSizes.at(view.value);});
			});
// make for all widgets
//		// check properties
//		if ((TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSlider)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWKnob)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderV)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWLabelBox)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWNotesBox)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWActionButton)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DSlider)
//			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWNumberBox)
//			, {

			// go to next line
			propertiesBox.decorator.nextLine;
			// button swap width and height
			rotateButton = Button(propertiesBox, Rect(0, 0, 240, 20))
				.states_([["Swap width and height", 
					TXColor.white, TXColor.blue]]);
			rotateButton.action = {arg view; 
				var holdHeight, holdWidth;
				holdHeight = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).width;
				holdWidth = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).height;
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).height_(holdHeight);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).width_(holdWidth);
				// update view
				system.showView;
			};
//		});
		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 60, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("From left");
			
		// numberbox - current module from left
		currWidgetFromLeft = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(classData.screenWidth - TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).width);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).fromLeft_(view.value, classData.screenWidth);
				// update view
				system.showView;
			});
		currWidgetFromLeft.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).fromLeft(classData.screenWidth).asInteger;

		// text label  
		StaticText(propertiesBox, Rect(0, 0, 60, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("From top");
			
		// numberbox - current module from top
		currWidgetFromTop = NumberBox(propertiesBox, Rect(0, 0, 35, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(classData.screenHeight - TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).height);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).fromTop_(view.value, classData.screenHeight);
				// update view
				system.showView;
			});
		currWidgetFromTop.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).fromTop(classData.screenHeight).asInteger;

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;


		// text label  
		StaticText(propertiesBox, Rect(0, 0, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Box colour");
		// boxColourBox 
		boxColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
		boxColourBox.background_(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background);
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
			TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background = holdDragObject;
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
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background = 
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
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.red = view.value /255;
				// update view
				system.showView;
			});
		backgroundRed.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.red * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleGreen)
			.align_(\centre)
			.string_("G");
			
		// numberbox - background green
		backgroundGreen = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.green = view.value /255;
				// update view
				system.showView;
			});
		backgroundGreen.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.green * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 20, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue)
			.align_(\centre)
			.string_("B");
		// numberbox - background blue
		backgroundBlue = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.blue = view.value /255;
				// update view
				system.showView;
			});
		backgroundBlue.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.blue * 255).round;
		// text label  
		StaticText(propertiesBox, Rect(0, 0, 35, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Alpha");
		// numberbox - background alpha
		backgroundAlpha = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
			.scroll_(false)
			.action_({arg view; 
				view.value = view.value.max(0).min(255);
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.alpha = view.value /255;
				// update view
				system.showView;
			});
		backgroundAlpha.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background.alpha * 255).round;
		
		// ==========================================================================================
		
		// check properties
		if ((TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSlider)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWKnob)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderV)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderNo)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderNoV)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DSlider)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet)
		,{

			// spacer & next line
			propertiesBox.decorator.shift(0, 5).nextLine;
	
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 80, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Knob/Text colour");
			// knobColourBox 
			knobColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
			knobColourBox.background_(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour);
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
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour = holdDragObject;
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
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour = TXColour.perform(
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
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.red = view.value /255;
					// update view
					system.showView;
				});
			knobColourRed.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.red * 255).round;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleGreen)
				.align_(\centre)
				.string_("G");
				
			// numberbox - knobColour green
			knobColourGreen = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.green = view.value /255;
					// update view
					system.showView;
				});
			knobColourGreen.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.green * 255).round;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 20, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue)
				.align_(\centre)
				.string_("B");
			// numberbox - knobColour blue
			knobColourBlue = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.blue = view.value /255;
					// update view
					system.showView;
				});
			knobColourBlue.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.blue * 255).round;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 35, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Alpha");
			// numberbox - knobColour alpha
			knobColourAlpha = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(0).min(255);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.alpha = view.value /255;
					// update view
					system.showView;
				});
			knobColourAlpha.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour.alpha * 255).round;
			
			// go to next line
			propertiesBox.decorator.nextLine;
			// colour swap button
			colourSwapButton = Button(propertiesBox, Rect(0, 0, 240, 20))
				.states_([["Swap knob/text colour and box colour", 
					TXColor.white, TXColor.blue]]);
			colourSwapButton.action = {arg view; 
				var holdKnobColor2, holdBackground2;
				holdKnobColor2 = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background;
				holdBackground2 = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour;
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).knobColour = holdKnobColor2;
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background = holdBackground2;
				// update view
				system.showView;
			};
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
	
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Knob size");
				
			// numberbox - knob width
			knobWidthBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(0).min(600);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).thumbSize = view.value;
					// update view
					system.showView;
				});
			knobWidthBox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).thumbSize ;

			// check properties
			if ((TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderNo)
				or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderNoV)
				, {
	
				// spacer 
				propertiesBox.decorator.shift(10, 0);
	
				// text label  
				StaticText(propertiesBox, Rect(0, 0, 70, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
					.align_(\left)
					.string_("Number size");
					
				// numberbox - number size
				numberSizeBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.scroll_(false)
					.action_({arg view; 
						view.value = view.value.max(0).min(600);
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).numberSize = view.value;
						// update view
						system.showView;
					});
				numberSizeBox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).numberSize ;
	
			});
		});

		// ==========================================================================================
		
		// check properties
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).properties.includes(\string) and: 
			(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class != TXWNotesBox), {
		
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 150, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Text to be displayed");
			// clear text button
			clearTextButton = Button(propertiesBox, Rect(0, 0, 60, 20))
				.states_([["Clear text", 
					TXColor.white, TXColor.sysDeleteCol]])
				.action = {arg view; 
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).string = "";
					// update view
					system.showView;
				};
			// text box
			labelText = TextField(propertiesBox, Rect(0, 0, 240, 20))
				.action_({arg view; 
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).string = view.string;
				})
				.align_(\left);
			labelText.string = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).string;
		});
		
		// ==========================================================================================
		
		// check properties
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).properties.includes(\stringColorAsArgs), {
		
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 80, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Text colour");
			// textColourBox 
			textColourBox = DragBoth.new(propertiesBox, Rect(0, 0, 40, 20));
			textColourBox.background_(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).stringColor);
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
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).stringColor = holdDragObject;
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
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).stringColor = TXColour.perform(
							TXColour.colourNames.at(view.value - 1).asSymbol).copy;
						// update view
						system.showView;
					});
				});

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 60, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Font");
				
			// popup - font
			fontPopup = PopUpMenu(propertiesBox, Rect(105, 30, 140, 20))
				.background_(TXColor.white).stringColor_(TXColor.sysGuiCol1)
				.items_(classData.arrFonts)
				.action_({arg view; 
					if (view.value > 0, {
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).font = classData.arrFonts.at(view.value);
					});
				});
			fontPopup.value = classData.arrFonts.indexOf(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).font) ? 0;

			// numberbox - font size
			fontSizeBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(2).min(200);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).fontSize = view.value;
					// update view
					system.showView;
				});
			fontSizeBox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).fontSize;

			// go to next line
			propertiesBox.decorator.nextLine;
			// colour swap button
			colourSwapButton = Button(propertiesBox, Rect(0, 0, 240, 20))
				.states_([["Swap text colour and box colour", 
					TXColor.white, TXColor.blue]]);
			colourSwapButton.action = {arg view; 
				var holdStringColor2, holdBackground2;
				holdStringColor2 = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background;
				holdBackground2 = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).stringColor;
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).stringColor = holdStringColor2;
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).background = holdBackground2;
				// update view
				system.showView;
			};
		});

		// ==========================================================================================
		
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWActionButton, {

			// midi note trigger

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Midi Trigger");
				
			// checkbox midi Listen
			midiListenCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 40, 20),
				" ", TXColour.black, TXColor.white, 
				TXColour.black, TXColor.yellow, 4);
			midiListenCheckbox.action = {arg view; 
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiListen = view.value;
			};
			midiListenCheckbox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiListen ? 0;
			
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 30, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Note");
				
			// numberbox - midi Note
			midiNoteBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
				.scroll_(false)
				.action_({arg view; 
					view.value = view.value.max(0).min(127);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiNote = view.value;
					midiNotePopup.value = view.value;
					// update view
					system.showView;
				});
			midiNoteBox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiNote ? 0;

			// popup - midi note text
			midiNotePopup = PopUpMenu(propertiesBox, Rect(0, 0, 50, 20))
				.items_((0 .. 127).collect({arg item, i; TXGetMidiNoteString.new(item)});)
				.background_(TXColor.white)
				.action_({arg view; 
					midiNoteBox.valueAction = view.value;
				});
			midiNotePopup.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiNote ? 0;

			//  go to next line
			propertiesBox.decorator.nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 50, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Channels ");
				
			// numberbox - midi channel min
			midiChannelMinPopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((1..16).collect({arg item, i; item.asString}))
				.background_(TXColor.white)
				.action_({arg view; 
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMinChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMinPopup.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMinChannel ? 1) - 1;
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
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMaxChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMaxPopup.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMaxChannel ? 1) - 1;
			
			// checkbox Midi Note Learn
			midiLearnCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 70, 20),
				"Learn", TXColour.blue, TXColor.white, 
				TXColour.white, TXColor.blue, 0);
			midiLearnCheckbox.action = {arg view; 
				var midiNoteResponder;
				TXFrontScreen.midiDeActivate;
				midiNoteResponder = NoteOnResponder ({  |src, chan, note, vel|
					// stop responder
					midiNoteResponder.remove;
					//  set min/max channel and note no
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMinChannel = chan + 1;
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMaxChannel = chan + 1;
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiNote = note;
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiListen = 1;
					// update view
					system.showView;
				});
				TXFrontScreen.registerMidiResponder(midiNoteResponder);
			};

			// key down trigger

			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 70, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Key Trigger");
				
			// checkbox key Listen
			keyListenCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 40, 20),
				" ", TXColour.black, TXColor.white, 
				TXColour.black, TXColor.yellow, 4);
			keyListenCheckbox.action = {arg view; 
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).keyListen = view.value;
			};
			keyListenCheckbox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).keyListen ? 0;
			
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 30, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Key");
				
			// text field - key
			keyTextField = TextField(propertiesBox, Rect(0, 0, 28, 20))
				.action_({arg view; 
					if (view.string.size == 0, {view.string = "";});
					view.string = view.string.at(0);
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).keyChar = view.string;
					// update view
					system.showView;
				});
			keyTextField.string = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).keyChar ? "";

		});

		// ==========================================================================================
		
		// check properties
		if ((TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSlider)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWKnob)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderV)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderNo)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWSliderNoV)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DSlider)
			or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet)
		,{
			// spacer & go to next line
			propertiesBox.decorator.shift(0, 5).nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 31, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Midi");
				
			// checkbox midi Listen
			midiListenCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 35, 20),
				" ", TXColour.black, TXColor.white, 
				TXColour.black, TXColor.yellow, 4);
			midiListenCheckbox.action = {arg view; 
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiListen = view.value;
			};
			midiListenCheckbox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiListen ? 0;
			
			if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DSlider 
					or: TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet ,{
				// text label  
				StaticText(propertiesBox, Rect(0, 0, 80, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
					.align_(\left)
					.string_("Controller nos");
					
				// numberbox - midi controller no
				midiCCNoBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.scroll_(false)
					.action_({arg view; 
						view.value = view.value.max(0).min(127);
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo = view.value;
						// update view
						system.showView;
					});
				midiCCNoBox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo ? 0;
				// numberbox - midi controller no 2
				midiCCNoBox2 = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.scroll_(false)
					.action_({arg view; 
						view.value = view.value.max(0).min(127);
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo2 = view.value;
						// update view
						system.showView;
					});
				midiCCNoBox2.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo2 ? 0;
			},{
				// text label  
				StaticText(propertiesBox, Rect(0, 0, 80, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
					.align_(\left)
					.string_("Controller no");
					
				// numberbox - midi controller no
				midiCCNoBox = NumberBox(propertiesBox, Rect(0, 0, 28, 20))
					.scroll_(false)
					.action_({arg view; 
						view.value = view.value.max(0).min(127);
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo = view.value;
						// update view
						system.showView;
					});
				midiCCNoBox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo ? 0;
			});
			
			//  go to next line
			propertiesBox.decorator.nextLine;
			// text label  
			StaticText(propertiesBox, Rect(0, 0, 50, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\left)
				.string_("Channels ");
				
			// numberbox - midi channel min
			midiChannelMinPopup = PopUpMenu(propertiesBox, Rect(0, 0, 40, 20))
				.items_((1..16).collect({arg item, i; item.asString}))
				.background_(TXColor.white)
				.action_({arg view; 
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMinChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMinPopup.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMinChannel ? 1) - 1;
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
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMaxChannel = view.value + 1;
					// update view
					system.showView;
				});
			midiChannelMaxPopup.value = (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMaxChannel ? 16) - 1;
			
			// checkbox Midi CC Learn
			midiLearnCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 70, 20),
				"Learn", TXColour.blue, TXColor.white, 
				TXColour.white, TXColor.blue, 0);
			midiLearnCheckbox.action = {arg view; 
				var midiCCNoResponder;
				TXFrontScreen.midiDeActivate;
				midiCCNoResponder = CCResponder({ |src, chan, num, val|
					// stop responder
					midiCCNoResponder.remove;
					//  set min/max channel and controller no
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMinChannel = chan + 1;
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiMaxChannel = chan + 1;
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiCCNo = num;
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).midiListen = 1;
					// update view
					system.showView;
				});
				TXFrontScreen.registerMidiResponder(midiCCNoResponder);
			};

		});

		// ==========================================================================================
		
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWCheckBox, {
			// go to next line
			propertiesBox.decorator.nextLine;
			// checkbox reverse colours
			colourRevCheckbox = TXCheckBox(propertiesBox, Rect(0, 0, 240, 20),
				"Reverse colours when switched on", 
				TXColour.blue, TXColor.white, 
				TXColour.white, TXColor.blue);
			colourRevCheckbox.action = {arg view; 
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).colourReverse = view.value;
			};
			colourRevCheckbox.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).colourReverse ? 0;
			
		});

		// ==========================================================================================

		// spacer & go to next line
		propertiesBox.decorator.shift(0, 5).nextLine;
		// copy properties button
		copyPropertiesButton = Button(propertiesBox, Rect(0, 0, 100, 20))
			.states_([["Copy properties", 
				TXColor.white, TXColor.bluegreen]]);
		copyPropertiesButton.action = {arg view; 
			classData.holdWidgetClass = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class.asSymbol;
			classData.holdWidgetPropertyList = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).getPropertyList;
		};
		// paste properties button
		pastePropertiesButton = Button(propertiesBox, Rect(0, 0, 100, 20))
			.states_([["Paste properties", 
				TXColor.white, TXColor.bluegreen]]);
		pastePropertiesButton.action = {arg view; 
			var selectedPropertyList;
			// only paste relevent properties
			if (classData.holdWidgetClass.notNil, {
				// if classes match, copy all properties, else only display properties
				if (classData.holdWidgetClass == TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class.asSymbol, {
					selectedPropertyList = classData.holdWidgetPropertyList.select({arg item, i;
						TXFrontScreen.copyAllProperties.includes(item.at(0));
					});
				},{
					selectedPropertyList = classData.holdWidgetPropertyList.select({arg item, i;
						TXFrontScreen.copyDisplayProperties.includes(item.at(0));
					});
				});
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).setPropertyList(selectedPropertyList.deepCopy);
			});
			
			// update view
			system.showView;
		};
		// ==========================================================================================
		
		// check properties
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWNotesBox, {
		
			// make box to display notes
			notesBox = CompositeView(window,Rect(254, (classData.screenHeight * 0.75) + 35, 750, 150))
				.background_(TXColor.sysInterface);
			notesBox.decorator = FlowLayout(notesBox.bounds);
		

			// text label  
			StaticText(notesBox, Rect(0, 0, 400, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\centre)
				.string_("Enter notes text below, then press button to store notes:");

			// update button
			updateButton = Button(notesBox, Rect(0, 0, 100, 20))
				.states_([ ["Store Notes", TXColor.white, TXColor.sysEditCol] ]);
			updateButton.action = {arg view; 
				TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).string = notesView.string;
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
				.string_(TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).string)
				;
		});
		// ==========================================================================================
		
		if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).properties.includes(\arrActions), {
		
			// make box to display actions
			actionsBox = CompositeView(window,Rect(254, (classData.screenHeight * 0.75) + 35, 750, 150));
			actionsBox.decorator = FlowLayout(actionsBox.bounds);
		
			if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWActionButton, {
				// select actions button
				selectActionsButton = Button(actionsBox, Rect(0, 0, 76, 20))
					.states_([
						["1-5", TXColor.white, TXColor.blue],
						["6-10", TXColor.white, TXColor.blue]
					]);
				selectActionsButton.action = {arg view; 
					TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActions6to10 = view.value;
					// update view
					system.showView;
				};
				selectActionsButton.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActions6to10 ? 0;
				// text label  
				StaticText(actionsBox, Rect(0, 0, 70, 20))
					.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
					.align_(\centre)
					.string_("Module");
			},{	
				if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DSlider, {
					// x-y axis button
					selectActionsButton = Button(actionsBox, Rect(0, 0, 76, 20))
						.states_([
							["x-Axis", TXColor.white, TXColor.blue],
							["y-Axis", TXColor.white, TXColor.blue]
						]);
					selectActionsButton.action = {arg view; 
						TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showYAxis = view.value;
						// update view
						system.showView;
					};
					selectActionsButton.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showYAxis ? 0;
					// text label  
					StaticText(actionsBox, Rect(0, 0, 70, 20))
						.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
						.align_(\centre)
						.string_("Module");
				},{	
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet, {
						// x-y axis button
						selectActionsButton = PopUpMenu(actionsBox, Rect(0, 0, 90, 20))
							.stringColor_(TXColour.black).background_(TXColor.white)
							.items_(["x-axis", "y-axis", "pressure", "tilt x", "tilt y", "mouse-down", 
								"mouse-drag", "mouse-up", "double-click", 
							]);
						selectActionsButton.action = {arg view; 
							TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex = view.value;
							// update view
							system.showView;
						};
						selectActionsButton.value = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex ? 0;
						// text label  
						StaticText(actionsBox, Rect(0, 0, 54, 20))
							.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
							.align_(\centre)
							.string_("Module");
					},{	
						// text label  
						StaticText(actionsBox, Rect(0, 0, 150, 20))
							.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
							.align_(\centre)
							.string_("Module");
					});
				});
			});

			// text label  
			holdView = StaticText(actionsBox, Rect(0, 0, 300, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\centre);
			if (holdActionType == "commandAction", {
				holdView.string_("Action");
			},{
				holdView.string_("Parameter to update");
			});

			// text label  
			holdView = StaticText(actionsBox, Rect(0, 0, 250, 20))
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
				.align_(\centre);
			if (holdActionType == "commandAction", {
				holdView.string_("Value settings");
			},{
				holdView.string_("Extra parameter settings");
			});

			// up to 5 actions can be defined  
			if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWTextDisplayBox, {
				actionCount = 1;
			},{
				actionCount = 5;
			});
			
			actionCount.do({ arg item, i;
				var arrModules, modulePopup, arrActionItems, arrLegacyActionItems, actionPopup;
				var holdModuleID, holdModule;
				var holdControlSpec1, holdControlSpec2, holdControlSpec3, holdControlSpec4, holdArrActionSpecs;
				var val1NumberBox, val1Slider, val2NumberBox, val3NumberBox, val4NumberBox, valPopup; 
				var valCheckbox, valTextbox, valipaddress, holdArrActions;

				holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).arrActions;
				if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DSlider, {
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showYAxis == 1, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).arrActions2;
					});
				});
				if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWActionButton, {
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActions6to10 == 1, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).arrActions2;
					});
				});
				if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet, {
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 1, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).arrActions2;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 2, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).pressureActions;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 3, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).tiltXActions;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 4, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).tiltYActions;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 5, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).mouseDownActions;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 6, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).mouseDragActions;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 7, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).mouseUpActions;
					});
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex == 8, {
						holdArrActions = TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).mouseDoubleClickActions;
					});
				});

				// go to next line
				actionsBox.decorator.nextLine;

				// update variables
				if (holdActionType == "commandAction", {
					arrModules = system.arrWidgetActionModules;
				},{
					arrModules = system.arrWidgetValueActionModules;
//						.select({arg item, i; item.arrActionSpecs
//							.select({arg action, i; action.guiObjectType 
//								== TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).guiObjectType}).size > 0;  });
				});
				holdModuleID = holdArrActions.at(i).at(0);
				holdModule = system.getModuleFromID(holdModuleID);
				if (holdModule == 0, {holdModule = system});
				if (holdActionType == "commandAction", {
					holdArrActionSpecs = holdModule.arrActionSpecs;
					arrActionItems = holdArrActionSpecs
						.collect({arg item, i; item.actionName;});
					arrLegacyActionItems = holdArrActionSpecs .select({arg item, i; item.legacyType == 1})
						.collect({arg item, i; item.actionName;});

				},{
					holdArrActionSpecs = holdModule.arrActionSpecs
						.select({arg action, i;
						 (action.actionType == \valueAction) 
							and: (action.guiObjectType == TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).guiObjectType);});
					arrActionItems = holdArrActionSpecs
						.collect({arg item, i; item.actionName;});
					arrLegacyActionItems = holdModule.arrActionSpecs .select({arg item, i; item.legacyType == 1})
						.select({arg action, i;
						 (action.actionType == \valueAction) 
							and: (action.guiObjectType == TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).guiObjectType);})
						.collect({arg item, i; item.actionName;});
				});

				// popup - module
				modulePopup = PopUpMenu(actionsBox, Rect(0, 0, 150, 20))
					.background_(TXColor.white).stringColor_(TXColor.black)
					.items_(arrModules.collect({arg item, i; item.instName;}))
					.action_({arg view; 
						var holdAction;
						holdArrActions.at(i).put(0, arrModules.at(view.value).moduleID);
						// reset action index and text
						holdArrActions.at(i).put(1, 0);
						if (holdArrActions.at(i).size<8, {
							holdAction = holdArrActions.at(i).deepCopy;
							holdAction = holdAction.addAll([nil, nil, nil, nil, nil, nil]);
							holdArrActions.put(i, holdAction.deepCopy);
						},{
							holdArrActions.at(i).put(7, arrActionItems.at(0));
						});
						// update view
						system.showView;
					});
				modulePopup.value = arrModules.indexOf(holdModule) ? 0;
				
				// popup - action
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
					if (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXWActionButton
							or: (TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).class == TXW2DTablet 
								and: {TXFrontScreen.arrWidgets.at(TXFrontScreen.currWidgetInd).showActionIndex > 4})
					, {
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
								.scroll_(false)
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
							valTextbox.string = holdArrActions.at(i).at(2) ? " ";
						});
						
						// ipaddress 
						if (holdModule.arrActionSpecs.at(actionPopup.value).guiObjectType == \ipaddress, {
							valipaddress = TXNetAddress(actionsBox, Rect(0, 0, 250, 20),
							labelWidth: 0, showPresets: false);
							valipaddress.action = {arg view; 
								holdArrActions.at(i).put(2, view.string);
							};
							valipaddress.string = holdArrActions.at(i).at(2) ? "0.0.0.0";
						});
	
					}); // end of if ...TXWActionButton
					// if more than 1 control spec given, then create extra numberbox
					if (holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.size > 1, {
					// numberbox - value 2
						holdControlSpec2 = 
							holdArrActionSpecs.at(actionPopup.value).arrControlSpecFuncs.at(1);
						val2NumberBox = NumberBox(actionsBox, Rect(0, 0, 60, 20))
						.scroll_(false)
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
						.scroll_(false)
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
						.scroll_(false)
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
		layoutView = EnvelopeView(window, Rect(254, 30, classData.screenWidth * 0.75, classData.screenHeight * 0.75))
		.drawLines_(false)
		.selectionColor_(TXColor.sysGuiCol4)
		.strokeColor_ (Color.white)
		.background_(classData.screenColour)
		.drawRects_(true)
		.thumbSize_(5)
		.value_([Array.fill(TXFrontScreen.arrWidgets.size, 0.1), Array.fill(TXFrontScreen.arrWidgets.size, 0.1)])
		;
		// check for empty TXFrontScreen.arrWidgets
		if (TXFrontScreen.arrWidgets.size == 0, {TXFrontScreen.initArrWidgets});
		// display boxes to represent widgets 
		TXFrontScreen.arrWidgets.do({arg item, i;
			layoutView 
				.setThumbWidth(i, item.width * 0.75)
				.setThumbHeight(i, item.height * 0.75)
				.setString(i, "W " ++ item.widgetID.asString)
			;
			if (i == TXFrontScreen.currWidgetInd, {
				layoutView.setFillColor(i, TXColor.sysGuiCol4)
			}, {
				layoutView.setFillColor(i, TXColor.sysGuiCol2)
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
			if ((classData.lockWidgets == 0), {
				arrLayoutXvals = view.value.at(0);
				arrLayoutYvals = view.value.at(1);
				TXFrontScreen.arrWidgets.do({arg item, i;
					item.layoutX = arrLayoutXvals.at(i);
					item.layoutY = arrLayoutYvals.at(i);
					item.fitToGrid(classData.gridSize, classData.screenWidth, classData.screenHeight);
				});
			});
			// update variables & view  
			if (view.index.isPositive, {
				TXFrontScreen.currWidgetInd = view.index; 
				system.showView;
			});
		});

		// text label for clipboard items
		offsetHeight = (classData.screenHeight * 0.75) + 40;
		StaticText(window, Rect(1014, offsetHeight, 80, 20))
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.paleBlue2)
			.align_(\left)
			.string_("Clipboards");
		// number boxes  
		 NumberBox(window, Rect(1014, offsetHeight + 30, 80, 20))
		.action_({arg view; classData.clipboard1 = view.value;})
		.value_(classData.clipboard1);
		 NumberBox(window, Rect(1014, offsetHeight + 60, 80, 20))
		.action_({arg view; classData.clipboard2 = view.value;})
		.value_(classData.clipboard2);
		 NumberBox(window, Rect(1014, offsetHeight + 90, 80, 20))
		.action_({arg view; classData.clipboard3 = view.value;})
		.value_(classData.clipboard3);
//		 TextField(window, Rect(1010, offsetHeight + 120, 80, 20))
//		.action_({arg view; classData.clipboard4 = view.string;})
//		.value_(classData.clipboard4);


} // end of class method makeGui

}	// end of class


