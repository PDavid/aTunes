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

package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;

public abstract class AbstractEncoder implements Encoder {

	private String album;
	
    private String albumArtist;
    
    private String genre;
	
	private String extensionOfEncodedFiles;
	
    private ProgressListener listener;
    
    private int year;
    
    private String quality;

	private String[] availableQualities;

	private String defaultQuality;

	private String formatName;

	/**
	 * @param extensionOfEncodedFiles
	 * @param availableQualities
	 * @param defaultQuality
	 * @param formatName
	 */
	public AbstractEncoder(String extensionOfEncodedFiles, String[] availableQualities, String defaultQuality, String formatName) {
		this.extensionOfEncodedFiles = extensionOfEncodedFiles;
		this.availableQualities = availableQualities;
		this.defaultQuality = defaultQuality;
		this.formatName = formatName;
	}
	
	public final String getExtensionOfEncodedFiles() {
		return extensionOfEncodedFiles;
	}
	
    @Override
    public final void setAlbum(String album) {
        this.album = album;
    }
    
    public final String getAlbum() {
		return album;
	}
    
    @Override
    public final void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }
    
    public final String getAlbumArtist() {
		return albumArtist;
	}

    @Override
    public final void setGenre(String genre) {
        this.genre = genre;
    }

    public final String getGenre() {
		return genre;
	}
    
    @Override
    public final void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public final void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public final void setYear(int year) {
        this.year = year;
    }

    public final ProgressListener getListener() {
		return listener;
	}
    
    public final int getYear() {
		return year;
	}
    
    public final String getQuality() {
		return quality;
	}

    @Override
    public final String[] getAvailableQualities() {
        return availableQualities;
    }

    @Override
    public final String getDefaultQuality() {
        return defaultQuality;
    }

    @Override
    public final String getFormatName() {
        return formatName;
    }
}
