package net.sourceforge.atunes.plugins.webinterface;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;

/**
 * Simple implementation of a static web server 
 * @author fleax
 *
 */
public class StaticHttpHandler extends AbstractHttpHandler {

	/**
	 * Folder where resources are located
	 */
	private String base;
	
	protected StaticHttpHandler(String base) {
		this.base = base;
	}
	
	@Override
	protected boolean isResourceFound(HttpRequest request) {
		File file = new File(new StringBuilder().append(base).append(getUri(request)).toString());
		return file.exists();
	}

	protected void process(final HttpRequest request, final HttpResponse response, final HttpContext context) throws IOException {
        response.setStatusCode(HttpStatus.SC_OK);
        File file = new File(new StringBuilder().append(base).append(getUri(request)).toString());
        String mimeType = getMimeType(file);
        FileEntity body = new FileEntity(file, mimeType);
        response.setEntity(body);
	}
	
	private String getUri(HttpRequest request) {
		return request.getRequestLine().getUri().equals("/") ? "/index.html" : request.getRequestLine().getUri(); 
	}
}
