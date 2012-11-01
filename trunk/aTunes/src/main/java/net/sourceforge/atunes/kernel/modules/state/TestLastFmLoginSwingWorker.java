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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Window;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

final class TestLastFmLoginSwingWorker extends
	SwingWorker<Boolean, Void> {

    private final String user;

    private final String password;

    private final JButton loginButton;

    private final Window window;

    private final IBeanFactory beanFactory;

    /**
     * @param user
     * @param password
     * @param loginButton
     * @param window
     * @param beanFactory
     */
    public TestLastFmLoginSwingWorker(final String user,
	    final String password, final JButton loginButton,
	    final Window window, final IBeanFactory beanFactory) {
	this.user = user;
	this.password = password;
	this.loginButton = loginButton;
	this.window = window;
	this.beanFactory = beanFactory;
    }

    @Override
    protected Boolean doInBackground() {
	return beanFactory.getBean(IWebServicesHandler.class)
		.testLogin(user, password);
    }

    @Override
    protected void done() {
	try {
	    boolean loginSuccessful;
	    loginSuccessful = get();
	    if (loginSuccessful) {
		beanFactory
			.getBean(IDialogFactory.class)
			.newDialog(IMessageDialog.class)
			.showMessage(
				I18nUtils.getString("LOGIN_SUCCESSFUL"),
				window);
	    } else {
		beanFactory
			.getBean(IDialogFactory.class)
			.newDialog(IErrorDialog.class)
			.showErrorDialog(
				I18nUtils.getString("LOGIN_FAILED"),
				window);
	    }
	} catch (InterruptedException e) {
	    Logger.error(e);
	} catch (ExecutionException e) {
	    Logger.error(e);
	} finally {
	    loginButton.setEnabled(true);
	}
    }
}