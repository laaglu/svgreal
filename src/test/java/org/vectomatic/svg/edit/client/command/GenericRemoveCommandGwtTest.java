/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of svgreal.
 * 
 * svgreal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * svgreal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with svgreal.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.command;

import java.util.Arrays;

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.svg.edit.client.EditTestBase;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;


public class GenericRemoveCommandGwtTest extends EditTestBase {
	public void testCommand() {
		/*=========================================
		 root
		  a
		    (b)
		        c
		          (d)
		     e
		       (f)
		     g
		    (h)
		    (i)
		       (j)
		     k
		    (l)
		    (m)
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		owner.remove(owner.getViewBox()); // Remove viewBox to simplify test
		SVGElementModel a = owner.getRoot();
		SVGElementModel b = createElementModel(owner, "b");
		SVGElementModel c = createElementModel(owner, "c");
		SVGElementModel d = createElementModel(owner, "d");
		SVGElementModel e = createElementModel(owner, "e");
		SVGElementModel f = createElementModel(owner, "f");
		SVGElementModel g = createElementModel(owner, "g");
		SVGElementModel h = createElementModel(owner, "h");
		SVGElementModel i = createElementModel(owner, "i");
		SVGElementModel j = createElementModel(owner, "j");
		SVGElementModel k = createElementModel(owner, "k");
		SVGElementModel l = createElementModel(owner, "l");
		SVGElementModel m = createElementModel(owner, "m");
		createTree(owner, new SVGElementModel[][]{{a, b}, {a, e}, {a, g}, {a, h}, {a, i}, {a, k}, {a, l}, {a, m}, {b, c}, {c, d}, {e, f}, {i, j}});
		String dump = toString(dump(owner));
		
		GenericRemoveCommand command = new GenericRemoveCommand(RemoveElementsCommandFactory.INSTANTIATOR.create(), Arrays.<SVGElementModel>asList(new SVGElementModel[]{h, j, b, d, m, i, f, l}), "GenericRemoveCommandUnitTest");
		/*=========================================
		 root
		  a
		     e
		     g
		     k
		 =========================================*/
		command.commit();
		assertEquals("{{a, e}, {a, g}, {a, k}}", toString(dump(owner)));

		command.rollback();
		assertEquals(dump, toString(dump(owner)));
	}
}
