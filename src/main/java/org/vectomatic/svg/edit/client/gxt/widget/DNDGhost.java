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

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGSVGElement;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppCss;
import org.vectomatic.svg.edit.client.command.dnd.IDndHandler;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;

/**
 * Class to give users visual feedback on the drag operation
 * about to be performed
 * @author laaglu
 */
public class DNDGhost {
	private static final String ATT_OPERATION = "operation";
	/**
	 * To update the DND operation
	 */
	private static SVGSVGElement operationElement;
	private static DivElement messageElement;
	private DivElement ghostElement;
	private List<SVGElementModel> sourceElements;
	public DNDGhost(List<SVGElementModel> sourceElements, DNDEvent event) {
		/*
		 * The custom ghost has the following css structure
		 * custom ghost container div (ghost)
		 *  source div (ghost-source)
		 *  operation div (ghost-operation)
		 *  message div (ghost-message)
		 */
		this.sourceElements = sourceElements;
		
		// Create the SVG miniature of the first element in the source
		AppCss css = AppBundle.INSTANCE.css();
		OMSVGSVGElement svg = new OMSVGSVGElement();
		OMSVGElement svgElement = sourceElements.get(0).getElementWrapper();
		ISVGTransformable transformable = (ISVGTransformable) svgElement;
	    OMSVGRect bbox = getScreenBBox(svg, transformable);
		OMSVGRect viewBox = svg.getViewBox().getBaseVal();
		bbox.assignTo(viewBox);
		OMSVGElement clone = (OMSVGElement) svgElement.cloneNode(true);
		OMSVGMatrix m = transformable.getCTM();
		if (!m.isIdentity()) {
			StringBuilder builder = new StringBuilder();
			builder.append(SVGConstants.TRANSFORM_MATRIX + "(");
			builder.append(m.getDescription());
			builder.append(")");
			clone.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, builder.toString());
		} else {
			clone.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
		}
		
		svg.appendChild(clone);
		svg.setClassNameBaseVal(css.ghostSource());

		// Create the operation icon
		if (operationElement == null) {
			operationElement = (SVGSVGElement) AppBundle.INSTANCE.dndIcons().getSvg().getElement();
			operationElement.getClassName_().setBaseVal(css.ghostOperation());
		}

		// Create the DND message
		if (messageElement == null) {
			messageElement = Document.get().createDivElement();
			messageElement.setClassName(css.ghostMessage());
		}
		
		// Create the ghost
		ghostElement = Document.get().createDivElement();
		ghostElement.setClassName(css.ghost());
		ghostElement.appendChild(svg.getElement());
		ghostElement.appendChild(operationElement);
		ghostElement.appendChild(messageElement);
		
		DragEvent dragEvent = event.getDragEvent();
		dragEvent.setWidth(120);
		dragEvent.setHeight(94);
	}
	
	public void update(IDndHandler dndHandler) {
		operationElement.setAttribute(ATT_OPERATION, dndHandler.getOperationCssAttr());
		messageElement.setInnerText(dndHandler.getMessage(sourceElements));
	}

	public Element getElement() {
		return (com.google.gwt.user.client.Element) ghostElement.cast();
	}

	private static OMSVGRect getScreenBBox(OMSVGSVGElement svg, ISVGTransformable transformable) {
		OMSVGRect bbox = transformable.getBBox();
	    OMSVGMatrix m = transformable.getCTM();
	    List<OMSVGPoint> list = new ArrayList<OMSVGPoint>();
	    list.add(svg.createSVGPoint(bbox.getX(), bbox.getY()).matrixTransform(m));
	    list.add(svg.createSVGPoint(bbox.getMaxX(), bbox.getY()).matrixTransform(m));
	    list.add(svg.createSVGPoint(bbox.getX(), bbox.getMaxY()).matrixTransform(m));
	    list.add(svg.createSVGPoint(bbox.getMaxX(), bbox.getMaxY()).matrixTransform(m));
	    float xmin = list.get(0).getX(), ymin = list.get(0).getY(), xmax = list.get(0).getX(), ymax = list.get(0).getY();
		for (int i = 1, size = list.size(); i < size; i++) {
			OMSVGPoint p = list.get(i);
			xmin = Math.min(p.getX(), xmin);
			ymin = Math.min(p.getY(), ymin);
			xmax = Math.max(p.getX(), xmax);
			ymax = Math.max(p.getY(), ymax);
		}
	    OMSVGRect screenBBox = svg.createSVGRect(xmin, ymin, xmax - xmin, ymax - ymin);
		GWT.log("m="+m.getDescription());
		GWT.log("bbox="+bbox.getDescription());
		GWT.log("screenBBox="+screenBBox.getDescription());
		return screenBBox;
	}

}
