package net.sourceforge.atunes.plugins.webinterface;

import java.util.Map;

import org.apache.velocity.VelocityContext;

public class TestAction extends VelocityAction {

	@Override
	public VelocityContext execute(Map<String, String> parameters) {
		VelocityContext context = new VelocityContext();
		context.put("test", "test value");
		context.put("parameters", parameters);
		return context;
	}

}
