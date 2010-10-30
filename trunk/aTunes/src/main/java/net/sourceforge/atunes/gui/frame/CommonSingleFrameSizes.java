/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.utils.GuiUtils;

public class CommonSingleFrameSizes {

	public static final int NOT_SIGNIFICANT_DIMENSION = 1;
	
	/*
	 * Frame minimum size
	 */
	public static final int WINDOW_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.65f);
	public static final int WINDOW_MINIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.5f);
	
    /*
     * Navigation panel sizes
     */
	public static final int NAVIGATION_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.12f);
    public static final int NAVIGATION_MINIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.15f);
    
	public static final int NAVIGATION_PREFERRED_WIDTH = GuiUtils.getComponentWidthForResolution(0.15f);
    public static final int NAVIGATION_PREFERRED_HEIGHT = GuiUtils.getComponentHeightForResolution(0.3f);
    
	public static final int NAVIGATION_MAXIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.2f);
    public static final int NAVIGATION_MAXIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.6f);

    /*
     * Context panel width 
     */
    public static final int CONTEXT_PANEL_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.2f);
    public static final int CONTEXT_PANEL_PREFERRED_WIDTH = GuiUtils.getComponentWidthForResolution(0.25f);
    public static final int CONTEXT_PANEL_MAXIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.4f);

    /**
     * Properties panel height
     */
    public static final int AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT = 100;

    /*
     * Play list panel widths 
     */
    public static final int PLAY_LIST_PANEL_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.38f);
    public static final int PLAY_LIST_PANEL_MINIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.2f);
    public static final int PLAY_LIST_PANEL_PREFERRED_WIDTH = GuiUtils.getComponentWidthForResolution(0.48f);
    public static final int PLAY_LIST_PANEL_PREFERRED_HEIGHT = GuiUtils.getComponentHeightForResolution(0.3f);
    public static final int PLAY_LIST_PANEL_MAXIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(0.55f);
    public static final int PLAY_LIST_PANEL_MAXIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.5f);

}
