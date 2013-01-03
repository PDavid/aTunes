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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.sourceforge.atunes.model.IFrameState;

/**
 * Common code for single frames. Right now all single frames have two split
 * panes, a tool bar and a status bar
 * 
 * Components are named from left to right: A | B | C where "|" is a split pane
 * divider
 */
public abstract class CommonSingleFrame extends AbstractSingleFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6728998633745513923L;

	protected static final String LEFT_SPLIT_PANE = "1";
	protected static final String RIGHT_SPLIT_PANE = "2";

	private JSplitPane leftSplitPane;
	private JSplitPane rightSplitPane;

	/**
	 * @return
	 */
	protected JSplitPane getLeftSplitPane() {
		return this.leftSplitPane;
	}

	/**
	 * @return
	 */
	protected JSplitPane getRightSplitPane() {
		return this.rightSplitPane;
	}

	@Override
	protected void setContent() {
		setLayout(new GridBagLayout());

		// Main Split Pane
		this.leftSplitPane = getControlsBuilder().createSplitPane(
				getLeftSplitType());

		GridBagConstraints c = new GridBagConstraints();

		JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
		this.rightSplitPane = getControlsBuilder().createSplitPane(
				getRightSplitType());
		this.rightSplitPane.setBorder(BorderFactory.createEmptyBorder());
		this.rightSplitPane.setResizeWeight(0.5);
		this.rightSplitPane.setLeftComponent(getRightSplitPaneLeftComponent());
		this.rightSplitPane
				.setRightComponent(getRightSplitPaneRightComponent());

		nonNavigatorPanel.add(this.rightSplitPane, BorderLayout.CENTER);

		this.leftSplitPane.setLeftComponent(getLeftSplitPaneLeftComponent());
		this.leftSplitPane.setRightComponent(getLeftSplitPaneRightComponent());
		this.leftSplitPane.setResizeWeight(0.5);

		this.leftSplitPane.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						getFrameState().putSplitPaneDividerPos(LEFT_SPLIT_PANE,
								(Integer) evt.getNewValue());
						storeFrameState();
					}
				});

		this.rightSplitPane.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(final PropertyChangeEvent evt) {
						getFrameState()
								.putSplitPaneDividerPos(RIGHT_SPLIT_PANE,
										((Integer) evt.getNewValue()));
						storeFrameState();
					}
				});

		c.gridx = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		c.gridy = getStateUI().isShowPlayerControlsOnTop() ? 1 : 0;
		c.weighty = 1;
		add(getMainSplitPane(), c);

		c.gridy = getStateUI().isShowPlayerControlsOnTop() ? 0 : 1;
		c.weighty = 0;
		add(getPlayerControls().getSwingComponent(), c);

		c.gridy = 2;
		c.weighty = 0;
		add(getStatusBar(), c);
	}

	protected abstract JSplitPane getMainSplitPane();

	protected abstract JComponent getLeftSplitPaneLeftComponent();

	protected abstract JComponent getLeftSplitPaneRightComponent();

	protected abstract JComponent getRightSplitPaneLeftComponent();

	protected abstract JComponent getRightSplitPaneRightComponent();

	@Override
	protected void setupSplitPaneDividerPosition(final IFrameState frameState) {
		applySplitPaneDividerPosition(this.leftSplitPane,
				frameState.getSplitPaneDividerPos(LEFT_SPLIT_PANE), 0.5);
		applySplitPaneDividerPosition(this.rightSplitPane,
				frameState.getSplitPaneDividerPos(RIGHT_SPLIT_PANE), 0.5);
	}

	@Override
	protected void applySplitPaneDividerPosition(final JSplitPane splitPane,
			final int location, final double relPos) {
		if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			applyHorizontalSplitPaneDividerPosition(splitPane, location, relPos);
		} else {
			applyVerticalSplitPaneDividerPosition(splitPane, location, relPos);
		}
	}

	/**
	 * Places split pane divider position in an horizontal split pane
	 * 
	 * @param splitPane
	 * @param location
	 * @param relPos
	 */
	private void applyHorizontalSplitPaneDividerPosition(
			final JSplitPane splitPane, final int location, final double relPos) {
		// Avoid right component to have a width less than its minimum size
		int l = location;
		if (splitPane.getWidth() > 0) {
			int rightWidth = splitPane.getWidth() - l;
			int rightMinWidth = (int) splitPane.getRightComponent()
					.getMinimumSize().getWidth();
			if (rightMinWidth > rightWidth) {
				l = l
						- (rightMinWidth - rightWidth + getControlsBuilder()
								.getSplitPaneDividerSize());
			}
		}
		super.applySplitPaneDividerPosition(splitPane, l, relPos);
	}

	/**
	 * Places split pane divider position in a vertical split pane
	 * 
	 * @param splitPane
	 * @param location
	 * @param relPos
	 */
	private void applyVerticalSplitPaneDividerPosition(
			final JSplitPane splitPane, final int location, final double relPos) {
		// Avoid bottom component to have a height less than its minimum size
		int l = location;
		if (splitPane.getHeight() > 0) {
			int rightHeight = splitPane.getHeight() - l;
			int rightMinHeight = (int) splitPane.getRightComponent()
					.getMinimumSize().getHeight();
			if (rightMinHeight > rightHeight) {
				l = l
						- (rightMinHeight - rightHeight + getControlsBuilder()
								.getSplitPaneDividerSize());
			}
		}
		super.applySplitPaneDividerPosition(splitPane, l, relPos);
	}

	@Override
	protected Dimension getContextPanelMinimumSize() {
		return getContext().getBean("contextMinimumSize", Dimension.class);
	}

	@Override
	protected Dimension getContextPanelPreferredSize() {
		return getContext().getBean("contextPreferredSize", Dimension.class);
	}

	@Override
	protected Dimension getContextPanelMaximumSize() {
		return getContext().getBean("contextMaximumSize", Dimension.class);
	}

	@Override
	protected Dimension getPlayListPanelMinimumSize() {
		return getContext().getBean("playListMinimumSize", Dimension.class);
	}

	@Override
	protected Dimension getPlayListPanelPreferredSize() {
		return getContext().getBean("playListPreferredSize", Dimension.class);
	}

	@Override
	protected Dimension getPlayListPanelMaximumSize() {
		return getContext().getBean("playListMaximumSize", Dimension.class);
	}

	@Override
	protected Dimension getWindowMinimumSize() {
		return getContext().getBean("windowMinimumSize", Dimension.class);
	}

	protected abstract JComponent getComponentA();

	protected abstract JComponent getComponentB();

	protected abstract JComponent getComponentC();

	protected abstract int getLeftSplitType();

	protected abstract int getRightSplitType();
}
