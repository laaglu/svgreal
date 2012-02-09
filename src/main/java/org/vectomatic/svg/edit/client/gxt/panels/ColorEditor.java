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
package org.vectomatic.svg.edit.client.gxt.panels;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.OMCSSPrimitiveValue;
import org.vectomatic.dom.svg.OMRGBColor;
import org.vectomatic.dom.svg.OMSVGPaint;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.SvgrealApp;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.HSliderBar;
import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.widgetideas.client.SliderListener;
import com.google.gwt.widgetideas.client.SliderListenerAdapter;
import com.google.gwt.widgetideas.client.VSliderBar;

/**
 * Window class to implement a gimp-like color editor
 * @author laaglu
 */
public class ColorEditor extends Window implements HasValueChangeHandlers<OMSVGPaint>, HasCloseHandlers<ColorEditor> {
	private static ColorEditor INSTANCE;
	private interface IColorPanel {
		public void render();
	}
	public static ColorEditor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ColorEditor();
		}
		return INSTANCE;
	}
	/**
	 * Class to represent an RGB or HSV Color. It can
	 * perform conversions between these two models
	 */
	private static class Color {
		public static final int HSV = 0;
		public static final int RGB = 1;
		public static final int R = 0;
		public static final int G = 1;
		public static final int B = 2;
		public static final int H = 0;
		public static final int S = 1;
		public static final int V = 2;
		private static final int[][] channelMaxs = {{359, 100, 100}, {255, 255, 255}};
		private static final String[][] channelNames = {{"H", "S", "V"}, {"R", "G", "B"}};
		private int channels[];
		private int space;
		
		public static Color fromRGB(int r, int g, int b) {
			Color color = new Color();
			color.channels[R] = r;
			color.channels[G] = g;
			color.channels[B] = b;
			color.space = RGB;
			return color;
		}
		
		public static Color fromHSV(int h, int s, int v) {
			Color color = new Color();
			color.channels[H] = h;
			color.channels[S] = s;
			color.channels[V] = v;
			color.space = HSV;
			return color;
		}
		
		public static Color fromRgbColor(OMRGBColor rgbColor) {
			Color color = new Color();
			color.channels[R] = (int)rgbColor.getRed().getFloatValue(OMCSSPrimitiveValue.CSS_NUMBER);
			color.channels[G] = (int)rgbColor.getGreen().getFloatValue(OMCSSPrimitiveValue.CSS_NUMBER);
			color.channels[B] = (int)rgbColor.getBlue().getFloatValue(OMCSSPrimitiveValue.CSS_NUMBER);
			color.space = RGB;
			return color;
		}
		
		public Color() {
			channels = new int[4];
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof Color) {
				Color c = (Color)o;
				return (channels[0] == c.channels[0]) && (channels[1] == c.channels[1]) && (channels[2] == c.channels[2]) && (space == c.space);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return channels[0] + 256 * channels[1] + 65536 * channels[2] + space * 16777216;
		}
		
		public int getChannel(int index) {
			return channels[index];
		}
		
		public void setChannel(int index, int value) {	
			channels[index] = value;
		}
		
		public static int getChannelMax(int space, int index) {
			return channelMaxs[space][index];
		}
		
		public static String getChannelName(int space, int index) {
			return channelNames[space][index];
		}

		public int getSpace() {
			return space;
		}

		public void setSpace(int space) {
			this.space = space;
		}
		
		public Color copyTo(Color c) {
			c.channels[0] = channels[0];
			c.channels[1] = channels[1];
			c.channels[2] = channels[2];
			c.space = space;
			return c;
		}
		
		public Color convertToColorSpace(int space, Color c) {
			if (this.space == space) {
				copyTo(c);
			} else if (space == HSV) {
				int max = (channels[R] > channels[G]) ? (channels[R] > channels[B] ? channels[R] : channels[B])  : (channels[G] > channels[B] ? channels[G] : channels[B]);
				int min = (channels[R] < channels[G]) ? (channels[R] < channels[B] ? channels[R] : channels[B])  : (channels[G] < channels[B] ? channels[G] : channels[B]);
				if (max != min) {
					if (max == channels[R]) {
						if (channels[1] >= channels[2]) {
							c.channels[H] = 60 * (channels[G] - channels[B]) / (max - min);
						} else {
							c.channels[H] = 60 * (channels[G] - channels[B]) / (max - min) + 360;
						}
					} else if (max == channels[1]) {
						c.channels[H] = 60 * (channels[B] - channels[R]) / (max - min) + 120;
					} else {
						c.channels[H] = 60 * (channels[R] - channels[G]) / (max - min) + 240;
					}	
				} else {
					c.channels[H] = 0;
				}
				c.channels[S] = (max == 0) ? 0 : (100 - 100 * min / max);
				c.channels[V] = max * 100 / 255;
				c.space = space;
			} else if (space == RGB) {
				int h = (channels[H] / 60);
				int p = (255 * channels[V] * (100 - channels[S])) / 10000;
				int q = (255 * channels[V] * (6000 - channels[S] * (channels[H] - 60 * h))) / 600000;
				int t = (255 * channels[V] * (6000 - channels[S] * (60 - (channels[H] - 60 * h)))) / 600000;
				switch(h) {
					case 0:
						c.channels[R] = channels[V] * 255 / 100;
						c.channels[G]  = t;
						c.channels[B]  = p;
						break;
					case 1:
						c.channels[R] = q;
						c.channels[G]  = channels[V] * 255 / 100;
						c.channels[B]  = p;
						break;
					case 2:
						c.channels[R] = p;
						c.channels[G]  = channels[V] * 255 / 100;
						c.channels[B]  = t;
						break;
					case 3:
						c.channels[R] = p;
						c.channels[G]  = q;
						c.channels[B]  = channels[V] * 255 / 100;
						break;
					case 4:
						c.channels[R] = t;
						c.channels[G]  = p;
						c.channels[B]  = channels[V] * 255 / 100;
						break;
					case 5:
						c.channels[R] = channels[V] * 255 / 100;
						c.channels[G] = p;
						c.channels[B]  = q;
						break;
				}
				c.space = space;
			} else {
				throw new IllegalArgumentException();
			}
			return c;
		}

		public static String convertToRgb(int space, int ix0, int v0, int ix1, int v1, int ix2, int v2) {
			if (ix0 == 1) {
				if (ix1 == 2) {
					v0 ^= v2;
					v2 ^= v0;
					v0 ^= v2;
					v1 ^= v2;
					v2 ^= v1;
					v1 ^= v2;
				} else {
					v0 ^= v1;
					v1 ^= v0;
					v0 ^= v1;
				}
			} else if (ix0 == 2) {
				if (ix1 == 0) {
					v0 ^= v1;
					v1 ^= v0;
					v0 ^= v1;
					v1 ^= v2;
					v2 ^= v1;
					v1 ^= v2;
				} else {
					v0 ^= v2;
					v2 ^= v0;
					v0 ^= v2;
				}
			} else {
				if (ix1 == 2) {
					v1 ^= v2;
					v2 ^= v1;
					v1 ^= v2;
				}
			}
			assert v0 <= channelMaxs[space][0];
			assert v1 <= channelMaxs[space][1];
			assert v2 <= channelMaxs[space][2];
			StringBuilder builder = new StringBuilder("rgb(");
			if (space == RGB) {
				builder.append(v0);
				builder.append(",");
				builder.append(v1);
				builder.append(",");
				builder.append(v2);
			} else {
				int h = (v0 / 60);
				int p = (255 * v2 * (100 - v1)) / 10000;
				int q = (255 * v2 * (6000 - v1 * (v0 - 60 * h))) / 600000;
				int t = (255 * v2 * (6000 - v1 * (60 - (v0 - 60 * h)))) / 600000;
				switch(h) {
					case 0:
						builder.append(v2 * 255 / 100);
						builder.append(",");
						builder.append(t);
						builder.append(",");
						builder.append(p);
						break;
					case 1:
						builder.append(q);
						builder.append(",");
						builder.append(v2 * 255 / 100);
						builder.append(",");
						builder.append(p);
						break;
					case 2:
						builder.append(p);
						builder.append(",");
						builder.append(v2 * 255 / 100);
						builder.append(",");
						builder.append(t);
						break;
					case 3:
						builder.append(p);
						builder.append(",");
						builder.append(q);
						builder.append(",");
						builder.append(v2 * 255 / 100);
						break;
					case 4:
						builder.append(t);
						builder.append(",");
						builder.append(p);
						builder.append(",");
						builder.append(v2 * 255 / 100);
						break;
					case 5:
						builder.append(v2 * 255 / 100);
						builder.append(",");
						builder.append(p);
						builder.append(",");
						builder.append(q);
						break;
				}
			}
			builder.append(")");
			return builder.toString();
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (space == RGB) {
				builder.append("rgb(");
				builder.append(channels[R]);
				builder.append(",");
				builder.append(channels[G]);
				builder.append(",");
				builder.append(channels[B]);
				builder.append(")");
			} else {
				builder.append("hsv(");
				builder.append(channels[H]);
				builder.append(",");
				builder.append(channels[S]);
				builder.append(",");
				builder.append(channels[V]);
				builder.append(")");
			}
			return builder.toString();
		}
	}
	/**
	 * Bilinear gradient editor. The vertical slider lets one vary
	 * the value of a color channel. The 2D bilinear gradient shows
	 * all the possible values attainable by varying the other two
	 * color channels.
	 * @author laaglu
	 */
	private class GradientPanel extends LayoutContainer implements IColorPanel {
		/**
		 * Bilinear gradient class
		 */
		private class Gradient2D extends Widget {
			private boolean mouseDown;
			private int hcount;
			private int vcount;
			private float subWidth;
			private float subHeight;
			private int valueChannel;
			private int gradientChannel1;
			private int gradientChannel2;
			private int space;
			private int value;
			private List<Element> subdivs;
			private Element posDiv;

			public Gradient2D(int hcount, int vcount) {
				super();
				this.hcount = hcount;
				this.vcount = vcount;

				Element div = DOM.createDiv();
				setElement(div);
				DOM.setStyleAttribute(div, "position", "relative");
				DOM.setStyleAttribute(div, "border", "1px solid black");
				subWidth = 100f / hcount;
				subHeight = 100f / vcount;
				subdivs = new ArrayList<Element>();
				for (int i = 0; i < vcount; i++) {
					for (int j = 0; j < hcount; j++) {
						Element subdiv = DOM.createDiv();
						DOM.setStyleAttribute(subdiv, "position", "absolute");
						DOM.setStyleAttribute(subdiv, "left", j * subWidth + "%");
						DOM.setStyleAttribute(subdiv, "top", i * subHeight + "%");
						// Increase size by 10% as the squares do not always
						// fully overlap due to numerical precision problems.
						DOM.setStyleAttribute(subdiv, "width", subWidth * 1.1f + "%");
						DOM.setStyleAttribute(subdiv, "height", subHeight * 1.1f + "%");
						DOM.appendChild(div, subdiv);
						subdivs.add(subdiv);
					}
				}
				posDiv = DOM.createDiv();
				DOM.setStyleAttribute(posDiv, "position", "absolute");
				DOM.setStyleAttribute(posDiv, "left", 0 + "%");
				DOM.setStyleAttribute(posDiv, "top", 0 + "%");
				DOM.setStyleAttribute(posDiv, "width", subWidth + "%");
				DOM.setStyleAttribute(posDiv, "height", subHeight + "%");
				DOM.setStyleAttribute(posDiv, "border", "3px double black");
				DOM.appendChild(div, posDiv);
				sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEMOVE | Event.ONMOUSEUP);
			}

			public void render() {
				int max1 = Color.getChannelMax(space, gradientChannel1);
				int max2 = Color.getChannelMax(space, gradientChannel2);
				for (int i = 0; i < vcount; i++) {
					for (int j = 0; j < hcount; j++) {
						Element subdiv =  subdivs.get(i * hcount + j);
						DOM.setStyleAttribute(subdiv, "backgroundColor", Color.convertToRgb(
								space, 
								valueChannel, value,
								gradientChannel1, max1 * j / (hcount - 1),
								gradientChannel2, max2 * i / (vcount - 1)));
					}
				}
				updatePos(
					colors[space].getChannel(gradientChannel1) * (hcount - 1) / max1,
					colors[space].getChannel(gradientChannel2) * (vcount - 1) / max2);
			}
			
			public void updatePos(int u, int v) {
				DOM.setStyleAttribute(posDiv, "left", u * subWidth + "%");
				DOM.setStyleAttribute(posDiv, "top", v * subHeight + "%");
			}

			public void setConfiguration(int space, int valueChannel, int gradientChannel1, int gradientChannel2) {
//				GWT.log("setConfiguration(" + space + ": " + valueChannel + ", " + gradientChannel1 + ", " + gradientChannel2 + ")");
				this.space = space;
				this.valueChannel = valueChannel;
				this.gradientChannel1 = gradientChannel1;
				this.gradientChannel2 = gradientChannel2;
			}

			public void setValue(int value) {
				this.value = value;
				render();
			}
		  
			@Override
			public void onBrowserEvent(Event event) {
				switch (DOM.eventGetType(event)) {
					case Event.ONMOUSEUP:
						mouseDown = false;
						break;
					case Event.ONMOUSEDOWN:
						mouseDown = true;
					case Event.ONMOUSEMOVE:
						if (mouseDown) {
							int width = getElement().getClientWidth();
							int height = getElement().getClientHeight();
//							GWT.log("w=" + width + " h=" + height);
							int x = DOM.eventGetClientX(event);
							x -= ColorEditor.getAbsoluteLeft(getElement());
							int y = DOM.eventGetClientY(event);
							y -= ColorEditor.getAbsoluteTop(getElement());
							int subWidth = width / hcount;
							int subHeight = height / vcount;
							int u = Math.min(x / subWidth, hcount - 1);
							int v = Math.min(y / subHeight, vcount - 1);
							updatePos(u,v);
//							GWT.log("u=" + u + " v=" + v);
							int a = (u * Color.getChannelMax(space, gradientChannel1)) / (hcount - 1);
							int b = (v * Color.getChannelMax(space, gradientChannel2)) / (vcount - 1);
							if ((a != colors[space].getChannel(gradientChannel1)) || (b != colors[space].getChannel(gradientChannel2))) {
								tmpColor.setSpace(space);
								tmpColor.setChannel(gradientChannel1, a);
								tmpColor.setChannel(gradientChannel2, b);
								tmpColor.setChannel(valueChannel, value);
								updateColor(tmpColor);
							}
						}
						break;
				}
				event.preventDefault();
				event.stopPropagation();
			}
		}

		/**
		 * Vertical color slider
		 * @author laaglu
		 */
		private class VColorSliderBar extends VSliderBar {
			private List<Element> subdivs;
			
			public VColorSliderBar(double minValue, double maxValue, int count) {
				super(minValue, maxValue, null, new AbstractImagePrototype[] {AbstractImagePrototype.create(AppBundle.INSTANCE.cvslider()), AbstractImagePrototype.create(AppBundle.INSTANCE.cvsliderSliding())}, "colorEditor-VSliderBar");
				subdivs = new ArrayList<Element>();
				for (int i = 0; i < count; i++) {
					Element subdiv = DOM.createDiv();
					DOM.setStyleAttribute(subdiv, "position", "absolute");
					subdivs.add(subdiv);					
				}
			}

			@Override
			public void onResize(int width, int height) {
				if (height > 0) {
				    // Center the line in the shell
				    int lineHeight = DOM.getElementPropertyInt(lineElement, "offsetHeight");
				    lineOffset = (height / 2) - (lineHeight / 2);
				    DOM.setStyleAttribute(lineElement, "top", lineOffset + "px");
		
					drawColors();
					drawKnob();
					if (sliderListeners != null) {
						sliderListeners.fireValueChanged(this, getCurrentValue());
					}
				}
			}

			private void drawColors() {
			    // Abort if not attached
				if (!isAttached()) {
					return;
				}

				// Draw the color subdivs
				int lineLeft = DOM.getElementPropertyInt(lineElement, "offsetLeft") + 1;
				int lineHeight = DOM.getElementPropertyInt(lineElement, "offsetHeight") - 2;
				int subWidth = DOM.getElementPropertyInt(lineElement, "offsetWidth") - 2;
				int subHeight = lineHeight / subdivs.size();
				// Create the ticks or make them visible
				for (int i = 0, count = subdivs.size(); i < count; i++) {
					Element subdiv = subdivs.get(i);
					if (subdiv.getParentNode() == null) {
						DOM.appendChild(getElement(), subdiv);
					}
					DOM.setStyleAttribute(subdiv, "top", (lineOffset + 1 + i * subHeight) + "px");
					DOM.setStyleAttribute(subdiv, "left", lineLeft + "px");
					DOM.setStyleAttribute(subdiv, "width", subWidth + "px");
					DOM.setStyleAttribute(subdiv, "height", ((i == count - 1) ? (subHeight + lineHeight % count) : subHeight) + "px");
				}
			}

			public List<Element> getSubdivs() {
				return subdivs;
			}
			
			@Override
			public void setMaxValue(double maxValue) {
				this.maxValue = maxValue;
			}
		}

		private VColorSliderBar slider;
		private Gradient2D gradient2D;
		private ToggleButton[] buttons;
		private Color tmpColor;

		public GradientPanel() {
			RowLayout rowLayout = new RowLayout(Orientation.HORIZONTAL);
			setLayout(rowLayout);
			tmpColor = Color.fromRGB(0, 0, 0);
			slider = new VColorSliderBar(0, Color.getChannelMax(Color.HSV, Color.H), 16);
			slider.setStepSize(1.0);
			slider.addSliderListener(new SliderListenerAdapter() {
				@Override
				public void onValueChanged(SliderBar slider, double curValue) {
					colors[gradient2D.space].copyTo(tmpColor);
					tmpColor.setChannel(gradient2D.valueChannel, (int)curValue);
					updateColor(tmpColor);
					gradient2D.setValue((int)curValue);
				}
			});
			gradient2D = new Gradient2D(16, 16);
			gradient2D.setConfiguration(Color.HSV, Color.H, Color.S, Color.V);
			buttons = new ToggleButton[6];
			LayoutContainer buttonContainer = new LayoutContainer();
			VBoxLayout vboxLayout = new VBoxLayout();
			vboxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.LEFT);
			buttonContainer.setLayout(vboxLayout);
			AppConstants constants = AppConstants.INSTANCE;
			String[] toolTips = { constants.hue(), constants.saturation(), constants.value(),
					constants.red(), constants.green(), constants.blue() };
			for (int i = 0; i < 6; i++) {
				buttons[i] = new ToggleButton(Color.getChannelName(i / 3, i % 3));
				buttons[i].setToggleGroup("hsvrgb");
				buttons[i].setToolTip(toolTips[i]);
				buttons[i].addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						GradientPanel.this.onClick((ToggleButton) ce.getButton());
					}
				});
				VBoxLayoutData vflex0 = new VBoxLayoutData(new Margins(0, 0, 5, 0));
				vflex0.setFlex(1);
				VBoxLayoutData vflex1 = new VBoxLayoutData(new Margins(0));
				vflex1.setFlex(1);
				buttonContainer.add(buttons[i], i < 5 ? vflex0 : vflex1);
			}
			buttons[0].toggle(true);
			add(buttonContainer, new RowData(30, 1, new Margins(3, 5, 0, 3)));
			add(slider, new RowData(30, 1, new Margins(0, 5, 0, 0)));
			add(gradient2D, new RowData(1, 1, new Margins(0)));
		}
		
		public void onClick(ToggleButton sender) {
			for (int i = 0, isize = buttons.length; i < isize; i ++) {
				if (buttons[i] == sender) {
					if (buttons[i].isPressed()) {
						int space = i / 3;
						int sliderChannel = i % 3;
						int gradientChannel1 = (i + 1) % 3;
						int gradientChannel2 = (i + 2) % 3;
						slider.setMaxValue(Color.getChannelMax(space, sliderChannel));
						slider.setCurrentValue(colors[space].getChannel(sliderChannel), false);
						gradient2D.setConfiguration(space, sliderChannel, gradientChannel1, gradientChannel2);
						gradient2D.setValue(colors[space].getChannel(sliderChannel));
					}
				}
			}
			renderSlider();
		}
		
		private void renderSlider() {
			for (int i = 0, isize = buttons.length; i < isize; i ++) {
				if (buttons[i].isPressed()) {
					// Select the new button
					int space = i / 3;
					int sliderChannel = i % 3;
					int gradientChannel1 = (i + 1) % 3;
					int gradientChannel2 = (i + 2) % 3;
					int max = Color.getChannelMax(space, sliderChannel);
					
					List<Element> subdivs = slider.getSubdivs();
					for (int j = 0, jsize = subdivs.size(); j < jsize; j++) {
						Element subdiv = subdivs.get(j);
						DOM.setStyleAttribute(subdiv, "backgroundColor", Color.convertToRgb(
							i / 3, 
							sliderChannel, max * j / (subdivs.size() - 1), 
							gradientChannel1, i < 2 ? Color.getChannelMax(space, gradientChannel1) : 0, 
							gradientChannel2, i < 2 ? Color.getChannelMax(space, gradientChannel2) : 0));
					}
				}
			}
		}

		@Override
		public void render() {
			GWT.log("GradientPanel.render");
			slider.setCurrentValue(colors[gradient2D.space].getChannel(gradient2D.valueChannel));
			renderSlider();
		}
	}
	
	private class ValuePanel extends LayoutContainer implements IColorPanel {
		/**
		 * Horizontal color slider
		 * @author laaglu
		 */
		class HColorSliderBar extends HSliderBar {
			private List<Element> subdivs;
			
			public HColorSliderBar(double minValue, double maxValue, int count) {
				super(minValue, maxValue, null, new AbstractImagePrototype[] {AbstractImagePrototype.create(AppBundle.INSTANCE.chslider()), AbstractImagePrototype.create(AppBundle.INSTANCE.chsliderSliding())}, "colorEditor-HSliderBar");
				subdivs = new ArrayList<Element>();
				for (int i = 0; i < count; i++) {
					Element subdiv = DOM.createDiv();
					DOM.setStyleAttribute(subdiv, "position", "absolute");
					subdivs.add(subdiv);					
				}
			}

			@Override
			public void onResize(int width, int height) {
				if (width > 0) {
					// Center the line in the shell
					int lineWidth = DOM.getElementPropertyInt(lineElement, "offsetWidth");
					lineOffset = (width / 2) - (lineWidth / 2);
					DOM.setStyleAttribute(lineElement, "left", lineOffset + "px");
		
					drawColors();
					drawKnob();
				}
			}
			
			private void drawColors() {
			    // Abort if not attached
				if (!isAttached()) {
					return;
				}

				// Draw the color subdivs
				int lineWidth = DOM.getElementPropertyInt(lineElement, "offsetWidth") - 2;
				int lineTop = DOM.getElementPropertyInt(lineElement, "offsetTop") + 1;
				int subWidth = lineWidth / subdivs.size();
				int subHeight = DOM.getElementPropertyInt(lineElement, "offsetHeight") - 2;
				// Create the ticks or make them visible
				for (int i = 0, count = subdivs.size(); i < count; i++) {
					Element subdiv = subdivs.get(i);
					if (subdiv.getParentNode() == null) {
						DOM.appendChild(getElement(), subdiv);
					}
					DOM.setStyleAttribute(subdiv, "left", (lineOffset + 1 + i * subWidth) + "px");
					DOM.setStyleAttribute(subdiv, "top", lineTop + "px");
					DOM.setStyleAttribute(subdiv, "width", ((i == count - 1) ? (subWidth + lineWidth % count) : subWidth) + "px");
					DOM.setStyleAttribute(subdiv, "height", subHeight + "px");
				}
			}

			public List<Element> getSubdivs() {
				return subdivs;
			}
		}
		private HColorSliderBar[] sliders;
		private TextField[] textFields;
		protected Color tmpColor;
		private SliderListener sliderListener = new SliderListenerAdapter() {
			@Override
			public void onValueChanged(SliderBar slider, double curValue) {
				if (!eventsDisabled) {
					eventsDisabled = true;
					for (int i = 0; i < 6; i++) {
						if (slider == sliders[i]) {
							int space = i / 3;
							colors[space].copyTo(tmpColor);
							tmpColor.setChannel(i % 3, (int)curValue);
							updateColor(tmpColor);
							render();
							break;
						}
					}				
					eventsDisabled = false;
				}
			}
		};
		
		public ValuePanel() {
			VBoxLayout vboxLayout = new VBoxLayout();
			vboxLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);  
	        setLayout(vboxLayout);
	        VBoxLayoutData vflex0 = new VBoxLayoutData(new Margins(0, 0, 5, 0));  
	        vflex0.setFlex(1);
	        VBoxLayoutData vflex1 = new VBoxLayoutData(new Margins(0));  
	        vflex1.setFlex(1);
			tmpColor = Color.fromRGB(0, 0, 0);
			sliders = new HColorSliderBar[6];
			textFields = new TextField[6];
			for (int i = 0; i < 6; i++) {
				LayoutContainer rowContainer = new LayoutContainer();
				RowLayout rowLayout = new RowLayout(Orientation.HORIZONTAL);
				rowContainer.setLayout(rowLayout);
				sliders[i] = new HColorSliderBar(0, Color.getChannelMax(i / 3, i % 3), 16);
				sliders[i].setStepSize(1.0);
				sliders[i].setCurrentValue(0.0);
				sliders[i].addSliderListener(sliderListener);
				sliders[i].setTabIndex(2 * i + 2);
				textFields[i] = new TextField<String>();
				textFields[i].setMaxLength(3);
				textFields[i].setWidth("40px");
				textFields[i].addListener(Events.Change, new Listener<FieldEvent>() {
					@Override
					public void handleEvent(FieldEvent be) {
						TextField<String> sender = (TextField<String>) be.getField();
						if (!eventsDisabled) {
							eventsDisabled = true;
							try {
								int value = Integer.parseInt(sender.getValue());
								for (int i = 0; i < 6; i++) {
									if (sender == textFields[i]) {
										if ((0 <= value) && (value <= Color.getChannelMax(i / 3, i % 3))) {
											int space = i / 3;
											int nspace = (space == Color.HSV) ? Color.RGB : Color.HSV;
											colors[space].setChannel(i % 3, value);
											colors[space].convertToColorSpace(nspace, colors[nspace]);
										}
										break;
									}
								}
							} catch(NumberFormatException e) {
							}
							render();
							eventsDisabled = false;
						}

					}
				});
				textFields[i].setTabIndex(2 * i + 1);
				rowContainer.add(new Label(Color.getChannelName(i / 3, i % 3)), new RowData(-1, -1, new Margins(4, 5, 4, 4)));
				rowContainer.add(textFields[i], new RowData(-1, -1, new Margins(0, 5, 0, 0)));
				rowContainer.add(sliders[i], new RowData(1, -1, new Margins(0)));
				add(rowContainer, i < 5 ? vflex0 : vflex1);
			}
			render();
		}

		@Override
		public void render() {
			GWT.log("ValuePanel.render");
			eventsDisabled = true;
			for (int i = 0; i < 6; i++) {
				sliders[i].setCurrentValue(colors[i / 3].getChannel(i % 3));
				textFields[i].setValue(Integer.toString(colors[i / 3].getChannel(i % 3)));

				// Update the slider bar colors
				List<Element> subdivs = sliders[i].getSubdivs();
				int max = Color.getChannelMax(i / 3, i % 3);
				for (int j = 0, size = subdivs.size(); j < size; j++) {
					Element subdiv = subdivs.get(j);
					DOM.setStyleAttribute(subdiv, "backgroundColor", Color.convertToRgb(
							i / 3, 
							(i + 1) % 3, colors[i / 3].getChannel((i + 1) % 3), 
							(i + 2) % 3, colors[i / 3].getChannel((i + 2) % 3), 
							i % 3, max * j / (subdivs.size() - 1)));
				}
			}
			eventsDisabled = false;
		}
	}

	public static class NamedColor implements BeanModelTag {
		private String colorName;
		public static NamedColor createColor(String colorName) {
			NamedColor namedColor = new NamedColor();
			namedColor.colorName = colorName;
			return namedColor;
		}
		public NamedColor() {
		}
		public String getColorName() {
			return colorName;
		}
	}

	private class NamedColorsPanel extends LayoutContainer {
		
		/**
		 * The list view
		 */
		private ListView<BeanModel> view;
		/**
		 * A store from which the list view fetches
		 * NamedColor records
		 */
		private ListStore<BeanModel> store;
		protected Color tmpColor;

		public NamedColorsPanel() {
			tmpColor = Color.fromRGB(0, 0, 0);
		    setLayout(new FitLayout());
			BeanModelFactory beanFactory = BeanModelLookup.get().getFactory(NamedColor.class);
			store = new ListStore<BeanModel>();
			String[] svgColorNames = {
				SVGConstants.CSS_ALICEBLUE_VALUE,
				SVGConstants.CSS_ANTIQUEWHITE_VALUE,
				SVGConstants.CSS_AQUA_VALUE,
				SVGConstants.CSS_AQUAMARINE_VALUE,
				SVGConstants.CSS_AZURE_VALUE,
				SVGConstants.CSS_BEIGE_VALUE,
				SVGConstants.CSS_BISQUE_VALUE,
				SVGConstants.CSS_BLACK_VALUE,
				SVGConstants.CSS_BLANCHEDALMOND_VALUE,
				SVGConstants.CSS_BLUE_VALUE,
				SVGConstants.CSS_BLUEVIOLET_VALUE,
				SVGConstants.CSS_BROWN_VALUE,
				SVGConstants.CSS_BURLYWOOD_VALUE,
				SVGConstants.CSS_CADETBLUE_VALUE,
				SVGConstants.CSS_CHARTREUSE_VALUE,
				SVGConstants.CSS_CHOCOLATE_VALUE,
				SVGConstants.CSS_CORAL_VALUE,
				SVGConstants.CSS_CORNFLOWERBLUE_VALUE,
				SVGConstants.CSS_CORNSILK_VALUE,
				SVGConstants.CSS_CRIMSON_VALUE,
				SVGConstants.CSS_CYAN_VALUE,
				SVGConstants.CSS_DARKBLUE_VALUE,
				SVGConstants.CSS_DARKCYAN_VALUE,
				SVGConstants.CSS_DARKGOLDENROD_VALUE,
				SVGConstants.CSS_DARKGRAY_VALUE,
				SVGConstants.CSS_DARKGREEN_VALUE,
				SVGConstants.CSS_DARKGREY_VALUE,
				SVGConstants.CSS_DARKKHAKI_VALUE,
				SVGConstants.CSS_DARKMAGENTA_VALUE,
				SVGConstants.CSS_DARKOLIVEGREEN_VALUE,
				SVGConstants.CSS_DARKORANGE_VALUE,
				SVGConstants.CSS_DARKORCHID_VALUE,
				SVGConstants.CSS_DARKRED_VALUE,
				SVGConstants.CSS_DARKSALMON_VALUE,
				SVGConstants.CSS_DARKSEAGREEN_VALUE,
				SVGConstants.CSS_DARKSLATEBLUE_VALUE,
				SVGConstants.CSS_DARKSLATEGRAY_VALUE,
				SVGConstants.CSS_DARKSLATEGREY_VALUE,
				SVGConstants.CSS_DARKTURQUOISE_VALUE,
				SVGConstants.CSS_DARKVIOLET_VALUE,
				SVGConstants.CSS_DEEPPINK_VALUE,
				SVGConstants.CSS_DEEPSKYBLUE_VALUE,
				SVGConstants.CSS_DIMGRAY_VALUE,
				SVGConstants.CSS_DIMGREY_VALUE,
				SVGConstants.CSS_DODGERBLUE_VALUE,
				SVGConstants.CSS_FIREBRICK_VALUE,
				SVGConstants.CSS_FLORALWHITE_VALUE,
				SVGConstants.CSS_FORESTGREEN_VALUE,
				SVGConstants.CSS_FUCHSIA_VALUE,
				SVGConstants.CSS_GAINSBORO_VALUE,
				SVGConstants.CSS_GHOSTWHITE_VALUE,
				SVGConstants.CSS_GOLD_VALUE,
				SVGConstants.CSS_GOLDENROD_VALUE,
				SVGConstants.CSS_GRAY_VALUE,
				SVGConstants.CSS_GREY_VALUE,
				SVGConstants.CSS_GREEN_VALUE,
				SVGConstants.CSS_GREENYELLOW_VALUE,
				SVGConstants.CSS_HONEYDEW_VALUE,
				SVGConstants.CSS_HOTPINK_VALUE,
				SVGConstants.CSS_INDIANRED_VALUE,
				SVGConstants.CSS_INDIGO_VALUE,
				SVGConstants.CSS_IVORY_VALUE,
				SVGConstants.CSS_KHAKI_VALUE,
				SVGConstants.CSS_LAVENDER_VALUE,
				SVGConstants.CSS_LAVENDERBLUSH_VALUE,
				SVGConstants.CSS_LAWNGREEN_VALUE,
				SVGConstants.CSS_LEMONCHIFFON_VALUE,
				SVGConstants.CSS_LIGHTBLUE_VALUE,
				SVGConstants.CSS_LIGHTCORAL_VALUE,
				SVGConstants.CSS_LIGHTCYAN_VALUE,
				SVGConstants.CSS_LIGHTGOLDENRODYELLOW_VALUE,
				SVGConstants.CSS_LIGHTGRAY_VALUE,
				SVGConstants.CSS_LIGHTGREEN_VALUE,
				SVGConstants.CSS_LIGHTGREY_VALUE,
				SVGConstants.CSS_LIGHTPINK_VALUE,
				SVGConstants.CSS_LIGHTSALMON_VALUE,
				SVGConstants.CSS_LIGHTSEAGREEN_VALUE,
				SVGConstants.CSS_LIGHTSKYBLUE_VALUE,
				SVGConstants.CSS_LIGHTSLATEGRAY_VALUE,
				SVGConstants.CSS_LIGHTSLATEGREY_VALUE,
				SVGConstants.CSS_LIGHTSTEELBLUE_VALUE,
				SVGConstants.CSS_LIGHTYELLOW_VALUE,
				SVGConstants.CSS_LIME_VALUE,
				SVGConstants.CSS_LIMEGREEN_VALUE,
				SVGConstants.CSS_LINEN_VALUE,
				SVGConstants.CSS_MAGENTA_VALUE,
				SVGConstants.CSS_MAROON_VALUE,
				SVGConstants.CSS_MEDIUMAQUAMARINE_VALUE,
				SVGConstants.CSS_MEDIUMBLUE_VALUE,
				SVGConstants.CSS_MEDIUMORCHID_VALUE,
				SVGConstants.CSS_MEDIUMPURPLE_VALUE,
				SVGConstants.CSS_MEDIUMSEAGREEN_VALUE,
				SVGConstants.CSS_MEDIUMSLATEBLUE_VALUE,
				SVGConstants.CSS_MEDIUMSPRINGGREEN_VALUE,
				SVGConstants.CSS_MEDIUMTURQUOISE_VALUE,
				SVGConstants.CSS_MEDIUMVIOLETRED_VALUE,
				SVGConstants.CSS_MIDNIGHTBLUE_VALUE,
				SVGConstants.CSS_MINTCREAM_VALUE,
				SVGConstants.CSS_MISTYROSE_VALUE,
				SVGConstants.CSS_MOCCASIN_VALUE,
				SVGConstants.CSS_NAVAJOWHITE_VALUE,
				SVGConstants.CSS_NAVY_VALUE,
				SVGConstants.CSS_OLDLACE_VALUE,
				SVGConstants.CSS_OLIVE_VALUE,
				SVGConstants.CSS_OLIVEDRAB_VALUE,
				SVGConstants.CSS_ORANGE_VALUE,
				SVGConstants.CSS_ORANGERED_VALUE,
				SVGConstants.CSS_ORCHID_VALUE,
				SVGConstants.CSS_PALEGOLDENROD_VALUE,
				SVGConstants.CSS_PALEGREEN_VALUE,
				SVGConstants.CSS_PALETURQUOISE_VALUE,
				SVGConstants.CSS_PALEVIOLETRED_VALUE,
				SVGConstants.CSS_PAPAYAWHIP_VALUE,
				SVGConstants.CSS_PEACHPUFF_VALUE,
				SVGConstants.CSS_PERU_VALUE,
				SVGConstants.CSS_PINK_VALUE,
				SVGConstants.CSS_PLUM_VALUE,
				SVGConstants.CSS_POWDERBLUE_VALUE,
				SVGConstants.CSS_PURPLE_VALUE,
				SVGConstants.CSS_RED_VALUE,
				SVGConstants.CSS_ROSYBROWN_VALUE,
				SVGConstants.CSS_ROYALBLUE_VALUE,
				SVGConstants.CSS_SADDLEBROWN_VALUE,
				SVGConstants.CSS_SALMON_VALUE,
				SVGConstants.CSS_SANDYBROWN_VALUE,
				SVGConstants.CSS_SEAGREEN_VALUE,
				SVGConstants.CSS_SEASHELL_VALUE,
				SVGConstants.CSS_SIENNA_VALUE,
				SVGConstants.CSS_SILVER_VALUE,
				SVGConstants.CSS_SKYBLUE_VALUE,
				SVGConstants.CSS_SLATEBLUE_VALUE,
				SVGConstants.CSS_SLATEGRAY_VALUE,
				SVGConstants.CSS_SLATEGREY_VALUE,
				SVGConstants.CSS_SNOW_VALUE,
				SVGConstants.CSS_SPRINGGREEN_VALUE,
				SVGConstants.CSS_STEELBLUE_VALUE,
				SVGConstants.CSS_TAN_VALUE,
				SVGConstants.CSS_TEAL_VALUE,
				SVGConstants.CSS_THISTLE_VALUE,
				SVGConstants.CSS_TOMATO_VALUE,
				SVGConstants.CSS_TURQUOISE_VALUE,
				SVGConstants.CSS_VIOLET_VALUE,
				SVGConstants.CSS_WHEAT_VALUE,
				SVGConstants.CSS_WHITE_VALUE,
				SVGConstants.CSS_WHITESMOKE_VALUE,
				SVGConstants.CSS_YELLOW_VALUE,	
			};
			for( String svgColorName : svgColorNames) {
				store.add(beanFactory.createModel(NamedColor.createColor(svgColorName)));
			}
			view = new ListView<BeanModel>();
		    view.setTemplate(getTemplate());
		    view.setStore(store);
		    view.setItemSelector("table.named-color");
		    view.getSelectionModel().addListener(Events.SelectionChange,
	            new Listener<SelectionChangedEvent<BeanModel>>() {
	              public void handleEvent(SelectionChangedEvent<BeanModel> be) {
	            	  BeanModel beanModel = be.getSelectedItem();
	            	  if (beanModel != null) {
	            		  NamedColor namedColor = (NamedColor)beanModel.getBean();
	            		  OMSVGPaint paint = OMSVGParser.parsePaint(namedColor.getColorName());
	            		  tmpColor.setChannel(Color.R, (int) paint.getRgbColor().getRed().getFloatValue(OMCSSPrimitiveValue.CSS_NUMBER));
	            		  tmpColor.setChannel(Color.G, (int) paint.getRgbColor().getGreen().getFloatValue(OMCSSPrimitiveValue.CSS_NUMBER));
	            		  tmpColor.setChannel(Color.B, (int) paint.getRgbColor().getBlue().getFloatValue(OMCSSPrimitiveValue.CSS_NUMBER));
	            		  updateColor(tmpColor);
	            	  }
	              }
	        });
		    add(view);
		}
		
		private native String getTemplate() /*-{
			return ['<tpl for=".">',
			'<table class="named-color"><tbody><tr>',
			'<td><div style="background-color:{colorName};"></td>',
			'<td>{colorName}</td>',
			'</tr></tbody></table>',
			'</tpl>',
			'<div class="x-clear"></div>'].join("");
		}-*/;

	}

	/**
	 * To avoid firing event when setting the color editor value
	 */
	private boolean eventsDisabled;
	/**
	 * Two colors to keep track of the (hsv) and (rgb) values
	 */
	private Color[] colors;
	/**
	 * A tab panel
	 */
	private TabPanel tabPanel;
	
	public ColorEditor()  {
		super();
		
		AppConstants constants = AppConstants.INSTANCE;
        int w = 320, h = 300;
        setMinWidth(w);
        setMinHeight(h);
        setWidth(w);
        setHeight(h);
	    setPlain(true);  
	    setModal(true);  
	    setBlinkModal(true);
	    setHeading(constants.colorEditor());  
	    setLayout(new FitLayout());  
        setResizable(true);

		colors = new Color[2];
		colors[Color.HSV] = Color.fromHSV(0, 0, 0);
		colors[Color.RGB] = Color.fromRGB(0, 0, 0);
    
	    TabItem gradientsTab = new TabItem(constants.gradients());
	    gradientsTab.setLayout(new FitLayout());
	    gradientsTab.add(new GradientPanel());  
	  
	    TabItem valuesTab = new TabItem(constants.values());  
	    valuesTab.setLayout(new FitLayout());
	    valuesTab.add(new ValuePanel());
	    
	    TabItem namesTab = new TabItem(constants.names());
	    namesTab.setLayout(new FitLayout());
	    namesTab.add(new NamedColorsPanel());

	    tabPanel = new TabPanel();  
	    tabPanel.setBorders(false);
	    tabPanel.add(valuesTab);  
	    tabPanel.add(gradientsTab);  
	    tabPanel.add(namesTab);
	    tabPanel.addListener(Events.BeforeSelect, new Listener<TabPanelEvent>() {
			@Override
			public void handleEvent(TabPanelEvent pe) {
				Component c = pe.getItem().getItem(0);
				if (c instanceof IColorPanel) {
					((IColorPanel)c).render();
				}
			}	    	
	    });
	  
	    add(tabPanel, new FitData(4));
	    
		addButton(new Button(constants.closeButton(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
				CloseEvent.<ColorEditor>fire(ColorEditor.this, ColorEditor.this);
			}
		}));
	}

	private void updateColor(Color color) {
		int space = color.getSpace();
		int nspace = (space == Color.HSV) ? Color.RGB : Color.HSV;
		color.copyTo(colors[space]);
		colors[space].convertToColorSpace(nspace, colors[nspace]);
		ValueChangeEvent.fire(this, getPaint());
	}
	
	public void setPaint(OMSVGPaint paint) {
		updateColor(Color.fromRgbColor(paint.getRgbColor()));
		Component c = tabPanel.getItem(0).getItem(0);
		if (c instanceof IColorPanel) {
			((IColorPanel)c).render();
		}
	}
	
	public OMSVGPaint getPaint() {
		return OMSVGParser.parsePaint(colors[Color.RGB].toString());
	}
	
    public static native int getAbsoluteTop(Element elem) /*-{
	    var top = 0;
	    var curr = elem;
	    // This intentionally excludes body which has a null offsetParent.    
	    while (curr) {
	      top -= curr.scrollTop;
	      curr = curr.offsetParent;
	    }
	    while (elem) {
	      top += elem.offsetTop;
	      elem = elem.offsetParent;
	    }
	    return top;
	}-*/;
	  
	public static native int getAbsoluteLeft(Element elem) /*-{
	    var left = 0;
	    var curr = elem;
	    // This intentionally excludes body which has a null offsetParent.    
	    while (curr) {
	      left -= curr.scrollLeft;
	      curr = curr.offsetParent;
	    }
	    while (elem) {
	      left += elem.offsetLeft;
	      elem = elem.offsetParent;
	    }
	    return left;
	}-*/;

	///////////////////////////////////////////////////
	// Event management
	///////////////////////////////////////////////////

	@Override
	public void fireEvent(GwtEvent<?> event) {
		SvgrealApp.getApp().getEventBus().fireEventFromSource(event, this);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<OMSVGPaint> handler) {
		return SvgrealApp.getApp().getEventBus().addHandlerToSource(ValueChangeEvent.getType(), this, handler);
	}

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<ColorEditor> handler) {
		return SvgrealApp.getApp().getEventBus().addHandlerToSource(CloseEvent.getType(), this, handler);
	}
	
	@Override
	public void show() {
		super.show();
		tabPanel.setSelection(tabPanel.getItem(0));
		layout(true);
	}
}
