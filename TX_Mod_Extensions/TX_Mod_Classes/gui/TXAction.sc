// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXAction {

	var <actionType, <>actionName;
	var <>actionFunction, <>initActionFunc, <>getValueFunction, <>setValueFunction;
	var <>arrControlSpecFuncs, <>guiObjectType, <>getItemsFunction, <>arrWidgetIDs;
	var <>legacyType = 0;
	// Note: legacyType is a variable that is used as a fix for systems saved in version 0.10.6,   
	//    where action text was not saved for widgets. This also affects TXWidget 
	//    & TXBuildActions (set in TXBuildActions)

	*initClass{
		// initialise class variables
	}
	*new { arg argActionType, argActionName, argActionFunction, argGuiObjectType = \number;
		^super.new.init(argActionType, argActionName, argActionFunction, argGuiObjectType);
	}
	init { arg argActionType, argActionName, argActionFunction, argGuiObjectType;
		//	assign variables
		actionType = argActionType;
		actionName = argActionName;
		actionFunction = argActionFunction;
		guiObjectType = argGuiObjectType;
	}	

}

