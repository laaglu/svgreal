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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGPointList;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.itf.ISVGAnimatedPoints;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.ICommandFactory;
import org.vectomatic.svg.edit.client.gxt.widget.GridSelectionModelExt;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.core.client.GWT;

/**
 * Store of points class. The goal of this class is to give the user
 * access to individual points in a polygon or a polyline
 * @author laaglu
 */
public class SVGPointsStore extends ListStore<SVGPointsStore.SVGPoint> {
	public class SVGPoint implements ModelData {
		private OMSVGPoint p;

		public SVGPoint(OMSVGPoint p) {
			this.p = p;
		}

		@Override
		public Float get(String property) {
			return "x".equals(property) ? p.getX() : p.getY();
		}

		@Override
		public Map<String, Object> getProperties() {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("x", p.getX());
			properties.put("y", p.getY());
			return properties;
		}

		@Override
		public Collection<String> getPropertyNames() {
			return Arrays.asList(new String[]{"x", "y"});
		}

		@Override
		public <X> X remove(String property) {
			throw new IllegalStateException();
		}

		@Override
		public <X> X set(String property, X value) {
			SVGPoints oldValue = null;
			if (!model.isSilent()) {
				oldValue = new SVGPoints(model.getElement());
			}
			X prev;
			if ("x".equals(property)) {
				prev = (X)Float.valueOf(p.getX());
				p.setX(((Float)value).floatValue());
			} else {
				prev = (X)Float.valueOf(p.getY());
				p.setY(((Float)value).floatValue());
			}
			if (!prev.equals(value)) {
				StoreEvent<SVGPointsStore.SVGPoint> evt = new StoreEvent<SVGPointsStore.SVGPoint>(SVGPointsStore.this);
				evt.setModel(this);
				fireEvent(Store.DataChanged, evt);
				fireChangeEvent(oldValue, new SVGPoints(model.getElement()));
			}
			return prev;
		}
		
		public OMSVGPoint getPoint() {
			return p;
		}
		
		@Override
		public String toString() {
			return p.getDescription();
		}
	}

	/**
	 * The model owning this store
	 */
	private SVGElementModel model;
	/**
	 * The point selection model
	 */
	private GridSelectionModel<SVGPointsStore.SVGPoint> selectionModel;
	
	public GridSelectionModel<SVGPointsStore.SVGPoint> getSelectionModel() {
		// This needs to be re-asserted as unbinding the inspector section
		// will disconnect the selection from the store.
		selectionModel.bind(this);
		return selectionModel;
	}
	public OMSVGPointList getElementPoints() {
		return ((ISVGAnimatedPoints)model.getElementWrapper()).getPoints();
	}
	public OMSVGPointList getTwinPoints() {
		return ((ISVGAnimatedPoints)model.getTwinWrapper()).getPoints();
	}

	public SVGPointsStore(SVGElementModel model) {
		this.model = model;
		selectionModel = new GridSelectionModelExt<SVGPointsStore.SVGPoint>();
		setFiresEvents(false);
		OMSVGPointList pList = getElementPoints();
		int pCount = pList.getNumberOfItems();
		for (int i = 0; i < pCount; i++) {
			OMSVGPoint p = pList.getItem(i);
			add(new SVGPoint(p));
		}
		setFiresEvents(true);
	}
	
	public void update(SVGPoints value) {
		GWT.log("SVGPointsStore.update");
		setFiresEvents(false);
		OMSVGSVGElement svg = OMNode.convert(model.getElement().getOwnerSVGElement());
		OMSVGPointList pList = getElementPoints();
		OMSVGPointList tList = getTwinPoints();
		int pCount = pList.getNumberOfItems();
		OMSVGPointList qList = value.getValue();
		int qCount = qList.getNumberOfItems();
		if (qCount < pCount) {
			for (int i = pCount - 1; i >= qCount ; i--) {
				pList.removeItem(i);
				tList.removeItem(i);
				remove(i);
			}
		} else if (qCount > pCount) {
			for (int i = pCount; i < qCount; i++) {
				OMSVGPoint p = svg.createSVGPoint();
				pList.appendItem(p);
				tList.appendItem(svg.createSVGPoint(p));
				add(new SVGPoint(p));
			}
		}
		for (int i = 0; i < qCount; i++) {
			OMSVGPoint p = pList.getItem(i);
			OMSVGPoint t = tList.getItem(i);
			OMSVGPoint q = qList.getItem(i);
			p.setX(q.getX());
			p.setY(q.getY());
			t.setX(q.getX());
			t.setY(q.getY());
		}
		setFiresEvents(true);
		StoreEvent<SVGPointsStore.SVGPoint> evt = new StoreEvent<SVGPointsStore.SVGPoint>(SVGPointsStore.this);
		evt.setModels(all);
		fireEvent(Store.DataChanged, evt);
	}
	

	public void appendPoint() {
		/*SVGPoints oldValue = null;
		if (!model.isSilent()) {
			oldValue = new SVGPoints(model.getElement());
		}
		OMSVGSVGElement svg = OMNode.convert(model.getElement().getOwnerSVGElement());
		OMSVGPointList pList = getElementPoints();
		OMSVGPointList tList = getTwinPoints();
		pList.appendItem(p);
		tList.appendItem(svg.createSVGPoint(p));
		// add will fire a StoreEvent
		add(new SVGPoint(p));
		if (!model.isSilent()) {
			fireChangeEvent(oldValue, new SVGPoints(model.getElement()));
		}*/
		OMSVGPoint p0 = getAt(0).getPoint();
		OMSVGPoint p1 = getAt(getCount() - 1).getPoint();
		OMSVGPoint midPoint = p0.add(p1, model.getElement().getOwnerSVGElement().createSVGPoint()).scale(0.5f);
		OMSVGPointList pList = getElementPoints();
		insertPointBefore(midPoint, pList.getNumberOfItems());
	}
	
	public void insertPoint() {
		int ix = indexOf(selectionModel.getSelectedItem());
		OMSVGPoint p0 = getAt(ix - 1 < 0 ? getCount() - 1 : ix - 1).getPoint();
		OMSVGPoint p1 = getAt(ix).getPoint();
		OMSVGPoint midPoint = p0.add(p1, model.getElement().getOwnerSVGElement().createSVGPoint()).scale(0.5f);
		insertPointBefore(midPoint, ix);
	}
	
	public void insertPointBefore(OMSVGPoint p, int index) {
		SVGPoints oldValue = null;
		if (!model.isSilent()) {
			oldValue = new SVGPoints(model.getElement());
		}
		OMSVGSVGElement svg = OMNode.convert(model.getElement().getOwnerSVGElement());
		OMSVGPointList pList = getElementPoints();
		OMSVGPointList tList = getTwinPoints();
		pList.insertItemBefore(p, index);
		tList.insertItemBefore(svg.createSVGPoint(p), index);
		// insert will fire a StoreEvent
		insert(new SVGPoint(p), index);
		fireChangeEvent(oldValue, new SVGPoints(model.getElement()));
	}
	
	public void removeSelectedPoints() {
		SVGPoints oldValue = null;
		if (!model.isSilent()) {
			oldValue = new SVGPoints(model.getElement());
		}
		List<SVGPoint> selectedItems = selectionModel.getSelectedItems();
		OMSVGPointList pList = getElementPoints();
		OMSVGPointList tList = getTwinPoints();
		for (SVGPoint p : selectedItems) {
			int index = indexOf(p);
			pList.removeItem(index);
			tList.removeItem(index);
			remove(p);
		}
		fireChangeEvent(oldValue, new SVGPoints(model.getElement()));
	}
	
	/**
	 * Returns true if the selected points can be removed from
	 * this ISVGAnimatedPoints. Points can be removed if there
	 * is a selection and removing those selected points would 
	 * leave at least two points in this ISVGAnimatedPoints.
	 * @return true if the selected points can be removed from
	 * this ISVGAnimatedPoints.
	 */
	public boolean canRemoveSelectedPoints() {
		List<SVGPoint> selectedItems = selectionModel.getSelectedItems();
		return selectedItems.size() > 0 && (getCount() - selectedItems.size()) >= 2;
	}
	
	/**
	 * Return the index of the point where 
	 * a point can be inserted. A point can be inserted
	 * when two consecutive points are selected.
	 * @return
	 */
	private int getInsertionIndex() {
		List<SVGPoint> selectedItems = selectionModel.getSelectedItems();
		return selectedItems.size() == 1 ? selectedItems.indexOf(selectionModel.getSelectedItem()) : -1;
	}

	/**
	 * Returns true if a point can be inserted.
	 * @return true if a point can be inserted.
	 */
	public boolean canInsertPoint() {
		return getInsertionIndex() != -1;
	}
	
	private void fireChangeEvent(final SVGPoints oldValue, final SVGPoints newValue) {
		if (!model.isSilent()) {
			ICommandFactory factory = model.getMetaModel().getCategory(ModelCategory.GEOMETRY).getMetadata(SVGConstants.SVG_POINTS_ATTRIBUTE).getCommandFactory().create();
			factory.start(this);
			TreeStore<SVGElementModel> store = model.getOwner().getStore();
			final Record record = new Record(model) {
				  public Map<String, Object> getChanges() {
					  return Collections.<String, Object>singletonMap(SVGConstants.SVG_POINTS_ATTRIBUTE, oldValue);
				  }
			};
			PropertyChangeEvent changeEvent = new PropertyChangeEvent(ChangeEventSource.Update, model, SVGConstants.SVG_POINTS_ATTRIBUTE, oldValue, newValue);
			model.notify(changeEvent);
			TreeStoreEvent<SVGElementModel> storeEvent = new TreeStoreEvent<SVGElementModel>(store);
			storeEvent.setRecord(record);
			storeEvent.setOperation(RecordUpdate.EDIT);
			store.fireEvent(Update, storeEvent);
			selectionModel.refresh();
			model.getTwin().setAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE, model.getElement().getAttribute(SVGConstants.SVG_POINTS_ATTRIBUTE));
			factory.stop();
//		SelectionChangedEvent<SVGPoint> selectionEvent = new SelectionChangedEvent<SVGPoint>(selectionModel, selectionModel.getSelection());
//		selectionModel.fireEvent(Events.SelectionChange, selectionEvent);
		}
	}
}
