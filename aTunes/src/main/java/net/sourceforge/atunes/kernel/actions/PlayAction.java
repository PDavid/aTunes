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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class PlayAction extends CustomAbstractAction {

    private static final long serialVersionUID = -1122746023245126869L;

    PlayAction() {
        super(I18nUtils.getString("PLAY"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("PLAY"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setCommandHandler(getBean(ICommandHandler.class));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // disable enter key when focus is on filter text field (so event is not fired from PLAY/PAUSE button)
        if (getBean(IFrame.class).getPlayerControls().getFilterPanel().getFilterTextField().isFocusOwner()
                && e != null && !(e.getSource().getClass().equals(PlayPauseButton.class))) {
            return;
        }
        
        int selAudioObject = getBean(IFrame.class).getPlayListTable().getSelectedRow();
        int currPlayingAudioObject = getBean(IPlayListHandler.class).getIndexOfAudioObject(Context.getBean(IPlayerHandler.class).getAudioObject());

        if (selAudioObject != currPlayingAudioObject) {
            // another song selected to play
            if (e == null || e.getSource().getClass().equals(PlayPauseButton.class)) {
                // action is from PlayPauseButton (or system tray) -> pause
                Context.getBean(IPlayerHandler.class).playCurrentAudioObject(true);
            } else {
                // play another song
            	getBean(IPlayListHandler.class).setPositionToPlayInVisiblePlayList(selAudioObject);
                Context.getBean(IPlayerHandler.class).playCurrentAudioObject(false);
            }
        } else
            // selected song equals to song being currently played -> pause
            Context.getBean(IPlayerHandler.class).playCurrentAudioObject(true);
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        // Play action is always enabled even if play list or selection are empty, because this action is used in play button
        return true;
    }

    @Override
    public String getCommandName() {
        return "play";
    }

}
