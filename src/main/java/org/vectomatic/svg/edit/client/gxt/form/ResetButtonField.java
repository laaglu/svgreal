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
package org.vectomatic.svg.edit.client.gxt.form;

import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.model.CssMetadata;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;

/**
 * Reset button field. When clicked, it restores the
 * associated property to its default value
 * @author laaglu
 */
public class ResetButtonField extends AdapterField {

	public ResetButtonField(final CssMetadata<?> metadata) {
		super(null);
		Button resetButton = new Button(AppConstants.INSTANCE.clearButton());
		setResizeWidget(true);
		resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// fire a change event
				ResetButtonField.super.setValue(metadata.getDefaultValue());
			}
		});
		widget = resetButton;
		setFireChangeEventOnSetValue(true);
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public void setValue(Object value) {
		// do not fire a change (the method is called because the associated
		// field has changed)
	    this.value = value;
	}
}
