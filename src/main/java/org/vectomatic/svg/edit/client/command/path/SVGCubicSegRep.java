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
package org.vectomatic.svg.edit.client.command.path;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoCubicAbs;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.svg.edit.client.command.path.IPathRepOwner.Mode;

import com.google.gwt.dom.client.Element;

/**
 * Class to represent a path cubic segment 
 * @author laaglu
 */
public class SVGCubicSegRep extends SVGSegRep {
	protected OMSVGPathSegCurvetoCubicAbs cubicToSeg;
	protected OMSVGCircleElement cp1;
	protected OMSVGCircleElement cp2;
	protected OMSVGLineElement tg1;
	protected OMSVGLineElement tg2;

	public SVGCubicSegRep(IPathRepOwner owner, OMSVGPathSegCurvetoCubicAbs cubicToSeg) {
		super(owner);
		this.cubicToSeg = cubicToSeg;
		
		// Create the tangents
		OMSVGDocument document = (OMSVGDocument) owner.getSvg().getOwnerDocument();
		cp1 = document.createSVGCircleElement();
		cp2 = document.createSVGCircleElement();
		tg1 = document.createSVGLineElement();
		tg2 = document.createSVGLineElement();
		tangents.appendChild(tg1);
		tangents.appendChild(tg2);
		tangents.appendChild(cp1);
		tangents.appendChild(cp2);
	}
	
	@Override
	public OMSVGPathSeg getElement() {
		return cubicToSeg;
	}
	
	@Override
	public float getX() {
		return cubicToSeg.getX();
	}
	@Override
	public void setX(float x) {
		cubicToSeg.setX(x);
	}
	@Override
	public float getY() {
		return cubicToSeg.getY();
	}
	@Override
	public void setY(float y) {
		cubicToSeg.setY(y);
	}
	@Override
	public float getX1() {
		return cubicToSeg.getX1();
	}
	@Override
	public float getY1() {
		return cubicToSeg.getY1();
	}
	@Override
	public float getX2() {
		return cubicToSeg.getX2();
	}
	@Override
	public float getY2() {
		return cubicToSeg.getY2();
	}
	@Override
	public Element getCp1() {
		return cp1.getElement();
	}
	@Override
	public void setCp1(OMSVGPoint p, float hs) {
		cubicToSeg.setX1(p.getX());
		cubicToSeg.setY1(p.getY());
		update(hs);
	}
	@Override
	public Element getCp2() {
		return cp2.getElement();
	}
	@Override
	public void setCp2(OMSVGPoint p, float hs) {
		cubicToSeg.setX2(p.getX());
		cubicToSeg.setY2(p.getY());
		update(hs);
	}

	@Override
	public void update(float hs) {
		float x = cubicToSeg.getX();
		float y = cubicToSeg.getY();
		vertex.getX().getBaseVal().setValue(x - hs);
		vertex.getY().getBaseVal().setValue(y - hs);
		vertex.getWidth().getBaseVal().setValue(hs * 2);
		vertex.getHeight().getBaseVal().setValue(hs * 2);
		if (owner.getMode() == Mode.TANGENT) {
			float px = previous != null ? previous.getX() : 0;
			float py = previous != null ? previous.getY() : 0;
			float x1 = cubicToSeg.getX1();
			float y1 = cubicToSeg.getY1();
			float x2 = cubicToSeg.getX2();
			float y2 = cubicToSeg.getY2();
			tg1.getX1().getBaseVal().setValue(px);
			tg1.getY1().getBaseVal().setValue(py);
			tg1.getX2().getBaseVal().setValue(x1);
			tg1.getY2().getBaseVal().setValue(y1);
			
			tg2.getX1().getBaseVal().setValue(x);
			tg2.getY1().getBaseVal().setValue(y);
			tg2.getX2().getBaseVal().setValue(x2);
			tg2.getY2().getBaseVal().setValue(y2);
			
			cp1.getCx().getBaseVal().setValue(x1);
			cp1.getCy().getBaseVal().setValue(y1);
			cp1.getR().getBaseVal().setValue(hs);
	
			cp2.getCx().getBaseVal().setValue(x2);
			cp2.getCy().getBaseVal().setValue(y2);
			cp2.getR().getBaseVal().setValue(hs);
		}
	}

	@Override
	public void updateStart(OMSVGPoint delta, float hs) {
		cubicToSeg.setX1(cubicToSeg.getX1() + delta.getX());
		cubicToSeg.setY1(cubicToSeg.getY1() + delta.getY());
		update(hs);
	}
	
	@Override
	public void updateEnd(OMSVGPoint delta, float hs) {
		cubicToSeg.setX2(cubicToSeg.getX2() + delta.getX());
		cubicToSeg.setY2(cubicToSeg.getY2() + delta.getY());
		cubicToSeg.setX(cubicToSeg.getX() + delta.getX());
		cubicToSeg.setY(cubicToSeg.getY() + delta.getY());
		update(hs);
	}

	@Override
	public void processMouseMove(OMSVGPoint delta, Element target, float hs, boolean isCtrlKeyDown) {
		if (target == null || tg2.getElement() == target) {
			updateEnd(delta, hs);
			if (next != null) {
				next.updateStart(delta, hs);
			}
		} else if (tg1.getElement() == target) {
			updateStart(delta, hs);
			if (previous != null) {
				previous.updateEnd(delta, hs);
			}
		} else if (cp1.getElement() == target) {
			SVGSegRep prevSeg = getPreviousSplineSeg();
			Float angle = null;
			if (isCtrlKeyDown && prevSeg != null) {
				// Compute the angle between the tangent and the updated tangent
				OMSVGPoint v1 = owner.getSvg().createSVGPoint(
						cubicToSeg.getX1() - prevSeg.getX(),
						cubicToSeg.getY1() - prevSeg.getY());
				OMSVGPoint v2 = owner.getSvg().createSVGPoint(
						cubicToSeg.getX1() + delta.getX() - prevSeg.getX(),
						cubicToSeg.getY1() + delta.getY() - prevSeg.getY());
				float d = v1.length() * v2.length();
				if (d != 0) {
					angle = (float)(Math.acos(v1.dotProduct(v2) / d) * 180 / Math.PI);						
					if (v1.crossProduct(v2) < 0) {
						angle = 360 - angle;
					}
				}
			}
			cubicToSeg.setX1(cubicToSeg.getX1() + delta.getX());
			cubicToSeg.setY1(cubicToSeg.getY1() + delta.getY());
			update(hs);
			if (angle != null) {
				// Apply the same rotation to the next spline tangent
				OMSVGMatrix m = owner.getSvg().createSVGMatrix();
				m = m.translate(prevSeg.getX(), prevSeg.getY());
				m = m.rotate(angle);
				m = m.translate(-prevSeg.getX(), -prevSeg.getY());
				OMSVGPoint p0 = owner.getSvg().createSVGPoint(
						prevSeg.getX2(),
						prevSeg.getY2());
				OMSVGPoint p1 = p0.matrixTransform(m).substract(p0);
				prevSeg.processMouseMove(p1, prevSeg.getCp2(), hs, false);
			}
		} else if (cp2.getElement() == target) {
			SVGSegRep nextSeg = getNextSplineSeg();
			Float angle = null;
			if (isCtrlKeyDown && next != null) {
				// Compute the angle between the tangent and the updated tangent
				OMSVGPoint v1 = owner.getSvg().createSVGPoint(
						cubicToSeg.getX2() - cubicToSeg.getX(),
						cubicToSeg.getY2() - cubicToSeg.getY());
				OMSVGPoint v2 = owner.getSvg().createSVGPoint(
						cubicToSeg.getX2() + delta.getX() - cubicToSeg.getX(),
						cubicToSeg.getY2() + delta.getY() - cubicToSeg.getY());
				float d = v1.length() * v2.length();
				if (d != 0) {
					angle = (float)(Math.acos(v1.dotProduct(v2) / d) * 180 / Math.PI);						
					if (v1.crossProduct(v2) < 0) {
						angle = 360 - angle;
					}
				}
			}
			cubicToSeg.setX2(cubicToSeg.getX2() + delta.getX());
			cubicToSeg.setY2(cubicToSeg.getY2() + delta.getY());
			update(hs);
			if (angle != null) {
				// Apply the same rotation to the next spline tangent
				OMSVGMatrix m = owner.getSvg().createSVGMatrix();
				m = m.translate(cubicToSeg.getX(), cubicToSeg.getY());
				m = m.rotate(angle);
				m = m.translate(-cubicToSeg.getX(), -cubicToSeg.getY());
				OMSVGPoint p0 = owner.getSvg().createSVGPoint(
						nextSeg.getX1(),
						nextSeg.getY1());
				OMSVGPoint p1 = p0.matrixTransform(m).substract(p0);
				nextSeg.processMouseMove(p1, nextSeg.getCp1(), hs, false);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("C ");
		builder.append(cubicToSeg.getX1());
		builder.append(",");
		builder.append(cubicToSeg.getY1());
		builder.append(" ");
		builder.append(cubicToSeg.getX2());
		builder.append(",");
		builder.append(cubicToSeg.getY2());
		builder.append(" ");
		builder.append(cubicToSeg.getX());
		builder.append(",");
		builder.append(cubicToSeg.getY());
		return builder.toString();
	}
}
