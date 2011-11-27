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

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.RepositorySearchableObject;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

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
	
    private RepositoryAutoRefresher repositoryRefresher;
    
    /** Listeners notified when an audio file is removed */
    private List<IAudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<IAudioFilesRemovedListener>();

	private ITaskService taskService;

	private IFavoritesHandler favoritesHandler;
	
	private RepositoryTransaction transaction;
	
	private RepositoryActionsHelper repositoryActions;
	
	private ShowRepositoryDataHelper showRepositoryDataHelper;
	
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
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
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
	
    @Override
    public void applicationStateChanged(IState newState) {
    	if (caseSensitiveTrees != newState.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
    		refreshRepository();
    	}
    }

    @Override
    protected void initHandler() {
        // Add itself as listener
    	caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
        addAudioFilesRemovedListener(this);
    }

    @Override
    public void allHandlersInitialized() {
    	repositoryReader.applyRepositoryFromCache();
        searchHandler.registerSearchableObject(RepositorySearchableObject.getInstance());
        repositoryRefresher = new RepositoryAutoRefresher(this, getState());
    }
    
    @Override
    public int requestUserInteraction() {
    	return 2;
    }
    
    @Override
    public void doUserInteraction() {
    	repositoryReader.testRepositoryRetrievedFromCache();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#addFilesAndRefresh(java.util.List)
	 */
    @Override
	public void addFilesAndRefresh(final List<File> files) {
    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    			getFrame().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
    		}
    	});
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
            	startTransaction();
                RepositoryLoader.addToRepository(RepositoryHandler.this.getState(), repository, files);
                endTransaction();
                return null;
            }

            @Override
            protected void done() {
            	getFrame().hideProgressBar();
            	showRepositoryDataHelper.showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
                navigationHandler.repositoryReloaded();
                Logger.info("Repository refresh done");
            }
        };
        worker.execute();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#addExternalPictureForAlbum(java.lang.String, java.lang.String, java.io.File)
	 */
    @Override
	public void addExternalPictureForAlbum(String artistName, String albumName, File picture) {
    	startTransaction();
        RepositoryLoader.addExternalPictureForAlbum(repository, artistName, albumName, picture);
        endTransaction();
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        if (repositoryRefresher != null) {
            repositoryRefresher.interrupt();
        }
        if (repository != null) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getFolders()
	 */
    @Override
	public List<File> getFolders() {
        if (repository != null) {
            return repository.getRepositoryFolders();
        }
        return Collections.emptyList();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getAlbums()
	 */
    @Override
	public List<Album> getAlbums() {
        List<Album> result = new ArrayList<Album>();
        if (repository != null) {
            Collection<Artist> artists = repository.getArtists();
            for (Artist a : artists) {
                result.addAll(a.getAlbums().values());
            }
            Collections.sort(result);
        }
        return result;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getArtists()
	 */
    @Override
	public List<Artist> getArtists() {
        List<Artist> result = new ArrayList<Artist>();
        if (repository != null) {
            result.addAll(repository.getArtists());
            Collections.sort(result);
        }
        return result;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getArtist(java.lang.String)
	 */
    @Override
	public Artist getArtist(String name) {
    	if (repository != null) {
    		return repository.getArtist(name);
    	}
    	return null;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#removeArtist(net.sourceforge.atunes.model.Artist)
	 */
    @Override
	public void removeArtist(Artist artist) {
    	if (repository != null) {
    		repository.removeArtist(artist);
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getGenre(java.lang.String)
	 */
    @Override
	public Genre getGenre(String genre) {
    	if (repository != null) {
    		return repository.getGenre(genre);
    	}
    	return null;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#removeGenre(net.sourceforge.atunes.kernel.modules.repository.data.Genre)
	 */
    @Override
	public void removeGenre(Genre genre) {
    	if (repository != null) {
    		repository.removeGenre(genre);
    	}
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getFileIfLoaded(java.lang.String)
	 */
    @Override
	public ILocalAudioObject getFileIfLoaded(String fileName) {
        return repository == null ? null : repository.getFile(fileName);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getFoldersCount()
	 */
    @Override
	public int getFoldersCount() {
        if (repository != null) {
            return repository.getRepositoryFolders().size();
        }
        return 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getPathForNewAudioFilesRipped()
	 */
    @Override
	public String getPathForNewAudioFilesRipped() {
        return StringUtils.getString(getRepositoryPath(), getOsManager().getFileSeparator(), Album.getUnknownAlbum(), " - ", DateUtils.toPathString(new DateTime()));
    }

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    IRepository getRepository() {
        return repository;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getRepositoryFolderContainingFile(net.sourceforge.atunes.model.ILocalAudioObject)
	 */
    @Override
	public File getRepositoryFolderContainingFile(ILocalAudioObject file) {
        if (repository == null) {
            return null;
        }
        for (File folder : repository.getRepositoryFolders()) {
            if (file.getUrl().startsWith(folder.getAbsolutePath())) {
                return folder;
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getRepositoryPath()
	 */
    @Override
	public String getRepositoryPath() {
        // TODO: Remove this method as now more than one folder can be added to repository
        return repository != null ? repository.getRepositoryFolders().get(0).getAbsolutePath() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getRepositoryTotalSize()
	 */
    @Override
	public long getRepositoryTotalSize() {
        return repository != null ? repository.getTotalSizeInBytes() : 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getNumberOfFiles()
	 */
    @Override
	public int getNumberOfFiles() {
        return repository != null ? repository.countFiles() : 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getAudioFilesList()
	 */
    @Override
	public Collection<ILocalAudioObject> getAudioFilesList() {
        if (repository != null) {
            return repository.getFiles();
        }
        return Collections.emptyList();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getAudioFilesForAlbums(java.util.Map)
	 */
    @Override
	public List<ILocalAudioObject> getAudioFilesForAlbums(Map<String, Album> albums) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, Album> entry : albums.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getAudioFilesForArtists(java.util.Map)
	 */
    @Override
	public List<ILocalAudioObject> getAudioFilesForArtists(Map<String, Artist> artists) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#isRepository(java.io.File)
	 */
    @Override
	public boolean isRepository(File folder) {
        if (repository == null) {
            return false;
        }
        String path = folder.getAbsolutePath();
        for (File folders : repository.getRepositoryFolders()) {
            if (path.startsWith(folders.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#notifyCancel()
	 */
    @Override
	public void notifyCancel() {
    	repositoryReader.notifyCancel();
    }


    @Override
    protected Runnable getPreviousInitializationTask() {
        return new PreviousInitializationTask(getState(), repositoryReader, stateHandler);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#refreshFile(net.sourceforge.atunes.model.ILocalAudioObject)
	 */
    @Override
	public void refreshFile(ILocalAudioObject file) {
        RepositoryLoader.refreshFile(getState(),repository, file, statisticsHandler);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#refreshFolders(java.util.List)
	 */
    @Override
	public void refreshFolders(List<Folder> folders) {
    	getFrame().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
    	repositoryActions.enableRepositoryActions(false);
    	new RefreshFoldersSwingWorker(repositoryReader, this, repository, folders, statisticsHandler, getOsManager(), getState()).execute();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#refreshRepository()
	 */
    @Override
	public void refreshRepository() {
        if (!repositoryIsNull()) {
            String text = StringUtils.getString(I18nUtils.getString("REFRESHING"), "...");
            getFrame().showProgressBar(true, text);
            repositoryActions.enableRepositoryActions(false);
            repositoryReader.refresh();
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#removeFolders(java.util.List)
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#remove(java.util.List)
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#rename(net.sourceforge.atunes.model.ILocalAudioObject, java.lang.String)
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#repositoryIsNull()
	 */
    @Override
	public boolean repositoryIsNull() {
        return repository == null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#selectRepository()
	 */
    @Override
	public boolean selectRepository() {
        return repositoryReader.selectRepository(false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#importFoldersToRepository()
	 */
    @Override
	public void importFoldersToRepository() {
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#importFolders(java.util.List, java.lang.String)
	 */
    @Override
	public void importFolders(final List<File> folders, final String path) {
    	IProgressDialog progressDialog = (IProgressDialog) getBean("progressDialog");
    	progressDialog.setTitle(StringUtils.getString(I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."));
        progressDialog.disableCancelButton();
        progressDialog.showDialog();
        SwingWorker<List<ILocalAudioObject>, Void> worker = new ImportFoldersSwingWorker(this, folders, path, progressDialog, getFrame(), getState(), errorDialogFactory, getOsManager());
        worker.execute();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#addAudioFilesRemovedListener(net.sourceforge.atunes.model.IAudioFilesRemovedListener)
	 */
    @Override
	public void addAudioFilesRemovedListener(IAudioFilesRemovedListener listener) {
        audioFilesRemovedListeners.add(listener);
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        // Update status bar
    	showRepositoryDataHelper.showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#doInBackground()
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getRepositoryConfigurationFolder()
	 */
    @Override
	public String getRepositoryConfigurationFolder() {
        String customRepositoryConfigFolder = getOsManager().getCustomRepositoryConfigFolder();
        return customRepositoryConfigFolder != null ? customRepositoryConfigFolder : getOsManager().getUserConfigFolder(getBean(ApplicationArguments.class).isDebug());
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}
	
	@Override
	public void repositoryChanged(final IRepository repository) {
		taskService.submitNow("Persist Repository Cache", new Runnable() {
			@Override
			public void run() {
				stateHandler.persistRepositoryCache(repository, true);
			}
		});
		
		favoritesHandler.updateFavorites(repository);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#startTransaction()
	 */
	@Override
	public void startTransaction() {
		startRepositoryTransaction();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#endTransaction()
	 */
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
	


	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getDataForView(net.sourceforge.atunes.model.ViewMode)
	 */
	@Override
	public Map<String, ?> getDataForView(ViewMode viewMode) {
		return viewMode.getDataForView(repository);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getFile(java.lang.String)
	 */
	@Override
	public ILocalAudioObject getFile(String fileName) {
		if (repository != null) {
			return repository.getFile(fileName);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getYear(java.lang.String)
	 */
	@Override
	public Year getYear(String year) {
		if (repository != null) {
			return repository.getYear(year);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#removeYear(net.sourceforge.atunes.kernel.modules.repository.data.Year)
	 */
	@Override
	public void removeYear(Year year) {
		if (repository != null) {
			repository.removeYear(year);
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#removeFile(net.sourceforge.atunes.model.ILocalAudioObject)
	 */
	@Override
	public void removeFile(ILocalAudioObject file) {
		if (repository != null) {
			repository.removeFile(file);
			repository.removeSizeInBytes(file.getFile().length());
			repository.removeDurationInSeconds(file.getDuration());
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler#getFolder(java.lang.String)
	 */
	@Override
	public Folder getFolder(String path) {
		if (repository != null) {
			return repository.getFolder(path);
		}
		return null;
	}
}
