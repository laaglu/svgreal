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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.EditTransformCommandFactory;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.model.AbstractModel;
import org.vectomatic.svg.edit.client.model.AttrMetadata;
import org.vectomatic.svg.edit.client.model.CssMetadata;
import org.vectomatic.svg.edit.client.model.IConverter;
import org.vectomatic.svg.edit.client.model.IMetadata;
import org.vectomatic.svg.edit.client.model.ModelCategory;
import org.vectomatic.svg.edit.client.model.ModelConstants;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;

/**
 * Base model class for svg elements.
 * @author laaglu
 */
public abstract class SVGElementModel extends AbstractModel<SVGElement> implements TreeModel {
	private static ModelCategory<SVGElement> displayCategory;
	private static ModelCategory<SVGElement> transformCategory;
	
	/**
	 * The DOM element represented by this tree node in
	 * the selectionGroup
	 */
	protected SVGElement twin;
	/**
	 * The model's parent.
	 */
	protected TreeModel parent;
	/**
	 * The model's children.
	 */
	protected List<ModelData> children;
	/**
	 * The SVG model which owns this element
	 */
	protected SVGModel owner;
	
	public SVGElementModel(SVGModel owner, SVGElement element, SVGElement twin) {
		super(element);
		this.owner = owner;
		this.twin = twin;
	    children = new ArrayList<ModelData>();
	}

	public SVGElement getTwin() {
		return twin;
	}

	/**
	 * Returns the SVG model to which this model belongs
	 * @return the SVG model to which this model belongs
	 */
	public SVGModel getOwner() {
		return owner;
	}
	
	/**
	 * Sets the SVG model to which this model belongs
	 * @return an SVG model
	 */
	public void setOwner(SVGModel owner) {
		this.owner = owner;
	}
	
	@Override
	public Store getStore() {
		return owner.getStore();
	}
	
	public Record getRecord() {
		return getStore().getRecord(this);
	}
	
	public OMSVGElement getElementWrapper() {
		return OMNode.convert(element);
	}
	public OMSVGElement getTwinWrapper() {
		return OMNode.convert(twin);
	}

	///////////////////////////////////////////////////
	// Re-implement the ModelData interface: 
	// Modifications need to be applied to both
	// the element and its twin
	///////////////////////////////////////////////////

	@Override
	public <X> X remove(String property) {
		IMetadata propertyDefinition = getMetaModel().getMetadata(property);
		assert propertyDefinition != null;
		X oldValue = (X)propertyDefinition.remove(element);
		propertyDefinition.remove(twin);
		notifyPropertyChanged(propertyDefinition.getName(), null, oldValue);
		return oldValue;
	}

	@Override
	public <X> X set(String property, X value) {
		IMetadata propertyDefinition = getMetaModel().getMetadata(property);
		assert propertyDefinition != null;
		X oldValue = (X)propertyDefinition.set(element, value);
		propertyDefinition.set(twin, value);
//		GWT.log("set(" + property + ") = " + oldValue + ", " + value);
		notifyPropertyChanged(propertyDefinition.getName(), value, oldValue);
		return oldValue;
	}
	
	///////////////////////////////////////////////////
	// Implementation of the TreeModel interface
	///////////////////////////////////////////////////
	/**
	 * Adds a child to the model and fires an {@link ChangeEventSource#Add}
	 * event.
	 * @param child the child to be added
	 */
	public void add(ModelData child) {
		insert(child, getChildCount());
	}

	/**
	 * Returns the child at the given index or <code>null</code> if the index is
	 * out of range.
	 * @param index the index to be retrieved
	 * @return the model at the index
	 */
	public ModelData getChild(int index) {
		if ((index < 0) || (index >= children.size())) {
			return null;
		}
		return children.get(index);
	}

	/**
	 * Returns the number of children.
	 * @return the number of children
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * Returns the model's children.
	 * @return the children
	 */
	public List<ModelData> getChildren() {
		return children;
	}

	/**
	 * Returns the model's parent or <code>null</code> if no parent.
	 * @return the parent
	 */
	public TreeModel getParent() {
		return parent;
	}

	public int indexOf(ModelData child) {
		return children.indexOf(child);
	}

	/**
	 * Inserts a child to the model and fires an {@link ChangeEventSource#Add}
	 * event.
	 * @param child the child to be inserted
	 * @param index the location to insert the child
	 */
	public void insert(ModelData child, int index) {
		adopt(child);
		children.add(index, child);
		ChangeEvent evt = new ChangeEvent(Add, this);
		evt.setParent(this);
		evt.setItem(child);
		evt.setIndex(index);
		notify(evt);
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}

	/**
	 * Removes the child at the given index.
	 * @param index the child index
	 */
	public void remove(int index) {
		if (index >= 0 && index < getChildCount()) {
			remove(getChild(index));
		}
	}

	/**
	 * Removes the child from the model and fires a
	 * {@link ChangeEventSource#Remove} event.
	 * @param child  the child to be removed
	 */
	public void remove(ModelData child) {
		orphan(child);
		children.remove(child);
		ChangeEvent evt = new ChangeEvent(Remove, this);
		evt.setParent(this);
		evt.setItem(child);
		notify(evt);
	}

	public void removeAll() {
		for (int i = children.size() - 1; i >= 0; i--) {
			remove(getChild(i));
		}
	}

	/**
	 * Sets the model's children. All existing children are first removed.
	 * @param children the children to be set
	 */
	public void setChildren(List<ModelData> children) {
		removeAll();
		if (children != null) {
			for (ModelData child : children) {
				add(child);
			}
		}
	}

	public void setParent(TreeModel parent) {
		this.parent = parent;
	}

	private void setParentInternal(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			treeChild.setParent(this);
		} else {
			child.set("gxt-parent", child);
		}
	}

	private TreeModel getParentInternal(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			return treeChild.getParent();
		} else {
			return (TreeModel) child.get("gxt-parent");
		}
	}

	private void adopt(ModelData child) {
		TreeModel p = getParentInternal(child);
		if (p != null && p != this) {
			p.remove(child);
		}
		setParentInternal(child);
	}

	private void orphan(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			treeChild.setParent(null);
		} else {
			child.remove("gxt-parent");
		}
	}
	
	/**
	 * Returns true if the specified model is an ancestor
	 * of this model. The following rules apply.
	 * <ul>
	 * <li>a model is considered a ancestor of itself</li>
	 * <li>a model in another svg model is not a ancestor
	 * of this model</li>
	 * </ul>
	 * @param model
	 * @return
	 */
	public boolean isAncestorOf(SVGElementModel model) {
		if (model.getOwner() != owner) {
			return false;
		}
		SVGElementModel root = owner.getRoot();
		while(model != root) {
			if (this == model) {
				return true;
			}
			model = (SVGElementModel) model.getParent();
		}
		return false;
	}
	
	/**
	 * Returns this node's previous sibling, or null if no previous sibling exist
	 * @return
	 * this node's previous sibling
	 */
	public SVGElementModel getPreviousSibling() {
		if (parent != null) {
			int index = parent.indexOf(this);
			if (index > 0) {
				return (SVGElementModel) parent.getChild(index - 1);
			}
		}
		return null;
	}


	/**
	 * Returns this node's next sibling, or null if no next sibling exist
	 * @return
	 * this node's next sibling
	 */
	public SVGElementModel getNextSibling() {
		if (parent != null) {
			int index = parent.indexOf(this);
			if (index < parent.getChildCount() - 1) {
				return (SVGElementModel) parent.getChild(index + 1);
			}
		}
		return null;
	}


	/*==================================================
	 * Categories 
	 *=================================================*/

	public static ModelCategory<SVGElement> getDisplayCategory() {
		init();
		return displayCategory;
	}
	
	public static ModelCategory<SVGElement> getTransformCategory() {
		init();
	    return transformCategory;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGElementModel(");
		builder.append(getClass().getName());
		builder.append(", ");
		builder.append(getProperties());
		builder.append(")");
		return builder.toString();
	}
	
	private static void init() {
		if (displayCategory == null) {
			ModelConstants constants = ModelConstants.INSTANCE;

			displayCategory = new ModelCategory<SVGElement>(
					ModelCategory.DISPLAY, constants.display(), null);
		    CssMetadata<Boolean> visibility = new CssMetadata<Boolean>(
		    		SVGConstants.CSS_VISIBILITY_PROPERTY, 
		    		constants.visibility(), 
		    		null, 
		    		new IConverter<String, Boolean>() {

						@Override
						public Boolean sourceToDest(String source) {
							return (source != null && source.length() > 0) ? SVGConstants.CSS_VISIBLE_VALUE.equals(source) : null;
						}

						@Override
						public String destToSource(Boolean dest) {
							return dest != null ? (dest ? SVGConstants.CSS_VISIBLE_VALUE : SVGConstants.CSS_HIDDEN_VALUE) : null;
						}
					},
					Boolean.TRUE,
					null);
		    displayCategory.addMetadata(visibility);
		    
			transformCategory = new ModelCategory<SVGElement>(
					ModelCategory.TRANSFORM, 
					constants.transform(), 
					null);
			AttrMetadata transform = new AttrMetadata(
					SVGConstants.SVG_TRANSFORM_ATTRIBUTE, 
					constants.title(), 
					null, 
					SVGConstants.SVG_TRANSFORM_ATTRIBUTE,
					EditTransformCommandFactory.INSTANTIATOR);
			transformCategory.addMetadata(transform);
		    
		}
	}
	/*==================================================
	 * Utilities 
	 *=================================================*/
	/**
	 * Returns the depth of this node in the tree
	 * @return the depth of this node in the tree
	 */
	public int getDepth() {
		int depth = 0;
		TreeModel node = parent;
		while (node != null) {
			node = node.getParent();
			depth++;
		}
		return depth;
	}
	
	/**
	 * Returns the N-th ancestor of this node, or null if no such ancestor exists
	 * @return the N-th ancestor of this node, or null if no such ancestor exists
	 */
	public SVGElementModel getAncestor(int generation) {
		TreeModel node = this;
		while (generation > 0 && node != null) {
			node = node.getParent();
			generation--;
		}
		return (SVGElementModel) node;
	}
	
	/**
	 * Returns a comparator which sorts nodes in treeview ascending order.
	 */
	public static Comparator<SVGElementModel> getAscendingCompataror() {
		return new Comparator<SVGElementModel>() {
			@Override
			public int compare(SVGElementModel model1, SVGElementModel model2) {
				if (model1 == model2) {
					return 0;
				}
				SVGElementModel parent1 = (SVGElementModel) model1.getParent();
				SVGElementModel parent2 = (SVGElementModel) model2.getParent();
				if (parent1 != parent2) {
					int depth1 = model1.getDepth();
					int depth2 = model2.getDepth();
					if (depth1 > depth2) {
						model1 = model1.getAncestor(depth1 - depth2);
						if (model1 == model2) {
							return 1;
						}
						parent1 = (SVGElementModel) model1.getParent();
					} else if (depth1 < depth2) {
						model2 = model2.getAncestor(depth2 - depth1);
						if (model1 == model2) {
							return -1;
						}
						parent2 = (SVGElementModel) model2.getParent();
					}
					while (parent1 != parent2) {
						SVGElementModel tmp = parent1;
						parent1 = (SVGElementModel) model1.getParent();
						model1 = tmp;
						tmp = parent2;
						parent2 = (SVGElementModel) model2.getParent();
						model2 = tmp;
					}
				}
				if (parent1 == null) {
					return -1;
				} else if (parent2 == null) {
					return 1;
				} else {	
					int ix1 = parent1.indexOf(model1);
					int ix2 = parent2.indexOf(model2);
					return (ix1 < ix2) ? -1 : (ix1 > ix2 ? 1 : 0);
				}
			}
		};
	}
	
	/**
	 * Update the transform of this element model
	 * @param m the transform matrix
	 */
	public void updateTransform(OMSVGMatrix m) {
		if (!m.isIdentity()) {
			StringBuilder builder = new StringBuilder();
			builder.append(SVGConstants.TRANSFORM_MATRIX + "(");
			builder.append(m.getDescription());
			builder.append(")");
			set(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, builder.toString());
		} else {
			remove(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
		}
	}
}
