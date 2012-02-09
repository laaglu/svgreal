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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vectomatic.svg.edit.client.SvgrealApp;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.google.gwt.core.client.GWT;

/**
 * Class to represent the stack of commands which have
 * been applied to an SVG model to edit it.
 */
public class CommandStore extends ListStore<BeanModel> {
	/**
	 * The current command index
	 * Can vary between 0 and N where N is the number of commands
	 * in the stack. 0 means before the least recent command (redo
	 * is possible, undo is impossible). N means after the most recent
	 * command (redo is impossible, undo is possible)
	 */
	private int current;
	
	public CommandStore() {
		super();
		applyFilters(null);
	}
	
	/**
	 * Returns true if the command stack contains a command which can be 
	 * undone false otherwise.
	 * @return true if the command stack contains a command which can be 
	 * undone false otherwise.
	 */
	public boolean canUndo() {
		return (current > 0);
	}
	
	/**
	 * Returns true if the command stack contains a command which can be 
	 * redone false otherwise.
	 * @return true if the command stack contains a command which can be 
	 * redone false otherwise.
	 */
	public boolean canRedo() {
		return (current < snapshot.size());
	}
	
	/**
	 * Undoes the current command
	 */
	public void undo() {
		if (!canUndo()) {
			throw new IllegalStateException("Invalid undo");
		}
		SvgrealApp.getApp().getCommandFactorySelector().suspendAll();
		((ICommand)getUndoCommand().getBean()).rollback();
		current--;
		applyFilters(null);
		SvgrealApp.getApp().getCommandFactorySelector().resumeAll();
	}
	
	/**
	 * Undoes all the commands up to the specified command
	 */
	public void undo(BeanModel command) {
		if (!canUndo()) {
			throw new IllegalStateException("Invalid undo");
		}
		SvgrealApp.getApp().getCommandFactorySelector().suspendAll();
		boolean found = false;
		for (int i = 0; i < current; i++) {
			if (all.get(i) == command) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new IllegalStateException("Invalid undo");
		}
		
		BeanModel currentCommand;
		do {
			currentCommand = getUndoCommand();
			((ICommand)currentCommand.getBean()).rollback();
			current--;
		} while(currentCommand != command);
		
		applyFilters(null);
		SvgrealApp.getApp().getCommandFactorySelector().resumeAll();
	}

	
	/**
	 * Redoes the previously undone command
	 */
	public void redo() {
		if (!canRedo()) {
			throw new IllegalStateException("Invalid redo");
		}
		SvgrealApp.getApp().getCommandFactorySelector().suspendAll();
		((ICommand)getRedoCommand().getBean()).commit();
		current++;
		applyFilters(null);
		SvgrealApp.getApp().getCommandFactorySelector().resumeAll();
	}

	/**
	 * Redoes the previously undone command up to the specified command
	 */
	public void redo(BeanModel command) {
		if (!canRedo()) {
			throw new IllegalStateException("Invalid redo");
		}
		SvgrealApp.getApp().getCommandFactorySelector().suspendAll();
		boolean found = false;
		for (int i = current, size = snapshot.size(); i < size; i++) {
			if (snapshot.get(i) == command) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new IllegalStateException("Invalid redo");
		}
		BeanModel currentCommand;
		do {
			currentCommand = getRedoCommand();
			((ICommand)currentCommand.getBean()).commit();
			current++;
		} while(currentCommand != command);
		applyFilters(null);
		SvgrealApp.getApp().getCommandFactorySelector().resumeAll();
	}

	/**
	 * Returns the command which will be undone if undo is invoked
	 * @return
	 */
	public BeanModel getUndoCommand() {
		if (!canUndo()) {
			return null;
		}
		return snapshot.get(current - 1);
	}
	
	/**
	 * Returns the command which will be redone if undo is invoked
	 * @return
	 */
	public BeanModel getRedoCommand() {
		if (!canRedo()) {
			return null;
		}
		return snapshot.get(current);
	}
	
	/**
	 * Gets all the commands currently in the stack
	 * @return
	 */
	public List<BeanModel> getCommands() {
		return all;
	}
	
	/**
	 * Gets all the commands which can be undone
	 * @return
	 */
	public List<BeanModel> getUndoCommands() {
		if (!canUndo()) {
			return null;
		}
		return snapshot.subList(0, current);
	}

	/**
	 * Gets all the commands which can be redone
	 * @return
	 */
	public List<BeanModel> getRedoCommands() {
		if (!canRedo()) {
			return null;
		}
		return snapshot.subList(current, snapshot.size());
	}

	/**
	 * Adds a new command to the stack
	 * @param command the command to add
	 */
	public void addCommand(ICommand command) {
		GWT.log("CommandStore.addCommand: " + command.getDescription());
		if (current < snapshot.size()) {
			for (int i = snapshot.size() - 1; i >= current; i--) {
				snapshot.remove(i);
			}
		}
        StoreEvent<BeanModel> evt = createStoreEvent();
        BeanModel commandModel = command.asModel();
        List<BeanModel> commandList = Collections.<BeanModel>singletonList(commandModel);
        evt.setModels(commandList);
        if (!fireEvent(BeforeAdd, evt)) {
          return;
        }
        snapshot.add(commandModel);
        all.add(commandModel);
		current = snapshot.size();
        evt = createStoreEvent();
        evt.setModels(commandList);
        evt.setIndex(current - 1);
        fireEvent(Add, evt);
	}	

	@Override
	public void applyFilters(String property) {
	    filterProperty = property;
	    if (!filtersEnabled) {
	    	snapshot = all;
		    filtersEnabled = true;
	    }
		all = filtered = new ArrayList<BeanModel>(snapshot.subList(0, current));
		fireEvent(Filter, createStoreEvent());
	}
	
	@Override
	public void clearFilters() {
		// Filters cannot be cleared as they are part of the
		// way this class work.
	}
}
