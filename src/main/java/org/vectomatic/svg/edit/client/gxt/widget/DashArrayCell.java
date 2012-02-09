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
package org.vectomatic.svg.edit.client.gxt.widget;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.model.svg.DashArray;

import com.google.gwt.dom.client.Style.Unit;

/**
 * Widget to display a dash-array value
 * @author laaglu
 */
public class DashArrayCell extends SVGImage {
	protected OMSVGLineElement line;
	protected DashArray dashArray;

	public DashArrayCell() {
		OMSVGDocument document = OMSVGParser.currentDocument();
		OMSVGSVGElement svg = document.createSVGSVGElement();
		svg.getStyle().setHeight(10, Unit.PX);
		svg.getStyle().setWidth(100, Unit.PCT);
		OMSVGRectElement rect = document.createSVGRectElement();
		rect.getX().getBaseVal().setValueAsString("0%");
		rect.getY().getBaseVal().setValueAsString("0%");
		rect.getWidth().getBaseVal().setValueAsString("100%");
		rect.getHeight().getBaseVal().setValueAsString("100%");
		line = document.createSVGLineElement();
		line.getX1().getBaseVal().setValueAsString("0%");
		line.getY1().getBaseVal().setValueAsString("50%");
		line.getX2().getBaseVal().setValueAsString("100%");
		line.getY2().getBaseVal().setValueAsString("50%");
		svg.setClassNameBaseVal(AppBundle.INSTANCE.css().dasharrayCell());
		svg.appendChild(rect);
		svg.appendChild(line);
		setSvgElement(svg);
	}

	public void setDashArray(DashArray dashArray) {
		OMSVGStyle style = line.getStyle();
		String cssText = (dashArray != null) ? dashArray.toString() : "";
		if (cssText.length() > 0) {
			style.setSVGProperty(SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY, cssText);
		} else {
			style.clearSVGProperty(SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY);
		}
		this.dashArray = dashArray;
	}
	
	public DashArray getDashArray() {
		return dashArray;
	}
}
