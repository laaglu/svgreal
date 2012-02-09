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
package org.vectomatic.svg.edit.client.gxt.form;

import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.gxt.panels.DashArrayEditor;
import org.vectomatic.svg.edit.client.gxt.widget.DashArrayCell;
import org.vectomatic.svg.edit.client.gxt.widget.SVGButton;
import org.vectomatic.svg.edit.client.model.svg.CssContextModel;
import org.vectomatic.svg.edit.client.model.svg.DashArray;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Field subclass to edit DashArray values
 * @author laaglu
 */
public class DashArrayField extends AdapterField {
	private static class DashArrayMenuItem extends AdapterMenuItem implements ClickHandler {
		public DashArrayMenuItem(DashArrayCell cell) {
			super(cell);
			cell.addClickHandler(this);
		}
		
		@Override
		public void onClick(ClickEvent event) {
			ComponentEvent be = new ComponentEvent(this);
		    be.stopEvent();
		    MenuEvent me = new MenuEvent(parentMenu);
		    me.setItem(this);
		    me.setEvent(be.getEvent());
			if (!disabled && fireEvent(Events.Select, me)) {
				handleClick(be);
			}
		}
		
		public DashArrayCell getCell() {
			return (DashArrayCell)widget;
		}
		
	}
	
	private class DashArrayFieldPanel extends LayoutContainer {
		private DashArrayCell dashArrayCell;
		private SVGButton menuButton;
		private MenuItem editItem;
		private Menu menu;
		
		private SelectionListener<MenuEvent> menuListener = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				DashArrayCell cell = ((DashArrayMenuItem)ce.getItem()).getCell();
				setValue(cell.getDashArray());
				DashArrayField.this.fireEvent(Events.AfterEdit, new FieldEvent(DashArrayField.this));
			}
		};
		public DashArrayFieldPanel() {
			editItem = new MenuItem(AppConstants.INSTANCE.editDashArray());
			editItem.addSelectionListener(new SelectionListener<MenuEvent>() {
				private HandlerRegistration changeReg;
				private HandlerRegistration closeReg;
				@Override
				public void componentSelected(MenuEvent ce) {
					final DashArrayEditor editor = new DashArrayEditor();
					final DashArray oldValue = (DashArray) getValue();
					changeReg = editor.addValueChangeHandler(new ValueChangeHandler<DashArray>() {
						@Override
						public void onValueChange(ValueChangeEvent<DashArray> event) {
							GWT.log("DashArrayField.onValueChange(" + event.getValue() + ")");
							setValue(event.getValue());
						}					
					});
					closeReg = editor.addCloseHandler(new CloseHandler<DashArrayEditor>() {
						@Override
						public void onClose(CloseEvent<DashArrayEditor> event) {
							GWT.log("DashArrayField.onClose()");
							changeReg.removeHandler();
							closeReg.removeHandler();
							if (!event.isAutoClosed()) {
								// The user clicked the commit button in the dash array editor
								// Save the new default dash arrays
								CssContextModel.setDefaultDashArrays(editor.getDashArrays());
								// Record the change as a command
								DashArrayField.this.fireEvent(Events.AfterEdit, new FieldEvent(DashArrayField.this));
								// Update the button menu
								updateMenu();
							} else {
								// Restore the previous dash array
								setValue(oldValue);
							}
						}
					});
					editor.show();
				}
			});
			dashArrayCell = new DashArrayCell();
			menuButton = new SVGButton(dashArrayCell);
			setLayout(new FitLayout());
			add(menuButton);
			updateMenu();
		}
		public void update(DashArray value) {
			dashArrayCell.setDashArray(value);
		}
		private void updateMenu() {
			menu = new Menu();
			ListStore<DashArray> dashArrays = CssContextModel.getDefaultDashArrays();
			for (DashArray dashArray : dashArrays.getModels()) {
				DashArrayCell cell = new DashArrayCell();
				cell.setDashArray(dashArray);
				DashArrayMenuItem item = new DashArrayMenuItem(cell);
				item.addSelectionListener(menuListener);
				menu.add(item);
			}
			menu.add(editItem);
			menuButton.setMenu(menu);
		}
		
		@Override
		protected void onResize(int width, int height) {
			super.onResize(width, height);
			menu.setWidth(width);
		}
	}
	
	
	public DashArrayField() {
		super(null);
		widget = new DashArrayFieldPanel();
		setResizeWidget(true);
		setFireChangeEventOnSetValue(true);
	}
	
	@Override
	public void setValue(Object value) {
		((DashArrayFieldPanel)widget).update((DashArray)value);
		super.setValue(value);
	}
	
	@Override
	public Object getValue() {
		return value;
	}
}
