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
package org.vectomatic.svg.edit.client.utils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple image class which fires load events even
 * if it is not attached to the DOM.
 * @author laaglu
 */
public class SimpleImage extends Widget implements HasLoadHandlers {
	public SimpleImage() {
		setElement(Document.get().createImageElement());
	    DOM.setEventListener(getElement(), this);    
	    DOM.sinkEvents(getElement(), Event.getTypeInt(LoadEvent.getType().getName()) | DOM.getEventsSunk(getElement()));

	}
	
	public void setSrc(String url) {
		getElement().<ImageElement>cast().setSrc(url);
	}
	
	@Override
	public HandlerRegistration addLoadHandler(LoadHandler handler) {
		return addDomHandler(handler, LoadEvent.getType());
	}
	public String getSrc() {
		return getElement().<ImageElement>cast().getSrc();
	}

	public int getHeight() {
		return getElement().<ImageElement>cast().getHeight();
	}

	public int getWidth() {
		return getElement().<ImageElement>cast().getWidth();
	}
}
