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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListTableTransferHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListToDeviceDragAndDropListener;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IPlayListPanel;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;
import net.sourceforge.atunes.model.IPlayListTable;

/**
 * The playlist panel.
 * 
 * @author fleax
 */
public final class PlayListPanel extends JPanel implements IPlayListPanel {

	private static final long serialVersionUID = -1886323054505118303L;

	/** The play list tab panel. */
	private IPlayListSelectorPanel playListSelectorPanel;

	/** The play list table. */
	private IPlayListTable playListTable;

	/** The play list table scroll. */
	private JScrollPane playListTableScroll;

	private PlayListTableTransferHandler playListTableTransferHandler;

	private PlayListToDeviceDragAndDropListener playListToDeviceDragAndDropListener;

	private IControlsBuilder controlsBuilder;

	/**
	 * Instantiates a new play list panel.
	 */
	public PlayListPanel() {
		super(new BorderLayout(0, 1));
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param playListToDeviceDragAndDropListener
	 */
	public void setPlayListToDeviceDragAndDropListener(
			final PlayListToDeviceDragAndDropListener playListToDeviceDragAndDropListener) {
		this.playListToDeviceDragAndDropListener = playListToDeviceDragAndDropListener;
	}

	/**
	 * @param playListTableTransferHandler
	 */
	public void setPlayListTableTransferHandler(
			final PlayListTableTransferHandler playListTableTransferHandler) {
		this.playListTableTransferHandler = playListTableTransferHandler;
	}

	/**
	 * Adds the content.
	 * 
	 * @param lookAndFeelManager
	 */
	public void initialize() {
		this.playListTableScroll = this.controlsBuilder
				.createScrollPane(this.playListTable.getSwingComponent());

		add(this.playListSelectorPanel.getSwingComponent(), BorderLayout.NORTH);
		add(this.playListTableScroll, BorderLayout.CENTER);
	}

	/**
	 * @param playListSelectorPanel
	 */
	public void setPlayListSelectorPanel(
			final IPlayListSelectorPanel playListSelectorPanel) {
		this.playListSelectorPanel = playListSelectorPanel;
	}

	@Override
	public void enableDragAndDrop() {
		this.playListToDeviceDragAndDropListener.initialize();

		this.playListTable
				.setTransferHandler(this.playListTableTransferHandler);
		this.playListTableScroll
				.setTransferHandler(this.playListTableTransferHandler);
	}

	@Override
	public JComponent getSwingComponent() {
		return this;
	}

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(final IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}
}
