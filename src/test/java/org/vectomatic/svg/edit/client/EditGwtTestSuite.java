/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client;

import junit.framework.Test;

import org.vectomatic.svg.edit.client.command.GenericRemoveCommandGwtTest;
import org.vectomatic.svg.edit.client.command.dnd.CloneCommandGwtTest;
import org.vectomatic.svg.edit.client.command.dnd.ReorderCommandGwtTest;
import org.vectomatic.svg.edit.client.engine.SVGModelGwtTest;
import org.vectomatic.svg.edit.client.engine.SVGProcessorGwtTest;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModelGwtTest;

import com.google.gwt.junit.tools.GWTTestSuite;

public class EditGwtTestSuite {
	public static Test suite() {
		GWTTestSuite suite = new GWTTestSuite();
		suite.addTestSuite(SVGModelGwtTest.class);
		suite.addTestSuite(SVGProcessorGwtTest.class);
		suite.addTestSuite(SVGElementModelGwtTest.class);
		suite.addTestSuite(GenericRemoveCommandGwtTest.class);
		suite.addTestSuite(ReorderCommandGwtTest.class);
		suite.addTestSuite(CloneCommandGwtTest.class);
		return suite;
	}
}
