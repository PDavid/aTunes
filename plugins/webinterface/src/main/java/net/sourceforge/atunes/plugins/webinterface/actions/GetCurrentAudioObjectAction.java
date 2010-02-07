package net.sourceforge.atunes.plugins.webinterface.actions;

import java.util.Map;

import net.sourceforge.atunes.api.PlayerApi;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.plugins.webinterface.VelocityAction;

import org.apache.velocity.VelocityContext;

public class GetCurrentAudioObjectAction extends VelocityAction {
	
	@Override
	public VelocityContext execute(Map<String, String> parameters) {
		VelocityContext context = new VelocityContext();
		AudioObject audioObject = PlayerApi.getCurrentAudioObject();
		context.put("audioObject", audioObject != null ? audioObject : "null");
		return context;
	}

}
