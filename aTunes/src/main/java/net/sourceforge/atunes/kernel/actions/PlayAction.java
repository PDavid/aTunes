/*
 * aTunes 2.0.0-SNAPSHOT
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
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class PlayAction extends Action {

    private static final long serialVersionUID = -1122746023245126869L;

    PlayAction() {
        super(I18nUtils.getString("PLAY"), Images.getImage(Images.PLAY_TINY));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("PLAY"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PlayerHandler.getInstance().playCurrentAudioObject(true);
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
    	// Play action is always enabled even if play list or selection are empty, because this action is used in play button
        return true;
    }

    @Override
    public String getCommandName() {
        return "play";
    }

}
