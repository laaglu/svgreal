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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.svg.edit.client.SvgrealApp;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;

/**
 * Class to preload a list of images
 * @author laaglu
 */
public class ImageLoader implements HasInitializeHandlers {
	protected List<SimpleImage> images;
	protected ImageResource[] resources;
	
	/**
	 * Constructor
	 */
	public ImageLoader() {			
		images = new ArrayList<SimpleImage>();
		resources = new ImageResource[0];
	}
	
	/**
	 * Preloads the specified images, asynchronously. Once loaded, a
	 * {@link com.google.gwt.event.dom.client.LoadEvent} is fired.
	 * @param resources
	 * The images to preload.
	 */
	public void loadImages(ImageResource[] resources) {
		images.clear();
		this.resources = resources;
		readNext();
	}
	
	private void readNext() {
		if (images.size() < resources.length) {
			final SimpleImage image = new SimpleImage();
			images.add(image);
			image.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					readNext();
				}
			});
			image.setSrc(resources[images.size() - 1].getSafeUri().asString());
		} else {
			InitializeEvent.fire(this);
		}
	}

	/**
	 * Returns the preloaded images.
	 * @return the preloaded images.
	 * @throws IllegalStateException if this method is
	 * invoked synchronously before the 
	 * {@link com.google.gwt.event.dom.client.LoadEvent} has been fired.
	 */
	public Map<ImageResource, SimpleImage> getImages() {
		if (resources.length != images.size()) {
			throw new IllegalStateException();
		}
		Map<ImageResource, SimpleImage> resourceToImage = new HashMap<ImageResource, SimpleImage>();
		for (int i = 0; i < resources.length; i++) {
			resourceToImage.put(resources[i], images.get(i));
		}
		return resourceToImage;
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		SvgrealApp.getApp().getEventBus().fireEventFromSource(event, this);
		
	}

	@Override
	public HandlerRegistration addInitializeHandler(InitializeHandler handler) {
		return SvgrealApp.getApp().getEventBus().addHandlerToSource(InitializeEvent.getType(), this, handler);
	}	
}
