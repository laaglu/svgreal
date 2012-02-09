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
package org.vectomatic.svg.edit.client.model.svg;

import org.vectomatic.svg.edit.client.model.IConverter;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.resources.client.ImageResource;

public class ComboStore extends ListStore<ModelData> {
	public static final String LABEL = "label";
	public static final String ICON = "icon";
	public static final String VALUE = "value";
	private boolean hasIcons;
	
	private static class ComboData extends BaseModelData {
		@Override
		public String toString() {
			return this.<String>get(LABEL);
		}
	}
	
	public ComboStore(String[] values, String[] labels) {
		this(values, labels, null);
	}
	
	public ComboStore(String[] values, String[] labels, ImageResource icons[]) {
		super();
		assert values.length == labels.length;
		assert icons == null || icons.length == labels.length;
		hasIcons = icons != null;
		for (int i = 0; i < values.length; i++) {
			ModelData model = new ComboData(); 
			model.set(VALUE, values[i]);
			model.set(LABEL, labels[i]);
			if (hasIcons()) {
				model.set(ICON, icons[i].getSafeUri().asString());
			}
			add(model);
		}
	}
	
	public IConverter<String, ModelData> getConverter() {
		return new IConverter<String, ModelData>() {

			@Override
			public ModelData sourceToDest(String source) {
				return findModel(VALUE, source);
			}

			@Override
			public String destToSource(ModelData dest) {
				return dest.get(VALUE);
			}
			
		};
	}

	public boolean hasIcons() {
		return hasIcons;
	}
}
