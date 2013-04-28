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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.gui.views.controls.FadeInPanel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class ExtendedToolTip. This is a special window shown as tooltip for
 * navigator tree objects
 */
public final class ExtendedToolTip extends AbstractCustomWindow {

	private static final long serialVersionUID = -5041702404982493070L;

	private final FadeInPanel imagePanel;
	private final JLabel image;
	private final JLabel line1;
	private final JLabel line2;
	private final JLabel line3;

	/**
	 * Instantiates a new extended tool tip.
	 * 
	 * @param controlsBuilder
	 * @param width
	 * @param height
	 */
	public ExtendedToolTip(final IControlsBuilder controlsBuilder,
			final int width, final int height) {
		super(null, width, height, controlsBuilder);

		setFocusableWindowState(false);
		JPanel container = new JPanel(new GridBagLayout());

		this.image = new JLabel();
		this.imagePanel = new FadeInPanel();
		this.imagePanel.setLayout(new GridLayout(1, 1));
		this.imagePanel.add(this.image);
		this.line1 = new JLabel();
		this.line2 = new JLabel();
		this.line3 = new JLabel();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(0, 5, 0, 0);
		container.add(this.imagePanel, c);
		c.gridx = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		// c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 0, 10);
		container.add(this.line1, c);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 10, 0, 10);
		container.add(this.line2, c);
		c.gridx = 1;
		c.gridy = 2;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(0, 10, 0, 10);
		container.add(this.line3, c);
		// Use scroll pane to draw a border consistent with look and feel
		JScrollPane scrollPane = controlsBuilder.createScrollPane(container);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		add(scrollPane);
	}

	/**
	 * Sets the text of line 1
	 * 
	 * @param text
	 * 
	 */
	public void setLine1(final String text) {
		this.line1.setText(text);
	}

	/**
	 * Sets the text of line 2
	 * 
	 * @param text
	 * 
	 */
	public void setLine2(final String text) {
		this.line2.setText(text);
	}

	/**
	 * Sets the image
	 * 
	 * @param img
	 *            the new image
	 */
	public void setImage(final ImageIcon img) {
		if (img != null) {
			// Add 50 to width to force images to fit height of tool tip as much
			// as possible
			this.image.setIcon(ImageUtils.scaleImageBicubic(img.getImage(),
					Constants.TOOLTIP_IMAGE_WIDTH + 50,
					Constants.TOOLTIP_IMAGE_HEIGHT));
			this.imagePanel.setVisible(true);
		} else {
			this.image.setIcon(null);
			this.imagePanel.setVisible(false);
		}
	}

	/**
	 * Sets the text of line 3
	 * 
	 * @param text
	 * 
	 */
	public void setLine3(final String text) {
		this.line3.setText(text);
	}
}
