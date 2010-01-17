package net.sourceforge.atunes.plugins.webinterface;

import java.util.Map;

import org.apache.velocity.VelocityContext;

public abstract class VelocityAction {
	
	public abstract VelocityContext execute(Map<String, String> parameters);

}
