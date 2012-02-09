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

import java.util.Set;

import org.vectomatic.svg.edit.client.model.ValidationError.Severity;

/**
 * Interface for models which support validation
 * @author laaglu
 */
public interface IValidatable<T> {
	/**
	 * Returns the severity for this model. Null
	 * values mean that the model is valid. 
	 * {@link org.vectomatic.svg.edit.client.model.ValidationError.Severity#WARNING}
	 * means that the model has at least one warning, but no errors.
	 * {@link org.vectomatic.svg.edit.client.model.ValidationError.Severity#ERROR}
	 * means that the model has at least one error.
	 * @return
	 */
	public Severity getSeverity();
	/**
	 * Updates the severity for this model. The severity for
	 * the parent model of this model is updated, recursively.
	 * If the supplied severity is less severe than the 
	 * present severity, the actual severity for this model
	 * will be computed by taking the most severe severity
	 * of all the child model or metadata of this model.
	 * @param severity the severity
	 */
	public void updateSeverity(Severity severity);
	/**
	 * Returns the errors of the specified severity
	 * associated with this model.
	 * @param severity The severity. If null, both errors
	 * and warnings are returned
	 * @return the errors associated with this model.
	 */
	public Set<ValidationError> getErrors(T model, Severity severity);
	
}
