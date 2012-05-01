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

import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IInputDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class RenamePlaylistAction extends CustomAbstractAction {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8445003048535126058L;

    private IPlayListHandler playListHandler;
    
    private IInputDialogFactory inputDialogFactory;
    
    /**
     * @param inputDialogFactory
     */
    public void setInputDialogFactory(IInputDialogFactory inputDialogFactory) {
		this.inputDialogFactory = inputDialogFactory;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    public RenamePlaylistAction() {
        super(I18nUtils.getString("RENAME_PLAYLIST"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_PLAYLIST"));
    }

    @Override
    protected void executeAction() {
    	String currentName = playListHandler.getCurrentVisiblePlayListName();
        IInputDialog dialog = inputDialogFactory.getDialog();
        dialog.setTitle(I18nUtils.getString("RENAME_PLAYLIST"));
        dialog.showDialog(currentName);
        playListHandler.renameCurrentVisiblePlayList(dialog.getResult());
    }

}
