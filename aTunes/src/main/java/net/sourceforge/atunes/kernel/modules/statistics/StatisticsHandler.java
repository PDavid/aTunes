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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.model.IStatisticsAlbum;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.RankList;

/**
 * Responsible of managing statistics about listening habits of user
 * 
 * @author alex
 * 
 */
public final class StatisticsHandler extends AbstractHandler implements
		IStatisticsHandler {

	private IStatistics statistics = new Statistics();

	private StatsDialogController controller;

	private IStateService stateService;

	private IRepositoryHandler repositoryHandler;

	private ITaskService taskService;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * Fill stats.
	 * 
	 * @param repository
	 *            the repository
	 * @param audioObject
	 *            the audio file
	 */
	private void fillStats(final IAudioObject audioObject) {
		String songPath = audioObject.getUrl();
		if (audioObjectInRepository(songPath)) {
			updateStatistics(audioObject, songPath);
		}
	}

	/**
	 * Updates statistics for audio object
	 * 
	 * @param audioObject
	 * @param songPath
	 */
	private void updateStatistics(final IAudioObject audioObject,
			final String songPath) {
		this.statistics.setTotalPlays(this.statistics.getTotalPlays() + 1);

		IAudioObjectStatistics stats = getOrCreateAudioObjectStatistics(songPath);
		stats.increaseStatistics();

		updateAudioObjectRanking(songPath);

		String artist = audioObject.getArtist(this.unknownObjectChecker);
		IArtist a = this.repositoryHandler.getArtist(artist);

		// Unknown artist -> don't fill artist stats
		if (a == null || this.unknownObjectChecker.isUnknownArtist(a.getName())) {
			return;
		}

		updateArtistRanking(a);
		updateAlbumRanking(audioObject, artist, a);
	}

	/**
	 * Returns audio object statistics or create a new one
	 * 
	 * @param songPath
	 * @return
	 */
	private IAudioObjectStatistics getOrCreateAudioObjectStatistics(
			final String songPath) {
		IAudioObjectStatistics stats = this.statistics.getAudioFilesStats()
				.get(songPath);
		if (stats == null) {
			stats = new AudioObjectStats();
			this.statistics.getAudioFilesStats().put(songPath, stats);
			this.statistics.setDifferentAudioFilesPlayed(this.statistics
					.getDifferentAudioFilesPlayed() + 1);
		}
		return stats;
	}

	/**
	 * Updates audio object ranking
	 * 
	 * @param songPath
	 */
	private void updateAudioObjectRanking(final String songPath) {
		this.statistics.getAudioFilesRanking().addItem(songPath);
	}

	/**
	 * Updates album ranking
	 * 
	 * @param audioObject
	 * @param artist
	 * @param a
	 */
	private void updateAlbumRanking(final IAudioObject audioObject,
			final String artist, final IArtist a) {
		String album = audioObject.getAlbum(this.unknownObjectChecker);

		IAlbum alb = a.getAlbum(album);

		// Unknown album -> don't fill album stats
		if (alb == null
				|| this.unknownObjectChecker.isUnknownAlbum(alb.getName())) {
			return;
		}

		IStatisticsAlbum statisticsAlbum = new StatisticsAlbum(artist, album);
		this.statistics.getAlbumsRanking().addItem(statisticsAlbum);
	}

	/**
	 * Updates artist ranking
	 * 
	 * @param a
	 */
	private void updateArtistRanking(final IArtist a) {
		this.statistics.getArtistsRanking().addItem(a.getName());
	}

	/**
	 * Returns if audio object is in repository
	 * 
	 * @param audioObjectPath
	 * @return
	 */
	private boolean audioObjectInRepository(final String audioObjectPath) {
		return this.repositoryHandler.getFile(audioObjectPath) != null;
	}

	@Override
	public int getArtistTimesPlayed(final IArtist artist) {
		if (this.statistics.getArtistsRanking().getCount(artist.getName()) != null) {
			return this.statistics.getArtistsRanking().getCount(
					artist.getName());
		}
		return 0;
	}

	@Override
	public int getDifferentAudioObjectsPlayed() {
		return this.statistics.getDifferentAudioFilesPlayed();
	}

	@Override
	public List<IAlbum> getMostPlayedAlbums(final int n) {
		List<IStatisticsAlbum> statisticsAlbums = this.statistics
				.getAlbumsRanking().getNFirstElements(n);
		List<IAlbum> albums = new ArrayList<IAlbum>();
		for (IStatisticsAlbum statisticAlbum : statisticsAlbums) {
			IArtist artist = this.repositoryHandler.getArtist(statisticAlbum
					.getArtist());
			if (artist != null) {
				IAlbum album = artist.getAlbum(statisticAlbum.getAlbum());
				if (album != null) {
					albums.add(album);
				}
			}
		}
		return albums;
	}

	@Override
	public List<Integer> getMostPlayedAlbumsCount(final int n) {
		return this.statistics.getAlbumsRanking().getNFirstElementCounts(n);
	}

	@Override
	public List<IArtist> getMostPlayedArtists(final int n) {
		List<String> artistsNames = this.statistics.getArtistsRanking()
				.getNFirstElements(n);
		List<IArtist> artists = new ArrayList<IArtist>();
		for (String artistName : artistsNames) {
			artists.add(this.repositoryHandler.getArtist(artistName));
		}
		return artists;
	}

	@Override
	public List<Integer> getMostPlayedArtistsCount(final int n) {
		return this.statistics.getArtistsRanking().getNFirstElementCounts(n);
	}

	@Override
	public List<IAudioObject> getMostPlayedAudioObjects(final int n) {
		List<String> audioFilesUrls = this.statistics.getAudioFilesRanking()
				.getNFirstElements(n);
		List<IAudioObject> audioFiles = new ArrayList<IAudioObject>();
		for (String audioFileUrl : audioFilesUrls) {
			audioFiles
					.add(this.repositoryHandler.getFileIfLoaded(audioFileUrl));
		}
		return audioFiles;
	}

	/**
	 * @return
	 */
	IStatistics getStatistics() {
		return this.statistics;
	}

	/**
	 * @param statistics
	 */
	void setStatistics(final IStatistics statistics) {
		this.statistics = statistics;
	}

	@Override
	public List<Integer> getMostPlayedAudioObjectsCount(final int n) {
		return this.statistics.getAudioFilesRanking().getNFirstElementCounts(n);
	}

	@Override
	public IAudioObjectStatistics getAudioObjectStatistics(
			final IAudioObject audioObject) {
		return this.statistics.getStatsForAudioFile(audioObject);
	}

	@Override
	public int getTotalAudioObjectsPlayed() {
		return this.statistics.getTotalPlays();
	}

	@Override
	public List<IAudioObject> getUnplayedAudioObjects() {
		List<IAudioObject> unplayedAudioFiles = new ArrayList<IAudioObject>(
				this.repositoryHandler.getAudioFilesList());
		unplayedAudioFiles.removeAll(this.statistics.getAudioFilesRanking()
				.getNFirstElements(-1));
		return unplayedAudioFiles;
	}

	@Override
	public void updateAudioObjectStatistics(final IAudioObject audioObject) {
		if (audioObject != null) {
			fillStats(audioObject);
			// Store stats
			storeStatistics();
			// Update dialog if visible
			if (this.controller != null
					&& this.controller.getComponentControlled().isVisible()) {
				this.controller.updateStats();
			}
		}
	}

	@Override
	public void replaceArtist(final String oldArtist, final String newArtist) {
		// Update artist ranking
		this.statistics.getArtistsRanking().replaceItem(oldArtist, newArtist);

		// Update album ranking
		RankList<IStatisticsAlbum> albumsRanking = this.statistics
				.getAlbumsRanking();
		for (IStatisticsAlbum album : albumsRanking.getOrder()) {
			if (album.getArtist().equals(oldArtist)) {
				this.statistics.getAlbumsRanking().replaceItem(album,
						new StatisticsAlbum(newArtist, album.getAlbum()));
			}
		}
		storeStatistics();
	}

	@Override
	public void replaceAlbum(final String artist, final String oldAlbum,
			final String newAlbum) {
		RankList<IStatisticsAlbum> albumsRanking = this.statistics
				.getAlbumsRanking();
		for (IStatisticsAlbum album : albumsRanking.getOrder()) {
			if (album.getArtist().equals(artist)
					&& album.getAlbum().equals(oldAlbum)) {
				this.statistics.getAlbumsRanking().replaceItem(album,
						new StatisticsAlbum(artist, newAlbum));
			}
		}
		storeStatistics();
	}

	@Override
	public void updateFileName(final ILocalAudioObject audioFile,
			final String absolutePath, final String newAbsolutePath) {
		this.statistics.getAudioFilesRanking().replaceItem(absolutePath,
				newAbsolutePath);
		this.statistics.getAudioFilesStats().put(newAbsolutePath,
				this.statistics.getAudioFilesStats().get(absolutePath));
		this.statistics.getAudioFilesStats().remove(absolutePath);
		storeStatistics();
	}

	@Override
	public void showStatistics() {
		if (this.controller == null) {
			this.controller = getBean(StatsDialogController.class);
		}
		this.controller.showStats();
	}

	/**
	 * Stores statistics
	 */
	private void storeStatistics() {
		new StoreStatistics(this.taskService, this.stateService, this)
				.storeStatistics();
	}
}
