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

import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.ICommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

/**
 * Menu item class for menu items in the command factory menu.
 * @author laaglu
 */
public class CommandFactoryMenuItem extends MenuItem {
	private IFactoryInstantiator<?> instantiator;
	public CommandFactoryMenuItem(IFactoryInstantiator<?> instantiator) {
		this.instantiator = instantiator;
		setText(instantiator.getName());
	}
	@Override
	protected void onClick(ComponentEvent be) {
		GWT.log("CommandFactoryMenuItem.onClick");
		// Terminate current factory if needed.
		ICommandFactory factory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (factory != null) {
			factory.stop();
		}
		instantiator.create().start(CommandFactoryMenuItem.this);
		super.onClick(be);
	}
}
