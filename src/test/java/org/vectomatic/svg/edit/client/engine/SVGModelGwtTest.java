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
package org.vectomatic.svg.edit.client.engine;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGCircleElement;
import org.vectomatic.dom.svg.OMSVGDescElement;
import org.vectomatic.dom.svg.OMSVGEllipseElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPolygonElement;
import org.vectomatic.dom.svg.OMSVGPolylineElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTitleElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.EditTestCase;
import org.vectomatic.svg.edit.client.model.svg.SVGCircleElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGEllipseElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPathElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolygonElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGPolylineElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGRectElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGViewBoxElementModel;

import com.extjs.gxt.ui.client.store.TreeStore;

public class SVGModelGwtTest extends EditTestCase {
	public void testNewInstance() {
		String data = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='10 20 640 480'>" +
		"<rect/>" +
		"<ellipse/>" +
		"</svg>";
		String name = "testNewInstance";
		SVGModel owner = SVGModel.newInstance(OMSVGParser.parse(data), name);
		assertNotNull(owner);
		SVGElementModel geometryModel = owner.getRoot();
		SVGViewBoxElementModel viewBox = owner.getViewBox();
		
		assertEquals(3, geometryModel.getChildCount());
		assertEquals(viewBox, geometryModel.getChild(0));
		assertEquals(SVGRectElementModel.class, geometryModel.getChild(1).getClass());
		assertEquals(SVGEllipseElementModel.class, geometryModel.getChild(2).getClass());
		
		SVGElement geometryElement = geometryModel.getElement();
		assertEquals(5, geometryElement.getChildCount());
		OMSVGTitleElement title = OMNode.convert(geometryElement.getChild(0));
		assertEquals(name, title.getElement().getInnerText());
		OMSVGDescElement desc = OMNode.convert(geometryElement.getChild(1));
		assertEquals("", desc.getElement().getInnerText());
		OMSVGRectElement rect = OMNode.convert(geometryElement.getChild(2));
		assertEquals(OMNode.convert(viewBox.getElement()), rect);
		
		assertEquals((Float)10f, viewBox.<Float>get(SVGConstants.SVG_X_ATTRIBUTE));
		assertEquals((Float)20f, viewBox.<Float>get(SVGConstants.SVG_Y_ATTRIBUTE));
		assertEquals((Float)640f, viewBox.<Float>get(SVGConstants.SVG_WIDTH_ATTRIBUTE));
		assertEquals((Float)480f, viewBox.<Float>get(SVGConstants.SVG_HEIGHT_ATTRIBUTE));
	}
	
	public void testCreate() {
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "testCreate");
		
		// Circle
		SVGElement e1 = (SVGElement)new OMSVGCircleElement().getElement().cast();
		SVGElement t1 = (SVGElement)new OMSVGCircleElement().getElement().cast();
		SVGElementModel m1 = owner.create(e1, t1);
		assertTrue(m1 instanceof SVGCircleElementModel);
		assertEquals(e1, m1.getElement());
		assertEquals(t1, m1.getTwin());
		assertEquals(owner, m1.getOwner());

		// Ellipse
		SVGElement e2 = (SVGElement)new OMSVGEllipseElement().getElement().cast();
		SVGElement t2 = (SVGElement)new OMSVGEllipseElement().getElement().cast();
		SVGElementModel m2 = owner.create(e2, t2);
		assertTrue(m2 instanceof SVGEllipseElementModel);
		assertEquals(e2, m2.getElement());
		assertEquals(t2, m2.getTwin());
		assertEquals(owner, m2.getOwner());

		// Circle
		SVGElement e3 = (SVGElement)new OMSVGRectElement().getElement().cast();
		SVGElement t3 = (SVGElement)new OMSVGRectElement().getElement().cast();
		SVGElementModel m3 = owner.create(e3, t3);
		assertTrue(m3 instanceof SVGRectElementModel);
		assertEquals(e3, m3.getElement());
		assertEquals(t3, m3.getTwin());
		assertEquals(owner, m3.getOwner());

		// Circle
		SVGElement e4 = (SVGElement)new OMSVGPolylineElement().getElement().cast();
		SVGElement t4 = (SVGElement)new OMSVGPolylineElement().getElement().cast();
		SVGElementModel m4 = owner.create(e4, t4);
		assertTrue(m4 instanceof SVGPolylineElementModel);
		assertEquals(e4, m4.getElement());
		assertEquals(t4, m4.getTwin());
		assertEquals(owner, m4.getOwner());

		// Circle
		SVGElement e5 = (SVGElement)new OMSVGPolygonElement().getElement().cast();
		SVGElement t5 = (SVGElement)new OMSVGPolygonElement().getElement().cast();
		SVGElementModel m5 = owner.create(e5, t5);
		assertTrue(m5 instanceof SVGPolygonElementModel);
		assertEquals(e5, m5.getElement());
		assertEquals(t5, m5.getTwin());
		assertEquals(owner, m5.getOwner());

		// Circle
		SVGElement e6 = (SVGElement)new OMSVGLineElement().getElement().cast();
		SVGElement t6 = (SVGElement)new OMSVGLineElement().getElement().cast();
		SVGElementModel m6 = owner.create(e6, t6);
		assertTrue(m6 instanceof SVGLineElementModel);
		assertEquals(e6, m6.getElement());
		assertEquals(t6, m6.getTwin());
		assertEquals(owner, m6.getOwner());

		// Circle
		SVGElement e7 = (SVGElement)new OMSVGPathElement().getElement().cast();
		SVGElement t7 = (SVGElement)new OMSVGPathElement().getElement().cast();
		SVGElementModel m7 = owner.create(e7, t7);
		assertTrue(m7 instanceof SVGPathElementModel);
		assertEquals(e7, m7.getElement());
		assertEquals(t7, m7.getTwin());
		assertEquals(owner, m7.getOwner());
	}
	
	public void testInsert() {
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "testCreate");

		SVGElementModel root = owner.getRoot();
		
		assertNotNull(root);
		TreeStore<SVGElementModel> store = owner.getStore();
		assertNotNull(store);
		// Store level
		assertEquals(1, store.getChildCount(root)); // viewBox
		// Model level
		assertEquals(1, root.getChildCount()); // viewBox
		// DOM level
		assertEquals(3, root.getElement().getChildCount()); // title + desc + rect(viewBox)

		/*=========================================
		 root
		  viewBox
		  a
		 =========================================*/
		SVGElementModel a = createElementModel(owner, "a");
		owner.insertBefore(root, a, null);
		// SVG model
		assertTrue(owner.contains(a.getElement()));
		assertTrue(owner.contains(a.getTwin()));
		
		// Store level
		assertNotNull(store.findModel(a));
		assertEquals(root, store.getParent(a));
		assertEquals(2, store.getChildCount(root));
		assertEquals(a, store.getChild(root, 1));
		
		// Model level
		assertEquals(root, a.getParent());
		assertEquals(2, root.getChildCount());
		assertEquals(a, root.getChild(1));
		
		// DOM level
		assertEquals(root.getElement(), a.getElement().getParentNode());
		assertEquals(4, root.getElement().getChildCount());
		assertEquals(a.getElement(), root.getElement().getChild(3));

		assertEquals(root.getTwin(), a.getTwin().getParentNode());
		assertEquals(4, root.getTwin().getChildCount());
		assertEquals(a.getTwin(), root.getTwin().getChild(3));

		/*=========================================
		 root
		  viewBox
		  a
		  b
		 =========================================*/
		SVGElementModel b = createElementModel(owner, "b");
		owner.insertBefore(root, b, null);
		// SVG model
		assertTrue(owner.contains(b.getElement()));
		assertTrue(owner.contains(b.getTwin()));
		
		// Store level
		assertNotNull(store.findModel(b));
		assertEquals(root, store.getParent(b));
		assertEquals(3, store.getChildCount(root));
		assertEquals(b, store.getChild(root, 2));
		
		// Model level
		assertEquals(root, b.getParent());
		assertEquals(3, root.getChildCount());
		assertEquals(b, root.getChild(2));
		
		// DOM level
		assertEquals(root.getElement(), b.getElement().getParentNode());
		assertEquals(5, root.getElement().getChildCount());
		assertEquals(b.getElement(), root.getElement().getChild(4));

		assertEquals(root.getTwin(), b.getTwin().getParentNode());
		assertEquals(5, root.getTwin().getChildCount());
		assertEquals(b.getTwin(), root.getTwin().getChild(4));
		
		/*=========================================
		 root
		  viewBox
		  a
		  c
		  b
		 =========================================*/
		SVGElementModel c = createElementModel(owner, "c");
		owner.insertBefore(root, c, b);
		// SVG model
		assertTrue(owner.contains(c.getElement()));
		assertTrue(owner.contains(c.getTwin()));
		
		// Store level
		assertNotNull(store.findModel(c));
		assertEquals(root, store.getParent(c));
		assertEquals(4, root.getChildCount());
		assertEquals(c, store.getChild(root, 2));
		assertEquals(b, store.getChild(root, 3));
		
		// Model level
		assertEquals(root, c.getParent());
		assertEquals(4, root.getChildCount());
		assertEquals(c, root.getChild(2));
		assertEquals(b, root.getChild(3));
		
		// DOM level
		assertEquals(root.getElement(), c.getElement().getParentNode());
		assertEquals(6, root.getElement().getChildCount());
		assertEquals(c.getElement(), root.getElement().getChild(4));
		assertEquals(b.getElement(), root.getElement().getChild(5));

		assertEquals(root.getTwin(), c.getTwin().getParentNode());
		assertEquals(6, root.getTwin().getChildCount());
		assertEquals(c.getTwin(), root.getTwin().getChild(4));
		assertEquals(b.getTwin(), root.getTwin().getChild(5));
		
		/*=========================================
		 root
		  viewBox
		  a
		  c
		   d
		  b
		 =========================================*/
		SVGElementModel d = createElementModel(owner, "d");
		owner.insertBefore(c, d, null);
		// SVG model
		assertTrue(owner.contains(d.getElement()));
		assertTrue(owner.contains(d.getTwin()));
		
		// Store level
		assertNotNull(store.findModel(d));
		assertEquals(c, store.getParent(d));
		assertEquals(1, c.getChildCount());
		assertEquals(d, store.getChild(c, 0));
		
		// Model level
		assertEquals(c, d.getParent());
		assertEquals(1, c.getChildCount());
		assertEquals(d, c.getChild(0));
		
		// DOM level
		assertEquals(c.getElement(), d.getElement().getParentNode());
		assertEquals(3, c.getElement().getChildCount()); // title + desc
		assertEquals(d.getElement(), c.getElement().getChild(2));

		assertEquals(c.getTwin(), d.getTwin().getParentNode());
		assertEquals(3, c.getTwin().getChildCount()); // title + desc
		assertEquals(d.getTwin(), c.getTwin().getChild(2));
		
		/*=========================================
		 root
		  viewBox
		  a
		  c
		   e
		   d
		  b
		 =========================================*/
		SVGElementModel e = createElementModel(owner, "e");
		owner.insertBefore(c, e, d);
		// SVG model
		assertTrue(owner.contains(e.getElement()));
		assertTrue(owner.contains(e.getTwin()));
		
		// Store level
		assertNotNull(store.findModel(e));
		assertEquals(c, store.getParent(e));
		assertEquals(2, store.getChildCount(c));
		assertEquals(e, store.getChild(c, 0));
		
		// Model level
		assertEquals(c, e.getParent());
		assertEquals(2, c.getChildCount());
		assertEquals(e, c.getChild(0));
		
		// DOM level
		assertEquals(c.getElement(), e.getElement().getParentNode());
		assertEquals(4, c.getElement().getChildCount()); // title + desc
		assertEquals(e.getElement(), c.getElement().getChild(2));

		assertEquals(c.getTwin(), e.getTwin().getParentNode());
		assertEquals(4, c.getTwin().getChildCount()); // title + desc
		assertEquals(e.getTwin(), c.getTwin().getChild(2));
	}
	
	public void testInsertXDoc() {

		/*=========================================
		 root1
		  a1
		   b1
		    c1
		    d1
		   e1
		    f1
		   g1
		 root2
		  a2
		   b2
		   c2
		    d2
		    e2
		 =========================================*/
		OMSVGSVGElement svg1 = new OMSVGSVGElement();
		svg1.setViewBox(0, 0, 600, 400);
		SVGModel owner1 = SVGModel.newInstance(svg1, "testInsertXDoc1");

		SVGElementModel root1 = owner1.getRoot();
		assertNotNull(root1);
		TreeStore<SVGElementModel> store1 = owner1.getStore();
		assertNotNull(store1);
		
		SVGElementModel a1 = createElementModel(owner1, "a1");
		SVGElementModel b1 = createElementModel(owner1, "b1");
		SVGElementModel c1 = createElementModel(owner1, "c1");
		SVGElementModel d1 = createElementModel(owner1, "d1");
		SVGElementModel e1 = createElementModel(owner1, "e1");
		SVGElementModel f1 = createElementModel(owner1, "f1");
		SVGElementModel g1 = createElementModel(owner1, "g1");
//		owner1.insertBefore(root1, a1, null);
//		owner1.insertBefore(a1, b1, null);
//		owner1.insertBefore(a1, e1, null);
//		owner1.insertBefore(a1, g1, null);
//		owner1.insertBefore(b1, c1, null);
//		owner1.insertBefore(b1, d1, null);
//		owner1.insertBefore(e1, f1, null);
		createTree(owner1, new SVGElementModel[][]{{root1, a1}, {a1, b1}, {a1, e1}, {a1, g1}, {b1, c1}, {b1, d1}, {e1, f1}});
		
		OMSVGSVGElement svg2 = new OMSVGSVGElement();
		svg2.setViewBox(0, 0, 600, 400);
		SVGModel owner2 = SVGModel.newInstance(svg2, "testInsertXDoc2");
		SVGElementModel root2 = owner2.getRoot();
		assertNotNull(root2);
		TreeStore<SVGElementModel> store2 = owner2.getStore();
		assertNotNull(store2);
		
		SVGElementModel a2 = createElementModel(owner2, "a2");
		SVGElementModel b2 = createElementModel(owner2, "b2");
		SVGElementModel c2 = createElementModel(owner2, "c2");
		SVGElementModel d2 = createElementModel(owner2, "d2");
		SVGElementModel e2 = createElementModel(owner2, "e2");
		createTree(owner2, new SVGElementModel[][]{{root2, a2}, {a2, b2}, {a2, c2}, {c2, d2}, {c2, e2}});

		/*=========================================
		 root1
		  a1
		   b1
		    c1
		    c2
		     d2
		     e2
		    d1
		   e1
		    f1
		   g1
		 root2
		  a2
		   b2
		 =========================================*/
		owner1.insertBefore(b1, c2, d1);
		
		// SVG model
		assertEquals(owner1, c2.getOwner());
		assertEquals(owner1, d2.getOwner());
		assertEquals(owner1, e2.getOwner());
		assertTrue(owner1.contains(c2.getElement()));
		assertTrue(owner1.contains(c2.getTwin()));
		assertTrue(owner1.contains(d2.getElement()));
		assertTrue(owner1.contains(d2.getTwin()));
		assertTrue(owner1.contains(e2.getElement()));
		assertTrue(owner1.contains(e2.getTwin()));
		assertFalse(owner2.contains(c2.getElement()));
		assertFalse(owner2.contains(c2.getTwin()));
		assertFalse(owner2.contains(d2.getElement()));
		assertFalse(owner2.contains(d2.getTwin()));
		assertFalse(owner2.contains(e2.getElement()));
		assertFalse(owner2.contains(e2.getTwin()));
		
		// Store level
		assertNotNull(store1.findModel(c2));
		assertNotNull(store1.findModel(d2));
		assertNotNull(store1.findModel(e2));
		assertNull(store2.findModel(c2));
		assertNull(store2.findModel(d2));
		assertNull(store2.findModel(e2));
		assertEquals(b1, store1.getParent(c2));
		assertEquals(c2, store1.getParent(d2));
		assertEquals(c2, store1.getParent(e2));
		assertEquals(3, store1.getChildCount(b1));
		assertEquals(c2, store1.getChild(b1, 1));
		assertEquals(2, store1.getChildCount(c2));
		assertEquals(d2, store1.getChild(c2, 0));
		assertEquals(e2, store1.getChild(c2, 1));

		// Model level
		assertEquals(b1, c2.getParent());
		assertEquals(c2, d2.getParent());
		assertEquals(c2, e2.getParent());
		assertEquals(3, b1.getChildCount());
		assertEquals(c2, b1.getChild(1));
		assertEquals(2, c2.getChildCount());
		assertEquals(d2, c2.getChild(0));
		assertEquals(e2, c2.getChild(1));
		
		// DOM level
		assertEquals(b1.getElement(), c2.getElement().getParentNode());
		assertEquals(c2.getElement(), d2.getElement().getParentNode());
		assertEquals(c2.getElement(), e2.getElement().getParentNode());
		assertEquals(5, b1.getElement().getChildCount());
		assertEquals(c2.getElement(), b1.getElement().getChild(3));
		assertEquals(4, c2.getElement().getChildCount());
		assertEquals(d2.getElement(), c2.getElement().getChild(2));
		assertEquals(e2.getElement(), c2.getElement().getChild(3));

		assertEquals(b1.getTwin(), c2.getTwin().getParentNode());
		assertEquals(c2.getTwin(), d2.getTwin().getParentNode());
		assertEquals(c2.getTwin(), e2.getTwin().getParentNode());
		assertEquals(5, b1.getTwin().getChildCount());
		assertEquals(c2.getTwin(), b1.getTwin().getChild(3));
		assertEquals(4, c2.getTwin().getChildCount());
		assertEquals(d2.getTwin(), c2.getTwin().getChild(2));
		assertEquals(e2.getTwin(), c2.getTwin().getChild(3));
		
		try {
			owner2.insertBefore(c1, e1, null);
			fail();
		} catch(Throwable t) {
		}
		try {
			owner2.insertBefore(c2, e1, c1);
			fail();
		} catch(Throwable t) {
		}
	}
	
	public void testRemove() {
		/*=========================================
		 root
		  a
		   b
		    c
		    d
		   e
		    f
		   g
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "testCreate");
		SVGElementModel root = owner.getRoot();
		assertNotNull(root);
		TreeStore<SVGElementModel> store = owner.getStore();
		assertNotNull(store);
		
		SVGElementModel a = createElementModel(owner, "a");
		SVGElementModel b = createElementModel(owner, "b");
		SVGElementModel c = createElementModel(owner, "c");
		SVGElementModel d = createElementModel(owner, "d");
		SVGElementModel e = createElementModel(owner, "e");
		SVGElementModel f = createElementModel(owner, "f");
		SVGElementModel g = createElementModel(owner, "g");
		createTree(owner, new SVGElementModel[][]{{root, a}, {a, b}, {a, e}, {a, g}, {b, c}, {b, d}, {e, f}});

		/*=========================================
		 root
		  a
		   e
		    f
		   g
		 =========================================*/
		owner.remove(b);
		
		// SVG model
		assertNull(b.getOwner());
		assertNull(c.getOwner());
		assertNull(d.getOwner());
		assertFalse(owner.contains(b.getElement()));
		assertFalse(owner.contains(b.getTwin()));
		assertFalse(owner.contains(c.getElement()));
		assertFalse(owner.contains(c.getTwin()));
		assertFalse(owner.contains(d.getElement()));
		assertFalse(owner.contains(d.getTwin()));
		
		// Store model
		assertNull(store.findModel(b));
		assertNull(store.findModel(c));
		assertNull(store.findModel(d));
		
		// SVG model
		assertNull(b.getParent());
		assertEquals(2, a.getChildCount());
		
		// DOM level
		assertNull(b.getElement().getParentNode());
		assertEquals(4, a.getElement().getChildCount());
		assertNull(b.getTwin().getParentNode());
		assertEquals(4, a.getTwin().getChildCount());
	}
}
