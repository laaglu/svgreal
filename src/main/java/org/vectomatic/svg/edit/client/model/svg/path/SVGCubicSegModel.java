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
package org.vectomatic.svg.edit.client.model.svg.path;

import java.util.Collections;

import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicAbs;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.gxt.binding.FormPanelUtils;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.IPropertyAccessor;
import org.vectomatic.svg.edit.client.model.JSMetadata;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;

/**
 * Cubic spline segment model class.
 * @author laaglu
 */
public class SVGCubicSegModel extends SVGSegModel {
	private static MetaModel<OMSVGPathSeg> metaModel;

	public SVGCubicSegModel(SVGSegStore owner, OMSVGPathSegCurvetoCubicAbs cubicToSeg) {
		super(cubicToSeg, owner);
	}
	
	@Override
	public SVGSegModel createNextSegment() {
		int count = owner.getCount();
		assert count > 1;
		SVGSegModel prevSeg = owner.getAt(count - 2);
		float px = prevSeg.getX();
		float py = prevSeg.getY();
		OMSVGPathSegCurvetoCubicAbs cubicToSeg = (OMSVGPathSegCurvetoCubicAbs)element;
		float x = cubicToSeg.getX();
		float y = cubicToSeg.getY();
		float x1 = cubicToSeg.getX1();
		float y1 = cubicToSeg.getY1();
		float x2 = cubicToSeg.getX2();
		float y2 = cubicToSeg.getY2();
		OMSVGPathElement path = owner.getPath();
		return new SVGCubicSegModel(owner, (OMSVGPathSegCurvetoCubicAbs)path.getPathSegList().appendItem(
				path.createSVGPathSegCurvetoCubicAbs(
						2 * x - px, 
						2 * y - py, 
						2 * x - x2, 
						2 * y - y2, 
						2 * x - x1, 
						2 * y - y1)));
	}
	
	@Override
	public SVGSegModel split(int index) {
		assert index > 0;
		SVGSegModel prevSeg = owner.getAt(index - 1);
		float px = prevSeg.getX();
		float py = prevSeg.getY();
		OMSVGPathSegCurvetoCubicAbs cubicToSeg = (OMSVGPathSegCurvetoCubicAbs)element;
		float x = cubicToSeg.getX();
		float y = cubicToSeg.getY();
		float x1 = cubicToSeg.getX1();
		float y1 = cubicToSeg.getY1();
		float x2 = cubicToSeg.getX2();
		float y2 = cubicToSeg.getY2();
		
		// Use Casteljau's algorithm
		float b10x = 0.5f * (px + x1);
		float b10y = 0.5f * (py + y1);
		float b12x = 0.5f * (x + x2);
		float b12y = 0.5f * (y + y2);
		float b11x = 0.5f * (x1 + x2);
		float b11y = 0.5f * (y1 + y2);
		
		float b20x = 0.5f * (b10x + b11x);
		float b20y = 0.5f * (b10y + b11y);
		float b21x = 0.5f * (b12x + b11x);
		float b21y = 0.5f * (b12y + b11y);
		
		float b30x = 0.5f * (b20x + b21x);
		float b30y = 0.5f * (b20y + b21y);
		
		cubicToSeg.setX1(b21x);
		cubicToSeg.setY1(b21y);
		cubicToSeg.setX2(b12x);
		cubicToSeg.setY2(b12y);
		OMSVGPathElement path = owner.getPath();
		return new SVGCubicSegModel(owner, (OMSVGPathSegCurvetoCubicAbs)path.getPathSegList().insertItemBefore(
				path.createSVGPathSegCurvetoCubicAbs(
						b30x, 
						b30y,
						b10x,
						b10y,
						b20x,
						b20y), index));
	}
		
	@Override
	public MetaModel<OMSVGPathSeg> getMetaModel() {
		return SVGCubicSegModel.getCubicModelMetaModel();
	}
	
	public static MetaModel<OMSVGPathSeg> getCubicModelMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<OMSVGPathSeg>();
			final ModelConstants constants = ModelConstants.INSTANCE;
			MetadataBase<Float, OMSVGPathSeg> X = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_X_ATTRIBUTE, 
					constants.segX(),
					null,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegCurvetoCubicAbs)model).getX();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegCurvetoCubicAbs)model).setX(value);
						}
					},
					null,
					null);
			MetadataBase<Float, OMSVGPathSeg> Y = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_Y_ATTRIBUTE, 
					constants.segY(),
					null,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegCurvetoCubicAbs)model).getY();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegCurvetoCubicAbs)model).setY(value);
						}
					},
					null,
					null);
			MetadataBase<Float, OMSVGPathSeg> X1 = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_X1_ATTRIBUTE, 
					constants.segX1(),
					FormPanelUtils.NUMBER_FIELD_FACTORY,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegCurvetoCubicAbs)model).getX1();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegCurvetoCubicAbs)model).setX1(value);
						}
					},
					null,
					null);
			MetadataBase<Float, OMSVGPathSeg> Y1 = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_Y1_ATTRIBUTE, 
					constants.segY1(),
					FormPanelUtils.NUMBER_FIELD_FACTORY,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegCurvetoCubicAbs)model).getY1();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegCurvetoCubicAbs)model).setY1(value);
						}
					},
					null,
					null);
			MetadataBase<Float, OMSVGPathSeg> X2 = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_X2_ATTRIBUTE, 
					constants.segX2(),
					FormPanelUtils.NUMBER_FIELD_FACTORY,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegCurvetoCubicAbs)model).getX2();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegCurvetoCubicAbs)model).setX2(value);
						}
					},
					null,
					null);
			MetadataBase<Float, OMSVGPathSeg> Y2 = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_Y2_ATTRIBUTE, 
					constants.segY2(),
					FormPanelUtils.NUMBER_FIELD_FACTORY,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegCurvetoCubicAbs)model).getY2();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegCurvetoCubicAbs)model).setY2(value);
						}
					},
					null,
					null);
			ModelCategory<OMSVGPathSeg> geometricCategory = new ModelCategory<OMSVGPathSeg>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			geometricCategory.addMetadata(X);
			geometricCategory.addMetadata(Y);
			geometricCategory.addMetadata(SVGSegModel.TYPE);
			geometricCategory.addMetadata(X1);
			geometricCategory.addMetadata(Y1);
			geometricCategory.addMetadata(X2);
			geometricCategory.addMetadata(Y2);
			metaModel.init(
					constants.segCubicTo(),
					AppBundle.INSTANCE.pathCubic(),
					Collections.singletonList(geometricCategory),
					null
			);
		}
		return metaModel;
	}
}
