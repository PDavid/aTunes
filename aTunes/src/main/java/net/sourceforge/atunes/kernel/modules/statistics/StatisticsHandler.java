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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.model.IStatisticsAlbum;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.RankList;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

public final class StatisticsHandler extends AbstractHandler implements IStatisticsHandler {

    private IStatistics statistics;
    
    private StatsDialogController controller;
    
    private IStateHandler stateHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private ITaskService taskService;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    /**
     * @param stateHandler
     */
    public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new StatisticsLoadTask(this, stateHandler);
    }

    /**
     * Fill stats.
     * 
     * @param repository
     *            the repository
     * @param audioObject
     *            the audio file
     */
    private void fillStats(IAudioObject audioObject) {
        String songPath = audioObject.getUrl();
        if (audioObjectInRepository(songPath)) {
            updateStatistics(audioObject, songPath);
        }
    }

	/**
	 * Updates statistics for audio object
	 * @param audioObject
	 * @param songPath
	 */
	private void updateStatistics(IAudioObject audioObject, String songPath) {
		statistics.setTotalPlays(statistics.getTotalPlays() + 1);

		IAudioObjectStatistics stats = getOrCreateAudioObjectStatistics(songPath);
		stats.increaseStatistics();
		
		updateAudioObjectRanking(songPath);

		String artist = audioObject.getArtist();
		IArtist a = repositoryHandler.getArtist(artist);

		// Unknown artist -> don't fill artist stats
		if (a == null || UnknownObjectCheck.isUnknownArtist(a.getName())) {
		    return;
		}

		updateArtistRanking(a);
		updateAlbumRanking(audioObject, artist, a);
	}

	/**
	 * Returns audio object statistics or create a new one
	 * @param songPath
	 * @return
	 */
	private IAudioObjectStatistics getOrCreateAudioObjectStatistics(String songPath) {
		IAudioObjectStatistics stats = statistics.getAudioFilesStats().get(songPath);
		if (stats == null) {
		    stats = new AudioObjectStats();
		    statistics.getAudioFilesStats().put(songPath, stats);
		    statistics.setDifferentAudioFilesPlayed(statistics.getDifferentAudioFilesPlayed() + 1);
		}
		return stats;
	}

	/**
	 * Updates audio object ranking
	 * @param songPath
	 */
	private void updateAudioObjectRanking(String songPath) {
		statistics.getAudioFilesRanking().addItem(songPath);
	}

	/**
	 * Updates album ranking
	 * @param audioObject
	 * @param artist
	 * @param a
	 */
	private void updateAlbumRanking(IAudioObject audioObject, String artist, IArtist a) {
		String album = audioObject.getAlbum();

		IAlbum alb = a.getAlbum(album);

		// Unknown album -> don't fill album stats
		if (alb == null || UnknownObjectCheck.isUnknownAlbum(alb.getName())) {
		    return;
		}

		IStatisticsAlbum statisticsAlbum = new StatisticsAlbum(artist, album);
		statistics.getAlbumsRanking().addItem(statisticsAlbum);
	}

	/**
	 * Updates artist ranking
	 * @param a
	 */
	private void updateArtistRanking(IArtist a) {
		statistics.getArtistsRanking().addItem(a.getName());
	}

	/**
	 * Returns if audio object is in repository
	 * @param audioObjectPath
	 * @return
	 */
	private boolean audioObjectInRepository(String audioObjectPath) {
		return repositoryHandler.getFile(audioObjectPath) != null;
	}

    @Override
    public int getArtistTimesPlayed(IArtist artist) {
        if (statistics.getArtistsRanking().getCount(artist.getName()) != null) {
            return statistics.getArtistsRanking().getCount(artist.getName());
        }
        return 0;
    }

    @Override
    public int getDifferentAudioObjectsPlayed() {
        return statistics.getDifferentAudioFilesPlayed();
    }

    @Override
    public List<IAlbum> getMostPlayedAlbums(int n) {
        List<IStatisticsAlbum> statisticsAlbums = statistics.getAlbumsRanking().getNFirstElements(n);
        if (statisticsAlbums != null) {
            List<IAlbum> albums = new ArrayList<IAlbum>();
            for (IStatisticsAlbum statisticAlbum : statisticsAlbums) {
            	IArtist artist = repositoryHandler.getArtist(statisticAlbum.getArtist());
            	if (artist != null) {
            		IAlbum album = artist.getAlbum(statisticAlbum.getAlbum());
            		if (album != null) {
            			albums.add(album);
            		}
            	}
            }
            return albums;
        }
        return null;
    }

    @Override
    public List<Integer> getMostPlayedAlbumsCount(int n) {
        return statistics.getAlbumsRanking().getNFirstElementCounts(n);
    }

    @Override
    public List<IArtist> getMostPlayedArtists(int n) {
        List<String> artistsNames = statistics.getArtistsRanking().getNFirstElements(n);
        if (artistsNames != null) {
            List<IArtist> artists = new ArrayList<IArtist>();
            for (String artistName : artistsNames) {
                artists.add(repositoryHandler.getArtist(artistName));
            }
            return artists;
        }
        return null;
    }

    @Override
    public List<Integer> getMostPlayedArtistsCount(int n) {
        return statistics.getArtistsRanking().getNFirstElementCounts(n);
    }

    @Override
    public List<IAudioObject> getMostPlayedAudioObjects(int n) {
        List<String> audioFilesUrls = statistics.getAudioFilesRanking().getNFirstElements(n);
        if (audioFilesUrls != null) {
            List<IAudioObject> audioFiles = new ArrayList<IAudioObject>();
            for (String audioFileUrl : audioFilesUrls) {
                audioFiles.add(repositoryHandler.getFileIfLoaded(audioFileUrl));
            }
            return audioFiles;
        }
        return null;
    }

    /**
     * @return
     */
    IStatistics getStatistics() {
		return statistics;
	}
    
    /**
     * @param statistics
     */
    void setStatistics(IStatistics statistics) {
		this.statistics = statistics;
	}
    
    @Override
    public List<Integer> getMostPlayedAudioObjectsCount(int n) {
        return statistics.getAudioFilesRanking().getNFirstElementCounts(n);
    }

    @Override
    public IAudioObjectStatistics getAudioObjectStatistics(IAudioObject audioObject) {
        return statistics.getStatsForAudioFile(audioObject);
    }

    @Override
    public int getTotalAudioObjectsPlayed() {
        return statistics.getTotalPlays();
    }

    @Override
    public List<IAudioObject> getUnplayedAudioObjects() {
        List<IAudioObject> unplayedAudioFiles = new ArrayList<IAudioObject>(repositoryHandler.getAudioFilesList());
        unplayedAudioFiles.removeAll(statistics.getAudioFilesRanking().getNFirstElements(-1));
        return unplayedAudioFiles;
    }

    @Override
    public void updateAudioObjectStatistics(IAudioObject audioObject) {
    	if (audioObject != null) {
    		fillStats(audioObject);
    		// Store stats
    		storeStatistics();
    		// Update dialog if visible
    		if (controller != null && controller.getComponentControlled().isVisible()) {
    			controller.updateStats();
    		}
    	}
    }

    @Override
    public void replaceArtist(String oldArtist, String newArtist) {
        // Update artist ranking
        statistics.getArtistsRanking().replaceItem(oldArtist, newArtist);

        // Update album ranking
        RankList<IStatisticsAlbum> albumsRanking = statistics.getAlbumsRanking();
        for (IStatisticsAlbum album : albumsRanking.getOrder()) {
            if (album.getArtist().equals(oldArtist)) {
                statistics.getAlbumsRanking().replaceItem(album, new StatisticsAlbum(newArtist, album.getAlbum()));
            }
        }
        storeStatistics();
    }

    @Override
    public void replaceAlbum(String artist, String oldAlbum, String newAlbum) {
        RankList<IStatisticsAlbum> albumsRanking = statistics.getAlbumsRanking();
        for (IStatisticsAlbum album : albumsRanking.getOrder()) {
            if (album.getArtist().equals(artist) && album.getAlbum().equals(oldAlbum)) {
                statistics.getAlbumsRanking().replaceItem(album, new StatisticsAlbum(artist, newAlbum));
            }
        }
        storeStatistics();
    }

    @Override
    public void updateFileName(ILocalAudioObject audioFile, String absolutePath, String newAbsolutePath) {
        statistics.getAudioFilesRanking().replaceItem(absolutePath, newAbsolutePath);
        statistics.getAudioFilesStats().put(newAbsolutePath, statistics.getAudioFilesStats().get(absolutePath));
        statistics.getAudioFilesStats().remove(absolutePath);
        storeStatistics();
    }
    
    
    @Override
    public void showStatistics() {
		if (controller == null) {
			controller = new StatsDialogController(getBean(StatsDialog.class), getState(), this, lookAndFeelManager, repositoryHandler); 
		}
		controller.showStats();
	}
    
    /**
     * Stores statistics
     */
    private void storeStatistics() {
    	new StoreStatistics(taskService, stateHandler, this).storeStatistics();
    }
}
