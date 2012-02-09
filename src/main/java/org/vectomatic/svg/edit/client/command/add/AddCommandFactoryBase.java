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
package org.vectomatic.svg.edit.client.command.add;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.command.CommandFactoryBase;
import org.vectomatic.svg.edit.client.command.GenericAddCommand;
import org.vectomatic.svg.edit.client.command.ICommand;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.panels.CommandFactoryToolBar;
import org.vectomatic.svg.edit.client.model.IMetadata;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.svg.CssContextModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.google.gwt.core.client.GWT;


/**
 * Base class for command factories which add elements
 * to the model.
 */
public abstract class AddCommandFactoryBase extends CommandFactoryBase {
	/**
	 * The SVG model to which the element will be added
	 */
	protected SVGModel owner;
	/**
	 * If true, the command should create just one element
	 * before auto-stopping, otherwise, it should keep building
	 * commands until explicitely stopped
	 */
	protected boolean oneShot;

	@Override
	public void start(Object requester) {
		GWT.log("AddCommandFactoryBase.start(" + requester + ")");
		super.start(requester);
		if (requester instanceof CommandFactoryToolBar) {
			// The request comes from the command factory combo		
			// Become the target command factory.
			oneShot = false;
		} else {
			// The request comes from the context menu. Create
			// just one element
			oneShot = true;
		}
	}
	
	protected void createCommand(OMSVGElement svgElement) {
		GWT.log("AddCommandFactoryBase.createCommand(" + svgElement + ")");
		SVGElement twin = (SVGElement) svgElement.getElement();
		SVGElement element = (SVGElement) twin.cloneNode(true);
		owner.getElementGroup().getElement().appendChild(element);
		SVGElementModel model = owner.create(element, twin);
		SVGElementModel parentModel = owner.convert((SVGElement) owner.getElementGroup().getElement());
		owner.add(parentModel, model);
		ICommand command = new GenericAddCommand(this, model);
		owner.getCommandStore().addCommand(command);
		svgElement.getStyle().clearSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY);		
		if (oneShot) {
			stop();
		}
	}
	
	protected void applyCssContextStyle(SVGElement element) {
		MetaModel<SVGElement> metamodel = SVGModel.getMetamodel(element);
		CssContextModel cssContext = SvgrealApp.getApp().getCssContext();
		for (IMetadata metadata : metamodel.getCategory(ModelCategory.STROKEFILL).getMetadata()) {
			metadata.set(element, cssContext.get(metadata.getName()));
		}
	}
}
