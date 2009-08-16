/*
 * aTunes 1.14.0
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
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class FillDeviceWithRandomSongsAction extends Action {

    private static final long serialVersionUID = -201250351035880261L;

    FillDeviceWithRandomSongsAction() {
        super(LanguageTool.getString("FILL_DEVICE_WITH_RANDOM_SONGS"), ImageLoader.SHUFFLE_PLAYLIST);
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("FILL_DEVICE_WITH_RANDOM_SONGS"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String freeMemory = "20";
        // Ask how much memory should be left free
        freeMemory = VisualHandler.getInstance().showInputDialog(LanguageTool.getString("MEMORY_TO_LEAVE_FREE"), freeMemory, ImageLoader.DEVICE.getImage());
        long result;
        try {
            result = Long.parseLong(freeMemory.trim());
            DeviceHandler.getInstance().fillWithRandomSongs(result);
        } catch (Exception e2) {
            // User did not enter numerical value. Show error dialog
            VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("ERROR_NO_NUMERICAL_VALUE"));
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }
}
