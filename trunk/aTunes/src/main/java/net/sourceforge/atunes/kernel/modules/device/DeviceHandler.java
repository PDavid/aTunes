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
package net.sourceforge.atunes.kernel.modules.device;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ConnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.DisconnectDeviceAction;
import net.sourceforge.atunes.kernel.actions.RefreshDeviceAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFileSelectionDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public final class DeviceHandler extends AbstractHandler implements IDeviceHandler {

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

    @Override
    protected void initHandler() {
    	caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
    	getBean(IRepositoryHandler.class).addAudioFilesRemovedListener(this);
    }

    @Override
    public void allHandlersInitialized() {
    	if (isDefaultDeviceLocationConfigured(getState())) { 
    		// Start device monitor if necessary
    		DeviceMonitor.startMonitor(getState(), getBean(ITaskService.class), this);
    	}
    }
    
    @Override
    public void applicationStateChanged(IState newState) {
    	if (caseSensitiveTrees != newState.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		caseSensitiveTrees = getState().isKeyAlwaysCaseSensitiveInRepositoryStructure();
    		refreshDevice();
    	}
    	
    	if (isDefaultDeviceLocationConfigured(newState) && !DeviceMonitor.isMonitorRunning()) {
    		DeviceMonitor.startMonitor(getState(), getBean(ITaskService.class), this);
    	} else if (!isDefaultDeviceLocationConfigured(newState) && DeviceMonitor.isMonitorRunning()) {
    		DeviceMonitor.stopMonitor();
    	}
    }
    
    /**
     * Returns if user configured a default device location
     * @param state
     * @return
     */
    private boolean isDefaultDeviceLocationConfigured(IState state) {
    	return state.getDefaultDeviceLocation() != null && !state.getDefaultDeviceLocation().isEmpty();
    }
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#fillWithRandomSongs(long)
	 */
    @Override
	public void fillWithRandomSongs(long leaveFreeLong) {
        long leaveFree = leaveFreeLong;

        // Get reference to Repository songs
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(getBean(IRepositoryHandler.class).getAudioFilesList());

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
            getBean(IErrorDialog.class).showErrorDialog(getFrame(), I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE"));
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#connectDevice()
	 */
    @Override
	public void connectDevice() {
    	IFileSelectionDialog dialog = getBean(IFileSelectionDialog.class);
    	dialog.setDirectoryOnly(true);
        dialog.setTitle(I18nUtils.getString("SELECT_DEVICE"));
        dialog.showDialog();
        if (!dialog.isCanceled()) {
            File dir = dialog.getSelectedDir();
            getFrame().showProgressBar(true, null);
            retrieveDevice(dir);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#copyFilesToDevice(java.util.Collection)
	 */
    @Override
	public void copyFilesToDevice(Collection<ILocalAudioObject> collection) {
        copyFilesToDevice(collection, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#copyFilesToDevice(java.util.Collection, net.sourceforge.atunes.model.IProcessListener)
	 */
    @Override
	public void copyFilesToDevice(Collection<ILocalAudioObject> collection, IProcessListener listener) {
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
        	boolean truncate = getBean(IConfirmationDialogFactory.class).getDialog().showDialog(
        			String.format("%1s%n%2s"
        					, I18nUtils.getString("NOT_ENOUGH_SPACE_ON_DEVICE")
        					, I18nUtils.getString("TRUNCATE_COPYLIST")
        			));
        	//If so truncate the list and continue
        	if (truncate) {
        		Collection<ILocalAudioObject> toCopy = new LinkedList<ILocalAudioObject>();
        		size = 0;
        		long fileSize = 0;
        		for (ILocalAudioObject file : collection) {
        			fileSize = file.getFile().length();
        			if ((fileSize + size) < deviceFreeSpace) {
        				toCopy.add(file);
        				size += fileSize;
        			} else {
        				break;
        			}

        		}
        		collection = toCopy;
        	}//Otherwise quit 
        	else {
        		return;
        	}
        }

        final TransferToDeviceProcess process = new TransferToDeviceProcess(collection, deviceRepository.getRepositoryFolders().get(0).getAbsolutePath(), getState(), getFrame(), getOsManager());
        process.addProcessListener(new IProcessListener() {

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
                        	getBean(IErrorDialog.class).showErrorDialog(getFrame(), I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
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
    public void deviceConnected(final String location) {
    	try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
			        if (getBean(IConfirmationDialogFactory.class).getDialog().showDialog(I18nUtils.getString("DEVICE_CONNECT_CONFIRMATION"))) {
			        	getFrame().showProgressBar(true, null);
			            DeviceHandler.this.retrieveDevice(new File(location));
			        } else {
			        	DeviceMonitor.stopMonitor();
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
    	getBean(IStateHandler.class).persistDeviceCache(deviceId, deviceRepository);

        deviceRepository = null;

    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    	        notifyFinishRefresh(null);
    	        getBean(ConnectDeviceAction.class).setEnabled(true);
    	        getBean(RefreshDeviceAction.class).setEnabled(false);
    	        getBean(DisconnectDeviceAction.class).setEnabled(false);
    	        Actions.getAction(SynchronizeDeviceWithPlayListAction.class).setEnabled(false);
    	        getBean(CopyPlayListToDeviceAction.class).setEnabled(false);
    	        getFrame().showDeviceInfo(false);
    	        getBean(IMessageDialog.class).showMessage(I18nUtils.getString("DEVICE_DISCONNECTION_DETECTED"), getFrame());		    
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
        	getBean(IStateHandler.class).persistDeviceCache(deviceId, deviceRepository);
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getAudioFilesList()
	 */
    @Override
	public Collection<ILocalAudioObject> getAudioFilesList() {
        if (deviceRepository != null) {
            return deviceRepository.getFiles();
        }
        return Collections.emptyList();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#isDeviceConnected()
	 */
    @Override
	public boolean isDeviceConnected() {
        return deviceRepository != null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#isDevicePath(java.lang.String)
	 */
    @Override
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
    	getFrame().hideProgressBar();
    	getBean(INavigationHandler.class).refreshView(DeviceNavigationView.class);

        // Enable action to copy to device
    	getBean(CopyPlayListToDeviceAction.class).setEnabled(true);
        Actions.getAction(SynchronizeDeviceWithPlayListAction.class).setEnabled(true);

        getBean(ConnectDeviceAction.class).setEnabled(false);
    	getBean(RefreshDeviceAction.class).setEnabled(true);
    	getBean(DisconnectDeviceAction.class).setEnabled(true);

    	// Update status bar info
    	if (loader != null) {
            getFrame().setStatusBarDeviceLabelText(getDeviceData());
            getFrame().showDeviceInfo(true);
    	} else {
    		getFrame().showDeviceInfo(false);
    	}

    	if (loader != null) {
    		// Switch to device view in navigator
    		getBean(INavigationHandler.class).setNavigationView(DeviceNavigationView.class.getName());
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
        Logger.debug("Device read done");
        notifyDeviceReload(loader);
    }

    @Override
    public void notifyFinishRefresh(RepositoryLoader loader) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#refreshDevice()
	 */
    @Override
	public void refreshDevice() {
    	getFrame().showProgressBar(true, null);
        Logger.info("Refreshing device");
        Repository oldDeviceRepository = deviceRepository;
        deviceRepository = new Repository(oldDeviceRepository.getRepositoryFolders(), null, getState());
        currentLoader = new RepositoryLoader(getState(), getBean(IRepositoryHandler.class), oldDeviceRepository.getRepositoryFolders(), oldDeviceRepository, deviceRepository, true);
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
            deviceRepository = Context.getBean(IStateHandler.class).retrieveDeviceCache(deviceId);
            if (deviceRepository != null) {
                refreshDevice();
                return;
            }
        }

        List<File> folders = new ArrayList<File>();
        folders.add(path);
        deviceRepository = new Repository(folders, null, getState());
        currentLoader = new RepositoryLoader(getState(), getBean(IRepositoryHandler.class), folders, null, deviceRepository, false);
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getFileIfLoaded(java.lang.String)
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getFilesCopiedToDevice()
	 */
    @Override
	public int getFilesCopiedToDevice() {
        return filesCopiedToDevice;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getElementsNotPresentInDevice(java.util.List)
	 */
    @Override
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getElementsNotPresentInList(java.util.List)
	 */
    @Override
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
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getDeviceLocation()
	 */
	@Override
	public String getDeviceLocation() {
		return deviceRepository != null ? deviceRepository.getRepositoryFolders().get(0).getAbsolutePath() : null;
	}

	@Override
	public void notifyCurrentAlbum(String artist, String album) {
	}
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getArtist(java.lang.String)
	 */
    @Override
	public Artist getArtist(String name) {
    	if (deviceRepository != null) {
    		return deviceRepository.getArtist(name);
    	}
    	return null;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.device.IDeviceHandler#getDataForView(net.sourceforge.atunes.model.ViewMode)
	 */
	@Override
	public Map<String, ?> getDataForView(ViewMode viewMode) {
		return viewMode.getDataForView(deviceRepository);
	}
}
