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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.svg.edit.client.EditTestCase;
import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.google.gwt.user.client.ui.RootPanel;

public class CloneCommandGwtTest extends EditTestCase {
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
		     h
		    (i)
		        j
		     k  <----		        
		     l
		    (m)
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		RootPanel.get().add(new SVGImage(svg));
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
		Map<SVGElementModel, OMSVGMatrix> xforms = randomTreeTranform(owner);
		
		List<SVGElementModel> models = Arrays.<SVGElementModel>asList(new SVGElementModel[]{i, m, d, b, f});
		CloneCommand command = new CloneCommand(models, k, DropGesture.OnNode);
		/*=========================================
		 root
		  a
		     b 
		        c
		           d
		     e
		        f
		     g
		     h
		     i
		        j
		     k
		        Copy of i
		           j
		        Copy of m
		        Copy of d
		        Copy of b
		           c
		               d
		        Copy of f		        
		     l
		     m
		 =========================================*/
		command.commit();
		for (int ix = 0, size = models.size(); ix < size; ix++) {
			xforms.put(command.clones.get(ix), xforms.get(models.get(ix)));
		}
		assertEquals("{{a, b}, {a, e}, {a, g}, {a, h}, {a, i}, {a, k}, {a, l}, {a, m}, {b, c}, {e, f}, {i, j}, {k, Copy of i}, {k, Copy of m}, {k, Copy of d}, {k, Copy of b}, {k, Copy of f}, {c, d}, {Copy of i, j}, {Copy of b, c}, {c, d}}", toString(dump(owner)));
		
		assertTransformsUnchanged(xforms);

		command.rollback();
		assertEquals(dump, toString(dump(owner)));
	}
}
