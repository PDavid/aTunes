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
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.ExportOptionsDialog;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.repository.ExportFilesProcess;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls export process
 * 
 * @author fleax
 * 
 */
public class ExportAction extends Action {

    private static final long serialVersionUID = -6661702915765846089L;

    ExportAction() {
        super(StringUtils.getString(I18nUtils.getString("EXPORT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("EXPORT"), "..."));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ExportOptionsDialog dialog = VisualHandler.getInstance().getExportDialog();
        dialog.startDialog();

        // If user didn't cancel dialog...
        if (!dialog.isCancel()) {
            String path = dialog.getExportLocation();
            boolean exportNavigator = dialog.exportNavigatorSelection();
            if (path != null && !path.trim().equals("")) {
                boolean pathExists = new File(path).exists();
                boolean userWantsToCreate = false;

                // If path does not exist, then ask user to create it
                if (!pathExists) {
                    if (VisualHandler.getInstance().showConfirmationDialog(I18nUtils.getString("DIR_NO_EXISTS"), I18nUtils.getString("INFO")) == JOptionPane.OK_OPTION) {
                        pathExists = new File(path).mkdir();
                        userWantsToCreate = true;
                    }
                }

                // If path exists then start export
                if (pathExists) {
                    List<AudioFile> songs;

                    // If user wants to export navigator ask current navigation view to return selected objects
                    if (exportNavigator) {
                        songs = AudioFile.getAudioFiles(NavigationHandler.getInstance().getCurrentView().getSelectedAudioObjects());
                    } else {
                        // Get only AudioFile objects of current play list
                        songs = AudioFile.getAudioFiles(PlayListHandler.getInstance().getSelectedAudioObjects());
                    }

                    ExportFilesProcess process = new ExportFilesProcess(songs, path);
                    process.addProcessListener(new ProcessListener() {
                        @Override
                        public void processCanceled() { /* Nothing to do */
                        }

                        @Override
                        public void processFinished(final boolean ok) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ok) {
                                        VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
                                    }
                                }
                            });
                        }
                    });
                    process.execute();
                } else if (userWantsToCreate) {
                    // If path does not exist and app is not able to create it show an error dialog
                    VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("COULD_NOT_CREATE_DIR"));
                }
            } else {
                VisualHandler.getInstance().showErrorDialog(I18nUtils.getString("INCORRECT_EXPORT_PATH"));
            }
        }
    }
}
