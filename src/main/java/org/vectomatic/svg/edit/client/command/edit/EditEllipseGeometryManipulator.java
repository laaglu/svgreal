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
package org.vectomatic.svg.edit.client.command.edit;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGEllipseElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.model.svg.SVGEllipseElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * 2D manipulator class to edit ellipse geometry.
 */
public class EditEllipseGeometryManipulator extends EditManipulatorBase {
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		POS {
			public boolean consumeEvent() { return true; }
		},
		RX {
			public boolean consumeEvent() { return true; }
		},
		RY {
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
	protected OMSVGEllipseElement posHandle;
	/**
	 * The x-radius editor handle
	 */
	protected OMSVGRectElement rxHandle;
	/**
	 * The y-radius editor handle
	 */
	protected OMSVGRectElement ryHandle;
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
	public EditEllipseGeometryManipulator() {
	}

	/**
	 * Binds this manipulator to the specified SVG ellipse.
	 * @param element
	 * The SVG ellipse this manipulator is applied to.
	 * @return The root element of the manipulator
	 */
	@Override
	public OMSVGElement bind(Record record) {
		this.record = record;
		SVGEllipseElementModel model = (SVGEllipseElementModel)record.getModel();
		mode = Mode.PASSIVE;
		// Create the graphical representations for the manipulator
		// The manipulator has the following SVG structure
		// <g>
		//  <ellipse/>    position
		//  <g>
		//   <rect/>   cx
		//   <rect/>   cy
		//  </g>
		// </g>
		OMSVGEllipseElement ellipse = (OMSVGEllipseElement) model.getElementWrapper();
		svg = ellipse.getOwnerSVGElement();
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		g = document.createSVGGElement();
		g.setClassNameBaseVal(AppBundle.INSTANCE.css().ellipseGeometryManipulator());
		posHandle = document.createSVGEllipseElement();
		OMSVGGElement handleGroup = document.createSVGGElement();
		rxHandle = document.createSVGRectElement();
		ryHandle = document.createSVGRectElement();
		g.appendChild(posHandle);
		g.appendChild(handleGroup);
		handleGroup.appendChild(rxHandle);
		handleGroup.appendChild(ryHandle);
		monitorModel = true;
		model.addChangeListener(this);
		scheduleInit();
		return g;
	}

	@Override
	public void unbind() {
		if (g != null) {
			Element parent = g.getElement().getParentElement();
			if (parent != null) {
				parent.removeChild(g.getElement());
			}
			SVGEllipseElementModel model = (SVGEllipseElementModel) record.getModel();
			model.removeChangeListener(this);
			record = null;
			g = null;
			posHandle = null;
			rxHandle = null;
			ryHandle = null;
			mode = Mode.PASSIVE;
		}
	}
	
	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			SVGEllipseElementModel model = (SVGEllipseElementModel)record.getModel();
			if (model.getElement().hasAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE)) {
				g.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, model.getElement().getAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
			}
			SVGLength cx = model.get(SVGConstants.SVG_CX_ATTRIBUTE);
			SVGLength cy = model.get(SVGConstants.SVG_CY_ATTRIBUTE);
			SVGLength rx = model.get(SVGConstants.SVG_RX_ATTRIBUTE);
			SVGLength ry = model.get(SVGConstants.SVG_RY_ATTRIBUTE);
			posHandle.getCx().getBaseVal().newValueSpecifiedUnits(cx.getUnit(), cx.getValue());
			posHandle.getCy().getBaseVal().newValueSpecifiedUnits(cy.getUnit(), cy.getValue());
			posHandle.getRx().getBaseVal().newValueSpecifiedUnits(rx.getUnit(), rx.getValue());
			posHandle.getRy().getBaseVal().newValueSpecifiedUnits(ry.getUnit(), ry.getValue());
			update();
		}
	}

	private void update() {
		float cx = posHandle.getCx().getBaseVal().getValue();
		float cy = posHandle.getCy().getBaseVal().getValue();
		float rx = posHandle.getRx().getBaseVal().getValue();
		float ry = posHandle.getRy().getBaseVal().getValue();
		float hs = Math.max(5, Math.min(rx, ry) * 0.4f);
		rxHandle.getX().getBaseVal().setValue(cx - rx);
		rxHandle.getY().getBaseVal().setValue(cy - 0.5f * hs);
		rxHandle.getWidth().getBaseVal().setValue(hs);
		rxHandle.getHeight().getBaseVal().setValue(hs);
		ryHandle.getX().getBaseVal().setValue(cx - 0.5f * hs);
		ryHandle.getY().getBaseVal().setValue(cy - ry);
		ryHandle.getWidth().getBaseVal().setValue(hs);
		ryHandle.getHeight().getBaseVal().setValue(hs);
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			monitorModel = false;
			record.beginEdit();
			record.set(SVGConstants.SVG_CX_ATTRIBUTE, new SVGLength(posHandle.getCx().getBaseVal()));
			record.set(SVGConstants.SVG_CY_ATTRIBUTE, new SVGLength(posHandle.getCy().getBaseVal()));
			record.set(SVGConstants.SVG_RX_ATTRIBUTE, new SVGLength(posHandle.getRx().getBaseVal()));
			record.set(SVGConstants.SVG_RY_ATTRIBUTE, new SVGLength(posHandle.getRy().getBaseVal()));
			record.endEdit();
			record.commit(false);
			monitorModel = true;
		}
		return true;
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		JavaScriptObject target = event.getNativeEvent().getEventTarget();
		m = g.getScreenCTM().inverse();
		delta = getCoordinates(event, m);
		float cx = posHandle.getCx().getBaseVal().getValue();
		float cy = posHandle.getCy().getBaseVal().getValue();
		float rx = posHandle.getRx().getBaseVal().getValue();
		float ry = posHandle.getRy().getBaseVal().getValue();
		OMSVGPoint p = svg.createSVGPoint();
		if (target == posHandle.getElement()) {
			mode = Mode.POS;
			p.setX(cx);
			p.setY(cy);
		} else if (target == rxHandle.getElement()) {
			p.setX(cx - rx);
			p.setY(cy);
			mode = Mode.RX;
		} else if (target == ryHandle.getElement()) {
			p.setX(cx);
			p.setY(cy - ry);
			mode = Mode.RY;
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
				case RX:
					{
						float cx = posHandle.getCx().getBaseVal().getValue();
						posHandle.getRx().getBaseVal().setValue(Math.abs(p.getX() - cx));
					}
					break;
				case RY:
					{
						float cy = posHandle.getCy().getBaseVal().getValue();
						posHandle.getRy().getBaseVal().setValue(Math.abs(p.getY() - cy));
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
