package org.vectomatic.svg.edit.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.vectomatic.svg.edit.client.load.FetchUtils;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

public class FetchServletTest extends TestCase {
    ServletRunner runner;
    ServletUnitClient client;
    
    @Override
	public void setUp() {
        runner = new ServletRunner();
	    runner.registerServlet(FetchUtils.FETCH_URI, FetchServlet.class.getName() );
	    client = runner.newClient();
	}
    
    public void testbadProtocol() throws Throwable {
    	String uri = FetchUtils.getFetchUrl("http://foobar/" + FetchUtils.FETCH_URI, "ftp://openclipart.org/people/conte%20magnus/Caffettiera.svg", "text/xml");
	    WebRequest request = new GetMethodWebRequest(uri);
	    try {
	    	WebResponse response = client.getResponse( request );
	    	fail("Error not detected");
	    } catch(HttpException e) {
	    	assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getResponseCode());
	    }
    }

    public void testLocalhost() throws Throwable {
    	String uri = FetchUtils.getFetchUrl("http://foobar/" + FetchUtils.FETCH_URI, "http://localhost/server.xml", "text/xml");
	    WebRequest request = new GetMethodWebRequest(uri);
	    try {
	    	WebResponse response = client.getResponse( request );
	    	fail("Error not detected");
	    } catch(HttpException e) {
	    	assertEquals(HttpServletResponse.SC_FORBIDDEN, e.getResponseCode());
	    }
    }

    public void testBadUrl() throws Throwable {
    	String uri = FetchUtils.getFetchUrl("http://foobar/" + FetchUtils.FETCH_URI, "http://dkfsdkfhsdkfh.com", "text/xml");
	    WebRequest request = new GetMethodWebRequest(uri);
	    try {
	    	WebResponse response = client.getResponse( request );
	    	fail("Error not detected");
	    } catch(HttpException e) {
	    	assertEquals(HttpServletResponse.SC_NOT_FOUND, e.getResponseCode());
	    }
    }
    
    public void testHtml5Fetch() throws Throwable {
    	String uri = FetchUtils.getFetchUrl("http://foobar/" + FetchUtils.FETCH_URI, "http://www.w3.org/html/logo/downloads/HTML5_Badge.svg", "text/xml");
	    WebRequest request = new GetMethodWebRequest(uri);
    	WebResponse response = client.getResponse( request );
    	String html5Badge = readResource("HTML5_Badge.svg").replaceAll("\n", "\r\n") + "\r\n";
    	String responseText = response.getText();
    	assertEquals(html5Badge, responseText);
    }
    
    private static String readResource(String resourceName) throws IOException {
    	StringBuilder builder = new StringBuilder();
    	char[] buffer = new char[2048];
    	InputStreamReader stream = new InputStreamReader(FetchServletTest.class.getResourceAsStream(resourceName), "UTF-8");
    	try {
    		int length;
    		while((length = stream.read(buffer)) != -1) {
    			builder.append(buffer, 0, length);
    		}
    	} finally {
    		if (stream != null) {
    			stream.close();
    		}
    	}
    	return builder.toString();
    }
}
