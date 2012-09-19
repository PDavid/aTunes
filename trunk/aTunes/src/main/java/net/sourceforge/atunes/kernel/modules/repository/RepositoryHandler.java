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

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * The repository handler.
 */
public final class RepositoryHandler extends AbstractHandler implements IRepositoryHandler {

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
	public void setBackgroundWorkerFactory(final IBackgroundWorkerFactory backgroundWorkerFactory) {
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
	public void setLocalAudioObjectRefresher(final LocalAudioObjectRefresher localAudioObjectRefresher) {
		this.localAudioObjectRefresher = localAudioObjectRefresher;
	}

	/**
	 * @param persistRepositoryTask
	 */
	public void setPersistRepositoryTask(final PersistRepositoryTask persistRepositoryTask) {
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
	public void setShowRepositoryDataHelper(final ShowRepositoryDataHelper showRepositoryDataHelper) {
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
	public void setRepositoryRefresher(final RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}

	@Override
	public void applicationStateChanged() {
		// User changed repository folders
		if (foldersSelectedFromPreferences != null) {
			repositoryReader.newRepositoryWithFolders(foldersSelectedFromPreferences);
			foldersSelectedFromPreferences = null;
		} else if (caseSensitiveTrees != stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
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
	public void addFilesAndRefresh(final List<File> files) {
		getBean(AddFilesTask.class).execute(repository, files);
	}

	/**
	 * Finish.
	 */
	@Override
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
	public IArtist getArtist(final String name) {
		return repository.getArtist(name);
	}

	@Override
	public void removeArtist(final IArtist artist) {
		repository.removeArtist(artist);
	}

	@Override
	public IGenre getGenre(final String genre) {
		return repository.getGenre(genre);
	}

	@Override
	public void removeGenre(final IGenre genre) {
		repository.removeGenre(genre);
	}

	@Override
	public ILocalAudioObject getFileIfLoaded(final String fileName) {
		return repository.getFile(fileName);
	}

	@Override
	public int getFoldersCount() {
		return repository.getRepositoryFolders().size();
	}

	@Override
	public File getRepositoryFolderContainingFile(final ILocalAudioObject file) {
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
	public List<ILocalAudioObject> getAudioFilesForAlbums(final Map<String, IAlbum> albums) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (Map.Entry<String, IAlbum> entry : albums.entrySet()) {
			result.addAll(entry.getValue().getAudioObjects());
		}
		return result;
	}

	@Override
	public List<ILocalAudioObject> getAudioFilesForArtists(final Map<String, IArtist> artists) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (Map.Entry<String, IArtist> entry : artists.entrySet()) {
			result.addAll(entry.getValue().getAudioObjects());
		}
		return result;
	}

	@Override
	public boolean isRepository(final File folder) {
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
	public void refreshFiles(final List<ILocalAudioObject> files) {
		startTransaction();
		for (ILocalAudioObject file : files) {
			localAudioObjectRefresher.refreshFile(repository, file);
		}
		endTransaction();
	}

	@Override
	public void refreshFolders(final List<IFolder> folders) {
		getBean(RefreshFoldersTask.class).execute(repository, folders);
	}

	@Override
	public void refreshRepository() {
		if (!isRepositoryVoid()) {
			repositoryReader.refresh();
		}
	}

	@Override
	public void removeFolders(final List<IFolder> foldersToRemove) {
		startTransaction();
		removeFoldersInsideTransaction(foldersToRemove);
		endTransaction();
	}

	private void removeFoldersInsideTransaction(final List<IFolder> foldersToRemove) {
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

		for (ILocalAudioObject fileToRemove : filesToRemove) {
			repositoryRemover.deleteFile(fileToRemove);
		}

		// Notify listeners
		for (IAudioFilesRemovedListener listener : audioFilesRemovedListeners) {
			listener.audioFilesRemoved(filesToRemove);
		}

		endTransaction();
	}

	@Override
	public void rename(final ILocalAudioObject audioFile, final String name) {
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
	private void renameFile(final ILocalAudioObject audioFile, final File oldFile, final File newFile) {
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
	public boolean addFolderToRepository() {
		return repositoryReader.addFolderToRepository();
	}

	@Override
	public void addAudioFilesRemovedListener(final IAudioFilesRemovedListener listener) {
		audioFilesRemovedListeners.add(listener);
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
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
	public void repositoryChanged(final IRepository repository) {
		persistRepositoryTask.persist(repository);
		favoritesHandler.updateFavorites(repository);
		// Update navigator
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				navigationHandler.repositoryReloaded();
			}
		});
	}

	protected final void startTransaction() {
		this.transaction = new RepositoryTransaction(repository, this);
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
		return viewMode.getDataForView(repository);
	}

	@Override
	public ILocalAudioObject getFile(final String fileName) {
		return repository.getFile(fileName);
	}

	@Override
	public IYear getYear(final String year) {
		return repository.getYear(year);
	}

	@Override
	public void removeYear(final IYear year) {
		repository.removeYear(year);
	}

	@Override
	public void removeFile(final ILocalAudioObject file) {
		repository.removeFile(file);
		repository.removeSizeInBytes(file.getFile().length());
		repository.removeDurationInSeconds(file.getDuration());
	}

	@Override
	public IFolder getFolder(final String path) {
		return repository.getFolder(path);
	}

	@Override
	public List<ILocalAudioObject> getAudioObjectsByTitle(final String artistName, final List<String> titlesList) {
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
	private Map<String, ILocalAudioObject> getNormalizedAudioObjectsTitles(final IArtist artist) {
		List<ILocalAudioObject> audioObjects = artist.getAudioObjects();
		Map<String, ILocalAudioObject> titles = new HashMap<String, ILocalAudioObject>();
		for (ILocalAudioObject lao : audioObjects) {
			if (lao.getTitle() != null) {
				titles.put(lao.getTitle().toLowerCase(), lao); // Do lower case for a better match
			}
		}
		return titles;
	}

	@Override
	public void importFolders(final List<File> folders, final String path) {
		final IIndeterminateProgressDialog indeterminateDialog = dialogFactory.newDialog(IIndeterminateProgressDialog.class);
		indeterminateDialog.setTitle(StringUtils.getString(I18nUtils.getString("READING_FILES_TO_IMPORT"), "..."));

		IBackgroundWorker<List<ILocalAudioObject>> worker = backgroundWorkerFactory.getWorker();
		worker.setActionsBeforeBackgroundStarts(new ShowIndeterminateDialogRunnable(indeterminateDialog));
		ImportFoldersToRepositoryCallable callable = Context.getBean(ImportFoldersToRepositoryCallable.class);
		callable.setFolders(folders);
		worker.setBackgroundActions(callable);
		ImportFoldersToRepositoryActionsWithBackgroundResult actionsWhenDone = Context.getBean(ImportFoldersToRepositoryActionsWithBackgroundResult.class);
		actionsWhenDone.setFolders(folders);
		actionsWhenDone.setPath(path);
		actionsWhenDone.setIndeterminateDialog(indeterminateDialog);
		worker.setActionsWhenDone(actionsWhenDone);
		worker.execute();
	}

	@Override
	public void setRepositoryFolders(final List<File> folders) {
		foldersSelectedFromPreferences = folders;
	}

	@Override
	public void folderMoved(final IFolder sourceFolder, final File destination) {
		startTransaction();
		removeFoldersInsideTransaction(Collections.singletonList(sourceFolder));
		repositoryAddService.addFoldersToRepositoryInsideTransaction(repository, Collections.singletonList(destination));
		showRepositoryDataHelper.showRepositoryAudioFileNumber(repository.getFiles().size(), repository.getTotalSizeInBytes(), repository.getTotalDurationInSeconds());
		endTransaction();
	}

	@Override
	public void setStars(final IAudioObject audioObject, final Integer value) {
		// Open repository transaction
		startTransaction();

		audioObject.setStars(value);

		// End repository transaction
		endTransaction();
	}
}
