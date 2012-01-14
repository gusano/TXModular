// Copyright (C) 2011  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

// a very simpe class to display a multiline text but not allow editing
// useful as display-only replacement for TextField that still responds to string in a similar way

TXDisplayTextField : TXDisplayNumBox {
}

/*

TXDisplayTextField {
	var <>textView, <string, fnt, fillColor, background;
	
	*new { arg window, dimensions;
		^super.new.init(window, dimensions);
	}
	init { arg window, dimensions;

// testing
	var v;
	v=UserView(window, dimensions);
	v.background_(Color.rand);
	v.drawFunc={|uview|
		Pen.stringInRect( "a matrix test", uview.bounds);
	};
//Pen.fillColor_(TXColour.yellow);
//Pen.color = TXColour.yellow;
//Pen.font = Font( "Helvetica-Bold", 12 );



		textView = UserView(window, dimensions);
		textView.background_(background ? Color.green);
		textView.drawFunc = {|uview|


//			Pen.fillColor_(fillColor ? Color.orange);
//			Pen.font_(fnt ? Font.defaultSerifFace);
//			Pen.stringInRect( string, uview.bounds.insetBy(4));
			Pen.stringInRect( "xxxx yyyyy zzzzzzzz", uview.bounds.insetBy(4));

//	// testing
			uview.bounds.insetBy(4).postln;
			// set the Color
			Pen.fillColor = Color.red;
			Pen.moveTo(22@4);
			Pen.lineTo(3@20);
			Pen.lineTo(20@0);
			Pen.fill

		};



	}
	string_ { arg argstring; 
		string = argstring;
		textView.refresh;
	}	
	action{
		// dummy method
	}	
	action_{
		// dummy method
	}	

	stringColor {
		^fillColor;
	}
	stringColor_ { arg color;
		fillColor = color;
		textView.refresh;
	}

	font{
		^fnt;
	}
	font_{arg f;
		fnt = f;
		textView.refresh;
	}

	background_ {arg bg;
		background = bg;
		textView.background_(background);
	}
	background {
		^background;
	}

	visible { ^textView.visible }
	visible_ { |bool| textView.visible_(bool) }
	
	enabled {  ^textView.enabled } 
	enabled_ { |bool| textView.enabled_(bool) }
	
	canFocus {
		^false;
	} 
	canFocus_ {
		// dummy method
	}
	
	
	
	remove { textView.remove }
}

*/
