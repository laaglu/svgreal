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
package org.vectomatic.svg.edit.client.command;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Base class for classes which instantiate command factories.
 * @author laaglu
 * @param <T>
 * The command factory class to instantiate.
 */
@SuppressWarnings("serial")
public abstract class FactoryInstantiatorBase<T extends ICommandFactory> extends BaseModelData implements IFactoryInstantiator<T> {
	protected FactoryInstantiatorBase(String name, String description) {
		set(IFactoryInstantiator.NAME, name);
		set(IFactoryInstantiator.DESCRIPTION, description);
	}

	@Override
	public String getName() {
		return get(IFactoryInstantiator.NAME);
	}

	@Override
	public String getDescription() {
		return get(IFactoryInstantiator.DESCRIPTION);
	}

}
