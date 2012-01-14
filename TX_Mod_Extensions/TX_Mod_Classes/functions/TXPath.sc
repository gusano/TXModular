// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).
TXPath {		// TX Path - converts a path if required for autonomous systems

classvar <autonomousPath;    
	*initClass { 
		autonomousPath = TXSystem1.filenameSymbol.asString.dirname ++ "/" ++ "TXDefaultSystemData/";
	}
	*convert { arg inPath;
		if (inPath.notNil, {
			// if this is an autonomous standalone system, use autonomousPath for all files
			if (TXSystem1.showSystemControls == 1, {
				^inPath.standardizePath;
			},{
				^autonomousPath ++ inPath.basename;
			});
		});
	}
}

