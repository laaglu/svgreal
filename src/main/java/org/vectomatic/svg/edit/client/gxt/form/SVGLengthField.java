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

import java.util.Arrays;

import org.vectomatic.svg.edit.client.gxt.widget.SpinnerFieldExt;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Field subclass to edit SVGLength values
 * @author laaglu
 */
public class SVGLengthField extends AdapterField {
	private class SVGLengthPanel extends LayoutContainer {
		private SpinnerFieldExt valueField;
		private SimpleComboBox<Style.Unit> unitField;
	
		/**
		 * Constructor
		 * @param metadata metadata used to label the field.
		 */
		public SVGLengthPanel(float min, float max, float increment) {
		    valueField = new SpinnerFieldExt();
		    valueField.setPropertyEditorType(Float.class);
		    valueField.setAllowDecimals(true);
		    valueField.setAllowNegative(false);
		    valueField.setFormat(NumberFormat.getFormat("#0.0"));
		    valueField.setMinValue(min);
		    valueField.setMaxValue(max);
		    valueField.setIncrement(increment);
		    
			unitField = new SimpleComboBox<Style.Unit>();
			unitField.setLazyRender(false);
			unitField.add(Arrays.asList(Style.Unit.values()));

			setLayout(new ColumnLayout());
			add(valueField, new ColumnData(.775f));
			add(new Html("&nbsp;"), new ColumnData(.025f));
			add(unitField, new ColumnData(.2f));
			valueField.setFireChangeEventOnSetValue(true);
			unitField.setFireChangeEventOnSetValue(true);

			valueField.setFireChangeEventOnSetValue(true);
			unitField.setFireChangeEventOnSetValue(true);
		}
		
		public void update(SVGLength length) {
			if (length != null) {
				if (valueField.getValue() == null || length.getValue() != valueField.getValue().floatValue()) {
					valueField.setFireChangeEventOnSetValue(false);
					valueField.setValue(length.getValue());
					valueField.setFireChangeEventOnSetValue(true);
				}
				if (unitField.getValue() == null || length.getUnit() != unitField.getValue().getValue()) {
					unitField.setFireChangeEventOnSetValue(false);
					unitField.setValue(unitField.findModel(length.getUnit()));
					unitField.setFireChangeEventOnSetValue(true);
				}
			}
		}
	}

	public SVGLengthField(float min, float max, float increment) {
		super(null);
		widget = new SVGLengthPanel(min, max, increment);
		setResizeWidget(true);
		setFireChangeEventOnSetValue(true);
	}

	@Override
	public void setValue(Object value) {
		((SVGLengthPanel)widget).update((SVGLength)value);
		super.setValue(value);
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	public SpinnerFieldExt getValueField() {
		return ((SVGLengthPanel)widget).valueField;
	}
	
	public SimpleComboBox<Style.Unit> getUnitField() {
		return ((SVGLengthPanel)widget).unitField;
	}

}
