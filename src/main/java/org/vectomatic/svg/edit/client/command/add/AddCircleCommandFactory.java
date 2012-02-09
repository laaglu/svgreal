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
package org.vectomatic.svg.edit.client.command.add;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.command.FactoryInstantiatorBase;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.event.MouseUpProcessor;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * Command factory to add new circles to the the SVG model.
 * @author laaglu
 */
public class AddCircleCommandFactory extends AddCommandFactoryBase implements MouseDownProcessor, MouseMoveProcessor, MouseUpProcessor {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddCircleCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddCircleCommandFactory>(ModelConstants.INSTANCE.addCircleCmdFactory(), ModelConstants.INSTANCE.addCircleCmdFactoryDesc()) {
		@Override
		public AddCircleCommandFactory create() {
			return new AddCircleCommandFactory();
		}
	};
	
	/**
	 * The new circle element
	 */
	protected OMSVGCircleElement circle;
	/**
	 * The circle center
	 */
	protected OMSVGPoint c;
	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}
	
	@Override
	public void start(Object requester) {
		GWT.log("AddCircleCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.addCircleCmdFactory1());
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		if (owner != null && circle != null) {
			owner.getTwinGroup().removeChild(circle);
		}
		updateStatus(ModelConstants.INSTANCE.addCircleCmdFactory2());
		owner = SvgrealApp.getApp().getActiveModel();
		c = owner.getCoordinates(event, true);
		circle = new OMSVGCircleElement(c.getX(), c.getY(), 0);
		applyCssContextStyle((SVGElement) circle.getElement().cast());
		circle.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
		owner.getTwinGroup().appendChild(circle);
		return true;
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (owner != null) {
			if (circle.getR().getBaseVal().getValue() == 0) {
				owner.getTwinGroup().removeChild(circle);
			} else {
				createCommand(circle);
			}
			circle = null;
			owner = null;
			updateStatus(ModelConstants.INSTANCE.addCircleCmdFactory1());
		}
		return true;
	}
	
	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (owner != null) {
			OMSVGPoint p = owner.getCoordinates(event, true);
			circle.getR().getBaseVal().setValue(p.substract(c).length());
		}
		return true;
	}
}
