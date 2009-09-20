/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.repository;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositoryProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.ReviewImportDialog;
import net.sourceforge.atunes.gui.views.dialogs.SelectorDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.ExportAction;
import net.sourceforge.atunes.kernel.actions.ImportToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RefreshRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.actions.SelectRepositoryAction;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.RepositorySearchableObject;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.RankList;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * The repository handler
 */
public final class RepositoryHandler extends Handler implements LoaderListener, AudioFilesRemovedListener {

    public enum SortType {

        /** by track sort order */
        BY_TRACK_NUMBER,

        /** by artist sort order */
        BY_ARTIST_AND_ALBUM,

        /** by title sort order */
        BY_TITLE,

        /** by file name sort order */
        BY_FILE,

        /** by modification date sort order */
        BY_MODIFICATION_TIME
    }

    private static RepositoryHandler instance = new RepositoryHandler();

    Repository repository;
    private int filesLoaded;
    private RepositoryLoader currentLoader;
    private boolean backgroundLoad = false;
    RepositoryAutoRefresher repositoryRefresher;
    Repository repositoryRetrievedFromCache = null;
    /** Listeners notified when an audio file is removed */
    private List<AudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<AudioFilesRemovedListener>();
    
    private RepositoryProgressDialog progressDialog;

    private MouseListener progressBarMouseAdapter = new MouseAdapter() {
    	public void mouseClicked(java.awt.event.MouseEvent e) {
			backgroundLoad = false;
			VisualHandler.getInstance().hideProgressBar();
			if (progressDialog != null) {
				progressDialog.showProgressDialog();
			}
	        VisualHandler.getInstance().getProgressBar().removeMouseListener(progressBarMouseAdapter);
    	};
	};
    
    /**
     * Instantiates a new repository handler.
     */
    private RepositoryHandler() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {

    }

    @Override
    protected void initHandler() {
        // Add itself as listener
        addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted() {
        SearchHandler.getInstance().registerSearchableObject(RepositorySearchableObject.getInstance());
        RepositoryHandler.this.repositoryRefresher = new RepositoryAutoRefresher(RepositoryHandler.this);
    }

    /**
     * Gets the single instance of RepositoryHandler.
     * 
     * @return single instance of RepositoryHandler
     */
    public static RepositoryHandler getInstance() {
        return instance;
    }

    /**
     * Adds the given files to repository and refresh.
     * 
     * @param files
     *            the files
     */
    public void addFilesAndRefresh(final List<File> files) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VisualHandler.getInstance().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
            }
        });
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                RepositoryLoader.addToRepository(repository, files);
                return null;
            }

            @Override
            protected void done() {
                super.done();
                // Persist
                ApplicationStateHandler.getInstance().persistRepositoryCache(repository, false);

                VisualHandler.getInstance().hideProgressBar();
                VisualHandler.getInstance().showRepositoryAudioFileNumber(getAudioFiles().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
                if (ControllerProxy.getInstance().getNavigationController() != null) {
                    ControllerProxy.getInstance().getNavigationController().notifyReload();
                }
                getLogger().info(LogCategories.REPOSITORY, "Repository refresh done");
            }
        };
        worker.execute();
    }

    /**
     * Adds the external picture for album.
     * 
     * @param artistName
     *            the artist name
     * @param albumName
     *            the album name
     * @param picture
     *            the picture
     */
    public void addExternalPictureForAlbum(String artistName, String albumName, File picture) {
        if (repository != null) {
            Artist artist = repository.getStructure().getTreeStructure().get(artistName);
            if (artist == null) {
                return;
            }
            Album album = artist.getAlbum(albumName);
            if (album == null) {
                return;
            }
            List<AudioFile> audioFiles = album.getAudioFiles();
            for (AudioFile af : audioFiles) {
                af.addExternalPicture(picture);
            }
        }
    }

    /**
     * Permanently deletes an audio file from the repository metainformation
     * 
     * @param file
     *            File to be removed permanently
     */
    private void deleteFile(AudioFile file) {
        String albumArtist = file.getAlbumArtist();
        String artist = file.getArtist();
        String album = file.getAlbum();
        String genre = file.getGenre();

        // Only do this if file is in repository
        if (getFolderForFile(file) != null) {
            // Remove from file structure
            Folder f = getFolderForFile(file);
            if (f != null) {
                f.removeFile(file);
                // If folder is empty, remove too
                if (f.isEmpty()) {
                    f.getParentFolder().removeFolder(f);
                }
            }

            // Remove from tree structure
            Artist a = repository.getStructure().getTreeStructure().get(albumArtist);
            if (a == null) {
                a = repository.getStructure().getTreeStructure().get(artist);
            }
            if (a != null) {
                Album alb = a.getAlbum(album);
                if (alb != null) {
                    if (alb.getAudioObjects().size() == 1) {
                        a.removeAlbum(alb);
                    } else {
                        alb.removeAudioFile(file);
                    }

                    if (a.getAudioObjects().size() <= 1) {
                        repository.getStructure().getTreeStructure().remove(a.getName());
                    }
                }
            }

            // Remove from genre structure
            Genre g = repository.getStructure().getGenreStructure().get(genre);
            if (g != null) {
                Artist art = g.getArtist(artist);
                if (art != null) {
                    Album alb = art.getAlbum(album);
                    if (alb != null) {
                        if (alb.getAudioObjects().size() == 1) {
                            art.removeAlbum(alb);
                        } else {
                            alb.removeAudioFile(file);
                        }
                    }

                    if (art.getAudioObjects().size() <= 1) {
                        g.removeArtist(art);
                    }
                }

                if (g.getAudioObjects().size() <= 1) {
                    repository.getStructure().getGenreStructure().remove(genre);
                }
            }

            // Remove from file list
            repository.getAudioFiles().remove(file.getUrl());

            // Update repository size
            repository.setTotalSizeInBytes(repository.getTotalSizeInBytes() - file.getFile().length());

            // Update repository duration
            repository.removeDurationInSeconds(file.getDuration());
        }
        // File is on a device
        else if (DeviceHandler.getInstance().isDevicePath(file.getUrl())) {
            getLogger().info(LogCategories.REPOSITORY, StringUtils.getString("Deleted file ", file, " from device"));
        }
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        if (repositoryRefresher != null) {
            repositoryRefresher.interrupt();
        }
        ApplicationStateHandler.getInstance().persistRepositoryCache(repository, true);

        // Execute command after last access to repository
        String command = ApplicationState.getInstance().getCommandAfterAccessRepository();
        if (command != null && !command.trim().equals("")) {
            try {
                Process p = Runtime.getRuntime().exec(command);
                // Wait process to end
                p.waitFor();
                int rc = p.exitValue();
                getLogger().info(LogCategories.END, StringUtils.getString("Command '", command, "' return code: ", rc));
            } catch (Exception e) {
                getLogger().error(LogCategories.END, e);
            }
        }
    }

    /**
     * Gets the album most played.
     * 
     * @return the album most played
     */

    public Map<String, Integer> getAlbumMostPlayed() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        if (repository != null && repository.getStats().getAlbumsRanking().size() > 0) {
            String firstAlbum = repository.getStats().getAlbumsRanking().getNFirstElements(1).get(0).toString();
            Integer count = repository.getStats().getAlbumsRanking().getNFirstElementCounts(1).get(0);
            result.put(firstAlbum, count);
        } else {
            result.put(null, 0);
        }
        return result;
    }

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
    public List<Album> getAlbums() {
        List<Album> result = new ArrayList<Album>();
        if (repository != null) {
            Collection<Artist> artists = repository.getStructure().getTreeStructure().values();
            for (Artist a : artists) {
                result.addAll(a.getAlbums().values());
            }
            Collections.sort(result);
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
        if (audioFile != null) {
            if (repository != null) {
                Album a = repository.getStructure().getTreeStructure().get(audioFile.getArtist()).getAlbum(audioFile.getAlbum());
                if (repository.getStats().getAlbumsRanking().getCount(a) != null) {
                    return repository.getStats().getAlbumsRanking().getCount(a);
                }
            }
            return 0;
        }
        return null;
    }

    /**
     * Gets the artist and album structure.
     * 
     * @return the artist and album structure
     */
    public Map<String, Artist> getArtistStructure() {
        if (repository != null) {
            return repository.getStructure().getTreeStructure();
        }
        return new HashMap<String, Artist>();
    }

    /**
     * Gets the artist most played.
     * 
     * @return the artist most played
     */

    public Map<String, Integer> getArtistMostPlayed() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        if (repository != null && repository.getStats().getArtistsRanking().size() > 0) {
            String firstArtist = repository.getStats().getArtistsRanking().getNFirstElements(1).get(0).toString();
            Integer count = repository.getStats().getArtistsRanking().getNFirstElementCounts(1).get(0);
            result.put(firstArtist, count);
        } else {
            result.put(null, 0);
        }
        return result;
    }

    /**
     * Gets the artists.
     * 
     * @return the artists
     */
    public List<Artist> getArtists() {
        List<Artist> result = new ArrayList<Artist>();
        if (repository != null) {
            result.addAll(repository.getStructure().getTreeStructure().values());
            Collections.sort(result);
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
        if (repository != null) {
            if (repository.getStats().getArtistsRanking().getCount(artist) != null) {
                return repository.getStats().getArtistsRanking().getCount(artist);
            }
        }
        return 0;
    }

    /**
     * Gets the different audio files played.
     * 
     * @return the different audio files played
     */
    public int getDifferentAudioFilesPlayed() {
        if (repository != null) {
            return repository.getStats().getDifferentAudioFilesPlayed();
        }
        return 0;
    }

    /**
     * Gets the file if loaded.
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file if loaded
     */
    public AudioFile getFileIfLoaded(String fileName) {
        return repository == null ? null : repository.getFile(fileName);
    }

    /**
     * Finds folder that contains file.
     * 
     * @param file
     *            Audio file for which the folder is wanted
     * 
     * @return Either folder or null if file is not in repository
     */
    private Folder getFolderForFile(AudioFile file) {
        // Get repository folder where file is
        File repositoryFolder = getRepositoryFolderContainingFile(file);
        // If the file is not in the repository, return null
        if (repositoryFolder == null) {
            return null;
        }

        // Get root folder
        Folder rootFolder = repository.getStructure().getFolderStructure().get(repositoryFolder.getAbsolutePath());

        // Now navigate through folder until find folder that contains file
        String path = file.getFile().getParentFile().getAbsolutePath();
        path = path.replace(repositoryFolder.getAbsolutePath(), "");

        Folder f = rootFolder;
        StringTokenizer st = new StringTokenizer(path, SystemProperties.FILE_SEPARATOR);
        while (st.hasMoreTokens()) {
            String folderName = st.nextToken();
            f = f.getFolder(folderName);
        }
        return f;
    }

    /**
     * Gets the folder structure.
     * 
     * @return the folder structure
     */
    public Map<String, Folder> getFolderStructure() {
        if (repository != null) {
            return repository.getStructure().getFolderStructure();
        }
        return new HashMap<String, Folder>();
    }

    /**
     * Gets the genre structure.
     * 
     * @return the genre structure
     */
    public Map<String, Genre> getGenreStructure() {
        if (repository != null) {
            return repository.getStructure().getGenreStructure();
        }
        return new HashMap<String, Genre>();
    }

    /**
     * Gets the album structure
     * 
     * @return
     */
    public Map<String, Album> getAlbumStructure() {
        if (repository != null) {
            Map<String, Album> albumsStructure = new HashMap<String, Album>();
            Collection<Artist> artistCollection = repository.getStructure().getTreeStructure().values();
            for (Artist artist : artistCollection) {
                for (Album album : artist.getAlbums().values()) {
                    albumsStructure.put(album.getNameAndArtist(), album);
                }
            }
            return albumsStructure;
        }
        return new HashMap<String, Album>();
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
        if (repository != null) {
            List<Album> albums = repository.getStats().getAlbumsRanking().getNFirstElements(n);
            if (albums != null) {
                return albums;
            }
        }
        return null;
    }

    /**
     * Gets the most played albums in ranking.
     * 
     * @param n
     *            the n
     * 
     * @return the most played albums in ranking
     */
    public List<Object[]> getMostPlayedAlbumsInRanking(int n) {
        if (repository != null) {
            List<Object[]> result = new ArrayList<Object[]>();
            List<Album> albums = repository.getStats().getAlbumsRanking().getNFirstElements(n);
            List<Integer> count = repository.getStats().getAlbumsRanking().getNFirstElementCounts(n);
            if (albums != null) {
                for (int i = 0; i < albums.size(); i++) {
                    Object[] obj = new Object[2];
                    obj[0] = albums.get(i).toString();
                    obj[1] = count.get(i);
                    result.add(obj);
                }
            }
            return result;
        }
        return null;
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
        if (repository != null) {
            List<Artist> artists = repository.getStats().getArtistsRanking().getNFirstElements(n);
            if (artists != null) {
                return artists;
            }
        }
        return null;
    }

    /**
     * Gets the most played artists in ranking.
     * 
     * @param n
     *            the n
     * 
     * @return the most played artists in ranking
     */
    public List<Object[]> getMostPlayedArtistsInRanking(int n) {
        if (repository != null) {
            List<Object[]> result = new ArrayList<Object[]>();
            List<Artist> artists = repository.getStats().getArtistsRanking().getNFirstElements(n);
            List<Integer> count = repository.getStats().getArtistsRanking().getNFirstElementCounts(n);
            if (artists != null) {
                for (int i = 0; i < artists.size(); i++) {
                    Object[] obj = new Object[2];
                    obj[0] = artists.get(i).toString();
                    obj[1] = count.get(i);
                    result.add(obj);
                }
            }
            return result;
        }
        return null;
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
        if (repository != null) {
            List<AudioFile> audioFiles = repository.getStats().getAudioFilesRanking().getNFirstElements(n);
            if (audioFiles != null) {
                return audioFiles;
            }
        }
        return null;
    }

    /**
     * Gets the most played audio files in ranking.
     * 
     * @param n
     *            the n
     * 
     * @return the most played audio files in ranking
     */
    public List<Object[]> getMostPlayedAudioFilesInRanking(int n) {
        if (repository != null) {
            List<Object[]> result = new ArrayList<Object[]>();
            List<AudioFile> audioFiles = repository.getStats().getAudioFilesRanking().getNFirstElements(n);
            List<Integer> count = repository.getStats().getAudioFilesRanking().getNFirstElementCounts(n);
            if (audioFiles != null) {
                for (int i = 0; i < audioFiles.size(); i++) {
                    Object[] obj = new Object[2];
                    AudioFile audioFile = audioFiles.get(i);
                    obj[0] = StringUtils.getString(audioFile.getTitleOrFileName(), " (", audioFile.getArtist(), ")");
                    obj[1] = count.get(i);
                    result.add(obj);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * Gets the path for new audio files ripped.
     * 
     * @return the path for new audio files ripped
     */
    public String getPathForNewAudioFilesRipped() {
        return StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), SystemProperties.FILE_SEPARATOR, Album.getUnknownAlbum(), " - ", DateUtils
                .toPathString(new Date()));
    }

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Returns repository root folder that contains file.
     * 
     * @param file
     *            the file
     * 
     * @return the repository folder containing file
     */
    public File getRepositoryFolderContainingFile(AudioFile file) {
        if (repository == null) {
            return null;
        }
        for (File folder : repository.getFolders()) {
            if (file.getUrl().startsWith(folder.getAbsolutePath())) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Gets the repository path.
     * 
     * @return the repository path
     */
    public String getRepositoryPath() {
        return repository != null ? repository.getFolders().get(0).getAbsolutePath() : "";
    }

    /**
     * Gets the repository total size.
     * 
     * @return the repository total size
     */
    public long getRepositoryTotalSize() {
        return repository != null ? repository.getTotalSizeInBytes() : 0;
    }

    /**
     * Gets the audio file most played.
     * 
     * @return the audio file most played
     */

    public Map<AudioFile, Integer> getAudioFileMostPlayed() {
        Map<AudioFile, Integer> result = new HashMap<AudioFile, Integer>();
        if (repository != null && repository.getStats().getAudioFilesRanking().size() > 0) {
            AudioFile firstAudioFile = repository.getStats().getAudioFilesRanking().getNFirstElements(1).get(0);
            Integer count = repository.getStats().getAudioFilesRanking().getNFirstElementCounts(1).get(0);
            result.put(firstAudioFile, count);
        } else {
            result.put(null, 0);
        }
        return result;
    }

    /**
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public List<AudioFile> getAudioFiles() {
        if (repository != null) {
            return repository.getAudioFilesList();
        }
        return new ArrayList<AudioFile>();
    }

    /**
     * Gets the audio files for albums.
     * 
     * @param albums
     *            the albums
     * 
     * @return the audio files for albums
     */
    public List<AudioFile> getAudioFilesForAlbums(Map<String, Album> albums) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (Map.Entry<String, Album> entry : albums.entrySet()) {
            result.addAll(entry.getValue().getAudioFiles());
        }
        return result;
    }

    /**
     * Gets the auio files for artists.
     * 
     * @param artists
     *            the artists
     * 
     * @return the audio files for artists
     */
    public List<AudioFile> getAudioFilesForArtists(Map<String, Artist> artists) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            result.addAll(entry.getValue().getAudioFiles());
        }
        return result;
    }

    /**
     * Gets the audio files played.
     * 
     * @return the audio files played
     */

    public String getAudioFilesPlayed() {
        if (repository != null) {
            int totalPlays = repository.getStats().getDifferentAudioFilesPlayed();
            int total = repository.countFiles();
            float perCent = (float) totalPlays / (float) total * 100;
            return StringUtils.getString(totalPlays, " / ", total, " (", StringUtils.toString(perCent, 2), "%)");
        }
        return "0 / 0 (0%)";
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
        if (repository != null) {
            return repository.getStats().getStatsForAudioFile(audioFile);
        }
        return null;
    }

    /**
     * Gets the total audio files played.
     * 
     * @return the total audio files played
     */
    public int getTotalAudioFilesPlayed() {
        return repository != null ? repository.getStats().getTotalPlays() : -1;
    }

    /**
     * Gets the unplayed audio files.
     * 
     * @return the unplayed audio files
     */
    public List<AudioFile> getUnplayedAudioFiles() {
        if (repository != null) {
            List<AudioFile> unplayedAudioFiles = repository.getAudioFilesList();
            unplayedAudioFiles.removeAll(repository.getStats().getAudioFilesRanking().getNFirstElements(-1));
            return unplayedAudioFiles;
        }
        return new ArrayList<AudioFile>();
    }

    /**
     * Returns true if folder is in repository.
     * 
     * @param folder
     *            the folder
     * 
     * @return true, if checks if is repository
     */
    public boolean isRepository(File folder) {
        if (repository == null) {
            return false;
        }
        String path = folder.getAbsolutePath();
        for (File folders : repository.getFolders()) {
            if (path.startsWith(folders.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notify cancel.
     */
    public void notifyCancel() {
        currentLoader.interruptLoad();
        repository = currentLoader.getOldRepository();
        notifyFinishRepositoryRead();
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.repository.LoaderListener#
     * notifyCurrentPath(java.lang.String)
     */
    @Override
    public void notifyCurrentPath(final String dir) {
    	if (progressDialog != null) {
    		progressDialog.getFolderLabel().setText(dir);
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.repository.LoaderListener#
     * notifyFileLoaded()
     */
    @Override
    public void notifyFileLoaded() {
        this.filesLoaded++;
        if (progressDialog != null) {
        	progressDialog.getProgressLabel().setText(Integer.toString(filesLoaded));
        	progressDialog.getProgressBar().setValue(filesLoaded);
        }
        VisualHandler.getInstance().getProgressBar().setValue(filesLoaded);
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.repository.LoaderListener#
     * notifyFilesInRepository(int)
     */

    @Override
    public void notifyFilesInRepository(int totalFiles) {
        getLogger().debug(LogCategories.REPOSITORY, new String[] { Integer.toString(totalFiles) });
        // When total files has been calculated change to determinate progress bar
        if (progressDialog != null) {
        	progressDialog.getProgressBar().setIndeterminate(false);
        	progressDialog.getTotalFilesLabel().setText(StringUtils.getString(totalFiles));
        	progressDialog.getProgressBar().setMaximum(totalFiles);
        }
        VisualHandler.getInstance().getProgressBar().setIndeterminate(false);
    	VisualHandler.getInstance().getProgressBar().setMaximum(totalFiles);
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.repository.LoaderListener#
     * notifyFinishRead
     * (net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader)
     */
    @Override
    public void notifyFinishRead(RepositoryLoader loader) {
    	if (progressDialog != null) {
    		progressDialog.setButtonsEnabled(false);
    		progressDialog.getLabel().setText(I18nUtils.getString("STORING_REPOSITORY_INFORMATION"));
    		progressDialog.getProgressLabel().setText("");
    		progressDialog.getTotalFilesLabel().setText("");
    		progressDialog.getFolderLabel().setText(" ");
    	}

        // Save folders: if repository config is lost application can reload data without asking user to select folders again
        List<String> repositoryFolders = new ArrayList<String>();
        for (File folder : repository.getFolders()) {
            repositoryFolders.add(folder.getAbsolutePath());
        }
        ApplicationState.getInstance().setLastRepositoryFolders(repositoryFolders);

        if (backgroundLoad) {
        	VisualHandler.getInstance().hideProgressBar();
        }
        
        notifyFinishRepositoryRead();
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.repository.LoaderListener#
     * notifyFinishRefresh
     * (net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader)
     */
    @Override
    public void notifyFinishRefresh(RepositoryLoader loader) {
        // Persist
        ApplicationStateHandler.getInstance().persistRepositoryCache(repository, false);

    	enableRepositoryActions(true);

        VisualHandler.getInstance().hideProgressBar();
        VisualHandler.getInstance().showRepositoryAudioFileNumber(getAudioFiles().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
        if (ControllerProxy.getInstance().getNavigationController() != null) {
            ControllerProxy.getInstance().getNavigationController().notifyReload();
        }
        getLogger().info(LogCategories.REPOSITORY, "Repository refresh done");
        
        currentLoader = null;
    }

    /**
     * Notify finish repository read.
     */
    private void notifyFinishRepositoryRead() {
    	enableRepositoryActions(true);
    	if (progressDialog != null) {
    		progressDialog.hideProgressDialog();
    		progressDialog.dispose();
    		progressDialog = null;
    	}
        ControllerProxy.getInstance().getNavigationController().notifyReload();
        VisualHandler.getInstance().showRepositoryAudioFileNumber(getAudioFiles().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
        
        currentLoader = null;
    }

    @Override
    public void notifyRemainingTime(final long millis) {
    	if (progressDialog != null) {
    		progressDialog.getRemainingTimeLabel().setText(StringUtils.getString(I18nUtils.getString("REMAINING_TIME"), ":   ", StringUtils.milliseconds2String(millis)));
    	}
    }
    
    @Override
    public void notifyReadProgress() {
        ControllerProxy.getInstance().getNavigationController().notifyReload();
        VisualHandler.getInstance().showRepositoryAudioFileNumber(getAudioFiles().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                // This is the first access to repository, so execute the command defined by user
                String command = ApplicationState.getInstance().getCommandBeforeAccessRepository();
                if (command != null && !command.trim().equals("")) {
                    try {
                        Process p = Runtime.getRuntime().exec(command);
                        // Wait process to end
                        p.waitFor();
                        int rc = p.exitValue();
                        getLogger().info(LogCategories.START, StringUtils.getString("Command '", command, "' return code: ", rc));
                    } catch (Exception e) {
                        getLogger().error(LogCategories.START, e);
                    }
                }
                repositoryRetrievedFromCache = ApplicationStateHandler.getInstance().retrieveRepositoryCache();
            }
        };
    }

    /**
     * Sets the repository.
     */
    public void setRepository() {
        // Try to read repository cache. If fails or not exists, should be selected again
        final Repository rep = repositoryRetrievedFromCache;
        if (rep != null) {
            if (!rep.exists()) {
                Object selection = null;
                // Test if repository exists and show message until repository exists or user doesn't press "RETRY"
                do {
                    selection = VisualHandler.getInstance().showMessage(StringUtils.getString(I18nUtils.getString("REPOSITORY_NOT_FOUND"), ": ", rep.getFolders().get(0)),
                            I18nUtils.getString("REPOSITORY_NOT_FOUND"), JOptionPane.WARNING_MESSAGE,
                            new String[] { I18nUtils.getString("RETRY"), I18nUtils.getString("SELECT_REPOSITORY") });
                } while (I18nUtils.getString("RETRY").equals(selection) && !rep.exists());

                if (!rep.exists() && !selectRepository(true)) {
                    // select "old" repository if repository was not found and no new repository was selected
                    repository = rep;
                } else if (rep.exists()) {
                    // repository exists
                	repository = rep;
                    notifyFinishRead(null);
                }
            } else {
                // repository exists
            	repository = rep;
                notifyFinishRead(null);
            }
        } else {
            // If any repository was loaded previously, try to reload folders
            List<String> lastRepositoryFolders = ApplicationState.getInstance().getLastRepositoryFolders();
            if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
                List<File> foldersToRead = new ArrayList<File>();
                for (String f : lastRepositoryFolders) {
                    foldersToRead.add(new File(f));
                }
                VisualHandler.getInstance().showMessage(I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
                retrieve(foldersToRead);
                return;
            }

            VisualHandler.getInstance().showRepositorySelectionInfoDialog();
            selectRepository();
        }
    }

    /**
     * Read repository.
     * 
     * @param folders
     *            the folders
     */
    private void readRepository(List<File> folders) {
    	backgroundLoad = false;
        Repository oldRepository = repository;
        repository = new Repository(folders);
        // Save stats
        if (oldRepository != null) {
        	transferStatsFrom(oldRepository, repository);
        }
        currentLoader = new RepositoryLoader(folders, oldRepository, repository, false);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Refresh.
     */
    private void refresh() {
        getLogger().info(LogCategories.REPOSITORY, "Refreshing repository");
        filesLoaded = 0;
        Repository oldRepository = repository;
        repository = new Repository(oldRepository.getFolders());
        // Save stats
        if (oldRepository != null) {
        	transferStatsFrom(oldRepository, repository);
        }
        currentLoader = new RepositoryLoader(oldRepository.getFolders(), oldRepository, repository, true);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Refresh file.
     * 
     * @param file
     *            the file
     */
    public void refreshFile(AudioFile file) {
        RepositoryLoader.refreshFile(repository, file);
    }

    /**
     * Refresh repository.
     */
    public void refreshRepository() {
        if (!repositoryIsNull()) {
            String text = StringUtils.getString(I18nUtils.getString("REFRESHING"), "...");
            VisualHandler.getInstance().showProgressBar(true, text);
            enableRepositoryActions(false);
            refresh();
        }
    }

    /**
     * Removes a list of folders from repository.
     * 
     * @param foldersToRemove
     */
    public void removeFolders(List<Folder> foldersToRemove) {
        for (Folder folder : foldersToRemove) {

            // Remove content
            remove(folder.getAudioFiles());

            // Remove from model
            if (folder.getParentFolder() != null) {
                folder.getParentFolder().removeFolder(folder);
            }

            // Update navigator
            ControllerProxy.getInstance().getNavigationController().notifyReload();
        }
    }

    /**
     * Removes a list of files from repository
     * 
     * @param filesToRemove
     *            Files that should be removed
     */
    public void remove(List<AudioFile> filesToRemove) {
        if (filesToRemove == null || filesToRemove.isEmpty()) {
            return;
        }

        for (AudioFile fileToRemove : filesToRemove) {
            deleteFile(fileToRemove);
        }

        // Notify listeners
        for (AudioFilesRemovedListener listener : audioFilesRemovedListeners) {
            listener.audioFilesRemoved(filesToRemove);
        }
    }

    /**
     * Renames an audio file
     * 
     * @param audioFile
     *            the audio file that should be renamed
     * @param name
     *            the new name of the audio file
     */
    public void rename(AudioFile audioFile, String name) {
        File file = audioFile.getFile();
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        File newFile = new File(StringUtils.getString(file.getParentFile().getAbsolutePath() + "/" + FileNameUtils.getValidFileName(name) + "." + extension));
        boolean succeeded = file.renameTo(newFile);
        if (succeeded) {
            repository.getAudioFiles().remove(file.getAbsolutePath());
            audioFile.setFile(newFile);
            repository.getAudioFiles().put(file.getAbsolutePath(), audioFile);
            ControllerProxy.getInstance().getNavigationController().notifyReload();
        }
    }

    /**
     * Repository is null.
     * 
     * @return true, if successful
     */
    public boolean repositoryIsNull() {
        return repository == null;
    }

    /**
     * Retrieve.
     * 
     * @param folders
     *            the folders
     * 
     * @return true, if successful
     */
    public boolean retrieve(List<File> folders) {
    	enableRepositoryActions(false);
    	progressDialog = VisualHandler.getInstance().getProgressDialog();
    	// Start with indeterminate dialog
        progressDialog.showProgressDialog();
        progressDialog.getProgressBar().setIndeterminate(true);
        VisualHandler.getInstance().getProgressBar().setIndeterminate(true);
        filesLoaded = 0;
        try {
            if (folders == null || folders.isEmpty()) {
                repository = null;
                return false;
            }
            readRepository(folders);
            return true;
        } catch (Exception e) {
            repository = null;
            getLogger().error(LogCategories.REPOSITORY, e);
            return false;
        }
    }

    /**
     * Select repository.
     * 
     * @return true, if successful
     */
    public boolean selectRepository() {
        return selectRepository(false);
    }

    /**
     * Select repository.
     * 
     * @param repositoryNotFound
     *            the repository not found
     * 
     * @return true, if successful
     */
    public boolean selectRepository(boolean repositoryNotFound) {
        MultiFolderSelectionDialog dialog = VisualHandler.getInstance().getMultiFolderSelectionDialog();
        dialog.setText(I18nUtils.getString("SELECT_REPOSITORY_FOLDERS"));
        dialog.startDialog((repository != null && !repositoryNotFound) ? repository.getFolders() : null);
        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            if (!folders.isEmpty()) {
                this.retrieve(folders);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the audio file statistics.
     * 
     * @param audioFile
     *            the new audio file statistics
     */
    public void setAudioFileStatistics(AudioFile audioFile) {
        if (repository != null) {
            RepositoryLoader.fillStats(repository, audioFile);
        }
    }

    /**
     * Sort.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the list
     */
    public List<AudioObject> sort(List<? extends AudioObject> audioObjects) {
        return sort(audioObjects, ApplicationState.getInstance().getSortType());
    }

    /**
     * Sort.
     * 
     * @param audioObjects
     *            the audio objects
     * @param type
     *            the type
     * 
     * @return the list
     */
    public List<AudioObject> sort(List<? extends AudioObject> audioObjects, SortType type) {
        AudioObject[] array = audioObjects.toArray(new AudioObject[audioObjects.size()]);

        if (type == SortType.BY_TRACK_NUMBER) {
            Arrays.sort(array, new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject a1, AudioObject a2) {
                    return Integer.valueOf(a1.getTrackNumber()).compareTo(a2.getTrackNumber());
                }
            });
        } else if (type == SortType.BY_ARTIST_AND_ALBUM) {
            Arrays.sort(array, new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject a1, AudioObject a2) {

                    // Sort by album artist
                    int c1 = a1.getAlbumArtist().compareTo(a2.getAlbumArtist());
                    if (c1 != 0) {
                        return c1;
                    }

                    /*
                     * If album artist is "" in both audio objects (we just need
                     * to check only one audio object since if execution reaches
                     * this code both album artist fields are equal) then sort
                     * by artist, album and track If album artist is not "",
                     * then only sort by album and track
                     */
                    if (a1.getAlbumArtist().isEmpty()) {
                        int c2 = a1.getArtist().compareTo(a2.getArtist());
                        if (c2 != 0) {
                            return c2;
                        }
                    }

                    // Sort by album
                    int c3 = a1.getAlbum().compareTo(a2.getAlbum());
                    if (c3 != 0) {
                        return c3;
                    }

                    // Sort by disc number
                    int c4 = Integer.valueOf(a1.getDiscNumber()).compareTo(a2.getDiscNumber());
                    if (c4 != 0) {
                        return c4;
                    }

                    return Integer.valueOf(a1.getTrackNumber()).compareTo(a2.getTrackNumber());
                }
            });
        } else if (type == SortType.BY_TITLE) {
            Arrays.sort(array, new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject a0, AudioObject a1) {
                    return a0.getTitleOrFileName().compareTo(a1.getTitleOrFileName());
                }
            });
        } else if (type == SortType.BY_MODIFICATION_TIME) {
            Arrays.sort(array, new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject o1, AudioObject o2) {
                    if (o1 instanceof AudioFile && o2 instanceof AudioFile) {
                        return Long.valueOf(((AudioFile) o1).getFile().lastModified()).compareTo(Long.valueOf(((AudioFile) o2).getFile().lastModified()));
                    }
                    return 0;
                }
            });
        } else {
            // Sort audio objects by file name
            // This call uses compareTo methods of every class 
            Arrays.sort(array);
        }

        return Arrays.asList(array);
    }

    /**
     * Gets stats from a repository.
     * 
     * @param oldRepository
     *            the old repository
     * @param newRepository
     *            the new repository
     */
    private void transferStatsFrom(Repository newRepository, Repository oldRepository) {
        // Total plays
        newRepository.getStats().setTotalPlays(oldRepository.getStats().getTotalPlays());

        // Different audio files played
        newRepository.getStats().setDifferentAudioFilesPlayed(oldRepository.getStats().getDifferentAudioFilesPlayed());

        // Audio file stats
        newRepository.getStats().setAudioFilesStats(oldRepository.getStats().getAudioFilesStats());

        // Audio files ranking
        newRepository.getStats().setAudioFilesRanking(oldRepository.getStats().getAudioFilesRanking());

        // Album ranking
        RankList<Album> albumsRanking = oldRepository.getStats().getAlbumsRanking();
        List<Album> albums = albumsRanking.getOrder();
        for (int i = 0; i < albums.size(); i++) {
            Album oldAlbum = albums.get(i);
            Artist artist = newRepository.getStructure().getTreeStructure().get(oldAlbum.getArtist());
            if (artist != null) {
                Album newAlbum = artist.getAlbum(oldAlbum.getName());
                if (newAlbum != null) {
                    albumsRanking.replaceItem(oldAlbum, newAlbum);
                }
            }
        }
        newRepository.getStats().setAlbumsRanking(albumsRanking);

        // Artist Ranking
        RankList<Artist> artistsRanking = oldRepository.getStats().getArtistsRanking();
        List<Artist> artists = artistsRanking.getOrder();
        for (int i = 0; i < artists.size(); i++) {
            Artist oldArtist = artists.get(i);
            Artist newArtist = newRepository.getStructure().getTreeStructure().get(oldArtist.getName());
            if (newArtist != null) {
                artistsRanking.replaceItem(oldArtist, newArtist);
            }
        }
        newRepository.getStats().setArtistsRanking(artistsRanking);
    }

    /**
     * Imports folders to repository
     */
    public void importFoldersToRepository() {
        // First check if repository is selected. If not, display a message
        if (getRepository() == null) {
            VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("SELECT_REPOSITORY_BEFORE_IMPORT"));
            return;
        }

        // Now show dialog to select folders
        MultiFolderSelectionDialog dialog = VisualHandler.getInstance().getMultiFolderSelectionDialog();
        dialog.setTitle(I18nUtils.getString("IMPORT"));
        dialog.setText(I18nUtils.getString("SELECT_FOLDERS_TO_IMPORT"));
        dialog.startDialog(null);
        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            // If user selected folders...
            if (!folders.isEmpty()) {
                String path;
                // If repository folders are more than one then user must select where to import songs
                if (getRepository().getFolders().size() > 1) {
                    String[] folderNames = new String[getRepository().getFolders().size()];
                    for (int i = 0; i < getRepository().getFolders().size(); i++) {
                        folderNames[i] = getRepository().getFolders().get(i).getAbsolutePath();
                    }
                    SelectorDialog selector = new SelectorDialog(VisualHandler.getInstance().getFrame().getFrame(), I18nUtils.getString("SELECT_REPOSITORY_FOLDER_TO_IMPORT"),
                            folderNames, null);
                    selector.setVisible(true);
                    path = selector.getSelection();
                    // If user closed dialog then select first entry
                    if (path == null) {
                        path = getRepositoryPath();
                    }
                } else {
                    path = getRepositoryPath();
                }
                this.importFolders(folders, path);
            }
        }
    }

    /**
     * Imports folders passed as argument to repository
     * 
     * @param folders
     * @param path
     */
    private void importFolders(final List<File> folders, final String path) {
        SwingWorker<List<AudioFile>, Void> worker = new SwingWorker<List<AudioFile>, Void>() {
            @Override
            protected List<AudioFile> doInBackground() throws Exception {
                VisualHandler.getInstance().showIndeterminateProgressDialog(StringUtils.getString(I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."));
                return RepositoryLoader.getSongsForFolders(folders);
            }

            @Override
            protected void done() {
                super.done();
                VisualHandler.getInstance().hideIndeterminateProgressDialog();

                try {
                    final List<AudioFile> filesToLoad = get();

                    TagAttributesReviewed tagAttributesReviewed = null;
                    // Review tags if selected in settings
                    if (ApplicationState.getInstance().isReviewTagsBeforeImport()) {
                        ReviewImportDialog reviewImportDialog = VisualHandler.getInstance().getReviewImportDialog();
                        reviewImportDialog.show(folders, filesToLoad);
                        if (reviewImportDialog.isDialogCancelled()) {
                            return;
                        }
                        tagAttributesReviewed = reviewImportDialog.getResult();
                    }

                    final ImportFilesProcess process = new ImportFilesProcess(filesToLoad, folders, path, tagAttributesReviewed);
                    process.addProcessListener(new ProcessListener() {
                        @Override
                        public void processCanceled() {
                            // Nothing to do, files copied will be removed before calling this method 
                        }

                        @Override
                        public void processFinished(final boolean ok) {
                            if (!ok) {
                                try {
                                    SwingUtilities.invokeAndWait(new Runnable() {
                                        @Override
                                        public void run() {
                                            VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_IMPORT_PROCESS"));
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    // Do nothing
                                } catch (InvocationTargetException e) {
                                    // Do nothing
                                }
                            } else {
                                // If import is ok then add files to repository
                                addFilesAndRefresh(process.getFilesTransferred());
                            }
                        }
                    });
                    process.execute();

                } catch (InterruptedException e) {
                    getLogger().error(LogCategories.REPOSITORY, e);
                } catch (ExecutionException e) {
                    getLogger().error(LogCategories.REPOSITORY, e);
                }
            }
        };
        worker.execute();
    }

    /**
     * Adds a listener to be notified when an audio file is removed
     * 
     * @param listener
     */
    public void addAudioFilesRemovedListener(AudioFilesRemovedListener listener) {
        audioFilesRemovedListeners.add(listener);
    }

    @Override
    public void audioFilesRemoved(List<AudioFile> audioFiles) {
        // Update status bar
        VisualHandler.getInstance().showRepositoryAudioFileNumber(getAudioFiles().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
    }

	/**
	 * @param repository the repository to set
	 */
	protected void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void doInBackground() {
		if (currentLoader != null) {
			backgroundLoad = true;
			currentLoader.setPriority(Thread.MIN_PRIORITY);
			if (progressDialog != null) {
				progressDialog.hideProgressDialog();
			}
            VisualHandler.getInstance().showProgressBar(false, StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
            VisualHandler.getInstance().getProgressBar().addMouseListener(progressBarMouseAdapter);
		}
        
	}

	/**
	 * Enables or disables actions that can't be performed while loading repository
	 * @param enable
	 */
	private void enableRepositoryActions(boolean enable) {
    	Actions.getAction(SelectRepositoryAction.class).setEnabled(enable);
    	Actions.getAction(RefreshRepositoryAction.class).setEnabled(enable);
    	Actions.getAction(ImportToRepositoryAction.class).setEnabled(enable);
    	Actions.getAction(ExportAction.class).setEnabled(enable);
    	Actions.getAction(ConnectDeviceAction.class).setEnabled(enable);
    	Actions.getAction(RipCDAction.class).setEnabled(enable);
	}
	
	/**
	 * Returns <code>true</code>if there is a loader reading or refreshing repository
	 * @return
	 */
	protected boolean isLoaderWorking() {
		return currentLoader != null;
	}
}
