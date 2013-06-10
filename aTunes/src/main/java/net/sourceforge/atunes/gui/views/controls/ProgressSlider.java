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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IProgressSlider;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * Slider to show and move progress of an audio object
 * 
 * @author alex
 * 
 */
public class ProgressSlider extends JPanel implements IProgressSlider {

	private static final long serialVersionUID = 8921834666233975274L;

	private JLabel time;
	private JLabel remainingTime;
	private JProgressBar progressBar;

	private boolean paintIcon = true;

	private boolean paintIconAllowed;

	/**
	 * @param paintIconAllowed
	 */
	public void setPaintIconAllowed(final boolean paintIconAllowed) {
		this.paintIconAllowed = paintIconAllowed;
	}

	/**
	 * Builds a new progress slider
	 */
	public ProgressSlider() {
		super(new GridBagLayout());
	}

	/**
	 * Initialize component
	 */
	public void initialize() {
		this.time = new JLabel();
		this.time.setHorizontalAlignment(SwingConstants.RIGHT);
		setTimeSize(this.time);

		this.progressBar = new JProgressBar();
		this.progressBar.setVisible(false);
		this.progressBar.setFocusable(false);
		this.progressBar.setBorder(BorderFactory.createEmptyBorder());

		this.remainingTime = new JLabel();
		this.remainingTime.setHorizontalAlignment(SwingConstants.LEFT);
		setTimeSize(this.remainingTime);

		setLayout();
	}

	private void setTimeSize(JLabel timeLabel) {
		// Need enough space to show time for long audio objects
		timeLabel.setPreferredSize(new Dimension(70, 0));
		timeLabel.setMinimumSize(new Dimension(70, 0));
		timeLabel.setMaximumSize(new Dimension(70, 0));
	}

	/**
	 * Sets played time
	 * 
	 * @param time
	 * @param remainingTime
	 * @param fading
	 */
	@Override
	public void setProgress(final long time, final long remainingTime,
			final boolean fading) {
		boolean showDeterminateControls = !fading && time != 0
				&& remainingTime != 0;
		boolean showIndeterminateControls = !fading && time > remainingTime
				&& remainingTime == 0;

		showControls(time, remainingTime, showDeterminateControls,
				showIndeterminateControls);

		if (this.paintIconAllowed) {
			boolean previousPaintIcon = this.paintIcon;
			this.paintIcon = !showDeterminateControls
					&& !showIndeterminateControls;
			if (previousPaintIcon != this.paintIcon) {
				repaint();
			}
		}
	}

	/**
	 * @param time
	 * @param remainingTime
	 * @param showDeterminateControls
	 * @param showIndeterminateControls
	 */
	private void showControls(final long time, final long remainingTime,
			final boolean showDeterminateControls,
			final boolean showIndeterminateControls) {

		this.time.setText(getTimeText(time, showDeterminateControls,
				showIndeterminateControls));

		this.remainingTime.setText(getRemainingTime(remainingTime,
				showDeterminateControls, showIndeterminateControls));

		this.progressBar.setVisible(showDeterminateControls
				|| showIndeterminateControls);

		this.progressBar.setIndeterminate(showIndeterminateControls);
	}

	/**
	 * @param time
	 * @param showDeterminateControls
	 * @param showIndeterminateControls
	 * @return
	 */
	private String getTimeText(final long time,
			final boolean showDeterminateControls,
			final boolean showIndeterminateControls) {
		if (showDeterminateControls && !showIndeterminateControls) {
			return time > 0 ? TimeUtils.millisecondsToHoursMinutesSeconds(time)
					: "";
		} else {
			return "";
		}
	}

	/**
	 * @param remainingTime
	 * @param showDeterminateControls
	 * @param showIndeterminateControls
	 * @return
	 */
	private String getRemainingTime(final long remainingTime,
			final boolean showDeterminateControls,
			final boolean showIndeterminateControls) {
		if (showDeterminateControls && !showIndeterminateControls) {
			return remainingTime > 0 ? StringUtils.getString("- ",
					TimeUtils.millisecondsToHoursMinutesSeconds(remainingTime))
					: "";
		} else {
			return "";
		}
	}

	/**
	 * Arrange components
	 */
	private void setLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 3, 0);
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(this.time, c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.weighty = 1;
		add(getProgressBarPanel(), c);
		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 3, 0);
		add(this.remainingTime, c);
	}

	/**
	 * @return
	 */
	private JPanel getProgressBarPanel() {
		JPanel progressBarPanel = new JPanel(new GridBagLayout());
		progressBarPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 15, 0, 15);
		progressBarPanel.add(this.progressBar, c);
		return progressBarPanel;
	}

	/**
	 * Delegate method
	 * 
	 * @param value
	 */
	@Override
	public void setValue(final int value) {
		this.progressBar.setValue(value);
	}

	/**
	 * Delegate method
	 * 
	 * @return
	 */
	@Override
	public int getMaximum() {
		return this.progressBar.getMaximum();
	}

	/**
	 * Delegate method
	 * 
	 * @return
	 */
	@Override
	public int getProgressBarWidth() {
		return this.progressBar.getWidth();
	}

	/**
	 * Delegate method
	 * 
	 * @param length
	 */
	@Override
	public void setMaximum(final int length) {
		this.progressBar.setMaximum(length);
	}

	/**
	 * Delegate method
	 * 
	 * @return
	 */
	@Override
	public int getValue() {
		return this.progressBar.getValue();
	}

	@Override
	public void setFont(final Font font) {
		// Can be null when calling "super"
		if (this.time != null) {
			this.time.setFont(font);
		}
		if (this.remainingTime != null) {
			this.remainingTime.setFont(font);
		}
	}

	@Override
	public synchronized void addMouseListener(final MouseListener l) {
		this.progressBar.addMouseListener(l);
	}

	@Override
	public void setOpaque(final boolean isOpaque) {
		super.setOpaque(isOpaque);
		if (this.time != null) {
			this.time.setOpaque(isOpaque);
		}
		if (this.remainingTime != null) {
			this.remainingTime.setOpaque(isOpaque);
		}
		if (this.progressBar != null) {
			this.progressBar.setOpaque(isOpaque);
		}
	}

	@Override
	public JPanel getSwingComponent() {
		return this;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (this.paintIcon && this.paintIconAllowed) {
			int width = getWidth();

			Graphics2D graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			ImageIcon icon = Images.getImage(Images.APP_LOGO_48);

			graphics.drawImage(icon.getImage(), width / 2 - icon.getIconWidth()
					/ 2, 2, null);
		}
	}
}
