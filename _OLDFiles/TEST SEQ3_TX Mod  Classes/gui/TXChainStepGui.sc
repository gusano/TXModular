// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
		
TXChainStepGui {	// specific gui for sequencer chain steps
	var <>labelView, <>labelView2, <>arrButtonViews, <>arrNumberViews, 
		<>action, <value, <arrChainSlots, <firstItemInd, <size;
	
	*new { arg window, dimensions, label, label2, rowSize, action, initVal, 
		initArrChainSlots, chainLo, chainHi, argFirstItemInd, initAction=false, 
		labelWidth=80, cloneButton=true, backgroundColour;
	^super.new.init(window, dimensions, label, label2, rowSize, action, initVal, 
		initArrChainSlots, chainLo, chainHi, argFirstItemInd, initAction, 
		labelWidth, cloneButton, backgroundColour);
	}
	init { arg window, dimensions, label, label2, rowSize, argAction, initVal, 
		initArrChainSlots, chainLo, chainHi, argFirstItemInd, initAction, 
		labelWidth, cloneButton, backgroundColour;
		
		action = argAction;
		value = initVal;
		arrChainSlots = initArrChainSlots;
		firstItemInd = argFirstItemInd;

		labelView = StaticText(window, labelWidth @ dimensions.y);
		labelView.string = label ? "Chain step";
		labelView.align = \right;
		
		// size of display array
		size = (chainHi - chainLo - firstItemInd + 1).max(1).min(rowSize);

		size.do({ arg item, i;
			var holdButton, holdColour, holdActionFunc;
			if (value == (chainLo + firstItemInd + i), {
				holdColour = TXColor.sysGuiCol2;
				holdActionFunc = nil;
			},{
				holdColour = TXColor.sysGuiCol1;
				holdActionFunc = {|view|
					value = chainLo + firstItemInd + i;
					action.value(this);			
				};
			});
			holdButton = Button(window, 20 @ dimensions.y)
				.states_([[(chainLo + firstItemInd + i).asString, TXColor.white, holdColour]])
				.action_(holdActionFunc);
			arrButtonViews = arrButtonViews.add(holdButton);
		});

		// add spacing 
		(rowSize - size).do({StaticText(window, 20 @ dimensions.y);});
		// create buttons to move firstItemInd
		[ ["<<", rowSize.neg], ["<", -1], [">", 1], [">>", rowSize]].do({ arg item, i;
			Button(window, 18 @ 20)
			.states_([[item.at(0), TXColor.white, TXColor.sysGuiCol1]])
			.action_({|view|
				firstItemInd = (firstItemInd + item.at(1)).max(0).min(chainHi - chainLo);
				action.value(this);
			});
		});

		// decorator next line & shift 
			if (window.class == Window, {
				window.view.decorator.nextLine;
			}, {
				window.decorator.nextLine;
			});
			
		labelView2 = StaticText(window, labelWidth @ dimensions.y);
		labelView2.string = label ? "Chain slot";
		labelView2.align = \right;
		
		size.do({ arg item, i;
			var holdNumberBox;
			holdNumberBox = TXScrollNumBox(window, 20 @ dimensions.y, [0,99].asSpec)
				.font_(Font("Gill Sans", 10));
			if (backgroundColour.notNil, {holdNumberBox.background_(backgroundColour);}); 
			holdNumberBox.action = { arg view;
				arrChainSlots.put((chainLo + firstItemInd + i - 1), view.value);
				action.value(this);
			};
			arrNumberViews = arrNumberViews.add(holdNumberBox);
			holdNumberBox.value = arrChainSlots.at(chainLo + firstItemInd + i - 1);
		});
		
		// add spacing 
		(rowSize - size).do({StaticText(window, 20 @ dimensions.y);});
		
		if (cloneButton == true, {
			Button(window, 50 @ 20)
			.states_([["clone 1", TXColor.white, TXColor.sysGuiCol1]])
			.action_({|view|
				arrNumberViews.do({ arg item, i;
					if (i > 0, {
						item.valueAction = arrNumberViews.at(0).value;
					});
				});
				action.value(this);
			});
		});
		
		if (initAction) {
			action.value(this);
		};
	}
	value_ { arg argValue; 
		value = argValue;
	}
	valueAction_ { arg argValue; 
		value = argValue;
		action.value(this);
	}
}

