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

import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel;

import com.extjs.gxt.ui.client.util.Format;

/**
 * Command to represent a change in the href of an SVG image element
 * @author laaglu
 */
public class EditImageHrefCommand extends GenericEditCommand {
	protected String resourceName;
	public EditImageHrefCommand(CommandFactoryBase factory,
			SVGElementModel model, Map<String, Object> oldValues,
			String description) {
		super(factory, model, oldValues, description);
		resourceName = ((SVGImageElementModel)model).getResourceName();
	}

	@Override
	public String getDescription() {
		Map<String, Object> values = new HashMap<String, Object>(newValues);
		values.put(SVGConstants.XLINK_HREF_ATTRIBUTE, resourceName);
		return Format.substitute(description, model.get(SVGConstants.SVG_TITLE_TAG), values.toString());
	}
}
