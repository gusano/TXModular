// Copyright (C) 2008  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

/* e.g. use:
	TXAudioDevices.post;
	TXAudioDevices.devices;
*/

TXAudioDevices {
classvar <devices;

*initClass {
}

*scan {
	var pipe, line, n, x;

	if (GUI.current.asSymbol !== \SwingGUI, {
		devices = List.new;
		pipe = Pipe.new("./scsynth -u 57119 ", "r");
	
		line = pipe.getLine;
		while({line.contains("Number of Devices:").not Ê}, { line = pipe.getLine;});
		n = line.split($:)[1].asInteger;
		n.do ({ 	
			line = pipe.getLine; 
			x = line.split($:)[1];
			devices.add(x.copyRange(2, x.size - 2) ) 
		});
		pipe.close;
	
		devices = devices.asArray;
		this.post;
	});
}

*post { 
	"--------------------------------------".postln;
	("found" + devices.size + "audio devices:").postln;
	devices.do { arg d, i; ("\t" + i + ":" + "\""++d++"\"").postln; };
	"--------------------------------------".postln;
}
} 