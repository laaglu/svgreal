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

import java.util.Stack;

import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.event.CommandFactorySelectorChangeEvent;
import org.vectomatic.svg.edit.client.event.CommandFactorySelectorChangeHandler;
import org.vectomatic.svg.edit.client.event.HasCommandFactorySelectorChangeHandlers;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Class to manage the command factories and their lifecycle.
 * @author laaglu
 */
public class CommandFactorySelector implements HasCommandFactorySelectorChangeHandlers, SelectionChangedProcessor<SVGElementModel> {
	private Stack<ICommandFactory> factoryStack;
	private boolean suspended;
	
	public CommandFactorySelector() {
		factoryStack = new Stack<ICommandFactory>();
		SelectionService.get().addListener(new SelectionChangedProxy<SVGElementModel>(this));
	}
	
	/**
	 * Returns the currently active factory
	 * @return
	 */
	public ICommandFactory getActiveFactory() {
		if (suspended || factoryStack.empty()) {
			return null;
		}
		return factoryStack.peek();
	}
	
	/**
	 * Pops the current factory from the stack.
	 * @return
	 */
	public void popFactory() {
		GWT.log("CommandFactorySelector.popFactory(" + getActiveFactory().getInstantiator().getName() + ")");
		factoryStack.pop();
		fireFactoryStackChangedEvent();
	}
	
	/**
	 * Pushes the factory on the stack.
	 * @return
	 */
	public void pushFactory(ICommandFactory factory) {
		GWT.log("CommandFactorySelector.pushFactory(" + factory.getInstantiator().getName() + ")");
		factoryStack.push(factory);
		fireFactoryStackChangedEvent();
	}
	
	public int size() {
		return factoryStack.size();
	}
	
	public void fireFactoryStackChangedEvent() {
		GWT.log("fireFactoryStackChangedEvent: " + toString());
		fireEvent(new CommandFactorySelectorChangeEvent(getActiveFactory()));
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		VectomaticApp2.getApp().getEventBus().fireEventFromSource(event, this);
		
	}
	@Override
	public HandlerRegistration addCommandFactoryChangeHandler(CommandFactorySelectorChangeHandler handler) {
		return VectomaticApp2.getApp().getEventBus().addHandlerToSource(CommandFactorySelectorChangeEvent.getType(), this, handler);
	}

	public void suspendAll() {
		suspended = true;
	}

	public void resumeAll() {
		suspended = false;
	}
	
	public boolean isSuspended() {
		return suspended;
	}

	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<SVGElementModel> se) {
		GWT.log("CommandFactorySelector.processSelectionChanged(" + se.getSelection() + ")");
		ICommandFactory factory = getActiveFactory();
		if (factory instanceof SelectionChangedProcessor) {
			((SelectionChangedProcessor)factory).processSelectionChanged(se);
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("CommandFactorySelector(suspended=");
		builder.append(suspended);
		builder.append("");
		builder.append(factoryStack);
		builder.append(")");
		return builder.toString();
	}

}
