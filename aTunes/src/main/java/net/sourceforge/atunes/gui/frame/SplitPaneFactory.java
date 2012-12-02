/*
 * aTunes 3.0.0
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.model.IControlsBuilder;

/**
 * Creates split pane
 * 
 * @author alex
 * 
 */
class SplitPaneFactory {

	private static final class StoreFrameStateWhenDividerLocationChangeListener
			implements PropertyChangeListener {
		private final AbstractSingleFrame frame;
		private final String splitPaneId;

		private StoreFrameStateWhenDividerLocationChangeListener(
				final AbstractSingleFrame frame, final String splitPaneId) {
			this.frame = frame;
			this.splitPaneId = splitPaneId;
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			this.frame.getFrameState().putSplitPaneDividerPos(this.splitPaneId,
					(Integer) evt.getNewValue());
			this.frame.storeFrameState();
		}
	}

	/**
	 * Creates a new navigator split pane for a frame with a split pane id
	 * 
	 * @param controlsBuilder
	 * @param frame
	 * @param splitPaneId
	 * @param orientation
	 * @param left
	 * @param right
	 * @return
	 */
	public JSplitPane getSplitPane(final IControlsBuilder controlsBuilder,
			final AbstractSingleFrame frame, final String splitPaneId,
			final int orientation, final JComponent left, final JComponent right) {
		if (frame == null || splitPaneId == null) {
			throw new IllegalStateException("Not initialized");
		}
		if (orientation != JSplitPane.HORIZONTAL_SPLIT
				&& orientation != JSplitPane.VERTICAL_SPLIT) {
			throw new IllegalStateException("Wrong orientation");
		}

		JSplitPane navigatorSplitPane = controlsBuilder
				.createSplitPane(orientation);
		navigatorSplitPane.setLeftComponent(left);
		navigatorSplitPane.setRightComponent(right);
		navigatorSplitPane.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new StoreFrameStateWhenDividerLocationChangeListener(frame,
						splitPaneId));
		return navigatorSplitPane;
	}
}
