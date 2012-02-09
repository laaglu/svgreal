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

import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegMovetoAbs;
import org.vectomatic.dom.svg.OMSVGPoint;

import com.google.gwt.dom.client.Element;

/**
 * Class to represent a path moveto segment 
 * @author laaglu
 */
public class SVGMoveSegRep extends SVGSegRep {
	/**
	 * The moveto component of this path segment
	 */
	protected OMSVGPathSegMovetoAbs moveToSeg;

	public SVGMoveSegRep(IPathRepOwner owner, OMSVGPathSegMovetoAbs moveToSeg) {
		super(owner);
		this.moveToSeg = moveToSeg;
	}

	public OMSVGPathSeg getElement() {
		return moveToSeg;
	}

	@Override
	public float getX() {
		return moveToSeg.getX();
	}
	@Override
	public void setX(float x) {
		moveToSeg.setX(x);
	}
	@Override
	public float getY() {
		return moveToSeg.getY();
	}
	@Override
	public void setY(float y) {
		moveToSeg.setY(y);
	}

	@Override
	public void update(float hs) {
		float x = moveToSeg.getX();
		float y = moveToSeg.getY();
		vertex.getX().getBaseVal().setValue(x - hs);
		vertex.getY().getBaseVal().setValue(y - hs);
		vertex.getWidth().getBaseVal().setValue(hs * 2);
		vertex.getHeight().getBaseVal().setValue(hs * 2);
	}

	@Override
	public void updateStart(OMSVGPoint delta, float hs) {
		if (owner.isClosed() && owner.getSegments().get(0) == this) {
			processMouseMove(delta, null, hs, false);
		}
	}

	@Override
	public void updateEnd(OMSVGPoint delta, float hs) {
		moveToSeg.setX(moveToSeg.getX() + delta.getX());
		moveToSeg.setY(moveToSeg.getY() + delta.getY());
		update(hs);
	}

	@Override
	public void processMouseMove(OMSVGPoint delta, Element target, float hs, boolean isCtrlKeyDown) {
		if (target == null) {
			updateEnd(delta, hs);
			if (next != null) {
				next.updateStart(delta, hs);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("M ");
		builder.append(moveToSeg.getX());
		builder.append(",");
		builder.append(moveToSeg.getY());
		return builder.toString();
	}
}
