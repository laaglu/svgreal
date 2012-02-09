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

import org.vectomatic.dom.svg.OMCSSPrimitiveValue;
import org.vectomatic.dom.svg.OMCSSValueList;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.impl.DashArrayParser;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Class to represent a stroke-dasharray value
 * @author laaglu
 */
@SuppressWarnings("serial")
public class DashArray extends BaseModel implements Comparable<DashArray> {
	
	public static class Dash extends BaseModel {
		public static final String VALUE_PROPERTY = "value";
		public static final String UNIT_PROPERTY = "unit";
		public static final String CSS_TEXT_PROPERTY = "cssText";
		
		public Dash(OMCSSPrimitiveValue primitiveValue) {
			short primitiveType = primitiveValue.getCssValueType();
			set(VALUE_PROPERTY, primitiveValue.getFloatValue(primitiveType));
			set(UNIT_PROPERTY, codeToUnit(primitiveType));
		}
		
//		@Override
//		public int hashCode() {
//			return toString().hashCode();
//		}
//		
//		@Override
//		public boolean equals(Object o) {
//			if (o instanceof Dash) {
//				return toString().equals(o.toString());
//			}
//			return false;
//		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(get(VALUE_PROPERTY));
			Unit unit = get(UNIT_PROPERTY);
			if (unit != null) {
				builder.append(unit);
			}
			return builder.toString();
		}

		private static final Unit codeToUnit(short unit) {
			switch (unit) {
			case OMSVGLength.SVG_LENGTHTYPE_NUMBER:
				return null;
			case OMSVGLength.SVG_LENGTHTYPE_PERCENTAGE:
				return com.google.gwt.dom.client.Style.Unit.PCT;
			case OMSVGLength.SVG_LENGTHTYPE_EMS:
				return com.google.gwt.dom.client.Style.Unit.EM;
			case OMSVGLength.SVG_LENGTHTYPE_EXS:
				return com.google.gwt.dom.client.Style.Unit.EX;
			case OMSVGLength.SVG_LENGTHTYPE_PX:
				return com.google.gwt.dom.client.Style.Unit.PX;
			case OMSVGLength.SVG_LENGTHTYPE_CM:
				return com.google.gwt.dom.client.Style.Unit.CM;
			case OMSVGLength.SVG_LENGTHTYPE_MM:
				return com.google.gwt.dom.client.Style.Unit.MM;
			case OMSVGLength.SVG_LENGTHTYPE_IN:
				return com.google.gwt.dom.client.Style.Unit.IN;
			case OMSVGLength.SVG_LENGTHTYPE_PT:
				return com.google.gwt.dom.client.Style.Unit.PT;
			case OMSVGLength.SVG_LENGTHTYPE_PC:
				return com.google.gwt.dom.client.Style.Unit.PC;
			}
			throw new IllegalStateException("Unsupported unit conversion");
		}
	}
	
	protected ListStore<Dash> store;
	public static final String STORE_PROPERTY = "store";

	protected DashArray() {
		store = new ListStore<Dash>();
		set(STORE_PROPERTY, store);
	}
	
	/**
	 * Creates a new dasharray from a CSS text value
	 * @param cssText
	 * a CSS text value
	 * @return
	 * a new dasharray
	 */
	public static DashArray parse(String cssText) {
		DashArray dashArray = new DashArray();
		OMCSSValueList valueList = DashArrayParser.INSTANCE.parse(cssText);
		for (int i = 0, size = valueList.getLength(); i < size; i++) {
			OMCSSPrimitiveValue value = (OMCSSPrimitiveValue) valueList.getItem(i);
			Dash dash = new Dash(value);
			dashArray.store.add(dash);
		}
		return dashArray;
	}
	
	/**
	 * Converts the dasharray to a CSS text value
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0, count = store.getCount(); i < count; i++) {
			if (i > 0) {
				builder.append(",");
			}
			builder.append(store.getAt(i));
		}
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		int count = store.getCount();
		int hash = 0;
		for (int i = 0; i < count; i++) {
			hash += store.getAt(i).hashCode();
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DashArray) {
			DashArray dashArray = (DashArray)o;
			int count = store.getCount();
			if (count == dashArray.store.getCount()) {
				for (int i = 0; i < count; i++) {
					// TODO better implementation not case sensitive and precision-sensitive
					if (!store.getAt(i).toString().equals(dashArray.store.getAt(i).toString())) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Compare two dash arrays.
	 */
	@Override
	public int compareTo(DashArray o) {
		return toString().compareTo(o.toString());
	}
}
