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

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.impl.SVGCircleElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.EditGeometryCommandFactory;
import org.vectomatic.svg.edit.client.command.EditTransformCommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.JSMetadata;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;

/**
 * Circle model class.
 * @author laaglu
 */
public class SVGCircleElementModel extends SVGStyledElementModel {
	private static MetaModel<SVGElement> metaModel;
	
	public SVGCircleElementModel(SVGModel owner, SVGCircleElement element, SVGCircleElement twin) {
		super(owner, element, twin);
	}
	
	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getCircleElementMetaModel();
	}
	
	public static MetaModel<SVGElement> getCircleElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			ModelConstants constants = ModelConstants.INSTANCE;
					
			ModelCategory<SVGElement> geometricCategory = new ModelCategory<SVGElement>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			MetadataBase<SVGLength, SVGElement> cx = new JSMetadata<SVGLength, SVGElement>(
					SVGConstants.SVG_CX_ATTRIBUTE, 
					constants.circleCx(),
					FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
					new SVGLengthAccessor() {
						@Override
						public OMSVGLength getLength(SVGElement element) {
							return ((SVGCircleElement)element.cast()).getCx().getBaseVal();
						}
					},
					EditGeometryCommandFactory.INSTANTIATOR,
					null);
			MetadataBase<SVGLength, SVGElement> cy = new JSMetadata<SVGLength, SVGElement>(
					SVGConstants.SVG_CY_ATTRIBUTE, 
					constants.circleCy(),
					FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
					new SVGLengthAccessor() {
						@Override
						public OMSVGLength getLength(SVGElement element) {
							return ((SVGCircleElement)element.cast()).getCy().getBaseVal();
						}
					},
					EditGeometryCommandFactory.INSTANTIATOR,
					null);
			MetadataBase<SVGLength, SVGElement> r = new JSMetadata<SVGLength, SVGElement>(
					SVGConstants.SVG_R_ATTRIBUTE, 
					constants.circleR(),
					FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
					new SVGLengthAccessor() {
						@Override
						public OMSVGLength getLength(SVGElement element) {
							return ((SVGCircleElement)element.cast()).getR().getBaseVal();
						}
					},
					EditGeometryCommandFactory.INSTANTIATOR,
					SVGLength.RADIUS_VALIDATOR);
			geometricCategory.addMetadata(cx);
			geometricCategory.addMetadata(cy);
			geometricCategory.addMetadata(r);
			IFactoryInstantiator<?>[][] contextMenuFactories = new IFactoryInstantiator<?>[][] {
				{
					EditGeometryCommandFactory.INSTANTIATOR,
					EditTransformCommandFactory.INSTANTIATOR
				}
			};
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(SVGNamedElementModel.getGlobalCategory());
			categories.add(SVGElementModel.getDisplayCategory());
			categories.add(geometricCategory);
			categories.add(SVGStyledElementModel.createStrokeFillCategory(
					new String[] {
						SVGConstants.CSS_FILL_PROPERTY,
						SVGConstants.CSS_FILL_OPACITY_PROPERTY,
						SVGConstants.CSS_STROKE_PROPERTY,
						SVGConstants.CSS_STROKE_OPACITY_PROPERTY,
						SVGConstants.CSS_STROKE_WIDTH_PROPERTY,
						SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY,
						SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY
					}));
			categories.add(SVGElementModel.getTransformCategory());
			metaModel.init(
				constants.circle(),
				AppBundle.INSTANCE.circle(), 
				categories,
				contextMenuFactories);
		}
		return metaModel;
	}
}
