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
package net.sourceforge.atunes.kernel.controllers.searchResults;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;

/**
 * The listener interface for receiving searchResults events.
 */
public final class SearchResultsListener implements ActionListener {

    /** The controller. */
    private SearchResultsController controller;

    /** The dialog. */
    private SearchResultsDialog dialog;

    /**
     * Instantiates a new search results listener.
     * 
     * @param controller
     *            the controller
     * @param dialog
     *            the dialog
     */
    SearchResultsListener(SearchResultsController controller, SearchResultsDialog dialog) {
        this.controller = controller;
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(dialog.getShowElementInfo())) {
            controller.showInfo();
        } else if (e.getSource().equals(dialog.getAddToCurrentPlayList())) {
            controller.addToPlayList();
        } else if (e.getSource().equals(dialog.getAddToNewPlayList())) {
            controller.addToNewPlayList();
        }
    }

}
