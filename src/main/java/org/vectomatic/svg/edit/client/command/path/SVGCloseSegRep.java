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

import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegClosePath;
import org.vectomatic.dom.svg.OMSVGPoint;

import com.google.gwt.dom.client.Element;

/**
 * Class to represent a path close segment 
 * @author laaglu
 */
public class SVGCloseSegRep extends SVGSegRep {
	/**
	 * The close component of this path segment
	 */
	protected OMSVGPathSegClosePath closeSeg;

	public SVGCloseSegRep(IPathRepOwner owner, OMSVGPathSegClosePath closeSeg) {
		super(owner);
		this.closeSeg = closeSeg;
	}

	@Override
	public OMSVGPathSeg getElement() {
		return closeSeg;
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}

	@Override
	public void setX(float x) {
	}

	@Override
	public void setY(float y) {
	}

	@Override
	public void processMouseMove(OMSVGPoint delta, Element target, float hs, boolean isCtrlKeyDown) {
	}

	@Override
	public void updateEnd(OMSVGPoint delta, float hs) {
	}

	@Override
	public void update(float hs) {
	}

}
