/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client;

import com.google.gwt.resources.client.CssResource;

/**
 * CSS definition class
 * @author laaglu
 */
public interface AppCss extends CssResource {
	@ClassName("thumb")
	public String thumb();
	@ClassName("thumb-wrap")
	public String thumbWrap();
	@ClassName("compass-container")
	public String compassContainer();
	@ClassName("compass")
	public String compass();
	@ClassName("scale-slider-container")
	public String scaleSliderContainer();
	@ClassName("scale-slider")
	public String scaleSlider();
	
	////////////////////////////////////////
	// Manipulators
	////////////////////////////////////////
	@ClassName("transform-manipulator")
	public String transformManipulator();
	@ClassName("rect-geometry-manipulator")
	public String rectGeometryManipulator();
	@ClassName("ellipse-geometry-manipulator")
	public String ellipseGeometryManipulator();
	@ClassName("circle-geometry-manipulator")
	public String circleGeometryManipulator();
	@ClassName("animated-point-selected")
	public String animatedPointSelected();
	@ClassName("animated-point-unselected")
	public String animatedPointUnselected();
	@ClassName("animated-points-geometry-manipulator")
	public String animatedPointsGeometryManipulator();
	@ClassName("line-geometry-manipulator")
	public String lineGeometryManipulator();
	@ClassName("path-geometry-manipulator")
	public String pathGeometryManipulator();
	
	////////////////////////////////////////
	// Dash-array editor
	////////////////////////////////////////
	@ClassName("dasharray-cell")
	public String dasharrayCell();

	////////////////////////////////////////
	// Drag and drop
	////////////////////////////////////////
	@ClassName("ghost-source")
	public String ghostSource();
	@ClassName("ghost-operation")
	public String ghostOperation();
	@ClassName("ghost-message")
	public String ghostMessage();
	@ClassName("ghost")
	public String ghost();

	////////////////////////////////////////
	// Grid and rulers
	////////////////////////////////////////
	@ClassName("grid1")
	public String grid1();
	@ClassName("grid2")
	public String grid2();
	@ClassName("hruler")
	public String hruler();
	@ClassName("vruler")
	public String vruler();
	@ClassName("hruler-pattern")
	public String hrulerPattern();
	@ClassName("vruler-pattern")
	public String vrulerPattern();
	@ClassName("grid-marker")
	public String gridMarker();

	////////////////////////////////////////
	// Image href preview
	////////////////////////////////////////
	@ClassName("image-href-drop-area")
	public String imageHrefDropArea();
}
