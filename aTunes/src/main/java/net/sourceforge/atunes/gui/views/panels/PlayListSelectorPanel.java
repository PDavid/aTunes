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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Component;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;

/**
 * Selects between different play lists
 * 
 * @author alex
 * 
 */
public final class PlayListSelectorPanel extends JPanel implements
		IPlayListSelectorPanel {

	private static final long serialVersionUID = 7382098268271937439L;

	private IFilterPanel playListFilterPanel;

	/**
	 * Instantiates a new play list tab panel.
	 */
	public PlayListSelectorPanel() {
		super(new GridBagLayout());
	}

	/**
	 * @param playListFilterPanel
	 */
	public void setPlayListFilterPanel(final IFilterPanel playListFilterPanel) {
		this.playListFilterPanel = playListFilterPanel;
	}

	/**
	 * Adds the content.
	 */
	public void initialize() {
	}

	/**
	 * @return
	 */
	public IFilterPanel getPlayListFilterPanel() {
		return this.playListFilterPanel;
	}

	@Override
	public Component getSwingComponent() {
		return this;
	}
}
