/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.views.dialogs.fullScreen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.Cover3D;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.ImageUtils;

public class CoverFlow extends JPanel {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5982158797052430789L;

    static Logger logger = new Logger();
    
    private List<Cover3D> covers;
    
    /** The current audio object. */
    Map<AudioObject, Image> cachedImages = new HashMap<AudioObject, Image>();
    
    CoverFlow() {
        super(new GridBagLayout());
        covers = new ArrayList<Cover3D>();
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));
        covers.add(new Cover3D(0));

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
     * @param audioObject
     *            the song
     * @param updateCover
     *            the update cover
     */
   void paint(final List<AudioObject> objects) {
        int i = 0;
        for (AudioObject ao : objects) {
            paint(ao, covers.get(i), i == 2);
            i++;
        }
    }
   private void paint(final AudioObject audioObject, final Cover3D cover, boolean current) {
       // No object
       if (audioObject == null) {
           return;
       }
       
       // Fetch cover
       new SwingWorker<Image, Void>() {
           @Override
           protected Image doInBackground() throws Exception {
               if (audioObject instanceof Radio) {
                   return ImageLoader.getImage(ImageLoader.RADIO_BIG).getImage();
               } else if (audioObject instanceof PodcastFeedEntry) {
                   return ImageLoader.getImage(ImageLoader.RSS_BIG).getImage();
               }
               if (cachedImages.containsKey(audioObject)) {
                   return cachedImages.get(audioObject);
               }
               Image image = getPicture((AudioFile)audioObject);
               cachedImages.put(audioObject, image);
               return image;
           }

           @Override
           protected void done() {
               Image image;
               try {
                   image = get();
                   setPicture(audioObject, image, cover);
               } catch (InterruptedException e) {
                   logger.error(LogCategories.IMAGE, e);
               } catch (ExecutionException e) {
                   logger.error(LogCategories.IMAGE, e);
               }
           }
       }.execute();
   }
   
   /**
    * Returns picture for audio file
    * @param audioFile
    * @return
    */
   protected Image getPicture(AudioFile audioFile) {
       Image result = LastFmService.getInstance().getAlbumImage(audioFile.getArtist(), audioFile.getAlbum());
       if (result == null) {
           ImageIcon[] pictures = AudioFilePictureUtils.getPicturesForFile(audioFile, -1, -1);
           if (pictures != null && pictures.length > 0) {
               result = pictures[0].getImage();
           }
       }
       if (result == null) {
           result = ImageLoader.getImage(ImageLoader.NO_COVER).getImage();
       }
       return result;
   }

   protected void setPicture(AudioObject object, Image image, Cover3D cover) {
       if (image == null) {
           cover.setImage(null);
           return;
       }
       
       if (object == null) {
           cover.setImage(ImageUtils.scaleImageBicubic(ImageLoader.getImage(ImageLoader.NO_COVER).getImage(), getImageSize(covers.indexOf(cover)), getImageSize(covers.indexOf(cover))));
       } else {
           int size = getImageSize(covers.indexOf(cover));
           ImageIcon imageScaled = ImageUtils.scaleImageBicubic(image, size, size);
           cover.setImage(imageScaled);
       }
   }
   
   private int getImageSize(int index) {
       if (index == 2) {
           return Constants.FULL_SCREEN_COVER;
       } else if (index == 1 || index == 3) {
           return Constants.FULL_SCREEN_COVER * 3 / 4;
       } else {
           return Constants.FULL_SCREEN_COVER * 9 / 16;
       }
   }   
}
