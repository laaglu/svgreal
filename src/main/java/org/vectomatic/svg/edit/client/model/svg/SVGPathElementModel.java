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

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegArcAbs;
import org.vectomatic.dom.svg.OMSVGPathSegArcRel;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicRel;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicSmoothAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicSmoothRel;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticRel;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticSmoothAbs;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticSmoothRel;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoAbs;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoHorizontalAbs;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoHorizontalRel;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoRel;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoVerticalAbs;
import org.vectomatic.dom.svg.OMSVGPathSegLinetoVerticalRel;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoRel;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGPathElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.command.EditGeometryCommandFactory;
import org.vectomatic.svg.edit.client.command.EditTransformCommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.inspector.PathSectionFactory;
import org.vectomatic.svg.edit.client.model.AttrMetadata;
import org.vectomatic.svg.edit.client.model.IMetadata;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.path.SVGSegStore;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Path model class.
 * @author laaglu
 */
public class SVGPathElementModel extends SVGStyledElementModel {
	private static MetaModel<SVGElement> metaModel;
	private SVGSegStore store;

	public SVGPathElementModel(SVGModel owner, SVGPathElement element, SVGPathElement twin) {
		super(owner, element, twin);
	}

	@Override
	public MetaModel<SVGElement> getMetaModel() {
		return getPathElementMetaModel();
	}
	
	@Override
	public <X> X set(String property, X value) {
		IMetadata propertyDefinition = getMetaModel().getMetadata(property);
		assert propertyDefinition != null;
		X oldValue = (X)propertyDefinition.set(element, value);
		propertyDefinition.set(twin, value);
//		GWT.log("set(" + property + ") = " + oldValue + ", " + value);
		if (SVGConstants.SVG_D_ATTRIBUTE.equals(property)) {
			sync();
		}
		notifyPropertyChanged(propertyDefinition.getName(), value, oldValue);
		return oldValue;
	}

	
	public static MetaModel<SVGElement> getPathElementMetaModel() {
		if (metaModel == null) {
			metaModel = new MetaModel<SVGElement>();

			ModelConstants constants = ModelConstants.INSTANCE;

			ModelCategory<SVGElement> geometricCategory = new ModelCategory<SVGElement>(
					ModelCategory.GEOMETRY, 
					constants.geometry(), 
					PathSectionFactory.INSTANCE);
			MetadataBase<String, SVGElement> d = new AttrMetadata(
					SVGConstants.SVG_D_ATTRIBUTE, 
					constants.d(),
					null,
					SVGConstants.SVG_D_ATTRIBUTE, 
					EditGeometryCommandFactory.INSTANTIATOR);
			geometricCategory.addMetadata(d);
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
				}));
			categories.add(SVGElementModel.getTransformCategory());
			metaModel.init(
				constants.path(),
				AbstractImagePrototype.create(AppBundle.INSTANCE.path()), 
				categories,
				contextMenuFactories);
		}
		return metaModel;
	}

	public SVGSegStore getSegStore() {
		if (store == null) {
			normalize();
			store = new SVGSegStore(this);
			store.update((OMSVGPathElement) getElementWrapper());
		}
		return store;
	}
	
	public void sync() {
		getSegStore().update((OMSVGPathElement) getElementWrapper());
	}
	
	/**
	 * Normalizes the path definition so that only
	 * M, L, Q, C, A, and Z segment types remain. Contrary to 
	 * OMSVGPathElement.getNormalizedPathSegList, it does
	 * not simplify the geometry (Q and A are preserved), and
	 * it is implemented.
	 * @param path The path to normalize
	 */
	public void normalize() {
		OMSVGPathElement path = (OMSVGPathElement) getElementWrapper();
		OMSVGPathSegList segs = path.getPathSegList();
		float x = 0f, y = 0f, prevcpx = 0, prevcpy = 0, x1, y1;
		boolean prevIsQuadratic = false;
		boolean prevIsCubic = false;
		for (int i = 0, size = segs.getNumberOfItems(); i < size; i++) {
			OMSVGPathSeg seg = segs.getItem(i);
			switch(seg.getPathSegType()) {
			  	case OMSVGPathSeg.PATHSEG_UNKNOWN:
				  	{
	
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CLOSEPATH:
				  	{
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_MOVETO_ABS:
				  	{
				  		OMSVGPathSegMovetoAbs moveToAbs = (OMSVGPathSegMovetoAbs)seg;
						prevcpx = x = moveToAbs.getX();
						prevcpy = y = moveToAbs.getY();
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_MOVETO_REL:
				  	{
						OMSVGPathSegMovetoRel moveToRel = (OMSVGPathSegMovetoRel)seg;
						x += moveToRel.getX();
						y += moveToRel.getY();
						prevcpx = x;
						prevcpy = y;
						segs.replaceItem(path.createSVGPathSegMovetoAbs(x, y), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_ABS:
				  	{
						OMSVGPathSegLinetoAbs lineToAbs = (OMSVGPathSegLinetoAbs)seg;
						prevcpx = x = lineToAbs.getX();
						prevcpy = y = lineToAbs.getY();
						prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_REL:
				  	{
						OMSVGPathSegLinetoRel lineToRel = (OMSVGPathSegLinetoRel)seg;
						x += lineToRel.getX();
						y += lineToRel.getY();
						prevcpx = x;
						prevcpy = y;
						segs.replaceItem(path.createSVGPathSegLinetoAbs(x, y), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS:
				  	{
						OMSVGPathSegCurvetoCubicAbs curveToCubicAbs = (OMSVGPathSegCurvetoCubicAbs)seg;
						x = curveToCubicAbs.getX();
						y = curveToCubicAbs.getY();
						prevcpx = curveToCubicAbs.getX2();
						prevcpy = curveToCubicAbs.getY2();
						prevIsCubic = true;
						prevIsQuadratic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_REL:
				  	{
						OMSVGPathSegCurvetoCubicRel curveToCubicRel = (OMSVGPathSegCurvetoCubicRel)seg;
						prevcpx = curveToCubicRel.getX2() + x;
						prevcpy = curveToCubicRel.getY2() + y;
						segs.replaceItem(path.createSVGPathSegCurvetoCubicAbs(
								curveToCubicRel.getX() + x, 
								curveToCubicRel.getY() + y, 
								curveToCubicRel.getX1() + x, 
								curveToCubicRel.getY1() + y, 
								prevcpx, 
								prevcpy), i);
						x += curveToCubicRel.getX();
						y += curveToCubicRel.getY();
						prevIsCubic = true;
						prevIsQuadratic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
				  	{
						OMSVGPathSegCurvetoQuadraticAbs curveToQuadraticAbs = (OMSVGPathSegCurvetoQuadraticAbs)seg;
						x = curveToQuadraticAbs.getX();
						y = curveToQuadraticAbs.getY();
						prevcpx = curveToQuadraticAbs.getX1();
						prevcpy = curveToQuadraticAbs.getY1();
						prevIsCubic = false;
						prevIsQuadratic = true;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL:
				  	{
						OMSVGPathSegCurvetoQuadraticRel curveToQuadraticRel = (OMSVGPathSegCurvetoQuadraticRel)seg;
						prevcpx = curveToQuadraticRel.getX1() + x;
						prevcpy = curveToQuadraticRel.getY1() + y;
						x += curveToQuadraticRel.getX();
						y += curveToQuadraticRel.getY();
						segs.replaceItem(path.createSVGPathSegCurvetoQuadraticAbs(x, y, prevcpx, prevcpy), i);
						prevIsCubic = false;
						prevIsQuadratic = true;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_ARC_ABS:
				  	{
						OMSVGPathSegArcAbs arcAbs = (OMSVGPathSegArcAbs)seg;
						prevcpx = x = arcAbs.getX();
						prevcpy = y = arcAbs.getY();
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_ARC_REL:
				  	{
						OMSVGPathSegArcRel arcRel = (OMSVGPathSegArcRel)seg;
						x += arcRel.getX();
						y += arcRel.getY();
						prevcpx = x;
						prevcpy = y;
						segs.replaceItem(path.createSVGPathSegArcAbs(x, y, arcRel.getR1(), arcRel.getR2(), arcRel.getAngle(), arcRel.getLargeArcFlag(), arcRel.getSweepFlag()), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS:
				  	{
						OMSVGPathSegLinetoHorizontalAbs lineToHorizAbs = (OMSVGPathSegLinetoHorizontalAbs)seg;
						prevcpx = x = lineToHorizAbs.getX();
						segs.replaceItem(path.createSVGPathSegLinetoAbs(x, y), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL:
				  	{
						OMSVGPathSegLinetoHorizontalRel lineToHorizRel = (OMSVGPathSegLinetoHorizontalRel)seg;
						x += lineToHorizRel.getX();
						prevcpx = x;
						segs.replaceItem(path.createSVGPathSegLinetoAbs(x, y), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS:
				  	{
						OMSVGPathSegLinetoVerticalAbs lineToVertAbs = (OMSVGPathSegLinetoVerticalAbs)seg;
						prevcpy = y = lineToVertAbs.getY();
						segs.replaceItem(path.createSVGPathSegLinetoAbs(x, y), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_LINETO_VERTICAL_REL:
				  	{
						OMSVGPathSegLinetoVerticalRel lineToVertRel = (OMSVGPathSegLinetoVerticalRel)seg;
						y += lineToVertRel.getY();
						prevcpy = y;
						segs.replaceItem(path.createSVGPathSegLinetoAbs(x, y), i);
				  		prevIsQuadratic = prevIsCubic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS:
				  	{
						OMSVGPathSegCurvetoCubicSmoothAbs curveToSmoothAbs = (OMSVGPathSegCurvetoCubicSmoothAbs)seg;
						if (prevIsCubic) {
							x1 = 2 * x - prevcpx;
							y1 = 2 * y - prevcpy;
						} else {
							x1 = x;
							y1 = y;
						}
						x = curveToSmoothAbs.getX();
						y = curveToSmoothAbs.getY();
						prevcpx = curveToSmoothAbs.getX2();
						prevcpy = curveToSmoothAbs.getY2();
						segs.replaceItem(path.createSVGPathSegCurvetoCubicAbs(
								x, 
								y, 
								x1, 
								y1, 
								prevcpx, 
								prevcpy), i);
						prevIsCubic = true;
						prevIsQuadratic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL:
				  	{
						OMSVGPathSegCurvetoCubicSmoothRel curveToSmoothRel = (OMSVGPathSegCurvetoCubicSmoothRel)seg;
						if (prevIsCubic) {
							x1 = 2 * x - prevcpx;
							y1 = 2 * y - prevcpy;
						} else {
							x1 = x;
							y1 = y;
						}
						prevcpx = (x + curveToSmoothRel.getX2());
						prevcpy = (y + curveToSmoothRel.getY2());
						x += curveToSmoothRel.getX();
						y += curveToSmoothRel.getY();
						segs.replaceItem(path.createSVGPathSegCurvetoCubicAbs(
								x, 
								y, 
								x1, 
								y1, 
								prevcpx, 
								prevcpy), i);
						prevIsCubic = true;
						prevIsQuadratic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS:
				  	{
						OMSVGPathSegCurvetoQuadraticSmoothAbs curveToQuadraticSmoothAbs = (OMSVGPathSegCurvetoQuadraticSmoothAbs)seg;
						if (prevIsQuadratic) {
							x1 = 2 * x - prevcpx;
							y1 = 2 * y - prevcpy;
						} else {
							x1 = x;
							y1 = y;
						}
						x = curveToQuadraticSmoothAbs.getX();
						y = curveToQuadraticSmoothAbs.getY();
						segs.replaceItem(path.createSVGPathSegCurvetoQuadraticAbs(x, y, x1, y1), i);
						prevIsCubic = true;
						prevIsQuadratic = false;
				  	}
			  		break;
			    case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL:
				  	{
						OMSVGPathSegCurvetoQuadraticSmoothRel curveToQuadraticSmoothRel = (OMSVGPathSegCurvetoQuadraticSmoothRel)seg;				  		
						if (prevIsQuadratic) {
							x1 = 2 * x - prevcpx;
							y1 = 2 * y - prevcpy;
						} else {
							x1 = x;
							y1 = y;
						}
						x += curveToQuadraticSmoothRel.getX();
						y += curveToQuadraticSmoothRel.getY();
						segs.replaceItem(path.createSVGPathSegCurvetoQuadraticAbs(x, y, x1, y1), i);
						prevIsCubic = true;
						prevIsQuadratic = false;
				  	}
			  		break;
			}
		}
	}
}
