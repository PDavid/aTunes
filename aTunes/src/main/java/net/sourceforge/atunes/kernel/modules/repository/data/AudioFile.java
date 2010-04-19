/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.repository.ImageCache;
import net.sourceforge.atunes.kernel.modules.repository.tags.reader.TagDetector;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.AbstractTag;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

import org.commonjukebox.plugins.PluginApi;
import org.jaudiotagger.audio.AudioFileIO;

/**
 * AudioFile class initializes audio files so that tags and audio information
 * can be retrieved by the tagging library. Provides information about bitrate,
 * duration and frequency of the audio file. Provides tag information.
 * 
 * @author fleax
 */
@PluginApi
public final class AudioFile implements AudioObject, Serializable, Comparable<AudioFile> {

    private static final long serialVersionUID = -1139001443603556703L;

    private static ImageCache imageCache = new ImageCache();

    private AbstractTag tag;
    private List<File> externalPictures;
    private int duration;
    private long bitrate;
    private int frequency;
    private long readTime;
    private int stars;
    /** The file on disk. */
    private File file;

    /**
     * Instantiates a new audio file.
     * 
     * @param fileName
     *            the file name
     */
    public AudioFile(String fileName) {
        readFile(new File(fileName));
    }

    /**
     * Reads a file
     * 
     * @param file
     *            the file
     */
    private void readFile(File file) {
        this.file = file;
        // Don't read from formats not supported by Jaudiotagger
        if (!isValidAudioFile(file, Format.APE, Format.MPC)) {
            introspectTags();
            readAudioProperties();
        }
        this.readTime = System.currentTimeMillis();
    }

    /**
     * Gets the audio files.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the audio files
     */
    public static List<AudioFile> getAudioFiles(List<AudioObject> audioObjects) {
        if (audioObjects == null) {
            return Collections.emptyList();
        }
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (AudioObject audioObject : audioObjects) {
            if (audioObject instanceof AudioFile) {
                result.add((AudioFile) audioObject);
            }
        }
        return result;
    }

    /**
     * Returns a list of audio objects for the given list of audio files
     * 
     * @param audioFiles
     * @return
     */
    public static List<AudioObject> getAudioObjects(List<AudioFile> audioFiles) {
        if (audioFiles == null) {
            return Collections.emptyList();
        }
        return new ArrayList<AudioObject>(audioFiles);
    }

    /**
     * Gets the new tag.
     * 
     * @param file
     *            the file
     * @param editTagInfo
     *            the edit tag info
     * 
     * @return the new tag
     */
    public static AbstractTag getNewTag(AudioFile file, EditTagInfo editTagInfo) {
        return new DefaultTag().getTagFromProperties(editTagInfo, file.getTag());
    }

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    public static boolean isValidAudioFile(File file) {
        return !file.isDirectory()
                && isValidAudioFile(file, Format.MP3, Format.OGG, Format.MP4_1, Format.MP4_2, Format.WAV, Format.WMA, Format.FLAC, Format.REAL_1, Format.REAL_2, Format.APE,
                        Format.MPC);
    }

    /**
     * Checks if a file is a valid audio file
     * 
     * @param file
     *            the file to check
     * @param formats
     *            the allowed formats
     * @return if the file is a valid audio file
     */
    public static boolean isValidAudioFile(File file, Format... formats) {
        String path = file.getAbsolutePath().toLowerCase();
        for (Format format : formats) {
            if (path.endsWith(format.getExtension())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    public static boolean isValidAudioFile(String file) {
        File f = new File(file);
        return f.exists() && isValidAudioFile(f);
    }

    /**
     * Read audio properties.
     * 
     * @param audioFile
     *            the audio file
     */
    private void readAudioProperties() {
        try {
            org.jaudiotagger.audio.AudioFile af = AudioFileIO.read(this.getFile());
            duration = af.getAudioHeader().getTrackLength();
            bitrate = af.getAudioHeader().getBitRateAsNumber();
            frequency = af.getAudioHeader().getSampleRateAsNumber();
        } catch (Exception e) {
            new Logger().error(LogCategories.FILE_READ, e.getMessage());
        }
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
        if (tag != null && tag.getAlbumArtist() != null) {
            albumArtist = tag.getAlbumArtist();
        } else {
            albumArtist = "";
        }
        return albumArtist;
    }

    @Override
    public String getAlbumArtistOrArtist() {
        return getAlbumArtist().isEmpty() ? getArtist() : getAlbumArtist();
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
    public File getFile() {
        return file;
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
    public String getNameWithoutExtension() {
        if (file == null) {
            return null;
        }
        if (file.getName().indexOf('.') != -1) {
            return file.getName().substring(0, file.getName().lastIndexOf('.'));
        }
        return file.getName();
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
    public AbstractTag getTag() {
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
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    @Override
    public String getYear() {
        if (tag != null && tag.getYear() > 0) {
            return Integer.toString(tag.getYear());
        }
        return "";
    }

    @Override
    public Date getDate() {
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
    public final boolean hasInternalPicture() {
        return tag != null && tag.hasInternalImage();
    }

    /**
     * Checks if the tag of this audio file does support internal images
     * 
     * @return if the tag of this audio file does support internal images
     */
    public final boolean supportsInternalPicture() {
        return isValidAudioFile(getFile(), Format.FLAC, Format.MP3, Format.MP4_1, Format.MP4_2, Format.OGG, Format.WMA);
    }

    /**
     * Introspect tags. Get the tag for the file.
     */
    private void introspectTags() {
        TagDetector.getTags(this);
    }

    /**
     * Checks if is up to date.
     * 
     * @return true, if is up to date
     */
    public boolean isUpToDate() {
        if (file == null) {
            return false;
        }
        return readTime > file.lastModified();
    }

    /**
     * Refresh tag.
     */
    public void refreshTag() {
        deleteTags();
        introspectTags();
        readTime = System.currentTimeMillis();
    }

    /**
     * Sets the external pictures.
     * 
     * @param externalPictures
     *            the new external pictures
     */
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
        readFile(file);
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
    public void setTag(AbstractTag tag) {
        this.tag = tag;
    }

    /**
     * Sets write permissions if is not writable.
     */
    public void setWritable() {
        // Set write permission on file
        if (!file.canWrite()) {
            file.setWritable(true);
        }
        // Set write permission on parent
        if (!file.getParentFile().canWrite()) {
            file.getParentFile().setWritable(true);
        }
    }

    @Override
    public String toString() {
        return file.getName();
    }

    @Override
    public boolean isSeekable() {
        return true;
    }

    @Override
    public int compareTo(AudioFile o) {
        if (file == null || o.getFile() == null) {
            return 0;
        }
        return file.compareTo(o.getFile());
    }

    @Override
    public int getDiscNumber() {
        if (tag != null && tag.getDiscNumber() >= 1) {
            return tag.getDiscNumber();
        }
        return 0;
    }

    /**
     * Returns a list where there are no repeated songs (same title and artist)
     * 
     * @param list
     * @return
     */
    public static List<AudioFile> filterRepeatedSongs(List<AudioFile> list) {
        List<AudioFile> result = new ArrayList<AudioFile>(list);
        HashSet<Integer> artistAndTitles = new HashSet<Integer>();
        for (AudioFile af : list) {
            // Build a set of strings of type artist_hash * title_hash
            Integer hash = (!af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist()).hashCode() * af.getTitle().hashCode();
            if (artistAndTitles.contains(hash)) {
                // Repeated artist + title, remove from result list
                result.remove(af);
            } else {
                artistAndTitles.add(hash);
            }
        }
        return result;
    }

    /**
     * Returns a list where there are no repeated songs (same title and album
     * and artist)
     * 
     * @param list
     * @return
     */
    public static List<AudioFile> filterRepeatedSongsAndAlbums(List<AudioFile> list) {
        List<AudioFile> result = new ArrayList<AudioFile>(list);
        Set<Integer> artistAndTitles = new HashSet<Integer>();
        for (AudioFile af : list) {
            // Build a set of strings of type artist_hash * album_hash * title_hash
            Integer hash = (!af.getAlbumArtist().trim().equals("") ? af.getAlbumArtist() : af.getArtist()).hashCode() * af.getAlbum().hashCode() * af.getTitle().hashCode();
            if (artistAndTitles.contains(hash)) {
                // Repeated artist + album + title, remove from result list
                result.remove(af);
            } else {
                artistAndTitles.add(hash);
            }
        }
        return result;
    }

    /**
     * Returns an image associated to an audio file, with following order: - If
     * not, find an internal image - If not, find an external image - If not,
     * return null
     * 
     * @param width
     *            Width in pixels or -1 to keep original width
     * @param height
     *            Height in pixels or -1 to keep original height
     * 
     * @return the image for audio file
     */
    @Override
    public ImageIcon getImage(ImageSize imageSize) {
        ImageIcon result = null;

        result = imageCache.retrieveImage(this, imageSize);

        if (result == null) {
            result = AudioFilePictureUtils.getInsidePicture(this, imageSize.getSize(), imageSize.getSize());
            if (result == null) {
                result = AudioFilePictureUtils.getExternalPicture(this, imageSize.getSize(), imageSize.getSize());
            }
        } else {
            return result;
        }

        if (result != null) {
            imageCache.storeImage(this, imageSize, result);
        }

        return result;
    }

    @Override
    public ImageIcon getGenericImage(GenericImageSize imageSize) {
        switch (imageSize) {
        case SMALL: {
            return Images.getImage(Images.AUDIO_FILE_LITTLE);
        }
        case MEDIUM: {
            return Images.getImage(Images.AUDIO_FILE);
        }
        case BIG: {
            return Images.getImage(Images.AUDIO_FILE_BIG);
        }
        default: {
            throw new IllegalArgumentException("unknown image size");
        }
        }
    }

    public static ImageCache getImageCache() {
        return imageCache;
    }

}
