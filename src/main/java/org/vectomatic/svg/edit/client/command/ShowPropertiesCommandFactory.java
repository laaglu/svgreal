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

import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.model.ModelConstants;

/**
 * Command factory to trigger the display of the property inspector
 * @author laaglu
 */
public class ShowPropertiesCommandFactory extends CommandFactoryBase {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<ShowPropertiesCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<ShowPropertiesCommandFactory>(ModelConstants.INSTANCE.showPropertiesCmdFactory(), ModelConstants.INSTANCE.showPropertiesCmdFactoryDesc()) {
		@Override
		public ShowPropertiesCommandFactory create() {
			return new ShowPropertiesCommandFactory();
		}
	};
	
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	@Override
	public void start(Object requester) {
		super.start(" + requester + ");
		SvgrealApp app = SvgrealApp.getApp();
		app.inspector();
		stop();
	}
}
