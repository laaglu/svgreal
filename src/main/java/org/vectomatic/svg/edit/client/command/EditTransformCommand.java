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

import java.util.Map;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGTransform;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.Transformation;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.util.Format;

/**
 * Command to represent changes in the svg transform attribute of an element.
 */
public class EditTransformCommand extends GenericEditCommand {
	private OMSVGTransform told, tnew, rold, rnew, sold, snew;
	public EditTransformCommand(CommandFactoryBase factory, SVGElementModel model, Map<String, Object> oldValues) {
		super(factory, model, oldValues, ModelConstants.INSTANCE.transformCmd());
		OMSVGElement element = model.getElementWrapper();
		element.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, (String) oldValues.get(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
		Transformation xform0 = Transformation.decompose((ISVGTransformable)element);
		told = xform0.getT();
		rold = xform0.getR();
		sold = xform0.getS();
		element.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, (String) newValues.get(SVGConstants.SVG_TRANSFORM_ATTRIBUTE));
		Transformation xform1 = Transformation.decompose((ISVGTransformable)element);
		tnew = xform1.getT();
		rnew = xform1.getR();
		snew = xform1.getS();
	}
	
	@Override
	public String getDescription() {
		StringBuilder changes = new StringBuilder();
		if (!sameTranslation(told, tnew)) {
			changes.append(tnew.getDescription());
		}
		if (!sameRotation(rold, rnew)) {
			if (changes.length() > 0) {
				changes.append("; ");
			}
			changes.append(rnew.getDescription());
		}
		if (!sameScaling(sold, snew)) {
			if (changes.length() > 0) {
				changes.append("; ");
			}
			changes.append(snew.getDescription());
		}
		return Format.substitute(ModelConstants.INSTANCE.transformCmd(), model.get(SVGConstants.SVG_TITLE_TAG), changes);
	}
	public static final boolean sameRotation(OMSVGTransform t1, OMSVGTransform t2) {
		return sameValue(t1.getAngle(), t2.getAngle());
	}
	public static final boolean sameScaling(OMSVGTransform t1, OMSVGTransform t2) {
		return sameValue(t1.getMatrix().getA(), t2.getMatrix().getA()) && sameValue(t1.getMatrix().getD(), t2.getMatrix().getD());
	}
	public static final boolean sameTranslation(OMSVGTransform t1, OMSVGTransform t2) {
		return sameValue(t1.getMatrix().getE(), t2.getMatrix().getE()) && sameValue(t1.getMatrix().getF(), t2.getMatrix().getF());
	}
	public static final boolean sameValue(float f1, float f2) {
		return Math.abs(f1 - f2) < 1e-03f;
	}
	
	@Override
	public BeanModel asModel() {
		return BeanModelLookup.get().getFactory(EditTransformCommand.class).createModel(this);
	}

}
