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

import org.vectomatic.dom.svg.ui.SVGImage;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Application about dialog class
 * @author laaglu
 */
public class AboutDialog extends Dialog {

	public AboutDialog() {
		super();
		setHideOnButtonClick(true);
		setHeading(AppConstants.INSTANCE.aboutMenuItem());
		setModal(true);
		setResizable(false);
		setSize(300, 330);
		okText = AppConstants.INSTANCE.closeButton();
		setButtons(Dialog.OK);
		RowLayout layout = new RowLayout(Orientation.VERTICAL);
		setLayout(layout);
		SVGImage logo = new SVGImage(AppBundle.INSTANCE.logo());
		logo.getStyle().setWidth(100, Unit.PCT);
		logo.getStyle().setHeight(82, Unit.PX);
//		logo.setPixelSize(195, 82);
		add(logo, new RowData(-1, -1, new Margins(5)));
		add(new Html(AppConstants.INSTANCE.aboutText()), new RowData(-1, -1, new Margins(0)));
		SVGImage gplv3 = new SVGImage(AppBundle.INSTANCE.gplv3());
		gplv3.getStyle().setWidth(100, Unit.PCT);
		gplv3.getStyle().setHeight(37, Unit.PX);
//		gplv3.setPixelSize(74,37);
		add(gplv3, new RowData(-1, -1, new Margins(5)));
	}

}
