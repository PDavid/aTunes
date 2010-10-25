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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
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
    }

    @Override
    public void showNavigationTable(boolean show) {
        applyVisibility(show, NAVIGATOR_SPLIT_PANE, getNavigationTablePanel(), navigatorSplitPane);
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
            }
        });
    	return navigatorSplitPane;
    }
    
    @Override
    protected JComponent getComponentB() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
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
        return centerPanel;
    }
    
    @Override
    protected JComponent getComponentC() {
    	return getContextPanel();
    }
}

