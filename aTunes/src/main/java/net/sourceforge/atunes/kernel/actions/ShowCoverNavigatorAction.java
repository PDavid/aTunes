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

import net.sourceforge.atunes.kernel.modules.covernavigator.CoverNavigatorController;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action shows cover navigator
 * 
 * @author fleax
 * 
 */
public class ShowCoverNavigatorAction extends CustomAbstractAction {

	private static final long serialVersionUID = 4927892497869144235L;

	private IRepositoryHandler repositoryHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Default constructor
	 */
	public ShowCoverNavigatorAction() {
		super(I18nUtils.getString("COVER_NAVIGATOR"));
	}

	@Override
	protected void executeAction() {
		CoverNavigatorController controller = this.beanFactory
				.getBean(CoverNavigatorController.class);
		controller.setArtists(this.repositoryHandler.getArtists());
		controller.setVisible(true);
	}
}
