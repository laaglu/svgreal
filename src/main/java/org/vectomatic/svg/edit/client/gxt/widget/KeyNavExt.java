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
/*
 * Ext GWT 2.2.4 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.vectomatic.svg.edit.client.gxt.widget;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.dom.client.Element;

/**
 * Provides a convenient wrapper for normalized keyboard navigation. Provides an
 * easy way to implement custom navigation schemes for any UI component.
 */
public class KeyNavExt<E extends ComponentEvent> implements Listener<E> {
	public static final int KEY_F2 = 113;
	private static EventType keyPressEvent;
	private Component component;

	static {
		if (GXT.isIE || GXT.isGecko || GXT.isWebKit) {
			keyPressEvent = Events.OnKeyDown;
		} else {
			keyPressEvent = Events.OnKeyPress;
		}
	}

	/**
	 * Returns the key event type.
	 * 
	 * @return the key event type
	 */
	public static EventType getKeyEvent() {
		return keyPressEvent;
	}

	/**
	 * Creates a new KeyNav without a target component. Events must be passed to
	 * the {@link #handleEvent(BaseEvent)} method.
	 */
	public KeyNavExt() {

	}

	/**
	 * Creates a new key nav for the specified target. The KeyNav will listen
	 * for the key events.
	 * @param target the target component
	 */
	public KeyNavExt(Component target) {
		bind(target);
	}

	/**
	 * Binds the key nav to the component.
	 * @param target the target component
	 */
	public void bind(final Component target) {
		if (this.component != null) {
			this.component.removeListener(keyPressEvent, this);
			this.component.removeListener(Events.OnKeyUp, this);
		}
		if (target != null) {
			target.addListener(keyPressEvent, this);
			target.addListener(Events.OnKeyUp, this);
			target.sinkEvents(keyPressEvent.getEventCode());
			target.sinkEvents(Events.OnKeyUp.getEventCode());
		}
		this.component = target;
	}

	/**
	 * Returns the target component.
	 * 
	 * @return the target component
	 */
	public Component getComponent() {
		return component;
	}

	@SuppressWarnings("unchecked")
	public void handleEvent(ComponentEvent ce) {
		EventType type = ce.getType();
		if (type == keyPressEvent || type == Events.OnKeyUp) {
			if (component.getElement() != (Element) ce.getEvent().getCurrentEventTarget().cast()) {
				return;
			}

			E e = (E) ce;

			if (type == keyPressEvent) {
				onKeyPress(e);
			} else {
				onKeyUp(e);
			}
		}
	}

	public void onKeyPress(E ce) {
	}

	public void onKeyUp(E ce) {
	}
}
