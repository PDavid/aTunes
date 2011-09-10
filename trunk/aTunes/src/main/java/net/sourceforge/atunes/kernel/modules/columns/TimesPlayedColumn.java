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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.statistics.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsHandler;
import net.sourceforge.atunes.model.IAudioObject;

public class TimesPlayedColumn extends AbstractColumn {

    
    private static final long serialVersionUID = 7879150472122090859L;

    public TimesPlayedColumn() {
        super("TIMES_PLAYED", String.class);
        setWidth(100);
        setVisible(false);
        setAlignment(SwingConstants.CENTER);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        int times1 = 0;
        int times2 = 0;
        if (ao1 instanceof AudioFile) {
            AudioFileStats stats1 = StatisticsHandler.getInstance().getAudioFileStatistics((AudioFile) ao1);
            times1 = stats1 != null ? stats1.getTimesPlayed() : 0;
        }
        if (ao2 instanceof AudioFile) {
            AudioFileStats stats2 = StatisticsHandler.getInstance().getAudioFileStatistics((AudioFile) ao2);
            times2 = stats2 != null ? stats2.getTimesPlayed() : 0;
        }
        return ((Integer) times1).compareTo(times2);
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
        if (audioObject instanceof Radio) {
            return "";
        }
        if (audioObject instanceof PodcastFeedEntry) {
            return "";
        }
        // Return times played
        AudioFileStats stats = StatisticsHandler.getInstance().getAudioFileStatistics((AudioFile) audioObject);
        if (stats != null && stats.getTimesPlayed() > 0) {
            return Integer.toString(stats.getTimesPlayed());
        }
        return "";
    }

}
