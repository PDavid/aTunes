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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.TransferHandler.TransferSupport;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Perform drop with data dragged from another component
 * 
 * @author alex
 * 
 */
public class InternalImportProcessor {

	private IPlayListTable playListTable;

	private IPlayListHandler playListHandler;

	private IDialogFactory dialogFactory;

	private IRepositoryHandler repositoryHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
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
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Perform drop with data dragged from another component
	 * 
	 * @param support
	 * @param frame
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean processInternalImport(final TransferSupport support) {
		List<?> listOfObjectsDragged = getObjectsDragged(support);
		if (!CollectionUtils.isEmpty(listOfObjectsDragged)) {
			Object object = listOfObjectsDragged.get(0);
			// DRAG AND DROP FROM PLAY LIST -> MOVE
			if (object instanceof PlayListDragableRow) {
				return moveRowsInPlayList(
						(List<PlayListDragableRow>) listOfObjectsDragged,
						((JTable.DropLocation) support.getDropLocation())
								.getRow());
			}

			// DRAG AND DROP OF AN ARTIST -> add songs from this artist
			else if (object instanceof DragableArtist) {
				return getArtistSongs((List<DragableArtist>) listOfObjectsDragged);
			}

			// DRAG AND DROP FROM NAVIGATOR TREE
			else if (object instanceof ITreeNode) {
				return this.beanFactory.getBean(
						NavigatorToPlayListDragAndDropProcessor.class)
						.dragFromNavigatorTree(
								playListTable.rowAtPoint(support
										.getDropLocation().getDropPoint()),
								(List<ITreeNode>) listOfObjectsDragged);

			} else if (object instanceof Integer) {
				return this.beanFactory.getBean(
						NavigatorToPlayListDragAndDropProcessor.class)
						.dragFromNavigatorTable(
								playListTable.rowAtPoint(support
										.getDropLocation().getDropPoint()),
								(List<Integer>) listOfObjectsDragged);

			}
		}
		return false;
	}

	/**
	 * @param support
	 * @return
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private List<?> getObjectsDragged(final TransferSupport support) {
		try {
			return (List<?>) support.getTransferable().getTransferData(
					DragAndDropHelper.getInternalDataFlavor());
		} catch (UnsupportedFlavorException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	private boolean getArtistSongs(
			final List<DragableArtist> listOfObjectsDragged) {
		DragableArtist dragabreArtist = listOfObjectsDragged.get(0);
		IArtist currentArtist = repositoryHandler.getArtist(dragabreArtist
				.getArtistInfo().getName());
		showAddArtistDragDialog(currentArtist);
		return true;
	}

	private void showAddArtistDragDialog(final IArtist currentArtist) {
		IArtistAlbumSelectorDialog dialog = dialogFactory
				.newDialog(IArtistAlbumSelectorDialog.class);
		dialog.setArtist(currentArtist);
		dialog.showDialog();
		IAlbum album = dialog.getAlbum();
		if (album != null) {
			playListHandler.addToVisiblePlayList(album.getAudioObjects());
		}
	}

	/**
	 * Move rows in play list
	 * 
	 * @param rowsDragged
	 * @param targetRow
	 * @return
	 */
	private boolean moveRowsInPlayList(
			final List<PlayListDragableRow> rowsDragged, final int targetRow) {
		if (rowsDragged == null || rowsDragged.isEmpty()) {
			return true;
		}

		// sort rows in reverse order if necessary: if target row index is
		// greater than original row position we need to reverse rows to move
		// them without change the order
		final boolean needReverseRows = rowsDragged.get(0).getRowPosition() < targetRow;
		Collections.sort(rowsDragged, new PlayListDragableRowComparator(
				needReverseRows));
		// get first row index
		int baseRow = (needReverseRows ? rowsDragged
				.get(rowsDragged.size() - 1) : rowsDragged.get(0))
				.getRowPosition();

		List<Integer> rowsToKeepSelected = new ArrayList<Integer>();

		boolean dropAtEnd = targetRow == playListHandler.getVisiblePlayList()
				.size() - 1;

		int rowMovedCounter = 0;
		// Move every row
		for (PlayListDragableRow rowDragged : rowsDragged) {
			// Calculate drop row, since targetRow is the row where to drop the
			// first row (baseRow)
			int dropRow = targetRow + (rowDragged.getRowPosition() - baseRow);
			if (dropAtEnd) {
				dropRow = playListHandler.getVisiblePlayList().size()
						- (rowMovedCounter + 1);
			}
			rowsToKeepSelected.add(dropRow);
			playListHandler.moveRowTo(rowDragged.getRowPosition(), dropRow);
			rowMovedCounter++;
		}

		// Refresh play list after moving
		playListHandler.refreshPlayList();

		// Set dragged rows as selected
		for (Integer rowToKeepSelected : rowsToKeepSelected) {
			playListTable.getSelectionModel().addSelectionInterval(
					rowToKeepSelected, rowToKeepSelected);
		}

		return true;
	}
}
