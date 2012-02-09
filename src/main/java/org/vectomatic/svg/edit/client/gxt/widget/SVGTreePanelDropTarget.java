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
package org.vectomatic.svg.edit.client.gxt.widget;

import org.vectomatic.svg.edit.client.SVGWindow;
import org.vectomatic.svg.edit.client.command.CommandFactories;
import org.vectomatic.svg.edit.client.command.DndCommandFactory;
import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.ScrollSupport;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * Drop target for SVGWindow
 * @author laaglu
 */
public class SVGTreePanelDropTarget extends DropTarget {
	protected DndCommandFactory dndCommandFactory;
	protected TreePanelExt<SVGElementModel> tree;
	protected TreeNode activeItem, appendItem;
	protected DropGesture dropGesture;

	private boolean restoreTrackMouse;
	private ScrollSupport scrollSupport;

	protected SVGModel svgModel;
	public SVGTreePanelDropTarget(SVGWindow window) {
		super(window.getTree());
		this.tree = window.getTree();
		svgModel = window.getSvgModel();
		dndCommandFactory = CommandFactories.getDndCommandFactory();
	}

	protected void clearStyles(DNDEvent event) {
		Insert.get().hide();
		event.getStatus().setStatus(false);
		if (activeItem != null) {
			tree.getView().onDropChange(activeItem, false);
		}
	}

	@Override
	protected void onDragDrop(DNDEvent event) {
		GWT.log("SVGTreePanelDropTarget.onDragDrop");
		super.onDragDrop(event);
		if (event.getData() == null)
			return;

		tree.getView().onDropChange(activeItem, false);
		dndCommandFactory.processDragAndDrop(dropGesture);
		tree.setTrackMouseOver(restoreTrackMouse);
		activeItem = null;
		appendItem = null;
		scrollSupport.stop();
	}

	@Override
	protected void onDragEnter(DNDEvent e) {
		super.onDragEnter(e);
		e.getStatus().setStatus(false);
		restoreTrackMouse = tree.isTrackMouseOver();
		tree.setTrackMouseOver(false);

		if (scrollSupport == null) {
			scrollSupport = new ScrollSupport(tree.el());
		}
		scrollSupport.start();
	}

	@Override
	protected void onDragFail(DNDEvent event) {
		super.onDragFail(event);
		scrollSupport.stop();
	}

	@Override
	protected void onDragLeave(DNDEvent e) {
		super.onDragLeave(e);
		if (activeItem != null) {
			tree.getView().onDropChange(activeItem, false);
			activeItem = null;
		}
		tree.setTrackMouseOver(restoreTrackMouse);
		scrollSupport.stop();
	}

	@Override
	protected void onDragMove(DNDEvent event) {
		event.setCancelled(false);
	}

	@Override
	protected void showFeedback(DNDEvent event) {
		// GWT.log("showFeedback");
		// Determine the tree node over which the cursor is hovering, if any
		final TreeNode overItem = tree.findNode(event.getTarget());
		if (!dndCommandFactory.isValidDropTarget(getModel(overItem))) {
			// Not over a tree item
			clearStyles(event);
			return;
		}
		handleInsert(event, overItem);
	}
	

	protected void handleInsert(DNDEvent event, final TreeNode item) {
		// Determine the position of the cursor with regards to the drop item
		// to find out if the drag source should be inserted before or after
		// the drop item
		int height = item.getElement().getOffsetHeight();
		int mid = height / 2;
		int top = item.getElement().getAbsoluteTop();
		mid += top;
		int y = event.getClientY();
		boolean before = y < mid;

		if ((!item.isLeaf() || SVGProcessor.isGroupElement(getModel(item).getElement()))
				&& ((before && y > top + 4) || (!before && y < top + height - 4))) {
			handleAppend(event, item);
			return;
		}
		dropGesture = before ? DropGesture.BeforeNode : DropGesture.AfterNode;

		// clear any active append item
		if (activeItem != null) {
			tree.getView().onDropChange(activeItem, false);
		}
		appendItem = null;
		activeItem = item;

		int idx = -1;
		if (activeItem.getParent() == null) {
			idx = tree.getStore().getRootItems().indexOf(activeItem);
		} else {
			idx = activeItem.getParent().indexOf(item);
		}
		
		event.getStatus().setStatus(true);

		showInsert(event, item.getElement(), before);
	}
	
	protected void handleAppend(DNDEvent event, final TreeNode item) {
		dropGesture = DropGesture.OnNode;
		Insert.get().hide();
		event.getStatus().setStatus(true);
		// clear any active append item
		if (activeItem != null) {
			tree.getView().onDropChange(activeItem, false);
		}

		if (item != null && item != appendItem && !item.isExpanded()) {
			Timer t = new Timer() {
				@Override
				public void run() {
					if (item == appendItem) {

						item.setExpanded(true);
					} else {
					}
				}
			};
			// auto-expand delay
			t.schedule(800);
		}
		appendItem = item;
		activeItem = item;
		if (activeItem != null) {
			tree.getView().onDropChange(activeItem, true);
		}
	}



	private void showInsert(DNDEvent event, Element elem, boolean before) {
		Insert insert = Insert.get();
		insert.show(elem);
		Rectangle rect = El.fly(elem).getBounds();
		int y = before ? rect.y - 2 : (rect.y + rect.height - 4);
		insert.setBounds(rect.x, y, rect.width, 6);
	}
	
	
	public SVGElementModel getActiveItem() {
		return getModel(activeItem);
	}
	public SVGElementModel getModel(TreeNode treeNode) {
		if (treeNode != null) {
			return (SVGElementModel)treeNode.getModel();
		}
		return null;
	}
	public SVGModel getSvgModel() {
		return svgModel;
	}
}
