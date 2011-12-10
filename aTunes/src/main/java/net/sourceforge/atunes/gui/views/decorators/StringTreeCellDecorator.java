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

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.utils.I18nUtils;

public class StringTreeCellDecorator extends AbstractTreeCellDecorator<JLabel, String> {
	
	private CachedIconFactory artistImageIcon;
	
	private CachedIconFactory albumSmallIcon;
	
	private CachedIconFactory audioFileSmallIcon;
	
	private CachedIconFactory deviceIcon;
	
	private CachedIconFactory favoriteIcon;
	
	private CachedIconFactory folderIcon;
	
	private CachedIconFactory rssSmallIcon;
	
	private CachedIconFactory radioSmallIcon;
	
	/**
	 * @param radioSmallIcon
	 */
	public void setRadioSmallIcon(CachedIconFactory radioSmallIcon) {
		this.radioSmallIcon = radioSmallIcon;
	}
	
	/**
	 * @param rssSmallIcon
	 */
	public void setRssSmallIcon(CachedIconFactory rssSmallIcon) {
		this.rssSmallIcon = rssSmallIcon;
	}
	
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
    public Component decorateTreeCellComponent(JLabel component, String userObject, boolean isSelected) {
    	Color color = getLookAndFeel().getPaintForColorMutableIcon(component, isSelected);
    	component.setIcon(getIcon(userObject, component, color).getIcon(color));
    	component.setToolTipText(null);
        return component;
    }

	/**
	 * Returns icon to use depending on text
	 * @param text
	 * @param label
	 * @param color
	 * @return 
	 */
	private CachedIconFactory getIcon(String text, JLabel label, Color color) {
		if (text.equals(I18nUtils.getString("REPOSITORY"))) {
		    return audioFileSmallIcon;
		} else if (text.equals(I18nUtils.getString("DEVICE"))) {
		    return deviceIcon;
		} else if (text.equals(I18nUtils.getString("ARTISTS"))) {
		    return artistImageIcon;
		} else if (text.equals(I18nUtils.getString("ALBUMS"))) {
		    return albumSmallIcon;
		} else if (text.equals(I18nUtils.getString("SONGS"))) {
		    return audioFileSmallIcon;
		} else if (text.equals(I18nUtils.getString("FAVORITES"))) {
		    return favoriteIcon;
		} else if (text.equals(I18nUtils.getString("PODCAST_FEEDS"))) {
		    return rssSmallIcon;
		} else if (text.equals(I18nUtils.getString("RADIO"))) {
		    return radioSmallIcon;
		} else {
		    // For radio view
		    return folderIcon;
		}
	}
}
