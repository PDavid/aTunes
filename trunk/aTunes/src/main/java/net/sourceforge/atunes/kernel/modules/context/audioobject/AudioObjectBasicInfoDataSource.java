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

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
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

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Output parameter
     */
    public static final String OUTPUT_AUDIO_OBJECT = INPUT_AUDIO_OBJECT;

    /**
     * Output parameter
     */
    public static final String OUTPUT_IMAGE = "IMAGE";

    /**
     * Output parameter
     */
    public static final String OUTPUT_TITLE = "TITLE";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ARTIST = "ARTIST";

    /**
     * Output parameter
     */
    public static final String OUTPUT_LASTPLAYDATE = "LAST_PLAY_DATE";
    
    private IWebServicesHandler webServicesHandler;
    
    private IOSManager osManager;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private CachedIconFactory rssMediumIcon;
    
    private CachedIconFactory radioMediumIcon;
    
    /**
     * @param radioMediumIcon
     */
    public void setRadioMediumIcon(CachedIconFactory radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}
    
    /**
     * @param rssMediumIcon
     */
    public void setRssMediumIcon(CachedIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}

    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            IAudioObject audioObject = (IAudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            result.put(OUTPUT_AUDIO_OBJECT, audioObject);
            result.put(OUTPUT_IMAGE, getImage(audioObject));
            if (audioObject instanceof AudioFile) {
                result.put(OUTPUT_TITLE, audioObject.getTitleOrFileName());
                result.put(OUTPUT_ARTIST, audioObject.getArtist());
                result.put(OUTPUT_LASTPLAYDATE, getLastPlayDate(audioObject));
            } else if (audioObject instanceof IRadio) {
                result.put(OUTPUT_TITLE, ((IRadio) audioObject).getName());
                result.put(OUTPUT_ARTIST, ((Radio) audioObject).getUrl());
            } else if (audioObject instanceof IPodcastFeedEntry) {
                result.put(OUTPUT_TITLE, ((IPodcastFeedEntry) audioObject).getTitle());
            }
        }
        return result;
    }

    /**
     * Returns image for audio object
     * 
     * @param audioObject
     * @return
     */
    private ImageIcon getImage(IAudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            ImageIcon localImage = audioObject.getImage(Constants.ALBUM_IMAGE_SIZE, osManager);
            if (localImage == null) {
                Image image = webServicesHandler.getAlbumImage(audioObject.getArtist(), audioObject.getAlbum());
                if (image != null) {
                    localImage = ImageUtils.resize(new ImageIcon(image), Constants.ALBUM_IMAGE_SIZE.getSize(), Constants.ALBUM_IMAGE_SIZE.getSize());
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
    private String getLastPlayDate(IAudioObject audioObject) {
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
    
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
}
