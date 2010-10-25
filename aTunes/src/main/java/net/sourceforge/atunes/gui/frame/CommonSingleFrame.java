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

import java.awt.BorderLayout;
import java.awt.Container;
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

	protected abstract JComponent getComponentA();
	protected abstract JComponent getComponentB();
	protected abstract JComponent getComponentC();
}
