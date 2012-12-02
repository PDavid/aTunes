/*
 * aTunes 3.0.0
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

import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.Logger;

/**
 * Gets image and paints in a cover of full screen window
 * 
 * @author alex
 * 
 */
public final class PaintCoversSwingWorker extends SwingWorker<Void, Void> {

    private Cover cover;
    private IAudioObject audioObject;
    private int imageSize;

    private FullScreenCoverImageRetriever fullScreenCoverImageRetriever;

    /**
     * @param fullScreenCoverImageRetriever
     */
    public void setFullScreenCoverImageRetriever(
	    final FullScreenCoverImageRetriever fullScreenCoverImageRetriever) {
	this.fullScreenCoverImageRetriever = fullScreenCoverImageRetriever;
    }

    void getCover(final Cover cover, final IAudioObject audioObject,
	    final int imageSize) {
	this.cover = cover;
	this.audioObject = audioObject;
	this.imageSize = imageSize;
	execute();
    }

    @Override
    protected Void doInBackground() {
	ImageIcon image = fullScreenCoverImageRetriever.getPicture(audioObject);

	if (cover != null) {
	    if (image == null) {
		cover.setImage(null, 0, 0);
	    } else if (audioObject == null) {
		cover.setImage(Images.getImage(Images.APP_LOGO_300).getImage(),
			imageSize, imageSize);
	    } else {
		cover.setImage(image.getImage(), imageSize, imageSize);
	    }
	}
	return null;
    }

    @Override
    protected void done() {
	try {
	    get();
	} catch (InterruptedException e) {
	    Logger.error(e);
	} catch (ExecutionException e) {
	    Logger.error(e);
	}
	if (cover != null) {
	    cover.repaint();
	}
    }
}