/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.RankList;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

public final class StatisticsHandler extends AbstractHandler {

    private static StatisticsHandler instance;

    private Statistics statistics;

    public static StatisticsHandler getInstance() {
        if (instance == null) {
            instance = new StatisticsHandler();
        }
        return instance;
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStarted(List<AudioObject> playList) {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
    }

    @Override
    protected void initHandler() {
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                statistics = ApplicationStateHandler.getInstance().retrieveStatisticsCache();
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
    private void fillStats(AudioFile audioFile) {
        String songPath = audioFile.getUrl();
        if (RepositoryHandler.getInstance().getAudioFilesMap().containsKey(songPath)) {
            statistics.setTotalPlays(statistics.getTotalPlays() + 1);

            AudioFileStats stats = statistics.getAudioFilesStats().get(songPath);
            if (stats != null) {
                stats.setLastPlayed(new Date());
                stats.increaseTimesPlayed();
            } else {
                stats = new AudioFileStats();
                statistics.getAudioFilesStats().put(songPath, stats);
                statistics.setDifferentAudioFilesPlayed(statistics.getDifferentAudioFilesPlayed() + 1);
            }
            statistics.getAudioFilesRanking().addItem(audioFile.getUrl());

            String artist = audioFile.getArtist();

            Artist a = RepositoryHandler.getInstance().getArtistStructure().get(artist);

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

    /**
     * Gets the album most played.
     * 
     * @return the album most played
     */

    public Map<String, Integer> getAlbumMostPlayed() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        if (statistics.getAlbumsRanking().size() > 0) {
            String firstAlbum = statistics.getAlbumsRanking().getNFirstElements(1).get(0).toString();
            Integer count = statistics.getAlbumsRanking().getNFirstElementCounts(1).get(0);
            result.put(firstAlbum, count);
        } else {
            result.put(null, 0);
        }
        return result;
    }

    /**
     * Gets the album times played.
     * 
     * @param audioFile
     *            the audio file
     * 
     * @return the album times played
     */

    public Integer getAlbumTimesPlayed(AudioFile audioFile) {
        if (audioFile != null && statistics.getAlbumsRanking().getCount(new StatisticsAlbum(audioFile.getArtist(), audioFile.getAlbum())) != null) {
            return statistics.getAlbumsRanking().getCount(new StatisticsAlbum(audioFile.getArtist(), audioFile.getAlbum()));
        }
        return 0;
    }

    /**
     * Gets the artist most played.
     * 
     * @return the artist most played
     */

    public Map<String, Integer> getArtistMostPlayed() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        if (statistics.getArtistsRanking().size() > 0) {
            String firstArtist = statistics.getArtistsRanking().getNFirstElements(1).get(0);
            Integer count = statistics.getArtistsRanking().getNFirstElementCounts(1).get(0);
            result.put(firstArtist, count);
        } else {
            result.put(null, 0);
        }
        return result;
    }

    /**
     * Gets the artist times played.
     * 
     * @param audioFile
     *            the audio file
     * 
     * @return the artist times played
     */

    public Integer getArtistTimesPlayed(Artist artist) {
        if (statistics.getArtistsRanking().getCount(artist.getName()) != null) {
            return statistics.getArtistsRanking().getCount(artist.getName());
        }
        return 0;
    }

    /**
     * Gets the different audio files played.
     * 
     * @return the different audio files played
     */
    public int getDifferentAudioFilesPlayed() {
        return statistics.getDifferentAudioFilesPlayed();
    }

    /**
     * Gets the most played albums.
     * 
     * @param n
     *            the n
     * 
     * @return the most played albums
     */
    public List<Album> getMostPlayedAlbums(int n) {
        List<StatisticsAlbum> statisticsAlbums = statistics.getAlbumsRanking().getNFirstElements(n);
        if (statisticsAlbums != null) {
            List<Album> albums = new ArrayList<Album>();
            for (StatisticsAlbum statisticAlbum : statisticsAlbums) {
            	Artist artist = RepositoryHandler.getInstance().getArtistStructure().get(statisticAlbum.getArtist());
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

    /**
     * Gets the most played albums count
     * 
     * @param n
     * @return
     */
    public List<Integer> getMostPlayedAlbumsCount(int n) {
        return statistics.getAlbumsRanking().getNFirstElementCounts(n);
    }

    /**
     * Gets the most played artists.
     * 
     * @param n
     *            the n
     * 
     * @return the most played artists
     */
    public List<Artist> getMostPlayedArtists(int n) {
        List<String> artistsNames = statistics.getArtistsRanking().getNFirstElements(n);
        if (artistsNames != null) {
            List<Artist> artists = new ArrayList<Artist>();
            for (String artistName : artistsNames) {
                artists.add(RepositoryHandler.getInstance().getArtistStructure().get(artistName));
            }
            return artists;
        }
        return null;
    }

    /**
     * Gets the most played artists count
     * 
     * @param n
     * @return
     */
    public List<Integer> getMostPlayedArtistsCount(int n) {
        return statistics.getArtistsRanking().getNFirstElementCounts(n);
    }

    /**
     * Gets the most played audio files.
     * 
     * @param n
     *            the n
     * 
     * @return the most played audio files
     */
    public List<AudioFile> getMostPlayedAudioFiles(int n) {
        List<String> audioFilesUrls = statistics.getAudioFilesRanking().getNFirstElements(n);
        if (audioFilesUrls != null) {
            List<AudioFile> audioFiles = new ArrayList<AudioFile>();
            for (String audioFileUrl : audioFilesUrls) {
                audioFiles.add(RepositoryHandler.getInstance().getFileIfLoaded(audioFileUrl));
            }
            return audioFiles;
        }
        return null;
    }

    /**
     * Gets the most played audio files count
     * 
     * @param n
     * @return
     */
    public List<Integer> getMostPlayedAudioFilesCount(int n) {
        return statistics.getAudioFilesRanking().getNFirstElementCounts(n);
    }

    /**
     * Gets the audio file most played.
     * 
     * @return the audio file most played
     */

    public Map<AudioFile, Integer> getAudioFileMostPlayed() {
        Map<AudioFile, Integer> result = new HashMap<AudioFile, Integer>();
        if (statistics.getAudioFilesRanking().size() > 0) {
            AudioFile firstAudioFile = getMostPlayedAudioFiles(1).get(0);
            Integer count = statistics.getAudioFilesRanking().getNFirstElementCounts(1).get(0);
            result.put(firstAudioFile, count);
        } else {
            result.put(null, 0);
        }
        return result;
    }

    /**
     * Gets the audio files played.
     * 
     * @return the audio files played
     */

    public String getAudioFilesPlayed() {
        int totalPlays = statistics.getDifferentAudioFilesPlayed();
        int total = RepositoryHandler.getInstance().getNumberOfFiles();
        float perCent = total == 0 ? 0 : (float) totalPlays / (float) total * 100;
        return StringUtils.getString(totalPlays, " / ", total, " (", StringUtils.toString(perCent, 2), "%)");
    }

    /**
     * Gets the audio file statistics.
     * 
     * @param audioFile
     *            the audio file
     * 
     * @return the audio file statistics
     */
    public AudioFileStats getAudioFileStatistics(AudioFile audioFile) {
        return statistics.getStatsForAudioFile(audioFile);
    }

    /**
     * Gets the total audio files played.
     * 
     * @return the total audio files played
     */
    public int getTotalAudioFilesPlayed() {
        return statistics.getTotalPlays();
    }

    /**
     * Gets the unplayed audio files.
     * 
     * @return the unplayed audio files
     */
    public List<AudioFile> getUnplayedAudioFiles() {
        List<AudioFile> unplayedAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();
        unplayedAudioFiles.removeAll(statistics.getAudioFilesRanking().getNFirstElements(-1));
        return unplayedAudioFiles;
    }

    /**
     * Sets the audio file statistics.
     * 
     * @param audioFile
     *            the new audio file statistics
     */
    public void setAudioFileStatistics(AudioFile audioFile) {
        fillStats(audioFile);
        // Store stats
        storeStatistics();
    }

    /**
     * Stores statistics
     * 
     */
    private void storeStatistics() {
        Thread t = new Thread() {
            @Override
            public void run() {
                ApplicationStateHandler.getInstance().persistStatisticsCache(statistics);
            };
        };
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    /**
     * Called to update an artist
     * 
     * @param oldArtist
     * @param newArtist
     */
    public void updateArtist(String oldArtist, String newArtist) {
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

    /**
     * Called to update an album
     * 
     * @param artist
     * @param oldAlbum
     * @param newAlbum
     */
    public void updateAlbum(String artist, String oldAlbum, String newAlbum) {
        RankList<StatisticsAlbum> albumsRanking = statistics.getAlbumsRanking();
        for (StatisticsAlbum album : albumsRanking.getOrder()) {
            if (album.getArtist().equals(artist) && album.getAlbum().equals(oldAlbum)) {
                statistics.getAlbumsRanking().replaceItem(album, new StatisticsAlbum(artist, newAlbum));
            }
        }
        storeStatistics();
    }

    /**
     * Called to update a file name
     * 
     * @param oldFileName
     * @param newFileName
     */
    public void updateFileName(String oldFileName, String newFileName) {
        statistics.getAudioFilesRanking().replaceItem(oldFileName, newFileName);
        statistics.getAudioFilesStats().put(newFileName, statistics.getAudioFilesStats().get(oldFileName));
        statistics.getAudioFilesStats().remove(oldFileName);
        storeStatistics();
    }

}
