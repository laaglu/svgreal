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
 **********************************************/package org.vectomatic.svg.edit.client.model;

import java.util.Collection;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.Model;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.core.client.GWT;

/**
 * Base class for GXT {@link com.extjs.gxt.ui.client.data.Model}
 * implementations which rely on a {@link MetaModel} to supply
 * the model properties.
 * @author laaglu
 */
public abstract class AbstractModel<U> implements Model {
	/**
	 * To implement ChangeEventSource
	 */
	protected transient ChangeEventSupport changeEventSupport;
	/**
	 * The native object backing this model
	 */
	protected U element;

	public AbstractModel(U element) {
	    this.element = element;
	    changeEventSupport = new ChangeEventSupport();
	}
	
	public abstract MetaModel<U> getMetaModel();
	
	public U getElement() {
		return element;
	}
	
	/**
	 * Returns the store which contains this model
	 * @return
	 */
	public abstract Store getStore();

	///////////////////////////////////////////////////
	// Implementation of the ModelData interface
	///////////////////////////////////////////////////
	
	@Override
	public <X> X get(String property) {
		IMetadata propertyDefinition = getMetaModel().getMetadata(property);
		assert propertyDefinition != null;
		return (X)propertyDefinition.get(element);
	}

	@Override
	public Map<String, Object> getProperties() {
		return getMetaModel().getProperties(element);
	}

	@Override
	public Collection<String> getPropertyNames() {
		return getMetaModel().getPropertyNames();
	}

	@Override
	public <X> X remove(String property) {
		IMetadata propertyDefinition = getMetaModel().getMetadata(property);
		assert propertyDefinition != null;
		X oldValue = (X)propertyDefinition.remove(element);
		notifyPropertyChanged(propertyDefinition.getName(), null, oldValue);
		return oldValue;
	}

	@Override
	public <X> X set(String property, X value) {
		IMetadata propertyDefinition = getMetaModel().getMetadata(property);
		assert propertyDefinition != null;
		X oldValue = (X)propertyDefinition.set(element, value);
		GWT.log("AbstractModel.set(" + property + ") = " + oldValue + ", " + value);
		notifyPropertyChanged(propertyDefinition.getName(), value, oldValue);
		return oldValue;
	}
	
	///////////////////////////////////////////////////
	// Implementation of the ChangeEventSource interface
	///////////////////////////////////////////////////
	@Override
	public void addChangeListener(ChangeListener... listener) {
	    changeEventSupport.addChangeListener(listener);
	}

	@Override
	public void removeChangeListener(ChangeListener... listener) {
	    changeEventSupport.removeChangeListener(listener);
	}

	@Override
	public void removeChangeListeners() {
	    changeEventSupport.removeChangeListeners();
	}

	@Override
	public void setSilent(boolean silent) {
	    changeEventSupport.setSilent(silent);
	}

	@Override
	public void notify(ChangeEvent event) {
	    changeEventSupport.notify(event);
	}
	
	protected void notifyPropertyChanged(String name, Object value, Object oldValue) {
		if (!Util.equalWithNull(value, oldValue)) {
			notify(new PropertyChangeEvent(Update, this, name, oldValue, value));
		}
	}
	
	public boolean isSilent() {
	    return changeEventSupport.isSilent();
	}

	protected void fireEvent(int type) {
		notify(new ChangeEvent(type, this));
	}

	protected void fireEvent(int type, Model item) {
		notify(new ChangeEvent(type, this, item));
	}

}
