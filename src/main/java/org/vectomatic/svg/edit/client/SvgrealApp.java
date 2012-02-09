/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGResource;
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
import org.vectomatic.svg.edit.client.utils.DecoratedImageCache;
import org.vectomatic.svg.edit.client.utils.ImageLoader;

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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuBarItem;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Application main class.
 * @author laaglu
 */
public class SvgrealApp implements EntryPoint, HasActivateWindowHandlers, HasDeactivateWindowHandlers {
	/**
	 * The locale URL parameter
	 */
	private static final String PARAM_LOCALE = "locale";
	/**
	 * The gwt.codesvr URL parameter
	 */
	private static final String PARAM_CODE_SERVER = "gwt.codesvr";
	/**
	 * The demo URL parameter
	 */
	private static final String PARAM_DEMO = "demo";
	/**
	 * Application singleton
	 */
	private static SvgrealApp instance;
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
	/**
	 * A cache for decorated treeview icons
	 */
	private DecoratedImageCache imageCache;
	
	private List<MetaModel<?>> metaModels;
	private int windowX, windowY;
	private ViewportExt viewport;
	private Menu recentDocsMenu;
	private MenuItem resetViewItem;
	private MenuItem tileWindowsItem;
	private MenuItem stackWindowsItem;
	private MenuItem closeWindowItem;
	private MenuItem exportAsSvgMenuItem;
	private Menu displayWindowMenu;
	private MenuItem displayWindowMenuItem;
	private Map<String, ImageResource> localeToIcon;
	/**
	 * To process menu item selection events
	 */
	private SelectionListener<MenuEvent> dispatcher;
	private Menu languageMenu;
	
	public SvgrealApp() {
		
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
		HBoxLayout hboxLayout = new HBoxLayout();
		hboxLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);  
        LayoutContainer menuBarContainer = new LayoutContainer(hboxLayout);
		HBoxLayoutData layoutData = new HBoxLayoutData();
		layoutData.setFlex(1);
		menuBarContainer.add(createMenuBar(), layoutData);
		menuBarContainer.add(createLanguageBar(), new HBoxLayoutData());
		viewport.add(menuBarContainer, new BorderLayoutData(LayoutRegion.NORTH, getWindowBarHeight()));
		viewport.setStyleAttribute("background-color", SVGConstants.CSS_BEIGE_VALUE);
		
		commandToolBar = new CommandFactoryToolBar(CommandFactories.getAllFactoriesStore(), getCommandFactorySelector());
		ContentPanel commandPanel = new ContentPanel();
		commandPanel.setHeaderVisible(false);
		commandPanel.setBottomComponent(commandToolBar);
		viewport.add(commandPanel, new BorderLayoutData(LayoutRegion.SOUTH, getWindowBarHeight()));


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
		
		
		ImageLoader loader = new ImageLoader();
		loader.addInitializeHandler(new InitializeHandler() {
			
			@Override
			public void onInitialize(InitializeEvent event) {
				ImageLoader loader = (ImageLoader) event.getSource();
				imageCache = new DecoratedImageCache(loader.getImages());
				init();
			}
		});
		loader.loadImages(new ImageResource[]{
			AppBundle.INSTANCE.altGlyphDef(), 
			AppBundle.INSTANCE.altGlyphItem(), 
			AppBundle.INSTANCE.altGlyph(), 
			AppBundle.INSTANCE.animateColor(), 
			AppBundle.INSTANCE.animateMotion(), 
			AppBundle.INSTANCE.animate(), 
			AppBundle.INSTANCE.animateTransform(), 
			AppBundle.INSTANCE.circle(), 
			AppBundle.INSTANCE.clipPath(), 
			AppBundle.INSTANCE.colorProfile(), 
			AppBundle.INSTANCE.cursor(), 
			AppBundle.INSTANCE.defs(), 
			AppBundle.INSTANCE.desc(), 
			AppBundle.INSTANCE.ellipse(), 
			AppBundle.INSTANCE.feBlend(), 
			AppBundle.INSTANCE.feColorMatrix(), 
			AppBundle.INSTANCE.feComponentTransfer(), 
			AppBundle.INSTANCE.feComposite(), 
			AppBundle.INSTANCE.feConvolveMatrix(), 
			AppBundle.INSTANCE.feDiffuseLighting(), 
			AppBundle.INSTANCE.feDisplacementMap(), 
			AppBundle.INSTANCE.feDistantLight(), 
			AppBundle.INSTANCE.feFlood(), 
			AppBundle.INSTANCE.feFuncA(), 
			AppBundle.INSTANCE.feFuncB(), 
			AppBundle.INSTANCE.feFuncG(), 
			AppBundle.INSTANCE.feFuncR(), 
			AppBundle.INSTANCE.feGaussianBlur(), 
			AppBundle.INSTANCE.feMergeNode(), 
			AppBundle.INSTANCE.feMerge(), 
			AppBundle.INSTANCE.feMorphology(), 
			AppBundle.INSTANCE.feOffset(), 
			AppBundle.INSTANCE.fePointLight(), 
			AppBundle.INSTANCE.feSpecularLight(), 
			AppBundle.INSTANCE.feSpotLight(), 
			AppBundle.INSTANCE.feTile(), 
			AppBundle.INSTANCE.feTurbulence(), 
			AppBundle.INSTANCE.filter(), 
			AppBundle.INSTANCE.fontFaceFormat(), 
			AppBundle.INSTANCE.fontFaceName(), 
			AppBundle.INSTANCE.fontFace(), 
			AppBundle.INSTANCE.fontFaceSrc(), 
			AppBundle.INSTANCE.fontFaceUri(), 
			AppBundle.INSTANCE.font(), 
			AppBundle.INSTANCE.foreignObject(), 
			AppBundle.INSTANCE.glyph(), 
			AppBundle.INSTANCE.glyphRef(), 
			AppBundle.INSTANCE.g(), 
			AppBundle.INSTANCE.hkern(), 
			AppBundle.INSTANCE.image(), 
			AppBundle.INSTANCE.linearGradient(), 
			AppBundle.INSTANCE.line(), 
			AppBundle.INSTANCE.marker(), 
			AppBundle.INSTANCE.mask(), 
			AppBundle.INSTANCE.metadata(), 
			AppBundle.INSTANCE.missingGlyph(), 
			AppBundle.INSTANCE.mpath(), 
			AppBundle.INSTANCE.path(), 
			AppBundle.INSTANCE.pattern(), 
			AppBundle.INSTANCE.polygon(), 
			AppBundle.INSTANCE.polyline(), 
			AppBundle.INSTANCE.radialGradient(), 
			AppBundle.INSTANCE.rect(), 
			AppBundle.INSTANCE.script(), 
			AppBundle.INSTANCE.set(), 
			AppBundle.INSTANCE.stop(), 
			AppBundle.INSTANCE.style(), 
			AppBundle.INSTANCE.svg(), 
			AppBundle.INSTANCE.switch_(), 
			AppBundle.INSTANCE.symbol(), 
			AppBundle.INSTANCE.textPath(), 
			AppBundle.INSTANCE.text(), 
			AppBundle.INSTANCE.title(), 
			AppBundle.INSTANCE.tref(), 
			AppBundle.INSTANCE.tspan(), 
			AppBundle.INSTANCE.use(), 
			AppBundle.INSTANCE.view(), 
			AppBundle.INSTANCE.vkern(), 			
			AppBundle.INSTANCE.error(), 			
			AppBundle.INSTANCE.warning(), 			
		});
	}
	
	private void init() {
		// Open demo documents if requested by the demo URL parameter
		String demo = getParameter(PARAM_DEMO);
		if (demo != null) {
			SVGResource[] demoResources = {
					AppBundle.INSTANCE.fish(),
					AppBundle.INSTANCE.fries(),
					AppBundle.INSTANCE.chess(),
					AppBundle.INSTANCE.sample()
				};
				String[] demoNames = {
					"fish.svg",
					"fries.svg",
					"chess.svg",
					"sample.svg"
				};
			for (int i = 0; i < demoResources.length; i++) {
				if (demo.indexOf(Integer.toString(i)) != -1) {
					new InternalLoadRequest(demoResources[i], demoNames[i]).load();
				}
			}
		}
	}
	
	private MenuBar createMenuBar() {
		Menu fileMenu = new Menu();
		AppConstants cst = AppConstants.INSTANCE;
		final MenuItem newDocumentMenuItem = new MenuItem(cst.newDocumentMenuItem());
		fileMenu.add(newDocumentMenuItem);
		final MenuItem openUrlItem = new MenuItem(cst.openUrlMenuItem());
		fileMenu.add(openUrlItem);
		final MenuItem openLocalMenuItem = new MenuItem(cst.openLocalMenuItem());
		fileMenu.add(openLocalMenuItem);
		final MenuItem openRssFeedItem = new MenuItem(cst.openRssFeedMenuItem());
		fileMenu.add(openRssFeedItem);
		exportAsSvgMenuItem = new MenuItem(cst.exportAsSvgMenuItem());
		fileMenu.add(exportAsSvgMenuItem);
		MenuItem recentDocumentsItem = new MenuItem(cst.recentDocumentsMenuItem());
		recentDocsMenu = new Menu();
		recentDocumentsItem.setSubMenu(recentDocsMenu);
		fileMenu.add(recentDocumentsItem);
		
		Menu windowMenu = new Menu();
		resetViewItem = new MenuItem(cst.resetViewMenuItem());
		windowMenu.add(resetViewItem);
		windowMenu.add(new SeparatorMenuItem());
		tileWindowsItem = new MenuItem(cst.tileWindowsMenuItem());
		windowMenu.add(tileWindowsItem);
		stackWindowsItem = new MenuItem(cst.stackWindowsMenuItem());
		windowMenu.add(stackWindowsItem);
		displayWindowMenuItem = new MenuItem(cst.displayWindowMenuItem());
		displayWindowMenu = new Menu();
		displayWindowMenuItem.setSubMenu(displayWindowMenu);
		windowMenu.add(displayWindowMenuItem);
		windowMenu.add(new SeparatorMenuItem());
		closeWindowItem = new MenuItem(cst.closeWindowMenuItem());
		windowMenu.add(closeWindowItem);
		
		Menu toolsMenu = new Menu();
		final MenuItem inspectorMenuItem = new MenuItem(cst.inspectorMenuItem());
		toolsMenu.add(inspectorMenuItem);
		
		Menu aboutMenu = new Menu();
		final MenuItem aboutItem = new MenuItem(cst.aboutMenuItem());
		aboutMenu.add(aboutItem);

		MenuBar menuBar = new MenuBar();
		menuBar.setBorders(true);  
		menuBar.setStyleAttribute("borderTop", "none");
		menuBar.add(new MenuBarItem(cst.fileMenu(), fileMenu));
		menuBar.add(new MenuBarItem(cst.windowMenu(), windowMenu));
		menuBar.add(new MenuBarItem(cst.toolsMenu(), toolsMenu));
		menuBar.add(new MenuBarItem(cst.aboutMenu(), aboutMenu));
		
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
				} else if (item.getParentMenu() == displayWindowMenu) {
					for (SVGWindow window : svgWindows) {
						if (window.getHeading().equals(item.getText())) {
							window.setActive(true);
							window.toFront();
							break;
						}
					}
				} else if (item.getParentMenu() == languageMenu) {
					redirect(item.getText());
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
	
	public void redirect(String language) {
		StringBuilder path = new StringBuilder(GWT.getHostPageBaseURL());
		path.append("?");
		path.append(PARAM_LOCALE);
		path.append("=");
		path.append(language);
		path.append(copyParam(PARAM_CODE_SERVER));
		path.append(copyParam(PARAM_DEMO));
	    Location.replace(path.toString());
	}
	
	private static String copyParam(String name) {
		StringBuilder copy = new StringBuilder();
		String value = getParameter(name);
		if (value != null && value.length() > 0) {
			copy.append("&");
			copy.append(name);
			copy.append("=");
			copy.append(value);
		}
		return copy.toString();
	}
	
	private ToolBar createLanguageBar() {
		if (localeToIcon == null) {
			localeToIcon = new HashMap<String, ImageResource>();
			localeToIcon.put("fr", AppBundle.INSTANCE.flagFr());
			localeToIcon.put("en", AppBundle.INSTANCE.flagUs());
		}
		ToolBar toolBar = new ToolBar();
		languageMenu = new Menu();
		for (Map.Entry<String, ImageResource> entry : localeToIcon.entrySet()) {
			MenuItem item = new MenuItem(entry.getKey(), AbstractImagePrototype.create(entry.getValue()));
			item.addSelectionListener(dispatcher);
			languageMenu.add(item);
		}
		String locale = getParameter(PARAM_LOCALE);
		if (!localeToIcon.containsKey(locale)) {
			locale = "en";
		}
		Button languageButton = new Button(locale, AbstractImagePrototype.create((localeToIcon.get(locale))));
		languageButton.setMenu(languageMenu);
		toolBar.add(languageButton);
		toolBar.setWidth(60);
		toolBar.setHeight(getWindowBarHeight() -1);
		return toolBar;
	}
	

	public SVGWindow addWindow(OMSVGSVGElement svg, ILoadRequest request) {
		String title = request.getTitle();
		SVGModel model = SVGModel.newInstance(svg, title, SVGProcessor.newIdPrefix());
		SVGWindow window = new SVGWindow(model);
		window.setHeading(title);
		svgWindows.add(window);
		MenuItem displayItem = new MenuItem(title);
		displayItem.addSelectionListener(dispatcher);
		displayWindowMenu.add(displayItem);
		
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
				for (Component c : displayWindowMenu.getItems()) {
					MenuItem item = (MenuItem)c;
					if (item.getText().equals(svgWindow.getHeading())) {
						displayWindowMenu.remove(item);
						break;
					}
				}
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

	public static String getParameter(String param) {
		return com.google.gwt.user.client.Window.Location.getParameter(param);
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
		displayWindowMenuItem.setEnabled(svgWindows.size() > 0);
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
	
	public DecoratedImageCache getImageCache() {
		return imageCache;
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

	public static SvgrealApp getApp() {
		if (instance == null) {
			instance = new SvgrealApp();
		}
		return instance;
	}
}
