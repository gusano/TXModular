// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
		TXGridColour {	// self-building module of various gui elements
	var <>labelView, <>labelView2, <>labelView3;
	var <>colourGrid, <>activeGrid, <>snapshotButton, <>infoLabel, <>shadeBox;
	var <>redNumBox, <>greenNumBox, <>blueNumBox;
	var <>allOnButton, <>allOffButton, <>dragOptionPopup, <>colourPickerButton; 
	var snapshotArr;
	
	*new { arg argParent, dimensions, label, gridType, noRows, noCols, getSnapArrFunc, getValueFunc, 
		setValueFunc, labelWidth=80, system;
		^super.new.init(argParent, dimensions, label, gridType, noRows, noCols, getSnapArrFunc, getValueFunc, 
			setValueFunc, labelWidth, system);
	}

	init { arg argParent, dimensions, label, gridType, noRows, noCols, getSnapArrFunc, getValueFunc, 
		setValueFunc, labelWidth, system;
		
		var left, top;
				
		// initialise
		snapshotArr = getSnapArrFunc.value;
		left = 0;
		top = 0;
		// decorator next line 
		if (argParent.class == SCWindow, {
			argParent.view.decorator.nextLine;
			argParent.view.decorator.shift(0, 2);
		}, {
			argParent.decorator.nextLine;
			argParent.decorator.shift(0, 2);
		});

		// use left and top because  TXBoxGrid doesn't work properly with decorator
		if (argParent.decorator.notNil, {
			left = argParent.decorator.left;
			top = argParent.decorator.top;
		});
		// colour grid
		colourGrid = TXBoxGrid.new(argParent, Rect(left, top, 320, 240), columns: noCols, rows: noRows)
			.setBackgrColor_(Color.grey(0.4))
			.setNodeBorder_(0)
			.setFillMode_(true)
			.setFillColor_(Color.white);
		colourGrid.setTrailDrag_(true, false);
//		colourGrid.nodeDownAction = ({arg nodeloc; nodeloc.postln;})
		colourGrid.nodeUpAction_({arg nodeloc; 
				if (gridType == "Target", {
					// set target 
					this.setTarget(snapshotArr.at(nodeloc.at(1)).at(nodeloc.at(0))/255);
					// update module 
					setValueFunc.value(snapshotArr.at(nodeloc.at(1)).at(nodeloc.at(0))/255);
					// reset node states
					colourGrid.setNodeStates_(Array.fill2D(colourGrid.rows, colourGrid.columns, 1));
					// update gui
					system.flagGuiUpd;
				}); 
				if (gridType == "Zone", {
					// set target 
					activeGrid = getValueFunc.value.deepCopy;
					activeGrid.at(nodeloc.at(1)).put(nodeloc.at(0), colourGrid.getState(nodeloc.at(0), nodeloc.at(1)));
					// update array 
					setValueFunc.value(activeGrid);
					// update gui
					system.flagGuiUpd;
				}); 
			});
		colourGrid.nodeTrackAction_(colourGrid.upAction);
		if (gridType == "Zone", {
			colourGrid.setNodeStates_(getValueFunc.value);
		},{ 
			colourGrid.setNodeStates_(Array.fill2D(colourGrid.rows, colourGrid.columns, 1));
		}); 

		// create button
		snapshotButton = SCButton(argParent, 150 @ 20)
		.states_([["Take snapshot", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			snapshotArr = getSnapArrFunc.value;
			noRows.do({ arg row, i;
				noCols.do({arg col, j;
					colourGrid.setBoxColor_(j, i, Color.fromArray((snapshotArr.at(i).at(j) ? 0)/255));
				});
			});
			// update gui
			system.flagGuiUpd;
		});
		// label 
		labelView = SCStaticText(argParent, labelWidth @ 20)
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
		labelView.string = label;
		labelView.align = \left;
		
		if (gridType == "Target", {
			// label 
			labelView2 = SCStaticText(argParent, 320 @ 20)
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			labelView2.string = "click on a box, or use RGB values, to choose target colour";
			labelView2.align = \center;

			// decorator next line 
			if (argParent.class == SCWindow, {
				argParent.view.decorator.nextLine;
			}, {
				argParent.decorator.nextLine;
			});

			// shadebox 
			shadeBox = DragBoth.new(argParent, 40 @ 40);
			shadeBox.background_(Color.fromArray(getValueFunc.value));
			shadeBox.beginDragAction_({ arg view, x, y;
				var holdColour;
				view.dragLabel_("Colour");
				holdColour = shadeBox.background;
				// return colour
				holdColour;
		 	});
			shadeBox.canReceiveDragHandler = {
				SCView.currentDrag.isKindOf( Color )
			};
			shadeBox.receiveDragHandler = {
				var holdDragObject;
				holdDragObject = SCView.currentDrag;
				shadeBox.background_(holdDragObject);
				setValueFunc.value(shadeBox.background.asArray);
				redNumBox.value = shadeBox.background.red * 255;
				greenNumBox.value = shadeBox.background.green * 255;
				blueNumBox.value = shadeBox.background.blue * 255;
			};
			// label
			SCStaticText(argParent, 40 @ 20)
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white).string = "Red";
			// redNumBox
			redNumBox = TXScrollNumBox(argParent, 40 @ 20);
			redNumBox.action = {
				redNumBox.value = redNumBox.value.max(0).min(255);
				shadeBox.background_(shadeBox.background.red_(redNumBox.value / 255));
				setValueFunc.value(shadeBox.background.red_(redNumBox.value / 255).asArray);
			};
			redNumBox.value = (getValueFunc.value).at(0) * 255;
			// label
			SCStaticText(argParent, 40 @ 20)
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white).string = "Green";
			// greenNumBox
			greenNumBox = TXScrollNumBox(argParent, 40 @ 20);
			greenNumBox.action = {
				greenNumBox.value = greenNumBox.value.max(0).min(255);
				shadeBox.background_(shadeBox.background.green_(greenNumBox.value / 255));
				setValueFunc.value(shadeBox.background.green_(greenNumBox.value / 255).asArray);
			};
			greenNumBox.value = (getValueFunc.value).at(1) * 255;
			// label
			SCStaticText(argParent, 40 @ 20)
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white).string = "Blue";
			// blueNumBox
			blueNumBox = TXScrollNumBox(argParent, 40 @ 20);
			blueNumBox.action = {
				blueNumBox.value = blueNumBox.value.max(0).min(255);
				shadeBox.background_(shadeBox.background.blue_(blueNumBox.value / 255));
				setValueFunc.value(shadeBox.background.blue_(blueNumBox.value / 255).asArray);
			};
			blueNumBox.value = (getValueFunc.value).at(2) * 255;
			// colourPickerButton			
			colourPickerButton = Button(argParent, 120 @ 20)
			.states_([["Colour Picker", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				TXColour.showPicker;
			});
		}); 
		if (gridType == "Zone", {
			// allOnButton			
			allOnButton = SCButton(argParent, 50 @ 20)
			.states_([["All On", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				activeGrid = Array.fill2D(colourGrid.rows, colourGrid.columns, 1);
				colourGrid.setNodeStates_(activeGrid);
				// update array 
				setValueFunc.value(activeGrid);
			});
			// allOffButton
			allOffButton = SCButton(argParent, 50 @ 20)
			.states_([["All Off", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				activeGrid = Array.fill2D(colourGrid.rows, colourGrid.columns, 0);
				colourGrid.setNodeStates_(activeGrid);
				// update array 
				setValueFunc.value(activeGrid);
			});
			
			// dragOptionPopup
			dragOptionPopup = SCPopUpMenu(argParent, 200 @ 20)
				.stringColor_(TXColour.black).background_(TXColor.white);
			dragOptionPopup.items = ["dragging turns boxes on", "dragging switches box values"];
			dragOptionPopup.action = {
				if (dragOptionPopup.value == 0, {colourGrid.setTrailDrag_(true, false)});
				if (dragOptionPopup.value == 1, {colourGrid.setTrailDrag_(true, true)});
			};
			dragOptionPopup.value = 0;
		}); 
	}
	
	setActiveGrid_ {arg arrActiveStates;
		colourGrid.setNodeStates_(arrActiveStates);		
	}
	
	setTarget {arg target;
		redNumBox.value = target.at(0) * 255;
		greenNumBox.value = target.at(1) * 255;
		blueNumBox.value = target.at(2) * 255;
		shadeBox.background_(Color.fromArray(target));
	}
}

