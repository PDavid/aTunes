/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Renames a play list
 * 
 * @author alex
 * 
 */
public class RenamePlaylistAction extends CustomAbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8445003048535126058L;

	private IPlayListHandler playListHandler;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Constructor
	 */
	public RenamePlaylistAction() {
		super(I18nUtils.getString("RENAME_PLAYLIST"));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
	}

	@Override
	protected void executeAction() {
		String currentName = playListHandler.getCurrentVisiblePlayListName();
		IInputDialog dialog = dialogFactory.newDialog(IInputDialog.class);
		dialog.setTitle(I18nUtils.getString("RENAME_PLAYLIST"));
		dialog.setText(currentName);
		dialog.showDialog();
		playListHandler.renameCurrentVisiblePlayList(dialog.getResult());
	}

	@Override
	public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
		return true;
	}
}
