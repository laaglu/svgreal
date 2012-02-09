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
package org.vectomatic.svg.edit.client.command.add;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.CommandFactoryBase;
import org.vectomatic.svg.edit.client.command.FactoryInstantiatorBase;
import org.vectomatic.svg.edit.client.command.GenericAddCommand;
import org.vectomatic.svg.edit.client.command.ICommand;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

/**
 * Command factory to add new groups to the the SVG model.
 * @author laaglu
 */
public class AddGroupCommandFactory extends CommandFactoryBase {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddGroupCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddGroupCommandFactory>(ModelConstants.INSTANCE.addGroupCmdFactory(), ModelConstants.INSTANCE.addGroupCmdFactoryDesc()) {
		@Override
		public AddGroupCommandFactory create() {
			return new AddGroupCommandFactory();
		}
	};

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		SVGModel owner = VectomaticApp2.getApp().getActiveModel();
		OMSVGElement parent = owner.getTwinGroup();
		OMSVGSVGElement svg = parent.getOwnerSVGElement();
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		OMSVGGElement group = document.createSVGGElement();
		parent.appendChild(group);
		
		SVGElement twin = (SVGElement) group.getElement();
		SVGElement element = (SVGElement) twin.cloneNode(true);
		owner.getElementGroup().getElement().appendChild(element);
		
		SVGElementModel model = owner.create(element, twin);
		SVGElementModel parentModel = owner.convert((SVGElement) owner.getElementGroup().getElement());
		owner.add(parentModel, model);
		ICommand command = new GenericAddCommand(this, model);
		owner.getCommandStore().addCommand(command);
	}
}
