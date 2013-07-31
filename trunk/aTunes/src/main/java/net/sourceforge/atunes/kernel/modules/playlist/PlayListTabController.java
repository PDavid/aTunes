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

package net.sourceforge.atunes.kernel.modules.playlist;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.panels.PlayListSelectorPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;

class PlayListTabController extends
		AbstractSimpleController<PlayListSelectorPanel> implements
		IPlayListTabController {

	private IPlayListSelectorPanel playListSelectorPanel;

	private PlayListSelectorWrapper playListSelectorWrapper;

	/**
	 * @param playListSelectorWrapper
	 */
	public void setPlayListSelectorWrapper(
			final PlayListSelectorWrapper playListSelectorWrapper) {
		this.playListSelectorWrapper = playListSelectorWrapper;
	}

	/**
	 * @param playListSelectorPanel
	 */
	public void setPlayListSelectorPanel(
			final IPlayListSelectorPanel playListSelectorPanel) {
		this.playListSelectorPanel = playListSelectorPanel;
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		setComponentControlled((PlayListSelectorPanel) this.playListSelectorPanel);
		addBindings();
		addStateBindings();

		this.playListSelectorWrapper
				.arrangeComponents(getComponentControlled());
	}

	@Override
	public void addBindings() {
		PlayListTabListener l = new PlayListTabListener(
				this.playListSelectorWrapper);
		this.playListSelectorWrapper.addBindings(l);
	}

	/**
	 * Delete play list.
	 * 
	 * @param index
	 *            the index
	 */
	@Override
	public void deletePlayList(final int index) {
		int selectedPlaylist = getSelectedPlayListIndex();
		this.playListSelectorWrapper.deletePlayList(index);
		if (index == selectedPlaylist) {
			forceSwitchTo(0);
		}
	}

	/**
	 * Force switch to.
	 * 
	 * @param index
	 *            the index
	 */
	void forceSwitchTo(final int index) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				PlayListTabController.this.playListSelectorWrapper
						.forceSwitchTo(index);
			}
		});
	}

	/**
	 * New play list.
	 * 
	 * @param name
	 *            the name
	 */
	void newPlayList(final String name, final IColorMutableImageIcon icon) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				PlayListTabController.this.playListSelectorWrapper.newPlayList(
						name, icon);
			}
		});
	}

	/**
	 * Rename play list.
	 * 
	 * @param index
	 *            the index
	 * @param newName
	 *            the new name
	 */
	void renamePlayList(final int index, final String newName) {
		this.playListSelectorWrapper.renamePlayList(index, newName);
	}

	/**
	 * Returns selected play list index
	 * 
	 * @return
	 */
	@Override
	public int getSelectedPlayListIndex() {
		return this.playListSelectorWrapper.getSelectedPlayListIndex();
	}

	/**
	 * Returns name of play list at given position
	 * 
	 * @param index
	 * @return
	 */
	String getPlayListName(final int index) {
		return this.playListSelectorWrapper.getPlayListName(index);
	}

	/**
	 * Shows combo box to select play lists if necessary
	 */
	public void showPlayListSelectorComboBox() {
		getComponentControlled().removeAll();
		this.playListSelectorWrapper
				.arrangeComponents(getComponentControlled());
	}
}
