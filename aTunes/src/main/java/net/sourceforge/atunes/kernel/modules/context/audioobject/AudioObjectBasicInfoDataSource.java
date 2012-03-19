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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Data Source for basic audio object information
 * 
 * @author alex
 * 
 */
public class AudioObjectBasicInfoDataSource implements IContextInformationSource {

    private IWebServicesHandler webServicesHandler;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IIconFactory rssMediumIcon;
    
    private IIconFactory radioMediumIcon;
    
    private IAudioObjectImageLocator audioObjectImageLocator;
    
    private IAudioObject audioObject;
    
    private ImageIcon image;
    
    private String title;
    
    private String artist;
    
    private String lastPlayDate;
    
    
    /**
     * @param audioObjectImageLocator
     */
    public void setAudioObjectImageLocator(IAudioObjectImageLocator audioObjectImageLocator) {
		this.audioObjectImageLocator = audioObjectImageLocator;
	}
    
    /**
     * @param radioMediumIcon
     */
    public void setRadioMediumIcon(IIconFactory radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}
    
    /**
     * @param rssMediumIcon
     */
    public void setRssMediumIcon(IIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}
    
    @Override
    public void getData(IAudioObject audioObject) {
    	this.audioObject = audioObject;
    	this.image = getImageData(audioObject);
    	if (audioObject instanceof ILocalAudioObject) {
    		this.title = audioObject.getTitleOrFileName();
    		this.artist = audioObject.getArtist();
    		this.lastPlayDate = getLastPlayDateData(audioObject);
    	} else if (audioObject instanceof IRadio) {
    		this.title = ((IRadio) audioObject).getName();
    		this.artist = ((IRadio) audioObject).getUrl();
    	} else if (audioObject instanceof IPodcastFeedEntry) {
    		this.title = ((IPodcastFeedEntry) audioObject).getTitle();
    	}
    }
    
    /**
     * @return
     */
    public IAudioObject getAudioObject() {
		return audioObject;
	}
    
    /**
     * @return
     */
    public ImageIcon getImage() {
		return image;
	}
    
    /**
     * @return
     */
    public String getTitle() {
		return title;
	}
    
    /**
     * @return
     */
    public String getArtist() {
		return artist;
	}
    
    /**
     * @return
     */
    public String getLastPlayDate() {
		return lastPlayDate;
	}

    /**
     * Returns image for audio object
     * 
     * @param audioObject
     * @return
     */
    private ImageIcon getImageData(IAudioObject audioObject) {
        if (audioObject instanceof ILocalAudioObject) {
            ImageIcon localImage = audioObjectImageLocator.getImage(audioObject, Constants.ALBUM_IMAGE_SIZE);
            if (localImage == null) {
                ImageIcon albumImage = webServicesHandler.getAlbumImage(audioObject.getArtist(), audioObject.getAlbum());
                if (albumImage != null) {
                    localImage = ImageUtils.resize(albumImage, Constants.ALBUM_IMAGE_SIZE.getSize(), Constants.ALBUM_IMAGE_SIZE.getSize());
                }
            }
            return localImage;
        } else if (audioObject instanceof IRadio) {
            return radioMediumIcon.getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
        } else if (audioObject instanceof IPodcastFeedEntry) {
            return rssMediumIcon.getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
        }
        return null;
    }

    /**
     * Return last play date for given audio object
     * 
     * @param audioObject
     * @return
     */
    private String getLastPlayDateData(IAudioObject audioObject) {
        // Get last date played
        IAudioObjectStatistics stats = Context.getBean(IStatisticsHandler.class).getAudioObjectStatistics((ILocalAudioObject) audioObject);
        if (stats == null) {
            return I18nUtils.getString("SONG_NEVER_PLAYED");
        } else {
            DateTime date = stats.getLastPlayed();
            // If date is null -> never played
            if (date == null) {
                return I18nUtils.getString("SONG_NEVER_PLAYED");
            } else {
                return StringUtils.getString("<html>", I18nUtils.getString("LAST_DATE_PLAYED"), ":<br/><center> ", DateTimeFormat.shortDateTime().print(date), "<center></html>");
            }
        }
    }
    
    /**
     * @param webServicesHandler
     */
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
}
