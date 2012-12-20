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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.process.SetStarsProcess;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * The repository handler.
 */
public final class RepositoryHandler extends AbstractHandler implements
		IRepositoryHandler {

	private RepositoryReader repositoryReader;

	private boolean caseSensitiveTrees;

	private IStatisticsHandler statisticsHandler;

	private INavigationHandler navigationHandler;

	private IStateHandler stateHandler;

	private IRepository repository;

	private VoidRepository voidRepository;

	private RepositoryAutoRefresher repositoryRefresher;

	/** Listeners notified when an audio file is removed */
	private final List<IAudioFilesRemovedListener> audioFilesRemovedListeners = new ArrayList<IAudioFilesRemovedListener>();

	private IFavoritesHandler favoritesHandler;

	private IRepositoryTransaction transaction;

	private ShowRepositoryDataHelper showRepositoryDataHelper;

	private PersistRepositoryTask persistRepositoryTask;

	private LocalAudioObjectRefresher localAudioObjectRefresher;

	private RepositoryRemover repositoryRemover;

	private IStateRepository stateRepository;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IDialogFactory dialogFactory;

	private List<File> foldersSelectedFromPreferences;

	private RepositoryAddService repositoryAddService;

	private IUnknownObjectChecker unknownObjectChecker;

	private IProcessFactory processFactory;

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param repositoryAddService
	 */
	public void setRepositoryAddService(
			final RepositoryAddService repositoryAddService) {
		this.repositoryAddService = repositoryAddService;
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
	 * @param repositoryRemover
	 */
	public void setRepositoryRemover(final RepositoryRemover repositoryRemover) {
		this.repositoryRemover = repositoryRemover;
	}

	/**
	 * @param localAudioObjectRefresher
	 */
	public void setLocalAudioObjectRefresher(
			final LocalAudioObjectRefresher localAudioObjectRefresher) {
		this.localAudioObjectRefresher = localAudioObjectRefresher;
	}

	/**
	 * @param persistRepositoryTask
	 */
	public void setPersistRepositoryTask(
			final PersistRepositoryTask persistRepositoryTask) {
		this.persistRepositoryTask = persistRepositoryTask;
	}

	/**
	 * @param voidRepository
	 */
	public void setVoidRepository(final VoidRepository voidRepository) {
		this.voidRepository = voidRepository;
	}

	/**
	 * @param showRepositoryDataHelper
	 */
	public void setShowRepositoryDataHelper(
			final ShowRepositoryDataHelper showRepositoryDataHelper) {
		this.showRepositoryDataHelper = showRepositoryDataHelper;
	}

	/**
	 * @param repository
	 */
	void setRepository(final IRepository repository) {
		this.repository = repository;
	}

	/**
	 * @param repositoryReader
	 */
	public void setRepositoryReader(final RepositoryReader repositoryReader) {
		this.repositoryReader = repositoryReader;
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

	/**
	 * @param stateHandler
	 */
	public void setStateHandler(final IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
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
			this.repositoryReader
					.newRepositoryWithFolders(this.foldersSelectedFromPreferences);
			this.foldersSelectedFromPreferences = null;
		} else if (this.caseSensitiveTrees != this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			this.caseSensitiveTrees = this.stateRepository
					.isKeyAlwaysCaseSensitiveInRepositoryStructure();
			refreshRepository();
		}
		// Reschedule repository refresher
		this.repositoryRefresher.start();
	}

	@Override
	protected void initHandler() {
		// Initially use void repository until one is loaded or selected
		this.repository = this.voidRepository;

		// Add itself as listener
		this.caseSensitiveTrees = this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure();
		addAudioFilesRemovedListener(this);
	}

	@Override
	public void addFilesAndRefresh(final List<File> files) {
		getBean(AddFilesToRepositoryTask.class).execute(this.repository, files);
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
				this.stateHandler.persistRepositoryCache(this.repository);
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
			final Map<String, IAlbum> albums) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (Map.Entry<String, IAlbum> entry : albums.entrySet()) {
			result.addAll(entry.getValue().getAudioObjects());
		}
		return result;
	}

	@Override
	public List<ILocalAudioObject> getAudioFilesForArtists(
			final Map<String, IArtist> artists) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (Map.Entry<String, IArtist> entry : artists.entrySet()) {
			result.addAll(entry.getValue().getAudioObjects());
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
		this.repositoryReader.notifyCancel();
	}

	@Override
	public void refreshFiles(final List<ILocalAudioObject> files) {
		startTransaction();
		for (ILocalAudioObject file : files) {
			this.localAudioObjectRefresher.refreshFile(this.repository, file);
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
			this.repositoryReader.refresh();
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
		for (ILocalAudioObject fileToRemove : filesToRemove) {
			this.repositoryRemover.deleteFile(fileToRemove);
		}

		// Notify listeners
		for (IAudioFilesRemovedListener listener : this.audioFilesRemovedListeners) {
			listener.audioFilesRemoved(filesToRemove);
		}
	}

	@Override
	public void rename(final ILocalAudioObject audioFile, final String name) {
		File file = audioFile.getFile();
		String extension = FilenameUtils
				.getExtension(net.sourceforge.atunes.utils.FileUtils
						.getPath(file));
		File newFile = getOsManager().getFile(
				FileUtils.getPath(file.getParentFile()),
				StringUtils.getString(
						FileNameUtils.getValidFileName(name, getOsManager()),
						".", extension));
		boolean succeeded = file.renameTo(newFile);
		if (succeeded) {
			renameFile(audioFile, file, newFile);
			this.navigationHandler.repositoryReloaded();
			this.statisticsHandler.updateFileName(audioFile,
					net.sourceforge.atunes.utils.FileUtils.getPath(file),
					net.sourceforge.atunes.utils.FileUtils.getPath(newFile));
		}
	}

	/**
	 * Renames a file in repository
	 * 
	 * @param audioFile
	 * @param oldFile
	 * @param newFile
	 */
	private void renameFile(final ILocalAudioObject audioFile,
			final File oldFile, final File newFile) {
		startTransaction();
		audioFile.setFile(newFile);
		this.repository.removeFile(oldFile);
		this.repository.putFile(audioFile);
		endTransaction();
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
		return this.repositoryReader.addFolderToRepository();
	}

	@Override
	public void addAudioFilesRemovedListener(
			final IAudioFilesRemovedListener listener) {
		this.audioFilesRemovedListeners.add(listener);
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		// Update status bar
		this.showRepositoryDataHelper.showRepositoryAudioFileNumber(
				getAudioFilesList().size(), getRepositoryTotalSize(),
				this.repository.getTotalDurationInSeconds());
	}

	@Override
	public void doInBackground() {
		this.repositoryReader.doInBackground();
	}

	/**
	 * Returns <code>true</code>if there is a loader reading or refreshing
	 * repository
	 * 
	 * @return
	 */
	protected boolean isLoaderWorking() {
		return this.repositoryReader.isWorking();
	}

	@Override
	public void repositoryChanged(final IRepository repository) {
		this.persistRepositoryTask.persist(repository);
		this.favoritesHandler.updateFavorites(repository);
	}

	protected final void startTransaction() {
		this.transaction = new RepositoryTransaction(this.repository, this);
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
		this.repository.removeSizeInBytes(file.getFile().length());
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

		IBackgroundWorker<List<ILocalAudioObject>> worker = this.backgroundWorkerFactory
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
		worker.execute();
	}

	@Override
	public void setRepositoryFolders(final List<File> folders) {
		this.foldersSelectedFromPreferences = folders;
	}

	@Override
	public void folderMoved(final IFolder sourceFolder, final File destination) {
		startTransaction();
		removeFoldersInsideTransaction(Collections.singletonList(sourceFolder));
		this.repositoryAddService.addFoldersToRepositoryInsideTransaction(
				this.repository, Collections.singletonList(destination));
		this.showRepositoryDataHelper.showRepositoryAudioFileNumber(
				this.repository.getFiles().size(),
				this.repository.getTotalSizeInBytes(),
				this.repository.getTotalDurationInSeconds());
		endTransaction();
	}

	@Override
	public void setStars(final ILocalAudioObject audioObject,
			final Integer value) {
		SetStarsProcess process = (SetStarsProcess) this.processFactory
				.getProcessByName("setStarsProcess");
		process.setFilesToChange(Collections.singletonList(audioObject));
		process.setStars(value);
		process.execute();
	}
}
