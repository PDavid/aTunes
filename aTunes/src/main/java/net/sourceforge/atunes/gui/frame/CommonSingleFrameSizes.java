package net.sourceforge.atunes.gui.frame;

import net.sourceforge.atunes.utils.GuiUtils;

public class CommonSingleFrameSizes {

	public static final int NOT_SIGNIFICANT_DIMENSION = 1;
	
	/*
	 * Frame minimum size
	 */
	public static final int WINDOW_MINIMUM_WIDTH = 655;
	public static final int WINDOW_MINIMUM_HEIGHT = 410;
	
    /*
     * Navigation tree sizes
     */
	public static final int NAVIGATION_TREE_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 280);
    public static final int NAVIGATION_TREE_MINIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(1024, 280);
    
	public static final int NAVIGATION_TREE_PREFERRED_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 280);
    public static final int NAVIGATION_TREE_PREFERRED_HEIGHT = GuiUtils.getComponentHeightForResolution(1024, 350);
    
	public static final int NAVIGATION_TREE_MAXIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 280);
    public static final int NAVIGATION_TREE_MAXIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(1024, 700);

    /*
     * Context panel width 
     */
    public static final int CONTEXT_PANEL_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 250);
    public static final int CONTEXT_PANEL_PREFERRED_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 300);
    public static final int CONTEXT_PANEL_MAXIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 400);

    /**
     * Properties panel height
     */
    public static final int AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT = 100;

    /*
     * Play list panel widths 
     */
    public static final int PLAY_LIST_PANEL_MINIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 490);
    public static final int PLAY_LIST_PANEL_PREFERRED_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 600);
    public static final int PLAY_LIST_PANEL_MAXIMUM_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 700);

}
