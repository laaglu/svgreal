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

/**
 * Interface for classes which provide data validation
 * for a metadata.
 * @param <T>
 * The metadata type
 * @param <U>
 * The javascript type which provides the property or object
 * @author laaglu
 */
public interface IValidator<T, U> {
	/**
	 * Validates the specified value.
	 * @param model The model object.
	 * @param value The value of this property for the specified model object.
	 * @return null if the value is valid, or a validation error
	 * message if the value is invalid.
	 */
	ValidationError validate(U model, T value);
}
