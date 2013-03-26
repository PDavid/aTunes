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

import java.awt.Color;

import javax.swing.UIManager;

import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IUrlLabel;

import org.jdesktop.swingx.JXHyperlink;

/**
 * A label which opens browser when user clicks
 * 
 * @author alex
 * 
 */
public final class UrlLabel extends JXHyperlink implements IUrlLabel {

	private static final long serialVersionUID = -8368596300673361747L;

	private final IDesktop desktop;

	/**
	 * Instantiates a new url label.
	 * 
	 * @param desktop
	 */
	UrlLabel(final IDesktop desktop) {
		super();
		this.desktop = desktop;
	}

	/**
	 * Instantiates a new url label.
	 * 
	 * @param desktop
	 * @param text
	 * @param url
	 */
	UrlLabel(final IDesktop desktop, final String text, final String url) {
		super(new Link(desktop, url));
		this.desktop = desktop;
		setTextAndColor(text);
	}

	@Override
	public void setText(final String text, final String url) {
		setAction(new Link(this.desktop, url));
		setTextAndColor(text);
	}

	/**
	 * Sets the text and color.
	 * 
	 * @param text
	 *            the new text and color
	 */
	private void setTextAndColor(final String text) {
		setText(text);
		setForeground((Color) UIManager.get("Label.foreground"));
	}
}
