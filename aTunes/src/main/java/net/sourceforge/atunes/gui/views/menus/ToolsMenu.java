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

package net.sourceforge.atunes.gui.views.menus;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.utils.I18nUtils;

public class ToolsMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private Action ripCDAction;
	private Action showStatsAction;
	private Action showCoverNavigatorAction;
	private Action addRadioAction;
	private Action showRadioBrowserAction;
	private Action addPodcastFeedAction;
	private Action customSearchAction;
	private Action importLovedTracksFromLastFMAction;
	
	/**
	 * @param i18nKey
	 */
	public ToolsMenu(String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}
	
	/**
	 * @param ripCDAction
	 */
	public void setRipCDAction(Action ripCDAction) {
		this.ripCDAction = ripCDAction;
	}
	
	/**
	 * @param showStatsAction
	 */
	public void setShowStatsAction(Action showStatsAction) {
		this.showStatsAction = showStatsAction;
	}
	
	/**
	 * @param showCoverNavigatorAction
	 */
	public void setShowCoverNavigatorAction(Action showCoverNavigatorAction) {
		this.showCoverNavigatorAction = showCoverNavigatorAction;
	}
	
	/**
	 * @param addRadioAction
	 */
	public void setAddRadioAction(Action addRadioAction) {
		this.addRadioAction = addRadioAction;
	}
	
	/**
	 * @param showRadioBrowserAction
	 */
	public void setShowRadioBrowserAction(Action showRadioBrowserAction) {
		this.showRadioBrowserAction = showRadioBrowserAction;
	}
	
	/**
	 * @param addPodcastFeedAction
	 */
	public void setAddPodcastFeedAction(Action addPodcastFeedAction) {
		this.addPodcastFeedAction = addPodcastFeedAction;
	}
	
	/**
	 * @param customSearchAction
	 */
	public void setCustomSearchAction(Action customSearchAction) {
		this.customSearchAction = customSearchAction;
	}
	
	/**
	 * @param importLovedTracksFromLastFMAction
	 */
	public void setImportLovedTracksFromLastFMAction(Action importLovedTracksFromLastFMAction) {
		this.importLovedTracksFromLastFMAction = importLovedTracksFromLastFMAction;
	}
	
	/**
	 * Initializes menu
	 */
	public void initialize() {
        add(ripCDAction);
        add(new JSeparator());
        add(showStatsAction);
        add(showCoverNavigatorAction);
        add(new JSeparator());
        add(addRadioAction);
        add(showRadioBrowserAction);
        add(addPodcastFeedAction);
        add(new JSeparator());
        add(customSearchAction);
        add(new JSeparator());
        add(importLovedTracksFromLastFMAction);
	}
}
