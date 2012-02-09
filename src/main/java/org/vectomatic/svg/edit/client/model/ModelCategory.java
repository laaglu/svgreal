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
package org.vectomatic.svg.edit.client.model;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.svg.edit.client.inspector.IInspectorSection;
import org.vectomatic.svg.edit.client.inspector.IInspectorSectionFactory;

/**
 * Class to group related metadata of a model into a logical abstraction
 * @author laaglu
 * @param <U>
 * The model class
 */
public class ModelCategory<U> {
	public static final String GLOBAL = "global";
	public static final String DISPLAY = "display";
	public static final String GEOMETRY = "geometry";
	public static final String TRANSFORM = "transform";
	public static final String STROKEFILL = "strokefill";
	private String name;
	private String description;
	private IInspectorSectionFactory sectionFactory;
	private List<IMetadata<?, U>> metadataList;
	public ModelCategory(String name, String description, IInspectorSectionFactory sectionFactory) {
		this.name = name;
		this.description = description;
		this.sectionFactory = sectionFactory;
		this.metadataList = new ArrayList<IMetadata<?, U>>();
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public IInspectorSection getInspectorSection() {
		return sectionFactory != null ? sectionFactory.createSection(this) : null;
	}
	public List<IMetadata<?, U>> getMetadata() {
		return metadataList;
	}
	public <T> IMetadata<T, U> getMetadata(String name) {
		for (IMetadata<?, U> m : metadataList) {
			if (name.equals(m.getName())) {
				return (IMetadata<T, U>)m;
			}
		}
		return null;
	}
	public void addMetadata(MetadataBase<?,U> metadata) {
		metadata.category = this;
		metadataList.add(metadata);
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("ModelCategory(");
		builder.append(name);
		builder.append(", ");
		builder.append(metadataList);
		builder.append(")");
		return builder.toString();
	}
}
