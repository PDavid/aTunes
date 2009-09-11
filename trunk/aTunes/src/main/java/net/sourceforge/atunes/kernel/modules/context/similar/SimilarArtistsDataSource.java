package net.sourceforge.atunes.kernel.modules.context.similar;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Similar artists data source
 * @author alex
 *
 */
public class SimilarArtistsDataSource implements ContextInformationDataSource {
	
	/**
	 * Input parameter
	 */
	public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

	/**
	 * Output parameter
	 */
	public static final String OUTPUT_ARTISTS = "ARTISTS";

	@Override
	public Map<String, ?> getData(Map<String, ?> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
			result.put(OUTPUT_ARTISTS, getSimilarArtists((AudioObject)parameters.get(INPUT_AUDIO_OBJECT)));
		}
		return result;
	}
	
	/**
	 * Returns similar artists
	 * @param audioObject
	 */
	private SimilarArtistsInfo getSimilarArtists(AudioObject audioObject) {
        if (!Artist.isUnknownArtist(audioObject.getArtist())) {
            SimilarArtistsInfo artists = LastFmService.getInstance().getSimilarArtists(audioObject.getArtist());
            if (artists != null) {
                for (int i = 0; i < artists.getArtists().size(); i++) {
                    ArtistInfo a = artists.getArtists().get(i);
                    Image img = LastFmService.getInstance().getImage(a);
                    a.setImage(ImageUtils.scaleImageBicubic(img, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
                    a.setAvailable(RepositoryHandler.getInstance().getArtistStructure().containsKey(a.getName()));
                }
            }
            return artists;
        }
        return null;
	}

}
