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

package net.sourceforge.atunes.kernel.modules.repository.data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.modules.repository.ImageCache;
import net.sourceforge.atunes.kernel.modules.repository.LocalAudioObjectValidator;
import net.sourceforge.atunes.kernel.modules.tags.DefaultTag;
import net.sourceforge.atunes.kernel.modules.tags.TagDetector;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.model.PluginApi;
import org.joda.time.base.BaseDateTime;

/**
 * AudioFile class initializes audio files so that tags and audio information
 * can be retrieved by the tagging library. Provides information about bitrate,
 * duration and frequency of the audio file. Provides tag information.
 * 
 * @author fleax
 */
@PluginApi
public final class AudioFile implements ILocalAudioObject, Serializable {

    private static final long serialVersionUID = -1139001443603556703L;

    private ITag tag;
    private List<File> externalPictures;
    private int duration;
	private long bitrate;
    private int frequency;
    private long readTime;
    private int stars;
    
    /** The file on disk. */
    private String filePath;

    /**
     * Instantiates a new audio file. File is read
     * 
     * @param file
     *            the file
     */
    public AudioFile(File file) {
    	setFile(file);
    }

    /**
     * Instantiates a new audio file. File is NOT read
     * 
     * @param fileName
     *            the file name
     */
    public AudioFile(String fileName) {
    	this.filePath = fileName;
    }

    /**
     * Reads a file
     * 
     * @param file
     *            the file
     */
    private void readFile() {
        // Don't read from formats not supported by Jaudiotagger
        if (!LocalAudioObjectValidator.isValidAudioFile(filePath, Format.APE, Format.MPC)) {
            readInformation(true);
        }
        this.readTime = System.currentTimeMillis();
    }

    /**
     * Adds the external picture.
     * 
     * @param picture
     *            the picture
     */
    public void addExternalPicture(File picture) {
        if (externalPictures == null) {
            externalPictures = new ArrayList<File>();
        }
        if (!externalPictures.contains(picture)) {
            externalPictures.add(0, picture);
        }
    }

    /**
     * Delete tags.
     */
    private void deleteTags() {
        tag = null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AudioFile)) {
            return false;
        }
        return ((AudioFile) o).getUrl().equals(getUrl());
    }

    @Override
    public String getAlbum() {
        String album;
        if (tag != null && tag.getAlbum() != null && !tag.getAlbum().isEmpty()) {
            album = tag.getAlbum();
        } else {
            album = Album.getUnknownAlbum();
        }
        return album;
    }

    @Override
    public String getAlbumArtist() {
        String albumArtist;
        if (tag != null && tag.getAlbumArtist() != null && !tag.getAlbumArtist().isEmpty()) {
            albumArtist = tag.getAlbumArtist();
        } else {
            albumArtist = Artist.getUnknownArtist();
        }
        return albumArtist;
    }

    @Override
    public String getAlbumArtistOrArtist() {
        return getAlbumArtist().isEmpty() || getAlbumArtist().equals(Artist.getUnknownArtist()) ? getArtist() : getAlbumArtist();
    }

    @Override
    public String getArtist() {
        String artist;
        if (tag != null && tag.getArtist() != null && !tag.getArtist().isEmpty()) {
            artist = tag.getArtist();
        } else {
            artist = Artist.getUnknownArtist();
        }
        return artist;
    }

    @Override
    public long getBitrate() {
        return bitrate;
    }

    @Override
    public String getComposer() {
        String composer;
        if (tag != null && tag.getComposer() != null) {
            composer = tag.getComposer();
        } else {
            composer = "";
        }
        return composer;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the external pictures.
     * 
     * @return the external pictures
     */
    public List<File> getExternalPictures() {
        return externalPictures;
    }

    /**
     * Gets the external pictures count.
     * 
     * @return the external pictures count
     */
    public int getExternalPicturesCount() {
        return externalPictures != null ? externalPictures.size() : 0;
    }

    /**
     * Return the file on disk
     * 
     * @return the file on disk
     */
    @Override
    public File getFile() {
    	return filePath != null ? new File(filePath) : null;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public String getGenre() {
        if (tag != null && tag.getGenre() != null && !tag.getGenre().isEmpty()) {
            return tag.getGenre();
        }
        return Genre.getUnknownGenre();
    }

    @Override
    public String getLyrics() {
        String lyrics;
        if (tag != null && tag.getLyrics() != null) {
            lyrics = tag.getLyrics();
        } else {
            lyrics = "";
        }
        return lyrics;
    }

    /**
     * Return tag comment
     * 
     * @return
     */
    @Override
    public String getComment() {
        if (tag != null && tag.getComment() != null) {
            return tag.getComment();
        }
        return "";
    }

    /**
     * Gets the name without extension.
     * 
     * @return the name without extension
     */
    @Override
    public String getNameWithoutExtension() {
        if (filePath == null) {
            return null;
        }
        return getFile().getName();
    }

    /**
     * Gets the stars.
     * 
     * @return the stars
     */
    @Override
    public int getStars() {
        return stars;
    }

    /**
     * Gets the tag.
     * 
     * @return the tag
     */
    public ITag getTag() {
        return tag;
    }

    @Override
    public String getTitle() {
        String title;
        if (tag != null && tag.getTitle() != null) {
            title = tag.getTitle();
        } else {
            title = "";
        }
        return title;
    }

    @Override
    public String getTitleOrFileName() {
        String title;
        if (tag != null && tag.getTitle() != null && !tag.getTitle().isEmpty()) {
            title = tag.getTitle();
        } else {
            title = getNameWithoutExtension();
        }
        return title;
    }

    @Override
    public int getTrackNumber() {
        if (tag != null) {
            if (tag instanceof DefaultTag) {
                return ((DefaultTag) tag).getTrackNumber() > 0 ? ((DefaultTag) tag).getTrackNumber() : 0;
            }
            return 0;
        }
        return 0;
    }

    @Override
    public String getUrl() {
    	return filePath;
    }

    @Override
    public String getYear() {
        if (tag != null && tag.getYear() > 0) {
            return Integer.toString(tag.getYear());
        }
        return "";
    }

    @Override
    public BaseDateTime getDate() {
        if (tag != null) {
            return tag.getDate();
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return getUrl().hashCode();
    }

    /**
     * Checks for internal picture.
     * 
     * @return true, if successful
     */
    @Override
    public final boolean hasInternalPicture() {
        return tag != null && tag.hasInternalImage();
    }

    /**
     * Checks if the tag of this audio file does support internal images
     * 
     * @return if the tag of this audio file does support internal images
     */
    @Override
    public final boolean supportsInternalPicture() {
        return LocalAudioObjectValidator.isValidAudioFile(filePath, Format.FLAC, Format.MP3, Format.MP4_1, Format.MP4_2, Format.OGG, Format.WMA);
    }

    /**
     * Introspect tags. Get the tag for the file.
     */
    private void readInformation(boolean readAudioProperties) {
        TagDetector.readInformation(this, readAudioProperties);
    }

    /**
     * Checks if is up to date.
     * 
     * @return true, if is up to date
     */
    public boolean isUpToDate() {
        if (filePath == null) {
            return false;
        }
        return readTime > getFile().lastModified();
    }

    /**
     * Refresh tag.
     */
    public void refreshTag() {
        deleteTags();
        readInformation(false);
        readTime = System.currentTimeMillis();
    }

    /**
     * Sets the external pictures.
     * 
     * @param externalPictures
     *            the new external pictures
     */
    @Override
    public void setExternalPictures(List<File> externalPictures) {
        this.externalPictures = externalPictures;
    }

    /**
     * Sets the file of this audio file
     * 
     * @param file
     *            the file of this audio file
     */
    public void setFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        this.filePath = file.getAbsolutePath();
        readFile();
    }
    
    /**
     * Sets the stars.
     * 
     * @param stars
     *            the stars to set
     */
    @Override
    public void setStars(int stars) {
        this.stars = stars;
    }

    /**
     * Sets the tag.
     * 
     * @param tag
     *            the new tag
     */
    public void setTag(ITag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return filePath;
    }

    @Override
    public boolean isSeekable() {
        return true;
    }

    @Override
    public int compareTo(ILocalAudioObject o) {
        if (getFile() == null || o.getFile() == null) {
            return 0;
        }
        return getFile().compareTo(o.getFile());
    }

    @Override
    public int getDiscNumber() {
        if (tag != null && tag.getDiscNumber() >= 1) {
            return tag.getDiscNumber();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.atunes.model.IAudioObject#getImage(net.sourceforge.atunes.model.ImageSize, net.sourceforge.atunes.model.IOSManager)
     */
    @Override
    public ImageIcon getImage(ImageSize imageSize, IOSManager osManager) {
        ImageIcon result = null;

        result = ImageCache.getImageCache().retrieveImage(this, imageSize);

        if (result == null) {
            result = AudioFilePictureUtils.getInsidePicture(this, imageSize.getSize(), imageSize.getSize());
            if (result == null) {
                result = AudioFilePictureUtils.getExternalPicture(this, imageSize.getSize(), imageSize.getSize(), osManager);
            }
        } else {
            return result;
        }

        if (result != null) {
        	ImageCache.getImageCache().storeImage(this, imageSize, result);
        }

        return result;
    }

    /**
     * Sets duration
     * @param duration
     */
    public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @param bitrate the bitrate to set
	 */
	public void setBitrate(long bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	@Override
	public String getAudioObjectDescription() {
		return StringUtils.getString(getTitleOrFileName(), " - ", getArtist(), " (", StringUtils.seconds2String(getDuration()), ")");
	}
}
