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

import org.vectomatic.svg.edit.client.gxt.form.ResetButtonField;
import org.vectomatic.svg.edit.client.model.CssMetadata;
import org.vectomatic.svg.edit.client.model.svg.SVGStyledElementModel;

import com.extjs.gxt.ui.client.binding.FieldBinding;

/**
 * Binds a reset CSS property button with a 
 * CSS property of an SVGStyleElementModel.
 * @author laaglu
 */
public class ResetButtonBinding extends FieldBinding {
	
	public ResetButtonBinding(ResetButtonField field, String property) {
		super(field, property);
	}
	
	private static boolean equals(Object v1, Object v2) {
		if (v1 != null) {
			return v1.equals(v2);
		}
		return v2 == null;
	}
	
	@Override
	protected Object onConvertModelValue(Object value) {
		// Enable the reset button only if the field does not
		// have the default value.
		CssMetadata<?> metadata = (CssMetadata<?>) ((SVGStyledElementModel)model).getMetaModel().getMetadata(property);
		field.setEnabled(!equals(metadata.getDefaultValue(), value));
		return value;
	}

	@Override
	public void updateModel() {
		// create a command to record the update to the default value
		super.updateModel();
		
		// since the model value is now the default value, the CSS
		// property can be removed
		model.remove(ResetButtonBinding.this.property);
	}
}
