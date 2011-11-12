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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IProgressSlider;

/**
 * Controls size of player controls panel to move slider on top of controls or not
 * @author alex
 *
 */
final class PlayerControlsPanelBottomProgressSliderPanelComponentAdapter extends ComponentAdapter {
	
	/**
	 * Minimum width of progress bar to be shown at bottom
	 */
	private static final int PROGRESS_BAR_BOTTOM_MINIMUM_SIZE = 300;

	private final IProgressSlider progressSlider;
	private final JPanel bottomProgressSliderPanel;
	private final JPanel topProgressSliderPanel;
	private Boolean showProgressOnTop = null;

	PlayerControlsPanelBottomProgressSliderPanelComponentAdapter(IProgressSlider progressSlider, JPanel bottomProgressSliderPanel, JPanel topProgressSliderPanel) {
		this.progressSlider = progressSlider;
		this.bottomProgressSliderPanel = bottomProgressSliderPanel;
		this.topProgressSliderPanel = topProgressSliderPanel;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		boolean showOnTop = bottomProgressSliderPanel.getWidth() < PROGRESS_BAR_BOTTOM_MINIMUM_SIZE;

		if (showProgressOnTop == null || showProgressOnTop != showOnTop) {
			if (showOnTop) {
				bottomProgressSliderPanel.remove(progressSlider.getSwingComponent());
				progressSlider.setLayout();
				topProgressSliderPanel.add(progressSlider.getSwingComponent(), BorderLayout.CENTER);
			} else {
				topProgressSliderPanel.remove(progressSlider.getSwingComponent());
				progressSlider.setLayout();
				bottomProgressSliderPanel.add(progressSlider.getSwingComponent(), BorderLayout.CENTER);
			}
			showProgressOnTop = showOnTop;
		}
	}
}