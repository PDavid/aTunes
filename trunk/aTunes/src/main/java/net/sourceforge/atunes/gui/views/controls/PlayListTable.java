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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListDragableRow;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * The play list table.
 */
public final class PlayListTable extends JTable implements IPlayListTable {

	private static final long serialVersionUID = 9209069236823917569L;

	private JPopupMenu playListPopupMenu;

	/**
	 * Drag source for this play list to drag songs to device
	 */
	private DragSource dragSource;

	private IPlayListHandler playListHandler;

	private ILookAndFeelManager lookAndFeelManager;

	private AbstractColumnSetTableModel playListTableModel;

	private ListSelectionListener playListListener;

	private KeyAdapter playListKeyListener;

	/**
	 * @param playListKeyListener
	 */
	public void setPlayListKeyListener(final KeyAdapter playListKeyListener) {
		this.playListKeyListener = playListKeyListener;
	}

	/**
	 * @param playListListener
	 */
	public void setPlayListListener(final ListSelectionListener playListListener) {
		this.playListListener = playListListener;
	}

	/**
	 * @param playListTableModel
	 */
	public void setPlayListTableModel(
			final AbstractColumnSetTableModel playListTableModel) {
		this.playListTableModel = playListTableModel;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playListPopupMenu
	 */
	public void setPlayListPopupMenu(final JPopupMenu playListPopupMenu) {
		this.playListPopupMenu = playListPopupMenu;
	}

	/**
	 * Initializes play list
	 */
	public void initialize() {
		this.lookAndFeelManager.getCurrentLookAndFeel().decorateTable(this);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setDropMode(DropMode.ON);

		// Set table model
		setModel(this.playListTableModel);

		// Disable autoresize, as we will control it
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Remove enter key event, which moves selection down
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

		// Remove F2 key event
		InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
		im.put(f2, "none");

		// Create drag source and set listener
		this.dragSource = new DragSource();
		this.dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY, this);

		// Force minimum row height to 16 pixels to avoid icons height greater
		// than row height
		if (getRowHeight() < 16) {
			setRowHeight(16);
		}

		setOpaque(false);

		addMouseListener((MouseListener) this.playListListener);
		addKeyListener(this.playListKeyListener);
		getSelectionModel().addListSelectionListener(this.playListListener);
	}

	@Override
	public JPopupMenu getMenu() {
		return this.playListPopupMenu;
	}

	@Override
	public void playbackStateChanged(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		revalidate();
		repaint();
	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {
	}

	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dragExit(final DragSourceEvent dse) {
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {
		// Only allow drag events initiated with left mouse button
		InputEvent event = dge.getTriggerEvent();
		if (!(event instanceof MouseEvent)
				|| !GuiUtils.isPrimaryMouseButton((MouseEvent) event)) {
			return;
		}

		// Get selected rows, add PlayListDragableRow objects to a list and
		// start a drag event
		List<Object> itemsToDrag = new ArrayList<Object>();
		int[] selectedRows = getSelectedRows();
		List<IAudioObject> selectedAudioObjects = this.playListHandler
				.getSelectedAudioObjects();
		for (int i = 0; i < selectedAudioObjects.size(); i++) {
			itemsToDrag.add(new PlayListDragableRow(
					selectedAudioObjects.get(i), selectedRows[i]));
		}
		TransferableList<Object> items = new TransferableList<Object>(
				itemsToDrag);
		this.dragSource.startDrag(dge, DragSource.DefaultCopyDrop, items, this);
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
	}

	@Override
	public List<IAudioObject> getSelectedAudioObjects() {
		return this.playListHandler.getSelectedAudioObjects();
	}

	@Override
	public JTable getSwingComponent() {
		return this;
	}
}