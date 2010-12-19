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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.utils.I18nUtils;

public class StringTreeCellDecorator extends AbstractTreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof String && component instanceof JLabel) {
            String text = ((String) userObject);
            JLabel label = (JLabel) component;
            if (text.equals(I18nUtils.getString("REPOSITORY"))) {
                label.setIcon(Images.getImage(Images.AUDIO_FILE_LITTLE));
            } else if (text.equals(I18nUtils.getString("DEVICE"))) {
                label.setIcon(Images.getImage(Images.DEVICE));
            } else if (text.equals(I18nUtils.getString("ARTISTS"))) {
                label.setIcon(Images.getImage(Images.ARTIST));
            } else if (text.equals(I18nUtils.getString("ALBUMS"))) {
                label.setIcon(Images.getImage(Images.ALBUM));
            } else if (text.equals(I18nUtils.getString("SONGS"))) {
                label.setIcon(Images.getImage(Images.AUDIO_FILE_LITTLE));
            } else if (text.equals(I18nUtils.getString("FAVORITES"))) {
                label.setIcon(Images.getImage(Images.FAVORITE));
            } else if (text.equals(I18nUtils.getString("PODCAST_FEEDS"))) {
                label.setIcon(Images.getImage(Images.RSS_LITTLE));
            } else if (text.equals(I18nUtils.getString("RADIO"))) {
                label.setIcon(Images.getImage(Images.RADIO_LITTLE));
            } else {
                // For radio view
                label.setIcon(Images.getImage(Images.FOLDER));
            }

            label.setToolTipText(null);
        }
        return component;
    }

}
