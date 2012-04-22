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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import net.sourceforge.atunes.model.IState;

final class RemoveBackgroundActionListener implements ActionListener {
	
	private final FullScreenWindow window;
	
	private final IState state;

	public RemoveBackgroundActionListener(FullScreenWindow window, IState state) {
		this.window = window;
		this.state = state;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.setBackground((File)null);
	    state.setFullScreenBackground(null);
	    window.invalidate();
	    window.repaint();
	}
}