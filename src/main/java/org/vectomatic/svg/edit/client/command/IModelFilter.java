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

import java.util.List;

import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

/**
 * Interface to filter SVG models
 * @author laaglu
 */
public interface IModelFilter {
	/**
	 * Returns true if the filter accepts the specified models,
	 * false otherwise
	 * @param models
	 * The models to test for acceptance by the filter
	 * @return
	 * True if the filter accepts the specified models,
	 * false otherwise
	 */
	public boolean accept(List<SVGElementModel> models);
}
