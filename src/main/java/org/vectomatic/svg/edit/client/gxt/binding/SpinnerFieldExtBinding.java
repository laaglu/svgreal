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

import org.vectomatic.svg.edit.client.gxt.widget.SpinnerFieldExt;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ClickRepeaterEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.ClickRepeater;

/**
 * Bind a spinner with a 
 * CSS property of an SVGStyleElementModel. It makes the
 * changes of the spinner value atomic with respect to the
 * object model, even when the spinner buttons are held down 
 * @author laaglu
 */
public class SpinnerFieldExtBinding extends DelayedBindingBase {
	private SpinnerFieldExt spinnerField;
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
	
	public SpinnerFieldExtBinding(SpinnerFieldExt field, String property) {
		super(field, property);
		this.spinnerField = field;
	}


	@Override
	public void bind(ModelData model) {
		super.bind(model);
		spinnerField.addListener(Events.Change, changeListener);
		ClickRepeater repeater = spinnerField.getRepeater();
		repeater.addListener(Events.OnMouseDown, repeaterDownListener);
		repeater.addListener(Events.OnMouseUp, repeaterUpListener);
		ClickRepeater twinRepeater = spinnerField.getTwinRepeater();
		twinRepeater.addListener(Events.OnMouseDown, repeaterDownListener);
		twinRepeater.addListener(Events.OnMouseUp, repeaterUpListener);
	}
	
	@Override
	public void unbind() {
		spinnerField.removeListener(Events.Change, changeListener);
		ClickRepeater repeater = spinnerField.getRepeater();
		repeater.removeListener(Events.OnMouseDown, repeaterDownListener);
		repeater.removeListener(Events.OnMouseUp, repeaterUpListener);
		ClickRepeater twinRepeater = spinnerField.getTwinRepeater();
		twinRepeater.removeListener(Events.OnMouseDown, repeaterDownListener);
		twinRepeater.removeListener(Events.OnMouseUp, repeaterUpListener);
		super.unbind();
	}

	public void handleChange() {
		if (!disabled) {
			commitChanges();
		}
	}
}
