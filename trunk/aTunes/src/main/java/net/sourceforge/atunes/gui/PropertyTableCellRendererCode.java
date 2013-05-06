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

package net.sourceforge.atunes.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.model.AudioObjectProperty;
import net.sourceforge.atunes.model.IIconFactory;

/**
 * Renderer for properties of audio objects in navigation table
 * 
 * @author alex
 * 
 */
public class PropertyTableCellRendererCode extends
		AbstractTableCellRendererCode<JLabel, AudioObjectProperty> {

	private IIconFactory favoriteIcon;

	private IIconFactory newIcon;

	private IIconFactory downloadIcon;

	/**
	 * @param favoriteIcon
	 */
	public void setFavoriteIcon(final IIconFactory favoriteIcon) {
		this.favoriteIcon = favoriteIcon;
	}

	/**
	 * @param newIcon
	 */
	public void setNewIcon(final IIconFactory newIcon) {
		this.newIcon = newIcon;
	}

	/**
	 * @param downloadIcon
	 */
	public void setDownloadIcon(final IIconFactory downloadIcon) {
		this.downloadIcon = downloadIcon;
	}

	@Override
	public JLabel getComponent(final JLabel comp, final JTable table,
			final AudioObjectProperty val, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		ImageIcon icon = null;
		if (val == AudioObjectProperty.FAVORITE) {
			icon = this.favoriteIcon.getIcon(getLookAndFeel()
					.getPaintForColorMutableIcon(comp, isSelected));
		} else if (val == AudioObjectProperty.NOT_LISTENED_ENTRY) {
			icon = this.newIcon.getIcon(getLookAndFeel()
					.getPaintForColorMutableIcon(comp, isSelected));
		} else if (val == AudioObjectProperty.DOWNLOADED_ENTRY) {
			icon = this.downloadIcon.getIcon(getLookAndFeel()
					.getPaintForColorMutableIcon(comp, isSelected));
		}
		comp.setIcon(icon);
		comp.setText(null);
		return comp;
	}

}
