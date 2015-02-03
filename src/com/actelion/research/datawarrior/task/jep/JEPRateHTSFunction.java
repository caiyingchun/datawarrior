/*
 * Copyright 2014 Actelion Pharmaceuticals Ltd., Gewerbestrasse 16, CH-4123 Allschwil, Switzerland
 *
 * This file is part of DataWarrior.
 * 
 * DataWarrior is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * DataWarrior is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with DataWarrior.
 * If not, see http://www.gnu.org/licenses/.
 *
 * @author Thomas Sander
 */

package com.actelion.research.datawarrior.task.jep;

import java.util.*;
import org.nfunk.jep.*;
import org.nfunk.jep.function.*;
/**
 * to rate HTS results considering the molecular weight.
 */
public class JEPRateHTSFunction extends PostfixMathCommand {

	public JEPRateHTSFunction(CompoundTableModel tableModel) {
        mTableModel = tableModel;
		numberOfParameters = 3;
	    }
	/**
	public void run(Stack inStack) throws ParseException {
		// check the stack
		// get the parameters from the stack
		Object param3 = inStack.pop();
		Object param2 = inStack.pop();
		// check whether the argument is of the right type
    			// calculate the result
    			double effect = Math.min(99.0, Math.max(1.0, ra));
			// push the result on the inStack
		    }
			throw new ParseException("Invalid parameter type");
    }