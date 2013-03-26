/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.model;

import java.util.Map;

/**
 * Listening statistics
 * 
 * @author alex
 * 
 */
public interface IStatistics {

    /**
     * Gets the albums ranking.
     * 
     * @return the albums ranking
     */
    public RankList<IStatisticsAlbum> getAlbumsRanking();

    /**
     * Gets the artists ranking.
     * 
     * @return the artists ranking
     */
    public RankList<String> getArtistsRanking();

    /**
     * Gets the different audio files played.
     * 
     * @return the different audio files played
     */
    public int getDifferentAudioFilesPlayed();

    /**
     * Gets the audio files ranking.
     * 
     * @return the audio files ranking
     */
    public RankList<String> getAudioFilesRanking();

    /**
     * Gets the audio files stats.
     * 
     * @return the audio files stats
     */
    public Map<String, IAudioObjectStatistics> getAudioFilesStats();

    /**
     * Gets the stats for file.
     * 
     * @param audioFile
     *            the audio file
     * 
     * @return the stats for file
     */
    public IAudioObjectStatistics getStatsForAudioFile(IAudioObject audioFile);

    /**
     * Gets the total plays.
     * 
     * @return the total plays
     */
    public int getTotalPlays();

    /**
     * Sets the different audio files played.
     * 
     * @param differentAudioFilesPlayed
     *            the new different audio files played
     */
    public void setDifferentAudioFilesPlayed(int differentAudioFilesPlayed);

    /**
     * Sets the auio files stats.
     * 
     * @param audioFilesStats
     *            the audio files stats
     */
    public void setAudioFilesStats(
	    Map<String, IAudioObjectStatistics> audioFilesStats);

    /**
     * Sets the total plays.
     * 
     * @param totalPlays
     *            the new total plays
     */
    public void setTotalPlays(int totalPlays);

}