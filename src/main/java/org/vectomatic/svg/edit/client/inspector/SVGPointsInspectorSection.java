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
package org.vectomatic.svg.edit.client.inspector;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGAnimatedPointsModelBase;
import org.vectomatic.svg.edit.client.model.svg.SVGPointsStore;
import org.vectomatic.svg.edit.client.model.svg.SVGPointsStore.SVGPoint;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Inspector section dedicated to SVG polygon and polyline geometry
 * @author laaglu
 */
public class SVGPointsInspectorSection implements IInspectorSection<SVGAnimatedPointsModelBase> {
	private class SVGPointsPanel extends ContentPanel implements SelectionChangedProcessor<SVGPoint> {
		protected ColumnModel cm;
		protected EditorGrid<SVGPoint> grid;
		protected Button addPointButton;
		protected Button insertPointButton;
		protected Button removePointsButton;
		protected SelectionChangedProxy<SVGPoint> selChangeProxy = new SelectionChangedProxy<SVGPointsStore.SVGPoint>(this);

		public SVGPointsPanel() {
			setHeading(Format.capitalize(category.getDescription()));
			ModelConstants constants = ModelConstants.INSTANCE;
			List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
			
			ColumnConfig xColumn = new ColumnConfig();
			xColumn.setSortable(false);
			xColumn.setId(SVGConstants.SVG_X_ATTRIBUTE);
			xColumn.setHeader(constants.x());
			xColumn.setWidth(220);
			NumberField xField = new NumberField();
			xField.setPropertyEditorType(Float.class);
			xField.setAllowBlank(false);
			xColumn.setEditor(new CellEditor(xField));
			configs.add(xColumn); 

			ColumnConfig yColumn = new ColumnConfig();
			yColumn.setSortable(false);
			yColumn.setId(SVGConstants.SVG_Y_ATTRIBUTE);
			yColumn.setHeader(constants.y());
			yColumn.setWidth(220);
			NumberField yField = new NumberField();
			yField.setPropertyEditorType(Float.class);
			yField.setAllowBlank(false);
			yColumn.setEditor(new CellEditor(yField));
			configs.add(yColumn); 

			cm = new ColumnModel(configs);
//			final RowEditor<SVGPoint> re = new RowEditor<SVGPoint>();
			grid = new EditorGrid<SVGPoint>(new ListStore<SVGPoint>(), cm);
			grid.setClicksToEdit(ClicksToEdit.TWO);
			addPointButton = new Button();
			addPointButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					GWT.log("Append point");
					getStore().appendPoint();
				}
			});
			addPointButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.addPoint()));
			addPointButton.setToolTip(Format.capitalize(constants.addPointButton()));

			insertPointButton = new Button();
			insertPointButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					GWT.log("Insert point");
					getStore().insertPoint();
				}
			});
			insertPointButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.insertPoint()));
			insertPointButton.setToolTip(Format.capitalize(constants.insertPointButton()));
			
			removePointsButton = new Button();
			removePointsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					GWT.log("Remove point");
					getStore().removeSelectedPoints();
				}
			});
			removePointsButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.removePoints()));
			removePointsButton.setToolTip(Format.capitalize(constants.removePointsButton()));

			LayoutContainer c = new LayoutContainer(new ColumnLayout());  
			c.add(addPointButton);
			c.add(insertPointButton);
			c.add(removePointsButton);

			Button showManipulatorButton = new Button(AppConstants.INSTANCE.displayManipulatorButton());
			showManipulatorButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
//					ManipulatorCommandFactory factory = (ManipulatorCommandFactory)category.getCommandFactory();
				}
			});
			
			setLayout(new VBoxLayout(VBoxLayoutAlign.STRETCH));
			VBoxLayoutData fl1 = new VBoxLayoutData(new Margins(10, 10, 0, 10));
			fl1.setFlex(0);
			add(c, fl1);
			VBoxLayoutData fl2 = new VBoxLayoutData(new Margins(10));
			fl2.setFlex(1);
			add(grid, fl2);
			VBoxLayoutData fl3 = new VBoxLayoutData(new Margins(10));
			fl3.setFlex(0);
			add(showManipulatorButton, fl3);
		}
		
		public void bind(SVGPointsStore store) {
			grid.reconfigure(store, cm);
			final GridSelectionModel<SVGPoint> selectionModel = store.getSelectionModel();
			selectionModel.addSelectionChangedListener(selChangeProxy);
			grid.setSelectionModel(selectionModel);
			selectionModel.refresh();
		}
		
		public void unbind() {
			GridSelectionModel<SVGPoint> selectionModel = grid.getSelectionModel();
			if (selectionModel != null) {
				selectionModel.removeSelectionListener(selChangeProxy);
			}
			grid.setSelectionModel(null);
		}
		
		@Override
		public boolean processSelectionChanged(SelectionChangedEvent<SVGPoint> se) {
			List<SVGPoint> selection = se.getSelection();
			GWT.log("SVGPointsPanel.selectionChanged: " + selection);
			SVGPointsStore store = getStore();
			insertPointButton.setEnabled(store.canInsertPoint());
			removePointsButton.setEnabled(store.canRemoveSelectedPoints());
			return true;
		}
		private SVGPointsStore getStore() {
			return (SVGPointsStore) grid.getStore();
		}

	}
	private SVGPointsPanel panel;
	private ModelCategory category;

	public SVGPointsInspectorSection(ModelCategory category) {
		this.category = category;
		panel = new SVGPointsPanel();
	}
	@Override
	public Component getPanel() {
		return panel ;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGPointsInspectorSection(");
		builder.append(category);
		builder.append(")");
		return builder.toString();
	}

	@Override
	public void bind(SVGAnimatedPointsModelBase model) {
		GWT.log("SVGPointsInspectorSection.bind(" + model + ")");
		panel.bind(model.getPointsStore());
		
	}

	@Override
	public void unbind() {
		panel.unbind();
	}

}
