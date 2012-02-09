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
 * Base class for metadata
 * @author laaglu
 * @param <T>
 * The metadata type
 * @param <U>
 * The javascript type which provides the property or object
 */
public abstract class MetadataBase<T,U> implements IMetadata<T,U> {
	protected String propertyName;
	protected String description;
	protected IFieldFactory fieldFactory;
	protected ModelCategory<U> category;
	protected IFactoryInstantiator<?> factory;
	protected IValidator<T,U> validator;

	public MetadataBase(String propertyName, String description, IFieldFactory fieldFactory, IFactoryInstantiator<?> factory, IValidator<T,U> validator) {
		this.propertyName = propertyName;
		this.description = description;
		this.fieldFactory = fieldFactory;
		this.factory = factory;
		this.validator = validator;
	}

	@Override
	public String getName() {
		return propertyName;
	}
	@Override
	public String getDescription() {
		return description;
	}
	@Override
	public IFieldFactory getFieldFactory() {
		return fieldFactory;
	}
	@Override
	public IFactoryInstantiator<?> getCommandFactory() {
		return factory;
	}
	@Override
	public ModelCategory<U> getCategory() {
		return category;
	}
	@Override
	public ValidationError validate(U model, T value) {
		return (validator == null) ? null : validator.validate(model, value);
	}
}
