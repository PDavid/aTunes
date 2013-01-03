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

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IFrameState;

/**
 * State of aTunes main frame.
 */
public final class FrameState implements Serializable, IFrameState {

	private static final long serialVersionUID = -6195147915384832556L;
	
	private Map<String, Integer> splitPaneDividerPositions;
	
	/**
	 * Default constructor
	 */
	public FrameState() {
        this(new HashMap<String, Integer>());
    }

    /**
     * @param frameState
     */
    public FrameState(IFrameState frameState) {
        this(frameState.getSplitPaneDividerPositions());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((splitPaneDividerPositions == null) ? 0
						: splitPaneDividerPositions.hashCode());
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
		if (splitPaneDividerPositions == null) {
			if (other.splitPaneDividerPositions != null) {
				return false;
			}
		} else if (!splitPaneDividerPositions.equals(other.splitPaneDividerPositions)) {
			return false;
		}
		return true;
	}
}
