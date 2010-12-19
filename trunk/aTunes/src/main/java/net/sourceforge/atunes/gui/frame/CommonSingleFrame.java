/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;

/**
 * Common code for single frames. Right now all single frames have two vertical split panes, a tool bar and a status bar
 * 
 * Components are named from left to right:  A | B | C where "|" is a split pane divider
 */
public abstract class CommonSingleFrame extends AbstractSingleFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6728998633745513923L;

    protected static final String LEFT_VERTICAL_SPLIT_PANE = "1";
    protected static final String RIGHT_VERTICAL_SPLIT_PANE = "2";

    protected CustomSplitPane leftVerticalSplitPane;
    protected CustomSplitPane rightVerticalSplitPane;

	public CommonSingleFrame() {
		super();
	}
    
    
	@Override
	protected Container getContentPanel() {
        // Main Container
        JPanel panel = new JPanel(new GridBagLayout());

        // Main Split Pane          
        leftVerticalSplitPane = new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Save divider size
        defaultDividerSize = leftVerticalSplitPane.getDividerSize();

        GridBagConstraints c = new GridBagConstraints();

        JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
        rightVerticalSplitPane = new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightVerticalSplitPane.setBorder(BorderFactory.createEmptyBorder());
        rightVerticalSplitPane.setResizeWeight(1);
        rightVerticalSplitPane.setLeftComponent(getComponentB());
        rightVerticalSplitPane.setRightComponent(getComponentC());

        nonNavigatorPanel.add(rightVerticalSplitPane, BorderLayout.CENTER);

        leftVerticalSplitPane.setLeftComponent(getComponentA());
        leftVerticalSplitPane.setRightComponent(nonNavigatorPanel);
        leftVerticalSplitPane.setResizeWeight(0.2);

        leftVerticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().putSplitPaneDividerPos(LEFT_VERTICAL_SPLIT_PANE, (Integer) evt.getNewValue());
            }
        });

        rightVerticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().putSplitPaneDividerPos(RIGHT_VERTICAL_SPLIT_PANE, ((Integer) evt.getNewValue()));
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(getToolBar(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1;
        panel.add(leftVerticalSplitPane, c);

        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(getStatusBar(), c);

        return panel;
	}

	@Override
	protected void setupSplitPaneDividerPosition(FrameState frameState) {
        applySplitPaneDividerPosition(leftVerticalSplitPane, frameState.getSplitPaneDividerPos(LEFT_VERTICAL_SPLIT_PANE), 0.5);
        applySplitPaneDividerPosition(rightVerticalSplitPane, frameState.getSplitPaneDividerPos(RIGHT_VERTICAL_SPLIT_PANE), 0.5);
	}
	
	@Override
	protected void applySplitPaneDividerPosition(JSplitPane splitPane, int location, double relPos) {
		// Avoid right component to have a width less than its minimum size
		if (splitPane.getWidth() > 0) {
			int rightWidth = splitPane.getWidth() - location;
			int rightMinWidth = (int) splitPane.getRightComponent().getMinimumSize().getWidth(); 
			if (rightMinWidth > rightWidth) {
				location = location - (rightMinWidth - rightWidth + defaultDividerSize);
			}
		}
		super.applySplitPaneDividerPosition(splitPane, location, relPos);
	}

	@Override
	protected Dimension getContextPanelMinimumSize() {
		return new Dimension(CommonSingleFrameSizes.CONTEXT_PANEL_MINIMUM_WIDTH, CommonSingleFrameSizes.NOT_SIGNIFICANT_DIMENSION);
	}
	
	@Override
	protected Dimension getContextPanelPreferredSize() {
		return new Dimension(CommonSingleFrameSizes.CONTEXT_PANEL_PREFERRED_WIDTH, CommonSingleFrameSizes.NOT_SIGNIFICANT_DIMENSION);
	}
	
	@Override
	protected Dimension getContextPanelMaximumSize() {
		return new Dimension(CommonSingleFrameSizes.CONTEXT_PANEL_MAXIMUM_WIDTH, CommonSingleFrameSizes.NOT_SIGNIFICANT_DIMENSION);
	}
	
	@Override
	protected Dimension getPlayListPanelMinimumSize() {
		return new Dimension(CommonSingleFrameSizes.PLAY_LIST_PANEL_MINIMUM_WIDTH, CommonSingleFrameSizes.PLAY_LIST_PANEL_MINIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getPlayListPanelPreferredSize() {
		return new Dimension(CommonSingleFrameSizes.PLAY_LIST_PANEL_PREFERRED_WIDTH, CommonSingleFrameSizes.PLAY_LIST_PANEL_PREFERRED_HEIGHT);
	}
	
	@Override
	protected Dimension getPlayListPanelMaximumSize() {
		return new Dimension(CommonSingleFrameSizes.PLAY_LIST_PANEL_MAXIMUM_WIDTH, CommonSingleFrameSizes.PLAY_LIST_PANEL_MAXIMUM_HEIGHT);
	}
	
	@Override
	protected Dimension getPropertiesPanelMinimumSize() {
		return new Dimension(1, CommonSingleFrameSizes.AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT);
	}
	
	@Override
	protected Dimension getPropertiesPanelPreferredSize() {
		return new Dimension(1, CommonSingleFrameSizes.AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT);
	}
	
	@Override
	protected Dimension getPropertiesPanelMaximumSize() {
		return new Dimension(1, CommonSingleFrameSizes.AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT);
	}
	
	@Override
	protected Dimension getWindowMinimumSize() {
		return new Dimension(CommonSingleFrameSizes.WINDOW_MINIMUM_WIDTH, CommonSingleFrameSizes.WINDOW_MINIMUM_HEIGHT);
	}
	
	
	protected abstract JComponent getComponentA();
	protected abstract JComponent getComponentB();
	protected abstract JComponent getComponentC();
}
