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
	
	public CssMetadata(String propertyName, String description, IFieldFactory fieldFactory, IConverter<String, T> converter, T defaultValue, IFactoryInstantiator<?> factory, IValidator<T, SVGElement> validator) {
		super(propertyName, description, fieldFactory, factory, validator);
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
		String str = converter.destToSource(value);
		if (str == null || str.length() == 0) {
			// This is to deal with CSS properties which are inherited
			// Setting the value to "" or null means the property should
			// be inherited
			return remove(element);
		}
		OMSVGStyle style = element.getStyle().cast();
		T oldValue = get(element);
		style.setSVGProperty(propertyName, str);
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