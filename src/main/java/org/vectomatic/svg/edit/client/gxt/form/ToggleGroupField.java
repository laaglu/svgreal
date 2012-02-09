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

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.layout.FillData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Field subclass with a discrete set of possible values,
 * each value being represented by a toggle button with
 * an icon
 * @author laaglu
 */
public class ToggleGroupField extends AdapterField {
	static int toggleGroupId;
	private class ToggleGroupFieldPanel extends LayoutContainer {
		private Map<ToggleButton, String> toggleToValue;
		private Map<String, ToggleButton> valueToToggle;
		private SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ToggleGroupField.this.setValue(toggleToValue.get(ce.getButton()));
			}
		};
		
		public ToggleGroupFieldPanel(
				String[] values,
				String[] tooltips,
				AbstractImagePrototype icons[]) { 
			setLayout(new FillLayout(Orientation.HORIZONTAL));
			toggleToValue = new HashMap<ToggleButton, String>();
			valueToToggle = new HashMap<String, ToggleButton>();
			String groupName = "ToggleGroupField" + toggleGroupId++;
			for (int i = 0; i < values.length;i++) {
				ToggleButton button = new ToggleButton();
				button.setToggleGroup(groupName);
				button.setToolTip(tooltips[i]);
				button.setIconAlign(IconAlign.TOP);
				button.setIcon(icons[i]);
				button.addSelectionListener(listener);
				add(button, i < values.length -1 ? new FillData(0, 2, 0, 0) : new FillData(0));
				toggleToValue.put(button, values[i]);
				valueToToggle.put(values[i], button);
			}
		}
		
		public void update(String value) {
			if (value != null) {
				ToggleButton button = valueToToggle.get(value);
				if (button != null) {
					button.toggle(true);
				}
			}
		}
	}
	
	public ToggleGroupField(
			String[] values,
			String[] tooltips,
			AbstractImagePrototype icons[]) {
		super(null);
		widget = new ToggleGroupFieldPanel(values, tooltips, icons);
		setResizeWidget(true);
		setFireChangeEventOnSetValue(true);
	}

	@Override
	public void setValue(Object value) {
		((ToggleGroupFieldPanel)widget).update((String)value);
		super.setValue(value);
	}
	
	@Override
	public Object getValue() {
		return value;
	}
}
