/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.controllers.editTitlesDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;

/**
 * The listener interface for receiving editTitlesDialogAction events.
 */
public final class EditTitlesDialogActionListener implements ActionListener {

    /** The dialog. */
    private EditTitlesDialog dialog;

    /** The controller. */
    private EditTitlesDialogController controller;

    /**
     * Instantiates a new edits the titles dialog action listener.
     * 
     * @param dialog
     *            the dialog
     * @param controller
     *            the controller
     */
    public EditTitlesDialogActionListener(EditTitlesDialog dialog, EditTitlesDialogController controller) {
        this.dialog = dialog;
        this.controller = controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dialog.getRetrieveTitles()) {
            AlbumInfo albumInfo = LastFmService.getInstance().getAlbum(controller.getAlbum().getArtist().toString(), controller.getAlbum().getName());
            if (albumInfo != null) {
                List<String> tracks = new ArrayList<String>();
                for (TrackInfo trackInfo : albumInfo.getTracks()) {
                    tracks.add(trackInfo.getTitle());
                }
                controller.setTitles(tracks);
            }
        } else if (e.getSource() == dialog.getOkButton()) {
            controller.editFiles();
            dialog.setVisible(false);
        } else {
            dialog.setVisible(false);
        }
    }
}
