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
package org.vectomatic.svg.edit.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * NLS constants definition class
 * @author laaglu
 */
public interface AppConstants extends Constants {
	public static final AppConstants INSTANCE = GWT.create(AppConstants.class);
	////////////////////////////////////////
	// App Menus
	////////////////////////////////////////
	public String fileMenu();
	public String newDocumentMenuItem();
	public String openUrlMenuItem();
	public String openLocalMenuItem();
	public String openRssFeedMenuItem();
	public String exportAsSvgMenuItem();
	public String showSvgMarkupMenuItem();
	public String recentDocumentsMenuItem();

	public String editMenu();
	public String undoMenuItem();
	public String redoMenuItem();

	public String windowMenu();
	public String resetViewMenuItem();
	public String tileWindowsMenuItem();
	public String stackWindowsMenuItem();
	public String displayWindowMenuItem();
	public String closeWindowMenuItem();

	public String toolsMenu();
	public String inspectorMenuItem();

	public String aboutMenu();
	public String aboutMenuItem();
	
	public String aboutText();
	public String openUrlText();

	public String openButton();
	public String cancelButton();
	public String closeButton();
	public String commitButton();
	public String displayManipulatorButton();
	public String undoButton();
	public String redoButton();
	public String clearButton();

	public String selectCommand();
	public String untitled();
	
	////////////////////////////////////////
	// Color editor
	////////////////////////////////////////
	public String colorEditor();
	public String gradients();
	public String values();
	public String names();
	public String hue();
	public String saturation();
	public String value();
	public String red();
	public String green();
	public String blue();
	
	public String paintNone();
	public String paintCurrent();
	public String paintPlain();
	public String paintLinearGradient();
	public String paintRadialGradient();
	public String paintPattern();

	public String graphicalContext();
	
	public String fill();
	public String stroke();
	
	////////////////////////////////////////
	// Dash array editor
	////////////////////////////////////////
	public String editDashArray();
	public String dashArrayEditor();
	public String dashArraysLabel();
	public String dashArrayLabel();
	public String addButton();
	public String addDashArrayTip();
	public String removeButton();
	public String removeDashArrayTip();
	public String addDashTip();
	public String removeDashTip();
	public String dashValue();
	public String dashUnit();

	////////////////////////////////////////
	// Grid
	////////////////////////////////////////

	public String grid();
	public String showGrid();
	public String showGuides();
	public String snapToGrid();
}
