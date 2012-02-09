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
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.google.gwt.event.dom.client.KeyCodes;


/**
 * Class to manage instantiation of def elements drag-and-drop interactions
 * @author laaglu
 */
public class DndLinkHandler extends DndHandlerBase {
	@Override
	public boolean isValidSource(DNDEvent event, List<SVGElementModel> sourceElements) {
		return event.isAltKey() && super.isValidSource(event, sourceElements);
	}

	@Override
	public String getOperationCssAttr() {
		return "link";
	}

	@Override
	public String getMessage(List<SVGElementModel> sourceElements) {
		return Format.substitute(ModelConstants.INSTANCE.dndLink(), getSourceElementNames(sourceElements));
	}

	@Override
	public int getKeyCode() {
		return KeyCodes.KEY_ALT;
	}

	@Override
	public void createCommands(List<SVGElementModel> sourceElements, SVGElementModel target, DropGesture dropGesture) {
		// TODO Auto-generated method stub
		
	}

}
