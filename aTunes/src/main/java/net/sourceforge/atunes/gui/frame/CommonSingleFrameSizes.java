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
