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

package net.sourceforge.atunes.gui.frame;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * State of aTunes main frame.
 */
public final class FrameState implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 455297656338188247L;
	
	private Map<String, Integer> splitPaneDividerPositions;
	
	private int xPosition = -1;
	
	private int yPosition = -1;
	
	private boolean maximized;
	
	private int windowWidth;
	
	private int windowHeight;

	public FrameState() {
        this.splitPaneDividerPositions = new HashMap<String, Integer>();
    }

    public FrameState(FrameState frameState) {
        this.splitPaneDividerPositions = new HashMap<String, Integer>(frameState.splitPaneDividerPositions);
    }

    @ConstructorProperties("splitPaneDividerPositions")
    public FrameState(Map<String, Integer> splitPaneDividerPositions) {
        this.splitPaneDividerPositions = splitPaneDividerPositions;
    }

    public void putSplitPaneDividerPos(String s, int pos) {
        splitPaneDividerPositions.put(s, pos);
    }

    public int getSplitPaneDividerPos(String s) {
        Integer value = splitPaneDividerPositions.get(s);
        if (value == null) {
            value = 0;
        }
        return value;
    }

    public Map<String, Integer> getSplitPaneDividerPositions() {
        return splitPaneDividerPositions;
    }
    
    public int getXPosition() {
		return xPosition;
	}

	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
}
