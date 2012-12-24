/*
 * aTunes 3.1.0
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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IKernel;
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
 * 
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

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
		this.repositoryProgressDialog = dialogFactory
				.newDialog(IRepositoryProgressDialog.class);
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @param showRepositoryDataHelper
	 */
	public void setShowRepositoryDataHelper(
			final ShowRepositoryDataHelper showRepositoryDataHelper) {
		this.showRepositoryDataHelper = showRepositoryDataHelper;
	}

	/**
	 * @param repositoryActions
	 */
	public void setRepositoryActions(
			final RepositoryActionsHelper repositoryActions) {
		this.repositoryActions = repositoryActions;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param repositoryRetrievedFromCache
	 */
	void setRepositoryRetrievedFromCache(
			final IRepository repositoryRetrievedFromCache) {
		this.repositoryRetrievedFromCache = repositoryRetrievedFromCache;
	}

	/**
	 * Sets the repository from the one read from cache
	 * 
	 * @param askUser
	 */
	void applyRepositoryFromCache() {
		IRepository rep = this.repositoryRetrievedFromCache;
		if (rep != null && rep.exists()) {
			applyExistingRepository(rep);
		}
		if (this.repository == null) {
			applyRepository();
		}
	}

	void testRepositoryRetrievedFromCache() {
		IRepository rep = this.repositoryRetrievedFromCache;
		if (rep == null) {
			reloadExistingRepository();
		}
	}

	/**
	 * Sets the repository.
	 * 
	 * @param askUser
	 */
	private void applyRepository() {
		IRepository rep = this.repositoryRetrievedFromCache;
		// Try to read repository cache. If fails or not exists, should be
		// selected again
		if (rep != null) {
			if (!rep.exists()) {
				askUserForRepository(rep);
				if (!rep.exists() && !addFolderToRepository()) {
					// select "old" repository if repository was not found and
					// no new repository was selected
					this.repository = rep;
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
			selection = this.dialogFactory
					.newDialog(IMessageDialog.class)
					.showMessage(
							StringUtils.getString(
									I18nUtils.getString("REPOSITORY_NOT_FOUND"),
									": ", rep.getRepositoryFolders().get(0)),
							I18nUtils.getString("REPOSITORY_NOT_FOUND"),
							JOptionPane.WARNING_MESSAGE,
							new String[] { I18nUtils.getString("RETRY"),
									I18nUtils.getString("SELECT_REPOSITORY"),
									I18nUtils.getString("EXIT") });

			if (selection.equals(I18nUtils.getString("EXIT"))) {
				GuiUtils.callInEventDispatchThread(new Runnable() {
					@Override
					public void run() {
						RepositoryReader.this.beanFactory
								.getBean(IKernel.class).finish();
					}

				});
			}
		} while (I18nUtils.getString("RETRY").equals(selection)
				&& !rep.exists());
	}

	private void applyExistingRepository(final IRepository rep) {
		this.repository = rep;
		repositoryReadCompleted();
	}

	/**
	 * If any repository was loaded previously, try to reload folders
	 */
	private void reloadExistingRepository() {
		List<String> lastRepositoryFolders = this.stateRepository
				.getLastRepositoryFolders();
		if (lastRepositoryFolders != null && !lastRepositoryFolders.isEmpty()) {
			List<File> foldersToRead = new ArrayList<File>();
			for (String f : lastRepositoryFolders) {
				foldersToRead.add(new File(f));
			}
			this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
					I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
			retrieve(foldersToRead);
		} else {
			RepositorySelectionInfoDialog dialog = this.dialogFactory
					.newDialog(RepositorySelectionInfoDialog.class);
			dialog.setVisible(true);
			if (dialog.userAccepted()) {
				this.repositoryHandler.addFolderToRepository();
			}
		}
	}

	/**
	 * Adds folder to repository
	 * 
	 * @return true, if successful
	 */
	boolean addFolderToRepository() {
		IFolderSelectorDialog dialog = this.dialogFactory
				.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("ADD_FOLDER_TO_REPOSITORY"));
		File folder = dialog.selectFolder(this.osManager.getUserHome());
		if (folder != null) {
			// Need to use an array list instead of Collections.singletonList
			// for Kryo serialization
			List<File> folders = new ArrayList<File>();
			// Add new folder
			folders.add(folder);
			// Add previous folders
			folders.addAll(this.repositoryHandler.getFolders());
			retrieve(folders);
			return true;
		}
		return false;
	}

	private boolean retrieve(final List<File> folders) {
		this.repositoryActions.disableAllRepositoryActions();
		// Start with indeterminate dialog
		this.repositoryProgressDialog.showDialog();
		this.repositoryProgressDialog.setProgressBarIndeterminate(true);
		this.frame.getProgressBar().setIndeterminate(true);
		this.filesLoaded = 0;
		try {
			if (folders == null || folders.isEmpty()) {
				this.repository = null;
				return false;
			}
			readRepository(folders);
			return true;
		} catch (Exception e) {
			this.repository = null;
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
	private void readRepository(final List<File> folders) {
		this.backgroundLoad = false;
		IRepository oldRepository = this.repository;
		this.repository = new Repository(folders, this.stateRepository);
		// Change repository to allow user start listening objects while loading
		this.repositoryHandler.setRepository(this.repository);
		this.currentLoader = this.beanFactory
				.getBean(RepositoryReadLoader.class);
		this.currentLoader.setRepositoryLoaderListener(this);
		this.currentLoader.start(new RepositoryTransaction(this.repository,
				this.repositoryHandler), folders, oldRepository,
				this.repository);
	}

	@Override
	public void notifyCurrentPath(final String dir) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RepositoryReader.this.repositoryProgressDialog
						.setCurrentFolder(dir);
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
					RepositoryReader.this.repositoryProgressDialog.setProgressText(Integer
							.toString(RepositoryReader.this.filesLoaded));
					RepositoryReader.this.repositoryProgressDialog
							.setProgressBarValue(RepositoryReader.this.filesLoaded);
					RepositoryReader.this.frame.getProgressBar().setValue(
							RepositoryReader.this.filesLoaded);
				}
			});
		}
	}

	@Override
	public void notifyFilesInRepository(final int totalFiles) {
		// When total files has been calculated change to determinate progress
		// bar
		this.repositoryProgressDialog.setProgressBarIndeterminate(false);
		this.repositoryProgressDialog.setTotalFiles(totalFiles);
		this.frame.getProgressBar().setIndeterminate(false);
		this.frame.getProgressBar().setMaximum(totalFiles);
	}

	@Override
	public void notifyFinishRead(final IRepositoryLoader loader) {
		this.repositoryProgressDialog.setButtonsEnabled(false);
		this.repositoryProgressDialog.setCurrentTask(I18nUtils
				.getString("STORING_REPOSITORY_INFORMATION"));
		this.repositoryProgressDialog.setProgressText("");
		this.repositoryProgressDialog.setCurrentFolder("");

		// Save folders: if repository config is lost application can reload
		// data without asking user to select folders again
		List<String> repositoryFolders = new ArrayList<String>();
		for (File folder : this.repository.getRepositoryFolders()) {
			repositoryFolders.add(net.sourceforge.atunes.utils.FileUtils
					.getPath(folder));
		}
		this.stateRepository.setLastRepositoryFolders(repositoryFolders);

		if (this.backgroundLoad) {
			this.frame.hideProgressBar();
		}

		this.repositoryProgressDialog.hideDialog();

		repositoryReadCompleted();
	}

	@Override
	public void notifyFinishRefresh(final IRepositoryLoader loader) {
		this.frame.hideProgressBar();
		Logger.info("Repository refresh done");
		repositoryReadCompleted();
	}

	@Override
	public void notifyRemainingTime(final long millis) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (RepositoryReader.this.repositoryProgressDialog != null) {
					RepositoryReader.this.repositoryProgressDialog.setRemainingTime(StringUtils.getString(
							I18nUtils.getString("REMAINING_TIME"), ":   ",
							TimeUtils.millisecondsToHoursMinutesSeconds(millis)));
				}
			}
		});
	}

	@Override
	public void notifyReadProgress() {
		try {
			// Use invoke and wait in this case to avoid concurrent problems
			// while reading repository and showing nodes in navigator tree
			// (ConcurrentModificationException)
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					RepositoryReader.this.navigationHandler
							.repositoryReloaded();
					RepositoryReader.this.showRepositoryDataHelper
							.showRepositoryAudioFileNumber(
									RepositoryReader.this.repository.getFiles()
											.size(),
									RepositoryReader.this.repository
											.getTotalSizeInBytes(),
									RepositoryReader.this.repository
											.getTotalDurationInSeconds());
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
		this.filesLoaded = 0;
		IRepository oldRepository = this.repository;
		this.repository = new Repository(oldRepository.getRepositoryFolders(),
				this.stateRepository);
		this.currentLoader = this.beanFactory
				.getBean(RepositoryRefreshLoader.class);
		this.currentLoader.setRepositoryLoaderListener(this);
		this.currentLoader.start(new RepositoryTransaction(this.repository,
				this.repositoryHandler), oldRepository.getRepositoryFolders(),
				oldRepository, this.repository);
	}

	@Override
	public void notifyCurrentAlbum(final String artist, final String album) {
		if (isProgressDialogVisible() && albumNotReadBefore(artist, album)) {
			this.lastArtistRead = artist;
			this.lastAlbumRead = album;
			if (this.coverWorker == null || this.coverWorker.isDone()) {
				// Try to find cover and set in progress dialog
				this.coverWorker = this.backgroundWorkerFactory.getWorker();
				this.coverWorker
						.setBackgroundActions(new Callable<ImageIcon>() {

							@Override
							public ImageIcon call() {
								return RepositoryReader.this.webServicesHandler
										.getAlbumImage(artist, album);
							}
						});

				this.coverWorker
						.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<ImageIcon>() {
							@Override
							public void call(final ImageIcon result) {
								RepositoryReader.this.repositoryProgressDialog
										.setImage(result != null ? result
												.getImage() : null);
							}
						});
				this.coverWorker.execute();
			}
		}
	}

	/**
	 * @param artist
	 * @param album
	 * @return
	 */
	private boolean albumNotReadBefore(final String artist, final String album) {
		return this.lastArtistRead == null || this.lastAlbumRead == null
				|| !this.lastArtistRead.equals(artist)
				|| !this.lastAlbumRead.equals(album);
	}

	/**
	 * @return
	 */
	private boolean isProgressDialogVisible() {
		return this.repositoryProgressDialog.isVisible();
	}

	/**
	 * Hides progress dialog so user can work with application while repository
	 * finishes loading
	 */
	public void doInBackground() {
		if (this.currentLoader != null) {
			this.backgroundLoad = true;
			this.repositoryProgressDialog.hideDialog();
			this.frame.showProgressBar(false, StringUtils.getString(
					I18nUtils.getString("LOADING"), "..."));
			this.frame.getProgressBar().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					RepositoryReader.this.backgroundLoad = false;
					RepositoryReader.this.frame.hideProgressBar();
					RepositoryReader.this.repositoryProgressDialog.showDialog();
					RepositoryReader.this.frame.getProgressBar()
							.removeMouseListener(this);
				};
			});
		}

	}

	void notifyCancel() {
		if (this.currentLoader != null) {
			this.currentLoader.interruptLoad();
			this.repository = this.currentLoader.getOldRepository();
			if (this.repository == null) {
				this.repository = new VoidRepository();
			}
			this.repositoryProgressDialog.hideDialog();
			repositoryReadCompleted();
		}
	}

	/**
	 * Notify finish repository read.
	 */
	private void repositoryReadCompleted() {
		this.repositoryHandler.setRepository(this.repository);
		this.repositoryActions
				.enableActionsDependingOnRepository(this.repository);
		this.showRepositoryDataHelper.showRepositoryAudioFileNumber(
				this.repository.getFiles().size(),
				this.repository.getTotalSizeInBytes(),
				this.repository.getTotalDurationInSeconds());
		this.navigationHandler.repositoryReloaded();
		this.currentLoader = null;
	}

	/**
	 * Returns <code>true</code>if there is a loader reading or refreshing
	 * repository
	 * 
	 * @return
	 */
	protected boolean isWorking() {
		return this.currentLoader != null;
	}

	/**
	 * @return the progressDialog
	 */
	protected IRepositoryProgressDialog getProgressDialog() {
		return this.repositoryProgressDialog;
	}

	/**
	 * Creates new repository with given folders
	 * 
	 * @param folders
	 */
	protected void newRepositoryWithFolders(final List<File> folders) {
		retrieve(folders);
	}
}
