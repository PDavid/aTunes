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

package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.ITrayIcon;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * The system tray handler.
 */
public final class SystemTrayHandler extends AbstractHandler implements
		ISystemTrayHandler {

	private boolean trayInitialized;
	private boolean trayIconVisible;
	private boolean trayPlayerVisible;
	private SystemTray tray;
	private TrayIcon trayIcon;

	private TrayIcon previousIcon;
	private TrayIcon playIcon;
	private TrayIcon stopIcon;
	private TrayIcon nextIcon;

	private boolean playing;

	private ITrayIcon customTrayIcon;

	private PlayerTrayIconsBuilder playerTrayIconsBuilder;

	private IPlayerTrayIconsHandler iconsHandler;

	private IStateUI stateUI;

	private IUnknownObjectChecker unknownObjectChecker;

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
	 * @param playerTrayIconsBuilder
	 */
	public void setPlayerTrayIconsBuilder(
			final PlayerTrayIconsBuilder playerTrayIconsBuilder) {
		this.playerTrayIconsBuilder = playerTrayIconsBuilder;
	}

	@Override
	public void allHandlersInitialized() {
		if (getOsManager().areTrayIconsSupported()) {
			this.iconsHandler = getOsManager().getPlayerTrayIcons();
			this.customTrayIcon = getOsManager().getTrayIcon();
			// System tray player
			if (this.stateUI.isShowTrayPlayer()) {
				initTrayPlayerIcons();
			}

			// System tray
			if (this.stateUI.isShowSystemTray()) {
				initTrayIcon();
			}
		}
	}

	/**
	 * Finish.
	 */
	@Override
	public void applicationFinish() {
		setTrayIconVisible(false);
		setTrayPlayerVisible(false);
	}

	/**
	 * Inits the system tray.
	 */
	private void initSystemTray() {
		if (!this.trayInitialized && SystemTray.isSupported()) {
			this.tray = SystemTray.getSystemTray();
			this.trayInitialized = true;
		}
	}

	/**
	 * Inits the tray icon.
	 */
	private void initTrayIcon() {
		initSystemTray();
		if (isTrayInitialized()) {
			this.trayIconVisible = true;
			addTrayIcon(getTrayIcon());
			getFrame().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else {
			Logger.error("No system tray supported");
		}
	}

	/**
	 * Inits the tray player icons.
	 */
	private void initTrayPlayerIcons() {
		initSystemTray();
		if (isTrayInitialized()) {
			this.trayPlayerVisible = true;
			// Icons must be added in reverse order
			addTrayIcon(getNextTrayIcon());
			addTrayIcon(getPlayTrayIcon());
			addTrayIcon(getStopTrayIcon());
			addTrayIcon(getPreviousTrayIcon());
		} else {
			Logger.error("No system tray supported");
		}
	}

	/**
	 * Adds given tray icon
	 * 
	 * @param icon
	 */
	private void addTrayIcon(final TrayIcon icon) {
		try {
			this.tray.add(icon);
		} catch (AWTException e) {
			Logger.error(e);
		}
	}

	private void setPlaying(final boolean playing) {
		this.playing = playing;
		if (isTrayInitialized()) {
			Image icon = null;
			if (playing) {
				this.customTrayIcon.setPlayMenuItemText(I18nUtils
						.getString("PAUSE"));
				icon = this.iconsHandler.getPauseIcon(this.tray
						.getTrayIconSize());
			} else {
				this.customTrayIcon.setPlayMenuItemText(I18nUtils
						.getString("PLAY"));
				icon = this.iconsHandler.getPlayIcon(this.tray
						.getTrayIconSize());
			}
			getPlayTrayIcon().setImage(icon);
		}
	}

	/**
	 * Sets the tray icon visible.
	 * 
	 * @param visible
	 *            the new tray icon visible
	 */
	private void setTrayIconVisible(final boolean visible) {
		if (visible && !this.trayIconVisible) {
			initTrayIcon();
			if (isTrayInitialized()) {
				trayIconAdvice();
			}
		} else {
			if (!visible && this.trayIconVisible && isTrayInitialized()) {
				this.tray.remove(getTrayIcon());
				getFrame().setDefaultCloseOperation(
						WindowConstants.DISPOSE_ON_CLOSE);
				this.trayIconVisible = false;
			}
		}
	}

	/**
	 * Sets the tray player visible.
	 * 
	 * @param visible
	 *            the new tray player visible
	 */
	private void setTrayPlayerVisible(final boolean visible) {
		if (visible && !this.trayPlayerVisible) {
			initTrayPlayerIcons();
		} else {
			if (!visible && this.trayPlayerVisible && isTrayInitialized()) {
				this.tray.remove(getPreviousTrayIcon());
				this.tray.remove(getPlayTrayIcon());
				this.tray.remove(getStopTrayIcon());
				this.tray.remove(getNextTrayIcon());
				this.trayPlayerVisible = false;
			}
		}
	}

	private void setTrayToolTip(final String msg) {
		if (isTrayInitialized()) {
			getTrayIcon().setToolTip(msg);
		}
	}

	/**
	 * Tray icon advice.
	 */
	private void trayIconAdvice() {
		// For some reason, in Linux systems display message causes Swing freeze
		if (!getOsManager().isLinux() && isTrayInitialized()) {
			getTrayIcon().displayMessage(Constants.APP_NAME,
					I18nUtils.getString("TRAY_ICON_MESSAGE"),
					TrayIcon.MessageType.INFO);
		}
	}

	@Override
	public void applicationStateChanged() {
		setTrayIconVisible(this.stateUI.isShowSystemTray());
		setTrayPlayerVisible(this.stateUI.isShowTrayPlayer());
		updateTrayPlayerIconsColor();
	}

	/**
	 * Changes color of tray player icons
	 */
	private void updateTrayPlayerIconsColor() {
		if (this.trayPlayerVisible) {
			getStopTrayIcon().setImage(
					this.iconsHandler.getStopIcon(this.tray.getTrayIconSize()));
			if (this.playing) {
				getPlayTrayIcon().setImage(
						this.iconsHandler.getPauseIcon(this.tray
								.getTrayIconSize()));
			} else {
				getPlayTrayIcon().setImage(
						this.iconsHandler.getPlayIcon(this.tray
								.getTrayIconSize()));
			}
			getNextTrayIcon().setImage(
					this.iconsHandler.getNextIcon(this.tray.getTrayIconSize()));
			getPreviousTrayIcon().setImage(
					this.iconsHandler.getPreviousIcon(this.tray
							.getTrayIconSize()));
		}
	}

	/**
	 * Getter for trayIcon
	 * 
	 * @return
	 */
	private TrayIcon getTrayIcon() {
		if (this.trayIcon == null) {
			this.trayIcon = this.customTrayIcon.getTrayIcon(
					Images.getImage(Images.APP_LOGO_32).getImage(),
					this.tray.getTrayIconSize().width);
		}
		return this.trayIcon;
	}

	/**
	 * Getter for nextIcon
	 * 
	 * @return
	 */
	private TrayIcon getNextTrayIcon() {
		if (this.nextIcon == null) {
			this.nextIcon = this.playerTrayIconsBuilder
					.getNextTrayIcon(this.tray.getTrayIconSize());
		}
		return this.nextIcon;
	}

	/**
	 * Getter for stopIcon
	 * 
	 * @return
	 */
	private TrayIcon getStopTrayIcon() {
		if (this.stopIcon == null) {
			this.stopIcon = this.playerTrayIconsBuilder
					.getStopTrayIcon(this.tray.getTrayIconSize());
		}
		return this.stopIcon;
	}

	/**
	 * Getter for playIcon
	 * 
	 * @return
	 */
	private TrayIcon getPlayTrayIcon() {
		if (this.playIcon == null) {
			this.playIcon = this.playerTrayIconsBuilder
					.getPlayTrayIcon(this.tray.getTrayIconSize());
		}
		return this.playIcon;
	}

	/**
	 * Getter for previousIcon
	 * 
	 * @return
	 */
	private TrayIcon getPreviousTrayIcon() {
		if (this.previousIcon == null) {
			this.previousIcon = this.playerTrayIconsBuilder
					.getPreviousTrayIcon(this.tray.getTrayIconSize());
		}
		return this.previousIcon;
	}

	/**
	 * @return the trayInitialized
	 */
	protected boolean isTrayInitialized() {
		return this.trayInitialized;
	}

	@Override
	public void playbackStateChanged(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				playbackStateChangedEDT(newState, currentAudioObject);
			}
		});
	}

	private void playbackStateChangedEDT(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		setPlaying(newState == PlaybackState.RESUMING
				|| newState == PlaybackState.PLAYING);

		String text = currentAudioObject != null ? currentAudioObject
				.getAudioObjectDescription(this.unknownObjectChecker) : "";
		StringBuilder strBuilder = new StringBuilder();
		if (!text.equals("")) {
			strBuilder.append(text);
			strBuilder.append(" - ");
		}
		strBuilder.append(Constants.APP_NAME);
		strBuilder.append(" ");
		strBuilder.append(Constants.VERSION.toShortString());
		setTrayToolTip(strBuilder.toString());
	}
}
