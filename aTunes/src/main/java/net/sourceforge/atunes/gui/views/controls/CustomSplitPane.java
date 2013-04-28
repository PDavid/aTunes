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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;

import javax.swing.JSplitPane;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * @author alex
 * 
 *         JSplitPane does not support component orientation, so we must do this
 *         manually http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
 * 
 */
public class CustomSplitPane extends JSplitPane implements
		ILookAndFeelChangeListener {

	private static final long serialVersionUID = 7760369696865269164L;

	private final IControlsBuilder controlsBuilder;

	private final ILookAndFeelManager lookAndFeelManager;

	CustomSplitPane(final int newOrientation,
			final IControlsBuilder controlsBuilder,
			final ILookAndFeelManager lookAndFeelManager) {
		super(newOrientation);
		this.controlsBuilder = controlsBuilder;
		this.lookAndFeelManager = lookAndFeelManager;
		this.lookAndFeelManager.addLookAndFeelChangeListener(this);
		this.lookAndFeelManager.getCurrentLookAndFeel()
				.customizeSplitPane(this);
	}

	@Override
	public void setLeftComponent(final Component comp) {
		if (!this.controlsBuilder.getComponentOrientation().isLeftToRight()
				&& getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			super.setRightComponent(comp);
		} else {
			super.setLeftComponent(comp);
		}
	}

	@Override
	public void setRightComponent(final Component comp) {
		if (!this.controlsBuilder.getComponentOrientation().isLeftToRight()
				&& getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			super.setLeftComponent(comp);
		} else {
			super.setRightComponent(comp);
		}
	}

	@Override
	public void lookAndFeelChanged() {
		this.lookAndFeelManager.getCurrentLookAndFeel()
				.customizeSplitPane(this);
	}
}
