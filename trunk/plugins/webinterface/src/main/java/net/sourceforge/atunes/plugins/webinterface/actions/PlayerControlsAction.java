package net.sourceforge.atunes.plugins.webinterface.actions;

import java.util.Map;

import org.apache.velocity.VelocityContext;

import net.sourceforge.atunes.api.PlayerApi;
import net.sourceforge.atunes.plugins.webinterface.VelocityAction;

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
			}
		}
		return null;
	}

}
