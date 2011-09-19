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

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.utils.I18nUtils;

public class FillDeviceWithRandomSongsAction extends CustomAbstractAction {

    private static final long serialVersionUID = -201250351035880261L;

    FillDeviceWithRandomSongsAction() {
        super(I18nUtils.getString("FILL_DEVICE_WITH_RANDOM_SONGS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("FILL_DEVICE_WITH_RANDOM_SONGS"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String freeMemory = "20";
        // Ask how much memory should be left free
        IInputDialog dialog = getBean(IInputDialog.class);
        dialog.setTitle(I18nUtils.getString("MEMORY_TO_LEAVE_FREE"));
        dialog.showDialog(freeMemory);
        freeMemory = dialog.getResult();
        try {
            DeviceHandler.getInstance().fillWithRandomSongs(Long.parseLong(freeMemory.trim()));
        } catch (Exception e2) {
            // User did not enter numerical value. Show error dialog
        	getBean(IErrorDialog.class).showErrorDialog(getBean(IFrame.class), I18nUtils.getString("ERROR_NO_NUMERICAL_VALUE"));
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return DeviceHandler.getInstance().isDeviceConnected();
    }
}
