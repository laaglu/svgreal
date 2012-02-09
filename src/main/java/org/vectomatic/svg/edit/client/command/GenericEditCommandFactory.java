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
package org.vectomatic.svg.edit.client.command;

import java.util.Map;

import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

/**
 * Command factory to record changes to one property of an SVG element.
 * @author laaglu
 */
public class GenericEditCommandFactory extends EditCommandFactoryBase {

	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<GenericEditCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<GenericEditCommandFactory>(ModelConstants.INSTANCE.genericEditCmdFactory(), ModelConstants.INSTANCE.genericEditCmdFactoryDesc()) {

		@Override
		public GenericEditCommandFactory create() {
			return new GenericEditCommandFactory();
		}	
	};
	
	protected GenericEditCommandFactory() {
	}

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public ICommand createCommand(SVGElementModel model,
			Map<String, Object> changes) {
		return new GenericEditCommand(this, model, changes, ModelConstants.INSTANCE.editCmd());
	}

}
