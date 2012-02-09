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

import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.CommandFactories;
import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

/**
 * Class to represent a svg model to svg model command
 * @author laaglu
 */
public class CopyCommand extends DndCommandBase {
//	public enum CopyRatio {
//		/**
//		 * The original dimensions of the source element are preserved,
//		 * which can make it appear either very large or very small
//		 * if the source and destination models have widely differing
//		 * coordinate systems.
//		 */
//		SourceSize,
//		/**
//		 * The size of the source element is altered so that
//		 * it appears to have the same size as the source element
//		 * in the destination model when the destination model
//		 * is not zoomed.
//		 */
//		DestinationSize,
//		/**
//		 * The size of the copied element is altered so that
//		 * it appears to have the same size as the source element
//		 * at the current zoom level of the destination model
//		 * and the current zoom level of the source model 
//		 */
//		VisibleSize
//	}
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
//	/**
//	 * The copy ratio
//	 */
//	protected CopyRatio copyRatio;
	/**
	 * The command description
	 */
	protected String description;

	public CopyCommand(List<SVGElementModel> models, SVGElementModel target, DropGesture dropGesture, String description) {
		super(CommandFactories.getDndCommandFactory());
		this.models = models;
		this.target = target;
		this.dropGesture = dropGesture;
		this.description = description;
//		copyRatio = CopyRatio.SourceSize;
	}

	@Override
	public String getDescription() {
		return description;
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
		
		if (clones == null) {
			ISVGTransformable parentElement = (ISVGTransformable)parentModel.getElementWrapper();
			OMSVGMatrix parentCTM = parentElement.getCTM();
			clones = new ArrayList<SVGElementModel>();
			for (SVGElementModel model : models) {
				SVGElementModel clone = owner.clone(model, model.<String>get(SVGConstants.SVG_TITLE_TAG));
				
				// Update the transform of a model which is going to be
				// attached to a parent model so that the model appears 
				// unchanged to the end-user. This is done by applying
				// a counter transform (the inverse of the transform
				// of the parent) to to nullify its effect
				ISVGTransformable element = (ISVGTransformable)model.getElementWrapper();
				OMSVGMatrix elementCTM = element.getCTM();
				clone.updateTransform(parentCTM.inverse().multiply(elementCTM));
				clones.add(clone);
			}
		}
		
		for (SVGElementModel clone : clones) {
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
		return BeanModelLookup.get().getFactory(CopyCommand.class).createModel(this);
	}

}
