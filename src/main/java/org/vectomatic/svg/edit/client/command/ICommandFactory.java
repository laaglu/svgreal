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


/**
 * Interface for command factories. Command factories
 * are responsible for translating user input into
 * instances of ICommand
 */
public interface ICommandFactory {
	public IFactoryInstantiator<?> getInstantiator();
	/**
	 * Returns the status to display in the status line in
	 * the command factory toolbar
	 * @return the status to display in the status line in
	 * the command factory toolbar
	 */
	public String getStatus();
	/**
	 * Invoked when the user selects this command factory (through
	 * the command toolbar, the context menu, or implicitely by editing
	 * a field in the inspector).
	 * @param requester The object requesting this command to start
	 */
	public void start(Object requester);
	/**
	 * Invoked when this command factory terminates its lifecycle (on
	 * its own accord or because or because another command factory
	 * preempts it, or because the end-user terminates it)
	 */
	public void stop();
}
