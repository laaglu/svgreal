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
package org.vectomatic.svg.edit.client.command.add;

import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.command.FactoryInstantiatorBase;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.event.MouseUpProcessor;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * Command factory to add new lines to the the SVG model.
 * @author laaglu
 */
public class AddLineCommandFactory extends AddCommandFactoryBase implements MouseDownProcessor, MouseMoveProcessor, MouseUpProcessor {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddLineCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddLineCommandFactory>(ModelConstants.INSTANCE.addLineCmdFactory(), ModelConstants.INSTANCE.addLineCmdFactoryDesc()) {
		@Override
		public AddLineCommandFactory create() {
			return new AddLineCommandFactory();
		}
	};

	/**
	 * The new line element
	 */
	protected OMSVGLineElement line;
	/**
	 * The line second point
	 */
	protected OMSVGPoint p;
	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		GWT.log("AddLineCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.addLineCmdFactory1());
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		if (owner != null && line != null) {
			owner.getTwinGroup().removeChild(line);
		}
		updateStatus(ModelConstants.INSTANCE.addLineCmdFactory2());
		owner = SvgrealApp.getApp().getActiveModel();
		p = owner.getCoordinates(event, true);
		line = new OMSVGLineElement(p.getX(), p.getY(), p.getX(), p.getY());
		applyCssContextStyle((SVGElement) line.getElement().cast());
		line.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
		owner.getTwinGroup().appendChild(line);
		return true;
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (owner != null) {
			updateStatus(ModelConstants.INSTANCE.addLineCmdFactory1());
			if (line.getX1().getBaseVal() == line.getX2().getBaseVal()
			 && line.getY1().getBaseVal() == line.getY2().getBaseVal()) {
				owner.getTwinGroup().removeChild(line);
			} else {
				createCommand(line);
			}
			line = null;
			owner = null;
		}
		return true;
	}
	
	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (owner != null) {
			p = owner.getCoordinates(event, true);
			line.getX2().getBaseVal().setValue(p.getX());
			line.getY2().getBaseVal().setValue(p.getY());
		}
		return true;
	}
}
