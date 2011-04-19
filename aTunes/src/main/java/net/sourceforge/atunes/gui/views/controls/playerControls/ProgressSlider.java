/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Dictionary;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ProgressSlider extends JPanel {

    private static final long serialVersionUID = 8921834666233975274L;

    private JLabel time;
    private JLabel remainingTime;
    private JSlider progressBar;

    public ProgressSlider() {
        super(new GridBagLayout());
        time = new JLabel();
        time.setHorizontalAlignment(SwingConstants.CENTER);
        // Set enough width to allow more than three digits
        time.setPreferredSize(new Dimension(50, 10));

        progressBar = new JSlider();
        progressBar.setToolTipText(I18nUtils.getString("CLICK_TO_SEEK"));
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setFocusable(false);

        remainingTime = new JLabel();
        remainingTime.setHorizontalAlignment(SwingConstants.CENTER);
        // Set enough width to allow more than three digits
        remainingTime.setPreferredSize(new Dimension(50, 10));
        setOpaque(false);

        setExpandable(true);
    }

    /**
     * Sets played time
     * @param time in milliseconds
     */
    public void setProgress(long time, long remainingTime) {
        this.time.setText(time > 0 ? StringUtils.milliseconds2String(time) : "");
        this.remainingTime.setText(remainingTime > 0 ? StringUtils.getString("- ", StringUtils.milliseconds2String(remainingTime)) : "");
       	this.progressBar.setVisible(time != 0 || remainingTime != 0);
    }

    /**
     * Sets if progress slider should expand to fit all available space
     * @param expandable
     */
    public void setExpandable(boolean expandable) {
        removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(2, 10, 0, 0);
        add(time, c);
        c.gridx = 1;
        c.weightx = expandable ? 1 : 0;
        c.weighty = 1;
        c.insets = new Insets(3, 5, 0, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, c);
        c.gridx = 2;
        c.weightx = 0;
        c.insets = new Insets(2, 0, 0, 5);
        c.fill = GridBagConstraints.NONE;
        add(remainingTime, c);

    }

	/**
	 * Delegate method
	 * @param showTicks
	 */
	public void setPaintLabels(boolean showTicks) {
		progressBar.setPaintLabels(showTicks);
	}

	/**
	 * Delegate method
	 * @param showTicks
	 */
	public void setPaintTicks(boolean showTicks) {
		progressBar.setPaintTicks(showTicks);
		
		// Some look and feels (system look and feel) need to invalidate and repaint component to show/hide ticks
		progressBar.invalidate();
		progressBar.repaint();
	}

	/**
	 * Delegate method
	 * @param value
	 */
	public void setValue(int value) {
		progressBar.setValue(value);
	}

	/**
	 * Delegate method
	 * @return
	 */
	public int getMaximum() {
		return progressBar.getMaximum();
	}

	/**
	 * Delegate method
	 * @return
	 */
	public int getProgressBarWidth() {
		return progressBar.getWidth();
	}

	/**
	 * Delegate method
	 * @param length
	 */
	public void setMaximum(int length) {
		progressBar.setMaximum(length);
	}

	/**
	 * Delegate method
	 * @return
	 */
	public int getValue() {
		return progressBar.getValue();
	}

	/**
	 * Delegate method
	 * @param dictionary
	 */
	public void setLabelTable(Dictionary<?, ?> dictionary) {
		progressBar.setLabelTable(dictionary);
	}

	/**
	 * Delegate method
	 * @param majorTickSpacing
	 */
	public void setMajorTickSpacing(int majorTickSpacing) {
		progressBar.setMajorTickSpacing(majorTickSpacing);
		
	}

	/**
	 * Delegate method
	 * @param minorTickSpacing
	 */
	public void setMinorTickSpacing(int minorTickSpacing) {
		progressBar.setMinorTickSpacing(minorTickSpacing);
	}
	
	@Override
	public void setFont(Font font) {
		// Can be null when calling "super"
		if (time != null) {
			time.setFont(font);
		}
		if (remainingTime != null) {
			remainingTime.setFont(font);
		}
	}
	
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		progressBar.addMouseListener(l);
	}
	
	@Override
	public synchronized void addKeyListener(KeyListener l) {
		progressBar.addKeyListener(l);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		progressBar.setEnabled(enabled);
	}
	
	@Override
	public void setOpaque(boolean isOpaque) {
		super.setOpaque(isOpaque);
		if (time != null) {
			time.setOpaque(isOpaque);
		}
		if (remainingTime != null) {
			remainingTime.setOpaque(isOpaque);
		}
		if (progressBar != null) {
			progressBar.setOpaque(isOpaque);
		}
	}
}

