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
package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.ImageInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

/**
 * Class for writing tags to an audio file. We only use JAudiotagger for it. In
 * general, for writing a complete tag, call setInfo.
 * 
 * In general all methods in this class should test if audio file is writable.
 * This is done calling method AudioFile.setWritable which sets write
 * permissions to underlying file if necessary.
 * 
 * @author sylvain
 */
public class TagModifier {

    /** The logger. */
    private static Logger logger = new Logger();

    /**
     * Delete tags.
     * 
     * @param file
     *            the file
     */
    static void deleteTags(AudioFile file) {
        // Be sure file is writable before setting info
        file.setWritable();
        try {
            org.jaudiotagger.audio.AudioFileIO.delete(org.jaudiotagger.audio.AudioFileIO.read(file.getFile()));
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, e);
        }

    }

    /**
     * Refresh after tag modify.
     * 
     * @param audioFilesEditing
     *            the audio files editing
     */
    static void refreshAfterTagModify(final List<AudioFile> audioFilesEditing) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // update Swing components if necessary
                boolean playListContainsRefreshedFile = false;
                for (int i = 0; i < audioFilesEditing.size(); i++) {
                    if (PlayListHandler.getInstance().getCurrentPlayList(true).contains(audioFilesEditing.get(i))) {
                        playListContainsRefreshedFile = true;
                    }

                    // Changed current playing song
                    if (PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList() != null
                            && PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList().equals(audioFilesEditing.get(i))) {
                        PlayListHandler.getInstance().selectedAudioObjectChanged(audioFilesEditing.get(i));

                        if (PlayerHandler.getInstance().isEnginePlaying()) {
                            VisualHandler.getInstance().updateTitleBar(audioFilesEditing.get(i));
                        }
                    }
                }
                if (playListContainsRefreshedFile) {
                    ControllerProxy.getInstance().getPlayListController().refreshPlayList();
                }
                ControllerProxy.getInstance().getNavigationController().notifyReload();
            }
        });
    }

    /**
     * Writes album to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param album
     *            Album of file
     */
    static void setAlbum(AudioFile file, String album) {
        // Be sure file is writable before setting info
        file.setWritable();
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setAlbum(album);
            audioFile.commit();
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        }
    }

    /**
     * Writes genre to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param genre
     *            Genre of file
     */
    static void setGenre(AudioFile file, String genre) {
        // Be sure file is writable before setting info
        file.setWritable();
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            //newTag.setGenre(genre);
            newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.GENRE, genre));
            audioFile.commit();
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        }
    }

    /**
     * Writes tag to audiofile using JAudiotagger.
     * 
     * @param file
     *            File to which the tags should be written
     * @param tag
     *            Tag to be written
     */
    public static void setInfo(AudioFile file, Tag tag) {
        setInfo(file, tag, false, null);
    }

    /**
     * Writes tag to audio file using JAudiotagger.
     * 
     * @param file
     *            File to which the tags should be written
     * @param tag
     *            Tag to be written
     * @param shouldEditCover
     *            if the cover should be edited
     * @param cover
     *            cover data as byte array
     */
    static void setInfo(AudioFile file, Tag tag, boolean shouldEditCover, byte[] cover) {
        // Be sure file is writable before setting info
        file.setWritable();

        String title = tag.getTitle() != null ? tag.getTitle() : "";
        String album = tag.getAlbum() != null ? tag.getAlbum() : "";
        String artist = tag.getArtist() != null ? tag.getArtist() : "";
        int year = tag.getYear();
        String comment = tag.getComment() != null ? tag.getComment() : "";
        String genre = tag.getGenre() != null ? tag.getGenre() : "";
        String lyrics = tag.getLyrics() != null ? tag.getLyrics() : "";
        String composer = tag.getComposer() != null ? tag.getComposer() : "";
        int track = tag.getTrackNumber();
        int discNumber = tag.getDiscNumber();
        String albumArtist = tag.getAlbumArtist() != null ? tag.getAlbumArtist() : "";

        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();

            if (AudioFile.isMp3File(file.getFile())) {
                org.jaudiotagger.audio.mp3.MP3File mp3file = (org.jaudiotagger.audio.mp3.MP3File) audioFile;
                if (mp3file.hasID3v1Tag() && !mp3file.hasID3v2Tag()) {
                    deleteTags(file);
                    audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
                    newTag = audioFile.getTagOrCreateAndSetDefault();
                }
            }

            if (shouldEditCover) {
                // Remove old images
                try {
                    newTag.deleteArtworkField();
                } catch (KeyNotFoundException e) {
                    logger.error(LogCategories.IMAGE, e);
                }

                if (cover != null) {
                    Artwork artwork = new Artwork();
                    artwork.setBinaryData(cover);
                    artwork.setDescription("cover");
                    artwork.setPictureType(PictureTypes.DEFAULT_ID);
                    artwork.setLinked(false);
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setInput(new ByteArrayInputStream(cover));
                    artwork.setMimeType(imageInfo.getMimeType());
                    newTag.createAndSetArtworkField(artwork);
                }
            }

            // Workaround for mp4 files - strings outside genre list might not be written otherwise
            if (AudioFile.isMp4File(file.getFile())) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.GENRE);
            }

            // Delete tag field if value is empty
            if (album.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.ALBUM);
            } else {
                newTag.setAlbum(album);
            }
            if (artist.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.ARTIST);
            } else {
                newTag.setArtist(artist);
            }
            if (comment.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.COMMENT);
            } else {
                newTag.setComment(comment);
            }
            if (genre.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.GENRE);
            } else {
                newTag.setGenre(genre);
            }
            if (title.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.TITLE);
            } else {
                newTag.setTitle(title);
            }
            if (year == -1) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.YEAR);
            } else {
                newTag.setYear(Integer.toString(year));
            }
            if (track == -1) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.TRACK);
            } else {
                newTag.setTrack(Integer.toString(track));
            }
            if (discNumber == 0) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.DISC_NO);
            } else {
                newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.DISC_NO, Integer.toString(discNumber)));
            }
            if (lyrics.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.LYRICS);
            } else {
                newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.LYRICS, lyrics));
            }
            if (albumArtist.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.ALBUM_ARTIST);
            } else {
                newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.ALBUM_ARTIST, albumArtist));
            }
            if (composer.isEmpty()) {
                newTag.deleteTagField(org.jaudiotagger.tag.TagFieldKey.COMPOSER);
            } else {
                newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.COMPOSER, composer));
            }

            audioFile.setTag(newTag);
            audioFile.commit();
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        }

    }

    /**
     * Sets the lyrics.
     * 
     * @param file
     *            the file
     * @param lyrics
     *            the lyrics
     */
    static void setLyrics(AudioFile file, String lyrics) {
        // Be sure file is writable before setting info
        file.setWritable();
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.set(newTag.createTagField(org.jaudiotagger.tag.TagFieldKey.LYRICS, lyrics));
            audioFile.commit();
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        }
    }

    /**
     * Writes title name to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param newTitle
     *            New title
     */
    static void setTitles(AudioFile file, String newTitle) {
        // Be sure file is writable before setting info
        file.setWritable();
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setTitle(newTitle);
            audioFile.commit();
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        }
    }

    /**
     * Sets track number on a file.
     * 
     * @param file
     *            File to which the tag should be written
     * @param track
     *            Track number
     */
    static void setTrackNumber(AudioFile file, Integer track) {
        // Be sure file is writable before setting info
        file.setWritable();
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setTrack(track.toString());
            audioFile.commit();
        } catch (Exception e) {
            logger.error(LogCategories.FILE_WRITE, StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        }
    }

}
