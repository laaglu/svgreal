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
package org.vectomatic.svg.edit.client.gxt.form;

import java.util.Arrays;

import org.vectomatic.svg.edit.client.model.IMetadata;

import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to create a composite widget dedicated to editing
 * SVG lengths (a number field for the numerical value plus
 * a combo for the unit)
 * @author laaglu
 */
public class SVGLengthContainer extends LayoutContainer {
	public SVGLengthContainer(IMetadata<?, ?> metadata) {
	    NumberField valueField = new NumberField();
	    valueField.setName(metadata.getName());
	    valueField.setFieldLabel(Format.capitalize(metadata.getDescription()));
	    valueField.setPropertyEditorType(Float.class);
	    
		SimpleComboBox<Style.Unit> unitField = new SimpleComboBox<Style.Unit>();
		unitField.add(Arrays.asList(Style.Unit.values()));
		unitField.setName(metadata.getName());
	
		FormData formData = new FormData("100%");
		LayoutContainer left = new LayoutContainer();  
		left.setStyleAttribute("paddingRight", "10px");  
		FormLayout layout = new FormLayout();  
		layout.setLabelAlign(LabelAlign.TOP);
		left.setLayout(layout);
		left.add(valueField, formData);
	
		LayoutContainer right = new LayoutContainer();
		right.setStyleAttribute("paddingLeft", "10px");
		layout = new FormLayout();
		layout.setLabelAlign(LabelAlign.TOP);
		layout.setLabelSeparator("");
		right.setLayout(layout); 
		right.add(unitField, formData);
	
		setLayout(new ColumnLayout());
		add(left, new ColumnData(.8f));
		add(right, new ColumnData(.2f));  
	}
	
	public static boolean containsField(Field<?> f) {
		Widget w = f;
		while ((w = w.getParent()) != null) {
			if (w instanceof SVGLengthContainer) {
				return true;
			}
		}
		return false;
	}
}
