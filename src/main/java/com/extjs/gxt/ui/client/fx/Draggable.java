/*
 * Ext GWT 2.2.5 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.fx;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Shim;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

/**
 * Adds drag behavior to any widget. Drag operations can be initiated from the
 * widget itself, or another widget, such as the header in a dialog.
 * 
 * <p/>
 * It is possible to specify event targets that will be ignored. If the target
 * element has a 'x-nodrag' style it will not trigger a drag operation.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>DragStart</b> : DragEvent(draggable, component, event) <br>
 * <div>Fires after a drag has started.</div>
 * <ul>
 * <li>draggable : this</li>
 * <li>component : drag component</li>
 * <li>event : the dom event</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>DragMove</b> : DragEvent(draggable, component, event)<br>
 * <div>Fires after the mouse moves.</div>
 * <li>draggable : this</li>
 * <li>component : drag component</li>
 * <li>event : the dom event</li>
 * </ul></dd>
 * 
 * <dd><b>DragCancel</b> : DragEvent(draggable, component, event)<br>
 * <div>Fires after a drag has been cancelled.</div>
 * <ul>
 * <li>draggable : this</li>
 * <li>component : drag component</li>
 * <li>event : the dom event</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>DragEnd</b> : DragEvent(draggable, component, event) <br>
 * <div>Fires after a drag has ended.</div>
 * <ul>
 * <li>draggable : this</li>
 * <li>component : drag widget</li>
 * <li>event : the dom event</li>
 * </ul>
 * </dd>
 * </dl>
 */
@SuppressWarnings("deprecation")
public class Draggable extends BaseObservable {

  protected int conX, conY, conWidth, conHeight;
  protected int dragStartX, dragStartY;
  protected int lastX, lastY;
  protected El proxyEl;
  protected Rectangle startBounds;

  private int clientWidth, clientHeight;
  private boolean constrainClient = true;
  private boolean constrainHorizontal;
  private boolean constrainVertical;
  private Component container;
  private DragEvent dragEvent;
  private boolean dragging;
  private Component dragWidget;
  private boolean enabled = true;
  private Component handle;
  private Listener<ComponentEvent> listener;
  private boolean moveAfterProxyDrag = true;
  private BaseEventPreview preview;
  private String proxyStyle = "x-drag-proxy";
  private boolean sizeProxyToSource = true;
  private int startDragDistance = 2;
  private Element startElement;
  // config
  private boolean updateZIndex = true;
  private boolean useProxy = true;
  private int xLeft = Style.DEFAULT, xRight = Style.DEFAULT;
  private int xTop = Style.DEFAULT, xBottom = Style.DEFAULT;

  /**
   * Creates a new draggable instance.
   * 
   * @param dragComponent the component to be dragged
   */
  public Draggable(Component dragComponent) {
    this(dragComponent, dragComponent);
  }

  /**
   * Create a new draggable instance.
   * 
   * @param dragComponent the component to be dragged
   * @param handle the component drags will be initiated from
   */
  public Draggable(final Component dragComponent, final Component handle) {
    listener = new Listener<ComponentEvent>() {
      public void handleEvent(ComponentEvent ce) {
        onMouseDown(ce);
      }
    };
    this.dragWidget = dragComponent;
    this.handle = handle;

    handle.addListener(Events.OnMouseDown, listener);

    preview = new BaseEventPreview() {

      @Override
      public boolean onPreview(PreviewEvent event) {
        event.preventDefault();
        switch (event.getEventTypeInt()) {
          case Event.ONKEYDOWN:
            if (dragging && event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
              cancelDrag();
            }
            break;
          case Event.ONMOUSEMOVE:
            onMouseMove(event.getEvent());
            break;
          case Event.ONMOUSEUP:
            stopDrag(event.getEvent());
            break;
        }
        return true;
      }

    };
    preview.setAutoHide(false);

    handle.sinkEvents(Event.ONMOUSEDOWN);
  }

  /**
   * Adds a listener to receive drag events.
   * 
   * @param listener the drag listener to be added
   */
  public void addDragListener(DragListener listener) {
    addListener(Events.DragStart, listener);
    addListener(Events.DragMove, listener);
    addListener(Events.DragCancel, listener);
    addListener(Events.DragEnd, listener);
  }

  /**
   * Cancels the drag if running.
   */
  public void cancelDrag() {
    preview.remove();
    if (dragging) {
      dragging = false;
      if (isUseProxy()) {
        proxyEl.disableTextSelection(false);
        proxyEl.setVisibility(false);
        proxyEl.remove();
      } else {
        dragWidget.el().setPagePosition(startBounds.x, startBounds.y);
      }
      DragEvent de = new DragEvent(this);
      de.setStartElement(startElement);
      fireEvent(Events.DragCancel, de);
      afterDrag();
    }
    startElement = null;
  }

  /**
   * Returns the drag container.
   * 
   * @return the drag container
   */
  public Component getContainer() {
    return container;
  }

  /**
   * Returns the drag handle.
   * 
   * @return the drag handle
   */
  public Component getDragHandle() {
    return handle;
  }

  /**
   * Returns the widget being dragged.
   * 
   * @return the drag widget
   */
  public Component getDragWidget() {
    return dragWidget;
  }

  /**
   * Returns the proxy style.
   * 
   * @return the proxy style
   */
  public String getProxyStyle() {
    return proxyStyle;
  }

  /**
   * Returns the number of pixels the cursor must move before dragging begins.
   * 
   * @return the distance in pixels
   */
  public int getStartDragDistance() {
    return startDragDistance;
  }

  /**
   * Returns true if drag is constrained to the viewport.
   * 
   * @return the constrain client state
   */
  public boolean isConstrainClient() {
    return constrainClient;
  }

  /**
   * Returns true if horizontal movement is constrained.
   * 
   * @return the horizontal constrain state
   */
  public boolean isConstrainHorizontal() {
    return constrainHorizontal;
  }

  /**
   * Returns true if vertical movement is constrained.
   * 
   * @return true if vertical movement is constrained
   */
  public boolean isConstrainVertical() {
    return constrainVertical;
  }

  /**
   * Returns <code>true</code> if a drag is in progress.
   * 
   * @return the drag state
   */
  public boolean isDragging() {
    return dragging;
  }

  /**
   * Returns <code>true</code> if enabled.
   * 
   * @return the enable state
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns true if the drag widget is moved after a proxy drag.
   * 
   * @return the move after proxy state
   */
  public boolean isMoveAfterProxyDrag() {
    return moveAfterProxyDrag;
  }

  /**
   * Returns true if the proxy element is sized to match the drag widget.
   * 
   * @return the size proxy to source state
   */
  public boolean isSizeProxyToSource() {
    return sizeProxyToSource;
  }

  /**
   * Returns true if the z-index is updated after a drag.
   * 
   * @return the update z-index state
   */
  public boolean isUpdateZIndex() {
    return updateZIndex;
  }

  /**
   * Returns true if proxy element is enabled.
   * 
   * @return the use proxy state
   */
  public boolean isUseProxy() {
    return useProxy;
  }

  /**
   * Removes the drag handles.
   */
  public void release() {
    cancelDrag();
    handle.removeListener(Events.OnMouseDown, listener);
  }

  /**
   * Removes a previously added listener.
   * 
   * @param listener the listener to be removed
   */
  public void removeDragListener(DragListener listener) {
    if (hasListeners()) {
      removeListener(Events.DragStart, listener);
      removeListener(Events.DragMove, listener);
      removeListener(Events.DragCancel, listener);
      removeListener(Events.DragEnd, listener);
    }
  }

  /**
   * True to set constrain movement to the viewport (defaults to true).
   * 
   * @param constrainClient true to constrain to viewport
   */
  public void setConstrainClient(boolean constrainClient) {
    this.constrainClient = constrainClient;
  }

  /**
   * True to stop horizontal movement (defaults to false).
   * 
   * @param constrainHorizontal true to stop horizontal movement
   */
  public void setConstrainHorizontal(boolean constrainHorizontal) {
    this.constrainHorizontal = constrainHorizontal;
  }

  /**
   * True to stop vertical movement (defaults to false).
   * 
   * @param constrainVertical true to stop vertical movement
   */
  public void setConstrainVertical(boolean constrainVertical) {
    this.constrainVertical = constrainVertical;
  }

  /**
   * Specifies a container to which the drag widget is constrained.
   * 
   * @param container the container
   */
  public void setContainer(Component container) {
    this.container = container;
  }

  /**
   * Enables dragging if the argument is <code>true</code>, and disables it
   * otherwise.
   * 
   * @param enabled the new enabled state
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * True to move source widget after a proxy drag (defaults to true).
   * 
   * @param moveAfterProxyDrag true to move after a proxy drag
   */
  public void setMoveAfterProxyDrag(boolean moveAfterProxyDrag) {
    this.moveAfterProxyDrag = moveAfterProxyDrag;
  }

  /**
   * Sets the proxy element.
   * 
   * @param element the proxy element
   */
  public void setProxy(El element) {
    proxyEl = element;
  }

  /**
   * Sets the style name used for proxy drags (defaults to 'my-drag-proxy').
   * 
   * @param proxyStyle the proxy style
   */
  public void setProxyStyle(String proxyStyle) {
    this.proxyStyle = proxyStyle;
  }

  /**
   * True to set proxy dimensions the same as the drag widget (defaults to
   * true).
   * 
   * @param sizeProxyToSource true to update proxy size
   */
  public void setSizeProxyToSource(boolean sizeProxyToSource) {
    this.sizeProxyToSource = sizeProxyToSource;
  }

  /**
   * Specifies how far the cursor must move after mousedown to start dragging
   * (defaults to 2).
   * 
   * @param startDragDistance the start distance in pixels
   */
  public void setStartDragDistance(int startDragDistance) {
    this.startDragDistance = startDragDistance;
  }

  /**
   * True if the CSS z-index should be updated on the widget being dragged.
   * Setting this value to <code>true</code> will ensure that the dragged
   * element is always displayed over all other widgets (defaults to true).
   * 
   * @param updateZIndex true update the z-index
   */
  public void setUpdateZIndex(boolean updateZIndex) {
    this.updateZIndex = updateZIndex;
  }

  /**
   * True to use a proxy widget during drag operation (defaults to true).
   * 
   * @param useProxy true use a proxy
   */
  public void setUseProxy(boolean useProxy) {
    this.useProxy = useProxy;
  }

  /**
   * Constrains the horizontal travel.
   * 
   * @param left the number of pixels the element can move to the left
   * @param right the number of pixels the element can move to the right
   */
  public void setXConstraint(int left, int right) {
    xLeft = left;
    xRight = right;
  }

  /**
   * Constrains the vertical travel.
   * 
   * @param top the number of pixels the element can move to the up
   * @param bottom the number of pixels the element can move to the down
   */
  public void setYConstraint(int top, int bottom) {
    xTop = top;
    xBottom = bottom;
  }

  protected void afterDrag() {
    XDOM.getBodyEl().removeStyleName("x-unselectable");
    XDOM.getBodyEl().removeStyleName("x-dd-cursor");
    Shim.get().uncover();
  }

  protected El createProxy() {
    proxyEl = new El(DOM.createDiv());
    proxyEl.setVisibility(false);
    proxyEl.dom.setClassName(getProxyStyle());
    proxyEl.disableTextSelection(true);
    return proxyEl;
  }

  protected void onMouseDown(ComponentEvent ce) {
    if (!enabled || ce.getEvent().getButton() != Event.BUTTON_LEFT) {
      return;
    }
    Element target = ce.getTarget();
    String s = DOM.getElementProperty(target, "className");
    if (s != null && s.indexOf("x-nodrag") != -1) {
      return;
    }

    // still allow text selection, prevent drag of other elements
    if ((!"input".equalsIgnoreCase(ce.getTarget().getTagName()) && !"textarea".equalsIgnoreCase(ce.getTarget().getTagName()))
        || ce.getTarget().getPropertyBoolean("disabled")) {
      ce.preventDefault();
    }

    startBounds = dragWidget.el().getBounds();

    startElement = ce.getTarget();

    dragStartX = ce.getClientX();
    dragStartY = ce.getClientY();

    preview.add();

    clientWidth = Window.getClientWidth() + XDOM.getBodyScrollLeft();
    clientHeight = Window.getClientHeight() + XDOM.getBodyScrollTop();

    if (container != null) {
      conX = container.getAbsoluteLeft();
      conY = container.getAbsoluteTop();
      conWidth = container.getOffsetWidth();
      conHeight = container.getOffsetHeight();
    }

    if (startDragDistance == 0) {
      startDrag(ce.getEvent());
    }

  }

  protected void onMouseMove(Event event) {
    Element elem = event.getEventTarget().cast();
    // elem.getClassName throwing GWT exception when dragged component is over
    // SVG / VML
    if (hasAttribute(elem, "class")) {
        /* begin laaglu */
//      String cls = ((Element) event.getEventTarget().cast()).getClassName();
        String cls = El.getClassName(((Element) event.getEventTarget().cast()));
      /* end laaglu */
      if (cls != null && cls.contains("x-insert")) {
        return;
      }
    }

    int x = DOM.eventGetClientX(event);
    int y = DOM.eventGetClientY(event);

    if (!dragging && (Math.abs(dragStartX - x) > startDragDistance || Math.abs(dragStartY - y) > startDragDistance)) {
      startDrag(event);
    }

    if (dragging) {
      int left = constrainHorizontal ? startBounds.x : startBounds.x + (x - dragStartX);
      int top = constrainVertical ? startBounds.y : startBounds.y + (y - dragStartY);

      if (constrainClient) {
        if (!constrainHorizontal) {
          int width = startBounds.width;
          left = Math.max(left, 0);
          left = Math.max(0, Math.min(clientWidth - width, left));
        }
        if (!constrainVertical) {
          top = Math.max(top, 0);
          int height = startBounds.height;
          if (Math.min(clientHeight - height, top) > 0) {
            top = Math.max(2, Math.min(clientHeight - height, top));
          }
        }
      }

      if (container != null) {
        int width = startBounds.width;
        int height = startBounds.height;
        if (!constrainHorizontal) {
          left = Math.max(left, conX);
          left = Math.min(conX + conWidth - width, left);
        }
        if (!constrainVertical) {
          top = Math.min(conY + conHeight - height, top);
          top = Math.max(top, conY);
        }
      }
      if (!constrainHorizontal) {
        if (xLeft != Style.DEFAULT) {
          left = Math.max(startBounds.x - xLeft, left);
        }
        if (xRight != Style.DEFAULT) {
          left = Math.min(startBounds.x + xRight, left);
        }
      }

      if (!constrainVertical) {
        if (xTop != Style.DEFAULT) {
          top = Math.max(startBounds.y - xTop, top);
        }
        if (xBottom != Style.DEFAULT) {
          top = Math.min(startBounds.y + xBottom, top);
        }
      }

      lastX = left;
      lastY = top;

      dragEvent.setSource(this);
      dragEvent.setStartElement(startElement);
      dragEvent.setComponent(dragWidget);
      dragEvent.setEvent(event);
      dragEvent.setCancelled(false);
      dragEvent.setX(lastX);
      dragEvent.setY(lastY);
      fireEvent(Events.DragMove, dragEvent);

      if (dragEvent.isCancelled()) {
        cancelDrag();
        return;
      }

      int tl = dragEvent.getX() != lastX ? dragEvent.getX() : lastX;
      int tt = dragEvent.getY() != lastY ? dragEvent.getY() : lastY;
      if (useProxy) {
        proxyEl.setPagePosition(tl, tt);
      } else {
        dragWidget.el().setPagePosition(tl, tt);
      }
    }

  }

  protected void startDrag(Event event) {
    DragEvent de = new DragEvent(this);
    de.setComponent(dragWidget);
    de.setEvent(event);
    de.setX(startBounds.x);
    de.setY(startBounds.y);
    de.setStartElement(startElement);

    if (fireEvent(Events.DragStart, de)) {
      dragging = true;
      XDOM.getBodyEl().addStyleName("x-unselectable");
      XDOM.getBodyEl().addStyleName("x-dd-cursor");
      dragWidget.el().makePositionable();

      event.preventDefault();
      Shim.get().cover(true);

      lastX = startBounds.x;
      lastY = startBounds.y;

      if (dragEvent == null) {
        dragEvent = new DragEvent(this);
      }

      if (useProxy) {
        if (proxyEl == null) {
          createProxy();
        }
        if (container == null) {
          XDOM.getBody().appendChild(proxyEl.dom);
        } else {
          container.el().appendChild(proxyEl.dom);
        }
        proxyEl.setVisibility(true);
        proxyEl.setZIndex(XDOM.getTopZIndex());
        proxyEl.makePositionable(true);

        if (sizeProxyToSource) {
          proxyEl.setBounds(startBounds);
        } else {
          proxyEl.setXY(startBounds.x, startBounds.y);
        }

        // did listeners change size?
        if (de.getHeight() > 0 && de.getWidth() > 0) {
          proxyEl.setSize(de.getWidth(), de.getHeight(), true);
        } else if (de.getHeight() > 0) {
          proxyEl.setHeight(de.getHeight(), true);
        } else if (de.getWidth() > 0) {
          proxyEl.setWidth(de.getWidth(), true);
        }
      } else if (updateZIndex) {
        dragWidget.setZIndex(XDOM.getTopZIndex());
      }
    } else {
      cancelDrag();
    }
  }

  protected void stopDrag(Event event) {
    preview.remove();
    if (dragging) {
      dragging = false;
      if (isUseProxy()) {
        if (isMoveAfterProxyDrag()) {
          Rectangle rect = proxyEl.getBounds();
          dragWidget.el().setPagePosition(rect.x, rect.y);
        }
        proxyEl.setVisibility(false);
        proxyEl.disableTextSelection(false);
        DeferredCommand.addCommand(new Command() {
          public void execute() {
            if (proxyEl != null) {
              proxyEl.remove();
            }
          }
        });
      }
      DragEvent de = new DragEvent(this);
      de.setStartElement(startElement);
      de.setComponent(dragWidget);
      de.setEvent(event);
      de.setX(lastX);
      de.setY(lastY);
      fireEvent(Events.DragEnd, de);
      afterDrag();
    }
    startElement = null;
  }

  private native boolean hasAttribute(Element elem, String name) /*-{
                                                                 return elem.hasAttribute ? elem.hasAttribute(name) : true;
                                                                 }-*/;

}
