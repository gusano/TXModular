// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXModGui {	  

classvar	<>system;	    			// parent system - set by parent
classvar <>displayModule; 		// shows current module who's gui will be shown 
classvar popNewModuleInd;
classvar column0, column1, column2, column3, column4;

////////////////////////////////////////////////////////////////////////////////////

*initClass{
	displayModule = nil;
	popNewModuleInd = 0;
} 

*makeGui{ arg parent;
	var arrAllPossSourceActClasses, arrAllPossSourceActNames, arrAllSourceActModules, arrAllSourceActModNames;
	var arrAllDisplayModules, arrAllDisplayModNames;
	var popNewModule, btnAddModule, popDisplayMod, btnHideModule;

	// create array of names of all possible modules. 
	arrAllPossSourceActClasses = system.arrAllPossModules
		.select({ arg item, i; 
			(item.moduleType == "source") 
				or: (item.moduleType == "groupsource") 
				or: (item.moduleType == "action"); 
		});  // only show source, groupsource or action modules

	arrAllPossSourceActNames = arrAllPossSourceActClasses.collect({ arg item, i; item.defaultName; });
	// create array of names of all modules. 
	arrAllSourceActModules = system.arrSystemModules
		.select({ arg item, i;
			arrAllPossSourceActClasses.indexOf(item.class).notNil;
		 })
		.sort({ arg a, b; 
			a.instName < b.instName 
		});
	arrAllSourceActModNames = arrAllSourceActModules.collect({arg item, i;  item.instName; });
	// create array of names of all Display modules. 
	arrAllDisplayModules = 	system.arrSystemModules
		.select({ arg item, i; 
			(item.class.moduleType == "source") 
			or: (item.class.moduleType == "groupsource") 
			or: (item.class.moduleType == "insert")    
			or: (item.class.moduleType == "action");
		 })
		.sort({ arg a, b; 
			a.instName < b.instName 
		});
	arrAllDisplayModNames = arrAllDisplayModules.collect({arg item, i;  item.instName; });
	// popup - display module selector  
	popDisplayMod = PopUpMenu(parent, 250 @ 24)
		.background_(TXColor.white.blend(TXColor.sysGuiCol2, 0.1)).stringColor_(TXColor.sysGuiCol1);
	popDisplayMod.items = ([ "All Modules "] 
		++ arrAllDisplayModNames).collect({ arg item, i; "Module Display: " ++ item;});
	popDisplayMod.action = {|view|
		// set variable 
		if (view.value == 0, {
			displayModule = nil;
		}, {
			displayModule = arrAllDisplayModules.at(view.value-1)
		});
		// update view
		system.showView;
	};
	popDisplayMod.value = ((arrAllDisplayModules.indexOf(displayModule) ? -1) + 1);

	if (displayModule.notNil, {
		// button - hide module 
		btnHideModule = Button(parent, 100 @ 24);
		btnHideModule.states = [["Hide Module", TXColor.white, TXColor.sysGuiCol1]];
		btnHideModule.action = {
			// reset variable 
			displayModule = nil;	
			// update view
			system.showView;
		};
	}, {
		// spacer 
		StaticText(parent, 100 @ 24);
	});

	// spacer 
	StaticText(parent, 50 @ 20);

	// popup - new module  
	popNewModule = PopUpMenu(parent, 250 @ 24).background_(TXColor.white).stringColor_(TXColor.sysGuiCol1);
	popNewModule.items = ["Select type of module before adding ..."] ++ arrAllPossSourceActNames;
	popNewModule.action = {|view|
		// store current data 
		popNewModuleInd = view.value;
	};
	popNewModule.value = popNewModuleInd ? 0;

	// button - Add new Source module	  
	btnAddModule = Button(parent, 200 @ 24);
	btnAddModule.states = [["<- Add new module of this type", TXColor.white, TXColor.sysGuiCol1]];
	btnAddModule.action = {
		var newModuleClass, newModule;
		// first item has no effect
		if (popNewModule.value > 0, {
		// set new module class
			newModuleClass = arrAllPossSourceActClasses.at(popNewModule.value - 1);
			// ask system to add new module
			newModule = system.addModule(newModuleClass);
			// reset variable 
			displayModule = newModule;	
			// update view
			system.showView;
		});
	};

	// spacer line	
	parent.decorator.nextLine;
	// display module gui or all modules
	if (displayModule.notNil, {
		displayModule.openGui(parent);
	},{
		this.guiViewAllModules(parent);
	});
	parent.refresh;
} 

*guiViewAllModules { arg parent;
	// spacing
	parent.decorator.shift(0, 5);
	// create columns with titles
	column0 =  CompositeView(parent,Rect(0,0,200,500));
	column0.decorator = FlowLayout(column0.bounds);
	StaticText(column0, 160 @ 24).string_("Audio Sources").align_(\center)
		.background_(TXColor.grey6).stringColor_(TXColor.sysGuiCol1);

	column1 =  CompositeView(parent,Rect(0,0,200,500));
	column1.decorator = FlowLayout(column1.bounds);
	StaticText(column1, 160 @ 24).string_("Audio Inserts").align_(\center)
		.background_(TXColor.grey6).stringColor_(TXColor.sysGuiCol1);

	column2 =  CompositeView(parent,Rect(0,0,200,500));
	column2.decorator = FlowLayout(column2.bounds);
	StaticText(column2, 160 @ 24).string_("Control Sources").align_(\center)
		.background_(TXColor.grey6).stringColor_(TXColor.sysGuiCol1);

	column3 =  CompositeView(parent,Rect(0,0,200,500));
	column3.decorator = FlowLayout(column3.bounds);
	StaticText(column3, 160 @ 24).string_("Control Inserts").align_(\center)
		.background_(TXColor.grey6).stringColor_(TXColor.sysGuiCol1);

	column4 =  CompositeView(parent,Rect(0,0,200,500));
	column4.decorator = FlowLayout(column4.bounds);
	StaticText(column4, 160 @ 24).string_("Action Modules").align_(\center)
		.background_(TXColor.grey6).stringColor_(TXColor.sysGuiCol1);

	// display all modules
	system.arrSystemModules.do({ arg item, i;
		this.addModToColumn(item, this.getColumn(item.class), i);
	});
}

*getColumn {arg moduleClass;
	var retColumn = 99;
	if ( (moduleClass.moduleRate == "audio") and: (moduleClass.moduleType == "source"), {retColumn = 0});
	if ( (moduleClass.moduleRate == "audio") and: (moduleClass.moduleType == "groupsource"), {retColumn = 0});
	if ( (moduleClass.moduleRate == "audio") and: (moduleClass.moduleType == "insert"), {retColumn = 1});
	if ( (moduleClass.moduleRate == "control") and: (moduleClass.moduleType == "source"), {retColumn = 2});
	if ( (moduleClass.moduleRate == "control") and: (moduleClass.moduleType == "groupsource"), {retColumn = 2});
	if ( (moduleClass.moduleRate == "control") and: (moduleClass.moduleType == "insert"), {retColumn = 3});
	if ( (moduleClass.moduleRate == "control") and: (moduleClass.moduleType == "action"), {retColumn = 4});
	^retColumn;
}

*addModToColumn {arg module, moduleIndex;
	var column, columnNo, btnModEdit, btnModDel;
	columnNo = this.getColumn(module.class);
	column = [column0, column1, column2, column3, column4].at(columnNo);
	column.decorator.nextLine;
	btnModEdit = Button(column, 140 @ 20);
	btnModEdit.states = [[module.instName, TXColor.white, TXColor.sysGuiCol1]];
	btnModEdit.action = {
		displayModule = module;
		// update view
		system.showView;
	};
	btnModDel = Button(column, 40 @ 20);
	btnModDel.states = [["Delete", TXColor.white, TXColor.sysDeleteCol]];
	btnModDel.action = {module.confirmDeleteModule};
}

*checkDeletions {
	// reset variable 
	displayModule = nil;	
}

}


