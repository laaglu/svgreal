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
package org.vectomatic.svg.edit.client.engine;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGEllipseElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.svg.edit.client.EditTestCase;

public class SVGProcessorGwtTest extends EditTestCase {
	public void testNormalizeIds() {
		// TODO
	}
	public void testHasGraphicalElements() {
		// TODO
	}
	
	public void testReparent() {
		String data = "<svg xmlns='http://www.w3.org/2000/svg'>" +
		"<rect/>" +
		"<ellipse/>" +
		"</svg>";
		OMSVGSVGElement svg = OMSVGParser.parse(data);
		OMSVGGElement g = new OMSVGGElement();
		SVGProcessor.reparent(svg, g);
		
		OMNodeList<OMNode> svgNodes = svg.getChildNodes();
		assertEquals(0, svgNodes.getLength());
		OMNodeList<OMNode> gNodes = g.getChildNodes();
		assertEquals(2, gNodes.getLength());
		assertEquals(OMSVGRectElement.class, gNodes.getItem(0).getClass());
		assertEquals(OMSVGEllipseElement.class, gNodes.getItem(1).getClass());
	}

}
