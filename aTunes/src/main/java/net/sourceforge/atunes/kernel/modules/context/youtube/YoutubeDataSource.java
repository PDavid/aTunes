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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;

/**
 * Similar artists data source
 * 
 * @author alex
 * 
 */
public class YoutubeDataSource implements IContextInformationSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_VIDEOS = "VIDEOS";

    private YoutubeService youtubeService;
    
    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            result.put(OUTPUT_VIDEOS, getYoutubeVideos((IAudioObject) parameters.get(INPUT_AUDIO_OBJECT)));
        }
        return result;
    }

    private List<YoutubeResultEntry> getYoutubeVideos(IAudioObject audioObject) {
        String searchString = youtubeService.getSearchForAudioObject(audioObject);
        if (searchString.length() > 0) {
            return youtubeService.searchInYoutube(searchString, 1);
        }
        return null;
    }
    
    public void setYoutubeService(YoutubeService youtubeService) {
		this.youtubeService = youtubeService;
	}
}
