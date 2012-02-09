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

import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.gxt.widget.CommandFactoryMenuItem;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.google.gwt.core.client.GWT;

/**
 * Command factory to build commands which remove elements from the model
 * @author laaglu
 */
public class RemoveElementsCommandFactory extends CommandFactoryBase implements SelectionChangedProcessor<SVGElementModel> {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<RemoveElementsCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<RemoveElementsCommandFactory>(ModelConstants.INSTANCE.removeElementsCmdFactory(), ModelConstants.INSTANCE.removeElementsCmdFactoryDesc()) {
		@Override
		public RemoveElementsCommandFactory create() {
			return new RemoveElementsCommandFactory();
		}
	};

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		GWT.log("RemoveElementsCommandFactory.start(" + requester + ")");
		SVGModel svgModel = VectomaticApp2.getApp().getActiveModel();
		if (requester instanceof SVGModel || requester instanceof CommandFactoryMenuItem) {
			// The request comes from the DEL key or the context menu.
			// Erase the elements, but do not push the factory on the stack.
			createCommand(svgModel);
			return;
		} 
		super.start(requester);

		// The request comes from the command factory combo
		// Clear the selection and listen to selection events
		if (svgModel != null) {
			svgModel.getSelectionModel().deselectAll();
		}
		
		// Become the target command factory.
		updateStatus(ModelConstants.INSTANCE.removeElementsCmdFactory1());
	}

	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<SVGElementModel> se) {
		List<SVGElementModel> models = se.getSelection();
		GWT.log("RemoveElementsCommandFactory.processSelectionChanged: " + models);
		if (models.size() > 0) {
			createCommand(models.get(0).getOwner());
			return true;
		}
		return false;
	}
	
	private void createCommand(SVGModel svgModel) {
		GWT.log("RemoveElementsCommandFactory.createCommand(" + svgModel + ")");
		if (svgModel != null) {
			List<SVGElementModel> models = svgModel.getSelectionModel().getSelectedItems();
			// Prevent the viewBox from being removed
			models.remove(svgModel.getViewBox());
			if (models.size() > 0) {
				GenericRemoveCommand command = new GenericRemoveCommand(this, models, Format.substitute(ModelConstants.INSTANCE.removeCmd(), SVGNamedElementModel.getNames(models)));
				command.commit();
				svgModel.getCommandStore().addCommand(command);
			}
		}
	}
}
