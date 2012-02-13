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
package org.vectomatic.svg.edit.client.command.edit;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.model.svg.SVGCircleElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * 2D manipulator class to edit circle geometry.
 */
public class EditCircleGeometryManipulator extends EditManipulatorBase {
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		POS {
			public boolean consumeEvent() { return true; }
		},
		R {
			public boolean consumeEvent() { return true; }
		};
		public abstract boolean consumeEvent();
	}
	/**
	 * The mode the manipulator is presently using
	 */
	protected Mode mode;
	/**
	 * The (x,y) editor handle
	 */
	protected OMSVGCircleElement posHandle;
	/**
	 * The radius editor handle
	 */
	protected OMSVGCircleElement rHandle;
	/**
	 * The transform from screen coordinates to
	 * manipulator coordinates when a mousedown event occurs
	 */
	protected OMSVGMatrix m;
	/**
	 * Vector from the mousedown point to the manipulator handle hotspot
	 */
	protected OMSVGPoint delta;

	/**
	 * Constructor
	 */
	public EditCircleGeometryManipulator() {
	}

	/**
	 * Binds this manipulator to the specified SVG circle.
	 * @param element
	 * The SVG circle this manipulator is applied to.
	 * @return The root element of the manipulator
	 */
	@Override
	public OMSVGElement bind(Record record) {
		try {
			this.record = record;
			SVGCircleElementModel model = (SVGCircleElementModel) record.getModel();
			mode = Mode.PASSIVE;
			// Create the graphical representations for the manipulator
			// The manipulator has the following SVG structure
			// <g>
			//  <circle/>    position
			//  <g>
			//   <circle/>   r
			//  </g>
			// </g>
			OMSVGCircleElement circle = (OMSVGCircleElement) model.getElementWrapper();
			svg = circle.getOwnerSVGElement();
			OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
			g = document.createSVGGElement();
			g.setClassNameBaseVal(AppBundle.INSTANCE.css().circleGeometryManipulator());
			posHandle = document.createSVGCircleElement();
			OMSVGGElement handleGroup = document.createSVGGElement();
			rHandle = document.createSVGCircleElement();
			g.appendChild(posHandle);
			g.appendChild(handleGroup);
			handleGroup.appendChild(rHandle);
			monitorModel = true;
			model.addChangeListener(this);
			scheduleInit();
			return g;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public void unbind() {
		if (g != null) {
			Element parent = g.getElement().getParentElement();
			if (parent != null) {
				parent.removeChild(g.getElement());
			}
			SVGCircleElementModel model = (SVGCircleElementModel) record.getModel();
			model.removeChangeListener(this);
			record = null;
			g = null;
			posHandle = null;
			rHandle = null;
			mode = Mode.PASSIVE;
		}
	}

	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			SVGCircleElementModel model = (SVGCircleElementModel) record.getModel();
			super.modelChanged(event);
			SVGLength cx = model.get(SVGConstants.SVG_CX_ATTRIBUTE);
			SVGLength cy = model.get(SVGConstants.SVG_CY_ATTRIBUTE);
			SVGLength r = model.get(SVGConstants.SVG_R_ATTRIBUTE);
			posHandle.getCx().getBaseVal().newValueSpecifiedUnits(cx.getUnit(), cx.getValue());
			posHandle.getCy().getBaseVal().newValueSpecifiedUnits(cy.getUnit(), cy.getValue());
			posHandle.getR().getBaseVal().newValueSpecifiedUnits(r.getUnit(), r.getValue());
			update();
		}
	}

	private void update() {
		float cx = posHandle.getCx().getBaseVal().getValue();
		float cy = posHandle.getCy().getBaseVal().getValue();
		GWT.log("cy=" + cy);
		float r = posHandle.getR().getBaseVal().getValue();
		GWT.log("r=" + r);
		float hs = Math.max(5, r * 0.2f);
		rHandle.getCx().getBaseVal().setValue(cx + r - hs);
		rHandle.getCy().getBaseVal().setValue(cy);
		rHandle.getR().getBaseVal().setValue(hs);
	}


	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		GWT.log("begin processMouseUp");
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			GWT.log("R = " + posHandle.getR().getBaseVal().getValue());
			monitorModel = false;
			record.beginEdit();
			record.set(SVGConstants.SVG_CX_ATTRIBUTE, new SVGLength(posHandle.getCx().getBaseVal()));
			record.set(SVGConstants.SVG_CY_ATTRIBUTE, new SVGLength(posHandle.getCy().getBaseVal()));
			record.set(SVGConstants.SVG_R_ATTRIBUTE, new SVGLength(posHandle.getR().getBaseVal()));
			record.endEdit();
			record.commit(false);
			monitorModel = true;
		}
		GWT.log("end processMouseUp");
		return true;
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		JavaScriptObject target = event.getNativeEvent().getEventTarget();
		m = g.getScreenCTM().inverse();
		delta = getCoordinates(event, m);
		float cx = posHandle.getCx().getBaseVal().getValue();
		float cy = posHandle.getCy().getBaseVal().getValue();
		float r = posHandle.getR().getBaseVal().getValue();
		OMSVGPoint p = svg.createSVGPoint();
		if (target == posHandle.getElement()) {
			mode = Mode.POS;
			p.setX(cx);
			p.setY(cy);
		} else if (target == rHandle.getElement()) {
			p.setX(cx + r);
			p.setY(cy);
			mode = Mode.R;
		}
		if (mode.consumeEvent()) {
			delta.substract(p);
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}
	
	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (mode.consumeEvent()) {
			OMSVGPoint p = getCoordinates(event, m).substract(delta);
			switch(mode) {
				case POS:
					{
						posHandle.getCx().getBaseVal().setValue(p.getX());
						posHandle.getCy().getBaseVal().setValue(p.getY());
					}
					break;
				case R:
					{
						float cx = posHandle.getCx().getBaseVal().getValue();
						posHandle.getR().getBaseVal().setValue(Math.abs(p.getX() - cx));
					}
					break;
			}
			update();
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}
}
