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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.Cover;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.Logger;

public final class CoverFlow extends JPanel {

    private final class PaintCoversSwingWorker extends SwingWorker<Void, Void> {
		private final Cover cover;
		private final IAudioObject audioObject;
		private final int index;
		private IOSManager osManager;

		private PaintCoversSwingWorker(Cover cover, IAudioObject audioObject, int index, IOSManager osManager) {
			this.cover = cover;
			this.audioObject = audioObject;
			this.index = index;
			this.osManager = osManager;
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
	                cover.setImage(Images.getImage(Images.APP_LOGO_300).getImage(), getImageSize(covers.indexOf(cover)), getImageSize(covers.indexOf(cover)));
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
	}

	private static final long serialVersionUID = -5982158797052430789L;

    private List<Cover> covers;
    
    private int coverSize;
    
    CoverFlow(int coverSize) {
        super(new GridBagLayout());
        this.coverSize = coverSize;
        covers = new ArrayList<Cover>(5);
        covers.add(new Cover());
        covers.add(new Cover());
        covers.add(new Cover());
        covers.add(new Cover());
        covers.add(new Cover());

        setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(covers.get(0), c);
        c.gridx = 1;
        add(covers.get(1), c);
        c.gridx = 2;
        c.weightx = 0.3;
        add(covers.get(2), c);
        c.gridx = 3;
        c.weightx = 0.2;
        add(covers.get(3), c);
        c.gridx = 4;
        add(covers.get(4), c);
    }

    /**
     * Paint.
     * 
     * @param objects
     * @param osManager
     */
    void paint(final List<IAudioObject> objects, IOSManager osManager) {
        int i = 0;
        for (IAudioObject ao : objects) {
            paint(ao, i < covers.size() ? covers.get(i) : null, i, osManager);
            i++;
        }
    }

    private void paint(final IAudioObject audioObject, final Cover cover, int index, IOSManager osManager) {
        // No object
        if (audioObject == null) {
            return;
        }

        // Fetch cover
        new PaintCoversSwingWorker(cover, audioObject, index, osManager).execute();
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
