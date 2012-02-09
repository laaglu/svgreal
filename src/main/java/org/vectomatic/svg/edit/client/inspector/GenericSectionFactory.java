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

import org.vectomatic.svg.edit.client.model.IFieldFactory;
import org.vectomatic.svg.edit.client.model.IMetadata;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormData;

/**
 * Factory to instantiate inspector sections for model categories
 * which do not provide a dedicated inspector section. The inspector
 * section is based on a form dynamically generated from the category.
 * @author laaglu
 */
public class GenericSectionFactory implements IInspectorSectionFactory {
	public static final IInspectorSectionFactory INSTANCE = new GenericSectionFactory();
	public IInspectorSection createSection(ModelCategory<?> category) {
		FormPanel formPanel = new FormPanel();
		formPanel.setLabelAlign(LabelAlign.TOP);
		FormData formData = new FormData("100%");
		for (IMetadata<?,?> m : category.getMetadata()) {
			IFieldFactory fieldFactory = m.getFieldFactory();
			if (fieldFactory != null) {
				formPanel.add(fieldFactory.createField(m), formData);
			}
		}
		formPanel.setScrollMode(Scroll.AUTOY);
		return new FormInspectorSection(formPanel, category);
	}
}
