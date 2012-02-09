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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.List;

import org.vectomatic.svg.edit.client.command.CommandFactories;
import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.command.GenericRemoveCommand;
import org.vectomatic.svg.edit.client.command.ICommand;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;

import com.extjs.gxt.ui.client.util.Format;

/**
 * Class to manage intra-model reorder drag-and-drop interactions
 * and inter-model move drag-and-drop interactions 
 * @author laaglu
 */
public class DndMoveHandler extends DndHandlerBase {

	@Override
	public String getOperationCssAttr() {
		return "move";
	}
	
	@Override
	public String getMessage(List<SVGElementModel> sourceElements) {
		return Format.substitute(ModelConstants.INSTANCE.dndMove(), getSourceElementNames(sourceElements));
	}

	@Override
	public void createCommands(List<SVGElementModel> sourceElements, SVGElementModel target, DropGesture dropGesture) {
		SVGModel src = sourceElements.get(0).getOwner();
		SVGModel dest = target.getOwner();
		if (src == dest) {
			ICommand command = new ReorderCommand(sourceElements, target, dropGesture);
			command.commit();
			src.getCommandStore().addCommand(command);
		} else {
			String description = null;
			switch(dropGesture) {
				case OnNode:
					description = ModelConstants.INSTANCE.dndMoveCmdDestIn();
					break;
				case BeforeNode:
					description = ModelConstants.INSTANCE.dndMoveCmdDestBefore();
					break;
				case AfterNode:
					description = ModelConstants.INSTANCE.dndMoveCmdDestAfter();
					break;
			}
			description = Format.substitute(description, SVGNamedElementModel.getNames(sourceElements), target.toString(), ((SVGNamedElementModel)src.getRoot()).getName());
			ICommand copyCommand = new CopyCommand(sourceElements, target, dropGesture, description);
			copyCommand.commit();
			ICommand removeCommand = new GenericRemoveCommand(CommandFactories.getDndCommandFactory(), sourceElements, Format.substitute(ModelConstants.INSTANCE.dndMoveCmdSrc(), SVGNamedElementModel.getNames(sourceElements), ((SVGNamedElementModel)dest.getRoot()).getName()));
			removeCommand.commit();
			dest.getCommandStore().addCommand(copyCommand);
			src.getCommandStore().addCommand(removeCommand);
		}
	}
}
