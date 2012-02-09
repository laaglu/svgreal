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
import org.vectomatic.dom.svg.impl.SVGEllipseElement;
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
 * Ellipse model class.
 * @author laaglu
 */
public class SVGEllipseElementModel extends SVGStyledElementModel {
	private static MetaModel<SVGElement> metaModel;
	public SVGEllipseElementModel(SVGModel owner, SVGEllipseElement element, SVGEllipseElement twin) {
		super(owner, element, twin);
	}
	
	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getEllipseElementMetaModel();
	}
	
	public static MetaModel<SVGElement> getEllipseElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			ModelConstants constants = ModelConstants.INSTANCE;

			ModelCategory<SVGElement> geometricCategory = new ModelCategory<SVGElement>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			MetadataBase<SVGLength, SVGElement> cx = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_CX_ATTRIBUTE, 
				constants.ellipseCx(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGEllipseElement)element.cast()).getCx().getBaseVal();
					}
				}, 
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> cy = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_CY_ATTRIBUTE, 
				constants.ellipseCy(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGEllipseElement)element.cast()).getCy().getBaseVal();
					}
				}, 
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> rx = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_RX_ATTRIBUTE, 
				constants.ellipseRx(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGEllipseElement)element.cast()).getRx().getBaseVal();
					}
				}, 
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> ry = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_RY_ATTRIBUTE, 
				constants.ellipseRy(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGEllipseElement)element.cast()).getRy().getBaseVal();
					}
				}, 
				EditGeometryCommandFactory.INSTANTIATOR);
			geometricCategory.addMetadata(cx);
			geometricCategory.addMetadata(cy);
			geometricCategory.addMetadata(rx);
			geometricCategory.addMetadata(ry);
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
				constants.ellipse(),
				AbstractImagePrototype.create(AppBundle.INSTANCE.ellipse()),
				categories,
				contextMenuFactories);
		}
		return metaModel;
	}
}
