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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.autocomplete.AutoCompleteDecorator;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.tags.Genres;
import net.sourceforge.atunes.model.IStateRipper;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class RipCdDialogController extends AbstractSimpleController<RipCdDialog> {

    // Default encoder "quality" settings.

    private boolean cancelled;
    private boolean encoderSettingChanged;
    private String artist;
    private String album;
    private int year;
    private String genre;
    private int discNumber;
    
    private RipperHandler ripperHandler;
    
    private IStateRipper stateRipper;

    /**
     * Instantiates a new rip cd dialog controller.
     * 
     * @param dialogControlled
     * @param stateRipper
     * @param ripperHandler
     */
    RipCdDialogController(RipCdDialog dialogControlled, IStateRipper stateRipper, RipperHandler ripperHandler) {
        super(dialogControlled);
        this.stateRipper = stateRipper;
        this.ripperHandler = ripperHandler;
        addBindings();
    }

    @Override
	public void addBindings() {

        // Add genres combo box items
        List<String> genresSorted = Context.getBean(Genres.class).getGenres();
        Collections.sort(genresSorted);
        getComponentControlled().getGenreComboBox().setModel(new ListComboBoxModel<String>(genresSorted));

        Set<String> encoders = ripperHandler.getAvailableEncodersNames();
        String[] avaibleEncoders = encoders.toArray(new String[encoders.size()]);

        getComponentControlled().getFormat().setModel(new DefaultComboBoxModel(avaibleEncoders));

        // Add autocompletion
        AutoCompleteDecorator.decorate(getComponentControlled().getGenreComboBox());

        RipCdDialogListener listener = new RipCdDialogListener(getComponentControlled(), this, ripperHandler);
        getComponentControlled().getOk().addActionListener(listener);
        getComponentControlled().getCancel().addActionListener(listener);
        getComponentControlled().getFormat().addActionListener(listener);
        getComponentControlled().getTitlesButton().addActionListener(listener);
        getComponentControlled().getArtistTextField().addKeyListener(listener);
        getComponentControlled().getAlbumTextField().addKeyListener(listener);
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
     * @return disc number
     */
    int getDiscNumber() {
		return discNumber;
	}
    
    /**
     * @param discNumber
     */
    void setDiscNumber(int discNumber) {
		this.discNumber = discNumber;
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
     * Checks if is canceled.
     * 
     * @return the canceled
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
     * Show cd info and let user select settings
     * @param cdInfo
     */
    void showCdInfo(CDInfo cdInfo) {
        showArtist(cdInfo);
        showAlbum(cdInfo);
        showYear();
        showGenre(cdInfo);
        enableGetTitlesButton(cdInfo);
        getComponentControlled().getFormat().setSelectedItem(ripperHandler.getEncoderName());
        getComponentControlled().getQualityComboBox().setSelectedItem(stateRipper.getEncoderQuality());
        getComponentControlled().getUseCdErrorCorrection().setSelected(stateRipper.isUseCdErrorCorrection());
        getComponentControlled().setTableData(cdInfo);
        getComponentControlled().updateTrackNames(cdInfo.getTitles());
        getComponentControlled().updateArtistNames(cdInfo);
        getComponentControlled().updateComposerNames(cdInfo.getComposers());
        getComponentControlled().setVisible(true);
    }
    
    private boolean artistAndAlbumRetrieved(CDInfo cdInfo) {
    	return cdInfo.getArtist() != null && cdInfo.getAlbum() != null;
    }

	private void enableGetTitlesButton(CDInfo cdInfo) {
		getComponentControlled().getTitlesButton().setEnabled(artistAndAlbumRetrieved(cdInfo));
	}

	private void showGenre(CDInfo cdInfo) {
		if (artistAndAlbumRetrieved(cdInfo)) {
			if (cdInfo.getGenre() != null) {
				getComponentControlled().getGenreComboBox().setSelectedItem(cdInfo.getGenre());
				setGenre(cdInfo.getGenre());
			}
        } else {
        	getComponentControlled().getGenreComboBox().setSelectedItem("");
			setGenre(null);
        }
	}

	private void showYear() {
		setYear(0);
        getComponentControlled().getYearTextField().setText("");
	}

	private void showAlbum(CDInfo cdInfo) {
		setAlbum(cdInfo.getAlbum());
        getComponentControlled().getAlbumTextField().setText(cdInfo.getAlbum());
	}

	private void showArtist(CDInfo cdInfo) {
		setArtist(cdInfo.getArtist());
        getComponentControlled().getArtistTextField().setText(cdInfo.getArtist());
	}

}
