/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.controllers.ripcd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;

import net.sourceforge.atunes.gui.autocomplete.AutoCompleteDecorator;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.modules.cdripper.CdRipper;
import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.cdripper.encoders.Encoder;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * The Class RipCdDialogController.
 */
public class RipCdDialogController extends DialogController<RipCdDialog> {

    // Encoder options and file name patterns. Add here for more options

    /** The Constant FILENAMEPATTERN. */
    public static final String[] FILENAMEPATTERN = { StringUtils.getString(CdRipper.TRACK_NUMBER, " - ", CdRipper.TITLE_PATTERN),
            StringUtils.getString(CdRipper.ARTIST_PATTERN, " - ", CdRipper.ALBUM_PATTERN, " - ", CdRipper.TRACK_NUMBER, " - ", CdRipper.TITLE_PATTERN),
            StringUtils.getString(CdRipper.ARTIST_PATTERN, " - ", CdRipper.TITLE_PATTERN) };

    // Default encoder "quality" settings.

    /** The folder name edited. */
    private boolean folderNameEdited;

    /** The cancelled. */
    private boolean cancelled;

    /** The encoder setting changed. */
    private boolean encoderSettingChanged;

    /** The artist. */
    private String artist;

    /** The album. */
    private String album;

    /** The year. */
    private int year;

    /** The genre. */
    private String genre;

    /** The folder. */
    private String folder;

    /** The error correction setting for cd ripping */
    //private boolean useCdErrorCorrection;
    /**
     * Instantiates a new rip cd dialog controller.
     * 
     * @param dialogControlled
     *            the dialog controlled
     */
    public RipCdDialogController(RipCdDialog dialogControlled) {
        super(dialogControlled);
        addBindings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addBindings()
     */
    @Override
    protected void addBindings() {

        // Add genres combo box items
        List<String> genresSorted = Arrays.asList(Tag.genres);
        Collections.sort(genresSorted);
        getDialogControlled().getGenreComboBox().setModel(new ListComboBoxModel<String>(genresSorted));

        // Get the encoders
        Map<String, Encoder> encoders = RipperHandler.getInstance().getAvailableEncoders();

        // We must process "List encoderList" to display it correctly in the dropdown menu. Encoders don't work otherwise.
        String[] avaibleEncoders = encoders.keySet().toArray(new String[encoders.size()]);

        getDialogControlled().getFormat().setModel(new DefaultComboBoxModel(avaibleEncoders));

        // Add autocompletion
        AutoCompleteDecorator.decorate(getDialogControlled().getGenreComboBox());

        RipCdDialogListener listener = new RipCdDialogListener(getDialogControlled(), this);
        getDialogControlled().getOk().addActionListener(listener);
        getDialogControlled().getCancel().addActionListener(listener);
        getDialogControlled().getFolderSelectionButton().addActionListener(listener);
        getDialogControlled().getFormat().addActionListener(listener);
        getDialogControlled().getFilePattern().addActionListener(listener);
        getDialogControlled().getFolderName().addKeyListener(listener);
        getDialogControlled().getAmazonButton().addActionListener(listener);
        getDialogControlled().getArtistTextField().addKeyListener(listener);
        getDialogControlled().getAlbumTextField().addKeyListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addStateBindings
     * ()
     */
    @Override
    protected void addStateBindings() {
        // Nothing to to
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the folder.
     * 
     * @return the folder
     */
    public String getFolder() {
        return FileNameUtils.getValidFolderName(folder);
    }

    /**
     * Gets the genre.
     * 
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Gets the year.
     * 
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Checks if is cancelled.
     * 
     * @return the cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Checks if is encoder setting changed.
     * 
     * @return the encoderSettingChanged
     */
    public boolean isEncoderSettingChanged() {
        return encoderSettingChanged;
    }

    /**
     * Checks if is folder name edited.
     * 
     * @return the folderNameEdited
     */
    public boolean isFolderNameEdited() {
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
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Sets the cancelled.
     * 
     * @param cancelled
     *            the cancelled to set
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Sets the encoder setting changed.
     * 
     * @param encoderSettingChanged
     *            the encoderSettingChanged to set
     */
    public void setEncoderSettingChanged(boolean encoderSettingChanged) {
        this.encoderSettingChanged = encoderSettingChanged;
    }

    /**
     * Sets the folder.
     * 
     * @param folder
     *            the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * Sets the folder name edited.
     * 
     * @param folderNameEdited
     *            the folderNameEdited to set
     */
    public void setFolderNameEdited(boolean folderNameEdited) {
        this.folderNameEdited = folderNameEdited;
    }

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Sets the year.
     * 
     * @param year
     *            the year to set
     */
    public void setYear(int year) {
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
    public void showCdInfo(CDInfo cdInfo, String path) {
        setArtist(cdInfo.getArtist());
        getDialogControlled().getArtistTextField().setText(cdInfo.getArtist());
        setAlbum(cdInfo.getAlbum());
        getDialogControlled().getAlbumTextField().setText(cdInfo.getAlbum());
        setYear(DateUtils.getCurrentYear());
        getDialogControlled().getYearTextField().setText(Integer.toString(DateUtils.getCurrentYear()));
        setGenre(getDialogControlled().getGenreComboBox().getSelectedItem().toString());
        // Creates folders when information is coming from cdda2wav
        if (cdInfo.getArtist() != null && cdInfo.getAlbum() != null) {
            getDialogControlled().getFolderName().setText(
                    StringUtils.getString(RepositoryHandler.getInstance().getRepositoryPath(), SystemProperties.FILE_SEPARATOR, cdInfo.getArtist(),
                            SystemProperties.FILE_SEPARATOR, cdInfo.getAlbum()));
        } else {
            getDialogControlled().getFolderName().setText(path);
        }
        getDialogControlled().getAmazonButton().setEnabled(false);
        getDialogControlled().getFormat().setSelectedItem(RipperHandler.getInstance().getEncoder());
        getDialogControlled().getQualityComboBox().setSelectedItem(RipperHandler.getInstance().getEncoderQuality());
        getDialogControlled().getUseCdErrorCorrection().setSelected(RipperHandler.getInstance().getCdErrorCorrection());
        getDialogControlled().getFilePattern().setSelectedItem(RipperHandler.getInstance().getFileNamePattern());
        setFolder(null);
        getDialogControlled().setTableData(cdInfo);
        getDialogControlled().updateTrackNames(cdInfo.getTitles());
        getDialogControlled().updateArtistNames(cdInfo);
        getDialogControlled().updateComposerNames(cdInfo.getComposers());
        getDialogControlled().setVisible(true);
    }

}
