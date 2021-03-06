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

package com.actelion.research.gui.form;

import java.util.ArrayList;

import javax.swing.JMenuItem;

public interface ResultDetailPopupItemProvider {
	/**
	 * Creates a list of popup items meant to extend a native popup menu of a
	 * JResultDetailView's rendering component. Items may be specific for a particular source,
	 * e.g. an item to launch an associated application.
	 * @param source
	 * @param reference
	 * @return
	 */
	public ArrayList<JMenuItem> getExternalPopupItems(String source, String reference);
	}
