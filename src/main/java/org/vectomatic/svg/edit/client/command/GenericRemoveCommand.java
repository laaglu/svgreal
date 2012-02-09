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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.util.Format;

/**
 * Class to represent the removal of one or many elements from
 * the SVG model.
 * @author laaglu
 */
public class GenericRemoveCommand extends CommandBase {
	/**
	 * The models to remove
	 */
	protected List<SVGElementModel> models;
	/**
	 * The parent models of the models to remove
	 */
	protected List<SVGElementModel> parentModels;
	/**
	 * The next non-null siblings of the models to remove, or null if no such siblings exist
	 */
	protected List<SVGElementModel> refModels;
	/**
	 * The owner of the models to remove
	 */
	protected SVGModel owner;
	/**
	 * The command description
	 */
	protected String description;

	public GenericRemoveCommand(ICommandFactory factory, List<SVGElementModel> models, String description) {
		super(factory);
		// Sort nodes according to their order in the tree.
		Collections.<SVGElementModel>sort(models, SVGElementModel.getAscendingCompataror());
		Set<SVGElementModel> modelSet = new HashSet<SVGElementModel>(models);
		this.models = models;
		this.description = description;
		parentModels = new ArrayList<SVGElementModel>();
		refModels = new ArrayList<SVGElementModel>();
		for (SVGElementModel model : models) {
			SVGModel owner = model.getOwner();
			if (this.owner == null) {
				this.owner = owner;
			} else {
				assert this.owner == owner : "Attempt to remove models from heterogeneous owners: " + this.owner + ", " + owner;
			}
			SVGElementModel parentModel = (SVGElementModel) model.getParent();
			parentModels.add(parentModel);
			// Retrieve the next sibling not in the set of models to delete
			SVGElementModel siblingModel = model;
			while (((siblingModel = siblingModel.getNextSibling()) != null) && modelSet.contains(siblingModel)) {
			}
			refModels.add(siblingModel);
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void commit() {
		for (int i = models.size() - 1; i >= 0; i--) {
			SVGElementModel model = models.get(i);
			owner.remove(model);
		}
	}

	@Override
	public void rollback() {
		for (int i = 0, size = models.size(); i < size; i++) {
			SVGElementModel model = models.get(i);
			SVGElementModel parentModel = parentModels.get(i);
			SVGElementModel refModel = refModels.get(i);
			owner.insertBefore(parentModel, model, refModel);
		}
	}

	@Override
	public BeanModel asModel() {
		return BeanModelLookup.get().getFactory(GenericRemoveCommand.class).createModel(this);
	}

}
