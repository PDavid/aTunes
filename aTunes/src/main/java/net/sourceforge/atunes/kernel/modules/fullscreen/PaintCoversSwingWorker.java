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
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class PaintCoversSwingWorker extends SwingWorker<Void, Void> implements ApplicationContextAware {
	
	private Cover cover;
	private IAudioObject audioObject;
	private int imageSize;
	
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
	void getCover(Cover cover, IAudioObject audioObject, int imageSize) {
		this.cover = cover;
		this.audioObject = audioObject;
		this.imageSize = imageSize;
		execute();
	}
	
	@Override
	protected Void doInBackground() {
		ImageIcon image = null;
	    if (audioObject instanceof IRadio) {
	        image = context.getBean("radioBigIcon", IIconFactory.class).getIcon(Color.WHITE);
	    } else if (audioObject instanceof IPodcastFeedEntry) {
	        image = context.getBean("rssBigIcon", IIconFactory.class).getIcon(Color.WHITE);
	    } else if (audioObject instanceof ILocalAudioObject){
    		image = getPicture((ILocalAudioObject)audioObject, context.getBean(IOSManager.class));
	    }
	    
        if (cover != null) {
            if (image == null) {
                cover.setImage(null, 0, 0);
            } else if (audioObject == null) {
                cover.setImage(Images.getImage(Images.APP_LOGO_300).getImage(), imageSize, imageSize);
            } else {
                cover.setImage(image.getImage(), imageSize, imageSize);
            }
        }
        
        return null;
	}

	@Override
	protected void done() {
	        if (cover != null) {
	        	cover.repaint();
	        }
	}
	
    /**
     * Returns picture for audio file
     * 
     * @param audioFile
     * @param osManager
     * @return
     */
    protected ImageIcon getPicture(ILocalAudioObject audioFile, IOSManager osManager) {
    	ImageIcon result = context.getBean(IWebServicesHandler.class).getAlbumImage(audioFile.getArtist(), audioFile.getAlbum());
        if (result == null) {
        	// Get inside picture
        	ImageIcon icon = AudioFilePictureUtils.getInsidePicture(audioFile, -1, -1);
        	result = icon != null ? icon : null;
        }
        if (result == null) {
        	// Get external picture
        	ImageIcon icon = AudioFilePictureUtils.getExternalPicture(audioFile, -1, -1, osManager);
        	result = icon != null ? icon : null;
        }
        if (result == null) {
            result = Images.getImage(Images.APP_LOGO_300);
        }
        
        return result;
    }    
}