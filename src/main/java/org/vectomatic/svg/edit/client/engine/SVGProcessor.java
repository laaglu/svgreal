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
package org.vectomatic.svg.edit.client.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.impl.Attr;
import org.vectomatic.dom.svg.impl.NamedNodeMap;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.itf.ISVGTransformable;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

/**
 * Class to normalize id and idrefs in an SVG document
 * @author laaglu
 */
public class SVGProcessor {
	/*==========================================================
	 * 
	 * E L E M E N T   C L A S S I F I C A T I O N
	 * 
	 *==========================================================*/
	/**
	 * Tag names of definition elements which contain graphical
	 * elements but are not displayed directly
	 */
	protected static Set<String> definitionElementNames;
	/**
	 * Tag names of graphical elements
	 */
	protected static Set<String> graphicalElementNames;
	/**
	 * Tag names of group element
	 */
	protected static Set<String> groupElementNames;

	public static boolean isGroupElement(SVGElement element) {
		if (groupElementNames == null) {
			groupElementNames = new HashSet<String>(Arrays.asList(new String[] {
					SVGConstants.SVG_G_TAG,
					SVGConstants.SVG_DEFS_TAG
				}));
		}
		return groupElementNames.contains(DOMHelper.getLocalName(element));
	}

	/**
	 * Returns true if the specified node is a definition element.
	 * @param element the element to test.
	 * @return true if the specified node is a definition element.
	 */
	public static boolean isDefinitionElement(SVGElement element) {
		if (definitionElementNames == null) {
			definitionElementNames = new HashSet<String>(Arrays.asList(new String[] {
					SVGConstants.SVG_SYMBOL_TAG,
					SVGConstants.SVG_DEFS_TAG,
					SVGConstants.SVG_PATTERN_TAG,
					SVGConstants.SVG_MARKER_TAG,
					SVGConstants.SVG_CLIP_PATH_TAG,
					SVGConstants.SVG_MASK_TAG,
					SVGConstants.SVG_GLYPH_TAG,
					SVGConstants.SVG_MISSING_GLYPH_TAG
				}));
		}
		return definitionElementNames.contains(DOMHelper.getLocalName(element));
	}
	
	/**
	 * Returns true if the specified node is a graphical element.
	 * @param element the element to test.
	 * @return true if the specified node is a graphical element.
	 */
	public static boolean isGraphicalElement(SVGElement element) {
		if (graphicalElementNames == null) {
			graphicalElementNames = new HashSet<String>(Arrays.asList(new String[] {
			SVGConstants.SVG_CIRCLE_TAG,
			SVGConstants.SVG_ELLIPSE_TAG,
			SVGConstants.SVG_G_TAG,
			SVGConstants.SVG_IMAGE_TAG,
			SVGConstants.SVG_LINE_TAG,
			SVGConstants.SVG_PATH_TAG,
			SVGConstants.SVG_POLYLINE_TAG,
			SVGConstants.SVG_POLYGON_TAG,
			SVGConstants.SVG_RECT_TAG,
			SVGConstants.SVG_TEXT_TAG,
			SVGConstants.SVG_T_REF_TAG,
			SVGConstants.SVG_T_SPAN_TAG,
			SVGConstants.SVG_USE_TAG
			}));	
		}
		return graphicalElementNames.contains(DOMHelper.getLocalName(element));
	}
	
	/**
	 * Returns true if the specified node is an SVG element.
	 * @param node the node to test.
	 * @return true if the specified node is an SVG element.
	 */
	public static boolean isSvgElement(Node node) {
		return node.getNodeType() == Node.ELEMENT_NODE 
			&& SVGConstants.SVG_NAMESPACE_URI.equals(DOMHelper.getNamespaceURI(node));
	}
	
	/**
	 * Returns true if the specified element is an SVG title or desc element.
	 * @param element the element to test.
	 * @return true if the specified element is an SVG title or desc element.
	 */
	public static boolean isTitleDescElement(SVGElement element) {
		String localName = DOMHelper.getLocalName(element);
		return SVGConstants.SVG_TITLE_TAG.equals(localName) || SVGConstants.SVG_DESC_TAG.equals(localName);
	}

	/**
	 * Returns true if the specified element implements ISVGTransformable.
	 * @param node the node to test.
	 * @return true if the specified element implements ISVGTransformable.
	 */
	public static boolean isTransformable(Node node) {
		return OMNode.convert(node) instanceof ISVGTransformable;
	}
	
	/*==========================================================
	 * 
	 * M U L T I D O C U M E N T   M A N A G E M E N T 
	 * 
	 *==========================================================*/
	
	static int docId;
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			IdRefTokenizer tokenizer = new IdRefTokenizer();
			StringBuilder builder = new StringBuilder();
			tokenizer.tokenize(args[i]);
			IdRefTokenizer.IdRefToken token;
			while ((token = tokenizer.nextToken()) != null) {
				String txt = (token.getKind() == IdRefTokenizer.IdRefToken.DATA) ? token.getValue() : ("{" + token.getValue() + "}");
				builder.append(txt);
			}
			System.out.println("\"" + args[i] + "\" ==> \"" + builder.toString() + "\"");
		}
	}
	
	/**
	 * Creates a new unique id prefix for id attributes of an svg model.
	 * The ids have the following structure:
	 * <pre>{prefix/ext1/...extN}localId</pre>
	 * where:
	 * <dl>
	 * <dt>prefix</dt><dd>is the unique document prefix returned by this method</dd>
	 * <dt>extK</dt><dd>is a model specific extension used to avoid collisions between virtual hiearchies created in the model (such as element/twin)</dd>
	 * <dt>localId</dt><dd>is the actual id appearing in the source document</dd>
	 * </dl>
	 * @return
	 */
	public static String newIdPrefix() {
		docId++;
		return "d" + docId;
	}
	
	/**
	 * Creates a new unique id prefix with the specified extension
	 * @param base The base prefix
	 * @param extension The extension to add
	 * @return The prefix id
	 */
	public static String newPrefixExtension(String base, String extension) {
		return base + "/" + extension;
	}
	
	public static String makeId(String idPrefix, String localId) {
		return "{" + idPrefix + "}" + localId;
	}
	
	/**
	 * Transforms all ids and id-refs in the specified source svg to avoid
	 * collisions with other svgs in other models.
	 * @param srcSvg The source svg
	 * @param idPrefix A prefix to apply to avoid collisions
	 */
	public static void normalizeIds(OMSVGElement srcSvg, String idPrefix) {
		// Collect all the original element ids and replace them with a
		// normalized id
		int idIndex = 0;
		Map<String, Element> idToElement = new HashMap<String, Element>();
		Map<String, String> idToNormalizedId = new HashMap<String, String>();
		List<Element> queue = new ArrayList<Element>();
		queue.add(srcSvg.getElement());
		while (queue.size() > 0) {
			Element element = queue.remove(0);
			String id = element.getId();
			if (id != null) {
				idToElement.put(id, element);
				String normalizedId = makeId(idPrefix, Integer.toString(idIndex++));
				idToNormalizedId.put(id, normalizedId);
				element.setId(normalizedId);
			}
			NodeList<Node> childNodes =  element.getChildNodes();
			for (int i = 0, length = childNodes.getLength(); i < length; i++) {
				Node childNode = childNodes.getItem(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					queue.add((Element)childNode.cast());
				}
			}
		}
		
		// Change all the attributes which are URI references
		Set<String> attNames = new HashSet<String>(Arrays.asList(
			new String[] { "clip-path",
			 "mask",
			 "marker-start",
			 "marker-mid",
			 "marker-end",
			 "fill",
			 "stroke",
			 "filter",
			 "cursor",
			 "style"}));
		queue.add(srcSvg.getElement());
		IdRefTokenizer tokenizer = GWT.create(IdRefTokenizer.class);
		while (queue.size() > 0) {
			Element element = queue.remove(0);
			if (DOMHelper.hasAttributeNS(element, SVGConstants.XLINK_NAMESPACE_URI, SVGConstants.XLINK_HREF_ATTRIBUTE)) {
				String idRef = DOMHelper.getAttributeNS(element, SVGConstants.XLINK_NAMESPACE_URI, SVGConstants.XLINK_HREF_ATTRIBUTE);
				// TODO: Test will probably not work on Opera (where all URLs are made absolute)
				if (idRef.startsWith("#")) {
					// Normalize hrefs to internal elements (such as between two gradients),
					// not hrefs to external elements (such as between an image and its png)
					String normalizeIdRef = idToNormalizedId.get(idRef.substring(1));
					DOMHelper.setAttributeNS(element, SVGConstants.XLINK_NAMESPACE_URI, SVGConstants.XLINK_HREF_ATTRIBUTE, "#" + normalizeIdRef);
				}
			}
			NamedNodeMap<Attr> attrs = DOMHelper.getAttributes(element);
			for (int i = 0, length = attrs.getLength(); i < length; i++) {
				Attr attr = attrs.item(i);
				if (attNames.contains(attr.getName())) {
					StringBuilder builder = new StringBuilder();
					tokenizer.tokenize(attr.getValue());
					IdRefTokenizer.IdRefToken token;
					while ((token = tokenizer.nextToken()) != null) {
						String value = token.getValue();
						if (token.getKind() == IdRefTokenizer.IdRefToken.DATA) {
							builder.append(value);
						} else {
							value = idToNormalizedId.get(value);
							builder.append(value == null ? token.getValue() : value);
						}
					}
					attr.setValue(builder.toString());
				}
			}
			NodeList<Node> childNodes =  element.getChildNodes();
			for (int i = 0, length = childNodes.getLength(); i < length; i++) {
				Node childNode = childNodes.getItem(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					queue.add((Element)childNode.cast());
				}
			}
		}
	}

	/**
	 * Transfers all the children for one element to another element
	 * @param src the source element
	 * @param dest the destination element
	 */
	public static void reparent(OMSVGElement src, OMSVGElement dest) {
		Element srcElement = src.getElement();
		Element destElement = dest.getElement();
		Node node;
		while((node = srcElement.getFirstChild()) != null) {
			destElement.appendChild(srcElement.removeChild(node));
		}
	}

}

