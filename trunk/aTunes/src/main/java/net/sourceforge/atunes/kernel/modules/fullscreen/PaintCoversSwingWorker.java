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
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.Logger;

final class PaintCoversSwingWorker extends SwingWorker<Void, Void> {
	private final Cover cover;
	private final IAudioObject audioObject;
	private final int index;
	private IOSManager osManager;
	private final int coverSize;

	PaintCoversSwingWorker(Cover cover, IAudioObject audioObject, int index, IOSManager osManager, int coverSize) {
		this.cover = cover;
		this.audioObject = audioObject;
		this.index = index;
		this.osManager = osManager;
		this.coverSize = coverSize;
	}

	@Override
	protected Void doInBackground() {
		ImageIcon image = null;
	    if (audioObject instanceof IRadio) {
	        image = Context.getBean("radioBigIcon", IIconFactory.class).getIcon(Color.WHITE);
	    } else if (audioObject instanceof IPodcastFeedEntry) {
	        image = Context.getBean("rssBigIcon", IIconFactory.class).getIcon(Color.WHITE);
	    } else if (audioObject instanceof ILocalAudioObject){
    		image = getPicture((ILocalAudioObject)audioObject, osManager);
	    }
	    
        if (cover != null) {
            if (image == null) {
                cover.setImage(null, 0, 0);
            } else if (audioObject == null) {
                cover.setImage(Images.getImage(Images.APP_LOGO_300).getImage(), getImageSize(index), getImageSize(index));
            } else {
                cover.setImage(image.getImage(), getImageSize(index), getImageSize(index));
            }
        }
        
        return null;
	}

	@Override
	protected void done() {
	    try {
	        get();
	        if (cover != null) {
	        	cover.repaint();
	        }
	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
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
    	ImageIcon result = Context.getBean(IWebServicesHandler.class).getAlbumImage(audioFile.getArtist(), audioFile.getAlbum());
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
    
    private int getImageSize(int index) {
        if (index == 2) {
            return coverSize;
        } else if (index == 1 || index == 3) {
            return coverSize * 3 / 4;
        } else {
            return coverSize * 9 / 16;
        }
    }
}