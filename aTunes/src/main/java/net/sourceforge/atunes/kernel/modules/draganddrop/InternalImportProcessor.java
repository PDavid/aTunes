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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.api.RepositoryApi;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Perform drop with data dragged from another component
 * @author alex
 *
 */
class InternalImportProcessor {

	private final INavigationHandler navigationHandler;
	
    private final IPlayListTable playListTable;
    
    private final IPlayListHandler playListHandler;
    
    private final IAudioObjectComparator audioObjectComparator;
	
	/**
	 * @param navigationHandler
	 * @param playListTable
	 * @param playListHandler
	 * @param audioObjectComparator
	 */
	public InternalImportProcessor(INavigationHandler navigationHandler, IPlayListTable playListTable, IPlayListHandler playListHandler, IAudioObjectComparator audioObjectComparator) {
		this.navigationHandler = navigationHandler;
		this.playListTable = playListTable;
		this.playListHandler = playListHandler;
		this.audioObjectComparator = audioObjectComparator;
	}
	
    /**
     * Perform drop with data dragged from another component
     * 
     * @param support
     * @param frame
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean processInternalImport(TransferSupport support) {
    	List<?> listOfObjectsDragged = getObjectsDragged(support);
    	if (!CollectionUtils.isEmpty(listOfObjectsDragged)) {
    		// DRAG AND DROP FROM PLAY LIST -> MOVE			
    		if (listOfObjectsDragged.get(0) instanceof PlayListDragableRow) {
    			return moveRowsInPlayList((List<PlayListDragableRow>) listOfObjectsDragged, ((JTable.DropLocation) support.getDropLocation()).getRow());
    		}

    		// DRAG AND DROP OF AN ARTIST -> add songs from this artist			
    		if (listOfObjectsDragged.get(0) instanceof DragableArtist) {
    			return getArtistSongs((List<DragableArtist>) listOfObjectsDragged);
    		}

    		dragFromNavigator(support, listOfObjectsDragged);
    	}

    	return false;
    }

	/**
	 * @param support
	 * @param listOfObjectsDragged
	 */
	private void dragFromNavigator(TransferSupport support, List<?> listOfObjectsDragged) {
		List<IAudioObject> audioObjectsToAdd = new ArrayList<IAudioObject>();
		for (int i = 0; i < listOfObjectsDragged.size(); i++) {
			Object objectDragged = listOfObjectsDragged.get(i);
			// DRAG AND DROP FROM TREE
			if (objectDragged instanceof DefaultMutableTreeNode) {
				List<? extends IAudioObject> objectsToImport = navigationHandler.getAudioObjectsForTreeNode(
						navigationHandler.getCurrentView().getClass(), (DefaultMutableTreeNode) objectDragged);
				if (objectsToImport != null) {
					audioObjectsToAdd.addAll(objectsToImport);
				}
			} else if (objectDragged instanceof Integer) {
				// DRAG AND DROP FROM TABLE
				Integer row = (Integer) objectDragged;
				audioObjectsToAdd.add(navigationHandler.getAudioObjectInNavigationTable(row));
			}
		}

		int dropRow = playListTable.rowAtPoint(support.getDropLocation().getDropPoint());

		if (!audioObjectsToAdd.isEmpty()) {
			audioObjectComparator.sort(audioObjectsToAdd);
			playListHandler.addToPlayList(dropRow, audioObjectsToAdd, true);
			// Keep selected rows: if drop row is the bottom of play list (-1) then select last row
			if (dropRow == -1) {
				dropRow = playListHandler.getCurrentPlayList(true).size() - audioObjectsToAdd.size();
			}
			playListTable.getSelectionModel().addSelectionInterval(dropRow, dropRow + audioObjectsToAdd.size() - 1);
		}
	}

	/**
	 * @param support
	 * @return
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private List<?> getObjectsDragged(TransferSupport support) {
		try {
			return (List<?>) support.getTransferable().getTransferData(DragAndDropHelper.getInternalDataFlavor());
		} catch (UnsupportedFlavorException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

    private boolean getArtistSongs(List<DragableArtist> listOfObjectsDragged) {
    	DragableArtist dragabreArtist = listOfObjectsDragged.get(0);
    	IArtist currentArtist = RepositoryApi.getArtist(dragabreArtist.getArtistInfo().getName());
    	showAddArtistDragDialog(currentArtist);
    	return true;
	}
    
	private void showAddArtistDragDialog(IArtist currentArtist) {
    	IArtistAlbumSelectorDialog dialog = Context.getBean(IDialogFactory.class).newDialog(IArtistAlbumSelectorDialog.class);
    	dialog.setArtist(currentArtist);
    	dialog.showDialog();
    	IAlbum album = dialog.getAlbum();
    	if (album != null) {
    		playListHandler.addToPlayList(album.getAudioObjects());
    	}
    }
	
	/**
     * Move rows in play list
     * 
     * @param rowsDragged
     * @param targetRow
     * @return
     */
    private boolean moveRowsInPlayList(List<PlayListDragableRow> rowsDragged, int targetRow) {
        if (rowsDragged == null || rowsDragged.isEmpty()) {
            return true;
        }

        // sort rows in reverse order if necessary: if target row index is greater than original row position we need to reverse rows to move them without change the order		
        final boolean needReverseRows = rowsDragged.get(0).getRowPosition() < targetRow;
        Collections.sort(rowsDragged, new PlayListDragableRowComparator(needReverseRows));
        // get first row index
        int baseRow = (needReverseRows ? rowsDragged.get(rowsDragged.size() - 1) : rowsDragged.get(0)).getRowPosition();

        List<Integer> rowsToKeepSelected = new ArrayList<Integer>();

        boolean dropAtEnd = targetRow == playListHandler.getCurrentPlayList(true).size() - 1;

        int rowMovedCounter = 0;
        // Move every row
        for (PlayListDragableRow rowDragged : rowsDragged) {
            // Calculate drop row, since targetRow is the row where to drop the first row (baseRow)
            int dropRow = targetRow + (rowDragged.getRowPosition() - baseRow);
            if (dropAtEnd) {
                dropRow = playListHandler.getCurrentPlayList(true).size() - (rowMovedCounter + 1);
            }
            rowsToKeepSelected.add(dropRow);
            playListHandler.moveRowTo(rowDragged.getRowPosition(), dropRow);
            rowMovedCounter++;
        }

        // Refresh play list after moving
        playListHandler.refreshPlayList();

        // Set dragged rows as selected
        for (Integer rowToKeepSelected : rowsToKeepSelected) {
            playListTable.getSelectionModel().addSelectionInterval(rowToKeepSelected, rowToKeepSelected);
        }

        return true;
    }

}
