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
package org.vectomatic.svg.edit.client.gxt.widget;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.svg.edit.client.SVGWindow;
import org.vectomatic.svg.edit.client.command.CommandFactories;
import org.vectomatic.svg.edit.client.command.DndCommandFactory;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.google.gwt.core.client.GWT;

/**
 * Drag source for SVGWindow
 * @author laaglu
 */
public class SVGTreePanelDragSource extends TreePanelDragSource {
	protected DndCommandFactory dndCommandFactory;
	protected SVGModel svgModel;

	public SVGTreePanelDragSource(SVGWindow window) {
		super(window.getTree());
		svgModel = window.getSvgModel();
		dndCommandFactory = CommandFactories.getDndCommandFactory();
	}
	public SVGModel getSvgModel() {
		return svgModel;
	}

	protected void onDragStart(DNDEvent event) {
		super.onDragStart(event);
		if (!event.isCancelled()) {
			// Extract the SVG models from the drag event
			List<TreeStoreModel> storeModels = event.getData();
			if (storeModels != null) {
				List<SVGElementModel> svgModels = new ArrayList<SVGElementModel>();
				for (TreeStoreModel storeModel : storeModels) {
					svgModels.add((SVGElementModel) storeModel.getModel());
				}
				
				if (dndCommandFactory.isValidSource(event, svgModels)) {
					dndCommandFactory.start(this);
				} else {
					event.setCancelled(true);
					event.getStatus().setStatus(false);
				}
			}
		}
	}


	@Override
	protected void onDragDrop(DNDEvent event) {
		GWT.log("SVGTreePanelDragSource.onDragDrop");
		dndCommandFactory.stop();
	}

	@Override
	protected void onDragFail(DNDEvent event) {
		GWT.log("SVGTreePanelDragSource.onDragFail");
		dndCommandFactory.stop();
	}
}
