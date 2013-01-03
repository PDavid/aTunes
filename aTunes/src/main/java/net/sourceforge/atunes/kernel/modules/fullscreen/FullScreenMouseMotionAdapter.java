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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;

final class FullScreenMouseMotionAdapter extends MouseMotionAdapter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2501796497424349860L;
	private FullScreenWindow fullScreenWindow;
	
	/**
	 * @param fullScreenWindow
	 */
	FullScreenMouseMotionAdapter(FullScreenWindow fullScreenWindow) {
		super();
		this.fullScreenWindow = fullScreenWindow;
	}

	@Override
    public void mouseMoved(MouseEvent e) {
        fullScreenWindow.activateTimer();
    }
}