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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListLocalAudioObjectFilter;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMessageDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.LocalAudioObjectFilter;
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
	
	private static final long serialVersionUID = -1885495996370465881L;
	
	private IIndeterminateProgressDialogFactory indeterminateProgressDialogFactory;
	
	private IFrame frame;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	private IMessageDialogFactory messageDialogFactory;
	
	private IDeviceHandler deviceHandler;
	
	private IPlayListHandler playListHandler;
	
	private IRepositoryHandler repositoryHandler;
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}
	
	/**
	 * @param messageDialogFactory
	 */
	public void setMessageDialogFactory(IMessageDialogFactory messageDialogFactory) {
		this.messageDialogFactory = messageDialogFactory;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * @param indeterminateProgressDialogFactory
	 */
	public void setIndeterminateProgressDialogFactory(IIndeterminateProgressDialogFactory indeterminateProgressDialogFactory) {
		this.indeterminateProgressDialogFactory = indeterminateProgressDialogFactory;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    public SynchronizeDeviceWithPlayListAction() {
        super(I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
        setEnabled(false);
    }

    @Override
    protected void executeAction() {
        SwingWorker<Map<String, List<ILocalAudioObject>>, Void> worker = new SynchronizeDeviceWithPlayListSwingWorker();
        worker.execute();
        
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		dialog = indeterminateProgressDialogFactory.newDialog(frame, lookAndFeelManager);
        		dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
        		dialog.showDialog();
        	}
        });
    }
    
	private void showMessage(final int filesRemoved, final boolean added) {
	    // Show message
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	        	messageDialogFactory.getDialog().showMessage(
	                    StringUtils.getString(I18nUtils.getString("SYNCHRONIZATION_FINISHED"), " ", I18nUtils.getString("ADDED"), 
	                    		": ", added ? deviceHandler.getFilesCopiedToDevice() : 0, " ", 
	                    				I18nUtils.getString("REMOVED"), ": ", filesRemoved), frame);
	        }
	    });
	}
	
    private final class SynchronizeDeviceWithPlayListSwingWorker extends SwingWorker<Map<String, List<ILocalAudioObject>>, Void> {
    	
		private int filesRemoved = 0;

		@Override
		protected Map<String, List<ILocalAudioObject>> doInBackground() {
		    // Get play list elements
		    List<ILocalAudioObject> playListObjects;
		    LocalAudioObjectFilter filter = new LocalAudioObjectFilter();
		    if (SynchronizeDeviceWithPlayListAction.this.getState().isAllowRepeatedSongsInDevice()) {
		        // Repeated songs allowed, filter only if have same artist and album
		        playListObjects = filter.filterRepeatedObjectsWithAlbums(new PlayListLocalAudioObjectFilter().getObjects(playListHandler.getCurrentPlayList(true)));
		    } else {
		        // Repeated songs not allows, filter even if have different album
		        playListObjects = filter.filterRepeatedObjects(new PlayListLocalAudioObjectFilter().getObjects(playListHandler.getCurrentPlayList(true)));
		    }

		    // Get elements present in play list and not in device -> objects to be copied to device
		    List<ILocalAudioObject> objectsToCopyToDevice = deviceHandler.getElementsNotPresentInDevice(playListObjects);

		    // Get elements present in device and not in play list -> objects to be removed from device
		    List<ILocalAudioObject> objectsToRemoveFromDevice = deviceHandler.getElementsNotPresentInList(playListObjects);

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
		        repositoryHandler.startTransaction();
		        repositoryHandler.remove(filesToRemove);
		        repositoryHandler.endTransaction();
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
		        	deviceHandler.copyFilesToDevice(files.get("ADD"), new IProcessListener() {
						
						@Override
						public void processFinished(boolean ok) {
							showMessage(filesRemoved, true);
						}
						
						@Override
						public void processCanceled() {
						}
					});
		        } else {
		            showMessage(filesRemoved, false);
		        }
		    } catch (Exception e) {
		        Logger.error(e);
		    }
		}
	}
}
