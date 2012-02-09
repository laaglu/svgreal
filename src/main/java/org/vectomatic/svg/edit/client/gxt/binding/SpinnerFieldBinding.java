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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.google.gwt.core.client.GWT;

/**
 * Bind a spinner with a 
 * CSS property of an SVGStyleElementModel. It makes the
 * changes of the spinner value atomic with respect to the
 * object model, even when the spinner buttons are held down 
 * @author laaglu
 */
public class SpinnerFieldBinding extends DelayedBindingBase {
	private Listener<FieldEvent> spinnerListener;
	public SpinnerFieldBinding(SpinnerField field, String property) {
		super(field, property);
		spinnerListener = new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				GWT.log("SpinnerFieldBinding.onMouseUp");
				commitChanges();
			}
		};
	}

	@Override
	public void bind(ModelData model) {
		super.bind(model);
		field.addListener(Events.OnMouseUp, spinnerListener);
	}
	
	@Override
	public void unbind() {
		field.removeListener(Events.OnMouseUp, spinnerListener);
		super.unbind();
	}
}
