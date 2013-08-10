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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.gui.ColumnDecorator;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.PlayListColumnModel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.actions.SortPlayListColumnAscendingAction;
import net.sourceforge.atunes.kernel.actions.SortPlayListColumnDescendingAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumnSetPopupMenu;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListPanel;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.Logger;

final class PlayListController extends AbstractSimpleController<PlayListPanel>
		implements IPlayListController {

	private IPlayListHandler playListHandler;

	private IPlayerHandler playerHandler;

	private IPlayListTable playListTable;

	private IPlayListPanel playListPanel;

	private IStatePlaylist statePlaylist;

	private IBeanFactory beanFactory;

	private IControlsBuilder controlsBuilder;

	private AbstractColumnSetTableModel playListTableModel;

	/**
	 * @param playListTableModel
	 */
	public void setPlayListTableModel(
			final AbstractColumnSetTableModel playListTableModel) {
		this.playListTableModel = playListTableModel;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param playListPanel
	 */
	public void setPlayListPanel(final IPlayListPanel playListPanel) {
		this.playListPanel = playListPanel;
	}

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(final IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		setComponentControlled((PlayListPanel) this.playListPanel
				.getSwingComponent());

		this.beanFactory.getBean("playlistTableColumnDecorator",
				ColumnDecorator.class).decorate(false);

		// Set column model
		PlayListColumnModel columnModel = this.beanFactory
				.getBean(PlayListColumnModel.class);
		columnModel.setModel(this.playListTableModel);
		this.playListTable.setColumnModel(columnModel);

		// Bind column set popup menu
		IColumnSetPopupMenu popup = this.controlsBuilder
				.createColumnSetPopupMenu(
						this.playListTable.getSwingComponent(), columnModel,
						this.playListTableModel);
		popup.addAction(this.beanFactory
				.getBean(SortPlayListColumnAscendingAction.class));
		popup.addAction(this.beanFactory
				.getBean(SortPlayListColumnDescendingAction.class));
	}

	private static int arrMin(final int[] array) {
		int rs = Integer.MAX_VALUE;
		for (int i : array) {
			if (i < rs) {
				rs = i;
			}
		}
		return rs;
	}

	/**
	 * Delete selection.
	 */
	void deleteSelection() {
		int[] rows = this.playListTable.getSelectedRows();
		if (rows.length > 0) {
			this.playListTable.getSelectionModel().clearSelection();
			this.playListHandler.removeAudioObjects(rows);

			int rowCount = this.playListTable.getRowCount();
			if (rowCount > 0) {
				int newIndex = arrMin(rows);

				if (newIndex >= rowCount) {
					newIndex = rowCount - 1;
				}

				this.playListTable.getSelectionModel().setSelectionInterval(
						newIndex, newIndex);
			}
		}
	}

	/**
	 * Move down.
	 */
	void moveDown(final IPlayList playList) {
		int[] rows = this.playListTable.getSelectedRows();
		if (rows.length > 0
				&& rows[rows.length - 1] < this.playListTable.getRowCount() - 1) {
			moveDown(playList, rows);
			refreshPlayList();
			this.playListTable.getSelectionModel().setSelectionInterval(
					rows[0] + 1, rows[rows.length - 1] + 1);
		}
	}

	/**
	 * Move to bottom.
	 * 
	 * @param playList
	 */
	void moveToBottom(final IPlayList playList) {
		int[] rows = this.playListTable.getSelectedRows();
		if (rows.length > 0
				&& rows[rows.length - 1] < this.playListTable.getRowCount() - 1) {
			moveToBottom(playList, rows);
			refreshPlayList();
			this.playListTable.getSelectionModel().setSelectionInterval(
					this.playListTable.getRowCount() - rows.length,
					this.playListTable.getRowCount() - 1);
		}
	}

	/**
	 * Move to top.
	 * 
	 * @param playList
	 */
	void moveToTop(final IPlayList playList) {
		int[] rows = this.playListTable.getSelectedRows();
		if (rows.length > 0 && rows[0] > 0) {
			moveToTop(playList, rows);
			refreshPlayList();
			this.playListTable.getSelectionModel().setSelectionInterval(0,
					rows.length - 1);
		}
	}

	/**
	 * Move up.
	 */
	void moveUp(final IPlayList playList) {
		int[] rows = this.playListTable.getSelectedRows();
		if (rows.length > 0 && rows[0] > 0) {
			moveUp(playList, rows);
			refreshPlayList();
			this.playListTable.getSelectionModel().setSelectionInterval(
					rows[0] - 1, rows[rows.length - 1] - 1);
		}
	}

	private void moveRows(final IPlayList playList, final int[] rows,
			final boolean up) {
		if (rows == null || rows.length == 0) {
			return;
		}
		List<Integer> rowList = new ArrayList<Integer>();
		for (int row : rows) {
			rowList.add(row);
		}
		Collections.sort(rowList, new RowListComparator(up));
		for (Integer row : rowList) {
			playList.moveRowTo(row, row + (up ? -1 : 1));
		}
	}

	private void moveUp(final IPlayList playList, final int[] rows) {
		moveRows(playList, rows, true);
	}

	private void moveDown(final IPlayList playList, final int[] rows) {
		moveRows(playList, rows, false);
	}

	private void moveToBottom(final IPlayList playList, final int[] rows) {
		int j = 0;
		for (int i = rows.length - 1; i >= 0; i--) {
			IAudioObject aux = playList.get(rows[i]);
			playList.remove(rows[i]);
			playList.add(playList.size() - j++, aux);
		}
		if (rows[rows.length - 1] < playList.getCurrentAudioObjectIndex()) {
			playList.setCurrentAudioObjectIndex(playList
					.getCurrentAudioObjectIndex() - rows.length);
		} else if (rows[0] <= playList.getCurrentAudioObjectIndex()
				&& playList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
			playList.setCurrentAudioObjectIndex(playList
					.getCurrentAudioObjectIndex()
					+ playList.size()
					- rows[rows.length - 1] - 1);
		}
	}

	private void moveToTop(final IPlayList playList, final int[] rows) {
		for (int i = 0; i < rows.length; i++) {
			IAudioObject aux = playList.get(rows[i]);
			playList.remove(rows[i]);
			playList.add(i, aux);
		}
		if (rows[0] > playList.getCurrentAudioObjectIndex()) {
			playList.setCurrentAudioObjectIndex(playList
					.getCurrentAudioObjectIndex() + rows.length);
		} else if (rows[0] <= playList.getCurrentAudioObjectIndex()
				&& playList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
			playList.setCurrentAudioObjectIndex(playList
					.getCurrentAudioObjectIndex() - rows[0]);
		}
	}

	/**
	 * Play selected audio object.
	 * 
	 */
	void playSelectedAudioObject() {
		this.playerHandler
				.playAudioObjectInPlayListPositionOfVisiblePlayList(this.playListTable
						.getSelectedRow());
	}

	/**
	 * Scrolls to songs currently playing.
	 */
	@Override
	public void scrollPlayList(final boolean isUserAction) {
		// Sometimes scroll can be fired when application starts if window size
		// is changed
		// In that cases call to scroll could thrown an exception is current
		// play list is not yet initialized
		// So check first if the visible play list is initialized before scroll
		// it
		if (this.playListHandler.getVisiblePlayList() != null) {
			scrollPlayList(
					this.playListHandler
							.getCurrentAudioObjectIndexInVisiblePlayList(),
					isUserAction);
		}
	}

	/**
	 * Scrolls the playlist to the playing song.
	 * 
	 * @param audioObject
	 *            Audio object which should have focus
	 */
	private void scrollPlayList(final int audioObject,
			final boolean isUserAction) {
		if (!this.statePlaylist.isAutoScrollPlayListEnabled() && !isUserAction) {
			return;
		}

		// If visible playlist is not active then don't scroll
		if (!this.playListHandler.isActivePlayListVisible()) {
			return;
		}

		Logger.debug("Scrolling PlayList");

		// Get visible rectangle
		final Rectangle visibleRect = getVisibleRectangle(audioObject);

		// Apply scroll
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PlayListController.this.playListTable
						.scrollRectToVisible(visibleRect);
			}
		});
	}

	/**
	 * @param audioObject
	 * @return
	 */
	private Rectangle getVisibleRectangle(final int audioObject) {
		Rectangle visibleRect = (Rectangle) this.playListTable.getVisibleRect()
				.clone();

		// Get cell height
		int heightOfRow = this.playListTable.getCellRect(audioObject, 0, true).height;

		// Do calculation
		if (visibleRect.height == 0) {
			// At the first scroll of play list visibleRect.height is 0
			// So selected audio object is not visible
			visibleRect.y = (audioObject + 1) * heightOfRow;
		} else {
			int newYPosition = (audioObject + 1) * heightOfRow
					- visibleRect.height / 2;
			visibleRect.y = newYPosition;
		}

		// Correct negative value of y
		visibleRect.y = visibleRect.y >= 0 ? visibleRect.y : 0;
		return visibleRect;
	}

	/**
	 * Forces refresh of play list
	 */
	@Override
	public void refreshPlayList() {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				int[] selectedRows = PlayListController.this.playListTable
						.getSelectedRows();
				((PlayListTableModel) PlayListController.this.playListTable
						.getModel()).refresh(TableModelEvent.UPDATE);
				// Select previous selected rows
				for (int selectedRow : selectedRows) {
					PlayListController.this.playListTable.getSelectionModel()
							.addSelectionInterval(selectedRow, selectedRow);
				}
			}
		});
	}

	/**
	 * Soft refresh of play list, only repaints
	 */
	void updatePlayList() {
		this.playListTable.repaint();
	}

	/**
	 * Clear play list selection
	 */
	@Override
	public void clearSelection() {
		this.playListTable.getSelectionModel().clearSelection();
	}

	/**
	 * Sets given playlist as visible
	 * 
	 * @param playList
	 */
	@Override
	public void setVisiblePlayList(final IPlayList playList) {
		((PlayListTableModel) this.playListTable.getModel())
				.setVisiblePlayList(playList);
	}

	/**
	 * Sets selected elements in table
	 * 
	 * @param from
	 * @param to
	 */
	void setSelectionInterval(final int from, final int to) {
		this.playListTable.getSelectionModel().setSelectionInterval(from, to);
	}

	/**
	 * Return selected rows
	 * 
	 * @return
	 */
	int[] getSelectedRows() {
		return this.playListTable.getSelectedRows();
	}

	/**
	 * Enable drag and drop in play list
	 */
	void enableDragAndDrop() {
		this.playListPanel.enableDragAndDrop();
	}

	/**
	 * Repaint play list
	 */
	@Override
	public void repaint() {
		this.playListPanel.getSwingComponent().repaint();
	}
}
