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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.OMSVGPolylineElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGLineElement;
import org.vectomatic.dom.svg.impl.SVGRectElement;
import org.vectomatic.dom.svg.itf.ISVGAnimatedPoints;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.ScalingEvent;
import org.vectomatic.svg.edit.client.event.ScalingHandler;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.model.svg.SVGAnimatedPointsModelBase;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPoints;
import org.vectomatic.svg.edit.client.model.svg.SVGPointsStore;
import org.vectomatic.svg.edit.client.model.svg.SVGPointsStore.SVGPoint;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 2D manipulator class to edit polyline and polygon geometry.
 */
public class EditSVGPointsManipulator extends EditManipulatorBase implements SelectionChangedProcessor<SVGPoint>, ScalingHandler {
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		VERTEX {
			public boolean consumeEvent() { return true; }
		};
		public abstract boolean consumeEvent();
	}
	/**
	 * The mode the manipulator is presently using
	 */
	protected Mode mode;
	/**
	 * The group where line segments are nested.
	 */
	protected OMSVGGElement lHandleGroup;
	/**
	 * The group where vertices are nested.
	 */
	protected OMSVGGElement vHandleGroup;
	/**
	 * The position editor handle (translates all the vertices).
	 * It is an instance of polygon or polyline
	 */
	protected OMSVGElement posHandle;
	protected Map<SVGPoint, OMSVGPoint> storeToManipulator;
	protected Map<OMSVGPoint, SVGPoint> manipulatorToStore;
	protected Map<SVGLineElement, OMSVGPoint> lineTo1stPoint;
	protected Map<SVGLineElement, OMSVGPoint> lineTo2ndPoint;
	protected Map<SVGRectElement, OMSVGPoint> vertexToPoint;
	protected Map<OMSVGPoint, SVGLineElement> pointToLine1st;
	protected Map<OMSVGPoint, SVGLineElement> pointToLine2nd;
	protected Map<OMSVGPoint, SVGRectElement> pointToVertex;
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
	 * 
	 */
	protected Collection<OMSVGPoint> selectedVertices;
	/**
	 * To catch selection changes
	 */
	protected SelectionChangedProxy<SVGPoint> selChangeProxy = new SelectionChangedProxy<SVGPoint>(this);
	/**
	 * Event registration for the scaling handler
	 */
	protected HandlerRegistration scalingHandlerReg;

	/**
	 * Constructor
	 */
	public EditSVGPointsManipulator() {
		storeToManipulator = new HashMap<SVGPoint, OMSVGPoint>();
		manipulatorToStore = new HashMap<OMSVGPoint, SVGPoint>();
		lineTo1stPoint = new HashMap<SVGLineElement, OMSVGPoint>();
		lineTo2ndPoint = new HashMap<SVGLineElement, OMSVGPoint>();
		vertexToPoint = new HashMap<SVGRectElement, OMSVGPoint>();
		pointToLine1st = new HashMap<OMSVGPoint, SVGLineElement>();
		pointToLine2nd = new HashMap<OMSVGPoint, SVGLineElement>();
		pointToVertex = new HashMap<OMSVGPoint, SVGRectElement>();
		selectedVertices = new ArrayList<OMSVGPoint>();
	}

	/**
	 * Binds this manipulator to the specified
	 * ISVGAnimatedPoints instance.
	 * @param element
	 * The ISVGAnimatedPoints instance this manipulator is applied to.
	 * @return The root element of the manipulator
	 */
	@Override
	public OMSVGElement bind(Record record) {
		this.record = record;
		SVGAnimatedPointsModelBase model = (SVGAnimatedPointsModelBase) record.getModel();
		scalingHandlerReg = model.getOwner().addScalingHandler(this);
		this.mode = Mode.PASSIVE;
		// Create the graphical representations for the manipulator
		// The manipulator has the following SVG structure
		// <g>
		//  <polygon/> or <polyline/>   position
		//  <g>
		//   <line/>   vertex1-vertex2
		//   <line/>   vertex2-vertex3
		//   ...
		//   <line/>   vertexN-vertex1
		//  </g>
		//  <g>
		//   <rect/>   vertex1
		//   <rect/>   vertex2
		//   ...
		//   <rect/>   vertexN
		//  </g>
		// </g>
		OMSVGElement element = model.getElementWrapper();
		svg = element.getOwnerSVGElement();
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		g = document.createSVGGElement();
		g.setClassNameBaseVal(AppBundle.INSTANCE.css().animatedPointsGeometryManipulator());
		posHandle = (element instanceof OMSVGPolylineElement) ? document.createSVGPolylineElement() : document.createSVGPolygonElement();
		lHandleGroup = document.createSVGGElement();

		vHandleGroup = document.createSVGGElement();
		g.appendChild((OMSVGElement)posHandle);
		g.appendChild(lHandleGroup);
		g.appendChild(vHandleGroup);
		monitorModel = true;
		model.addChangeListener(this);
		getSelectionModel().addSelectionChangedListener(selChangeProxy);
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
			SVGAnimatedPointsModelBase model = (SVGAnimatedPointsModelBase) record.getModel();
			model.removeChangeListener(this);
			getSelectionModel().removeSelectionListener(selChangeProxy);
			record = null;
			g = null;
			posHandle = null;
			lHandleGroup = null;
			vHandleGroup = null;
			svg = null;
			mode = Mode.PASSIVE;
			storeToManipulator.clear();
			manipulatorToStore.clear();
			lineTo1stPoint.clear();
			lineTo2ndPoint.clear();
			vertexToPoint.clear();
			pointToLine1st.clear();
			pointToLine2nd.clear();
			pointToVertex.clear();
			scalingHandlerReg.removeHandler();
		}
	}
	
	@Override
	public void modelChanged(ChangeEvent event) {
		GWT.log("SVGPointsManipulator.modelChanged");
		if (monitorModel && record != null) {
			storeToManipulator.clear();
			manipulatorToStore.clear();
			lineTo1stPoint.clear();
			lineTo2ndPoint.clear();
			vertexToPoint.clear();
			pointToLine1st.clear();
			pointToLine2nd.clear();
			pointToVertex.clear();
			
			SVGAnimatedPointsModelBase model = (SVGAnimatedPointsModelBase) record.getModel();
			OMSVGElement element = model.getElementWrapper();
			if (element.hasAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE)) {
				g.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, element.getAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
			}
			OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
			SVGPointsStore store = model.getPointsStore();
			
			// Create the polygonal translate handle
			boolean isPolyline = (element instanceof OMSVGPolylineElement);
			OMSVGElement posHandle = isPolyline ? document.createSVGPolylineElement() : document.createSVGPolygonElement();
			posHandle.setAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE, element.getAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE));
			OMSVGPointList posHandlePoints = ((ISVGAnimatedPoints)posHandle).getPoints();

			// Map model points to manipulator points
			for (int i = 0, count = store.getCount(); i < count; i++) {
				SVGPoint p = store.getAt(i);
				OMSVGPoint q = posHandlePoints.getItem(i);
				storeToManipulator.put(p, q);
				manipulatorToStore.put(q, p);
			}
			
			// Create the line handles
			OMSVGGElement lHandleGroup = document.createSVGGElement();
			for (int i = 0, count = posHandlePoints.getNumberOfItems() - (isPolyline ? 1 : 0); i < count; i++) {
				OMSVGPoint q1 = posHandlePoints.getItem(i);
				OMSVGPoint q2 = posHandlePoints.getItem((i + 1) % (isPolyline ? (count + 1) : count));
				OMSVGLineElement line = document.createSVGLineElement();
				lHandleGroup.appendChild(line);
				lineTo1stPoint.put((SVGLineElement)line.getElement().cast(), q1);
				lineTo2ndPoint.put((SVGLineElement)line.getElement().cast(), q2);
				pointToLine1st.put(q1, (SVGLineElement)line.getElement().cast());
				pointToLine2nd.put(q2, (SVGLineElement)line.getElement().cast());
			}

			// Create the vertex handles
			OMSVGGElement vHandleGroup = document.createSVGGElement();
			for (int i = 0, count = posHandlePoints.getNumberOfItems(); i < count; i++) {
				OMSVGPoint q = posHandlePoints.getItem(i);
				OMSVGRectElement vertex = document.createSVGRectElement();
				vertexToPoint.put((SVGRectElement)vertex.getElement().cast(), q);
				pointToVertex.put(q, (SVGRectElement)vertex.getElement().cast());
				vHandleGroup.appendChild(vertex);
			}
			
			g.replaceChild(posHandle, this.posHandle);
			this.posHandle = posHandle;
			g.replaceChild(lHandleGroup, this.lHandleGroup);
			this.lHandleGroup = lHandleGroup;
			g.replaceChild(vHandleGroup, this.vHandleGroup);
			this.vHandleGroup = vHandleGroup;
			
			update(pointToVertex.keySet());
			processSelectionChanged(null);
		}
	}
	
	private void update(Collection<OMSVGPoint> points) {
		float hs = SVGModel.getVertexSize((SVGElementModel) record.getModel());
		GWT.log("hs=" + hs);
		GWT.log("points.size=" + points.size());
		for (OMSVGPoint q : points) {
			SVGRectElement v = getVertex(q);
			v.getX().getBaseVal().setValue(q.getX() - hs);
			v.getY().getBaseVal().setValue(q.getY() - hs);
			v.getWidth().getBaseVal().setValue(2 * hs);
			v.getHeight().getBaseVal().setValue(2 * hs);

			SVGLineElement line = pointToLine1st.get(q);
			line.getX1().getBaseVal().setValue(q.getX());
			line.getY1().getBaseVal().setValue(q.getY());
			line = pointToLine2nd.get(q);
			line.getX2().getBaseVal().setValue(q.getX());
			line.getY2().getBaseVal().setValue(q.getY());
		}
	}
	
	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<SVGPoint> se) {
		List<SVGPoint> selection = (se == null) ? null : se.getSelection();
		GWT.log("SVGPointsManipulator.processSelectionChanged" + selection);
		Set<SVGPoint> selected = (selection != null) ? new HashSet<SVGPoint>(selection) : new HashSet<SVGPoint>();
		for (SVGRectElement v : vertexToPoint.keySet()) {
			setSelected(v, selected.contains(getModelPoint(v)));
//			GWT.log(getModelPoint(v) + " --->  " + selected.contains(getModelPoint(v)) + " ---> " + isSelected(v));
		}
		for (SVGLineElement l : lineTo1stPoint.keySet()) {
			setSelected(l, isSelected(getVertex(lineTo1stPoint.get(l))) && isSelected(getVertex(lineTo2ndPoint.get(l))));
		}
		return true;
	}

	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		GWT.log("begin processMouseUp");
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			monitorModel = false;
			record.set(SVGConstants.SVG_POINTS_ATTRIBUTE, new SVGPoints((SVGElement)posHandle.getElement().cast()));
			record.commit(false);
			monitorModel = true;
		}
		GWT.log("end processMouseUp");
		return true;
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		selectedVertices.clear();
		JavaScriptObject target = event.getNativeEvent().getEventTarget();
		OMSVGElement element = OMNode.convert((Node)target.cast());
		m = g.getScreenCTM().inverse();
		p0 = getCoordinates(event, m);
		if (target == ((OMSVGElement)posHandle).getElement()) {
			mode = Mode.VERTEX;
			selectedVertices.addAll(pointToVertex.keySet());
		} else {
			GridSelectionModel<SVGPoint> selectionModel = getSelectionModel();
			
			if (element instanceof OMSVGRectElement) {
				// Click on a vertex
				SVGRectElement v = (SVGRectElement)target.cast();
				SVGPoint p = getModelPoint(v);
				boolean selected = selectionModel.isSelected(p);
				if (event.getNativeEvent().getShiftKey()) {
					selectionModel.select(true, p);
					mode = Mode.VERTEX;
				} else if (event.getNativeEvent().getCtrlKey()) {
					if (selected) {
						selectionModel.deselect(p);
					} else {
						selectionModel.select(true, p);
						mode = Mode.VERTEX;
					}
				} else {
					if (!selected) {
						selectionModel.select(false, p);
					}
					mode = Mode.VERTEX;
				}
			} else if (element instanceof OMSVGLineElement) {
				// Click on a line segment
				SVGLineElement l = (SVGLineElement)target.cast();
				List<SVGPoint> plist = getModelEndpoints(l);
				boolean selected = selectionModel.isSelected(plist.get(0)) && selectionModel.isSelected(plist.get(1));
				if (event.getNativeEvent().getShiftKey()) {
					selectionModel.select(plist, true);
					mode = Mode.VERTEX;
				} else if (event.getNativeEvent().getCtrlKey()) {
					if (selected) {
						selectionModel.deselect(plist);
					} else {
						selectionModel.select(plist, true);
						mode = Mode.VERTEX;
					}
				} else {
					if (!selected) {
						selectionModel.select(plist, false);
					}
					mode = Mode.VERTEX;
				}
			}
			if (mode == Mode.VERTEX) {
				for (SVGPoint p : getSelectionModel().getSelectedItems()) {
					selectedVertices.add(getManipulatorPoint(p));
				}
			}
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
			for (OMSVGPoint q : selectedVertices) {
				q.add(delta);
			}
			p1.assignTo(p0);
			update(selectedVertices);
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}

	@Override
	public boolean processKeyPress(ComponentEvent event) {
		GWT.log("SVGPointsManipulator.onKeyDown");
		int code = event.getKeyCode();
		SVGPointsStore store = getStore();
		if ((code == KeyCodes.KEY_DELETE || code == KeyCodes.KEY_BACKSPACE) && store.canRemoveSelectedPoints()) {
			store.removeSelectedPoints();
			return true;
		}
		return false;
	}
	
	private SVGPoint getModelPoint(SVGRectElement v) {
		OMSVGPoint q = vertexToPoint.get(v);
		assert q != null;
		return getModelPoint(q);
	}
	private SVGPoint getModelPoint(OMSVGPoint q) {
		SVGPoint p = manipulatorToStore.get(q);
		assert p != null;
		return p;
	}
	private SVGRectElement getVertex(OMSVGPoint q) {
		SVGRectElement r = pointToVertex.get(q);
		assert r != null;
		return r;
	}
	private List<SVGPoint> getModelEndpoints(SVGLineElement l) {
		List<SVGPoint> endpoints = new ArrayList<SVGPoint>();
		OMSVGPoint q1 = lineTo1stPoint.get(l);
		assert q1 != null;
		OMSVGPoint q2 = lineTo2ndPoint.get(l);
		assert q2 != null;
		endpoints.add(getModelPoint(q1));
		endpoints.add(getModelPoint(q2));
		return endpoints;
	}
	public OMSVGPoint getManipulatorPoint(SVGPoint p) {
		OMSVGPoint q = storeToManipulator.get(p);
		assert q != null;
		return q;
	}
	
	private void setSelected(SVGElement element, boolean selected) {
		element.getClassName_().setBaseVal(
			selected ? AppBundle.INSTANCE.css().animatedPointSelected() :
				AppBundle.INSTANCE.css().animatedPointUnselected());
	}

	private boolean isSelected(SVGElement element) {
		return AppBundle.INSTANCE.css().animatedPointSelected().equals(element.getClassName_().getBaseVal());
	}
	
	private SVGPointsStore getStore() {
		return ((SVGAnimatedPointsModelBase) record.getModel()).getPointsStore();
	}
	private GridSelectionModel<SVGPoint> getSelectionModel() {
		return getStore().getSelectionModel();
	}

	@Override
	public void onScale(ScalingEvent event) {
		update(pointToVertex.keySet());
	}
}
