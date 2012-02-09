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

import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.GenericEditCommandFactory;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.form.ImageHrefField;
import org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Binding class for ImageHrefField
 * @author laaglu
 */
public class ImageHrefFieldBinding extends FieldBinding implements ValueChangeHandler<Size> {
	private HandlerRegistration registration;
	
	public ImageHrefFieldBinding(ImageHrefField field, String property) {
		super(field, property);
	}
	public void updateModel() {
		ImageHrefField field = (ImageHrefField)this.field;
		SVGImageElementModel model = (SVGImageElementModel)this.model;
		model.setResourceName(field.getResourceName());
		super.updateModel();
	}
	@Override
	public void onValueChange(ValueChangeEvent<Size> event) {
		GenericEditCommandFactory factory = GenericEditCommandFactory.INSTANTIATOR.create();
		factory.start(this);
		Size size = event.getValue();
		SVGImageElementModel imageModel = (SVGImageElementModel) model;
		SVGModel owner = imageModel.getOwner();
		OMSVGSVGElement svg = owner.getSvgElement();
		Map<String, Object> oldValues = new HashMap<String, Object>();
		oldValues.put(SVGConstants.SVG_WIDTH_ATTRIBUTE, imageModel.get(SVGConstants.SVG_WIDTH_ATTRIBUTE));
		oldValues.put(SVGConstants.SVG_HEIGHT_ATTRIBUTE, imageModel.get(SVGConstants.SVG_HEIGHT_ATTRIBUTE));
		imageModel.setSilent(true);
		imageModel.set(SVGConstants.SVG_WIDTH_ATTRIBUTE, new SVGLength(svg.createSVGLength(OMSVGLength.SVG_LENGTHTYPE_PX, size.width)));
		imageModel.set(SVGConstants.SVG_HEIGHT_ATTRIBUTE, new SVGLength(svg.createSVGLength(OMSVGLength.SVG_LENGTHTYPE_PX, size.height)));
		imageModel.setSilent(false);
		owner.getCommandStore().addCommand(factory.createCommand(imageModel, oldValues));
	}
	
	@Override
	public void bind(ModelData model) {
		super.bind(model);
		ImageHrefField imageField = (ImageHrefField)field;
		registration = imageField.addValueChangeHandler(this);
	}
	
	@Override
	public void unbind() {
		super.unbind();
		registration.removeHandler();
	}
}
