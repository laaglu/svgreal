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
package org.vectomatic.svg.edit.client.load;

import com.google.gwt.core.client.GWT;

/**
 * Class to abstract communications with the server
 * @author laaglu
 */
public class FetchUtils {
	/**
	 * Relative URI of the servlet used to fetch cross origin resources
	 */
	public static final String FETCH_URI = "fetch";
	public static final String FETCH_URL_PARAM = "url";
	public static final String FETCH_TYPE_PARAM = "url";

	public static String getFetchUrl(String url, String type) {
		StringBuilder builder = new StringBuilder();
		builder.append(GWT.getHostPageBaseURL());
		builder.append(FETCH_URI);
		return getFetchUrl(builder.toString(), url, type);
	}
	public static String getFetchUrl(String base, String url, String type) {
		StringBuilder builder = new StringBuilder();
		builder.append(base);
		builder.append("?");
		builder.append(FETCH_URL_PARAM);
		builder.append("=");
		builder.append(url);
		builder.append("&");
		builder.append(FETCH_TYPE_PARAM);
		builder.append("=");
		builder.append(type);
		return builder.toString();
	}
}
