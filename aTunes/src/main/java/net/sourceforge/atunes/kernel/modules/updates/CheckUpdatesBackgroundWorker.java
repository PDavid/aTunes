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

package net.sourceforge.atunes.kernel.modules.updates;

import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IUpdateDialog;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Checks for updates
 * 
 * @author alex
 * 
 */
public final class CheckUpdatesBackgroundWorker extends
		BackgroundWorker<ApplicationVersion, Void> {

	private IUpdateHandler updateHandler;
	private boolean showNoNewVersion;
	private boolean alwaysInDialog;
	private IStateUI stateUI;
	private IFrame frame;
	private IDialogFactory dialogFactory;

	private String downloadURLStart1;

	private String downloadURLStart2;

	/**
	 * @param downloadURLStart1
	 */
	public void setDownloadURLStart1(final String downloadURLStart1) {
		this.downloadURLStart1 = downloadURLStart1;
	}

	/**
	 * @param downloadURLStart2
	 */
	public void setDownloadURLStart2(final String downloadURLStart2) {
		this.downloadURLStart2 = downloadURLStart2;
	}

	/**
	 * @param updateHandler
	 */
	public void setUpdateHandler(final IUpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}

	/**
	 * @param showNoNewVersion
	 */
	public void setShowNoNewVersion(final boolean showNoNewVersion) {
		this.showNoNewVersion = showNoNewVersion;
	}

	/**
	 * @param alwaysInDialog
	 */
	public void setAlwaysInDialog(final boolean alwaysInDialog) {
		this.alwaysInDialog = alwaysInDialog;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}

	@Override
	protected ApplicationVersion doInBackground() {
		return this.updateHandler.getLastVersion();
	}

	@Override
	protected void done(final ApplicationVersion version) {
		if (isNewVersion(version)) {
			if (this.alwaysInDialog || !this.stateUI.isShowStatusBar()) {
				IUpdateDialog dialog = this.dialogFactory
						.newDialog(IUpdateDialog.class);
				dialog.initialize(version);
				dialog.showDialog();
			} else {
				this.frame.showNewVersionInfo(true, version);
			}
		} else if (this.showNoNewVersion) {
			this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
					I18nUtils.getString("NOT_NEW_VERSION"));
		}
	}

	/**
	 * @param version
	 * @return if version is valid
	 */
	private boolean isNewVersion(final ApplicationVersion version) {
		return version != null && version.compareTo(Constants.VERSION) == 1
				&& downloadURLIsValid(version);
	}

	private boolean downloadURLIsValid(final ApplicationVersion version) {
		return version.getDirectDownloadURL()
				.startsWith(this.downloadURLStart1)
				|| version.getDirectDownloadURL().startsWith(
						this.downloadURLStart2);

	}
}