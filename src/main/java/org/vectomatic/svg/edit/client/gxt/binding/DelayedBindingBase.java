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

import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.model.AbstractModel;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.GWT;

/**
 * Base class for bindings which do not immediately
 * commit their value to the model when the field is changed
 * @author laaglu
 */
public abstract class DelayedBindingBase extends FieldBinding {
	protected Object currentValue;

	public DelayedBindingBase(Field<?> field, String property) {
		super(field, property);
	}
	
	@Override
	public void bind(ModelData model) {
		super.bind(model);
		currentValue = model.get(property);
	}

	@Override
	public void updateModel() {
		GWT.log("DelayedBindingBase.updateModel()");
		Object val = field.getValue();
		GWT.log("/\\/\\/\\/\\/\\/\\/\\/\\/\\ updateModel: " + val);
		model.set(property, val);
		if (VectomaticApp2.getApp().getCommandFactorySelector().isSuspended()) {
			currentValue = val;
		}
	}
	
	protected void commitChanges() {
		GWT.log("DelayedBindingBase.commitChanges: " + currentValue);
		AbstractModel<?> amodel = (AbstractModel<?>)model;
		amodel.setSilent(true);
		model.set(property, currentValue);
		amodel.setSilent(false);
		super.updateModel();
		currentValue = field.getValue();
	}

}
