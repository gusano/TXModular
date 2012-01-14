// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXViewHolder {		// class which extracts/loads data from any view 

// n.b. data gets passed to and from this class as an ARRAY. 
// this is different to myView.value which returns just the value.*getData { arg view; 
	// safety check
	if (view.isNil, { 
		^"Error: invalid argument"; 
	 });
	// return an array with all values in it

	if (view.class == TXMinMaxSlider, { 
		^view.valueSplit; 
	 });

	if (view.class == TXRangeSlider, { 
		^view.valueBoth; 
	 });

	// HERE ADD IF STATEMENTS TO CHECK FOR OTHER VIEWS, which require more than one value
	//  INCLUDING check?: SCTextField?, SC2DSlider?, SCMultiSliderView?

	// default is to return array of 1 item:  view..value
	^[view.value]; 
}

*setData { arg view, arrData; 
	// safety check
	if (view.isNil or: arrData.isNil, { 
		^"Error: invalid argument"; 
	 });

	if (view.class == TXMinMaxSlider, { 
		^view.valueSplitAction_(arrData); 
	 });

	if (view.class == TXRangeSlider, { 
		^view.valueBoth_(arrData); 
	 });

	// HERE ADD IF STATEMENTS TO CHECK FOR OTHER VIEWS, which require more than one value
	//  INCLUDING: SCTextField, SC2DSlider, SCMultiSliderView
	
	// default is to use .value method on 1st item in array
	^view.value_(arrData.at(0)); 
 }
}
