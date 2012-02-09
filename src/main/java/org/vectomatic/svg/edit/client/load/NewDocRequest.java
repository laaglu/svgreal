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
package org.vectomatic.svg.edit.client.load;

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.SvgrealApp;

import com.extjs.gxt.ui.client.util.Format;

/**
 * Class to create new blank files into the application
 * @author laaglu
 */
public class NewDocRequest extends LoadRequestBase {
	private static int newDocumentCount;
	public NewDocRequest() {
		title = Format.capitalize(AppConstants.INSTANCE.untitled() + " " + newDocumentCount++);
	}
	@Override
	public void load() {
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SvgrealApp.getApp().addWindow(svg, this);
	}

}
