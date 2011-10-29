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

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public class PlayAction extends CustomAbstractAction {

    private static final long serialVersionUID = -1122746023245126869L;

    private IFrame frame;
    
    private IPlayListHandler playListHandler;
    
    private IPlayerHandler playerHandler;
    
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
    
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    public PlayAction() {
        super(I18nUtils.getString("PLAY"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("PLAY"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
    }

    @Override
    protected void executeAction() {
        // disable enter key when focus is on filter text field (so event is not fired from PLAY/PAUSE button)
        if (frame.getPlayerControls().getFilterPanel().getFilterTextField().isFocusOwner()
                && getSource() != null && !(getSource().getClass().equals(PlayPauseButton.class))) {
        	Logger.debug("Skipping play action");
            return;
        }
        
        int selAudioObject = frame.getPlayListTable().getSelectedRow();
        int currPlayingAudioObject = playListHandler.getIndexOfAudioObject(playerHandler.getAudioObject());

        if (selAudioObject != currPlayingAudioObject) {
            // another song selected to play
            if (getSource() == null || getSource().getClass().equals(PlayPauseButton.class)) {
                // action is from PlayPauseButton (or system tray) -> pause
                playerHandler.playCurrentAudioObject(true);
            } else {
                // play another song
            	playListHandler.setPositionToPlayInVisiblePlayList(selAudioObject);
                playerHandler.playCurrentAudioObject(false);
            }
        } else
            // selected song equals to song being currently played -> pause
            playerHandler.playCurrentAudioObject(true);
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
