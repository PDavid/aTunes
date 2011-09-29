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
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.commons.io.FilenameUtils;

public class RenameAudioFileInNavigationTableAction extends CustomAbstractAction {

    private static final long serialVersionUID = 5607758675193509752L;

    public RenameAudioFileInNavigationTableAction() {
        super(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<IAudioObject> audioFiles = getBean(INavigationHandler.class).getFilesSelectedInNavigator();
        if (audioFiles.size() == 1 && audioFiles.get(0) instanceof ILocalAudioObject) {
            IInputDialog dialog = getBean(IInputDialog.class);
            dialog.setTitle(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
            dialog.showDialog(FilenameUtils.getBaseName(((ILocalAudioObject)audioFiles.get(0)).getFile().getAbsolutePath()));
            String name = dialog.getResult();
            if (name != null && !name.isEmpty()) {
                getBean(IRepositoryHandler.class).rename(((ILocalAudioObject)audioFiles.get(0)), name);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return selection.size() == 1;
    }

}
