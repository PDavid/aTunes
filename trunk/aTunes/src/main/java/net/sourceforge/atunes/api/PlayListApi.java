package net.sourceforge.atunes.api;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class PlayListApi {

	/**
	 * Adds a list of audio objects to the current visible play list
	 * @param objects
	 */
	public static void add(List<AudioObject> objects) {
		PlayListHandler.getInstance().addToPlayList(objects);
	}
}
