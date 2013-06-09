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

package net.sourceforge.atunes.gui.views.menus;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.utils.I18nUtils;

/**
 * "Tools" menu
 * 
 * @author alex
 * 
 */
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
	private Action showRecommendedEventsFromLastFMAction;

	/**
	 * @param i18nKey
	 */
	public ToolsMenu(final String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * @param showRecommendedEventsFromLastFMAction
	 */
	public void setShowRecommendedEventsFromLastFMAction(
			Action showRecommendedEventsFromLastFMAction) {
		this.showRecommendedEventsFromLastFMAction = showRecommendedEventsFromLastFMAction;
	}

	/**
	 * @param ripCDAction
	 */
	public void setRipCDAction(final Action ripCDAction) {
		this.ripCDAction = ripCDAction;
	}

	/**
	 * @param showStatsAction
	 */
	public void setShowStatsAction(final Action showStatsAction) {
		this.showStatsAction = showStatsAction;
	}

	/**
	 * @param showCoverNavigatorAction
	 */
	public void setShowCoverNavigatorAction(
			final Action showCoverNavigatorAction) {
		this.showCoverNavigatorAction = showCoverNavigatorAction;
	}

	/**
	 * @param addRadioAction
	 */
	public void setAddRadioAction(final Action addRadioAction) {
		this.addRadioAction = addRadioAction;
	}

	/**
	 * @param showRadioBrowserAction
	 */
	public void setShowRadioBrowserAction(final Action showRadioBrowserAction) {
		this.showRadioBrowserAction = showRadioBrowserAction;
	}

	/**
	 * @param addPodcastFeedAction
	 */
	public void setAddPodcastFeedAction(final Action addPodcastFeedAction) {
		this.addPodcastFeedAction = addPodcastFeedAction;
	}

	/**
	 * @param customSearchAction
	 */
	public void setCustomSearchAction(final Action customSearchAction) {
		this.customSearchAction = customSearchAction;
	}

	/**
	 * @param importLovedTracksFromLastFMAction
	 */
	public void setImportLovedTracksFromLastFMAction(
			final Action importLovedTracksFromLastFMAction) {
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
		add(showRecommendedEventsFromLastFMAction);
	}
}
