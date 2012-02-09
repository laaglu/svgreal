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
package org.vectomatic.svg.edit.client.model;

import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;

/**
 * Metadata class based on a java script property or a function
 * @author laaglu
 * @param <T>
 * The metadata type
 * @param <U>
 * The javascript type which provides the property or object
 */
public class JSMetadata<T,U> extends MetadataBase<T,U> {
	protected IPropertyAccessor<T,U> accessor;
	public JSMetadata(String propertyName, String description, IFieldFactory fieldFactory, IPropertyAccessor<T,U> accessor, IFactoryInstantiator<?> factory) {
		super(propertyName, description, fieldFactory, factory);
		this.accessor = accessor;
	}

	@Override
	public T get(U element) {
		return accessor.get(element);
	}

	@Override
	public T set(U element, T value) {
		T oldValue = accessor.get(element);
		accessor.set(element, value);
		return oldValue;
	}

	@Override
	public T remove(U element) {
		throw new IllegalStateException("Property " + propertyName + " cannot be removed");
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("JSMetadata(");
		builder.append(propertyName);
		builder.append(")");
		return builder.toString();
	}
}
