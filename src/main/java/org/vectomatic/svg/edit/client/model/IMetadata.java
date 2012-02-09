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
package org.vectomatic.svg.edit.client.model;

import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;

/**
 * Interface to abstract access to a property of a
 * GXT ModelData model object
 * @author laaglu
 * @param <T> The property type
 * @param <U> The type of the object which has this property
 */
public interface IMetadata<T, U> {
	/**
	 * Returns the identifier of this property.
	 * @return
	 */
	String getName();
	/**
	 * Returns a human readable description of this
	 * property.
	 * @return
	 */
	String getDescription();
	/**
	 * Returns the field factory of this property.
	 * @return the field factory of this property
	 */
	IFieldFactory getFieldFactory();
	/**
	 * Returns the instantiator for the factory which will record modifications
	 * to this metadata
	 * @return the instantiator for the factory which will record modifications
	 * to this metadata
	 */
	IFactoryInstantiator<?> getCommandFactory();
	/**
	 * Returns the model category this metadata belongs to
	 * @return the model category this metadata belongs to
	 */
	ModelCategory<U> getCategory();
	/**
	 * Returns the value of this property for
	 * the specified model object.
	 * @param model The model object.
	 * @return The value of this property for the specified model object.
	 */
	T get(U model);
	/**
	 * Sets the value of this property for
	 * the specified model object and returns its former value.
	 * @param model The model object.
	 * @param value The value of this property for the specified model object.
	 * @return The former value of the property.
	 */
	T set(U model, T value);
	/**
	 * Removes the property from the the specified 
	 * model object and returns its former value.
	 * @param model The model object.
	 * @return The former value of the property.
	 */
	T remove(U model);
	/**
	 * Validates the specified metadata.
	 * @param model The model object.
	 * @param value The value of this property for the specified model object.
	 * @return null if the value is valid, or a validation error
	 * message if the value is invalid.
	 */
	ValidationError validate(U model, T value);
}