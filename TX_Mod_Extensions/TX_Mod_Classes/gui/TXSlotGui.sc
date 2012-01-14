// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXSlotGui {
	var <>labelView, <>labelView2, <>labelView3, <>labelView4, <>numberViewSlot, <>buttonPlus, <>buttonMinus, 
		<>popupMenuView, <>buttonGo, <>controlSpec, <>arrActions, <>arrSlots;
	
	// arrActions are functions for: store slot no (arg), get slot data, store slot data (arg), get next slot no, store next slot no (arg)

	*new { arg window, dimensions, label, arrSlots, arrActions, initVal, 
			initAction=false, labelWidth=80, numberWidth = 25;
		^super.new.init(window, dimensions, label, arrSlots, arrActions, initVal, 
			initAction, labelWidth, numberWidth);
	}
	init { arg window, dimensions, label, argArrSlots, argArrActions, initVal, 
			initAction, labelWidth, numberWidth;

		labelView = StaticText(window, labelWidth @ dimensions.y);
		labelView.string = label ? "Slot action";
		labelView.align = \right;
		
		labelView2 = StaticText(window, 40 @ dimensions.y);
		labelView2.string = "Slot";
		labelView2.align = \centre;
		
		controlSpec = ControlSpec(0, 127);
		initVal = initVal ? 0;
		arrSlots = argArrSlots;
		arrActions = argArrActions;
		
		numberViewSlot = TXScrollNumBox(window, numberWidth @ dimensions.y);
		numberViewSlot.action = {|view|
			view.value = view.value.max(0).min(127);
			arrActions.at(0).value(view.value);
			this.setLabelView3String;
		};
		
		labelView3 = StaticText(window, 40 @ dimensions.y);
		this.setLabelView3String;
		labelView3.align = \centre;

		buttonMinus = Button(window, 15 @ 20)
			.states_([["-", TXColor.white, TXColor.sysGuiCol1]])
			.action_({|view|
				this.valueAction = this.value - 1;
			});
		
		buttonPlus = Button(window, 15 @ 20)
			.states_([["+", TXColor.white, TXColor.sysGuiCol1]])
			.action_({|view|
				this.valueAction = this.value + 1;
			});
		
		popupMenuView = PopUpMenu(window, 120 @ dimensions.y);
		popupMenuView.items = ["Load slot now", "Load slot next time", "Store to slot", "Find empty slot", 
			"Clear next slot", "Erase slot", "Erase whole bank"];
		popupMenuView.stringColor_(TXColour.sysGuiCol1).background_(TXColor.white);
		
		buttonGo = Button(window, 25 @ 20)
			.states_([["Do", TXColor.white, TXColor.sysGuiCol1]])
		// Go actions depend on numberViewSlot values and popupMenuView selected from:
		// "Load slot now", "Load slot next time", "Store to slot", "Find empty slot", "Clear next slot", "Erase slot", "Erase whole bank"
		// arrActions are functions for: store slot no (arg), get slot data, store slot data (arg), get next slot no, store next slot no (arg)
			.action_({
				[	{arrActions.at(2).value(arrSlots.at(numberViewSlot.value));},
					{arrActions.at(4).value(numberViewSlot.value); this.setLabelView4String},
					{arrSlots.put(numberViewSlot.value, arrActions.at(1).value); this.setLabelView3String;},
					{this.valueAction = arrSlots.detectIndex({arg item, i; item.isNil});},
					{arrActions.at(4).value(nil); this.setLabelView4String}, 
					{arrSlots.put(numberViewSlot.value, nil); this.setLabelView3String;}, 
					{arrSlots = Array.fill(127, nil); this.setLabelView3String;}
				].at(popupMenuView.value).value;
			});
		
		labelView4 = StaticText(window, 100 @ dimensions.y);
		labelView4.align = \centre;
		this.setLabelView4String;
		
		if (initAction) {
			this.valueAction = initVal;
		}{
			this.value = initVal;
		};
	}
	value_ { arg value; 
		if (value.isNil, {^nil});
		value = controlSpec.constrain(value);
		numberViewSlot.value = value;
		this.setLabelView3String;
	}
	valueAction_ { arg value; 
		if (value.isNil, {^nil});
		value = controlSpec.constrain(value);
		numberViewSlot.valueAction = (value);
		this.setLabelView3String;
	}
	value { 
		^numberViewSlot.value;
	}
	setLabelView3String {
		if (arrSlots.at(numberViewSlot.value ? 0).isNil, {
			labelView3.string = "empty";
		},{
			labelView3.string = "filled";
		});
	}
	setLabelView4String {
		if (arrActions.at(3).value.notNil, {
			labelView4.string = "Next slot: " ++ arrActions.at(3).value.asString;
		},{
			labelView4.string = " ";
		});
	}
	set { arg label, argArrActions, initVal, initAction=false;
		labelView.string = label;
		arrActions = argArrActions;
		initVal = initVal ? controlSpec.default;
		if (initAction) {
			this.valueAction = initVal;
		}{
			this.value = initVal;
		};
	}
}

