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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.CommandFactories;
import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.util.Format;

/**
 * Class to represent a tree reordering command
 * @author laaglu
 */
public class MoveCommand extends DndCommandBase {
	/**
	 * The models to move, unsorted
	 */
	protected List<SVGElementModel> models;
	/**
	 * The models to move, sorted
	 */
	protected List<SVGElementModel> sortedModels;
	/**
	 * The parent models of the models to move, sorted
	 */
	protected List<SVGElementModel> parentModels;
	/**
	 * The next non-null siblings of the models to move, sorted, or null if no such siblings exist
	 */
	protected List<SVGElementModel> refModels;
	/**
	 * The transform attributes of the models to move, sorted
	 */
	protected List<String> xforms;
	/**
	 * The target model
	 */
	protected SVGElementModel target;
	/**
	 * The drop gesture
	 */
	protected DropGesture dropGesture;

	public MoveCommand(List<SVGElementModel> models, SVGElementModel target, DropGesture dropGesture) {
		super(CommandFactories.getDndCommandFactory());
		this.target = target;
		this.dropGesture = dropGesture;
		this.models = new ArrayList<SVGElementModel>(models);
		// Sort nodes according to their order in the tree.
		Collections.<SVGElementModel>sort(models, SVGElementModel.getAscendingCompataror());
		this.sortedModels = models;
		Set<SVGElementModel> modelSet = new HashSet<SVGElementModel>(models);
		parentModels = new ArrayList<SVGElementModel>();
		refModels = new ArrayList<SVGElementModel>();
		xforms = new ArrayList<String>();
		for (SVGElementModel model : sortedModels) {
			SVGElementModel parentModel = (SVGElementModel) model.getParent();
			parentModels.add(parentModel);
			// Retrieve the next sibling not in the set of models to delete
			SVGElementModel siblingModel = model;
			while (((siblingModel = siblingModel.getNextSibling()) != null) && modelSet.contains(siblingModel)) {
			}
			refModels.add(siblingModel);
			xforms.add(model.<String>get(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
		}
	}

	@Override
	public String getDescription() {
		String message = null;
		switch(dropGesture) {
			case OnNode:
				message = ModelConstants.INSTANCE.dndReorderCmdIn();
				break;
			case BeforeNode:
				message = ModelConstants.INSTANCE.dndReorderCmdBefore();
				break;
			case AfterNode:
				message = ModelConstants.INSTANCE.dndReorderCmdAfter();
				break;
		}
		return Format.substitute(message, SVGNamedElementModel.getNames(models), target.toString(), target.getOwner().getRoot().get(SVGConstants.SVG_TITLE_TAG));
	}

	@Override
	public void commit() {
		SVGModel owner = target.getOwner();
		SVGElementModel parentModel = null;
		SVGElementModel refModel = null;
		if (dropGesture == DropGesture.OnNode) {
			// target is a folder
			parentModel = target;
		} else {
			// target is a leaf in the same document
			parentModel = (SVGElementModel) target.getParent();
			if (dropGesture == DropGesture.BeforeNode) {
				refModel = target;
			} else if (dropGesture == DropGesture.AfterNode) {
				refModel = target.getNextSibling();
			}
		}
		
		for (SVGElementModel model : models) {
			
			// Update the transform of a model which is going to be
			// attached to a parent model so that the model appears 
			// unchanged to the end-user. This is done by applying
			// a counter transform (the inverse of the transform
			// of the parent) to to nullify its effect
			OMSVGElement element = model.getElementWrapper();
			OMSVGElement parentElement = parentModel.getElementWrapper();
			model.updateTransform(((ISVGTransformable)element).getTransformToElement(parentElement));

			owner.insertBefore(parentModel, model, refModel);
		}		
	}

	@Override
	public void rollback() {
		SVGModel owner = target.getOwner();
		for (int i = 0, size = sortedModels.size(); i < size; i++) {
			SVGElementModel model = sortedModels.get(i);
			SVGElementModel parentModel = parentModels.get(i);
			SVGElementModel refModel = refModels.get(i);
			owner.insertBefore(parentModel, model, refModel);
			String matrix = xforms.get(i);
			if (matrix == null || matrix.length() == 0) {
				model.remove(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
			} else {
				model.set(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, matrix);
			}
		}
	}
	
	@Override
	public BeanModel asModel() {
		return BeanModelLookup.get().getFactory(MoveCommand.class).createModel(this);
	}

}
