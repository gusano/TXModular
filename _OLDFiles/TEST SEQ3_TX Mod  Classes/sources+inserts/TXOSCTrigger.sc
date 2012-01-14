// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).


// testing xxx:
//  	note - this module isn't tested yet 18/4/05


TXOSCTrigger : TXModuleBase {

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiHeight=200;
	classvar	<guiWidth=450;
	classvar	<guiLeft=100;
	classvar	<guiTop=300;
	classvar	defaultActionStep;
	classvar arrUseOSCArgNames;
	
	var	displayOption;
	var	oscControlRoutine;
	var	<>oscString;
	var  arrActions;
	var	oscResponder;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "OSC Trigger";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 0;
	noOutChannels = 0;
	arrUseOSCArgNames = ["useOSCArgs1", "useOSCArgs2", "useOSCArgs3", "useOSCArgs4", "useOSCArgs5", "useOSCArgs6", 
		"useOSCArgs7", "useOSCArgs8", "useOSCArgs9", "useOSCArgs10"];
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	//	set  class specific instance variables
	displayOption = "showOSCString";
	arrActions = [99,0,0,0,0,0,0, nil].dup(10);
	arrSynthArgSpecs = [
		["OSCString", "/example/text", 0],
		["useOSCArgs1", 0],
		["useOSCArgs2", 0],
		["useOSCArgs3", 0],
		["useOSCArgs4", 0],
		["useOSCArgs5", 0],
		["useOSCArgs6", 0],
		["useOSCArgs7", 0],
		["useOSCArgs8", 0],
		["useOSCArgs9", 0],
		["useOSCArgs10", 0],
	]; 
	synthDefFunc = { arg out, controller=0;
		var mixOut=0;
		Out.ar(out, mixOut);
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["OSCString"], 
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
	this.oscControlActivate;
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "OSC String", {displayOption = "showOSCString"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showOSCString")], 
		["Spacer", 3], 
		["ActionButton", "Actions 1-4", {displayOption = "showActions1-4"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showActions1-4")], 
		["Spacer", 3], 
		["ActionButton", "Actions 5-8", {displayOption = "showActions5-8"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showActions5-8")], 
		["Spacer", 3], 
		["ActionButton", "Actions 9-10", {displayOption = "showActions9-10"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showActions9-10")], 
		["Spacer", 3], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showOSCString", {
		guiSpecArray = guiSpecArray ++[
		["OSCString"], 
		["SpacerLine", 4], 
		["TXStaticText", "Please note:", 
			"The Network Port for receiving OSC messages is  " ++ NetAddr.langPort.asString],
		];
	});
	if (displayOption == "showActions1-4", {
		guiSpecArray = guiSpecArray ++[
		["TXActionView", arrActions, 0], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs1", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 1], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs2", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 2], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs3", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 3], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs4", nil, 300],
		["DividingLine"], 
		];
	});
	if (displayOption == "showActions5-8", {
		guiSpecArray = guiSpecArray ++[
		["TXActionView", arrActions, 4], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs5", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 5], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs6", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 6], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs7", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 7], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs8", nil, 300],
		["DividingLine"], 
		];
	});
	if (displayOption == "showActions9-10", {
		guiSpecArray = guiSpecArray ++[
		["TXActionView", arrActions, 8], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs9", nil, 300],
		["DividingLine"], 
		["TXActionView", arrActions, 9], 
		["NextLine"], 
		["TXCheckBox", "Use OSC arguments for value settings", "useOSCArgs10", nil, 300],
		["DividingLine"], 
		];
	});
}

getButtonColour { arg colour2Boolean;
	if (colour2Boolean == true, {
		^TXColor.sysGuiCol4;
	},{
		^TXColor.sysGuiCol1;
	});
}

runAction {this.oscControlActivate}   //	override base class

pauseAction {this.oscControlDeactivate}   //	override base class

extraSaveData {	
	^[oscString, arrActions];
	
}
loadExtraData {arg argData;  // override default method
	oscString = argData.at(0); 
	arrActions = argData.at(1); 
	this.oscControlActivate;
}

oscControlActivate { 
	//	stop any previous responder 
	this.oscControlDeactivate;
	oscResponder = OSCresponderNode(nil, oscString.asSymbol, { arg time, responder, msg;

//	For testing  - post details
//	"TXOSCController : ".postln;
//	[time, responder, msg].postln;

		// run actions
		this.performActions(msg);
		
	}).add;
}

oscControlDeactivate { 
	if (oscResponder.notNil, {
		oscResponder.remove;
	});
 }

deleteModuleExtraActions {     
	this.oscControlActivate;
}

rebuildSynth { 
	// override base class method
}

performActions { arg oscMsg;
	arrActions.do({ arg item, i;
		var holdModuleID, holdModule, holdActionInd, holdArrActionItems, holdActionText, 
			holdAction, holdVal1, holdVal2, holdVal3, holdVal4, actionArg1, actionArg2, actionArg3, actionArg4,
			oscArg1, oscArg2, oscArg3, oscArg4, holdIndex, holdItems;
		holdModuleID = item.at(0);
		holdActionInd = item.at(1);
		holdVal1 = item.at(2);
		holdVal2 = item.at(3);
		holdVal3 = item.at(4);
		holdVal4 = item.at(5);
		holdActionText = item.at(7);
		holdModule = system.getModuleFromID(holdModuleID);
		if (holdModule != 0, {
			holdArrActionItems = holdModule.arrActionSpecs.collect({arg item, i; item.actionName;});
			// if text found, match action string with text, else use numerical value
				if (holdActionText.notNil, {
					holdActionInd = holdArrActionItems.indexOfEqual(holdActionText) ? holdActionInd;
					holdAction = holdModule.arrActionSpecs.at(holdActionInd);
				},{
					// if text not found, use number but only select older actions with legacyType == 1
					holdAction = holdModule.arrActionSpecs.select({arg item, i; item.legacyType == 1}).at(holdActionInd);
				});

			actionArg1 = holdVal1;
			actionArg2 = holdVal2;
			actionArg3 = holdVal3;
			actionArg4 = holdVal4;
			oscArg1 = oscMsg.at(1);
			oscArg2 = oscMsg.at(2);
			oscArg3 = oscMsg.at(3);
			oscArg4 = oscMsg.at(4);
			
			// if using OSC arguments
			if (this.getSynthArgSpec(arrUseOSCArgNames.at(i)) == 1, {
				// if object type is number
				if (holdAction.guiObjectType == \number, {
					if (oscArg1.isNumber, {
						actionArg1 = holdAction.arrControlSpecFuncs.at(0).value.constrain(oscArg1);
					});
					if (oscArg2.isNumber, {
						actionArg2 = holdAction.arrControlSpecFuncs.at(1).value.constrain(oscArg2);
					});
					if (oscArg3.isNumber, {
						actionArg3 = holdAction.arrControlSpecFuncs.at(2).value.constrain(oscArg3);
					});
					if (oscArg4.isNumber, {
						actionArg4 = holdAction.arrControlSpecFuncs.at(3).value.constrain(oscArg4);
					});
				});
				// if object type is number
				holdItems = holdAction.getItemsFunction.value;
				if (holdAction.guiObjectType == \popup, {
					if (oscArg1.isNumber, {
						actionArg1 = ControlSpec(0, holdItems.size).constrain(oscArg1);
					});
					if (oscArg1.isSymbol, {
						holdIndex = holdItems.indexOfEqual(oscArg1.asString);
						if (holdIndex.notNil, {
							actionArg1 = holdIndex;
						});
					});
				});
				// if object type is checkbox
				if (holdAction.guiObjectType == \checkbox, {
					if (oscArg1.isNumber, {
						actionArg1 = ControlSpec(0, 1, step: 1).constrain(oscArg1);
					});
					if (oscArg1.isSymbol, {
						holdIndex = ["ON", "On", "on"].indexOfEqual(oscArg1.asString);
						if (holdIndex.notNil, {
							actionArg1 = 1;
						},{
							holdIndex = ["OFF", "Off", "off"].indexOfEqual(oscArg1.asString);
							if (holdIndex.notNil, {
								actionArg1 = 0;
							});
						});
					});
				});
				// if object type is textedit
				if (holdAction.guiObjectType == \textedit, {
					if (oscArg1.isSymbol, {
						actionArg1 = oscArg1.asString;
					});
				});
			});
			// if action type is commandAction then value it with arguments
			if (holdAction.actionType == \commandAction, {
				holdAction.actionFunction.value(actionArg1, actionArg2, actionArg3, actionArg4);
			});
			// if action type is valueAction then value it with arguments
			if (holdAction.actionType == \valueAction, {
				holdAction.setValueFunction.value(actionArg1, actionArg2, actionArg3, actionArg4);
			});
		});
	});
	//	gui update
//	system.flagGuiUpd;
}	// end of performActions

}

