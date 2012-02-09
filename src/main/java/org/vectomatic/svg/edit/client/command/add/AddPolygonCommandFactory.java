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
import org.vectomatic.dom.svg.OMSVGPolygonElement;
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
 * Command factory to add new polygons to the the SVG model.
 * @author laaglu
 */
public class AddPolygonCommandFactory extends AddCommandFactoryBase implements MouseDownProcessor, MouseMoveProcessor {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddPolygonCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddPolygonCommandFactory>(ModelConstants.INSTANCE.addPolygonCmdFactory(), ModelConstants.INSTANCE.addPolygonCmdFactoryDesc()) {
		@Override
		public AddPolygonCommandFactory create() {
			return new AddPolygonCommandFactory();
		}
	};

	/**
	 * The new polygon element
	 */
	protected OMSVGPolygonElement polygon;
	protected OMSVGPointList points;

	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		GWT.log("AddPolygonCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.addPolygonCmdFactory1());
	}

	@Override
	public void stop() {
		GWT.log("AddPolygonCommandFactory.stop()");
		if (polygon != null) {
			OMSVGElement parent = owner.getTwinGroup();
			if (polygon != null) {
				parent.removeChild(polygon);		
				polygon = null;
			}
			points = null;
			owner = null;
		}
		super.stop();
	}


	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		if (polygon == null) {
			owner = VectomaticApp2.getApp().getActiveModel();
			OMSVGElement parent = owner.getTwinGroup();
			polygon = new OMSVGPolygonElement();
			points = polygon.getPoints();
			applyCssContextStyle((SVGElement) polygon.getElement().cast());
//			polygon.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
//			polygon.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
			polygon.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
			parent.appendChild(polygon);
			points.appendItem(owner.getCoordinates(event, true));
		}
		points.appendItem(owner.getCoordinates(event, true));
		int count = points.getNumberOfItems();
		if (count > 2 && points.getItem(count - 1).substract(points.getItem(count - 3), owner.getSvgElement().createSVGPoint()).length() < 3) {
			points.removeItem(count - 1);
			points.removeItem(count - 2);
//			polygon.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_LIGHTCORAL_VALUE);
			OMSVGPolygonElement newPolygon = polygon;
			polygon = null;
			createCommand(newPolygon);
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
