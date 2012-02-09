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
package org.vectomatic.svg.edit.client.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathSegType;
import org.vectomatic.svg.edit.client.model.svg.path.SVGCloseSegModel;
import org.vectomatic.svg.edit.client.model.svg.path.SVGSegModel;
import org.vectomatic.svg.edit.client.model.svg.path.SVGSegStore;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Inspector section dedicated to SVG path geometry
 * @author laaglu
 */
public class PathInspectorSection implements IInspectorSection<SVGPathElementModel> {
	private class PathPanel extends ContentPanel implements SelectionChangedProcessor<SVGSegModel> {
		protected ColumnModel cm;
		protected EditorGrid<SVGSegModel> grid;
		
		protected Button addSegmentButton;
		protected Button insertSegmentButton;
		protected Button removeSegmentsButton;
		
		protected SelectionChangedProxy<SVGSegModel> selChangeProxy = new SelectionChangedProxy<SVGSegModel>(this);
		
		private CardLayout cardLayout;
		private LayoutContainer container;
		private LayoutContainer noSelection;
		private LayoutContainer multipleSelection;
		private IInspectorSection<SVGSegModel> currentSection;

		public PathPanel() {
			setHeading(Format.capitalize(category.getDescription()));
			ModelConstants constants = ModelConstants.INSTANCE;
			List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
			
			final SimpleComboBox<String> combo = (SimpleComboBox<String>) SVGPathSegType.INSTANCE.createField(SVGSegModel.TYPE);
			CellEditor editor = new CellEditor(combo) {
				@Override
				public Object preProcessValue(Object value) {
					if (value == null) {
						return value;
					}
					return combo.findModel(value.toString());
				}

				@Override
				public Object postProcessValue(Object value) {
					if (value == null) {
						return value;
					}
					return ((ModelData) value).get("value");
				}
			};
		    
			ColumnConfig typeColumn = new ColumnConfig();
			typeColumn.setSortable(false);
			typeColumn.setId(SVGSegModel.TYPE_ID);
			typeColumn.setHeader(constants.segTypeDesc());
			typeColumn.setWidth(220);
			typeColumn.setEditor(editor);
			configs.add(typeColumn);
			
			GridCellRenderer<ModelData> coordinateRenderer = new GridCellRenderer<ModelData>() {
				@Override
				public Object render(ModelData model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<ModelData> store, Grid<ModelData> grid) {
					if (model instanceof SVGCloseSegModel) {
						return "";
					}
					return null;
				}
			};
			ColumnConfig xColumn = new ColumnConfig();
			xColumn.setSortable(false);
			xColumn.setId(SVGConstants.SVG_X_ATTRIBUTE);
			xColumn.setHeader(constants.x());
			xColumn.setWidth(100);
			NumberField xField = new NumberField();
			xField.setPropertyEditorType(Float.class);
			xField.setAllowBlank(false);
			xColumn.setEditor(new CellEditor(xField));
			xColumn.setRenderer(coordinateRenderer);
			configs.add(xColumn);

			ColumnConfig yColumn = new ColumnConfig();
			yColumn.setSortable(false);
			yColumn.setId(SVGConstants.SVG_Y_ATTRIBUTE);
			yColumn.setHeader(constants.y());
			yColumn.setWidth(100);
			NumberField yField = new NumberField();
			yField.setPropertyEditorType(Float.class);
			yField.setAllowBlank(false);
			yColumn.setEditor(new CellEditor(yField));
			yColumn.setRenderer(coordinateRenderer);
			configs.add(yColumn);

			cm = new ColumnModel(configs);
			grid = new EditorGrid<SVGSegModel>(new SVGSegStore(), cm);  
		    grid.setAutoExpandColumn(SVGSegModel.TYPE_ID);  
		    grid.setBorders(true);
			grid.setClicksToEdit(ClicksToEdit.TWO);
			grid.addListener(Events.BeforeEdit, new Listener<GridEvent<SVGSegModel>>() {
				@Override
				public void handleEvent(GridEvent<SVGSegModel> ge) {
					String property = ge.getProperty();
					// Do not allow changing the type of the first moveto segment
					if (SVGSegModel.TYPE_ID.equals(property) && ge.getRowIndex() == 0) {
						ge.setCancelled(true);
					}
					// Do not allow changing the coordinates of a close path
					if (ge.getModel() instanceof SVGCloseSegModel
					 && (SVGConstants.SVG_X_ATTRIBUTE.equals(property)
					  || SVGConstants.SVG_Y_ATTRIBUTE.equals(property))) {
						ge.setCancelled(true);
					}
				}			
			});
			
			
			addSegmentButton = new Button();
			addSegmentButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					GWT.log("Add segment");
					getStore().appendSegment();
				}
			});
			addSegmentButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.addPoint()));
			addSegmentButton.setToolTip(Format.capitalize(constants.addSegmentButton()));

			insertSegmentButton = new Button();
			insertSegmentButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					GWT.log("Insert segment");
					getStore().insertSegment();
				}
			});
			insertSegmentButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.insertPoint()));
			insertSegmentButton.setToolTip(Format.capitalize(constants.insertSegmentButton()));
			
			removeSegmentsButton = new Button();
			removeSegmentsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					GWT.log("Remove segment");
					getStore().removeSelectedSegments();
				}
			});
			removeSegmentsButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.removePoints()));
			removeSegmentsButton.setToolTip(Format.capitalize(constants.removeSegmentsButton()));
			
			LayoutContainer c = new LayoutContainer(new ColumnLayout());  
			c.add(addSegmentButton);
			c.add(insertSegmentButton);
			c.add(removeSegmentsButton);
			
			container = new LayoutContainer();
			cardLayout = new CardLayout();
			container.setLayout(cardLayout);

			noSelection = new LayoutContainer();
			noSelection.addText("No selection");
			noSelection.setLayout(new FitLayout());
			container.add(noSelection);

			multipleSelection = new LayoutContainer();  
			multipleSelection.addText("Multiple selection");  
			multipleSelection.setLayout(new FitLayout());
			container.add(multipleSelection);

			LayoutContainer detailPanel = new LayoutContainer();
			detailPanel.setLayout(new FitLayout());
			detailPanel.add(container);

			LayoutContainer top = new LayoutContainer();
			top.setLayout(new VBoxLayout(VBoxLayoutAlign.STRETCH));
			VBoxLayoutData fl1 = new VBoxLayoutData(new Margins(10, 10, 0, 10));
			fl1.setFlex(0);
			top.add(c, fl1);
			VBoxLayoutData fl2 = new VBoxLayoutData(new Margins(10));
			fl2.setFlex(1);
			top.add(grid, fl2);
			
			BorderLayout layout = new BorderLayout();  
		    layout.setEnableState(true);  
		    setLayout(layout);
		    
			BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		    centerData.setMargins(new Margins(0)); 
		    add(top, centerData);
			
		    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 100); 
		    southData.setMargins(new Margins(0,10,10,10)); 
		    southData.setSplit(true); 
		    southData.setCollapsible(true); 
		    add(detailPanel, southData);
		    setScrollMode(Scroll.AUTOY);
			
		    processSelectionChanged(null);
		}
		
		public void bind(SVGSegStore store) {
			grid.reconfigure(store, cm);
			final GridSelectionModel<SVGSegModel> selectionModel = store.getSelectionModel();
			selectionModel.addSelectionChangedListener(selChangeProxy);
			grid.setSelectionModel(selectionModel);
			selectionModel.refresh();
		}
		
		public void unbind() {
			GridSelectionModel<SVGSegModel> selectionModel = grid.getSelectionModel();
			if (selectionModel != null) {
				selectionModel.removeSelectionListener(selChangeProxy);
			}
			grid.setSelectionModel(null);
		}

		@Override
		public boolean processSelectionChanged(SelectionChangedEvent<SVGSegModel> se) {
			List<SVGSegModel> models = se != null ? se.getSelection() : Collections.<SVGSegModel>emptyList();
			GWT.log("PathPanel.selectionChanged: " + models);
			SVGSegStore store = getStore();
			insertSegmentButton.setEnabled(store.canInsertSegment());
			removeSegmentsButton.setEnabled(store.canRemoveSelectedSegments());
			
			Component panel = noSelection;
			SVGSegModel model = null;
			if (currentSection != null) {
				currentSection.unbind();
				currentSection = null;
			}
			if (models != null) {
				if (models.size() > 1) {
					panel = multipleSelection;
				} else if (models.size() == 1) {
					model = models.get(0);
					currentSection = getSection(model);
					currentSection.bind(model);
					panel = currentSection.getPanel();
				}
			}
			cardLayout.setActiveItem(panel);
			return false;
		}

		private SVGSegStore getStore() {
			return (SVGSegStore) grid.getStore();
		}

		private IInspectorSection<SVGSegModel> getSection(SVGSegModel model) {
			if (metaModelToSection == null) {
				metaModelToSection = new HashMap<MetaModel<OMSVGPathSeg>, IInspectorSection<SVGSegModel>>();
			}
			MetaModel<OMSVGPathSeg> metaModel = model.getMetaModel();
			IInspectorSection<SVGSegModel> section = metaModelToSection.get(metaModel);
			if (section == null) {
				ModelCategory<?> geometry = model.getMetaModel().getCategory(ModelCategory.GEOMETRY);
				section = geometry.getInspectorSection();
				metaModelToSection.put(metaModel, section);
				FormPanel form = (FormPanel)section.getPanel();
				form.setHeaderVisible(false);
				container.add(form);
			}
			return section;
		}

	}

	protected static Map<MetaModel<OMSVGPathSeg>, IInspectorSection<SVGSegModel>> metaModelToSection;
	private PathPanel panel;
	private ModelCategory<?> category;

	public PathInspectorSection(ModelCategory<?> category) {
		this.category = category;
		panel = new PathPanel();
	}
	@Override
	public Component getPanel() {
		return panel ;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("PathInspectorSection(");
		builder.append(category);
		builder.append(")");
		return builder.toString();
	}

	@Override
	public void bind(SVGPathElementModel model) {
		GWT.log("PathInspectorSection.bind(" + model + ")");
		panel.bind(model.getSegStore());
	}

	@Override
	public void unbind() {
		panel.unbind();
	}

}

