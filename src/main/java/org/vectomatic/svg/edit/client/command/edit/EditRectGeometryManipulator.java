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

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;
import org.vectomatic.svg.edit.client.model.svg.SVGRectElementModel;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * 2D manipulator class to edit rectangle geometry.
 */
public class EditRectGeometryManipulator extends EditManipulatorBase {
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		POS {
			public boolean consumeEvent() { return true; }
		},
		TOP_LEFT {
			public boolean consumeEvent() { return true; }
		},
		BOTTOM_RIGHT {
			public boolean consumeEvent() { return true; }
		},
		RADIUS_X {
			public boolean consumeEvent() { return true; }
		},
		RADIUS_Y {
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
	protected OMSVGRectElement posHandle;
	/**
	 * The top-left corner editor handle
	 */
	protected OMSVGRectElement topLeftHandle;
	/**
	 * The bottom right corner editor handle
	 */
	protected OMSVGRectElement bottomRightHandle;
	/**
	 * The corner x radius handle
	 */
	protected OMSVGCircleElement radiusXHandle;
	/**
	 * The corner y radius handle
	 */
	protected OMSVGCircleElement radiusYHandle;
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
	public EditRectGeometryManipulator() {
	}
	/**
	 * Binds this manipulator to the specified SVG rect.
	 * @param element
	 * The SVG rect this manipulator is applied to.
	 * @return The root element of the manipulator
	 */
	@Override
	public OMSVGElement bind(Record record) {
		this.record = record;
		SVGRectElementModel model = (SVGRectElementModel) record.getModel();
		mode = Mode.PASSIVE;
		// Create the graphical representations for the manipulator
		// The manipulator has the following SVG structure
		// <g>
		//  <rect/>    position
		//  <g>
		//   <rect/>   top-left corner
		//   <rect/>   bottom-right corner
		//   <circle/> rx handle
		//   <circle/> ry handle
		//  </g>
		// </g>
		OMSVGRectElement rect = (OMSVGRectElement) model.getElementWrapper();
		svg = rect.getOwnerSVGElement();
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		g = document.createSVGGElement();
		g.setClassNameBaseVal(AppBundle.INSTANCE.css().rectGeometryManipulator());
		posHandle = document.createSVGRectElement();
		OMSVGGElement handleGroup = document.createSVGGElement();
		topLeftHandle = document.createSVGRectElement();
		bottomRightHandle = document.createSVGRectElement();
		radiusXHandle = document.createSVGCircleElement();
		radiusYHandle = document.createSVGCircleElement();
		g.appendChild(posHandle);
		g.appendChild(handleGroup);
		handleGroup.appendChild(topLeftHandle);
		handleGroup.appendChild(bottomRightHandle);
		handleGroup.appendChild(radiusXHandle);
		handleGroup.appendChild(radiusYHandle);
		monitorModel = true;
		model.addChangeListener(this);
		scheduleInit();
		return g;
	}
	
	/**
	 * Detaches this manipulator from the DOM tree
	 */
	@Override
	public void unbind() {
		if (g != null) {
			Element parent = g.getElement().getParentElement();
			if (parent != null) {
				parent.removeChild(g.getElement());
			}
			SVGRectElementModel model = (SVGRectElementModel) record.getModel();
			model.removeChangeListener(this);
			record = null;
			g = null;
			posHandle = null;
			topLeftHandle = null;
			bottomRightHandle = null;
			radiusXHandle = null;
			radiusYHandle = null;
			mode = Mode.PASSIVE;
		}
	}
	
	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			SVGRectElementModel model = (SVGRectElementModel) record.getModel();
			if (model.getElement().hasAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE)) {
				g.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, model.getElement().getAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
			}
			SVGLength x = model.get(SVGConstants.SVG_X_ATTRIBUTE);
			SVGLength y = model.get(SVGConstants.SVG_Y_ATTRIBUTE);
			SVGLength width = model.get(SVGConstants.SVG_WIDTH_ATTRIBUTE);
			SVGLength height = model.get(SVGConstants.SVG_HEIGHT_ATTRIBUTE);
			SVGLength rx = model.get(SVGConstants.SVG_RX_ATTRIBUTE);
			SVGLength ry = model.get(SVGConstants.SVG_RY_ATTRIBUTE);
			posHandle.getX().getBaseVal().newValueSpecifiedUnits(x.getUnit(), x.getValue());
			posHandle.getY().getBaseVal().newValueSpecifiedUnits(y.getUnit(), y.getValue());
			posHandle.getWidth().getBaseVal().newValueSpecifiedUnits(width.getUnit(), width.getValue());
			posHandle.getHeight().getBaseVal().newValueSpecifiedUnits(height.getUnit(), height.getValue());
			posHandle.getRx().getBaseVal().newValueSpecifiedUnits(rx.getUnit(), rx.getValue());
			posHandle.getRy().getBaseVal().newValueSpecifiedUnits(ry.getUnit(), ry.getValue());
			update();
		}
	}
	
	private void update() {
		float x = posHandle.getX().getBaseVal().getValue();
		float y = posHandle.getY().getBaseVal().getValue();
		float width = posHandle.getWidth().getBaseVal().getValue();
		float height = posHandle.getHeight().getBaseVal().getValue();
		float rx = posHandle.getRx().getBaseVal().getValue();
		float ry = posHandle.getRy().getBaseVal().getValue();
		float hs = Math.max(5, Math.min(width, height) * 0.2f);
		topLeftHandle.getX().getBaseVal().setValue(x);
		topLeftHandle.getY().getBaseVal().setValue(y);
		topLeftHandle.getWidth().getBaseVal().setValue(hs);
		topLeftHandle.getHeight().getBaseVal().setValue(hs);
		bottomRightHandle.getX().getBaseVal().setValue(x + width - hs);
		bottomRightHandle.getY().getBaseVal().setValue(y + height - hs);
		bottomRightHandle.getWidth().getBaseVal().setValue(hs);
		bottomRightHandle.getHeight().getBaseVal().setValue(hs);
		radiusXHandle.getCx().getBaseVal().setValue(x + width - rx);
		radiusXHandle.getCy().getBaseVal().setValue(y + 0.5f * hs);
		radiusXHandle.getR().getBaseVal().setValue(0.5f * hs);
		radiusYHandle.getCx().getBaseVal().setValue(x + width - 0.5f * hs);
		radiusYHandle.getCy().getBaseVal().setValue(y + ry);
		radiusYHandle.getR().getBaseVal().setValue(0.5f * hs);
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			monitorModel = false;
			record.beginEdit();
			record.set(SVGConstants.SVG_X_ATTRIBUTE, new SVGLength(posHandle.getX().getBaseVal()));
			record.set(SVGConstants.SVG_Y_ATTRIBUTE, new SVGLength(posHandle.getY().getBaseVal()));
			record.set(SVGConstants.SVG_WIDTH_ATTRIBUTE, new SVGLength(posHandle.getWidth().getBaseVal()));
			record.set(SVGConstants.SVG_HEIGHT_ATTRIBUTE, new SVGLength(posHandle.getHeight().getBaseVal()));
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
		float x = posHandle.getX().getBaseVal().getValue();
		float y = posHandle.getY().getBaseVal().getValue();
		float width = posHandle.getWidth().getBaseVal().getValue();
		float height = posHandle.getHeight().getBaseVal().getValue();
		float rx = posHandle.getRx().getBaseVal().getValue();
		float ry = posHandle.getRy().getBaseVal().getValue();
		float hs = Math.max(5, Math.min(width, height) * 0.2f);
		OMSVGPoint p = svg.createSVGPoint();
		if (target == posHandle.getElement()) {
			mode = Mode.POS;
			p.setX(x);
			p.setY(y);
		} else if (target == topLeftHandle.getElement()) {
			p.setX(x);
			p.setY(y);
			mode = Mode.TOP_LEFT;
		} else if (target == bottomRightHandle.getElement()) {
			p.setX(x + width);
			p.setY(y + height);
			mode = Mode.BOTTOM_RIGHT;
		} else if (target == radiusXHandle.getElement()) {
			p.setX(x + width - rx);
			p.setY(y + 0.5f * hs);
			mode = Mode.RADIUS_X;
		} else if (target == radiusYHandle.getElement()) {
			p.setX(x + width - 0.5f * hs);
			p.setY(y + ry);
			mode = Mode.RADIUS_Y;
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
			float x = posHandle.getX().getBaseVal().getValue();
			float y = posHandle.getY().getBaseVal().getValue();
			float width = posHandle.getWidth().getBaseVal().getValue();
			float height = posHandle.getHeight().getBaseVal().getValue();
			OMSVGPoint p = getCoordinates(event, m).substract(delta);
			switch(mode) {
				case POS:
					{
						posHandle.getX().getBaseVal().setValue(p.getX());
						posHandle.getY().getBaseVal().setValue(p.getY());
					}
					break;
				case TOP_LEFT:
					{
						float xmax = Math.min(p.getX(), x + width);
						float ymax = Math.min(p.getY(), y + height);
						posHandle.getX().getBaseVal().setValue(xmax);
						posHandle.getY().getBaseVal().setValue(ymax);
						posHandle.getWidth().getBaseVal().setValue(width + x - xmax);
						posHandle.getHeight().getBaseVal().setValue(height + y - ymax);
					}
					break;
				case BOTTOM_RIGHT:
					{
						float xmin = Math.max(p.getX(), x);
						float ymin = Math.max(p.getY(), y);
						posHandle.getWidth().getBaseVal().setValue(xmin - x);
						posHandle.getHeight().getBaseVal().setValue(ymin - y);
					}
					break;
				case RADIUS_X:
					{
						float rx = Math.min(Math.max(0, x + width - p.getX()), 0.5f * width);
						posHandle.getRx().getBaseVal().setValue(rx);
					}
					break;
				case RADIUS_Y:
					{
						float ry = Math.min(Math.max(0, p.getY() - y), 0.5f * height);
						posHandle.getRy().getBaseVal().setValue(ry);
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
