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

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.itf.ISVGAnimatedPoints;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * SVG point list value type
 * @author laaglu
 */
public class SVGPoints {
	/**
	 * A polygon to store the value of the points
	 */
	private SVGElement value;
	public SVGPoints(SVGElement element) {
		value = element.cloneNode(false).cast();
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof SVGPoints) {
			return value.getAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE).equals(((SVGPoints)o).value.getAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE));
		}
		return false;
	}
	@Override
	public int hashCode() {
		return value.getAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE).hashCode();
	}
	@Override
	public String toString() {
		return value.getAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE);
	}
	public OMSVGPointList getValue() {
		return ((ISVGAnimatedPoints)OMNode.convert(value)).getPoints();
	}
}
