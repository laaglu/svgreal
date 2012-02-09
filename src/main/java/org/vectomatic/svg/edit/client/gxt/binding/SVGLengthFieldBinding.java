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
package org.vectomatic.svg.edit.client.gxt.binding;

import org.vectomatic.svg.edit.client.gxt.form.SVGLengthField;
import org.vectomatic.svg.edit.client.gxt.widget.SpinnerFieldExt;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ClickRepeaterEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.ClickRepeater;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Binds an SVGLengthField to a model property of
 * type SVGLength.
 * @author laaglu
 */
public class SVGLengthFieldBinding extends DelayedBindingBase {
	private SpinnerFieldExt valueField;
	private boolean disabled;
	private Listener<FieldEvent> changeListener = new Listener<FieldEvent>() {
		public void handleEvent(FieldEvent fe) {
			handleChange();
		}
	};
	
	private Listener<ClickRepeaterEvent> repeaterDownListener = new Listener<ClickRepeaterEvent>() {
		public void handleEvent(ClickRepeaterEvent re) {
			disabled = true;
		}
	};
	private Listener<ClickRepeaterEvent> repeaterUpListener = new Listener<ClickRepeaterEvent>() {
		public void handleEvent(ClickRepeaterEvent re) {
			disabled = false;
			handleChange();
		}
	};

	public SVGLengthFieldBinding(SVGLengthField field, String property) {
		super(field, property);
	}

	@Override
	public void bind(ModelData model) {
		super.bind(model);
		SVGLengthField lengthField = (SVGLengthField)field;
		valueField = lengthField.getValueField();
		valueField.addListener(Events.Change, changeListener);
		ClickRepeater repeater = valueField.getRepeater();
		repeater.addListener(Events.OnMouseDown, repeaterDownListener);
		repeater.addListener(Events.OnMouseUp, repeaterUpListener);
		ClickRepeater twinRepeater = valueField.getTwinRepeater();
		twinRepeater.addListener(Events.OnMouseDown, repeaterDownListener);
		twinRepeater.addListener(Events.OnMouseUp, repeaterUpListener);
		lengthField.getUnitField().addListener(Events.Change, changeListener);
		disabled = false;
	}
	
	@Override
	public void unbind() {
		SVGLengthField lengthField = (SVGLengthField)field;
		SpinnerFieldExt valueField = lengthField.getValueField();
		valueField.removeListener(Events.Change, changeListener);
		ClickRepeater repeater = valueField.getRepeater();
		repeater.removeListener(Events.OnMouseDown, repeaterDownListener);
		repeater.removeListener(Events.OnMouseUp, repeaterUpListener);
		ClickRepeater twinRepeater = valueField.getTwinRepeater();
		twinRepeater.removeListener(Events.OnMouseDown, repeaterDownListener);
		twinRepeater.removeListener(Events.OnMouseUp, repeaterUpListener);
		lengthField.getUnitField().removeListener(Events.Change, changeListener);
		super.unbind();
	}
	
	public void handleChange() {
		SVGLengthField lengthField = (SVGLengthField)field;
		SpinnerFieldExt valueField = lengthField.getValueField();
		SimpleComboBox<Unit> unitField = lengthField.getUnitField();
		Number value = valueField.getValue();
		if (value != null) {
			SVGLength length = model.get(property);
			length.setValue(value.floatValue());
			Style.Unit unit = null;
			String rawUnit = unitField.getRawValue();
			if (rawUnit != null && rawUnit.length() > 0) {
				unit = unitField.getSimpleValue();
			}
			length.setUnit(unit);
			model.set(property, length);
			if (!disabled) {
				commitChanges();
			}
		}
	}
}
