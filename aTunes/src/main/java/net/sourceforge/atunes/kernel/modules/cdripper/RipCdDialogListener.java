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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IRipperHandler;

/**
 * The listener interface for receiving ripCdDialog events.
 */
final class RipCdDialogListener extends KeyAdapter implements ActionListener {

    /** The rip cd dialog. */
    private RipCdDialog ripCdDialog;

    /** The rip cd dialog controller. */
    private RipCdDialogController ripCdDialogController;
    
    private IRipperHandler ripperHandler;

    /**
     * Instantiates a new rip cd dialog listener.
     * @param ripCdDialog
     * @param ripCdDialogController
     * @param ripperHandler
     */
    public RipCdDialogListener(RipCdDialog ripCdDialog, RipCdDialogController ripCdDialogController, IRipperHandler ripperHandler) {
        this.ripCdDialog = ripCdDialog;
        this.ripCdDialogController = ripCdDialogController;
        this.ripperHandler = ripperHandler;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ripCdDialog.getOk()) {
            ripCdDialogController.setCancelled(false);
            ripCdDialogController.setArtist(ripCdDialog.getArtistTextField().getText());
            ripCdDialogController.setAlbum(ripCdDialog.getAlbumTextField().getText());
            try {
                ripCdDialogController.setYear(Integer.parseInt(ripCdDialog.getYearTextField().getText()));
            } catch (NumberFormatException ex) {
                ripCdDialogController.setYear(0);
            }
            try {
                ripCdDialogController.setDiscNumber(Integer.parseInt(ripCdDialog.getDiscNumberField().getText()));
            } catch (NumberFormatException ex) {
                ripCdDialogController.setDiscNumber(1);
            }
            ripCdDialogController.setGenre(ripCdDialog.getGenreComboBox().getSelectedItem().toString());
            ripCdDialog.setVisible(false);
        } else if (e.getSource() == ripCdDialog.getCancel()) {
            ripCdDialogController.setCancelled(true);
            ripCdDialog.setVisible(false);
        } else if (e.getSource() == ripCdDialog.getFormat()) {
            // Fill quality combo
            String[] qualities = ripperHandler.getEncoderQualities((String) ripCdDialog.getFormat().getSelectedItem());
            ripCdDialog.getQualityComboBox().setEnabled(qualities.length > 0);
            ripCdDialog.getQualityLabel().setEnabled(qualities.length > 0);
            ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(qualities));
            ripCdDialog.getQualityComboBox().setSelectedItem(ripperHandler.getEncoderDefaultQuality((String) ripCdDialog.getFormat().getSelectedItem()));
        } else if (e.getSource() == ripCdDialog.getTitlesButton()) {
        	ripperHandler.fillSongTitles(ripCdDialog.getArtistTextField().getText(), ripCdDialog.getAlbumTextField().getText());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == ripCdDialog.getArtistTextField() || e.getSource() == ripCdDialog.getAlbumTextField()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String artist = ripCdDialog.getArtistTextField().getText();
                    String album = ripCdDialog.getAlbumTextField().getText();
                    boolean enabled = !artist.equals("") && !album.equals("");
                    ripCdDialog.getTitlesButton().setEnabled(enabled);
                }
            });
        }
    }
}
