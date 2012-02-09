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
package org.vectomatic.svg.edit.client.model.svg;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.IValidator;
import org.vectomatic.svg.edit.client.model.MetadataBase;

/**
 * Metadata svg points type.
 * @author laaglu
 */
public class SVGPointsMetadata extends MetadataBase<SVGPoints, SVGElement> {
	public SVGPointsMetadata(String propertyName, String description, IFactoryInstantiator<?> factory, IValidator<SVGPoints, SVGElement> validator) {
		super(propertyName, description, null, factory, validator);
	}

	@Override
	public SVGPoints get(SVGElement element) {
		return new SVGPoints(element);
	}

	@Override
	public SVGPoints set(SVGElement element, SVGPoints value) {
		SVGPoints oldValue = new SVGPoints(element);
		SVGModel svgModel = SvgrealApp.getApp().getWindow(element).getSvgModel();
		SVGAnimatedPointsModelBase model = (SVGAnimatedPointsModelBase) svgModel.convert(element);
		if (element == model.getElement()) {
			// This method is called twice (once for the element and once for the twin)
			// Do the update only once, for both the twin and the store
			model.getPointsStore().update(value);
		}
		return oldValue;
	}

	@Override
	public SVGPoints remove(SVGElement element) {
		throw new IllegalStateException("Property " + propertyName + " cannot be removed");
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGPointsMetadata(");
		builder.append(propertyName);
		builder.append(")");
		return builder.toString();
	}
}
