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

import org.vectomatic.svg.edit.client.gxt.form.DashArrayField;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;

/**
 * Binds a DashArrayField with a 
 * CSS property of an SVGStyleElementModel.
 * @author laaglu
 */
public class DashArrayFieldBinding extends DelayedBindingBase {
	private Listener<FieldEvent> afterEditListener;

	public DashArrayFieldBinding(DashArrayField field, String property) {
		super(field, property);
		afterEditListener = new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				commitChanges();
			}			
		};
	}

	@Override
	public void bind(ModelData model) {
		GWT.log("DashArrayFieldBinding.bind(" + model + ")");
		super.bind(model);
		field.addListener(Events.AfterEdit, afterEditListener);
		currentValue = model.get(property);
	}
	
	@Override
	public void unbind() {
		field.removeListener(Events.AfterEdit, afterEditListener);
		super.unbind();
	}

}
