/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.List;

import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IVideoEntry;

/**
 * Youtube data source
 * 
 * @author alex
 * 
 */
public class YoutubeDataSource implements IContextInformationSource {

    private YoutubeService youtubeService;

    private List<IVideoEntry> videos;

    private IUnknownObjectChecker unknownObjectChecker;

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    @Override
    public void getData(final IAudioObject audioObject) {
	this.videos = getYoutubeVideos(audioObject);
    }

    /**
     * @return videos found
     */
    public List<IVideoEntry> getVideos() {
	return videos;
    }

    private List<IVideoEntry> getYoutubeVideos(final IAudioObject audioObject) {
	String searchString = youtubeService
		.getSearchForAudioObject(audioObject);
	if (searchString.length() > 0) {
	    return youtubeService.searchInYoutube(
		    audioObject.getArtist(unknownObjectChecker), searchString,
		    1);
	}
	return null;
    }

    /**
     * @param youtubeService
     */
    public void setYoutubeService(final YoutubeService youtubeService) {
	this.youtubeService = youtubeService;
    }

    @Override
    public void cancel() {
    }
}
