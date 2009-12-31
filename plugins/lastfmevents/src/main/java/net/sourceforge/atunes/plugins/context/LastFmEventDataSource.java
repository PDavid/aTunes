package net.sourceforge.atunes.plugins.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.roarsoftware.lastfm.Event;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;

public class LastFmEventDataSource implements ContextInformationDataSource {
	
	protected static final String INPUT_ARTIST = "ARTIST";
	
	protected static final String OUTPUT_EVENTS = "EVENTS";
	
	@Override
	public Map<String, ?> getData(Map<String, ?> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		String artist = (String) parameters.get(INPUT_ARTIST);
		if (!Artist.isUnknownArtist(artist)) {
			Collection<Event> events = LastFmService.getInstance().getArtistEvents(artist);
			result.put(OUTPUT_EVENTS, events);
		}
		return result;
	}

}
