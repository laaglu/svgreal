/**********************************************
 * Copyright (C) 2011 Lukas Laag
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
package org.vectomatic.svg.edit.client.command.dnd;

import java.util.Arrays;
import java.util.Map;

import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.svg.edit.client.EditTestBase;
import org.vectomatic.svg.edit.client.command.DndCommandFactory.DropGesture;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.engine.SVGProcessor;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;

import com.google.gwt.user.client.ui.RootPanel;

public class ReorderCommandGwtTest extends EditTestBase {
	public void testCommand() {
		/*==================================================================================
		                                        TEST 1
		 ==================================================================================*/
		/*=========================================
		 root
		  a
		    (b)
		        c
		          (d)
		     e
		       (f)
		     g
		    (h)
		    (i)
		        j
		     k  <----		        
		    (l)
		    (m)
		 =========================================*/
		OMSVGSVGElement svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		SVGModel owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		RootPanel.get().add(new SVGImage(svg));
		owner.remove(owner.getViewBox()); // Remove viewBox to simplify test
		SVGElementModel a = owner.getRoot();
		SVGElementModel b = createElementModel(owner, "b");
		SVGElementModel c = createElementModel(owner, "c");
		SVGElementModel d = createElementModel(owner, "d");
		SVGElementModel e = createElementModel(owner, "e");
		SVGElementModel f = createElementModel(owner, "f");
		SVGElementModel g = createElementModel(owner, "g");
		SVGElementModel h = createElementModel(owner, "h");
		SVGElementModel i = createElementModel(owner, "i");
		SVGElementModel j = createElementModel(owner, "j");
		SVGElementModel k = createElementModel(owner, "k");
		SVGElementModel l = createElementModel(owner, "l");
		SVGElementModel m = createElementModel(owner, "m");
		createTree(owner, new SVGElementModel[][]{{a, b}, {a, e}, {a, g}, {a, h}, {a, i}, {a, k}, {a, l}, {a, m}, {b, c}, {c, d}, {e, f}, {i, j}});
		String dump = toString(dump(owner));
		Map<SVGElementModel, OMSVGMatrix> xforms = randomTreeTranform(owner);
		
		DndCommandBase command = new ReorderCommand(Arrays.<SVGElementModel>asList(new SVGElementModel[]{i, l, m, d, b, f, h}), k, DropGesture.OnNode);
		/*=========================================
		 root
		  a
		     e
		     g
		     k
		        i
		           j
		        l
		        m
		        d
		        b
		           c
		        f
		        h
		 =========================================*/
		command.commit();
		assertEquals("{{a, e}, {a, g}, {a, k}, {k, i}, {k, l}, {k, m}, {k, d}, {k, b}, {k, f}, {k, h}, {i, j}, {b, c}}", toString(dump(owner)));
		assertTransformsUnchanged(xforms);

		command.rollback();
		assertEquals(dump, toString(dump(owner)));
		assertTransformsUnchanged(xforms);

		/*==================================================================================
		                                        TEST 2
		==================================================================================*/

		/*=========================================
		 root
		  a
		     b
		    (c)
		    (d)
		    (e)
		     f
		     g <----
		     h
		    (i)
		    (j)
		    (k)
		     l
		 =========================================*/
		svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		RootPanel.get().add(new SVGImage(svg));
		owner.remove(owner.getViewBox()); // Remove viewBox to simplify test
		a = owner.getRoot();
		b = createElementModel(owner, "b");
		c = createElementModel(owner, "c");
		d = createElementModel(owner, "d");
		e = createElementModel(owner, "e");
		f = createElementModel(owner, "f");
		g = createElementModel(owner, "g");
		h = createElementModel(owner, "h");
		i = createElementModel(owner, "i");
		j = createElementModel(owner, "j");
		k = createElementModel(owner, "k");
		l = createElementModel(owner, "l");
		createTree(owner, new SVGElementModel[][]{{a, b}, {a, c}, {a, d}, {a, e}, {a, f}, {a, g}, {a, h}, {a, i}, {a, j}, {a, k}, {a, l}});
		dump = toString(dump(owner));
		
		command = new ReorderCommand(Arrays.<SVGElementModel>asList(new SVGElementModel[]{k, d, e, j, i, c}), g, DropGesture.OnNode);
		/*=========================================
		 root
		  a
		     b
		     f
		     g
		        k
		        d
		        e
		        j
		        i
		        c
		     h
		     l
		 =========================================*/
		command.commit();
		assertEquals("{{a, b}, {a, f}, {a, g}, {a, h}, {a, l}, {g, k}, {g, d}, {g, e}, {g, j}, {g, i}, {g, c}}", toString(dump(owner)));

		command.rollback();
		assertEquals(dump, toString(dump(owner)));

		/*==================================================================================
		                                        TEST 3
		==================================================================================*/

		/*=========================================
		 root
		  a
		        <----
		     b
		       (c)
		           (d)
		               e
		                 (f)
		                     g
		                       (h)
		                           (i)
		     j
		    (k)
		 =========================================*/
		svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		RootPanel.get().add(new SVGImage(svg));
		owner.remove(owner.getViewBox()); // Remove viewBox to simplify test
		a = owner.getRoot();
		b = createElementModel(owner, "b");
		c = createElementModel(owner, "c");
		d = createElementModel(owner, "d");
		e = createElementModel(owner, "e");
		f = createElementModel(owner, "f");
		g = createElementModel(owner, "g");
		h = createElementModel(owner, "h");
		i = createElementModel(owner, "i");
		j = createElementModel(owner, "j");
		k = createElementModel(owner, "k");
		createTree(owner, new SVGElementModel[][]{{a, b}, {a, j}, {a, k}, {b, c}, {c, d}, {d, e}, {e, f}, {f, g}, {g, h}, {h, i}});
		dump = toString(dump(owner));
		
		command = new ReorderCommand(Arrays.<SVGElementModel>asList(new SVGElementModel[]{f, d, c, k, h, i}), b, DropGesture.BeforeNode);
		/*=========================================
		 root
		  a
		     f
		        g
		     d
		        e
		     c
		     k
		     h
		     i
		     b
		     j
		 =========================================*/
		command.commit();
		assertEquals("{{a, f}, {a, d}, {a, c}, {a, k}, {a, h}, {a, i}, {a, b}, {a, j}, {f, g}, {d, e}}", toString(dump(owner)));

		command.rollback();
		assertEquals(dump, toString(dump(owner)));
		
		/*==================================================================================
                                                TEST 4
		==================================================================================*/
	
		/*=========================================
		 root
		  a
		    (b)
		       (c)
		           d
		          (e)
		          (f)
		             g
		       (h)
		           i
		          (j)
		        k
		        l
		     m
		        <----
		 =========================================*/
		svg = new OMSVGSVGElement();
		svg.setViewBox(0, 0, 600, 400);
		owner = SVGModel.newInstance(svg, "a", SVGProcessor.newIdPrefix());
		RootPanel.get().add(new SVGImage(svg));
		owner.remove(owner.getViewBox()); // Remove viewBox to simplify test
		a = owner.getRoot();
		b = createElementModel(owner, "b");
		c = createElementModel(owner, "c");
		d = createElementModel(owner, "d");
		e = createElementModel(owner, "e");
		f = createElementModel(owner, "f");
		g = createElementModel(owner, "g");
		h = createElementModel(owner, "h");
		i = createElementModel(owner, "i");
		j = createElementModel(owner, "j");
		k = createElementModel(owner, "k");
		l = createElementModel(owner, "l");
		m = createElementModel(owner, "m");
		createTree(owner, new SVGElementModel[][]{{a, b}, {a, m}, {b, c}, {b, h}, {b, k}, {b, l}, {c, d}, {c, e}, {c, f}, {h, i}, {h, j}, {f, g}});
		dump = toString(dump(owner));
		
		command = new ReorderCommand(Arrays.<SVGElementModel>asList(new SVGElementModel[]{f, b, c, e, j, h}), m, DropGesture.AfterNode);
		/*=========================================
		 root
		  a
		     m
		     f
		        g
		     b
		        k
		        l
		     c
		        d
		     e
		     j
		     h
		        i
		 =========================================*/
		command.commit();
		assertEquals("{{a, m}, {a, f}, {a, b}, {a, c}, {a, e}, {a, j}, {a, h}, {f, g}, {b, k}, {b, l}, {c, d}, {h, i}}", toString(dump(owner)));

		command.rollback();
		assertEquals(dump, toString(dump(owner)));

	}
}
