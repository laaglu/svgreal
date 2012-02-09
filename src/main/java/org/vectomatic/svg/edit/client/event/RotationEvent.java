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
package org.vectomatic.svg.edit.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event emitted when a rotation occurs
 * @author laaglu
 */
public class RotationEvent extends GwtEvent<RotationHandler> {
	/**
	 * Handler type.
	 */
	private static Type<RotationHandler> TYPE;

	private final float angleDeg;

	public RotationEvent(float angleDeg) {
		this.angleDeg = angleDeg;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RotationHandler> getAssociatedType() {
		if (TYPE == null) {
			TYPE = new Type<RotationHandler>();
		}
		return TYPE;
	}

	/**
	 * Ensures the existence of the handler hook and then returns it.
	 * 
	 * @return returns a handler hook
	 */
	public static Type<RotationHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<RotationHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(RotationHandler handler) {
		handler.onRotate(this);
	}

	public float getAngle() {
		return angleDeg;
	}

}
