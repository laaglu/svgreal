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
package org.vectomatic.svg.edit.client.command.edit;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.itf.ISVGLocatable;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.event.KeyPressProcessor;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.event.MouseUpProcessor;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * Base class for manipulators
 * @author laaglu
 */
public abstract class EditManipulatorBase implements MouseDownProcessor, MouseMoveProcessor, MouseUpProcessor, KeyPressProcessor, ChangeListener {
	/**
	 * A SVGElementModel record this manipulator applies to.
	 * The record is used to batch modifications to the model.
	 */
	protected Record record;
	/**
	 * True to temporarily disconnect from model updates, false otherwise
	 */
	protected boolean monitorModel;
	/**
	 * The SVG element this manipulator lives in
	 */
	protected OMSVGSVGElement svg;
	/**
	 * The group where SVG elements representing this
	 * manipulator are nested
	 */
	protected OMSVGGElement g;

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		return false;
	}

	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		return false;
	}

	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		return false;
	}

	@Override
	public boolean processKeyPress(ComponentEvent event) {
		return false;
	}
	
	/**
	 * Returns the graphical element which represents this manipulator.
	 * @return the graphical element which represents this manipulator.
	 */
	public OMSVGElement getManipulatorElement() {
		return g;
	}
	
	/**
	 * Returns the circle this manipulator applies to.
	 * @return the circle this manipulator applies to.
	 */
	public Record getManipulatedElement() {
		return record;
	}
	
	/**
	 * Detaches this manipulator from the DOM tree
	 */
	public abstract void unbind();
	
	/**
	 * Binds this manipulator to the specified SVG element.
	 * @param record
	 * The SVGElementModel record this manipulator is applied to.
	 * @return
	 * return the root element of the manipulator
	 */
	public abstract OMSVGElement bind(Record record);
	
	protected void scheduleInit() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				modelChanged(null);
			}				
		});
	}

    /**
     * Returns the coordinates of a mouse event, converted
     * to the coordinate system of the specified matrix
     * @param e
     * A mouse event
     * @param m
     * A transformation matrix
     * @return
     * The coordinates of the mouse event, converted
     * to the coordinate system of the specified matrix
     */
    public OMSVGPoint getCoordinates(MouseEvent<? extends EventHandler> e, OMSVGMatrix m) {
    	return svg.createSVGPoint(e.getClientX(), e.getClientY()).matrixTransform(m);
    }
    
	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			// Update the transform for the group containing the
			// manipulator UI
			SVGElementModel model = (SVGElementModel) record.getModel();
			ISVGLocatable locatable = (ISVGLocatable) model.getElementWrapper();
			OMSVGGElement elementGroup = model.getOwner().getElementGroup();
			OMSVGMatrix m = locatable.getTransformToElement(elementGroup);
			if (!m.isIdentity()) {
				StringBuilder builder = new StringBuilder();
				builder.append(SVGConstants.TRANSFORM_MATRIX + "(");
				builder.append(m.getDescription());
				builder.append(")");
				g.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, builder.toString());
			}
		}
	}
}
