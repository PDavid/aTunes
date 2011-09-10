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

package net.sourceforge.atunes.kernel.modules.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.FileSelectionDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.DisconnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.RefreshDeviceAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.LoaderListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class DeviceHandler extends AbstractHandler implements LoaderListener, AudioFilesRemovedListener {

    private static DeviceHandler instance = new DeviceHandler();

    private Repository deviceRepository;
    private RepositoryLoader currentLoader;
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

    /**
     * Instantiates a new device handler.
     */
    private DeviceHandler() {    	
    }

    @Override
    protected void initHandler() {
    	caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }
    
    @Override
    public void allHandlersInitialized() {
        // Start device monitor
        DeviceMonitor.startMonitor(getState());
    }

    /**
     * Gets the single instance of DeviceHandler.
     * 
     * @return single instance of DeviceHandler
     */
    public static DeviceHandler getInstance() {
        return instance;
    }

    /**
     * Fills the device with songs until the specified memory is left.
     * 
     * @param leaveFreeLong
     *            Memory to leave free
     */
    public void fillWithRandomSongs(long leaveFreeLong) {
        long leaveFree = leaveFreeLong;

        // Get reference to Repository songs
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(RepositoryHandler.getInstance().getAudioFilesList());

        // Songs selected
        Map<Integer, ILocalAudioObject> songsSelected = new HashMap<Integer, ILocalAudioObject>();

        // Initialize random generator
        Random r = new Random(new Date().getTime());

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
            GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
            return;
        }

        // Get n songs
        // Stop when there will be no free Space of no more files ...
        for (int i = 0; (leaveFree < deviceFreeSpace) && (songs.size() > songsSelected.size()); i++) {
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

    /**
     * Connect device.
     */
    public void connectDevice() {
        FileSelectionDialog dialog = GuiHandler.getInstance().getFileSelectionDialog(true);
        dialog.setTitle(I18nUtils.getString("SELECT_DEVICE"));
        dialog.startDialog();
        if (!dialog.isCanceled()) {
            File dir = dialog.getSelectedDir();

            GuiHandler.getInstance().showProgressBar(true, null);
            this.retrieveDevice(dir);
        }
    }

    /**
     * Copy files to device
     * 
     * @param collection
     */
    public void copyFilesToDevice(Collection<ILocalAudioObject> collection) {
        copyFilesToDevice(collection, null);
    }

    /**
     * Copy files to mp3 device.
     * 
     * @param collection
     *            Files to be written to a mp3 device
     * @param listener
     *            A listener to be notified
     */
    public void copyFilesToDevice(Collection<ILocalAudioObject> collection, ProcessListener listener) {
        filesCopiedToDevice = 0;
        if (collection.isEmpty()) {
            return;
        }

        // Get size of files
        long size = 0;
        for (ILocalAudioObject file : collection) {
            size = size + file.getFile().length();
        }
        // Get free space in device
        long deviceFreeSpace = deviceRepository.getRepositoryFolders().get(0).getFreeSpace();

        // Check if there is enough free space on device
        if (size > deviceFreeSpace) {
            GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
            return;
        }

        final TransferToDeviceProcess process = new TransferToDeviceProcess(collection, deviceRepository.getRepositoryFolders().get(0).getAbsolutePath(), getState());
        process.addProcessListener(new ProcessListener() {
            @Override
            public void processCanceled() {
                // Nothing to do
            }

            @Override
            public void processFinished(final boolean ok) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshDevice();
                        filesCopiedToDevice = process.getFilesTransferred().size();
                        if (!ok) {
                            GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
                        }
                    }
                });
            }
        });
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
    public void deviceConnected(String location) {
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("DEVICE_CONNECT_CONFIRMATION"), I18nUtils.getString("DEVICE_DETECTED")) == JOptionPane.OK_OPTION) {
            GuiHandler.getInstance().showProgressBar(true, null);
            this.retrieveDevice(new File(location));
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
        ApplicationStateHandler.getInstance().persistDeviceCache(deviceId, deviceRepository);

        deviceRepository = null;

    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    	        notifyFinishRefresh(null);
    	        Actions.getAction(ConnectDeviceAction.class).setEnabled(true);
    	        Actions.getAction(RefreshDeviceAction.class).setEnabled(false);
    	        Actions.getAction(DisconnectDeviceAction.class).setEnabled(false);
    	        Actions.getAction(SynchronizeDeviceWithPlayListAction.class).setEnabled(false);
    	        Actions.getAction(CopyPlayListToDeviceAction.class).setEnabled(false);
    		}
    	});

        Logger.info("Device disconnected");
    }

    /**
     * Called when closing application
     */
    public void applicationFinish() {
    	DeviceMonitor.stopMonitor();
        if (isDeviceConnected()) {
            // Persist device metadata
            ApplicationStateHandler.getInstance().persistDeviceCache(deviceId, deviceRepository);
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

    /**
     * Gets the device repository.
     * 
     * @return the device repository
     */
    public Repository getDeviceRepository() {
        return deviceRepository;
    }

    /**
     * Gets the device songs.
     * 
     * @return the device songs
     */
    public Collection<ILocalAudioObject> getAudioFilesList() {
        if (deviceRepository != null) {
            return deviceRepository.getFiles();
        }
        return Collections.emptyList();
    }

    /**
     * Checks if is device connected.
     * 
     * @return true, if is device connected
     */
    public boolean isDeviceConnected() {
        return deviceRepository != null;
    }

    /**
     * Checks if given file is in the device path.
     * 
     * @param path
     *            Absolute path of the file
     * 
     * @return true if file is in device, false otherwise
     */
    public boolean isDevicePath(String path) {
        if (path.contains(devicePath.toString())) {
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
    private void notifyDeviceReload(RepositoryLoader loader) {
    	GuiHandler.getInstance().hideProgressBar();
    	NavigationHandler.getInstance().notifyDeviceReload();

    	Actions.getAction(ConnectDeviceAction.class).setEnabled(loader == null);
    	Actions.getAction(RefreshDeviceAction.class).setEnabled(loader != null);
    	Actions.getAction(DisconnectDeviceAction.class).setEnabled(loader != null);

    	// Update status bar info
    	if (loader != null) {
    		GuiHandler.getInstance().showDeviceInfoOnStatusBar(getDeviceData());
    	} else {
    		GuiHandler.getInstance().hideDeviceInfoOnStatusBar();
    	}

    	if (loader != null) {
    		// Switch to device view in navigator
    		NavigationHandler.getInstance().setNavigationView(DeviceNavigationView.class.getName());
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
    public void notifyFinishRead(RepositoryLoader loader) {
        Logger.info("Device read");
        notifyDeviceReload(loader);
    }

    @Override
    public void notifyFinishRefresh(RepositoryLoader loader) {
        notifyDeviceReload(loader);

        // Enable action to copy to device
        Actions.getAction(CopyPlayListToDeviceAction.class).setEnabled(true);
        Actions.getAction(SynchronizeDeviceWithPlayListAction.class).setEnabled(true);
    }

    @Override
    public void notifyRemainingTime(long time) {
        // Nothing to do
    }

    @Override
    public void notifyReadProgress() {
        // Nothing to do
    }

    /**
     * Refresh device.
     */
    public void refreshDevice() {
        GuiHandler.getInstance().showProgressBar(true, null);
        Logger.info("Refreshing device");
        Repository oldDeviceRepository = deviceRepository;
        deviceRepository = new Repository(oldDeviceRepository.getRepositoryFolders(), null, getState());
        currentLoader = new RepositoryLoader(oldDeviceRepository.getRepositoryFolders(), oldDeviceRepository, deviceRepository, true);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
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
            deviceRepository = ApplicationStateHandler.getInstance().retrieveDeviceCache(deviceId);
            if (deviceRepository != null) {
                refreshDevice();
                return;
            }
        }

        List<File> folders = new ArrayList<File>();
        folders.add(path);
        deviceRepository = new Repository(folders, null, getState());
        currentLoader = new RepositoryLoader(folders, null, deviceRepository, false);
        currentLoader.addRepositoryLoaderListener(this);
        currentLoader.start();
    }

    /**
     * Set the device path.
     * 
     * @param path
     *            Device path (absolute)
     */
    private void setDevicePath(File path) {
        devicePath = path;
    }

    /**
     * Gets the file if is in device
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file if loaded
     */
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
            File f = new File(StringUtils.getString(devicePath, OsManager.getFileSeparator(), Constants.DEVICE_ID_FILE));
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

    /**
     * @return the filesCopiedToDevice
     */
    public int getFilesCopiedToDevice() {
        return filesCopiedToDevice;
    }

    /**
     * Returns elements present in list and not present in device
     * 
     * @param list
     * @return
     */
    public List<ILocalAudioObject> getElementsNotPresentInDevice(List<ILocalAudioObject> list) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        if (list != null && !list.isEmpty()) {
            for (ILocalAudioObject af : list) {
                String artist = af.getAlbumArtist() != null && !af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist();
                String album = af.getAlbum();
                String title = af.getTitle();

                // If artist is not present in device then add
                Artist a = getArtist(artist);
                if (a == null) {
                    result.add(af);
                } else {
                    // Artist is present, then find song or album and song
                    if (getState().isAllowRepeatedSongsInDevice()) {
                        if (a.getAlbum(album) == null) {
                            result.add(af);
                        } else {
                            // Compare title of every file and add if title is not in list
                            Album alb = a.getAlbum(album);
                            List<ILocalAudioObject> deviceFiles = alb.getAudioObjects();
                            HashSet<String> titles = new HashSet<String>();
                            for (IAudioObject deviceFile : deviceFiles) {
                                titles.add(deviceFile.getTitle());
                            }
                            if (!titles.contains(title)) {
                                result.add(af);
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
                            result.add(af);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns elements present in device and not present in list
     * 
     * @param list
     * @return
     */
    public List<ILocalAudioObject> getElementsNotPresentInList(List<ILocalAudioObject> list) {
        // Start with all device
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>(getAudioFilesList());

        for (ILocalAudioObject af : list) {
            String artist = af.getAlbumArtist() != null && !af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist();
            String album = af.getAlbum();
            String title = af.getTitle();

            // Remove objects present in device
            Artist a = getArtist(artist);
            if (a != null) {
                if (getState().isAllowRepeatedSongsInDevice()) {
                    if (a.getAlbum(album) != null) {
                        Album alb = a.getAlbum(album);
                        List<ILocalAudioObject> deviceFiles = alb.getAudioObjects();
                        HashMap<String, IAudioObject> titles = new HashMap<String, IAudioObject>();
                        for (IAudioObject deviceFile : deviceFiles) {
                            titles.put(deviceFile.getTitle(), deviceFile);
                        }
                        if (titles.containsKey(title)) {
                            result.remove(titles.get(title));
                        }
                    }
                } else {
                    List<ILocalAudioObject> deviceFiles = a.getAudioObjects();
                    HashMap<String, IAudioObject> titles = new HashMap<String, IAudioObject>();
                    for (IAudioObject deviceFile : deviceFiles) {
                        titles.put(deviceFile.getTitle(), deviceFile);
                    }
                    if (titles.containsKey(title)) {
                        result.remove(titles.get(title));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void applicationStateChanged(IState newState) {
    	if (caseSensitiveTrees != newState.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
    		refreshDevice();
    	}
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

	/**
	 * Returns device location
	 * @return
	 */
	public String getDeviceLocation() {
		return getDeviceRepository() != null ? getDeviceRepository().getRepositoryFolders().get(0).getAbsolutePath() : null;
	}

	@Override
	public void notifyCurrentAlbum(String artist, String album) {
	}
	
    /**
     * Returns artist with given name
     * @param name
     * @return
     */
    public Artist getArtist(String name) {
    	if (deviceRepository != null) {
    		return deviceRepository.getArtist(name);
    	}
    	return null;
    }

	/**
	 * Returns data to show in a tree
	 * @param viewMode
	 * @return
	 */
	public Map<String, ?> getDataForView(ViewMode viewMode) {
		return viewMode.getDataForView(deviceRepository);
	}

}
