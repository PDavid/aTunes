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

package net.sourceforge.atunes.kernel.modules.context;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.utils.Logger;

class ContextTableRowPanelFactory<T> {

	/**
	 * Creates a panel to be shown in each row of a panel table
	 * 
	 * @param table
	 * @param image
	 * @param text
	 * @param imageMaxWidth
	 * @param controlsBuilder
	 * @return
	 */
	public ContextTableRowPanel<T> getPanelForTableRenderer(
			final ContextTable table, final ImageIcon image, final String text,
			final int imageMaxWidth, final IControlsBuilder controlsBuilder) {

		Logger.debug("Building ContextTableRowPanel for ", text);

		// This renderer is a little tricky because images have no the same size
		// so we must add two labels with custom insets to
		// get desired alignment of images and text. Other ways to achieve this
		// like setPreferredSize doesn't work because when width of panel is low
		// preferred size is ignored, but insets don't

		final ContextTableRowPanel<T> panel = new ContextTableRowPanel<T>();
		panel.setImage(image);
		panel.setText(text);
		panel.build(imageMaxWidth, table, controlsBuilder);
		return panel;
	}

}
