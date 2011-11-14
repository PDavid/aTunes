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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.IProgressSlider;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ProgressSlider extends JPanel implements IProgressSlider {

    private static final long serialVersionUID = 8921834666233975274L;

    private static final int SECONDS_10 = 10000;
    private static final int SECONDS_30 = 30000;
    private static final int MINUTES_1 = 60000;
    private static final int MINUTES_2 = 120000;
    private static final int MINUTES_5 = 300000;
    private static final int MINUTES_10 = 600000;
    private static final int MINUTES_30 = 1800000;

    private JLabel time;
    private JLabel remainingTime;
    private JSlider progressBar;
    
    private IState state;

    public ProgressSlider() {
        super(new GridBagLayout());
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
     * @param state
     */
    @Override
	public void setState(IState state) {
		this.state = state;
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
     * Shows or hides ticks and labels in progress bar
     * @param showTicks
     */
    @Override
	public void setShowTicksAndLabels(boolean showTicks) {
        setPaintLabels(showTicks);
        setPaintTicks(showTicks);
    }
    
	/**
	 * Delegate method
	 * @param showTicks
	 */
	private void setPaintLabels(boolean showTicks) {
		progressBar.setPaintLabels(showTicks);
	}

	/**
	 * Delegate method
	 * @param showTicks
	 */
	private void setPaintTicks(boolean showTicks) {
		progressBar.setPaintTicks(showTicks);
		
		// Some look and feels (system look and feel) need to invalidate and repaint component to show/hide ticks
		progressBar.invalidate();
		progressBar.repaint();
		
		// Update minimum size
        setMinimumSize(new Dimension(progressBar.getPreferredSize().width + time.getPreferredSize().width * 2, progressBar.getPreferredSize().height + time.getPreferredSize().height));
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

	/**
	 * Delegate method
	 * @param dictionary
	 */
	private void setSliderLabels(Dictionary<?, ?> dictionary) {
		progressBar.setLabelTable(dictionary);
	}

	/**
	 * Delegate method
	 * @param majorTickSpacing
	 */
	private void setMajorTickSpacing(int majorTickSpacing) {
		progressBar.setMajorTickSpacing(majorTickSpacing);
		
	}

	/**
	 * Delegate method
	 * @param minorTickSpacing
	 */
	private void setMinorTickSpacing(int minorTickSpacing) {
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
	
    /**
     * Setup ticks spacing
     * 
     * @param length
     */
    @Override
	public void setupProgressTicks(long length) {
        int minorTickSpacing = SECONDS_10;
        int majorTickSpacing = SECONDS_30;

        if (length > MINUTES_10 && length <= MINUTES_30) {
            minorTickSpacing = SECONDS_30;
            majorTickSpacing = MINUTES_1;

        } else if (length > MINUTES_30) {
            minorTickSpacing = MINUTES_1;
            majorTickSpacing = MINUTES_5;
        }

        //avoid NullPointerException 
        setSliderLabels(null);

        setPaintTicks(state.isShowTicks());
        setMajorTickSpacing(majorTickSpacing);
        setMinorTickSpacing(minorTickSpacing);
        setupTicksLabels(length);
    }

    /**
     * Setup ticks labels
     * 
     * @param length
     */
    private void setupTicksLabels(long length) {
        Map<Integer, JLabel> ticksLabels = new HashMap<Integer, JLabel>();

        for (int k = 0; k < length; k++) {

            if (length < MINUTES_1) {
                if (k % SECONDS_10 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }

            } else if (length > MINUTES_1 && length <= MINUTES_10) {
                if (k % MINUTES_1 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }

            } else if (length > MINUTES_10 && length <= MINUTES_30) {
                if (k % MINUTES_2 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }

            } else {
                if (k % MINUTES_10 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }
            }
        }
        setPaintLabels(state.isShowTicks() && ticksLabels.size() > 0);
        if (ticksLabels.size() > 0) {
        	setSliderLabels(new Hashtable<Integer, JLabel>(ticksLabels));
        }
    }
    
    /**
     * Get label for duration
     * 
     * @param duration
     * @return the label for duration
     */
    private JLabel getLabelForDuration(int unit) {
        String duration = StringUtils.milliseconds2String(unit);
        JLabel label = new JLabel(duration, SwingConstants.CENTER);
        Font currentFont = label.getFont();
        label.setFont(new Font(currentFont.getFontName(), currentFont.getStyle(), Math.max(currentFont.getSize() - 3, 7)));
        return label;
    }

	@Override
	public JPanel getSwingComponent() {
		return this;
	}    
}

