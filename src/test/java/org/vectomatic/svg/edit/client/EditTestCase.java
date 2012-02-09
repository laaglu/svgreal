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
package org.vectomatic.svg.edit.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Random;

public class EditTestCase extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "org.vectomatic.svg.edit.vectomatic2";
	}


	public static void createTree(SVGModel owner, SVGElementModel pairs[][]) {
		for (int i = 0; i < pairs.length; i++) {
			SVGElementModel[] pair = pairs[i];
			owner.insertBefore(pair[0], pair[1], null);
			for (int j = 0; j <= i; j++) {
				assertEquals("Corrupted after insertion of {" + pair[0] + ", " + pair[1] + "}", pairs[j][0], pairs[j][1].getParent());
			}
		}
	}
	
	public Map<SVGElementModel, OMSVGMatrix> randomTreeTranform(SVGModel owner) {
		OMSVGSVGElement svg = owner.getSvgElement();
		Map<SVGElementModel, OMSVGMatrix> modelToTransform = new HashMap<SVGElementModel, OMSVGMatrix>();
		List<ModelData> queue = new ArrayList<ModelData>();
		queue.add(owner.getRoot());
		while (!queue.isEmpty()) {
			SVGElementModel model = (SVGElementModel) queue.remove(0);
			String xform = randomTransform();
			model.set(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, xform);
			ISVGTransformable g = (ISVGTransformable)OMNode.convert(model.getElement());
			modelToTransform.put(model, svg.createSVGMatrix(g.getScreenCTM()));
			queue.addAll(model.getChildren());
		}
		return modelToTransform;
	}
	
	public static float compareMatrix(OMSVGMatrix m1, OMSVGMatrix m2) {
		return (m1.getA() - m2.getA()) * (m1.getA() - m2.getA())
		+ (m1.getB() - m2.getB()) * (m1.getB() - m2.getB())
		+ (m1.getC() - m2.getC()) * (m1.getC() - m2.getC())
		+ (m1.getD() - m2.getD()) * (m1.getD() - m2.getD())
		+ (m1.getE() - m2.getE()) * (m1.getE() - m2.getE())
		+ (m1.getF() - m2.getF()) * (m1.getF() - m2.getF());
	}
	
	public void assertTransformsUnchanged(Map<SVGElementModel, OMSVGMatrix> xforms) {
		for(Entry<SVGElementModel, OMSVGMatrix> entry : xforms.entrySet()) {
			OMSVGMatrix m1 = entry.getValue();
			ISVGTransformable g = (ISVGTransformable)OMNode.convert(entry.getKey().getElement());
			OMSVGMatrix m2 = g.getScreenCTM();
			if (compareMatrix(m1, m2) > 0.1f) {
				StringBuilder builder = new StringBuilder();
				builder.append(m1.getA());
				builder.append(" | ");
				builder.append(m2.getA());
				builder.append(", ");
				builder.append(m1.getB());
				builder.append(" | ");
				builder.append(m2.getB());
				builder.append(", ");
				builder.append(m1.getC());
				builder.append(" | ");
				builder.append(m2.getC());
				builder.append(", ");
				builder.append(m1.getD());
				builder.append(" | ");
				builder.append(m2.getD());
				builder.append(", ");
				builder.append(m1.getE());
				builder.append(" | ");
				builder.append(m2.getE());
				builder.append(", ");
				builder.append(m1.getF());
				builder.append(" | ");
				builder.append(m2.getF());
				fail("Matrices differ for " + entry.getKey() + ": " + builder.toString());
			}
		}
	}

	public static SVGElementModel[][] dump(SVGModel owner) {
		owner.getRoot();
		List<SVGElementModel[]> pairs = new ArrayList<SVGElementModel[]>();
		List<ModelData> queue = new ArrayList<ModelData>();
		queue.add(owner.getRoot());
		while (!queue.isEmpty()) {
			SVGElementModel model = (SVGElementModel) queue.remove(0);
			SVGElementModel parent = (SVGElementModel) model.getParent();
			pairs.add(new SVGElementModel[]{parent, model});
			queue.addAll(model.getChildren());
		}
		return pairs.toArray(new SVGElementModel[pairs.size()][]);
	}

	public static String toString(SVGElementModel[][] pairs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		int count = 0;
		for (SVGElementModel[] pair : pairs) {
			if (pair[0] != null) {
				if (count > 0) {
					buffer.append(", ");
				}
				buffer.append("{");
				buffer.append(pair[0].toString());
				buffer.append(", ");
				buffer.append(pair[1].toString());
				buffer.append("}");
				count++;
			}
		}
		buffer.append("}");
		return buffer.toString();
	}

	public static SVGElementModel createElementModel(SVGModel owner, String name) {
		SVGElementModel model = owner.create((SVGElement)new OMSVGGElement().getElement().cast(), (SVGElement)new OMSVGGElement().getElement().cast());
		model.set(SVGConstants.SVG_TITLE_TAG, name);
		return model;
	}
	
	public static String randomTransform() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			if (i > 0) {
				builder.append(" ");			
			}
			switch(Random.nextInt(3)) {
				case 0:
					builder.append("rotate(" + Random.nextDouble() * 360 + ")");			
					break;
				case 1:
					builder.append("translate(" + Random.nextDouble() * 100 + "," + Random.nextDouble() * 100 + ")");			
					break;
				case 2:
					builder.append("scale(" + (Random.nextDouble() * 3 + 1) + "," + (Random.nextDouble() * 3 + 1) + ")");			
					break;
				}
		}
		return builder.toString();
	}
}
