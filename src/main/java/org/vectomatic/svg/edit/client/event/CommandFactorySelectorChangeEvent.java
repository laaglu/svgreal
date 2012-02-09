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
package org.vectomatic.svg.edit.client.event;

import org.vectomatic.svg.edit.client.command.ICommandFactory;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event emitted when the command factory selector changes
 * @author laaglu
 */
public class CommandFactorySelectorChangeEvent extends GwtEvent<CommandFactorySelectorChangeHandler> {
	/**
	 * Handler type.
	 */
	private static Type<CommandFactorySelectorChangeHandler> TYPE;

	private final ICommandFactory commandFactory;

	public CommandFactorySelectorChangeEvent(ICommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommandFactorySelectorChangeHandler> getAssociatedType() {
		if (TYPE == null) {
			TYPE = new Type<CommandFactorySelectorChangeHandler>();
		}
		return TYPE;
	}

	/**
	 * Ensures the existence of the handler hook and then returns it.
	 * 
	 * @return returns a handler hook
	 */
	public static Type<CommandFactorySelectorChangeHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<CommandFactorySelectorChangeHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(CommandFactorySelectorChangeHandler handler) {
		handler.onChange(this);
	}

	public ICommandFactory getCommandFactory() {
		return commandFactory;
	}

}
