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

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListLocalAudioObjectFilter;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Copies current selected play list to device
 * 
 * @author fleax
 * 
 */
public class CopyPlayListToDeviceAction extends CustomAbstractAction {

    private static final long serialVersionUID = 5899793232403738425L;

    CopyPlayListToDeviceAction() {
        super(I18nUtils.getString("COPY_PLAYLIST_TO_DEVICE"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COPY_PLAYLIST_TO_DEVICE"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Copy only LocalAudioObject objects
        DeviceHandler.getInstance().copyFilesToDevice(new PlayListLocalAudioObjectFilter().getObjects(Context.getBean(IPlayListHandler.class).getCurrentPlayList(true)));
    }

}
