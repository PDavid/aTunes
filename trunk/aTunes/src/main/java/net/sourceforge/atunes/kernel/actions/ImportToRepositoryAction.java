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

import net.sourceforge.atunes.gui.views.dialogs.SelectorDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMultiFolderSelectionDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action refreshes repository
 * 
 * @author fleax
 * 
 */
public class ImportToRepositoryAction extends CustomAbstractAction {

    private static final long serialVersionUID = -5708270585764283210L;

    private IRepositoryHandler repositoryHandler;
    
    private IFrame frame;
    
    private IDialogFactory dialogFactory;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Default constructor
     */
    public ImportToRepositoryAction() {
        super(StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
        setEnabled(false); // Initially disabled, will be enabled when repository is loaded
    }
    
    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    @Override
    protected void executeAction() {
        // Now show dialog to select folders
        IMultiFolderSelectionDialog dialog = (IMultiFolderSelectionDialog) dialogFactory.newDialog(IMultiFolderSelectionDialog.class);
        dialog.setTitle(I18nUtils.getString("IMPORT"));
        dialog.setText(I18nUtils.getString("SELECT_FOLDERS_TO_IMPORT"));
        dialog.setSelectedFolders(null);
        dialog.showDialog();
        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            // If user selected folders...
            if (!folders.isEmpty()) {
                String path;
                String[] foldersList = new String[repositoryHandler.getFoldersCount()];
                for (int i = 0; i < repositoryHandler.getFolders().size(); i++) {
                    foldersList[i] = repositoryHandler.getFolders().get(i).getAbsolutePath();
                }
                // If repository folders are more than one then user must select where to import songs
                if (foldersList.length > 1) {
                    SelectorDialog selector = new SelectorDialog(frame.getFrame(), I18nUtils.getString("SELECT_REPOSITORY_FOLDER_TO_IMPORT"),
                            foldersList, null, lookAndFeelManager.getCurrentLookAndFeel());
                    selector.setVisible(true);
                    path = selector.getSelection();
                    // If user closed dialog then select first entry
                    if (path == null) {
                        path = foldersList[0];
                    }
                } else {
                    path = foldersList[0];
                }
                repositoryHandler.importFolders(folders, path);
            }
        }
    }
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
}
