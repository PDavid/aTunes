package net.sourceforge.atunes.plugins.webinterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;

import net.sourceforge.atunes.utils.StringUtils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Web Server for Velocity templates
 * 
 * Requests to this handler have to follow this pattern:
 * <code>/${base}?action=${actionClass}&template=${template}</code>
 * @author fleax
 *
 */
public class VelocityHttpHandler extends AbstractHttpHandler {

	/**
	 * Folder where templates are located
	 */
	private String templatesBase;
	
	protected VelocityHttpHandler(String templatesBase, String baseDirectory) {
		this.templatesBase = templatesBase;
		Properties p = new Properties();
		p.put(Velocity.FILE_RESOURCE_LOADER_PATH, templatesBase);
		p.put(Velocity.RUNTIME_LOG, StringUtils.getString(baseDirectory, "/velocity.log"));
		try {
			Velocity.init(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isResourceFound(HttpRequest request) {
		String uri = request.getRequestLine().getUri();
		
		VelocityRequest velocityRequest = VelocityRequest.getRequest(uri);
		
		if (request != null) {
			// Find action
			try {
				getAction(velocityRequest);
			} catch (Exception e) {
				return false;
			}
			
			// Find template
			File template = getTemplate(velocityRequest);
			if (template == null) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	protected void process(final HttpRequest request, final HttpResponse response, final HttpContext context) throws Exception {
		VelocityRequest velocityRequest = VelocityRequest.getRequest(request.getRequestLine().getUri());
		
		// Execute action
		VelocityAction action = getAction(velocityRequest);
		VelocityContext actionContext = action.execute(velocityRequest.getAditionalParameters());
		
		// Find template
		File template = getTemplate(velocityRequest);
		
		// Apply template
		final StringWriter templateResult = processTemplate(actionContext, template);
		
        response.setStatusCode(HttpStatus.SC_OK);
        EntityTemplate body = new EntityTemplate(new ContentProducer() {
            
            public void writeTo(final OutputStream outstream) throws IOException {
                OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
                writer.write(templateResult.toString());
                writer.flush();
            }
            
        });
        body.setContentType(getMimeType(template));
        response.setEntity(body);
	}
	
	private VelocityAction getAction(VelocityRequest request) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (VelocityAction) Class.forName(request.getAction()).newInstance();
	}
	
	private File getTemplate(VelocityRequest request) {
		File template = new File(new StringBuilder().append(templatesBase).append(request.getTemplate()).toString());
		return template.exists() ? template : null;
	}
	
	private static StringWriter processTemplate(VelocityContext context, File template) throws Exception {
		StringWriter w = new StringWriter();
		Velocity.mergeTemplate(template.getName(), "UTF-8", context, w);
		return w;
	}

}
