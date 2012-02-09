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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * AbstractImagePrototype subclass to act as an adapter
 * for data url images.
 * @author laaglu
 */
public class SimpleImagePrototype extends AbstractImagePrototype {
	protected String data;
	protected int width;
	protected int height;
	public SimpleImagePrototype(String data, int width, int height) {
		this.data = data;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void applyTo(Image image) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public Image createImage() {
		Image image = new Image(data);
		image.setPixelSize(width, height);
		return image;
	}

	public ImagePrototypeElement createElement() {
		ImageElement elt = Document.get().createImageElement();
		elt.setWidth(width);
		elt.setHeight(height);
		elt.setSrc(data);
		return elt.cast();
	}
}
