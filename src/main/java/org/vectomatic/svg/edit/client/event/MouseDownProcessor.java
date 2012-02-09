/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.vectomatic.svg.edit.client.event;

import com.google.gwt.event.dom.client.MouseDownEvent;

/**
 * Interface for mousedown event processors
 * @author laaglu
 */
public interface MouseDownProcessor extends EventProcessor {
	/**
	 * Processes a mousedown event
	 * @param e the mousedown event
	 * @return true if the receiver has processed the event,
	 * false if processing should be delegated to another object.
	 */
	public boolean processMouseDown(MouseDownEvent event);
}
