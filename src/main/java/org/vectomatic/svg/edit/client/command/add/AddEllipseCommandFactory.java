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
package org.vectomatic.svg.edit.client.command.add;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGEllipseElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.itf.ISVGLocatable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.VectomaticApp2;
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
 * Command factory to add new ellipses to the the SVG model.
 * @author laaglu
 */
public class AddEllipseCommandFactory extends AddCommandFactoryBase implements MouseDownProcessor, MouseMoveProcessor, MouseUpProcessor {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddEllipseCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddEllipseCommandFactory>(ModelConstants.INSTANCE.addEllipseCmdFactory(), ModelConstants.INSTANCE.addEllipseCmdFactoryDesc()) {
		@Override
		public AddEllipseCommandFactory create() {
			return new AddEllipseCommandFactory();
		}
	};

	/**
	 * The new ellipse element
	 */
	protected OMSVGEllipseElement ellipse;
	/**
	 * The ellipse center
	 */
	protected OMSVGPoint c;
	/**
	 * The ellipse corner
	 */
	protected OMSVGPoint p;

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}
	
 	@Override
	public void start(Object requester) {
		GWT.log("AddEllipseCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.addEllipseCmdFactory1());
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		if (owner != null && ellipse != null) {
			owner.getTwinGroup().removeChild(ellipse);
		}
		updateStatus(ModelConstants.INSTANCE.addEllipseCmdFactory2());
		owner = VectomaticApp2.getApp().getActiveModel();
		OMSVGElement parent = owner.getTwinGroup();
		svg = parent.getOwnerSVGElement();
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		m = ((ISVGLocatable)parent).getScreenCTM().inverse();
		c = getCoordinates(event, m);
		p = svg.createSVGPoint(c);
		ellipse = document.createSVGEllipseElement(c.getX(), c.getY(), 0, 0);
		OMSVGTransform xform = svg.createSVGTransform();
		xform.setRotate(-owner.getRotation(), c.getX(), c.getY());
		ellipse.getTransform().getBaseVal().appendItem(xform);
//		ellipse.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_LIGHTCORAL_VALUE);
//		ellipse.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
		applyCssContextStyle((SVGElement) ellipse.getElement().cast());
		ellipse.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
		parent.appendChild(ellipse);
		m = ellipse.getScreenCTM().inverse();
		return true;
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (owner != null) {
			updateStatus(ModelConstants.INSTANCE.addEllipseCmdFactory1());
			if (ellipse.getRx().getBaseVal().getValue() == 0 || ellipse.getRy().getBaseVal().getValue() == 0) {
				owner.getTwinGroup().removeChild(ellipse);
			} else {
				createCommand(ellipse);
			}
			ellipse = null;
			owner = null;
		}
		return true;
	}
	
	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (owner != null) {
			p = getCoordinates(event, m);
			float xmin = Math.min(c.getX(), p.getX());
			float ymin = Math.min(c.getY(), p.getY());
			float xmax = Math.max(c.getX(), p.getX());
			float ymax = Math.max(c.getY(), p.getY());
			ellipse.getRx().getBaseVal().setValue(xmax - xmin);
			ellipse.getRy().getBaseVal().setValue(ymax - ymin);
		}
		return true;
	}
}
