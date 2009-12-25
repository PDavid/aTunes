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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;
import net.sourceforge.atunes.utils.GuiUtils;

public final class EnhancedSingleFrame extends AbstractSingleFrame implements net.sourceforge.atunes.gui.frame.Frame {

    private static final long serialVersionUID = 1L;

    private static final int SPLIT_PANE_DEFAULT_DIVIDER_SIZE = 10;

    private CustomSplitPane leftVerticalSplitPane;
    private CustomSplitPane rightVerticalSplitPane;
    private CustomSplitPane playListSplitPane;

    /**
     * Gets the content panel.
     * 
     * @return the content panel
     */
    protected Container getContentPanel() {
        // Main Container
        JPanel panel = new JPanel(new GridBagLayout());

        // Main Split Pane          
        leftVerticalSplitPane = new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Create menu bar
        setJMenuBar(getAppMenuBar());

        GridBagConstraints c = new GridBagConstraints();

        JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
        rightVerticalSplitPane = new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightVerticalSplitPane.setBorder(BorderFactory.createEmptyBorder());
        rightVerticalSplitPane.setResizeWeight(1);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        centerPanel.add(getPlayListPanel(), c);
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        centerPanel.add(getPropertiesPanel(), c);
        c.gridy = 2;
        centerPanel.add(getPlayerControls(), c);

        playListSplitPane = new CustomSplitPane(JSplitPane.VERTICAL_SPLIT);
        playListSplitPane.setLeftComponent(getNavigationTablePanel());
        playListSplitPane.setRightComponent(centerPanel);
        playListSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().setSplitPaneDividerLocation3((Integer) evt.getNewValue());
            }
        });

        rightVerticalSplitPane.setLeftComponent(playListSplitPane);
        rightVerticalSplitPane.setRightComponent(getContextPanel());

        nonNavigatorPanel.add(rightVerticalSplitPane, BorderLayout.CENTER);

        leftVerticalSplitPane.setLeftComponent(getNavigationTreePanel());
        leftVerticalSplitPane.setRightComponent(nonNavigatorPanel);
        leftVerticalSplitPane.setResizeWeight(0.2);

        leftVerticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().setSplitPaneDividerLocation1((Integer) evt.getNewValue());
            }
        });

        rightVerticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().setSplitPaneDividerLocation2(((Integer) evt.getNewValue()));
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        panel.add(getToolBar(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(leftVerticalSplitPane, c);

        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(getStatusBar(), c);

        return panel;
    }

    @Override
    protected void setupSplitPaneDividerPosition(FrameState frameState) {
        // Split panes divider location
        if (frameState.getSplitPaneDividerLocation1() != 0) {
            leftVerticalSplitPane.setDividerLocation(frameState.getSplitPaneDividerLocation1());
            setWindowSize();
        }
        if (frameState.getSplitPaneDividerLocation2() != 0) {
            rightVerticalSplitPane.setDividerLocation(frameState.getSplitPaneDividerLocation2());
            setWindowSize();
        }
        if (frameState.getSplitPaneDividerLocation3() != 0) {
            playListSplitPane.setDividerLocation((frameState.getSplitPaneDividerLocation3()));
        }
    }

    @Override
    public void showContextPanel(boolean show, boolean changeSize) {
        boolean wasVisible = getContextPanel().isVisible();
        getContextPanel().setVisible(show);
        if (!wasVisible && show) {
            int panelWidth = getPlayListPanel().getWidth();
            int rightDividerLocation = getFrameState().getSplitPaneDividerLocation2();
            if (rightDividerLocation != 0 && rightDividerLocation < (panelWidth - AbstractSingleFrame.CONTEXT_PANEL_WIDTH)) {
                rightVerticalSplitPane.setDividerLocation(getFrameState().getSplitPaneDividerLocation2());
            } else {
                rightVerticalSplitPane.setDividerLocation(rightVerticalSplitPane.getSize().width - AbstractSingleFrame.CONTEXT_PANEL_WIDTH);
            }
            panelWidth = panelWidth - AbstractSingleFrame.CONTEXT_PANEL_WIDTH;
            if (panelWidth < PLAY_LIST_PANEL_WIDTH && changeSize) {
                int diff = PLAY_LIST_PANEL_WIDTH - panelWidth;
                // If window is almost as big as device, move left vertical split pane to the left
                if (getSize().width + diff > GuiUtils.getDeviceWidth()) {
                    leftVerticalSplitPane.setDividerLocation(leftVerticalSplitPane.getLocation().x - diff);
                } else {
                    // Resize window
                    setSize(getSize().width + diff, getSize().height);
                }
            }
        } else if (!show) {
            // Save panel width
            getFrameState().setSplitPaneDividerLocation2(rightVerticalSplitPane.getDividerLocation());
        }
        if (show) {
            rightVerticalSplitPane.setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        } else {
            rightVerticalSplitPane.setDividerSize(0);
        }
    }

    // TODO Auto-generated method stub
    @Override
    public void showNavigationTable(boolean show) {
        //        getNavigationTablePanel().setVisible(show);
        //        if (show) {
        //            super.setVisible(show);
        //            getNavigatorSplitPane().setDividerLocation(getFrameState().getLeftHorizontalSplitPaneDividerLocation());
        //            getNavigatorSplitPane().setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        //        } else {
        //            // Save location
        //            getFrameState().setLeftHorizontalSplitPaneDividerLocation(getNavigatorSplitPane().getDividerLocation());
        //            getNavigatorSplitPane().setDividerSize(0);
        //        }
    }

    @Override
    public void showNavigationTree(boolean show) {
        // TODO Auto-generated method stub
    }

}
