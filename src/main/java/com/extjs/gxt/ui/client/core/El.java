/*
 * Ext GWT 2.2.5 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Direction;
import com.extjs.gxt.ui.client.Style.ScrollDir;
import com.extjs.gxt.ui.client.core.impl.ComputedStyleImpl;
import com.extjs.gxt.ui.client.fx.BaseEffect;
import com.extjs.gxt.ui.client.fx.Fx;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.fx.Move;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Markup;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Region;
import com.extjs.gxt.ui.client.util.Scroll;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.util.TextMetrics;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;

/**
 * Represents an Element in the DOM.
 */
@SuppressWarnings("deprecation")
public class El {

  /**
   * VisMode enumeration. Specifies the the element should hidden using the CSS
   * display or visibility style.
   * 
   */
  public enum VisMode {
    DISPLAY, VISIBILITY
  }

  private static Map<String, Boolean> borderBoxMap = new FastMap<Boolean>();

  /**
   * The globally shared El instance.
   */
  private static Map<String, El> flyweights = new FastMap<El>();

  private static ComputedStyleImpl computedStyle = GWT.create(ComputedStyleImpl.class);
  private static JavaScriptObject leftRightTest;
  private static JavaScriptObject removeStyleNameReCache;

  static {
    GXT.init();
  }

  /**
   * Tests to see if the value has units, otherwise appends the default (px).
   * 
   * @param v the value
   * @return the value with units
   */
  public native static String addUnits(String v, String defaultUnit) /*-{
		if (v === "" || v == "auto") {
			return v;
		}
		if (v === undefined) {
			return '';
		}
		if (typeof v == "number"
				|| !/\d+(px|em|%|en|ex|pt|in|cm|mm|pc)$/i.test(v)) {
			return v + (defaultUnit || 'px');
		}
		return v;
  }-*/;

  public static El fly(com.google.gwt.dom.client.Element element) {
    return fly(element, "_global");
  }

  /**
   * Gets the globally shared flyweight El, with the passed node as the active
   * element. Do not store a reference to this element - the dom node can be
   * overwritten by other code.
   * 
   * @param element the element
   * @return the el instance
   */
  public static El fly(com.google.gwt.dom.client.Element element, String s) {
    assert element != null : "Element my not be null";
    El g = flyweights.get(s);
    if (g == null) {
      g = new El(DOM.createDiv());
      flyweights.put(s, g);
    }
    g.dom = (Element) element;
    return g;
  }

  public static El fly(Element element) {
    return fly(element, "_global");
  }

  /**
   * Gets the globally shared flyweight El, with the passed node as the active
   * element. Do not store a reference to this element - the dom node can be
   * overwritten by other code.
   * 
   * @param element the element to be wrapped
   * @return the global El object
   */
  public static El fly(Element element, String s) {
    assert element != null : "Element my not be null";
    El g = flyweights.get(s);
    if (g == null) {
      g = new El(DOM.createDiv());
      flyweights.put(s, g);
    }
    g.dom = (Element) element;
    return g;
  }

  /**
   * Returns true if the passed element has a border box.
   * 
   * @param element the element to test
   * @return true if the passed element has a border box
   */
  public static boolean isBorderBox(Element element) {
    assert element != null : "Element may not be null";
    String tag = element.getTagName().toLowerCase();
    Boolean r = borderBoxMap.get(tag);
    if (r == null) {
      Element testElement = (Element) Document.get().createElement(tag);
      testElement.getStyle().setPropertyPx("padding", 1);
      testElement.getStyle().setPropertyPx("width", 100);
      testElement.getStyle().setProperty("visibility", "hidden");
      testElement.getStyle().setProperty("position", "absolute");
      XDOM.getBody().appendChild(testElement);
      r = testElement.getOffsetWidth() == 100;
      XDOM.getBody().removeChild(testElement);
      borderBoxMap.put(tag, r);
    }
    return r;
  }

  private native static void disableTextSelectInternal(Element e, boolean disable)/*-{
		if (disable) {
			e.ondrag = function(evt) {
				var targ;
				if (!evt)
					evt = $wnd.event;
				if (evt.target)
					targ = evt.target;
				else if (evt.srcElement)
					targ = evt.srcElement;
				if (targ.nodeType == 3) // defeat Safari bug
					targ = targ.parentNode;
				if (targ.tagName == 'INPUT' || targ.tagName == 'TEXTAREA') {
					return true;
				}
				return false;
			};
			e.onselectstart = function(evt) {
				var targ;
				if (!evt)
					evt = $wnd.event;
				if (evt.target)
					targ = evt.target;
				else if (evt.srcElement)
					targ = evt.srcElement;
				if (targ.nodeType == 3) // defeat Safari bug
					targ = targ.parentNode;
				if (targ.tagName == 'INPUT' || targ.tagName == 'TEXTAREA') {
					return true;
				}
				return false;
			};
		} else {
			e.ondrag = null;
			e.onselectstart = null;
		}
  }-*/;

  /**
   * The wrapped dom element.
   */
  public Element dom;

  private VisMode visiblityMode = VisMode.DISPLAY;

  private String originalDisplay = "block";
  private El _mask;
  private El _maskMsg;
  private boolean isClipped;
  private String[] originalClipped;

  /**
   * Creates a new el instance.
   * 
   * @param element the element to be wrapped
   */
  public El(Element element) {
    assert element != null : "The element may not be null";
    this.dom = element;
  }

  /**
   * Creates a new El instance from the HTML fragment.
   * 
   * @param html the html
   */
  public El(String html) {
    this(XDOM.create(html));
  }

  /**
   * Adds the event type to the element's sunk events.
   * 
   * @param event the events to add
   * @return this
   */
  public El addEventsSunk(int event) {
    int bits = DOM.getEventsSunk(dom);
    DOM.sinkEvents(dom, bits | event);
    return this;
  }

  /**
   * Adds the style name to the element. Duplicate styles are automatically
   * filtered out.
   * 
   * @param styleNames the new style names
   * @return this
   */
  public El addStyleName(String... styleNames) {
    if (styleNames != null) {
      for (String styleName : styleNames) {
        if (styleName != null && !hasStyleName(styleName)) {
          styleName = styleName.trim();
          /* begin laaglu */
//        dom.setClassName(dom.getClassName() + " " + styleName);
          setClassName(dom, getClassName(dom) + " " + styleName);
          /* end laaglu */        }
      }
    }
    return this;
  }

  /**
   * Toggles the given style name, adding if not already present, removing if
   * present.
   * 
   * @param styleName the style name to toggle
   * @return this
   */
  public El toggleStyleName(String styleName) {
    if (hasStyleName(styleName)) {
      removeStyleName(styleName);
    } else {
      addStyleName(styleName);
    }
    return this;
  }

  /**
   * Ensures the element is within the browser viewport.
   * 
   * @param p the target destination
   * @return the new location
   */
  public Point adjustForConstraints(Point p) {
    return getConstrainToXY(XDOM.getBody(), p);
  }

  /**
   * Aligns the element with another element relative to the specified anchor
   * points. Two values from the table below should be passed separated by a
   * dash, the first value is used as the element's anchor point, and the second
   * value is used as the target's anchor point.
   * <p>
   * In addition to the anchor points, the position parameter also supports the
   * "?" character. If "?" is passed at the end of the position string, the
   * element will attempt to align as specified, but the position will be
   * adjusted to constrain to the viewport if necessary. Note that the element
   * being aligned might be swapped to align to a different position than that
   * specified in order to enforce the viewport constraints. Following are all
   * of the supported anchor positions:
   * </p>
   * <dl>
   * <dt>Following are all of the supported anchor positions:</dt>
   * </dl>
   * <code><pre>
   *  Value  Description
   *  -----  -----------------------------
   *  tl     The top left corner (default)
   *  t      The center of the top edge
   *  tr     The top right corner
   *  l      The center of the left edge
   *  c      In the center of the element
   *  r      The center of the right edge
   *  bl     The bottom left corner
   *  b      The center of the bottom edge
   *  br     The bottom right corner
   * </code></pre>
   * 
   * @param align the element to align to
   * @param pos the position to align to
   * @param offsets the offsets or <code>null</code>
   * @return this
   */
  public El alignTo(Element align, String pos, int[] offsets) {
    if (offsets == null) {
      offsets = new int[] {0, 0};
    }
    Point p = getAlignToXY(align, pos, offsets);
    setXY(p);
    return this;
  }

  /**
   * Appends a element.
   * 
   * @param child the element to add
   * @return the child element
   */
  public El appendChild(Element child) {
    dom.appendChild(child);
    return new El(child);
  }

  /**
   * More flexible version of {@link #setStyleAttribute} for setting style
   * properties. Style attribute names must be in lower camel case, e.g.
   * "backgroundColor:white"
   * 
   * @param styles a style specification string
   * @return this
   */
  public native El applyStyles(String styles) /*-{
		var re = /\s?([a-z\-]*)\:\s?([^;]*);?/gi;
		var matches;
		while ((matches = re.exec(styles)) != null) {
			this.@com.extjs.gxt.ui.client.core.El::setStyleAttribute(Ljava/lang/String;Ljava/lang/Object;)(matches[1], matches[2]);
		}
		return this;
  }-*/;

  /**
   * Blinks the element.
   * 
   * @param config the fx config
   * @return this
   */
  public El blink(FxConfig config) {
    BaseEffect.blink(this, config, 50);
    return this;
  }

  /**
   * Removes focus.
   * 
   * @return this
   */
  public El blur() {
    return setFocus(false);
  }

  /**
   * Wraps the specified element with a special markup/CSS block.
   * 
   * @param style a base CSS class to apply to the containing wrapper element
   *          (defaults to 'x-box').
   * @return this
   */
  public El boxWrap(String style) {
    String s = style != null ? style : "x-box";
    El temp = insertHtml("beforeBegin", Format.substitute("<div class={0}>" + Markup.BBOX, s) + "</div>");
    temp.child("." + s + "-mc").appendChild(dom);
    return temp;
  }

  /**
   * Centers the element in the viewport.
   * 
   * @return this
   */
  public El center() {
    return center(null);
  }

  /**
   * Centers the element.
   * 
   * @param constrainViewport true to constrain the element position to the
   *          viewport.
   * @return this
   */
  public El center(boolean constrainViewport) {
    return alignTo(XDOM.getBody(), "c-c" + (constrainViewport ? "?" : ""), null);
  }

  /**
   * Centers an element.
   * 
   * @param container the container element
   * @return this
   */
  public El center(Element container) {
    if (container == null) {
      container = XDOM.getBody();
    }
    return alignTo(container, "c-c", null);
  }

  /**
   * Selects a single child at any depth below this element based on the passed
   * CSS selector.
   * 
   * @param selector the css selector
   * @return the child element
   */
  public El child(String selector) {
    Element child = childElement(selector);
    return child == null ? null : new El(child);
  }

  /**
   * Selects a single child at any depth below this element based on the passed
   * CSS selector.
   * 
   * @param selector the css selector
   * @return the child element
   */
  public Element childElement(String selector) {
    return DomQuery.selectNode(selector, dom);
  }

  /**
   * Returns the element's child.
   * 
   * @param index the index of the child element
   * @return the child element
   */
  public El childNode(int index) {
    Element e = DOM.getChild(dom, index);
    return e == null ? null : new El(e);
  }

  /**
   * Clears the set opacity correctly. This is mostly needed only on IE.
   * 
   * @return this
   */
  public native El clearOpacity() /*-{
		var dom = this.@com.extjs.gxt.ui.client.core.El::dom;
		var style = dom.style;
		if (@com.extjs.gxt.ui.client.GXT::isIE) {
			dom.style.filter = (dom.style.filter || '').replace(
					/alpha\([^\)]*\)/gi, "");
		} else {
			style.opacity = style['-moz-opacity'] = style['-khtml-opacity'] = '';
		}
		return this;
  }-*/;

  /**
   * Generators a native dom click on the element.
   * 
   * @return this
   */
  public native El click() /*-{
		var dom = this.@com.extjs.gxt.ui.client.core.El::dom;
		if (dom.click) {
			dom.click();
		} else {
			var event = $doc.createEvent("MouseEvents");
			event.initEvent('click', true, true, $wnd, 0, 0, 0, 0, 0, false,
					false, false, false, 1, dom);
			dom.dispatchEvent(event);
		}
		return this;
  }-*/;

  /**
   * Clips overflow on the element.
   * 
   * @return this
   */
  public El clip() {
    if (!isClipped) {
      isClipped = true;
      originalClipped = new String[3];
      originalClipped[0] = getStyleAttribute("overflow");
      originalClipped[1] = getStyleAttribute("overflowX");
      originalClipped[2] = getStyleAttribute("overflowY");
      setStyleAttribute("overflow", "hidden");
      setStyleAttribute("overflowX", "hidden");
      setStyleAttribute("overflowY", "hidden");
    }
    return this;
  }

  /**
   * Clones the element.
   * 
   * @param deep true to clone children
   * @return the new element
   */
  public Element cloneNode(boolean deep) {
    return (Element) dom.cloneNode(deep);
  }

  /**
   * Creates and adds a child using the HTML fragment.
   * 
   * @param html the html fragment
   * @return the new child
   */
  public El createChild(String html) {
    return appendChild(XDOM.create(html));
  }

  /**
   * Creates and inserts a child using the HTML fragment.
   * 
   * @param html the html fragment
   * @param insertBefore a child element of this element
   * @return the new child
   */
  public El createChild(String html, Element insertBefore) {
    Element element = XDOM.create(html);
    int idx = DOM.getChildIndex(dom, insertBefore);
    insertChild(element, idx);
    return new El(element);
  }

  /**
   * Disables the element.
   * 
   * @return this
   */
  public El disable() {
    dom.setPropertyBoolean("disabled", true);
    return this;
  }

  /**
   * Enables and disables the browsers default context menu for the specified
   * element.A circular reference will be created when disabling text selection.
   * Disabling should be cleared when the element is detached. See the
   * <code>Component</code> source for an example.
   * 
   * @param disable true to disable, false to enable
   * @return this
   */
  public native El disableContextMenu(boolean disable) /*-{
		var e = this.@com.extjs.gxt.ui.client.core.El::dom;
		e.oncontextmenu = disable ? function() {
			return false
		} : null;
		return this;
  }-*/;

  /**
   * Enables or disables text selection for the element. A circular reference
   * will be created when disabling text selection. Disabling should be cleared
   * when the element is detached. See the <code>Component</code> source for an
   * example.
   * 
   * @param disable true to disable, false to enable
   * @return this
   */
  public El disableTextSelection(boolean disable) {
    setStyleName("x-unselectable", disable);
    setElementAttribute("unselectable", disable ? "on" : "");
    disableTextSelectInternal(dom, disable);
    return this;
  }

  /**
   * Selects a single *direct* child based on the passed CSS selector (the
   * selector should not contain an id).
   * 
   * @param selector the CSS selector
   * @return the child element
   */
  public El down(String selector) {
    Element elem = DomQuery.selectNode(" > " + selector, dom);
    if (elem != null) {
      return new El(elem);
    }
    return null;
  }

  /**
   * Enables the element.
   * 
   * @return this
   */
  public El enable() {
    dom.setPropertyBoolean("disabled", false);
    return this;
  }

  /**
   * Convenience method for setVisibilityMode(VisibilityMode.DISPLAY).
   * 
   * @param display what to set display to when visible
   * @return this
   */
  public El enableDisplayMode(String display) {
    setVisibilityMode(VisMode.DISPLAY);
    if (display != null) {
      originalDisplay = display;
    }
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof El) {
      return getId().equals(((El) obj).getId());
    }
    return super.equals(obj);
  }

  /**
   * Fades in the element.
   * 
   * @param config the fx config
   * @return this
   */
  public El fadeIn(FxConfig config) {
    BaseEffect.fadeIn(this, config);
    return this;
  }

  /**
   * Fades out the element.
   * 
   * @param config the fx config
   * @return this
   */
  public El fadeOut(FxConfig config) {
    BaseEffect.fadeOut(this, config);
    return this;
  }

  /**
   * Toggles the element visibility using a fade effect.
   * 
   * @param config the fx config
   * @return this
   */
  public El fadeToggle(FxConfig config) {
    if (!isVisible()) {
      BaseEffect.fadeIn(this, config);
    } else {
      BaseEffect.fadeOut(this, config);
    }
    return this;
  }

  /**
   * Looks at this node and then at parent nodes for a match of the passed
   * simple selector (e.g. div.some-class or span:first-child).
   * 
   * @param selector the simple selector to test
   * @return the matching element
   */
  public El findParent(String selector, int maxDepth) {
    Element elem = findParentElement(selector, maxDepth);
    if (elem == null) {
      return null;
    }
    return new El(elem);
  }

  /**
   * Looks at this node and then at parent nodes for a match of the passed
   * simple selector (e.g. div.some-class or span:first-child).
   * 
   * @param selector the simple selector to test
   * @param maxDepth the max depth
   * @return the matching element
   */
  public Element findParentElement(String selector, int maxDepth) {
    Element p = dom;
    Element b = XDOM.getBody();
    int depth = 0;
    while (p != null && p.getNodeType() == 1 && (maxDepth == -1 || depth < maxDepth) && p != b) {
      if (DomQuery.is(p, selector)) {
        return p;
      }
      depth++;
      p = (Element) p.getParentElement();
    }
    return null;
  }

  /**
   * Returns the element's first child.
   * 
   * @return the first child
   */
  public El firstChild() {
    Element firstChild = DOM.getFirstChild(dom);
    return firstChild == null ? null : new El(firstChild);
  }

  /**
   * Tries to focus the element.
   * 
   * @return this
   */
  public El focus() {
    return setFocus(true);
  }

  public Point getAlignToXY(Element elem, String p, int ox, int oy) {
    El el = new El(elem);

    if (p == null) {
      p = "tl-bl";
    } else if (p.equals("?")) {
      p = "tl-bl?";
    } else if (p.indexOf("-") == -1) {
      p = "tl-" + p;
    }
    p = p.toLowerCase();
    boolean c = false;
    String p1 = p.substring(0, p.indexOf("-"));
    String p2 = p.substring(p.indexOf("-") + 1, ((c = p.contains("?")) ? p.indexOf("?") : p.length()));
    // Subtract the aligned el's internal xy from the target's offset xy
    // plus custom offset to get the aligned el's new offset xy
    Point a1 = getAnchorXY(p1, true);
    Point a2 = el.getAnchorXY(p2, false);

    int x = a2.x - a1.x + ox;
    int y = a2.y - a1.y + oy;

    if (c) {
      // constrain the aligned el to viewport if necessary
      int w = getWidth();
      int h = getHeight();
      Region r = el.getRegion();
      // 5px of margin for ie
      int dw = XDOM.getViewWidth(false) - 10;
      int dh = XDOM.getViewHeight(false) - 10;

      // If we are at a viewport boundary and the aligned el is anchored on a
      // target border that is
      // perpendicular to the vp border, allow the aligned el to slide on that
      // border,
      // otherwise swap the aligned el to the opposite border of the target.
      char p1y = p1.charAt(0), p1x = p1.charAt(p1.length() - 1);
      char p2y = p2.charAt(0), p2x = p2.charAt(p2.length() - 1);

      boolean swapY = ((p1y == 't' && p2y == 'b') || (p1y == 'b' && p2y == 't'));
      boolean swapX = ((p1x == 'r' && p2x == 'l') || (p1x == 'l' && p2x == 'r'));

      int scrollX = XDOM.getBodyScrollLeft() + 5;
      int scrollY = XDOM.getBodyScrollTop() + 5;

      if ((x + w) > dw + scrollX) {
        x = swapX ? r.left - w : dw + scrollX - w;
      }
      if (x < scrollX) {
        x = swapX ? r.right : scrollX;
      }

      if ((y + h) > (dh + scrollY)) {
        y = swapY ? r.top - h : dh + scrollY - h;
      }
      if (y < scrollY) {
        y = swapY ? r.bottom : scrollY;
      }
    }

    return new Point(x, y);
  }

  /**
   * Gets the x,y coordinates to align this element with another element. See
   * {@link #alignTo} for more info on the supported position values.
   * 
   * @param align the element to align to
   * @param pos the position to align to
   * @param offsets the offsets or <code>null</code>
   * @return the point
   */
  public Point getAlignToXY(Element align, String pos, int[] offsets) {
    if (offsets == null) {
      offsets = new int[] {0, 0};
    }
    return getAlignToXY(align, pos, offsets[0], offsets[1]);
  }

  /**
   * Returns the x,y coordinates specified by the anchor position on the
   * element.
   * 
   * @param anchor the specified anchor position (defaults to "c"). See
   *          {@link #alignTo} for details on supported anchor positions.
   * @param local <code>true</code> to get the local (element top/left-relative)
   *          anchor position instead of page coordinates
   * @return the position
   */
  public Point getAnchorXY(String anchor, boolean local) {
    if (anchor == null) {
      return null;
    }
    boolean vp = false;
    int w;
    int h;
    if (dom == XDOM.getBody() || dom == XDOM.getDocument()) {
      vp = true;
      w = XDOM.getViewWidth(false);
      h = XDOM.getViewHeight(false);
    } else {
      w = getWidth();
      h = getHeight();
    }

    int x = 0, y = 0;
    if (anchor.length() == 1) {
      if ("c".equalsIgnoreCase(anchor)) {
        x = (int) Math.round(w * .5);
        y = (int) Math.round(h * .5);
      } else if ("t".equalsIgnoreCase(anchor)) {
        x = (int) Math.round(w * .5);
        y = 0;
      } else if ("l".equalsIgnoreCase(anchor)) {
        x = 0;
        y = (int) Math.round(h * .5);
      } else if ("r".equalsIgnoreCase(anchor)) {
        x = w;
        y = (int) Math.round(h * .5);
      } else if ("b".equalsIgnoreCase(anchor)) {
        x = (int) Math.round(w * .5);
        y = h;
      }
    } else {
      if ("tl".equalsIgnoreCase(anchor)) {
        x = 0;
        y = 0;
      } else if ("bl".equalsIgnoreCase(anchor)) {
        x = 0;
        y = h;
      } else if ("br".equalsIgnoreCase(anchor)) {
        x = w;
        y = h;
      } else if ("tr".equalsIgnoreCase(anchor)) {
        x = w;
        y = 0;
      }
    }

    if (local) {
      return new Point(x, y);
    }
    if (vp) {
      Scroll sc = getScroll();
      return new Point(x + sc.getScrollLeft(), y + sc.getScrollTop());
    }
    // Add the element's offset xy

    Point o = getXY();
    return new Point(x + o.x, y + o.y);
  }

  /**
   * Returns the width of the border(s) for the specified side(s).
   * 
   * @param sides can be t, l, r, b or any combination of those to add multiple
   *          values. For example, passing lr would get the border (l)eft width
   *          + the border (r)ight width.
   * @return the width of the sides passed added together
   */
  public int getBorderWidth(String sides) {
    double borderWidth = 0;
    List<String> list = new ArrayList<String>();
    if (sides.contains("l")) {
      list.add("borderLeftWidth");
    }
    if (sides.contains("r")) {
      list.add("borderRightWidth");
    }
    if (sides.contains("t")) {
      list.add("borderTopWidth");
    }
    if (sides.contains("b")) {
      list.add("borderBottomWidth");
    }
    FastMap<String> map = getStyleAttribute(list);
    for (String s : map.keySet()) {
      borderWidth += Util.parseFloat(map.get(s), 0);
    }
    return (int) Math.round(borderWidth);
  }

  /**
   * Returns the bottom Y coordinate of the element (element Y position +
   * element height).
   * 
   * @param local
   * @return the bottom value
   */
  public int getBottom(boolean local) {
    return getHeight() + (local ? getTop() : getY());
  }

  /**
   * Returns the elements bounds in page coordinates.
   * 
   * @return the bounds
   */
  public Rectangle getBounds() {
    return getBounds(false, false);
  }

  /**
   * Returns the elements bounds in page coordinates.
   * 
   * @param local if true the element's left and top are returned instead of
   *          page coordinates
   * 
   * @return the bounds
   */
  public Rectangle getBounds(boolean local) {
    return getBounds(local, false);
  }

  /**
   * Returns the element's bounds in page coordinates.
   * 
   * @param local if true the element's left and top are returned instead of
   *          page coordinates
   * @param adjust if true sizes get adjusted
   * 
   * @return the element's bounds
   */
  public Rectangle getBounds(boolean local, boolean adjust) {
    Size s = getSize(adjust);
    Rectangle rect = new Rectangle();
    rect.width = s.width;
    rect.height = s.height;
    if (local) {
      rect.x = getLeft(true);
      rect.y = getTop(true);
    } else {
      Point p = getXY();
      rect.x = p.x;
      rect.y = p.y;
    }
    return rect;
  }

  /**
   * Returns a child element.
   * 
   * @param index the child index
   * @return the child
   */
  public El getChild(int index) {
    Element child = getChildElement(index);
    return child == null ? null : new El(child);
  }

  /**
   * Returns a child element.
   * 
   * @param index the child index
   * @return the child
   */
  public Element getChildElement(int index) {
    return DOM.getChild(dom, index);
  }

  /**
   * Returns the index of the child element.
   * 
   * @return the index
   */
  public int getChildIndex(Element child) {
    return DOM.getChildIndex(dom, child);
  }

  /**
   * Returns the element's client height property.
   * 
   * @return the client width value
   */
  public int getClientHeight() {
    return DOM.getElementPropertyInt(dom, "clientHeight");
  }

  /**
   * Returns the element's client width property.
   * 
   * @return the client width value
   */
  public int getClientWidth() {
    return DOM.getElementPropertyInt(dom, "clientWidth");
  }

  /**
   * Returns either the offsetHeight or the height of this element based on it's
   * CSS height.
   * 
   * @return the height
   */
  public int getComputedHeight() {
    int h = getHeight();
    if (h == 0) {
      h = getIntStyleAttribute("height");
    }
    return h;
  }

  /**
   * Returns either the offsetWidth or the width of this element based on it's
   * CSS width.
   * 
   * @return the width
   */
  public int getComputedWidth() {
    int w = getWidth();
    if (w == 0) {
      w = getIntStyleAttribute("width");
    }
    return w;
  }

  /**
   * Returns the sum width of the padding and borders for all "sides". See
   * #getBorderWidth() for more information about the sides.
   * 
   * @return the frame size
   */
  public Size getFrameSize() {
    double width = 0;
    double height = 0;
    List<String> list = new ArrayList<String>();
    list.add("paddingLeft");
    list.add("borderLeftWidth");

    list.add("paddingRight");
    list.add("borderRightWidth");

    list.add("paddingTop");
    list.add("borderTopWidth");

    list.add("paddingBottom");
    list.add("borderBottomWidth");

    FastMap<String> map = getStyleAttribute(list);
    for (String s : map.keySet()) {
      if (isLeftorRight(s)) {
        width += Util.parseFloat(map.get(s), 0);
      } else {
        height += Util.parseFloat(map.get(s), 0);
      }
    }
    return new Size((int) Math.round(width), (int) Math.round(height));
  }

  /**
   * Returns the sum width of the padding and borders for the passed "sides".
   * See #getBorderWidth() for more information about the sides.
   * 
   * @param sides sides
   * @return the width
   */
  public int getFrameWidth(String sides) {
    double frameWidth = 0;
    List<String> list = new ArrayList<String>();
    if (sides.contains("l")) {
      list.add("paddingLeft");
      list.add("borderLeftWidth");
    }
    if (sides.contains("r")) {
      list.add("paddingRight");
      list.add("borderRightWidth");
    }
    if (sides.contains("t")) {
      list.add("paddingTop");
      list.add("borderTopWidth");
    }
    if (sides.contains("b")) {
      list.add("paddingBottom");
      list.add("borderBottomWidth");
    }
    FastMap<String> map = getStyleAttribute(list);
    for (String s : map.keySet()) {
      frameWidth += Util.parseFloat(map.get(s), 0);
    }
    return (int) Math.round(frameWidth);
  }

  /**
   * Returns the offset height of the element.
   * 
   * @return the height
   */
  public int getHeight() {
    return dom.getOffsetHeight();
  }

  /**
   * Returns the element's height.
   * 
   * @param content true to get the height minus borders and padding
   * @return the element's height
   */
  public int getHeight(boolean content) {
    int h = getHeight();
    if (content) {
      h -= getFrameWidth("tb");
    }
    return Math.max(0, h);
  }

  /**
   * Returns the element's id.
   * 
   * @return the id
   */
  public String getId() {
    String id = DOM.getElementProperty(dom, "id");
    if (id == null || (id != null && id.length() == 0)) {
      id = XDOM.getUniqueId();
      setId(id);
    }
    return id;
  }

  /**
   * Returns the element's inner HTML.
   * 
   * @return the inner html
   */
  public String getInnerHtml() {
    return DOM.getInnerHTML(dom);
  }

  /**
   * Returns the element's style value.
   * 
   * @param attr the attribute name
   * @return the value
   */
  public int getIntStyleAttribute(String attr) {
    String v = DOM.getStyleAttribute(dom, attr);
    if (v == null || v.equals("")) {
      return 0;
    }
    return Util.parseInt(v, 0);
  }

  /**
   * Returns the element's content area bounds.
   * 
   * @return the bounds
   */
  public Rectangle getLayoutBounds() {
    Rectangle r = getBounds();
    r.width -= getFrameWidth("lr");
    r.height -= getFrameWidth("tb");
    return r;
  }

  /**
   * Returns the top Y coordinate.
   * 
   * @return the top value
   */
  public int getLeft() {
    return getLeft(true);
  }

  /**
   * Gets the left X coordinate.
   * 
   * @param local true to get the local css position instead of page coordinate
   * @return the left value
   */
  public int getLeft(boolean local) {
    return local ? Util.parseInt(getStyleAttribute("left"), 0) : getX();
  }

  /**
   * Returns an object with properties top, left, right and bottom representing
   * the margins of this element unless sides is passed, then it returns the
   * calculated width of the sides (see #getPadding).
   * 
   * @param sides any combination of l, r, t, b to get the sum of those sides
   * @return the margins
   */
  public int getMargins(String sides) {
    double margin = 0;
    List<String> list = new ArrayList<String>();
    if (sides.contains("l")) {
      list.add("marginLeft");
    }
    if (sides.contains("r")) {
      list.add("marginRight");
    }
    if (sides.contains("t")) {
      list.add("marginTop");
    }
    if (sides.contains("b")) {
      list.add("marginBottom");
    }
    FastMap<String> map = getStyleAttribute(list);
    for (String s : map.keySet()) {
      margin += Util.parseFloat(map.get(s), 0);
    }
    return (int) Math.round(margin);
  }

  /**
   * Returns the offsets between two elements. Both element must be part of the
   * DOM tree and not have display:none to have page coordinates.
   * 
   * @param to the to element
   * @return the xy page offsets
   */
  public Point getOffsetsTo(Element to) {
    Point o = getXY();
    Point e = El.fly(to, "_internal").getXY();
    return new Point(o.x - e.x, o.y - e.y);
  }

  /**
   * Returns the element's outer HTML.
   * 
   * @return the inner html
   */
  public String getOuterHtml() {
    return dom.getAttribute("outerHTML");
  }

  /**
   * Gets the width of the padding(s) for the specified side(s).
   * 
   * @param sides can be t, l, r, b or any combination of those to add multiple
   *          values. For example, passing lr would get the border (l)eft width
   *          + the border (r)ight width.
   * @return the width of the sides passed added together
   */
  public int getPadding(String sides) {
    double padding = 0;
    List<String> list = new ArrayList<String>();
    if (sides.contains("l")) {
      list.add("paddingLeft");
    }
    if (sides.contains("r")) {
      list.add("paddingRight");
    }
    if (sides.contains("t")) {
      list.add("paddingTop");
    }
    if (sides.contains("b")) {
      list.add("paddingBottom");
    }
    FastMap<String> map = getStyleAttribute(list);
    for (String s : map.keySet()) {
      padding += Util.parseFloat(map.get(s), 0);
    }
    return (int) Math.round(padding);
  }

  /**
   * Returns the element's parent.
   * 
   * @return the parent
   */
  public El getParent() {
    Element e = DOM.getParent(dom);
    return e == null ? null : new El(e);
  }

  /**
   * Returns the region of the given element. The element must be part of the
   * DOM tree to have a region.
   * 
   * @return a region containing top, left, bottom, right
   */
  public Region getRegion() {
    Rectangle bounds = getBounds();
    Region r = new Region();
    r.left = bounds.x;
    r.top = bounds.y;
    r.right = r.left + bounds.width;
    r.bottom = r.top + bounds.height;
    return r;
  }

  /**
   * Returns the right X coordinate of the element (element X position + element
   * width).
   * 
   * @param local <code>true</code> to get the local css position instead of
   *          page coordinate
   * @return the right value
   */
  public int getRight(boolean local) {
    return getWidth() + (local ? getLeft(true) : getX());
  }

  /**
   * Returns the body elements current scroll position.
   * 
   * @return the scroll position
   */
  public Scroll getScroll() {
    if (dom == XDOM.getBody() || dom == XDOM.getDocument()) {
      return new Scroll(XDOM.getBodyScrollLeft(), XDOM.getBodyScrollTop());
    } else {
      return new Scroll(getScrollLeft(), getScrollTop());
    }
  }

  /**
   * Returns the horizontal scroll position.
   * 
   * @return the scroll position
   */
  public int getScrollLeft() {
    return DOM.getElementPropertyInt(dom, "scrollLeft");
  }

  /**
   * Returns the current vertical scroll position.
   * 
   * @return the scroll position
   */
  public int getScrollTop() {
    return DOM.getElementPropertyInt(dom, "scrollTop");
  }

  /**
   * Returns the size of the element.
   * 
   * @return the size
   */
  public Size getSize() {
    return getSize(false);
  }

  /**
   * Returns the element's size.
   * 
   * @param content true to get the size minus borders and padding
   * @return the size
   */
  public Size getSize(boolean content) {
    int w = getWidth();
    int h = getHeight();
    if (content) {
      Size frameWidth = getFrameSize();
      w -= frameWidth.width;
      h -= frameWidth.height;
    }
    return new Size(Math.max(0, w), Math.max(0, h));
  }

  public FastMap<String> getStyleAttribute(List<String> attr) {
    return computedStyle.getStyleAttribute(dom, attr);
  }

  /**
   * Normalizes currentStyle and computedStyle.
   * 
   * @param attr the style attribute whose value is returned.
   * @return the current value of the style attribute for this element.
   */
  public String getStyleAttribute(String attr) {
    return getStyleAttribute(Arrays.asList(attr)).get(attr);
  }

  /**
   * Returns the style width.
   * 
   * @return the style width
   */
  public int getStyleHeight() {
    String h = dom.getStyle().getProperty("height");
    if (h == null || h.equals("")) return 0;
    if (h.matches("(auto|em|%|en|ex|pt|in|cm|mm|pc)")) {
      return 0;
    }
    return Util.parseInt(h, 0);
  }

  /**
   * Returns the element's style name.
   * 
   * @return the style name
   */
  public String getStyleName() {
	  /* begin laaglu */
//    return dom.getClassName();
	  return getClassName(dom);
	  /* end laaglu */
  }

  public Size getStyleSize() {
    return getStyleSize(true);
  }

  /**
   * Returns the element's size, using style attribute before offsets.
   * 
   * @return the size
   */
  public Size getStyleSize(boolean contentOnly) {
    int h = Util.parseInt(dom.getStyle().getProperty("height"), Style.DEFAULT);
    int w = Util.parseInt(dom.getStyle().getProperty("width"), Style.DEFAULT);

    boolean isBorderBox = isBorderBox();

    if (isBorderBox && contentOnly && w != Style.DEFAULT) {
      w -= getFrameWidth("lr");
      if (w < 0) {
        w = Style.DEFAULT;
      }
    } else if (!isBorderBox && !contentOnly && w != Style.DEFAULT) {
      w += getFrameWidth("lr");
    }
    if (isBorderBox && contentOnly && h != Style.DEFAULT) {
      h -= getFrameWidth("tb");
      if (h < 0) {
        h = Style.DEFAULT;
      }
    } else if (!isBorderBox && !contentOnly && h != Style.DEFAULT) {
      h += getFrameWidth("tb");
    }

    int offsetWidth = Style.DEFAULT;
    int offsetHeight = Style.DEFAULT;
    if (w == Style.DEFAULT && h == Style.DEFAULT) {
      Size s = getSize(contentOnly);
      offsetWidth = s.width;
      offsetHeight = s.height;
      if (s.width > 0) {
        w = s.width;
      }
      if (s.height > 0) {
        h = s.height;
      }
    } else if (w == Style.DEFAULT) {
      offsetWidth = getWidth(contentOnly);
      if (offsetWidth > 0) {
        w = offsetWidth;
      }
    } else if (h == Style.DEFAULT) {
      offsetHeight = getHeight(contentOnly);
      if (offsetHeight > 0) {
        h = offsetHeight;
      }
    }

    List<String> l = new ArrayList<String>();
    if (w == Style.DEFAULT) {
      l.add("width");
    }
    if (h == Style.DEFAULT) {
      l.add("height");
    }
    Map<String, String> map = getStyleAttribute(l);
    if (map != null) {
      String wid = map.get("width");
      if (wid != null) {
        w = Util.parseInt(wid, Style.DEFAULT);
        if (offsetWidth == 0 && isBorderBox && contentOnly && w != Style.DEFAULT && !GXT.isIE) {
          w -= getFrameWidth("lr");
        } else if (GXT.isIE && isBorderBox && w != Style.DEFAULT && contentOnly) {
          w -= getFrameWidth("lr");
        } else if (offsetWidth == 0 && !isBorderBox && !contentOnly && w != Style.DEFAULT) {
          w += getFrameWidth("lr");
        }
      }
      String hei = map.get("height");
      if (hei != null) {
        h = Util.parseInt(hei, Style.DEFAULT);
        if (offsetHeight == 0 && isBorderBox && contentOnly && h != Style.DEFAULT && !GXT.isIE) {
          h -= getFrameWidth("tb");
        } else if (GXT.isIE && isBorderBox && h != Style.DEFAULT && contentOnly) {
          h -= getFrameWidth("tb");
        } else if (offsetHeight == 0 && !isBorderBox && !contentOnly && h != Style.DEFAULT) {
          h += getFrameWidth("tb");
        }
      }
    }
    if (w == Style.DEFAULT && h == Style.DEFAULT) {
      return new Size(offsetWidth, offsetHeight);
    }
    return new Size(w != Style.DEFAULT ? w : offsetWidth, h != Style.DEFAULT ? h : offsetHeight);
  }

  /**
   * Returns the style width. A value is only returned if the specified style is
   * in pixels.
   * 
   * @return the style width
   */
  public int getStyleWidth() {
    String w = dom.getStyle().getProperty("width");
    if (w == null || w.equals("")) return 0;
    if (w.matches("(auto|em|%|en|ex|pt|in|cm|mm|pc)")) {
      return 0;
    }
    return Util.parseInt(w, 0);
  }

  /**
   * Returns the element's sub child.
   * 
   * @param depth the child node depth
   * @return the child element
   */
  public Element getSubChild(int depth) {
    Element child = dom;
    while (depth-- > 0) {
      child = DOM.getChild(child, 0);
    }
    return child;
  }

  /**
   * Returns the measured width of the element's text.
   * 
   * @return the width
   */
  public int getTextWidth() {
    String html = getInnerHtml();
    TextMetrics metrics = TextMetrics.get();
    metrics.bind(dom);
    return metrics.getWidth(html);
  }

  /**
   * Returns the top Y coordinate.
   * 
   * @return the top value
   */
  public int getTop() {
    return getTop(true);
  }

  /**
   * Gets the top Y coordinate.
   * 
   * @param local true to get the local css position instead of page coordinate
   * @return the top value
   */
  public int getTop(boolean local) {
    return local ? Util.parseInt(getStyleAttribute("top"), 0) : getY();
  }

  /**
   * Returns the the "value" attribute.
   * 
   * @return the value
   */
  public String getValue() {
    return DOM.getElementProperty(dom, "value");
  }

  /**
   * Returns the offset width.
   * 
   * @return the width
   */
  public int getWidth() {
    return dom.getOffsetWidth();
  }

  /**
   * Returns the element's width.
   * 
   * @param content true to get the width minus borders and padding
   * @return the width
   */
  public int getWidth(boolean content) {
    int w = getWidth();
    if (content) {
      w -= getFrameWidth("lr");
    }
    return Math.max(0, w);
  }

  /**
   * Gets the current X position of the element based on page coordinates.
   * Element must be part of the DOM tree to have page coordinates.
   * 
   * @return the x position of the element
   */
  public int getX() {
    return dom.getAbsoluteLeft();
  }

  /**
   * Gets the current position of the element based on page coordinates. Element
   * must be part of the DOM tree to have page coordinates.
   * 
   * @return the location
   */
  public Point getXY() {
    return new Point(getX(), getY());
  }

  /**
   * Gets the current Y position of the element based on page coordinates.
   * 
   * @return the y position of the element
   */
  public int getY() {
    return dom.getAbsoluteTop();
  }

  /**
   * Returns the element's z-index.
   * 
   * @return the z-index
   */
  public int getZIndex() {
    return Util.parseInt(getStyleAttribute("zIndex"), 0);
  }

  /**
   * Checks if the specified CSS style name exists on this element's DOM node.
   * 
   * @param style the style name
   * @return true if the style name exists, else false
   */
  public boolean hasStyleName(String style) {
	  /* begin laaglu */
//	  String cls = dom.getClassName();
	  String cls = getClassName(dom);
	  /* end laaglu */    
	  return (" " + cls + " ").indexOf(" " + style + " ") != -1 ? true : false;
  }

  /**
   * Hides this element
   * 
   * @return this
   */
  public El hide() {
    return setVisible(false);
  }

  /**
   * Inserts this element before the passed element.
   * 
   * @param before the element to insert before
   * @return this
   */
  public El insertBefore(Element before) {
    before.getParentElement().insertBefore(dom, before);
    return this;
  }

  /**
   * Inserts the element as a child before the given element.
   * 
   * @param child the element to insert
   * @param before the element the child will be inserted before
   * @return this
   */
  public El insertBefore(Element child, Element before) {
    dom.insertBefore(child, before);
    return this;
  }

  /**
   * Inserts the elements as a child before the given element.
   * 
   * @param elements the elements to insert
   * @param before the element the children will be inserted before
   * @return this
   */
  public El insertBefore(Element[] elements, Element before) {
    for (int i = 0; i < elements.length; i++) {
      insertBefore(elements[i], before);
    }
    return this;
  }

  /**
   * Inserts an element at the specified index.
   * 
   * @param child the child element
   * @param index the insert location
   * @return this
   */
  public El insertChild(Element child, int index) {
    DOM.insertChild(dom, child, index);
    return this;
  }

  /**
   * Inserts the children at the specified index.
   * 
   * @param children the children to add
   * @param index the insert location
   * @return this
   */
  public El insertChild(Element[] children, int index) {
    for (int i = children.length - 1; i >= 0; i--) {
      DOM.insertChild(dom, children[i], index);
    }
    return this;
  }

  /**
   * Inserts an element as the first child.
   * 
   * @param element the child element
   * @return this
   */
  public El insertFirst(Element element) {
    DOM.insertChild(dom, element, 0);
    return this;
  }

  /**
   * Inserts the children.
   * 
   * @param elems the child elements
   * @return this
   */
  public El insertFirst(Element[] elems) {
    for (int i = 0; i < elems.length; i++) {
      DOM.appendChild(dom, elems[i]);
    }
    return this;
  }

  /**
   * Creates and inserts a child element.
   * 
   * @param html the HTML fragment
   * @return the new child
   */
  public El insertFirst(String html) {
    return new El(DomHelper.insertFirst(dom, html));
  }

  /**
   * Inserts an html fragment into this element
   * 
   * @param where where to insert the html in relation to el - beforeBegin,
   *          afterBegin, beforeEnd, afterEnd.
   * @param html the HTML fragment
   * @return the inserted node (or nearest related if more than 1 inserted)
   */
  public El insertHtml(String where, String html) {
    return new El(DomHelper.insertHtml(where, dom, html));
  }

  /**
   * Inserts the element into the given parent.
   * 
   * @param parent the parent element
   * @return this
   */
  public El insertInto(Element parent) {
    fly(parent, "_internal").appendChild(dom);
    return this;
  }

  /**
   * Inserts a element.
   * 
   * @param parent the parent element
   * @param index the insert index
   * @return this
   */
  public El insertInto(Element parent, int index) {
    fly(parent, "_internal").insertChild(dom, index);
    return this;
  }

  /**
   * Inserts the child element as the last child.
   * 
   * @param child the element to insert
   * @return this
   */
  public El insertLast(Element child) {
    int idx = dom.getChildNodes().getLength();
    insertChild(child, idx);
    return this;
  }

  /**
   * Inserts the passed element as a sibling of this element.
   * 
   * @param elem the element to insert
   * @param where 'before' or 'after'
   * @return the inserted element
   */
  public Element insertSibling(Element elem, String where) {
    Element refNode = where.equals("before") ? dom : nextSibling();
    if (refNode == null) {
      DOM.appendChild(DOM.getParent(dom), elem);
    } else {
      DOM.insertBefore(getParent().dom, elem, refNode);
    }
    return elem;
  }

  /**
   * Inserts the passed elements as a sibling of this element.
   * 
   * @param elems the elements to insert
   * @param where where 'before' or 'after'
   * @return this
   */
  public El insertSibling(Element[] elems, String where) {
    for (int i = 0; i < elems.length; i++) {
      insertSibling(elems[i], where);
    }
    return this;
  }

  /**
   * Returns true if this element matches the passed simple selector (e.g.
   * div.some-class or span:first-child).
   * 
   * @param selector selector
   * @return true if the element matches the selector, else false
   */
  public boolean is(String selector) {
    return DomQuery.is(dom, selector);
  }

  /**
   * Returns true if the element is a border box.
   * 
   * @return true for border box
   */
  public boolean isBorderBox() {
    return isBorderBox(dom);
  }

  /**
   * Returns true if the element is part of the browser's DOM.
   * 
   * @return the dom state
   */
  public boolean isConnected() {
    return Document.get().getBody().isOrHasChild(dom);
  }

  /**
   * Returns true if this element is masked.
   * 
   * @return the masked state
   */
  public boolean isMasked() {
    return _mask != null && _mask.isVisible();
  }

  /**
   * Returns true if the child element is or a child of this element.
   * 
   * @param child the child element
   * @return true if is or child of element
   */
  public boolean isOrHasChild(Element child) {
    return DOM.isOrHasChild(dom, child);
  }

  /**
   * Returns whether the element is scrollable (x or y).
   * 
   * @return true if scrollable
   */
  public boolean isScrollable() {
    return isScrollableX() || isScrollableY();
  }

  /**
   * Returns whether the element is scrollable on the x-axis.
   * 
   * @return true if scrollable on the x-axis
   */
  public boolean isScrollableX() {
    return dom.getScrollWidth() > dom.getClientWidth();
  }

  /**
   * Returns whether the element is scrollable on the y-axis.
   * 
   * @return true if scrollable on the y-axis
   */
  public boolean isScrollableY() {
    return dom.getScrollHeight() > dom.getClientHeight();
  }

  public boolean isStyleAttribute(Map<String, String> map, boolean matchAll) {
    Set<String> collection = map.keySet();
    FastMap<String> a = getStyleAttribute(new ArrayList<String>(collection));
    for (String s : collection) {
      if (map.get(s).equals(a.get(s))) {
        if (!matchAll) {
          return true;
        }
      } else {
        if (matchAll) {
          return false;
        }
      }
    }
    return false;
  }

  public boolean isStyleAttribute(String attr, String value) {
    String a = getStyleAttribute(attr);
    return a != null && a.equals(value);
  }

  /**
   * Returns <code>true</code> if the element is visible using the css
   * 'visibiliy' attribute.
   * 
   * @return the visible state
   */
  public boolean isVisibility() {
    return isStyleAttribute("visibility", "hidden");
  }

  /**
   * Returns whether the element is currently visible.
   * 
   * @return true if visible
   */
  public boolean isVisible() {
    return isVisible(false);
  }

  /**
   * Returns whether the element is currently visible.
   * 
   * @param deep true to deep test
   * 
   * @return true if visible
   */
  public boolean isVisible(boolean deep) {
    Map<String, String> map = new FastMap<String>();
    map.put("visibility", "hidden");
    map.put("display", "none");
    boolean vis = !isStyleAttribute(map, false);
    El parent = getParent();
    Element p = parent != null ? parent.dom : null;
    if (p == null) {
      return false;
    }
    if (!deep || !vis) {
      return vis;
    }
    while (p != null && p != XDOM.getBody()) {
      if (!fly(p, "_isVisible").isVisible()) {
        return false;
      }
      p = (Element) p.getParentElement();
    }

    return true;

  }

  /**
   * Returns the element's last child.
   * 
   * @return the last child
   */
  public El lastChild() {
    Element e = DOM.getChild(dom, DOM.getChildCount(dom) - 1);
    return e == null ? null : new El(e);
  }

  /**
   * Retrieves the data using the request builder and updates the element'c
   * contents.
   * <p>
   * This method is subject to change.
   * 
   * @param builder the request builder
   */
  public Request load(RequestBuilder builder) {
    try {
      builder.setCallback(new RequestCallback() {

        public void onError(Request request, Throwable exception) {
          setInnerHtml(exception.getMessage());
        }

        public void onResponseReceived(Request request, Response response) {
          setInnerHtml(response.getText());
        }

      });
      return builder.send();
    } catch (Exception e) {
      setInnerHtml(e.getMessage());
      return null;
    }
  }

  /**
   * Makes an element positionable.
   */
  public El makePositionable() {
    return makePositionable(false);
  }

  /**
   * Makes an element positionable.
   * 
   * @param absolute <code>true</code> to position absolutely
   * @return this
   */
  public El makePositionable(boolean absolute) {
    if (absolute) {
      setStyleAttribute("position", "absolute");
    } else if ("static".equals(getStyleAttribute("position"))) {
      setStyleAttribute("position", "relative");
    }
    return this;
  }

  /**
   * Puts a mask over this element to disable user interaction.
   * 
   * @return the mask element
   */
  public El mask() {
    return mask(null, null);
  }

  /**
   * Puts a mask over this element to disable user interaction.
   * 
   * @param message a message to display in the mask
   * @return the mask element
   */
  public El mask(String message) {
    return mask(message, null);
  }

  /**
   * Puts a mask over this element to disable user interaction.
   * 
   * @param message a message to display in the mask
   * @param messageStyleName a CSS style name to be applied to the message text
   * @return the mask element
   */
  public El mask(String message, String messageStyleName) {
    if ("static".equals(getStyleAttribute("position"))) {
      addStyleName("x-masked-relative");
    }
    if (_maskMsg != null) {
      _maskMsg.remove();
    }
    if (_mask != null) {
      _mask.remove();
    }

    _mask = new El("<div class='ext-el-mask'></div>");

    addStyleName("x-masked");
    _mask.setDisplayed(true);

    appendChild(_mask.dom);
    if (message != null) {
      _maskMsg = new El("<div class='ext-el-mask-msg'><div></div></div>");
      if (messageStyleName != null) {
        _maskMsg.addStyleName(messageStyleName);
      }
      _maskMsg.firstChild().setInnerHtml(message);
      _maskMsg.setDisplayed(true);

      appendChild(_maskMsg.dom);
      _maskMsg.center(dom);
    }
    if (GXT.isIE && !(GXT.isIE7 && GXT.isStrict) && "auto".equals(getStyleAttribute("height"))) {
      _mask.setSize(getWidth(), getHeight());
    }

    return _mask;
  }

  /**
   * Returns the elements next sibling.
   * 
   * @return the sibling element
   */
  public Element nextSibling() {
    return DOM.getNextSibling(dom);
  }

  /**
   * Returns the elements previous sibling.
   * 
   * @return the previous sibling
   */
  public Element previousSibling() {
    return dom.getPreviousSibling().cast();
  }

  /**
   * Removes this element from the DOM
   */
  public El remove() {
    return removeFromParent();
  }

  /**
   * Removes a child.
   * 
   * @param child the child to remove
   * @return this
   */
  public El removeChild(Element child) {
    dom.removeChild(child);
    return this;
  }

  /**
   * Removes all the elements children.
   */
  public El removeChildren() {
    El child = null;
    while ((child = firstChild()) != null) {
      dom.removeChild(child.dom);
    }
    setInnerHtml("");
    return this;
  }

  /**
   * Removes the element from it's parent.
   */
  public El removeFromParent() {
    com.google.gwt.dom.client.Element p = dom.getParentElement();
    if (p != null) {
      p.removeChild(dom);
    }
    return this;
  }

  /**
   * Removes the style names(s) from the element.
   * 
   * @param styleNames the style names
   * @return this
   */
  public El removeStyleName(String... styleNames) {
    for (String s : styleNames) {
      removeStyleName(s);
    }
    return this;
  }

  /**
   * Removes a style name.
   * 
   * @param styleName the style names to remove
   * @return this
   */
  public native El removeStyleName(String styleName) /*-{
		var dom = this.@com.extjs.gxt.ui.client.core.El::dom;
		if (!@com.extjs.gxt.ui.client.core.El::removeStyleNameReCache) {
			@com.extjs.gxt.ui.client.core.El::removeStyleNameReCache = {};
		}
		if (styleName && dom.className) {
			var s = @com.extjs.gxt.ui.client.core.El::removeStyleNameReCache[styleName] = @com.extjs.gxt.ui.client.core.El::removeStyleNameReCache[styleName]
					|| new RegExp('(?:^|\\s+)' + styleName + '(?:\\s+|$)', "g");
			dom.className = dom.className.replace(s, " ");
		}
		return this;
  }-*/;

  /**
   * Forces the Browser to repaint this element.
   * 
   * @return this
   */
  public El repaint() {
    addStyleName("x-repaint");
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        removeStyleName("x-repaint");
      }
    });
    return this;
  }

  /**
   * Replaces a style name on the element with another. If the old name does not
   * exist, the new name will simply be added.
   * 
   * @param oldStyle the style to replace
   * @param newStyle the new style
   * @return this
   */
  public El replaceStyleName(String oldStyle, String newStyle) {
    return removeStyleName(oldStyle).addStyleName(newStyle);
  }

  public El scrollIntoView(Element container, boolean hscroll) {
    return scrollIntoView(container, hscroll, null);
  }

  /**
   * Scrolls the element into view.
   * 
   * @param container the container element
   * @param hscroll <code>false</code> to disable horizontal scrolling.
   */
  public El scrollIntoView(Element container, boolean hscroll, int[] offsets) {
    if (offsets == null) {
      offsets = new int[] {0, 0};
    }
    Element c = container != null ? container : XDOM.getBody();

    Point o = getOffsetsTo(c);
    int l = o.x;
    int t = o.y;
    l = l + c.getScrollLeft();
    t = t + c.getScrollTop();
    int b = t + getHeight() + offsets[0];
    int r = l + getWidth() + offsets[1];

    int ch = c.getClientHeight();
    int ct = c.getScrollTop();
    int cb = ct + ch;

    if (getHeight() > ch || t < ct) {
      c.setScrollTop(t);
    } else if (b > cb) {
      c.setScrollTop(b - ch);
    }

    if (hscroll) {
      int cl = c.getScrollLeft();
      int cw = c.getClientWidth();
      int cr = cl + cw;

      if (getWidth() > cw || l < cl) {
        c.setScrollLeft(l);
      } else if (r > cr) {
        c.setScrollLeft(r - cw);
      }
    }
    return this;
  }

  /**
   * Scrolls this element the specified scroll point.
   * 
   * @param side either "left" for scrollLeft values or "top" for scrollTop
   *          values.
   * @param value the new scroll value
   * @return this
   */
  public El scrollTo(String side, int value) {
    if ("left".equalsIgnoreCase(side)) {
      setScrollLeft(value);
    } else if ("top".equalsIgnoreCase(side)) {
      setScrollTop(value);
    }
    return this;
  }

  /**
   * Scrolls this element the specified scroll point.
   * 
   * @param side side either "left" for scrollLeft values or "top" for scrollTop
   *          values.
   * @param value the new scroll value
   * @param config the fx config
   * @return this
   */
  public El scrollTo(String side, int value, FxConfig config) {
    ScrollDir dir = ScrollDir.VERTICAL;
    if (side.equalsIgnoreCase("left")) {
      dir = ScrollDir.HORIZONTAL;
    }
    BaseEffect.scroll(this, config, dir, value);
    return this;
  }

  /**
   * Selects child nodes based on the passed CSS selector (the selector should
   * not contain an id).
   * 
   * @param selector the selector/xpath query
   * @return the matching elements
   */
  public NodeList<Element> select(String selector) {
    return DomQuery.select(selector, dom);
  }

  /**
   * Selects a single element.
   * 
   * @param selector the CSS selector
   * @return the matching element
   */
  public El selectNode(String selector) {
    Element el = DomQuery.selectNode(selector, dom);
    if (el != null) {
      return new El(el);
    }
    return null;
  }

  /**
   * Adds or removes a border. The style name 'x-border' is added to the widget
   * to display a border.
   * 
   * @param show the show state
   * @return this
   */
  public El setBorders(boolean show) {
    if (show) {
      addStyleName("x-border");
      setStyleAttribute("borderWidth", "1px");
    } else {
      removeStyleName("x-border");
      setStyleAttribute("borderWidth", "0px");
    }
    return this;
  }

  /**
   * Sets the element's bounds.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the new width
   * @param height the new height
   * @return this
   */
  public El setBounds(int x, int y, int width, int height) {
    return setBounds(x, y, width, height, false);
  }

  /**
   * Sets the element's bounds.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the new width
   * @param height the new height
   * @param adjust true to adjust for box model issues
   * @return this
   */
  public El setBounds(int x, int y, int width, int height, boolean adjust) {
    setPagePosition(x, y);
    setSize(width, height, adjust);
    return this;
  }

  /**
   * Sets the element's bounds.
   * 
   * @param bounds the new bounds
   * @return this
   */
  public El setBounds(Rectangle bounds) {
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    return this;
  }

  /**
   * Sets the element's bounds.
   * 
   * @param bounds the new bounds
   * @param content <code>true</code> to adjust for box model issues
   * @return this
   */
  public El setBounds(Rectangle bounds, boolean content) {
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height, content);
    return this;
  }

  /**
   * Sets the CSS display property.
   * 
   * @param display true to display the element using its default display
   * @return this
   */
  public El setDisplayed(boolean display) {
    String value = display ? originalDisplay : "none";
    setStyleAttribute("display", value);
    return this;
  }

  /**
   * Sets the CSS display property.
   * 
   * @param display the display value
   * @return this
   */
  public El setDisplayed(String display) {
    setStyleAttribute("display", display);
    return this;
  }

  /**
   * Sets an element's attribute.
   * 
   * @param attr the attribute name
   * @param value the value
   * @return this
   */
  public El setElementAttribute(String attr, boolean value) {
    DOM.setElementPropertyBoolean(dom, attr, value);
    return this;
  }

  /**
   * Sets an element's attribute.
   * 
   * @param attr the attribute name
   * @param value the value
   * @return this
   */
  public El setElementAttribute(String attr, int value) {
    return setElementAttribute(attr, "" + value);
  }

  /**
   * Sets an element's attribute.
   * 
   * @param attr the attribute name
   * @param value the value
   * @return this
   */
  public El setElementAttribute(String attr, String value) {
    DOM.setElementAttribute(dom, attr, value);
    return this;
  }

  /**
   * Convenience function for setting disabled/enabled by boolean.
   * 
   * @param enabled the enabled state
   */
  public void setEnabled(boolean enabled) {
    if (!enabled) {
      disable();
    } else {
      enable();
    }
  }

  /**
   * True to focus, false to blur.
   * 
   * @param focus the new focus state
   */
  public native El setFocus(boolean focus) /*-{
		var dom = this.@com.extjs.gxt.ui.client.core.El::dom;
		try {
			if (focus) {
				dom.focus();
			} else {
				dom.blur();
			}
		} catch (err) {
		}
		return this;
  }-*/;

  /**
   * Sets the elements height.
   * 
   * @param height the height
   * @return this
   */
  public El setHeight(int height) {
    return setHeight(height, false);
  }

  /**
   * Sets the elements height.
   * 
   * @param height the height
   * @param adjust <code>true</code> to adjust for box model issues
   * @return this
   */
  public El setHeight(int height, boolean adjust) {
    if (adjust && !isBorderBox()) {
      height -= getFrameWidth("tb");
    }
    if (height >= 0) {
      dom.getStyle().setPropertyPx("height", height);
    }
    return this;
  }

  /**
   * Sets the elements height.
   * 
   * @param height the height
   * @return this
   */
  public El setHeight(String height) {
    DOM.setStyleAttribute(dom, "height", addUnits(height, "px"));
    return this;
  }

  /**
   * Sets the the icon for an element either as a CSS style name or image path.
   * 
   * @param style the style are image path
   */
  public El setIconStyle(String style) {
    if (Util.isImagePath(style)) {
      setStyleAttribute("backgroundImage", "url(" + style + ")");
    } else {
      dom.setClassName(style);
    }
    return this;
  }

  /**
   * Sets the element's id.
   * 
   * @param id the new id
   * @return this
   */
  public El setId(String id) {
    if (id == null) {
      id = XDOM.getUniqueId();
    }
    dom.setId(id);
    return this;
  }

  /**
   * Sets the element's inner html.
   * 
   * @param html the new HTML
   * @return this
   */
  public El setInnerHtml(String html) {
    DOM.setInnerHTML(dom, html);
    return this;
  }

  /**
   * Sets an element's property.
   * 
   * @param property the property name
   * @param value the value
   * @return this
   */
  public El setIntElementProperty(String property, int value) {
    DOM.setElementPropertyInt(dom, property, value);
    return this;
  }

  /**
   * Sets the element's left position directly using CSS style (instead of
   * {@link #setX}).
   * 
   * @param left the left value
   * @return this
   */
  public El setLeft(int left) {
    dom.getStyle().setPropertyPx("left", left);
    return this;
  }

  /**
   * Quick set left and top adding default units.
   * 
   * @param left the left value
   * @param top the top value
   * @return this
   */
  public El setLeftTop(int left, int top) {
    setLeft(left);
    setTop(top);
    return this;
  }

  /**
   * Sets the elements's margin.
   * 
   * @param margin the margin
   * @return this
   */
  public El setMargins(Margins margin) {
    if (margin != null) {
      setStyleAttribute("marginLeft", margin.left + "px");
      setStyleAttribute("marginTop", margin.top + "px");
      setStyleAttribute("marginRight", margin.right + "px");
      setStyleAttribute("marginBottom", margin.bottom + "px");
    }
    return this;
  }

  /**
   * Sets the elements's padding.
   * 
   * @param padding the padding
   * @return this
   */
  public El setPadding(Padding padding) {
    if (padding != null) {
      setStyleAttribute("paddingLeft", padding.left + "px");
      setStyleAttribute("paddingTop", padding.top + "px");
      setStyleAttribute("paddingRight", padding.right + "px");
      setStyleAttribute("paddingBottom", padding.bottom + "px");
    }
    return this;
  }

  /**
   * Sets the element's position in page coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @return this
   */
  public El setPagePosition(int x, int y) {
    setX(x);
    setY(y);
    return this;
  }

  /**
   * Sets the element's horizontal scroll position.
   * 
   * @param left the left value
   * @return this
   */
  public El setScrollLeft(int left) {
    DOM.setElementPropertyInt(dom, "scrollLeft", left);
    return this;
  }

  /**
   * Sets the element's vertical scroll position.
   * 
   * @param top the top value
   * @return this
   */
  public El setScrollTop(int top) {
    DOM.setElementPropertyInt(dom, "scrollTop", top);
    return this;
  }

  /**
   * Sets the element's size.
   * 
   * @param width the new width
   * @param height the new height
   * @return this
   */
  public El setSize(int width, int height) {
    setSize(width, height, false);
    return this;
  }

  /**
   * Set the size of the element.
   * 
   * @param width the new width
   * @param height the new height
   * @param adjust <code>true</code> to adjust for box model issues
   * @return this
   */
  public El setSize(int width, int height, boolean adjust) {
    if (adjust && !isBorderBox()) {
      Size frameWidth = getFrameSize();
      width -= frameWidth.width;
      height -= frameWidth.height;
    }
    if (width >= 0) {
      dom.getStyle().setPropertyPx("width", width);
    }
    if (height >= 0) {
      dom.getStyle().setPropertyPx("height", height);
    }
    return this;
  }

  /**
   * Sets the element's size.
   * 
   * @param size the size
   * @return this
   */
  public El setSize(Size size) {
    setSize(size.width, size.height);
    return this;
  }

  /**
   * Sets the element's size.
   * 
   * @param width the new size
   * @param height the new height
   * @return this
   */
  public El setSize(String width, String height) {
    setWidth(width);
    setHeight(height);
    return this;
  }

  /**
   * Sets a style attribute.
   * 
   * @param attr the attribute name
   * @param value the int value
   * @return this
   */
  public El setStyleAttribute(String attr, Object value) {
    computedStyle.setStyleAttribute(dom, attr, value);
    return this;
  }

  /**
   * Sets the element's style name.
   * 
   * @param style the style name
   * @return this
   */
  public El setStyleName(String style) {
    dom.setClassName(style);
    return this;
  }

  /**
   * Adds or removes the style name.
   * 
   * @param style the style name
   * @param add true to add, false to remove
   * @return this
   */
  public El setStyleName(String style, boolean add) {
    if (add) {
      addStyleName(style);
    } else {
      removeStyleName(style);
    }
    return this;
  }

  /**
   * Sets the element's size using style attributes.
   * 
   * @param width the width
   * @param height the height
   * @return this
   */
  public El setStyleSize(int width, int height) {
    setStyleAttribute("width", width);
    setStyleAttribute("height", height);
    return this;
  }

  /**
   * Sets the element's tab index.
   * 
   * @param index the tab index
   * @return this
   */
  public El setTabIndex(int index) {
    DOM.setElementPropertyInt(dom, "tabIndex", index);
    return null;
  }

  /**
   * Sets the element's title property.
   * 
   * @param title the new title
   * @return this
   */
  public El setTitle(String title) {
    dom.setTitle(title);
    return this;
  }

  /**
   * Sets the element's top position directly using CSS style (instead of
   * {@link #setY}).
   * 
   * @param top the top value
   * @return this
   */
  public El setTop(int top) {
    dom.getStyle().setPropertyPx("top", top);
    return this;
  }

  /**
   * Sets the element's value property.
   * 
   * @param value the value
   */
  public El setValue(String value) {
    dom.setPropertyString("value", value);
    return this;
  }

  /**
   * Sets the elements css 'visibility' property. Behavior is different than
   * using the 'display' property.
   * 
   * @param visible <code>true</code> to show, <code>false</code> to hide
   * @return this
   */
  public El setVisibility(boolean visible) {
    setStyleAttribute("visibility", visible ? "visible" : "hidden");
    return this;
  }

  /**
   * Sets the element's visibility mode. When setVisible() is called it will use
   * this to determine whether to set the visibility or the display property.
   * 
   * @param visMode value {link VisMode#VISIBILITY}} or {@link VisMode#DISPLAY}
   * @return this
   */
  public El setVisibilityMode(VisMode visMode) {
    visiblityMode = visMode;
    return this;
  }

  /**
   * Sets the visibility of the element (see details). If the vis mode is set to
   * DISPLAY, it will use the display property to hide the element, otherwise it
   * uses visibility. The default is to hide and show using the DISPLAY
   * property.
   * 
   * @param visible whether the element is visible
   * @return this
   */
  public El setVisible(boolean visible) {
    if (visiblityMode == VisMode.DISPLAY) {
      return setDisplayed(visible);
    } else {
      return setVisibility(visible);
    }
  }

  /**
   * Sets the element's width.
   * 
   * @param width the new width
   * @return this
   */
  public El setWidth(int width) {
    return setWidth(width, false);
  }

  /**
   * Sets the elements's width.
   * 
   * @param width the new width
   * @param adjust <code>true</code> to adjust for box model issues
   * @return this
   */
  public El setWidth(int width, boolean adjust) {
    if (adjust && !isBorderBox()) {
      width -= getFrameWidth("lr");
    }
    if (width >= 0) {
      dom.getStyle().setPropertyPx("width", width);
    }
    return this;
  }

  /**
   * Sets the element's width.
   * 
   * @param width the new width
   * @return this
   */
  public El setWidth(String width) {
    DOM.setStyleAttribute(dom, "width", addUnits(width, "px"));
    return this;
  }

  /**
   * Sets the X position of the element based on page coordinates. Element must
   * be part of the DOM tree to have page coordinates.
   * 
   * @param x the x coordinate
   * @return this
   */
  public El setX(int x) {
    return setXY(x, Style.DEFAULT);
  }

  /**
   * Sets the elements position in page coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @return this
   */
  public El setXY(int x, int y) {
    return setXY(new Point(x, y));
  }

  /**
   * Sets the elements position in page coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @return this
   */
  public El setXY(int x, int y, FxConfig config) {
    if (config == null) {
      setXY(x, y);
    } else {
      Fx fx = new Fx(config);
      fx.run(new Move(this, x, y));
    }
    return this;
  }

  /**
   * Sets the element's position in page coordinates.
   * 
   * @param p the position
   * @return this
   */
  public El setXY(Point p) {
    makePositionable();
    Point pts = translatePoints(p);
    if (p.x != Style.DEFAULT) {
      setLeft(pts.x);
    }
    if (p.y != Style.DEFAULT) {
      setTop(pts.y);
    }
    return this;
  }

  /**
   * Sets the Y position of the element based on page coordinates. Element must
   * be part of the DOM tree to have page coordinates.
   * 
   * @param y the y coordinate
   * @return this
   */
  public El setY(int y) {
    return setXY(Style.DEFAULT, y);
  }

  /**
   * Sets the element's z-index.
   * 
   * @param zIndex the z-index value
   * @return this
   */
  public El setZIndex(int zIndex) {
    DOM.setIntStyleAttribute(dom, "zIndex", Math.max(0, zIndex));
    return this;
  }

  /**
   * Shows this element
   * 
   * @return this
   */
  public El show() {
    return setVisible(true);
  }

  /**
   * Slides the element in.
   * 
   * @param direction the direction
   * @param config the fx config
   * @return this
   */
  public El slideIn(Direction direction, FxConfig config) {
    BaseEffect.slideIn(this, config, direction);
    return this;
  }

  /**
   * Slides the element out.
   * 
   * @param direction the direction
   * @param config the fx config
   * @return this
   */
  public El slideOut(Direction direction, FxConfig config) {
    BaseEffect.slideOut(this, config, direction);
    return this;
  }

  /**
   * Returns the element's sub child.
   * 
   * @param depth the child node depth
   * @return the child element
   */
  public El subChild(int depth) {
    Element child = dom;
    while (depth-- > 0) {
      child = DOM.getChild(child, 0);
    }
    return new El(child);
  }

  /**
   * Synchronizes the layer.
   * 
   * @param show true to show
   */
  public El sync(boolean show) {
    return this;
  }

  public String toString() {
    return getOuterHtml();
  }

  public Point translatePoints(Point p) {
    List<String> list = new ArrayList<String>(3);
    list.add("position");
    list.add("left");
    list.add("top");

    Map<String, String> map = getStyleAttribute(list);
    boolean relative = "relative".equals(map.get("position"));
    int l = Util.parseInt(map.get("left"), -11234);
    int t = Util.parseInt(map.get("top"), -11234);

    l = l != -11234 ? l : (relative ? 0 : dom.getOffsetLeft());
    t = t != -11234 ? t : (relative ? 0 : dom.getOffsetTop());

    Point o = getXY();
    return new Point(p.x - o.x + l, p.y - o.y + t);
  }

  /**
   * Return clipping (overflow) to original clipping before clip() was called.
   * 
   * @return this
   */
  public El unclip() {
    if (isClipped) {
      isClipped = false;
      setStyleAttribute("overflow", originalClipped[0]);
      setStyleAttribute("overflowX", originalClipped[1]);
      setStyleAttribute("overflowY", originalClipped[2]);
    }
    return this;
  }

  /**
   * Removes a previously applied mask.
   * 
   * return this
   */
  public El unmask() {
    if (_mask != null) {
      if (_maskMsg != null) {
        _maskMsg.remove();
        _maskMsg = null;
      }
      _mask.setVisible(false);
      _mask.remove();
      _mask = null;
      removeStyleName("x-masked", "x-masked-relative");
    }
    return this;
  }

  /**
   * Unwraps the child element.
   * 
   * @param bounds the original bounds
   */
  public El unwrap(Element child, Rectangle bounds) {
    El.fly(child, "_internal").setLeftTop(bounds.x, bounds.y);
    Element p = dom.getParentElement().cast();
    int pos = DOM.getChildIndex(p, dom);
    p.removeChild(dom);
    DOM.insertChild(p, child, pos);
    return this;
  }

  /**
   * Sets the innerHTML to the given markup.
   * 
   * @param html the html
   * @return this
   */
  public El update(String html) {
    DOM.setInnerHTML(dom, html);
    return this;
  }

  /**
   * Sets the element's z-index using {@link XDOM#getTopZIndex()} to ensure it
   * has the highest values.
   * 
   * @param adj the adjustment to be applied to the z-index value
   * 
   * @return this
   */
  public El updateZIndex(int adj) {
    setZIndex(XDOM.getTopZIndex() + adj);
    return this;
  }

  /**
   * Wraps the element with the specified wrapper. The wrapper will have the
   * same size and position of the element. The original bounds can be used to
   * 'unwrap' the element.
   * 
   * @param wrapper the wrapper element
   * @return the original bounds
   */
  public Rectangle wrap(Element wrapper) {
    El wrap = new El(wrapper);
    wrap.setVisible(false);

    String pos = getStyleAttribute("position");
    wrap.setStyleAttribute("position", pos);

    int l = getLeft();
    int t = getTop();

    setLeft(5000);
    setVisible(true);

    int h = getComputedHeight();
    int w = getComputedWidth();

    setLeft(1);
    setStyleAttribute("overflow", "hidden");
    setVisible(false);

    wrap.insertBefore(dom);
    wrap.appendChild(dom);

    wrap.setStyleAttribute("overflow", "hidden");

    wrap.setLeft(l);
    wrap.setTop(t);

    setTop(0);
    setLeft(0);

    return new Rectangle(l, t, w, h);
  }

  protected Point getConstrainToXY(Element elem, Point proposedXY) {
    int vw, vh, vx = 0, vy = 0;
    if (elem == XDOM.getBody()) {
      vw = XDOM.getViewportSize().width;
      vh = XDOM.getViewportSize().height;

      if (!GXT.isIE && !fly(elem.getParentElement(), "_internal").isStyleAttribute("overflowY", "hidden")
          && fly(elem.getParentElement(), "_internal").isScrollableY()) {
        vw -= (XDOM.getScrollBarWidth());
      }
      if (!GXT.isIE && !fly(elem.getParentElement(), "_internal").isStyleAttribute("overflowX", "hidden")
          && fly(elem.getParentElement(), "_internal").isScrollableX()) {
        vh -= (XDOM.getScrollBarWidth());
      }

    } else {
      vw = fly(elem, "_internal").getWidth();
      vh = fly(elem, "_internal").getHeight();
    }

    Point xy = proposedXY;
    int x = xy.x;
    int y = xy.y;

    int vr = vx + vw;
    int vb = vy + vh;

    int w = getWidth();
    int h = getHeight();

    if ((x + w) > vr) {
      x = vr - w;
    }
    if ((y + h) > vb) {
      y = vb - h;
    }

    // then make sure top/left isn't negative
    if (x < vx) {
      x = vx;
    }
    if (y < vy) {
      y = vy;
    }

    return new Point(x, y);
  }

  private native boolean isLeftorRight(String s) /*-{
		if (@com.extjs.gxt.ui.client.core.El::leftRightTest == null) {
			@com.extjs.gxt.ui.client.core.El::leftRightTest = new RegExp(
					"Left|Right");
		}
		return @com.extjs.gxt.ui.client.core.El::leftRightTest.test(s);
  }-*/;
  /* begin laaglu */
  public static native String getClassName(Element dom) /*-{
     var cn = dom.className;
	 //@com.google.gwt.core.client.GWT::log(Ljava/lang/String;)(typeof(cn));
     if ("object" == typeof(cn)) {
       return cn.baseVal;
     }
	 return cn;
  }-*/;
  

  private static native void setClassName(Element dom, String value) /*-{
     var cn = dom.className;
     if ("object" == typeof(cn)) {
       dom.className.baseVal = value;
       return;
     }
     dom.className = value;
  }-*/;


  /* end laaglu */
}
