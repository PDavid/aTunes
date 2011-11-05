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
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.repository.processes.ExportFilesProcess;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IExportOptionsDialog;
import net.sourceforge.atunes.model.IExportOptionsDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.LocalAudioObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls export process
 * 
 * @author fleax
 * 
 */
public class ExportAction extends CustomAbstractAction {

    private static final long serialVersionUID = -6661702915765846089L;

    private IExportOptionsDialogFactory exportOptionsDialogFactory;
    
    private IConfirmationDialogFactory confirmationDialogFactory;
    
    private IErrorDialogFactory errorDialogFactory;
    
    private INavigationHandler navigationHandler;
    
    private IPlayListHandler playListHandler;
    
    private IFrame frame;
    
    private IOSManager osManager;
    
    public ExportAction() {
        super(StringUtils.getString(I18nUtils.getString("EXPORT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("EXPORT"), "..."));
    }

    @Override
    protected void executeAction() {
        IExportOptionsDialog dialog = exportOptionsDialogFactory.getDialog();
        dialog.startDialog();

        // If user didn't cancel dialog...
        if (!dialog.isCancel()) {
            String path = dialog.getExportLocation();
            boolean exportNavigator = dialog.isExportNavigatorSelection();
            if (path != null && !path.trim().equals("")) {
                boolean pathExists = new File(path).exists();
                boolean userWantsToCreate = false;

                // If path does not exist, then ask user to create it
                if (!pathExists && confirmationDialogFactory.getDialog().showDialog(I18nUtils.getString("DIR_NO_EXISTS"))) {
                	pathExists = new File(path).mkdir();
                	userWantsToCreate = true;
                }

                // If path exists then start export
                if (pathExists) {
                    List<ILocalAudioObject> songs;

                    // If user wants to export navigator ask current navigation view to return selected objects
                    LocalAudioObjectFilter filter = new LocalAudioObjectFilter();
                    if (exportNavigator) {
                        songs = filter.getLocalAudioObjects(navigationHandler.getCurrentView().getSelectedAudioObjects());
                    } else {
                        // Get only LocalAudioObject objects of current play list
                        songs = filter.getLocalAudioObjects(playListHandler.getSelectedAudioObjects());
                    }

                    ExportFilesProcess process = new ExportFilesProcess(songs, path, getState(), frame, osManager);
                    ExportProcessListener listener = new ExportProcessListener();
                    listener.setErrorDialogFactory(errorDialogFactory);
                    listener.setFrame(frame);
                    process.addProcessListener(listener);
                    process.execute();
                } else if (userWantsToCreate) {
                    // If path does not exist and app is not able to create it show an error dialog
                	errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("COULD_NOT_CREATE_DIR"));
                }
            } else {
            	errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("INCORRECT_EXPORT_PATH"));
            }
        }
    }
    
    private static class ExportProcessListener implements IProcessListener {

    	private IErrorDialogFactory errorDialogFactory;
    	
    	private IFrame frame;

    	/**
    	 * @param errorDialogFactory
    	 */
    	public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
			this.errorDialogFactory = errorDialogFactory;
		}
    	
    	/**
    	 * @param frame
    	 */
    	public void setFrame(IFrame frame) {
			this.frame = frame;
		}
    	
        @Override
        public void processCanceled() { 
        	// Nothing to do
        }

        @Override
        public void processFinished(final boolean ok) {
            SwingUtilities.invokeLater(new Runnable() {
            	@Override
            	public void run() {
                    if (!ok) {
                    	errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
                    }
            	}
            });
        }
    }
    
    /**
     * @param exportOptionsDialogFactory
     */
    public void setExportOptionsDialogFactory(IExportOptionsDialogFactory exportOptionsDialogFactory) {
    	this.exportOptionsDialogFactory = exportOptionsDialogFactory;
	}
    
    /**
     * @param confirmationDialogFactory
     */
    public void setConfirmationDialogFactory(IConfirmationDialogFactory confirmationDialogFactory) {
		this.confirmationDialogFactory = confirmationDialogFactory;
	}
    
    /**
     * @param errorDialogFactory
     */
    public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
	}
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

}
