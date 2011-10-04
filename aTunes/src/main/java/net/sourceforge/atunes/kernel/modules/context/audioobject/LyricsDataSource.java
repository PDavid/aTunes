/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsService;
import net.sourceforge.atunes.utils.I18nUtils;

public class LyricsDataSource implements IContextInformationSource {

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

    private ILyricsService lyricsService;
    
	@Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            IAudioObject audioObject = (IAudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            result.put(OUTPUT_AUDIO_OBJECT, audioObject);
            result.put(OUTPUT_LYRIC, getLyrics(audioObject));
        }
        return result;
    }

    /**
     * Returns lyrics
     * 
     * @param audioObject
     * @return
     */
    private ILyrics getLyrics(IAudioObject audioObject) {
        ILyrics lyrics = null;
        // First check if tag contains the lyrics. Favour this over internet services.
        if (!audioObject.getLyrics().trim().isEmpty()) {
            lyrics = new Lyrics(audioObject.getLyrics(), null);
        }
        // Query internet service for lyrics
        else {
            if (!audioObject.getTitle().trim().isEmpty() && !audioObject.getArtist().trim().isEmpty() && !audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
                lyrics = lyricsService.getLyrics(audioObject.getArtist().trim(), audioObject.getTitle().trim());
            }
        }

        if (lyrics == null) {
            lyrics = new Lyrics("", "");
        }

        return lyrics;
    }
    
    public void setLyricsService(ILyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}

}
