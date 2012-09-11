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
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStateRepository;
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
public final class DeviceHandler extends AbstractHandler implements IDeviceHandler {

	private final class CopyFilesToDeviceProcessListener implements IProcessListener<List<File>> {

		@Override
		public void processCanceled() {
			// Nothing to do
		}

		@Override
		public void processFinished(final boolean ok, final List<File> result) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					refreshDevice();
					filesCopiedToDevice = result.size();
					if (!ok) {
						dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
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

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @param deviceNavigationView
	 */
	public void setDeviceNavigationView(INavigationView deviceNavigationView) {
		this.deviceNavigationView = deviceNavigationView;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param deviceMonitor
	 */
	public void setDeviceMonitor(DeviceMonitor deviceMonitor) {
		this.deviceMonitor = deviceMonitor;
	}

	@Override
	protected void initHandler() {
		caseSensitiveTrees = stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure();
		repositoryHandler.addAudioFilesRemovedListener(this);
	}

	@Override
	public void deferredInitialization() {
		if (isDefaultDeviceLocationConfigured(stateDevice)) { 
			// Start device monitor if necessary
			deviceMonitor.startMonitor();
		}
	}

	@Override
	public void applicationStateChanged() {
		if (caseSensitiveTrees != stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			caseSensitiveTrees = stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure();
			refreshDevice();
		}

		if (isDefaultDeviceLocationConfigured(stateDevice) && !deviceMonitor.isMonitorRunning()) {
			deviceMonitor.startMonitor();
		} else if (!isDefaultDeviceLocationConfigured(stateDevice) && deviceMonitor.isMonitorRunning()) {
			deviceMonitor.stopMonitor();
		}
	}

	/**
	 * Returns if user configured a default device location
	 * @param state
	 * @return
	 */
	private boolean isDefaultDeviceLocationConfigured(IStateDevice state) {
		return state.getDefaultDeviceLocation() != null && !state.getDefaultDeviceLocation().isEmpty();
	}

	@Override
	public void fillWithRandomSongs(long leaveFreeLong) {
		long leaveFree = leaveFreeLong;

		// Get reference to Repository songs
		List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(repositoryHandler.getAudioFilesList());

		// Songs selected
		Map<Integer, ILocalAudioObject> songsSelected = new HashMap<Integer, ILocalAudioObject>();

		// Initialize random generator
		Random r = new Random(System.currentTimeMillis());

		// Get free space in device
		long deviceFreeSpace = deviceRepository.getRepositoryFolders().get(0).getFreeSpace();
		if (leaveFree >= 0) {
			leaveFree = (leaveFree) * 1048576; // Transfrom to same unit
		} else {
			leaveFree = 0; // User typed nonsense value. So fill his device ...
		}

		// Not enough space avaible
		if (leaveFree > deviceFreeSpace) {
			Logger.debug(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
			dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
			return;
		}

		// Get n songs
		// Stop when there will be no free Space of no more files ...
		while (leaveFree < deviceFreeSpace && songs.size() > songsSelected.size()) {
			// Get song number
			int number = r.nextInt(songs.size());

			if (!songsSelected.containsKey(number)) {
				//add song  to selected ones ...
				songsSelected.put(number, songs.get(number));
			}
		}

		// Write files
		copyFilesToDevice(new ArrayList<ILocalAudioObject>(songsSelected.values()));
	}

	@Override
	public void connectDevice() {
		IFolderSelectorDialog dialog = dialogFactory.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("SELECT_DEVICE"));
		File folder = dialog.selectFolder(getOsManager().getUserHome());
		if (folder != null) {
			getFrame().showProgressBar(true, null);
			retrieveDevice(folder);
		}
	}

	@Override
	public void copyFilesToDevice(Collection<ILocalAudioObject> collection) {
		copyFilesToDevice(collection, null);
	}

	@Override
	public void copyFilesToDevice(Collection<ILocalAudioObject> collectionToCopy, IProcessListener<List<File>> listener) {
		Collection<ILocalAudioObject> collection = collectionToCopy;
		filesCopiedToDevice = 0;
		if (collection.isEmpty()) {
			return;
		}

		long freeSpace = getFreeSpaceOnDevice();
		// Check if there is enough free space on device
		if (getSize(collection) > freeSpace) {
			//If user accepts truncate the list and continue
			if (askUserToTruncateCopy()) {
				Collection<ILocalAudioObject> toCopy = new ArrayList<ILocalAudioObject>();
				long size = 0;
				for (ILocalAudioObject file : collection) {
					long fileSize = file.getFile().length();
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
	private long getSize(Collection<ILocalAudioObject> collection) {
		long size = 0;
		for (ILocalAudioObject file : collection) {
			size = size + file.getFile().length();
		}
		return size;
	}

	/**
	 * @return
	 */
	private boolean askUserToTruncateCopy() {
		IConfirmationDialog dialog = dialogFactory.newDialog(IConfirmationDialog.class);
		dialog.setMessage(String.format("%1s%n%2s"
				, I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE")
				, I18nUtils.getString("TRUNCATE_COPYLIST")));
		dialog.showDialog();
		return dialog.userAccepted();
	}

	/**
	 * @return free space on device
	 */
	private long getFreeSpaceOnDevice() {
		// Leave a security margin of 5 MB
		return deviceRepository.getRepositoryFolders().get(0).getFreeSpace() - FileUtils.MEGABYTE * 5;
	}

	/**
	 * @param collection
	 * @param listener
	 */
	private void runProcessToCopyFiles(Collection<ILocalAudioObject> collection, IProcessListener<List<File>> listener) {
		final ILocalAudioObjectTransferProcess process = (ILocalAudioObjectTransferProcess) processFactory.getProcessByName("transferToDeviceProcess");
		process.setFilesToTransfer(collection);
		process.setDestination(deviceRepository.getRepositoryFolders().get(0).getAbsolutePath());
		process.addProcessListener(new CopyFilesToDeviceProcessListener());
		// Add this listener second so when this is called filesCopiedToDevice has been updated
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
					IConfirmationDialog dialog = dialogFactory.newDialog(IConfirmationDialog.class);
					dialog.setMessage(I18nUtils.getString("DEVICE_CONNECT_CONFIRMATION"));
					dialog.showDialog();
					if (dialog.userAccepted()) {
						getFrame().showProgressBar(true, null);
						DeviceHandler.this.retrieveDevice(new File(location));
					} else {
						deviceMonitor.stopMonitor();
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
	public void deviceDisconnected(String location) {
		// Persist device metadata
		beanFactory.getBean(IStateHandler.class).persistDeviceCache(deviceId, deviceRepository);

		deviceRepository = null;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				notifyFinishRefresh(null);
				beanFactory.getBean(ConnectDeviceAction.class).setEnabled(true);
				beanFactory.getBean(RefreshDeviceAction.class).setEnabled(false);
				beanFactory.getBean(DisconnectDeviceAction.class).setEnabled(false);
				beanFactory.getBean(SynchronizeDeviceWithPlayListAction.class).setEnabled(false);
				beanFactory.getBean(CopyPlayListToDeviceAction.class).setEnabled(false);
				getFrame().showDeviceInfo(false);
				dialogFactory.newDialog(IMessageDialog.class).showMessage(I18nUtils.getString("DEVICE_DISCONNECTION_DETECTED"));		    
			}
		});

		Logger.info("Device disconnected");
	}

	/**
	 * Called when closing application
	 */
	public void applicationFinish() {
		if (isDeviceConnected()) {
			// Persist device metadata
			beanFactory.getBean(IStateHandler.class).persistDeviceCache(deviceId, deviceRepository);
		}
	}

	/**
	 * Returns a string with total and free space for a dir.
	 * 
	 * @return the device data
	 */
	private String getDeviceData() {
		if (deviceRepository != null) {
			int songs = deviceRepository.countFiles();
			File dir = deviceRepository.getRepositoryFolders().get(0);
			return StringUtils.getString(Integer.toString(songs), " ", I18nUtils.getString("SONGS"), "  (", I18nUtils.getString("FREE_SPACE"), ": ", StringUtils
					.fromByteToMegaOrGiga(dir.getFreeSpace()), ")");
		}
		return null;
	}

	@Override
	public Collection<ILocalAudioObject> getAudioFilesList() {
		if (deviceRepository != null) {
			return deviceRepository.getFiles();
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isDeviceConnected() {
		return deviceRepository != null;
	}

	@Override
	public boolean isDevicePath(String path) {
		if (isDeviceConnected() && path.contains(devicePath.toString())) {
			return true;
		}
		return false;
	}

	@Override
	public void notifyCurrentPath(String path) {
		// Nothing to do
	}

	/**
	 * Notify device reload.
	 * 
	 * @param loader
	 *            the loader
	 */
	private void notifyDeviceReload(IRepositoryLoader loader) {
		getFrame().hideProgressBar();
		navigationHandler.refreshView(deviceNavigationView);

		// Enable action to copy to device
		beanFactory.getBean(CopyPlayListToDeviceAction.class).setEnabled(true);
		beanFactory.getBean(SynchronizeDeviceWithPlayListAction.class).setEnabled(true);

		beanFactory.getBean(ConnectDeviceAction.class).setEnabled(false);
		beanFactory.getBean(RefreshDeviceAction.class).setEnabled(true);
		beanFactory.getBean(DisconnectDeviceAction.class).setEnabled(true);

		// Update status bar info
		if (loader != null) {
			getFrame().setStatusBarDeviceLabelText(getDeviceData());
			getFrame().showDeviceInfo(true);
		} else {
			getFrame().showDeviceInfo(false);
		}

		if (loader != null) {
			// Switch to device view in navigator
			navigationHandler.setNavigationView(deviceNavigationView.getClass().getName());
		}
	}

	@Override
	public void notifyFileLoaded() {
		// Nothing to do
	}

	@Override
	public void notifyFilesInRepository(int files) {
		// Nothing to do
	}

	@Override
	public void notifyFinishRead(IRepositoryLoader loader) {
		Logger.debug("Device read done");
		notifyDeviceReload(loader);
	}

	@Override
	public void notifyFinishRefresh(IRepositoryLoader loader) {
		Logger.debug("Device refresh done");
		notifyDeviceReload(loader);
	}

	@Override
	public void notifyRemainingTime(long time) {
		// Nothing to do
	}

	@Override
	public void notifyReadProgress() {
		// Nothing to do
	}

	@Override
	public void refreshDevice() {
		if (isDeviceConnected()) {
			getFrame().showProgressBar(true, null);
			Logger.info("Refreshing device");
			IRepository oldDeviceRepository = deviceRepository;
			deviceRepository = new Repository(oldDeviceRepository.getRepositoryFolders(), stateRepository);
			currentLoader = beanFactory.getBean(RepositoryRefreshLoader.class);
			currentLoader.setRepositoryLoaderListener(this);
			currentLoader.start(new RepositoryTransaction(deviceRepository, null), oldDeviceRepository.getRepositoryFolders(), oldDeviceRepository, deviceRepository);
		}
	}

	/**
	 * Retrieve device.
	 * 
	 * @param path
	 *            the path
	 */
	private void retrieveDevice(File path) {
		Logger.info(StringUtils.getString("Reading device mounted on ", path));

		setDevicePath(path);

		// Get a unique device identifier
		deviceId = getDeviceIndentificationInfo();

		// Device has been connected before, try to get data from cache
		if (deviceId != null) {
			deviceRepository = beanFactory.getBean(IStateHandler.class).retrieveDeviceCache(deviceId);
			if (deviceRepository != null) {
				refreshDevice();
				return;
			}
		}

		List<File> folders = new ArrayList<File>();
		folders.add(path);
		deviceRepository = new Repository(folders, stateRepository);
		currentLoader = beanFactory.getBean(RepositoryReadLoader.class);
		currentLoader.setRepositoryLoaderListener(this);
		currentLoader.start(new RepositoryTransaction(deviceRepository, null), folders, null, deviceRepository);
	}

	/**
	 * Set the device path.
	 * 
	 * @param path
	 *            Device path (absolute)
	 */
	protected final void setDevicePath(File path) {
		devicePath = path;
	}

	@Override
	public ILocalAudioObject getFileIfLoaded(String fileName) {
		return deviceRepository == null ? null : deviceRepository.getFile(fileName);
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
			File f = new File(StringUtils.getString(devicePath, getOsManager().getFileSeparator(), Constants.DEVICE_ID_FILE));
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
	public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
		if (!isDeviceConnected()) {
			return;
		}

		boolean refresh = false;
		for (ILocalAudioObject af : audioFiles) {
			if (isDevicePath(af.getFile().getAbsolutePath())) {
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
		return filesCopiedToDevice;
	}

	@Override
	public List<ILocalAudioObject> getElementsNotPresentInDevice(List<ILocalAudioObject> list) {
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
	 * @param af
	 * @return
	 */
	private boolean isElementNotPresentInDevice(ILocalAudioObject af) {
		String artist = af.getAlbumArtist() != null && !af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist();
		String album = af.getAlbum();
		String title = af.getTitle();

		// If artist is not present in device then
		IArtist a = getArtist(artist);
		if (a == null) {
			return true;
		} else {
			// Artist is present, then find song or album and song
			if (stateDevice.isAllowRepeatedSongsInDevice()) {
				if (a.getAlbum(album) == null) {
					return true;
				} else {
					// Compare title of every file and add if title is not in list
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
				// Compare title of every file of artist and add if title is not in list
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

	@Override
	public List<ILocalAudioObject> getElementsNotPresentInList(List<ILocalAudioObject> list) {
		// Start with all device
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>(getAudioFilesList());

		for (ILocalAudioObject af : list) {
			if (!isElementNotPresentInList(af)) {
				result.remove(af);
			}
		}
		return result;
	}

	private boolean isElementNotPresentInList(ILocalAudioObject af) {
		String artist = af.getAlbumArtist() != null && !af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist();
		String album = af.getAlbum();
		String title = af.getTitle();

		// Remove objects present in device
		IArtist a = getArtist(artist);
		if (a != null) {
			if (stateDevice.isAllowRepeatedSongsInDevice()) {
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
	public String getDeviceLocation() {
		return deviceRepository != null ? deviceRepository.getRepositoryFolders().get(0).getAbsolutePath() : null;
	}

	@Override
	public void notifyCurrentAlbum(String artist, String album) {
	}

	@Override
	public IArtist getArtist(String name) {
		if (deviceRepository != null) {
			return deviceRepository.getArtist(name);
		}
		return null;
	}

	@Override
	public Map<String, ?> getDataForView(ViewMode viewMode) {
		return viewMode.getDataForView(deviceRepository);
	}
}
