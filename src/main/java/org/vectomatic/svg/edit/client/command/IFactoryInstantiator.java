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

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * Interface to instantiate command factories
 * @param <T>
 * The kind of factory which will be create by this instantiator
 */
public interface IFactoryInstantiator<T extends ICommandFactory> extends ModelData {
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	/**
	 * Create a new ICommandFactory instance
	 * @return
	 */
	public T create();
	/**
	 * Provides a short description of the command manufactured by
	 * this factory. The message must be suitable for display 
	 * in an undo stack ("Group elements" or "Transform element")
	 */
	public String getName();
	/**
	 * Provides a long description explaining in a few line what
	 * the command does.
	 */
	public String getDescription();
}
