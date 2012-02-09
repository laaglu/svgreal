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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.Collection;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGTitleElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;
import org.vectomatic.svg.edit.client.command.EditTitleCommandFactory;
import org.vectomatic.svg.edit.client.command.GenericEditCommandFactory.GenericEditFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.XPathMetadata;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * Base class for elements which accept an svg title and an svg desc.
 * @author laaglu
 */
public abstract class SVGNamedElementModel extends SVGElementModel {

	private static ModelCategory<SVGElement> namingCategory;

	public SVGNamedElementModel(SVGModel owner, SVGElement element, SVGElement twin) {
		super(owner, element, twin);
		String title = createTitleDesc(element, "");
		createTitleDesc(twin, title);
		if (title.length() == 0) {
			set(SVGConstants.SVG_TITLE_TAG, owner.generateName(this));
		}
	}
	
	/**
	 * Ensures the specified element has a title and a desc. If
	 * no title is found, a new title is set to specified value
	 * @param element
	 * The element to control
	 * @param name
	 * The value of the title none pre-exists in the element
	 * @return
	 * The title value
	 */
	public static String createTitleDesc(SVGElement element, String name) {
		// Create the title child node if missing
		Document document = DOMHelper.getCurrentDocument();
		SVGTitleElement title = DOMHelper.evaluateNodeXPath(element, "./svg:title", SVGPrefixResolver.INSTANCE);
		if (title == null) {
			title = DOMHelper.createElementNS(document, SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_TITLE_TAG).cast();
			Node firstChild = element.getFirstChild();
			if (firstChild == null) {
				element.appendChild(title);
			} else {
				element.insertBefore(title, firstChild);
			}
		}
		Text titleText = DOMHelper.evaluateNodeXPath(element, "./svg:title/text()", SVGPrefixResolver.INSTANCE);
		if (titleText == null) {
			titleText = title.appendChild(document.createTextNode(""));
		}
		if (titleText.getData().length() == 0) {
			titleText.setData(name);
		}
		
		// Create the desc child node if missing
		SVGTitleElement desc = DOMHelper.evaluateNodeXPath(element, "./svg:desc", SVGPrefixResolver.INSTANCE);
		if (desc == null) {
			desc = DOMHelper.createElementNS(document, SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_DESC_TAG).cast();
			element.insertAfter(desc, title);
		}
		Text descText = DOMHelper.evaluateNodeXPath(element, "./svg:desc/text()", SVGPrefixResolver.INSTANCE);
		if (descText == null) {
			descText = desc.appendChild(document.createTextNode(""));
		}
		return titleText.getData();
	}
	
	public static ModelCategory<SVGElement> getNamingCategory() {
		if (SVGNamedElementModel.namingCategory == null) {
			ModelConstants constants = ModelConstants.INSTANCE;
			SVGNamedElementModel.namingCategory = new ModelCategory<SVGElement>(
					ModelCategory.NAMING, 
					constants.naming(), 
					GenericSectionFactory.INSTANCE);
			XPathMetadata<String> title = new XPathMetadata<String>(
					SVGConstants.SVG_TITLE_TAG, 
					constants.title(), 
					FormPanelUtils.TEXTFIELD_FACTORY, 
					"./svg:title/text()",
					EditTitleCommandFactory.INSTANTIATOR);
			XPathMetadata<String> desc = new XPathMetadata<String>(
					SVGConstants.SVG_DESC_TAG, 
					constants.desc(), 
					FormPanelUtils.TEXTAREA_FACTORY, 
					"./svg:desc/text()",
					new GenericEditFactoryInstantiator(constants.genericEditCmdFactory(), constants.genericEditCmdFactoryDesc()));
			SVGNamedElementModel.namingCategory.addMetadata(title);
			SVGNamedElementModel.namingCategory.addMetadata(desc);
		}
	    return SVGNamedElementModel.namingCategory;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public static String getNames(Collection<SVGElementModel> models) {
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for (SVGElementModel model : models) {
			if (count > 0) {
				builder.append(", ");
			}
			builder.append(((SVGNamedElementModel)model).getName());
			count++;
		}
		return builder.toString();
	}
	
	public String getName() {
		return get(SVGConstants.SVG_TITLE_TAG);
	}
	public String getDescription() {
		return get(SVGConstants.SVG_DESC_TAG);
	}
}
