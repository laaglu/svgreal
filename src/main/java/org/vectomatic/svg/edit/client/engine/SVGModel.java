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
package org.vectomatic.svg.edit.client.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGStyle;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.impl.SVGGElement;
import org.vectomatic.dom.svg.impl.SVGRectElement;
import org.vectomatic.dom.svg.itf.ISVGLocatable;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.dom.svg.utils.SVGPrefixResolver;
import org.vectomatic.svg.edit.client.SVGSelectionModel;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.CommandStore;
import org.vectomatic.svg.edit.client.command.ICommandFactory;
import org.vectomatic.svg.edit.client.command.RemoveElementsCommandFactory;
import org.vectomatic.svg.edit.client.command.ShowPropertiesCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddCircleCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddEllipseCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddGroupCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddLineCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddPathCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddPolygonCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddPolylineCommandFactory;
import org.vectomatic.svg.edit.client.command.add.AddRectCommandFactory;
import org.vectomatic.svg.edit.client.event.HasRotationHandlers;
import org.vectomatic.svg.edit.client.event.HasScalingHandlers;
import org.vectomatic.svg.edit.client.event.KeyPressProcessor;
import org.vectomatic.svg.edit.client.event.KeyUpProcessor;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.event.MouseUpProcessor;
import org.vectomatic.svg.edit.client.event.RotationEvent;
import org.vectomatic.svg.edit.client.event.RotationHandler;
import org.vectomatic.svg.edit.client.event.ScalingEvent;
import org.vectomatic.svg.edit.client.event.ScalingHandler;
import org.vectomatic.svg.edit.client.event.StoreEventProcessor;
import org.vectomatic.svg.edit.client.gxt.widget.CommandFactoryMenuItem;
import org.vectomatic.svg.edit.client.gxt.widget.KeyNavExt;
import org.vectomatic.svg.edit.client.model.MetaModel;
import org.vectomatic.svg.edit.client.model.svg.SVGCircleElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGEllipseElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGNamedElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolygonElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolylineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGRectElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGViewBoxElementModel;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Model class for an SVG document edited by the application.
 * The document has the following DOM structure:
 * <pre>
 * <svg>
 *   <g> (elementGroup)    <- where transforms are applied
 *     <title>
 *     <desc>
 *     <g> (gridGroup)     <- where grid wigets, rulers are attached
 *     <g> (geometryGroup) <- where opacity is applied
 *       <rect> (viewBox)  <- special: no title, desc
 *       <elt1>
 *         <title>
 *         <desc>
 *       <eltN>
 *         <title>
 *         <desc>
 *   <g> (twinGroup)       <- clone of element group (except there is no grid group)
 *     <title>
 *     <desc>
 *     <g> (geometryGroup)
 *       <rect> (viewBox)
 *       <elt1>
 *         <title>
 *         <desc>
 *       <eltN>
 *         <title>
 *         <desc>
 * </pre>
 * <dl>
 * <dt>elementGroup</dt><dd>Contains all the elements from the original SVG, id-normalized.</dd>
 * <dt>twinGroup</dt><dd>Contains a visibility-hidden clone of the previous group. It is used to display the selection and implement highlighting.</dd>
 * <dt>gridGroup</dt><dd>Contains all the helper elements required to display grids, rulers...</dd>
 * <dt>geometryGroup</dt><dd>Used to applied opacity for highlighting operations</dd>
 * </dl>
 * @author laaglu
 */
public class SVGModel implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, HasScalingHandlers, HasRotationHandlers {
	/**
	 * To be able to identify the viewBox model
	 */
	private static final String ATTR_KIND = "kind";
	private static final String ATTR_KIND_VIEWBOX = "viewBox";
	/**
	 * Id Prefix extension for twins
	 */
	public static final String EXT_TWIN = "twin";
	/**
	 * The prefix used for all id attributes in this model
	 */
	protected String idPrefix;
	/**
	 * The root of the SVG document
	 */
	protected OMSVGSVGElement svg;
	/**
	 * A group containing all the elements from the original SVG, id-normalized. 
	 * Is also servers to apply a visualization transform change the display
	 * of the document
	 */
	protected SVGElementModel geometryGroup;
	/**
	 * The matrix transform to the geometryGroup
	 */
	protected OMSVGMatrix m;
	/**
	 * The current scaling the elementGroup
	 */
	protected float angle;
	/**
	 * The current rotation of the elementGroup
	 */
	protected float scale;
	/**
	 * The selection model
	 */
	protected SVGSelectionModel selectionModel;
	/**
	 * The current mode (false = display mode, true = highlighting mode) 
	 */
	protected boolean highlightingMode;
	/**
	 * The highlighted model in highlighting mode
	 */
	protected SVGElementModel highlightedModel;
	/**
	 * A map used to generate node names for nodes
	 * which do not have a title element.
	 */
	protected Map<String, Integer> tagNameToTagCount;
	/**
	 * The Store which contains this model data
	 */
	protected TreeStore<SVGElementModel> store;
	/**
	 * The Store which contains this model commands
	 */
	protected CommandStore commandStore;
	/**
	 * Associates SVG elements with their model wrapper
	 */
	protected Map<SVGElement, SVGElementModel> elementToModel;
	/**
	 * The svg rect use to represent the viewBox
	 */
	protected SVGViewBoxElementModel viewBox;
	/**
	 * A rectangled defining the bounds of the GXT viewport
	 */
	protected OMSVGRect windowRect;
	/**
	 * The grid settings for this model
	 */
	protected Grid grid;

	/*==========================================================
	 * 
	 * C O N S T R U C T O R 
	 * 
	 *==========================================================*/

	public SVGModel() {
		elementToModel = new HashMap<SVGElement, SVGElementModel>();
		tagNameToTagCount = new HashMap<String, Integer>();
		grid = new Grid();
	}
	
	/*==========================================================
	 * 
	 * M O D E L   C O N S T R U C T I O N 
	 * 
	 *==========================================================*/

	/**
	 * Factory method. Creates a new SVG model from the supplied
	 * SVG root and title
	 * @param svg The svg root
	 * @param title The svg title
	 * @param idPrefix the id prefix for this model
	 * @return The new SVG document
	 */
	public static SVGModel newInstance(OMSVGSVGElement svg, String title, String idPrefix) {
		SVGModel model = GWT.create(SVGModel.class);
		model.setSvgElement(svg, title, idPrefix);
		return model;
	}
	
	static {
		initialize();
	}
	protected static Map<String, MetaModel<SVGElement>> tagNameToMetamodel;
	public static MetaModel<SVGElement> getMetamodel(SVGElement element) {
		if (tagNameToMetamodel == null) {
			tagNameToMetamodel = new HashMap<String, MetaModel<SVGElement>>();
			tagNameToMetamodel.put(SVGConstants.SVG_CIRCLE_TAG, SVGCircleElementModel.getCircleElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_ELLIPSE_TAG, SVGEllipseElementModel.getEllipseElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_LINE_TAG, SVGLineElementModel.getLineElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_RECT_TAG, SVGRectElementModel.getRectElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_POLYGON_TAG, SVGPolygonElementModel.getPolygonElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_POLYLINE_TAG, SVGPolylineElementModel.getPolylineElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_PATH_TAG, SVGPathElementModel.getPathElementMetaModel());
			tagNameToMetamodel.put(SVGConstants.SVG_IMAGE_TAG, SVGImageElementModel.getImageElementMetaModel());
		}
		return tagNameToMetamodel.get(element.getTagName());
	}

	private static final native void initialize() /*-{
		if ($wnd.otToModel == null) {
	    	$wnd.otToModel = new Object();
	    }
		$wnd.otToModel["SVGCircleElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGCircleElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGCircleElement;Lorg/vectomatic/dom/svg/impl/SVGCircleElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGEllipseElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGEllipseElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGEllipseElement;Lorg/vectomatic/dom/svg/impl/SVGEllipseElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGLineElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGLineElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGLineElement;Lorg/vectomatic/dom/svg/impl/SVGLineElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGRectElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGRectElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGRectElement;Lorg/vectomatic/dom/svg/impl/SVGRectElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGPolygonElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGPolygonElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGPolygonElement;Lorg/vectomatic/dom/svg/impl/SVGPolygonElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGPolylineElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGPolylineElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGPolylineElement;Lorg/vectomatic/dom/svg/impl/SVGPolylineElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGPathElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGPathElement;Lorg/vectomatic/dom/svg/impl/SVGPathElement;)(owner, elem, twin); };
		$wnd.otToModel["SVGImageElement"] = function(owner, elem, twin) { return @org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGImageElement;Lorg/vectomatic/dom/svg/impl/SVGImageElement;)(owner, elem, twin); };
	}-*/;
	
	/**
	 * Generates a model around an overlay type node
	 * @param <T> the node type
	 * @param element The overlay type node
	 * @return The node model
	 */
	public SVGElementModel convert(SVGElement element) {
		SVGElementModel model = elementToModel.get(element);
//		assert(model != null);
		return model;
	}
	
	public OMSVGSVGElement getSvgElement() {
		return svg;
	}
	
	/**
	 * Binds this SVG model to the specified SVG 'svg' element
	 * @param svg an SVG 'svg' element
	 * @param title the name of the root element
	 * @param idPrefix the id prefix for this model
	 */
	public void setSvgElement(OMSVGSVGElement svg, String title, String idPrefix) {
		this.svg = svg;
		this.idPrefix = idPrefix;
		windowRect = svg.createSVGRect();

		// Force the svg to have its size managed by CSS
		// (no width and height attributes). This size will
		// be the min (window size, bbox of the svg in
		// screen coordinates taking into account the
		// viewing transform).
		svg.removeAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE);
		svg.removeAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE);
		
	    // Add event handlers. These event handlers will re-route
	    // events to the highlighter or the manipulators
	    svg.addMouseMoveHandler(this);
	    svg.addMouseDownHandler(this);
	    svg.addMouseUpHandler(this);

		elementToModel.clear();
		tagNameToTagCount.clear();

		// Normalize ids to support multi-docs
	    SVGProcessor.normalizeIds(svg, idPrefix);

	    // Build the geometry group (used to control the viewing
	    // transform)
	    OMSVGGElement elementGroup = new OMSVGGElement();
		SVGNamedElementModel.createTitleDesc(elementGroup.getElement().<SVGElement>cast(), title);
	    SVGProcessor.reparent(svg, elementGroup);
	    
	    // Create the viewbox transform.
	    elementGroup.getTransform().getBaseVal().appendItem(svg.createSVGTransform());

	    // Create the selection group to handle highlighting
	    // of the selection and hovered elements.
	    OMSVGGElement twinGroup = (OMSVGGElement) elementGroup.cloneNode(true);
	    SVGProcessor.normalizeIds(twinGroup, SVGProcessor.newPrefixExtension(idPrefix, EXT_TWIN));
	    twinGroup.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_HIDDEN_VALUE);
	    svg.appendChild(twinGroup);
	    
	    geometryGroup = create(elementGroup.getElement().<SVGElement>cast(), twinGroup.getElement().<SVGElement>cast());
	    svg.appendChild(elementGroup);
	    svg.appendChild(twinGroup);
	    setScale(1f);
	    
	    // Build the SVG tree store
	    store = new TreeStore<SVGElementModel>();
	    store.add(geometryGroup, true);
	    
	    // From now on, listen to changes to the model, to translate
	    // them into commands
	    store.addStoreListener(new StoreListener<SVGElementModel>() {
			@Override
			public void storeUpdate(StoreEvent<SVGElementModel> se) {
				SVGModel.this.storeUpdate(se);
			}
		});

		// Keep the viewBox if available for later computation of the viewBox model
		// on svg attach. The actual SVG viewBox is removed.
		OMSVGRect viewBoxRect = svg.getViewBox().getBaseVal();
		if (viewBoxRect.getWidth() != 0f && viewBoxRect.getHeight() != 0f) {
			createViewBox(viewBoxRect);
		}
		svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);

	    // Build the command store
	    commandStore = new CommandStore();

	    // Build the selection model
	    selectionModel = new SVGSelectionModel();
	    
	}
	
	/**
	 * Return true if the specified SVG element is part of this model
	 * @param element the element to test
	 * @return true if the specified SVG element is part of this model
	 */
	public boolean contains(SVGElement element) {
		return elementToModel.containsKey(element);
	}
	
	protected void adopt(SVGElementModel model) {
		adopt(model, true);
	}
	protected void adopt(SVGElementModel model, boolean root) {
		model.setOwner(this);
		elementToModel.put(model.getElement(), model);
		elementToModel.put(model.getTwin(), model);
		
		// Do a DFS-preorder traversal of the DOM tree
		SVGElementModel firstChild = (SVGElementModel) model.getChild(0);
		if (firstChild != null) {
			adopt(firstChild, false);
		}
		if (!root) {
			SVGElementModel nextSibling = model.getNextSibling();
			if (nextSibling != null) {
				adopt(nextSibling, false);
			}
		}
	}

	protected void orphan(SVGElementModel model) {
		orphan(model, true);
	}
	
	protected void orphan(SVGElementModel model, boolean root) {
		model.setOwner(null);
		elementToModel.remove(model.getElement());
		elementToModel.remove(model.getTwin());
		
		// Do a DFS-preorder traversal of the DOM tree
		SVGElementModel firstChild = (SVGElementModel) model.getChild(0);
		if (firstChild != null) {
			orphan(firstChild, false);
		}
		if (!root) {
			SVGElementModel nextSibling = model.getNextSibling();
			if (nextSibling != null) {
				orphan(nextSibling, false);
			}
		}
	}

	public SVGElementModel create(Node modelNode, Node modelTwin) {
		Stack<Node> stack = new Stack<Node>();
		stack.push(modelNode);
		stack.push(modelTwin);
		while(!stack.empty()) {
			Node twin = stack.pop();
			Node node = stack.pop();
			if (SVGProcessor.isSvgElement(node)) {
				if (SVGProcessor.isDefinitionElement(node.<SVGElement>cast())) {
					continue;
				}
				if (SVGProcessor.isGraphicalElement(node.<SVGElement>cast())) {
					SVGElementModel model = convert(node.<SVGElement>cast());
					if (model == null) {
						model = create(this, node, twin);
						adopt(model, false);
					}
					SVGElementModel parentModel = (SVGElementModel) model.getParent();
					if (parentModel == null) {
						parentModel = convert(node.getParentElement().<SVGElement>cast());
					}
					if (parentModel != null) {
						parentModel.add(model);
					}
				}
			}
			NodeList<Node> nodeChildren = node.getChildNodes();
			NodeList<Node> twinChildren = twin.getChildNodes();
			for (int i = nodeChildren.getLength() - 1; i >= 0; i--) {
				stack.push(nodeChildren.getItem(i));
				stack.push(twinChildren.getItem(i));
			}
		}
		return convert(modelNode.<SVGElement>cast());
	}
	
	private final native SVGElementModel create(SVGModel owner, Node element, Node twin) /*-{
	    var type = @org.vectomatic.dom.svg.utils.DOMHelper::getType(Lcom/google/gwt/core/client/JavaScriptObject;)(element);
	    if (type) {
	    	var ctor = $wnd.otToModel[type];
	    	if (ctor != null) {
				return ctor(owner, element, twin);
	    	} else {
	    		return @org.vectomatic.svg.edit.client.model.svg.SVGGenericElementModel::new(Lorg/vectomatic/svg/edit/client/engine/SVGModel;Lorg/vectomatic/dom/svg/impl/SVGElement;Lorg/vectomatic/dom/svg/impl/SVGElement;)(owner, element, twin);
	    	}
	    }
	    return null;
	}-*/;
	
	/**
	 * Appends a model to the children of the specified model
	 * @param parentModel The parent model
	 * @param model The model to append
	 */
	public void add(SVGElementModel parentModel, SVGElementModel model) {
		insertBefore(parentModel, model, null);
	}

	/**
	 * Insert a new model into this SVG model before the specified child model
	 * or the specified parent model.
	 * @param parentModel The parent model
	 * @param newModel The model to insert. If the model is not in this SVG model,
	 * it is removed from its previous SVG model before being inserted into this SVG model.
	 * @param refModel The reference model. If null, the new model is appended to
	 * the children of the parent model
	 */
	public void insertBefore(SVGElementModel parentModel, SVGElementModel newModel, SVGElementModel refModel) {
		// Sanity checks
		Element element = newModel.getElement();
		Element parentElement = parentModel.getElement();
		assert(parentElement != null);
		assert(contains(parentElement.<SVGElement>cast())) : parentModel.toString() + " element is not in this model";
		Element refElement = refModel != null ? refModel.getElement() : null;
		if (refElement != null) {
			assert(contains(refElement.<SVGElement>cast())) : refElement.toString() + " element is not in this model";
		}
		
		Element twin = newModel.getTwin();
		Element parentTwin = parentModel.getTwin();
		assert(parentTwin != null);
		assert(contains(parentTwin.<SVGElement>cast())) : parentModel.toString() + " twin is not in this model";
		Element refTwin = refModel != null ? refModel.getTwin() : null;
		if (refElement != null) {
			assert(contains(refTwin.<SVGElement>cast())) : refElement.toString() + " twin is not in this model";
		}
		
		// Update SVG models
		SVGModel owner = newModel.getOwner();
		if (owner != null) {
			owner.remove(newModel);
		}
		adopt(newModel);

		// Update the DOM tree
		parentElement.insertBefore(element, refElement);
		// Update the twin DOM tree
		parentTwin.insertBefore(twin, refTwin);
		
		
		if (refModel == null) {
			// Update the model tree
			parentModel.add(newModel);

			// Update the store
			store.add(parentModel, newModel, true);
		} else {
			int index = parentModel.indexOf(refModel);
			// Update the model tree
			parentModel.insert(newModel, index);
			
			// Update the store
			store.insert(parentModel, newModel, index, true);			
		}
	}

	/**
	 * Removes the specified model
	 * @param model
	 */
	public void remove(SVGElementModel model) {
		if (model.getOwner() == this) {
			// Update the SVG model
			orphan(model);

			// Update the DOM tree
			Element element = model.getElement();
			Element parentElement = element.getParentElement();
			if (parentElement != null) {
				parentElement.removeChild(element);
			}
			Element twin = model.getTwin();
			Element parentTwin = twin.getParentElement();
			if (parentTwin != null) {
				parentTwin.removeChild(twin);
			}
			
			// Update the model tree
			if (model.getParent() != null) {
				model.getParent().remove(model);
			}
			
			// Update the store
			store.removeAll(model);
			store.remove(model);
		}
	}
	
	/**
	 * Recursively clones the specified model.
	 * @param model
	 * @return the root of the cloned tree
	 */
	public SVGElementModel clone(SVGElementModel model, String name) {
		SVGElementModel clone = create(model.getElement().cloneNode(true).<SVGElement>cast(), model.getTwin().cloneNode(true).<SVGElement>cast());
		clone.set(SVGConstants.SVG_TITLE_TAG, name);
		return clone;
	}
	
	
	protected void storeUpdate(StoreEvent<SVGElementModel> se) {
		ICommandFactory factory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (factory instanceof StoreEventProcessor) {
			((StoreEventProcessor)factory).processStoreEvent(se);
		}
	}

	public String getMarkup() {
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setAttribute(SVGConstants.XMLNS_PREFIX + ":" + SVGConstants.XLINK_PREFIX, SVGConstants.XLINK_NAMESPACE_URI);
		svg.setViewBox(
				viewBox.<Float>get(SVGConstants.SVG_X_ATTRIBUTE), 
				viewBox.<Float>get(SVGConstants.SVG_Y_ATTRIBUTE), 
				viewBox.<Float>get(SVGConstants.SVG_WIDTH_ATTRIBUTE),
				viewBox.<Float>get(SVGConstants.SVG_HEIGHT_ATTRIBUTE));
		SVGGElement g = geometryGroup.getElement().cloneNode(true).cast();
		
		// Skip the element representing the viewbox
		g.removeChild(DOMHelper.evaluateNodeXPath(g, "//svg:rect[@" + ATTR_KIND + "='" + ATTR_KIND_VIEWBOX + "']", SVGPrefixResolver.INSTANCE));
		
		Node node = null;
		while((node = g.getFirstChild()) != null) {
			svg.getElement().appendChild(g.removeChild(node));
		}
		return svg.getMarkup();
	}
	
	/**
	 * Returns the root node of this model
	 * @return
	 */
	public SVGElementModel getRoot() {
		return store.getRootItems().get(0);
	}

	/*==========================================================
	 * 
	 * G E T T E R S 
	 * 
	 *==========================================================*/

	/**
	 * Returns the Store which contains this model data
	 * @return
	 */
	public TreeStore<SVGElementModel> getStore() {
		return store;
	}
	/**
	 * Returns the CommandStore which contains this model commands
	 * @return
	 */
	public CommandStore getCommandStore() {
		return commandStore;
	}

	/**
	 * Returns the root of the SVG document.
	 * @return the root of the SVG document.
	 */
	public OMSVGSVGElement getDocumentRoot() {
		return svg;
	}

	/**
	 * Returns the selection model.
	 * @return the selection model.
	 */
	public TreePanelSelectionModel<SVGElementModel> getSelectionModel() {
		return selectionModel;
	}

	/**
	 * Returns the model viewBox.
	 * @return the model viewBox.
	 */
	public SVGViewBoxElementModel getViewBox() {
		return viewBox;
	}
	
	/**
	 * Returns the document id prefix.
	 * @return the document id prefix.
	 */
	public String getIdPrefix() {
		return idPrefix;
	}

	
	/*==========================================================
	 * 
	 * E V E N T   H A N D L I N G 
	 * 
	 *==========================================================*/
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// Forward to manipulator
		ICommandFactory commandFactory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (commandFactory instanceof MouseMoveProcessor) {
			if (((MouseMoveProcessor)commandFactory).processMouseMove(event)) {
				return;
			}
		}

		// Highlighting
		if (highlightingMode) {
			SVGElementModel model = convert(event.getNativeEvent().getEventTarget().<SVGElement>cast());
			highlightModel(model);
		} 
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		GWT.log("SVGModel.onMouseDown");
		SVGElement target = event.getNativeEvent().getEventTarget().cast();
		
		// Context menu
		if (event.getNativeButton() == 2) {
			SVGElementModel model = convert(target);
			if (model == null || SVGConstants.SVG_SVG_TAG.equals(target.getTagName())) {
				// Empty selection or unknown element
				selectionModel.deselectAll();
			} else {
				if (!selectionModel.isSelected(model)) {
					// mono selection
					selectionModel.select(model, false);
				} /*
				else {
					// mono or multiselection
				}
				*/
			}
			return;
		}

		// Forward to manipulator
		ICommandFactory commandFactory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (commandFactory instanceof MouseDownProcessor) {
			if (((MouseDownProcessor)commandFactory).processMouseDown(event)) {
				return;
			}
		}
		
		// Selection
		if (SVGConstants.SVG_SVG_TAG.equals(target.getTagName())) {
			selectionModel.deselectAll();
		} else {
			SVGElementModel model = convert(target);			
			if (model != null) {
				if (selectionModel.isSelected(model)) {
					if (event.isControlKeyDown()) {
						// Toggle selection
						selectionModel.deselect(model);
					}
				} else if (event.isShiftKeyDown() | event.isControlKeyDown()) {
					// Add to selection
					selectionModel.select(model, true);
				} else {
					// New selection
					selectionModel.select(model, false);
				}
			}
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		GWT.log("SVGModel.onMouseUp");
		ICommandFactory commandFactory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (commandFactory instanceof MouseUpProcessor) {
			((MouseUpProcessor)commandFactory).processMouseUp(event);
		}
	}

	public void onKeyPress(ComponentEvent event) {
		int code = event.getKeyCode();
		GWT.log("SVGModel.onKeyPress: " + code);
		ICommandFactory commandFactory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (commandFactory instanceof KeyPressProcessor && ((KeyPressProcessor)commandFactory).processKeyPress(event)) {
			return;
		}
		if (code == KeyCodes.KEY_DELETE || code == KeyCodes.KEY_BACKSPACE) {
			RemoveElementsCommandFactory.INSTANTIATOR.create().start(this);
		}
		if (code == KeyNavExt.KEY_F2) {
			List<SVGElementModel> selectedItems = selectionModel.getSelectedItems();
			if (selectedItems.size() == 1) {
				SVGElementModel model = selectedItems.get(0);
				VectomaticApp2.getApp().getWindow(model.getElement()).renameModel(model);
			}
		}
	}
	
	public void onKeyUp(ComponentEvent event) {
		int code = event.getKeyCode();
		GWT.log("SVGModel.onKeyUp: " + code);
		ICommandFactory commandFactory = VectomaticApp2.getApp().getCommandFactorySelector().getActiveFactory();
		if (commandFactory instanceof KeyUpProcessor && ((KeyUpProcessor)commandFactory).processKeyUp(event)) {
			return;
		}
	}
	/**
	 * Updates the context menu based on the model selection
	 * @param contextMenu The context menu to update
	 */
	public void updateContextMenu(Menu contextMenu) {
		List<SVGElementModel> selectedModels = selectionModel.getSelectedItems();
		List<Item> items = new ArrayList<Item>();
		int size = selectedModels.size();
		VectomaticApp2 app = VectomaticApp2.getApp();
		if (size == 0) {
			// Empty selection
			items.add(new CommandFactoryMenuItem(AddLineCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddCircleCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddEllipseCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddRectCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddPolylineCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddPolygonCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddPathCommandFactory.INSTANTIATOR));
			items.add(new CommandFactoryMenuItem(AddGroupCommandFactory.INSTANTIATOR));
		} else if (size == 1) {
			// Mono selection
			MetaModel metaModel = selectedModels.get(0).getMetaModel();
			items.addAll(metaModel.getContextMenuItems());
			items.add(new CommandFactoryMenuItem(RemoveElementsCommandFactory.INSTANTIATOR));
		} else {
			// Multi selection
			items.add(new CommandFactoryMenuItem(RemoveElementsCommandFactory.INSTANTIATOR));
		}
		items.add(new CommandFactoryMenuItem(ShowPropertiesCommandFactory.INSTANTIATOR));
		contextMenu.removeAll();
		for (Item item : items) {
			contextMenu.add(item);			
		}
	}

    /**
     * Returns the coordinates of a mouse event, converted
     * to the coordinate system of the model
     * @param e
     * A mouse event
     * @param snap
     * True if the coordinate should be snapped to the grid when
     * grid snapping is activated
     * @return
     * The coordinates of the mouse event, converted
     * to the coordinate system of the specified matrix
     */
    public OMSVGPoint getCoordinates(MouseEvent<? extends EventHandler> e, boolean snap) {
    	OMSVGMatrix m = geometryGroup.getElement().<SVGGElement>cast().getScreenCTM().inverse();
    	OMSVGPoint p = svg.createSVGPoint(e.getClientX(), e.getClientY()).matrixTransform(m);
    	return grid.snapsToGrid() ? grid.snap(p) : p;
    }

	/*==========================================================
	 * 
	 * C A N V A S   S I Z I N G
	 * 
	 *==========================================================*/
	
	public void onAttach() {
		// Create a viewbox for model which do not define one.
		// The viewbox is created to be 10% larger that the bbox
		if (viewBox == null) {
    		GWT.log(svg.getBBox().getDescription());
    		createViewBox(svg.getBBox().inset(svg.createSVGRect(), -0.1f * svg.getBBox().getWidth(), -0.1f * svg.getBBox().getHeight()));
		}
		if (!grid.isAttached()) {
			grid.attach(this);
			svg.insertBefore(grid.getDefs(), geometryGroup.getElementWrapper());
			geometryGroup.getElement().insertAfter(grid.getRoot().getElement(), geometryGroup.getElement().getChild(1));
		}
	}
	
	private void createViewBox(OMSVGRect viewBoxRect) {
		OMSVGDocument document = (OMSVGDocument) svg.getOwnerDocument();
		OMSVGRectElement rect = document.createSVGRectElement(viewBoxRect);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
		rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_DASHARRAY_PROPERTY, "4, 2");
		rect.setAttribute(ATTR_KIND, ATTR_KIND_VIEWBOX);
		SVGRectElement element = rect.getElement().cast();
		SVGRectElement twin = element.cloneNode(true).cast();
		viewBox = new SVGViewBoxElementModel(this, element, twin);
		adopt(viewBox);
		
		insertBefore(geometryGroup, viewBox, (SVGElementModel) geometryGroup.getChild(0) /* will return null if geometryGroup has not children*/);
	}

	/**
	 * Returns the scaling of the SVG.
	 * @return The scale (1 means scale 1:1, 2 means scale 2:1)
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Sets the scaling of the SVG.
	 * @param scale
	 * The scale (1 means scale 1:1, 2 means scale 2:1)
	 */
	public void setScale(float scale) {
		this.scale = scale;
		updateTransform();
		fireEvent(new ScalingEvent(scale));
	}
	
	public float getRotation() {
		return angle;
	}
	
	/**
	 * Sets the rotation of the SVG.
	 * @param angle
	 * The angle (in degrees)
	 */
	public void setRotation(float angle) {
		this.angle = angle;
		updateTransform();
		fireEvent(new RotationEvent(angle));
	}
	
	/**
	 * Returns this document grid
	 * @return
	 */
	public Grid getGrid() {
		return grid;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		VectomaticApp2.getApp().getEventBus().fireEventFromSource(event, this);
	}
	
	@Override
	public HandlerRegistration addRotationHandler(RotationHandler handler) {
		return VectomaticApp2.getApp().getEventBus().addHandlerToSource(RotationEvent.getType(), this, handler);
	}

	@Override
	public HandlerRegistration addScalingHandler(ScalingHandler handler) {
		return VectomaticApp2.getApp().getEventBus().addHandlerToSource(ScalingEvent.getType(), this, handler);
	}
	
	/**
	 * Specifies the dimensions of the window viewport
	 * @param width width of the window viewport
	 * @param height height of the window viewport
	 */
	public void setWindowRect(int width, int height) {
		GWT.log("setWindowRect(" + width + ", " + height + ")");
		windowRect.setWidth(width);
		windowRect.setHeight(height);
		updateTransform();
	}
	public OMSVGRect getWindowRect() {
		return windowRect;
	}
	/**
	 * Updates the display group transform and changes the CSS size
	 * of the SVG accordingly
	 */
	public void updateTransform() {
		if (viewBox != null) {
			OMSVGRect bbox = ((SVGRectElement)viewBox.getElement()).getBBox();
//			GWT.log("bbox = " + bbox.getDescription());
			float d = (float)Math.sqrt((bbox.getWidth() * bbox.getWidth() + bbox.getHeight() * bbox.getHeight()) * 0.25) * scale * 2;
//			GWT.log("d = " + d);
		
			// Compute the actual canvas size. It is the max of the window rect
			// and the transformed bbox.
			float width = Math.max(d, windowRect.getWidth());
			float height = Math.max(d, windowRect.getHeight());
//			GWT.log("width = " + width);
//			GWT.log("height = " + height);

			// Compute the display transform to center the image in the
			// canvas
			OMSVGMatrix m = svg.createSVGMatrix();
			float cx = bbox.getCenterX();
			float cy = bbox.getCenterY();
			m = m.translate(0.5f * (width - bbox.getWidth()) -bbox.getX(), 0.5f * (height - bbox.getHeight()) -bbox.getY())
			.translate(cx, cy)
			.rotate(angle)
			.scale(scale)
			.translate(-cx, -cy);
			((ISVGTransformable)geometryGroup.getElementWrapper()).getTransform().getBaseVal().getItem(0).setMatrix(m);
			((ISVGTransformable)geometryGroup.getTwinWrapper()).getTransform().getBaseVal().getItem(0).setMatrix(m);
//			GWT.log("m=" + m.getDescription());
			svg.getStyle().setWidth(width, Unit.PX);
			svg.getStyle().setHeight(height, Unit.PX);
		}
	}

	/**
	 * Computes the size of the vertex representation (it should be 1mm
	 * whatever the scaling factor).
	 * @return
	 */
	public static float getVertexSize(SVGElementModel model) {
		return getVertexSize((ISVGLocatable)model.getElementWrapper());
	}
	
	public static float getVertexSize(ISVGLocatable element) {
		OMSVGMatrix m = element.getScreenCTM().inverse();
		// 1mm = 3.543307px
		float a = 3.543307f * m.getA();
		float b = 3.543307f * m.getD();
		return (float)Math.sqrt(a * a + b * b);
	}

	/*==========================================================
	 * 
	 * H I G H L I G H T I N G
	 * 
	 *==========================================================*/

	
	public OMSVGGElement getElementGroup() {
		return (OMSVGGElement) geometryGroup.getElementWrapper();
	}
	public OMSVGGElement getTwinGroup() {
		return (OMSVGGElement) geometryGroup.getTwinWrapper();
	}

	public boolean isHighlightingMode() {
		return highlightingMode;
	}
	
	public void setHighlightingMode(boolean highlightingMode) {
		if (highlightingMode != this.highlightingMode) {
//			GWT.log("setHighlightingMode(" + highlightingMode + ")");
			this.highlightingMode = highlightingMode;
			float opacity = this.highlightingMode ? 0.25f : 1f;
			geometryGroup.getElementWrapper().getStyle().setSVGProperty(SVGConstants.CSS_OPACITY_PROPERTY, Float.toString(opacity));

			for (SVGElementModel model : selectionModel.getSelectedItems()) {
				displayTwin(model, highlightingMode);
			}
			highlightModel(null);
		}
	}
	
	public void highlightModel(SVGElementModel model) {
		if (model != highlightedModel) {
//			GWT.log("highlightModel(" + (model != null ? model.getTwin() : null) + ")");
			if (highlightedModel != null && !selectionModel.isSelected(highlightedModel)) {
				displayTwin(highlightedModel, false);
			}
			if (model != null) {
				displayTwin(model, true);
			}
			highlightedModel = model;
		}
	}
	
	public void displayTwin(SVGElementModel model, boolean value) {
//		GWT.log("displayTwin(" + ((model == null) ? "null" : model.getTwin()) + " ==> " + value + ")");
		if (model != geometryGroup) {
			OMSVGStyle style  = model.getTwin().getStyle().cast();
			if (value) {
				style.setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
//				style.setSVGProperty(SVGConstants.CSS_POINTER_EVENTS_PROPERTY, SVGConstants.CSS_NONE_VALUE);
			} else {
				style.clearSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY);		
			}
		}
	}
	
	/*==========================================================
	 * 
	 * E L E M E N T   N A M I N G
	 * 
	 *==========================================================*/

	public String generateName(SVGElementModel model) {
		String name = model.getMetaModel().getName();
		if (name == null) {
			name = DOMHelper.getLocalName(model.getElement());
		}
		Integer count = tagNameToTagCount.get(name);
		if (count == null) {
			count = 0;
		}
		tagNameToTagCount.put(name, count + 1);
		return name + (count + 1);
	}
}
