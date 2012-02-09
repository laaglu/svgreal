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
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.inspector.GenericSectionFactory;
import org.vectomatic.svg.edit.client.model.IPropertyAccessor;
import org.vectomatic.svg.edit.client.model.JSMetadata;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;

/**
 * Moveto path segment model class.
 * @author laaglu
 */
public class SVGMoveSegModel extends SVGSegModel {
	private static MetaModel<OMSVGPathSeg> metaModel;

	public SVGMoveSegModel(SVGSegStore owner, OMSVGPathSegMovetoAbs moveToSeg) {
		super(moveToSeg, owner);
	}
	
	@Override
	public SVGSegModel createNextSegment() {
		int count = owner.getCount();
		OMSVGPathSegMovetoAbs moveToSeg = (OMSVGPathSegMovetoAbs)element;
		float x = moveToSeg.getX();
		float y = moveToSeg.getY();
		OMSVGPathElement path = owner.getPath();
		if (count > 1) {
			SVGSegModel prevSeg = owner.getAt(count - 2);
			float px = prevSeg.getX();
			float py = prevSeg.getY();
			return new SVGMoveSegModel(owner, (OMSVGPathSegMovetoAbs)path.getPathSegList().appendItem(
					path.createSVGPathSegMovetoAbs(
							2 * x - px, 
							2 * y - py)));
		} else {
			return new SVGMoveSegModel(owner, (OMSVGPathSegMovetoAbs)path.getPathSegList().appendItem(
					path.createSVGPathSegMovetoAbs(
							x + SvgrealApp.getApp().getRectangle().width * 0.05f, 
							y)));
		}
	}
	
	@Override
	public SVGSegModel split(int index) {
		assert index > 0;
		SVGSegModel prevSeg = owner.getAt(index - 1);
		float px = prevSeg.getX();
		float py = prevSeg.getY();
		OMSVGPathSegMovetoAbs moveToSeg = (OMSVGPathSegMovetoAbs)element;
		float x = moveToSeg.getX();
		float y = moveToSeg.getY();
		OMSVGPathElement path = owner.getPath();
		return new SVGMoveSegModel(owner, (OMSVGPathSegMovetoAbs)path.getPathSegList().insertItemBefore(
				path.createSVGPathSegMovetoAbs(
						0.5f * (px + x), 
						0.5f * (py + y)), index));
	}

	@Override
	public MetaModel<OMSVGPathSeg> getMetaModel() {
		return SVGMoveSegModel.getMoveModelMetaModel();
	}
	
	public static MetaModel<OMSVGPathSeg> getMoveModelMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<OMSVGPathSeg>();
			final ModelConstants constants = ModelConstants.INSTANCE;
			ModelCategory<OMSVGPathSeg> geometricCategory = new ModelCategory<OMSVGPathSeg>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					GenericSectionFactory.INSTANCE);
			MetadataBase<Float, OMSVGPathSeg> X = new JSMetadata<Float, OMSVGPathSeg>(
					SVGConstants.SVG_X_ATTRIBUTE, 
					constants.segX(),
					null,
					new IPropertyAccessor<Float, OMSVGPathSeg>() {
						@Override
						public Float get(OMSVGPathSeg model) {
							return ((OMSVGPathSegMovetoAbs)model).getX();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegMovetoAbs)model).setX(value);
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
							return ((OMSVGPathSegMovetoAbs)model).getY();
						}
						@Override
						public void set(OMSVGPathSeg model, Float value) {
							((OMSVGPathSegMovetoAbs)model).setY(value);
						}
					},
					null,
					null);
			geometricCategory.addMetadata(X);
			geometricCategory.addMetadata(Y);
			geometricCategory.addMetadata(SVGSegModel.TYPE);
			metaModel.init(
				constants.segMoveTo(),
				AppBundle.INSTANCE.pathMove(),
				Collections.singletonList(geometricCategory),
				null
			);
		}
		return metaModel;
	}

}
