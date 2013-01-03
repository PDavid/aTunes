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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JWindow;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IControlsBuilder;

/**
 * A custom window
 * 
 * @author alex
 * 
 */
public abstract class AbstractCustomWindow extends JWindow {

	private static final long serialVersionUID = -8846047318549650938L;

	private final IControlsBuilder controlsBuilder;

	/**
	 * Creates a window with given parent (owner) and size
	 * 
	 * @param owner
	 * @param width
	 * @param height
	 * @param controlsBuilder
	 */
	public AbstractCustomWindow(final JFrame owner, final int width,
			final int height, final IControlsBuilder controlsBuilder) {
		super(owner);
		this.controlsBuilder = controlsBuilder;
		setSize(width, height);
		setLocationRelativeTo(owner);
		GuiUtils.addAppIcons(this);
	}

	@Override
	public Component add(final Component comp) {
		if (comp instanceof JComponent) {
			((JComponent) comp).setOpaque(false);
		}
		Component c = super.add(comp);
		this.controlsBuilder.applyComponentOrientation(this);
		return c;
	}
}
