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

import java.util.List;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.svg.edit.client.command.path.IPathRepOwner.VertexState;

import com.google.gwt.dom.client.Element;

/**
 * Base class to path segment representation
 * @author laaglu
 */
public abstract class SVGSegRep {
	/**
	 * The owner of this segment
	 */
	protected IPathRepOwner owner;
	/**
	 * The previous segment in the path
	 */
	protected SVGSegRep previous;
	/**
	 * The next segment in the path
	 */
	protected SVGSegRep next;
	/**
	 * The group where SVG elements representing tangents
	 * to this segment are nested
	 */
	protected OMSVGGElement tangents;
	/**
	 * The endpoint of the segment
	 */
	protected OMSVGRectElement vertex;

	public SVGSegRep(IPathRepOwner owner) {
		this.owner = owner;
		OMSVGDocument document = (OMSVGDocument) owner.getSvg().getOwnerDocument();
		tangents = document.createSVGGElement();
		vertex = document.createSVGRectElement();
	}

	public OMSVGGElement getTangents() {
		return tangents;
	}

	public OMSVGRectElement getVertex() {
		return vertex;
	}

	public abstract OMSVGPathSeg getElement();
	public abstract float getX();
	public abstract float getY();
	public abstract void setX(float x);
	public abstract void setY(float y);
	public float getX1() {
		return 0;
	}
	public float getY1() {
		return 0;
	}
	public float getX2() {
		return 0;
	}
	public float getY2() {
		return 0;
	}
	public Element getCp1() {
		return null;
	}
	public void setCp1(OMSVGPoint p, float hs) {
	}
	public Element getCp2() {
		return null;
	}
	public void setCp2(OMSVGPoint p, float hs) {
	}
	/**
	 * Updates the vertex, control points and tangents defining the segment
	 * @param delta
	 * The translation to apply
	 * @param target
	 * The vertex, control point or tangeant to update. null means the segment endpoint vertex
	 * @param hs
	 * The control point size
	 * @param isCtrlKeyDown
	 * True if the control key is pressed (spline will interpret this to preserve
	 * the angle between tangents when a tangent control point is dragged)
	 */
	public abstract void processMouseMove(OMSVGPoint delta, Element target, float hs, boolean isCtrlKeyDown);
	public void updateStart(OMSVGPoint delta, float hs) {
	}
	public abstract void updateEnd(OMSVGPoint delta, float hs);
	public abstract void update(float hs);

	public VertexState getState() {
		return VertexState.read(vertex);
	}
	
	public void setState(VertexState state) {
		state.write(vertex);
	}
	
	public SVGSegRep getPreviousSplineSeg() {
		SVGSegRep splineSeg = null;
		List<SVGSegRep> segments = owner.getSegments();
		splineSeg = previous;
		return (splineSeg instanceof SVGCubicSegRep || splineSeg instanceof SVGQuadraticSegRep) ? splineSeg : null;
	}

	public SVGSegRep getNextSplineSeg() {
		SVGSegRep splineSeg = null;
		splineSeg = next;
		return (splineSeg instanceof SVGCubicSegRep || splineSeg instanceof SVGQuadraticSegRep) ? splineSeg : null;
	}
	
	public void setNext(SVGSegRep seg) {
		next = seg;
	}
	public void setPrevious(SVGSegRep seg) {
		previous = seg;
	}

}


