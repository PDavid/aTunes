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

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
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

    protected static final String LEFT_SPLIT_PANE = "1";
    protected static final String RIGHT_SPLIT_PANE = "2";

    protected CustomSplitPane leftSplitPane;
    protected CustomSplitPane rightSplitPane;

    
	@Override
	protected Container getContentPanel() {
        // Main Container
        JPanel panel = new JPanel(new GridBagLayout());

        // Main Split Pane          
        leftSplitPane = new CustomSplitPane(getLeftSplitType());
        
        GridBagConstraints c = new GridBagConstraints();

        JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
        rightSplitPane = new CustomSplitPane(getRightSplitType());
        rightSplitPane.setBorder(BorderFactory.createEmptyBorder());
        rightSplitPane.setResizeWeight(0.5);
        rightSplitPane.setLeftComponent(getRightSplitPaneLeftComponent());
        rightSplitPane.setRightComponent(getRightSplitPaneRightComponent());

        nonNavigatorPanel.add(rightSplitPane, BorderLayout.CENTER);

        leftSplitPane.setLeftComponent(getLeftSplitPaneLeftComponent());
        leftSplitPane.setRightComponent(getLeftSplitPaneRightComponent());
        leftSplitPane.setResizeWeight(0.5);

        leftSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().putSplitPaneDividerPos(LEFT_SPLIT_PANE, (Integer) evt.getNewValue());
                storeFrameState();
            }
        });

        rightSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().putSplitPaneDividerPos(RIGHT_SPLIT_PANE, ((Integer) evt.getNewValue()));
                storeFrameState();
            }
        });

        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;

        c.gridy = state.isShowPlayerControlsOnTop() ? 1 : 0;
        c.weighty = 1;
        panel.add(getMainSplitPane(), c);

        c.gridy = state.isShowPlayerControlsOnTop() ? 0 : 1;
        c.weighty = 0;
        panel.add(getPlayerControls(), c);

        
        
        c.gridy = 2;
        c.weighty = 0;
        panel.add(getStatusBar(), c);

        return panel;
	}

	protected abstract JSplitPane getMainSplitPane();
	protected abstract JComponent getLeftSplitPaneLeftComponent();
	protected abstract JComponent getLeftSplitPaneRightComponent();
	protected abstract JComponent getRightSplitPaneLeftComponent();
	protected abstract JComponent getRightSplitPaneRightComponent();
	
	@Override
	protected void setupSplitPaneDividerPosition(FrameState frameState) {
        applySplitPaneDividerPosition(leftSplitPane, frameState.getSplitPaneDividerPos(LEFT_SPLIT_PANE), 0.5);
        applySplitPaneDividerPosition(rightSplitPane, frameState.getSplitPaneDividerPos(RIGHT_SPLIT_PANE), 0.5);
	}
	
	@Override
	protected void applySplitPaneDividerPosition(JSplitPane splitPane, int location, double relPos) {
		if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			applyHorizontalSplitPaneDividerPosition(splitPane, location, relPos);
		} else {
			applyVerticalSplitPaneDividerPosition(splitPane, location, relPos);
		}
	}
	
	/**
	 * Places split pane divider position in an horizontal split pane
	 * @param splitPane
	 * @param location
	 * @param relPos
	 */
	private void applyHorizontalSplitPaneDividerPosition(JSplitPane splitPane, int location, double relPos) {		
		// Avoid right component to have a width less than its minimum size
		if (splitPane.getWidth() > 0) {
			int rightWidth = splitPane.getWidth() - location;
			int rightMinWidth = (int) splitPane.getRightComponent().getMinimumSize().getWidth(); 
			if (rightMinWidth > rightWidth) {
				location = location - (rightMinWidth - rightWidth + LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getSplitPaneDividerSize());
			}
		}
		super.applySplitPaneDividerPosition(splitPane, location, relPos);
	}

	/**
	 * Places split pane divider position in a vertical split pane
	 * @param splitPane
	 * @param location
	 * @param relPos
	 */
	private void applyVerticalSplitPaneDividerPosition(JSplitPane splitPane, int location, double relPos) {		
		// Avoid bottom component to have a height less than its minimum size
		if (splitPane.getHeight() > 0) {
			int rightHeight = splitPane.getHeight() - location;
			int rightMinHeight = (int) splitPane.getRightComponent().getMinimumSize().getHeight(); 
			if (rightMinHeight > rightHeight) {
				location = location - (rightMinHeight - rightHeight + LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getSplitPaneDividerSize());
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
	protected Dimension getWindowMinimumSize() {
		return new Dimension(CommonSingleFrameSizes.WINDOW_MINIMUM_WIDTH, CommonSingleFrameSizes.WINDOW_MINIMUM_HEIGHT);
	}
	
	
	protected abstract JComponent getComponentA();
	protected abstract JComponent getComponentB();
	protected abstract JComponent getComponentC();
	
	protected abstract int getLeftSplitType();
	protected abstract int getRightSplitType();
}
