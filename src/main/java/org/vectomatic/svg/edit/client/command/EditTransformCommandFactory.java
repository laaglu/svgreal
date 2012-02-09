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

import java.util.List;
import java.util.Map;

import org.vectomatic.svg.edit.client.command.edit.EditManipulatorBase;
import org.vectomatic.svg.edit.client.command.edit.EditTransformManipulator;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

/**
 * Command factory to record changes in the svg transform attribute of an element.
 * @author laaglu
 */
public class EditTransformCommandFactory extends ManipulatorCommandFactoryBase {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<EditTransformCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<EditTransformCommandFactory>(ModelConstants.INSTANCE.transformCmdFactory(), ModelConstants.INSTANCE.transformCmdFactoryDesc()) {
		@Override
		public EditTransformCommandFactory create() {
			return new EditTransformCommandFactory();
		}
	};
	
	public EditTransformCommandFactory() {
		ModelConstants constants = ModelConstants.INSTANCE;
		state1 = constants.transformCmdFactory1();
		state2 = constants.transformCmdFactory2();
		filter = new IModelFilter() {

			@Override
			public boolean accept(List<SVGElementModel> models) {
				return models.size() == 1 && models.get(0).getMetaModel().getCategory(ModelCategory.TRANSFORM) != null;
			}			
		};
	}

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	protected ICommand createCommand(SVGElementModel model, Map<String, Object> changes) {
		return new EditTransformCommand(this, model, changes);
	}
	@Override
	protected EditManipulatorBase getManipulator(SVGElementModel model) {
		return EditTransformManipulator.INSTANCE;
	}

}
