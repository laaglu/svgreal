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
package org.vectomatic.svg.edit.client.gxt.binding;

import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.dom.client.Style;

/**
 * Binding class dedicated to maintaining the link between
 * the combo box representing the unit part of an SVG length an the
 * associated model.
 * @author laaglu
 */
@SuppressWarnings({"unchecked"})
public class SVGLengthUnitBinding extends SimpleComboBoxFieldBinding {

	public SVGLengthUnitBinding(SimpleComboBox<Style.Unit> field, String property) {
		super(field, property);
	}

	@Override
	protected Object onConvertFieldValue(Object value) {
		SVGLength length = model.get(property);
		length.setUnit((Style.Unit)simpleComboBox.getSimpleValue());
		return length;
	}

	@Override
	protected Object onConvertModelValue(Object value) {
		SVGLength length = (SVGLength)value;
		return simpleComboBox.findModel(length.getUnit());
	}

}
