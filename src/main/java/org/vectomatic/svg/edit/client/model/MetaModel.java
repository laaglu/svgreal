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
package org.vectomatic.svg.edit.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.gxt.widget.CommandFactoryMenuItem;

import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.resources.client.ImageResource;

/**
 * Metamodel contains information which enable the introspection
 * of a model. Notably, all the model properties are described
 * by metadata. The model properties are grouped into logical
 * categories
 * @author laaglu
 * @param <U>
 * The model class
 */
public class MetaModel<U> {
	private String name;
	private Map<String, ModelCategory<U>> nameToCategory;
	private Map<String, IMetadata<?, U>> nameToMetadata;
	private List<ModelCategory<U>> categories;
	private IFactoryInstantiator<?>[][] contextMenuFactories;
	private ImageResource icon;
	private List<Item> modelItems;
	
	public void init(String name, ImageResource icon, List<ModelCategory<U>> categories, IFactoryInstantiator<?>[][] contextMenuFactories) {
		this.name = name;
		this.icon = icon;
		this.categories = categories;
		this.contextMenuFactories = contextMenuFactories;
		nameToCategory = new HashMap<String, ModelCategory<U>>();
		nameToMetadata = new HashMap<String, IMetadata<?,U>>();
		for (ModelCategory<U> c : categories) {
			assert !nameToCategory.containsKey(c.getName());
			nameToCategory.put(c.getName(), c);
			for (IMetadata<?,U> m : c.getMetadata()) {
				assert !nameToMetadata.containsKey(m.getName());
				nameToMetadata.put(m.getName(), m);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<ModelCategory<U>> getCategories() {
		return categories;
	}

	public ModelCategory<U> getCategory(String name) {
		return nameToCategory.get(name);
	}

	public ImageResource getIcon() {
		return icon;
	}

	public IMetadata<?,U> getMetadata(String name) {
		return nameToMetadata.get(name);
	}
	
	public Map<String, Object> getProperties(U model) {
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Entry<String, IMetadata<?,U>> entry : nameToMetadata.entrySet()) {
			properties.put(entry.getKey(), entry.getValue().get(model));
		}
		return properties;
	}
	
	public Collection<String> getPropertyNames() {
		return nameToMetadata.keySet();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGMetaModel(");
		builder.append(categories);
		builder.append(")");
		return builder.toString();
	}

	public List<Item> getContextMenuItems() {
		if (modelItems == null) {
			modelItems = new ArrayList<Item>();
			if (contextMenuFactories != null) {
				for (int i = 0; i < contextMenuFactories.length; i++) {
					for (int j = 0; j < contextMenuFactories[i].length; j++) {
						modelItems.add(new CommandFactoryMenuItem((contextMenuFactories[i][j])));
					}
					if (i < contextMenuFactories.length - 1) {
						modelItems.add(new SeparatorMenuItem());
					}
				}
			}
		}
		return modelItems;
	}
}
