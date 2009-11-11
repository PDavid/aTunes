/*
 * aTunes 2.0.0-SNAPSHOT
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
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This class clears current play list
 * 
 * @author fleax
 */
public class ClearPlayListAction extends Action {

    private static final long serialVersionUID = 7784228526804232608L;

    ClearPlayListAction() {
        super(I18nUtils.getString("CLEAR"), ImageLoader.getImage(ImageLoader.CLEAR));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("CLEAR_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int returnValue = JOptionPane.showConfirmDialog(GuiHandler.getInstance().getFrame().getFrame(), I18nUtils.getString("CLEAR_PLAYLIST_WARNING"), I18nUtils
                .getString("CLEAR"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (JOptionPane.YES_OPTION == returnValue) {
            PlayListHandler.getInstance().clearPlayList();
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
        return true;
    }

}
