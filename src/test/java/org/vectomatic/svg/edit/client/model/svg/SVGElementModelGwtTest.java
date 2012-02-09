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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.EditTestCase;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;

public class SVGElementModelGwtTest extends EditTestCase {
	public void testGetDepth() {
		/*=========================================
		 root
		  a
		   b
		    c
		     d
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		SVGElementModel a = owner.getRoot();
		SVGElementModel b = createElementModel(owner, "b");
		SVGElementModel c = createElementModel(owner, "c");
		SVGElementModel d = createElementModel(owner, "d");
		owner.insertBefore(a, b, null);
		owner.insertBefore(b, c, null);
		owner.insertBefore(c, d, null);
		assertEquals(0, a.getDepth());
		assertEquals(1, b.getDepth());
		assertEquals(2, c.getDepth());
		assertEquals(3, d.getDepth());
	}

	public void testGetAncestor() {
		/*=========================================
		 root
		  a
		   b
		    c
		     d
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		SVGElementModel a = owner.getRoot();
		SVGElementModel b = createElementModel(owner, "b");
		SVGElementModel c = createElementModel(owner, "c");
		SVGElementModel d = createElementModel(owner, "d");
		owner.insertBefore(a, b, null);
		owner.insertBefore(b, c, null);
		owner.insertBefore(c, d, null);
		assertEquals(a, a.getAncestor(0));
		assertNull(a.getAncestor(1));
		assertEquals(c, d.getAncestor(1));
		assertEquals(b, d.getAncestor(2));
		assertEquals(a, d.getAncestor(3));
	}

	
	public void testGetAscendingCompataror() {
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
		     l
		     m
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
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

		Comparator<SVGElementModel> comparator = SVGElementModel.getAscendingCompataror();
		assertEquals(1, comparator.compare(f, a));
		
		SVGElementModel[] nodes = {a, b, c, d, e, f, g, h, i, j, k, l, m};
		for (int ix1 = 0; ix1 < nodes.length; ix1++) {
			for (int ix2 = 0; ix2 < nodes.length; ix2++) {
				switch(comparator.compare(nodes[ix1], nodes[ix2])) {
					case -1:
						assertTrue("Failed to honor " + nodes[ix1] + " < " + nodes[ix2], nodes[ix1].<String>get(SVGConstants.SVG_TITLE_TAG).charAt(0) < nodes[ix2].<String>get(SVGConstants.SVG_TITLE_TAG).charAt(0));
						break;
					case 1:
						assertTrue("Failed to honor " + nodes[ix1] + " > " + nodes[ix2], nodes[ix1].<String>get(SVGConstants.SVG_TITLE_TAG).charAt(0) > nodes[ix2].<String>get(SVGConstants.SVG_TITLE_TAG).charAt(0));
						break;
					case 0:
						assertEquals(nodes[ix1], nodes[ix2]);
						break;
					default:
						fail();
						break;
				}
			}
		}
		
		List<SVGElementModel> l1 = new ArrayList<SVGElementModel>(Arrays.asList(new SVGElementModel[]{h, j, b, d, m, i, f, l}));
		List<SVGElementModel> l2 = new ArrayList<SVGElementModel>(Arrays.asList(new SVGElementModel[]{b, d, f, h, i, j, l, m}));
		Collections.<SVGElementModel>sort(l1, SVGElementModel.getAscendingCompataror());
		assertEquals(l2, l1);
	}
}
