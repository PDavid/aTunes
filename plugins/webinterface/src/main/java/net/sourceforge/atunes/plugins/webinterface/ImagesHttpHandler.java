package net.sourceforge.atunes.plugins.webinterface;

import java.awt.Image;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.utils.ImageUtils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;

/**
 * Get images 
 * @author fleax
 *
 */
public class ImagesHttpHandler extends AbstractHttpHandler {

	@Override
	protected boolean isResourceFound(HttpRequest request) {
		String uri = request.getRequestLine().getUri();
		
		ImageRequest imageRequest = ImageRequest.getRequest(uri);
		
		if (request != null) {
			// Find action
			try {
				getAction(imageRequest);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	protected void process(final HttpRequest request, final HttpResponse response, final HttpContext context) throws Exception {
		ImageRequest imageRequest = ImageRequest.getRequest(request.getRequestLine().getUri());
		
		// Execute action
		ImageAction action = getAction(imageRequest);
		final Image image = action.execute(imageRequest.getAditionalParameters());
		
        response.setStatusCode(HttpStatus.SC_OK);
        EntityTemplate body = new EntityTemplate(new ContentProducer() {
            
            public void writeTo(final OutputStream outstream) throws IOException {
            	ImageIO.write(ImageUtils.toBufferedImage(image), "PNG", outstream);
            }
            
        });
        body.setContentType("image/png");
        response.setEntity(body);
	}
	
	private ImageAction getAction(ImageRequest request) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (ImageAction) Class.forName(request.getAction()).newInstance();
	}

}
