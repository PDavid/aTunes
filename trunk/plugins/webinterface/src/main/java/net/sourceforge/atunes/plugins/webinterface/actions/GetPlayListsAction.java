package net.sourceforge.atunes.plugins.webinterface.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import net.sourceforge.atunes.api.PlayListApi;
import net.sourceforge.atunes.plugins.webinterface.VelocityAction;

public class GetPlayListsAction extends VelocityAction {
	
	@Override
	public VelocityContext execute(Map<String, String> parameters) {
		VelocityContext context = new VelocityContext();
		List<String> playLists = new ArrayList<String>();
		int playListNameCounter = 1;
		for (int i = 0; i < PlayListApi.getNumberOfPlayLists(); i++) {
			String name = PlayListApi.getNameOfPlayList(i);
			if (name == null) {
				name = new StringBuilder().append("Play list ").append(Integer.toString(playListNameCounter)).toString();
				playListNameCounter++;
			}
			playLists.add(name);
		}
		context.put("playLists", playLists);
		return context;
	}

}
