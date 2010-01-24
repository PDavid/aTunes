package net.sourceforge.atunes.plugins.webinterface.actions;

import java.util.Map;

import net.sourceforge.atunes.api.PlayerApi;
import net.sourceforge.atunes.plugins.webinterface.VelocityAction;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.velocity.VelocityContext;

public class PlayerControlsAction extends VelocityAction {
	
	@Override
	public VelocityContext execute(Map<String, String> parameters) {
		if (parameters.containsKey("option")) {
			String option = parameters.get("option").toLowerCase();
			if ("stop".equals(option)) {
				PlayerApi.stop();
			} else if ("previous".equals(option)) {
				PlayerApi.previous();
			} else if ("play".equals(option)) {
				PlayerApi.play();
			} else if ("next".equals(option)) {
				PlayerApi.next();
			} else if ("volumedown".equals(option)) {
				PlayerApi.volumeDown();
			} else if ("volumeup".equals(option)) {
				PlayerApi.volumeUp();
			} else if ("state".equals(option)) {
				VelocityContext context = new VelocityContext();
				context.put("state", PlayerApi.getCurrentPlaybackState());
				context.put("time", StringUtils.milliseconds2String(PlayerApi.getCurrentAudioObjectPlayedTime()));
				return context;
			} else if ("time".equals(option)) {
				VelocityContext context = new VelocityContext();
				context.put("elapsed_time", PlayerApi.getCurrentAudioObjectPlayedTime());
				context.put("total_time", PlayerApi.getCurrentAudioObjectLength());
				return context;
			}
		}
		return null;
	}

}
