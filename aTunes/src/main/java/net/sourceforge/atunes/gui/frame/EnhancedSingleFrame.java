/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;
import net.sourceforge.atunes.utils.GuiUtils;

public final class EnhancedSingleFrame extends MainSplitPaneLeftSingleFrame implements net.sourceforge.atunes.gui.frame.Frame {

    private static final long serialVersionUID = 1L;

    private static final String PLAYLIST_SPLIT_PANE = "3";

    private CustomSplitPane playListSplitPane;

    private static final int NAVIGATION_TABLE_MINIMUM_WIDTH = CommonSingleFrameSizes.PLAY_LIST_PANEL_MINIMUM_WIDTH;
    private static final int NAVIGATION_TABLE_MINIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.1f);
   
    private static final int NAVIGATION_TABLE_PREFERRED_WIDTH = CommonSingleFrameSizes.PLAY_LIST_PANEL_PREFERRED_WIDTH;
    private static final int NAVIGATION_TABLE_PREFERRED_HEIGHT = GuiUtils.getComponentHeightForResolution(0.25f);
    
    private static final int NAVIGATION_TABLE_MAXIMUM_WIDTH = CommonSingleFrameSizes.PLAY_LIST_PANEL_MAXIMUM_WIDTH;
    private static final int NAVIGATION_TABLE_MAXIMUM_HEIGHT = GuiUtils.getComponentHeightForResolution(0.4f);

    @Override
    protected void setupSplitPaneDividerPosition(FrameState frameState) {
    	super.setupSplitPaneDividerPosition(frameState);
        applySplitPaneDividerPosition(playListSplitPane, frameState.getSplitPaneDividerPos(PLAYLIST_SPLIT_PANE), 0.5);
    }

    @Override
    public void showContextPanel(boolean show) {
        applyVisibility(show, RIGHT_SPLIT_PANE, getContextPanel(), rightSplitPane);
    }

    @Override
    public void showNavigationTree(boolean show) {
        applyVisibility(show, LEFT_SPLIT_PANE, getNavigationTreePanel(), leftSplitPane);
    }

    @Override
    public void showNavigationTable(boolean show) {
        applyVisibility(show, PLAYLIST_SPLIT_PANE, getNavigationTablePanel(), playListSplitPane);
    }
    
    @Override
    protected JComponent getComponentA() {
    	return getNavigationTreePanel();
    }
    
    @Override
    protected JComponent getComponentB() {
        playListSplitPane = new CustomSplitPane(JSplitPane.VERTICAL_SPLIT);
        playListSplitPane.setLeftComponent(getNavigationTablePanel());
        playListSplitPane.setRightComponent(getPlayListPanel());
        playListSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().putSplitPaneDividerPos(PLAYLIST_SPLIT_PANE, (Integer) evt.getNewValue());
                storeFrameState();
            }
        });
        return playListSplitPane;
    }
    
    @Override
    protected JComponent getComponentC() {
    	return getContextPanel();
    }
    
	@Override
	protected Dimension getNavigationTablePanelMinimumSize() {
		return new Dimension(NAVIGATION_TABLE_MINIMUM_WIDTH, NAVIGATION_TABLE_MINIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getNavigationTablePanelPreferredSize() {
		return new Dimension(NAVIGATION_TABLE_PREFERRED_WIDTH, NAVIGATION_TABLE_PREFERRED_HEIGHT);
	}

	@Override
	protected Dimension getNavigationTablePanelMaximumSize() {
		return new Dimension(NAVIGATION_TABLE_MAXIMUM_WIDTH, NAVIGATION_TABLE_MAXIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getNavigationTreePanelMinimumSize() {
		return new Dimension(CommonSingleFrameSizes.NAVIGATION_MINIMUM_WIDTH, CommonSingleFrameSizes.NAVIGATION_MINIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getNavigationTreePanelPreferredSize() {
		return new Dimension(CommonSingleFrameSizes.NAVIGATION_PREFERRED_WIDTH, CommonSingleFrameSizes.NAVIGATION_PREFERRED_HEIGHT);
	}
	
	@Override
	protected Dimension getNavigationTreePanelMaximumSize() {
		return new Dimension(CommonSingleFrameSizes.NAVIGATION_MAXIMUM_WIDTH, CommonSingleFrameSizes.NAVIGATION_MAXIMUM_HEIGHT);
	}
	
	@Override
	public Map<String, Double> getDefaultSplitPaneRelativePositions() {
		Map<String, Double> values = new HashMap<String, Double>();
		values.put(LEFT_SPLIT_PANE, 0.15);
		values.put(RIGHT_SPLIT_PANE, 0.85);
		values.put(PLAYLIST_SPLIT_PANE, 0.5);
		return values;
	}

	@Override
	protected int getLeftSplitType() {
		return JSplitPane.HORIZONTAL_SPLIT;
	}
	
	@Override
	protected int getRightSplitType() {
		return JSplitPane.HORIZONTAL_SPLIT;
	}

}
