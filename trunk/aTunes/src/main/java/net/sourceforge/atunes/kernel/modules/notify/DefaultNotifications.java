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

package net.sourceforge.atunes.kernel.modules.notify;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Default built-in notification
 * 
 * @author alex
 * 
 */
public class DefaultNotifications extends CommonNotificationEngine {

	/**
	 * OSD controller
	 */
	private OSDDialogController osdDialogController;

	/**
	 * OSD Dialog
	 */
	private OSDDialog osdDialog;

	private IStateUI stateUI;

	private IUnknownObjectChecker unknownObjectChecker;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * Gets the oSD dialog controller.
	 * 
	 * @return the oSD dialog controller
	 */
	private OSDDialogController getOSDDialogController() {
		if (this.osdDialogController == null) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			this.osdDialog = new OSDDialog(this.stateUI.getOsdWidth(),
					getLookAndFeelManager().getCurrentLookAndFeel(),
					this.controlsBuilder);
			JDialog.setDefaultLookAndFeelDecorated(getLookAndFeelManager()
					.getCurrentLookAndFeel().isDialogUndecorated());
			this.osdDialogController = new OSDDialogController(this.osdDialog,
					this.stateUI, getAudioObjectGenericImageFactory(),
					getLookAndFeelManager(), getBeanFactory(),
					this.unknownObjectChecker);
		}
		return this.osdDialogController;
	}

	@Override
	public void updateNotification(final IStateUI newState) {
		if (this.osdDialog != null) {
			this.osdDialog.setWidth(newState.getOsdWidth());
		}
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public void showNotification(final IAudioObject audioObject) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getOSDDialogController().showOSD(audioObject);
			}
		});
	}

	@Override
	public void disposeNotifications() {
	}

	@Override
	public boolean testEngineAvailable() {
		return true; // Always available
	}

	@Override
	public String getDescription() {
		return I18nUtils.getString("NOTIFICATION_ENGINE_DEFAULT_DESCRIPTION");
	}

	@Override
	public String getUrl() {
		return null;
	}
}
