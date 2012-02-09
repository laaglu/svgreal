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

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

/**
 * GXT layout class to implement a multilayer layout based
 * on several CSS absolutely positionned divs with different
 * z-indices.
 * @author laaglu
 */
public class AbsoluteLayerLayout extends FitLayout {
	protected void onLayout(Container<?> container, El target) {
		super.onLayout(container, target);
		// Retrieve the first parent with absolute layout (it
		// ought to be the Window)
		Element elt = container.getElement();
		Point p = target.getOffsetsTo((com.google.gwt.user.client.Element)elt.cast());
		GWT.log("p = " + p.toString());
		Size windowSize = El.fly(elt).getStyleSize();
		for (int i = 0, count = container.getItemCount(); i < count; i++) {
			Component c = container.getItem(i);
			Size containerSize = target.getStyleSize();
			containerSize.width -= getSideMargins(c);
			containerSize.height -= c.el().getMargins("tb");
			c.getElement().getStyle().setPosition(Position.ABSOLUTE);
			AbsoluteLayerLayoutData data = (AbsoluteLayerLayoutData) c.getData("layoutData");
			int horizontalOffset = data.getHorizontalOffset();
			if (data.isAttachedLeft()) {
				c.getElement().getStyle().setLeft(p.x + horizontalOffset, Unit.PX);
			} else {
				c.getElement().getStyle().setRight(windowSize.width - containerSize.width - p.x + horizontalOffset, Unit.PX);
			}
			int verticalOffset = data.getVerticalOffset();
			if (data.isAttachedTop()) {
				c.getElement().getStyle().setTop(p.y + verticalOffset, Unit.PX);
			} else {
				c.getElement().getStyle().setBottom(windowSize.height - containerSize.height - p.y + verticalOffset, Unit.PX);
			}
			if (c.isRendered()) {
				setSize(c, 
						data.getWidth() > 0 ? data.getWidth() : containerSize.width, 
						data.getHeight() > 0 ? data.getHeight() : containerSize.height);
			}
			c.getElement().getStyle().setZIndex(data.getZIndex());
		}
	}
}

