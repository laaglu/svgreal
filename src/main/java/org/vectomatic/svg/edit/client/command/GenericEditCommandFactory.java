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

import java.util.Map;

import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

/**
 * Command factory to record changes to one property of an SVG element.
 * @author laaglu
 */
public class GenericEditCommandFactory extends EditCommandFactoryBase {
	private GenericEditFactoryInstantiator instantiator;
	
	@SuppressWarnings("serial")
	public static class GenericEditFactoryInstantiator extends FactoryInstantiatorBase<GenericEditCommandFactory> {

		public GenericEditFactoryInstantiator(String name, String description) {
			super(name, description);
		}

		@Override
		public GenericEditCommandFactory create() {
			return new GenericEditCommandFactory(this);
		}	
	}
	

	protected GenericEditCommandFactory(GenericEditFactoryInstantiator instantiator) {
		this.instantiator = instantiator;
	}

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return instantiator;
	}

	@Override
	protected ICommand createCommand(SVGElementModel model,
			Map<String, Object> changes) {
		return new GenericEditCommand(this, model, changes, ModelConstants.INSTANCE.editCmd());
	}

}
