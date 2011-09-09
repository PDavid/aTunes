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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.webservices.WebServicesHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class ArtistInfoDataSource implements ContextInformationDataSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Input parameter
     */
    public static final String INPUT_ALBUMS = "ALBUMS";

    /**
     * Input parameter
     */
    public static final String INPUT_BOOLEAN_IMAGE = "IMAGE";

    /**
     * Output parameter
     */
    public static final String OUTPUT_AUDIO_OBJECT = INPUT_AUDIO_OBJECT;

    /**
     * Output parameter
     */
    public static final String OUTPUT_IMAGE = "IMAGE";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ARTIST_NAME = "ARTIST_NAME";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ARTIST_URL = "ARTIST_URL";

    /**
     * Output parameter
     */
    public static final String OUTPUT_WIKI_TEXT = "WIKI_TEXT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_WIKI_URL = "WIKI_URL";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ALBUMS = "ALBUMS";

    private IState state;

    public ArtistInfoDataSource(IState state) {
    	super();
    	this.state = state;
	}
    
    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            AudioObject audioObject = (AudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            result.put(OUTPUT_AUDIO_OBJECT, audioObject);

            // Basic artist information
            if (!parameters.containsKey(INPUT_ALBUMS)) {
                result.put(OUTPUT_WIKI_TEXT, getWikiText(audioObject));
                result.put(OUTPUT_WIKI_URL, getWikiUrl(audioObject));

                AlbumListInfo albumList = getAlbumList(audioObject);
                if (albumList != null && !albumList.getAlbums().isEmpty()) {
                    result.put(OUTPUT_ARTIST_NAME, albumList.getAlbums().get(0).getArtist());
                    result.put(OUTPUT_ARTIST_URL, albumList.getAlbums().get(0).getArtistUrl());
                }
                result.put(OUTPUT_IMAGE, getArtistImage(audioObject));
            } else {
                // Albums list and images
                AlbumListInfo albumList = getAlbumList(audioObject);
                if (albumList != null && !albumList.getAlbums().isEmpty()) {
                    for (AlbumInfo album : albumList.getAlbums()) {
                        album.setCover(ImageUtils.scaleImageBicubic(getAlbumImage(album), Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
                    }
                    result.put(OUTPUT_ALBUMS, albumList.getAlbums());
                }
            }
        }
        return result;
    }

    /**
     * Return wiki text for artist
     * 
     * @param audioObject
     * @return
     */
    private String getWikiText(AudioObject audioObject) {
        if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
            return WebServicesHandler.getInstance().getLastFmService().getWikiText(audioObject.getArtist());
        }
        return null;
    }

    /**
     * Return wiki url for artist
     * 
     * @param audioObject
     * @return
     */
    private String getWikiUrl(AudioObject audioObject) {
        if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
            return WebServicesHandler.getInstance().getLastFmService().getWikiURL(audioObject.getArtist());
        }
        return null;
    }

    /**
     * Return album list for artist
     * 
     * @param audioObject
     * @return
     */
    private AlbumListInfo getAlbumList(AudioObject audioObject) {
        if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
            return WebServicesHandler.getInstance().getLastFmService().getAlbumList(audioObject.getArtist(), state.isHideVariousArtistsAlbums(),
                    state.getMinimumSongNumberPerAlbum());
        }
        return null;
    }

    /**
     * Returns image for artist
     * 
     * @param audioObject
     * @return
     */
    private Image getArtistImage(AudioObject audioObject) {
        return WebServicesHandler.getInstance().getLastFmService().getImage(audioObject.getArtist());
    }

    /**
     * Returns image for album
     * 
     * @param album
     * @return
     */
    private Image getAlbumImage(AlbumInfo album) {
        return WebServicesHandler.getInstance().getLastFmService().getImage(album);
    }

}
