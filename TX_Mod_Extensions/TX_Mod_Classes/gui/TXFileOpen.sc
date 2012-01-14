// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
		
TXFileOpen {	// file open module with filename string

// NOTE - action function should return a path to be displayed

	var <>buttonView, <>textView, <>action, <string, holdVal;
	
	*new { arg argParent, dimensions, label, action, initString, 
			buttonWidth=80;
		^super.new.init(argParent, dimensions, label, action, initString, 
			buttonWidth);
	}
	init { arg argParent, dimensions, label, argAction, initString, buttonWidth;
	
		action = argAction;
		buttonView = Button(argParent, buttonWidth @ dimensions.y)
		.states_([
			[label ? "Open new file", TXColor.white, TXColor.sysGuiCol1]
		])
		.action_({
			// get path/filename
			Dialog.getPaths({ arg paths;
				// run action function which returns a path, and assign this to textView.string
				holdVal = action.value(paths.at(0));
				textView.string = holdVal.asString.keep(-60);
			});
		});
		textView = StaticText(argParent, (dimensions.x - buttonWidth+4) @ dimensions.y)
			.stringColor_(TXColor.sysGuiCol1).background_(TXColor.white);
		textView.string = initString.asString.keep(-60);
		textView.font = (Font("Gill Sans", 11));
		textView.align = \left;
}
	
	string_ { arg argVal;
		textView.string = argVal.asString.keep(-60);
	}
	
	value {
		^this.string;
	}
	
	value_ { arg argVal;
		this.string = argVal;
	}
	
	set { arg label, argAction, initString;
		buttonView.states = ([ [label ? "Open new file", TXColor.white, TXColor.sysGuiCol1] ]);
		action = argAction;
		initString = initString ? "";
		this.string_(initString);
	}
}

