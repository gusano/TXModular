// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
TXGridGrey {	// self-building module of various gui elements
	var <>labelView, <>labelView2, <>labelView3;
	var <>greyGrid, <>activeGrid, <>snapshotButton, <>infoLabel, <>shadeBox, <>shadeSlider;
	var <>allOnButton, <>allOffButton, <>dragOptionPopup; 
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
		if (argParent.class == Window, {
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
		// grey grid
		greyGrid = TXBoxGrid.new(argParent, Rect(left, top, 320, 240), columns: noCols, rows: noRows)
			.setBackgrColor_(Color.grey(0.4))
			.setNodeBorder_(0)
			.setFillMode_(true)
			.setFillColor_(Color.white);
		greyGrid.setTrailDrag_(true, false);
//		greyGrid.nodeDownAction = ({arg nodeloc; nodeloc.postln;})
		greyGrid.nodeUpAction_({arg nodeloc; 
				if (gridType == "Target", {
					// set target 
					this.setTarget(snapshotArr.at(nodeloc.at(1)).at(nodeloc.at(0))/255);
					// update module 
					setValueFunc.value(snapshotArr.at(nodeloc.at(1)).at(nodeloc.at(0))/255);
					// reset node states
					greyGrid.setNodeStates_(Array.fill2D(greyGrid.rows, greyGrid.columns, 1));
					// update gui
					system.flagGuiUpd;
				}); 
				if (gridType == "Zone", {
					// set target 
					activeGrid = getValueFunc.value.deepCopy;
					activeGrid.at(nodeloc.at(1)).put(nodeloc.at(0), greyGrid.getState(nodeloc.at(0), nodeloc.at(1)));
					// update array 
					setValueFunc.value(activeGrid);
					// update gui
					system.flagGuiUpd;
				}); 
			});
		greyGrid.nodeTrackAction_(greyGrid.upAction);
		if (gridType == "Zone", {
			greyGrid.setNodeStates_(getValueFunc.value);
		},{ 
			greyGrid.setNodeStates_(Array.fill2D(greyGrid.rows, greyGrid.columns, 1));
		}); 

		// create button
		snapshotButton = Button(argParent, 150 @ 20)
		.states_([["Take snapshot", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			snapshotArr = getSnapArrFunc.value;
			noRows.do({ arg row, i;
				noCols.do({arg col, j;
					greyGrid.setBoxColor_(j, i, TXColor.grey((snapshotArr.at(i).at(j) ? 0)/255));
				});
			});
			// update gui
			system.flagGuiUpd;
		});
		// label 
		labelView = StaticText(argParent, labelWidth @ 20)
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
		labelView.string = label;
		labelView.align = \left;
		
		if (gridType == "Target", {
			// label 
			labelView2 = StaticText(argParent, 320 @ 20)
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			labelView2.string = "click on a box, or use slider, to choose target shade";
			labelView2.align = \center;

			// decorator next line 
			if (argParent.class == Window, {
				argParent.view.decorator.nextLine;
			}, {
				argParent.decorator.nextLine;
			});

			// shadebox 
			shadeBox = StaticText(argParent, 40 @ 40);
			shadeBox.background_(Color.grey(getValueFunc.value));
			// shadeSlider
			shadeSlider = Slider(argParent, 100 @ 20);
			shadeSlider.action = {
				shadeBox.background_(Color.grey(shadeSlider.value));
				setValueFunc.value(shadeSlider.value);
			};
			shadeSlider.value = (getValueFunc.value);
		}); 
		if (gridType == "Zone", {
			// allOnButton			
			allOnButton = Button(argParent, 50 @ 20)
			.states_([["All On", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				activeGrid = Array.fill2D(greyGrid.rows, greyGrid.columns, 1);
				greyGrid.setNodeStates_(activeGrid);
				// update array 
				setValueFunc.value(activeGrid);
			});
			// allOffButton
			allOffButton = Button(argParent, 50 @ 20)
			.states_([["All Off", TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				activeGrid = Array.fill2D(greyGrid.rows, greyGrid.columns, 0);
				greyGrid.setNodeStates_(activeGrid);
				// update array 
				setValueFunc.value(activeGrid);
			});
			
			// dragOptionPopup
			dragOptionPopup = PopUpMenu(argParent, 200 @ 20)
				.stringColor_(TXColour.black).background_(TXColor.white);
			dragOptionPopup.items = ["dragging turns boxes on", "dragging switches box values"];
			dragOptionPopup.action = {
				if (dragOptionPopup.value == 0, {greyGrid.setTrailDrag_(true, false)});
				if (dragOptionPopup.value == 1, {greyGrid.setTrailDrag_(true, true)});
			};
			dragOptionPopup.value = 0;
		}); 
	}
	
	setActiveGrid_ {arg arrActiveStates;
		greyGrid.setNodeStates_(arrActiveStates);		
	}
	
	setTarget {arg target;
		shadeSlider.value = (target);
		shadeBox.background_(Color.grey(target));
	}
}

