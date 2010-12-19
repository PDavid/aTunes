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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class AlbumInfoDataSource implements ContextInformationDataSource {

    /**
     * Input parameter
     */
    public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

    /**
     * Input parameter
     */
    public static final String INPUT_BOOLEAN_IMAGE = "IMAGE";

    /**
     * Output parameter
     */
    public static final String OUTPUT_AUDIO_OBJECT = INPUT_AUDIO_OBJECT;

    /**
     * Output parameter
     */
    public static final String OUTPUT_IMAGE = "IMAGE";

    /**
     * Output parameter
     */
    public static final String OUTPUT_ALBUM = "ALBUM";

    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            AudioObject audioObject = (AudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            result.put(OUTPUT_AUDIO_OBJECT, audioObject);
            AlbumInfo albumInfo = getAlbumInfo(audioObject);
            if (albumInfo != null) {
                result.put(OUTPUT_ALBUM, albumInfo);
                if (parameters.containsKey(INPUT_BOOLEAN_IMAGE)) {
                    result.put(OUTPUT_IMAGE, getImage(albumInfo, audioObject));
                }
            }
        }
        return result;
    }

    /**
     * Returns album information
     * 
     * @param audioObject
     * @return
     */
    private AlbumInfo getAlbumInfo(AudioObject audioObject) {
        // If possible use album artist
        String artist = audioObject.getAlbumArtist().isEmpty() ? audioObject.getArtist() : audioObject.getAlbumArtist();

        // Get album info
        AlbumInfo album = LastFmService.getInstance().getAlbum(artist, audioObject.getAlbum());

        // If album was not found try to get an album from the same artist that match
        if (album == null) {
            // Wait a second to prevent IP banning
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            List<AlbumInfo> albums = null;
            if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
                // Get 
                AlbumListInfo albumList = LastFmService.getInstance().getAlbumList(audioObject.getArtist(), ApplicationState.getInstance().isHideVariousArtistsAlbums(),
                        ApplicationState.getInstance().getMinimumSongNumberPerAlbum());
                if (albumList != null) {
                    albums = albumList.getAlbums();
                }
            }

            if (albums != null) {
                // Try to find an album which fits 
                AlbumInfo auxAlbum = null;
                int i = 0;
                while (auxAlbum == null && i < albums.size()) {
                    AlbumInfo a = albums.get(i);
                    StringTokenizer st = new StringTokenizer(a.getTitle(), " ");
                    boolean matches = true;
                    int tokensAnalyzed = 0;
                    while (st.hasMoreTokens() && matches) {
                        String t = st.nextToken();
                        if (forbiddenToken(t)) { // Ignore album if contains forbidden chars
                            matches = false;
                            break;
                        }
                        if (!validToken(t)) { // Ignore tokens without alphanumerics
                            if (tokensAnalyzed == 0 && !st.hasMoreTokens()) {
                                matches = false;
                            } else {
                                continue;
                            }
                        }
                        if (!audioObject.getAlbum().toLowerCase().contains(t.toLowerCase())) {
                            matches = false;
                        }
                        tokensAnalyzed++;
                    }
                    if (matches) {
                        auxAlbum = a;
                    }
                    i++;
                }
                if (auxAlbum != null) {
                    // Get full information for album
                    auxAlbum = LastFmService.getInstance().getAlbum(auxAlbum.getArtist(), auxAlbum.getTitle());
                    if (auxAlbum != null) {
                        album = auxAlbum;
                    }
                }
            }
        }

        return album;
        // Get image of album or custom image for audio object
    }

    /**
     * Returns image from lastfm or from custom image
     * 
     * @param albumInfo
     * @param audioObject
     * @return
     */
    private Image getImage(AlbumInfo albumInfo, AudioObject audioObject) {
        Image image = null;
        if (albumInfo != null) {
            image = LastFmService.getInstance().getImage(albumInfo);
            // This data source should only be used with audio files but anyway check if audioObject is an AudioFile before save picture
            if (audioObject instanceof AudioFile) {
                savePicture(image, (AudioFile) audioObject);
            }
        } else {
            image = audioObject.getImage(ImageSize.SIZE_MAX).getImage();
        }
        return image;
    }

    /**
     * Saves an image related to an audio file from a web service in the folder
     * where audio file is
     * 
     * @param img
     * @param file
     */
    private void savePicture(Image img, AudioFile file) {
        if (img != null && ApplicationState.getInstance().isSaveContextPicture()) { // save image in folder of file
            String imageFileName = AudioFilePictureUtils.getFileNameForCover(file);

            File imageFile = new File(imageFileName);
            if (!imageFile.exists()) {
                // Save picture
                try {
                    ImageUtils.writeImageToFile(img, imageFileName);
                    // Add picture to songs of album
                    RepositoryHandler.getInstance().addExternalPictureForAlbum(file.getArtist(), file.getAlbum(), imageFile);

                    // Update file properties panel
                    ControllerProxy.getInstance().getFilePropertiesController().refreshPicture();
                } catch (IOException e) {
                    new Logger().internalError(e);
                }
            }
        }
    }

    /**
     * Valid token.
     * 
     * @param t
     *            the t
     * 
     * @return true, if successful
     */
    private boolean validToken(String t) {
        return t.matches("[A-Za-z]+");
        //t.contains("(") || t.contains(")")
    }

    /**
     * Forbidden token.
     * 
     * @param t
     *            the t
     * 
     * @return true, if successful
     */
    private boolean forbiddenToken(String t) {
        return t.contains("/");
    }

}
