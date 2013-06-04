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
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Close other play lists (not the visible one)
 * 
 * @author alex
 * 
 */
public class CloseOtherPlaylistsAction extends CustomAbstractAction {

	private static final long serialVersionUID = 902195930910854889L;

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	CloseOtherPlaylistsAction() {
		super(I18nUtils.getString("CLOSE_OTHER_PLAYLISTS"));
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.ALT_MASK
						+ InputEvent.SHIFT_MASK));
	}

	@Override
	protected void executeAction() {
		playListHandler.closeOtherPlaylists();
	}

	@Override
	public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
		return this.playListHandler.getPlayListCount() > 1;
	}
}
