package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Similar artists data source
 * @author alex
 *
 */
public class YoutubeDataSource implements ContextInformationDataSource {
	
	/**
	 * Input parameter
	 */
	public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

	/**
	 * Output parameter
	 */
	public static final String OUTPUT_VIDEOS = "VIDEOS";

	@Override
	public Map<String, ?> getData(Map<String, ?> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
			result.put(OUTPUT_VIDEOS, getYoutubeVideos((AudioObject)parameters.get(INPUT_AUDIO_OBJECT)));
		}
		return result;
	}

	private List<YoutubeResultEntry> getYoutubeVideos(AudioObject audioObject) {
        String searchString = YoutubeService.getInstance().getSearchForAudioObject(audioObject);
        if (searchString.length() > 0) {
            return YoutubeService.getInstance().searchInYoutube(searchString, 1);
        }
        return null;
	}
}
