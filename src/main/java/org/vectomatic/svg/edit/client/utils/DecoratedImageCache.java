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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Class to create and cache images decorated by
 * other images (typically, an image representing an object
 * decorated by a image representing this object's status,
 * à la eclipse)
 * @author laaglu
 */
public class DecoratedImageCache {
	/**
	 * Horizontal alignment of the decoration image
	 */
	public enum HAlign {
		LEFT,
		CENTER,
		RIGHT
	};
	/**
	 * Vertical alignment of the decoration image
	 */
	public enum VAlign {
		TOP,
		CENTER,
		BOTTOM
	};
	/**
	 * Composite key class for entries in the cache
	 */
	protected class StoreKey {
		private HAlign halign;
		private VAlign valign;
		private ImageResource decoration;
		public StoreKey(ImageResource decoration, HAlign halign, VAlign valign) {
			this.decoration = decoration;
			this.halign = halign;
			this.valign = valign;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			if (decoration != null) {
				builder.append(decoration.getName());
			} else {
				builder.append(decoration);
			}
			builder.append("; ");
			builder.append(halign);
			builder.append("; ");
			builder.append(valign);
			builder.append("}");
			return builder.toString();
		}
		@Override
		public boolean equals(Object o) {
			if (o instanceof StoreKey) {
				StoreKey storeKey = (StoreKey)o;
				return halign == storeKey.halign
				&& valign == storeKey.valign
				&& decoration == storeKey.decoration;
			}
			return false;
		}
		@Override
		public int hashCode() {
			return halign.hashCode() + 13 * valign.hashCode() + 37 * (decoration == null ? 0 : decoration.hashCode());
		}
	}
	
	/**
	 * A canvas to create the decorated images
	 */
	protected Canvas canvas;
	/**
	 * The canvas context
	 */
	protected Context2d ctx;
	/**
	 * Maps image resources to simple images
	 */
	protected Map<ImageResource, SimpleImage> resourceToImage;
	/**
	 * Cache storage
	 */
	protected Map<ImageResource,Map<StoreKey, AbstractImagePrototype>> imageToDecorations;
	/**
	 * Constructor
	 * @param resourceToImage
	 * A map containing the base images and the decoration images
	 */
	public DecoratedImageCache(Map<ImageResource, SimpleImage> resourceToImage) {
		this.resourceToImage = resourceToImage;
		imageToDecorations = new HashMap<ImageResource, Map<StoreKey, AbstractImagePrototype>>();
		canvas = Canvas.createIfSupported();
		ctx = canvas.getContext2d();
	}
	
	/**
	 * Creates or returns a decorated image
	 * @param base
	 * The base image
	 * @param decoration
	 * The image decoration
	 * @param halign
	 * Horizontal alignment of the decoration
	 * @param valign
	 * Vertical alignment of the decoration
	 * @return
	 * The decorated image
	 */
	public AbstractImagePrototype getImageWithDecoration(ImageResource base, ImageResource decoration, HAlign halign, VAlign valign) {
		Map<StoreKey, AbstractImagePrototype> decorationToImage = imageToDecorations.get(base);
		if (decorationToImage == null) {
			decorationToImage = new HashMap<StoreKey, AbstractImagePrototype>();
			imageToDecorations.put(base, decorationToImage);
		}
		StoreKey key = new StoreKey(decoration, halign, valign);
		AbstractImagePrototype image = decorationToImage.get(key);
		if (image == null) {
			if (decoration != null) {
				SimpleImage baseImg = resourceToImage.get(base);
				if (baseImg == null) {
					throw new IllegalArgumentException();
				}
				SimpleImage decorationImg = resourceToImage.get(decoration);
				if (decorationImg == null) {
					throw new IllegalArgumentException();
				}
				image = decorate(baseImg, decorationImg, halign, valign);
			} else {
				image = AbstractImagePrototype.create(base);
			}
			decorationToImage.put(key, image);
		}
		return image;
	}
	
	/**
	 * Decorates the specified image
	 * @param base
	 * The base image
	 * @param decoration
	 * The image decoration
	 * @param halign
	 * Horizontal alignment of the decoration
	 * @param valign
	 * Vertical alignment of the decoration
	 * @return
	 * The decorated image
	 */
	public AbstractImagePrototype decorate(SimpleImage base, SimpleImage decoration, HAlign halign, VAlign valign) {
		ctx.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());  
		int baseWidth = base.getWidth();
		int baseHeight = base.getHeight();
		int decWidth = decoration.getWidth();
		int decHeight = decoration.getHeight();
		canvas.setCoordinateSpaceWidth(baseWidth);
		canvas.setCoordinateSpaceHeight(baseHeight);
		int x = 0;
		switch(halign) {
			case LEFT:
				break;
			case CENTER:
				x = (baseWidth - decWidth) / 2;
				break;
			case RIGHT:
				x = baseWidth - decWidth;
				break;
		}
		int y = 0;
		switch(valign) {
			case TOP:
				break;
			case CENTER:
				y = (baseHeight - decHeight) / 2;
				break;
			case BOTTOM:
				y = baseHeight - decHeight;
				break;
		}
		ctx.drawImage(base.getElement().<ImageElement>cast(), 0, 0);
		ctx.drawImage(decoration.getElement().<ImageElement>cast(), x, y);
		return new SimpleImagePrototype(canvas.toDataUrl(), baseWidth, baseHeight);
	}
}
