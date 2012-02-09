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

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;

/**
 * Metadata class based on DOM attributes
 * @author laaglu
 */
public class AttrMetadata extends MetadataBase<String, SVGElement> {
	protected String attrName;
	public AttrMetadata(String propertyName, String description, IFieldFactory fieldFactory, String attrName, IFactoryInstantiator<?> factory) {
		super(propertyName, description, fieldFactory, factory);
		this.attrName = attrName;
	}

	@Override
	public String get(SVGElement element) {
		return element.getAttribute(attrName);
	}

	@Override
	public String set(SVGElement element, String value) {
		String oldValue = element.getAttribute(attrName);
		element.setAttribute(attrName, value);
		return oldValue;
	}

	@Override
	public String remove(SVGElement element) {
		String oldValue = element.getAttribute(attrName);
		element.removeAttribute(attrName);
		return oldValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("AttrMetadata(");
		builder.append(propertyName);
		builder.append(", ");
		builder.append(attrName);
		builder.append(")");
		return builder.toString();
	}
}
