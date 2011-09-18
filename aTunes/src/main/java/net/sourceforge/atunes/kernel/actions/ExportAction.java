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
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.processes.ExportFilesProcess;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IExportOptionsDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls export process
 * 
 * @author fleax
 * 
 */
public class ExportAction extends CustomAbstractAction {

    private static class ExportProcessListener implements ProcessListener {
        private static final class ShowErrorDialogRunnable implements Runnable {
            private final boolean ok;

            private ShowErrorDialogRunnable(boolean ok) {
                this.ok = ok;
            }

            @Override
            public void run() {
                if (!ok) {
                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
                }
            }
        }

        @Override
        public void processCanceled() { /* Nothing to do */
        }

        @Override
        public void processFinished(final boolean ok) {
            SwingUtilities.invokeLater(new ShowErrorDialogRunnable(ok));
        }
    }

    private static final long serialVersionUID = -6661702915765846089L;

    ExportAction() {
        super(StringUtils.getString(I18nUtils.getString("EXPORT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("EXPORT"), "..."));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        IExportOptionsDialog dialog = Context.getBean(IExportOptionsDialog.class);
        dialog.startDialog();

        // If user didn't cancel dialog...
        if (!dialog.isCancel()) {
            String path = dialog.getExportLocation();
            boolean exportNavigator = dialog.isExportNavigatorSelection();
            if (path != null && !path.trim().equals("")) {
                boolean pathExists = new File(path).exists();
                boolean userWantsToCreate = false;

                // If path does not exist, then ask user to create it
                if (!pathExists && Context.getBean(IConfirmationDialog.class).showDialog(I18nUtils.getString("DIR_NO_EXISTS"))) {
                	pathExists = new File(path).mkdir();
                	userWantsToCreate = true;
                }

                // If path exists then start export
                if (pathExists) {
                    List<ILocalAudioObject> songs;

                    // If user wants to export navigator ask current navigation view to return selected objects
                    if (exportNavigator) {
                        songs = AudioFile.getAudioFiles(NavigationHandler.getInstance().getCurrentView().getSelectedAudioObjects());
                    } else {
                        // Get only LocalAudioObject objects of current play list
                        songs = AudioFile.getAudioFiles(PlayListHandler.getInstance().getSelectedAudioObjects());
                    }

                    ExportFilesProcess process = new ExportFilesProcess(songs, path, getState(), Context.getBean(IFrame.class), Context.getBean(IOSManager.class));
                    process.addProcessListener(new ExportProcessListener());
                    process.execute();
                } else if (userWantsToCreate) {
                    // If path does not exist and app is not able to create it show an error dialog
                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("COULD_NOT_CREATE_DIR"));
                }
            } else {
                GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("INCORRECT_EXPORT_PATH"));
            }
        }
    }
}
