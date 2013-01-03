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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IOSManager;

final class FullScreenShowMenuMouseAdapter extends MouseAdapter {

	private final JPopupMenu options;

	private final IOSManager osManager;

	/**
	 * @param options
	 * @param osManager
	 */
	public FullScreenShowMenuMouseAdapter(final JPopupMenu options,
			final IOSManager osManager) {
		this.options = options;
		this.osManager = osManager;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
			this.options.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}