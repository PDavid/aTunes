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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.ScrollableFlowPanel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

public class ArtistAlbumsFlowContent extends AbstractContextPanelContent {

    private static final class CoverMouseAdapter extends MouseAdapter {
        private final IAlbumInfo album;
        private final JLabel coverLabel;

        private CoverMouseAdapter(IAlbumInfo album, JLabel coverLabel) {
            this.album = album;
            this.coverLabel = coverLabel;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            coverLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            coverLabel.setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            DesktopUtils.openURL(album.getUrl());
        }
    }

    private ScrollableFlowPanel coversPanel;

    @Override
    protected Component getComponent() {
        coversPanel = new ScrollableFlowPanel();
        coversPanel.setOpaque(false);
        coversPanel.setLayout(new FlowLayout());
        return coversPanel;
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("ALBUMS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(IAudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ArtistInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        parameters.put(ArtistInfoDataSource.INPUT_ALBUMS, true);
        return parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result != null && result.containsKey(ArtistInfoDataSource.OUTPUT_ALBUMS)) {
            List<IAlbumInfo> albums = (List<IAlbumInfo>) result.get(ArtistInfoDataSource.OUTPUT_ALBUMS);
            for (IAlbumInfo album : albums) {
                coversPanel.add(getLabelForAlbum(album));
            }
            coversPanel.revalidate();
            coversPanel.repaint();
            coversPanel.validate();
        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        coversPanel.removeAll();
    };

    /**
     * Gets the Label for album.
     * 
     * @param album
     *            the album
     * @return the label for album
     */
    JLabel getLabelForAlbum(final IAlbumInfo album) {
        final JLabel coverLabel = new JLabel(album.getCover());
        coverLabel.setToolTipText(album.getTitle());
        if (album.getCover() == null) {
            coverLabel.setPreferredSize(new Dimension(Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
            coverLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        } else {
            coverLabel.setBorder(new DropShadowBorder());
        }

        coverLabel.addMouseListener(new CoverMouseAdapter(album, coverLabel));

        return coverLabel;
    }

}
