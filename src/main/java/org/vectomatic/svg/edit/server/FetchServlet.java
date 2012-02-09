/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
/**********************************************
 * Copyright (C) 2010 Lukas Laag
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
package org.vectomatic.svg.edit.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to fetch resources from domains outside
 * the vectomatic domain.
 */
public class FetchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SIZE = 1024 * 2024;
	private static final String PARAM_URL = "url";
	private static final String PARAM_TYPE = "type";
	private static final String HTTP_PROTOCOL = "http";

    /**
     * Default constructor. 
     */
    public FetchServlet() {
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL url = new URL(request.getParameter(PARAM_URL));
		String contentType = request.getParameter(PARAM_TYPE);
		System.out.println("Fetching: " + url.toExternalForm() + " contentType: " + contentType);
		// Reject all non HTTP urls
		if (!HTTP_PROTOCOL.equals(url.getProtocol())) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported protocol: " + url.getProtocol());
		} else {
			InputStream istream = null;
			OutputStream ostream = null;
			try {
				// Copy the requested stream. Stop if it exceeds MAX_SIZE
				istream = url.openStream();
				if (contentType != null) {
					response.setContentType(contentType);
				}
				ostream = response.getOutputStream();

				byte[] buffer = new byte[4096];
				int length, totalLength = 0;
				while ((totalLength < MAX_SIZE) && ((length = istream.read(buffer)) != -1)) {
					ostream.write(buffer, 0, length);
					totalLength += length;
				}
			} finally {
				if (istream != null) {
					istream.close();
				}
				if (ostream != null) {
					ostream.close();
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot fetch: " + url.getProtocol());
				}
			}
		}
	}
}
