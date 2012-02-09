/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.client.load;

import java.io.IOException;
import java.util.Iterator;

import org.vectomatic.dom.svg.OMAttr;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNode;
import org.vectomatic.dom.svg.impl.SVGParserImpl;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.XPathPrefixResolver;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.AppMessages;
import org.vectomatic.svg.edit.client.SvgrealApp;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * Dialog to display the OpenClipArt 'new svg' RSS feed.
 * @author laaglu
 */
public class RSSReader extends Dialog {
	/**
	 * Bean to represent a single entry in the
	 * OpenClipArt 'new svg' RSS feed. 
	 * @author laaglu
	 */
	public static class RSSEntry implements BeanModelTag {
		private String pngPath;
		private String svgPath;
		public RSSEntry() {
		}

		public void setPngPath(String pngPath) {
			this.pngPath = pngPath;
		}

		public String getPngPath() {
			return pngPath;
		}

		public void setSvgPath(String svgPath) {
			this.svgPath = svgPath;
		}
		
		public String getName() {
			int index = svgPath.lastIndexOf('/');
			return (index != -1) ? svgPath.substring(1 + index) : svgPath;
		}

		public String getSvgPath() {
			return svgPath;
		}
	}

	/**
	 * The list view
	 */
	private ListView<BeanModel> view;
	/**
	 * A store from which the list view fetches
	 * RSS records
	 */
	private ListStore<BeanModel> store;
	/**
	 * A bean factory to wrap RSSEntry into BeanModel
	 * to make them compatible with ListView
	 */
	private BeanModelFactory beanFactory;
	
	public RSSReader() {
		super();
		okText = AppConstants.INSTANCE.openButton();
		cancelText = AppConstants.INSTANCE.cancelButton();
		setButtons(Dialog.OKCANCEL);
		setScrollMode(Scroll.AUTO);
		setHideOnButtonClick(true);
		setHeading(AppConstants.INSTANCE.openRssFeedMenuItem());
		setModal(true);
		setSize(520, 300);
		getButtonById(OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				for (BeanModel beanModel :  view.getSelectionModel().getSelectedItems()) {
					RSSEntry rssEntry = (RSSEntry)beanModel.getBean();
					new UrlLoadRequest(rssEntry.getSvgPath()).load();
				}
			}
		});
		
		beanFactory = BeanModelLookup.get().getFactory(RSSEntry.class);
		store = new ListStore<BeanModel>();
		view = new ListView<BeanModel>() {
			@Override
			protected BeanModel prepareData(BeanModel model) {
				String s = model.get("name");
				model.set("shortName", Format.ellipse(s, 15));
				return model;
			}
		};
	    view.setTemplate(getTemplate());
	    view.setStore(store);
	    view.setItemSelector("div.thumb-wrap");
	    view.getSelectionModel().addListener(Events.SelectionChange,
            new Listener<SelectionChangedEvent<BeanModel>>() {
              public void handleEvent(SelectionChangedEvent<BeanModel> be) {
            	  BeanModel beanModel = be.getSelectedItem();
            	  if (beanModel != null) {
	            	  RSSEntry rssEntry = (RSSEntry)beanModel.getBean();
	            	  GWT.log("Selected: " + rssEntry.getName());
            	  }
              }
        });
	    add(view);
	    load();
	}

	public void load() {

		final String url = "http://www.openclipart.org/rss/new.xml";
		String resourceUrl = FetchUtils.getFetchUrl(url, "text/xml");
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, resourceUrl);
		requestBuilder.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable e) {
				GWT.log("Cannot fetch " + url, e);
				SvgrealApp.getApp().info(AppConstants.INSTANCE.openRssFeedMenuItem(), AppMessages.INSTANCE.loadErrorMessage(url, e.getMessage()));
			}

			private void onSuccess(Request request, Response response) {
				// Create a store of BeanModel of RSSEntry
				SVGParserImpl impl = GWT.create(SVGParserImpl.class);
				Document doc = impl.parseFromString(response.getText(), "text/xml").cast();
				OMElement root = OMNode.convert(doc.getDocumentElement());


				Iterator<OMAttr> iterator = DOMHelper.evaluateXPath(root, "//rss/channel/item/enclosure/@url", null);
				while(iterator.hasNext()) {
					RSSEntry rssEntry = new RSSEntry();
					rssEntry.setSvgPath(iterator.next().getValue());
					store.add(beanFactory.createModel(rssEntry));
				}

				iterator = DOMHelper.evaluateXPath(root, "//rss/channel/item/media:thumbnail/@url", new XPathPrefixResolver() {

					@Override
					public String resolvePrefix(String prefix) {
						if ("media".equals(prefix)) {
							return "http://search.yahoo.com/mrss/";
						}
						return null;
					}
				});
				int index = 0;
				while(iterator.hasNext()) {
					BeanModel beanModel = store.getAt(index++);
					RSSEntry rssEntry = (RSSEntry)beanModel.getBean();
					rssEntry.setPngPath(iterator.next().getValue());
					store.update(beanModel);
				}
			}
			
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					onSuccess(request, response);
				} else {
					onError(request, new IOException(AppMessages.INSTANCE.httpErrorMessage(Integer.toString(response.getStatusCode()))));
				}
			}
		});
		try {
			requestBuilder.send();
		} catch (RequestException e) {
			GWT.log("Cannot fetch " + url, e);
			SvgrealApp.getApp().info(AppConstants.INSTANCE.openRssFeedMenuItem(), AppMessages.INSTANCE.loadErrorMessage(url, e.getMessage()));
		}
	}
	
	private static native String getTemplate() /*-{
		return ['<tpl for=".">',
		'<div class="thumb-wrap" id="{svgPath}">',
		'<div class="thumb"><img src="{pngPath}" title="{name}"></div>',
		'<span class="x-editable">{shortName}</span></div>',
		'</tpl>',
		'<div class="x-clear"></div>'].join("");
	}-*/;
}
