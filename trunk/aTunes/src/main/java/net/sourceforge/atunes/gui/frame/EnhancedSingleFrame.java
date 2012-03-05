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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;
import net.sourceforge.atunes.model.IFrameState;

public final class EnhancedSingleFrame extends MainSplitPaneLeftSingleFrame {

    private static final long serialVersionUID = 1L;

    private static final String PLAYLIST_SPLIT_PANE = "3";

    private CustomSplitPane playListSplitPane;

    @Override
    protected void setupSplitPaneDividerPosition(IFrameState frameState) {
    	super.setupSplitPaneDividerPosition(frameState);
        applySplitPaneDividerPosition(playListSplitPane, frameState.getSplitPaneDividerPos(PLAYLIST_SPLIT_PANE), 0.5);
    }

    @Override
    public void showContextPanel(boolean show) {
        applyVisibility(show, RIGHT_SPLIT_PANE, getContextPanel().getSwingComponent(), getRightSplitPane());
    }

    @Override
    public void showNavigationTree(boolean show) {
        applyVisibility(show, LEFT_SPLIT_PANE, getNavigationTreePanel().getSwingComponent(), getLeftSplitPane());
    }

    @Override
    public void showNavigationTable(boolean show) {
        applyVisibility(show, PLAYLIST_SPLIT_PANE, getNavigationTablePanel().getSwingComponent(), playListSplitPane);
    }
    
    @Override
    protected JComponent getComponentA() {
    	return getNavigationTreePanel().getSwingComponent();
    }
    
    @Override
    protected JComponent getComponentB() {
    	playListSplitPane = new SplitPaneFactory().getSplitPane(this, 
    																    PLAYLIST_SPLIT_PANE, 
    																    JSplitPane.VERTICAL_SPLIT, 
    																    getNavigationTablePanel().getSwingComponent(),
    																    getPlayListPanel().getSwingComponent());
    	return playListSplitPane;
    }
    
    @Override
    protected JComponent getComponentC() {
    	return getContextPanel().getSwingComponent();
    }
    
	@Override
	protected Dimension getNavigationTablePanelMinimumSize() {
		return getContext().getBean("navigationTableMinimumSize", Dimension.class);
	}
	
	@Override
	protected Dimension getNavigationTablePanelPreferredSize() {
		return getContext().getBean("navigationTablePreferredSize", Dimension.class);
	}

	@Override
	protected Dimension getNavigationTablePanelMaximumSize() {
		return getContext().getBean("navigationTableMaximumSize", Dimension.class);
	}
	
	@Override
	protected Dimension getNavigationTreePanelMinimumSize() {
		return getContext().getBean("navigationMinimumSize", Dimension.class);
	}
	
	@Override
	protected Dimension getNavigationTreePanelPreferredSize() {
		return getContext().getBean("navigationPreferredSize", Dimension.class);
	}
	
	@Override
	protected Dimension getNavigationTreePanelMaximumSize() {
		return getContext().getBean("navigationMaximumSize", Dimension.class);
	}
	
	@Override
	public Map<String, Double> getDefaultSplitPaneRelativePositions() {
		Map<String, Double> values = new HashMap<String, Double>();
		values.put(LEFT_SPLIT_PANE, 0.3);
		values.put(RIGHT_SPLIT_PANE, 0.6);
		values.put(PLAYLIST_SPLIT_PANE, 0.4);
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
