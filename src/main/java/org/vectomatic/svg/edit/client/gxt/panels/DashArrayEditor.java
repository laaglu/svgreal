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
package org.vectomatic.svg.edit.client.gxt.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vectomatic.dom.svg.OMCSSPrimitiveValue;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.gxt.widget.DashArrayCell;
import org.vectomatic.svg.edit.client.model.svg.CssContextModel;
import org.vectomatic.svg.edit.client.model.svg.DashArray;
import org.vectomatic.svg.edit.client.model.svg.DashArray.Dash;
import org.vectomatic.svg.edit.client.model.svg.DashArrayStore;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Window class to implement a dash pattern editor
 * @author laaglu
 */
public class DashArrayEditor extends Window implements HasCloseHandlers<DashArrayEditor>, HasValueChangeHandlers<DashArray> {
	/**
	 * A store of defined dash arrays
	 */
	private DashArrayStore dashArrays;
	/**
	 * The dash array currently being edited
	 */
	private GridSelectionModel<DashArray> dashArraySelectionModel;
	/**
	 * Listens to changes in the dash array currently being edited
	 */
	private StoreListener<Dash> storeListener;
	private EditorGrid<Dash> dashGrid;
	private GridSelectionModel<Dash> dashSelectionModel;
	private Button removeDashArrayButton;
	private Button addDashButton;
	private Button removeDashButton;
	private ColumnModel dashModel;
	private SelectionChangedListener<Dash> dashSelectionListener;
	private static final ListStore<Dash> NO_DASH = new ListStore<Dash>();
	
	public DashArrayEditor() {
		AppConstants appConstants = AppConstants.INSTANCE;
		setPlain(true);
	    setModal(true);  
	    setBlinkModal(true);
		setMaximizable(true);
		setSize(500, 300);
		setMinWidth(500);
		setMinHeight(300);
		setHeading(appConstants.dashArrayEditor());
		
		////////////////////////////////
		// Left part: dash-array list
		////////////////////////////////
		Label dashArraysLabel = new Label(appConstants.dashArraysLabel());
		
		List<ColumnConfig> arrayConfigs = new ArrayList<ColumnConfig>();
		ColumnConfig dashArrayColumn = new ColumnConfig();
		dashArrayColumn.setSortable(false);
		dashArrayColumn.setId(DashArray.STORE_PROPERTY);
		dashArrayColumn.setWidth(220);
		dashArrayColumn.setRenderer(new GridCellRenderer<DashArray>() {
			@Override
			public Object render(DashArray model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<DashArray> store, Grid<DashArray> grid) {
				DashArrayCell cell = new DashArrayCell();
				cell.setDashArray(model);
				return cell;
			}
		});
		arrayConfigs.add(dashArrayColumn);
		ColumnModel arrayModel = new ColumnModel(arrayConfigs);
		dashArrays = new DashArrayStore();
		dashArrays.add(CssContextModel.getDefaultDashArrays().getModels());
		Grid<DashArray> dashArrayGrid = new Grid<DashArray>(dashArrays, arrayModel);
		dashArrayGrid.setHideHeaders(true);
		dashArrayGrid.setBorders(true);
		
		dashArraySelectionModel = dashArrayGrid.getSelectionModel();
		storeListener = new StoreListener<Dash>() {
			public void handleEvent(StoreEvent<Dash> e) {
				EventType type = e.getType();
				if (type == Store.Add
				 || type == Store.Remove
				 || type == Store.Update) {
					dashArrays.fireStoreUpdateEvent();
					ValueChangeEvent.fire(DashArrayEditor.this, dashArraySelectionModel.getSelectedItem());
				}
			}
		};
		dashArraySelectionModel.addSelectionChangedListener(new SelectionChangedListener<DashArray>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<DashArray> se) {
				// Cleanup previous listeners
				dashGrid.getStore().removeStoreListener(storeListener);
				dashGrid.getSelectionModel().removeSelectionListener(dashSelectionListener);
				
				// Bind to selected dash array
				DashArray dashArray = se.getSelectedItem();
				ListStore<Dash> dashStore = NO_DASH;
				if (dashArray != null) {
					dashStore = dashArray.<ListStore<Dash>>get(DashArray.STORE_PROPERTY);
					dashStore.addStoreListener(storeListener);
				}
				dashGrid.reconfigure(dashStore, dashModel);
				dashSelectionModel = new GridSelectionModel<Dash>();
				dashGrid.setSelectionModel(dashSelectionModel);
				dashSelectionModel.addSelectionChangedListener(dashSelectionListener);
				update();
				ValueChangeEvent.fire(DashArrayEditor.this, dashArray);
			}
		});
		
		Button addDashArrayButton = new Button(appConstants.addButton());
		addDashArrayButton.setToolTip(appConstants.addDashArrayTip());
		addDashArrayButton.addSelectionListener(new SelectionListener<ButtonEvent>() {	
			@Override
			public void componentSelected(ButtonEvent ce) {
				addDashArray();
			}
		});
		
		removeDashArrayButton = new Button(appConstants.removeButton());
		removeDashArrayButton.setToolTip(appConstants.removeDashArrayTip());
		removeDashArrayButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				removeDashArray();				
			}
		});
		LayoutContainer leftButtonContainer = new LayoutContainer();
		leftButtonContainer.setLayout(new RowLayout(Orientation.HORIZONTAL));
		RowData rowData = new RowData(.5, 1);
		leftButtonContainer.add(addDashArrayButton, rowData);
		leftButtonContainer.add(removeDashArrayButton, rowData);
		leftButtonContainer.setHeight(30);
		LayoutContainer leftContainer = new LayoutContainer();
        VBoxLayout leftLayout = new VBoxLayout();  
        leftLayout.setPadding(new Padding(5));  
        leftLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
        VBoxLayoutData vbl1 = new VBoxLayoutData(new Margins(0, 0, 5, 0));
        VBoxLayoutData vbl2 = new VBoxLayoutData(new Margins(0, 0, 5, 0));
        vbl2.setFlex(1);
        VBoxLayoutData vbl3 = new VBoxLayoutData(new Margins(0));
		leftContainer.setLayout(leftLayout);
		leftContainer.add(dashArraysLabel, vbl1);
		leftContainer.add(dashArrayGrid, vbl2);
		leftContainer.add(leftButtonContainer, vbl3);

		
		////////////////////////////////
		// Right part: current dash array
		////////////////////////////////
		
		Label dashArrayLabel = new Label(appConstants.dashArrayLabel());
		
		List<ColumnConfig> dashConfigs = new ArrayList<ColumnConfig>();

		SpinnerField valueField = new SpinnerField();
		valueField.setFireChangeEventOnSetValue(true);
		valueField.setAllowDecimals(false);
		valueField.setAllowNegative(false);
		ColumnConfig valueColumn = new ColumnConfig();
		valueColumn.setSortable(false);
		valueColumn.setId(Dash.VALUE_PROPERTY);
		valueColumn.setHeader(appConstants.dashValue());
		valueColumn.setWidth(160);
		valueColumn.setEditor(new CellEditor(valueField));
		dashConfigs.add(valueColumn); 

		final SimpleComboBox<Style.Unit> unitField = new SimpleComboBox<Style.Unit>();
		unitField.setForceSelection(true);
		unitField.setTriggerAction(TriggerAction.ALL);
		unitField.add(Arrays.asList(Style.Unit.values()));

		ColumnConfig unitColumn = new ColumnConfig();
		unitColumn.setSortable(false);
		unitColumn.setId(Dash.UNIT_PROPERTY);
		unitColumn.setHeader(appConstants.dashUnit());
		unitColumn.setWidth(60);
		CellEditor unitEditor = new CellEditor(unitField) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return unitField.findModel((Style.Unit)value);
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((ModelData) value).get("value");
			}
		};
		unitColumn.setEditor(unitEditor);
		dashConfigs.add(unitColumn); 

		dashModel = new ColumnModel(dashConfigs);
		dashGrid = new EditorGrid<Dash>(NO_DASH, dashModel);
		dashGrid.setClicksToEdit(ClicksToEdit.TWO);
		dashGrid.setAutoExpandColumn(Dash.VALUE_PROPERTY);  
		dashGrid.setBorders(true);
		dashSelectionListener = new SelectionChangedListener<Dash>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<Dash> se) {
				GWT.log("dashSelectionModel.selectionChanged");
				update();
			}
		};

		addDashButton = new Button(appConstants.addButton());
		addDashButton.setToolTip(appConstants.addDashTip());
		addDashButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addDash();
			}
		});
		
		removeDashButton = new Button(appConstants.removeButton());
		removeDashButton.setToolTip(appConstants.removeDashTip());
		removeDashButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				removeDash();
			}
		});
		LayoutContainer rightButtonContainer = new LayoutContainer();
		rightButtonContainer.setLayout(new RowLayout(Orientation.HORIZONTAL));
		rightButtonContainer.add(addDashButton, rowData);
		rightButtonContainer.add(removeDashButton, rowData);
		rightButtonContainer.setHeight(30);
		LayoutContainer rightContainer = new LayoutContainer();
        VBoxLayout rightLayout = new VBoxLayout();  
        rightLayout.setPadding(new Padding(5));
        rightLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
        rightContainer.setLayout(rightLayout);
		rightContainer.add(dashArrayLabel, vbl1);
		rightContainer.add(dashGrid, vbl2);
		rightContainer.add(rightButtonContainer, vbl3);

		////////////////////////////////
		// final assembly
		////////////////////////////////

		setLayout(new RowLayout(Orientation.HORIZONTAL));
	    rowData.setMargins(new Margins(5));  
	     
	    add(leftContainer, rowData);  
	    add(rightContainer, rowData);
	    
	    Button okButton = new Button(appConstants.commitButton());
	    okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				GWT.log("DashArrayEditor.ok");
				hide();
				CloseEvent.<DashArrayEditor>fire(DashArrayEditor.this, DashArrayEditor.this, false);
			}
		});
	    addButton(okButton);
	    Button cancelButton = new Button(appConstants.cancelButton());
	    cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				GWT.log("DashArrayEditor.cancel");
				hide();
				CloseEvent.<DashArrayEditor>fire(DashArrayEditor.this, DashArrayEditor.this, true);
			}
		});
	    addButton(cancelButton);
	}
	
	@Override
	public void show() {
		super.show();
		layout(true);
	}
	
	public void addDashArray() {
		GWT.log("DashArrayEditor.addDashArray");
		dashArrays.add(DashArray.parse(""));
	}
	
	public void removeDashArray() {
		GWT.log("DashArrayEditor.removeDashArray");
		dashArrays.remove(dashArraySelectionModel.getSelectedItem());
	}
	
	public DashArrayStore getDashArrays() {
		return dashArrays;
	}
	
	public void addDash() {
		GWT.log("DashArrayEditor.addDash");
		ListStore<Dash> dashStore = dashGrid.getStore();
		dashStore.add(new Dash(new OMCSSPrimitiveValue(1, OMCSSPrimitiveValue.CSS_NUMBER)));
	}
	
	public void removeDash() {
		GWT.log("DashArrayEditor.removeDash");
		ListStore<Dash> dashStore = dashGrid.getStore();
		for (Dash dash : dashSelectionModel.getSelectedItems()) {
			dashStore.remove(dash);
		}
		update();
	}
	
	public void update() {
		removeDashArrayButton.setEnabled(dashArraySelectionModel.getSelectedItem() != null);
		ListStore<Dash> dashStore = dashGrid.getStore();
		addDashButton.setEnabled(dashStore != NO_DASH);
		removeDashButton.setEnabled(dashStore != NO_DASH && dashSelectionModel.getSelectedItem() != null);
	}

	///////////////////////////////////////////////////
	// Event management
	///////////////////////////////////////////////////

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DashArray> handler) {
		return VectomaticApp2.getApp().getEventBus().addHandlerToSource(ValueChangeEvent.getType(), this, handler);
	}

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<DashArrayEditor> handler) {
		return VectomaticApp2.getApp().getEventBus().addHandlerToSource(CloseEvent.getType(), this, handler);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		VectomaticApp2.getApp().getEventBus().fireEventFromSource(event, this);
	}
}
