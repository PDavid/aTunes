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
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.Repository;
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
	
	private IErrorDialogFactory errorDialogFactory;
	
	private IStateHandler stateHandler;
	
	private IDeviceHandler deviceHandler;
	
	private IRepository repository;
	
	private VoidRepository voidRepository;
	
    private RepositoryAutoRefresher repositoryRefresher;
    
    /** Listeners notified when an audio file is removed */
    private List<IAudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<IAudioFilesRemovedListener>();

	private IFavoritesHandler favoritesHandler;
	
	private RepositoryTransaction transaction;
	
	private RepositoryActionsHelper repositoryActions;
	
	private ShowRepositoryDataHelper showRepositoryDataHelper;
	
	private ISearchableObject repositorySearchableObject;
	
	private PersistRepositoryTask persistRepositoryTask;
	
	private ILocalAudioObjectFactory localAudioObjectFactory;
	
	private ILocalAudioObjectValidator localAudioObjectValidator;
	
	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
	
	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
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
	 * @param repositoryActions
	 */
	public void setRepositoryActions(RepositoryActionsHelper repositoryActions) {
		this.repositoryActions = repositoryActions;
	}

	/**
	 * @param repository
	 */
	void setRepository(Repository repository) {
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
     * @param deviceHandler
     */
    public void setDeviceHandler(IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
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
	 * @param errorDialogFactory
	 */
	public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
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
    public void applicationStateChanged(IState newState) {
    	if (caseSensitiveTrees != newState.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
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
    	caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
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
            	stateHandler.persistRepositoryCache(repository, true);
            } else {
                Logger.info("Repository is clean");
            }

            // Execute command after last access to repository
            new LoadRepositoryCommandExecutor().execute(getState().getCommandAfterAccessRepository());
        }
        
        ImageCache.shutdown();
    }

    @Override
	public List<File> getFolders() {
    	return repository.getRepositoryFolders();
    }

    @Override
	public List<Album> getAlbums() {
        List<Album> result = new ArrayList<Album>();
        Collection<Artist> artists = repository.getArtists();
        for (Artist a : artists) {
        	result.addAll(a.getAlbums().values());
        }
        Collections.sort(result);
        return result;
    }

    @Override
	public List<Artist> getArtists() {
        List<Artist> result = new ArrayList<Artist>();
        result.addAll(repository.getArtists());
        Collections.sort(result);
        return result;
    }
    
    @Override
	public Artist getArtist(String name) {
    	return repository.getArtist(name);
    }
    
    @Override
	public void removeArtist(Artist artist) {
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
	public List<ILocalAudioObject> getAudioFilesForAlbums(Map<String, Album> albums) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, Album> entry : albums.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
    }

    @Override
	public List<ILocalAudioObject> getAudioFilesForArtists(Map<String, Artist> artists) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
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
        RepositoryLoader.refreshFile(getState(), repository, file, statisticsHandler, localAudioObjectFactory);
    }

    @Override
	public void refreshFolders(List<Folder> folders) {
    	getFrame().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
    	repositoryActions.enableRepositoryActions(false);
    	new RefreshFoldersSwingWorker(repositoryReader, this, repository, folders, statisticsHandler, getOsManager(), getState(), localAudioObjectFactory, localAudioObjectValidator).execute();
    }

    @Override
	public void refreshRepository() {
        if (!isRepositoryVoid()) {
            String text = StringUtils.getString(I18nUtils.getString("REFRESHING"), "...");
            getFrame().showProgressBar(true, text);
            repositoryActions.enableRepositoryActions(false);
            repositoryReader.refresh();
        }
    }

    @Override
	public void removeFolders(List<Folder> foldersToRemove) {
        for (Folder folder : foldersToRemove) {

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
            RepositoryLoader.deleteFile(fileToRemove, getOsManager(), this, deviceHandler);
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
        SwingWorker<List<ILocalAudioObject>, Void> worker = new ImportFoldersSwingWorker(this, folders, path, progressDialog, getFrame(), getState(), errorDialogFactory, getOsManager(), localAudioObjectFactory, localAudioObjectValidator);
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
        return customRepositoryConfigFolder != null ? customRepositoryConfigFolder : getOsManager().getUserConfigFolder(getBean(ApplicationArguments.class).isDebug());
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
	public Folder getFolder(String path) {
		return repository.getFolder(path);
	}
}
