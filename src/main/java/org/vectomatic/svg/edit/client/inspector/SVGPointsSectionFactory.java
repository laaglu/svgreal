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
package org.vectomatic.svg.edit.client.inspector;

import org.vectomatic.svg.edit.client.model.ModelCategory;

/**
 * Factory to instantiate an inspector section dedicated to
 * SVG polygon and polyline geometry
 * @author laaglu
 */
public class SVGPointsSectionFactory implements IInspectorSectionFactory {
	public static final IInspectorSectionFactory INSTANCE = new SVGPointsSectionFactory();

	@Override
	public IInspectorSection createSection(ModelCategory<?> category) {
		return new SVGPointsInspectorSection(category);
	}

}
