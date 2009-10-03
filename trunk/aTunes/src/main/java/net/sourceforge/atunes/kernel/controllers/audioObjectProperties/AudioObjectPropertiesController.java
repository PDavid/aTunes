/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.controllers.audioObjectProperties;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * The audio object properties controller.
 * 
 * @author fleax
 */
public class AudioObjectPropertiesController extends PanelController<AudioObjectPropertiesPanel> {

    /** The current audio object. */
    AudioObject currentAudioObject;

    /**
     * Instantiates a new audio object properties controller.
     * 
     * @param panelControlled
     *            the panel controlled
     */
    public AudioObjectPropertiesController(AudioObjectPropertiesPanel pControlled) {
        super(pControlled);
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
        getPanelControlled().getPictureLabel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VisualHandler.getInstance().showImageDialog(currentAudioObject);
            }
        });
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Clears the main panel.
     */
    public void clear() {
        currentAudioObject = null;
        getPanelControlled().getPictureLabel().setIcon(null);
        getPanelControlled().getPictureLabel().setVisible(false);
        getPanelControlled().getMainPanel().setVisible(false);
    }

    /**
     * Fill file properties.
     */
    void fillFileProperties() {
        if (currentAudioObject != null) {
            getPanelControlled().getBitrateLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("BITRATE"), ":</b>    ", currentAudioObject.getBitrate(), " Kbps"));
            getPanelControlled().getFrequencyLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("FREQUENCY"), ":</b>    ", currentAudioObject.getFrequency(), " Hz"));
        } else {
            getPanelControlled().getBitrateLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("BITRATE"), ":</b>    "));
            getPanelControlled().getFrequencyLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("FREQUENCY"), ":</b>    "));
        }

    }

    /**
     * Fill picture.
     */
    void fillPicture() {
        if (currentAudioObject != null) {
            new SwingWorker<ImageIcon, Void>() {
                boolean shadowBorder;

                @Override
                protected ImageIcon doInBackground() throws Exception {
                    ImageIcon imageForAudioObject;
                    imageForAudioObject = currentAudioObject.getImage(ImageSize.SIZE_90);
                    if (imageForAudioObject == null) {
                        imageForAudioObject = currentAudioObject.getGenericImage(GenericImageSize.MEDIUM);
                    }
                    return imageForAudioObject;
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon imageIcon = get();
                        if (imageIcon != null) {
                            getPanelControlled().getPictureLabel().setIcon(imageIcon);
                            getPanelControlled().getPictureLabel().setVisible(true);
                        } else {
                            getPanelControlled().getPictureLabel().setIcon(null);
                            getPanelControlled().getPictureLabel().setVisible(false);
                        }
                        if (shadowBorder) {
                            getPanelControlled().getPictureLabel().setBorder(new DropShadowBorder());
                        } else {
                            getPanelControlled().getPictureLabel().setBorder(BorderFactory.createEmptyBorder());
                        }
                    } catch (InterruptedException e) {
                        getLogger().internalError(e);
                    } catch (ExecutionException e) {
                        getLogger().internalError(e);
                    }
                }
            }.execute();
        }

    }

    /**
     * Returns super logger
     */
    @Override
    protected Logger getLogger() {
        return super.getLogger();
    }

    /**
     * Fill song properties.
     */
    void fillSongProperties() {
        if (currentAudioObject != null) {

            // \u202D is Unicode symbol for orientation override to LTR. 
            getPanelControlled().getUrlLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("FILE"), ":</b>    ", "\u202D", currentAudioObject.getUrl(), " \u202C </html>"));

            getPanelControlled().getTitleLabel().setText(
                    StringUtils.getString("<html><b>", I18nUtils.getString("SONG"), ":</b>    ", currentAudioObject.getTitleOrFileName(), " - ", currentAudioObject.getArtist(),
                            " - ", currentAudioObject.getAlbum(), "\u202D (", StringUtils.seconds2String(currentAudioObject.getDuration()), ") \u202C </html>"));

            if (currentAudioObject.getTrackNumber() > 0) {
                getPanelControlled().getTrackLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("TRACK"), ":</b>    ", currentAudioObject.getTrackNumber()));
            } else {
                getPanelControlled().getTrackLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("TRACK"), ":"));
            }

            if (currentAudioObject.getYear().isEmpty()) {
                getPanelControlled().getYearLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("YEAR"), ":</b>    ", currentAudioObject.getYear()));
            } else {
                getPanelControlled().getYearLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("YEAR"), ":"));
            }

            if (currentAudioObject.getDiscNumber() > 0) {
                getPanelControlled().getDiscNumberLabel().setText(
                        StringUtils.getString("<html><b>", I18nUtils.getString("DISC_NUMBER"), ":</b>    ", currentAudioObject.getDiscNumber()));
            } else {
                getPanelControlled().getDiscNumberLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("DISC_NUMBER"), ":"));
            }

            getPanelControlled().getGenreLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("GENRE"), ":</b>    ", currentAudioObject.getGenre()));

            // Favorite icons
            refreshFavoriteIcons();
        } else {
            getPanelControlled().getUrlLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("FILE"), ":</b>    "));
            getPanelControlled().getTitleLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("SONG"), ":</b>    "));
            getPanelControlled().getTrackLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("TRACK"), ":</b>    "));
            getPanelControlled().getYearLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("YEAR"), ":</b>    "));
            getPanelControlled().getGenreLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("GENRE"), ":</b>    "));
            getPanelControlled().getDiscNumberLabel().setText(StringUtils.getString("<html><b>", I18nUtils.getString("DISC_NUMBER"), ":</b>    "));
        }
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Only show properties panel.
     */
    public void onlyShowPropertiesPanel() {
        currentAudioObject = null;
        // Song properties
        fillSongProperties();
        // File Properties
        fillFileProperties();
        // Picture
        fillPicture();
        getPanelControlled().getMainPanel().setVisible(true);
    }

    /**
     * Refresh favorite icons.
     */
    public void refreshFavoriteIcons() {
        if (currentAudioObject != null) {
            boolean favorite = FavoritesHandler.getInstance().getFavoriteSongsInfo().containsValue(currentAudioObject)
                    || FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsKey(currentAudioObject.getArtist())
                    || FavoritesHandler.getInstance().getFavoriteAlbumsInfo().containsKey(currentAudioObject.getAlbum());

            getPanelControlled().getTitleLabel().setIcon(favorite ? ImageLoader.getImage(ImageLoader.FAVORITE) : null);
        }
    }

    /**
     * Refresh picture.
     */
    public void refreshPicture() {
        fillPicture();
    }

    /**
     * Update values.
     * 
     * @param audioObject
     *            the audio object that should be shown in the audio object
     *            properties panel
     */
    public void updateValues(AudioObject audioObject) {
        if (audioObject != null) {
            getLogger().debug(LogCategories.CONTROLLER, audioObject.getUrl());
            currentAudioObject = audioObject;
            // Song properties
            fillSongProperties();
            // File Properties
            fillFileProperties();
            // Picture
            fillPicture();
            getPanelControlled().getMainPanel().setVisible(true);
        } else {
            clear();
        }
    }

    @Override
    protected AudioObjectPropertiesPanel getPanelControlled() {
        return super.getPanelControlled();
    }

}
