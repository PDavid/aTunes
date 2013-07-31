/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryListener;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IRepositoryProgressDialog;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.ITaskService;
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

	private IBackgroundWorker<ImageIcon, Void> coverWorker;

	private int filesLoaded;

	private IRepository repository;

	private IDialogFactory dialogFactory;

	private IRepositoryProgressDialog repositoryProgressDialog;

	private IRepositoryLoader currentLoader;

	private IFrame frame;

	private RepositoryHandler repositoryHandler;

	private INavigationHandler navigationHandler;

	private IWebServicesHandler webServicesHandler;

	private IStateRepository stateRepository;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IOSManager osManager;

	private IBeanFactory beanFactory;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

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
	}

	private IRepositoryProgressDialog getRepositoryProgressDialog() {
		if (this.repositoryProgressDialog == null) {
			this.repositoryProgressDialog = this.dialogFactory
					.newDialog(IRepositoryProgressDialog.class);
		}
		return this.repositoryProgressDialog;
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
	 * Adds folder to repository
	 * 
	 * @return true, if successful
	 */
	boolean addFolderToRepository(final IRepository repository) {
		this.repository = repository;
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
			retrieve(folders, false);
			return true;
		}
		return false;
	}

	private boolean retrieve(final List<File> folders, final boolean reload) {
		GuiUtils.callInEventDispatchThreadAndWait(new Runnable() {
			@Override
			public void run() {
				RepositoryReader.this.beanFactory.getBean(
						RepositoryActionsHelper.class)
						.disableAllRepositoryActions();
				// Start with indeterminate dialog
				getRepositoryProgressDialog().showDialog();
				getRepositoryProgressDialog().setProgressBarIndeterminate(true);
				RepositoryReader.this.frame.getProgressBar().setIndeterminate(
						true);
			}
		});
		this.filesLoaded = 0;
		try {
			if (folders == null || folders.isEmpty()) {
				this.repository = null;
				return false;
			}
			readRepository(folders, reload);
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
	 * @param reload
	 */
	private void readRepository(final List<File> folders, final boolean reload) {
		IRepository oldRepository = this.repository;
		this.repository = new Repository(folders, this.stateRepository);
		// Change repository to allow user start listening objects while loading
		this.repositoryHandler.setRepository(this.repository);
		this.currentLoader = this.beanFactory
				.getBean(RepositoryReadLoader.class);
		this.currentLoader.setRepositoryLoaderListener(this);
		this.currentLoader.start(new RepositoryTransaction(this.repository,
				this.beanFactory.getBeans(IRepositoryListener.class)), folders,
				reload ? null : oldRepository, this.repository);
	}

	@Override
	public void notifyCurrentPath(final String dir) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getRepositoryProgressDialog().setCurrentFolder(dir);
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
					getRepositoryProgressDialog()
							.setProgressText(
									Integer.toString(RepositoryReader.this.filesLoaded));
					getRepositoryProgressDialog().setProgressBarValue(
							RepositoryReader.this.filesLoaded);
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
		getRepositoryProgressDialog().setProgressBarIndeterminate(false);
		getRepositoryProgressDialog().setTotalFiles(totalFiles);
		this.frame.getProgressBar().setIndeterminate(false);
		this.frame.getProgressBar().setMaximum(totalFiles);
	}

	@Override
	public void notifyFinishRead(final IRepositoryLoader loader) {
		getRepositoryProgressDialog().setButtonsEnabled(false);
		getRepositoryProgressDialog().setCurrentTask(
				I18nUtils.getString("STORING_REPOSITORY_INFORMATION"));
		getRepositoryProgressDialog().setProgressText("");
		getRepositoryProgressDialog().setCurrentFolder("");

		// Save folders: if repository config is lost application can reload
		// data without asking user to select folders again
		List<String> repositoryFolders = new ArrayList<String>();
		for (File folder : this.repository.getRepositoryFolders()) {
			repositoryFolders.add(net.sourceforge.atunes.utils.FileUtils
					.getPath(folder));
		}
		this.stateRepository.setLastRepositoryFolders(repositoryFolders);

		getRepositoryProgressDialog().hideDialog();

		this.beanFactory.getBean(RepositoryLoadedActions.class)
				.repositoryReadCompleted(this.repository);
		this.currentLoader = null;
	}

	@Override
	public void notifyFinishRefresh(final IRepositoryLoader loader) {
		Logger.info("Repository refresh done");
		this.beanFactory.getBean(RepositoryLoadedActions.class)
				.repositoryReadCompleted(this.repository);
		this.currentLoader = null;
	}

	@Override
	public void notifyRemainingTime(final long millis) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (getRepositoryProgressDialog() != null) {
					getRepositoryProgressDialog()
							.setRemainingTime(
									StringUtils.getString(
											I18nUtils
													.getString("REMAINING_TIME"),
											":   ",
											TimeUtils
													.millisecondsToHoursMinutesSeconds(millis)));
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
					RepositoryReader.this.beanFactory.getBean(
							ShowRepositoryDataHelper.class)
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
	 * Refreshes oldRepository
	 */
	void refresh(final IRepository oldRepository) {
		Logger.info("Refreshing repository");
		this.filesLoaded = 0;
		this.repository = new Repository(oldRepository.getRepositoryFolders(),
				this.stateRepository);
		this.currentLoader = this.beanFactory
				.getBean(RepositoryRefreshLoader.class);
		((RepositoryRefreshLoader) this.currentLoader)
				.setDisableRepositoryActions(true);
		this.currentLoader.setRepositoryLoaderListener(this);
		this.currentLoader.start(new RepositoryTransaction(this.repository,
				this.beanFactory.getBeans(IRepositoryListener.class)),
				oldRepository.getRepositoryFolders(), oldRepository,
				this.repository);
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
								getRepositoryProgressDialog().setImage(
										result != null ? result.getImage()
												: null);
							}
						});
				this.coverWorker.execute(this.taskService);
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
		return getRepositoryProgressDialog().isVisible();
	}

	/**
	 * Hides progress dialog so user can work with application while repository
	 * finishes loading
	 */
	public void doInBackground() {
		if (this.currentLoader != null) {
			getRepositoryProgressDialog().hideDialog();
			this.frame.showProgressBar(false, StringUtils.getString(
					I18nUtils.getString("LOADING"), "..."));
			this.frame.getProgressBar().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					RepositoryReader.this.frame.hideProgressBar();
					getRepositoryProgressDialog().showDialog();
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
			getRepositoryProgressDialog().hideDialog();
			this.beanFactory.getBean(RepositoryLoadedActions.class)
					.repositoryReadCompleted(this.repository);
			this.currentLoader = null;
		}
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
	 * Creates new repository with given folders Don't reload folders already
	 * read
	 * 
	 * @param folders
	 */
	protected void newRepositoryWithFolders(final List<File> folders) {
		retrieve(folders, false);
	}

	/**
	 * Creates new repository with given folders Reloads all folders
	 * 
	 * @param folders
	 */
	protected void newRepositoryWithFoldersReloaded(final List<File> folders) {
		retrieve(folders, true);
	}
}
