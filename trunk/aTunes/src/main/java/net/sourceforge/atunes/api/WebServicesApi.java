package net.sourceforge.atunes.api;

import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class WebServicesApi {

	/**
	 * Returns similar artists information for the given artist name
	 * @param artistName
	 * @return
	 */
	public static SimilarArtistsInfo getSimilarArtists(String artistName) {
		return LastFmService.getInstance().getSimilarArtists(artistName);
	}
}
