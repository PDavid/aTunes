/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 * Common code for single frames with two split panes, where main split pane (parent of the other one) is at right
 * 
 * Components are named from left to right:  A | B | C where "|" is a split pane divider
 */
public abstract class MainSplitPaneRightSingleFrame extends CommonSingleFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8996304310400258287L;


	@Override
	protected JComponent getLeftSplitPaneLeftComponent() {
		return getComponentA();
	}
	
	@Override
	protected JComponent getLeftSplitPaneRightComponent() {
		return getComponentB();
	}
	
	@Override
	protected JComponent getRightSplitPaneLeftComponent() {
		return getLeftSplitPane();
	}
	
	@Override
	protected JComponent getRightSplitPaneRightComponent() {
		return getComponentC();
	}
	
	@Override
	protected JSplitPane getMainSplitPane() {
		return getRightSplitPane();
	}
	
}
