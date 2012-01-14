// Copyright (C) 2005  Paul Miller. This file is part of TX Modular system distributed under the terms of the GNU General Public License (see file LICENSE).

TXLoadQueue {	 

	var <arrConditions;	

*new { 
	^super.new.init;
}

init{
	arrConditions = [];		
} 

addCondition { arg argCondition;
	 // if no condition passed, make new one
	 if (argCondition.isNil, {
	 	argCondition= Condition.new(false);
	 });
	 arrConditions = arrConditions.add(argCondition);
	// if this is the only condition, then set to true
	if (arrConditions.size == 1, {
	 	argCondition.test = true;
	 	argCondition.signal;
 	});
 	^argCondition;
} 

removeCondition { arg argCondition;
	 arrConditions.remove(argCondition);
	// set first condition to true
	if (arrConditions.size > 0, {
		arrConditions.first.test = true;
		arrConditions.first.signal;
 	});
} 

}

