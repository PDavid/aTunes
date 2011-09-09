/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.statistics.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsHandler;
import net.sourceforge.atunes.kernel.modules.webservices.WebServicesHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Data Source for basic audio object information
 * 
 * @author alex
 * 
 */
public class AudioObjectBasicInfoDataSource implements ContextInformationDataSource {

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

    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            AudioObject audioObject = (AudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            result.put(OUTPUT_AUDIO_OBJECT, audioObject);
            result.put(OUTPUT_IMAGE, getImage(audioObject));
            if (audioObject instanceof AudioFile) {
                result.put(OUTPUT_TITLE, audioObject.getTitleOrFileName());
                result.put(OUTPUT_ARTIST, audioObject.getArtist());
                result.put(OUTPUT_LASTPLAYDATE, getLastPlayDate(audioObject));
            } else if (audioObject instanceof Radio) {
                result.put(OUTPUT_TITLE, ((Radio) audioObject).getName());
                result.put(OUTPUT_ARTIST, ((Radio) audioObject).getUrl());
            } else if (audioObject instanceof PodcastFeedEntry) {
                result.put(OUTPUT_TITLE, ((PodcastFeedEntry) audioObject).getTitle());
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
    private ImageIcon getImage(AudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            ImageIcon localImage = audioObject.getImage(Constants.ALBUM_IMAGE_SIZE);
            if (localImage == null) {
                Image image = WebServicesHandler.getInstance().getLastFmService().getAlbumImage(audioObject.getArtist(), audioObject.getAlbum());
                if (image != null) {
                    localImage = ImageUtils.resize(new ImageIcon(image), Constants.ALBUM_IMAGE_SIZE.getSize(), Constants.ALBUM_IMAGE_SIZE.getSize());
                }
            }
            return localImage;
        } else if (audioObject instanceof Radio) {
            return RadioImageIcon.getIcon();
        } else if (audioObject instanceof PodcastFeedEntry) {
            return RssImageIcon.getIcon();
        }
        return null;
    }

    /**
     * Return last play date for given audio object
     * 
     * @param audioObject
     * @return
     */
    private String getLastPlayDate(AudioObject audioObject) {
        // Get last date played
        AudioFileStats stats = StatisticsHandler.getInstance().getAudioFileStatistics((AudioFile) audioObject);
        if (stats == null) {
            return I18nUtils.getString("SONG_NEVER_PLAYED");
        } else {
            Date date = stats.getLastPlayed();
            // If date is null -> never played
            if (date == null) {
                return I18nUtils.getString("SONG_NEVER_PLAYED");
            } else {
                return StringUtils.getString("<html>", I18nUtils.getString("LAST_DATE_PLAYED"), ":<br/><center> ", DateUtils.toString(date), "<center></html>");
            }
        }
    }
}
