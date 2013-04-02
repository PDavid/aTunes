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
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Shows or hides context panel
 * 
 * @author alex
 * 
 */
public class ShowContextAction extends CustomAbstractAction {

	private static final long serialVersionUID = 5939730387818346294L;

	private IContextHandler contextHandler;

	private IStateContext stateContext;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
	 * Default constructor
	 */
	public ShowContextAction() {
		super(I18nUtils.getString("SHOW_CONTEXT_INFORMATION"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_I,
						GuiUtils.getCtrlOrMetaActionEventMask(this.osManager)));
		putValue(SELECTED_KEY, stateContext.isUseContext());
	}

	@Override
	protected void executeAction() {
		contextHandler.showContextPanel((Boolean) getValue(SELECTED_KEY));
	}
}
