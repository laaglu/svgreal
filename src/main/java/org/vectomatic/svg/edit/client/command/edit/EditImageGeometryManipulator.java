/**********************************************
 * Copyright (C) 2012 Lukas Laag
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
package org.vectomatic.svg.edit.client.command.edit;

import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.SvgrealApp;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel;
import org.vectomatic.svg.edit.client.model.svg.SVGLength;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * 2D manipulator class to edit image geometry.
 */
public class EditImageGeometryManipulator extends EditManipulatorBase {
	private static final String ATT_ACCEPT = "accept";
	private static final String ATT_ACCEPT_YES = "yes";
	private static final String ATT_ACCEPT_NO = "no";
	protected static enum Mode {
		PASSIVE {
			public boolean consumeEvent() { return false; }
		},
		POS {
			public boolean consumeEvent() { return true; }
		},
		TOP_LEFT {
			public boolean consumeEvent() { return true; }
		},
		BOTTOM_RIGHT {
			public boolean consumeEvent() { return true; }
		};
		public abstract boolean consumeEvent();
	}
	/**
	 * The mode the manipulator is presently using
	 */
	protected Mode mode;
	/**
	 * The (x,y) editor handle
	 */
	protected OMSVGRectElement posHandle;
	/**
	 * The top-left corner editor handle
	 */
	protected OMSVGRectElement topLeftHandle;
	/**
	 * The bottom right corner editor handle
	 */
	protected OMSVGRectElement bottomRightHandle;
	/**
	 * The transform from screen coordinates to
	 * manipulator coordinates when a mousedown event occurs
	 */
	protected OMSVGMatrix m;
	/**
	 * Vector from the mousedown point to the manipulator handle hotspot
	 */
	protected OMSVGPoint delta;
	/**
	 * A file reader object
	 */
	private FileReader reader;
	/**
	 * Url of the dropped file if drag and drop has been used.
	 */
	private String resourceName;

	/**
	 * Constructor
	 */
	public EditImageGeometryManipulator() {
	}
	/**
	 * Binds this manipulator to the specified SVG rect.
	 * @param element
	 * The SVG rect this manipulator is applied to.
	 * @return The root element of the manipulator
	 */
	@Override
	public OMSVGElement bind(Record record) {
		this.record = record;
		SVGElementModel model = (SVGElementModel) record.getModel();
		mode = Mode.PASSIVE;
		// Create the graphical representations for the manipulator
		// The manipulator has the following SVG structure
		// <g>
		//  <rect/>    position
		//  <g>
		//   <rect/>   top-left corner
		//   <rect/>   bottom-right corner
		//  </g>
		// </g>
		svg = model.getOwner().getSvgElement();
		g = new OMSVGGElement();
		g.setClassNameBaseVal(AppBundle.INSTANCE.css().imageGeometryManipulator());
		posHandle = new OMSVGRectElement();
		OMSVGGElement handleGroup = new OMSVGGElement();
		topLeftHandle = new OMSVGRectElement();
		bottomRightHandle = new OMSVGRectElement();
		g.appendChild(posHandle);
		g.appendChild(handleGroup);
		handleGroup.appendChild(topLeftHandle);
		handleGroup.appendChild(bottomRightHandle);
		monitorModel = true;
		model.addChangeListener(this);
		scheduleInit();
		
		g.addDragEnterHandler(new DragEnterHandler() {	
			@Override
			public void onDragEnter(DragEnterEvent event) {
				g.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_YES);
				event.stopPropagation();
				event.preventDefault();
			}
		});
		g.addDragLeaveHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				g.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_NO);
				event.stopPropagation();
				event.preventDefault();
			}
		});
		g.addDragOverHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				event.stopPropagation();
				event.preventDefault();
			}
		});
		g.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				g.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_NO);
				processFiles(event.getDataTransfer().<DataTransferExt>cast().getFiles());
				event.stopPropagation();
				event.preventDefault();
			}
		});
		
		return g;
	}
	
	public void processFiles(FileList files) {
		for (File file : files) {
			final String type = file.getType();
			if (type.startsWith("image")) {
				if (reader == null) {
	 				reader = new FileReader();
	 				reader.addErrorHandler(new org.vectomatic.file.events.ErrorHandler() {
						@Override
						public void onError(org.vectomatic.file.events.ErrorEvent event) {
							FileError error = reader.getError();
							String errorDesc = "";
							if (error != null) {
								ErrorCode errorCode = error.getCode();
								if (errorCode != null) {
									errorDesc = errorCode.name();
								}
							}
							SvgrealApp.getApp().info(ModelConstants.INSTANCE.imageLoadError(), errorDesc);
						}
	 				});
	 				reader.addLoadEndHandler(new LoadEndHandler() {
	 					
	 					@Override
	 					public void onLoadEnd(LoadEndEvent event) {
	 						if (reader.getError() == null) {
		 						try {
		 							String result = reader.getStringResult();
		 							String url = "data:" + type + ";base64," + DOMHelper.base64encode(result);
		 							((SVGImageElementModel)record.getModel()).setResourceName(resourceName);
		 							record.set(SVGConstants.XLINK_HREF_ATTRIBUTE, url);
		 							record.commit(false);
		 						} catch(Throwable t) {
		 							SvgrealApp.getApp().info(ModelConstants.INSTANCE.imageLoadError(), t.getMessage());
		 						}
	 						}
	 					}
	 				});
				}
 				try {
 					reader.readAsBinaryString(file);
	 				resourceName = file.getName();
 				} catch(Throwable t) {
 					// mozilla bug 701154: exception should not be thrown here
 					// the error handler ought to be invoked instead
 					SvgrealApp.getApp().info(ModelConstants.INSTANCE.imageLoadError(), t.getMessage());
 				}
 				break;
			}
		}
	}

	
	/**
	 * Detaches this manipulator from the DOM tree
	 */
	@Override
	public void unbind() {
		if (g != null) {
			Element parent = g.getElement().getParentElement();
			if (parent != null) {
				parent.removeChild(g.getElement());
			}
			SVGElementModel model = (SVGElementModel) record.getModel();
			model.removeChangeListener(this);
			record = null;
			g = null;
			posHandle = null;
			topLeftHandle = null;
			bottomRightHandle = null;
			mode = Mode.PASSIVE;
		}
	}
	
	@Override
	public void modelChanged(ChangeEvent event) {
		if (monitorModel) {
			SVGElementModel model = (SVGElementModel) record.getModel();
			super.modelChanged(event);
			SVGLength x = model.get(SVGConstants.SVG_X_ATTRIBUTE);
			SVGLength y = model.get(SVGConstants.SVG_Y_ATTRIBUTE);
			SVGLength width = model.get(SVGConstants.SVG_WIDTH_ATTRIBUTE);
			SVGLength height = model.get(SVGConstants.SVG_HEIGHT_ATTRIBUTE);
			posHandle.getX().getBaseVal().newValueSpecifiedUnits(x.getUnit(), x.getValue());
			posHandle.getY().getBaseVal().newValueSpecifiedUnits(y.getUnit(), y.getValue());
			posHandle.getWidth().getBaseVal().newValueSpecifiedUnits(width.getUnit(), width.getValue());
			posHandle.getHeight().getBaseVal().newValueSpecifiedUnits(height.getUnit(), height.getValue());
			update();
		}
	}
	
	private void update() {
		float x = posHandle.getX().getBaseVal().getValue();
		float y = posHandle.getY().getBaseVal().getValue();
		float width = posHandle.getWidth().getBaseVal().getValue();
		float height = posHandle.getHeight().getBaseVal().getValue();
		float hs = Math.max(5, Math.min(width, height) * 0.2f);
		topLeftHandle.getX().getBaseVal().setValue(x);
		topLeftHandle.getY().getBaseVal().setValue(y);
		topLeftHandle.getWidth().getBaseVal().setValue(hs);
		topLeftHandle.getHeight().getBaseVal().setValue(hs);
		bottomRightHandle.getX().getBaseVal().setValue(x + width - hs);
		bottomRightHandle.getY().getBaseVal().setValue(y + height - hs);
		bottomRightHandle.getWidth().getBaseVal().setValue(hs);
		bottomRightHandle.getHeight().getBaseVal().setValue(hs);
	}
	
	@Override
	public boolean processMouseUp(MouseUpEvent event) {
		if (mode != Mode.PASSIVE) {
			mode = Mode.PASSIVE;
			monitorModel = false;
			record.beginEdit();
			record.set(SVGConstants.SVG_X_ATTRIBUTE, new SVGLength(posHandle.getX().getBaseVal()));
			record.set(SVGConstants.SVG_Y_ATTRIBUTE, new SVGLength(posHandle.getY().getBaseVal()));
			record.set(SVGConstants.SVG_WIDTH_ATTRIBUTE, new SVGLength(posHandle.getWidth().getBaseVal()));
			record.set(SVGConstants.SVG_HEIGHT_ATTRIBUTE, new SVGLength(posHandle.getHeight().getBaseVal()));
			record.endEdit();
			record.commit(false);
			monitorModel = true;
		}
		return true;
	}

	@Override
	public boolean processMouseDown(MouseDownEvent event) {
		JavaScriptObject target = event.getNativeEvent().getEventTarget();
		m = g.getScreenCTM().inverse();
		delta = getCoordinates(event, m);
		float x = posHandle.getX().getBaseVal().getValue();
		float y = posHandle.getY().getBaseVal().getValue();
		float width = posHandle.getWidth().getBaseVal().getValue();
		float height = posHandle.getHeight().getBaseVal().getValue();
		OMSVGPoint p = svg.createSVGPoint();
		if (target == posHandle.getElement()) {
			mode = Mode.POS;
			p.setX(x);
			p.setY(y);
		} else if (target == topLeftHandle.getElement()) {
			p.setX(x);
			p.setY(y);
			mode = Mode.TOP_LEFT;
		} else if (target == bottomRightHandle.getElement()) {
			p.setX(x + width);
			p.setY(y + height);
			mode = Mode.BOTTOM_RIGHT;
		}
		if (mode.consumeEvent()) {
			delta.substract(p);
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}

	@Override
	public boolean processMouseMove(MouseMoveEvent event) {
		if (mode.consumeEvent()) {
			float x = posHandle.getX().getBaseVal().getValue();
			float y = posHandle.getY().getBaseVal().getValue();
			float width = posHandle.getWidth().getBaseVal().getValue();
			float height = posHandle.getHeight().getBaseVal().getValue();
			OMSVGPoint p = getCoordinates(event, m).substract(delta);
			switch(mode) {
				case POS:
					{
						posHandle.getX().getBaseVal().setValue(p.getX());
						posHandle.getY().getBaseVal().setValue(p.getY());
					}
					break;
				case TOP_LEFT:
					{
						float xmax = Math.min(p.getX(), x + width);
						float ymax = Math.min(p.getY(), y + height);
						posHandle.getX().getBaseVal().setValue(xmax);
						posHandle.getY().getBaseVal().setValue(ymax);
						posHandle.getWidth().getBaseVal().setValue(width + x - xmax);
						posHandle.getHeight().getBaseVal().setValue(height + y - ymax);
					}
					break;
				case BOTTOM_RIGHT:
					{
						float xmin = Math.max(p.getX(), x);
						float ymin = Math.max(p.getY(), y);
						posHandle.getWidth().getBaseVal().setValue(xmin - x);
						posHandle.getHeight().getBaseVal().setValue(ymin - y);
					}
					break;
			}
			update();
			event.preventDefault();
			event.stopPropagation();
		}
		return true;
	}
}
