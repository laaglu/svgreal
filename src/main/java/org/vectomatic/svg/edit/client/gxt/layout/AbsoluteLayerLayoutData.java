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
package org.vectomatic.svg.edit.client.gxt.layout;

import com.extjs.gxt.ui.client.widget.layout.LayoutData;

/**
 * Class to position layers in containers which use
 * AbsoluteLayerLayout.
 * @author laaglu
 */
public class AbsoluteLayerLayoutData extends LayoutData {
	public static final int HORIZONTAL_ATTACH_LEFT = 0x01;
	public static final int HORIZONTAL_ATTACH_RIGHT = 0x02;
	public static final int VERTICAL_ATTACH_TOP = 0x04;
	public static final int VERTICAL_ATTACH_BOTTOM = 0x08;
	private int attachmentFlags;
	private int horizontalOffset;
	private int verticalOffset;
	private int width;
	private int height;
	private int zIndex;
	/**
	 * Constructor
	 * @param attachmentFlags
	 * A combination of an horizontal attachment flag and
	 * a vertical attachment flag
	 * @param horizontalOffset
	 * The distance to the horizontal attachment point
	 * @param verticalOffset
	 * The distance to the vertical attachment point
	 * @param width
	 * The width (if 0, it will adjust to the container width)
	 * @param height
	 * The height (if 0, it will adjust to the container height)
	 * @param zIndex
	 * The z index
	 */
	public AbsoluteLayerLayoutData(
			int attachmentFlags,
			int horizontalOffset,
			int verticalOffset,
			int width,
			int height,
			int zIndex) {
		this.attachmentFlags = attachmentFlags;
		assert((attachmentFlags & (HORIZONTAL_ATTACH_LEFT|HORIZONTAL_ATTACH_RIGHT)) != 0);
		assert((attachmentFlags & (VERTICAL_ATTACH_TOP|VERTICAL_ATTACH_BOTTOM)) != 0);
		this.horizontalOffset = horizontalOffset;
		this.verticalOffset = verticalOffset;
		this.width = width;
		this.height = height;
		this.zIndex = zIndex;
	}
	public boolean isAttachedLeft() {
		return (attachmentFlags & HORIZONTAL_ATTACH_LEFT) != 0;
	}
	public boolean isAttachedRight() {
		return (attachmentFlags & HORIZONTAL_ATTACH_RIGHT) != 0;
	}
	public boolean isAttachedTop() {
		return (attachmentFlags & VERTICAL_ATTACH_TOP) != 0;
	}
	public boolean isAttachedBottom() {
		return (attachmentFlags & VERTICAL_ATTACH_BOTTOM) != 0;
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public int getHorizontalOffset() {
		return horizontalOffset;
	}
	public int getVerticalOffset() {
		return verticalOffset;
	}
	public int getZIndex() {
		return zIndex;
	}
}
