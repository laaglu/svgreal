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
package org.vectomatic.svg.edit.client.gxt.widget;

import org.vectomatic.svg.edit.client.command.CommandStore;
import org.vectomatic.svg.edit.client.command.ICommand;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

/**
 * Menu item class for items in the redo menu
 * @author laaglu
 */
public class RedoCommandMenuItem extends MenuItem {
	private CommandStore store;
	private BeanModel command;
	public RedoCommandMenuItem(CommandStore store, BeanModel command) {
		this.store = store;
		this.command = command;
		setText(((ICommand)command.getBean()).getDescription());
	}
	@Override
	protected void onClick(ComponentEvent be) {
		GWT.log("RedoCommandMenuItem.onClick");
		super.onClick(be);
		store.redo(command);
	}

}
