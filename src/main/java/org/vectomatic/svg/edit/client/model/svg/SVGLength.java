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
package org.vectomatic.svg.edit.client.model.svg;

import org.vectomatic.dom.svg.OMSVGLength;

import com.google.gwt.dom.client.Style;

/**
 * Value type to represent an SVG length. An SVG length has
 * a unit and a numerical value.
 * @author laaglu
 */
public class SVGLength {
	private Style.Unit unit;
	private float value;
	private OMSVGLength length;
	public SVGLength(OMSVGLength value) {
		this.value = value.getValueInSpecifiedUnits();
		this.unit = value.getUnit();
		this.length = value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public float getValue() {
		return value;
	}
	public Style.Unit getUnit() {
		return unit;
	}
	public void setUnit(Style.Unit unit) {
		// backup length
		Style.Unit u0 = length.getUnit();
		float v0 = length.getValueInSpecifiedUnits();
//		GWT.log("L1=" + length.getValueInSpecifiedUnits() + " " + length.getUnitType());
		
		// make conversion
		length.newValueSpecifiedUnits(this.unit, this.value);
//		GWT.log("L2=" + length.getValueInSpecifiedUnits() + " " + length.getUnitType());
		length.convertToSpecifiedUnits(unit);
//		GWT.log("L3=" + length.getValueInSpecifiedUnits() + " " + length.getUnitType());
		
		// update values
		this.value = length.getValueInSpecifiedUnits();
		this.unit = unit;
		
		// restore length
		length.newValueSpecifiedUnits(u0, v0);
//		GWT.log("L4=" + length.getValueInSpecifiedUnits() + " " + length.getUnitType());
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SVGLength) {
			SVGLength l = (SVGLength)obj;
			return value == l.value && unit == l.unit;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int)value + 37 * (unit == null ? 0 : unit.ordinal());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(value);
		if (unit != null) {
			builder.append(" ");
			builder.append(unit);
		}
		return builder.toString();
	}
}
