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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action moves selected rows in play list to top
 * 
 * @author fleax
 * 
 */
public class MoveToTopAction extends CustomAbstractAction {

	private static final long serialVersionUID = 8200322913128575526L;

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public MoveToTopAction() {
		super(I18nUtils.getString("MOVE_TO_TOP"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("MOVE_TO_TOP_TOOLTIP"));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		setEnabled(false);
	}

	@Override
	protected void executeAction() {
		this.playListHandler.moveToTop();
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return selection != null && !selection.isEmpty();
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return !playlist.isDynamic();
	}

}
