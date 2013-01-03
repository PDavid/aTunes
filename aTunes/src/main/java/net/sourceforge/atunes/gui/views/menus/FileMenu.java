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
 * File menu
 * @author alex
 *
 */
public class FileMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private IOSManager osManager;
	
	private Action addFolderToRepositoryAction;
	private Action refreshRepositoryAction;
	private Action importToRepositoryAction;
	private Action exitAction;
	
	/**
	 * @param i18nKey
	 */
	public FileMenu(String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * @param addFolderToRepositoryAction
	 */
	public void setAddFolderToRepositoryAction(Action addFolderToRepositoryAction) {
		this.addFolderToRepositoryAction = addFolderToRepositoryAction;
	}
	
	/**
	 * @param refreshRepositoryAction
	 */
	public void setRefreshRepositoryAction(Action refreshRepositoryAction) {
		this.refreshRepositoryAction = refreshRepositoryAction;
	}
	
	/**
	 * @param importToRepositoryAction
	 */
	public void setImportToRepositoryAction(Action importToRepositoryAction) {
		this.importToRepositoryAction = importToRepositoryAction;
	}
	
	/**
	 * @param exitAction
	 */
	public void setExitAction(Action exitAction) {
		this.exitAction = exitAction;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * Initializes menu
	 */
	public void initialize() {
        add(addFolderToRepositoryAction);
        add(refreshRepositoryAction);
        add(new JSeparator());
        add(importToRepositoryAction);
        if (!osManager.areMenuEntriesDelegated()) {
        	add(new JSeparator());
        	add(exitAction);
        }
	}
}
