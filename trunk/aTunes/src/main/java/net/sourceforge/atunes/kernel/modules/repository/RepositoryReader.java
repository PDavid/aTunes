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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IRepositoryProgressDialog;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * Reads repository
 * @author alex
 *
 */
public class RepositoryReader implements IRepositoryLoaderListener {

	// Used to retrieve covers and show in progress dialog
	private String lastArtistRead;

	private String lastAlbumRead;

	private IBackgroundWorker<ImageIcon> coverWorker;

	private int filesLoaded;

	private boolean backgroundLoad = false;

	private IRepository repositoryRetrievedFromCache = null;

	private IDialogFactory dialogFactory;

	private IRepositoryProgressDialog repositoryProgressDialog;

	private IRepositoryLoader currentLoader;

	private IRepository repository;

	private IFrame frame;

	private RepositoryHandler repositoryHandler;

	private INavigationHandler navigationHandler;

	private IWebServicesHandler webServicesHandler;

	private RepositoryActionsHelper repositoryActions;

	private ShowRepositoryDataHelper showRepositoryDataHelper;

	private IStateRepository stateRepository;

	private IBackgroundWorkerFactory backgroundWorkerFactory;
	
	private IOSManager osManager;
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
		this.repositoryProgressDialog = dialogFactory.newDialog(IRepositoryProgressDialog.class);
	}
	
	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
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
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param repositoryRetrievedFromCache
	 */
	void setRepositoryRetrievedFromCache(IRepository repositoryRetrievedFromCache) {
		this.repositoryRetrievedFromCache = repositoryRetrievedFromCache;
	}

	/**
	 * Sets the repository from the one read from cache
	 * @param askUser
	 */
	void applyRepositoryFromCache() {
		IRepository rep = repositoryRetrievedFromCache;
		if (rep != null && rep.exists()) {
			applyExistingRepository(rep);
		}
		if (repository == null) {
			applyRepository();
		}
	}

	void testRepositoryRetrievedFromCache() {
		IRepository rep = repositoryRetrievedFromCache;
		if (rep == null) {
			reloadExistingRepository();
		}
	}

	/**
	 * Sets the repository.
	 * @param askUser
	 */
	private void applyRepository() {
		IRepository rep = repositoryRetrievedFromCache;
		// Try to read repository cache. If fails or not exists, should be selected again
		if (rep != null) {
			if (!rep.exists()) {
				askUserForRepository(rep);
				if (!rep.exists() && !addFolderToRepository()) {
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
	private void askUserForRepository(final IRepository rep) {
		Object selection;
		do {
			selection = dialogFactory.newDialog(IMessageDialog.class).
			showMessage(StringUtils.getString(I18nUtils.getString("REPOSITORY_NOT_FOUND"), ": ", rep.getRepositoryFolders().get(0)),
					I18nUtils.getString("REPOSITORY_NOT_FOUND"), JOptionPane.WARNING_MESSAGE,
					new String[] { I18nUtils.getString("RETRY"), I18nUtils.getString("SELECT_REPOSITORY"), I18nUtils.getString("EXIT") });

			if (selection.equals(I18nUtils.getString("EXIT"))) {
				SwingUtilities.invokeLater(new ExitWhenRepositoryNotFoundRunnable());
			}
		} while (I18nUtils.getString("RETRY").equals(selection) && !rep.exists());
	}

	private void applyExistingRepository(IRepository rep) {
		repository = rep;
		repositoryReadCompleted();
	}

	/**
	 * If any repository was loaded previously, try to reload folders
	 */
	private void reloadExistingRepository() {
		List<String> lastRepositoryFolders = stateRepository.getLastRepositoryFolders();
		if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
			List<File> foldersToRead = new ArrayList<File>();
			for (String f : lastRepositoryFolders) {
				foldersToRead.add(new File(f));
			}
			dialogFactory.newDialog(IMessageDialog.class).showMessage(I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
			retrieve(foldersToRead);
		} else {
			RepositorySelectionInfoDialog dialog = dialogFactory.newDialog(RepositorySelectionInfoDialog.class);
			dialog.setVisible(true);
			if (dialog.userAccepted()) {
				repositoryHandler.addFolderToRepository();
			}
		}
	}

	/**
	 * Adds folder to repository
	 * @return true, if successful
	 */
	boolean addFolderToRepository() {
		IFolderSelectorDialog dialog = dialogFactory.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("ADD_FOLDER_TO_REPOSITORY"));
		File folder = dialog.selectFolder(osManager.getUserHome());
		if (folder != null) {
			// Need to use an array list instead of Collections.singletonList for Kryo serialization
			List<File> folders = new ArrayList<File>();
			folders.add(folder);
			retrieve(folders);
			return true;
		}
		return false;
	}

	private boolean retrieve(List<File> folders) {
		repositoryActions.enableRepositoryActions(false);
		// Start with indeterminate dialog
		repositoryProgressDialog.showDialog();
		repositoryProgressDialog.setProgressBarIndeterminate(true);
		frame.getProgressBar().setIndeterminate(true);
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
	 * Read repository.
	 * 
	 * @param folders
	 *            the folders
	 */
	private void readRepository(List<File> folders) {
		backgroundLoad = false;
		IRepository oldRepository = repository;
		repository = new Repository(folders, stateRepository);
		// Change repository to allow user start listening objects while loading
		repositoryHandler.setRepository(repository);
		currentLoader = Context.getBean(RepositoryReadLoader.class);
		currentLoader.setRepositoryLoaderListener(this);
		currentLoader.start(new RepositoryTransaction(repository, repositoryHandler), folders, oldRepository, repository);
	}

	@Override
	public void notifyCurrentPath(final String dir) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repositoryProgressDialog.setCurrentFolder(dir);
			}
		});
	}

	@Override
	public void notifyFileLoaded() {
		this.filesLoaded++;
		// Update GUI every 50 files
		if (this.filesLoaded % 50 == 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repositoryProgressDialog.setProgressText(Integer.toString(filesLoaded));
					repositoryProgressDialog.setProgressBarValue(filesLoaded);
					frame.getProgressBar().setValue(filesLoaded);
				}
			});
		}
	}

	@Override
	public void notifyFilesInRepository(int totalFiles) {
		// When total files has been calculated change to determinate progress bar
		repositoryProgressDialog.setProgressBarIndeterminate(false);
		repositoryProgressDialog.setTotalFiles(totalFiles);
		frame.getProgressBar().setIndeterminate(false);
		frame.getProgressBar().setMaximum(totalFiles);
	}

	@Override
	public void notifyFinishRead(IRepositoryLoader loader) {
		repositoryProgressDialog.setButtonsEnabled(false);
		repositoryProgressDialog.setCurrentTask(I18nUtils.getString("STORING_REPOSITORY_INFORMATION"));
		repositoryProgressDialog.setProgressText("");
		repositoryProgressDialog.setCurrentFolder("");

		// Save folders: if repository config is lost application can reload data without asking user to select folders again
		List<String> repositoryFolders = new ArrayList<String>();
		for (File folder : repository.getRepositoryFolders()) {
			repositoryFolders.add(folder.getAbsolutePath());
		}
		stateRepository.setLastRepositoryFolders(repositoryFolders);

		if (backgroundLoad) {
			frame.hideProgressBar();
		}

		repositoryReadCompleted();
	}

	@Override
	public void notifyFinishRefresh(IRepositoryLoader loader) {
		repositoryActions.enableRepositoryActions(true);

		frame.hideProgressBar();
		showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository.getTotalDurationInSeconds());
		navigationHandler.repositoryReloaded();
		Logger.info("Repository refresh done");

		currentLoader = null;
	}

	@Override
	public void notifyRemainingTime(final long millis) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (repositoryProgressDialog != null) {
					repositoryProgressDialog.setRemainingTime(StringUtils.getString(I18nUtils.getString("REMAINING_TIME"), ":   ", TimeUtils.millisecondsToHoursMinutesSeconds(millis)));
				}
			}
		});
	}

	@Override
	public void notifyReadProgress() {
		try {
			// Use invoke and wait in this case to avoid concurrent problems while reading repository and showing nodes in navigator tree (ConcurrentModificationException)
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					navigationHandler.repositoryReloaded();
					showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository.getTotalDurationInSeconds());
				}
			});
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		}
	}

	/**
	 * Refresh.
	 */
	void refresh() {
		Logger.info("Refreshing repository");
		filesLoaded = 0;
		IRepository oldRepository = repository;
		repository = new Repository(oldRepository.getRepositoryFolders(), stateRepository);
		currentLoader = Context.getBean(RepositoryRefreshLoader.class);
		currentLoader.setRepositoryLoaderListener(this);
		currentLoader.start(new RepositoryTransaction(repository, repositoryHandler), oldRepository.getRepositoryFolders(), oldRepository, repository);
	}

	@Override
	public void notifyCurrentAlbum(final String artist, final String album) {
		if (isProgressDialogVisible() && albumNotReadBefore(artist, album)) {
			lastArtistRead = artist;
			lastAlbumRead = album;
			if (coverWorker == null || coverWorker.isDone()) {
				// Try to find cover and set in progress dialog
				coverWorker = backgroundWorkerFactory.getWorker();
				coverWorker.setBackgroundActions(new Callable<ImageIcon>() {

					@Override
					public ImageIcon call() {
						return webServicesHandler.getAlbumImage(artist, album);
					}
				});

				coverWorker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<ImageIcon>() {
					@Override
					public void call(ImageIcon result) {
						repositoryProgressDialog.setImage(result != null ? result.getImage() : null);
					}
				});
				coverWorker.execute();
			}
		}
	}

	/**
	 * @param artist
	 * @param album
	 * @return
	 */
	private boolean albumNotReadBefore(final String artist, final String album) {
		return lastArtistRead == null || lastAlbumRead == null || !lastArtistRead.equals(artist) || !lastAlbumRead.equals(album);
	}

	/**
	 * @return
	 */
	private boolean isProgressDialogVisible() {
		return repositoryProgressDialog.isVisible();
	}

	/**
	 * Hides progress dialog so user can work with application while repository finishes loading
	 */
	public void doInBackground() {
		if (currentLoader != null) {
			backgroundLoad = true;
			repositoryProgressDialog.hideDialog();
			frame.showProgressBar(false, StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
			frame.getProgressBar().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					backgroundLoad = false;
					frame.hideProgressBar();
					repositoryProgressDialog.showDialog();
					frame.getProgressBar().removeMouseListener(this);
				};
			});
		}

	}

	void notifyCancel() {
		currentLoader.interruptLoad();
		repository = currentLoader.getOldRepository();
		if (repository == null) {
			repository = new VoidRepository();
		}
		repositoryReadCompleted();
	}

	/**
	 * Notify finish repository read.
	 */
	private void repositoryReadCompleted() {
		repositoryProgressDialog.hideDialog();
		repositoryHandler.setRepository(repository);
		repositoryActions.enableRepositoryActions(true);
		showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository.getTotalDurationInSeconds());
		navigationHandler.repositoryReloaded();
		currentLoader = null;
	}

	/**
	 * Returns <code>true</code>if there is a loader reading or refreshing
	 * repository
	 * 
	 * @return
	 */
	protected boolean isWorking() {
		return currentLoader != null;
	}

	/**
	 * @return the progressDialog
	 */
	protected IRepositoryProgressDialog getProgressDialog() {
		return repositoryProgressDialog;
	}

	/**
	 * Creates new repository with given folders
	 * @param folders
	 */
	protected void newRepositoryWithFolders(List<File> folders) {
		retrieve(folders);
	}
}
