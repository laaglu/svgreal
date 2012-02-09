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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGLineElement;
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

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Line model class.
 * @author laaglu
 */
public class SVGLineElementModel extends SVGStyledElementModel {
	private static MetaModel<SVGElement> metaModel;
	
	public SVGLineElementModel(SVGModel owner, SVGLineElement element, SVGLineElement twin) {
		super(owner, element, twin);
	}
	
	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getLineElementMetaModel();
	}
	
	public static MetaModel<SVGElement> getLineElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			ModelConstants constants = ModelConstants.INSTANCE;

			ModelCategory<SVGElement> geometricCategory = new ModelCategory<SVGElement>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			MetadataBase<SVGLength, SVGElement> x1 = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_X1_ATTRIBUTE, 
				constants.lineX1(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGLineElement)element.cast()).getX1().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> y1 = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_Y1_ATTRIBUTE, 
				constants.lineY1(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGLineElement)element.cast()).getY1().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> x2 = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_X2_ATTRIBUTE, 
				constants.lineX2(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGLineElement)element.cast()).getX2().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> y2 = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_Y2_ATTRIBUTE, 
				constants.lineY2(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGLineElement)element.cast()).getY2().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			geometricCategory.addMetadata(x1);
			geometricCategory.addMetadata(y1);
			geometricCategory.addMetadata(x2);
			geometricCategory.addMetadata(y2);
			IFactoryInstantiator<?>[][] contextMenuFactories = new IFactoryInstantiator<?>[][] {
				{
					EditGeometryCommandFactory.INSTANTIATOR,
					EditTransformCommandFactory.INSTANTIATOR
				}
			};
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(SVGNamedElementModel.getNamingCategory());
			categories.add(SVGElementModel.getDisplayCategory());
			categories.add(geometricCategory);
			categories.add(SVGStyledElementModel.createStrokeFillCategory(
					new String[] {
						SVGConstants.CSS_STROKE_PROPERTY,
						SVGConstants.CSS_STROKE_OPACITY_PROPERTY,
						SVGConstants.CSS_STROKE_WIDTH_PROPERTY,
						SVGConstants.CSS_STROKE_LINECAP_PROPERTY,
						SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY,
						SVGConstants.CSS_STROKE_DASHOFFSET_PROPERTY
					}));
			categories.add(SVGElementModel.getTransformCategory());
			metaModel.init(
				constants.line(),
				AbstractImagePrototype.create(AppBundle.INSTANCE.line()),
				categories,
				contextMenuFactories);

		}
		return metaModel;
	}
}
