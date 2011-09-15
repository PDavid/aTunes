/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.RepositoryProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.gui.views.dialogs.ReviewImportDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.ExportAction;
import net.sourceforge.atunes.kernel.actions.ImportToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RefreshFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.RefreshRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.actions.SelectRepositoryAction;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.repository.processes.ImportFilesProcess;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.RepositorySearchableObject;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.tags.TagAttributesReviewed;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IMultiFolderSelectionDialog;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.model.RepositoryListener;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * The repository handler.
 */
public final class RepositoryHandler extends AbstractHandler implements LoaderListener, AudioFilesRemovedListener, RepositoryListener {

	// Used to retrieve covers and show in progress dialog
	private String lastArtistRead;
	
	private String lastAlbumRead;
	
	private SwingWorker<Image, Void> coverWorker;
	
	private ExecutorService repositoryChangesService = Executors.newSingleThreadExecutor();
	
	private boolean caseSensitiveTrees;
	
	private IStatisticsHandler statisticsHandler;
	
	private final class ImportFilesProcessListener implements ProcessListener {
		private final ImportFilesProcess process;

		private ImportFilesProcessListener(ImportFilesProcess process) {
			this.process = process;
		}

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
							GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_IMPORT_PROCESS"));
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
	}



    private final class ImportFoldersSwingWorker extends
			SwingWorker<List<ILocalAudioObject>, Void> {
    	
		private final class ImportFoldersLoaderListener implements
				LoaderListener {
			private int filesLoaded = 0;
			private int totalFiles;

			@Override
			public void notifyRemainingTime(long time) {
			}

			@Override
			public void notifyReadProgress() {
			}

			@Override
			public void notifyFinishRefresh(RepositoryLoader loader) {
			}
			
			@Override
			public void notifyCurrentAlbum(String artist, String album) {
			}

			@Override
			public void notifyFinishRead(RepositoryLoader loader) {
			    progressDialog.hideDialog();
			}

			@Override
			public void notifyFilesInRepository(final int files) {
			    this.totalFiles = files;
			    SwingUtilities.invokeLater(new Runnable() {
			        @Override
			        public void run() {
			            progressDialog.setTotalProgress(files);
			        }
			    });
			}

			@Override
			public void notifyFileLoaded() {
			    this.filesLoaded++;
			    SwingUtilities.invokeLater(new Runnable() {
			        @Override
			        public void run() {
			            progressDialog.setCurrentProgress(filesLoaded);
			            progressDialog.setProgressBarValue((int) (filesLoaded * 100.0 / totalFiles));
			        }
			    });
			}

			@Override
			public void notifyCurrentPath(String path) {
			}
		}

		private final List<File> folders;
		private final String path;
		private final IProgressDialog progressDialog;

		private ImportFoldersSwingWorker(List<File> folders, String path,
				IProgressDialog progressDialog) {
			this.folders = folders;
			this.path = path;
			this.progressDialog = progressDialog;
		}

		@Override
		protected List<ILocalAudioObject> doInBackground() throws Exception {
		    return RepositoryLoader.getSongsForFolders(folders, new ImportFoldersLoaderListener());
		}

		@Override
		protected void done() {
		    super.done();
		    GuiHandler.getInstance().hideIndeterminateProgressDialog();

		    try {
		        final List<ILocalAudioObject> filesToLoad = get();

		        TagAttributesReviewed tagAttributesReviewed = null;
		        // Review tags if selected in settings
		        if (RepositoryHandler.this.getState().isReviewTagsBeforeImport()) {
		            ReviewImportDialog reviewImportDialog = GuiHandler.getInstance().getReviewImportDialog();
		            reviewImportDialog.show(folders, filesToLoad);
		            if (reviewImportDialog.isDialogCancelled()) {
		                return;
		            }
		            tagAttributesReviewed = reviewImportDialog.getResult();
		        }

		        final ImportFilesProcess process = new ImportFilesProcess(filesToLoad, folders, path, tagAttributesReviewed, RepositoryHandler.this.getState(), RepositoryHandler.this.getFrame(), getOsManager());
		        process.addProcessListener(new ImportFilesProcessListener(process));
		        process.execute();

		    } catch (InterruptedException e) {
		        Logger.error(e);
		    } catch (ExecutionException e) {
		        Logger.error(e);
		    }
		}
	}
    
    private static final class RefreshFoldersSwingWorker extends SwingWorker<Void, Void> {
    	
    	private Repository repository;
    	
    	private List<Folder> folders;
    	
    	private IStatisticsHandler statisticsHandler;
    	
    	private IOSManager osManager;
    	
    	public RefreshFoldersSwingWorker(Repository repository, List<Folder> folders, IStatisticsHandler statisticsHandler, IOSManager osManager) {
    		this.repository = repository;
    		this.folders = folders;
    		this.statisticsHandler = statisticsHandler;
    		this.osManager = osManager;
		}
    	
    	@Override
    	protected Void doInBackground() throws Exception {
    		getInstance().startTransaction();
            RepositoryLoader.refreshFolders(repository, folders, statisticsHandler, osManager);
            getInstance().endTransaction();
    		return null;
    	}
    	
    	@Override
    	protected void done() {
    		super.done();
    		getInstance().notifyFinishRefresh(null);
    	}
    }

	private static class ShowProgressBarRunnable implements Runnable {
        @Override
        public void run() {
            GuiHandler.getInstance().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
        }
    }

    private static class ExitRunnable implements Runnable {
        @Override
        public void run() {
            Actions.getAction(ExitAction.class).actionPerformed(null);
        }
    }

    private static RepositoryHandler instance = new RepositoryHandler();

    private Repository repository;
    private int filesLoaded;
    private RepositoryLoader currentLoader;
    private boolean backgroundLoad = false;
    private RepositoryAutoRefresher repositoryRefresher;
    private Repository repositoryRetrievedFromCache = null;
    /** Listeners notified when an audio file is removed */
    private List<AudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<AudioFilesRemovedListener>();

    private RepositoryProgressDialog progressDialog;

    private MouseListener progressBarMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            backgroundLoad = false;
            currentLoader.setPriority(Thread.MAX_PRIORITY);
            GuiHandler.getInstance().hideProgressBar();
            if (progressDialog != null) {
                progressDialog.showProgressDialog();
            }
            getFrame().getProgressBar().removeMouseListener(progressBarMouseAdapter);
        };
    };

    /**
     * Instantiates a new repository handler.
     */
    private RepositoryHandler() {
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
        statisticsHandler = Context.getBean(IStatisticsHandler.class);
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
        applyRepositoryFromCache();
    }
    
    @Override
    public void allHandlersInitialized() {
    	if (repository == null) {
    		applyRepository();
    	}
        SearchHandler.getInstance().registerSearchableObject(RepositorySearchableObject.getInstance());
        repositoryRefresher = new RepositoryAutoRefresher(RepositoryHandler.this, getState());
    }
    
    @Override
    public int requestUserInteraction() {
    	return 1;
    }
    
    @Override
    public void doUserInteraction() {
    	Repository rep = repositoryRetrievedFromCache;
    	if (rep == null) {
    		reloadExistingRepository();
    	}
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
        SwingUtilities.invokeLater(new ShowProgressBarRunnable());
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
            	startTransaction();
                RepositoryLoader.addToRepository(repository, files);
                endTransaction();
                return null;
            }

            @Override
            protected void done() {
                GuiHandler.getInstance().hideProgressBar();
                GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
                NavigationHandler.getInstance().notifyReload();
                Logger.info("Repository refresh done");
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
            if (repository.transactionPending()) {
                ApplicationStateHandler.getInstance().persistRepositoryCache(repository, true);
            } else {
                Logger.info("Repository is clean");
            }

            // Execute command after last access to repository
            String command = getState().getCommandAfterAccessRepository();
            if (command != null && !command.trim().isEmpty()) {
                try {
                    Process p = Runtime.getRuntime().exec(command);
                    // Wait process to end
                    p.waitFor();
                    int rc = p.exitValue();
                    Logger.info(StringUtils.getString("Command '", command, "' return code: ", rc));
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
        }
        
        repositoryChangesService.shutdown();
        AudioFile.getImageCache().shutdown();
    }

    public List<File> getFolders() {
        if (repository != null) {
            return repository.getRepositoryFolders();
        }
        return Collections.emptyList();
    }

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
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

    /**
     * Gets the artists.
     * 
     * @return the artists
     */
    public List<Artist> getArtists() {
        List<Artist> result = new ArrayList<Artist>();
        if (repository != null) {
            result.addAll(repository.getArtists());
            Collections.sort(result);
        }
        return result;
    }
    
    /**
     * Returns artist with given name
     * @param name
     * @return
     */
    public Artist getArtist(String name) {
    	if (repository != null) {
    		return repository.getArtist(name);
    	}
    	return null;
    }
    
    /**
     * Removes artist
     * @param artist
     */
    public void removeArtist(Artist artist) {
    	if (repository != null) {
    		repository.removeArtist(artist);
    	}
    }

    /**
     * Returns genre with given name
     * @param genre
     * @return
     */
    public Genre getGenre(String genre) {
    	if (repository != null) {
    		return repository.getGenre(genre);
    	}
    	return null;
    }
    
    /**
     * Removes genre
     * @param genre
     */
    public void removeGenre(Genre genre) {
    	if (repository != null) {
    		repository.removeGenre(genre);
    	}
    }
    
    /**
     * Gets the file if loaded.
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file if loaded
     */
    public ILocalAudioObject getFileIfLoaded(String fileName) {
        return repository == null ? null : repository.getFile(fileName);
    }

    /**
     * Returns number of root folders of repository
     * 
     * @return
     */
    public int getFoldersCount() {
        if (repository != null) {
            return repository.getRepositoryFolders().size();
        }
        return 0;
    }

    /**
     * Gets the path for new audio files ripped.
     * 
     * @return the path for new audio files ripped
     */
    public String getPathForNewAudioFilesRipped() {
        return StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), getOsManager().getFileSeparator(), Album.getUnknownAlbum(), " - ", DateUtils
                .toPathString(new Date()));
    }

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    Repository getRepository() {
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

    /**
     * Gets the repository path.
     * 
     * @return the repository path
     */
    public String getRepositoryPath() {
        // TODO: Remove this method as now more than one folder can be added to repository
        return repository != null ? repository.getRepositoryFolders().get(0).getAbsolutePath() : "";
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
     * Gets the number of files of repository
     * 
     * @return
     */
    public int getNumberOfFiles() {
        return repository != null ? repository.countFiles() : 0;
    }

    /**
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public Collection<ILocalAudioObject> getAudioFilesList() {
        if (repository != null) {
            return repository.getFiles();
        }
        return Collections.emptyList();
    }

    /**
     * Gets the audio files for albums.
     * 
     * @param albums
     *            the albums
     * 
     * @return the audio files for albums
     */
    public List<ILocalAudioObject> getAudioFilesForAlbums(Map<String, Album> albums) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, Album> entry : albums.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
    }

    /**
     * Gets the audio files for artists.
     * 
     * @param artists
     *            the artists
     * 
     * @return the audio files for artists
     */
    public List<ILocalAudioObject> getAudioFilesForArtists(Map<String, Artist> artists) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            result.addAll(entry.getValue().getAudioObjects());
        }
        return result;
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
        for (File folders : repository.getRepositoryFolders()) {
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

    @Override
    public void notifyCurrentPath(final String dir) {
        if (progressDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressDialog.getFolderLabel().setText(dir);
                }
            });
        }
    }

    @Override
    public void notifyFileLoaded() {
        this.filesLoaded++;
        // Update GUI every 50 files
        if (this.filesLoaded % 50 == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.getProgressLabel().setText(Integer.toString(filesLoaded));
                        progressDialog.getSeparatorLabel().setVisible(true);
                        progressDialog.getProgressBar().setValue(filesLoaded);
                    }
                    getFrame().getProgressBar().setValue(filesLoaded);
                }
            });
        }
    }

    @Override
    public void notifyFilesInRepository(int totalFiles) {
        // When total files has been calculated change to determinate progress bar
        if (progressDialog != null) {
            progressDialog.getProgressBar().setIndeterminate(false);
            progressDialog.getTotalFilesLabel().setText(StringUtils.getString(totalFiles));
            progressDialog.getProgressBar().setMaximum(totalFiles);
        }
        getFrame().getProgressBar().setIndeterminate(false);
        getFrame().getProgressBar().setMaximum(totalFiles);
    }

    @Override
    public void notifyFinishRead(RepositoryLoader loader) {
        if (progressDialog != null) {
            progressDialog.setButtonsEnabled(false);
            progressDialog.getLabel().setText(I18nUtils.getString("STORING_REPOSITORY_INFORMATION"));
            progressDialog.getProgressLabel().setText("");
            progressDialog.getSeparatorLabel().setVisible(false);
            progressDialog.getTotalFilesLabel().setText("");
            progressDialog.getFolderLabel().setText(" ");
        }

        // Save folders: if repository config is lost application can reload data without asking user to select folders again
        List<String> repositoryFolders = new ArrayList<String>();
        for (File folder : repository.getRepositoryFolders()) {
            repositoryFolders.add(folder.getAbsolutePath());
        }
        getState().setLastRepositoryFolders(repositoryFolders);

        if (backgroundLoad) {
            GuiHandler.getInstance().hideProgressBar();
        }

        notifyFinishRepositoryRead();
    }

    @Override
    public void notifyFinishRefresh(RepositoryLoader loader) {
        enableRepositoryActions(true);

        GuiHandler.getInstance().hideProgressBar();
        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
        NavigationHandler.getInstance().notifyReload();
        Logger.info("Repository refresh done");

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
        NavigationHandler.getInstance().notifyReload();
        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository != null ? repository.getTotalDurationInSeconds() : 0);

        currentLoader = null;
    }

    @Override
    public void notifyRemainingTime(final long millis) {
        if (progressDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressDialog.getRemainingTimeLabel().setText(StringUtils.getString(I18nUtils.getString("REMAINING_TIME"), ":   ", StringUtils.milliseconds2String(millis)));
                }
            });
        }
    }

    @Override
    public void notifyReadProgress() {
        try {
        	// Use invoke and wait in this case to avoid concurrent problems while reading repository and showing nodes in navigator tree (ConcurrentModificationException)
			SwingUtilities.invokeAndWait(new Runnable() {
			    @Override
			    public void run() {
			    	NavigationHandler.getInstance().notifyReload();
			        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
			    }
			});
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		}
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                // This is the first access to repository, so execute the command defined by user
                String command = getState().getCommandBeforeAccessRepository();
                if (command != null && !command.trim().equals("")) {
                    try {
                        Process p = Runtime.getRuntime().exec(command);
                        // Wait process to end
                        p.waitFor();
                        int rc = p.exitValue();
                        Logger.info(StringUtils.getString("Command '", command, "' return code: ", rc));
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                }
                repositoryRetrievedFromCache = ApplicationStateHandler.getInstance().retrieveRepositoryCache();
            }
        };
    }

    /**
     * Sets the repository from the one read from cache
     * @param askUser
     */
    private void applyRepositoryFromCache() {
        Repository rep = repositoryRetrievedFromCache;
        if (rep != null && rep.exists()) {
        	applyExistingRepository(rep);
        }
    }

    /**
     * Sets the repository.
     * @param askUser
     */
    private void applyRepository() {
        Repository rep = repositoryRetrievedFromCache;
        // Try to read repository cache. If fails or not exists, should be selected again
        if (rep != null) {
        	if (!rep.exists()) {
        		askUserForRepository(rep);
        		if (!rep.exists() && !selectRepository(true)) {
        			// select "old" repository if repository was not found and no new repository was selected
        			repository = rep;
        		} else if (rep.exists()) {
        			// repository exists
        			applyExistingRepository(rep);
        		}
        	} else {
        		// repository exists
        		applyExistingRepository(rep);
        	}
        }
    }

    /**
     * Test if repository exists and show message until repository exists or
     * user doesn't press "RETRY"
     * 
     * @param rep
     */
    private void askUserForRepository(final Repository rep) {
        Object selection;
        do {
            String exitString = Actions.getAction(ExitAction.class).getValue(Action.NAME).toString();
            selection = GuiHandler.getInstance().showMessage(StringUtils.getString(I18nUtils.getString("REPOSITORY_NOT_FOUND"), ": ", rep.getRepositoryFolders().get(0)),
                    I18nUtils.getString("REPOSITORY_NOT_FOUND"), JOptionPane.WARNING_MESSAGE,
                    new String[] { I18nUtils.getString("RETRY"), I18nUtils.getString("SELECT_REPOSITORY"), exitString });

            if (selection.equals(exitString)) {
                SwingUtilities.invokeLater(new ExitRunnable());
            }
        } while (I18nUtils.getString("RETRY").equals(selection) && !rep.exists());
    }

    private void applyExistingRepository(Repository rep) {
        repository = rep;
        repository.setListener(this);
        notifyFinishRepositoryRead();
    }

    /**
     * If any repository was loaded previously, try to reload folders
     */
    private void reloadExistingRepository() {
        List<String> lastRepositoryFolders = getState().getLastRepositoryFolders();
        if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
            List<File> foldersToRead = new ArrayList<File>();
            for (String f : lastRepositoryFolders) {
                foldersToRead.add(new File(f));
            }
            GuiHandler.getInstance().showMessage(I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
            retrieve(foldersToRead);
        } else {
        	RepositorySelectionInfoDialog dialog = new RepositorySelectionInfoDialog(getFrame().getFrame());
        	dialog.setVisible(true);
        	if (dialog.userAccepted()) {
        		selectRepository();
        	}
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
        repository = new Repository(folders, this, getState());
        currentLoader = new RepositoryLoader(folders, oldRepository, repository, false);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Refresh.
     */
    private void refresh() {
        Logger.info("Refreshing repository");
        filesLoaded = 0;
        Repository oldRepository = repository;
        repository = new Repository(oldRepository.getRepositoryFolders(), this, getState());
        currentLoader = new RepositoryLoader(oldRepository.getRepositoryFolders(), oldRepository, repository, true);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Refreshes a file after being modified
     * 
     * @param file
     *            the file
     */
    public void refreshFile(ILocalAudioObject file) {
        RepositoryLoader.refreshFile(repository, file, statisticsHandler);
    }

    /**
     * Refreshes a folder
     * 
     * @param file
     *            the file
     */
    public void refreshFolders(List<Folder> folders) {
        GuiHandler.getInstance().showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
        enableRepositoryActions(false);
    	new RefreshFoldersSwingWorker(repository, folders, statisticsHandler, getOsManager()).execute();
    }

    /**
     * Refresh repository.
     */
    public void refreshRepository() {
        if (!repositoryIsNull()) {
            String text = StringUtils.getString(I18nUtils.getString("REFRESHING"), "...");
            GuiHandler.getInstance().showProgressBar(true, text);
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
            remove(folder.getAudioObjects());

            // Remove from model
            if (folder.getParentFolder() != null) {
                folder.getParentFolder().removeFolder(folder);
            }

            // Update navigator
            NavigationHandler.getInstance().notifyReload();
        }
    }

    /**
     * Removes a list of files from repository
     * 
     * @param filesToRemove
     *            Files that should be removed
     */
    public void remove(List<ILocalAudioObject> filesToRemove) {
        if (filesToRemove == null || filesToRemove.isEmpty()) {
            return;
        }

        for (ILocalAudioObject fileToRemove : filesToRemove) {
            RepositoryLoader.deleteFile(fileToRemove, getOsManager());
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
    public void rename(ILocalAudioObject audioFile, String name) {
        File file = audioFile.getFile();
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        File newFile = new File(StringUtils.getString(file.getParentFile().getAbsolutePath() + "/" + FileNameUtils.getValidFileName(name, getOsManager()) + "." + extension));
        boolean succeeded = file.renameTo(newFile);
        if (succeeded) {
        	renameFile(audioFile, file, newFile);
            NavigationHandler.getInstance().notifyReload();
            Context.getBean(IStatisticsHandler.class).updateFileName(audioFile, file.getAbsolutePath(), newFile.getAbsolutePath());
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
        progressDialog = GuiHandler.getInstance().getProgressDialog();
        // Start with indeterminate dialog
        progressDialog.showProgressDialog();
        progressDialog.getProgressBar().setIndeterminate(true);
        getFrame().getProgressBar().setIndeterminate(true);
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
            Logger.error(e);
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
    private boolean selectRepository(boolean repositoryNotFound) {
    	IMultiFolderSelectionDialog dialog = Context.getBean(IMultiFolderSelectionDialog.class);
        dialog.setTitle(I18nUtils.getString("SELECT_REPOSITORY"));
        dialog.setText(I18nUtils.getString("SELECT_REPOSITORY_FOLDERS"));
        dialog.showDialog((repository != null && !repositoryNotFound) ? repository.getRepositoryFolders() : null);
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
     * Imports folders to repository
     */
    public void importFoldersToRepository() {
    }

    /**
     * Imports folders passed as argument to repository
     * 
     * @param folders
     * @param path
     */
    public void importFolders(final List<File> folders, final String path) {
    	IProgressDialog progressDialog = (IProgressDialog) Context.getBean("progressDialog");
    	progressDialog.setTitle(StringUtils.getString(I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."));
        progressDialog.disableCancelButton();
        progressDialog.showDialog();
        SwingWorker<List<ILocalAudioObject>, Void> worker = new ImportFoldersSwingWorker(folders, path, progressDialog);
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
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        // Update status bar
        GuiHandler.getInstance().showRepositoryAudioFileNumber(getAudioFilesList().size(), getRepositoryTotalSize(), repository.getTotalDurationInSeconds());
    }

    public void doInBackground() {
        if (currentLoader != null) {
            backgroundLoad = true;
            currentLoader.setPriority(Thread.MIN_PRIORITY);
            if (progressDialog != null) {
                progressDialog.hideProgressDialog();
            }
            GuiHandler.getInstance().showProgressBar(false, StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
            getFrame().getProgressBar().addMouseListener(progressBarMouseAdapter);
        }

    }

    /**
     * Enables or disables actions that can't be performed while loading
     * repository
     * 
     * @param enable
     */
    private void enableRepositoryActions(boolean enable) {
        Actions.getAction(SelectRepositoryAction.class).setEnabled(enable);
        Actions.getAction(RefreshRepositoryAction.class).setEnabled(enable);
        Actions.getAction(ImportToRepositoryAction.class).setEnabled(enable);
        Actions.getAction(ExportAction.class).setEnabled(enable);
        Actions.getAction(ConnectDeviceAction.class).setEnabled(enable);
        Actions.getAction(RipCDAction.class).setEnabled(enable);
        Actions.getAction(RefreshFolderFromNavigatorAction.class).setEnabled(enable);
    }

    /**
     * Returns <code>true</code>if there is a loader reading or refreshing
     * repository
     * 
     * @return
     */
    protected boolean isLoaderWorking() {
        return currentLoader != null;
    }

    /**
     * Returns folder where repository configuration is stored
     * 
     * @return
     */
    public String getRepositoryConfigurationFolder() {
        String customRepositoryConfigFolder = getOsManager().getCustomRepositoryConfigFolder();
        return customRepositoryConfigFolder != null ? customRepositoryConfigFolder : getOsManager().getUserConfigFolder(Kernel.isDebug());
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}
	
	@Override
	public void notifyCurrentAlbum(final String artist, final String album) {
		if (progressDialog != null && progressDialog.isVisible()) {
			if (lastArtistRead == null || lastAlbumRead == null || !lastArtistRead.equals(artist) || !lastAlbumRead.equals(album)) {
				lastArtistRead = artist;
				lastAlbumRead = album;
				if (coverWorker == null || coverWorker.isDone()) {
					// Try to find cover and set in progress dialog
					coverWorker = new SwingWorker<Image, Void>() {
						@Override
						protected Image doInBackground() throws Exception {
							return Context.getBean(IWebServicesHandler.class).getAlbumImage(artist, album);
						}

						@Override
						protected void done() {
							super.done();
							if (progressDialog != null) {
								try {
									progressDialog.setImage(get());
								} catch (InterruptedException e) {
									progressDialog.setImage(null);
								} catch (ExecutionException e) {
									progressDialog.setImage(null);
								}
							}
						}
					};
					coverWorker.execute();
				}
			}
		}
	}

	@Override
	public void repositoryChanged(final Repository repository) {
		repositoryChangesService.submit(new Runnable() {
			@Override
			public void run() {
				ApplicationStateHandler.getInstance().persistRepositoryCache(repository, true);
			}
		});
	}

	/**
	 * Starts a transaction
	 */
	public void startTransaction() {
		if (repository != null) {
			repository.startTransaction();
		}
	}
	
	/**
	 * Ends a transaction
	 */
	public void endTransaction() {
		if (repository != null) {
			repository.endTransaction();
		}
	}

	/**
	 * Returns data to show in tree
	 * @param viewMode
	 * @return
	 */
	public Map<String, ?> getDataForView(ViewMode viewMode) {
		return viewMode.getDataForView(repository);
	}

	public ILocalAudioObject getFile(String fileName) {
		if (repository != null) {
			return repository.getFile(fileName);
		}
		return null;
	}

	/**
	 * @param year
	 * @return
	 * @see net.sourceforge.atunes.model.Repository#getYear(java.lang.String)
	 */
	public Year getYear(String year) {
		if (repository != null) {
			return repository.getYear(year);
		}
		return null;
	}

	/**
	 * @param year
	 * @see net.sourceforge.atunes.model.Repository#removeYear(net.sourceforge.atunes.kernel.modules.repository.data.Year)
	 */
	public void removeYear(Year year) {
		if (repository != null) {
			repository.removeYear(year);
		}
	}

	/**
	 * @param file
	 * 
	 */
	public void removeFile(ILocalAudioObject file) {
		if (repository != null) {
			repository.removeFile(file);
			repository.removeSizeInBytes(file.getFile().length());
			repository.removeDurationInSeconds(file.getDuration());
		}
	}

	/**
	 * @param path
	 * @return
	 * @see net.sourceforge.atunes.model.Repository#getFolder(java.lang.String)
	 */
	public Folder getFolder(String path) {
		if (repository != null) {
			return repository.getFolder(path);
		}
		return null;
	}
}
