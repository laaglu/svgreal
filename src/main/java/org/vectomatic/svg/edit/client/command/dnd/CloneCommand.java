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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.ArrayList;
import java.util.List;

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
 * Class to represent a tree cloning command
 * @author laaglu
 */
public class CloneCommand extends DndCommandBase {
	/**
	 * The models to copy, unsorted
	 */
	protected List<SVGElementModel> models;
	/**
	 * The cloned models, unsorted
	 */
	protected List<SVGElementModel> clones;
	/**
	 * The target model
	 */
	protected SVGElementModel target;
	/**
	 * The drop gesture
	 */
	protected DropGesture dropGesture;
	
	public CloneCommand(List<SVGElementModel> models, SVGElementModel target, DropGesture dropGesture) {
		super(CommandFactories.getDndCommandFactory());
		this.models = models;
		this.target = target;
		this.dropGesture = dropGesture;
		for (SVGElementModel model : models) {
			assert model.getOwner() == target.getOwner() : "Attempt to copy models from heterogeneous owners: " + model.getOwner() + ", " + target.getOwner();
		}
	}

	@Override
	public String getDescription() {
		String message = null;
		switch(dropGesture) {
			case OnNode:
				message = ModelConstants.INSTANCE.dndCloneCmdIn();
				break;
			case BeforeNode:
				message = ModelConstants.INSTANCE.dndCloneCmdBefore();
				break;
			case AfterNode:
				message = ModelConstants.INSTANCE.dndCloneCmdAfter();
				break;
		}
		return Format.substitute(message, SVGNamedElementModel.getNames(models), target.toString());
	}

	@Override
	public void commit() {
		SVGModel owner = target.getOwner();
		if (clones == null) {
			clones = new ArrayList<SVGElementModel>();
			for (SVGElementModel model : models) {
				SVGElementModel clone = owner.clone(model, Format.substitute(ModelConstants.INSTANCE.copyOf(), model.get(SVGConstants.SVG_TITLE_TAG)));
				clones.add(clone);
			}
		}
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
		
		OMSVGElement parentElement = parentModel.getElementWrapper();
		for (int i = 0, size = models.size(); i < size; i++) {
			SVGElementModel model = models.get(i);
			SVGElementModel clone = clones.get(i);

			// Update the transform of a model which is going to be
			// attached to a parent model so that the model appears 
			// unchanged to the end-user. This is done by applying
			// a counter transform (the inverse of the transform
			// of the parent) to to nullify its effect
			OMSVGElement element = model.getElementWrapper();
			clone.updateTransform(((ISVGTransformable)element).getTransformToElement(parentElement));

			owner.insertBefore(parentModel, clone, refModel);
		}

	}

	@Override
	public void rollback() {
		SVGModel owner = target.getOwner();
		for (SVGElementModel clone : clones) {
			owner.remove(clone);
		}
	}

	@Override
	public BeanModel asModel() {
		return BeanModelLookup.get().getFactory(CloneCommand.class).createModel(this);
	}

}
