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

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IInputDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.commons.io.FilenameUtils;

public class RenameAudioFileInNavigationTableAction extends CustomAbstractAction {

    private static final long serialVersionUID = 5607758675193509752L;

    private INavigationHandler navigationHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private IInputDialogFactory inputDialogFactory;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    /**
     * @param inputDialogFactory
     */
    public void setInputDialogFactory(IInputDialogFactory inputDialogFactory) {
		this.inputDialogFactory = inputDialogFactory;
	}
    
    public RenameAudioFileInNavigationTableAction() {
        super(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
    }

    @Override
    protected void executeAction() {
        List<IAudioObject> audioFiles = navigationHandler.getFilesSelectedInNavigator();
        if (audioFiles.size() == 1 && audioFiles.get(0) instanceof ILocalAudioObject) {
        	ILocalAudioObject ao = (ILocalAudioObject) audioFiles.get(0);
            IInputDialog dialog = inputDialogFactory.getDialog();
            dialog.setTitle(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
            dialog.showDialog(FilenameUtils.getBaseName(ao.getFile().getAbsolutePath()));
            String name = dialog.getResult();
            if (name != null && !name.isEmpty()) {
            	repositoryHandler.rename(ao, name);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return selection.size() == 1;
    }
}
