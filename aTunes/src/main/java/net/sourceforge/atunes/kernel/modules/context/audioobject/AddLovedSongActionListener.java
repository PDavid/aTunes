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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;

class AddLovedSongActionListener implements ActionListener {
	
	private IWebServicesHandler webServicesHandler;
	
	private IContextHandler contextHandler;
	
    public AddLovedSongActionListener(IWebServicesHandler webServicesHandler2, IContextHandler contextHandler) {
		this.webServicesHandler = webServicesHandler2;
    	this.contextHandler = contextHandler;
	}

    @Override
    public void actionPerformed(ActionEvent e) {
    	webServicesHandler.addLovedSong(contextHandler.getCurrentAudioObject());
    }
}