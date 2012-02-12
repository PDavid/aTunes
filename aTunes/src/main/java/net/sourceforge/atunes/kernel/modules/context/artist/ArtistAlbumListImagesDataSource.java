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

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class ArtistAlbumListImagesDataSource implements IContextInformationSource {

    private IWebServicesHandler webServicesHandler;
    
    private IAudioObject audioObject;
    
    private IAlbumListInfo albumList;
    
    private Map<IAlbumInfo, ImageIcon> covers; 
    
    @Override
    public void getData(IAudioObject audioObject) {
    	this.audioObject = audioObject;
        this.albumList = getAlbumListData(audioObject);
        this.covers = new HashMap<IAlbumInfo, ImageIcon>();
        if (albumList != null && !albumList.getAlbums().isEmpty()) {
            for (IAlbumInfo album : albumList.getAlbums()) {
                covers.put(album, ImageUtils.scaleImageBicubic(getAlbumImageData(album), Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
            }
        }
    }
    
    /**
     * @return
     */
    public IAlbumListInfo getAlbumList() {
		return albumList;
	}
    
    /**
     * @return
     */
    public Map<IAlbumInfo, ImageIcon> getCovers() {
		return covers;
	}
    
    /**
     * Return album list for artist
     * 
     * @param audioObject
     * @return
     */
    private IAlbumListInfo getAlbumListData(IAudioObject audioObject) {
        if (!UnknownObjectCheck.isUnknownArtist(audioObject.getArtist())) {
            return webServicesHandler.getAlbumList(audioObject.getArtist());
        }
        return null;
    }

    /**
     * Returns image for album
     * 
     * @param album
     * @return
     */
    private Image getAlbumImageData(IAlbumInfo album) {
        return webServicesHandler.getAlbumThumbImage(album);
    }
    
    /**
     * @return
     */
    public IAudioObject getAudioObject() {
		return audioObject;
	}
    
    /**
     * @param webServicesHandler
     */
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
}
