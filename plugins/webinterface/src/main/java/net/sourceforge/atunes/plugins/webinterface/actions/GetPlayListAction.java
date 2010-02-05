package net.sourceforge.atunes.plugins.webinterface.actions;

import java.util.Map;

import org.apache.velocity.VelocityContext;

import net.sourceforge.atunes.api.PlayListApi;
import net.sourceforge.atunes.plugins.webinterface.VelocityAction;

public class GetPlayListAction extends VelocityAction {
	
	@Override
	public VelocityContext execute(Map<String, String> parameters) {
		VelocityContext context = new VelocityContext();
		if (parameters.containsKey("playlist")) {
			int playListIndex = Integer.parseInt(parameters.get("playlist"));
			context.put("playlist", PlayListApi.getPlayList(playListIndex));
		}		
		return context;
	}

}
