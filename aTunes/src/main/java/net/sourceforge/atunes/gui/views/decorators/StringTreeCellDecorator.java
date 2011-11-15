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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;
import java.awt.Paint;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.AlbumImageIcon;
import net.sourceforge.atunes.gui.images.ArtistImageIcon;
import net.sourceforge.atunes.gui.images.AudioFileImageIcon;
import net.sourceforge.atunes.gui.images.DeviceImageIcon;
import net.sourceforge.atunes.gui.images.FavoriteImageIcon;
import net.sourceforge.atunes.gui.images.FolderImageIcon;
import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.utils.I18nUtils;

public class StringTreeCellDecorator extends AbstractTreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject, boolean isSelected) {
        if (userObject instanceof String && component instanceof JLabel) {
            String text = ((String) userObject);
            JLabel label = (JLabel) component;
            Paint color = getLookAndFeel().getPaintForColorMutableIcon(label, isSelected);
            if (text.equals(I18nUtils.getString("REPOSITORY"))) {
                label.setIcon(AudioFileImageIcon.getSmallImageIcon(color, getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("DEVICE"))) {
                label.setIcon(DeviceImageIcon.getIcon(color, getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("ARTISTS"))) {
                label.setIcon(ArtistImageIcon.getIcon(color, getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("ALBUMS"))) {
                label.setIcon(AlbumImageIcon.getIcon(color, getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("SONGS"))) {
                label.setIcon(AudioFileImageIcon.getSmallImageIcon(color, getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("FAVORITES"))) {
                label.setIcon(FavoriteImageIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected), getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("PODCAST_FEEDS"))) {
                label.setIcon(RssImageIcon.getSmallIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected), getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("RADIO"))) {
                label.setIcon(RadioImageIcon.getSmallIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected), getLookAndFeel()));
            } else {
                // For radio view
                label.setIcon(FolderImageIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected), getLookAndFeel()));
            }

            label.setToolTipText(null);
        }
        return component;
    }

}
