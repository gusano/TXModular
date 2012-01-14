// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXStaticText {
	var <>labelView, <>textView, <string;
	
	*new { arg window, dimensions, label, initString, 
			labelWidth=80, textWidth;
		^super.new.init(window, dimensions, label, initString, 
			labelWidth, textWidth);
	}
	init { arg window, dimensions, label, initString, 
			labelWidth, textWidth;
		var spacingX;
		if (window.class == Window, {
			spacingX = window.view.decorator.gap.x;
		}, {
			spacingX = window.decorator.gap.x;
		});
		labelView = StaticText(window, labelWidth @ dimensions.y);
		labelView.string = label;
		labelView.align = \right;
		
		initString = initString ? " ";
		
		textView = StaticText(window, 
			(textWidth ? (dimensions.x - labelWidth - spacingX)) @ dimensions.y);
		
		this.string = initString;
	}
	string_ { arg argString; textView.string = string = argString;}
	isClosed { ^textView.isClosed }
	notClosed { ^textView.notClosed }
}



