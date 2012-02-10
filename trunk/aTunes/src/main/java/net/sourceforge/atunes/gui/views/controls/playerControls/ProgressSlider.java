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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IProgressSlider;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ProgressSlider extends JPanel implements IProgressSlider {

    private static final long serialVersionUID = 8921834666233975274L;

    private JLabel time;
    private JLabel remainingTime;
    private JSlider progressBar;
    
    private boolean paintIcon = true;
    
    private boolean paintIconAllowed;
    
    /**
     * @param paintIconAllowed
     */
    public void setPaintIconAllowed(boolean paintIconAllowed) {
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
        time = new JLabel();
        time.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JSlider();
        progressBar.setToolTipText(I18nUtils.getString("CLICK_TO_SEEK"));
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setFocusable(false);
        
        remainingTime = new JLabel();
        remainingTime.setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);

        setLayout();
    }
    
    /**
     * Sets played time
     * @param time in milliseconds
     */
    @Override
	public void setProgress(long time, long remainingTime) {
        this.time.setText(time > 0 ? StringUtils.milliseconds2String(time) : "");
        this.remainingTime.setText(remainingTime > 0 ? StringUtils.getString("- ", StringUtils.milliseconds2String(remainingTime)) : "");
       	this.progressBar.setVisible(time != 0 || remainingTime != 0);
       	
       	if (paintIconAllowed) {
       		boolean previousPaintIcon = paintIcon;
       		this.paintIcon = !this.progressBar.isVisible();
       		if (previousPaintIcon != paintIcon) {
       			repaint();
       		}
       	}
    }

    /**
     * Sets layout
     */
    @Override
	public void setLayout() {
        removeAll();
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 0.1;
        c.insets = new Insets(2, 10, 0, 0);
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(time, c);
        c.gridx = 1;
        c.weightx = 0.8;
        c.insets = new Insets(0, 5, 0, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, c);
        c.gridx = 2;
        c.weightx = 0.1;
        c.insets = new Insets(2, 5, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        add(remainingTime, c);

    }

	/**
	 * Delegate method
	 * @param value
	 */
	@Override
	public void setValue(int value) {
		progressBar.setValue(value);
	}

	/**
	 * Delegate method
	 * @return
	 */
	@Override
	public int getMaximum() {
		return progressBar.getMaximum();
	}

	/**
	 * Delegate method
	 * @return
	 */
	@Override
	public int getProgressBarWidth() {
		return progressBar.getWidth();
	}

	/**
	 * Delegate method
	 * @param length
	 */
	@Override
	public void setMaximum(int length) {
		progressBar.setMaximum(length);
	}

	/**
	 * Delegate method
	 * @return
	 */
	@Override
	public int getValue() {
		return progressBar.getValue();
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
	
	@Override
	public JPanel getSwingComponent() {
		return this;
	}    
	
	/** Double values for Horizontal and Vertical radius of corner arcs */
    protected Dimension arcs = new Dimension(20, 20);
    
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (paintIcon && paintIconAllowed) {
			int width = getWidth();
			int height = getHeight();

			Graphics2D graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			ImageIcon icon = ImageUtils.resize(Images.getImage(Images.APP_LOGO_90), width, height - 4);

			graphics.drawImage(icon.getImage(), width / 2 - icon.getIconWidth() / 2, 2, null);
		}

	}
}

