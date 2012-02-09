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

import org.vectomatic.svg.edit.client.gxt.form.CssContainer;
import org.vectomatic.svg.edit.client.gxt.form.DashArrayField;
import org.vectomatic.svg.edit.client.gxt.form.FloatSliderField;
import org.vectomatic.svg.edit.client.gxt.form.PaintField;
import org.vectomatic.svg.edit.client.gxt.form.ResetButtonField;
import org.vectomatic.svg.edit.client.gxt.form.SVGLengthContainer;
import org.vectomatic.svg.edit.client.gxt.form.ToggleGroupField;
import org.vectomatic.svg.edit.client.model.CssMetadata;
import org.vectomatic.svg.edit.client.model.IFieldFactory;
import org.vectomatic.svg.edit.client.model.IMetadata;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.binding.TimeFieldBinding;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Util class to operate on GXT forms
 * @author laaglu
 */
public class FormPanelUtils {

	public static void createFieldBindings(FormPanel formPanel, FormBinding formBinding) {
		GWT.log("Field count: " + formPanel.getFields().size());
		for (Field<?> f : formPanel.getFields()) {
			if (formBinding.getBinding(f) == null) {
				String name = f.getName();
				GWT.log("field: " + f.getName());
				if (name != null && name.length() > 0) {
					FieldBinding b = null;
					if (f instanceof SimpleComboBox) {
						if (SVGLengthContainer.containsField(f)) {
							b = new SVGLengthUnitBinding((SimpleComboBox<Style.Unit>)f, f.getName());
						} else {
							b = new SimpleComboBoxFieldBinding((SimpleComboBox)f, f.getName());
						}
					} else if (f instanceof NumberField && (SVGLengthContainer.containsField(f))) {
						b = new SVGLengthValueBinding((NumberField)f, f.getName());
					} else if (f instanceof FloatSliderField) {
						b = new FloatSliderFieldBinding((FloatSliderField)f, f.getName());
					} else if (f instanceof PaintField) {
						b = new PaintFieldBinding((PaintField)f, f.getName());
					} else if (f instanceof ResetButtonField) {
						b = new ResetButtonBinding((ResetButtonField)f, f.getName());
					} else if (f instanceof TimeField) {
						b = new TimeFieldBinding((TimeField)f, f.getName());
					} else if (f instanceof SpinnerField) {
						b = new SpinnerFieldBinding((SpinnerField)f, f.getName());
					} else if (f instanceof DashArrayField) {
						b = new DashArrayFieldBinding((DashArrayField)f, f.getName());
					} else {
						b = new FieldBinding(f, f.getName());
					}
					formBinding.addFieldBinding(b);
				}
			}
		}
	}
	
	public static final IFieldFactory TEXTFIELD_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			TextField<String> textField = new TextField<String>();
			textField.setName(metadata.getName());
			textField.setFieldLabel(Format.capitalize(metadata.getDescription()));
			return textField;
		}
	};

	public static final IFieldFactory TEXTAREA_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			TextArea textArea = new TextArea();
			textArea.setName(metadata.getName());
			textArea.setFieldLabel(Format.capitalize(metadata.getDescription()));
			return textArea;
		}
	};

	public static final IFieldFactory CHECKBOX_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			CheckBox checkBox = new CheckBox();
			checkBox.setName(metadata.getName());
			checkBox.setBoxLabel(Format.capitalize(metadata.getDescription()));
			return checkBox;
		}
	};
	
	public static final IFieldFactory NUMBER_FIELD_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
		    NumberField valueField = new NumberField();
		    valueField.setName(metadata.getName());
		    valueField.setFieldLabel(Format.capitalize(metadata.getDescription()));
		    valueField.setPropertyEditorType(Float.class);
			return valueField;
		}
	};

	public static final IFieldFactory SVGLENGTH_FIELD_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			return new SVGLengthContainer(metadata);
		}
	};
	
	public static final IFieldFactory SVGPAINT_FIELD_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			return new CssContainer((CssMetadata<?>)metadata, new PaintField());
		}
	};
	
	public static final IFieldFactory SPINNER_FIELD_FACTORY = new IFieldFactory() {
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			SpinnerField spinnerField = new SpinnerField();
			spinnerField.setFireChangeEventOnSetValue(true);
			spinnerField.setAllowDecimals(true);
			spinnerField.setAllowNegative(false);
			spinnerField.setFormat(NumberFormat.getFormat("#0.0"));
			spinnerField.setMinValue(0);
			spinnerField.setMaxValue(100);
			return new CssContainer((CssMetadata<?>)metadata, spinnerField);
		}
	};

	public static final IFieldFactory DASHARRAY_FIELD_FACTORY = new IFieldFactory() {

		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			return new CssContainer((CssMetadata<?>)metadata, new DashArrayField());
		}
		
	};
	
	
	
	public static class ToggleGroupFieldFactory implements IFieldFactory {
		private String[] values;
		private String[] tooltips;
		private AbstractImagePrototype icons[];
		
		public ToggleGroupFieldFactory(String[] values,
				String[] tooltips,
				AbstractImagePrototype icons[]) {
			this.values = values;
			this.tooltips = tooltips;
			this.icons = icons;
		}
		
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			return new CssContainer((CssMetadata<?>)metadata, new ToggleGroupField(values, tooltips, icons));
		}
	}
	public static class HSliderFieldFactory implements IFieldFactory {
		private float min;
		private float max;
		public HSliderFieldFactory(float min, float max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public Component createField(IMetadata<?, ?> metadata) {
			return new CssContainer((CssMetadata<?>)metadata, new FloatSliderField(min, max));
		}
	};
}
