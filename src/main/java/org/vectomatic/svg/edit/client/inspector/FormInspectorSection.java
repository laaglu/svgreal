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
package org.vectomatic.svg.edit.client.inspector;

import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.model.AbstractModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.core.client.GWT;

/**
 * Generic inspector section. Its content is a form
 * dynamically generated from a model category
 * @author laaglu
 * @param <M>
 * An SVG model
 */
public class FormInspectorSection<M extends AbstractModel<?>> implements IInspectorSection<M> {
	private FormPanel formPanel;
	private FormBinding formBinding;
	private ModelCategory category;

	public FormInspectorSection(FormPanel formPanel, ModelCategory category) {
		this.formPanel = formPanel;
		this.category = category;
		formPanel.setHeading(Format.capitalize(category.getDescription()));
		formPanel.setAnimCollapse(false);
		formBinding = new FormBinding(formPanel, false);
		FormPanelUtils.createFieldBindings(formPanel, formBinding);
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGInspectorFormSection(");
		builder.append(category);
		builder.append(")");
		return builder.toString();
	}
	public Component getPanel() {
		return formPanel;
	}
	public void bind(M model) {
		GWT.log("FormInspectorSection.bind(" + model + ")");
		formBinding.setStore(model.getStore());
		formBinding.bind(model);
	}
	public void unbind() {
		formBinding.unbind();
		formBinding.setStore(null);
	}
}
