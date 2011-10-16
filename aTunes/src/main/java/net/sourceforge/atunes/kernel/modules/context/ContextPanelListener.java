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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import net.sourceforge.atunes.model.IContextPanel;

/**
 * Listens for context panel combo changes and calls to update
 * @author alex
 */
final class ContextPanelListener implements ItemListener {
	
	/**
	 * Context Handler
	 */
	private final ContextHandler contextHandler;

	/**
	 * Context panel selected
	 */
	private String contextPanel;

	/**
	 * @param contextHandler
	 * @param contextPanel
	 */
	ContextPanelListener(ContextHandler contextHandler, String contextPanel) {
		this.contextHandler = contextHandler;
		this.contextPanel = contextPanel;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			String newPanel = ((IContextPanel)e.getItem()).getContextPanelName();
			if (!newPanel.equals(contextPanel)) {
				contextPanel = newPanel;
				this.contextHandler.setContextTab(contextPanel);
			}
		}
	}
}