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

import org.vectomatic.dnd.DataTransferExt;
import org.vectomatic.dnd.DropPanel;
import org.vectomatic.file.ErrorCode;
import org.vectomatic.file.File;
import org.vectomatic.file.FileError;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import org.vectomatic.svg.edit.client.AppBundle;
import org.vectomatic.svg.edit.client.VectomaticApp2;
import org.vectomatic.svg.edit.client.model.ModelConstants;
import org.vectomatic.svg.edit.client.model.svg.SVGImageElementModel;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;

/**
 * Field subclass to edit SVG image xlink:href values.
 * This field is used both by the image inspector and the
 * add new image command.
 * @author laaglu
 */
public class ImageHrefField extends AdapterField implements HasValueChangeHandlers<Size> {
	private static final String ATT_ACCEPT = "accept";
	private static final String ATT_ACCEPT_YES = "yes";
	private static final String ATT_ACCEPT_NO = "no";
	
	private class ImageHrefPanel extends LayoutContainer {
		/**
		 * Card layout to alternate between external
		 * and embedded panel (managed by radio buttons)
		 */
		private CardLayout cardLayout1;
		/**
		 * Card layout to alternate between status panel
		 * and error panel
		 */
		private CardLayout cardLayout2;
		/**
		 * Radio button to select an external URL
		 */
		private Radio externalRadio;
		/**
		 * Radio button to select an embedded data url
		 */
		private Radio embeddedRadio;
		/**
		 * Panel grouping widgets to edit an external URL
		 */
		private LayoutContainer externalPanel;
		/**
		 * Panel grouping widgets to edit an embedded URL
		 */
		private LayoutContainer embeddedPanel;
		/**
		 * Panel to give info on a loaded image
		 */
		private LayoutContainer statusPanel;
		/**
		 * Panel to give error info on a image which could
		 * not be loaded
		 */
		private LayoutContainer errorPanel;
		/**
		 * A textfield to specify external urls
		 */
		private TextField<String> urlField;
		/**
		 * A file upload dialog
		 */
		private FileUploadExt fileUploadExt;
		/**
		 * Name of the resource used to initialize this field (url
		 * or file name).
		 */
		private String resourceName;
		/**
		 * A file reader object
		 */
		private FileReader reader;
		/**
		 * The hidden image used to determine the native image size
		 */
		private Image hiddenImage;
		/**
		 * Label to display the size of the original image
		 */
		private Label sizeLabel;
		/**
		 * Label to display error messages
		 */
		private Label errorLabel;
		/**
		 * The bitmap width and height
		 */
		private int bitmapWidth, bitmapHeight;
		
		public ImageHrefPanel() {
			ModelConstants constants = ModelConstants.INSTANCE;

			/*==============================================
			 * External URL pane
			 *==============================================*/
			
			Label externalLabel = new Label(constants.url());
			
			urlField = new TextField<String>();
			urlField.setToolTip(constants.urlTooltip());
			urlField.setFireChangeEventOnSetValue(true);
			urlField.addListener(Events.Change, new Listener<FieldEvent>() {
				@Override
				public void handleEvent(FieldEvent be) {
					String value = urlField.getValue();
	 				resourceName = value;
	 				if (value != null && value.length() > 0) {
	 					hiddenImage.setUrl(value);
	 				} else {
	 					reportNull();
	 				}
	 				setValue(value, false);
				}
			});
			
			externalPanel = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
			externalPanel.add(externalLabel, new RowData(.10, 1, new Margins(0, 5, 0, 5)));
			externalPanel.add(urlField, new RowData(.90, 1, new Margins(0, 5, 0, 0)));
			
			/*==============================================
			 * Embedded Image pane
			 *==============================================*/
			final DropPanel dropArea = new DropPanel();
			dropArea.addDragEnterHandler(new DragEnterHandler() {	
				@Override
				public void onDragEnter(DragEnterEvent event) {
					dropArea.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_YES);
					event.stopPropagation();
					event.preventDefault();
				}
			});
			dropArea.addDragLeaveHandler(new DragLeaveHandler() {
				@Override
				public void onDragLeave(DragLeaveEvent event) {
					dropArea.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_NO);
					event.stopPropagation();
					event.preventDefault();
				}
			});
			dropArea.addDragOverHandler(new DragOverHandler() {
				@Override
				public void onDragOver(DragOverEvent event) {
					event.stopPropagation();
					event.preventDefault();
				}
			});
			dropArea.addDropHandler(new DropHandler() {
				@Override
				public void onDrop(DropEvent event) {
					dropArea.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_NO);
					processFiles(event.getDataTransfer().<DataTransferExt>cast().getFiles());
					event.stopPropagation();
					event.preventDefault();
				}
			});
			Document document = Document.get();
			DivElement div = document.createDivElement();
			Text text = document.createTextNode(constants.dropPanelText());
			div.appendChild(text);
			dropArea.getElement().appendChild(div);
			dropArea.setStyleName(AppBundle.INSTANCE.css().imageHrefDropArea());
			dropArea.getElement().setAttribute(ATT_ACCEPT, ATT_ACCEPT_NO);
			
			fileUploadExt = new FileUploadExt();
			fileUploadExt.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			fileUploadExt.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					processFiles(fileUploadExt.getFiles());
				}
			});
			Button openButton = new Button(constants.openLocalImageButton());
			openButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					fileUploadExt.click();	
				}
			});
			
			embeddedPanel = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
			embeddedPanel.add(dropArea, new RowData(.85, 1, new Margins(0, 5, 0, 5)));
			embeddedPanel.add(openButton, new RowData(.15, 1, new Margins(0, 5, 0, 0)));

			/*==============================================
			 * Status panel
			 *==============================================*/
			statusPanel = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
			sizeLabel = new Label();
			Button resetButton = new Button(constants.resetHrefButton());
			resetButton.setToolTip(constants.resetHrefTooltip());
			statusPanel.add(sizeLabel, new RowData(.85, 1, new Margins(0, 5, 0, 5)));
			statusPanel.add(resetButton, new RowData(.15, 1, new Margins(0, 5, 0, 0)));
			resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					ValueChangeEvent.fire(ImageHrefField.this, new Size(bitmapWidth, bitmapHeight));
				}
			});

			/*==============================================
			 * Error panel
			 *==============================================*/
			errorPanel = new LayoutContainer(new FitLayout());
			errorLabel = new Label();
			errorPanel.add(errorLabel);

			/*==============================================
			 * Main panel
			 *==============================================*/
			externalRadio = new Radio();
			externalRadio.setBoxLabel(constants.externalRadio());
			externalRadio.setFireChangeEventOnSetValue(true);
			embeddedRadio = new Radio();
			embeddedRadio.setBoxLabel(constants.embeddedRadio());
			embeddedRadio.setFireChangeEventOnSetValue(true);
			
			RadioGroup radioGroup = new RadioGroup(constants.dropPanelText());
			radioGroup.add(externalRadio);
			radioGroup.add(embeddedRadio);
			radioGroup.setSelectionRequired(true);
			radioGroup.addListener(Events.Change, new Listener<FieldEvent>() {
				@Override
				public void handleEvent(FieldEvent be) {
					cardLayout1.setActiveItem(isExternal() ? externalPanel : embeddedPanel);				
				}				
			});
			
			LayoutContainer cardPanel1 = new LayoutContainer();
			cardLayout1 = new CardLayout();
			cardPanel1.setHeight(25);
			cardPanel1.setLayout(cardLayout1);
			cardPanel1.add(externalPanel);
			cardPanel1.add(embeddedPanel);

			LayoutContainer cardPanel2 = new LayoutContainer();
			cardLayout2 = new CardLayout();
			cardPanel2.setHeight(25);
			cardPanel2.setLayout(cardLayout2);
			cardPanel2.add(statusPanel);
			cardPanel2.add(errorPanel);
			
			hiddenImage = new Image();
			hiddenImage.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			hiddenImage.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					String w = hiddenImage.getElement().getAttribute("width");
//					GWT.log("++++++ hiddenImage.load w=" + w);
					bitmapWidth = hiddenImage.getWidth();
					bitmapHeight = hiddenImage.getHeight();
					sizeLabel.setText(ModelConstants.INSTANCE.originalSizeLabel() + ": " + bitmapWidth + "x" + bitmapHeight);
					cardLayout2.setActiveItem(statusPanel);
				}
			});
			hiddenImage.addErrorHandler(new ErrorHandler() {		
				@Override
				public void onError(ErrorEvent event) {
//					GWT.log("++++++ hiddenImage.error");
					reportError(null);
				}
			});
			
			setLayout(new RowLayout(Orientation.VERTICAL));
			add(radioGroup, new RowData(1, -1, new Margins(5, 5, 0, 5)));
			add(cardPanel1, new RowData(1, -1, new Margins(0, 0, 5, 0)));	
			add(cardPanel2, new RowData(1, -1));
			add(hiddenImage);
			add(fileUploadExt);
			setBorders(true);
			setHeight(90);
		}
		
		public boolean isExternal() {
			return externalRadio.getValue();
		}
		
		public void update(String value) {
//			GWT.log("ImageHrefField.update(" + value + ")");
			if (SVGImageElementModel.isDataUrl(value)) {
				if (!embeddedRadio.getValue()) {
					embeddedRadio.setValue(true);
				}
				hiddenImage.setUrl(value);
			} else if (value != null && value.length() > 0) {
				if (!externalRadio.getValue()) {
					externalRadio.setValue(true);
				}
				if (!Util.equalWithNull(value, urlField.getValue())) {
					urlField.setFireChangeEventOnSetValue(false);
					urlField.setValue(value);
					urlField.setFireChangeEventOnSetValue(true);
				}
				hiddenImage.setUrl(value);
			} else {
				reportNull();
			}
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
								reportError(errorDesc);
	 							setValue(null, false);
							}
		 				});
		 				reader.addLoadEndHandler(new LoadEndHandler() {
		 					
		 					@Override
		 					public void onLoadEnd(LoadEndEvent event) {
		 						if (reader.getError() == null) {
			 						try {
			 							String result = reader.getStringResult();
			 							String url = "data:" + type + ";base64," + base64encode(result);
			 							setValue(url, false);
			 						} catch(Throwable t) {
			 							reportError(t.getMessage());
			 							setValue(null, false);
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
						reportError(t.getMessage());
						setValue(null, false);
	 				}
	 				break;
				}
			}
		}
		
		public void reportError(String message) {
//			GWT.log("++++++ ImageStatusPanel.reportError");
			StringBuilder builder = new StringBuilder(ModelConstants.INSTANCE.imageLoadError());
			if (message != null && message.length() > 0) {
				builder.append(": ");
				builder.append(message);
			}
			errorLabel.setText(builder.toString());
			cardLayout2.setActiveItem(errorPanel);
		}
		
		public void reportNull() {
			errorLabel.setText(ModelConstants.INSTANCE.noImage());
			cardLayout2.setActiveItem(errorPanel);
		}


		public String getResourceName() {
			return resourceName;
		}
	}
	
	public ImageHrefField() {
		super(null);
		widget = new ImageHrefPanel();
		setResizeWidget(true);
		setFireChangeEventOnSetValue(true);
	}

	@Override
	public void setValue(Object value) {
		setValue(value, true);
	}

	public void setValue(Object value, boolean update) {
		if (update) {
			((ImageHrefPanel)widget).update((String)value);
		    this.value = value;
		} else {
			// Fires change event
			super.setValue(value);
		}
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	private static native String base64encode(String str) /*-{
		return $wnd.btoa(str);
	}-*/;

	public String getResourceName() {
		return ((ImageHrefPanel)widget).getResourceName();
	}

	///////////////////////////////////////////////////
	// Event management
	///////////////////////////////////////////////////
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		VectomaticApp2.getApp().getEventBus().fireEventFromSource(event, this);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Size> handler) {
		return VectomaticApp2.getApp().getEventBus().addHandlerToSource(ValueChangeEvent.getType(), this, handler);
	}
}
