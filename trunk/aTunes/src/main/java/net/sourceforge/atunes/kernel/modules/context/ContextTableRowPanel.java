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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.model.IControlsBuilder;

/**
 * A panel to render each row of a context table
 * 
 * @author alex
 * 
 * @param <T>
 */
public class ContextTableRowPanel<T> extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = 3227801852177772588L;

	private JLabel imageLabel;

	private JLabel textLabel;

	private PopUpButton button;

	/**
	 * Default constructor
	 */
	public ContextTableRowPanel() {
		setLayout(new GridBagLayout());
		setOpaque(false);
	}

	/**
	 * Updates colors
	 * 
	 * @param backgroundColor
	 * @param foregroundColor
	 */
	public void setColors(final Color backgroundColor,
			final Color foregroundColor) {
		if (backgroundColor != null) {
			this.textLabel.setBackground(backgroundColor);
			setBackground(backgroundColor);
			this.imageLabel.setBackground(backgroundColor);
		}
		if (foregroundColor != null) {
			this.textLabel.setForeground(foregroundColor);
		}
	}

	/**
	 * Sets image
	 * 
	 * @param image
	 */
	public void setImage(final ImageIcon image) {
		this.imageLabel = new JLabel(image);
		this.imageLabel.setOpaque(false);
	}

	/**
	 * Sets text
	 * 
	 * @param text
	 */
	public void setText(final String text) {
		this.textLabel = new JLabel(text);
		this.textLabel.setOpaque(false);
		this.textLabel.setVerticalAlignment(SwingConstants.TOP);
	}

	/**
	 * @param imageMaxWidth
	 * @param actions
	 * @param table
	 * @param controlsBuilder
	 */
	public void build(final int imageMaxWidth, final ContextTable table,
			final IControlsBuilder controlsBuilder) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, (imageMaxWidth + 20)
				/ 2
				- (this.imageLabel.getIcon() != null ? this.imageLabel
						.getIcon().getIconWidth() : 0) / 2, 0, 0);
		add(this.imageLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, (imageMaxWidth + 20)
				/ 2
				- (this.imageLabel.getIcon() != null ? this.imageLabel
						.getIcon().getIconWidth() : 0) / 2, 0, 0);
		add(this.textLabel, c);

		controlsBuilder.applyComponentOrientation(this);
	}

	/**
	 * Shows or hides options if panel has focus
	 * 
	 * @param hasFocus
	 */
	public void setFocus(final boolean hasFocus) {
		if (this.button != null) {
			this.button.setVisible(hasFocus);
		}
	}
}
