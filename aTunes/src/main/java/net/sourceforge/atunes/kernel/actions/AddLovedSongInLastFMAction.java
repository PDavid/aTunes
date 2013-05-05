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

package net.sourceforge.atunes.kernel.actions;

import java.util.Collections;

import net.sourceforge.atunes.kernel.modules.webservices.AddLovedSongBackgroundWorker;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds a song to loved tracks in Last.fm profile
 * 
 * @author fleax
 * 
 */
public class AddLovedSongInLastFMAction extends CustomAbstractAction {

	private static final long serialVersionUID = -2687851398606488392L;

	private IContextHandler contextHandler;

	private IStateContext stateContext;

	private IBeanFactory beanFactory;

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
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
     * 
     */
	public AddLovedSongInLastFMAction() {
		super(I18nUtils.getString("ADD_LOVED_SONG_IN_LASTFM"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		setEnabled(this.stateContext.isLastFmEnabled());
	}

	@Override
	protected void executeAction() {
		this.beanFactory.getBean(AddLovedSongBackgroundWorker.class).add(
				Collections.singletonList(this.contextHandler
						.getCurrentAudioObject()));
	}
}
