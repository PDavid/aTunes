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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class OSDDialog.
 */
public final class OSDDialog extends AbstractCustomWindow {

	private static final long serialVersionUID = 8991547440913162267L;
	private static final int IMAGE_SIZE = 80;

	private int width;
	private final int height = 100;
	private JLabel line1;
	private JLabel line2;
	private JLabel line3;
	private JLabel image;

	/**
	 * Vertical position of line 1
	 */
	private static final int LINE1_Y_POSITION = 15;

	/**
	 * Vertical position of line 2
	 */
	private static final int LINE2_Y_POSITION = 37;

	/**
	 * Vertical position of line 3
	 */
	private static final int LINE3_Y_POSITION = 60;

	private final ILookAndFeel lookAndFeel;

	/**
	 * Instantiates a new osd dialog.
	 * 
	 * @param width
	 * @param lookAndFeel
	 * @param controlsBuilder
	 */
	public OSDDialog(final int width, final ILookAndFeel lookAndFeel,
			final IControlsBuilder controlsBuilder) {
		super(null, 0, 0, controlsBuilder);
		this.width = width;
		this.lookAndFeel = lookAndFeel;
		setSize(width, this.height);
		setFocusableWindowState(false);
		setAlwaysOnTop(true);
		add(getContent());
	}

	/**
	 * Set width of OSD
	 * 
	 * @param width
	 */
	public void setWidth(final int width) {
		this.width = width;
		setSize(width, this.height);
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(null);
		panel.setSize(this.width, this.height);
		this.image = new JLabel();
		this.image.setOpaque(true);
		this.line1 = new JLabel();
		this.line2 = new JLabel();
		this.line3 = new JLabel();

		this.line1.setFont(this.lookAndFeel.getOsdLine1Font());
		this.line2.setFont(this.lookAndFeel.getOsdLine2Font());
		this.line3.setFont(this.lookAndFeel.getOsdLine3Font());

		this.line1.setHorizontalAlignment(SwingConstants.CENTER);
		this.line2.setHorizontalAlignment(SwingConstants.CENTER);
		this.line3.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(this.image);
		panel.add(this.line1);
		panel.add(this.line2);
		panel.add(this.line3);
		return panel;
	}

	/**
	 * Sets the image.
	 * 
	 * @param img
	 *            the new image
	 */
	public void setImage(final ImageIcon img) {
		ImageIcon imgResized = img != null ? ImageUtils.resize(img, IMAGE_SIZE,
				IMAGE_SIZE) : null;
		if (imgResized != null) {
			this.image.setIcon(imgResized);
			this.image.setSize(imgResized.getIconWidth() + 5,
					imgResized.getIconHeight() + 5);
			this.image.setLocation(10, (this.height - IMAGE_SIZE) / 2);
			this.line1.setSize(this.width - 100, 20);
			this.line1.setLocation(90, LINE1_Y_POSITION);
			this.line2.setSize(this.width - 100, 20);
			this.line2.setLocation(90, LINE2_Y_POSITION);
			this.line3.setSize(this.width - 100, 20);
			this.line3.setLocation(90, LINE3_Y_POSITION);
		} else {
			this.image.setSize(0, 0);
			this.line1.setSize(this.width - 20, 20);
			this.line1.setLocation(10, LINE1_Y_POSITION);
			this.line2.setSize(this.width - 20, 20);
			this.line2.setLocation(10, LINE2_Y_POSITION);
			this.line3.setSize(this.width - 20, 20);
			this.line3.setLocation(10, LINE3_Y_POSITION);
		}
	}

	/**
	 * Sets the line1.
	 * 
	 * @param text
	 *            the new line1
	 */
	public void setLine1(final String text) {
		this.line1.setText(text);
	}

	/**
	 * Sets the line2.
	 * 
	 * @param text
	 *            the new line2
	 */
	public void setLine2(final String text) {
		this.line2.setText(text);
	}

	/**
	 * Sets the line3.
	 * 
	 * @param text
	 *            the new line3
	 */
	public void setLine3(final String text) {
		this.line3.setText(text);
	}

	/**
	 * Sets rounded borders.
	 * 
	 * @param set
	 *            the set
	 */
	public void setRoundedBorders(final boolean set) {
		Shape mask = null;
		if (set) {
			mask = new Area(new RoundRectangle2D.Float(2, 2, this.width - 3,
					this.height - 3, 20, 25));
		}
		GuiUtils.setWindowShape(this, mask);
	}
}
