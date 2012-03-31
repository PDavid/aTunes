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

package net.sourceforge.atunes.kernel.modules.covernavigator;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorDialog;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;

final class GenerateAndShowAlbumPanelsSwingWorker extends SwingWorker<Void, IntermediateResult> {

	private final CoverNavigatorDialog dialog;
	
    private final IAudioObjectImageLocator audioObjectImageLocator;
    
	private final IArtist artistSelected;
	
	/**
	 * @param dialog
	 * @param audioObjectImageLocator
	 * @param artistSelected
	 */
	GenerateAndShowAlbumPanelsSwingWorker(CoverNavigatorDialog dialog, IAudioObjectImageLocator audioObjectImageLocator, IArtist artistSelected) {
		this.dialog = dialog;
		this.audioObjectImageLocator = audioObjectImageLocator;
		this.artistSelected = artistSelected;
	}

	@Override
	protected Void doInBackground() {
	    final List<IAlbum> albums = new ArrayList<IAlbum>(artistSelected.getAlbums().values());
	    Collections.sort(albums);

	    for (IAlbum album : albums) {
	        ImageIcon cover = audioObjectImageLocator.getImage(album, Constants.COVER_NAVIGATOR_IMAGE_SIZE);
	        publish(new IntermediateResult(album, cover));
	    }
	    return null;
	}

	@Override
	protected void done() {
	    dialog.setCursor(Cursor.getDefaultCursor());
	    dialog.getList().setEnabled(true);
	    dialog.getCoversButton().setEnabled(true);
	}

	@Override
	protected void process(List<IntermediateResult> intermediateResults) {
	    for (IntermediateResult intermediateResult : intermediateResults) {
	    	dialog.getCoversPanel().add(getPanelForAlbum(intermediateResult.getAlbum(), intermediateResult.getCover()));
	    	dialog.getCoversPanel().revalidate();
	    	dialog.getCoversPanel().repaint();
	    	dialog.getCoversPanel().validate();
	    }
	}
	
    /**
     * Gets the panel for album.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     * @param coversSize
     *            the covers size
     * 
     * @return the panel for album
     */
    private JPanel getPanelForAlbum(IAlbum album, ImageIcon cover) {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel coverLabel = new JLabel(cover);
        coverLabel.setToolTipText(album.getName());
        if (cover == null) {
            coverLabel.setPreferredSize(new Dimension(Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize(), Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize()));
            coverLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        }

        JLabel label = new JLabel(album.getName(), SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize(), 20));

        panel.add(coverLabel);
        panel.add(label);
        panel.setPreferredSize(new Dimension(CoverNavigatorController.COVER_PANEL_WIDTH, CoverNavigatorController.COVER_PANEL_HEIGHT));
        panel.setOpaque(false);

        GuiUtils.applyComponentOrientation(panel);
        return panel;
    }
}