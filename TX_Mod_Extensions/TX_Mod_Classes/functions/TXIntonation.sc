// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXIntonation {		// Intonation module 

classvar arrScales;	
classvar <arrScalesText;	
classvar noteFunc;	

*initClass{
	//	
	// set class specific variables
	//	
	// these scales are just intonation
	arrScales = [		
		[1, 16/15, 9/8, 6/5, 5/4, 4/3, 7/5, 3/2, 8/5, 5/3, 16/9, 15/8],
		[1, 16/15, 9/8, 6/5, 5/4, 4/3, 7/5, 3/2, 8/5, 5/3, 7/4, 15/8], 
		[1, 16/15, 9/8, 7/6, 5/4, 4/3, 7/5, 3/2, 14/9, 5/3, 7/4, 15/8],
		[1, 16/15, 9/8, 6/5, 9/7, 4/3, 7/5, 3/2, 8/5, 5/3, 9/5, 15/8],
		[1, 16/15, 8/7, 6/5, 9/7, 4/3, 10/7, 3/2, 8/5, 12/7, 16/9, 15/8],
		[1, 16/15, 10/9, 6/5, 5/4, 4/3, 7/5, 3/2, 14/9, 5/3, 16/9, 15/8],
		[1, 16/15, 9/8, 6/5, 5/4, 4/3, 11/8, 3/2, 8/5, 5/3, 7/4, 15/8], 
	];
	arrScalesText = [		
		"Semitones spaced equally (using 12th root of 2)",
		"1 16/15 9/8 6/5 5/4 4/3 7/5 3/2 8/5 5/3 16/9 15/8",
		"1 16/15 9/8 6/5 5/4 4/3 7/5 3/2 8/5 5/3 7/4 15/8", 
		"1 16/15 9/8 7/6 5/4 4/3 7/5 3/2 14/9 5/3 7/4 15/8",
		"1 16/15 9/8 6/5 9/7 4/3 7/5 3/2 8/5 5/3 9/5 15/8",
		"1 16/15 8/7 6/5 9/7 4/3 10/7 3/2 8/5 12/7 16/9 15/8",
		"1 16/15 10/9 6/5 5/4 4/3 7/5 3/2 14/9 5/3 16/9 15/8",
		"1 16/15 9/8 6/5 5/4 4/3 11/8 3/2 8/5 5/3 16/9 15/8",
	];
	noteFunc = {arg note, key, scaleNo; 
		var fundNote, scaleNote;     
		//	scale = array of 12 fractions (corresponding to semitones)
		//	key = 0-11, is root note of scale
		// fundNote is first note of scale in relevent octave
		fundNote = (12 * floor((note - key)/12)) + key;
		// scaleNote is no. of notes along from first note in scale
		scaleNote = (note - fundNote);
		// output frequency
		fundNote.midicps * Select.kr(scaleNote, arrScales.at(scaleNo));
	}
} 

*arrOptionData {
	^ [
		["Equal temperament", 
			{arg note, key; note.midicps; }
		],
		["Just intonation - set 1", 
			{arg note, key; noteFunc.value(note, key, 0); }
		],
		["Just intonation - set 2", 
			{arg note, key; noteFunc.value(note, key, 1); }
		],
		["Just intonation - set 3", 
			{arg note, key; noteFunc.value(note, key, 2); }
		],
		["Just intonation - set 4", 
			{arg note, key; noteFunc.value(note, key, 3); }
		],
		["Just intonation - set 5", 
			{arg note, key; noteFunc.value(note, key, 4); }
		],
		["Just intonation - set 6", 
			{arg note, key; noteFunc.value(note, key, 5); }
		],
		["Just intonation - set 7", 
			{arg note, key; noteFunc.value(note, key, 6); }
		],
	];
}

}


