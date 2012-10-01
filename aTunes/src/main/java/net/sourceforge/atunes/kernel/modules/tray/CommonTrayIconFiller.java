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

package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.TrayIcon;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.RepeatModeAction;
import net.sourceforge.atunes.kernel.actions.ShowAboutAction;
import net.sourceforge.atunes.kernel.actions.ShuffleModeAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ToggleOSDSettingAction;
import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Responsible of filling tray menu with tray icons
 * @author alex
 *
 */
public class CommonTrayIconFiller implements ITrayIconFiller {

	private JMenuItem playMenuItem;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void fillTrayIcon(final TrayIcon trayIcon) {
		if (trayIcon instanceof JTrayIcon) {
			JPopupMenu popupmenu = ((JTrayIcon)trayIcon).getJTrayIconPopup();

			popupmenu.add(getPlayMenuItem());
			popupmenu.add(getStopMenuItem());
			popupmenu.add(getPreviousMenuItem());
			popupmenu.add(getNextMenuItem());
			popupmenu.add(new JSeparator());
			popupmenu.add(getMuteCheckBoxMenuItem());
			popupmenu.add(new JSeparator());
			popupmenu.add(getShuffleCheckBoxMenuItem());
			popupmenu.add(getRepeatCheckBoxMenuItem());
			popupmenu.add(new JSeparator());
			popupmenu.add(getShowOSDCheckBoxMenuItem());
			popupmenu.add(new JSeparator());
			popupmenu.add(getAboutMenuItem());
			popupmenu.add(new JSeparator());
			popupmenu.add(getExitMenuItem());

			GuiUtils.applyComponentOrientation(popupmenu);
		}
	}

	@Override
	public void setPlayMenuItemText(final String text) {
		playMenuItem.setText(text);
	}

	/**
	 * Getter of play menu item
	 * 
	 * @return
	 */
	private JMenuItem getPlayMenuItem() {
		if (playMenuItem == null) {
			playMenuItem = new JMenuItem(beanFactory.getBean(PlayAction.class));
		}
		return playMenuItem;
	}

	/**
	 * Getter of stop menu item
	 * 
	 * @return
	 */
	private JMenuItem getStopMenuItem() {
		return new JMenuItem(beanFactory.getBean(StopCurrentAudioObjectAction.class));
	}

	/**
	 * Getter of previous menu item
	 * 
	 * @return
	 */
	private JMenuItem getPreviousMenuItem() {
		return new JMenuItem(beanFactory.getBean(PlayPreviousAudioObjectAction.class));
	}

	/**
	 * Getter for next menu item
	 * 
	 * @return
	 */
	private JMenuItem getNextMenuItem() {
		return new JMenuItem(beanFactory.getBean(PlayNextAudioObjectAction.class));
	}

	/**
	 * Getter for mute menu item
	 * 
	 * @return
	 */
	private JCheckBoxMenuItem getMuteCheckBoxMenuItem() {
		JCheckBoxMenuItem mute = new JCheckBoxMenuItem(beanFactory.getBean(MuteAction.class));
		mute.setIcon(null);
		return mute;
	}

	/**
	 * Getter for shuffle menu item
	 * 
	 * @return
	 */
	private JCheckBoxMenuItem getShuffleCheckBoxMenuItem() {
		return new JCheckBoxMenuItem(beanFactory.getBean(ShuffleModeAction.class));
	}

	/**
	 * Getter for repeat menu item
	 */
	private JCheckBoxMenuItem getRepeatCheckBoxMenuItem() {
		return new JCheckBoxMenuItem(beanFactory.getBean(RepeatModeAction.class));
	}

	/**
	 * Getter for showOSD menu item
	 * 
	 * @return
	 */
	private JCheckBoxMenuItem getShowOSDCheckBoxMenuItem() {
		return new JCheckBoxMenuItem(beanFactory.getBean(ToggleOSDSettingAction.class));
	}

	/**
	 * Getter for about menu item
	 * 
	 * @return
	 */
	private JMenuItem getAboutMenuItem() {
		return new JMenuItem(beanFactory.getBean(ShowAboutAction.class));
	}

	/**
	 * Getter for exit menu item
	 * 
	 * @return
	 */
	private JMenuItem getExitMenuItem() {
		return new JMenuItem(beanFactory.getBean(ExitAction.class));
	}
}
