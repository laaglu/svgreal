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
package org.vectomatic.svg.edit.client.command;

import org.vectomatic.svg.edit.client.VectomaticApp2;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.core.client.GWT;
/**
 * Base class for command factories
 * @author laaglu
 */
public abstract class CommandFactoryBase implements ICommandFactory, BeanModelTag {
	protected String status;
	
	@Override
	public String getStatus() {
		return status;
	}
	
	@Override
	public void start(Object requester) {
		GWT.log("CommandFactoryBase.start(" + requester + ")");
		VectomaticApp2.getApp().getCommandFactorySelector().pushFactory(this);
	}

	@Override
	public void stop() {
		GWT.log("CommandFactoryBase.stop()");
		VectomaticApp2.getApp().getCommandFactorySelector().popFactory();
	}
	
	public void updateStatus(String status) {
		this.status = status;
		VectomaticApp2.getApp().getCommandToolBar().updateStatus();
	}
}
