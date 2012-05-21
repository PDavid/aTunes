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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

/**
 * The repository handler.
 */
public final class RepositoryHandler extends AbstractHandler implements IRepositoryHandler {

	private RepositoryReader repositoryReader;
	
	private boolean caseSensitiveTrees;
	
	private IStatisticsHandler statisticsHandler;
	
	private INavigationHandler navigationHandler;
	
	private ISearchHandler searchHandler;
	
	private IStateHandler stateHandler;
	
	private IRepository repository;
	
	private VoidRepository voidRepository;
	
    private RepositoryAutoRefresher repositoryRefresher;
    
    /** Listeners notified when an audio file is removed */
    private List<IAudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<IAudioFilesRemovedListener>();

	private IFavoritesHandler favoritesHandler;
	
	private IRepositoryTransaction transaction;
	
	private ShowRepositoryDataHelper showRepositoryDataHelper;
	
	private ISearchableObject repositorySearchableObject;
	
	private PersistRepositoryTask persistRepositoryTask;
	
	private LocalAudioObjectRefresher localAudioObjectRefresher;
	
	private RepositoryRemover repositoryRemover;
	
	private IStateRepository stateRepository;
	
	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}
	
	/**
	 * @param repositoryRemover
	 */
	public void setRepositoryRemover(RepositoryRemover repositoryRemover) {
		this.repositoryRemover = repositoryRemover;
	}
	
	/**
	 * @param localAudioObjectRefresher
	 */
	public void setLocalAudioObjectRefresher(LocalAudioObjectRefresher localAudioObjectRefresher) {
		this.localAudioObjectRefresher = localAudioObjectRefresher;
	}
	
	/**
	 * @param persistRepositoryTask
	 */
	public void setPersistRepositoryTask(PersistRepositoryTask persistRepositoryTask) {
		this.persistRepositoryTask = persistRepositoryTask;
	}
	
	/**
	 * @param repositorySearchableObject
	 */
	public void setRepositorySearchableObject(ISearchableObject repositorySearchableObject) {
		this.repositorySearchableObject = repositorySearchableObject;
	}
	
	/**
	 * @param voidRepository
	 */
	public void setVoidRepository(VoidRepository voidRepository) {
		this.voidRepository = voidRepository;
	}
	
	/**
	 * @param showRepositoryDataHelper
	 */
	public void setShowRepositoryDataHelper(ShowRepositoryDataHelper showRepositoryDataHelper) {
		this.showRepositoryDataHelper = showRepositoryDataHelper;
	}
	
	/**
	 * @param repository
	 */
	void setRepository(IRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * @param repositoryReader
	 */
	public void setRepositoryReader(RepositoryReader repositoryReader) {
		this.repositoryReader = repositoryReader;
	}
    
	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
	
    /**
     * @param stateHandler
     */
    public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
    
    /**
     * @param searchHandler
     */
    public void setSearchHandler(ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}
	
	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
	
	/**
	 * @param repositoryRefresher
	 */
	public void setRepositoryRefresher(RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}
	
    @Override
    public void applicationStateChanged() {
    	if (caseSensitiveTrees != stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		caseSensitiveTrees = stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure();
    		refreshRepository();
    	}
    	// Reschedule repository refresher
    	repositoryRefresher.start();
    }

    @Override
    protected void initHandler() {
    	// Initially use void repository until one is loaded or selected
    	this.repository = this.voidRepository;
    	
        // Add itself as listener
    	caseSensitiveTrees = stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure();
        addAudioFilesRemovedListener(this);
    }

    @Override
    public void allHandlersInitialized() {
    	repositoryReader.applyRepositoryFromCache();
        searchHandler.registerSearchableObject(repositorySearchableObject);
        repositoryRefresher.start();
    }
    
    @Override
    public int requestUserInteraction() {
    	return 2;
    }
    
    @Override
    public void doUserInteraction() {
    	repositoryReader.testRepositoryRetrievedFromCache();
    }

    @Override
	public void addFilesAndRefresh(final List<File> files) {
    	getBean(AddFilesTask.class).execute(repository, files);
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        repositoryRefresher.stop();
        if (!isRepositoryVoid()) {
            // Only store repository if it's dirty
            if (transactionPending()) {
            	stateHandler.persistRepositoryCache(repository);
            } else {
                Logger.info("Repository is clean");
            }

            // Execute command after last access to repository
            new LoadRepositoryCommandExecutor().execute(stateRepository.getCommandAfterAccessRepository());
        }
    }

    @Override
	public List<File> getFolders() {
    	return repository.getRepositoryFolders();
    }

    @Override
	public List<IAlbum> getAlbums() {
        List<IAlbum> result = new ArrayList<IAlbum>();
        Collection<IArtist> artists = repository.getArtists();
        for (IArtist a : artists) {
        	result.addAll(a.getAlbums().values());
        }
        Collections.sort(result);
        return result;
    }

    @Override
	public List<IArtist> getArtists() {
        List<IArtist> result = new ArrayList<IArtist>();
        result.addAll(repository.getArtists());
        Collections.sort(result);
        return result;
    }
    
    @Override
	public IArtist getArtist(String name) {
    	return repository.getArtist(name);
    }
    
    @Override
	public void removeArtist(IArtist artist) {
    	repository.removeArtist(artist);
    }

    @Override
	public IGenre getGenre(String genre) {
    	return repository.getGenre(genre);
    }
    
    @Override
	public void removeGenre(IGenre genre) {
    	repository.removeGenre(genre);
    }
    
    @Override
	public ILocalAudioObject getFileIfLoaded(String fileName) {
        return repository.getFile(fileName);
    }

    @Override
	public int getFoldersCount() {
        return repository.getRepositoryFolders().size();
    }

    @Override
	public String getPathForNewAudioFilesRipped() {
        return StringUtils.getString(getRepositoryPath(), getOsManager().getFileSeparator(), UnknownObjectCheck.getUnknownAlbum(), " - ", DateUtils.toPathString(new DateTime()));
    }

    @Override
	public File getRepositoryFolderContainingFile(ILocalAudioObject file) {
        for (File folder : repository.getRepositoryFolders()) {
            if (file.getUrl().startsWith(folder.getAbsolutePath())) {
                return folder;
            }
        }
        return null;
    }

    @Override
	public String getRepositoryPath() {
        // TODO: Remove this method as now more than one folder can be added to repository
        return repository.getRepositoryFolders().size() > 0 ? repository.getRepositoryFolders().get(0).getAbsolutePath() : "";
    }

    @Override
	public long getRepositoryTotalSize() {
        return repository.getTotalSizeInBytes();
    }

    @Override
	public int getNumberOfFiles() {
        return repository.countFiles();
    }

    @Override
	public Collection<ILocalAudioObject> getAudioFilesList() {
    	return repository.getFiles();
    }

    @Override
	public List<ILocalAudioObject> getAudioFilesForAlbums(Map<String, IAlbum> albums) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, IAlbum> entry : albums.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
    }

    @Override
	public List<ILocalAudioObject> getAudioFilesForArtists(Map<String, IArtist> artists) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, IArtist> entry : artists.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
    }
    
    @Override
	public boolean isRepository(File folder) {
        String path = folder.getAbsolutePath();
        for (File folders : repository.getRepositoryFolders()) {
            if (path.startsWith(folders.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    @Override
	public void notifyCancel() {
    	repositoryReader.notifyCancel();
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return getBean(PreviousInitializationTask.class);
    }
    
    @Override
	public void refreshFile(ILocalAudioObject file) {
    	localAudioObjectRefresher.refreshFile(repository, file);
    }

    @Override
	public void refreshFolders(List<IFolder> folders) {
    	getBean(RefreshFoldersTask.class).execute(repository, folders);
    }

    @Override
	public void refreshRepository() {
        if (!isRepositoryVoid()) {
            repositoryReader.refresh();
        }
    }

    @Override
	public void removeFolders(List<IFolder> foldersToRemove) {
        for (IFolder folder : foldersToRemove) {

            // Remove content
            remove(folder.getAudioObjects());

            // Remove from model
            if (folder.getParentFolder() != null) {
                folder.getParentFolder().removeFolder(folder);
            }

            // Update navigator
            navigationHandler.repositoryReloaded();
        }
    }

    @Override
	public void remove(List<ILocalAudioObject> filesToRemove) {
        if (filesToRemove == null || filesToRemove.isEmpty()) {
            return;
        }

        for (ILocalAudioObject fileToRemove : filesToRemove) {
            repositoryRemover.deleteFile(fileToRemove);
        }

        // Notify listeners
        for (IAudioFilesRemovedListener listener : audioFilesRemovedListeners) {
            listener.audioFilesRemoved(filesToRemove);
        }
    }

    @Override
	public void rename(ILocalAudioObject audioFile, String name) {
        File file = audioFile.getFile();
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        File newFile = new File(StringUtils.getString(file.getParentFile().getAbsolutePath() + "/" + FileNameUtils.getValidFileName(name, getOsManager()) + "." + extension));
        boolean succeeded = file.renameTo(newFile);
        if (succeeded) {
        	renameFile(audioFile, file, newFile);
            navigationHandler.repositoryReloaded();
            statisticsHandler.updateFileName(audioFile, file.getAbsolutePath(), newFile.getAbsolutePath());
        }
    }
    
	/**
	 * Renames a file in repository
	 * 
	 * @param audioFile
	 * @param oldFile
	 * @param newFile
	 */
	private void renameFile(ILocalAudioObject audioFile, File oldFile, File newFile) {
    	startTransaction(); 
		audioFile.setFile(newFile);
		repository.removeFile(oldFile);
		repository.putFile(audioFile);
    	endTransaction();
	}

	
	/**
	 * Returns if Repository is void (not yet loaded or selected)
	 * @return
	 */
	private boolean isRepositoryVoid() {
        return repository instanceof VoidRepository;
    }

    @Override
	public boolean selectRepository() {
        return repositoryReader.selectRepository(false);
    }

    @Override
	public void importFoldersToRepository() {
    }

    @Override
	public void importFolders(final List<File> folders, final String path) {
    	IProgressDialog progressDialog = (IProgressDialog) getBean("progressDialog");
    	progressDialog.setTitle(StringUtils.getString(I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."));
        progressDialog.disableCancelButton();
        progressDialog.showDialog();
        ImportFoldersSwingWorker worker = getBean(ImportFoldersSwingWorker.class);
        worker.setFolders(folders);
        worker.setPath(path);
        worker.execute();
    }

    @Override
	public void addAudioFilesRemovedListener(IAudioFilesRemovedListener listener) {
        audioFilesRemovedListeners.add(listener);
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        // Update status bar
    	showRepositoryDataHelper.showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
    }

    @Override
	public void doInBackground() {
    	repositoryReader.doInBackground();
    }

    /**
     * Returns <code>true</code>if there is a loader reading or refreshing
     * repository
     * 
     * @return
     */
    protected boolean isLoaderWorking() {
    	return repositoryReader.isWorking();
    }

    @Override
	public String getRepositoryConfigurationFolder() {
        String customRepositoryConfigFolder = getOsManager().getCustomRepositoryConfigFolder();
        return customRepositoryConfigFolder != null ? customRepositoryConfigFolder : getOsManager().getUserConfigFolder();
    }
    
	@Override
	public void repositoryChanged(final IRepository repository) {
		persistRepositoryTask.persist(repository);
		favoritesHandler.updateFavorites(repository);
	}

	@Override
	public void startTransaction() {
		startRepositoryTransaction();
	}
	
	@Override
	public void endTransaction() {
		endRepositoryTransaction();
	}
	
	private void startRepositoryTransaction() {
		this.transaction = new RepositoryTransaction(repository, this);
	}
	
	private void endRepositoryTransaction() {
		if (this.transaction != null) {
			this.transaction.finishTransaction();
		}
	}
	
	private boolean transactionPending() {
		return this.transaction != null && this.transaction.isPending();
	}

	@Override
	public Map<String, ?> getDataForView(ViewMode viewMode) {
		return viewMode.getDataForView(repository);
	}

	@Override
	public ILocalAudioObject getFile(String fileName) {
		return repository.getFile(fileName);
	}

	@Override
	public IYear getYear(String year) {
		return repository.getYear(year);
	}

	@Override
	public void removeYear(IYear year) {
		repository.removeYear(year);
	}

	@Override
	public void removeFile(ILocalAudioObject file) {
		repository.removeFile(file);
		repository.removeSizeInBytes(file.getFile().length());
		repository.removeDurationInSeconds(file.getDuration());
	}

	@Override
	public IFolder getFolder(String path) {
		return repository.getFolder(path);
	}

	@Override
	public List<ILocalAudioObject> getAudioObjectsByTitle(String artistName, List<String> titlesList) {
		if (StringUtils.isEmpty(artistName)) {
			throw new IllegalArgumentException("Invalid artist name");
		}
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		IArtist artist = getArtist(artistName);
		if (artist != null) {
			Map<String, ILocalAudioObject> normalizedTitles = getNormalizedAudioObjectsTitles(artist);
			for (String title : titlesList) {
				String normalizedTitle = title.toLowerCase();
				if (normalizedTitles.containsKey(normalizedTitle)) {
					result.add(normalizedTitles.get(normalizedTitle));
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param artist
	 * @return
	 */
	private Map<String, ILocalAudioObject> getNormalizedAudioObjectsTitles(IArtist artist) {
		List<ILocalAudioObject> audioObjects = artist.getAudioObjects();
		Map<String, ILocalAudioObject> titles = new HashMap<String, ILocalAudioObject>();
		for (ILocalAudioObject lao : audioObjects) {
			if (lao.getTitle() != null) {
				titles.put(lao.getTitle().toLowerCase(), lao); // Do lower case for a better match
			}
		}
		return titles;
	}
	
}
