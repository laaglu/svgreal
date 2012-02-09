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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.EditTitleCommandFactory;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.XPathMetadata;

import com.extjs.gxt.ui.client.store.Store;

/**
 * Class to model the Css graphic context (the set of CSS properties
 * used to instantiate new SVG element models)
 * @author laaglu
 */
public class CssContextModel extends SVGStyledElementModel {
	private static MetaModel<SVGElement> metaModel;
	private static DashArrayStore defaultDashArrays;

	public CssContextModel(SVGElement element) {
		super(null, element, element);
	}

	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getContextElementMetaModel();
	}
	
	@Override
	public Store getStore() {
		return null;
	}
	
	public static DashArrayStore getDefaultDashArrays() {
		if (defaultDashArrays == null) {
			defaultDashArrays = new DashArrayStore();
			String[] dashArrays = {null, "1,1", "2,2", "5,5", "2,1", "5,1", "10,3,5,3"};
			for (String dashArray : dashArrays) {
				defaultDashArrays.add(DashArray.parse(dashArray));
			}
		}
		return defaultDashArrays;
	}
	
	public static void setDefaultDashArrays(DashArrayStore dashArrays) {
		defaultDashArrays = dashArrays;
	}

	private static MetaModel<SVGElement> getContextElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			ModelCategory<SVGElement> category = SVGStyledElementModel.createStrokeFillCategory(
					new String[] {
						SVGConstants.CSS_FILL_PROPERTY,
						SVGConstants.CSS_FILL_OPACITY_PROPERTY,
						SVGConstants.CSS_FILL_RULE_PROPERTY,
						SVGConstants.CSS_STROKE_PROPERTY,
						SVGConstants.CSS_STROKE_OPACITY_PROPERTY,
						SVGConstants.CSS_STROKE_WIDTH_PROPERTY,
						SVGConstants.CSS_STROKE_LINECAP_PROPERTY,
						SVGConstants.CSS_STROKE_LINEJOIN_PROPERTY,
						SVGConstants.CSS_STROKE_MITERLIMIT_PROPERTY,
						SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY,
						SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY
					});
			XPathMetadata<String> title = new XPathMetadata<String>(
					SVGConstants.SVG_TITLE_TAG, 
					ModelConstants.INSTANCE.title(), 
					null, 
					"./svg:title/text()",
					EditTitleCommandFactory.INSTANTIATOR,
					null);
			category.addMetadata(title);
			categories.add(category);
			metaModel.init(
				"cssContext",
				null, 
				categories,
				null);
		}
		return metaModel;
	}
}
