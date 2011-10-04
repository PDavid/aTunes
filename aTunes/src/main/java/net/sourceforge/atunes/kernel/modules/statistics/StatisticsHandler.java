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

import net.sourceforge.atunes.gui.views.dialogs.StatsDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.RankList;

public final class StatisticsHandler extends AbstractHandler implements IStatisticsHandler {

    private Statistics statistics;
    
    private StatsDialogController controller;

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                statistics = getBean(IStateHandler.class).retrieveStatisticsCache();
            }
        };
    }

    /**
     * Fill stats.
     * 
     * @param repository
     *            the repository
     * @param audioFile
     *            the audio file
     */
    private void fillStats(IAudioObject audioFile) {
        String songPath = audioFile.getUrl();
        if (getBean(IRepositoryHandler.class).getFile(songPath) != null) {
            statistics.setTotalPlays(statistics.getTotalPlays() + 1);

            AudioObjectStats stats = statistics.getAudioFilesStats().get(songPath);
            if (stats == null) {
                stats = new AudioObjectStats();
                statistics.getAudioFilesStats().put(songPath, stats);
                statistics.setDifferentAudioFilesPlayed(statistics.getDifferentAudioFilesPlayed() + 1);
            }
            stats.increaseStatistics();
            statistics.getAudioFilesRanking().addItem(audioFile.getUrl());

            String artist = audioFile.getArtist();

            Artist a = getBean(IRepositoryHandler.class).getArtist(artist);

            // Unknown artist -> don't fill artist stats
            if (a == null) {
                return;
            }

            statistics.getArtistsRanking().addItem(a.getName());

            String album = audioFile.getAlbum();

            Album alb = a.getAlbum(album);

            // Unknown album -> don't fill album stats
            if (alb == null) {
                return;
            }

            StatisticsAlbum statisticsAlbum = new StatisticsAlbum(artist, album);
            statistics.getAlbumsRanking().addItem(statisticsAlbum);
        }
    }

    @Override
    public int getArtistTimesPlayed(Artist artist) {
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
    public List<Album> getMostPlayedAlbums(int n) {
        List<StatisticsAlbum> statisticsAlbums = statistics.getAlbumsRanking().getNFirstElements(n);
        if (statisticsAlbums != null) {
            List<Album> albums = new ArrayList<Album>();
            for (StatisticsAlbum statisticAlbum : statisticsAlbums) {
            	Artist artist = getBean(IRepositoryHandler.class).getArtist(statisticAlbum.getArtist());
            	if (artist != null) {
            		Album album = artist.getAlbum(statisticAlbum.getAlbum());
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
    public List<Artist> getMostPlayedArtists(int n) {
        List<String> artistsNames = statistics.getArtistsRanking().getNFirstElements(n);
        if (artistsNames != null) {
            List<Artist> artists = new ArrayList<Artist>();
            for (String artistName : artistsNames) {
                artists.add(getBean(IRepositoryHandler.class).getArtist(artistName));
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
                audioFiles.add(getBean(IRepositoryHandler.class).getFileIfLoaded(audioFileUrl));
            }
            return audioFiles;
        }
        return null;
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
        List<IAudioObject> unplayedAudioFiles = new ArrayList<IAudioObject>(getBean(IRepositoryHandler.class).getAudioFilesList());
        unplayedAudioFiles.removeAll(statistics.getAudioFilesRanking().getNFirstElements(-1));
        return unplayedAudioFiles;
    }

    @Override
    public void updateAudioObjectStatistics(IAudioObject audioObject) {
        fillStats(audioObject);
        // Store stats
        storeStatistics();
        // Update dialog if visible
        if (controller != null && controller.getComponentControlled().isVisible()) {
            controller.updateStats();
        }
    }

    /**
     * Stores statistics
     * 
     */
    private void storeStatistics() {
    	getBean(ITaskService.class).submitNow("Persist statistics", new Runnable() {
    		
            @Override
            public void run() {
            	getBean(IStateHandler.class).persistStatisticsCache(statistics);
            };
        });
    }

    @Override
    public void replaceArtist(String oldArtist, String newArtist) {
        // Update artist ranking
        statistics.getArtistsRanking().replaceItem(oldArtist, newArtist);

        // Update album ranking
        RankList<StatisticsAlbum> albumsRanking = statistics.getAlbumsRanking();
        for (StatisticsAlbum album : albumsRanking.getOrder()) {
            if (album.getArtist().equals(oldArtist)) {
                statistics.getAlbumsRanking().replaceItem(album, new StatisticsAlbum(newArtist, album.getAlbum()));
            }
        }
        storeStatistics();
    }

    @Override
    public void replaceAlbum(String artist, String oldAlbum, String newAlbum) {
        RankList<StatisticsAlbum> albumsRanking = statistics.getAlbumsRanking();
        for (StatisticsAlbum album : albumsRanking.getOrder()) {
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
			controller = new StatsDialogController(new StatsDialog(getFrame().getFrame(), getBean(ILookAndFeelManager.class)), getState(), this, getBean(ILookAndFeelManager.class), getBean(IRepositoryHandler.class)); 
		}
		controller.showStats();
	}
	
}
