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

package net.sourceforge.atunes.gui.frame;

import java.awt.Dimension;
import java.awt.Frame;

import net.sourceforge.atunes.model.IFrameSize;
import net.sourceforge.atunes.model.IStateUI;

/**
 * Calculates window size and applies to a frame
 * @author alex
 *
 */
public class WindowSizeCalculator {

	private Dimension screenSize;
	
	private Dimension margin;
	
	/**
	 * @param screenSize
	 */
	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}
	
	/**
	 * @param margin
	 */
	public void setMargin(Dimension margin) {
		this.margin = margin;
	}
	
	/**
     * Sets the window size.
     * @param frame
     * @param stateUI
     */
    public void setWindowSize(AbstractSingleFrame frame, IStateUI stateUI) {
    	IFrameSize frameSize = stateUI.getFrameSize();
        frame.setMinimumSize(frame.getWindowMinimumSize());
        if (frameSize.isMaximized()) {
            setWindowSizeMaximized(frame);
        } else {
            Dimension dimension = null;
            if (frameSize.getWindowWidth() != 0 && frameSize.getWindowHeight() != 0) {
                dimension = new Dimension(frameSize.getWindowWidth(), frameSize.getWindowHeight());
            }
            if (dimension == null) {
            	dimension = getDefaultWindowSize();
            }
            if (dimension != null) {
                frame.setSize(dimension);
            }
        }
    }
    
    /**
     * @param frame
     */
    private final void setWindowSizeMaximized(AbstractSingleFrame frame) {
        Dimension screen = frame.getToolkit().getScreenSize();
        frame.setSize(screen.width - margin.width, screen.height - margin.height);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }
    
    /**
     * Calculates default window size
     * @return
     */
    public Dimension getDefaultWindowSize() {
        // Set size always according to main device dimension 
    	return new Dimension(screenSize.width - margin.width, screenSize.height - margin.height);
    }
}
