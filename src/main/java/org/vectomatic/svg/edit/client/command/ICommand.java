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

import com.extjs.gxt.ui.client.data.BeanModel;


/**
 * Interface to represent a command. A command is an atomic modification of
 * the SVG model. The modification can be done or undone. The command provides various descriptions
 * of its effect on the model.
 */
public interface ICommand {
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	/**
	 * Provides a detailed description of this command. The message 
	 * must describe precisely what the command does ("Group circle #11, square #207" or
	 * "Apply translate(24, 14) to circle #22")
	 */
	public String getDescription();

	/**
	 * Applies the modification represented by this command to the model
	 */
	public void commit();

	/**
	 * Restores the model to its previous state
	 */
	public void rollback();
	
	/**
	 * Returns the factory which manufactured this command
	 */
	public ICommandFactory getFactory();
	
	/**
	 * Returns a BeanModel wrapping this command
	 * @return a BeanModel wrapping this command
	 */
	public BeanModel asModel();
}
