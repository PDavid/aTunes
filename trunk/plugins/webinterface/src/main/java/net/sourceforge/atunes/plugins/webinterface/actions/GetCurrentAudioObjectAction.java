package net.sourceforge.atunes.plugins.webinterface.actions;

import java.util.Map;

import org.apache.velocity.VelocityContext;

import net.sourceforge.atunes.api.PlayerApi;
import net.sourceforge.atunes.plugins.webinterface.VelocityAction;

public class GetCurrentAudioObjectAction extends VelocityAction {
	
	@Override
	public VelocityContext execute(Map<String, String> parameters) {
		VelocityContext context = new VelocityContext();
		context.put("audioObject", PlayerApi.getCurrentAudioObject());
		return context;
	}

}
