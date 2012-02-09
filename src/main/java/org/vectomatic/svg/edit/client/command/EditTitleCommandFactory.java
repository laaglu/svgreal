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

import com.google.gwt.core.client.GWT;

/**
 * Command factory to record changes in the title of svg elements
 * @author laaglu
 */
public class EditTitleCommandFactory extends EditCommandFactoryBase {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<EditTitleCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<EditTitleCommandFactory>(ModelConstants.INSTANCE.renameElementCmdFactory(), ModelConstants.INSTANCE.renameElementCmdFactoryDesc()) {
		@Override
		public EditTitleCommandFactory create() {
			return new EditTitleCommandFactory();
		}
	};
	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		GWT.log("EditTitleCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.renameElementCmdFactory1());
	}

	@Override
	protected ICommand createCommand(SVGElementModel model,	Map<String, Object> changes) {
		return new EditTitleCommand(this, model, changes, ModelConstants.INSTANCE.renameCmd());
	}
}
