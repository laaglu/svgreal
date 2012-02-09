/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client.engine;

import org.vectomatic.dom.svg.OMSVGDefsElement;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPatternElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppCss;
import org.vectomatic.svg.edit.client.model.svg.SVGViewBoxElementModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

/**
 * Class to represent grids.
 * The grid has the following SVG structure:
 * <pre>
 * <defs>
 *  <pattern id="docid-grid">
 *   <path> // fine grid
 *   <path> // coarse grid
 *  </pattern>
 *  <pattern id="docid-vrule"> // vrule
 *   <rect> // ruler background
 *   <path> // ruler gradations
 *  </pattern>
 *  <pattern id="docid-rule"> // hrule
 *   <rect> // ruler background
 *   <path> // ruler gradations
 *  </pattern>
 * </defs>
 * 
 * <g>
 *	<rect style="fill:url('#docid-grid');stroke:black;"/> // grid
 * 
 *  <g>	// hruler
 *   <rect style="fill:url('#docid-hrule');"/>
 *	 <g>
 * 	  <text x="0" y="-10">0</text>
 * 	  ...
 * 	  <text x="200" y="-10">N</text>
 * 	 </g>
 *   <path transform='translate(x,y)'> // hmarker
 *  </g>
 *  
 *  <g> // vruler
 *	 <rect style="fill:url('#docid-vrule');"/> 
 * 	 <g>
 * 	  <text x="-15" y="0">0</text>
 * 	  <text x="-15" y="300">300</text>
 * 	 </g>
 *   <path transform='translate(x,y)'> // hmarker
 *  </g>
 * </g>
 * </pre>
 * @author laaglu
 */
public class Grid {
	/**
	 * True if the grid and rulers are visible
	 */
	private boolean showsGrid;
	/**
	 * True if the coordinates and guides are visible
	 */
	private boolean showsGuides;
	/**
	 * True if the mouse input is rounded to the grid
	 */
	private boolean snapsToGrid;
	/**
	 * The grid root defs
	 */
	private OMSVGDefsElement defs;
	/**
	 * The grid root element
	 */
	private OMSVGGElement root;
	/**
	 * The grid element
	 */
	private OMSVGRectElement grid;
	/**
	 * The horizontal ruler element
	 */
	private OMSVGGElement hruler;
	/**
	 * The vertical ruler element
	 */
	private OMSVGGElement vruler;
	/**
	 * The horizontal position marker
	 */
	private OMSVGPathElement hmarker;
	/**
	 * The vertical position marker
	 */
	private OMSVGPathElement vmarker;	
	/**
	 * To update position markers
	 */
	private MouseMoveHandler moveHandler;
	/**
	 * The svg model to which the grid is attached
	 */
	private SVGModel svgModel;
	/**
	 * The grid horizontal spacing
	 */
	private float dx;
	/**
	 * The grid vertical spacing
	 */
	private float dy;
	
	public Grid() {
		moveHandler = new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (showsGuides && isAttached()) {
					OMSVGPoint p = svgModel.getCoordinates(event, true);
					setHMarkerPosition(p.getX());
					setVMarkerPosition(p.getY());
				}
			}		
		};
	}
	
	public boolean showsGrid() {
		return showsGrid;
	}
	
	public void setShowsGrid(boolean showsGrid) {
		GWT.log("Grid.setShowsGrid(" + showsGrid + ")");
		this.showsGrid = showsGrid;
		grid.getStyle().setVisibility(showsGrid ? Visibility.VISIBLE : Visibility.HIDDEN);
	}
	
	public boolean showsGuides() {
		return showsGuides;
	}
	
	public void setShowsGuides(boolean showsGuides) {
		GWT.log("Grid.setShowsGuides(" + showsGuides + ")");
		this.showsGuides = showsGuides;
		hruler.getStyle().setVisibility(showsGuides ? Visibility.VISIBLE : Visibility.HIDDEN);
		vruler.getStyle().setVisibility(showsGuides ? Visibility.VISIBLE : Visibility.HIDDEN);
	}
	
	public boolean snapsToGrid() {
		return snapsToGrid;
	}
	
	public void setSnapsToGrid(boolean snapsToGrid) {
		GWT.log("Grid.setSnapsToGrid(" + snapsToGrid + ")");
		this.snapsToGrid = snapsToGrid;
	}
	
	public OMSVGDefsElement getDefs() {
		return defs;
	}
	
	public OMSVGGElement getRoot() {
		return root;
	}
	
	public MouseMoveHandler getMouseMoveHandler() {
		return moveHandler;
	}
	
	/**
	 * Snaps the specified point to the grid
	 * @param p A point in the model coordinate system
	 * @return A point snapped to grid
	 */
	public OMSVGPoint snap(OMSVGPoint p) {
		SVGViewBoxElementModel viewBox = svgModel.getViewBox();
		OMSVGSVGElement svg = svgModel.getSvgElement();
		OMSVGPoint p0 = svg.createSVGPoint(viewBox.<Float>get(SVGConstants.SVG_X_ATTRIBUTE), viewBox.<Float>get(SVGConstants.SVG_Y_ATTRIBUTE));
		OMSVGPoint p1 = p.substract(p0, svg.createSVGPoint());
		p1.setX(dx * (Math.round(p1.getX() / dx)));
		p1.setY(dy * (Math.round(p1.getY() / dy)));
		return p1.add(p0);
	}
	
	public void setHMarkerPosition(float xPos) {
		if (isAttached()) {
			// Clamp xPos
			SVGViewBoxElementModel viewBox = svgModel.getViewBox();
			float x = viewBox.<Float>get(SVGConstants.SVG_X_ATTRIBUTE);
			float width = viewBox.<Float>get(SVGConstants.SVG_WIDTH_ATTRIBUTE);
			xPos = Math.min(Math.max(xPos, x), x + width);
			
			hmarker.getTransform().getBaseVal().getItem(0).setTranslate(xPos, 0);
		}
	}

	public void setVMarkerPosition(float yPos) {
		if (isAttached()) {
			// Clamp yPos
			SVGViewBoxElementModel viewBox = svgModel.getViewBox();
			float y = viewBox.<Float>get(SVGConstants.SVG_Y_ATTRIBUTE);
			float height = viewBox.<Float>get(SVGConstants.SVG_HEIGHT_ATTRIBUTE);
			yPos = Math.min(Math.max(yPos, y), y + height);
			vmarker.getTransform().getBaseVal().getItem(0).setTranslate(0, yPos);
		}
	}
	
	public boolean isAttached() {
		return svgModel != null;
	}

	public void attach(SVGModel svgModel) {
		this.svgModel = svgModel;
		AppCss css = AppBundle.INSTANCE.css();
		OMSVGSVGElement svg = svgModel.getDocumentRoot();
		OMSVGDocument doc = (OMSVGDocument) svg.getOwnerDocument();
		String modelId = svgModel.getDocumentRoot().getId();
		
		// Build the grid pattern
		dx = dy = 5;
		OMSVGPathElement gridPath1 = new OMSVGPathElement();
		OMSVGPathSegList gridSegs1 = gridPath1.getPathSegList();
		for (int i = 1; i < 5; i++) {
			gridSegs1.appendItem(gridPath1.createSVGPathSegMovetoAbs(0, i * 5));
			gridSegs1.appendItem(gridPath1.createSVGPathSegLinetoHorizontalAbs(25));
		}
		for (int i = 1; i < 5; i++) {
			gridSegs1.appendItem(gridPath1.createSVGPathSegMovetoAbs(i * 5, 0));
			gridSegs1.appendItem(gridPath1.createSVGPathSegLinetoVerticalAbs(25));
		}
		gridPath1.setClassNameBaseVal(css.grid1());
		
		OMSVGPathElement gridPath2 = new OMSVGPathElement();
		OMSVGPathSegList gridSegs2 = gridPath2.getPathSegList();
		gridSegs2.appendItem(gridPath2.createSVGPathSegMovetoAbs(0, 0));
		gridSegs2.appendItem(gridPath1.createSVGPathSegLinetoHorizontalAbs(25));
		gridSegs2.appendItem(gridPath2.createSVGPathSegMovetoAbs(0, 0));
		gridSegs2.appendItem(gridPath1.createSVGPathSegLinetoVerticalAbs(25));
		OMSVGPatternElement gridPattern = createPattern(0, 0, 25, 25);
		gridPath2.setClassNameBaseVal(css.grid2());
		String gridPatternId = modelId + "-grid";
		gridPattern.setId(gridPatternId);
		gridPattern.appendChild(gridPath1);
		gridPattern.appendChild(gridPath2);
		
		// Build the horizontal ruler pattern
		OMSVGRectElement hrulerPatternRect = doc.createSVGRectElement(0, 0, 100, 20, 0, 0);
		OMSVGPathElement hrulerPatternPath = new OMSVGPathElement();
		OMSVGPathSegList hrulerPatternSegs = hrulerPatternPath.getPathSegList();
		for (int i = 0; i < 10; i++) {
			hrulerPatternSegs.appendItem(hrulerPatternPath.createSVGPathSegMovetoAbs(i * 10, 20));
			hrulerPatternSegs.appendItem(hrulerPatternPath.createSVGPathSegLinetoVerticalRel(i == 0 ? -18 : (i % 2 == 1 ? -5 : -10)));
		}
		OMSVGPatternElement hrulerPattern = createPattern(0, 0, 100, 20);
		hrulerPattern.setClassNameBaseVal(css.hrulerPattern());
		String hrulerPatternId = modelId + "-hruler";
		hrulerPattern.setId(hrulerPatternId);
		hrulerPattern.appendChild(hrulerPatternRect);
		hrulerPattern.appendChild(hrulerPatternPath);
		
		// Build the vertical ruler pattern
		OMSVGRectElement vrulerPatternRect = doc.createSVGRectElement(0, 0, 20, 100, 0, 0);
		OMSVGPathElement vrulerPatternPath = new OMSVGPathElement();
		OMSVGPathSegList vrulerSegs = vrulerPatternPath.getPathSegList();
		for (int i = 0; i < 10; i++) {
			vrulerSegs.appendItem(vrulerPatternPath.createSVGPathSegMovetoAbs(20, i * 10));
			vrulerSegs.appendItem(vrulerPatternPath.createSVGPathSegLinetoHorizontalRel(i == 0 ? -18 : (i % 2 == 1 ? -5 : -10)));
		}
		OMSVGPatternElement vrulerPattern = createPattern(0, 0, 20, 100);
		vrulerPattern.setClassNameBaseVal(css.vrulerPattern());
		String vrulerPatternId = modelId + "-vruler";
		vrulerPattern.setId(vrulerPatternId);
		vrulerPattern.appendChild(vrulerPatternRect);
		vrulerPattern.appendChild(vrulerPatternPath);
		
		// Create the definitions
		defs = new OMSVGDefsElement();
		defs.appendChild(gridPattern);
		defs.appendChild(hrulerPattern);
		defs.appendChild(vrulerPattern);
		
		// Create the grid
		SVGViewBoxElementModel viewBox = svgModel.getViewBox();
		float x = viewBox.<Float>get(SVGConstants.SVG_X_ATTRIBUTE);
		float y = viewBox.<Float>get(SVGConstants.SVG_Y_ATTRIBUTE);
		float width = viewBox.<Float>get(SVGConstants.SVG_WIDTH_ATTRIBUTE);
		float height = viewBox.<Float>get(SVGConstants.SVG_HEIGHT_ATTRIBUTE);
		grid = doc.createSVGRectElement(x, y, width, height, 0, 0);
		grid.getStyle().setProperty(SVGConstants.CSS_FILL_PROPERTY, DOMHelper.toUrl(gridPatternId));
		grid.getStyle().setProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
		
		// Create the horizontal ruler
		OMSVGRectElement hrulerRect = doc.createSVGRectElement(x, y - 20, width, 20, 0, 0);
		hrulerRect.getStyle().setProperty(SVGConstants.CSS_FILL_PROPERTY, DOMHelper.toUrl(hrulerPatternId));
		OMSVGGElement hGradations = new OMSVGGElement();
		for (int i = (((int)x)/100)*100; i < width; i+=100) {
			hGradations.appendChild(doc.createSVGTextElement(i, -10, OMSVGLength.SVG_LENGTHTYPE_PX, Integer.toString(i)));
		}
		
		// Create the horizontal marker
		hmarker = doc.createSVGPathElement();
		OMSVGPathSegList hmarkerSegs = hmarker.getPathSegList();
		hmarkerSegs.appendItem(hmarker.createSVGPathSegMovetoAbs(0, 0));
		hmarkerSegs.appendItem(hmarker.createSVGPathSegLinetoRel(-4, -7));
		hmarkerSegs.appendItem(hmarker.createSVGPathSegLinetoHorizontalRel(8));
		hmarkerSegs.appendItem(hmarker.createSVGPathSegClosePath());
		hmarker.getTransform().getBaseVal().appendItem(svg.createSVGTransform());
		hmarker.setClassNameBaseVal(css.gridMarker());

		hruler = new OMSVGGElement();
		hruler.setClassNameBaseVal(css.hruler());
		hruler.appendChild(hrulerRect);
		hruler.appendChild(hGradations);
		hruler.appendChild(hmarker);

		// Create the vertical ruler
		OMSVGRectElement vrulerRect = doc.createSVGRectElement(x - 20, y, 20, height, 0, 0);
		vrulerRect.getStyle().setProperty(SVGConstants.CSS_FILL_PROPERTY, DOMHelper.toUrl(vrulerPatternId));
		OMSVGGElement vGradations = new OMSVGGElement();
		for (int i = (((int)y)/100)*100; i < height; i+=100) {
			vGradations.appendChild(doc.createSVGTextElement(-15, i, OMSVGLength.SVG_LENGTHTYPE_PX, Integer.toString(i)));
		}

		// Create the vertical marker
		vmarker = doc.createSVGPathElement();
		OMSVGPathSegList vmarkerSegs = vmarker.getPathSegList();
		vmarkerSegs.appendItem(vmarker.createSVGPathSegMovetoAbs(0, 0));
		vmarkerSegs.appendItem(vmarker.createSVGPathSegLinetoRel(-7, -4));
		vmarkerSegs.appendItem(vmarker.createSVGPathSegLinetoVerticalRel(8));
		vmarkerSegs.appendItem(vmarker.createSVGPathSegClosePath());
		vmarker.getTransform().getBaseVal().appendItem(svg.createSVGTransform());
		vmarker.setClassNameBaseVal(css.gridMarker());

		vruler = new OMSVGGElement();
		vruler.setClassNameBaseVal(css.vruler());
		vruler.appendChild(vrulerRect);
		vruler.appendChild(vGradations);
		vruler.appendChild(vmarker);

		root = new OMSVGGElement();
		root.appendChild(grid);
		root.appendChild(hruler);
		root.appendChild(vruler);
		setShowsGrid(false);
		setShowsGuides(false);
	}
	
	private static OMSVGPatternElement createPattern(float x, float y, float width, float height) {
		OMSVGPatternElement pattern = new OMSVGPatternElement();
		pattern.getX().getBaseVal().newValueSpecifiedUnits(Unit.PX, x);
		pattern.getY().getBaseVal().newValueSpecifiedUnits(Unit.PX, y);
		pattern.getWidth().getBaseVal().newValueSpecifiedUnits(Unit.PX, width);
		pattern.getHeight().getBaseVal().newValueSpecifiedUnits(Unit.PX, height);
		pattern.getViewBox().getBaseVal().setX(x);
		pattern.getViewBox().getBaseVal().setY(y);
		pattern.getViewBox().getBaseVal().setWidth(width);
		pattern.getViewBox().getBaseVal().setHeight(height);
		pattern.setAttribute(SVGConstants.SVG_PATTERN_UNITS_ATTRIBUTE, SVGConstants.SVG_USER_SPACE_ON_USE_VALUE);
		return pattern;
	}
}
