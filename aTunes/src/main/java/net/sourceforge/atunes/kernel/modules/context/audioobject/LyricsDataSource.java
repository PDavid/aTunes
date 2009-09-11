package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

public class LyricsDataSource implements ContextInformationDataSource {
	
	/**
	 * Input parameter
	 */
	public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";
	
	/**
	 * Ouput parameter
	 */
	public static final String OUTPUT_LYRIC = "LYRIC";

	/**
	 * Ouput parameter
	 */
	public static final String OUTPUT_AUDIO_OBJECT = INPUT_AUDIO_OBJECT;

	@Override
	public Map<String, ?> getData(Map<String, ?> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
			AudioObject audioObject = (AudioObject) parameters.get(INPUT_AUDIO_OBJECT);
			result.put(OUTPUT_AUDIO_OBJECT, audioObject);
			result.put(OUTPUT_LYRIC, getLyrics(audioObject));
		}
		return result;
	}
	
	/**
	 * Returns lyrics
	 * @param audioObject
	 * @return
	 */
	private Lyrics getLyrics(AudioObject audioObject) {
		Lyrics lyrics = null;
        // First check if tag contains the lyrics. Favour this over internet services.
        if (!audioObject.getLyrics().trim().isEmpty()) {
            lyrics = new Lyrics(audioObject.getLyrics(), null);
        }
        // Query internet service for lyrics
        else {
            if (!audioObject.getTitle().trim().isEmpty() && !audioObject.getArtist().trim().isEmpty() && !audioObject.getArtist().equals(LanguageTool.getString("UNKNOWN_ARTIST"))) {
                lyrics = LyricsService.getInstance().getLyrics(audioObject.getArtist().trim(), audioObject.getTitle().trim());
            }
        }
        if (lyrics != null) {
            lyrics.setLyrics(lyrics.getLyrics().replaceAll("'", "\'"));
        }
        
        if (lyrics == null) {
        	lyrics = new Lyrics("", "");
        }
        
        return lyrics;
	}

}
