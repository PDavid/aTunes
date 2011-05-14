/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The listener interface for receiving ripCdDialog events.
 */
final class RipCdDialogListener extends KeyAdapter implements ActionListener {

    /** The rip cd dialog. */
    private RipCdDialog ripCdDialog;

    /** The rip cd dialog controller. */
    private RipCdDialogController ripCdDialogController;

    /**
     * Instantiates a new rip cd dialog listener.
     * 
     * @param ripCdDialog
     *            the rip cd dialog
     * @param ripCdDialogController
     *            the rip cd dialog controller
     */
    public RipCdDialogListener(RipCdDialog ripCdDialog, RipCdDialogController ripCdDialogController) {
        this.ripCdDialog = ripCdDialog;
        this.ripCdDialogController = ripCdDialogController;
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
                ripCdDialogController.setYear(DateUtils.getCurrentYear());
            }
            ripCdDialogController.setGenre(ripCdDialog.getGenreComboBox().getSelectedItem().toString());
            ripCdDialogController.setFolder(ripCdDialog.getFolderName().getText());
            ripCdDialog.setVisible(false);
        } else if (e.getSource() == ripCdDialog.getCancel()) {
            ripCdDialogController.setCancelled(true);
            ripCdDialog.setVisible(false);
        } else if (e.getSource() == ripCdDialog.getFolderSelectionButton()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showDialog(ripCdDialog, I18nUtils.getString("SELECT_FOLDER"));
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedPath = chooser.getSelectedFile();
                ripCdDialog.getFolderName().setText(selectedPath.getAbsolutePath());
                ripCdDialogController.setFolderNameEdited(true);
            }
        } else if (e.getSource() == ripCdDialog.getFormat()) {
            // Fill quality combo
            String[] qualities = RipperHandler.getInstance().getEncoderQualities((String) ripCdDialog.getFormat().getSelectedItem());
            ripCdDialog.getQualityComboBox().setEnabled(qualities.length > 0);
            ripCdDialog.getQualityComboBox().setModel(new DefaultComboBoxModel(qualities));
            ripCdDialog.getQualityComboBox().setSelectedItem(RipperHandler.getInstance().getEncoderDefaultQuality((String) ripCdDialog.getFormat().getSelectedItem()));
        } else if (e.getSource() == ripCdDialog.getTitlesButton()) {
            RipperHandler.getInstance().fillSongTitles(ripCdDialog.getArtistTextField().getText(), ripCdDialog.getAlbumTextField().getText());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == ripCdDialog.getFolderName() || e.getSource() == ripCdDialog.getAlbumTextField()) {
            ripCdDialogController.setFolderNameEdited(true);
        }

        if (e.getSource() == ripCdDialog.getArtistTextField() || e.getSource() == ripCdDialog.getAlbumTextField()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String artist = ripCdDialog.getArtistTextField().getText();
                    String album = ripCdDialog.getAlbumTextField().getText();
                    String repositoryPath = RepositoryHandler.getInstance().getRepositoryPath();
                    boolean enabled = !artist.equals("") && !album.equals("");
                    ripCdDialog.getTitlesButton().setEnabled(enabled);
                    if (!ripCdDialogController.isFolderNameEdited()) {
                        if (enabled) {
                            ripCdDialog.getFolderName().setText(
                                    StringUtils.getString(repositoryPath, OsManager.getFileSeparator(), artist, OsManager.getFileSeparator(), album));
                        } else if (artist.equals("")) {
                            ripCdDialog.getFolderName().setText(StringUtils.getString(repositoryPath, OsManager.getFileSeparator(), album));
                        } else {
                            ripCdDialog.getFolderName().setText(StringUtils.getString(repositoryPath, OsManager.getFileSeparator(), artist));
                        }
                    }
                }
            });
        }
    }

}
