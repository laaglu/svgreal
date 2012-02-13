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

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.ScalingEvent;
import org.vectomatic.svg.edit.client.event.ScalingHandler;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;
import org.vectomatic.svg.edit.client.model.svg.SVGLineElementModel;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 2D manipulator class to edit line segment geometry.
 */
public class EditLineGeometryManipulator extends EditManipulatorBase implements ScalingHandler {
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		LINE {
			public boolean consumeEvent() { return true; }
		},
		P1 {
			public boolean consumeEvent() { return true; }
		},
		P2 {
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
	protected OMSVGLineElement posHandle;
	/**
	 * The endpoint handles
	 */
	protected OMSVGRectElement p1Handle, p2Handle;
	/**
	 * The transform from screen coordinates to
	 * manipulator coordinates when a mousedown event occurs
	 */
	protected OMSVGMatrix m;
	/**
	 * The mousedown point in user space
	 */
	protected OMSVGPoint p0;
	/**
	 * Event registration for the scaling handler
	 */
	protected HandlerRegistration scalingHandlerReg;

	/**
	 * Constructor
	 */
	public EditLineGeometryManipulator() {
	}

	/**
	 * Binds this manipulator to the specified SVG line.
	 * @param element
	 * The SVG line this manipulator is applied to.
	 * @return The root element of the manipulator
	 */
	@Override
	public OMSVGElement bind(Record record) {
		try {
			this.record = record;
			SVGLineElementModel model = (SVGLineElementModel) record.getModel();
			scalingHandlerReg = model.getOwner().addScalingHandler(this);
			mode = Mode.PASSIVE;
			// Create the graphical representations for the manipulator
			// The manipulator has the following SVG structure
			// <g>
			//  <line/>    line handle
			//  <rect/>    first handle
			//  <rect/>    second handle
			// </g>
			OMSVGLineElement line = (OMSVGLineElement) model.getElementWrapper();
			svg = line.getOwnerSVGElement();
			OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
			g = document.createSVGGElement();
			g.setClassNameBaseVal(AppBundle.INSTANCE.css().lineGeometryManipulator());
			posHandle = document.createSVGLineElement();
			p1Handle = document.createSVGRectElement();
			p2Handle = document.createSVGRectElement();
			g.appendChild(posHandle);
			g.appendChild(p1Handle);
			g.appendChild(p2Handle);
			
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
			SVGLineElementModel model = (SVGLineElementModel) record.getModel();
			model.removeChangeListener(this);
			record = null;
			g = null;
			posHandle = null;
			p1Handle = null;
			p2Handle = null;
			mode = Mode.PASSIVE;
			scalingHandlerReg.removeHandler();
		}
	}

	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			SVGLineElementModel model = (SVGLineElementModel) record.getModel();
			super.modelChanged(event);
			SVGLength x1 = model.get(SVGConstants.SVG_X1_ATTRIBUTE);
			SVGLength y1 = model.get(SVGConstants.SVG_Y1_ATTRIBUTE);
			SVGLength x2 = model.get(SVGConstants.SVG_X2_ATTRIBUTE);
			SVGLength y2 = model.get(SVGConstants.SVG_Y2_ATTRIBUTE);
			posHandle.getX1().getBaseVal().newValueSpecifiedUnits(x1.getUnit(), x1.getValue());
			posHandle.getY1().getBaseVal().newValueSpecifiedUnits(y1.getUnit(), y1.getValue());
			posHandle.getX2().getBaseVal().newValueSpecifiedUnits(x2.getUnit(), x2.getValue());
			posHandle.getY2().getBaseVal().newValueSpecifiedUnits(y2.getUnit(), y2.getValue());
			update();
		}
	}

	private void update() {
		float x1 = posHandle.getX1().getBaseVal().getValue();
		float y1 = posHandle.getY1().getBaseVal().getValue();
		float x2 = posHandle.getX2().getBaseVal().getValue();
		float y2 = posHandle.getY2().getBaseVal().getValue();
		float hs = SVGModel.getVertexSize((SVGElementModel) record.getModel());
		p1Handle.getX().getBaseVal().setValue(x1 - hs);
		p1Handle.getY().getBaseVal().setValue(y1 - hs);
		p1Handle.getWidth().getBaseVal().setValue(hs * 2);
		p1Handle.getHeight().getBaseVal().setValue(hs * 2);
		p2Handle.getX().getBaseVal().setValue(x2 - hs);
		p2Handle.getY().getBaseVal().setValue(y2 - hs);
		p2Handle.getWidth().getBaseVal().setValue(hs * 2);
		p2Handle.getHeight().getBaseVal().setValue(hs * 2);
	}

	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		GWT.log("begin processMouseUp");
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			monitorModel = false;
			record.beginEdit();
			record.set(SVGConstants.SVG_X1_ATTRIBUTE, new SVGLength(posHandle.getX1().getBaseVal()));
			record.set(SVGConstants.SVG_Y1_ATTRIBUTE, new SVGLength(posHandle.getY1().getBaseVal()));
			record.set(SVGConstants.SVG_X2_ATTRIBUTE, new SVGLength(posHandle.getX2().getBaseVal()));
			record.set(SVGConstants.SVG_Y2_ATTRIBUTE, new SVGLength(posHandle.getY2().getBaseVal()));
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
		p0 = getCoordinates(event, m);
		if (target == posHandle.getElement()) {
			mode = Mode.LINE;
		} else if (target == p1Handle.getElement()) {
			mode = Mode.P1;
		} else if (target == p2Handle.getElement()) {
			mode = Mode.P2;
		}
		if (mode.consumeEvent()) {
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}

	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (mode.consumeEvent()) {
			OMSVGPoint p1 = getCoordinates(event, m);
			OMSVGPoint delta = p1.substract(p0, svg.createSVGPoint());
			OMSVGLength x1 = posHandle.getX1().getBaseVal();
			OMSVGLength y1 = posHandle.getY1().getBaseVal();
			OMSVGLength x2 = posHandle.getX2().getBaseVal();
			OMSVGLength y2 = posHandle.getY2().getBaseVal();
			switch(mode) {
				case LINE:
					x1.setValue(x1.getValue() + delta.getX());
					y1.setValue(y1.getValue() + delta.getY());
					x2.setValue(x2.getValue() + delta.getX());
					y2.setValue(y2.getValue() + delta.getY());
					break;
				case P1:
					x1.setValue(x1.getValue() + delta.getX());
					y1.setValue(y1.getValue() + delta.getY());
					break;
				case P2:
					x2.setValue(x2.getValue() + delta.getX());
					y2.setValue(y2.getValue() + delta.getY());
					break;
			}
			p1.assignTo(p0);
			update();
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}

	@Override
	public void onScale(ScalingEvent event) {
		update();
	}
}
