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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.DisconnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.RefreshDeviceAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectTransferProcess;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * @author alex
 * 
 */
public final class DeviceHandler extends AbstractHandler implements
		IDeviceHandler {

	private final class CopyFilesToDeviceProcessListener implements
			IProcessListener<List<ILocalAudioObject>> {

		@Override
		public void processCanceled() {
			// Nothing to do
		}

		@Override
		public void processFinished(final boolean ok,
				final List<ILocalAudioObject> result) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					refreshDevice();
					DeviceHandler.this.filesCopiedToDevice = result.size();
					if (!ok) {
						DeviceHandler.this.dialogFactory
								.newDialog(IErrorDialog.class)
								.showErrorDialog(
										I18nUtils
												.getString("ERRORS_IN_EXPORT_PROCESS"));
					}
				}
			});
		}
	}

	private IRepository deviceRepository;
	private IRepositoryLoader currentLoader;
	private File devicePath;
	/**
	 * Device identification
	 */
	private String deviceId;
	/**
	 * Used to store number of files copied
	 */
	private int filesCopiedToDevice;
	private boolean caseSensitiveTrees;

	private DeviceMonitor deviceMonitor;

	private IProcessFactory processFactory;

	private INavigationHandler navigationHandler;

	private INavigationView deviceNavigationView;

	private IStateRepository stateRepository;

	private IStateDevice stateDevice;

	private IDialogFactory dialogFactory;

	private IRepositoryHandler repositoryHandler;

	private IBeanFactory beanFactory;

	private IUnknownObjectChecker unknownObjectChecker;

	private IFileManager fileManager;

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
	 * @param beanFactory
	 */
	@Override
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(final IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @param deviceNavigationView
	 */
	public void setDeviceNavigationView(
			final INavigationView deviceNavigationView) {
		this.deviceNavigationView = deviceNavigationView;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param deviceMonitor
	 */
	public void setDeviceMonitor(final DeviceMonitor deviceMonitor) {
		this.deviceMonitor = deviceMonitor;
	}

	@Override
	protected void initHandler() {
		this.caseSensitiveTrees = this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure();
		this.repositoryHandler.addAudioFilesRemovedListener(this);
	}

	@Override
	public void deferredInitialization() {
		if (isDefaultDeviceLocationConfigured(this.stateDevice)) {
			// Start device monitor if necessary
			this.deviceMonitor.startMonitor();
		}
	}

	@Override
	public void applicationStateChanged() {
		if (this.caseSensitiveTrees != this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			this.caseSensitiveTrees = this.stateRepository
					.isKeyAlwaysCaseSensitiveInRepositoryStructure();
			refreshDevice();
		}

		if (isDefaultDeviceLocationConfigured(this.stateDevice)
				&& !this.deviceMonitor.isMonitorRunning()) {
			this.deviceMonitor.startMonitor();
		} else if (!isDefaultDeviceLocationConfigured(this.stateDevice)
				&& this.deviceMonitor.isMonitorRunning()) {
			this.deviceMonitor.stopMonitor();
		}
	}

	/**
	 * Returns if user configured a default device location
	 * 
	 * @param state
	 * @return
	 */
	private boolean isDefaultDeviceLocationConfigured(final IStateDevice state) {
		return state.getDefaultDeviceLocation() != null
				&& !state.getDefaultDeviceLocation().isEmpty();
	}

	@Override
	public void fillWithRandomSongs(final long leaveFreeLong) {
		long leaveFree = leaveFreeLong;

		// Get reference to Repository songs
		List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(
				this.repositoryHandler.getAudioFilesList());

		// Songs selected
		Map<Integer, ILocalAudioObject> songsSelected = new HashMap<Integer, ILocalAudioObject>();

		// Initialize random generator
		Random r = new Random(System.currentTimeMillis());

		// Get free space in device
		long deviceFreeSpace = this.deviceRepository.getRepositoryFolders()
				.get(0).getFreeSpace();
		if (leaveFree >= 0) {
			leaveFree = (leaveFree) * 1048576; // Transfrom to same unit
		} else {
			leaveFree = 0; // User typed nonsense value. So fill his device ...
		}

		// Not enough space avaible
		if (leaveFree > deviceFreeSpace) {
			Logger.debug(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
			this.dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(
					I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
			return;
		}

		// Get n songs
		// Stop when there will be no free Space of no more files ...
		while (leaveFree < deviceFreeSpace
				&& songs.size() > songsSelected.size()) {
			// Get song number
			int number = r.nextInt(songs.size());

			if (!songsSelected.containsKey(number)) {
				// add song to selected ones ...
				songsSelected.put(number, songs.get(number));
			}
		}

		// Write files
		copyFilesToDevice(new ArrayList<ILocalAudioObject>(
				songsSelected.values()));
	}

	@Override
	public void connectDevice() {
		IFolderSelectorDialog dialog = this.dialogFactory
				.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("SELECT_DEVICE"));
		File folder = dialog.selectFolder(getOsManager().getUserHome());
		if (folder != null) {
			getFrame().showProgressBar(true, null);
			retrieveDevice(folder);
		}
	}

	@Override
	public void copyFilesToDevice(final List<ILocalAudioObject> collection) {
		copyFilesToDevice(collection, null);
	}

	@Override
	public void copyFilesToDevice(
			final List<ILocalAudioObject> collectionToCopy,
			final IProcessListener<List<ILocalAudioObject>> listener) {
		List<ILocalAudioObject> collection = collectionToCopy;
		this.filesCopiedToDevice = 0;
		if (collection.isEmpty()) {
			return;
		}

		long freeSpace = getFreeSpaceOnDevice();
		// Check if there is enough free space on device
		if (getSize(collection) > freeSpace) {
			// If user accepts truncate the list and continue
			if (askUserToTruncateCopy()) {
				List<ILocalAudioObject> toCopy = new ArrayList<ILocalAudioObject>();
				long size = 0;
				for (ILocalAudioObject file : collection) {
					long fileSize = this.fileManager.getFileSize(file);
					if ((fileSize + size) < freeSpace) {
						toCopy.add(file);
						size += fileSize;
					} else {
						// Size exceeds free space
						break;
					}

				}
				collection = toCopy;
			} else {
				// Otherwise quit
				return;
			}
		}

		runProcessToCopyFiles(collection, listener);
	}

	/**
	 * @param collection
	 * @return
	 */
	private long getSize(final Collection<ILocalAudioObject> collection) {
		long size = 0;
		for (ILocalAudioObject file : collection) {
			size = size + this.fileManager.getFileSize(file);
		}
		return size;
	}

	/**
	 * @return
	 */
	private boolean askUserToTruncateCopy() {
		IConfirmationDialog dialog = this.dialogFactory
				.newDialog(IConfirmationDialog.class);
		dialog.setMessage(String.format("%1s%n%2s",
				I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"),
				I18nUtils.getString("TRUNCATE_COPYLIST")));
		dialog.showDialog();
		return dialog.userAccepted();
	}

	/**
	 * @return free space on device
	 */
	private long getFreeSpaceOnDevice() {
		// Leave a security margin of 5 MB
		return this.deviceRepository.getRepositoryFolders().get(0)
				.getFreeSpace()
				- FileUtils.MEGABYTE * 5;
	}

	/**
	 * @param collection
	 * @param listener
	 */
	private void runProcessToCopyFiles(
			final List<ILocalAudioObject> collection,
			final IProcessListener<List<ILocalAudioObject>> listener) {
		final ILocalAudioObjectTransferProcess process = (ILocalAudioObjectTransferProcess) this.processFactory
				.getProcessByName("transferToDeviceProcess");
		process.setFilesToTransfer(collection);
		process.setDestination(net.sourceforge.atunes.utils.FileUtils
				.getPath(this.deviceRepository.getRepositoryFolders().get(0)));
		process.addProcessListener(new CopyFilesToDeviceProcessListener());
		// Add this listener second so when this is called filesCopiedToDevice
		// has been updated
		if (listener != null) {
			process.addProcessListener(listener);
		}
		process.execute();
	}

	/**
	 * Called when a device monitor detects a device connected.
	 * 
	 * @param location
	 *            the location
	 */
	@Override
	public void deviceConnected(final String location) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					IConfirmationDialog dialog = DeviceHandler.this.dialogFactory
							.newDialog(IConfirmationDialog.class);
					dialog.setMessage(I18nUtils
							.getString("DEVICE_CONNECT_CONFIRMATION"));
					dialog.showDialog();
					if (dialog.userAccepted()) {
						getFrame().showProgressBar(true, null);
						DeviceHandler.this.retrieveDevice(new File(location));
					} else {
						DeviceHandler.this.deviceMonitor.stopMonitor();
					}
				}
			});
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		}
	}

	/**
	 * Called when device is disconnected
	 * 
	 * @param location
	 *            the location
	 */
	@Override
	public void deviceDisconnected(final String location) {
		// Persist device metadata
		this.beanFactory.getBean(IStateService.class).persistDeviceCache(
				this.deviceId, this.deviceRepository);

		this.deviceRepository = null;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				notifyFinishRefresh(null);
				DeviceHandler.this.beanFactory.getBean(
						ConnectDeviceAction.class).setEnabled(true);
				DeviceHandler.this.beanFactory.getBean(
						RefreshDeviceAction.class).setEnabled(false);
				DeviceHandler.this.beanFactory.getBean(
						DisconnectDeviceAction.class).setEnabled(false);
				DeviceHandler.this.beanFactory.getBean(
						SynchronizeDeviceWithPlayListAction.class).setEnabled(
						false);
				DeviceHandler.this.beanFactory.getBean(
						CopyPlayListToDeviceAction.class).setEnabled(false);
				getFrame().showDeviceInfo(false);
				DeviceHandler.this.dialogFactory
						.newDialog(IMessageDialog.class)
						.showMessage(
								I18nUtils
										.getString("DEVICE_DISCONNECTION_DETECTED"));
			}
		});

		Logger.info("Device disconnected");
	}

	/**
	 * Called when closing application
	 */
	@Override
	public void applicationFinish() {
		if (isDeviceConnected()) {
			// Persist device metadata
			this.beanFactory.getBean(IStateService.class).persistDeviceCache(
					this.deviceId, this.deviceRepository);
		}
	}

	/**
	 * Returns a string with total and free space for a dir.
	 * 
	 * @return the device data
	 */
	private String getDeviceData() {
		if (this.deviceRepository != null) {
			int songs = this.deviceRepository.countFiles();
			File dir = this.deviceRepository.getRepositoryFolders().get(0);
			return StringUtils.getString(Integer.toString(songs), " ",
					I18nUtils.getString("SONGS"), "  (",
					I18nUtils.getString("FREE_SPACE"), ": ",
					StringUtils.fromByteToMegaOrGiga(dir.getFreeSpace()), ")");
		}
		return null;
	}

	@Override
	public Collection<ILocalAudioObject> getAudioFilesList() {
		if (this.deviceRepository != null) {
			return this.deviceRepository.getFiles();
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isDeviceConnected() {
		return this.deviceRepository != null;
	}

	@Override
	public boolean isDevicePath(final String path) {
		if (isDeviceConnected() && path.contains(this.devicePath.toString())) {
			return true;
		}
		return false;
	}

	@Override
	public void notifyCurrentPath(final String path) {
		// Nothing to do
	}

	/**
	 * Notify device reload.
	 * 
	 * @param loader
	 *            the loader
	 */
	private void notifyDeviceReload(final IRepositoryLoader loader) {
		getFrame().hideProgressBar();
		this.navigationHandler.refreshView(this.deviceNavigationView);

		// Enable action to copy to device
		this.beanFactory.getBean(CopyPlayListToDeviceAction.class).setEnabled(
				true);
		this.beanFactory.getBean(SynchronizeDeviceWithPlayListAction.class)
				.setEnabled(true);

		this.beanFactory.getBean(ConnectDeviceAction.class).setEnabled(false);
		this.beanFactory.getBean(RefreshDeviceAction.class).setEnabled(true);
		this.beanFactory.getBean(DisconnectDeviceAction.class).setEnabled(true);

		// Update status bar info
		if (loader != null) {
			getFrame().setStatusBarDeviceLabelText(getDeviceData());
			getFrame().showDeviceInfo(true);
		} else {
			getFrame().showDeviceInfo(false);
		}

		if (loader != null) {
			// Switch to device view in navigator
			this.navigationHandler.setNavigationView(this.deviceNavigationView
					.getClass().getName());
		}
	}

	@Override
	public void notifyFileLoaded() {
		// Nothing to do
	}

	@Override
	public void notifyFilesInRepository(final int files) {
		// Nothing to do
	}

	@Override
	public void notifyFinishRead(final IRepositoryLoader loader) {
		Logger.debug("Device read done");
		notifyDeviceReload(loader);
	}

	@Override
	public void notifyFinishRefresh(final IRepositoryLoader loader) {
		Logger.debug("Device refresh done");
		notifyDeviceReload(loader);
	}

	@Override
	public void notifyRemainingTime(final long time) {
		// Nothing to do
	}

	@Override
	public void notifyReadProgress() {
		// Nothing to do
	}

	@Override
	public void refreshDevice() {
		if (isDeviceConnected()) {
			// Block disconnection until refresh ends
			this.beanFactory.getBean(DisconnectDeviceAction.class).setEnabled(
					false);

			GuiUtils.callInEventDispatchThread(new Runnable() {
				@Override
				public void run() {
					getFrame().showProgressBar(true, null);
				}
			});
			Logger.info("Refreshing device");
			IRepository oldDeviceRepository = this.deviceRepository;
			this.deviceRepository = new Repository(
					oldDeviceRepository.getRepositoryFolders(),
					this.stateRepository);
			this.currentLoader = this.beanFactory
					.getBean(RepositoryRefreshLoader.class);
			((RepositoryRefreshLoader) this.currentLoader)
					.setDisableRepositoryActions(false);
			this.currentLoader.setRepositoryLoaderListener(this);
			this.currentLoader.start(new RepositoryTransaction(
					this.deviceRepository, null), oldDeviceRepository
					.getRepositoryFolders(), oldDeviceRepository,
					this.deviceRepository);
		}
	}

	/**
	 * Retrieve device.
	 * 
	 * @param path
	 *            the path
	 */
	private void retrieveDevice(final File path) {
		Logger.info(StringUtils.getString("Reading device mounted on ", path));

		// Block connect device
		this.beanFactory.getBean(ConnectDeviceAction.class).setEnabled(false);

		setDevicePath(path);

		// Get a unique device identifier
		this.deviceId = getDeviceIndentificationInfo();

		// Device has been connected before, try to get data from cache
		if (this.deviceId != null) {
			this.deviceRepository = this.beanFactory.getBean(
					IStateService.class).retrieveDeviceCache(this.deviceId);
			if (this.deviceRepository != null) {
				refreshDevice();
				return;
			}
		}

		List<File> folders = new ArrayList<File>();
		folders.add(path);
		this.deviceRepository = new Repository(folders, this.stateRepository);
		this.currentLoader = this.beanFactory
				.getBean(RepositoryReadLoader.class);
		this.currentLoader.setRepositoryLoaderListener(this);
		this.currentLoader.start(new RepositoryTransaction(
				this.deviceRepository, null), folders, null,
				this.deviceRepository);
	}

	/**
	 * Set the device path.
	 * 
	 * @param path
	 *            Device path (absolute)
	 */
	protected final void setDevicePath(final File path) {
		this.devicePath = path;
	}

	@Override
	public ILocalAudioObject getFileIfLoaded(final String fileName) {
		return this.deviceRepository == null ? null : this.deviceRepository
				.getFile(fileName);
	}

	/**
	 * Tries to read device identifier of device if it has been connect to
	 * aTunes before. If not, writes a new device identifier and returns it
	 * 
	 * @return Unique identifier for connected device
	 */
	private String getDeviceIndentificationInfo() {
		String id = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			File f = new File(
					StringUtils.getString(this.devicePath, getOsManager()
							.getFileSeparator(), Constants.DEVICE_ID_FILE));
			if (f.exists()) {
				br = new BufferedReader(new FileReader(f));
				id = br.readLine();
				br.close();
			} else {
				// New device identifier is current system time
				id = Long.toString(System.currentTimeMillis());
				bw = new BufferedWriter(new FileWriter(f));
				bw.append(id);
				bw.newLine();
				bw.flush();
				bw.close();
			}
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(br);
			ClosingUtils.close(bw);
		}
		return id;
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		if (!isDeviceConnected()) {
			return;
		}

		boolean refresh = false;
		for (ILocalAudioObject af : audioFiles) {
			if (isDevicePath(this.fileManager.getPath(af))) {
				refresh = true;
				break;
			}
		}
		if (refresh) {
			refreshDevice();
		}
	}

	@Override
	public int getFilesCopiedToDevice() {
		return this.filesCopiedToDevice;
	}

	@Override
	public List<ILocalAudioObject> getElementsNotPresentInDevice(
			final List<ILocalAudioObject> list) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		if (list != null && !list.isEmpty()) {
			for (ILocalAudioObject af : list) {
				if (isElementNotPresentInDevice(af)) {
					result.add(af);
				}
			}
		}
		return result;
	}

	/**
	 * Returns if local audio object is in device
	 * 
	 * @param af
	 * @return
	 */
	private boolean isElementNotPresentInDevice(final ILocalAudioObject af) {
		String artist = af.getAlbumArtist(this.unknownObjectChecker) != null
				&& !af.getAlbumArtist(this.unknownObjectChecker).trim()
						.equals("") ? af
				.getAlbumArtist(this.unknownObjectChecker) : af
				.getArtist(this.unknownObjectChecker);
		String album = af.getAlbum(this.unknownObjectChecker);
		String title = af.getTitle();

		// If artist is not present in device then
		IArtist a = getArtist(artist);
		if (a == null) {
			return true;
		} else {
			// Artist is present, then find song or album and song
			if (this.stateDevice.isAllowRepeatedSongsInDevice()) {
				if (a.getAlbum(album) == null) {
					return true;
				} else {
					// Compare title of every file and add if title is not in
					// list
					IAlbum alb = a.getAlbum(album);
					List<ILocalAudioObject> deviceFiles = alb.getAudioObjects();
					HashSet<String> titles = new HashSet<String>();
					for (IAudioObject deviceFile : deviceFiles) {
						titles.add(deviceFile.getTitle());
					}
					if (!titles.contains(title)) {
						return true;
					}
				}
			} else {
				// Compare title of every file of artist and add if title is not
				// in list
				List<ILocalAudioObject> deviceFiles = a.getAudioObjects();
				HashSet<String> titles = new HashSet<String>();
				for (IAudioObject deviceFile : deviceFiles) {
					titles.add(deviceFile.getTitle());
				}
				if (!titles.contains(title)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isElementNotPresentInList(final ILocalAudioObject af) {
		String artist = af.getAlbumArtist(this.unknownObjectChecker) != null
				&& !af.getAlbumArtist(this.unknownObjectChecker).trim()
						.equals("") ? af
				.getAlbumArtist(this.unknownObjectChecker) : af
				.getArtist(this.unknownObjectChecker);
		String album = af.getAlbum(this.unknownObjectChecker);
		String title = af.getTitle();

		// Remove objects present in device
		IArtist a = getArtist(artist);
		if (a != null) {
			if (this.stateDevice.isAllowRepeatedSongsInDevice()) {
				if (a.getAlbum(album) != null) {
					IAlbum alb = a.getAlbum(album);
					List<ILocalAudioObject> deviceFiles = alb.getAudioObjects();
					HashMap<String, IAudioObject> titles = new HashMap<String, IAudioObject>();
					for (IAudioObject deviceFile : deviceFiles) {
						titles.put(deviceFile.getTitle(), deviceFile);
					}
					if (titles.containsKey(title)) {
						return false;
					}
				}
			} else {
				List<ILocalAudioObject> deviceFiles = a.getAudioObjects();
				HashMap<String, IAudioObject> titles = new HashMap<String, IAudioObject>();
				for (IAudioObject deviceFile : deviceFiles) {
					titles.put(deviceFile.getTitle(), deviceFile);
				}
				if (titles.containsKey(title)) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public List<ILocalAudioObject> getElementsNotPresentInList(
			final List<ILocalAudioObject> list) {
		// Start with all device
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>(
				getAudioFilesList());

		for (ILocalAudioObject af : list) {
			if (!isElementNotPresentInList(af)) {
				result.remove(af);
			}
		}
		return result;
	}

	@Override
	public String getDeviceLocation() {
		return this.deviceRepository != null ? net.sourceforge.atunes.utils.FileUtils
				.getPath(this.deviceRepository.getRepositoryFolders().get(0))
				: null;
	}

	@Override
	public void notifyCurrentAlbum(final String artist, final String album) {
	}

	@Override
	public IArtist getArtist(final String name) {
		if (this.deviceRepository != null) {
			return this.deviceRepository.getArtist(name);
		}
		return null;
	}

	@Override
	public Map<String, ?> getDataForView(final ViewMode viewMode) {
		return viewMode.getDataForView(this.deviceRepository);
	}
}
