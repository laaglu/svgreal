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
package org.vectomatic.svg.edit.client.gxt.widget;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGPaintParser;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * Widget to display a paint value
 * @author laaglu
 */
public class PaintCell extends SVGImage {
	protected OMSVGRectElement rect;
	public PaintCell() {
		OMSVGDocument document = OMSVGParser.currentDocument();
		OMSVGSVGElement svg = document.createSVGSVGElement();
		rect = document.createSVGRectElement();
		rect.getWidth().getBaseVal().setValueAsString("100%");
		rect.getHeight().getBaseVal().setValueAsString("100%");
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
		svg.appendChild(rect);
		setSvgElement(svg);
	}
	public void setPaint(OMSVGPaint paint) {
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, paint.getCssText());
	}
	public OMSVGPaint getPaint() {
		String cssText = rect.getStyle().getSVGProperty(SVGConstants.CSS_FILL_PROPERTY);
		return (cssText != null && cssText.length() > 0) ? OMSVGParser.parsePaint(cssText) : SVGPaintParser.NONE;
	}
	
}
