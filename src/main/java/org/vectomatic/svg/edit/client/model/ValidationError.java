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
	 * The error message before arguments substitution
	 */
	protected String message;
	/**
	 * Args of the error message
	 */
	protected Object[] args;
	/**
	 * Constructor
	 * @param severity
	 * The error severity
	 * @param message
	 * The error message before arguments substitution
	 */
	public ValidationError(Severity severity, String message) {
		this(severity, message, null);
	}
	/**
	 * Constructor
	 * @param severity
	 * The error severity
	 * @param message
	 * The error message before arguments substitution
	 * @param args
	 * Args of the error message
	 */
	public ValidationError(Severity severity, String message, Object[] args) {
		this.severity = severity;
		this.message = message;
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
	 * Returns a localized error message for this error.
	 * @return a localized error message for this error.
	 */
	public String getMessage() {
		return (args == null) ? message : Format.substitute(message, args);
	}
}

