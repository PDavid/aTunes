/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.awt.Image;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Similar artists data source
 * 
 * @author alex
 * 
 */
public class SimilarArtistsDataSource implements IContextInformationSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ARTISTS = "ARTISTS";

	private IWebServicesHandler webServicesHandler;

	private IRepositoryHandler repositoryHandler;
	
    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
        	ISimilarArtistsInfo info = getSimilarArtists((IAudioObject) parameters.get(INPUT_AUDIO_OBJECT));
        	if (info != null) {
        		result.put(OUTPUT_ARTISTS, info);
        	}
        }
        return result;
    }

    /**
     * Returns similar artists
     * 
     * @param audioObject
     */
    private ISimilarArtistsInfo getSimilarArtists(IAudioObject audioObject) {
        if (!Artist.isUnknownArtist(audioObject.getArtist())) {
            ISimilarArtistsInfo artists = webServicesHandler.getSimilarArtists(audioObject.getArtist());
            if (artists != null) {
            	Set<String> artistNamesSet = new HashSet<String>();
            	for (Artist a : repositoryHandler.getArtists()) {
            		artistNamesSet.add(a.getName().toUpperCase());
            	}
                for (int i = 0; i < artists.getArtists().size(); i++) {
                    IArtistInfo a = artists.getArtists().get(i);
                    Image img = webServicesHandler.getArtistThumbImage(a);
                    a.setImage(ImageUtils.scaleImageBicubic(img, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
                    a.setAvailable(artistNamesSet.contains(a.getName().toUpperCase()));
                }
            }
            return artists;
        }
        return null;
    }

    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
}
