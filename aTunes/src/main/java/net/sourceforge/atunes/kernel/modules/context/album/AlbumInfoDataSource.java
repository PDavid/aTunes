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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.atunes.kernel.modules.context.ContextInformationDataSource;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;

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

    private IState state;
    
    private IWebServicesHandler webServicesHandler;
    
    private IOSManager osManager;
    
    @Override
    public Map<String, ?> getData(Map<String, ?> parameters) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (parameters.containsKey(INPUT_AUDIO_OBJECT)) {
            IAudioObject audioObject = (IAudioObject) parameters.get(INPUT_AUDIO_OBJECT);
            result.put(OUTPUT_AUDIO_OBJECT, audioObject);
            IAlbumInfo albumInfo = getAlbumInfo(audioObject);
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
    private IAlbumInfo getAlbumInfo(IAudioObject audioObject) {
        // If possible use album artist
        String artist = audioObject.getAlbumArtist().isEmpty() ? audioObject.getArtist() : audioObject.getAlbumArtist();

        // Get album info
        IAlbumInfo album = webServicesHandler.getAlbum(artist, audioObject.getAlbum());

        // If album was not found try to get an album from the same artist that match
        if (album == null) {
            // Wait a second to prevent IP banning
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            List<IAlbumInfo> albums = null;
            if (!audioObject.getArtist().equals(I18nUtils.getString("UNKNOWN_ARTIST"))) {
                // Get 
                IAlbumListInfo albumList = webServicesHandler.getAlbumList(audioObject.getArtist(), state.isHideVariousArtistsAlbums(),
                        state.getMinimumSongNumberPerAlbum());
                if (albumList != null) {
                    albums = albumList.getAlbums();
                }
            }

            if (albums != null) {
                // Try to find an album which fits 
                IAlbumInfo auxAlbum = null;
                int i = 0;
                while (auxAlbum == null && i < albums.size()) {
                    IAlbumInfo a = albums.get(i);
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
                    auxAlbum = webServicesHandler.getAlbum(auxAlbum.getArtist(), auxAlbum.getTitle());
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
    private Image getImage(IAlbumInfo albumInfo, IAudioObject audioObject) {
        Image image = null;
        if (albumInfo != null) {
            image = webServicesHandler.getAlbumImage(albumInfo);
            // This data source should only be used with audio files but anyway check if audioObject is an LocalAudioObject before save picture
            if (audioObject instanceof ILocalAudioObject) {
                savePicture(image, (ILocalAudioObject) audioObject);
            }
        } else {
            image = audioObject.getImage(ImageSize.SIZE_MAX, osManager).getImage();
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
    private void savePicture(Image img, ILocalAudioObject file) {
        if (img != null && state.isSaveContextPicture()) { // save image in folder of file
            String imageFileName = AudioFilePictureUtils.getFileNameForCover(file, osManager);

            File imageFile = new File(imageFileName);
            if (!imageFile.exists()) {
                // Save picture
                try {
                    ImageUtils.writeImageToFile(img, imageFileName);
                    // Add picture to songs of album
                    RepositoryHandler.getInstance().addExternalPictureForAlbum(file.getArtist(), file.getAlbum(), imageFile);
                } catch (IOException e) {
                    Logger.error(e);
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
    
    public final void setState(IState state) {
		this.state = state;
	}
    
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    public final void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

}
