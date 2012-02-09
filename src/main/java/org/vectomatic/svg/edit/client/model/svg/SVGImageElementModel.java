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
import org.vectomatic.dom.svg.impl.SVGImageElement;
import org.vectomatic.dom.svg.impl.SVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.EditGeometryCommandFactory;
import org.vectomatic.svg.edit.client.command.EditImageHrefCommand;
import org.vectomatic.svg.edit.client.command.EditImageHrefCommandFactory;
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
 * Image model class.
 * @author laaglu
 */
public class SVGImageElementModel extends SVGNamedElementModel {
	private static MetaModel<SVGElement> metaModel;
	/**
	 * Name of the resource used to initialize this image (url
	 * or file name).
	 */
	protected String resourceName;

	public SVGImageElementModel(SVGModel owner, SVGImageElement element, SVGImageElement twin) {
		super(owner, element, twin);
	}
	
	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getImageElementMetaModel();
	}
	
	/**
	 * Returns the name of the resource used to initialize this image (url
	 * or file name).
	 * @return the resource name
	 */
	public String getResourceName() {
		return resourceName;
	}
	
	/**
	 * Sets the name of the resource used to initialize this image (url
	 * or file name).
	 * @param resourceName the resource name
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	/**
	 * Returns true if the specified url is an image data url, false otherwise
	 * @param url the url to test
	 * @return true if the specified url is a data url
	 */
	public static boolean isDataUrl(String url) {
		return url != null && url.startsWith("data:image");
	}

	public static MetaModel<SVGElement> getImageElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();
			ModelConstants constants = ModelConstants.INSTANCE;
			metaModel = new MetaModel<SVGElement>();

			ModelCategory<SVGElement> geometricCategory = new ModelCategory<SVGElement>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			MetadataBase<SVGLength, SVGElement> x = new JSMetadata<SVGLength, SVGElement>(
					SVGConstants.SVG_X_ATTRIBUTE, 
					constants.rectX(),
					FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
					new SVGLengthAccessor() {
						@Override
						public OMSVGLength getLength(SVGElement element) {
							return ((SVGRectElement)element.cast()).getX().getBaseVal();
						}
					},
					EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> y = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_Y_ATTRIBUTE, 
				constants.rectY(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGRectElement)element.cast()).getY().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> width = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_WIDTH_ATTRIBUTE, 
				constants.rectWidth(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
					@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGRectElement)element.cast()).getWidth().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<SVGLength, SVGElement> height = new JSMetadata<SVGLength, SVGElement>(
				SVGConstants.SVG_HEIGHT_ATTRIBUTE, 
				constants.rectHeight(),
				FormPanelUtils.SVGLENGTH_FIELD_FACTORY,
				new SVGLengthAccessor() {
				@Override
					public OMSVGLength getLength(SVGElement element) {
						return ((SVGRectElement)element.cast()).getHeight().getBaseVal();
					}
				},
				EditGeometryCommandFactory.INSTANTIATOR);
			MetadataBase<String, SVGElement> href = new JSMetadata<String, SVGElement>(
				SVGConstants.XLINK_HREF_ATTRIBUTE,
				constants.imageHref(),
				FormPanelUtils.IMAGE_HREF_FIELD_FACTORY,
				new IPropertyAccessor<String, SVGElement>() {

					@Override
					public String get(SVGElement element) {
						return element.<SVGImageElement>cast().getHref().getBaseVal();
					}

					@Override
					public void set(SVGElement element, String value) {
						element.<SVGImageElement>cast().getHref().setBaseVal(value);
					}
				},
				EditImageHrefCommandFactory.INSTANTIATOR);
			geometricCategory.addMetadata(x);
			geometricCategory.addMetadata(y);
			geometricCategory.addMetadata(width);
			geometricCategory.addMetadata(height);
			geometricCategory.addMetadata(href);
			IFactoryInstantiator<?>[][] contextMenuFactories = new IFactoryInstantiator<?>[][] {
				{
					EditGeometryCommandFactory.INSTANTIATOR,
				}
			};
			List<ModelCategory<SVGElement>> categories = new ArrayList<ModelCategory<SVGElement>>();
			categories.add(SVGNamedElementModel.getGlobalCategory());
			categories.add(SVGElementModel.getDisplayCategory());
			categories.add(geometricCategory);
			categories.add(SVGElementModel.getTransformCategory());
			metaModel.init(
				constants.image(),
				AbstractImagePrototype.create(AppBundle.INSTANCE.image()),
				categories,
				contextMenuFactories);
			
		}
		return metaModel;
	}
}
