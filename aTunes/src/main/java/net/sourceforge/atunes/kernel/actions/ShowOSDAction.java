/*
 * aTunes 2.0.0
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

import net.sourceforge.atunes.kernel.modules.notify.NotifyHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;

public class ShowOSDAction extends Action {

    private static final long serialVersionUID = 646318992035897920L;

    ShowOSDAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NotifyHandler.getInstance().showNotification(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
    }

    @Override
    public String getCommandName() {
        return "showOSD";
    }
}
