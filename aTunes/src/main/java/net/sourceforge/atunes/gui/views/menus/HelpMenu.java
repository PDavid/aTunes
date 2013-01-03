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

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Help menu
 * @author alex
 *
 */
public class HelpMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private IOSManager osManager;
	
	private Action goToWebSiteAction;
	private Action goToWikiAction;
	private Action reportBugOrFeatureRequestAction;
	private Action showLogAction;
	private Action checkUpdatesAction;
	private Action showAboutAction;
	private Action makeDonationAction;
	
	/**
	 * @param makeDonationAction
	 */
	public void setMakeDonationAction(Action makeDonationAction) {
		this.makeDonationAction = makeDonationAction;
	}
	
	/**
	 * @param i18nKey
	 */
	public HelpMenu(String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * @param goToWebSiteAction
	 */
	public void setGoToWebSiteAction(Action goToWebSiteAction) {
		this.goToWebSiteAction = goToWebSiteAction;
	}
	
	/**
	 * @param goToWikiAction
	 */
	public void setGoToWikiAction(Action goToWikiAction) {
		this.goToWikiAction = goToWikiAction;
	}
	
	/**
	 * @param reportBugOrFeatureRequestAction
	 */
	public void setReportBugOrFeatureRequestAction(Action reportBugOrFeatureRequestAction) {
		this.reportBugOrFeatureRequestAction = reportBugOrFeatureRequestAction;
	}
	
	/**
	 * @param showLogAction
	 */
	public void setShowLogAction(Action showLogAction) {
		this.showLogAction = showLogAction;
	}
	
	/**
	 * @param checkUpdatesAction
	 */
	public void setCheckUpdatesAction(Action checkUpdatesAction) {
		this.checkUpdatesAction = checkUpdatesAction;
	}
	
	/**
	 * @param showAboutAction
	 */
	public void setShowAboutAction(Action showAboutAction) {
		this.showAboutAction = showAboutAction;
	}
	
	/**
	 * Initializes menu
	 */
	public void initialize() {
        add(goToWebSiteAction);
        add(goToWikiAction);
        add(reportBugOrFeatureRequestAction);
        add(new JSeparator());
        add(makeDonationAction);
        add(new JSeparator());
        add(showLogAction);
        add(checkUpdatesAction);
        if (!osManager.areMenuEntriesDelegated()) {
        	add(new JSeparator());
        	add(showAboutAction);
        }
	}
}
