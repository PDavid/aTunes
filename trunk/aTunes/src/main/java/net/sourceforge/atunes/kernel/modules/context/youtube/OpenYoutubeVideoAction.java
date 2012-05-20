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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IVideoEntry;

final class OpenYoutubeVideoAction extends ContextTableAction<IVideoEntry> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7758596564970276630L;

	OpenYoutubeVideoAction(String name, ContextTable table, IDesktop desktop) {
		super(name, table, desktop);
	}

	@Override
	protected void execute(IVideoEntry entry) {
	     //open youtube url
		getDesktop().openURL(entry.getUrl());
	    // When playing a video in web browser automatically pause current song
	    if (Context.getBean(IPlayerHandler.class).isEnginePlaying()) {
	        Context.getBean(IPlayerHandler.class).playCurrentAudioObject(true);
	    }
	}

	@Override
	protected IVideoEntry getSelectedObject(int row) {
		return ((YoutubeResultTableModel) getTable().getModel()).getEntry(row);
	}

	@Override
	protected boolean isEnabledForObject(IVideoEntry object) {
		return true;
	}
}