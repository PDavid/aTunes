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

package net.sourceforge.atunes.kernel.modules.tags;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * The listener interface for receiving editTitlesDialogAction events.
 */
public final class EditTitlesDialogActionListener implements ActionListener {

	/** The dialog. */
	private final EditTitlesDialog dialog;

	/** The controller. */
	private final EditTitlesDialogController controller;

	private final IWebServicesHandler webServicesHandler;

	/**
	 * Instantiates a new edits the titles dialog action listener.
	 * 
	 * @param dialog
	 * @param controller
	 * @param webServicesHandler
	 */
	public EditTitlesDialogActionListener(EditTitlesDialog dialog,
			EditTitlesDialogController controller,
			IWebServicesHandler webServicesHandler) {
		this.dialog = dialog;
		this.controller = controller;
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == dialog.getRetrieveTitles()) {
			IAlbumInfo albumInfo = webServicesHandler.getAlbum(controller
					.getAlbum().getArtist().toString(), controller.getAlbum()
					.getName());
			if (albumInfo != null) {
				List<String> tracks = new ArrayList<String>();
				for (ITrackInfo trackInfo : albumInfo.getTracks()) {
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
