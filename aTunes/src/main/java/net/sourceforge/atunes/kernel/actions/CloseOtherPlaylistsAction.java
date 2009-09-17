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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class CloseOtherPlaylistsAction extends Action {

    private static final long serialVersionUID = 902195930910854889L;

    CloseOtherPlaylistsAction() {
        super(I18nUtils.getString("CLOSE_OTHER_PLAYLISTS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("CLOSE_OTHER_PLAYLISTS"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // The current selected play list when this action is fired
        int i = ControllerProxy.getInstance().getPlayListTabController().getSelectedTabIndex();
        if (i != -1) {
            // Remove play lists from 0 to i. Remove first play list until current play list is at index 0  
            for (int j = 0; j < i; j++) {
                PlayListHandler.getInstance().removePlayList(0);
            }
            // Now current play list is at index 0, so delete from play list size down to 1
            while (PlayListHandler.getInstance().getPlayListCount() > 1) {
                PlayListHandler.getInstance().removePlayList(PlayListHandler.getInstance().getPlayListCount() - 1);
            }
        }
    }

}
