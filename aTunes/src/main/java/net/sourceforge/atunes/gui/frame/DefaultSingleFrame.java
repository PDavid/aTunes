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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;
import net.sourceforge.atunes.model.IFrameState;

/**
 * The default frame.
 */
public final class DefaultSingleFrame extends MainSplitPaneLeftSingleFrame implements net.sourceforge.atunes.model.IFrame {

    private static final long serialVersionUID = 1L;

    private static final String NAVIGATOR_SPLIT_PANE = "3";

    private CustomSplitPane navigatorSplitPane;

    @Override
    protected void setupSplitPaneDividerPosition(IFrameState frameState) {
    	super.setupSplitPaneDividerPosition(frameState);
        applySplitPaneDividerPosition(navigatorSplitPane, frameState.getSplitPaneDividerPos(NAVIGATOR_SPLIT_PANE), 0.5);
    }

    @Override
    public void showContextPanel(boolean show) {
        applyVisibility(show, RIGHT_SPLIT_PANE, getContextPanel().getSwingComponent(), rightSplitPane);
    }

    @Override
    public void showNavigationTree(boolean show) {
        applyVisibility(show, NAVIGATOR_SPLIT_PANE, getNavigationTreePanel().getSwingComponent(), navigatorSplitPane);
        checkNavigatorSplitPaneVisibility();
    }

    @Override
    public void showNavigationTable(boolean show) {
        applyVisibility(show, NAVIGATOR_SPLIT_PANE, getNavigationTablePanel().getSwingComponent(), navigatorSplitPane);
        checkNavigatorSplitPaneVisibility();
    }
    
    /**
     * Check if navigator split pane must be visible or not based on tree and table visibility
     */
    private void checkNavigatorSplitPaneVisibility() {
    	boolean navigatorSplitPaneVisible = getNavigationTreePanel().isVisible() || getNavigationTablePanel().isVisible();
   		applyVisibility(navigatorSplitPaneVisible, LEFT_SPLIT_PANE, navigatorSplitPane, leftSplitPane);
    }
    
    @Override
    protected JComponent getComponentA() {
        navigatorSplitPane = new CustomSplitPane(JSplitPane.VERTICAL_SPLIT);
        navigatorSplitPane.setLeftComponent(getNavigationTreePanel().getSwingComponent());
        navigatorSplitPane.setRightComponent(getNavigationTablePanel().getSwingComponent());
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
    	return getContextPanel().getSwingComponent();
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
		values.put(LEFT_SPLIT_PANE, 0.3);
		values.put(RIGHT_SPLIT_PANE, 0.6);
		values.put(NAVIGATOR_SPLIT_PANE, 0.5);
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

