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
package org.vectomatic.svg.edit.client.command;

import java.util.List;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.edit.EditManipulatorBase;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.KeyPressProcessor;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.event.MouseUpProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.gxt.widget.CommandFactoryMenuItem;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * Base class for command factories which display a graphical
 * manipulator to let the end-user interact with an SVG element
 * @author laaglu
 */
public abstract class ManipulatorCommandFactoryBase extends EditCommandFactoryBase implements SelectionChangedProcessor<SVGElementModel>, MouseDownProcessor, MouseMoveProcessor, MouseUpProcessor, KeyPressProcessor {
	/**
	 * A filter to determine if the selection represents
	 * suitable input for this command factory
	 */
	protected IModelFilter filter;
	/**
	 * The manipulator used to interact with the SVG element
	 */
	protected EditManipulatorBase manipulator;
	/**
	 * If the command factory is invoked from the context menu,
	 * the corresponding model
	 */
	protected SVGElementModel focusModel;
	protected String state1;
	protected String state2;
	
	@Override
	public void start(Object requester) {
		GWT.log("ManipulatorCommandFactoryBase.start(" + requester + ")");
		super.start(requester);
		
		SVGModel svgModel = VectomaticApp2.getApp().getActiveModel();
		if (svgModel != null) {
			List<SVGElementModel> elements = svgModel.getSelectionModel().getSelectedItems();
			if (!(requester instanceof FieldBinding)) {
				// No manipulator is required for changes through the inspector
				showManipulator(elements);
			}
			
			// If the request comes from the context menu, stop the command
			// if the selection changes
			if (requester instanceof CommandFactoryMenuItem && elements.size() == 1) {
				focusModel = elements.get(0);
			}
		}
	}

	@Override
	public void stop() {
		GWT.log("ManipulatorCommandFactoryBase.stop()");
		super.stop();
		hideManipulator();
		focusModel = null;
	}
	
	public void showManipulator(List<SVGElementModel> models) {
		if (filter.accept(models)) {
			SVGElementModel model = models.get(0);
			manipulator = getManipulator(model);
			OMSVGElement manipulatorRep = manipulator.bind(model.getRecord());
			Element twinGroup = model.getOwner().getTwinGroup().getElement();
			twinGroup.appendChild(manipulatorRep.getElement());
			updateStatus(state2);
		}
	}
	
	public void hideManipulator() {
		if (manipulator != null) {
			manipulator.unbind();
			manipulator = null;
			updateStatus(state1);
		}
	}
	
	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<SVGElementModel> se) {
		List<SVGElementModel> models = se.getSelection();
		GWT.log("ManipulatorCommandFactoryBase.processSelectionChanged: " + models);
		hideManipulator();
		if (focusModel != null && (models.size() != 1 || models.get(0) != focusModel)) {
			stop();
		} else {
			showManipulator(models);
		}
		return true;
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (manipulator != null) {
			return manipulator.processMouseUp(event);
		}
		return false;
	}

	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (manipulator != null) {
			return manipulator.processMouseMove(event);
		}
		return false;
	}
	
	@Override
	public boolean processKeyPress(ComponentEvent event) {
		if (manipulator != null) {
			return manipulator.processKeyPress(event);
		}
		return false;
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		GWT.log("ManipulatorCommandFactoryBase.processMouseDown");
		// If the user clicks in the background or on another
		// element, do not process the event.
		SVGElement target = event.getNativeEvent().getEventTarget().cast();
		if (SVGConstants.SVG_SVG_TAG.equals(target.getTagName())) {
			return false;
		}
		
		// If the user clicks on an model svg element instead
		// of a manipulator svg element, do not process the event.
		SVGModel svgModel = VectomaticApp2.getApp().getActiveModel();
		SVGElementModel model = svgModel.convert(target);			
		if (model != null) {
			return false;
		}
		
		if (manipulator != null) {
			return manipulator.processMouseDown(event);
		}

		return false;
	}

	protected abstract EditManipulatorBase getManipulator(SVGElementModel model);
}
