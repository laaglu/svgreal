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

import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.util.Format;

/**
 * Generic command class to record addition of new SVG elements
 * to the model.
 * @author laaglu
 */
public class GenericAddCommand extends CommandBase {
	protected SVGElementModel model;
	protected SVGElementModel parentModel;
	protected String name;
	protected SVGModel owner;

	public GenericAddCommand(CommandFactoryBase factory, SVGElementModel model) {
		super(factory);
		this.model = model;
		owner = model.getOwner();
		parentModel = (SVGElementModel)model.getParent();
		name = model.<String>get(SVGConstants.SVG_TITLE_TAG);
	}

	@Override
	public String getDescription() {
		return Format.substitute(ModelConstants.INSTANCE.addCmd(), name);
	}

	@Override
	public void commit() {
		owner.add(parentModel, model);
	}

	@Override
	public void rollback() {
		owner.remove(model);
	}

	@Override
	public BeanModel asModel() {
		return BeanModelLookup.get().getFactory(GenericAddCommand.class).createModel(this);
	}
}
