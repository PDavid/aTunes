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

package net.sourceforge.atunes.kernel.modules.context.similar;

import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens url to read more information about an artist
 * 
 * @author alex
 * 
 */
public final class ReadMoreContextTableAction extends
		ContextTableAction<IArtistInfo> {

	private static final long serialVersionUID = -4964635263019533125L;

	/**
     * 
     */
	public ReadMoreContextTableAction() {
		super(I18nUtils.getString("READ_MORE"));
	}

	@Override
	protected void execute(final IArtistInfo object) {
		getDesktop().openURL(object.getUrl());
	}

	@Override
	protected IArtistInfo getSelectedObject(final int row) {
		return ((SimilarArtistsTableModel) getTable().getModel())
				.getArtist(row);
	}

	@Override
	protected boolean isEnabledForObject(final Object object) {
		return true;
	}
}