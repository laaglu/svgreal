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

import java.util.Arrays;

import org.vectomatic.svg.edit.client.command.add.AddCircleCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddEllipseCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddLineCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddPathCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddPolygonCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddPolylineCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddRectCommandFactory;

import com.extjs.gxt.ui.client.store.ListStore;

/**
 * Class to list all the command factories available in the application
 * @author laaglu
 */
public class CommandFactories {
	public static ListStore<IFactoryInstantiator<?>> getAllFactoriesStore() {
		ListStore<IFactoryInstantiator<?>> store = new ListStore<IFactoryInstantiator<?>>();
		store.add(Arrays.asList(new IFactoryInstantiator<?>[] {
				AddCircleCommandFactory.INSTANTIATOR,
				AddRectCommandFactory.INSTANTIATOR,
				AddEllipseCommandFactory.INSTANTIATOR,
				AddLineCommandFactory.INSTANTIATOR,
				AddPolylineCommandFactory.INSTANTIATOR,
				AddPolygonCommandFactory.INSTANTIATOR,
				AddPathCommandFactory.INSTANTIATOR,
				EditGeometryCommandFactory.INSTANTIATOR,
				EditTransformCommandFactory.INSTANTIATOR,
				RemoveElementsCommandFactory.INSTANTIATOR,
				EditTitleCommandFactory.INSTANTIATOR,
				ShowPropertiesCommandFactory.INSTANTIATOR,
			}));
		return store;
	}
	
	private static DndCommandFactory dndCommandFactory = null;
	public static DndCommandFactory getDndCommandFactory() {
		if (dndCommandFactory == null) {
			dndCommandFactory = DndCommandFactory.INSTANTIATOR.create();
		}
		return dndCommandFactory;
	}
}
