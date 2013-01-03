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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import net.sourceforge.atunes.gui.images.CachedIconFactory;
import net.sourceforge.atunes.gui.images.NextTrayImageIcon;
import net.sourceforge.atunes.gui.images.PauseTrayImageIcon;
import net.sourceforge.atunes.gui.images.PlayTrayImageIcon;
import net.sourceforge.atunes.gui.images.PreviousTrayImageIcon;
import net.sourceforge.atunes.gui.images.StopTrayImageIcon;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.IStateUI;

/**
 * Return icons for tray icons
 * @author alex
 *
 */
public class CommonPlayerTrayIconsHandler implements IPlayerTrayIconsHandler {

	private IStateUI stateUI;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	@Override
	public Image getNextIcon(final Dimension iconSize) {
		NextTrayImageIcon nextTrayIcon = beanFactory.getBean(NextTrayImageIcon.class);
		nextTrayIcon.setSize(iconSize);
		return getIcon(nextTrayIcon);
	}

	@Override
	public Image getPauseIcon(final Dimension iconSize) {
		PauseTrayImageIcon pauseTrayIcon = beanFactory.getBean(PauseTrayImageIcon.class);
		pauseTrayIcon.setSize(iconSize);
		return getIcon(pauseTrayIcon);
	}

	@Override
	public Image getPlayIcon(final Dimension iconSize) {
		PlayTrayImageIcon playTrayIcon = beanFactory.getBean(PlayTrayImageIcon.class);
		playTrayIcon.setSize(iconSize);
		return getIcon(playTrayIcon);
	}

	@Override
	public Image getPreviousIcon(final Dimension iconSize) {
		PreviousTrayImageIcon previousTrayIcon = beanFactory.getBean(PreviousTrayImageIcon.class);
		previousTrayIcon.setSize(iconSize);
		return getIcon(previousTrayIcon);
	}

	@Override
	public Image getStopIcon(final Dimension iconSize) {
		StopTrayImageIcon stopTrayIcon = beanFactory.getBean(StopTrayImageIcon.class);
		stopTrayIcon.setSize(iconSize);
		return getIcon(stopTrayIcon);
	}

	private Image getIcon(final CachedIconFactory iconFactory) {
		Color color = stateUI.getTrayPlayerIconsColor().getColor();
		return iconFactory.getIcon(color).getImage();
	}
}
