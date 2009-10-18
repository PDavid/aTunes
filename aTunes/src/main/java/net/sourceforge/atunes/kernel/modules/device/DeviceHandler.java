/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.DisconnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.RefreshDeviceAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.LoaderListener;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.DeviceSearchableObject;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class DeviceHandler extends Handler implements LoaderListener, DeviceConnectionListener, DeviceDisconnectionListener, AudioFilesRemovedListener {

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
    protected int filesCopiedToDevice;

    /**
     * Instantiates a new device handler.
     */
    private DeviceHandler() {
    }
    
    @Override
    protected void initHandler() {
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted() {
        // Start device monitor
        DeviceConnectionMonitor.startMonitor();
        DeviceConnectionMonitor.addListener(this);
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
        getLogger().debug(LogCategories.HANDLER, new String[] { Long.toString(leaveFree) });

        // Get reference to Repository songs
        List<AudioFile> songs = RepositoryHandler.getInstance().getAudioFilesList();

        // Songs selected
        Map<Integer, AudioFile> songsSelected = new HashMap<Integer, AudioFile>();

        // Initialize random generator
        Random r = new Random(new Date().getTime());

        // Get free space in device
        long deviceFreeSpace = deviceRepository.getFolders().get(0).getFreeSpace();
        if (leaveFree >= 0) {
            leaveFree = (leaveFree) * 1048576; // Transfrom to same unit
        } else {
            leaveFree = 0; // User typed nonsense value. So fill his device ...
        }

        // Not enough space avaible
        if (leaveFree > deviceFreeSpace) {
            getLogger().debug(LogCategories.HANDLER, I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
            VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
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
        copyFilesToDevice(new ArrayList<AudioFile>(songsSelected.values()));
    }

    /**
     * Connect device.
     */
    public void connectDevice() {
        FileSelectionDialog dialog = VisualHandler.getInstance().getFileSelectionDialog(true);
        dialog.setTitle(I18nUtils.getString("SELECT_DEVICE"));
        dialog.startDialog();
        if (!dialog.isCanceled()) {
            File dir = dialog.getSelectedDir();

            VisualHandler.getInstance().showProgressBar(true, null);
            this.retrieveDevice(dir);
        }
    }

    /**
     * Copy files to device
     * 
     * @param collection
     */
    public void copyFilesToDevice(Collection<AudioFile> collection) {
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
    public void copyFilesToDevice(Collection<AudioFile> collection, ProcessListener listener) {
        filesCopiedToDevice = 0;
        if (collection.isEmpty()) {
            return;
        }

        // Get size of files
        long size = 0;
        for (AudioFile file : collection) {
            size = size + file.getFile().length();
        }
        // Get free space in device
        long deviceFreeSpace = deviceRepository.getFolders().get(0).getFreeSpace();

        // Check if there is enough free space on device
        if (size > deviceFreeSpace) {
            VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
            return;
        }

        final TransferToDeviceProcess process = new TransferToDeviceProcess(collection, deviceRepository.getFolders().get(0).getAbsolutePath());
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
                            VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
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
        if (VisualHandler.getInstance().showConfirmationDialog(I18nUtils.getString("DEVICE_CONNECT_CONFIRMATION"), I18nUtils.getString("DEVICE_DETECTED")) == JOptionPane.OK_OPTION) {
            VisualHandler.getInstance().showProgressBar(true, null);
            this.retrieveDevice(new File(location));
        }
    }

    /**
     * Called when a device monitor detects a device disconnection.
     * 
     * @param location
     *            the location
     */
    @Override
    public void deviceDisconnected(String location) {
        VisualHandler.getInstance().showMessage(I18nUtils.getString("DEVICE_DISCONNECTION_DETECTED"));
        disconnectDevice();

        // Start device connection monitor
        DeviceConnectionMonitor.startMonitor();
    }

    /**
     * Disconnect device.
     */
    public void disconnectDevice() {
        // Persist device metadata
        ApplicationStateHandler.getInstance().persistDeviceCache(deviceId, deviceRepository);

        VisualHandler.getInstance().hideDeviceInfoOnStatusBar();

        List<Integer> songsToRemove = new ArrayList<Integer>();
        for (AudioObject ao : PlayListHandler.getInstance().getCurrentPlayList(true).getObjectsOfType(AudioFile.class)) {
            AudioFile audioFile = (AudioFile) ao;
            if (audioFile.getFile().getPath().startsWith(this.getDeviceRepository().getFolders().get(0).getPath())) {
                songsToRemove.add(PlayListHandler.getInstance().getCurrentPlayList(true).indexOf(audioFile));
            }
        }
        int[] indexes = new int[songsToRemove.size()];
        for (int i = 0; i < songsToRemove.size(); i++) {
            indexes[i] = songsToRemove.get(i);
        }

        if (indexes.length > 0) {
            //			PlayListTable table = VisualHandler.getInstance().getPlayListTable();
            //			((PlayListTableModel) table.getModel()).removeAudioObjects(indexes);
            VisualHandler.getInstance().getPlayListPanel().getPlayListTable().getSelectionModel().clearSelection();
            PlayListHandler.getInstance().removeAudioObjects(indexes);
        }

        deviceRepository = null;
        notifyFinishRefresh(null);
        getLogger().info(LogCategories.REPOSITORY, "Device disconnected");

        Actions.getAction(ConnectDeviceAction.class).setEnabled(true);
        Actions.getAction(RefreshDeviceAction.class).setEnabled(false);
        Actions.getAction(DisconnectDeviceAction.class).setEnabled(false);
        Actions.getAction(SynchronizeDeviceWithPlayListAction.class).setEnabled(false);

        // Unregister device to search in
        SearchHandler.getInstance().unregisterSearchableObject(DeviceSearchableObject.getInstance());
    }

    /**
     * Called when closing application
     */
    public void applicationFinish() {
        if (isDeviceConnected()) {
            // Persist device metadata
            ApplicationStateHandler.getInstance().persistDeviceCache(deviceId, deviceRepository);
        }
    }

    /**
     * Gets the device artist structure.
     * 
     * @return the device artist and album structure
     */
    public Map<String, Artist> getDeviceArtistStructure() {
        if (deviceRepository != null) {
            return deviceRepository.getStructure().getArtistStructure();
        }
        return new HashMap<String, Artist>();
    }

    /**
     * Gets the device album structure
     * 
     * @return
     */
    public Map<String, Album> getDeviceAlbumStructure() {
        if (deviceRepository != null) {
            Map<String, Album> albumsStructure = new HashMap<String, Album>();
            Collection<Artist> artistCollection = deviceRepository.getStructure().getArtistStructure().values();
            for (Artist artist : artistCollection) {
                for (Album album : artist.getAlbums().values()) {
                    albumsStructure.put(album.getNameAndArtist(), album);
                }
            }
            return albumsStructure;
        }
        return new HashMap<String, Album>();
    }

    /**
     * Gets the device's genre structure
     * 
     * @return
     */
    public Map<String, Genre> getDeviceGenreStructure() {
        if (deviceRepository != null) {
            return deviceRepository.getStructure().getGenreStructure();
        }
        return new HashMap<String, Genre>();
    }

    /**
     * Returns a string with total and free space for a dir.
     * 
     * @return the device data
     */
    private String getDeviceData() {
        if (deviceRepository != null) {
            int songs = deviceRepository.countFiles();
            File dir = deviceRepository.getFolders().get(0);
            return StringUtils.getString(Integer.toString(songs), " ", I18nUtils.getString("SONGS"), "  (", I18nUtils.getString("FREE_SPACE"), ": ", StringUtils
                    .fromByteToMegaOrGiga(dir.getFreeSpace()), ")");
        }
        return null;
    }

    /**
     * Gets the device folder structure.
     * 
     * @return the device folder structure
     */
    public Map<String, Folder> getDeviceFolderStructure() {
        if (deviceRepository != null) {
            return deviceRepository.getStructure().getFolderStructure();
        }
        return new HashMap<String, Folder>();
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
    public List<AudioFile> getAudioFilesList() {
        if (deviceRepository != null) {
            return deviceRepository.getAudioFilesList();
        }
        return new ArrayList<AudioFile>();
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
        if (ControllerProxy.getInstance().getNavigationController() != null) {
            VisualHandler.getInstance().hideProgressBar();
            ControllerProxy.getInstance().getNavigationController().notifyDeviceReload();

            Actions.getAction(ConnectDeviceAction.class).setEnabled(loader == null);
            Actions.getAction(RefreshDeviceAction.class).setEnabled(loader != null);
            Actions.getAction(DisconnectDeviceAction.class).setEnabled(loader != null);

            // Update status bar info
            if (loader != null) {
                VisualHandler.getInstance().showDeviceInfoOnStatusBar(getDeviceData());
            } else {
                VisualHandler.getInstance().hideDeviceInfoOnStatusBar();
            }

            if (loader != null) {
                // Switch to device view in navigator
                ControllerProxy.getInstance().getNavigationController().setNavigationView(DeviceNavigationView.class.getName());
            }
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
        getLogger().info(LogCategories.REPOSITORY, "Device read");
        notifyDeviceReload(loader);

        actionsAfterConnectDevice();
    }

    /**
     * Actions to execute after connect a device
     */
    private void actionsAfterConnectDevice() {
        // Start device disconnection monitor
        DeviceDisconnectionMonitor.startMonitor();
        DeviceDisconnectionMonitor.addListener(this);

        // Register device to search in
        SearchHandler.getInstance().registerSearchableObject(DeviceSearchableObject.getInstance());

        // Enable action to copy to device
        Actions.getAction(CopyPlayListToDeviceAction.class).setEnabled(true);
        Actions.getAction(SynchronizeDeviceWithPlayListAction.class).setEnabled(true);
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
        VisualHandler.getInstance().showProgressBar(true, null);
        getLogger().info(LogCategories.REPOSITORY, "Refreshing device");
        Repository oldDeviceRepository = deviceRepository;
        deviceRepository = new Repository(oldDeviceRepository.getFolders());
        currentLoader = new RepositoryLoader(oldDeviceRepository.getFolders(), oldDeviceRepository, deviceRepository, true);
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
        getLogger().info(LogCategories.REPOSITORY, StringUtils.getString("Reading device mounted on ", path));

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
        deviceRepository = new Repository(folders);
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
    public AudioFile getFileIfLoaded(String fileName) {
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
        try {
            File f = new File(StringUtils.getString(devicePath, SystemProperties.FILE_SEPARATOR, Constants.DEVICE_ID_FILE));
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                id = br.readLine();
                br.close();
            } else {
                // New device identifier is current system time
                id = Long.toString(System.currentTimeMillis());
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.append(id);
                bw.newLine();
                bw.flush();
                bw.close();
            }
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, e);
        }
        return id;
    }

    @Override
    public void audioFilesRemoved(List<AudioFile> audioFiles) {
        if (!isDeviceConnected()) {
            return;
        }

        boolean refresh = false;
        for (AudioFile af : audioFiles) {
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
    public List<AudioFile> getElementsNotPresentInDevice(List<AudioFile> list) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        if (list != null && !list.isEmpty()) {
            for (AudioFile af : list) {
                String artist = af.getAlbumArtist() != null && !af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist();
                String album = af.getAlbum();
                String title = af.getTitle();

                // If artist is not present in device then add
                if (getDeviceArtistStructure().get(artist) == null) {
                    result.add(af);
                } else {
                    // Artist is present, then find song or album and song
                    Artist a = getDeviceArtistStructure().get(artist);
                    if (ApplicationState.getInstance().isAllowRepeatedSongsInDevice()) {
                        if (a.getAlbum(album) == null) {
                            result.add(af);
                        } else {
                            // Compare title of every file and add if title is not in list
                            Album alb = a.getAlbum(album);
                            List<AudioFile> deviceFiles = alb.getAudioFiles();
                            HashSet<String> titles = new HashSet<String>();
                            for (AudioFile deviceFile : deviceFiles) {
                                titles.add(deviceFile.getTitle());
                            }
                            if (!titles.contains(title)) {
                                result.add(af);
                            }
                        }
                    } else {
                        // Compare title of every file of artist and add if title is not in list
                        List<AudioFile> deviceFiles = a.getAudioFiles();
                        HashSet<String> titles = new HashSet<String>();
                        for (AudioFile deviceFile : deviceFiles) {
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
    public List<AudioFile> getElementsNotPresentInList(List<AudioFile> list) {
        // Start with all device
        List<AudioFile> result = new ArrayList<AudioFile>(getAudioFilesList());

        for (AudioFile af : list) {
            String artist = af.getAlbumArtist() != null && !af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist();
            String album = af.getAlbum();
            String title = af.getTitle();

            // Remove objects present in device
            if (getDeviceArtistStructure().get(artist) != null) {
                Artist a = getDeviceArtistStructure().get(artist);
                if (ApplicationState.getInstance().isAllowRepeatedSongsInDevice()) {
                    if (a.getAlbum(album) != null) {
                        Album alb = a.getAlbum(album);
                        List<AudioFile> deviceFiles = alb.getAudioFiles();
                        HashMap<String, AudioFile> titles = new HashMap<String, AudioFile>();
                        for (AudioFile deviceFile : deviceFiles) {
                            titles.put(deviceFile.getTitle(), deviceFile);
                        }
                        if (titles.containsKey(title)) {
                            result.remove(titles.get(title));
                        }
                    }
                } else {
                    List<AudioFile> deviceFiles = a.getAudioFiles();
                    HashMap<String, AudioFile> titles = new HashMap<String, AudioFile>();
                    for (AudioFile deviceFile : deviceFiles) {
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
    public void applicationStateChanged(ApplicationState newState) {
        DeviceConnectionMonitor.startMonitor();
    }
}
