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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Displays custom search dialog
 * 
 * @author fleax
 * 
 */
public class CustomSearchAction extends CustomAbstractAction {

	private static final long serialVersionUID = 7036619806075628842L;

	private ISearchHandler searchHandler;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param searchHandler
	 */
	public void setSearchHandler(final ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}

	/**
	 * Default constructor
	 */
	public CustomSearchAction() {
		super(I18nUtils.getString("SEARCH"));
	}

	@Override
	protected void initialize() {
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F,
						GuiUtils.getCtrlOrMetaActionEventMask(this.osManager)));
	}

	@Override
	protected void executeAction() {
		this.searchHandler.startSearch();
	}
}
