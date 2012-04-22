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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.Color;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

public class FullScreenCoverImageRetriever {

	private IWebServicesHandler webServicesHandler;
	
	private IOSManager osManager;
	
	private IIconFactory radioBigIcon;
	
	private IIconFactory rssBigIcon;
	
	/**
	 * @param radioBigIcon
	 */
	public void setRadioBigIcon(IIconFactory radioBigIcon) {
		this.radioBigIcon = radioBigIcon;
	}
	
	/**
	 * @param rssBigIcon
	 */
	public void setRssBigIcon(IIconFactory rssBigIcon) {
		this.rssBigIcon = rssBigIcon;
	}
	
	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
    /**
     * Returns picture for audio object
     * 
     * @param audioObject
     * @param osManager
     * @return
     */
    ImageIcon getPicture(IAudioObject audioObject) {
    	ImageIcon image = null;
    	if (audioObject instanceof IRadio) {
    		image = radioBigIcon.getIcon(Color.WHITE);
    	} else if (audioObject instanceof IPodcastFeedEntry) {
    		image = rssBigIcon.getIcon(Color.WHITE);
    	} else if (audioObject instanceof ILocalAudioObject){
    		image = getPictureForLocalAudioObject(audioObject);
    	}

    	return image;
    }

	/**
	 * @param audioObject
	 * @return
	 */
	private ImageIcon getPictureForLocalAudioObject(IAudioObject audioObject) {
		ImageIcon image = webServicesHandler.getAlbumImage(audioObject.getArtist(), audioObject.getAlbum());
		if (image == null) {
			// Get inside picture
			image = AudioFilePictureUtils.getInsidePicture(audioObject, -1, -1);
		}
		if (image == null) {
			// Get external picture
			image = AudioFilePictureUtils.getExternalPicture(audioObject, -1, -1, osManager);
		}
		if (image == null) {
			image = Images.getImage(Images.APP_LOGO_300);
		}
		return image;
	}    
}
