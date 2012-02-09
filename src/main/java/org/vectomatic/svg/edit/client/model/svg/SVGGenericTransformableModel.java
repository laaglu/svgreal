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
import org.vectomatic.svg.edit.client.command.EditTransformCommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;

public class SVGGenericTransformableModel extends SVGGenericElementModel {
	private MetaModel<SVGElement> metaModel;
	public SVGGenericTransformableModel(SVGModel owner, SVGElement element,	SVGElement twin) {
		super(owner, element, twin);
	}
	
	@Override
	public MetaModel<SVGElement> getMetaModel() {
		if (metaModel == null) {
			IFactoryInstantiator<?>[][] contextMenuFactories = new IFactoryInstantiator<?>[][] {
				{
					EditTransformCommandFactory.INSTANTIATOR
				}
			};
			metaModel = new MetaModel<SVGElement>();
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(SVGNamedElementModel.getGlobalCategory());
			categories.add(SVGElementModel.getTransformCategory());
			metaModel.init(
				null,
				elementNameToIcon.get(element.getTagName()), 
				categories,
				contextMenuFactories);

		}
		return metaModel;
	}

}
