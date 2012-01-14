// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXNetAddress {
	classvar ipAddressClipboard, ipAddressClipboard2, ipAddressClipboard3, ipAddressClipboard4,
		ipAddressClipboard5;
	var <>labelView, <>numberView1, <>numberView2, <>numberView3, <>numberView4;
	var arrPresets, <>presetPopupView;
	var <>action, <string, controlSpec;
	
	*new { arg window, dimensions, label, action, initVal, 
			initAction=false, labelWidth=80, textWidth = 250;
		^super.new.init(window, dimensions, label, action, initVal, 
			initAction, labelWidth, textWidth);
	}
	init { arg window, dimensions, label, argAction, initVal, 
			initAction, labelWidth, textWidth;
		labelView = StaticText(window, labelWidth @ dimensions.y);
		labelView.string = label;
		labelView.align = \right;
		
		if (initVal == "", {initVal = "0.0.0.0"});
		if (initVal == " ", {initVal = "0.0.0.0"});
		if (initVal.isNil, {initVal = "0.0.0.0"});
		action = argAction;
		controlSpec = ControlSpec(0, 255, step:1);
		
		numberView1 = TXScrollNumBox(window, 50 @ dimensions.y);
		numberView1.action = {
			numberView1.value = controlSpec.constrain(numberView1.value);
			this.makeString;
			action.value(this);
		};
		StaticText(window, 10 @ dimensions.y).string_(" .");
		numberView2 = TXScrollNumBox(window, 50 @ dimensions.y);
		numberView2.action = {
			numberView2.value = controlSpec.constrain(numberView2.value);
			this.makeString;
			action.value(this);
		};
		StaticText(window, 10 @ dimensions.y).string_(" .");
		numberView3 = TXScrollNumBox(window, 50 @ dimensions.y);
		numberView3.action = {
			numberView3.value = controlSpec.constrain(numberView3.value);
			this.makeString;
			action.value(this);
		};
		StaticText(window, 10 @ dimensions.y).string_(" .");
		numberView4 = TXScrollNumBox(window, 50 @ dimensions.y);
		numberView4.action = {
			numberView4.value = controlSpec.constrain(numberView4.value);
			this.makeString;
			action.value(this);
		};
		
		StaticText(window, 20 @ dimensions.y);
		arrPresets = [
			["Do... ", {}],
			["Reset address: 0.0.0.0 ", {this.string_("0.0.0.0");}],
			["Loopback address to this computer: 127.0.0.1 ", {this.string_("127.0.0.1");}],
			["Copy to IP address clipboard 1", {ipAddressClipboard = string;}],
			["Paste from IP address clipboard 1", 
				{if (ipAddressClipboard.notNil, {this.string_(ipAddressClipboard); }); }],
			["Copy to IP address clipboard 2", {ipAddressClipboard2 = string;}],
			["Paste from IP address clipboard 2", 
				{if (ipAddressClipboard.notNil, {this.string_(ipAddressClipboard2); }); }],
			["Copy to IP address clipboard 3", {ipAddressClipboard3 = string;}],
			["Paste from IP address clipboard 3", 
				{if (ipAddressClipboard.notNil, {this.string_(ipAddressClipboard3); }); }],
			["Copy to IP address clipboard 4", {ipAddressClipboard4 = string;}],
			["Paste from IP address clipboard 4", 
				{if (ipAddressClipboard.notNil, {this.string_(ipAddressClipboard4); }); }],
			["Copy to IP address clipboard 5", {ipAddressClipboard5 = string;}],
			["Paste from IP address clipboard 5", 
				{if (ipAddressClipboard.notNil, {this.string_(ipAddressClipboard5); }); }],
		];
		presetPopupView = PopUpMenu(window, 42 @ dimensions.y)
			.background_(Color.white)
			.items_(arrPresets.collect({arg item, i; item.at(0);}))
			.action_({ arg view;
				arrPresets.at(view.value).at(1).value;
				view.value = 0;
			});

		if (initAction) {
			this.string = initVal;
		}{
			string = initVal;
		};
		this.setNumberViews;
	}
	
	string_ { arg argString; 
		string = argString;
		this.setNumberViews;
		action.value(this);
	}
	
	makeString{
		string = numberView1.value.asString ++ "." ++ numberView2.value.asString 
			++ "." ++ numberView3.value.asString ++ "." ++ numberView4.value.asString;
	}
	
	setNumberViews{
		var arrNumbers;
		arrNumbers = string.split($.);
		numberView1.value = (arrNumbers.at(0) ? 0).asInteger;
		numberView2.value = (arrNumbers.at(1) ? 0).asInteger;
		numberView3.value = (arrNumbers.at(2) ? 0).asInteger;
		numberView4.value = (arrNumbers.at(3) ? 0).asInteger;
	}
}



