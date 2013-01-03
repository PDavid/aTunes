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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Next button in full screen mode
 * 
 * @author alex
 * 
 */
public final class FullScreenNextButton extends JButton {

	private static final long serialVersionUID = -4939372038840047335L;

	/**
	 * Instantiates a new next button.
	 * 
	 * @param size
	 * @param icon
	 * @param action
	 */
	public FullScreenNextButton(final Dimension size, final ImageIcon icon,
			final Action action) {
		super(action);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setFocusable(false);
		setText(null);
		setIcon(icon);

		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
	}
}
