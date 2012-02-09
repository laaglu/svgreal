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

import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;

/**
 * Metadata class based on CSS properties
 * @author laaglu
 * @param <T>
 * The CSS property type
 */
public class CssMetadata<T> extends MetadataBase<T, SVGElement>  {
	protected IConverter<String, T> converter;
	protected T defaultValue;
	
	public CssMetadata(String propertyName, String description, IFieldFactory fieldFactory, IConverter<String, T> converter, T defaultValue, IFactoryInstantiator<?> factory) {
		super(propertyName, description, fieldFactory, factory);
		this.defaultValue = defaultValue;
		this.converter = converter;
	}
	
	@Override
	public T get(SVGElement element) {
		OMSVGStyle style = element.getStyle().cast();
		String value = style.getSVGProperty(propertyName);
		if (value == null || value.length() == 0) {
			value = element.getAttribute(propertyName);
		}
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return converter.sourceToDest(value);
	}
	
	@Override
	public T set(SVGElement element, T value) {
		OMSVGStyle style = element.getStyle().cast();
		T oldValue = get(element);
		style.setSVGProperty(propertyName, converter.destToSource(value));
		element.removeAttribute(propertyName);
		return oldValue;
	}
	
	@Override
	public T remove(SVGElement element) {
		OMSVGStyle style = element.getStyle().cast();
		T oldValue = converter.sourceToDest(style.getSVGProperty(propertyName));
		style.clearSVGProperty(propertyName);
		if (oldValue == null) {
			oldValue = converter.sourceToDest(element.getAttribute(propertyName));
		}
		element.removeAttribute(propertyName);
		return oldValue;
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("CSSMetadata(");
		builder.append(propertyName);
		builder.append(", ");
		builder.append(propertyName);
		builder.append(")");
		return builder.toString();
	}
}