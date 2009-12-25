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

import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The default frame.
 */
public final class DefaultSingleFrame extends AbstractSingleFrame implements net.sourceforge.atunes.gui.frame.Frame {

    private static final long serialVersionUID = 1L;

    private static final int SPLIT_PANE_DEFAULT_DIVIDER_SIZE = 10;

    private JSplitPane leftVerticalSplitPane;
    private JSplitPane rightVerticalSplitPane;
    private JSplitPane navigatorSplitPane;

    /**
     * Gets the content panel.
     * 
     * @return the content panel
     */
    protected Container getContentPanel() {
        // Main Container
        JPanel panel = new JPanel(new GridBagLayout());

        // Main Split Pane          
        leftVerticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Create menu bar
        setJMenuBar(getAppMenuBar());

        GridBagConstraints c = new GridBagConstraints();

        // Play List, File Properties, Context panel
        JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
        rightVerticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
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

        // JSplitPane does not support component orientation, so we must do this manually
        // -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
        if (GuiUtils.getComponentOrientation().isLeftToRight()) {
            rightVerticalSplitPane.add(centerPanel);
            rightVerticalSplitPane.add(getContextPanel());
        } else {
            rightVerticalSplitPane.add(getContextPanel());
            rightVerticalSplitPane.add(centerPanel);
        }

        nonNavigatorPanel.add(rightVerticalSplitPane, BorderLayout.CENTER);

        // Navigation Panel
        // JSplitPane does not support component orientation, so we must do this manually
        // -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
        if (GuiUtils.getComponentOrientation().isLeftToRight()) {
            leftVerticalSplitPane.add(getNavigatorSplitPane());
            leftVerticalSplitPane.add(nonNavigatorPanel);
            leftVerticalSplitPane.setResizeWeight(0.2);
        } else {
            leftVerticalSplitPane.add(nonNavigatorPanel);
            leftVerticalSplitPane.add(getNavigatorSplitPane());
            rightVerticalSplitPane.setResizeWeight(0.2);
        }

        leftVerticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().setLeftVerticalSplitPaneDividerLocation((Integer) evt.getNewValue());
            }
        });

        rightVerticalSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getFrameState().setRightVerticalSplitPaneDividerLocation(((Integer) evt.getNewValue()));
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

    protected void setupSplitPaneDividerPosition(FrameState frameState) {
        // Split panes divider location
        if (frameState.getLeftVerticalSplitPaneDividerLocation() != 0) {
            setLeftVerticalSplitPaneDividerLocationAndSetWindowSize(frameState.getLeftVerticalSplitPaneDividerLocation());
        }
        if (frameState.getRightVerticalSplitPaneDividerLocation() != 0) {
            setRightVerticalSplitPaneDividerLocationAndSetWindowSize(frameState.getRightVerticalSplitPaneDividerLocation());
        }
    }

    /**
     * Sets the right vertical split pane divider location and set window size.
     * 
     * @param location
     *            the new right vertical split pane divider location and set
     *            window size
     */
    private void setRightVerticalSplitPaneDividerLocationAndSetWindowSize(int location) {
        rightVerticalSplitPane.setDividerLocation(location);
        setWindowSize();
    }

    /**
     * Sets the left vertical split pane divider location and set window size.
     * 
     * @param location
     *            the new left vertical split pane divider location and set
     *            window size
     */
    private void setLeftVerticalSplitPaneDividerLocationAndSetWindowSize(int location) {
        leftVerticalSplitPane.setDividerLocation(location);
        setWindowSize();
    }

    private JSplitPane getNavigatorSplitPane() {
        if (navigatorSplitPane == null) {
            navigatorSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getNavigationTreePanel(), getNavigationTablePanel());
            navigatorSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    DefaultSingleFrame.this.getFrameState().setLeftHorizontalSplitPaneDividerLocation((Integer) evt.getNewValue());
                }
            });
        }
        return navigatorSplitPane;
    }

    @Override
    public void showContextPanel(boolean show, boolean changeSize) {
        boolean wasVisible = getContextPanel().isVisible();
        getContextPanel().setVisible(show);
        if (!wasVisible && show) {
            int panelWidth = getPlayListPanel().getWidth();
            int rightDividerLocation = getFrameState().getRightVerticalSplitPaneDividerLocation();
            if (rightDividerLocation != 0 && rightDividerLocation < (panelWidth - AbstractSingleFrame.CONTEXT_PANEL_WIDTH)) {
                rightVerticalSplitPane.setDividerLocation(getFrameState().getRightVerticalSplitPaneDividerLocation());
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
            getFrameState().setRightVerticalSplitPaneDividerLocation(rightVerticalSplitPane.getDividerLocation());
        }
        if (show) {
            rightVerticalSplitPane.setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        } else {
            rightVerticalSplitPane.setDividerSize(0);
        }
    }

    @Override
    public void showNavigationTable(boolean show) {
        getNavigationTablePanel().setVisible(show);
        if (show) {
            super.setVisible(show);
            getNavigatorSplitPane().setDividerLocation(getFrameState().getLeftHorizontalSplitPaneDividerLocation());
            getNavigatorSplitPane().setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        } else {
            // Save location
            getFrameState().setLeftHorizontalSplitPaneDividerLocation(getNavigatorSplitPane().getDividerLocation());
            getNavigatorSplitPane().setDividerSize(0);
        }
    }

    @Override
    public void showNavigationTree(boolean show) {
        // TODO Auto-generated method stub
    }

}
