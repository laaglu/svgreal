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

import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.EditGeometryCommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.IPropertyAccessor;
import org.vectomatic.svg.edit.client.model.JSMetadata;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Viewbox model class.
 * @author laaglu
 */
public class SVGViewBoxElementModel extends SVGElementModel {
	private static MetaModel<SVGElement> metaModel;

	public SVGViewBoxElementModel(SVGModel owner, SVGElement element, SVGElement twin) {
		super(owner, element, twin);
	}

	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getViewBoxElementMetaModel();
	}
	
	@Override
	public <X> X get(String property) {
		if (SVGConstants.SVG_TITLE_TAG.equals(property)) {
			return (X)ModelConstants.INSTANCE.viewBox();
		}
		return super.get(property);
	}

	@Override
	public <X> X set(String property, X value) {
		value = super.set(property, value);
		owner.updateTransform();
		return value;
	}

	public static MetaModel<SVGElement> getViewBoxElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			ModelConstants constants = ModelConstants.INSTANCE;

			ModelCategory<SVGElement> geometricCategory = new ModelCategory<SVGElement>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			MetadataBase<Float, SVGElement> x = new JSMetadata<Float, SVGElement>(
				SVGConstants.SVG_X_ATTRIBUTE, 
				constants.rectX(),
				FormPanelUtils.NUMBER_FIELD_FACTORY,
				new IPropertyAccessor<Float, SVGElement>() {
					@Override
					public Float get(SVGElement element) {
						return ((SVGRectElement)element.cast()).getX().getBaseVal().getValue();
					}

					@Override
					public void set(SVGElement element, Float value) {
						((SVGRectElement)element.cast()).getX().getBaseVal().setValue(value);
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<Float, SVGElement> y = new JSMetadata<Float, SVGElement>(
				SVGConstants.SVG_Y_ATTRIBUTE, 
				constants.rectY(),
				FormPanelUtils.NUMBER_FIELD_FACTORY,
				new IPropertyAccessor<Float, SVGElement>() {
					@Override
					public Float get(SVGElement element) {
						return ((SVGRectElement)element.cast()).getY().getBaseVal().getValue();
					}

					@Override
					public void set(SVGElement element, Float value) {
						((SVGRectElement)element.cast()).getY().getBaseVal().setValue(value);
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<Float, SVGElement> width = new JSMetadata<Float, SVGElement>(
				SVGConstants.SVG_WIDTH_ATTRIBUTE, 
				constants.rectWidth(),
				FormPanelUtils.NUMBER_FIELD_FACTORY,
				new IPropertyAccessor<Float, SVGElement>() {
					@Override
					public Float get(SVGElement element) {
						return ((SVGRectElement)element.cast()).getWidth().getBaseVal().getValue();
					}

					@Override
					public void set(SVGElement element, Float value) {
						((SVGRectElement)element.cast()).getWidth().getBaseVal().setValue(value);
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<Float, SVGElement> height = new JSMetadata<Float, SVGElement>(
				SVGConstants.SVG_HEIGHT_ATTRIBUTE, 
				constants.rectHeight(),
				FormPanelUtils.NUMBER_FIELD_FACTORY,
				new IPropertyAccessor<Float, SVGElement>() {
					@Override
					public Float get(SVGElement element) {
						return ((SVGRectElement)element.cast()).getHeight().getBaseVal().getValue();
					}

					@Override
					public void set(SVGElement element, Float value) {
						((SVGRectElement)element.cast()).getHeight().getBaseVal().setValue(value);
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			geometricCategory.addMetadata(x);
			geometricCategory.addMetadata(y);
			geometricCategory.addMetadata(width);
			geometricCategory.addMetadata(height);
			IFactoryInstantiator<?>[][] contextMenuFactories = new IFactoryInstantiator<?>[][] {
				{
					EditGeometryCommandFactory.INSTANTIATOR,
				}
			};
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(geometricCategory);
			metaModel.init(
				constants.viewBox(),
				AbstractImagePrototype.create(AppBundle.INSTANCE.viewBox()),
				categories,
				contextMenuFactories);
			
		}
		return metaModel;
	}
}
