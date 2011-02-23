/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

public class ShowPlayListControlsAction extends AbstractAction {

    private static final long serialVersionUID = 4338048323889228870L;

    ShowPlayListControlsAction() {
        super(I18nUtils.getString("SHOW_PLAYLIST_CONTROLS"), Images.getImage(Images.PLAY_LIST_CONTROLS));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_PLAYLIST_CONTROLS"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().isShowPlaylistControls());
        PlayListHandler.getInstance().showPlayListControls((Boolean) getValue(SELECTED_KEY));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationState.getInstance().setShowPlaylistControls((Boolean) getValue(SELECTED_KEY));
        PlayListHandler.getInstance().showPlayListControls((Boolean) getValue(SELECTED_KEY));
    }

}
