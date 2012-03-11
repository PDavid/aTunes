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

package net.sourceforge.atunes.kernel.modules.context.similar;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;

final class AddAlbumArtistToPlayListContextTableAction extends ContextTableAction<IArtistInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3920095074089169426L;

	AddAlbumArtistToPlayListContextTableAction(String name, ContextTable table, IDesktop desktop) {
		super(name, table, desktop);
	}

	@Override
	protected void execute(IArtistInfo object) {
		Context.getBean(IPlayListHandler.class).showAddArtistDragDialog(Context.getBean(IRepositoryHandler.class).getArtist(object.getName()));
	}

	@Override
	protected IArtistInfo getSelectedObject(int row) {
		return ((SimilarArtistsTableModel) getTable().getModel()).getArtist(row);
	}

	@Override
	protected boolean isEnabledForObject(IArtistInfo object) {
		return Context.getBean(IRepositoryHandler.class).getArtist(object.getName()) != null;
	}
}