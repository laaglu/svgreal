/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.svg.edit.client.command.CommandFactories;
import org.vectomatic.svg.edit.client.command.CommandFactorySelector;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.event.ActivateWindowEvent;
import org.vectomatic.svg.edit.client.event.ActivateWindowHandler;
import org.vectomatic.svg.edit.client.event.DeactivateWindowEvent;
import org.vectomatic.svg.edit.client.event.DeactivateWindowHandler;
import org.vectomatic.svg.edit.client.event.HasActivateWindowHandlers;
import org.vectomatic.svg.edit.client.event.HasDeactivateWindowHandlers;
import org.vectomatic.svg.edit.client.gxt.panels.CommandFactoryToolBar;
import org.vectomatic.svg.edit.client.gxt.widget.RecentDocMenuItem;
import org.vectomatic.svg.edit.client.gxt.widget.ViewportExt;
import org.vectomatic.svg.edit.client.inspector.InspectorWindow;
import org.vectomatic.svg.edit.client.load.FileLoadRequest;
import org.vectomatic.svg.edit.client.load.ILoadRequest;
import org.vectomatic.svg.edit.client.load.InternalLoadRequest;
import org.vectomatic.svg.edit.client.load.NewDocRequest;
import org.vectomatic.svg.edit.client.load.RSSReader;
import org.vectomatic.svg.edit.client.load.UrlLoadRequest;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.svg.CssContextModel;
import org.vectomatic.svg.edit.client.model.svg.SVGCircleElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGEllipseElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolygonElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolylineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGRectElementModel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Application main class.
 * @author laaglu
 */
public class VectomaticApp2 implements EntryPoint, HasActivateWindowHandlers, HasDeactivateWindowHandlers {
	/**
	 * Application singleton
	 */
	private static VectomaticApp2 instance;
	/**
	 * Application-wide event bus
	 */
	private EventBus eventBus;
	/**
	 * List of open documents
	 */
	private List<SVGWindow> svgWindows;
	/**
	 * Currently active document
	 */
	private SVGWindow activeWindow;
	/**
	 * Open URL panel
	 */
	private MessageBox openUrlBox;
	/**
	 * OpenClipArt RSS feed reader
	 */
	private RSSReader rssReader;
	/**
	 * About panel
	 */
	private AboutDialog aboutDialog;
	/**
	 * Inspector
	 */
	private InspectorWindow inspectorWindow;
	/**
	 * The command factory selector
	 */
	private CommandFactorySelector commandFactorySelector;
	/**
	 * The command toolbar at the bottom of the screen
	 */
	private CommandFactoryToolBar commandToolBar;
	/**
	 * To process local file open requests
	 */
	FileUploadExt fileUpload;
	/**
	 * CSS context model to provide default values
	 * when creating new SVG elements
	 */
	private CssContextModel cssContext;
	
	private List<MetaModel<?>> metaModels;
	private int windowX, windowY;
	private ViewportExt viewport;
	private Menu recentDocsMenu;
	private MenuItem resetViewItem;
	private MenuItem tileWindowsItem;
	private MenuItem stackWindowsItem;
	private MenuItem closeWindowItem;
	private MenuItem exportAsSvgMenuItem;
	private SelectionListener<MenuEvent> dispatcher;
	
	public VectomaticApp2() {
		
	}

	public void onModuleLoad() {
		instance = this;
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable) {
				GWT.log("Uncaught exception", throwable);
				if (!GWT.isScript()) {
					String text = "Uncaught exception: ";
					while (throwable != null) {
						StackTraceElement[] stackTraceElements = throwable
								.getStackTrace();
						text += throwable.toString() + "\n";
						for (int i = 0; i < stackTraceElements.length; i++) {
							text += "    at " + stackTraceElements[i] + "\n";
						}
						throwable = throwable.getCause();
						if (throwable != null) {
							text += "Caused by: ";
						}
					}
					DialogBox dialogBox = new DialogBox(true);
					DOM.setStyleAttribute(dialogBox.getElement(),
							"backgroundColor", "#ABCDEF");
					System.err.print(text);
					text = text.replaceAll(" ", "&nbsp;");
					dialogBox.setHTML("<pre>" + text + "</pre>");
					dialogBox.center();
				}
			}
		});
		AppBundle.INSTANCE.css().ensureInjected();
		
		// Create graphical context
		OMSVGDocument doc = OMSVGParser.currentDocument();
		SVGElement element = doc.createSVGPathElement().getElement().cast();
		element.getStyle().setProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_LIGHTBLUE_VALUE);
		element.getStyle().setProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
		SVGNamedElementModel.createTitleDesc(element, AppConstants.INSTANCE.graphicalContext());
		cssContext = new CssContextModel(element);

		svgWindows = new ArrayList<SVGWindow>();
		viewport = new ViewportExt();

		viewport.setLayout(new BorderLayout());
		viewport.add(createMenuBar(), new BorderLayoutData(LayoutRegion.NORTH, getWindowBarHeight()));
		viewport.setStyleAttribute("background-color", SVGConstants.CSS_BEIGE_VALUE);
		
		commandToolBar = new CommandFactoryToolBar(CommandFactories.getAllFactoriesStore(), getCommandFactorySelector());
		ContentPanel commandPanel = new ContentPanel();
		commandPanel.setHeaderVisible(false);
		commandPanel.setBottomComponent(commandToolBar);
		viewport.add(commandPanel, new BorderLayoutData(LayoutRegion.SOUTH, getWindowBarHeight()));

		new InternalLoadRequest(AppBundle.INSTANCE.fish(), "fish.svg").load();
//		new InternalLoadRequest(AppBundle.INSTANCE.fries(), "fries.svg").load();
		new InternalLoadRequest(AppBundle.INSTANCE.chess(), "chess.svg").load();
		new InternalLoadRequest(AppBundle.INSTANCE.sample(), "sample.svg").load();

		update();
		
		fileUpload = new FileUploadExt();
		Style style = fileUpload.getElement().getStyle();
		style.setVisibility(Visibility.HIDDEN);
		style.setWidth(0, Unit.PX);
		style.setHeight(0, Unit.PX);
		fileUpload.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				processFiles(fileUpload.getFiles());				
			}
		});
		
		RootPanel.get().add(viewport);
		RootPanel.get().add(fileUpload);
	}
	
	private MenuBar createMenuBar() {
		Menu fileMenu = new Menu();
		final MenuItem newDocumentMenuItem = new MenuItem(AppConstants.INSTANCE.newDocumentMenuItem());
		fileMenu.add(newDocumentMenuItem);
		final MenuItem openUrlItem = new MenuItem(AppConstants.INSTANCE.openUrlMenuItem());
		fileMenu.add(openUrlItem);
		final MenuItem openLocalMenuItem = new MenuItem(AppConstants.INSTANCE.openLocalMenuItem());
		fileMenu.add(openLocalMenuItem);
		final MenuItem openRssFeedItem = new MenuItem(AppConstants.INSTANCE.openRssFeedMenuItem());
		fileMenu.add(openRssFeedItem);
		exportAsSvgMenuItem = new MenuItem(AppConstants.INSTANCE.exportAsSvgMenuItem());
		fileMenu.add(exportAsSvgMenuItem);
		MenuItem recentDocumentsItem = new MenuItem(AppConstants.INSTANCE.recentDocumentsMenuItem());
		recentDocsMenu = new Menu();
		recentDocumentsItem.setSubMenu(recentDocsMenu);
		fileMenu.add(recentDocumentsItem);
		
		Menu windowMenu = new Menu();
		resetViewItem = new MenuItem(AppConstants.INSTANCE.resetViewMenuItem());
		windowMenu.add(resetViewItem);
		windowMenu.add(new SeparatorMenuItem());
		tileWindowsItem = new MenuItem(AppConstants.INSTANCE.tileWindowsMenuItem());
		windowMenu.add(tileWindowsItem);
		stackWindowsItem = new MenuItem(AppConstants.INSTANCE.stackWindowsMenuItem());
		windowMenu.add(stackWindowsItem);
		windowMenu.add(new SeparatorMenuItem());
		closeWindowItem = new MenuItem(AppConstants.INSTANCE.closeWindowMenuItem());
		windowMenu.add(closeWindowItem);
		
		Menu toolsMenu = new Menu();
		final MenuItem inspectorMenuItem = new MenuItem(AppConstants.INSTANCE.inspectorMenuItem());
		toolsMenu.add(inspectorMenuItem);
		
		Menu aboutMenu = new Menu();
		final MenuItem aboutItem = new MenuItem(AppConstants.INSTANCE.aboutMenuItem());
		aboutMenu.add(aboutItem);

		MenuBar menuBar = new MenuBar();
		menuBar.setBorders(true);  
		menuBar.setStyleAttribute("borderTop", "none");
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.fileMenu(), fileMenu));
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.windowMenu(), windowMenu));
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.toolsMenu(), toolsMenu));
		menuBar.add(new MenuBarItem(AppConstants.INSTANCE.aboutMenu(), aboutMenu));
		
		dispatcher = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				MenuItem item = (MenuItem) me.getItem();
				if (item == newDocumentMenuItem) {
					newDocument();
				} else if (item == openUrlItem) {
					openUrl();
				} else if (item == openLocalMenuItem) {
					openLocal();
				} else if (item == openRssFeedItem) {
					openRssFeed();
				} else if (item == exportAsSvgMenuItem) {
					exportAsSvg();
				} else if (item == resetViewItem) {
					resetView();
				} else if (item == tileWindowsItem) {
					tileWindows();
				} else if (item == stackWindowsItem) {
					stackWindows();
				} else if (item == closeWindowItem) {
					closeWindow(activeWindow);
				} else if (item == inspectorMenuItem) {
					inspector();
				} else if (item == aboutItem) {
					about();
				}
			}
		};
		newDocumentMenuItem.addSelectionListener(dispatcher);
		openUrlItem.addSelectionListener(dispatcher);
		openLocalMenuItem.addSelectionListener(dispatcher);
		openRssFeedItem.addSelectionListener(dispatcher);
		exportAsSvgMenuItem.addSelectionListener(dispatcher);
		resetViewItem.addSelectionListener(dispatcher);
		tileWindowsItem.addSelectionListener(dispatcher);
		stackWindowsItem.addSelectionListener(dispatcher);
		closeWindowItem.addSelectionListener(dispatcher);
		inspectorMenuItem.addSelectionListener(dispatcher);
		aboutItem.addSelectionListener(dispatcher);
		return menuBar;
	}
	

	public SVGWindow addWindow(OMSVGSVGElement svg, ILoadRequest request) {
		String title = request.getTitle();
		SVGModel model = SVGModel.newInstance(svg, title, SVGProcessor.newIdPrefix());
		SVGWindow window = new SVGWindow(model);
		window.setHeading(title);
		svgWindows.add(window);
		// To be notified when a window is activated in order to
		// keep track of the active window 
		window.addListener(Events.Activate, new Listener<WindowEvent>() {

			@Override
			public void handleEvent(WindowEvent we) {
				SVGWindow svgWindow = (SVGWindow) we.getWindow();
				GWT.log("VectomaticApp2.Activate(" + svgWindow.getHeading() + ")");
				if (activeWindow != svgWindow) {
					if (activeWindow != null) {
						activeWindow.deactivate();
						fireEvent(new DeactivateWindowEvent(activeWindow));
					}
					activeWindow = svgWindow;
					activeWindow.activate();
					fireEvent(new ActivateWindowEvent(activeWindow));
				}
			}
		});
		window.addListener(Events.BeforeHide, new Listener<WindowEvent>() {

			@Override
			public void handleEvent(WindowEvent we) {
				SVGWindow svgWindow = (SVGWindow) we.getWindow();
				svgWindow.getSvgModel().getSelectionModel().deselectAll();
				GWT.log("VectomaticApp2.BeforeHide(" + svgWindow.getHeading() + ")");
				svgWindow.removeAllListeners();
				svgWindow.deactivate();
				fireEvent(new DeactivateWindowEvent(svgWindow));
				svgWindows.remove(svgWindow);
				activeWindow = null;
				update();
			}
		});

		// Update the recent docs menu
		if (!(request instanceof NewDocRequest)) {
			List<Component> recentDocs = recentDocsMenu.getItems();
			boolean alreadyInRecentDocs = false;
			for (Component item : recentDocs) {
				RecentDocMenuItem menuItem = (RecentDocMenuItem)item;
				if (request.equals(menuItem.getRequest())) {
					alreadyInRecentDocs = true;
					break;
				}
			}
			if (!alreadyInRecentDocs) {
				if (recentDocs.size() >= 8) {
					Component oldestItem = recentDocs.get(0);
					oldestItem.removeAllListeners();
					recentDocsMenu.remove(oldestItem);
				}
				RecentDocMenuItem recentDocItem = new RecentDocMenuItem(request);
				recentDocsMenu.add(recentDocItem);
			}
		}

		
		int windowBarHeight = getWindowBarHeight();
		windowY += windowBarHeight;
		window.setPagePosition(windowX, windowY);
		windowX += windowBarHeight;
//		window.show();
		viewport.add(window);
		window.setVisible(true);
		update();
		return window;
	}

	public void newDocument() {
		GWT.log("newDocument()");
		new NewDocRequest().load();
	}
	
	public void openUrl() {
		GWT.log("openUrl()");
		openUrlBox = MessageBox.prompt(AppConstants.INSTANCE.openUrlMenuItem(), AppConstants.INSTANCE.openUrlText());
		openUrlBox.addCallback(new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent be) {
				new UrlLoadRequest(be.getValue()).load();
			}  
		});  
	}
	
	public void info(String command, String message) {
		Info.display(command, message);  
	}
	
	public void openRssFeed() {
		GWT.log("openRssFeed()");
		if (rssReader == null) {
			rssReader = new RSSReader();
		}
		rssReader.show();
	}

	private void openLocal() {
		GWT.log("openLocal()");
		fileUpload.click();
	}
	
	private void processFiles(FileList files) {
		for (File file : files) {
			String type = file.getType();
			if ("image/svg+xml".equals(type)) {
				new FileLoadRequest(file).load();
			}
		}		
	}

	protected void exportAsSvg() {
		GWT.log("exportAsSvg()");
		SVGModel model = getActiveModel();
		String markup = model.getMarkup();
		String url = "data:image/svg+xml;base64," + base64encode(markup);
		String title = ((SVGNamedElementModel)model.getRoot()).getName();
		com.google.gwt.user.client.Window.open(url, title, "");
	}
	
	private static native String base64encode(String str) /*-{
		return $wnd.btoa(str);
	}-*/;

	
	public void resetView() {
		GWT.log("resetView()");
		activeWindow.setRotationCompass(0);
		activeWindow.setScaleSlider(50);
	}
	private List<Window> getAllWindows() {
		List<Window> windows = new ArrayList<Window>(svgWindows);
		if (inspectorWindow != null && inspectorWindow.isVisible()) {
			windows.add(inspectorWindow);
		}
		return windows;
	}
	public void tileWindows() {
		GWT.log("tileWindows()");
		List<Window> windows = getAllWindows();
		Rectangle rect = getRectangle();
		int count = windows.size();
		int cols = (int)Math.ceil(Math.sqrt(count));
		int rows = (int)Math.ceil((double)count/cols);
		GWT.log("cols=" + cols + "; rows=" + rows);
		Size windowSize = new Size(rect.width / cols, rect.height / rows);
		int index = 0;
		for (Window window : windows) {
			window.setSize(windowSize.width, windowSize.height);
			int x = index % cols;
			int y = index / cols;
			window.setPagePosition(rect.x + x * windowSize.width, rect.y + y * windowSize.height);
			index++;
		}
	}
	public void stackWindows() {
		GWT.log("stackWindows()");
		List<Window> windows = getAllWindows();
		Rectangle rect = getRectangle();
		Size size = viewport.getSize();
		Size windowSize = new Size((int)(size.width * 0.75f), (int)(size.height * 0.75f));
		int windowBarHeight = getWindowBarHeight();
		int index = 0;
		for (Window window : windows) {
			window.setSize(windowSize.width, windowSize.height);
			int x = rect.x + index * windowBarHeight;
			int y = rect.y + index * windowBarHeight;
			window.setPagePosition(x, y);
			index++;
			if (y + windowSize.height > size.height) {
				x = rect.x;
				y = rect.y;
				index = 0;
			}
		}
	}
	public void closeWindow(SVGWindow window) {
		GWT.log("closeWindow()");
		if (window != null) {
			window.hide();
		}
	}
	
	public SVGWindow getActiveWindow() {
		return activeWindow;
	}
	
	public SVGModel getActiveModel() {
		if (activeWindow != null) {
			return activeWindow.getSvgModel();
		}
		return null;
	}
	
	public SVGWindow getWindow(SVGElement element) {
		SVGSVGElement svg = element.getOwnerSVGElement();
		for (SVGWindow svgWindow : svgWindows) {
			if (svg == svgWindow.getSvgModel().getDocumentRoot().getElement()) {
				return svgWindow;
			}
		}
		return null;
	}

	public void about() {
		GWT.log("about()");
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog();
		}
		aboutDialog.show();
	}
	public Rectangle getRectangle() {
		int windowBarHeight = getWindowBarHeight();
		Size commandBarSize = commandToolBar.getSize();
		Size viewPortSize = viewport.getSize();
		Rectangle rect = new Rectangle(0, windowBarHeight, viewPortSize.width, viewPortSize.height - windowBarHeight - commandBarSize.height);
		return rect;
	}
	public static final int getWindowBarHeight() {
		return 27;
	}
	
	private void update() {
		boolean hasActiveWindow = svgWindows.size() > 0;
		exportAsSvgMenuItem.setEnabled(hasActiveWindow);
		closeWindowItem.setEnabled(hasActiveWindow);
		tileWindowsItem.setEnabled(hasActiveWindow);
		stackWindowsItem.setEnabled(hasActiveWindow);
		resetViewItem.setEnabled(hasActiveWindow);
	}
	
    public static final native void log(String msg) /*-{
	    console.log(msg);
	}-*/;
    
    public void inspector() {
    	GWT.log("inspector()");
    	if (inspectorWindow == null) {
    		inspectorWindow = new InspectorWindow();
    	}
    	inspectorWindow.show();
    	if (activeWindow != null) {
    		activeWindow.updateSelectionListeners();
    	}
    }
    
    public CssContextModel getCssContext() {
    	return cssContext;
    }
    
	public List<MetaModel<?>> getMetaModels() {
		if (metaModels == null) {
			metaModels = new ArrayList<MetaModel<?>>();
			metaModels.add(SVGLineElementModel.getLineElementMetaModel());
			metaModels.add(SVGCircleElementModel.getCircleElementMetaModel());
			metaModels.add(SVGEllipseElementModel.getEllipseElementMetaModel());
			metaModels.add(SVGRectElementModel.getRectElementMetaModel());
			metaModels.add(SVGPolygonElementModel.getPolygonElementMetaModel());
			metaModels.add(SVGPolylineElementModel.getPolylineElementMetaModel());
			metaModels.add(SVGImageElementModel.getImageElementMetaModel());
		}
		return metaModels;
	}
	
	/**
	 * Returns the global command factory selector
	 * @return
	 */
	public CommandFactorySelector getCommandFactorySelector() {
		if (commandFactorySelector == null) {
		    commandFactorySelector = new CommandFactorySelector();		
		}
		return commandFactorySelector;
	}
	/**
	 * Returns the application event bus
	 * @return the application event bus
	 */
	public EventBus getEventBus() {
		if (eventBus == null) {
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}
	
	public CommandFactoryToolBar getCommandToolBar() {
		return commandToolBar;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}

	@Override
	public HandlerRegistration addDeactivateWindowHandler(
			DeactivateWindowHandler handler) {
		return eventBus.addHandlerToSource(DeactivateWindowEvent.getType(), this, handler);
	}

	@Override
	public HandlerRegistration addActivateWindowHandler(
			ActivateWindowHandler handler) {
		return eventBus.addHandlerToSource(ActivateWindowEvent.getType(), this, handler);
	}

	public static VectomaticApp2 getApp() {
		if (instance == null) {
			instance = new VectomaticApp2();
		}
		return instance;
	}
}
