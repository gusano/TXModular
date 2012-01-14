// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

// These scales are mostly taken from the class Scale.sc by Lance Putnam

TXScale {		

classvar	arrScalesSpecs;	// version of the system

*initClass{	

	arrScalesSpecs = [
		// intervals
		["interval: 1 semitones - minor 2nd", [1, 11]],
		["interval: 2 semitones -  major 2nd", [2, 10]],
		["interval: 3 semitones -  minor 3rd", [3, 9]],
		["interval: 4 semitones -  major 3rd", [4, 8]],
		["interval: 5 semitones -  perfect 4th", [5, 7]],
		["interval: 6 semitones -  tritone", [6, 6]],
		["interval: 7 semitones -  perfect 5th", [7, 5]],
		["interval: 8 semitones -  minor 6th", [8, 4]],
		["interval: 9 semitones -  major 6th", [9, 3]],
		["interval: 10 semitones -  minor 7th", [10, 2]],
		["interval: 11 semitones -  major 7th", [11, 1]],
		["interval: 12 semitones -  octave", [12]],
		// chords
		["chord: major", [4,3,5]],
		["chord: dominant 7", [4,3,4,1]],
		["chord: major 7", [4,3,3,2]],
		["chord: minor", [3,4,5]], 
		["chord: minor 7", [3,4,3,2]], 
		["chord: minor major 7", [3,4,4,1]], 
		["chord: minor 7 b5", [3,3,4,2]], 
		["chord: diminished", [3,3,3,3]], 
		
		// modes
		["mode: ionian", [2,2,1,2,2,2,1]],
		["mode: dorian", [2,1,2,2,2,1,2]],
		["mode: phrygian", [1,2,2,2,1,2,2]],
		["mode: lydian", [2,2,2,1,2,2,1]],
		["mode: mixolydian", [2,2,1,2,2,1,2]],
		["mode: aeolian", [2,1,2,2,1,2,2]],
		["mode: locrian", [1,2,2,1,2,2,2]],
		["mode: ionian 5", [2,2,3,2,3]],
		["mode: dorian 5", [2,1,4,2,3]],
		["mode: phrygian 5", [1,2,4,1,4]],
		["mode: lydian 5", [2,2,3,2,3]],
		["mode: mixolydian 5", [2,2,3,2,3]],
		["mode: aeolian 5", [2,1,4,1,4]],
		["mode: locrian 5", [1,2,3,2,4]],
	
		// scales
		["scale: augmented", [3,1,2,1,3,1]],
		["scale: balinese 1", [2,2,3,2,3]],
		["scale: blues", [3,1,1,2,2,1,2]],
		["scale: chinese 1", [2,2,3,2,3]],
		["scale: chinese 2", [4,2,1,4,1]],
		["scale: chromatic", [1,1,1,1,1,1,1,1,1,1,1,1]],
		["scale: diminished", [2,1,2,1,2,1,2,1]],
		["scale: enigmatic", [1,3,2,2,2,1,1]],
		["scale: double harmonic", [1,3,1,2,1,3,1]],
		["scale: ethiopian 1", [2,2,1,2,1,3,1]],
		["scale: flamenco", [1,2,1,1,2,1,2,2]],
		["scale: egyptian", [2,3,2,3,2]],
		["scale: hindu", [2,2,1,2,1,2,2]],
		["scale: hirajoshi, japanese", [2,2,3,2,3]],  // japanese
		["scale: hungarian gypsy", [2,1,3,1,1,2,2]],
		["scale: hungarian major", [3,1,2,1,2,1,2]],
		["scale: hungarian minor", [2,1,3,1,1,3,1]],
		["scale: indian", [1,2,1,3,2,2]],
		["scale: iwato, japanese", [1,4,1,4,2]], // japanese
		["scale: japanese 1", [1,4,2,1,4]],
		["scale: javanese", [1,2,2,2,2,1,2]],
		["scale: locrian major", [2,2,1,1,2,2,2]],
		["scale: locrian natural 2", [2,1,2,1,2,2,2]],
		["scale: locrian super", [1,2,1,2,2,2,2]],
		["scale: locrian ultra", [1,2,1,2,2,1,3]],
		["scale: lydian minor", [2,2,2,1,1,2,2]],
		["scale: lydian dom", [2,2,2,1,2,1,2]],
		["scale: lydian aug", [2,2,2,2,1,1,2]],
		["scale: major", [2,2,1,2,2,2,1]],
		["scale: major 5", [2,2,3,2,3]],
		["scale: major harmonic", [2,2,1,3,1,2,1]],
		["scale: marva, indian", [1,3,2,1,2,2,1]],	// indian
		["scale: minor natural", [2,1,2,2,1,2,2]], // natural
		["scale: minor 5", [3,2,2,3,2]],
		["scale: minor harmonic", [2,1,2,2,1,3,1]],
		["scale: minor melodic up", [2,1,2,2,1,3,1]], 	// ascending
		["scale: minor melodic down", [2,1,2,2,1,2,2]], // descending
		["scale: mixolydian aug", [2,2,1,3,1,1,2]],
		["scale: neapolitan major", [1,2,2,2,2,2,1]],
		["scale: neapolitan minor", [1,2,2,2,1,3,1]],
		["scale: oriental", [1,3,1,1,3,1,2]],
		["scale: pelog, balinese", [1,2,4,3,2]], // balinese
		["scale: persian", [1,3,1,1,2,3,1]],
		["scale: phrygian major", [1,3,1,2,1,2,2]],
		["scale: romanian", [2,1,3,1,2,1,2]],
		["scale: semitone 3", [3,3,3,3]],
		["scale: semitone 4", [4,4,4]],
		["scale: spanish 8", [1,2,1,1,1,2,2,2]],
		["scale: symmetrical", [1,2,1,2,1,2,1,2]],
		["scale: todi, indian", [1,2,3,1,1,3,1]], // indian
		["scale: whole tone", [2,2,2,2,2,2]],
		["scale: whole tone leading", [2,2,2,2,2,1,1]],
	];
} 

*arrScaleNames {
	^arrScalesSpecs.collect({ arg item, i;
		item.at(0);
	});
}

*arrScaleNotes {
	^arrScalesSpecs.collect({ arg item, i;
		var arrNotes;
		// pop last item, add a leading zero to scale and integrate values
		arrNotes = item.at(1).deepCopy;
		arrNotes.pop;
		([0] ++ arrNotes).integrate;
	});
}

}

