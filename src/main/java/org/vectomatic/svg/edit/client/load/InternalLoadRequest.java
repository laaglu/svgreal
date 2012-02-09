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

import org.vectomatic.dom.svg.ui.SVGResource;
import org.vectomatic.svg.edit.client.SvgrealApp;

/**
 * Class to load built-in resources into the application
 * @author laaglu
 */
public class InternalLoadRequest extends LoadRequestBase {
	private SVGResource resource;
	public InternalLoadRequest(SVGResource resource, String title) {
		this.resource = resource;
		this.title = title;
	}

	@Override
	public void load() {
		SvgrealApp.getApp().addWindow(resource.getSvg(), this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof InternalLoadRequest) {
			InternalLoadRequest r = (InternalLoadRequest)o;
			return resource.equals(r.resource);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return resource.hashCode();
	}

}
