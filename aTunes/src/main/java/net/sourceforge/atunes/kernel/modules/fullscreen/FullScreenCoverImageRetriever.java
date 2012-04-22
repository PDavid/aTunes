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

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

public class FullScreenCoverImageRetriever {

	private IWebServicesHandler webServicesHandler;
	
	private IOSManager osManager;
	
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
     * Returns picture for audio file
     * 
     * @param audioObject
     * @param osManager
     * @return
     */
    ImageIcon getPicture(ILocalAudioObject audioObject) {
    	ImageIcon result = webServicesHandler.getAlbumImage(audioObject.getArtist(), audioObject.getAlbum());
        if (result == null) {
        	// Get inside picture
        	ImageIcon icon = AudioFilePictureUtils.getInsidePicture(audioObject, -1, -1);
        	result = icon != null ? icon : null;
        }
        if (result == null) {
        	// Get external picture
        	ImageIcon icon = AudioFilePictureUtils.getExternalPicture(audioObject, -1, -1, osManager);
        	result = icon != null ? icon : null;
        }
        if (result == null) {
            result = Images.getImage(Images.APP_LOGO_300);
        }
        
        return result;
    }    
}
