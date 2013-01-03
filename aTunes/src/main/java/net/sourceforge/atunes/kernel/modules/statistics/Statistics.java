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

package net.sourceforge.atunes.kernel.modules.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.model.IStatisticsAlbum;
import net.sourceforge.atunes.model.RankList;

/**
 * Stores statistics about what user listens to
 * @author alex
 *
 */
public class Statistics implements Serializable, IStatistics {

	private static final long serialVersionUID = -3603927907730394505L;

	int totalPlays;
	int differentAudioFilesPlayed;
	/** The audio files ranking. The ranking contains URL of every audio file */
	RankList<String> audioFilesRanking;
	RankList<IStatisticsAlbum> albumsRanking;
	/** The artists ranking. The ranking contains names of artists */
	RankList<String> artistsRanking;
	Map<String, IAudioObjectStatistics> audioFilesStats;

	/**
	 * Instantiates a new repository stats.
	 */
	public Statistics() {
		audioFilesRanking = new RankList<String>();
		albumsRanking = new RankList<IStatisticsAlbum>();
		artistsRanking = new RankList<String>();
		audioFilesStats = new HashMap<String, IAudioObjectStatistics>();
	}

	/**
	 * Gets the albums ranking.
	 * 
	 * @return the albums ranking
	 */
	@Override
	public RankList<IStatisticsAlbum> getAlbumsRanking() {
		return albumsRanking;
	}

	/**
	 * Gets the artists ranking.
	 * 
	 * @return the artists ranking
	 */
	@Override
	public RankList<String> getArtistsRanking() {
		return artistsRanking;
	}

	/**
	 * Gets the different audio files played.
	 * 
	 * @return the different audio files played
	 */
	@Override
	public int getDifferentAudioFilesPlayed() {
		return differentAudioFilesPlayed;
	}

	/**
	 * Gets the audio files ranking.
	 * 
	 * @return the audio files ranking
	 */
	@Override
	public RankList<String> getAudioFilesRanking() {
		return audioFilesRanking;
	}

	/**
	 * Gets the audio files stats.
	 * 
	 * @return the audio files stats
	 */
	@Override
	public Map<String, IAudioObjectStatistics> getAudioFilesStats() {
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
	@Override
	public IAudioObjectStatistics getStatsForAudioFile(final IAudioObject audioFile) {
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
	@Override
	public int getTotalPlays() {
		return totalPlays;
	}

	/**
	 * Sets the different audio files played.
	 * 
	 * @param differentAudioFilesPlayed
	 *            the new different audio files played
	 */
	@Override
	public void setDifferentAudioFilesPlayed(final int differentAudioFilesPlayed) {
		this.differentAudioFilesPlayed = differentAudioFilesPlayed;
	}

	/**
	 * Sets the auio files stats.
	 * 
	 * @param audioFilesStats
	 *            the audio files stats
	 */
	@Override
	public void setAudioFilesStats(final Map<String, IAudioObjectStatistics> audioFilesStats) {
		this.audioFilesStats = audioFilesStats;
	}

	/**
	 * Sets the total plays.
	 * 
	 * @param totalPlays
	 *            the new total plays
	 */
	@Override
	public void setTotalPlays(final int totalPlays) {
		this.totalPlays = totalPlays;
	}
}
