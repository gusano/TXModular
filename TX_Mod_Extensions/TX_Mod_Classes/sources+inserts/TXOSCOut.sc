// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).


TXOSCOut : TXModuleBase {

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
	var	arrNetAddresses;

*initClass{
	arrInstances = [];		
	//	set class specific variables
	defaultName = "OSC Out";
	moduleRate = "control";
	moduleType = "action";
	noInChannels = 0;
	noOutChannels = 0;
} 

*new{ arg argInstName;
	 ^super.new.init(argInstName);
} 

*addressesCopyAll { arg argData;
	arrInstances.do({arg module, i; 
		module.loadArrAddressData(argData);
	}); 
}

init {arg argInstName;
	//	set  class specific instance variables
	displayOption = "showMessage";
	arrSynthArgSpecs = [
		["OSCString", "/example/text"],
		["numArgs", 0],
		["latency", 0.0],
		
		["address1", "0.0.0.0"],
		["port1", 57120],
		["notes1", ""],
		["activate1", 0],
		["address2", "0.0.0.0"],
		["port2", 57120],
		["notes2", ""],
		["activate2", 0],
		["address3", "0.0.0.0"],
		["port3", 57120],
		["notes3", ""],
		["activate3", 0],
		["address4", "0.0.0.0"],
		["port4", 57120],
		["notes4", ""],
		["activate4", 0],
		["address5", "0.0.0.0"],
		["port5", 57120],
		["notes5", ""],
		["activate5", 0],
		["address6", "0.0.0.0"],
		["port6", 57120],
		["notes6", ""],
		["activate6", 0],
		["address7", "0.0.0.0"],
		["port7", 57120],
		["notes7", ""],
		["activate7", 0],
		["address8", "0.0.0.0"],
		["port8", 57120],
		["notes8", ""],
		["activate8", 0],
		["address9", "0.0.0.0"],
		["port9", 57120],
		["notes9", ""],
		["activate9", 0],
		["address10", "0.0.0.0"],
		["port10", 57120],
		["notes10", ""],
		["activate10", 0],
		
		["argType1", 0],
		["argNumVal1", 0],
		["argStringVal1", " "],
		["argType2", 0],
		["argNumVal2", 0],
		["argStringVal2", " "],
		["argType3", 0],
		["argNumVal3", 0],
		["argStringVal3", " "],
		["argType4", 0],
		["argNumVal4", 0],
		["argStringVal4", " "],
		["argType5", 0],
		["argNumVal5", 0],
		["argStringVal5", " "],
		["argType6", 0],
		["argNumVal6", 0],
		["argStringVal6", " "],
		["argType7", 0],
		["argNumVal7", 0],
		["argStringVal7", " "],
		["argType8", 0],
		["argNumVal8", 0],
		["argStringVal8", " "],
		["argType9", 0],
		["argNumVal9", 0],
		["argStringVal9", " "],
		["argType10", 0],
		["argNumVal10", 0],
		["argStringVal10", " "],
	]; 
	this.buildGuiSpecArray;
	arrActionSpecs = this.buildActionSpecs([
			["commandAction", "Send OSC message", {this.sendMessage}],
			["TXTextBox", "OSC String", "OSCString"],
			["EZNumber", "No. of args", ControlSpec(0, 10, 'lin', 1), "numArgs"],
			["EZNumber", "Latency", ControlSpec(0, 1), "latency"],
			
			["TXPopupAction", "Argument 1 type", ["Number", "String"], "argType1", nil, 140], 
			["EZNumber", "Argument 1 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal1"],
			["TXTextBox","Argument 1 string", "argStringVal1"],
			["TXPopupAction", "Argument 2 type", ["Number", "String"], "argType2", nil, 140], 
			["EZNumber", "Argument 2 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal2"],
			["TXTextBox","Argument 2 string", "argStringVal2"],
			["TXPopupAction", "Argument 3 type", ["Number", "String"], "argType3", nil, 140], 
			["EZNumber", "Argument 3 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal3"],
			["TXTextBox","Argument 3 string", "argStringVal3"],
			["TXPopupAction", "Argument 4 type", ["Number", "String"], "argType4", nil, 140], 
			["EZNumber", "Argument 4 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal4"],
			["TXTextBox","Argument 4 string", "argStringVal4"],
			["TXPopupAction", "Argument 5 type", ["Number", "String"], "argType5", nil, 140], 
			["EZNumber", "Argument 5 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal5"],
			["TXTextBox","Argument 5 string", "argStringVal5"],
			["TXPopupAction", "Argument 6 type", ["Number", "String"], "argType6", nil, 140], 
			["EZNumber", "Argument 6 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal6"],
			["TXTextBox","Argument 6 string", "argStringVal6"],
			["TXPopupAction", "Argument 7 type", ["Number", "String"], "argType7", nil, 140], 
			["EZNumber", "Argument 7 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal7"],
			["TXTextBox","Argument 7 string", "argStringVal7"],
			["TXPopupAction", "Argument 8 type", ["Number", "String"], "argType8", nil, 140], 
			["EZNumber", "Argument 8 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal8"],
			["TXTextBox","Argument 8 string", "argStringVal8"],
			["TXPopupAction", "Argument 9 type", ["Number", "String"], "argType9", nil, 140], 
			["EZNumber", "Argument 9 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal9"],
			["TXTextBox","Argument 9 string", "argStringVal9"],
			["TXPopupAction", "Argument 10 type", ["Number", "String"], "argType10", nil, 140], 
			["EZNumber", "Argument 10 number", ControlSpec(-10000000.0, 10000000.0), "argNumVal10"],
			["TXTextBox","Argument 10 string", "argStringVal10"],

			["TXNetAddress","Address 1", "address1", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 1", ControlSpec(0, 99999, 'lin', 1), "port1"],
			["TXTextBox","Notes 1", "notes1"],
			["TXCheckBox", "Activate", "activate1"], 
			["TXNetAddress","Address 2", "address2", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 2", ControlSpec(0, 99999, 'lin', 1), "port2"],
			["TXTextBox","Notes 2", "notes2"],
			["TXCheckBox", "Activate", "activate2"],
			["TXNetAddress","Address 3", "address3", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 3", ControlSpec(0, 99999, 'lin', 1), "port3"],
			["TXTextBox","Notes 3", "notes3"],
			["TXCheckBox", "Activate", "activate3"],
			["TXNetAddress","Address 4", "address4", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 4", ControlSpec(0, 99999, 'lin', 1), "port4"],
			["TXTextBox","Notes 4", "notes4"],
			["TXCheckBox", "Activate", "activate4"],
			["TXNetAddress","Address 5", "address5", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 5", ControlSpec(0, 99999, 'lin', 1), "port5"],
			["TXTextBox","Notes 5", "notes5"],
			["TXCheckBox", "Activate", "activate5"],
			["TXNetAddress","Address 6", "address6", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 6", ControlSpec(0, 99999, 'lin', 1), "port6"],
			["TXTextBox","Notes 6", "notes6"],
			["TXCheckBox", "Activate", "activate6"],
			["TXNetAddress","Address 7", "address7", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 7", ControlSpec(0, 99999, 'lin', 1), "port7"],
			["TXTextBox","Notes 7", "notes7"],
			["TXCheckBox", "Activate", "activate7"],
			["TXNetAddress","Address 8", "address8", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 8", ControlSpec(0, 99999, 'lin', 1), "port8"],
			["TXTextBox","Notes 8", "notes8"],
			["TXCheckBox", "Activate", "activate8"],
			["TXNetAddress","Address 9", "address9", {this.buildArrNetAddresses;}],
			["EZNumber", "Port 9", ControlSpec(0, 99999, 'lin', 1), "port9"],
			["TXTextBox","Notes 9", "notes9"],
			["TXCheckBox", "Activate", "activate9"],
			["TXNetAddress","Address 10", "address10, {this.buildArrNetAddresses;}"],
			["EZNumber", "Port 10", ControlSpec(0, 99999, 'lin', 1), "port10"],
			["TXTextBox","Notes 10", "notes10"],
			["TXCheckBox", "Activate", "activate10"],
			["commandAction", "Copy Addresses 1-10 to all OSC Out modules", 
			{this.copyAddsToAllOSCOuts}],
			["commandAction", "Copy Addresses 1-10 to all OSC Control Out modules", 
			{this.copyAddsToAllOSCControlOuts}],
	]);	
	//	use base class initialise 
	this.baseInit(this, argInstName);
}

buildGuiSpecArray {
	guiSpecArray = [
		["ActionButton", "Message & Args 1-4", {displayOption = "showMessage"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showMessage")], 
		["Spacer", 3], 
		["ActionButton", "Args 5-10", {displayOption = "showArgs"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showArgs")], 
		["Spacer", 3], 
		["ActionButton", "Addresses 1-4", {displayOption = "showAddresses"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showAddresses")], 
		["NextLine", 3], 
		["ActionButton", "Addresses 5-8", {displayOption = "showAddresses2"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showAddresses2")], 
		["Spacer", 3], 
		["ActionButton", "Addresses 9-10", {displayOption = "showAddresses3"; 
			this.buildGuiSpecArray; system.showView;}, 130, 
			TXColor.white, this.getButtonColour(displayOption == "showAddresses3")], 
		["DividingLine"], 
		["SpacerLine", 6], 
	];
	if (displayOption == "showMessage", {
		guiSpecArray = guiSpecArray ++[
			["ActionButton", "Send OSC message now", {this.sendMessage}, 250, TXColor.white, 
				TXColor.sysGuiCol2], 
			["SpacerLine", 4], 
			["TXTextBox", "OSCString", "OSCString", nil, 380],
			["SpacerLine", 4], 
			["EZNumber", "No. of args", ControlSpec(0, 10, 'lin', 1), "numArgs"],
			["SpacerLine", 4], 
			["EZNumber", "Latency", ControlSpec(0, 1), "latency"],
			["SpacerLine", 4], 
			["DividingLine"], 
			["SpacerLine", 4], 
			["TXNumOrString", "Argument 1", "argType1", "argNumVal1", "argStringVal1", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 2", "argType2", "argNumVal2", "argStringVal2", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 3", "argType3", "argNumVal3", "argStringVal3", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 4", "argType4", "argNumVal4", "argStringVal4", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
		];
	});
	if (displayOption == "showArgs", {
		guiSpecArray = guiSpecArray ++[
			["TXNumOrString", "Argument 5", "argType5", "argNumVal5", "argStringVal5", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 6", "argType6", "argNumVal6", "argStringVal6", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 7", "argType7", "argNumVal7", "argStringVal7", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 8", "argType8", "argNumVal8", "argStringVal8", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 9", "argType9", "argNumVal9", "argStringVal9", 
				ControlSpec(-999999, 999999)],
			["SpacerLine", 2], 
			["TXNumOrString", "Argument 10", "argType10", "argNumVal10", "argStringVal10", 
				ControlSpec(-999999, 999999)],
		];
	});
	if (displayOption == "showAddresses", {
		guiSpecArray = guiSpecArray ++[
			["TXNetAddress","Address 1", "address1", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 1", ControlSpec(0, 99999, 'lin', 1), "port1"],
			["ActionButton", "default port", {this.setSynthArgSpec("port1", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate1", {this.buildArrNetAddresses;}], 
			["NextLine"], 
			["TXTextBox","Notes", "notes1", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 2", "address2", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 2", ControlSpec(0, 99999, 'lin', 1), "port2"],
			["ActionButton", "default port", {this.setSynthArgSpec("port2", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate2", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes2", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 3", "address3", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 3", ControlSpec(0, 99999, 'lin', 1), "port3"],
			["ActionButton", "default port", {this.setSynthArgSpec("port3", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate3", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes3", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 4", "address4", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 4", ControlSpec(0, 99999, 'lin', 1), "port4"],
			["ActionButton", "default port", {this.setSynthArgSpec("port4", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate4", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes4", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["ActionButton", "Copy Addresses 1-10 to all OSC Out modules", 
				{this.copyAddsToAllOSCOuts}, 400],
			["SpacerLine", 2], 
			["ActionButton", "Copy Addresses 1-10 to all OSC Control Out modules", 
				{this.copyAddsToAllOSCControlOuts}, 400],
		];
	});
	if (displayOption == "showAddresses2", {
		guiSpecArray = guiSpecArray ++[
			["TXNetAddress","Address 5", "address5", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 5", ControlSpec(0, 99999, 'lin', 1), "port5"],
			["ActionButton", "default port", {this.setSynthArgSpec("port5", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate5", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes5", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 6", "address6", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 6", ControlSpec(0, 99999, 'lin', 1), "port6"],
			["ActionButton", "default port", {this.setSynthArgSpec("port6", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate6", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes6", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 7", "address7", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 7", ControlSpec(0, 99999, 'lin', 1), "port7"],
			["ActionButton", "default port", {this.setSynthArgSpec("port7", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate7", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes7", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 8", "address8", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 8", ControlSpec(0, 99999, 'lin', 1), "port8"],
			["ActionButton", "default port", {this.setSynthArgSpec("port8", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate8", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes8", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["ActionButton", "Copy Addresses 1-10 to all OSC Out modules", 
				{this.copyAddsToAllOSCOuts}, 400],
			["SpacerLine", 2], 
			["ActionButton", "Copy Addresses 1-10 to all OSC Control Out modules", 
				{this.copyAddsToAllOSCControlOuts}, 400],
		];
	});
	if (displayOption == "showAddresses3", {
		guiSpecArray = guiSpecArray ++[
			["TXNetAddress","Address 9", "address9", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 9", ControlSpec(0, 99999, 'lin', 1), "port9"],
			["ActionButton", "default port", {this.setSynthArgSpec("port9", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate9", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes9", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["TXNetAddress","Address 10", "address10", {this.buildArrNetAddresses;}, 380],
			["NextLine"], 
			["EZNumber", "Port 10", ControlSpec(0, 99999, 'lin', 1), "port10"],
			["ActionButton", "default port", {this.setSynthArgSpec("port10", 57120); system.flagGuiUpd;}, 
				100, TXColor.white, TXColor.sysGuiCol2], 
			["Spacer"], 
			["TXCheckBox", "Activate", "activate10", {this.buildArrNetAddresses;}],
			["NextLine"], 
			["TXTextBox","Notes", "notes10", nil, 380],
			["DividingLine"], 
			["SpacerLine", 2], 
			["ActionButton", "Copy Addresses 1-10 to all OSC Out modules", 
				{this.copyAddsToAllOSCOuts}, 400],
			["SpacerLine", 2], 
			["ActionButton", "Copy Addresses 1-10 to all OSC Control Out modules", 
				{this.copyAddsToAllOSCControlOuts}, 400],
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

buildArrNetAddresses{
	var holdAddress, holdPort;
	arrNetAddresses = [];
	10.do({arg item, i;
		if (this.getSynthArgSpec("activate" ++ (i+1).asString) == 1, {
			holdAddress = this.getSynthArgSpec("address" ++ (i+1).asString);
			holdPort = this.getSynthArgSpec("port" ++ (i+1).asString);
			arrNetAddresses = arrNetAddresses.add(NetAddr(holdAddress, holdPort));
		});
	});
}

sendMessage {
	var holdMessage, holdTimestamp, holdNumArgs, holdArg;
	// build message
	holdMessage = [this.getSynthArgSpec("OSCString")];
	holdNumArgs = this.getSynthArgSpec("numArgs");
	holdNumArgs.do({arg item, i;
		if (this.getSynthArgSpec("argType" ++ (i+1).asString) == 0, {
			holdArg = this.getSynthArgSpec("argStringVal" ++ (i+1).asString);
		});
		if (this.getSynthArgSpec("argType" ++ (i+1).asString) == 1, {
			holdArg = this.getSynthArgSpec("argNumVal" ++ (i+1).asString).asFloat;
		});
		if (this.getSynthArgSpec("argType" ++ (i+1).asString) == 2, {
			holdArg = this.getSynthArgSpec("argNumVal" ++ (i+1).asString).asInteger;
		});
		holdMessage = holdMessage.add(holdArg);
	});
	// get latency
	holdTimestamp = this.getSynthArgSpec("latency");
	// send message to addresses
	arrNetAddresses.do({arg item, i;
		item.sendBundle(holdTimestamp, holdMessage);
	});
}

copyAddsToAllOSCOuts {
	TXOSCOut.addressesCopyAll(this.arrAddressData);
}

copyAddsToAllOSCControlOuts {
	TXOSCControlOut.addressesCopyAll(this.arrAddressData);
}

arrAddressData {
	^[ "address1", "port1", "notes1", "activate1", "address2", "port2", "notes2", "activate2", "address3", "port3", "notes3", "activate3", "address4", "port4", "notes4", "activate4", "address5", "port5", "notes5", "activate5", "address6", "port6", "notes6", "activate6", "address7", "port7", "notes7", "activate7", "address8", "port8", "notes8", "activate8", "address9", "port9", "notes9", "activate9", "address10", "port10", "notes10", "activate10" ]
	.collect ({arg item, i; this.getSynthArgSpec(item);});
}

loadArrAddressData { arg argData;
	[ "address1", "port1", "notes1", "activate1", "address2", "port2", "notes2", "activate2", "address3", "port3", "notes3", "activate3", "address4", "port4", "notes4", "activate4", "address5", "port5", "notes5", "activate5", "address6", "port6", "notes6", "activate6", "address7", "port7", "notes7", "activate7", "address8", "port8", "notes8", "activate8", "address9", "port9", "notes9", "activate9", "address10", "port10", "notes10", "activate10" ].do ({arg item, i; this.setSynthArgSpec(item, argData[i]);});
	this.buildArrNetAddresses;
}

loadExtraData {arg argData;  // override default method
	this.buildArrNetAddresses;
}

runAction {}   //	override base class

pauseAction {}   //	override base class

}

