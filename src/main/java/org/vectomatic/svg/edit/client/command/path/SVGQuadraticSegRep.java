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
package org.vectomatic.svg.edit.client.command.path;

import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegCurvetoQuadraticAbs;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.svg.edit.client.command.path.IPathRepOwner.Mode;

import com.google.gwt.dom.client.Element;

/**
 * Class to represent a path quadratic segment 
 * @author laaglu
 */
public class SVGQuadraticSegRep extends SVGSegRep {
	protected OMSVGPathSegCurvetoQuadraticAbs quadraticToSeg;
	protected OMSVGCircleElement cp;
	protected OMSVGLineElement tg1;
	protected OMSVGLineElement tg2;

	public SVGQuadraticSegRep(IPathRepOwner owner, OMSVGPathSegCurvetoQuadraticAbs quadraticToSeg) {
		super(owner);
		this.quadraticToSeg = quadraticToSeg;
		
		// Create the controls
		OMSVGDocument document = (OMSVGDocument) owner.getSvg().getOwnerDocument();
		cp = document.createSVGCircleElement();
		tg1 = document.createSVGLineElement();
		tg2 = document.createSVGLineElement();
		tangents.appendChild(tg1);
		tangents.appendChild(tg2);
		tangents.appendChild(cp);
	}

	@Override
	public OMSVGPathSeg getElement() {
		return quadraticToSeg;
	}

	@Override
	public float getX() {
		return quadraticToSeg.getX();
	}
	@Override
	public void setX(float x) {
		quadraticToSeg.setX(x);
	}
	@Override
	public float getY() {
		return quadraticToSeg.getY();
	}
	@Override
	public void setY(float y) {
		quadraticToSeg.setY(y);
	}
	@Override
	public float getX1() {
		return quadraticToSeg.getX1();
	}
	@Override
	public float getY1() {
		return quadraticToSeg.getY1();
	}
	@Override
	public float getX2() {
		return getX1();
	}
	@Override
	public float getY2() {
		return getY1();
	}
	@Override
	public Element getCp1() {
		return cp.getElement();
	}
	@Override
	public void setCp1(OMSVGPoint p, float hs) {
		quadraticToSeg.setX1(p.getX());
		quadraticToSeg.setY1(p.getY());
		update(hs);
	}
	@Override
	public Element getCp2() {
		return getCp1();
	}
	@Override
	public void setCp2(OMSVGPoint p, float hs) {
		setCp1(p, hs);
	}

	@Override
	public void update(float hs) {
		float x = quadraticToSeg.getX();
		float y = quadraticToSeg.getY();
		vertex.getX().getBaseVal().setValue(x - hs);
		vertex.getY().getBaseVal().setValue(y - hs);
		vertex.getWidth().getBaseVal().setValue(hs * 2);
		vertex.getHeight().getBaseVal().setValue(hs * 2);
		if (owner.getMode() == Mode.TANGENT) {
			float px = previous != null ? previous.getX() : 0;
			float py = previous != null ? previous.getY() : 0;
			float x1 = quadraticToSeg.getX1();
			float y1 = quadraticToSeg.getY1();
			
			tg1.getX1().getBaseVal().setValue(px);
			tg1.getY1().getBaseVal().setValue(py);
			tg1.getX2().getBaseVal().setValue(x1);
			tg1.getY2().getBaseVal().setValue(y1);
			
			tg2.getX1().getBaseVal().setValue(x);
			tg2.getY1().getBaseVal().setValue(y);
			tg2.getX2().getBaseVal().setValue(x1);
			tg2.getY2().getBaseVal().setValue(y1);
			
			cp.getCx().getBaseVal().setValue(x1);
			cp.getCy().getBaseVal().setValue(y1);
			cp.getR().getBaseVal().setValue(hs);
		}
	}

	@Override
	public void updateStart(OMSVGPoint delta, float hs) {
		quadraticToSeg.setX1(quadraticToSeg.getX1() + delta.getX());
		quadraticToSeg.setY1(quadraticToSeg.getY1() + delta.getY());
		update(hs);
	}
	
	@Override
	public void updateEnd(OMSVGPoint delta, float hs) {
		quadraticToSeg.setX(quadraticToSeg.getX() + delta.getX());
		quadraticToSeg.setY(quadraticToSeg.getY() + delta.getY());
		quadraticToSeg.setX1(quadraticToSeg.getX1() + delta.getX());
		quadraticToSeg.setY1(quadraticToSeg.getY1() + delta.getY());
		update(hs);
	}

	@Override
	public void processMouseMove(OMSVGPoint delta, Element target, float hs, boolean isCtrlKeyDown) {
		if (target == null) {
			updateEnd(delta, hs);
			if (next != null) {
				next.updateStart(delta, hs);
			}
		} else if (tg2.getElement() == target) {
			updateEnd(delta, hs);
			if (next != null) {
				next.updateStart(delta, hs);
			}
		} else if (tg1.getElement() == target) {
			updateStart(delta, hs);
			if (previous != null) {
				previous.updateEnd(delta, hs);
			}
		} else if (cp.getElement() == target) {
			SVGSegRep prevSeg = getPreviousSplineSeg();
			SVGSegRep nextSeg = getNextSplineSeg();
			Float anglePrev = null;
			Float angleNext = null;
			if (isCtrlKeyDown) {
				if (prevSeg != null) {
					// Compute the angle between the tangent and the updated tangent
					OMSVGPoint v1 = owner.getSvg().createSVGPoint(
							quadraticToSeg.getX1() - prevSeg.getX(),
							quadraticToSeg.getY1() - prevSeg.getY());
					OMSVGPoint v2 = owner.getSvg().createSVGPoint(
							quadraticToSeg.getX1() + delta.getX() - prevSeg.getX(),
							quadraticToSeg.getY1() + delta.getY() - prevSeg.getY());
					float d = v1.length() * v2.length();
					if (d != 0) {
						anglePrev = (float)(Math.acos(v1.dotProduct(v2) / d) * 180 / Math.PI);						
						if (v1.crossProduct(v2) < 0) {
							anglePrev = 360 - anglePrev;
						}
					}
				}
				if (nextSeg != null) {
					// Compute the angle between the tangent and the updated tangent
					OMSVGPoint v1 = owner.getSvg().createSVGPoint(
							quadraticToSeg.getX1() - quadraticToSeg.getX(),
							quadraticToSeg.getY1() - quadraticToSeg.getY());
					OMSVGPoint v2 = owner.getSvg().createSVGPoint(
							quadraticToSeg.getX1() + delta.getX() - quadraticToSeg.getX(),
							quadraticToSeg.getY1() + delta.getY() - quadraticToSeg.getY());
					float d = v1.length() * v2.length();
					if (d != 0) {
						angleNext = (float)(Math.acos(v1.dotProduct(v2) / d) * 180 / Math.PI);						
						if (v1.crossProduct(v2) < 0) {
							angleNext = 360 - angleNext;
						}
					}					
				}
			}
			quadraticToSeg.setX1(quadraticToSeg.getX1() + delta.getX());
			quadraticToSeg.setY1(quadraticToSeg.getY1() + delta.getY());
			update(hs);
			if (anglePrev != null) {
				// Apply the same rotation to the spline cubic tangent
				OMSVGMatrix m = owner.getSvg().createSVGMatrix();
				m = m.translate(prevSeg.getX(), prevSeg.getY());
				m = m.rotate(anglePrev);
				m = m.translate(-prevSeg.getX(), -prevSeg.getY());
				OMSVGPoint p0 = owner.getSvg().createSVGPoint(
						prevSeg.getX2(),
						prevSeg.getY2());
				OMSVGPoint p1 = p0.matrixTransform(m).substract(p0);
				prevSeg.processMouseMove(p1, prevSeg.getCp2(), hs, false);
			}
			if (angleNext != null) {
				// Apply the same rotation to the next spline tangent
				OMSVGMatrix m = owner.getSvg().createSVGMatrix();
				m = m.translate(quadraticToSeg.getX(), quadraticToSeg.getY());
				m = m.rotate(angleNext);
				m = m.translate(-quadraticToSeg.getX(), -quadraticToSeg.getY());
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
		StringBuilder builder = new StringBuilder("Q ");
		builder.append(quadraticToSeg.getX1());
		builder.append(",");
		builder.append(quadraticToSeg.getY1());
		builder.append(" ");
		builder.append(quadraticToSeg.getX());
		builder.append(",");
		builder.append(quadraticToSeg.getY());
		return builder.toString();
	}

}
