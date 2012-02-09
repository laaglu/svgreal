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

import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.svg.edit.client.gxt.widget.ViewportExt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;

/**
 * Class to handler drag and drop of external files on the viewport.
 * @author laaglu
 */
public class DndHandler implements DragEnterHandler, DragLeaveHandler, DragOverHandler, DropHandler {
	private ViewportExt viewport;
	public DndHandler(ViewportExt viewport) {
		this.viewport = viewport;
		viewport.addDragEnterHandler(this);
		viewport.addDragLeaveHandler(this);
		viewport.addDragOverHandler(this);
		viewport.addDropHandler(this);
	}
	
	@Override
	public void onDragEnter(DragEnterEvent event) {
		viewport.setStyleAttribute("background-color", SVGConstants.CSS_GAINSBORO_VALUE);
		event.stopPropagation();
		event.preventDefault();
	}
	@Override
	public void onDragLeave(DragLeaveEvent event) {
		viewport.setStyleAttribute("background-color", SVGConstants.CSS_BEIGE_VALUE);
		event.stopPropagation();
		event.preventDefault();
	}
	@Override
	public void onDragOver(DragOverEvent event) {
		// Mandatory handler, otherwise the default
		// behavior will kick in and onDrop will never
		// be called
		event.stopPropagation();
		event.preventDefault();
	}
	@Override
	public void onDrop(DropEvent event) {
		GWT.log("onDrop");
		viewport.setStyleAttribute("background-color", SVGConstants.CSS_BEIGE_VALUE);
		FileList files = event.getDataTransfer().<DataTransferExt>cast().getFiles();
		for (File file : files) {
			if ("image/svg+xml".equals(file.getType())) {
				new FileLoadRequest(file).load();
			}
		}
		event.stopPropagation();
		event.preventDefault();
	}
}
