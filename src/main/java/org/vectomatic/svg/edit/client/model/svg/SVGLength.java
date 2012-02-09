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
package org.vectomatic.svg.edit.client.model.svg;

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.svg.edit.client.model.IValidator;
import org.vectomatic.svg.edit.client.model.ValidationConstants;
import org.vectomatic.svg.edit.client.model.ValidationError;
import org.vectomatic.svg.edit.client.model.ValidationError.Severity;

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
			builder.append(unit);
		}
		return builder.toString();
	}
	
	///////////////////////////////////////////////////
	// Length validators
	///////////////////////////////////////////////////
	
	public static IValidator<SVGLength, SVGElement> RADIUS_VALIDATOR = new IValidator<SVGLength, SVGElement>() {
		final ValidationError zeroRadius = new ValidationError(Severity.WARNING, ValidationConstants.INSTANCE.zeroRadius());
		final ValidationError negativeRadius = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.negativeRadius());
		@Override
		public ValidationError validate(SVGElement model, SVGLength value) {
			float f = value.getValue();
			if (f == 0) {
				return zeroRadius;
			}
			if (f < 0) {
				return negativeRadius;
			}
			return null;
		}
	};

	public static IValidator<SVGLength, SVGElement> NEGATIVE_RADIUS_VALIDATOR = new IValidator<SVGLength, SVGElement>() {
		final ValidationError negativeRadius = new ValidationError(Severity.WARNING, ValidationConstants.INSTANCE.negativeRadius());
		@Override
		public ValidationError validate(SVGElement model, SVGLength value) {
			if (value.getValue() < 0) {
				return negativeRadius;
			}
			return null;
		}
	};

	public static IValidator<SVGLength, SVGElement> WIDTH_VALIDATOR = new IValidator<SVGLength, SVGElement>() {
		final ValidationError zeroWidth = new ValidationError(Severity.WARNING, ValidationConstants.INSTANCE.zeroWidth());
		final ValidationError negativeWidth = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.negativeWidth());
		@Override
		public ValidationError validate(SVGElement model, SVGLength value) {
			float f = value.getValue();
			if (f == 0) {
				return zeroWidth;
			}
			if (f < 0) {
				return negativeWidth;
			}
			return null;
		}
	};

	public static IValidator<SVGLength, SVGElement> HEIGHT_VALIDATOR = new IValidator<SVGLength, SVGElement>() {
		final ValidationError zeroHeight = new ValidationError(Severity.WARNING, ValidationConstants.INSTANCE.zeroHeight());
		final ValidationError negativeHeight = new ValidationError(Severity.ERROR, ValidationConstants.INSTANCE.negativeHeight());
		@Override
		public ValidationError validate(SVGElement model, SVGLength value) {
			float f = value.getValue();
			if (f == 0) {
				return zeroHeight;
			}
			if (f < 0) {
				return negativeHeight;
			}
			return null;
		}
	};
}
