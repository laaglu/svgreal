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

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Float slider field
 * @author laaglu
 */
public class FloatSliderField extends Field<Float> {
	/**
	 * Custom slider class. It fires extra event which the binding catches so
	 * that the binding will not create a command until the thumb is released
	 * or the slider is clicked.
	 * @author laaglu
	 */
	private static class FloatSlider extends Slider {
		@Override
		protected void onClick(ComponentEvent ce) {
			fireEvent(Events.OnClick, ce);
			super.onClick(ce);
		}
		
		@Override
		protected void onDragEnd(DragEvent de) {
			fireEvent(Events.DragEnd, de);
			super.onDragEnd(de);
		}
	}

	private float floatMin;
	private float floatMax;
	private FloatSlider slider;
	protected El hidden;
	protected Listener<SliderEvent> listener;

	/**
	 * Creates a new slider field.
	 * @param slider
	 * the slider to be wrapped.
	 */
	public FloatSliderField(float floatMin, float floatMax) {
		super();
		setFireChangeEventOnSetValue(true);
		this.floatMin = floatMin;
		this.floatMax = floatMax;
		FloatSlider slider = new FloatSlider();
		slider.setMinValue(0);
		slider.setMaxValue(100);
		slider.setIncrement(1);
		slider.setClickToChange(true);
		setSlider(slider);
	}

	/**
	 * Returns the slider component.
	 * @return the slider.
	 */
	public Slider getSlider() {
		return slider;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		if (readOnly) {
			slider.disable();
		} else if (!readOnly && isEnabled()) {
			slider.enable();
		}
	}

	/**
	 * Sets the slider component.
	 * @param slider
	 * the slider
	 */
	public void setSlider(FloatSlider slider) {
		assertPreRender();
		if (listener == null) {
			listener = new Listener<SliderEvent>() {
				public void handleEvent(SliderEvent be) {
					if (rendered) {
						updateHiddenField();
					}
					int intValue = FloatSliderField.this.slider.getValue();
					float modelValue = floatMin + (intValue / (float)((FloatSliderField.this.slider.getMaxValue() - FloatSliderField.this.slider.getMinValue()))) * (floatMax - floatMin);
					FloatSliderField.super.setValue(modelValue);
				}
			};
		}
		if (this.slider != slider) {
			if (this.slider != null) {
				ComponentHelper.removeFromParent(this.slider);
				this.slider.removeListener(Events.Change, listener);
			}
			this.slider = slider;
			slider.getFocusSupport().setIgnore(true);
			ComponentHelper.setParent(this, slider);
			slider.addListener(Events.Change, listener);
		}
	}
	
	@Override
	public Float getValue() {
		return value;
	}
	
	@Override
	public void setValue(Float value) {
		if (value == null) {
			int min = slider.getMinValue();
			slider.setValue(min);
			super.setValue((float)min);
		} else {
			int sliderValue = slider.getMinValue() + (int)((slider.getMaxValue() - slider.getMinValue()) * (value - floatMin) / (floatMax - floatMin));
			slider.setValue(sliderValue);
			super.setValue(value);
		}
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		updateHiddenField();

		El elem = findLabelElement();
		if (elem != null) {
			elem.dom.setAttribute("for", slider.getId());
		}
	}

	@Override
	protected void doAttachChildren() {
		super.doAttachChildren();
		ComponentHelper.doAttach(slider);
	}

	@Override
	protected void doDetachChildren() {
		super.doDetachChildren();
		ComponentHelper.doDetach(slider);
	}

	@Override
	protected El getFocusEl() {
		return slider.getFocusEl();
	}

	@Override
	protected El getInputEl() {
		return hidden;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		slider.disable();
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if (!readOnly) {
			slider.enable();
		}
	}

	@Override
	protected void onRender(Element parent, int index) {
		setElement(DOM.createDiv(), parent, index);

		slider.render(getElement());
		hidden = new El((Element) Document.get().createHiddenInputElement().cast());
		getElement().appendChild(hidden.dom);

		if (GXT.isIE) {
			el().makePositionable();
		}

		super.onRender(parent, index);
	}

	@Override
	protected void onResize(int width, int height) {
		if (rendered) {
			if (slider.isVertical()) {
				slider.setHeight(height);
			} else {
				slider.setWidth(width);
			}
		}
		super.onResize(width, height);
	}

	protected void updateHiddenField() {
		if (rendered) {
			hidden.setValue(slider.getValue() + "");
		}
	}
}
