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

package net.sourceforge.atunes.kernel.modules.context.artist;

import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IDesktop;

final class OpenAlbumUrlAction extends ContextTableAction<IAlbumInfo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4367597772680455920L;

	OpenAlbumUrlAction(String name, ContextTable table,
			IDesktop desktop) {
		super(name, table, desktop);
	}

	@Override
	protected void execute(IAlbumInfo object) {
		getDesktop().openURL(object.getUrl());
	}

	@Override
	protected IAlbumInfo getSelectedObject(int row) {
		return  ((ContextAlbumsTableModel) getTable().getModel()).getAlbum(row);
	}

	@Override
	protected boolean isEnabledForObject(IAlbumInfo object) {
		return true;
	}
}