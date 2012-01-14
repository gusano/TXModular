// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
		
TXWaveTableSpecs {	// MultiSlider, popup and buttons for wavetable specs with 5 user slots for saving banks
	var <>arrButtonViews, <>labelView, <>multiSliderView, <>popupView, 
		<>rangeSliderView1, <>doubleSliderView1, <>doubleSliderView2, <>doubleSliderView3, <>doubleSliderView4, <>doButtonView,
		<>action, <value, <arrSlotData, emptyArray, currentTable, arrProcessSpecs, processFunction, processType, maxNoHarmonics;
	
	*new { arg window, dimensions, label, action, initVal, initAction=false, labelWidth=80, initSlotVals, 
			argMaxNoHarmonics=32, argShowProcesses=1;
		^super.new.init(window, dimensions, label, action, initVal, initAction, labelWidth, initSlotVals, 
			argMaxNoHarmonics, argShowProcesses);
	}
	init { arg window, dimensions, label, argAction, initVal, initAction, labelWidth, initSlotVals, 
			argMaxNoHarmonics, argShowProcesses;
		var popItems, popAction, newArray, holdButton, holdClipboard;
	
		maxNoHarmonics = argMaxNoHarmonics.asInteger;
		this.initProcessSpecs;
		emptyArray = Array.newClear(maxNoHarmonics).fill(0);
		currentTable = 0;
		initVal = initVal ? emptyArray.dup(8);
		// conform each slot to correct size
		initVal = initVal.collect({arg item, i; (item ++ emptyArray).keep(maxNoHarmonics);});
		initSlotVals = initSlotVals ? emptyArray.dup(8).dup(5);
		arrSlotData = initSlotVals;
		action = argAction;
				
		labelView = StaticText(window, labelWidth @ 20);
		labelView.string = label;
		labelView.align = \right;

		// row of buttons 
		8.do({ arg i;
			holdButton = Button(window, 20 @ 20);
			holdButton.states = [
				[(i + 1).asString, TXColor.white, TXColor.sysGuiCol1],
				[(i + 1).asString, TXColor.white, TXColor.sysGuiCol2]
			];
			holdButton.action = { arg view;
				currentTable = i;
				this.updateButtonColours;
				multiSliderView.value = value.at(currentTable);
			};
			arrButtonViews = arrButtonViews.add(holdButton);
		});
		this.updateButtonColours;

		// decorator next line 
		this.nextLine(window);

		// text 
		StaticText(window, labelWidth @ 20)
			.string_("Harmonics")
			.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
			.align_(\right);

		// multislider 
		multiSliderView = MultiSliderView(window, 256 @ 120);
		multiSliderView.gap_(2);
		multiSliderView.indexThumbSize_((256 / maxNoHarmonics).asInteger - 2);
		multiSliderView.isFilled_(true).fillColor_(TXColor.sysGuiCol1);
		multiSliderView.action = {arg view;
			value.put(currentTable, view.value);
			action.value(this);
		};
		// create button
		Button(window, 50 @ 20)
		.states_([["Copy", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			holdClipboard = multiSliderView.value;
		});
		// decorator shift 
		if (window.class == Window, {
			window.view.decorator.shift(-54, 25);
		}, {
			window.decorator.shift(-54, 25);
		});
		// create button
		Button(window, 50 @ 20)
		.states_([["Paste", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			multiSliderView.value = holdClipboard;
			value.put(currentTable, holdClipboard);
			action.value(this);
		});
		// decorator shift 
		if (window.class == Window, {
			window.view.decorator.shift(-54, 25);
		}, {
			window.decorator.shift(-54, 25);
		});
		// create button
		Button(window, 50 @ 20)
		.states_([["Reset", TXColor.white, TXColor.sysGuiCol1]])
		.action_({
			multiSliderView.value = emptyArray;
			value.put(currentTable, emptyArray);
			action.value(this);
		});
		// decorator shift 
		if (window.class == Window, {
			window.view.decorator.shift(0,-50);
		}, {
			window.decorator.shift(0,-50);
		});

		// decorator next line 
		this.nextLine(window);
		
		// bank load buttons 
		StaticText(window, 80 @ 20)
			.string_("Load bank")
			.stringColor_(TXColour.white).background_(TXColor.sysGuiCol1);
		5.do({ arg i;
			Button(window, 20 @ 20)
			.states_([[(i+1).asString, TXColor.white, TXColor.sysGuiCol1]])
			.action_({
				this.loadSlot(i);
			});
		});
		// bank store buttons 
		StaticText(window, 10 @ 20);
		StaticText(window, 80 @ 20)
			.string_("Store bank")
			.stringColor_(TXColour.white).background_(TXColor.sysGuiCol2);
		5.do({ arg i;
			Button(window, 20 @ 20)
			.states_([[(i+1).asString, TXColor.white, TXColor.sysGuiCol2]])
			.action_({
				this.storeSlot(i);
			});
		});		

		// decorator next line 
		this.nextLine(window);

		// if Processes are shown 
		if (argShowProcesses == 1, {

			// decorator shift 
			if (window.class == Window, {
				window.view.decorator.shift(0, 20);
			}, {
				window.decorator.shift(0, 20);
			});
	
			// Popup 
			popItems = arrProcessSpecs.collect({arg item, i; item.at(0);});
			popAction = {arg view; 
				processFunction = arrProcessSpecs.at(view.value).at(1);
				[doubleSliderView1, doubleSliderView2, doubleSliderView3].do({arg item, i;
					item.labelView.string = arrProcessSpecs.at(view.value).at(i+2).at(0);
					item.controlSpec = arrProcessSpecs.at(view.value).at(i+2).at(1).value;
					item.lo = arrProcessSpecs.at(view.value).at(i+2).at(2).value;
					item.hi = arrProcessSpecs.at(view.value).at(i+2).at(3).value;
				});
				processType = arrProcessSpecs.at(view.value).at(5);
			};
			popupView = TXPopup(window, dimensions.x @ 20, "Process", popItems, popAction, 
				0, false, labelWidth);
			popupView.labelView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			popupView.popupMenuView.stringColor_(TXColour.black).background_(TXColor.white);
	
			// decorator next line 
			this.nextLine(window);
	
			// text 
			StaticText(window, 300 @ 20)
				.string_("Processing parameters - start & end values")
				.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white)
				.align = \centre;
	
			// RUN button 
			doButtonView = Button(window, 80 @ 20);
			doButtonView.states = [
				["RUN", TXColor.white, TXColor.sysGuiCol1]
			];
			doButtonView.action = { arg view;
				var loIndex, hiIndex, numTables, outArray, changeArray;
				if (processFunction.notNil, {
					outArray = value;
					loIndex = min(rangeSliderView1.lo, rangeSliderView1.hi) - 1;
					hiIndex = max(rangeSliderView1.lo, rangeSliderView1.hi) - 1;
					numTables = hiIndex - loIndex + 1;
					// check type of process
					if (processType == \localChange, {
						numTables.do({ arg i;
								var holdMult, arg1, arg2, arg3, arg4, blendPercent, holdProcessed, holdBlended;
								if (numTables > 1, {holdMult = i / (numTables-1);}, {holdMult = 0});
								arg1 = doubleSliderView1.lo 
									+ ((doubleSliderView1.hi - doubleSliderView1.lo) * holdMult);
								arg2 = doubleSliderView2.lo 
									+ ((doubleSliderView2.hi - doubleSliderView2.lo) * holdMult);
								arg3 = doubleSliderView3.lo 
									+ ((doubleSliderView3.hi - doubleSliderView3.lo) * holdMult);
								arg4 = outArray.at(loIndex + i);
								holdProcessed = processFunction.value([arg1, arg2, arg3, arg4]);
								blendPercent = doubleSliderView4.lo 
									+ ((doubleSliderView4.hi - doubleSliderView4.lo) * holdMult);
								holdBlended = outArray.at(loIndex + i).blend(holdProcessed, blendPercent/100);
								outArray.put(loIndex + i, holdBlended);
						});
					},{
						changeArray = processFunction.value(outArray.copyRange(loIndex.asInteger, hiIndex.asInteger));
						numTables.do({ arg i;
								outArray.put(loIndex + i, changeArray.at(i));
						});
					});
					value = outArray;
					multiSliderView.value = value.at(currentTable);
					action.value(this);
				});
			};
	
			// decorator next line 
			this.nextLine(window);
	
			// RangeSlider 
			rangeSliderView1 = TXRangeSlider(window, dimensions.x @ 20, "Wavetables", ControlSpec(1, 8, step:1), nil, 0, 8);
			rangeSliderView1.labelView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			
			// decorator next line 
			this.nextLine(window);
			
			// DoubleSlider 
			doubleSliderView4 = TXDoubleSlider(window, dimensions.x @ 20, "Mix percent", ControlSpec(0, 100), nil, 100, 100);
			doubleSliderView4.labelView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			doubleSliderView4.sliderView1.knobColor_(TXColour.sysGuiCol1);
			doubleSliderView4.sliderView2.knobColor_(TXColour.sysGuiCol1);
	
			// decorator next line 
			this.nextLine(window);
	
			// DoubleSlider 
			doubleSliderView1 = TXDoubleSlider(window, dimensions.x @ 20, "(unused)", ControlSpec(0, 1), nil, 0, 1);
			doubleSliderView1.labelView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			doubleSliderView1.sliderView1.knobColor_(TXColour.sysGuiCol1);
			doubleSliderView1.sliderView2.knobColor_(TXColour.sysGuiCol1);
	
			// decorator next line 
			this.nextLine(window);
	
			// DoubleSlider 
			doubleSliderView2 = TXDoubleSlider(window, dimensions.x @ 20, "(unused)", ControlSpec(0, 1), nil, 0, 1);
			doubleSliderView2.labelView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			doubleSliderView2.sliderView1.knobColor_(TXColour.sysGuiCol1);
			doubleSliderView2.sliderView2.knobColor_(TXColour.sysGuiCol1);
	
			// decorator next line 
			this.nextLine(window);
	
			// DoubleSlider 
			doubleSliderView3 = TXDoubleSlider(window, dimensions.x @ 20, "(unused)", ControlSpec(0, 1), nil, 0, 1);
			doubleSliderView3.labelView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
			doubleSliderView3.sliderView1.knobColor_(TXColour.sysGuiCol1);
			doubleSliderView3.sliderView2.knobColor_(TXColour.sysGuiCol1);
	
		});		

		if (initAction, {
			this.value = initVal;
		}, {
			value = initVal;
			multiSliderView.value = value.at(currentTable);
		});
	}
	value_ { arg argValue; 
		value = argValue;
		multiSliderView.value = value.at(currentTable);
		action.value(this);
	}
	nextLine {arg window;
		if (window.class == Window, {
			window.view.decorator.nextLine;
		}, {
			window.decorator.nextLine;
		});
	}
	storeSlot { arg num; 
		arrSlotData.put(num, this.value.deepCopy);
	}
	loadSlot { arg num; 
		this.value = arrSlotData.at(num).deepCopy;
	}
	updateButtonColours { arg num; 
		arrButtonViews.do({arg item, i;
			if (i == currentTable, {
				item.value = 1;
			}, {
				item.value = 0;
			});
			item.refresh;
		});
	}
	initProcessSpecs {
		arrProcessSpecs = [
			// note -  \localChange processes only change 1 wavetable spec, \globalChange processes change multiple wavetable specs
			["select a process...", 
				{ }, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: normalise harmonic levels", 
				{arg argArray; 
					argArray.at(3).normalize;
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: reverse wavetable order", 
				{arg argArray; 
					argArray.reverse;
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\globalChange,
			], 
			["Transform: randomise wavetable order", 
				{arg argArray; 
					argArray.scramble;
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\globalChange,
			], 
			["Transform: sort wavetables by harmonic weighting", 
				{arg argArray; 
					argArray.sort({ arg argArray1, argArray2;
						var weight1, weight2;
						weight1 = argArray1.normalizeSum.collect({arg item, i; i * item;}).sum;
						weight2 = argArray2.normalizeSum.collect({arg item, i; i * item;}).sum;
						weight1 < weight2;
					});
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\globalChange,
			], 
			["Transform: reverse order of harmonic levels", 
				{arg argArray; 
					argArray.at(3).reverse;
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: randomise order of harmonic levels", 
				{arg argArray; 
					argArray.at(3).scramble;
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: multiply all harmonic levels by a value", 
				{arg argArray; 
					(argArray.at(3) * argArray.at(0)).max(0).min(1);
				}, 
				["Multiply", ControlSpec(0, 2), 0.1, 2], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: add a value to all harmonic levels", 
				{arg argArray; 
					(argArray.at(3) + argArray.at(0)).max(0).min(1);
				}, 
				["Add", ControlSpec(0, 1), 0, 0.5], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: subtract a value from all harmonic levels", 
				{arg argArray; 
					(argArray.at(3) - argArray.at(0)).max(0).min(1);
				}, 
				["Subtract", ControlSpec(0, 1), 0, 0.5], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Transform: invert harmonic levels", 
				{arg argArray; 
					1 - argArray.at(3);
				}, 
				["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], ["(unused)", ControlSpec(0, 1), 0, 1], 
				\localChange,
			], 
			["Generate: Linear decay - decrease harmonic levels", 
				{arg argArray; 
					(	(Harmonics(maxNoHarmonics).decay(argArray.at(1)) .copyRange(0, argArray.at(0).asInteger-1)  
							++ Array.newClear(maxNoHarmonics).fill(0)
						).copyRange(0, maxNoHarmonics - 1) 
							+ Array.rand2(maxNoHarmonics, argArray.at(2))
					).max(0).min(1); 
				}, 
				["No. harmonics", {ControlSpec(1, maxNoHarmonics, step:1)}, {maxNoHarmonics}, {maxNoHarmonics}], 
				["Decay rate", ControlSpec(0.01, 3), 3, 0.3], 
				["Randomness", ControlSpec(0, 1), 0, 0], 
				\localChange,
			], 
			["Generate: Geometric decay - decrease harmonic levels", 
				{arg argArray; 
					(	(Harmonics(maxNoHarmonics).geom(argArray.at(1)) .copyRange(0, argArray.at(0).asInteger-1)  
							++ Array.newClear(maxNoHarmonics).fill(0)
						).copyRange(0, maxNoHarmonics - 1) 
							+ Array.rand2(maxNoHarmonics, argArray.at(2))
					).max(0).min(1);  
				}, 
				["No. harmonics", {ControlSpec(1, maxNoHarmonics, step:1)}, {maxNoHarmonics}, {maxNoHarmonics}], 
				["Decay rate", ControlSpec(1, 3), 3, 1.1], 
				["Randomness", ControlSpec(0, 1), 0, 0], 
				\localChange,
			], 
			["Generate: random harmonic levels", 
				{arg argArray; 
					( Harmonics(maxNoHarmonics).rand(argArray.at(1), argArray.at(2)) .copyRange(0, argArray.at(0).asInteger-1)  
						++ Array.newClear(maxNoHarmonics).fill(0)
					).copyRange(0, maxNoHarmonics - 1) 
				}, 
				["No. harmonics", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, {maxNoHarmonics}], 
				["Min. value", ControlSpec(0, 1), 0, 0], 
				["Max. value", ControlSpec(0, 1), 1, 1], 
				\localChange,
			], 
			["Generate: random levels in exponential distribution", 
				{arg argArray; 
					( Harmonics(maxNoHarmonics).exprand(argArray.at(1).max(0.001), argArray.at(2).max(0.001)) 
						.copyRange(0, argArray.at(0).asInteger-1)  
						++ Array.newClear(maxNoHarmonics).fill(0)
					).copyRange(0, maxNoHarmonics - 1) 
				}, 
				["No. harmonics", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, {maxNoHarmonics}], 
				["Min. value", ControlSpec(0, 1), 0, 0], 
				["Max. value", ControlSpec(0, 1), 1, 1], 
				\localChange,
			], 
			["Generate: random levels in linear distribution", 
				{arg argArray; 
					( Harmonics(maxNoHarmonics).linrand(argArray.at(1), argArray.at(2)) 
						.copyRange(0, argArray.at(0).asInteger-1)  
						++ Array.newClear(maxNoHarmonics).fill(0)
					).copyRange(0, maxNoHarmonics - 1) 
				}, 
				["No. harmonics", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, {maxNoHarmonics}], 
				["Min. value", ControlSpec(0, 1), 0, 0], 
				["Max. value", ControlSpec(0, 1), 1, 1], 
				\localChange,
			], 
			["Generate: harmonic levels with a formant shape", 
				{arg argArray; 
					( Harmonics(maxNoHarmonics).formant(argArray.at(1).asInteger-1, argArray.at(2).asInteger) 
						.copyRange(0, argArray.at(0).asInteger-1)  
						++ Array.newClear(maxNoHarmonics).fill(0)
					).copyRange(0, maxNoHarmonics - 1) 
				}, 
				["No. harmonics", {ControlSpec(1, maxNoHarmonics, step:1)}, {maxNoHarmonics}, {maxNoHarmonics}], 
				["Formant mid", {ControlSpec(1, maxNoHarmonics, step:1)}, 1, 13], 
				["Formant width", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, {maxNoHarmonics}], 
				\localChange,
			], 
			["Generate: harmonic levels with a shape of teeth", 
				{arg argArray; 
					( Harmonics(maxNoHarmonics).teeth(argArray.at(1).asInteger, argArray.at(2).asInteger-1)
						.copyRange(0, argArray.at(0).asInteger-1)  
						++ Array.newClear(maxNoHarmonics).fill(0)
					).copyRange(0, maxNoHarmonics - 1) 
				}, 
				["No. harmonics", {{ControlSpec(1, maxNoHarmonics, step:1)}}, 1, {maxNoHarmonics}], 
				["Spacing", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, 2], 
				["Start", {ControlSpec(1, maxNoHarmonics, step:1)}, 1, 1], 
				\localChange,
			], 
//
//		- the following processes need to be created:
//
//			["Transform: zero harmonic levels after each interval", 
//				{arg argArray; 
//					// ACTION
//					// ACTION
//				}, 
//				["Interval", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, 2], ["(unused)", ControlSpec(0, 1), 0, 1], 
//					["(unused)", ControlSpec(0, 1), 0, 1], 
//				\localChange,
//			], 
//			["Transform: zero all harmonic levels except at each interval", 
//				{arg argArray; 
//					// ACTION
//					// ACTION
//				}, 
//				["Interval", {ControlSpec(1, maxNoHarmonics, step:1)}, 2, 2], ["(unused)", ControlSpec(0, 1), 0, 1], 
//					["(unused)", ControlSpec(0, 1), 0, 1], 
//				\localChange,
//			], 
//			["Transform: zero harmonic levels of randomly selected harmonics", 
//				{arg argArray; 
//					// ACTION
//					// ACTION
//				}, 
//				["No. to remove", {ControlSpec(1, maxNoHarmonics, step:1)}, 1, 16], ["(unused)", ControlSpec(0, 1), 0, 1], 
//					["(unused)", ControlSpec(0, 1), 0, 1], 
//				\localChange,
//			], 

		];
	}
}

