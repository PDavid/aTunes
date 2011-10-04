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

package net.sourceforge.atunes.kernel.modules.updates;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

final class CheckUpdatesSwingWorker extends
		SwingWorker<ApplicationVersion, Void> {
	/**
	 * 
	 */
	private final IUpdateHandler updateHandler;
	private final boolean showNoNewVersion;
	private final boolean alwaysInDialog;
	private IState state;
	private IFrame frame;
	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * @param updateHandler
	 * @param showNoNewVersion
	 * @param alwaysInDialog
	 * @param state
	 * @param frame
	 * @param lookAndFeelManager
	 */
	CheckUpdatesSwingWorker(IUpdateHandler updateHandler, boolean showNoNewVersion, boolean alwaysInDialog, IState state, IFrame frame, ILookAndFeelManager lookAndFeelManager) {
		this.updateHandler = updateHandler;
		this.showNoNewVersion = showNoNewVersion;
		this.alwaysInDialog = alwaysInDialog;
		this.state = state;
		this.frame = frame;
		this.lookAndFeelManager = lookAndFeelManager;
	}

	@Override
	protected ApplicationVersion doInBackground() throws Exception {
	    return this.updateHandler.getLastVersion();
	}

	@Override
	protected void done() {
	    try {
	        ApplicationVersion version = get();
	        if (version != null && version.compareTo(Constants.VERSION) == 1) {
	        	 if (alwaysInDialog || !state.isShowStatusBar()) {
	                 new UpdateDialog(version, frame.getFrame(), lookAndFeelManager).setVisible(true);
	             } else {
	                 frame.showNewVersionInfo(true, version);
	             }
	        } else if (showNoNewVersion) {
	            Context.getBean(IMessageDialog.class).showMessage(I18nUtils.getString("NOT_NEW_VERSION"), frame);
	        }
	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    }
	}
}