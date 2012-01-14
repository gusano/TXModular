// Copyright (C) 2011  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).TXString {	// for string modifications

classvar <autonomousPath;    
	*initClass { 
		//
	}
	*removePopupSpecialCharacters { arg inString, replaceString = "_";
		var newString;
		newString = inString.replace("-", replaceString)
			.replace("<", replaceString)
			.replace("=", replaceString)
			.replace("(", replaceString);
		^newString;
	}
}

