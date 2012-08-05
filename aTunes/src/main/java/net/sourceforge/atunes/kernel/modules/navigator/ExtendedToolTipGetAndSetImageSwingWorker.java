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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.Logger;

public final class ExtendedToolTipGetAndSetImageSwingWorker extends SwingWorker<ImageIcon, Void> {

    private IAudioObjectImageLocator audioObjectImageLocator;
    
    private NavigationController navigationController;
    
    private IWebServicesHandler webServicesHandler;
    
	private Object currentObject;
	
	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param currentObject
	 */
	public void setCurrentObject(Object currentObject) {
		this.currentObject = currentObject;
	}
	
	/**
	 * @param navigationController
	 */
	public void setNavigationController(NavigationController navigationController) {
		this.navigationController = navigationController;
	}

	/**
	 * @param audioObjectImageLocator
	 */
	public void setAudioObjectImageLocator(IAudioObjectImageLocator audioObjectImageLocator) {
		this.audioObjectImageLocator = audioObjectImageLocator;
	}
	
	@Override
	protected ImageIcon doInBackground() {
	    // Get image for albums
        if (currentObject instanceof ITreeObject) {
        	if (currentObject instanceof IArtist) {
        		IArtist a = (IArtist) currentObject;
                return webServicesHandler.getArtistImage(a.getName());
        	} else if (currentObject instanceof IAlbum) {
        		return audioObjectImageLocator.getImage((IAlbum)currentObject, ImageSize.SIZE_MAX);
        	}
        }
        return null;
	}

	@Override
	protected void done() {
	    try {
	        // Set image in tooltip when done (tooltip can be not visible then)
	        if (navigationController.getCurrentExtendedToolTipContent() != null && navigationController.getCurrentExtendedToolTipContent().equals(currentObject)) {
	            navigationController.getExtendedToolTip().setImage(get());
	        }
	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    }
	}
}