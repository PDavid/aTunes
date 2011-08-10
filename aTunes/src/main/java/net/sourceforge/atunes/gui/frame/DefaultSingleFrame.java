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

/**
 * The default frame.
 */
public final class DefaultSingleFrame extends CommonSingleFrame implements net.sourceforge.atunes.gui.frame.Frame {

    private static final long serialVersionUID = 1L;

    private static final String NAVIGATOR_SPLIT_PANE = "3";

    private CustomSplitPane navigatorSplitPane;

    public DefaultSingleFrame() {
    	super();
    }
    
    @Override
    protected void setupSplitPaneDividerPosition(FrameState frameState) {
    	super.setupSplitPaneDividerPosition(frameState);
        applySplitPaneDividerPosition(navigatorSplitPane, frameState.getSplitPaneDividerPos(NAVIGATOR_SPLIT_PANE), 0.5);
    }

    @Override
    public void showContextPanel(boolean show) {
        applyVisibility(show, RIGHT_VERTICAL_SPLIT_PANE, getContextPanel(), rightVerticalSplitPane);
    }

    @Override
    public void showNavigationTree(boolean show) {
        applyVisibility(show, NAVIGATOR_SPLIT_PANE, getNavigationTreePanel(), navigatorSplitPane);
        checkNavigatorSplitPaneVisibility();
    }

    @Override
    public void showNavigationTable(boolean show) {
        applyVisibility(show, NAVIGATOR_SPLIT_PANE, getNavigationTablePanel(), navigatorSplitPane);
        checkNavigatorSplitPaneVisibility();
    }
    
    /**
     * Check if navigator split pane must be visible or not based on tree and table visibility
     */
    private void checkNavigatorSplitPaneVisibility() {
    	boolean navigatorSplitPaneVisible = getNavigationTreePanel().isVisible() || getNavigationTablePanel().isVisible();
   		applyVisibility(navigatorSplitPaneVisible, LEFT_VERTICAL_SPLIT_PANE, navigatorSplitPane, leftVerticalSplitPane);
    }
    
    @Override
    protected JComponent getComponentA() {
        navigatorSplitPane = new CustomSplitPane(JSplitPane.VERTICAL_SPLIT);
        navigatorSplitPane.setLeftComponent(getNavigationTreePanel());
        navigatorSplitPane.setRightComponent(getNavigationTablePanel());
        navigatorSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().putSplitPaneDividerPos(NAVIGATOR_SPLIT_PANE, (Integer) evt.getNewValue());
                storeFrameState();
            }
        });
    	return navigatorSplitPane;
    }
    
    @Override
    protected JComponent getComponentB() {
    	return getPlayListPanel();
    }
    
    @Override
    protected JComponent getComponentC() {
    	return getContextPanel();
    }
    
	@Override
	protected Dimension getNavigationTablePanelMinimumSize() {
		return new Dimension(CommonSingleFrameSizes.NAVIGATION_MINIMUM_WIDTH, CommonSingleFrameSizes.NAVIGATION_MINIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getNavigationTablePanelPreferredSize() {
		return new Dimension(CommonSingleFrameSizes.NAVIGATION_PREFERRED_WIDTH, CommonSingleFrameSizes.NAVIGATION_PREFERRED_HEIGHT);
	}

	@Override
	protected Dimension getNavigationTablePanelMaximumSize() {
		return new Dimension(CommonSingleFrameSizes.NAVIGATION_MAXIMUM_WIDTH, CommonSingleFrameSizes.NAVIGATION_MAXIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getNavigationTreePanelMinimumSize() {
		return getNavigationTablePanelMinimumSize();
	}
	
	@Override
	protected Dimension getNavigationTreePanelPreferredSize() {
		return getNavigationTablePanelPreferredSize();
	}
	
	@Override
	protected Dimension getNavigationTreePanelMaximumSize() {
		return getNavigationTablePanelMaximumSize();
	}
	
	@Override
	public Map<String, Double> getDefaultSplitPaneRelativePositions() {
		Map<String, Double> values = new HashMap<String, Double>();
		values.put(LEFT_VERTICAL_SPLIT_PANE, 0.2);
		values.put(RIGHT_VERTICAL_SPLIT_PANE, 0.8);
		values.put(NAVIGATOR_SPLIT_PANE, 0.5);
		return values;
	}

}

