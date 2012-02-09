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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionService;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

/**
 * Class to manage the inspector window. The inspector window contains
 * many inspectors (each inspector is dedicated to displaying the properties
 * of a given metamodel). The inspector window displays one inspector at a
 * time, corresponding to the currently selected object 
 * @author laaglu
 */
public class InspectorWindow extends Window implements SelectionChangedProcessor<SVGElementModel> {
	private ContentPanel contentPanel;
	private CardLayout cardLayout;
	private ContentPanel noSelection;
	private ContentPanel multipleSelection;
	private Inspector<SVGElementModel> currentInspector;
	private static Map<MetaModel<SVGElement>, Inspector<SVGElementModel>> metaModelToInspector;
	private SelectionChangedProxy<SVGElementModel> selChangeProxy = new SelectionChangedProxy<SVGElementModel>(this);
	
	public InspectorWindow() {
		contentPanel = new ContentPanel();
		cardLayout = new CardLayout();
		contentPanel.setHeaderVisible(false);
		contentPanel.setLayout(cardLayout);
		
		noSelection = new ContentPanel();
		noSelection.setHeaderVisible(false);
		noSelection.setAnimCollapse(false);  
		noSelection.addText("No selection");  
		contentPanel.add(noSelection); 

		multipleSelection = new ContentPanel();  
		multipleSelection.setHeaderVisible(false);
		multipleSelection.setAnimCollapse(false);  
		multipleSelection.addText("Multiple selection");  
		contentPanel.add(multipleSelection); 

		setPlain(true);
		setMaximizable(true);
		setSize(500, 300);
		setMinWidth(200);
		setMinHeight(170);
		setLayout(new FitLayout());
		add(contentPanel);		
	}
	
	@Override
	public void onShow() {
		GWT.log("InspectorWindow.onShow()");
		super.onShow();
		SelectionService.get().addListener(selChangeProxy);
	}
	
	@Override
	public void onHide() {
		GWT.log("InspectorWindow.onHide()");
		super.onHide();
		SelectionService.get().removeListener(selChangeProxy);
	}
	
	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<SVGElementModel> se) {
		ContentPanel contentPanel = noSelection;
		StringBuilder heading = new StringBuilder(Format.capitalize(ModelConstants.INSTANCE.inspectorWindow()));
		heading.append(": ");
		SVGElementModel model = null;
		if (currentInspector != null) {
			currentInspector.unbind();
			currentInspector = null;
		}
		List<SVGElementModel> models = se.getSelection();
		if (models != null) {
			if (models.size() > 1) {
				contentPanel = multipleSelection;
				heading.append(ModelConstants.INSTANCE.inspectorMultiSelection());
			} else {
				model = models.size() == 1 ? models.get(0) : SvgrealApp.getApp().getCssContext();
				currentInspector = getInspector(model);
				currentInspector.bind(model);
				contentPanel = currentInspector;
				heading.append(model.get(SVGConstants.SVG_TITLE_TAG));
			}
		}
		cardLayout.setActiveItem(contentPanel);
		setHeading(heading.toString());
		return true;
	}

	private Inspector<SVGElementModel> getInspector(SVGElementModel model) {
		if (metaModelToInspector == null) {
			metaModelToInspector = new HashMap<MetaModel<SVGElement>, Inspector<SVGElementModel>>();
		}
		MetaModel<SVGElement> metaModel = model.getMetaModel();
		Inspector<SVGElementModel> inspector = metaModelToInspector.get(metaModel);
		if (inspector == null) {
			inspector = new Inspector<SVGElementModel>(model.getMetaModel());
			metaModelToInspector.put(metaModel, inspector);
			contentPanel.add(inspector);
		}
		return inspector;
	}
}
