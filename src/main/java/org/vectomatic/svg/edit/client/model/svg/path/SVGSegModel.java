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
package org.vectomatic.svg.edit.client.model.svg.path;

import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.model.AbstractModel;
import org.vectomatic.svg.edit.client.model.IPropertyAccessor;
import org.vectomatic.svg.edit.client.model.JSMetadata;
import org.vectomatic.svg.edit.client.model.MetadataBase;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGPathSegType;

import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;

/**
 * Base model class for SVG segments
 * @author laaglu
 */
public abstract class SVGSegModel extends AbstractModel<OMSVGPathSeg> {
	public static final String TYPE_ID = "type";
	public static MetadataBase<String, OMSVGPathSeg> TYPE;
	static {
		ModelConstants constants = ModelConstants.INSTANCE;

		TYPE = new JSMetadata<String, OMSVGPathSeg>(
				TYPE_ID, 
				constants.segTypeDesc(),
				null,
				new IPropertyAccessor<String, OMSVGPathSeg>() {
					@Override
					public String get(OMSVGPathSeg model) {
						return SVGPathSegType.INSTANCE.fromLetter(model.getPathSegTypeAsLetter());
					}
					@Override
					public void set(OMSVGPathSeg model, String value) {
						throw new IllegalStateException("Property " + TYPE_ID + " cannot be set");
					}
				},
				null,
				null);
	}
	/**
	 * The path owning this segment
	 */
	protected SVGSegStore owner;

	public SVGSegModel(OMSVGPathSeg element, SVGSegStore owner) {
		super(element);
		this.owner = owner;
	}
	
	protected void fireChangeEvent(String oldValue) {
		owner.fireChangeEvent(oldValue, owner.getValue());
	}
	
	public Store getStore() {
		return owner;
	}
	
	/**
	 * Adds a segment to the model. The new segment is the symetric
	 * of the previous segment with respect to the last segment endpoint
	 * @return
	 */
	public abstract SVGSegModel createNextSegment();
	
	/**
	 * Splits this segment in two, inserting another segment
	 * before this segment. This is done without changing
	 * the geometry of the path.
	 * @param index
	 * The position of this segment in its parent model
	 * @return
	 * The newly inserted segment
	 */
	public abstract SVGSegModel split(int index);
	
	@Override
	public <X> X set(String property, X value) {
		String oldModelValue = (get(property) != value) ? owner.getValue() : null;
		X oldValue;
		if (TYPE_ID.equals(property)) {
			// Special case: segment type change involves replacing
			// the current segment with a new segment
			oldValue = (X)owner.setPathSegType(this, (String)value);
			StoreEvent<SVGSegModel> evt = new StoreEvent<SVGSegModel>(owner);
			evt.setModel(this);
			owner.fireEvent(Store.DataChanged, evt);
		} else {
			oldValue = super.set(property, value);
		}
		if (oldModelValue != null) {
			// propagate changes to objects which monitor
			// the path "d" attribute changes
			owner.fireChangeEvent(oldModelValue, owner.getValue());
		}
		return oldValue;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SVGSegModel(");
		builder.append(getClass().getName());
		builder.append(", ");
		builder.append(getProperties());
		builder.append(")");
		return builder.toString();
	}
	
	public float getX() {
		return this.<Float>get(SVGConstants.SVG_X_ATTRIBUTE);
	}
	public float getY() {
		return this.<Float>get(SVGConstants.SVG_Y_ATTRIBUTE);
	}
}


