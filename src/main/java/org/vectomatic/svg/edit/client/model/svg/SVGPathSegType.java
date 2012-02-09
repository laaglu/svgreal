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
package org.vectomatic.svg.edit.client.model.svg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.svg.edit.client.model.IFieldFactory;
import org.vectomatic.svg.edit.client.model.IMetadata;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;

/**
 * Metadata path segment type (will be edited by a combo box).
 * @author laaglu
 */
public class SVGPathSegType implements IFieldFactory {
	public static final SVGPathSegType INSTANCE = new SVGPathSegType();
	private List<String> typeNames;
	private Map<String,String> letterToName;
	private Map<String,Short> nameToType;
	
	public SVGPathSegType() {
		ModelConstants constants = ModelConstants.INSTANCE;
		letterToName = new HashMap<String,String>();
		letterToName.put("M", constants.segMoveTo());
		letterToName.put("L", constants.segLineTo());
		letterToName.put("Q", constants.segQuadraticTo());
		letterToName.put("C", constants.segCubicTo());
		nameToType = new HashMap<String,Short>();
		nameToType.put(constants.segMoveTo(), OMSVGPathSeg.PATHSEG_MOVETO_ABS);
		nameToType.put(constants.segLineTo(), OMSVGPathSeg.PATHSEG_LINETO_ABS);
		nameToType.put(constants.segQuadraticTo(), OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS);
		nameToType.put(constants.segCubicTo(), OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS);
		typeNames = Arrays.asList(new String[]{
				constants.segMoveTo(),
				constants.segLineTo(),
				constants.segQuadraticTo(),
				constants.segCubicTo()});
	}

	@Override
	public Component createField(IMetadata<?, ?> metadata) {
		SimpleComboBox<String> field = new SimpleComboBox<String>();
		field.add(typeNames);
		field.setName(metadata.getName());
		field.setForceSelection(true);
		field.setAllowBlank(false);
		field.setTriggerAction(TriggerAction.ALL);  

		return field;
	}
	
	public String fromLetter(String letter) {
		return letterToName.get(letter);
	}
	
	public short fromName(String name) {
		return nameToType.get(name);
	}
	
	public List<String> getTypeNames() {
		return typeNames;
	}
}
