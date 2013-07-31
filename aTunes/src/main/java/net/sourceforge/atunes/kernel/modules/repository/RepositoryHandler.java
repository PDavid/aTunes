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
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryListener;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The repository handler.
 */
public final class RepositoryHandler extends AbstractHandler implements
		IRepositoryHandler {

	private boolean caseSensitiveTrees;

	private boolean storeRatingInFile;

	private IStatisticsHandler statisticsHandler;

	private INavigationHandler navigationHandler;

	private IStateService stateService;

	private IRepository repository;

	private RepositoryAutoRefresher repositoryRefresher;

	/** Listeners notified when an audio file is removed */
	private final List<IAudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<IAudioFilesRemovedListener>();

	private IFavoritesHandler favoritesHandler;

	private IRepositoryTransaction transaction;

	private IStateRepository stateRepository;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IDialogFactory dialogFactory;

	private List<File> foldersSelectedFromPreferences;

	private IUnknownObjectChecker unknownObjectChecker;

	private IFileManager fileManager;

	private RepositoryReader currentRepositoryReader;

	private ITaskService taskService;

	private boolean repositoryNotSelected;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
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
	 * @param repository
	 */
	void setRepository(final IRepository repository) {
		this.repository = repository;
		setRepositoryNotSelected(repository == null
				|| (repository instanceof VoidRepository));
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(final IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param repositoryRefresher
	 */
	public void setRepositoryRefresher(
			final RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}

	@Override
	public void applicationStateChanged() {
		// User changed repository folders
		if (this.foldersSelectedFromPreferences != null) {
			this.currentRepositoryReader = getBean(RepositoryReader.class);
			this.currentRepositoryReader
					.newRepositoryWithFolders(this.foldersSelectedFromPreferences);
			this.foldersSelectedFromPreferences = null;
		} else if (checkPropertiesToTrackForReload()) {
			reloadRepository();
		}
		// Reschedule repository refresher
		this.repositoryRefresher.start();
	}

	@Override
	protected void initHandler() {
		// Initially use void repository until one is loaded or selected
		this.repository = getBean(VoidRepository.class);

		setPropertiesToTrackForReload();

		// Add itself as listener
		addAudioFilesRemovedListener(this);
	}

	private void setPropertiesToTrackForReload() {
		// A change in these properties needs a repository reload
		this.caseSensitiveTrees = this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure();
		this.storeRatingInFile = this.stateRepository.isStoreRatingInFile();
	}

	private boolean checkPropertiesToTrackForReload() {
		boolean reload = false;
		// Check properties and return true if needs to reload repository
		// Update properties to keep track or further changes
		if (this.caseSensitiveTrees != this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			reload = true;
			this.caseSensitiveTrees = this.stateRepository
					.isKeyAlwaysCaseSensitiveInRepositoryStructure();
		}
		if (this.storeRatingInFile != this.stateRepository
				.isStoreRatingInFile()) {
			reload = true;
			this.storeRatingInFile = this.stateRepository.isStoreRatingInFile();
		}

		return reload;
	}

	@Override
	public void addAudioObjectsAndRefresh(final List<ILocalAudioObject> result) {
		getBean(AddFilesToRepositoryTask.class)
				.execute(this.repository, result);
	}

	/**
	 * Finish.
	 */
	@Override
	public void applicationFinish() {
		this.repositoryRefresher.stop();
		if (!isRepositoryVoid()) {
			// Only store repository if it's dirty
			if (transactionPending()) {
				this.stateService.persistRepositoryCache(this.repository);
			} else {
				Logger.info("Repository is clean");
			}

			// Execute command after last access to repository
			new LoadRepositoryCommandExecutor().execute(this.stateRepository
					.getCommandAfterAccessRepository());
		}
	}

	@Override
	public List<File> getFolders() {
		return this.repository.getRepositoryFolders();
	}

	@Override
	public List<IAlbum> getAlbums() {
		List<IAlbum> result = new ArrayList<IAlbum>();
		Collection<IArtist> artists = this.repository.getArtists();
		for (IArtist a : artists) {
			result.addAll(a.getAlbums().values());
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public List<IArtist> getArtists() {
		List<IArtist> result = new ArrayList<IArtist>();
		result.addAll(this.repository.getArtists());
		Collections.sort(result);
		return result;
	}

	@Override
	public List<IGenre> getGenres() {
		List<IGenre> result = new ArrayList<IGenre>();
		result.addAll(this.repository.getGenres());
		Collections.sort(result);
		return result;
	}

	@Override
	public List<IYear> getYears() {
		List<IYear> result = new ArrayList<IYear>();
		result.addAll(this.repository.getYears());
		Collections.sort(result);
		return result;
	}

	@Override
	public IArtist getArtist(final String name) {
		return this.repository.getArtist(name);
	}

	@Override
	public void removeArtist(final IArtist artist) {
		this.repository.removeArtist(artist);
	}

	@Override
	public IGenre getGenre(final String genre) {
		return this.repository.getGenre(genre);
	}

	@Override
	public void removeGenre(final IGenre genre) {
		this.repository.removeGenre(genre);
	}

	@Override
	public ILocalAudioObject getFileIfLoaded(final String fileName) {
		return this.repository.getFile(fileName);
	}

	@Override
	public int getFoldersCount() {
		return this.repository.getRepositoryFolders().size();
	}

	@Override
	public File getRepositoryFolderContainingFile(final ILocalAudioObject file) {
		for (File folder : this.repository.getRepositoryFolders()) {
			if (file.getUrl().startsWith(
					net.sourceforge.atunes.utils.FileUtils.getPath(folder))) {
				return folder;
			}
		}
		return null;
	}

	@Override
	public String getRepositoryPath() {
		// TODO: Remove this method as now more than one folder can be added to
		// repository
		return this.repository.getRepositoryFolders().size() > 0 ? net.sourceforge.atunes.utils.FileUtils
				.getPath(this.repository.getRepositoryFolders().get(0)) : "";
	}

	@Override
	public long getRepositoryTotalSize() {
		return this.repository.getTotalSizeInBytes();
	}

	@Override
	public int getNumberOfFiles() {
		return this.repository.countFiles();
	}

	@Override
	public Collection<ILocalAudioObject> getAudioFilesList() {
		return this.repository.getFiles();
	}

	@Override
	public List<ILocalAudioObject> getAudioFilesForAlbums(
			final List<IAlbum> albums) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (IAlbum album : albums) {
			result.addAll(album.getAudioObjects());
		}
		return result;
	}

	@Override
	public List<ILocalAudioObject> getAudioFilesForArtists(
			final List<IArtist> artists) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (IArtist artist : artists) {
			result.addAll(artist.getAudioObjects());
		}
		return result;
	}

	@Override
	public boolean isRepository(final File folder) {
		String path = net.sourceforge.atunes.utils.FileUtils.getPath(folder);
		for (File folders : this.repository.getRepositoryFolders()) {
			if (path.startsWith(net.sourceforge.atunes.utils.FileUtils
					.getPath(folders))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void notifyCancel() {
		if (this.currentRepositoryReader != null) {
			this.currentRepositoryReader.notifyCancel();
		}
		this.currentRepositoryReader = null;
	}

	@Override
	public void refreshFiles(final List<ILocalAudioObject> files) {
		startTransaction();
		LocalAudioObjectRefresher refresher = getBean(LocalAudioObjectRefresher.class);
		for (ILocalAudioObject file : files) {
			refresher.refreshFile(this.repository, file);
		}
		endTransaction();
	}

	@Override
	public void refreshFolders(final List<IFolder> folders) {
		getBean(RefreshFoldersTask.class).execute(this.repository, folders);
	}

	@Override
	public void refreshRepository() {
		if (!isRepositoryVoid()) {
			this.currentRepositoryReader = getBean(RepositoryReader.class);
			this.currentRepositoryReader.refresh(this.repository);
		}
	}

	@Override
	public void reloadRepository() {
		if (!isRepositoryVoid()) {
			this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
					I18nUtils.getString("RELOAD_REPOSITORY_MESSAGE"));
			this.currentRepositoryReader = getBean(RepositoryReader.class);
			this.currentRepositoryReader
					.newRepositoryWithFoldersReloaded(this.repository
							.getRepositoryFolders());
		}
	}

	@Override
	public void removeFolders(final List<IFolder> foldersToRemove) {
		startTransaction();
		removeFoldersInsideTransaction(foldersToRemove);
		endTransaction();
	}

	private void removeFoldersInsideTransaction(
			final List<IFolder> foldersToRemove) {
		if (foldersToRemove == null || foldersToRemove.isEmpty()) {
			return;
		}

		for (IFolder folder : foldersToRemove) {

			// Remove content
			remove(folder.getAudioObjects());

			// Remove from model
			if (folder.getParentFolder() != null) {
				folder.getParentFolder().removeFolder(folder);
			}
		}
	}

	@Override
	public void remove(final List<ILocalAudioObject> filesToRemove) {
		if (filesToRemove == null || filesToRemove.isEmpty()) {
			return;
		}

		startTransaction();
		removeInsideTransaction(filesToRemove);
		endTransaction();
	}

	private void removeInsideTransaction(
			final List<ILocalAudioObject> filesToRemove) {
		RepositoryRemover remover = getBean(RepositoryRemover.class);
		for (ILocalAudioObject fileToRemove : filesToRemove) {
			remover.deleteFile(fileToRemove);
		}

		// Notify listeners
		for (IAudioFilesRemovedListener listener : this.audioFilesRemovedListeners) {
			listener.audioFilesRemoved(filesToRemove);
		}
	}

	@Override
	public void rename(final ILocalAudioObject audioFile, final String name) {
		String oldName = this.fileManager.getPath(audioFile);
		if (this.fileManager.rename(audioFile, name)) {
			startTransaction();
			this.repository.removeFile(oldName);
			this.repository.putFile(audioFile);
			endTransaction();
			this.navigationHandler.repositoryReloaded();
			this.statisticsHandler.updateFileName(audioFile, oldName,
					this.fileManager.getPath(audioFile));
		}
	}

	/**
	 * Returns if Repository is void (not yet loaded or selected)
	 * 
	 * @return
	 */
	private boolean isRepositoryVoid() {
		return this.repository instanceof VoidRepository;
	}

	@Override
	public boolean addFolderToRepository() {
		this.currentRepositoryReader = getBean(RepositoryReader.class);
		return this.currentRepositoryReader
				.addFolderToRepository(this.repository);
	}

	@Override
	public void addAudioFilesRemovedListener(
			final IAudioFilesRemovedListener listener) {
		this.audioFilesRemovedListeners.add(listener);
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		// Update status bar
		getBean(ShowRepositoryDataHelper.class).showRepositoryAudioFileNumber(
				getAudioFilesList().size(), getRepositoryTotalSize(),
				this.repository.getTotalDurationInSeconds());
	}

	@Override
	public void doInBackground() {
		if (this.currentRepositoryReader != null) {
			this.currentRepositoryReader.doInBackground();
		}
	}

	/**
	 * Returns <code>true</code>if there is a loader reading or refreshing
	 * repository
	 * 
	 * @return
	 */
	protected boolean isLoaderWorking() {
		return this.currentRepositoryReader != null
				&& this.currentRepositoryReader.isWorking();
	}

	@Override
	public void repositoryChanged(final IRepository repository) {
		getBean(PersistRepositoryTask.class).persist(repository);
		this.favoritesHandler.updateFavoritesAfterRepositoryChange(repository);
	}

	protected final void startTransaction() {
		this.transaction = new RepositoryTransaction(this.repository,
				getBeanFactory().getBeans(IRepositoryListener.class));
	}

	protected final void endTransaction() {
		if (this.transaction != null) {
			this.transaction.finishTransaction();
		}
	}

	private boolean transactionPending() {
		return this.transaction != null && this.transaction.isPending();
	}

	@Override
	public Map<String, ?> getDataForView(final ViewMode viewMode) {
		return viewMode.getDataForView(this.repository);
	}

	@Override
	public ILocalAudioObject getFile(final String fileName) {
		return this.repository.getFile(fileName);
	}

	@Override
	public IYear getYear(final String year) {
		return this.repository.getYear(year);
	}

	@Override
	public void removeYear(final IYear year) {
		this.repository.removeYear(year, this.unknownObjectChecker);
	}

	@Override
	public void removeFile(final ILocalAudioObject file) {
		this.repository.removeFile(file);
		this.repository.removeSizeInBytes(this.fileManager.getFileSize(file));
		this.repository.removeDurationInSeconds(file.getDuration());
	}

	@Override
	public IFolder getFolder(final String path) {
		return this.repository.getFolder(path);
	}

	@Override
	public List<ILocalAudioObject> getAudioObjectsByTitle(
			final String artistName, final List<String> titlesList) {
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

	@Override
	public void checkAvailability(final String artist,
			final List<ITrackInfo> tracks) {
		if (StringUtils.isEmpty(artist)) {
			throw new IllegalArgumentException("Invalid artist name");
		}
		if (!CollectionUtils.isEmpty(tracks)) {
			IArtist a = getArtist(artist);
			if (a != null) {
				Map<String, ILocalAudioObject> normalizedTitles = getNormalizedAudioObjectsTitles(a);
				for (ITrackInfo track : tracks) {
					track.setAvailable(normalizedTitles.containsKey(track
							.getTitle().toLowerCase()));
				}
			}
		}
	}

	/**
	 * 
	 * @param artist
	 * @return
	 */
	private Map<String, ILocalAudioObject> getNormalizedAudioObjectsTitles(
			final IArtist artist) {
		List<ILocalAudioObject> audioObjects = artist.getAudioObjects();
		Map<String, ILocalAudioObject> titles = new HashMap<String, ILocalAudioObject>();
		for (ILocalAudioObject lao : audioObjects) {
			if (lao.getTitle() != null) {
				titles.put(lao.getTitle().toLowerCase(), lao); // Do lower case
																// for a better
																// match
			}
		}
		return titles;
	}

	@Override
	public void importFolders(final List<File> folders, final String path) {
		final IIndeterminateProgressDialog indeterminateDialog = this.dialogFactory
				.newDialog(IIndeterminateProgressDialog.class);
		indeterminateDialog.setTitle(StringUtils.getString(
				I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."));

		IBackgroundWorker<List<ILocalAudioObject>, Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new ShowIndeterminateDialogRunnable(
				indeterminateDialog));
		ImportFoldersToRepositoryCallable callable = getBean(ImportFoldersToRepositoryCallable.class);
		callable.setFolders(folders);
		worker.setBackgroundActions(callable);
		ImportFoldersToRepositoryActionsWithBackgroundResult actionsWhenDone = getBean(ImportFoldersToRepositoryActionsWithBackgroundResult.class);
		actionsWhenDone.setFolders(folders);
		actionsWhenDone.setPath(path);
		actionsWhenDone.setIndeterminateDialog(indeterminateDialog);
		worker.setActionsWhenDone(actionsWhenDone);
		worker.execute(this.taskService);
	}

	@Override
	public void setRepositoryFolders(final List<File> folders) {
		this.foldersSelectedFromPreferences = folders;
	}

	@Override
	public void folderMoved(final IFolder sourceFolder, final File destination) {
		startTransaction();
		removeFoldersInsideTransaction(Collections.singletonList(sourceFolder));
		getBean(RepositoryAddService.class)
				.addFoldersToRepositoryInsideTransaction(this.repository,
						Collections.singletonList(destination));
		getBean(ShowRepositoryDataHelper.class).showRepositoryAudioFileNumber(
				this.repository.getFiles().size(),
				this.repository.getTotalSizeInBytes(),
				this.repository.getTotalDurationInSeconds());
		endTransaction();
	}

	@Override
	public IAudioObject getAudioObjectIfLoaded(final IAudioObject ao) {
		if (ao instanceof ILocalAudioObject) {
			IAudioObject cachedAO = getFileIfLoaded(ao.getUrl());
			if (cachedAO != null) {
				return cachedAO;
			}
		}
		return ao;
	}

	/**
	 * Called when repository read
	 */
	void notifyRepositoryRead() {
		this.currentRepositoryReader = null;
	}

	@Override
	public boolean existsArtist(final IArtist artist) {
		if (artist != null) {
			return this.repository.getArtist(artist.getName()) != null;
		}
		return false;
	}

	@Override
	public boolean existsArtist(final String artist) {
		return this.repository.getArtist(artist) != null;
	}

	@Override
	public boolean existsAlbum(final IAlbum album) {
		if (album != null) {
			IArtist artist = this.repository.getArtist(album.getArtist()
					.getName());
			if (artist != null) {
				if (artist.getAlbum(album.getName()) != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean existsAlbum(final String artistName, final String album) {
		IArtist artist = this.repository.getArtist(artistName);
		if (artist != null) {
			if (artist.getAlbum(album) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean existsFile(final ILocalAudioObject ao) {
		return getFileIfLoaded(ao.getUrl()) != null;
	}

	@Override
	public boolean isRepositoryNotSelected() {
		return this.repositoryNotSelected;
	}

	protected void setRepositoryNotSelected(final boolean notSelected) {
		this.repositoryNotSelected = notSelected;
		// Force navigation view refresh to show information about repository
		// not selected
		this.navigationHandler.refreshCurrentView();
	}
}
