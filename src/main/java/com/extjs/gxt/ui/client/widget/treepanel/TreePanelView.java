/*
 * Ext GWT 2.2.5 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treepanel;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Accessibility;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TreePanelView<M extends ModelData> {

  public enum TreeViewRenderMode {
    ALL, BODY, CONTAINER, MAIN
  };

  protected TreeNode over;
  protected TreePanel<M> tree;
  protected TreeStore<M> treeStore;

  private int cacheSize = 20;
  private int cleanDelay = 500;
  private int scrollDelay = 0;

  public void bind(Component component, Store store) {
    this.tree = (TreePanel) component;
    this.treeStore = (TreeStore) store;
  }

  public void collapse(TreeNode node) {
    getContainer(node).getStyle().setProperty("display", "none");
    tree.refresh((M) node.m);
    if (GXT.isFocusManagerEnabled()) {
      Accessibility.setState((Element) node.getElement().getFirstChildElement(), "aria-expanded", "false");
      FocusFrame.get().sync(tree);
    }
  }

  public void expand(TreeNode node) {
    getContainer(node).getStyle().setProperty("display", "block");
    tree.refresh((M) node.m);
    if (GXT.isFocusManagerEnabled()) {
      FocusFrame.get().sync(tree);
      Accessibility.setState((Element) node.getElement().getFirstChildElement(), "aria-expanded", "true");
    }
  }

  public int getCacheSize() {
    return cacheSize;
  }

  public Element getCheckElement(TreeNode node) {
    if (node.check == null) {
      node.check = getElementContainer(node) != null
          ? ((NodeList<Element>) getElementContainer(node).getChildNodes().cast()).getItem(2) : null;
    }
    return node.check;
  }

  public int getCleanDelay() {
    return cleanDelay;
  }

  public Element getContainer(TreeNode node) {
    if (node.container == null) {
      String s = getTemplate(node.m, null, null, null, false, false, null, 0, TreeViewRenderMode.CONTAINER);
      node.container = node.getElement().appendChild(XDOM.create(s));
    }
    return node.container;
  }

  public Element getElementContainer(TreeNode node) {
    if (node.elContainer == null) {
      node.elContainer = node.getElement() != null ? (Element) node.getElement().getFirstChild() : null;
    }
    return node.elContainer;
  }

  public Element getIconElement(TreeNode node) {
    if (node.icon == null) {
      node.icon = getElementContainer(node) != null
          ? ((NodeList<Element>) getElementContainer(node).getChildNodes().cast()).getItem(3) : null;
    }
    return node.icon;
  }

  public Element getJointElement(TreeNode node) {
    if (node.joint == null) {
      node.joint = ((NodeList<Element>) getElementContainer(node).getChildNodes().cast()).getItem(1);
    }
    return node.joint;
  }

  public int getScrollDelay() {
    return scrollDelay;
  }

  public String getTemplate(ModelData m, String id, String text, AbstractImagePrototype icon, boolean checkable,
      boolean checked, Joint joint, int level, TreeViewRenderMode renderMode) {
    if (renderMode == TreeViewRenderMode.CONTAINER) {
      return "<div unselectable=on class=\"x-tree3-node-ct\" role=\"group\"></div>";
    }
    StringBuilder sb = new StringBuilder();
    if (renderMode == TreeViewRenderMode.ALL || renderMode == TreeViewRenderMode.MAIN) {
      sb.append("<div unselectable=on id=\"");
      sb.append(id);
      sb.append("\"");

      sb.append(" class=\"x-tree3-node\"  role=\"presentation\">");

      String cls = "x-tree3-el";
      if (GXT.isHighContrastMode) {
        switch (joint) {
          case COLLAPSED:
            cls += " x-tree3-node-joint-collapse";
            break;
          case EXPANDED:
            cls += " x-tree3-node-joint-expand";
            break;
        }
      }

      sb.append("<div unselectable=on class=\"" + cls + "\" id=\"" + tree.getId() + "__" + id + "\" role=\"treeitem\" ");
      sb.append(" aria-level=\"" + (level + 1) + "\">");
    }
    if (renderMode == TreeViewRenderMode.ALL || renderMode == TreeViewRenderMode.BODY) {
      Element jointElement = null;
      switch (joint) {
        case COLLAPSED:
          jointElement = (Element) tree.getStyle().getJointCollapsedIcon().createElement().cast();
          break;
        case EXPANDED:
          jointElement = (Element) tree.getStyle().getJointExpandedIcon().createElement().cast();
          break;
      }

      if (jointElement != null) {
        El.fly(jointElement).addStyleName("x-tree3-node-joint");
      }

      sb.append("<img src=\"");
      sb.append(GXT.BLANK_IMAGE_URL);
      sb.append("\" style=\"height: 18px; width: ");
      sb.append(level * getIndenting(findNode((M) m)));
      sb.append("px;\" />");
      sb.append(jointElement == null ? "<img src=\"" + GXT.BLANK_IMAGE_URL
          + "\" style=\"width: 16px\" class=\"x-tree3-node-joint\" />" : DOM.toString(jointElement));
      if (checkable) {
        Element e = (Element) (checked ? GXT.IMAGES.checked().createElement().cast()
            : GXT.IMAGES.unchecked().createElement().cast());
        El.fly(e).addStyleName("x-tree3-node-check");
        sb.append(DOM.toString(e));
      } else {
        sb.append("<span class=\"x-tree3-node-check\"></span>");
      }
      if (icon != null) {
        Element e = icon.createElement().cast();
        El.fly(e).addStyleName("x-tree3-node-icon");
        sb.append(DOM.toString(e));
      } else {
        sb.append("<span class=\"x-tree3-node-icon\"></span>");
      }
      sb.append("<span  unselectable=on class=\"x-tree3-node-text\">");
      sb.append(text);
      sb.append("</span>");
    }

    if (renderMode == TreeViewRenderMode.ALL || renderMode == TreeViewRenderMode.MAIN) {
      sb.append("</div>");
      sb.append("</div>");
    }
    return sb.toString();
  }

  public Element getTextElement(TreeNode node) {
    if (node.text == null) {
      node.text = getElementContainer(node) != null
          ? ((NodeList<Element>) getElementContainer(node).getChildNodes().cast()).getItem(4) : null;
    }
    return node.text;

  }

  public boolean isSelectableTarget(M m, Element target) {
    TreeNode n = findNode(m);
    if (n == null) {
      return false;
    }
    boolean isNotJointTarget = false;
    if (GXT.isIE6) {
      isNotJointTarget = !El.fly(target).getParent().hasStyleName("x-tree3-node-joint");
    } else {
      isNotJointTarget = !El.fly(target).hasStyleName("x-tree3-node-joint");
    }
    if (isNotJointTarget && tree.isCheckable()) {
      boolean isNotCheckTarget = !El.fly(target).hasStyleName("x-tree3-node-check");
      return isNotCheckTarget;
    }
    return isNotJointTarget;
  }
  
  public void onCheckChange(TreeNode node, boolean checkable, boolean check) {
    Element checkEl = (Element) getCheckElement(node);
    if (checkEl != null) {
      Element e;
      if (checkable) {
        if (check) {
          e = (Element) GXT.IMAGES.checked().createElement().cast();
        } else {
          e = (Element) GXT.IMAGES.unchecked().createElement().cast();
        }
      } else {
        e = DOM.createSpan();
      }
      El.fly(e).addStyleName("x-tree3-node-check");
      node.check = (Element) node.getElement().getFirstChild().insertBefore(e, checkEl);
      El.fly(checkEl).remove();
    }
  }

  public void onDropChange(TreeNode node, boolean drop) {
    El.fly(getElementContainer(node)).setStyleName("x-ftree2-node-drop", drop);
  }

  public void onEvent(TreePanelEvent ce) {
    int type = ce.getEventTypeInt();
    switch (type) {
      case Event.ONMOUSEOVER:
        if (tree.isTrackMouseOver()) {
          onMouseOver(ce);
        }
        break;
      case Event.ONMOUSEOUT:
        if (tree.isTrackMouseOver()) {
          onMouseOut(ce);
        }
        break;
    }
  }

  public void onIconStyleChange(TreeNode node, AbstractImagePrototype icon) {
    Element iconEl = getIconElement(node);
    if (iconEl != null) {
      Element e;
      if (icon != null) {
        e = (Element) icon.createElement().cast();
      } else {
        e = DOM.createSpan();
      }
      El.fly(e).addStyleName("x-tree3-node-icon");
      node.icon = (Element) node.getElement().getFirstChild().insertBefore(e, iconEl);
      El.fly(iconEl).remove();
    }
  }

  public void onJointChange(TreeNode node, Joint joint) {
    Element jointEl = getJointElement(node);
    if (jointEl != null) {
      Element e;
      switch (joint) {
        case COLLAPSED:
          e = (Element) tree.getStyle().getJointCollapsedIcon().createElement().cast();
          if (GXT.isHighContrastMode) {
            El.fly(node.elContainer).addStyleName("x-tree3-node-joint-collapse").removeStyleName(
                "x-tree3-node-joint-expand");
          }
          break;
        case EXPANDED:
          e = (Element) tree.getStyle().getJointExpandedIcon().createElement().cast();
          if (GXT.isHighContrastMode) {
            El.fly(node.elContainer).addStyleName("x-tree3-node-joint-expand").removeStyleName(
                "x-tree3-node-joint-collapse");
          }
          break;
        default:
          e = XDOM.create("<img src=\"" + GXT.BLANK_IMAGE_URL + "\" width=\"16px\"/>");
          if (GXT.isHighContrastMode) {
            El.fly(node.elContainer).removeStyleName("x-tree3-node-joint-collapse").removeStyleName(
                "x-tree3-node-joint-expand");
          }
      }

      El.fly(e).addStyleName("x-tree3-node-joint");
      node.joint = (Element) node.getElement().getFirstChild().insertBefore(e, jointEl);
      El.fly(jointEl).remove();
    }
  }

  public void onLoading(TreeNode node) {
    onIconStyleChange(node, IconHelper.createStyle("x-tree3-loading"));
  }

  public void onOverChange(TreeNode node, boolean select) {
    El.fly(getElementContainer(node)).setStyleName("x-ftree2-node-over", select);
  }

  public void onSelectChange(M model, boolean select) {
    if (select) {
      tree.setExpanded(treeStore.getParent(model), true);
    }
    TreeNode node = findNode(model);
    if (node != null) {
      Element e = getElementContainer(node);
      if (e != null) {
        El.fly(e).setStyleName("x-ftree2-selected", select);
        if (select) {
          String tid = tree.getId();
          Accessibility.setState(tree.getElement(), "aria-activedescendant", tid + "__" + node.getElement().getId());
        }
      }
    }
  }

  public void onTextChange(TreeNode node, String text) {
    Element textEl = getTextElement(node);
    if (textEl != null) {
      textEl.setInnerHTML(Util.isEmptyString(text) ? "&#160;" : text);
    }
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public void setCleanDelay(int cleanDelay) {
    this.cleanDelay = cleanDelay;
  }

  public void setScrollDelay(int scrollDelay) {
    this.scrollDelay = scrollDelay;
  }

  protected TreeNode findNode(M m){
    return tree.findNode(m);
  }

  protected int getCalculatedRowHeight() {
    return 21;
  }

  protected int getIndenting(TreeNode node) {
    return 18;
  }

  protected void onMouseOut(TreePanelEvent ce) {
    if (over != null) {
      onOverChange(over, false);
      over = null;
    }
  }

  protected void onMouseOver(TreePanelEvent ce) {
    if (ce.getNode() != null) {
      if (over != ce.getNode()) {
        /* begin laaglu */
        tree.fireEvent(Events.OnMouseOver, ce);
        /* end laaglu */
        onMouseOut(ce);
        over = ce.getNode();
        onOverChange(over, true);
      }
    }
  }

}
