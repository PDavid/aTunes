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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import net.sourceforge.atunes.model.IStateUI;

final class RemoveBackgroundActionListener implements ActionListener {

    private final FullScreenWindow window;

    private final IStateUI stateUI;

    /**
     * @param window
     * @param stateUI
     */
    public RemoveBackgroundActionListener(final FullScreenWindow window,
	    final IStateUI stateUI) {
	this.window = window;
	this.stateUI = stateUI;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	window.setBackground((File) null);
	stateUI.setFullScreenBackground(null);
	window.invalidate();
	window.repaint();
    }
}