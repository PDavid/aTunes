/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

/**
 * State of aTunes main frame.
 */
public class FrameState {

    private int leftVerticalSplitPaneDividerLocation = AbstractSingleFrame.NAVIGATION_PANEL_WIDTH;
    private int rightVerticalSplitPaneDividerLocation;
    private int leftHorizontalSplitPaneDividerLocation = AbstractSingleFrame.NAVIGATOR_SPLIT_PANE_DIVIDER_LOCATION;

    public FrameState() {
    }

    public int getLeftVerticalSplitPaneDividerLocation() {
        return leftVerticalSplitPaneDividerLocation;
    }

    public void setLeftVerticalSplitPaneDividerLocation(int leftVerticalSplitPaneDividerLocation) {
        this.leftVerticalSplitPaneDividerLocation = leftVerticalSplitPaneDividerLocation;
    }

    public int getRightVerticalSplitPaneDividerLocation() {
        return rightVerticalSplitPaneDividerLocation;
    }

    public void setRightVerticalSplitPaneDividerLocation(int rightVerticalSplitPaneDividerLocation) {
        this.rightVerticalSplitPaneDividerLocation = rightVerticalSplitPaneDividerLocation;
    }

    public int getLeftHorizontalSplitPaneDividerLocation() {
        return leftHorizontalSplitPaneDividerLocation;
    }

    public void setLeftHorizontalSplitPaneDividerLocation(int leftHorizontalSplitPaneDividerLocation) {
        this.leftHorizontalSplitPaneDividerLocation = leftHorizontalSplitPaneDividerLocation;
    }

}
