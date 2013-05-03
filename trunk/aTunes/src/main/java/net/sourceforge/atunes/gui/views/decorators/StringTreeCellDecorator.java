/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Tree cell decorator for strings
 * 
 * @author alex
 * 
 */
public class StringTreeCellDecorator extends
		AbstractTreeCellDecorator<JLabel, String> {

	private IIconFactory artistImageIcon;

	private IIconFactory albumSmallIcon;

	private IIconFactory audioFileSmallIcon;

	private IIconFactory folderIcon;

	private Map<String, IIconFactory> iconsByString;

	/**
	 * @param folderIcon
	 */
	public void setFolderIcon(final IIconFactory folderIcon) {
		this.folderIcon = folderIcon;
	}

	/**
	 * @param audioFileSmallIcon
	 */
	public void setAudioFileSmallIcon(final IIconFactory audioFileSmallIcon) {
		this.audioFileSmallIcon = audioFileSmallIcon;
	}

	/**
	 * @param albumSmallIcon
	 */
	public void setAlbumSmallIcon(final IIconFactory albumSmallIcon) {
		this.albumSmallIcon = albumSmallIcon;
	}

	/**
	 * @param artistImageIcon
	 */
	public void setArtistImageIcon(final IIconFactory artistImageIcon) {
		this.artistImageIcon = artistImageIcon;
	}

	@Override
	public Component decorateTreeCellComponent(final JLabel component,
			final String userObject, final boolean isSelected) {
		Color color = getLookAndFeel().getPaintForColorMutableIcon(component,
				isSelected);
		component.setIcon(getIcon(userObject).getIcon(color));
		component.setToolTipText(null);
		return component;
	}

	/**
	 * Returns icon to use depending on text
	 * 
	 * @param text
	 * @return
	 */
	private IIconFactory getIcon(final String text) {
		IIconFactory factory = getIconsByString().get(text);
		return factory != null ? factory : folderIcon; // For radio view
	}

	private Map<String, IIconFactory> getIconsByString() {
		if (iconsByString == null) {
			iconsByString = new HashMap<String, IIconFactory>();
			iconsByString.put(I18nUtils.getString("ARTISTS"), artistImageIcon);
			iconsByString.put(I18nUtils.getString("ALBUMS"), albumSmallIcon);
			iconsByString.put(I18nUtils.getString("SONGS"), audioFileSmallIcon);
		}
		return iconsByString;
	}
}
