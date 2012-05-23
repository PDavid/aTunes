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

package net.sourceforge.atunes.kernel.modules.context.artist;

import net.sourceforge.atunes.kernel.modules.repository.UnknownObjectChecker;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Data Source for artist popular tracks
 * 
 * @author alex
 * 
 */
public class ArtistPopularTracksDataSource implements IContextInformationSource {

    private IWebServicesHandler webServicesHandler;
    
    private IArtistTopTracks topTracks;
    
    private IUnknownObjectChecker unknownObjectChecker;
    
    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}
    
    @Override
    public void getData(IAudioObject audioObject) {
    	this.topTracks = getTopTracksData(audioObject);
    }
    
    /**
     * @return
     */
    public IArtistTopTracks getTopTracks() {
		return topTracks;
	}

    private IArtistTopTracks getTopTracksData(IAudioObject audioObject) {
    	if (!unknownObjectChecker.isUnknownArtist(audioObject.getArtist())) {
    		return webServicesHandler.getTopTracks(audioObject.getArtist());
    	}
    	return null;
    }
    
    /**
     * @param webServicesHandler
     */
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
}
