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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Window;
import java.util.List;

import javax.swing.JButton;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Tests login in last.fm
 * 
 * @author alex
 * 
 */
public final class TestLastFmLoginBackgroundWorker extends
		BackgroundWorker<Boolean, Void> {

	private String user;

	private String password;

	private JButton loginButton;

	private Window window;

	private IWebServicesHandler webServicesHandler;

	private IDialogFactory dialogFactory;

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param user
	 * @param password
	 * @param loginButton
	 * @param window
	 */
	public void test(final String user, final String password,
			final JButton loginButton, final Window window) {
		this.user = user;
		this.password = password;
		this.loginButton = loginButton;
		this.window = window;
		execute();
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Boolean doInBackground() {
		return this.webServicesHandler.testLogin(this.user, this.password);
	}

	@Override
	protected void done(final Boolean loginSuccessful) {
		if (loginSuccessful) {
			this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
					I18nUtils.getString("LOGIN_SUCCESSFUL"), this.window);
		} else {
			this.dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(
					I18nUtils.getString("LOGIN_FAILED"), this.window);
		}
		this.loginButton.setEnabled(true);
	}
}