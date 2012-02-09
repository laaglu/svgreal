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
package org.vectomatic.svg.edit.client.model;

import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.svg.edit.client.model.svg.DashArray;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

/**
 * Interface to convert a data type into another.
 * @param S
 * The source data type
 * @param D
 * The destination data type
 * @author laaglu
 */
public interface IConverter<S,D> {
	public static final IConverter<String, String> NOP_CONVERTER = new IConverter<String, String> () {
		@Override
		public String sourceToDest(String source) {
			return source;
		}
		@Override
		public String destToSource(String dest) {
			return dest;
		}
	};
	public static final IConverter<String, Float> STRING_FLOAT_CONVERTER = new IConverter<String, Float> () {
		@Override
		public Float sourceToDest(String source) {
			return (source != null && source.length() > 0) ? Float.parseFloat(source) : null;
		}
		@Override
		public String destToSource(Float dest) {
			return dest != null ? Float.toString(dest) : null;
		}
	};
	public static final IConverter<String, Double> STRING_DOUBLE_CONVERTER = new IConverter<String, Double> () {
		@Override
		public Double sourceToDest(String source) {
			return (source != null && source.length() > 0) ? Double.parseDouble(source) : null;
		}
		@Override
		public String destToSource(Double dest) {
			return dest != null ? Double.toString(dest) : null;
		}
	};
	public static final IConverter<String, OMSVGPaint> STRING_SVGPAINT_CONVERTER = new IConverter<String, OMSVGPaint> () {
		@Override
		public OMSVGPaint sourceToDest(String source) {
			return (source != null && source.length() > 0) ? OMSVGParser.parsePaint(source) : null;
		}
		@Override
		public String destToSource(OMSVGPaint dest) {
			return dest != null ? dest.getCssText() : null;
		}
	};
	public static final IConverter<String, DashArray> STRING_DASHARRAY_CONVERTER = new IConverter<String, DashArray> () {
		@Override
		public DashArray sourceToDest(String source) {
			return (source != null && source.length() > 0) ? DashArray.parse(source) : null;
		}

		@Override
		public String destToSource(DashArray dest) {
			return dest != null ? dest.toString() : null;
		}
		
	};
	public static final IConverter<String, SVGLength> STRING_LENGTH_CONVERTER = new IConverter<String, SVGLength> () {
		private OMSVGSVGElement svg = OMSVGParser.currentDocument().createSVGSVGElement();
		@Override
		public SVGLength sourceToDest(String source) {
			if (source != null) {
				OMSVGLength l = svg.createSVGLength();
				l.setValueAsString(source);
				return new SVGLength(l);
			}
			return null;
		}

		@Override
		public String destToSource(SVGLength dest) {
			return dest != null ? dest.toString() : null;
		}
		
	};
	public D sourceToDest(S source);
	public S destToSource(D dest);
}
