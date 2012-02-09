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
package org.vectomatic.svg.edit.client.load;

import java.io.IOException;

import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.ParserException;
import org.vectomatic.svg.edit.client.AppConstants;
import org.vectomatic.svg.edit.client.AppMessages;
import org.vectomatic.svg.edit.client.SvgrealApp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * Class to load URLs into the application
 * @author laaglu
 */
public class UrlLoadRequest extends LoadRequestBase {
	private String url;
	public UrlLoadRequest(String url) {
		this.url = url;
		int index = url.lastIndexOf('/');
		this.title = (index != -1 && index != url.length() - 1) ? url.substring(index + 1) : url;
	}

	@Override
	public void load() {
		final SvgrealApp app = SvgrealApp.getApp();
		final String resourceUrl = FetchUtils.getFetchUrl(url, "text/xml");
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, resourceUrl);
		requestBuilder.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable e) {
				GWT.log("Cannot fetch " + url, e);
				app.info(AppConstants.INSTANCE.openUrlMenuItem(), AppMessages.INSTANCE.loadErrorMessage(url, e.getMessage()));
			}

			private void onSuccess(Request request, Response response) {
				try {
					app.addWindow(OMSVGParser.parse(response.getText()), UrlLoadRequest.this);
				} catch(ParserException e) {
					app.info(AppConstants.INSTANCE.openLocalMenuItem(), AppMessages.INSTANCE.loadErrorMessage(resourceUrl, e.getMessage()));
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
			app.info(AppConstants.INSTANCE.openUrlMenuItem(), AppMessages.INSTANCE.loadErrorMessage(url, e.getMessage()));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UrlLoadRequest) {
			UrlLoadRequest r = (UrlLoadRequest)o;
			return url.equals(r.url);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return url.hashCode();
	}

}
