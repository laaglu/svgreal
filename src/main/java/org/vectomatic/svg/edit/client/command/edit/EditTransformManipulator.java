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

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.itf.ISVGLocatable;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.Transformation;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * 2D manipulator class to edit an SVG transform.
 * The class makes the strong hypothesis that the transform
 * can be decomposed as the matrix product: t.r.s.tinv.
 * It provides graphical handles which enable t,r and s to
 * be manipulated with the mouse.
 * @author Lukas Laag (laaglu)
 */
public class EditTransformManipulator extends EditManipulatorBase {
	public static EditTransformManipulator INSTANCE = new EditTransformManipulator();
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		SCALE {
			public boolean consumeEvent() { return true; }
		},
		ROTATE {
			public boolean consumeEvent() { return true; }
		},
		TRANSLATE {
			public boolean consumeEvent() { return true; }
		};
		public abstract boolean consumeEvent();
	}
	/**
	 * The mode the manipulator is presently using
	 */
	protected Mode mode;
	/**
	 * The group where graphical SVG elements representing this
	 * manipulator are nested
	 */
	protected OMSVGGElement g0;
	/**
	 * The current transformed, decomposed as a matrix
	 * product of the form T.S.R.Tinv
	 */
	protected Transformation decomposition;
	/**
	 * The bounding box of the manipulated element
	 */
	protected OMSVGRect bbox;
	/**
	 * The translation handle
	 */
	protected OMSVGRectElement translateHandle;
	/**
	 * The top-left scale handle
	 */
	protected OMSVGRectElement scaleHandle1;
	/**
	 * The bottom-left scale handle
	 */
	protected OMSVGRectElement scaleHandle2;
	/**
	 * The top-right scale handle
	 */
	protected OMSVGRectElement scaleHandle3;
	/**
	 * The bottom-right scale handle
	 */
	protected OMSVGRectElement scaleHandle4;
	/**
	 * The rotation handle
	 */
	protected OMSVGCircleElement rotateHandle;
	/**
	 * The mousedown point
	 */
	protected OMSVGPoint p0;
	/**
	 * The translation when a mousedown event occurs
	 */
	protected OMSVGPoint t0;
	/**
	 * The scaling when a mousedown event occurs
	 */
	protected OMSVGPoint s0;
	/**
	 * The rotation when a mousedown event occurs
	 */
	protected float r0;
	/**
	 * The transform from screen coordinates to
	 * manipulator coordinates when a mousedown event occurs
	 */
	protected OMSVGMatrix m0;
	/**
	 * Vector from the mousedown point to the manipulator handle hotspot
	 */
	protected OMSVGPoint delta;
	
	/**
	 * Binds this manipulator to the specified transformable SVG element.
	 * @param element
	 * The transformable SVG element this manipulator is applied to.
	 * @return The root element of the manipulator.
	 */
	@Override
	public OMSVGElement bind(Record record) {
		this.record = record;
		SVGElementModel model = (SVGElementModel) record.getModel();
		this.mode = Mode.PASSIVE;
		
		// Create the graphical representations for the manipulator
		// The manipulator has the following SVG structure
		// <g transform="parent.transform">
		//  <g transform="t.r.s.tinv">
		//   <rect/> translation handle
		//   <g>
		//    <rect/> scaling handles
		//    <rect/>
		//    <rect/>
		//    <rect/>
		//   </g>
		//   <line>
		//   <circle> rotation handle
		//  </g>
		// </g>
		OMSVGElement element = model.getElementWrapper();
		
		OMSVGElement clone = (OMSVGElement) element.cloneNode(true);
		clone.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
		ISVGTransformable transformable = (ISVGTransformable)element;
		svg = ((OMSVGElement)transformable).getOwnerSVGElement();
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		bbox = transformable.getBBox();
		GWT.log(bbox.getDescription());
		
		g0 = document.createSVGGElement();
		g0.setClassNameBaseVal(AppBundle.INSTANCE.css().transformManipulator());
		g0.appendChild(clone);
		translateHandle = document.createSVGRectElement(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight(), 0, 0);
		// Handle size
		float hs = Math.min(bbox.getWidth(), bbox.getHeight()) * 0.2f;
		OMSVGGElement scaleHandleGroup = document.createSVGGElement();
		scaleHandle1 = document.createSVGRectElement(bbox.getX(), bbox.getY(), hs, hs, 0, 0);
		scaleHandle2 = document.createSVGRectElement(bbox.getX(), bbox.getMaxY() - hs, hs, hs, 0, 0);
		scaleHandle3 = document.createSVGRectElement(bbox.getMaxX() - hs, bbox.getY(), hs, hs, 0, 0);
		scaleHandle4 = document.createSVGRectElement(bbox.getMaxX() - hs, bbox.getMaxY() - hs, hs, hs, 0, 0);
		float rhr = (float)Math.sqrt(2 * hs * hs) * 0.5f;
		OMSVGLineElement line = document.createSVGLineElement(bbox.getMaxX(), bbox.getCenterY(), bbox.getMaxX() + 0.25f * bbox.getWidth() - rhr, bbox.getCenterY());
		rotateHandle = document.createSVGCircleElement(bbox.getMaxX() + 0.25f * bbox.getWidth(), bbox.getCenterY(), rhr);
		g0.appendChild(translateHandle);
		g0.appendChild(scaleHandleGroup);
		scaleHandleGroup.appendChild(scaleHandle1);
		scaleHandleGroup.appendChild(scaleHandle2);
		scaleHandleGroup.appendChild(scaleHandle3);
		scaleHandleGroup.appendChild(scaleHandle4);
		g0.appendChild(line);
		g0.appendChild(rotateHandle);

		// Compute the parent transform
		OMSVGElement parent = OMNode.convert(model.getElement().getParentNode());
		OMSVGMatrix xform = model.getOwner().getElementGroup().getTransformToElement(parent).inverse();
		g = document.createSVGGElement();
		g.getTransform().getBaseVal().appendItem(svg.createSVGTransformFromMatrix(xform));
		g.appendChild(g0);

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
			Element parent = g0.getElement().getParentElement();
			if (parent != null) {
				parent.removeChild(g0.getElement());
			}
			SVGElementModel model = (SVGElementModel) record.getModel();
			model.removeChangeListener(this);
			record = null;
			g = null;
			bbox = null;
			decomposition = null;
			translateHandle = null;
			rotateHandle = null;
			scaleHandle1 = null;
			scaleHandle2 = null;
			scaleHandle3 = null;
			scaleHandle4 = null;
			mode = Mode.PASSIVE;
		}
	}
	
	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			SVGElementModel model = (SVGElementModel)record.getModel();
			
			// Decompose the object transform
			g0.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, model.getElementWrapper().getAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
			decomposition = Transformation.decompose(g0);
			GWT.log(decomposition.toString());
		}
	}

	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		GWT.log("begin processMouseUp");
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			monitorModel = false;
			record.set(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, g0.getAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
			record.commit(false);
			monitorModel = true;
		}
		GWT.log("end processMouseUp");
		return true;
	}
	
	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		JavaScriptObject target = event.getNativeEvent().getEventTarget();
		if (target == translateHandle.getElement()) {
			mode = Mode.TRANSLATE;
	        m0 = ((ISVGLocatable) g0.getParentNode()).getScreenCTM().inverse();
			t0 = svg.createSVGPoint(decomposition.getT().getMatrix().getE(), decomposition.getT().getMatrix().getF());
			p0 = getCoordinates(event, m0);
		}
		if (target == rotateHandle.getElement()) {
			mode = Mode.ROTATE;
			m0 = g0.getScreenCTM().inverse();
			r0 = decomposition.getR().getAngle();
			p0 = getCoordinates(event, m0);
			OMSVGPoint hcenter = svg.createSVGPoint(rotateHandle.getCx().getBaseVal().getValue(), rotateHandle.getCy().getBaseVal().getValue());
			delta = p0.substract(hcenter, svg.createSVGPoint());
		}
		if (target == scaleHandle1.getElement() || target == scaleHandle2.getElement() || target == scaleHandle3.getElement() || target == scaleHandle4.getElement()) {
			mode = Mode.SCALE;
			m0 = g0.getScreenCTM().inverse();
			// Compensate for the fact that the user does not click at the
			// center of the scale handle. delta with store the vector from
			// the mousedown point to the handle hotspot
			delta = getCoordinates(event, m0);
			if (target == scaleHandle1.getElement()) {
				p0 = svg.createSVGPoint(bbox.getX(), bbox.getY());
				GWT.log("h1");
			} else if (target == scaleHandle2.getElement()) {
				p0 = svg.createSVGPoint(bbox.getX(), bbox.getMaxY());
				GWT.log("h2");
			} else if (target == scaleHandle3.getElement()) {
				p0 = svg.createSVGPoint(bbox.getMaxX(), bbox.getY());
				GWT.log("h3");
			} else {
				p0 = svg.createSVGPoint(bbox.getMaxX(), bbox.getMaxY());
				GWT.log("h4");
			}
			delta.substract(p0);
			s0 = svg.createSVGPoint(decomposition.getS().getMatrix().getA(), decomposition.getS().getMatrix().getD());
		}
		if (mode.consumeEvent()) {
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}
	
	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		switch(mode) {
			case TRANSLATE:
				{
					OMSVGPoint d = getCoordinates(event, m0).substract(p0);
					decomposition.getT().setTranslate(t0.getX() + d.getX(), t0.getY() + d.getY());
				}
			break;
			case SCALE:
				{
					// Compensate for the fact that the user does not click at the
					// center of the scale handle
					OMSVGPoint p = getCoordinates(event, m0).substract(delta);
					
					// The new scale is given by the ratio between a vector from the
					// mousemove point to the bbox dimensions
					OMSVGPoint center = svg.createSVGPoint(bbox.getCenterX(), bbox.getCenterY());
					p.substract(center);
					
					// Determine the sign of the scaling transform
					float signx = Math.signum(svg.createSVGPoint(p0.getX() - bbox.getCenterX(), 0f).dotProduct(p));
					float signy = Math.signum(svg.createSVGPoint(0f, p0.getY() - bbox.getCenterY()).dotProduct(p));
					float signx0 = Math.signum(s0.getX());
					float signy0 = Math.signum(s0.getY());
					
					GWT.log("p " + p.getDescription() + " " + signx + " " + signy + " " + signx0 + " " + signy0);
					decomposition.getS().setScale(signx * s0.getX() * 2f * Math.abs(p.getX()) / bbox.getWidth(), signy * s0.getY() * 2f * Math.abs(p.getY()) / bbox.getHeight());
				}
			break;
			case ROTATE:
				{
					// Compensate for the fact that the user does not click at the
					// center of the rotation handle
					OMSVGPoint p = getCoordinates(event, m0).substract(delta);
					
					// Compute the vector from the mousemove point to the bbox center
					OMSVGPoint center = svg.createSVGPoint(bbox.getCenterX(), bbox.getCenterY());
					p.substract(center);
					double a = Math.signum(m0.getA() * m0.getD()) * Math.acos(p.getX() / p.length());
					if (p.getY() < 0) {
						a = 2 * Math.PI - a;
					}
					decomposition.getR().setRotate((float)(a * 180f / Math.PI) + r0, 0, 0);
				}
			break;
		}
		return true;
	}
}