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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;

import net.sourceforge.atunes.gui.autocomplete.AutoCompleteDecorator;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.tags.AbstractTag;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class RipCdDialogController extends AbstractSimpleController<RipCdDialog> {

    // Default encoder "quality" settings.

    private boolean folderNameEdited;
    private boolean cancelled;
    private boolean encoderSettingChanged;
    private String artist;
    private String album;
    private int year;
    private String genre;
    private String folder;

    /** The error correction setting for cd ripping */
    //private boolean useCdErrorCorrection;
    /**
     * Instantiates a new rip cd dialog controller.
     * 
     * @param dialogControlled
     *            the dialog controlled
     */
    RipCdDialogController(RipCdDialog dialogControlled) {
        super(dialogControlled);
        addBindings();
    }

    @Override
    protected void addBindings() {

        // Add genres combo box items
        List<String> genresSorted = Arrays.asList(AbstractTag.genres);
        Collections.sort(genresSorted);
        getComponentControlled().getGenreComboBox().setModel(new ListComboBoxModel<String>(genresSorted));

        // Get the encoders
        Map<String, Encoder> encoders = RipperHandler.getInstance().getAvailableEncoders();

        // We must process "List encoderList" to display it correctly in the dropdown menu. Encoders don't work otherwise.
        String[] avaibleEncoders = encoders.keySet().toArray(new String[encoders.size()]);

        getComponentControlled().getFormat().setModel(new DefaultComboBoxModel(avaibleEncoders));

        // Add autocompletion
        AutoCompleteDecorator.decorate(getComponentControlled().getGenreComboBox());

        RipCdDialogListener listener = new RipCdDialogListener(getComponentControlled(), this);
        getComponentControlled().getOk().addActionListener(listener);
        getComponentControlled().getCancel().addActionListener(listener);
        getComponentControlled().getFolderSelectionButton().addActionListener(listener);
        getComponentControlled().getFormat().addActionListener(listener);
        getComponentControlled().getFilePattern().addActionListener(listener);
        getComponentControlled().getFolderName().addKeyListener(listener);
        getComponentControlled().getTitlesButton().addActionListener(listener);
        getComponentControlled().getArtistTextField().addKeyListener(listener);
        getComponentControlled().getAlbumTextField().addKeyListener(listener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to to
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    String getAlbum() {
        return album;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    String getArtist() {
        return artist;
    }

    /**
     * Gets the folder.
     * 
     * @return the folder
     */
    String getFolder() {
        return FileNameUtils.getValidFolderName(folder);
    }

    /**
     * Gets the genre.
     * 
     * @return the genre
     */
    String getGenre() {
        return genre;
    }

    /**
     * Gets the year.
     * 
     * @return the year
     */
    int getYear() {
        return year;
    }

    /**
     * Checks if is cancelled.
     * 
     * @return the cancelled
     */
    boolean isCancelled() {
        return cancelled;
    }

    /**
     * Checks if is encoder setting changed.
     * 
     * @return the encoderSettingChanged
     */
    boolean isEncoderSettingChanged() {
        return encoderSettingChanged;
    }

    /**
     * Checks if is folder name edited.
     * 
     * @return the folderNameEdited
     */
    boolean isFolderNameEdited() {
        return folderNameEdited;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#notifyReload()
     */
    @Override
    protected void notifyReload() {
        // Nothing to to
    }

    /**
     * Sets the album.
     * 
     * @param album
     *            the album to set
     */
    void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Sets the cancelled.
     * 
     * @param cancelled
     *            the cancelled to set
     */
    void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Sets the encoder setting changed.
     * 
     * @param encoderSettingChanged
     *            the encoderSettingChanged to set
     */
    void setEncoderSettingChanged(boolean encoderSettingChanged) {
        this.encoderSettingChanged = encoderSettingChanged;
    }

    /**
     * Sets the folder.
     * 
     * @param folder
     *            the folder to set
     */
    void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * Sets the folder name edited.
     * 
     * @param folderNameEdited
     *            the folderNameEdited to set
     */
    void setFolderNameEdited(boolean folderNameEdited) {
        this.folderNameEdited = folderNameEdited;
    }

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the genre to set
     */
    void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Sets the year.
     * 
     * @param year
     *            the year to set
     */
    void setYear(int year) {
        this.year = year;
    }

    /**
     * Show cd info.
     * 
     * @param cdInfo
     *            the cd info
     * @param path
     *            the path
     */
    void showCdInfo(CDInfo cdInfo, String path) {
        setArtist(cdInfo.getArtist());
        getComponentControlled().getArtistTextField().setText(cdInfo.getArtist());
        setAlbum(cdInfo.getAlbum());
        getComponentControlled().getAlbumTextField().setText(cdInfo.getAlbum());
        setYear(DateUtils.getCurrentYear());
        getComponentControlled().getYearTextField().setText(Integer.toString(DateUtils.getCurrentYear()));
        setGenre(getComponentControlled().getGenreComboBox().getSelectedItem().toString());
        // Creates folders when information is coming from cdda2wav
        if (cdInfo.getArtist() != null && cdInfo.getAlbum() != null) {
            getComponentControlled().getFolderName().setText(
                    StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), OsManager.getFileSeparator(), cdInfo.getArtist(),
                    		OsManager.getFileSeparator(), cdInfo.getAlbum()));
        } else {
            getComponentControlled().getFolderName().setText(path);
        }
        getComponentControlled().getTitlesButton().setEnabled(false);
        getComponentControlled().getFormat().setSelectedItem(RipperHandler.getInstance().getEncoder());
        getComponentControlled().getQualityComboBox().setSelectedItem(RipperHandler.getInstance().getEncoderQuality());
        getComponentControlled().getUseCdErrorCorrection().setSelected(RipperHandler.getInstance().getCdErrorCorrection());
        getComponentControlled().getFilePattern().setSelectedItem(RipperHandler.getInstance().getFileNamePattern());
        setFolder(null);
        getComponentControlled().setTableData(cdInfo);
        getComponentControlled().updateTrackNames(cdInfo.getTitles());
        getComponentControlled().updateArtistNames(cdInfo);
        getComponentControlled().updateComposerNames(cdInfo.getComposers());
        getComponentControlled().setVisible(true);
    }

}
