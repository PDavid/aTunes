package net.sourceforge.atunes.plugins.webinterface;

import java.util.HashMap;
import java.util.Map;

public class ImageRequest {
	
	private String action;
	
	private Map<String, String> aditionalParameters;
	
	private ImageRequest(String action) {
		this.action = action;
	}
	
	public static ImageRequest getRequest(String uri) {
		ImageRequest result = null;
		String action = null;
		
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
					} else {
						if (aditionalParameters == null) {
							aditionalParameters = new HashMap<String, String>();
						}
						aditionalParameters.put(name, value);
					}
				}
			}
		}
		
		if (action != null) {
			result = new ImageRequest(action);
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

	public Map<String, String> getAditionalParameters() {
		return aditionalParameters;
	}

}
