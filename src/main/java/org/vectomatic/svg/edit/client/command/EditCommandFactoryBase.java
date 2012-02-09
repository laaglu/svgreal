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

import java.util.Map;

import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.StoreEventProcessor;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.google.gwt.core.client.GWT;

/**
 * Base class for command factories which edit an existing SVG element model
 * @author laaglu
 */
public abstract class EditCommandFactoryBase extends CommandFactoryBase implements StoreEventProcessor {
	@Override
	public boolean processStoreEvent(StoreEvent<SVGElementModel> se) {
		GWT.log("EditCommandFactoryBase.processStoreEvent: " + se.getOperation());
		Record record = se.getRecord();
		if (record != null && se.getOperation() != RecordUpdate.COMMIT) {
			Map<String, Object> changes = record.getChanges();
			for(Map.Entry<String, Object> change : changes.entrySet()) {
				GWT.log("Change: " + change.getKey() + " = " + change.getValue());
			}
			SVGElementModel model = (SVGElementModel)record.getModel();
			SVGModel owner = model.getOwner();
			if (owner != null) {
				owner.getCommandStore().addCommand(createCommand(model, changes));
			}
			return true;
		}
		return false;
	}

	protected abstract ICommand createCommand(SVGElementModel model, Map<String, Object> changes);
}
