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
package org.vectomatic.svg.edit.client.command;

import java.util.Map;

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.util.Format;

/**
 * Command to represent a change in the title of an SVG element
 * @author laaglu
 */
public class EditTitleCommand extends GenericEditCommand {

	public EditTitleCommand(CommandFactoryBase factory, SVGElementModel model,
			Map<String, Object> oldValues, String description) {
		super(factory, model, oldValues, description);
	}
	
	@Override
	public String getDescription() {
		return Format.substitute(description, oldValues.get(SVGConstants.SVG_TITLE_TAG), newValues.get(SVGConstants.SVG_TITLE_TAG));
	}

}
