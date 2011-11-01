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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This class clears current play list
 * 
 * @author fleax
 */
public class ClearPlayListAction extends CustomAbstractAction {

    private static final long serialVersionUID = 7784228526804232608L;

    private IConfirmationDialogFactory confirmationDialogFactory;
    
    private IPlayListHandler playListHandler;
    
    /**
     * @param confirmationDialogFactory
     */
    public void setConfirmationDialogFactory(IConfirmationDialogFactory confirmationDialogFactory) {
		this.confirmationDialogFactory = confirmationDialogFactory;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    public ClearPlayListAction() {
        super(I18nUtils.getString("CLEAR"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("CLEAR_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
    }

    @Override
    protected void executeAction() {
        if (confirmationDialogFactory.getDialog().showDialog(I18nUtils.getString("CLEAR_PLAYLIST_WARNING"))) {
        	playListHandler.clearPlayList();
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return true;
    }
}
