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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegArcAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticAbs;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoAbs;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.path.IPathRepOwner;
import org.vectomatic.svg.edit.client.command.path.SVGCubicSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGLineSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGMoveSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGQuadraticSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGSegRep;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.ScalingEvent;
import org.vectomatic.svg.edit.client.event.ScalingHandler;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel;
import org.vectomatic.svg.edit.client.model.svg.path.SVGSegModel;
import org.vectomatic.svg.edit.client.model.svg.path.SVGSegStore;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 2D manipulator class to edit path geometry.
 */
public class EditPathGeometryManipulator extends EditManipulatorBase implements SelectionChangedProcessor<SVGSegModel>, IPathRepOwner, ScalingHandler {
	/**
	 * The path representation (differs from the actual svg path
	 * in the model so that the end user can see the difference
	 * between the actual model and its changes as they update the
	 * representation interactively).
	 */
	protected OMSVGPathElement path;
	/**
	 * The path segments
	 */
	protected List<SVGSegRep> segments;
	/**
	 * Map segment representations to their model
	 */
	protected Map<SVGSegRep, SVGSegModel> repToModel;
	/**
	 * Map model to their segment representations
	 */
	protected Map<SVGSegModel, SVGSegRep> modelToRep;
	/**
	 * A group for elements representing tangents
	 */
	protected OMSVGGElement tangentGroup;
	/**
	 * A group for elements representing vertices
	 */
	protected OMSVGGElement vertexGroup;
	/**
	 * Maps element representing a segment endpoint vertex
	 * the segment object
	 */
	private Map<Element, SVGSegRep> vertexToSeg;
	/**
	 * Maps element representing a segment tangent to
	 * the segment object
	 */
	private Map<Element, SVGSegRep> tangentToSeg;
	/**
	 * The mousedown target
	 */
	protected Element target;
	/**
	 * The mousedown point in user space
	 */
	protected OMSVGPoint p0;
	/**
	 * The transform from screen coordinates to
	 * manipulator coordinates when a mousedown event occurs
	 */
	protected OMSVGMatrix m;
	/**
	 * To catch selection changes
	 */
	protected SelectionChangedProxy<SVGSegModel> selChangeProxy = new SelectionChangedProxy<SVGSegModel>(this);
	/**
	 * Event registration for the scaling handler
	 */
	protected HandlerRegistration scalingHandlerReg;

	public EditPathGeometryManipulator() {
		segments = new ArrayList<SVGSegRep>();
		repToModel = new HashMap<SVGSegRep, SVGSegModel>();
		modelToRep = new HashMap<SVGSegModel, SVGSegRep>();
		vertexToSeg = new HashMap<Element, SVGSegRep>();
		tangentToSeg = new HashMap<Element, SVGSegRep>();
	}
	
	@Override
	public OMSVGElement bind(Record record) {
		this.record = record;
		SVGPathElementModel model = (SVGPathElementModel) record.getModel();
		scalingHandlerReg = model.getOwner().addScalingHandler(this);
		OMSVGElement element = model.getElementWrapper();
		svg = element.getOwnerSVGElement();

		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		path = document.createSVGPathElement();
		g = document.createSVGGElement();
		Mode.VERTEX.write(g);
		g.setClassNameBaseVal(AppBundle.INSTANCE.css().pathGeometryManipulator());
		tangentGroup = document.createSVGGElement();
		vertexGroup = document.createSVGGElement();
		g.appendChild(path);
		g.appendChild(tangentGroup);
		g.appendChild(vertexGroup);
		
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
			g = null;
			tangentGroup = null;
			vertexGroup = null;
			target = null;
			p0 = null;
			m = null;
			
			SVGPathElementModel model = (SVGPathElementModel) record.getModel();
			model.removeChangeListener(this);
			getSelectionModel().removeSelectionListener(selChangeProxy);
			record = null;
			repToModel.clear();
			modelToRep.clear();
			vertexToSeg.clear();
			tangentToSeg.clear();
			scalingHandlerReg.removeHandler();
		}
	}

	@Override
	public void modelChanged(ChangeEvent event) {
		GWT.log("SVGPathManipulator.modelChanged(" + monitorModel + "," + toString() + ")");
		if (monitorModel && record != null) {
			SVGPathElementModel model = (SVGPathElementModel) record.getModel();
			if (model.getElement().hasAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE)) {
				g.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, model.getElement().getAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
			}
			
			// Clear segment list
			path.setAttribute(SVGConstants.SVG_D_ATTRIBUTE, model.getElement().getAttribute(SVGConstants.SVG_D_ATTRIBUTE));
			segments.clear();
			
			// Clear mapping tables
			repToModel.clear();
			modelToRep.clear();
			vertexToSeg.clear();
			tangentToSeg.clear();
			
			// Clear DOM
			while (vertexGroup.hasChildNodes()) {
				vertexGroup.removeChild(vertexGroup.getLastChild());
			};
			while (tangentGroup.hasChildNodes()) {
				tangentGroup.removeChild(tangentGroup.getLastChild());
			};

			// Build segment list
			SVGSegStore store = model.getSegStore();
			List<SVGSegModel> segModels = store.getModels();
			OMSVGPathSegList segs = path.getPathSegList();
			for (int i = 0, size = segModels.size(); i < size; i++) {
				SVGSegModel segModel = segModels.get(i);
				OMSVGPathSeg seg = segs.getItem(i);
				switch(seg.getPathSegType()) {
				    case OMSVGPathSeg.PATHSEG_CLOSEPATH:
					  	{
					  	}
				  		break;
				    case OMSVGPathSeg.PATHSEG_MOVETO_ABS:
					  	{
					  		appendSegment(segModel, new SVGMoveSegRep(this, (OMSVGPathSegMovetoAbs)seg));
					  	}
				  		break;
				    case OMSVGPathSeg.PATHSEG_LINETO_ABS:
					  	{
							appendSegment(segModel, new SVGLineSegRep(this, (OMSVGPathSegLinetoAbs)seg));
					  	}
				  		break;
				    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
					  	{
							appendSegment(segModel, new SVGCubicSegRep(this, (OMSVGPathSegCurvetoCubicAbs)seg));
					  	}
				  		break;
				    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
					  	{
							appendSegment(segModel, new SVGQuadraticSegRep(this, (OMSVGPathSegCurvetoQuadraticAbs)seg));
					  	}
				  		break;
				    case OMSVGPathSeg.PATHSEG_ARC_ABS:
					  	{
							OMSVGPathSegArcAbs arcAbs = (OMSVGPathSegArcAbs)seg;
					  	}
				  		break;
				  	case OMSVGPathSeg.PATHSEG_UNKNOWN:
				    case OMSVGPathSeg.PATHSEG_MOVETO_REL:
				    case OMSVGPathSeg.PATHSEG_LINETO_REL:
				    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_REL:
				    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL:
				    case OMSVGPathSeg.PATHSEG_ARC_REL:
				    case OMSVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS:
				    case OMSVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL:
				    case OMSVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS:
				    case OMSVGPathSeg.PATHSEG_LINETO_VERTICAL_REL:
				    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS:
				    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL:
				    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS:
				    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL:
				    	GWT.log("Invalid segment type:" + seg.getPathSegTypeAsLetter());
				    	assert(false);
				  		break;
				}
			}
			
			// Connect the segments together
			for (int i = 0, size = segments.size(); i < size - 1; i++) {
				SVGSegRep seg = segments.get(i);
				SVGSegRep nextSeg = segments.get(i + 1);
				seg.setNext(nextSeg);
				nextSeg.setPrevious(seg);
			}
			
			update(SVGModel.getVertexSize((SVGElementModel) record.getModel()));
			processSelectionChanged(new SelectionChangedEvent<SVGSegModel>(getSelectionModel(), getSelectionModel().getSelectedItems()));
		}
		
	}
	
	public SVGSegModel getModel(SVGSegRep rep) {
		SVGSegModel model = repToModel.get(rep);
		assert model != null;
		return model;
	}
	public SVGSegRep getRepresentation(SVGSegModel model) {
		SVGSegRep rep = modelToRep.get(model);
		assert rep != null;
		return rep;	
	}

	@Override
	public OMSVGSVGElement getSvg() {
		return svg;
	}
	
	public OMSVGGElement getRootGroup() {
		return g;
	}
	
	public void appendSegment(SVGSegModel modelSeg, SVGSegRep repSeg) {
		segments.add(repSeg);
		
		OMSVGRectElement vertex = repSeg.getVertex();
		vertexGroup.appendChild(vertex);
		vertexToSeg.put(vertex.getElement(), repSeg);
		
		OMSVGGElement tangents = repSeg.getTangents();
		tangentGroup.appendChild(tangents);
		for (OMSVGElement element : tangents.<OMSVGElement>getChildNodes()) {
			tangentToSeg.put(element.getElement(), repSeg);
		}
		
		modelToRep.put(modelSeg, repSeg);
		repToModel.put(repSeg, modelSeg);
	}
	
	public SVGSegStore getSegStore() {
		return ((SVGPathElementModel) record.getModel()).getSegStore();
	}
	
	public GridSelectionModel<SVGSegModel> getSelectionModel() {
		return getSegStore().getSelectionModel();
	}
	
	public void selectSegment(SVGSegRep segment, boolean shiftDown, boolean ctrlDown) {
		GridSelectionModel<SVGSegModel> selectionModel = getSelectionModel();
		boolean selected = segment.getState() == VertexState.SELECTED;
		if (shiftDown) {
			List<SVGSegModel> selectedSegs = selectionModel.getSelectedItems();
			if (selectedSegs.size() > 0) {
				// Select all the segments in the range defined by the
				// last selected segment and this segment
				int ix1 = segments.indexOf(getRepresentation(selectedSegs.get(selectedSegs.size() - 1)));
				int ix2 = segments.indexOf(segment);
				for (int i = Math.min(ix1, ix2); i <= Math.max(ix1, ix2); i++) {
					SVGSegRep s = segments.get(i);
					if (s.getState() == VertexState.NONE) {
						selectionModel.select(i, true);
					}
				}
			} else {
				// Select segment
				selectionModel.select(getModel(segment), true);
			}
		} else if (ctrlDown) {
			// Toggle selection
			if (selected) {
				selectionModel.deselect(getModel(segment));
			} else {
				selectionModel.select(getModel(segment), true);
			}
		} else {
			// Clear previous selection
			selectionModel.deselectAll();
			
			// Select segment
			selectionModel.select(getModel(segment), false);
		}
	}

	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<SVGSegModel> se) {
		List<SVGSegModel> selection = (se == null) ? null : se.getSelection();
		Set<SVGSegModel> selected = (selection != null) ? new HashSet<SVGSegModel>(selection) : new HashSet<SVGSegModel>();
		// Clear rep selection
		for (SVGSegRep rep : segments) {
			rep.setState(selected.contains(getModel(rep)) ? VertexState.SELECTED : VertexState.NONE);
		}
		return true;
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		GWT.log("SVGPathManipulator.processMouseDown");
		Element eventTarget = event.getNativeEvent().getEventTarget().cast();
		if (SVGConstants.SVG_SVG_TAG.equals(eventTarget.getTagName())) {
			// Clear the selection
			getSelectionModel().deselectAll();
		} else if (path.getElement() == eventTarget) {
			Mode state = getMode();
			if (state == Mode.VERTEX) {
				Mode.TANGENT.write(g);
				update(SVGModel.getVertexSize((SVGElementModel) record.getModel()));
			} else if (state == Mode.TANGENT) {
				Mode.VERTEX.write(g);
				update(SVGModel.getVertexSize((SVGElementModel) record.getModel()));
			} else {
				assert false;
			}
		} else {
			m = path.getScreenCTM().inverse();
			p0 = getCoordinates(event, m);
			SVGSegRep segment = vertexToSeg.get(eventTarget);
			if (segment != null) {
				if (event.isControlKeyDown() || segment.getState() == VertexState.NONE) {
					selectSegment(segment, event.isShiftKeyDown(), event.isControlKeyDown());
				}
				if (segment.getState() == VertexState.SELECTED) {
					target = eventTarget;
				}
			} else if (tangentToSeg.containsKey(eventTarget)) {
				target = eventTarget;
			}
		}
		event.preventDefault();
		event.stopPropagation();
		return true;
	}

	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (target != null) {
//			GWT.log("SVGPathManipulator.processMouseMove");
			OMSVGPoint p1 = getCoordinates(event, m);
			OMSVGPoint delta = p1.substract(p0, svg.createSVGPoint());
			p1.assignTo(p0);
			
			SVGSegRep segment = tangentToSeg.get(target);
			float hs = SVGModel.getVertexSize((SVGElementModel) record.getModel());
			if (segment != null) {
				segment.processMouseMove(delta, target, hs, event.isControlKeyDown());
			} else {
				GridSelectionModel<SVGSegModel> selectionModel = getSelectionModel();
				for (SVGSegModel model : selectionModel.getSelectedItems()) {
					getRepresentation(model).processMouseMove(delta, null, hs, false);
				}
			}
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}

	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (target != null) {
			GWT.log("SVGPathManipulator.processMouseUp");
//			monitorModel = false;
			record.beginEdit();
			record.set(SVGConstants.SVG_D_ATTRIBUTE, path.getAttribute(SVGConstants.SVG_D_ATTRIBUTE));
			record.endEdit();
			record.commit(false);
//			monitorModel = true;
			target = null;
		}
		event.preventDefault();
		event.stopPropagation();
		return true;
	}
	
	@Override
	public Mode getMode() {
		return Mode.read(g);
	}
	
	public void update(float hs) {
		for (int i = 0; i < segments.size(); i++) {
			segments.get(i).update(hs);
		}
	}

	@Override
	public List<SVGSegRep> getSegments() {
		return segments;
	}

	@Override
	public void onScale(ScalingEvent event) {
		update(SVGModel.getVertexSize((SVGElementModel) record.getModel()));
	}
}
