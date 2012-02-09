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
package org.vectomatic.svg.edit.client.inspector;

import org.vectomatic.svg.edit.client.model.AbstractModel;

import com.extjs.gxt.ui.client.widget.Component;

/**
 * Interface for inspector sections. An inspector section
 * is dedicated to editing one category of a metamodel.
 * @author laaglu
 * @param <M>
 * The metamodel being edited
 */
public interface IInspectorSection<M extends AbstractModel<?>> {
	public Component getPanel();
	public void bind(M model);
	public void unbind();
}
