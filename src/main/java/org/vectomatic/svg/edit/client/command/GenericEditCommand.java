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

import java.util.HashMap;
import java.util.Map;

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.util.Format;

/**
 * Generic class to record the edition of a property of an
 * svg element.
 * @author laaglu
 */
public class GenericEditCommand extends CommandBase {
	protected SVGElementModel model;
	protected Map<String, Object> oldValues;
	protected Map<String, Object> newValues;
	protected String description;
	
	public GenericEditCommand(CommandFactoryBase factory, SVGElementModel model, Map<String, Object> oldValues, String description) {
		super(factory);
		this.model = model;
		this.oldValues = oldValues;
		this.description = description;
		newValues = new HashMap<String, Object>();
		for (String attrName : oldValues.keySet()) {
			newValues.put(attrName, model.get(attrName));
		}
	}
	
	@Override
	public String getDescription() {
		String changes = newValues.toString();
		return Format.substitute(description, model.get(SVGConstants.SVG_TITLE_TAG), changes.substring(1, changes.length() - 1));
	}
	
	@Override
	public void commit() {
		changeAttributes(newValues);
	}

	@Override
	public void rollback() {
		changeAttributes(oldValues);
	}
	
	private void changeAttributes(Map<String, Object> values) {
		Record record = model.getRecord();
		record.beginEdit();
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			record.set(entry.getKey(), entry.getValue());
		}
		record.endEdit();
		record.commit(false);
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	@Override
	public BeanModel asModel() {
		return BeanModelLookup.get().getFactory(GenericEditCommand.class).createModel(this);
	}

}
