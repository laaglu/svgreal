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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.List;

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGViewBoxElementModel;

import com.extjs.gxt.ui.client.event.DNDEvent;

/**
 * Base class for drag and drop handlers
 * @author laaglu
 */
public abstract class DndHandlerBase implements IDndHandler {
	@Override
	public boolean isValidSource(DNDEvent event, List<SVGElementModel> sourceElements) {
		// Forbid dragging the tree root or the viewbox of a model
		SVGModel model = sourceElements.get(0).getOwner();
		SVGElementModel root = model.getRoot();
		SVGViewBoxElementModel viewBox = model.getViewBox();
		for (SVGElementModel sourceElement : sourceElements) {
			if (sourceElement == root || sourceElement == viewBox) {
				return false;
			}
		}
		return true;
	}
	
	public static String getSourceElementNames(List<SVGElementModel> sourceElements) {
		int size = sourceElements.size();
		if (size == 1) {
			return sourceElements.get(0).<String>get(SVGConstants.SVG_TITLE_TAG);
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				builder.append(";");
			}
			builder.append(sourceElements.get(i).<String>get(SVGConstants.SVG_TITLE_TAG));
		}
		return builder.toString();
	}

	
	@Override
	public boolean isValidTarget(List<SVGElementModel> sourceElements, SVGElementModel target) {
		if (target == null) {
			return false;
		}
		// The viewBox is not a valid drop target
		if (target instanceof SVGViewBoxElementModel) {
			return false;
		}
		// An element cannot be dropped onto itself or one of its descendants
		for (SVGElementModel sourceElement : sourceElements) {
			if (sourceElement.isAncestorOf(target)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getKeyCode() {
		return -1;
	}
}
