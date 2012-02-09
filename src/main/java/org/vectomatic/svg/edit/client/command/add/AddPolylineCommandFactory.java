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

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.OMSVGPolylineElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.FactoryInstantiatorBase;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;

/**
 * Command factory to add new polylines to the the SVG model.
 * @author laaglu
 */
public class AddPolylineCommandFactory extends AddCommandFactoryBase implements MouseDownProcessor, MouseMoveProcessor {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddPolylineCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddPolylineCommandFactory>(ModelConstants.INSTANCE.addPolylineCmdFactory(), ModelConstants.INSTANCE.addPolylineCmdFactoryDesc()) {
		@Override
		public AddPolylineCommandFactory create() {
			return new AddPolylineCommandFactory();
		}
	};
	/**
	 * The new polyline element
	 */
	protected OMSVGPolylineElement polyline;
	protected OMSVGPointList points;
	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		GWT.log("AddPolylineCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.addPolylineCmdFactory1());
	}

	@Override
	public void stop() {
		GWT.log("AddPolygonCommandFactory.stop()");
		if (polyline != null) {
			OMSVGElement parent = owner.getTwinGroup();
			parent.removeChild(polyline);
			polyline = null;
			points = null;
			owner = null;
		}
		super.stop();
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		if (polyline == null) {
			owner = VectomaticApp2.getApp().getActiveModel();
			OMSVGElement parent = owner.getTwinGroup();
			polyline = new OMSVGPolylineElement();
			points = polyline.getPoints();
			applyCssContextStyle((SVGElement) polyline.getElement().cast());
//			polyline.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
			polyline.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
			polyline.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
			parent.appendChild(polyline);
			points.appendItem(owner.getCoordinates(event, true));
		}
		points.appendItem(owner.getCoordinates(event, true));
		int count = points.getNumberOfItems();
		if (count > 2 && points.getItem(count - 1).substract(points.getItem(count - 3), owner.getSvgElement().createSVGPoint()).length() < 3) {
			points.removeItem(count - 1);
			points.removeItem(count - 2);
			OMSVGPolylineElement newPolyline = polyline;
			polyline = null;
			createCommand(newPolyline);
			owner = null;
		}
		return true;
	}
	
	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (owner != null) {
			int count = points.getNumberOfItems();
			owner.getCoordinates(event, true).assignTo(points.getItem(count - 1));
		}
		return true;
	}
}
