/*
 * aTunes 2.0.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.utils.I18nUtils;

public class StringTreeCellDecorator extends TreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof String) {
            String text = ((String) userObject);
            if (text.equals(I18nUtils.getString("REPOSITORY"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.AUDIO_FILE_LITTLE));
            } else if (text.equals(I18nUtils.getString("DEVICE"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.DEVICE));
            } else if (text.equals(I18nUtils.getString("ARTISTS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.ARTIST));
            } else if (text.equals(I18nUtils.getString("ALBUMS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.ALBUM));
            } else if (text.equals(I18nUtils.getString("SONGS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.AUDIO_FILE_LITTLE));
            } else if (text.equals(I18nUtils.getString("FAVORITES"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.FAVORITE));
            } else if (text.equals(I18nUtils.getString("PODCAST_FEEDS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.RSS_LITTLE));
            } else if (text.equals(I18nUtils.getString("RADIO"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.RADIO_LITTLE));
            } else {
                // For radio view
                ((JLabel) component).setIcon(Images.getImage(Images.FOLDER));
            }

            ((JLabel) component).setToolTipText(null);
        }
        return component;
    }

}
