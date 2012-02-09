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

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.OMSVGTransformList;
import org.vectomatic.dom.svg.itf.ISVGTransformable;

import com.google.gwt.core.client.GWT;

/**
 * Class to decompose the transform attribute into a matrix
 * product of the form: t.r.s.tinv
 * @author laaglu
 */
public class Transformation {
	/**
	 * The transformable to which this transformation applies
	 */
	private ISVGTransformable transformable;
	/**
	 * The manipulator scale transform
	 */
	public OMSVGTransform getS() {
		return transformable.getTransform().getBaseVal().getItem(2);
	}
	/**
	 * The manipulator rotate transform
	 */
	public OMSVGTransform getR() {
		return transformable.getTransform().getBaseVal().getItem(1);
	}
	/**
	 * The manipulator translate transform
	 */
	public OMSVGTransform getT() {
		return transformable.getTransform().getBaseVal().getItem(0);
	}
	/**
	 * The manipulator translate inverse transform
	 */
	public OMSVGTransform getTinv() {
		return transformable.getTransform().getBaseVal().getItem(3);
	}
	
	public static Transformation decompose(ISVGTransformable transformable) {
		OMSVGTransformList transforms = transformable.getTransform().getBaseVal();
		OMSVGSVGElement svg = ((OMSVGElement)transformable).getOwnerSVGElement();
		OMSVGRect bbox = transformable.getBBox();
		OMSVGTransform transform = null;
		if (transforms.getNumberOfItems() == 0) {
			transform = transforms.appendItem(svg.createSVGTransform());
		} else {
			transform = transforms.consolidate();
		}
		OMSVGMatrix m = transform.getMatrix();
		float sx = (float)Math.sqrt(m.getA() * m.getA() + m.getB() * m.getB());
		float sy = (float)Math.sqrt(m.getC() * m.getC() + m.getD() * m.getD());
		float rrad = (float)(Math.acos(m.getA() / sx));
		if (m.getB() < 0f) {
			rrad = 2f * (float)Math.PI - rrad;
		}
		if (m.getA() * m.getD() < 0f) {
			sy = -sy;
		}
		float cos = (float)Math.cos(rrad);
		float sin = (float)Math.sin(rrad);
		float cx = bbox.getCenterX();
		float cy = bbox.getCenterY();
		GWT.log("t1x=" + cx + " t1y=" + cy);
		float tx = m.getE() + sx * cx * cos - sy * cy * sin;
		float ty = m.getF() + sx * cx * sin + sy * cy * cos;

		Transformation decomposition = new Transformation(transformable);
		decomposition.getS().setScale(sx, sy);
		decomposition.getR().setRotate((float)(rrad * 180f / Math.PI), 0, 0);
		decomposition.getT().setTranslate(tx, ty);
		decomposition.getTinv().setTranslate(-cx, -cy);
		
		return decomposition;		
	}

	private Transformation(ISVGTransformable transformable) {
		this.transformable = transformable;
		OMSVGTransformList xforms = transformable.getTransform().getBaseVal();
		xforms.clear();
		OMSVGSVGElement svg = ((OMSVGElement)transformable).getOwnerSVGElement();
		for (int i = 0; i < 4; i++) {
			xforms.appendItem(svg.createSVGTransform());
		}
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("t=");
		builder.append(getT().getDescription());
		builder.append(";r=");
		builder.append(getR().getDescription());
		builder.append(";s=");
		builder.append(getS().getDescription());
		builder.append(";tinv=");
		builder.append(getTinv().getDescription());
		return builder.toString();
	}
}