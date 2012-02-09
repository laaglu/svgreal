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

import com.extjs.gxt.ui.client.util.Format;

/**
 * Class to represent a validation error or warning.
 * @author laaglu
 */
public class ValidationError {
	/**
	 * Enum to represent the severity of an error
	 */
	public enum Severity {
		WARNING,
		ERROR
	}
	/**
	 * The error severity
	 */
	protected Severity severity;
	/**
	 * The name of the error in the 
	 * {@link com.google.gwt.i18n.client.ConstantsWithLookup} interface
	 */
	protected String id;
	/**
	 * Args of the error message
	 */
	protected Object[] args;
	/**
	 * Constructor
	 * @param severity
	 * The error severity
	 * @param id
	 * The name of the error in the 
	 * {@link com.google.gwt.i18n.client.ConstantsWithLookup} interface
	 */
	public ValidationError(Severity severity, String id) {
		this(severity, id, null);
	}
	/**
	 * Constructor
	 * @param severity
	 * The error severity
	 * @param id
	 * The name of the error in the 
	 * {@link com.google.gwt.i18n.client.ConstantsWithLookup} interface
	 * @param args
	 * Args of the error message
	 */
	public ValidationError(Severity severity, String id, Object[] args) {
		this.severity = severity;
		this.id = id;
		this.args = args;
	}
	/**
	 * Returns the error severity
	 * @return
	 * The error severity
	 */
	public Severity getSeverity() {
		return severity;
	}
	/**
	 * Returns the name of the error in the 
	 * {@link com.google.gwt.i18n.client.ConstantsWithLookup} interface
	 * @return
	 * The name of the error in the 
	 * {@link com.google.gwt.i18n.client.ConstantsWithLookup} interface
	 */
	public String getId() {
		return id;
	}
	/**
	 * Returns a localized error message for this error.
	 * @return a localized error message for this error.
	 */
	public String getMessage() {
		String message = ValidationConstants.INSTANCE.getString(id);
		return (args == null) ? message : Format.substitute(message, args);
	}
}

