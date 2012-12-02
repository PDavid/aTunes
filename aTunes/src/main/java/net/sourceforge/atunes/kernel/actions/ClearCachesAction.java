/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.Cursor;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Action to clear caches from last.fm and lyrics
 * 
 * @author alex
 * 
 */
public class ClearCachesAction extends CustomAbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5131926704037915711L;

	private IWebServicesHandler webServicesHandler;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * Default constructor
	 */
	public ClearCachesAction() {
		super(I18nUtils.getString("CLEAR_CACHE"));
	}

	@Override
	protected void executeAction() {
		setEnabled(false);
		((JPanel)((JButton)getSource()).getParent()).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		IBackgroundWorker<Void> backgroundWorker = backgroundWorkerFactory.getWorker();
		backgroundWorker.setBackgroundActions(new Callable<Void>() {

			@Override
			public Void call() {
				webServicesHandler.clearCache();
				return null;
			}
		});
		backgroundWorker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {
			@Override
			public void call(final Void result) {
				((JPanel)((JButton)getSource()).getParent()).setCursor(Cursor.getDefaultCursor());
				setEnabled(true);
			}
		});
		backgroundWorker.execute();
	}

}
