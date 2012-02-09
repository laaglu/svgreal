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

import org.vectomatic.dom.svg.impl.Attr;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * Metadata class based on DOM xpaths
 * @author laaglu
 * @param <T>
 * The property type
 */
public class XPathMetadata<T> extends MetadataBase<T, SVGElement> {
	private String xpath;
	public XPathMetadata(String propertyName, String description, IFieldFactory fieldFactory, String xpath, IFactoryInstantiator<?> factory, IValidator<T, SVGElement> validator) {
		super(propertyName, description, fieldFactory, factory, validator);
		this.xpath = xpath;
	}
	
	public String getXPath() {
		return xpath;
	}
	
	@Override
	public T get(SVGElement element) {
		Node node = DOMHelper.evaluateNodeXPath(element, xpath, SVGPrefixResolver.INSTANCE);
		if (node != null) {
			switch(node.getNodeType()) {
				case Node.TEXT_NODE:
					return (T)((Text)node.cast()).getData();
				case 2:
					return (T)((Attr)node.cast()).getValue();
				default:
					assert(false);
			}
		}
		return null;
	}
	
	@Override
	public T set(SVGElement element, T value) {
		T oldValue = null;
		Node node = DOMHelper.evaluateNodeXPath(element, xpath, SVGPrefixResolver.INSTANCE);
		if (node != null) {
			switch(node.getNodeType()) {
				case Node.TEXT_NODE:
					{
						Text text = node.cast();
						oldValue = (T)text.getData();
						text.setData((String)value);
					}
					break;
				case 2:
					{
						Attr attr = node.cast();
						oldValue = (T)attr.getValue();
						attr.setValue((String)value);
					}
					break;
				default:
					assert(false);
			}
		}
		return oldValue;
	}
	
	@Override
	public T remove(SVGElement element) {
		T oldValue = null;
		Node node = DOMHelper.evaluateNodeXPath(element, xpath, SVGPrefixResolver.INSTANCE);
		if (node != null) {
			switch(node.getNodeType()) {
				case Node.TEXT_NODE:
					{
						Text text = node.cast();
						oldValue = (T)text.getData();
						text.getParentElement().removeChild(text);
					}
				break;
				case 2:
					{
						Attr attr = node.cast();
						oldValue = (T)attr.getValue();
						attr.getOwnerElement().removeAttribute(attr.getName());
					}
				break;
				default:
					assert(false);
			}
		}
		return oldValue;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("XPathMetadata(");
		builder.append(propertyName);
		builder.append(", ");
		builder.append(xpath);
		builder.append(")");
		return builder.toString();
	}
}
