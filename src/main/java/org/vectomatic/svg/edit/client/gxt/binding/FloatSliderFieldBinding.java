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

import org.vectomatic.svg.edit.client.gxt.form.FloatSliderField;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.Slider;

/**
 * Binds a slider with a 
 * CSS property of an SVGStyleElementModel.
 * @author laaglu
 */
public class FloatSliderFieldBinding extends DelayedBindingBase {
	private Listener<SliderEvent> sliderClickListener;
	private DragListener dragListener;

	public FloatSliderFieldBinding(FloatSliderField field, String property) {
		super(field, property);
		sliderClickListener = new Listener<SliderEvent>() {
			public void handleEvent(SliderEvent be) {
//				GWT.log("FloatSliderFieldBinding.click");
				commitChanges();
			}
		};

		
		// This listener will temporarily disable the capture
		// of model changes to avoid generating dozens of
		// commands as one drags the slider knob. Only one
		// command should be generated, when the knob is released.
		dragListener = new DragListener() {
			@Override
			public void dragEnd(DragEvent de) {
//				GWT.log("FloatSliderFieldBinding.dragEnd");
				commitChanges();
			}
		};
	}
	
	@Override
	public void bind(ModelData model) {
		super.bind(model);
		Slider slider = ((FloatSliderField)field).getSlider();
		slider.addListener(Events.DragEnd, dragListener);
		slider.addListener(Events.OnClick, sliderClickListener);
	}
	
	@Override
	public void unbind() {
		Slider slider = ((FloatSliderField)field).getSlider();
		slider.removeListener(Events.DragEnd, dragListener);
		slider.removeListener(Events.OnClick, sliderClickListener);
		super.unbind();
	}
}
