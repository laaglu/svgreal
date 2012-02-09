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

import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.command.EditTitleCommandFactory;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.RotationEvent;
import org.vectomatic.svg.edit.client.event.RotationHandler;
import org.vectomatic.svg.edit.client.gxt.layout.AbsoluteLayerLayout;
import org.vectomatic.svg.edit.client.gxt.layout.AbsoluteLayerLayoutData;
import org.vectomatic.svg.edit.client.gxt.widget.Compass;
import org.vectomatic.svg.edit.client.gxt.widget.KeyNavExt;
import org.vectomatic.svg.edit.client.gxt.widget.SVGTreePanelDragSource;
import org.vectomatic.svg.edit.client.gxt.widget.SVGTreePanelDropTarget;
import org.vectomatic.svg.edit.client.gxt.widget.TreePanelExt;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.event.SelectionService;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Editor;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * GXT window class dedicated to displaying and editing
 * a single SVG image. The window has several layers: the
 * bottom layer contains the SVG image itself and the top
 * layer contains widgets to manipulate it (rotation compass
 * and scale slider).
 * @author laaglu
 */
public class SVGWindow extends Window {
	public static final String WINDOW_ACTIVE_STYLE = "x-window-active";
	/**
	 * The SVG model backing this window
	 */
	private SVGModel svgModel;
	/**
	 * The SVG rotation compass
	 */
	protected Compass compass;
	/**
	 * The SVG scale slider
	 */
	protected Slider scaleSlider;
	/**
	 * The navigation tree
	 */
	protected TreePanelExt<SVGElementModel> tree;
	/**
	 * The contextual menu
	 */
	protected Menu contextMenu;
	/**
	 * True when the context menu is displayed
	 */
	protected boolean displaysContextMenu;
	/**
	 * To control keyboard input
	 */
	protected KeyNavExt<ComponentEvent> keyNav;
	/**
	 * The drag'n'drop source
	 */
	protected SVGTreePanelDragSource dndSource;
	/**
	 * The drag'n'drop source
	 */
	protected SVGTreePanelDropTarget dndTarget;
	/**
	 * Constructor
	 * @param svgModel
	 * The SVG model to display
	 */
	public SVGWindow(final SVGModel svgModel) {
		super();
		keyNav = new KeyNavExt<ComponentEvent>(this) {
			@Override
			public void onKeyPress(ComponentEvent ce) {
				svgModel.onKeyPress(ce);
			}
			@Override
			public void onKeyUp(ComponentEvent ce) {
				svgModel.onKeyUp(ce);
			}
		};
		this.svgModel = svgModel;
		setPlain(true);
		setMaximizable(true);
		setSize(500, 300);
		setMinWidth(200);
		setMinHeight(170);
		
		// Listen to model name changes
		svgModel.getRoot().addChangeListener(new ChangeListener() {
			@Override
			public void modelChanged(ChangeEvent event) {
				String title = event.getSource().get(SVGConstants.SVG_TITLE_ATTRIBUTE);
				String heading = getHeading();
				if (!heading.equals(title)) {
					setHeading(title);
				}
			}			
		});
		
	    /////////////////////////////////////////////////
		// A CSS multi-layer container
	    // The container hierarchy is as follows:
	    // splitterPanel (LayoutContainer + BorderLayout)
	    //   tree (TreePanel)
	    //   layersContainer (LayoutContainer + AbsoluteLayerLayout)
	    //     svgContainer (LayoutContainer)
	    //       image (SVGImage)
	    //     compass (SVGImage)
	    //     scaleSlider (Slider)
	    /////////////////////////////////////////////////
	    LayoutContainer splitterPanel = new LayoutContainer();
	    splitterPanel.setLayout(new BorderLayout());

	    LayoutContainer layersContainer = new LayoutContainer();
		GWT.log("borders: " + getBorders());
	    layersContainer.setLayout(new AbsoluteLayerLayout());

	    // Create the contextual menu
		contextMenu = new Menu();

	    // Create the SVG view
		LayoutContainer svgContainer = new LayoutContainer() {
			@Override
			protected void onShowContextMenu(int x, int y) {
				GWT.log("SVGWindow.onShowContextMenu");
				displaysContextMenu = true;
				svgModel.updateContextMenu(contextMenu);
				super.onShowContextMenu(x, y);
			}
			@Override
			protected void onHideContextMenu() {
				GWT.log("SVGWindow.onHideContextMenu");
				displaysContextMenu = false;
			}
		};
		svgContainer.setLayout(new FitLayout() {
			@Override
			protected void setItemSize(Component item, Size size) {
				GWT.log("setItemSize(" + size + ")");
//				super.setItemSize(item, size);
				svgModel.setWindowRect(size.width, size.height);
			}
		});
		svgContainer.setContextMenu(contextMenu);
	    svgContainer.setScrollMode(Style.Scroll.AUTO);
	    svgContainer.setStyleAttribute("background-color", SVGConstants.CSS_WHITE_VALUE);
	    OMSVGSVGElement svg = svgModel.getDocumentRoot();
	    SVGImage image = new SVGImage(svg) {
	    	protected void onAttach() {
	    		GWT.log("onAttach");
	    		svgModel.onAttach();
	    	}
	    };
	    svgContainer.add(image);
	    layersContainer.add(svgContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_LEFT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		10));
	    
	    
	    // Create the tree view
	    TreeStore<SVGElementModel> treeStore = svgModel.getStore();
		tree = new TreePanelExt<SVGElementModel>(treeStore) {
			@Override
			protected void onShowContextMenu(int x, int y) {
				displaysContextMenu = true;
				svgModel.updateContextMenu(contextMenu);
				super.onShowContextMenu(x, y);
			}
			@Override
			protected void onHideContextMenu() {
				displaysContextMenu = false;
			}
            @Override
            protected void onDoubleClick(TreePanelEvent tpe) {
                TreeNode treeNode = tree.findNode(tpe.getTarget());
                SVGElementModel model = treeNode.getModel();
                renameModel(model);
           }
        };
		tree.setView(new TreePanelView<SVGElementModel>() {
			@Override
			public void onSelectChange(SVGElementModel model, boolean select) {
				super.onSelectChange(model, select);
				if (svgModel.isHighlightingMode()) {
					svgModel.displayTwin(model, select);
				}
			}			
		}); 
		tree.setSelectionModel(svgModel.getSelectionModel());
		tree.setContextMenu(contextMenu);

	    tree.setIconProvider(new ModelIconProvider<SVGElementModel>() {
			@Override
			public AbstractImagePrototype getIcon(SVGElementModel model) {
				return model.getMetaModel().getIcon();
			}
	    	
	    });
//	    tree.setCheckable(true);
	    tree.setWidth(150);  
	    tree.setDisplayProperty(SVGConstants.SVG_TITLE_TAG);
	    tree.setTrackMouseOver(true);
	    tree.setStyleAttribute("background-color", SVGConstants.CSS_WHITE_VALUE);
	    tree.setAutoExpand(true);
//	    ToolTipConfig tooltipConfig = new ToolTipConfig();
//	    tooltipConfig.setTrackMouse(true);
//	    tooltipConfig.setDismissDelay(0);
//	    tree.setToolTip(tooltipConfig);
	    
	    ///////////// Configure highlighting
	    svg.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
	        	if (!displaysContextMenu) {
	        		svgModel.setHighlightingMode(true);
	        	}
			}
		});
	    svg.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
	        	if (!displaysContextMenu) {
	        		svgModel.setHighlightingMode(false);
	        	}
			}
		});
	    tree.addListener(Events.OnMouseOver, new Listener<TreePanelEvent<SVGElementModel>>() {
	        public void handleEvent(TreePanelEvent<SVGElementModel> be) {
//	        	ToolTipConfig tooltipConfig = tree.getToolTip().getToolTipConfig();
//	        	String desc = null;
	        	if (!displaysContextMenu) {
					svgModel.setHighlightingMode(true);
		        	SVGElementModel model = be.getItem();
		        	if (model != null) {
		        		svgModel.highlightModel(model);
//		        		if (model instanceof SVGNamedElementModel) {
//		        			desc = model.get(SVGConstants.SVG_DESC_TAG);
//		        		}
		        	}
		        }
//	        	if (desc != null) {
//	        		if (!desc.equals(tooltipConfig.getTitle())) {
//	        			tooltipConfig.setTitle(desc);
//	        			tree.getToolTip().show();
//	        		}
//	        	} else {
//		        	tree.getToolTip().hide();
//	        	}
	        }
	    });
	    tree.addListener(Events.OnMouseOut, new Listener<TreePanelEvent<SVGElementModel>>() {
	        public void handleEvent(TreePanelEvent<SVGElementModel> be) {
	        	if (!displaysContextMenu) {
	        		svgModel.setHighlightingMode(false);
//        			tree.setToolTip((String)null);
	        	}
	        }
	    });

	    ///////////// Configure drag'n'drop
	    dndSource = new SVGTreePanelDragSource(this);
	    dndTarget = new SVGTreePanelDropTarget(this);
	    dndTarget.setAllowSelfAsSource(true);  
	    dndTarget.setFeedback(Feedback.BOTH);  

	    ///////////// Top-level layout
	    BorderLayoutData layoutData = new BorderLayoutData(LayoutRegion.WEST, 150, 100, 250);  
	    layoutData.setMargins(new Margins(0, 5, 0, 0));  
	    layoutData.setSplit(true);  
	    layoutData.setCollapsible(true);  
	    splitterPanel.add(tree, layoutData);
	    splitterPanel.add(layersContainer, new BorderLayoutData(LayoutRegion.CENTER));

	    /////////////////////////////////////////////////
	    // Populate the higher layer
	    /////////////////////////////////////////////////
		
		// Create the compass
	    compass = GWT.create(Compass.class);
	    final OMSVGSVGElement compassSvg = compass.getSvgElement();
	    compassSvg.getStyle().setWidth(100, Unit.PX);
	    compassSvg.getStyle().setHeight(100, Unit.PX);
	    compass.addRotationHandler(new RotationHandler() {
	    	@Override
	    	public void onRotate(RotationEvent event) {
	    		svgModel.setRotation(event.getAngle());
	    	}	
	    });
		LayoutContainer compassContainer = new LayoutContainer();
		AppCss css = AppBundle.INSTANCE.css();
		compassContainer.addStyleName(css.compassContainer());
		SVGImage compassImage = new SVGImage(compassSvg);
		compassImage.addClassNameBaseVal(css.compass());
		compassContainer.add(compassImage);
	    layersContainer.add(compassContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_RIGHT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		20));
	    
		// Create the scale slider
		LayoutContainer sliderContainer = new LayoutContainer();
		sliderContainer.addStyleName(css.scaleSliderContainer());
		scaleSlider = new Slider() {
			@Override
	    	protected String onFormatValue(int value) {
				return Integer.toString((int)(svgModel.getScale() * 100)) + "%";
	    	}

		};
		scaleSlider.addStyleName(css.scaleSlider());
		sliderContainer.add(scaleSlider);
		scaleSlider.setHeight(100);
		scaleSlider.setMinValue(0);
		scaleSlider.setMaxValue(100);
		scaleSlider.setIncrement(1);
		scaleSlider.setValue(50);
		scaleSlider.setVertical(true);
		layersContainer.add(sliderContainer, new AbsoluteLayerLayoutData(
	    		AbsoluteLayerLayoutData.HORIZONTAL_ATTACH_RIGHT | AbsoluteLayerLayoutData.VERTICAL_ATTACH_TOP,
	    		0,
	    		0,
	    		0,
	    		0,
	    		20));
		scaleSlider.addListener(Events.Change, new Listener<SliderEvent>() {
			@Override
			public void handleEvent(SliderEvent be) {
				// Convert from slider unit to transform unit
				int value = be.getNewValue();
				float scale;
				if (value >= 50) {
					scale = 1f + (value - 50f) / 10f * 4 / 5;
				} else {
					scale = 1f / (1f + (49 - value) / 10f * 4 / 5);
				}
				svgModel.setScale(scale);
			}	    	
	    });
	    
//	    ToolBar toolBar = new ToolBar();
//	    ToggleButton selectButton = new ToggleButton();
//	    selectButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.cursor()));
//	    selectButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				model.setSelectionMode(((ToggleButton)ce.getButton()).isPressed());
//			}
//	    });
//	    toolBar.add(selectButton);
//	    setTopComponent(toolBar);
	    
		setLayout(new FitLayout());
		add(splitterPanel, new FitData(4));
		
		/*addListener(Events.Activate, new Listener<WindowEvent>() {
			@Override
			public void handleEvent(WindowEvent we) {
				activate();
			}
		});*/
	}
	
	@Override
	protected void moveDrag(DragEvent de) {
		int windowBarHeight = VectomaticApp2.getWindowBarHeight();
		if (de.getY() < windowBarHeight) {
			de.setY(windowBarHeight);
		}
	}
	
	public SVGModel getSvgModel() {
		return svgModel;
	}
	
	public TreePanelExt<SVGElementModel> getTree() {
		return tree;
	}

	/**
	 * Sets the scaling of the main image through the scale slider.
	 * @param scale
	 * The scale (50 means scale 1:1)
	 */
	public void setScaleSlider(int value) {
		scaleSlider.setValue(value);
	}

	/**
	 * Sets the rotation of the main image through the
	 * compass widget.
	 * @param angleDeg
	 * The angle (in degrees)
	 */
	public void setRotationCompass(int angleDeg) {
		compass.setRotation(angleDeg);
	}	
	/* GWT bug ?
	 * line 234: The method endDrag(DragEvent) in the type Window is not applicable for the arguments (DragEvent, boolean)*/
//	protected void endDrag(DragEvent de, boolean canceled) {
//		GWT.log("endDrag" + de.getX() + " " + de.getY());
//		int windowBarHeight = VectomaticApp2.getWindowBarHeight();
//		if (de.getY() < windowBarHeight) {
//			de.setY(windowBarHeight);
//		}
//		super.endDrag(de, canceled);
//	}
	/*@Override
	protected void onHide() {
		GWT.log("SVGWindow(" + getHeading() + ").onHide");
		TreePanelSelectionModel<SVGElementModel> selection = tree.getSelectionModel();
		selection.deselectAll();
		super.onHide();
	}*/
	
	public void activate() {
		GWT.log("SVGWindow(" + getHeading() + ").activate");
		el().addStyleName(WINDOW_ACTIVE_STYLE);
		TreePanelSelectionModel<SVGElementModel> selection = tree.getSelectionModel();
		SelectionService.get().register(selection);
		updateSelectionListeners();
	}
	
	public void updateSelectionListeners() {
		TreePanelSelectionModel<SVGElementModel> selection = tree.getSelectionModel();
		selection.fireEvent(Events.SelectionChange, new SelectionChangedEvent<SVGElementModel>(selection, selection.getSelectedItems()));
	}
	
	public void deactivate() {
		GWT.log("SVGWindow(" + getHeading() + ").deactivate");
		Object selection = tree.getSelectionModel();
		SelectionService.get().unregister((SelectionProvider<ModelData>)selection);
		el().removeStyleName(WINDOW_ACTIVE_STYLE);
	}
	
	@Override
    protected void fitContainer() {
		Rectangle rect = VectomaticApp2.getApp().getRectangle();
		setPosition(rect.x, rect.y);
		setSize(rect.width, rect.height);
    }
	
	public void renameModel(final SVGElementModel model) {
        final EditTitleCommandFactory commandFactory = (EditTitleCommandFactory) EditTitleCommandFactory.INSTANTIATOR.create();
        commandFactory.start(this);
        commandFactory.updateStatus(ModelConstants.INSTANCE.renameElementCmdFactory2());

        final TextField<String> nameField = new TextField<String>();
        Editor nameEditor = new Editor(nameField);
        nameEditor.setAutoHeight(true);
        nameEditor.setAutoWidth(true);
        nameEditor.setCompleteOnEnter(true);
        Listener<BaseEvent> editorListener = new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
            	GWT.log("Editor: " + be.getType());
            	if (be.getType() == Events.Complete) {
                	Record record = svgModel.getStore().getRecord(model);
                	record.set(SVGConstants.SVG_TITLE_TAG, nameField.getValue());
                	record.commit(false);
                    commandFactory.stop();                    		
            	}
            	if (be.getType() == Events.CancelEdit) {
                    commandFactory.stop();                    		
            	}
           }
        };
        nameEditor.addListener(Events.Complete, editorListener);
        nameEditor.addListener(Events.CancelEdit, editorListener);
        TreeNode treeNode = tree.findTreeNode(model);
        nameEditor.startEdit(treeNode.getElement(), model.get(SVGConstants.SVG_TITLE_TAG));
	}

	
}

