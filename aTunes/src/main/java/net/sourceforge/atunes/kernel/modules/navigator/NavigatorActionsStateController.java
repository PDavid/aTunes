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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Component;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.atunes.kernel.actions.CustomAbstractAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITreeNode;

/**
 * Enables or disables navigator actions given selection
 * 
 * @author alex
 * 
 */
public class NavigatorActionsStateController {

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Enables or disables tree popup actions
	 * 
	 * @param rootSelected
	 * @param components
	 * @param nodes
	 */
	void updateTreePopupMenuWithTreeSelection(final boolean rootSelected,
			final Component[] components, final List<ITreeNode> nodes) {
		for (Component c : components) {
			updateMenuComponent(rootSelected, nodes, c);
		}
	}

	/**
	 * Enables or disables table popup actions
	 * 
	 * @param rootSelected
	 * @param components
	 * @param selection
	 */
	void updateTablePopupMenuWithTableSelection(final boolean rootSelected,
			final Component[] components, final List<IAudioObject> selection) {
		for (Component c : components) {
			updateTableMenuComponent(rootSelected, selection, c);
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param c
	 */
	private void updateMenuComponent(final boolean rootSelected,
			final List<ITreeNode> selection, final Component c) {
		if (c != null) {
			if (c instanceof JMenu) {
				for (int i = 0; i < ((JMenu) c).getItemCount(); i++) {
					updateMenuComponent(rootSelected, selection,
							((JMenu) c).getItem(i));
				}
			} else if (c instanceof JMenuItem) {
				updateMenuItem(rootSelected, selection, (JMenuItem) c);
			}
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param c
	 */
	private void updateTableMenuComponent(final boolean rootSelected,
			final List<IAudioObject> selection, final Component c) {
		if (c != null) {
			if (c instanceof JMenu) {
				for (int i = 0; i < ((JMenu) c).getItemCount(); i++) {
					updateTableMenuComponent(rootSelected, selection,
							((JMenu) c).getItem(i));
				}
			} else if (c instanceof JMenuItem) {
				updateTableMenuItem(rootSelected, selection, (JMenuItem) c);
			}
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param menuItem
	 */
	private void updateMenuItem(final boolean rootSelected,
			final List<ITreeNode> selection, final JMenuItem menuItem) {
		Action a = menuItem.getAction();
		if (a instanceof CustomAbstractAction) {
			CustomAbstractAction customAction = (CustomAbstractAction) a;
			if (!customAction.isEnabledForPlayList(this.playListHandler
					.getVisiblePlayList())) {
				customAction.setEnabled(false);
			} else {
				customAction.setEnabled(customAction
						.isEnabledForNavigationTreeSelection(rootSelected,
								selection));
			}
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param menuItem
	 */
	private void updateTableMenuItem(final boolean rootSelected,
			final List<IAudioObject> selection, final JMenuItem menuItem) {
		Action a = menuItem.getAction();
		if (a instanceof CustomAbstractAction) {
			CustomAbstractAction customAction = (CustomAbstractAction) a;
			if (!customAction.isEnabledForPlayList(this.playListHandler
					.getVisiblePlayList())) {
				customAction.setEnabled(false);
			} else {
				customAction.setEnabled(customAction
						.isEnabledForNavigationTableSelection(selection));
			}
		}
	}
}
