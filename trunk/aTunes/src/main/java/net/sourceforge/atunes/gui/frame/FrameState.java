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

package net.sourceforge.atunes.gui.frame;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IFrameState;

/**
 * State of aTunes main frame.
 */
public final class FrameState implements Serializable, IFrameState {

	private static final long serialVersionUID = 455297656338188247L;
	
	private Map<String, Integer> splitPaneDividerPositions;
	
	private int xPosition = -1;
	
	private int yPosition = -1;
	
	private boolean maximized;
	
	private int windowWidth;
	
	private int windowHeight;

	public FrameState() {
        this(new HashMap<String, Integer>());
    }

    public FrameState(IFrameState frameState) {
        this(frameState.getSplitPaneDividerPositions());
        this.xPosition = frameState.getXPosition();
        this.yPosition = frameState.getYPosition();
        this.maximized = frameState.isMaximized();
        this.windowWidth = frameState.getWindowWidth();
        this.windowHeight = frameState.getWindowHeight();
    }

    @ConstructorProperties("splitPaneDividerPositions")
    private FrameState(Map<String, Integer> splitPaneDividerPositions) {
        this.splitPaneDividerPositions = new HashMap<String, Integer>(splitPaneDividerPositions);
    }

    @Override
	public void putSplitPaneDividerPos(String s, int pos) {
    	// Don't save pos == 0, as it's due to a component being not visible
    	if (pos > 0) {
    		splitPaneDividerPositions.put(s, pos);
    	}
    }

    @Override
	public int getSplitPaneDividerPos(String s) {
        Integer value = splitPaneDividerPositions.get(s);
        if (value == null) {
            value = 0;
        }
        return value;
    }

    @Override
	public Map<String, Integer> getSplitPaneDividerPositions() {
        return splitPaneDividerPositions;
    }
    
    @Override
	public int getXPosition() {
		return xPosition;
	}

	@Override
	public void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	@Override
	public int getYPosition() {
		return yPosition;
	}

	@Override
	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	@Override
	public boolean isMaximized() {
		return maximized;
	}

	@Override
	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	@Override
	public int getWindowWidth() {
		return windowWidth;
	}

	@Override
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	@Override
	public int getWindowHeight() {
		return windowHeight;
	}

	@Override
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (maximized ? 1231 : 1237);
		result = prime
				* result
				+ ((splitPaneDividerPositions == null) ? 0
						: splitPaneDividerPositions.hashCode());
		result = prime * result + windowHeight;
		result = prime * result + windowWidth;
		result = prime * result + xPosition;
		result = prime * result + yPosition;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FrameState other = (FrameState) obj;
		if (maximized != other.maximized) {
			return false;
		}
		if (!isSameSplitPaneDividerPositions(other) || !isEqualSize(other) || !isSamePosition(other)) {
			return false;
		}
		return true;
	}

	/**
	 * @param other
	 */
	private boolean isSameSplitPaneDividerPositions(FrameState other) {
		if (splitPaneDividerPositions == null) {
			if (other.splitPaneDividerPositions != null) {
				return false;
			}
		} else if (!splitPaneDividerPositions
				.equals(other.splitPaneDividerPositions)) {
			return false;
		}
		return true;
	}

	/**
	 * @param other
	 */
	private boolean isSamePosition(FrameState other) {
		if (xPosition != other.xPosition) {
			return false;
		}
		if (yPosition != other.yPosition) {
			return false;
		}
		return true;
	}

	/**
	 * @param other
	 */
	private boolean isEqualSize(FrameState other) {
		if (windowHeight != other.windowHeight) {
			return false;
		}
		if (windowWidth != other.windowWidth) {
			return false;
		}
		return true;
	}
	
	
}
