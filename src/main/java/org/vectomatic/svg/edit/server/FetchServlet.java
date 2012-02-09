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
package org.vectomatic.svg.edit.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.vectomatic.svg.edit.client.load.FetchUtils;

/**
 * Servlet to fetch resources from domains outside
 * the vectomatic domain.
 */
public class FetchServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(FetchServlet.class);
	private static final long serialVersionUID = 1L;
	private static final int MAX_SIZE = 5 * 1024 * 1024;
	private static final String HTTP_PROTOCOL = "http";
	private String hostname;

    /**
     * Default constructor. 
     */
    public FetchServlet() {
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	try {
    	    InetAddress addr = InetAddress.getLocalHost();
    	    hostname = addr.getHostName();
    	} catch (UnknownHostException e) {
			logger.error("Cannot get host name", e);
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL url = new URL(request.getParameter(FetchUtils.FETCH_URL_PARAM));
		String contentType = request.getParameter(FetchUtils.FETCH_TYPE_PARAM);
		logger.info("Fetching: " + url.toExternalForm() + " contentType: " + contentType);
		// Reject all non HTTP urls
		if (!HTTP_PROTOCOL.equals(url.getProtocol())) {
			logger.error("Unsupported protocol: " + url.toExternalForm());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported protocol: " + url.getProtocol());
			return;
		} else {
			String host = url.getHost();
			if (host.startsWith("localhost") || host.startsWith(hostname) || host.startsWith("127.0.0.1")) {
				logger.error("Access not permitted: " + url.toExternalForm());
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access not permitted: " + url);
				return;
			}
			InputStream istream = null;
			OutputStream ostream = null;
			try {
				// Copy the requested stream. Stop if it exceeds MAX_SIZE
				istream = url.openStream();
			} catch(IOException e) {
				logger.error("Not found: " + url.toExternalForm());
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found: " + url.getProtocol());
				return;
			}
			try {
				if (contentType != null) {
					response.setContentType(contentType);
				}
				ostream = response.getOutputStream();

				byte[] buffer = new byte[4096];
				int length, totalLength = 0;
				while ((totalLength <= MAX_SIZE) && ((length = istream.read(buffer)) != -1)) {
					ostream.write(buffer, 0, length);
					totalLength += length;
				}
				if (totalLength > MAX_SIZE) {
					logger.error("Size limit exceeded: " + url.toExternalForm());
					return;
				}
			} catch(Throwable t) {
				logger.error("Load error: " + url.toExternalForm() + " " + t.getMessage());
			} finally {
				if (istream != null) {
					istream.close();
				}
				if (ostream != null) {
					ostream.close();
				}
			}
		}
	}
}
