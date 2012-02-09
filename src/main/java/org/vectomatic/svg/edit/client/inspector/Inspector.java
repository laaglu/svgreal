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
package org.vectomatic.svg.edit.client.inspector;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.svg.edit.client.model.AbstractModel;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.ModelCategory;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.google.gwt.core.client.GWT;

/**
 * Class to provide an editable view of a metamodel. The inspector
 * itself is organized in sections, which correspond to the various
 * categories on the metamodel
 * @author laaglu
 * @param <M>
 * The model being associated to this inspector
 */
public class Inspector<M extends AbstractModel<?>> extends ContentPanel {
	private List<IInspectorSection<M>> sections;
	private IInspectorSection<M> currentSection;
	private MetaModel<?> metamodel;
	private M model;
	
	public Inspector(MetaModel<?> metamodel) {
		this.metamodel = metamodel;
		setHeaderVisible(false);
		setBodyBorder(false);
		setLayout(new AccordionLayout() {
			/**
			 * Invoked when the section becomes visible. It binds
			 * the section to the model so that the section displays
			 * the proper data
			 */
			@Override
			protected void onLayout(Container<?> container, El target) {
				if (currentSection != null) {
					currentSection.unbind();
				}
				for (IInspectorSection<M> section : sections) {
					if (section.getPanel() == activeItem) {
						section.bind(model);
						currentSection = section;
						break;
					}
				}
				super.onLayout(container, target);
			}
		});
		
		sections = new ArrayList<IInspectorSection<M>>();
		for (ModelCategory<?> category : metamodel.getCategories()) {
			IInspectorSection<M> section = category.getInspectorSection();
			if (section != null) {
				sections.add(section);
				add(section.getPanel());
			}
		}
	}
	
	public void bind(M model) {
		GWT.log("Inspector.bind(" + model + ")");
		this.model = model;
		if (currentSection != null) {
			currentSection.unbind();
			currentSection.bind(model);
		}
	}
	
	public void unbind() {
		GWT.log("unbind()");
		if (currentSection != null) {
			currentSection.unbind();
		}
		this.model = null;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGInspector(");
		builder.append(metamodel);
		builder.append(")");
		return builder.toString();
	}

}
