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
import org.vectomatic.svg.edit.client.engine.SVGModel;

/**
 * Base model class for polygons and polyline.
 * @author laaglu
 */
public abstract class SVGAnimatedPointsModelBase extends SVGStyledElementModel {
	protected SVGPointsStore store;

	public SVGAnimatedPointsModelBase(SVGModel owner, SVGElement element, SVGElement twin) {
		super(owner, element, twin);
	}
	
	public SVGPointsStore getPointsStore() {
		if (store == null) {
			store = new SVGPointsStore(this);
		}
		return store;
	}
}
