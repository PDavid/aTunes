package net.sourceforge.atunes.api;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class RepositoryApi {

	/**
	 * Returns artist information for the given artist name or <code>null</code> if there is no information about
	 * @param artistName
	 * @return
	 */
	public static Artist getArtist(String artistName) {
		return RepositoryHandler.getInstance().getArtistStructure().get(artistName);
	}
}
