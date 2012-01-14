// Copyright (C) 2011  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXActionSlider : TXModuleBase {		//  

	classvar <>arrInstances;	
	classvar <defaultName;  		// default module name
	classvar <moduleRate;			// "audio" or "control"
	classvar <moduleType;			// "source", "insert", "bus",or  "channel"
	classvar <noInChannels;			// no of input channels 
	classvar <arrAudSCInBusSpecs; 	// audio side-chain input bus specs 
	classvar <>arrCtlSCInBusSpecs; 	// control side-chain input bus specs
	classvar <noOutChannels;		// no of output channels 
	classvar <arrOutBusSpecs; 		// output bus specs
	classvar	<guiWidth=500;

	var	displayOption;
	var	oscResponder;
	var  arrActions;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "Action Slider";
	moduleRate = "control";
	moduleType = "source";
	arrCtlSCInBusSpecs = [ 
		["Value", 1, "modSliderVal"],
		["Threshold", 1, "modThreshold", 0]
	];	
	noOutChannels = 1;
	arrOutBusSpecs = [ 
		["Out", [0]]
	];	
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

init {arg argInstName;
	var holdControlSpec;
	//	set  class specific instance variables
	displayOption = "showSlider";
	arrActions = [99,0,0,0,0,0,0, nil].dup(10);
	arrSynthArgSpecs = [
		["out", 0, 0],
		["sliderVal", 0, 0],
		["threshold", 0.5, 0],
		["trigHoldTime", 0.1, 0],
		["modSliderVal", 0, 0],
		["modThreshold", 0, 0],
	]; 
	arrOptions = [0];
	arrOptionData = [
		[
			["Upwards - trigger when slider goes above threshold", 
				{arg sliderValCombined, threshlevel, trigHoldTime; 
					Trig.kr((sliderValCombined - threshlevel).max(0), trigHoldTime); 
				}
			],
			["Downwards - trigger when slider goes below threshold", 
				{arg sliderValCombined, threshlevel, trigHoldTime; 
					Trig.kr( ( (1-sliderValCombined) - (1-threshlevel) ).max(0).min(1), trigHoldTime); 
				}
			],
			["Both ways - trigger whenever slider crosses threshold", 
				{arg sliderValCombined, threshlevel, trigHoldTime; 
					Trig.kr((sliderValCombined - threshlevel).max(0), trigHoldTime)
					+ Trig.kr( ( (1-sliderValCombined) - (1-threshlevel) ).max(0).min(1), trigHoldTime); 
				}
			],
		]
	];
	synthDefFunc = { 
		arg out, sliderVal, threshold, trigHoldTime, modSliderVal, modThreshold;
		var sliderValCombined, threshlevel, holdTrig, trigFunction;

		sliderValCombined = (sliderVal + modSliderVal).max(0).min(1);

		threshlevel = (threshold + modThreshold).max(0.001).min(1);
		trigFunction = this.getSynthOption(0);
		holdTrig = trigFunction.value(sliderValCombined, threshlevel, trigHoldTime);
		
		SendTrig.kr(holdTrig, 0, 1);

		Out.kr(out, sliderValCombined);
	};
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
		["SynthOptionPopup", "Trigger type", arrOptionData, 0], 
		["EZslider", "Value", ControlSpec(0, 1), "sliderVal"], 
		["EZslider", "Trig Threshold", ControlSpec(0, 1), "threshold"], 
		["EZslider", "Reload time", ControlSpec(0.01, 1), "trigHoldTime"], 
	]);
	//	use base class initialise 
	this.baseInit(this, argInstName);
	//	load the synthdef and create the synth
	this.loadAndMakeSynth;
	this.oscControlActivate;
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Slider", {displayOption = "showSlider"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showSlider")], 
		["Spacer", 3], 
		["ActionButton", "Actions 1-5", {displayOption = "showActions1-5"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showActions1-5")], 
		["Spacer", 3], 
		["ActionButton", "Actions 6-10", {displayOption = "showActions6-10"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showActions6-10")], 
		["Spacer", 3], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showSlider", {
		guiSpecArray = guiSpecArray ++[
			["EZslider", "Value", ControlSpec(0, 1), "sliderVal"], 
			["SpacerLine", 10], 
			["SynthOptionPopup", "Trigger type", arrOptionData, 0], 
			["SpacerLine", 2], 
			["EZslider", "Trig Threshold", ControlSpec(0, 1), "threshold"], 
			["SpacerLine", 10], 
			["EZslider", "Reload time", ControlSpec(0.01, 1), "trigHoldTime"], 
		];
	});
	if (displayOption == "showActions1-5", {
		guiSpecArray = guiSpecArray ++[
			["TXActionView", arrActions, 0], 
			["DividingLine"], 
			["TXActionView", arrActions, 1], 
			["DividingLine"], 
			["TXActionView", arrActions, 2], 
			["DividingLine"], 
			["TXActionView", arrActions, 3], 
			["DividingLine"], 
			["TXActionView", arrActions, 4], 
			["DividingLine"], 
		];
	});
	if (displayOption == "showActions6-10", {
		guiSpecArray = guiSpecArray ++[
			["TXActionView", arrActions, 5], 
			["DividingLine"], 
			["TXActionView", arrActions, 6], 
			["DividingLine"], 
			["TXActionView", arrActions, 7], 
			["DividingLine"], 
			["TXActionView", arrActions, 8], 
			["DividingLine"], 
			["TXActionView", arrActions, 9], 
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

extraSaveData {	
	^[arrActions];
	
}
loadExtraData {arg argData;  // override default method
	arrActions = argData.at(0); 
	{this.oscControlActivate;}.defer(2);
}

oscControlActivate { 	
	//	stop any previous responder 
	this.oscControlDeactivate;
	oscResponder = OSCresponderNode(nil, '/tr', { arg time, responder, msg;
		var holdNodeID;

//		For testing  - post details
//		"TXAudioTrigger : ".postln;
//		[time, responder, msg].postln;
	
		holdNodeID = msg.at(1);
		
		if (moduleNode.notNil, {
			if (holdNodeID == moduleNode.nodeID, {
				// run actions
				this.performActions;
			});
		});
	}).add;
}

oscControlDeactivate { 
	if (oscResponder.notNil, {
		oscResponder.remove;
	});
}

performActions {
	arrActions.do({ arg item, i;
		var holdModuleID, holdModule, holdActionInd, holdArrActionItems, holdActionText, 
			holdAction, holdVal1, holdVal2, holdVal3, holdVal4, actionArg1, actionArg2, actionArg3, actionArg4,
			holdIndex, holdItems;
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

