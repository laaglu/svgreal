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
package org.vectomatic.svg.edit.client.gxt.panels;

import java.util.List;

import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.CommandFactorySelector;
import org.vectomatic.svg.edit.client.command.CommandStore;
import org.vectomatic.svg.edit.client.command.ICommand;
import org.vectomatic.svg.edit.client.command.ICommandFactory;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.engine.Grid;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.ActivateWindowEvent;
import org.vectomatic.svg.edit.client.event.ActivateWindowHandler;
import org.vectomatic.svg.edit.client.event.CommandFactorySelectorChangeEvent;
import org.vectomatic.svg.edit.client.event.CommandFactorySelectorChangeHandler;
import org.vectomatic.svg.edit.client.event.DeactivateWindowEvent;
import org.vectomatic.svg.edit.client.event.DeactivateWindowHandler;
import org.vectomatic.svg.edit.client.event.SelectionChangedProcessor;
import org.vectomatic.svg.edit.client.event.SelectionChangedProxy;
import org.vectomatic.svg.edit.client.gxt.widget.BlurComboBox;
import org.vectomatic.svg.edit.client.gxt.widget.PaintCell;
import org.vectomatic.svg.edit.client.gxt.widget.RedoCommandMenuItem;
import org.vectomatic.svg.edit.client.gxt.widget.UndoCommandMenuItem;
import org.vectomatic.svg.edit.client.model.svg.CssContextModel;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Class to manage the command toolbar displayed at the bottom of the screen.
 * @author laaglu
 */
public class CommandFactoryToolBar extends ToolBar implements CommandFactorySelectorChangeHandler, ActivateWindowHandler, DeactivateWindowHandler, SelectionChangedProcessor<IFactoryInstantiator<?>> {
	/**
	 * The command factory selector (global to the app)
	 */
	private CommandFactorySelector factorySelector;
	/**
	 * The command list combo (global to the app)
	 */
	private BlurComboBox<IFactoryInstantiator<?>> factoryCombo;
	/**
	 * Button to cancel the current command
	 */
	private Button cancelButton;
	/**
	 * The undo button
	 */
	private SplitButton undoButton;
	/**
	 * The redo button
	 */
	private SplitButton redoButton;
	/**
	 * The undo button menu
	 */
	private Menu undoMenu;
	/**
	 * The redo button menu
	 */
	private Menu redoMenu;
	/**
	 * A status line to display command instructions
	 */
	private Status status;
	/**
	 * The command store of the currently selected model
	 */
	private CommandStore currentCommandStore;
	/**
	 * True if events are disabled
	 */
	private boolean silent;
	/**
	 * Displays the current stroke paint
	 */
	private PaintCell strokeCell;
	/**
	 * Displays the current fill paint
	 */
	private PaintCell fillCell;
	/**
	 * Grid menu
	 */
	private SplitButton gridButton;
	private CheckMenuItem showGridItem; 
	private CheckMenuItem showGuidesItem; 
	private CheckMenuItem snapToGridItem; 
	/**
	 * The x coordinate label 
	 */
	private Label xLabel;
	/**
	 * The y coordinate label 
	 */
	private Label yLabel;

	private StoreListener<BeanModel> commandStoreListener = new StoreListener<BeanModel>() {
		public void storeAdd(StoreEvent<BeanModel> se) {
			updateUndoRedo();
		}

		public void storeFilter(StoreEvent<BeanModel> se) {
			updateUndoRedo();
		}
	};

	public CommandFactoryToolBar(ListStore<IFactoryInstantiator<?>> factoryStore, CommandFactorySelector factoryStack) {
		setSpacing(2);
		this.factorySelector = factoryStack;
		factoryStack.addCommandFactoryChangeHandler(this);
		AppConstants constants = AppConstants.INSTANCE;
		factoryCombo = new BlurComboBox<IFactoryInstantiator<?>>();
		factoryCombo.setEmptyText(constants.selectCommand());  
		factoryCombo.setStore(factoryStore);
		factoryCombo.setDisplayField(IFactoryInstantiator.NAME);
		factoryCombo.setItemSelector("div.command-factory");  
		factoryCombo.setTemplate(getTemplate());  
	    factoryCombo.setWidth(200);
	    factoryCombo.setMinListWidth(500);
	    factoryCombo.setPageSize(5);
		factoryCombo.setTypeAhead(true);
		factoryCombo.setTriggerAction(TriggerAction.ALL);
		factoryCombo.addSelectionChangedListener(new SelectionChangedProxy<IFactoryInstantiator<?>>(this));
		cancelButton = new Button(constants.cancelButton());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				cancelCommandFactory();
			}
		});
		cancelButton.setEnabled(false);
		undoButton = new SplitButton();
		undoButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.undo()));
		undoButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				undoCommand();
			}
		});
		undoMenu = new Menu();
		undoButton.setMenu(undoMenu);
		redoButton = new SplitButton();
		redoButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.redo()));
		redoButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				redoCommand();
			}
		});
		redoMenu = new Menu();
		redoButton.setMenu(redoMenu);
		status = new Status();
		Label strokeLabel = new Label(constants.stroke() + ":");
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SVGModel model = VectomaticApp2.getApp().getActiveModel();
				if (model != null) {
					model.getSelectionModel().deselectAll();
				}
				VectomaticApp2.getApp().inspector();
			}
		};
		strokeCell = new PaintCell();
		strokeCell.getStyle().setWidth(15, Unit.PX);
		strokeCell.getStyle().setHeight(15, Unit.PX);
		strokeCell.addClickHandler(clickHandler);
		strokeCell.getElement().getStyle().setPosition(Position.RELATIVE);
		strokeCell.getElement().getStyle().setTop(2, Unit.PX);
		Label fillLabel = new Label(constants.fill() + ":");
		fillCell = new PaintCell();
		fillCell.getStyle().setWidth(15, Unit.PX);
		fillCell.getStyle().setHeight(15, Unit.PX);
		fillCell.addClickHandler(clickHandler);
		fillCell.getElement().getStyle().setPosition(Position.RELATIVE);
		fillCell.getElement().getStyle().setTop(2, Unit.PX);
		updatePaint();
		VectomaticApp2.getApp().getCssContext().addChangeListener(new ChangeListener() {
			@Override
			public void modelChanged(ChangeEvent event) {
				updatePaint();
			}
		});
		showGridItem = new CheckMenuItem(AppConstants.INSTANCE.showGrid());
		showGridItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Grid grid = VectomaticApp2.getApp().getActiveModel().getGrid();
				grid.setShowsGrid(((CheckMenuItem)ce.getItem()).isChecked());
			}
		});
		showGuidesItem = new CheckMenuItem(AppConstants.INSTANCE.showGuides());
		showGuidesItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Grid grid = VectomaticApp2.getApp().getActiveModel().getGrid();
				grid.setShowsGuides(((CheckMenuItem)ce.getItem()).isChecked());
			}
		});
		snapToGridItem = new CheckMenuItem(AppConstants.INSTANCE.snapToGrid());
		snapToGridItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Grid grid = VectomaticApp2.getApp().getActiveModel().getGrid();
				grid.setSnapsToGrid(((CheckMenuItem)ce.getItem()).isChecked());
			}
		});
		Menu gridMenu = new Menu();
		gridMenu.add(showGridItem);
		gridMenu.add(showGuidesItem);
		gridMenu.add(snapToGridItem);
		gridButton = new SplitButton(AppConstants.INSTANCE.grid());
		gridButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.grid()));
		gridButton.setMenu(gridMenu);
		
		add(strokeLabel);
		add(new WidgetComponent(strokeCell));
		add(fillLabel);
		add(new WidgetComponent(fillCell));
		add(new SeparatorToolItem());
		add(gridButton);
		add(new SeparatorToolItem());
		add(factoryCombo);
		add(new SeparatorToolItem());
		add(undoButton);
		add(redoButton);
		add(new SeparatorToolItem());
		add(cancelButton);
		add(new SeparatorToolItem());
		add(status);
		VectomaticApp2.getApp().addActivateWindowHandler(this);
		VectomaticApp2.getApp().addDeactivateWindowHandler(this);
	}
	
	private void updatePaint() {
		CssContextModel context = VectomaticApp2.getApp().getCssContext();
		strokeCell.setPaint(context.<OMSVGPaint>get(SVGConstants.CSS_STROKE_PROPERTY));
		fillCell.setPaint(context.<OMSVGPaint>get(SVGConstants.CSS_FILL_PROPERTY));
	}
	
	@Override
	public boolean processSelectionChanged(SelectionChangedEvent<IFactoryInstantiator<?>> se) {
		List<IFactoryInstantiator<?>> selection = se.getSelection();
		GWT.log("CommandFactoryToolBar.selectionChanged(" + selection + ")");
		if (!silent) {
			if (selection.size() == 1) {
				// Terminate current factory if needed.
				ICommandFactory factory = factorySelector.getActiveFactory();
				if (factory != null) {
					factory.stop();
				}
				selection.get(0).create().start(CommandFactoryToolBar.this);
			}
		}
		factoryCombo.triggerBlur();
		return true;
	}

	private native String getTemplate() /*-{ 
		return [ 
		'<tpl for="."><div class="command-factory">', 
		'<h3><span>{name}</h3>', 
		'{description}', 
		'</div></tpl>' 
		].join(""); 
	}-*/;

	@Override
	public void onChange(CommandFactorySelectorChangeEvent event) {
		silent = true;
		ICommandFactory factory = event.getCommandFactory();
		GWT.log("CommandFactoryToolBar.onChange(" + factory + ")");
		if (factory != null) {
			factoryCombo.disableEvents(true);
			factoryCombo.setValue(factory.getInstantiator());
			factoryCombo.disableEvents(false);
			status.setText(factory.getStatus());
		} else {
			factoryCombo.setValue(null);
			factoryCombo.clearSelections();
			status.setText("");
		}
		cancelButton.setEnabled(factorySelector.getActiveFactory() != null);
		silent = false;
	}

	private void updateUndoRedo() {
		GWT.log("CommandFactoryToolBar.updateUndoRedo()");
		undoButton.setEnabled(currentCommandStore != null && currentCommandStore.canUndo());
		redoButton.setEnabled(currentCommandStore != null && currentCommandStore.canRedo());
		if (currentCommandStore != null) {
			String undoTooltip = null;
			if (currentCommandStore.canUndo()) {
				undoMenu.removeAll();
				for (BeanModel command : currentCommandStore.getUndoCommands()) {
					undoMenu.add(new UndoCommandMenuItem(currentCommandStore, command));
				}
				undoButton.setMenu(undoMenu);
				undoTooltip = Format.capitalize(
					AppConstants.INSTANCE.undoButton() 
					+ " " 
					+ ((ICommand)currentCommandStore.getUndoCommand().getBean()).getDescription());
			}
			undoButton.setToolTip(undoTooltip);
			String redoTooltip = null;
			if (currentCommandStore.canRedo()) {
				redoMenu.removeAll();
				for (BeanModel command : currentCommandStore.getRedoCommands()) {
					redoMenu.add(new RedoCommandMenuItem(currentCommandStore, command));
				}
				redoButton.setMenu(redoMenu);
				redoTooltip = Format.capitalize(
					AppConstants.INSTANCE.redoButton() 
					+ " " 
					+ ((ICommand)currentCommandStore.getRedoCommand().getBean()).getDescription());
			}
			redoButton.setToolTip(redoTooltip);
		}
	}

	public void updateStatus() {
		ICommandFactory currentFactory = factorySelector.getActiveFactory();
		status.setText(currentFactory != null ? currentFactory.getStatus() : "");
	}
	
	private void cancelCommandFactory() {
		GWT.log("CommandFactoryToolBar.cancelCommandFactory()");
		factorySelector.getActiveFactory().stop();
	}
	
	private void undoCommand() {
		GWT.log("CommandFactoryToolBar.undoCommand()");
		currentCommandStore.undo();
	}

	private void redoCommand() {
		GWT.log("CommandFactoryToolBar.redoCommand()");
		currentCommandStore.redo();
	}

	@Override
	public void onDeactivate(DeactivateWindowEvent event) {
		GWT.log("CommandFactoryToolBar.onDeactivate");
		event.getWindow().getSvgModel().getCommandStore().removeStoreListener(commandStoreListener);

		// Udpate undo / redo menus
		currentCommandStore = null;
		updateUndoRedo();
		
		// Update the grid menu
		gridButton.setEnabled(false);
	}

	@Override
	public void onActivate(ActivateWindowEvent event) {
		GWT.log("CommandFactoryToolBar.onActivate");
		SVGModel model = event.getWindow().getSvgModel();
		
		// Udpate undo / redo menus
		currentCommandStore = model.getCommandStore();
		currentCommandStore.addStoreListener(commandStoreListener);
		updateUndoRedo();
		
		// Update the grid menu
		Grid grid = model.getGrid();
		showGridItem.setChecked(grid.showsGrid(), true);
		showGuidesItem.setChecked(grid.showsGuides(), true);
		snapToGridItem.setChecked(grid.snapsToGrid(), true);
		gridButton.setEnabled(true);
	}
}
