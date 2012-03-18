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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import net.sourceforge.atunes.gui.images.NextTrayImageIcon;
import net.sourceforge.atunes.gui.images.PauseTrayImageIcon;
import net.sourceforge.atunes.gui.images.PlayTrayImageIcon;
import net.sourceforge.atunes.gui.images.PreviousTrayImageIcon;
import net.sourceforge.atunes.gui.images.StopTrayImageIcon;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.IState;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CommonPlayerTrayIconsHandler implements IPlayerTrayIconsHandler, ApplicationContextAware {
	
	private IState state;
	
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
	@Override
	public Image getNextIcon(Dimension iconSize) {
    	Color color = state.getTrayPlayerIconsColor().getColor();
    	NextTrayImageIcon nextTrayIcon = context.getBean(NextTrayImageIcon.class);
    	nextTrayIcon.setSize(iconSize);
    	return nextTrayIcon.getIcon(color).getImage();
	}
	
	@Override
	public Image getPauseIcon(Dimension iconSize) {
    	Color color = state.getTrayPlayerIconsColor().getColor();
    	PauseTrayImageIcon pauseTrayIcon = context.getBean(PauseTrayImageIcon.class);
    	pauseTrayIcon.setSize(iconSize);
    	return pauseTrayIcon.getIcon(color).getImage();
	}
	
	@Override
	public Image getPlayIcon(Dimension iconSize) {
    	Color color = state.getTrayPlayerIconsColor().getColor();
    	PlayTrayImageIcon playTrayIcon = context.getBean(PlayTrayImageIcon.class);
    	playTrayIcon.setSize(iconSize);
    	return playTrayIcon.getIcon(color).getImage();
	}
	
	@Override
	public Image getPreviousIcon(Dimension iconSize) {
    	Color color = state.getTrayPlayerIconsColor().getColor();
    	PreviousTrayImageIcon previousTrayIcon = context.getBean(PreviousTrayImageIcon.class);
    	previousTrayIcon.setSize(iconSize);
    	return previousTrayIcon.getIcon(color).getImage();
	}
	
	@Override
	public Image getStopIcon(Dimension iconSize) {
    	Color color = state.getTrayPlayerIconsColor().getColor();
    	StopTrayImageIcon stopTrayIcon = context.getBean(StopTrayImageIcon.class);
    	stopTrayIcon.setSize(iconSize);
    	return stopTrayIcon.getIcon(color).getImage();
	}
}
