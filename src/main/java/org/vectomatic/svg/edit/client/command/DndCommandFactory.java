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
package org.vectomatic.svg.edit.client.command;

import java.util.List;

import org.vectomatic.svg.edit.client.command.dnd.DndCopyHandler;
import org.vectomatic.svg.edit.client.command.dnd.DndLinkHandler;
import org.vectomatic.svg.edit.client.command.dnd.DndMoveHandler;
import org.vectomatic.svg.edit.client.command.dnd.IDndHandler;
import org.vectomatic.svg.edit.client.event.KeyPressProcessor;
import org.vectomatic.svg.edit.client.event.KeyUpProcessor;
import org.vectomatic.svg.edit.client.gxt.widget.DNDGhost;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.dnd.StatusProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Base class for drag and drop command factories
 * @author laaglu
 */
public class DndCommandFactory extends CommandFactoryBase implements KeyPressProcessor, KeyUpProcessor {
	/**
	 * Enum to represent the drop gestures
	 */
	public enum DropGesture {
		/**
		 * The source is released precisely on the target
		 */
		OnNode,
		/**
		 * The source is released slightly before the target
		 */
		BeforeNode,
		/**
		 * The source is released slightly after the target
		 */
		AfterNode
	}
	/**
	 * Default handler for drag and drop events
	 */
	private static IDndHandler defaultHandler = new DndMoveHandler();
	/**
	 * Handlers for drag and drop events
	 */
	private static IDndHandler[] handlers = new IDndHandler[] { new DndCopyHandler(), new DndLinkHandler(), defaultHandler};
	/**
	 * The handler which currently processes the drag interactions
	 */
	private IDndHandler currentHandler;
	/**
	 * The drag and drop proxy
	 */
	StatusProxy proxy;
	/**
	 * To graphically represent the drag operation
	 */
	private DNDGhost dndGhost;
	/**
	 * The drag source elements
	 */
	private List<SVGElementModel> sourceElements;
	/**
	 * The drop target over which the user has last move the mouse
	 */
	private SVGElementModel targetElement;
	/**
	 * True if the current drop target is valid
	 */
	boolean validTarget;


	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<DndCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<DndCommandFactory>(ModelConstants.INSTANCE.dndCmdFactory(), ModelConstants.INSTANCE.dndCmdFactoryDesc()) {
		@Override
		public DndCommandFactory create() {
			return new DndCommandFactory();
		}
	};
	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}
	
	@Override
	public void start(Object requester) {
		GWT.log("DndCommandFactory.start(" + requester + ")");
		super.start(requester);
		updateStatus(ModelConstants.INSTANCE.dndCmdFactory1());
	}

	/**
	 * Returns true if the drag start event comes from a valid drag source
	 * @param event a drag start event
	 * @return true if the drag start event comes from a valid drag source
	 */
	public boolean isValidSource(DNDEvent event, List<SVGElementModel> sourceElements) {
		targetElement = null;
		validTarget = false;
		
		this.sourceElements = sourceElements;
		for (IDndHandler handler : handlers) {
			if (handler.isValidSource(event, sourceElements)) {
				dndGhost = new DNDGhost(sourceElements, event);
				proxy = event.getStatus();
				proxy.update(dndGhost.getElement());
				targetElement = null;
				setHandler(handler);

				/*
				 * The drag and drop elements in GXT are positionned as follows:
				 * div container (x-dd-drag-proxy)
				 *   div icon (x-dd-drop-icon)
				 *   div proxy (x-dd-drag-ghost)
				 *     div ghost (custom ghost)
				 *     
				 * x-dd-drag-ghost needs to be altered to be made absolute
				 * in order to support custom ghost with absolute positioning
				 */
				Style proxyStyle = dndGhost.getElement().getParentElement().getStyle();
				proxyStyle.setPosition(Position.ABSOLUTE);
				proxyStyle.setLeft(0, Unit.PX);
				proxyStyle.setRight(0, Unit.PX);
				proxyStyle.setTop(0, Unit.PX);
				proxyStyle.setBottom(0, Unit.PX);
				
				return true;
			}
		}
		return false;
	}
	
	public boolean isValidDropTarget(SVGElementModel element) {
		if (targetElement != element) {
			targetElement = element;
			validTarget = currentHandler.isValidTarget(sourceElements, targetElement);
			GWT.log("isValidDropTarget(" + targetElement + ") = " + validTarget);
		}
		return validTarget;
	}
	
	public void processDragAndDrop(DropGesture dropGesture) {
		GWT.log("processDragAndDrop(" + sourceElements + ", " + targetElement + "," + dropGesture + ") = " + validTarget);
		// TODO
		// ignore useless drag and drops:
		// drag last item of a folder in its parent
		// drag an item before of after itself
		currentHandler.createCommands(sourceElements, targetElement, dropGesture);
		currentHandler = null;
	}
	
	@Override
	public boolean processKeyPress(ComponentEvent event) {
		for (IDndHandler handler : handlers) {
			if (handler.getKeyCode() == event.getKeyCode()) {
				setHandler(handler);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean processKeyUp(ComponentEvent event) {
		for (IDndHandler handler : handlers) {
			if (handler.getKeyCode() == event.getKeyCode()) {
				setHandler(defaultHandler);
				return true;
			}
		}
		return false;
	}
	
	private void setHandler(IDndHandler handler) {
		if (handler != currentHandler) {
			currentHandler = handler;
			dndGhost.update(currentHandler);
			proxy.setStatus(currentHandler.isValidTarget(sourceElements, targetElement));
		}
	}
	
//	/**
//	 * Extracts the SVG models from a drag and drop event
//	 * @param event the event
//	 * @return the SVG models associated to the event
//	 */
//	public static List<SVGElementModel> getData(DNDEvent event) {
//		List<TreeStoreModel> storeModels = event.getData();
//		ArrayList<SVGElementModel> svgModels = new ArrayList<SVGElementModel>();
//		for (TreeStoreModel storeModel : storeModels) {
//			svgModels.add((SVGElementModel) storeModel.getModel());
//		}
//		return svgModels;
//	}
//
//
//	static private List<String> eventLog;
//	static public void logDNDEvent(String origin, DNDEvent event) {
//		List<String> strList = new ArrayList<String>();
//		strList.add("=================== DNDListener =====================");
//		strList.add("origin = " +  origin);
//		EventType type = event.getType();
//		if (type == Events.DragStart) {
//			strList.add("type = DragStart");
//		} else if (type == Events.DragEnter) {
//			strList.add("type = DragEnter");
//		} else if (type == Events.DragLeave) {
//			strList.add("type = DragLeave");
//		} else if (type == Events.DragMove) {
//			strList.add("type = DragMove");
//		} else if (type == Events.Drop) {
//			strList.add("type = Drop");
//		}
//		strList.add("component = " +  event.getComponent());
//		strList.add("data = " +  DndHandlerBase.getSourceElementNames(getData(event)));
//		strList.add("operation = " +  event.getOperation());
//		strList.add("source = " +  event.getSource());
//		DragSource dragSource = event.getDragSource();
//		if (dragSource instanceof SVGTreePanelDragSource) {
//			SVGTreePanelDragSource source = (SVGTreePanelDragSource)dragSource;
//			strList.add("sourceModel = " +  source.getSvgModel());
//		}
//		DropTarget dropTarget = event.getDropTarget();
//		if (dropTarget instanceof SVGTreePanelDropTarget) {
//			SVGTreePanelDropTarget target = (SVGTreePanelDropTarget)dropTarget;
//			strList.add("targetModel = " +  target.getSvgModel());
//			SVGElementModel activeItem = target.getActiveItem();
//			if (activeItem != null) {
//				strList.add("activeItem = " +  activeItem.get(SVGConstants.SVG_TITLE_TAG));
//			}
//			SVGElementModel appendItem = target.getActiveItem();
//			if (appendItem != null) {
//				strList.add("appendItem = " +  appendItem.get(SVGConstants.SVG_TITLE_TAG));
//			}
//		}
//		if (!strList.equals(eventLog)) {
//			eventLog = strList;
//			for(String str : eventLog) {
//				GWT.log(str);
//			}
//		}
//	}
}
