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
package org.vectomatic.svg.edit.client.gxt.form;

import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.OMSVGLinearGradientElement;
import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.OMSVGPatternElement;
import org.vectomatic.dom.svg.OMSVGRadialGradientElement;
import org.vectomatic.dom.svg.impl.SVGPaintParser;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.gxt.panels.ColorEditor;
import org.vectomatic.svg.edit.client.gxt.widget.PaintCell;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.layout.FillData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Field subclass to edit SVGPaint values
 * @author laaglu
 */
public class PaintField extends AdapterField {
	private class PaintFieldPanel extends LayoutContainer {
		private PaintCell paintCell;
		private ToggleButton[] toggleButtons;

		public PaintFieldPanel() {
			AppBundle bundle = AppBundle.INSTANCE;
			AppConstants constants = AppConstants.INSTANCE;
			String toggleGroup = "paintGroup";
			String[] tooltips = {
				constants.paintNone(),
				constants.paintCurrent(),
				constants.paintPlain(),
				constants.paintLinearGradient(),
				constants.paintRadialGradient(),
				constants.paintPattern()
			};
			AbstractImagePrototype icons[] = {
					AbstractImagePrototype.create(bundle.paintNone()),
					AbstractImagePrototype.create(bundle.paintCurrent()),
					AbstractImagePrototype.create(bundle.paintPlain()),
					AbstractImagePrototype.create(bundle.paintLinear()),
					AbstractImagePrototype.create(bundle.paintRadial()),
					AbstractImagePrototype.create(bundle.paintPattern())
			};
			SelectionListener[] listeners = {
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						setValue(SVGPaintParser.NONE);
						PaintField.this.fireEvent(Events.AfterEdit, new FieldEvent(PaintField.this));
					}
				},
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
					}
				},
				new SelectionListener<ButtonEvent>() {
					private HandlerRegistration changeReg;
					private HandlerRegistration closeReg;
					@Override
					public void componentSelected(ButtonEvent ce) {
						ColorEditor editor = ColorEditor.getInstance();
						OMSVGPaint paint = (OMSVGPaint) PaintField.this.value;
						// If the paint is already rgb, use it, otherwise, start from black
						editor.setPaint(paint.getPaintType() == OMSVGPaint.SVG_PAINTTYPE_RGBCOLOR ? paint : OMSVGParser.parsePaint(SVGConstants.CSS_BLACK_VALUE));
						changeReg = editor.addValueChangeHandler(new ValueChangeHandler<OMSVGPaint>() {
							@Override
							public void onValueChange(ValueChangeEvent<OMSVGPaint> event) {
								setValue(event.getValue());
							}					
						});
						closeReg = editor.addCloseHandler(new CloseHandler<ColorEditor>() {
							@Override
							public void onClose(CloseEvent<ColorEditor> event) {
								GWT.log("PaintField.onClose()");
								changeReg.removeHandler();
								closeReg.removeHandler();
								PaintField.this.fireEvent(Events.AfterEdit, new FieldEvent(PaintField.this));
							}
						});
						editor.show();
					}
				},
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
					}
				},
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
					}
				},
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
					}
				}
			};
			
			LayoutContainer toggleContainer = new LayoutContainer();
			toggleContainer.setLayout(new FillLayout(Orientation.HORIZONTAL));

			toggleButtons = new ToggleButton[6];
			for (int i = 0; i < toggleButtons.length;i++) {
				toggleButtons[i] = new ToggleButton();
				toggleButtons[i].setToggleGroup(toggleGroup);
				toggleButtons[i].setToolTip(tooltips[i]);
				toggleButtons[i].setIconAlign(IconAlign.TOP);
				toggleButtons[i].setIcon(icons[i]);
				toggleButtons[i].addSelectionListener(listeners[i]);
				toggleContainer.add(toggleButtons[i], i < toggleButtons.length -1 ? new FillData(0, 2, 0, 0) : new FillData(0));
			}

			paintCell = new PaintCell();
			RowLayout rowLayout = new RowLayout(Orientation.HORIZONTAL);
			setLayout(rowLayout);
			add(paintCell, new RowData(1,1, new Margins(0, 5, 0, 0)));
			add(toggleContainer, new RowData(156, 1, new Margins(0)));
			setHeight(26);
		}
		
		private ToggleButton getPaintButton(OMSVGPaint paint) {
			switch(paint.getPaintType()) {
				case OMSVGPaint.SVG_PAINTTYPE_NONE:
					return toggleButtons[0];
				case OMSVGPaint.SVG_PAINTTYPE_CURRENTCOLOR:
					return toggleButtons[1];
				case OMSVGPaint.SVG_PAINTTYPE_RGBCOLOR:
				case OMSVGPaint.SVG_PAINTTYPE_RGBCOLOR_ICCCOLOR:
					// Ignore ICC colors for the moment as they are not implemented
					// by browsers
					return toggleButtons[2];
				case OMSVGPaint.SVG_PAINTTYPE_URI:
				case OMSVGPaint.SVG_PAINTTYPE_URI_NONE:
				case OMSVGPaint.SVG_PAINTTYPE_URI_CURRENTCOLOR:
				case OMSVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR:
				case OMSVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR:
					String id = paint.getUri();
					if (id != null && id.startsWith("#")) {
						id = id.substring(1);
					}
					if (id != null) {
						OMNode node = OMNode.convert(OMSVGParser.currentDocument().getDocument().getElementById(id));
						if (node instanceof OMSVGLinearGradientElement) {
							return toggleButtons[3];
						} else if (node instanceof OMSVGRadialGradientElement) {
							return toggleButtons[4];
						} else if (node instanceof OMSVGPatternElement) {
							return toggleButtons[5];
						}
					}
					switch(paint.getPaintType()) {
						case OMSVGPaint.SVG_PAINTTYPE_URI_CURRENTCOLOR:
							return toggleButtons[1];
						case OMSVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR:
						case OMSVGPaint.SVG_PAINTTYPE_URI_RGBCOLOR_ICCCOLOR:
							return toggleButtons[2];
						case OMSVGPaint.SVG_PAINTTYPE_URI:
						case OMSVGPaint.SVG_PAINTTYPE_URI_NONE:
							return toggleButtons[0];
					}

				default:
					assert false;
					return null;
			}
		}
		
		public void update(OMSVGPaint paint) {
			if (paint != null) {
				getPaintButton(paint).toggle(true);
				paintCell.setPaint(paint);
			}
		}

	}
	public PaintField() {
		super(null);
		widget = new PaintFieldPanel();
		setResizeWidget(true);
		setFireChangeEventOnSetValue(true);
	}
	
	@Override
	public void setValue(Object value) {
		((PaintFieldPanel)widget).update((OMSVGPaint)value);
		super.setValue(value);
	}
	
	@Override
	public Object getValue() {
		return value;
	}

}
