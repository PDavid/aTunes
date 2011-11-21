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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.AudioFileImageMediumIcon;
import net.sourceforge.atunes.gui.images.DeviceImageIcon;
import net.sourceforge.atunes.gui.images.FavoriteImageIcon;
import net.sourceforge.atunes.gui.images.FolderImageIcon;
import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.utils.I18nUtils;

public class StringTreeCellDecorator extends AbstractTreeCellDecorator {
	
	private CachedIconFactory artistImageIcon;
	
	private CachedIconFactory albumSmallIcon;
	
	private CachedIconFactory audioFileSmallIcon;
	
	private CachedIconFactory deviceIcon;
	
	private CachedIconFactory favoriteIcon;
	
	private CachedIconFactory folderIcon;
	
	/**
	 * @param folderIcon
	 */
	public void setFolderIcon(CachedIconFactory folderIcon) {
		this.folderIcon = folderIcon;
	}
	
	/**
	 * @param favoriteIcon
	 */
	public void setFavoriteIcon(CachedIconFactory favoriteIcon) {
		this.favoriteIcon = favoriteIcon;
	}
	
	/**
	 * @param deviceIcon
	 */
	public void setDeviceIcon(CachedIconFactory deviceIcon) {
		this.deviceIcon = deviceIcon;
	}
	
	/**
	 * @param audioFileSmallIcon
	 */
	public void setAudioFileSmallIcon(CachedIconFactory audioFileSmallIcon) {
		this.audioFileSmallIcon = audioFileSmallIcon;
	}
	
	/**
	 * @param albumSmallIcon
	 */
	public void setAlbumSmallIcon(CachedIconFactory albumSmallIcon) {
		this.albumSmallIcon = albumSmallIcon;
	}
	
	/**
	 * @param artistImageIcon
	 */
	public void setArtistImageIcon(CachedIconFactory artistImageIcon) {
		this.artistImageIcon = artistImageIcon;
	}

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject, boolean isSelected) {
        if (userObject instanceof String && component instanceof JLabel) {
            String text = ((String) userObject);
            JLabel label = (JLabel) component;
            Color color = getLookAndFeel().getPaintForColorMutableIcon(label, isSelected);
            if (text.equals(I18nUtils.getString("REPOSITORY"))) {
                label.setIcon(audioFileSmallIcon.getIcon(color));
            } else if (text.equals(I18nUtils.getString("DEVICE"))) {
                label.setIcon(deviceIcon.getIcon(color));
            } else if (text.equals(I18nUtils.getString("ARTISTS"))) {
                label.setIcon(artistImageIcon.getIcon(color));
            } else if (text.equals(I18nUtils.getString("ALBUMS"))) {
                label.setIcon(albumSmallIcon.getIcon(color));
            } else if (text.equals(I18nUtils.getString("SONGS"))) {
                label.setIcon(audioFileSmallIcon.getIcon(color));
            } else if (text.equals(I18nUtils.getString("FAVORITES"))) {
                label.setIcon(favoriteIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected)));
            } else if (text.equals(I18nUtils.getString("PODCAST_FEEDS"))) {
                label.setIcon(RssImageIcon.getSmallIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected), getLookAndFeel()));
            } else if (text.equals(I18nUtils.getString("RADIO"))) {
                label.setIcon(RadioImageIcon.getSmallIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected), getLookAndFeel()));
            } else {
                // For radio view
                label.setIcon(folderIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(label, isSelected)));
            }

            label.setToolTipText(null);
        }
        return component;
    }

}
