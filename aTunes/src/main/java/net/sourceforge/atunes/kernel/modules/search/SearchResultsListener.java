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

package net.sourceforge.atunes.kernel.modules.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;

/**
 * The listener interface for receiving searchResults events.
 */
public final class SearchResultsListener implements ActionListener {

	/** The controller. */
	private final SearchResultsController controller;

	/** The dialog. */
	private final SearchResultsDialog dialog;

	/**
	 * Instantiates a new search results listener.
	 * 
	 * @param controller
	 *            the controller
	 * @param dialog
	 *            the dialog
	 */
	SearchResultsListener(final SearchResultsController controller,
			final SearchResultsDialog dialog) {
		this.controller = controller;
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource().equals(this.dialog.getShowElementInfo())) {
			this.controller.showInfo();
		} else if (e.getSource().equals(this.dialog.getAddToCurrentPlayList())) {
			this.controller.addToPlayList();
		} else if (e.getSource().equals(this.dialog.getAddToNewPlayList())) {
			this.controller.addToNewPlayList();
		} else if (e.getSource().equals(this.dialog.getCreateDynamicPlayList())) {
			this.controller.createDynamicPlayList();
		}
	}

}
