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
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens url to search about an artist
 * 
 * @author alex
 * 
 */
public final class SearchArtistContextTableAction extends
		ContextTableAction<IArtistInfo> {

	private static final long serialVersionUID = -4964635263019533125L;

	private IStateContext stateContext;

	private IBeanFactory beanFactory;

	private INetworkHandler networkHandler;

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
     * 
     */
	public SearchArtistContextTableAction() {
		super(I18nUtils.getString("SEARCH"));
	}

	@Override
	protected void execute(final IArtistInfo object) {
		String url = this.stateContext.getSimilarArtistSearchQuery().replace(
				this.beanFactory.getBean("similarArtistSearchQueryWildcard",
						String.class),
				this.networkHandler.encodeString(object.getName()));
		getDesktop().openURL(url);
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