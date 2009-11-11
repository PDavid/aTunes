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
package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Synchronizes play list and device: device is updated with play list content
 * 
 * @author fleax
 */
public class SynchronizeDeviceWithPlayListAction extends Action {

    private static final long serialVersionUID = -1885495996370465881L;

    protected Logger logger = new Logger();

    public SynchronizeDeviceWithPlayListAction() {
        super(I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        SwingWorker<Map<String, List<AudioFile>>, Void> worker = new SwingWorker<Map<String, List<AudioFile>>, Void>() {

            protected int filesRemoved = 0;

            protected void showMessage(final boolean added) {
                // Show message
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GuiHandler.getInstance().showMessage(
                                StringUtils.getString(I18nUtils.getString("SYNCHRONIZATION_FINISHED"), " ", I18nUtils.getString("ADDED"), ": ", added ? DeviceHandler
                                        .getInstance().getFilesCopiedToDevice() : 0, " ", I18nUtils.getString("REMOVED"), ": ", filesRemoved));
                    }
                });
            }

            protected ProcessListener listener = new ProcessListener() {
                @Override
                public void processCanceled() {
                    // Nothing to do
                }

                @Override
                public void processFinished(boolean ok) {
                    showMessage(true);
                }
            };

            @Override
            protected Map<String, List<AudioFile>> doInBackground() {
                // Get play list elements
                List<AudioFile> playListObjects;
                if (ApplicationState.getInstance().isAllowRepeatedSongsInDevice()) {
                    // Repeated songs allowed, filter only if have same artist and album
                    playListObjects = AudioFile.filterRepeatedSongsAndAlbums(AudioFile.getAudioFiles(PlayListHandler.getInstance().getCurrentPlayList(true).getObjectsOfType(
                            AudioFile.class)));
                } else {
                    // Repeated songs not allows, filter even if have different album
                    playListObjects = AudioFile.filterRepeatedSongs(AudioFile.getAudioFiles(PlayListHandler.getInstance().getCurrentPlayList(true)
                            .getObjectsOfType(AudioFile.class)));
                }

                // Get elements present in play list and not in device -> objects to be copied to device
                List<AudioFile> objectsToCopyToDevice = DeviceHandler.getInstance().getElementsNotPresentInDevice(playListObjects);

                // Get elements present in device and not in play list -> objects to be removed from device
                List<AudioFile> objectsToRemoveFromDevice = DeviceHandler.getInstance().getElementsNotPresentInList(playListObjects);

                Map<String, List<AudioFile>> result = new HashMap<String, List<AudioFile>>();
                result.put("ADD", objectsToCopyToDevice);
                result.put("REMOVE", objectsToRemoveFromDevice);
                filesRemoved = objectsToRemoveFromDevice.size();
                return result;
            }

            @Override
            protected void done() {
                try {
                    Map<String, List<AudioFile>> files = get();

                    GuiHandler.getInstance().hideIndeterminateProgressDialog();

                    // Remove elements from device
                    final List<AudioFile> filesToRemove = files.get("REMOVE");
                    RepositoryHandler.getInstance().remove(filesToRemove);
                    SynchronizeDeviceWithPlayListAction.this.setEnabled(false);
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            for (AudioFile audioFile : filesToRemove) {
                                File file = audioFile.getFile();
                                if (file != null) {
                                    file.delete();
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
                        DeviceHandler.getInstance().copyFilesToDevice(files.get("ADD"), listener);
                    } else {
                        showMessage(false);
                    }
                } catch (Exception e) {
                    logger.error(LogCategories.ACTION, e);
                }
            }
        };
        worker.execute();
        GuiHandler.getInstance().showIndeterminateProgressDialog(I18nUtils.getString("PLEASE_WAIT"));
    }

}
