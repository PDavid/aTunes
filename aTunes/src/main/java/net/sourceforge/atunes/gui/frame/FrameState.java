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

    private int splitPaneDividerLocation1;
    private int splitPaneDividerLocation2;
    private int splitPaneDividerLocation3;

    public FrameState() {
    }

    public int getSplitPaneDividerLocation1() {
        return splitPaneDividerLocation1;
    }

    public void setSplitPaneDividerLocation1(int splitPaneDividerLocation1) {
        this.splitPaneDividerLocation1 = splitPaneDividerLocation1;
    }

    public int getSplitPaneDividerLocation2() {
        return splitPaneDividerLocation2;
    }

    public void setSplitPaneDividerLocation2(int splitPaneDividerLocation2) {
        this.splitPaneDividerLocation2 = splitPaneDividerLocation2;
    }

    public int getSplitPaneDividerLocation3() {
        return splitPaneDividerLocation3;
    }

    public void setSplitPaneDividerLocation3(int splitPaneDividerLocation3) {
        this.splitPaneDividerLocation3 = splitPaneDividerLocation3;
    }

}
