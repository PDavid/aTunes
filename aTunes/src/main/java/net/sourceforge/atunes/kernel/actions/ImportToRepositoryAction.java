/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.SelectorDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action refreshes repository
 * 
 * @author fleax
 * 
 */
public class ImportToRepositoryAction extends AbstractAction {

    private static final long serialVersionUID = -5708270585764283210L;

    ImportToRepositoryAction() {
        super(StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // First check if repository is selected. If not, display a message
        if (RepositoryHandler.getInstance().repositoryIsNull()) {
            GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("SELECT_REPOSITORY_BEFORE_IMPORT"));
            return;
        }

        // Now show dialog to select folders
        MultiFolderSelectionDialog dialog = GuiHandler.getInstance().getMultiFolderSelectionDialog();
        dialog.setTitle(I18nUtils.getString("IMPORT"));
        dialog.setText(I18nUtils.getString("SELECT_FOLDERS_TO_IMPORT"));
        dialog.startDialog(null);
        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            // If user selected folders...
            if (!folders.isEmpty()) {
                String path;
                String[] foldersList = new String[RepositoryHandler.getInstance().getFoldersCount()];
                for (int i = 0; i < RepositoryHandler.getInstance().getFolders().size(); i++) {
                    foldersList[i] = RepositoryHandler.getInstance().getFolders().get(i).getAbsolutePath();
                }
                // If repository folders are more than one then user must select where to import songs
                if (foldersList.length > 1) {
                    SelectorDialog selector = new SelectorDialog(GuiHandler.getInstance().getFrame().getFrame(), I18nUtils.getString("SELECT_REPOSITORY_FOLDER_TO_IMPORT"),
                            foldersList, null);
                    selector.setVisible(true);
                    path = selector.getSelection();
                    // If user closed dialog then select first entry
                    if (path == null) {
                        path = foldersList[0];
                    }
                } else {
                    path = foldersList[0];
                }
                RepositoryHandler.getInstance().importFolders(folders, path);
            }
        }
    }

}
