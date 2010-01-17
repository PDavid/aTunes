package net.sourceforge.atunes.plugins.webinterface;

import java.util.HashMap;
import java.util.Map;

public class VelocityRequest {
	
	private String action;
	
	private String template;
	
	private Map<String, String> aditionalParameters;
	
	private VelocityRequest(String action, String template) {
		this.action = action;
		this.template = template;
	}
	
	public static VelocityRequest getRequest(String uri) {
		VelocityRequest result = null;
		String action = null;
		String template = null;
		
		Map<String, String> aditionalParameters = null;
		
		if (uri.indexOf('?') != -1) {
			String parameterString = uri.substring(uri.indexOf('?')+1);
			String[] parameters = parameterString.split("&");
			for (String parameter : parameters) {
				String[] parameterTokens = parameter.split("=");
				if (parameterTokens.length == 2) {
					String name = parameterTokens[0];
					String value = parameterTokens[1];
					if (name.equalsIgnoreCase("action")) {
						action = value;
					} else if (name.equalsIgnoreCase("template")) {
						template = value;
					} else {
						if (aditionalParameters == null) {
							aditionalParameters = new HashMap<String, String>();
						}
						aditionalParameters.put(name, value);
					}
				}
			}
		}
		
		if (action != null && template != null) {
			result = new VelocityRequest(action, template);
			result.aditionalParameters = aditionalParameters;
		}
		
		return result;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	public Map<String, String> getAditionalParameters() {
		return aditionalParameters;
	}

}
