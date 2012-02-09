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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.List;

import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.event.DNDEvent;

/**
 * Interface for classes which interpret drag and drop input
 * @author laaglu
 */
public interface IDndHandler {
	/**
	 * Determines the feasibility of creating a command using the
	 * specified source elements as drag sources.
	 * @param event
	 * The drag and drop event
	 * @param sourceElements
	 * The elements being dragged
	 * @return
	 * True if the drag and drop operation can proceed, false if
	 * it should be completely blocked.
	 */
	public boolean isValidSource(DNDEvent event, List<SVGElementModel> sourceElements);
	/**
	 * Determines if dropping on the specified target will result
	 * in a successful command creation.
	 * @param sourceElements
	 * The elements being dragged
	 * @param target
	 * The hovered target
	 * @return
	 * True if the target is authorized, false otherwise
	 */
	public boolean isValidTarget(List<SVGElementModel> sourceElements, SVGElementModel target);
	/**
	 * Creates the commands which represent the drag and drop operation
	 * @param sourceElements
	 * The drag source elements
	 * @param target
	 * The drop target elements
	 * @param dropGesture
	 * The drop gesture
	 */
	public void createCommands(List<SVGElementModel> sourceElements, SVGElementModel target, DropGesture dropGesture);
	/**
	 * Returns the DOM attribute value which trigger the display
	 * of the proper icons in the drag ghost using a CSS rule
	 * @return a DOM attribute value
	 */
	public String getOperationCssAttr();
	/**
	 * Returns the text which appears in the ghost during a
	 * drag and drop operation
	 * @param sourceElements
	 * The elements being dragged
	 * @return
	 */
	public String getMessage(List<SVGElementModel> sourceElements);
	/**
	 * Returns the key code associated with this handler
	 * @return
	 */
	public int getKeyCode();
}
