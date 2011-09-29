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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListLocalAudioObjectFilter;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Synchronizes play list and device: device is updated with play list content
 * 
 * @author fleax
 */
public class SynchronizeDeviceWithPlayListAction extends CustomAbstractAction {

	private IIndeterminateProgressDialog dialog;
	
    private final class SynchronizeDeviceWithPlayListSwingWorker extends
			SwingWorker<Map<String, List<ILocalAudioObject>>, Void> {
		private int filesRemoved = 0;
		private IProcessListener listener = new IProcessListener() {
		    @Override
		    public void processCanceled() {
		        // Nothing to do
		    }

		    @Override
		    public void processFinished(boolean ok) {
		        showMessage(true);
		    }
		};

		protected void showMessage(final boolean added) {
		    // Show message
		    SwingUtilities.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		        	getBean(IMessageDialog.class).showMessage(
		                    StringUtils.getString(I18nUtils.getString("SYNCHRONIZATION_FINISHED"), " ", I18nUtils.getString("ADDED"), ": ", added ? getBean(IDeviceHandler.class)
		                            .getFilesCopiedToDevice() : 0, " ", I18nUtils.getString("REMOVED"), ": ", filesRemoved), getBean(IFrame.class));
		        }
		    });
		}

		@Override
		protected Map<String, List<ILocalAudioObject>> doInBackground() {
		    // Get play list elements
		    List<ILocalAudioObject> playListObjects;
		    if (SynchronizeDeviceWithPlayListAction.this.getState().isAllowRepeatedSongsInDevice()) {
		        // Repeated songs allowed, filter only if have same artist and album
		        playListObjects = AudioFile.filterRepeatedSongsAndAlbums(new PlayListLocalAudioObjectFilter().getObjects(getBean(IPlayListHandler.class).getCurrentPlayList(true)));
		    } else {
		        // Repeated songs not allows, filter even if have different album
		        playListObjects = AudioFile.filterRepeatedSongs(new PlayListLocalAudioObjectFilter().getObjects(getBean(IPlayListHandler.class).getCurrentPlayList(true)));
		    }

		    // Get elements present in play list and not in device -> objects to be copied to device
		    List<ILocalAudioObject> objectsToCopyToDevice = getBean(IDeviceHandler.class).getElementsNotPresentInDevice(playListObjects);

		    // Get elements present in device and not in play list -> objects to be removed from device
		    List<ILocalAudioObject> objectsToRemoveFromDevice = getBean(IDeviceHandler.class).getElementsNotPresentInList(playListObjects);

		    Map<String, List<ILocalAudioObject>> result = new HashMap<String, List<ILocalAudioObject>>();
		    result.put("ADD", objectsToCopyToDevice);
		    result.put("REMOVE", objectsToRemoveFromDevice);
		    filesRemoved = objectsToRemoveFromDevice.size();
		    return result;
		}

		@Override
		protected void done() {
		    try {
		        Map<String, List<ILocalAudioObject>> files = get();

		        dialog.hideDialog();

		        // Remove elements from device
		        final List<ILocalAudioObject> filesToRemove = files.get("REMOVE");
		        getBean(IRepositoryHandler.class).startTransaction();
		        getBean(IRepositoryHandler.class).remove(filesToRemove);
		        getBean(IRepositoryHandler.class).endTransaction();
		        SynchronizeDeviceWithPlayListAction.this.setEnabled(false);
		        new SwingWorker<Void, Void>() {
		            @Override
		            protected Void doInBackground() {
		                for (ILocalAudioObject audioFile : filesToRemove) {
		                    File file = audioFile.getFile();
		                    if (file != null) {
		                        if (!file.delete()) {
		                        	Logger.error(StringUtils.getString(file, " not deleted"));
		                        }
		                    }
		                }
		                return null;
		            }

		            @Override
		            protected void done() {
		                SynchronizeDeviceWithPlayListAction.this.setEnabled(true);
		            }
		        }.execute();

		        // Copy elements to device if necessary, otherwise show message and finish
		        if (!files.get("ADD").isEmpty()) {
		            // The process will show message when finish
		        	getBean(IDeviceHandler.class).copyFilesToDevice(files.get("ADD"), listener);
		        } else {
		            showMessage(false);
		        }
		    } catch (Exception e) {
		        Logger.error(e);
		    }
		}
	}

	private static final long serialVersionUID = -1885495996370465881L;

    public SynchronizeDeviceWithPlayListAction() {
        super(I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        SwingWorker<Map<String, List<ILocalAudioObject>>, Void> worker = new SynchronizeDeviceWithPlayListSwingWorker();
        worker.execute();
        
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		dialog = getBean(IIndeterminateProgressDialogFactory.class).newDialog(getBean(IFrame.class), getBean(ILookAndFeelManager.class));
        		dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
        		dialog.showDialog();
        	}
        });
    }
}
