// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXCheckBox {		// CheckBox module made from a button

var <>buttonView, <>action, <value=0, <parent;
var <>offText, <>onText;

*new{arg argParent, bounds, text, offStringColor, offBackground, onStringColor, onBackground, onOffTextType;
	 ^super.new.init(argParent, bounds, text, offStringColor, offBackground, onStringColor, onBackground, onOffTextType);
} 

init {arg argParent, bounds, text, offStringColor, offBackground, onStringColor, onBackground, onOffTextType;

	// create on and off text 
	onOffTextType = onOffTextType ? 0;
	[
		{offText = "[  ]  "; onText =  "[X] "; }, // 0
		{offText = " "; onText = "X"; }, // 1
		{offText = "[OFF] "; onText = "[ON] "; }, // 2
		{offText = "[Off] "; onText = "[On] "; }, // 3
		{offText = "-OFF- "; onText = "-ON- "; }, // 4
		{offText = "(OFF) "; onText = "(ON) "; }, // 5
		{offText = "[-] "; onText = "[+] "; },// 6
		{offText = "OFF "; onText = "ON "; }, // 7
		{offText = "0 - OFF"; onText = "1 - ON"; }, // 8
		{offText = "0 - False"; onText = "1 - True"; }, // 9
		{offText = "NO "; onText = "YES "; }, // 10
		{offText = "[NO] "; onText = "[YES] "; }, // 11
	].at(onOffTextType).value;

	// assign parent 
	parent = argParent;
	// create button 
	buttonView = Button(argParent, bounds);
	buttonView.states = [
		[offText ++ (text ? ""), offStringColor, offBackground],
		[onText ++ (text ? ""), onStringColor, onBackground]
	];
	buttonView.action = { |view|
		value = buttonView.value;
		action.value(this);
		view.focus(false);
		argParent.refresh;
	};
} 

	enabled {
		^buttonView.getProperty(\enabled)
	}
	enabled_ { arg bool;
		buttonView.setProperty(\enabled, bool)
	}
value_ { arg argVal;
	buttonView.value = argVal;
}

valueAction_  { arg argVal;
	buttonView.valueAction = argVal;
}
visible_ { arg argVal;
	buttonView.visible = argVal;
}

}

