/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.util.List;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.commons.io.FilenameUtils;

public class RenameAudioFileInNavigationTableAction extends AbstractAction {

    private static final long serialVersionUID = 5607758675193509752L;

    public RenameAudioFileInNavigationTableAction() {
        super(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"), Images.getImage(Images.FILE_NAME));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<AudioFile> audioFiles = ControllerProxy.getInstance().getNavigationController().getFilesSelectedInNavigator();
        if (audioFiles.size() == 1) {
            String name = GuiHandler.getInstance().showInputDialog(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"),
                    FilenameUtils.getBaseName(audioFiles.get(0).getFile().getAbsolutePath()), Images.getImage(Images.FILE_NAME).getImage());
            if (name != null && !name.isEmpty()) {
                RepositoryHandler.getInstance().rename(audioFiles.get(0), name);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return selection.size() == 1;
    }

}
