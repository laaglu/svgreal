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
package org.vectomatic.svg.edit.client.model.svg.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegArcAbs;
import org.vectomatic.dom.svg.OMSVGPathSegClosePath;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticAbs;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoAbs;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.ICommandFactory;
import org.vectomatic.svg.edit.client.gxt.widget.GridSelectionModelExt;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathSegType;

import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.core.client.GWT;

/**
 * Store of path segment class. The goal of this class is to give the user
 * access to individual segments in a path.
 * @author laaglu
 */
public class SVGSegStore extends ListStore<SVGSegModel> {
	/**
	 * The model owning this store
	 */
	private SVGPathElementModel model;
	/**
	 * The segment selection model
	 */
	private GridSelectionModelExt<SVGSegModel> selectionModel;

	public SVGSegStore() {
		this(null);
	}

	public SVGSegStore(SVGPathElementModel model) {
		this.model = model;
		
		// Create the selection model
		selectionModel = new GridSelectionModelExt<SVGSegModel>();
	}
	
	public void update(OMSVGPathElement path) {
		// Clear selection
		Set<Integer> indices = selectionModel.getSelectedIndices();
		GWT.log("SVGSegStore.update " + indices.toString());
		selectionModel.setFiresEvents(false);
		selectionModel.deselectAll();
		
		// Clear store
		removeAll();
		
		// Build segment list
		OMSVGPathSegList segs = path.getPathSegList();
		for (OMSVGPathSeg seg : segs) {
			switch(seg.getPathSegType()) {
			    case OMSVGPathSeg.PATHSEG_CLOSEPATH:
				  	{
				  		OMSVGPathSegClosePath closePath = (OMSVGPathSegClosePath)seg;
				  		add(new SVGCloseSegModel(this, closePath));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_MOVETO_ABS:
				  	{
				  		OMSVGPathSegMovetoAbs moveToAbs = (OMSVGPathSegMovetoAbs)seg;
				  		add(new SVGMoveSegModel(this, moveToAbs));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_ABS:
				  	{
						OMSVGPathSegLinetoAbs lineToAbs = (OMSVGPathSegLinetoAbs)seg;
						add(new SVGLineSegModel(this, lineToAbs));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
				  	{
						OMSVGPathSegCurvetoCubicAbs curveToCubicAbs = (OMSVGPathSegCurvetoCubicAbs)seg;
						add(new SVGCubicSegModel(this, curveToCubicAbs));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
				  	{
						OMSVGPathSegCurvetoQuadraticAbs curveToQuadraticAbs = (OMSVGPathSegCurvetoQuadraticAbs)seg;
						add(new SVGQuadraticSegModel(this, curveToQuadraticAbs));
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
		
		// Restore the selection
		List<SVGSegModel> newSelection = new ArrayList<SVGSegModel>();
		for (int index : indices) {
			newSelection.add(getAt(index));
		}
		selectionModel.setFiresEvents(true);
		selectionModel.select(newSelection, false);
		selectionModel.fireEvent(Events.SelectionChange, new SelectionChangedEvent<SVGSegModel>(selectionModel, selectionModel.getSelectedItems()));
	}
	
	public GridSelectionModel<SVGSegModel> getSelectionModel() {
		// This needs to be re-asserted as unbinding the inspector section
		// will disconnect the selection from the store.
		selectionModel.bind(this);
		return selectionModel;
	}
	
	public void fireChangeEvent(final String oldValue, String newValue) {
		if (!model.isSilent()) {
			ICommandFactory factory = model.getMetaModel().getCategory(ModelCategory.GEOMETRY).getMetadata(SVGConstants.SVG_D_ATTRIBUTE).getCommandFactory().create();
			factory.start(this);
			TreeStore<SVGElementModel> store = model.getOwner().getStore();
			final Record record = new Record(model) {
				  public Map<String, Object> getChanges() {
					  return Collections.<String, Object>singletonMap(SVGConstants.SVG_D_ATTRIBUTE, oldValue);
				  }
			};
			PropertyChangeEvent changeEvent = new PropertyChangeEvent(ChangeEventSource.Update, model, SVGConstants.SVG_D_ATTRIBUTE, oldValue, newValue);
			model.notify(changeEvent);
			TreeStoreEvent<SVGElementModel> storeEvent = new TreeStoreEvent<SVGElementModel>(store);
			storeEvent.setRecord(record);
			storeEvent.setOperation(RecordUpdate.EDIT);
			store.fireEvent(Update, storeEvent);
			selectionModel.refresh();
			model.getTwin().setAttribute(SVGConstants.SVG_D_ATTRIBUTE, model.getElement().getAttribute(SVGConstants.SVG_D_ATTRIBUTE));
			factory.stop();
		}
	}
	
	public String getValue() {
		return model.getElement().getAttribute(SVGConstants.SVG_D_ATTRIBUTE);
	}
	
	public int getIndex(SVGSegModel segModel) {
		for (int index = 0, size = getCount(); index < size; index++) {
			// dumb comparison to avoid invoking equals()
			if (getAt(index) == segModel) {
				return index;
			}
		}
		return -1;
	}
	
	public String setPathSegType(SVGSegModel segModel, String type) {
		String oldType = segModel.get(SVGSegModel.TYPE_ID);
		if (!oldType.equals(type)) {
			int index = getIndex(segModel);
			SVGSegModel prevSegModel = getAt(index -1);
			float x = segModel.getX();
			float y = segModel.getY();
			OMSVGPathElement path = (OMSVGPathElement) model.getElementWrapper();
			OMSVGPathSegList segs = path.getPathSegList();
			switch (SVGPathSegType.INSTANCE.fromName(type)) {
			    case OMSVGPathSeg.PATHSEG_LINETO_ABS:
				  	{
						OMSVGPathSegLinetoAbs lineToAbs = path.createSVGPathSegLinetoAbs(x, y);
						segs.replaceItem(lineToAbs, index);
						swapModelInstance(segModel, new SVGLineSegModel(this, lineToAbs));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
				  	{
						float x0 = prevSegModel.getX();
						float y0 = prevSegModel.getY();
						float x1 = x0 + 0.25f * (x - x0);
						float y1 = y0 + 0.25f * (y - y0);
						float x2 = x0 + 0.75f * (x - x0);
						float y2 = y0 + 0.75f * (y - y0);
						OMSVGPathSegCurvetoCubicAbs curveToCubicAbs = path.createSVGPathSegCurvetoCubicAbs(x, y, x1, y1, x2, y2);
						segs.replaceItem(curveToCubicAbs, index);
						swapModelInstance(segModel, new SVGCubicSegModel(this, curveToCubicAbs));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
				  	{
						float x0 = prevSegModel.getX();
						float y0 = prevSegModel.getY();
						float x1 = x0 + 0.5f * (x - x0);
						float y1 = y0 + 0.5f * (y - y0);
						OMSVGPathSegCurvetoQuadraticAbs curveToQuadraticAbs = path.createSVGPathSegCurvetoQuadraticAbs(x, y, x1, y1);
						segs.replaceItem(curveToQuadraticAbs, index);
						swapModelInstance(segModel, new SVGQuadraticSegModel(this, curveToQuadraticAbs));
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CLOSEPATH:
				    {
				    	OMSVGPathSegClosePath closePath = path.createSVGPathSegClosePath();
						segs.replaceItem(closePath, index);
						swapModelInstance(segModel, new SVGCloseSegModel(this, closePath));
				    }
			    	break;
				default:
					assert false : "cannot change seg type";
					break;
			}
		}
		return oldType;
	}

	public void appendSegment() {
		String oldValue = getValue();
		add(lastSegment().createNextSegment());
		fireChangeEvent(oldValue, getValue());
	}

	public void insertSegment() {
		String oldValue = getValue();
		int index = selectionModel.getSelectedIndex();
		insert(getAt(index).split(index), index);
		fireChangeEvent(oldValue, getValue());
	}

	public void removeSelectedSegments() {
		List<Integer> indices = new ArrayList<Integer>();
		for (SVGSegModel model : selectionModel.getSelectedItems()) {
			indices.add(getIndex(model));
		}
		Collections.sort(indices);

		String oldValue = getValue();
		selectionModel.setFiresEvents(false);
		OMSVGPathElement path = (OMSVGPathElement) model.getElementWrapper();
		OMSVGPathSegList segs = path.getPathSegList();
		for (int i = indices.size() - 1; i >= 0; i--) {
			segs.removeItem(indices.get(i));
			remove(indices.get(i));
		}
		selectionModel.setFiresEvents(true);
		fireChangeEvent(oldValue, getValue());
	}

	/**
	 * Returns true if a segment can be inserted. A segment can
	 * be inserted before any other segment except the first segment.
	 * @return true if a point can be inserted.
	 */
	public boolean canInsertSegment() {
		return selectionModel.getCount() > 0 && selectionModel.getSelectedIndex() != 0;
	}

	public boolean canRemoveSelectedSegments() {
		List<SVGSegModel> selectedSegs = selectionModel.getSelectedItems();
		return !selectedSegs.contains(getAt(0)) && (getCount() - selectedSegs.size()) >= 2;
	}
	
	public SVGSegModel lastSegment() {
		int count = getCount();
		return count > 0 ? getAt(getCount() - 1) : null;
	}
	
	public OMSVGPathElement getPath() {
		return (OMSVGPathElement) model.getElementWrapper();
	}
}
