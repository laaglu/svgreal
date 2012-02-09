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
package org.vectomatic.svg.edit.client.command.add;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSeg;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.impl.SVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.command.FactoryInstantiatorBase;
import org.vectomatic.svg.edit.client.command.IFactoryInstantiator;
import org.vectomatic.svg.edit.client.command.path.IPathRepOwner;
import org.vectomatic.svg.edit.client.command.path.SVGCubicSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGLineSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGMoveSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGQuadraticSegRep;
import org.vectomatic.svg.edit.client.command.path.SVGSegRep;
import org.vectomatic.svg.edit.client.engine.SVGModel;
import org.vectomatic.svg.edit.client.event.MouseDownProcessor;
import org.vectomatic.svg.edit.client.event.MouseMoveProcessor;
import org.vectomatic.svg.edit.client.event.MouseUpProcessor;
import org.vectomatic.svg.edit.client.event.ScalingEvent;
import org.vectomatic.svg.edit.client.event.ScalingHandler;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGPathSegType;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Command factory to add new paths to the the SVG model.
 * @author laaglu
 */
public class AddPathCommandFactory extends AddCommandFactoryBase implements IPathRepOwner, MouseDownProcessor, MouseMoveProcessor, MouseUpProcessor, ScalingHandler {
	@SuppressWarnings("serial")
	public static final IFactoryInstantiator<AddPathCommandFactory> INSTANTIATOR = new FactoryInstantiatorBase<AddPathCommandFactory>(ModelConstants.INSTANCE.addPathCmdFactory(), ModelConstants.INSTANCE.addPathCmdFactoryDesc()) {
		@Override
		public AddPathCommandFactory create() {
			return new AddPathCommandFactory();
		}
	};

	@Override
	public IFactoryInstantiator<?> getInstantiator() {
		return INSTANTIATOR;
	}

	class AddPathToolBar extends Window {
		private ToggleButton[] pathButtons;
		private Button undoButton, redoButton, commitButton;
		private SelectionListener<ButtonEvent> segTypeListener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				ToggleButton button = (ToggleButton)ce.getButton();
				if (button.isPressed()) {
					setSegmentType(button.getToolTip().getToolTipConfig().getText());
				}
			}
		};
		public AddPathToolBar() {
			ModelConstants constants = ModelConstants.INSTANCE;
			AbstractImagePrototype icons[] = {
				AbstractImagePrototype.create(AppBundle.INSTANCE.pathMove()),
				AbstractImagePrototype.create(AppBundle.INSTANCE.pathLine()),
				AbstractImagePrototype.create(AppBundle.INSTANCE.pathQuadratic()),
				AbstractImagePrototype.create(AppBundle.INSTANCE.pathCubic()),
			};
			String[] toolTips = {
					constants.segMoveTo(),
					constants.segLineTo(),
					constants.segQuadraticTo(),
					constants.segCubicTo(),
			};
			LayoutContainer pathContainer = new LayoutContainer();
	        HBoxLayout hboxLayout = new HBoxLayout();  
	        hboxLayout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
	        pathContainer.setLayout(hboxLayout);
	        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
	        flex.setFlex(1);  
	        HBoxLayoutData flex2 = new HBoxLayoutData(new Margins(0));  
	        flex2.setFlex(1);  
			HBoxLayoutData[] flexes = {
				flex,
				flex,
				flex,
				flex2
			};
			pathButtons = new ToggleButton[icons.length];
			for (int i = 0; i < icons.length; i++) {
				pathButtons[i] = new ToggleButton();
				pathButtons[i].setIcon(icons[i]);
				pathButtons[i].setIconAlign(IconAlign.TOP);
				pathButtons[i].setToolTip(toolTips[i]);
				pathButtons[i].addSelectionListener(segTypeListener);
				pathButtons[i].setToggleGroup("path");
				pathButtons[i].setSize(20, 24);
		        pathContainer.add(pathButtons[i], flexes[i]);
			}

			undoButton = new Button();
			undoButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.undo()));
			undoButton.setIconAlign(IconAlign.TOP);
			undoButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					undoSegment();
				}
			});
			redoButton = new Button();
			redoButton.setIcon(AbstractImagePrototype.create(AppBundle.INSTANCE.redo()));
			redoButton.setIconAlign(IconAlign.TOP);
			redoButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					redoSegment();
				}
			});
			LayoutContainer urContainer = new LayoutContainer();
	        HBoxLayout urHboxLayout = new HBoxLayout();  
	        urHboxLayout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
	        urContainer.setLayout(urHboxLayout);
	        urContainer.add(undoButton, flex);
			urContainer.add(redoButton, flex2);

			commitButton = new Button();
			commitButton.setText(AppConstants.INSTANCE.commitButton());
			commitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					commit();
				}
			});

	        LayoutContainer container = new LayoutContainer();
	        VBoxLayout layout = new VBoxLayout();  
	        layout.setPadding(new Padding(5));  
	        layout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
	        VBoxLayoutData vbl1 = new VBoxLayoutData(new Margins(0, 0, 5, 0));
	        VBoxLayoutData vbl2 = new VBoxLayoutData(new Margins(0));
	        container.setLayout(layout);
	        container.add(pathContainer, vbl1);
	        container.add(urContainer, vbl1);
	        container.add(commitButton, vbl2);
	        
	        int w = 140, h = 120;
	        setMinWidth(w);
	        setMinHeight(h);
	        setWidth(w);
	        setHeight(h);
	        setResizable(false);
	        setLayout(new FitLayout());
	        add(container);
			setClosable(false);
			setBorders(true);
			
	        Rectangle rect = VectomaticApp2.getApp().getRectangle();
			setPagePosition(rect.x + rect.width - w -5, rect.y + rect.height - h - 5);
		}
		public ToggleButton getPathButton(String segType) {
			for (ToggleButton button : pathButtons) {
				if (segType.equals(button.getToolTip().getToolTipConfig().getText())) {
					return button;
				}
			}
			return null;
		}
	}

	enum State {
		MOVE {
			boolean processMouseDown(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				factory.appendSegment(new SVGMoveSegRep(factory, factory.path.createSVGPathSegMovetoAbs(p.getX(), p.getY())));
				factory.validateSegment(hs);
				return true;
			}
		},
		LINE {
			boolean processMouseDown(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryLineP2());
				if (factory.getSegCount() == 0) {
					factory.appendSegment(new SVGMoveSegRep(factory, factory.path.createSVGPathSegMovetoAbs(p.getX(), p.getY())));
					factory.validateSegment(hs);
				}
				if (factory.danglingSegment() != null) {
					factory.validateSegment(hs);
				}
				factory.appendSegment(new SVGLineSegRep(factory, factory.path.createSVGPathSegLinetoAbs(p.getX(), p.getY())));
				return true;
			}
			boolean processMouseMove(AddPathCommandFactory factory, OMSVGPoint delta, float hs) {
				SVGSegRep segRep = factory.danglingSegment();
				if (segRep != null) {
					// Update the second point
					segRep.updateEnd(delta, hs);
				}
				return true;
			}
		},
		QUADRATIC {
			@Override
			boolean processMouseDown(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				// Create the first point and first tangent
				if (factory.getSegCount() == 0) {
					factory.appendSegment(new SVGMoveSegRep(factory, factory.path.createSVGPathSegMovetoAbs(p.getX(), p.getY())));
					factory.validateSegment(hs);
				}
				if (factory.danglingSegment() != null) {
					factory.validateSegment(hs);
				}
				factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryQuadraticCp1b());
				SVGSegRep lastSegRep = factory.lastSegment();
				SVGSegRep segRep = factory.appendSegment(new SVGQuadraticSegRep(factory, factory.path.createSVGPathSegCurvetoQuadraticAbs(lastSegRep.getX(), lastSegRep.getY(), p.getX(), p.getY())));
				segRep.setCp1(p, hs);
				if (factory.ctrlPressed) {
					SVGSegRep prevSegRep = factory.previousCurve();
					if (prevSegRep != null) {
						prevSegRep.setCp2(factory.owner.getSvgElement().createSVGPoint(2 * segRep.getX() - p.getX(), 2 * segRep.getY() -p.getY()), hs);
					}
				}
				return true;
			}
			
			@Override
			boolean processMouseMove(AddPathCommandFactory factory, OMSVGPoint delta, float hs) {
				SVGSegRep segRep = factory.danglingSegment();
				if (segRep != null) {
					if (factory.mousePressed) {
						// Update the first tangent
						segRep.processMouseMove(delta, segRep.getCp1(), hs, false);
						if (factory.ctrlPressed) {
							SVGSegRep prevSegRep = factory.previousCurve();
							if (prevSegRep != null) {
								prevSegRep.processMouseMove(factory.owner.getSvgElement().createSVGPoint(-delta.getX(), -delta.getY()), prevSegRep.getCp2(), hs, false);
							}
						}
					} else {
						// Update the endpoint
						segRep.setX(segRep.getX() + delta.getX());
						segRep.setY(segRep.getY() + delta.getY());
						segRep.update(hs);
					}
				}
				return true;
			}
			
			@Override
			boolean processMouseUp(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryQuadraticP2());
				SVGSegRep segRep = factory.danglingSegment();
				factory.p0.setX(segRep.getX());
				factory.p0.setY(segRep.getY());
				return true;
			}
		},
		CUBIC1 {
			@Override
			boolean processMouseDown(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryCubicCp1b());
				// Create the first point and first tangent
				if (factory.getSegCount() == 0) {
					factory.appendSegment(new SVGMoveSegRep(factory, factory.path.createSVGPathSegMovetoAbs(p.getX(), p.getY())));
					factory.validateSegment(hs);
				}
				SVGSegRep segRep = factory.danglingSegment();
				if (segRep == null) {
					SVGSegRep lastSegRep = factory.lastSegment();
					segRep = factory.appendSegment(new SVGCubicSegRep(factory, factory.path.createSVGPathSegCurvetoCubicAbs(lastSegRep.getX(), lastSegRep.getY(), lastSegRep.getX(), lastSegRep.getY(), lastSegRep.getX(), lastSegRep.getY())));
				}
				segRep.setCp1(p, hs);
				if (factory.ctrlPressed) {
					SVGSegRep prevSegRep = factory.previousCurve();
					if (prevSegRep != null) {
						prevSegRep.setCp2(factory.owner.getSvgElement().createSVGPoint(2 * segRep.getX() - p.getX(), 2 * segRep.getY() - p.getY()), hs);
					}
				}
				return true;
			}
			
			@Override
			boolean processMouseMove(AddPathCommandFactory factory, OMSVGPoint delta, float hs) {
				if (factory.mousePressed) {
					// Update the first tangent
					SVGSegRep segRep = factory.danglingSegment();
					segRep.processMouseMove(delta, segRep.getCp1(), hs, false);
					if (factory.ctrlPressed) {
						SVGSegRep prevSegRep = factory.previousCurve();
						if (prevSegRep != null) {
							prevSegRep.processMouseMove(factory.owner.getSvgElement().createSVGPoint(-delta.getX(), -delta.getY()), prevSegRep.getCp2(), hs, false);
						}
					}
				}
				return true;
			}
			
			@Override
			boolean processMouseUp(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryCubicCp2a());
				factory.setState(State.CUBIC2);
				SVGSegRep segRep = factory.danglingSegment();
				factory.p0.setX(segRep.getX());
				factory.p0.setY(segRep.getY());
				return true;
			}
		},
		CUBIC2 {
			@Override
			boolean processMouseDown(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryCubicCp2b());
				if (factory.ctrlPressed) {
					// Continuity modef
					SVGSegRep segRep = factory.danglingSegment();
					factory.validateSegment(hs);
					factory.appendSegment(new SVGCubicSegRep(factory, factory.path.createSVGPathSegCurvetoCubicAbs(segRep.getX(), segRep.getY(), segRep.getX(), segRep.getY(), segRep.getX(), segRep.getY())));
				}
				return true;
			}
		
			@Override
			boolean processMouseMove(AddPathCommandFactory factory, OMSVGPoint delta, float hs) {
				SVGSegRep segRep = factory.danglingSegment();
				if (factory.ctrlPressed) {
					// Update the first tangent and the second tangent of the previous segment
					segRep.processMouseMove(delta, segRep.getCp1(), hs, false);
					SVGSegRep prevSegRep = factory.previousCurve();
					if (prevSegRep != null) {
						prevSegRep.processMouseMove(factory.owner.getSvgElement().createSVGPoint(-delta.getX(), -delta.getY()), prevSegRep.getCp2(), hs, false);
					}
				} else if (factory.mousePressed) {
					// Update the second tangent
					segRep.processMouseMove(delta, segRep.getCp2(), hs, false);
				} else {
					// Update the second point
					segRep.updateEnd(delta, hs);
				}
				return true;
			}

			@Override
			boolean processMouseUp(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
				SVGSegRep segRep = factory.danglingSegment();
				if (factory.ctrlPressed) {
					factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryCubicCp2a());
					factory.p0.setX(segRep.getX());
					factory.p0.setY(segRep.getY());
				} else {
					factory.updateStatus(ModelConstants.INSTANCE.addPathCmdFactoryCubicCp1a());
					// Create the next cubic segment
					factory.validateSegment(hs);
					factory.appendSegment(new SVGCubicSegRep(factory, factory.path.createSVGPathSegCurvetoCubicAbs(segRep.getX(), segRep.getY(), segRep.getX(), segRep.getY(), segRep.getX(), segRep.getY())));
					factory.setState(CUBIC1);
				}
				return true;
			}
		};
		boolean processMouseDown(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
			return false;
		}
		boolean processMouseMove(AddPathCommandFactory factory, OMSVGPoint delta, float hs) {
			return false;
		}
		boolean processMouseUp(AddPathCommandFactory factory, OMSVGPoint p, float hs) {
			return false;
		}
	}

	/**
	 * A toolbar to choose the type of the current segment
	 */
	private AddPathToolBar toolBar;
	/**
	 * The group where SVG elements representing this
	 * manipulator are nested
	 */
	protected OMSVGGElement g;
	/**
	 * A group for elements representing tangents
	 */
	protected OMSVGGElement tangentGroup;
	/**
	 * A group for elements representing vertices
	 */
	protected OMSVGGElement vertexGroup;
	/**
	 * The path representation
	 */
	protected OMSVGPathElement path;
	/**
	 * The path segment list
	 */
	OMSVGPathSegList segList;
	/**
	 * The path segment representations
	 */
	protected List<SVGSegRep> segments;
	/**
	 * The current mode
	 */
	protected State state;
	/**
	 * True if the mouse button is pressed, false otherwise
	 */
	protected boolean mousePressed;
	/**
	 * True if the ctrl key is pressed, false otherwise
	 */
	protected boolean ctrlPressed;
	/**
	 * The type of segment about to be added to the path
	 */
	protected String segType;
	/**
	 * The mousedown point in user space
	 */
	protected OMSVGPoint p0;
	/**
	 * The segment current being edited by the end-user,
	 * but not yet finished
	 */
	protected SVGSegRep danglingSegment;
	/**
	 * Index of the last visible segment in the segment stack
	 */
	protected int last;
	/**
	 * Event registration for the scaling handler
	 */
	protected HandlerRegistration scalingHandlerReg;

	public AddPathCommandFactory() {
//		setMode(Mode.CUBIC1);
		segments = new ArrayList<SVGSegRep>();
		toolBar = new AddPathToolBar();
	}

	@Override
	public void start(Object requester) {
		GWT.log("AddPathCommandFactory.start(" + requester + ")");
		super.start(requester);
		toolBar.getPathButton(ModelConstants.INSTANCE.segLineTo()).toggle(true);
		setSegmentType(ModelConstants.INSTANCE.segLineTo());
		toolBar.show();
	}

	@Override
	public void stop() {
		GWT.log("AddPathCommandFactory.stop()");
		clear();
		toolBar.hide();
		super.stop();
	}

	public void setSegmentType(String segType) {
		GWT.log("AddPathCommandFactory.setSegmentType(" + segType + ")");
		this.segType = segType;
		removeDanglingSegment();
		ModelConstants constants = ModelConstants.INSTANCE;
		switch(SVGPathSegType.INSTANCE.fromName(segType)) {
			case OMSVGPathSeg.PATHSEG_MOVETO_ABS:
				setState(State.MOVE);
				updateStatus(constants.addPathCmdFactoryMove());
				break;
			case OMSVGPathSeg.PATHSEG_LINETO_ABS: 
				setState(State.LINE);
				if (last == 0) {
					updateStatus(constants.addPathCmdFactoryLineFirst());
				} else {
					updateStatus(constants.addPathCmdFactoryLineP2());
					SVGSegRep segRep = lastSegment();
					appendSegment(new SVGLineSegRep(this, path.createSVGPathSegLinetoAbs(segRep.getX(), segRep.getY())));
					p0.setX(segRep.getX());
					p0.setY(segRep.getY());
				}
				break;
			case OMSVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS: 
				setState(State.QUADRATIC);
				updateStatus(last == 0 ? constants.addPathCmdFactoryQuadraticCp1First() : constants.addPathCmdFactoryQuadraticCp1a());
				break;
			case OMSVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS: 
				setState(State.CUBIC1);
				updateStatus(last == 0 ? constants.addPathCmdFactoryCubicCp1First() : constants.addPathCmdFactoryCubicCp1a());
				break;
		}
	}

	public void update() {
		toolBar.undoButton.setEnabled(last > 0);
		toolBar.redoButton.setEnabled(last < segments.size());
		toolBar.commitButton.setEnabled(last > 0);
	}
	
	public void undoSegment() {
		GWT.log("AddPathCommandFactory.undoSegment()");
		removeDanglingSegment();
		SVGSegRep segRep = lastSegment();
		last--;
		segList.removeItem(last);
		vertexGroup.removeChild(segRep.getVertex());
		tangentGroup.removeChild(segRep.getTangents());
		update();
		setSegmentType(segType);
	}
	
	public void redoSegment() {
		GWT.log("AddPathCommandFactory.redoSegment()");
		removeDanglingSegment();
		last++;
		SVGSegRep segRep = lastSegment();
		segList.appendItem(segRep.getElement());
		vertexGroup.appendChild(segRep.getVertex());
		tangentGroup.appendChild(segRep.getTangents());
		update();
		setSegmentType(segType);
	}

	public void commit() {
		GWT.log("AddPathCommandFactory.commit()");
		if (owner != null) {
			removeDanglingSegment();
			g.removeChild(path);
			owner.getTwinGroup().appendChild(path);
			applyCssContextStyle((SVGElement) path.getElement().cast());
//			path.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, SVGConstants.CSS_NONE_VALUE);
//			path.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, SVGConstants.CSS_BLACK_VALUE);
			path.getStyle().setSVGProperty(SVGConstants.CSS_VISIBILITY_PROPERTY, SVGConstants.CSS_VISIBLE_VALUE);
			createCommand(path);
			clear();
			setSegmentType(segType);
		}
	}

	public void clear() {
		if (owner != null) {
			scalingHandlerReg.removeHandler();
			OMSVGElement parent = owner.getTwinGroup();
			parent.removeChild(g);
			owner = null;
			g = null;
			path = null;
			tangentGroup = null;
			vertexGroup = null;
			segList = null;
			segments.clear();
			danglingSegment = null;
			last = 0;
			update();
		}
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
//		GWT.log("AddPathCommandFactory.processMouseDown(" + state + ")");
		if (owner == null) {
			owner = VectomaticApp2.getApp().getActiveModel();
			scalingHandlerReg = owner.addScalingHandler(this);
			OMSVGElement parent = owner.getTwinGroup();
			path = new OMSVGPathElement();
			segList = path.getPathSegList();
			g = new OMSVGGElement();
			Mode.TANGENT.write(g);
			g.setClassNameBaseVal(AppBundle.INSTANCE.css().pathGeometryManipulator());
			tangentGroup = new OMSVGGElement();
			vertexGroup = new OMSVGGElement();
			g.appendChild(path);
			g.appendChild(tangentGroup);
			g.appendChild(vertexGroup);
			parent.appendChild(g);

		}
		
		mousePressed = true;
		ctrlPressed = event.isControlKeyDown();
		p0 = owner.getCoordinates(event, true);
		
		boolean processed = state.processMouseDown(this, owner.getCoordinates(event, true), SVGModel.getVertexSize(path));
		if (processed) {
			event.stopPropagation();
			event.preventDefault();
		}
		return processed;
	}

	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (p0 == null) {
			return false;
		}
//		GWT.log("AddPathCommandFactory.processMouseMove(" + state + ")");
		OMSVGPoint p1 = owner.getCoordinates(event, true);
		OMSVGPoint delta = p1.substract(p0, owner.getSvgElement().createSVGPoint());
		p1.assignTo(p0);
		float hs = SVGModel.getVertexSize(path);
		boolean processed = state.processMouseMove(this, delta, hs);
		if (processed) {
			event.stopPropagation();
			event.preventDefault();
		}
		if (danglingSegment != null) {
			if (danglingAtOrigin(hs)) {
				danglingSegment.setState(VertexState.CLOSING);
				Closure.CLOSE.write(path);
			} else {
				danglingSegment.setState(VertexState.NONE);
				Closure.OPEN.write(path);
			}
		}
		return processed;
	}
	
	private boolean danglingAtOrigin(float hs) {
		float x = danglingSegment.getX() - segments.get(0).getX();
		float y = danglingSegment.getY() - segments.get(0).getY();
		return x*x + y*y < hs*hs;
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
//		GWT.log("AddPathCommandFactory.processMouseUp(" + state + ")");
		boolean processed = state.processMouseUp(this, owner.getCoordinates(event, true), SVGModel.getVertexSize(path));
		if (processed) {
			event.stopPropagation();
			event.preventDefault();
		}
		mousePressed = false;
		ctrlPressed = false;
		return processed;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	@Override
	public OMSVGSVGElement getSvg() {
		return owner.getSvgElement();
	}

	@Override
	public List<SVGSegRep> getSegments() {
		return segments;
	}
	
	public int getSegCount() {
		return last;
	}
	
	public SVGSegRep danglingSegment() {
		return danglingSegment;
	}

	public SVGSegRep lastSegment() {
		return last > 0 ? segments.get(last - 1) : null;
	}

	public SVGSegRep previousCurve() {
		SVGSegRep segRep = lastSegment();
		if (segRep instanceof SVGQuadraticSegRep || segRep instanceof SVGCubicSegRep) {
			return segRep;
		}
		return null;
	}

	@Override
	public Mode getMode() {
		return Mode.TANGENT;
	}

	@Override
	public boolean isClosed() {
		return false;
	}
	
	public SVGSegRep appendSegment(SVGSegRep segRep) {
		SVGSegRep prevSeg = lastSegment();
		segList.appendItem(segRep.getElement());
		if (prevSeg != null) {
			prevSeg.setNext(segRep);
			segRep.setPrevious(prevSeg);
		}
		vertexGroup.appendChild(segRep.getVertex());
		tangentGroup.appendChild(segRep.getTangents());
		danglingSegment = segRep;
		segRep.update(SVGModel.getVertexSize(path));
		return segRep;
	}
	
	public void validateSegment(float hs) {
		// Invalidate segments in the redo stack
		for (int i = segments.size() - 1; i >= last; i--) {
			segments.remove(i);
		}
		
		segments.add(danglingSegment);
		danglingSegment.setState(VertexState.SELECTED);
		
		if (last > 0 && danglingAtOrigin(hs) && path.getTotalLength() > hs) {
			// Auto-terminate the segment if:
			// + the segment is not the initial moveto segment
			// + the segment terminates at the path origin
			// + the path length is not null
			danglingSegment.setX(segments.get(0).getX());
			danglingSegment.setY(segments.get(0).getY());
			segList.appendItem(path.createSVGPathSegClosePath());
			danglingSegment = null;
			commit();
			return;
		}
		danglingSegment = null;
		last++;
		update();
	}
	
	public void removeDanglingSegment() {
		if (danglingSegment != null) {
			int index = segList.getNumberOfItems() - 1;
			segList.removeItem(index);
			vertexGroup.removeChild(danglingSegment.getVertex());
			tangentGroup.removeChild(danglingSegment.getTangents());
			danglingSegment = null;
		}		
	}

	@Override
	public void onScale(ScalingEvent event) {
		if (path != null) {
			float size = SVGModel.getVertexSize(path);
			for (SVGSegRep rep : segments) {
				rep.update(size);
			}
			if (danglingSegment != null) {
				danglingSegment.update(size);
			}
		}
	}
}
