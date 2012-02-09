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

import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.command.ICommand;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;

import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.google.gwt.event.dom.client.KeyCodes;


/**
 * Class to manage intra-model clone drag-and-drop interactions
 * and inter-model copy drag-and-drop interactions 
 * @author laaglu
 */
public class DndCopyHandler extends DndHandlerBase {
	@Override
	public boolean isValidSource(DNDEvent event, List<SVGElementModel> sourceElements) {
		return event.isControlKey() && super.isValidSource(event, sourceElements);
	}

	@Override
	public String getOperationCssAttr() {
		return "copy";
	}

	@Override
	public String getMessage(List<SVGElementModel> sourceElements) {
		return Format.substitute(ModelConstants.INSTANCE.dndCopy(), getSourceElementNames(sourceElements));
	}

	@Override
	public int getKeyCode() {
		return KeyCodes.KEY_CTRL;
	}

	@Override
	public void createCommands(List<SVGElementModel> sourceElements, SVGElementModel target, DropGesture dropGesture) {
		ICommand command = null;
		SVGModel src = sourceElements.get(0).getOwner();
		SVGModel dest = target.getOwner();
		if (src == dest) {
			command = new CloneCommand(sourceElements, target, dropGesture);
		} else {
			String description = null;
			switch(dropGesture) {
				case OnNode:
					description = ModelConstants.INSTANCE.dndCopyCmdIn();
					break;
				case BeforeNode:
					description = ModelConstants.INSTANCE.dndCopyCmdBefore();
					break;
				case AfterNode:
					description = ModelConstants.INSTANCE.dndCopyCmdAfter();
					break;
			}
			description = Format.substitute(description, SVGNamedElementModel.getNames(sourceElements), target.toString(), ((SVGNamedElementModel)src.getRoot()).getName());
			command = new CopyCommand(sourceElements, target, dropGesture, description);
		}
		command.commit();
		dest.getCommandStore().addCommand(command);		
	}
	
}
