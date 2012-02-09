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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * Interface to define text constants used in error validation messages.
 * @author laaglu
 */
public interface ValidationConstants extends ConstantsWithLookup {
	public ValidationConstants INSTANCE = GWT.create(ValidationConstants.class);

	/*======================================
	 = Validation warnings
	 =======================================*/

	public String zeroRadius();
	public String zeroWidth();
	public String zeroHeight();
	public String zeroLength();

	/*======================================
	 = Validation errors
	 =======================================*/

	public String negativeRadius();
	public String negativeWidth();
	public String negativeHeight();
	public String imageHref();
	public String noVertices();
	public String noSegments();
	
	public String outOfRangeOpacity();
	public String negativeStrokeWidth();
	public String negativeMiterLimit();
	public String negativeDashOffset();

}
