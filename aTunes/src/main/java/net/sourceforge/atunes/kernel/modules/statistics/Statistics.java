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

package net.sourceforge.atunes.kernel.modules.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.utils.RankList;

public class Statistics implements Serializable {

    private static final long serialVersionUID = -3603927907730394505L;

    private int totalPlays;
    private int differentAudioFilesPlayed;
    /** The audio files ranking. The ranking contains URL of every audio file */
    private RankList<String> audioFilesRanking;
    private RankList<StatisticsAlbum> albumsRanking;
    /** The artists ranking. The ranking contains names of artists */
    private RankList<String> artistsRanking;
    private Map<String, AudioObjectStats> audioFilesStats;

    /**
     * Instantiates a new repository stats.
     */
    public Statistics() {
        audioFilesRanking = new RankList<String>();
        albumsRanking = new RankList<StatisticsAlbum>();
        artistsRanking = new RankList<String>();
        audioFilesStats = new HashMap<String, AudioObjectStats>();
    }

    /**
     * Gets the albums ranking.
     * 
     * @return the albums ranking
     */
    public RankList<StatisticsAlbum> getAlbumsRanking() {
        return albumsRanking;
    }

    /**
     * Gets the artists ranking.
     * 
     * @return the artists ranking
     */
    public RankList<String> getArtistsRanking() {
        return artistsRanking;
    }

    /**
     * Gets the different audio files played.
     * 
     * @return the different audio files played
     */
    public int getDifferentAudioFilesPlayed() {
        return differentAudioFilesPlayed;
    }

    /**
     * Gets the audio files ranking.
     * 
     * @return the audio files ranking
     */
    public RankList<String> getAudioFilesRanking() {
        return audioFilesRanking;
    }

    /**
     * Gets the audio files stats.
     * 
     * @return the audio files stats
     */
    public Map<String, AudioObjectStats> getAudioFilesStats() {
        return audioFilesStats;
    }

    /**
     * Gets the stats for file.
     * 
     * @param audioFile
     *            the audio file
     * 
     * @return the stats for file
     */
    public IAudioObjectStatistics getStatsForAudioFile(IAudioObject audioFile) {
        if (audioFile != null) {
            return audioFilesStats.get(audioFile.getUrl());
        }
        return null;
    }

    /**
     * Gets the total plays.
     * 
     * @return the total plays
     */
    public int getTotalPlays() {
        return totalPlays;
    }

    /**
     * Sets the different audio files played.
     * 
     * @param differentAudioFilesPlayed
     *            the new different audio files played
     */
    public void setDifferentAudioFilesPlayed(int differentAudioFilesPlayed) {
        this.differentAudioFilesPlayed = differentAudioFilesPlayed;
    }

    /**
     * Sets the auio files stats.
     * 
     * @param audioFilesStats
     *            the audio files stats
     */
    public void setAudioFilesStats(Map<String, AudioObjectStats> audioFilesStats) {
        this.audioFilesStats = audioFilesStats;
    }

    /**
     * Sets the total plays.
     * 
     * @param totalPlays
     *            the new total plays
     */
    public void setTotalPlays(int totalPlays) {
        this.totalPlays = totalPlays;
    }

}
