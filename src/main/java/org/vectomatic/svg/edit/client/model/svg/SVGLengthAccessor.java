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

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.model.IPropertyAccessor;

/**
 * Accessor subclass dedicated to properties of type SVG length.
 * @author laaglu
 */
public abstract class SVGLengthAccessor implements IPropertyAccessor<SVGLength, SVGElement> {
	public abstract OMSVGLength getLength(SVGElement element);
	
	@Override
	public SVGLength get(SVGElement element) {
		return new SVGLength(getLength(element));
	}

	@Override
	public void set(SVGElement element, SVGLength value) {
		OMSVGLength length = getLength(element);
//		GWT.log("L=" + length.getValueInSpecifiedUnits() + " " + length.getUnitType());
//		length.convertToSpecifiedUnits(value.getUnit());
//		GWT.log("L=" + length.getValueInSpecifiedUnits() + " " + length.getUnitType());
		length.newValueSpecifiedUnits(value.getUnit(), value.getValue());
	}
}
