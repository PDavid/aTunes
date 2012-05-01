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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListPanel;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.Logger;

final class PlayListController extends AbstractSimpleController<PlayListPanel> implements IPlayListController {

    private IPlayListHandler playListHandler;
    
    private IPlayerHandler playerHandler;
    
    private IPlayListTable playListTable;
    
    private IPlayListPanel playListPanel;
    
    private IStatePlaylist statePlaylist;
    
    /**
     * @param statePlaylist
     */
    public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}
    
    /**
     * @param playListPanel
     */
    public void setPlayListPanel(IPlayListPanel playListPanel) {
		this.playListPanel = playListPanel;
	}
    
    /**
     * @param playListTable
     */
    public void setPlayListTable(IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

    /**
     * Initializes controller
     */
    public void initialize() {
    	setComponentControlled((PlayListPanel)playListPanel.getSwingComponent());
        addBindings();
        addStateBindings();
    }
    
    @Override
	public void addBindings() {
        // Set key listener for table
    	playListTable.addKeyListener(new PlayListKeyListener(playerHandler));
        PlayListListener listener = new PlayListListener(playListTable, this);
        playListTable.addMouseListener(listener);
        playListTable.getSelectionModel().addListSelectionListener(listener);
    }

    private static int arrMin(int[] array) {
        int rs = Integer.MAX_VALUE;
        for(int i : array) {
            if(i < rs) {
                rs = i;
            }
        }
        return rs;
    }
    /**
     * Delete selection.
     */
    void deleteSelection() {
        int[] rows = playListTable.getSelectedRows();
        if (rows.length > 0) {
        	playListTable.getSelectionModel().clearSelection();
            playListHandler.removeAudioObjects(rows);
            
            int rowCount = playListTable.getRowCount();
            if(rowCount > 0) {
                int newIndex = arrMin(rows);

                if(newIndex >= rowCount) {
                    newIndex = rowCount - 1;
                }

                playListTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
            }
        }
    }

    /**
     * Move down.
     */
    void moveDown(IPlayList playList) {
        int[] rows = playListTable.getSelectedRows();
        if (rows.length > 0 && rows[rows.length - 1] < playListTable.getRowCount() - 1) {
            moveDown(playList, rows);
            refreshPlayList();
            playListTable.getSelectionModel().setSelectionInterval(rows[0] + 1, rows[rows.length - 1] + 1);
        }
    }

    /**
     * Move to bottom.
     * @param playList
     */
    void moveToBottom(IPlayList playList) {
        int[] rows = playListTable.getSelectedRows();
        if (rows.length > 0 && rows[rows.length - 1] < playListTable.getRowCount() - 1) {
            moveToBottom(playList, rows);
            refreshPlayList();
            playListTable.getSelectionModel().setSelectionInterval(playListTable.getRowCount() - rows.length,
                    playListTable.getRowCount() - 1);
        }
    }

    /**
     * Move to top.
     * @param playList
     */
    void moveToTop(IPlayList playList) {
        int[] rows = playListTable.getSelectedRows();
        if (rows.length > 0 && rows[0] > 0) {
            moveToTop(playList, rows);
            refreshPlayList();
            playListTable.getSelectionModel().setSelectionInterval(0, rows.length - 1);
        }
    }

    /**
     * Move up.
     */
    void moveUp(IPlayList playList) {
        int[] rows = playListTable.getSelectedRows();
        if (rows.length > 0 && rows[0] > 0) {
            moveUp(playList, rows);
            refreshPlayList();
            playListTable.getSelectionModel().setSelectionInterval(rows[0] - 1, rows[rows.length - 1] - 1);
        }
    }
    
    private void moveRows(IPlayList playList, int[] rows, final boolean up) {
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

	private void moveUp(IPlayList playList, int[] rows) {
        moveRows(playList, rows, true);
    }

	private void moveDown(IPlayList playList, int[] rows) {
        moveRows(playList, rows, false);
    }

	private void moveToBottom(IPlayList playList, int[] rows) {
        int j = 0;
        for (int i = rows.length - 1; i >= 0; i--) {
            IAudioObject aux = playList.get(rows[i]);
            playList.remove(rows[i]);
            playList.add(playList.size() - j++, aux);
        }
        if (rows[rows.length - 1] < playList.getCurrentAudioObjectIndex()) {
        	playList.setCurrentAudioObjectIndex(playList.getCurrentAudioObjectIndex() - rows.length);
        } else if (rows[0] <= playList.getCurrentAudioObjectIndex() && playList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
        	playList.setCurrentAudioObjectIndex(playList.getCurrentAudioObjectIndex() + playList.size() - rows[rows.length - 1] - 1);
        }
    }

	private void moveToTop(IPlayList playList, int[] rows) {
        for (int i = 0; i < rows.length; i++) {
            IAudioObject aux = playList.get(rows[i]);
            playList.remove(rows[i]);
            playList.add(i, aux);
        }
        if (rows[0] > playList.getCurrentAudioObjectIndex()) {
            playList.setCurrentAudioObjectIndex(playList.getCurrentAudioObjectIndex() + rows.length);
        } else if (rows[0] <= playList.getCurrentAudioObjectIndex() && playList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
            playList.setCurrentAudioObjectIndex(playList.getCurrentAudioObjectIndex() - rows[0]);
        }
    }


    /**
     * Play selected audio object.
     * 
     */
    void playSelectedAudioObject() {
        int audioObject = playListTable.getSelectedRow();
        playListHandler.setPositionToPlayInVisiblePlayList(audioObject);
        playerHandler.playCurrentAudioObject(false);
    }

    /**
     * Scrolls to songs currently playing.
     */
    void scrollPlayList(boolean isUserAction) {
        // Sometimes scroll can be fired when application starts if window size is changed
        // In that cases call to scroll could thrown an exception is current play list is not yet initialized
        // So check first if the visible play list is initialized before scroll it
        if (playListHandler.getCurrentPlayList(true) != null) {
            scrollPlayList(playListHandler.getCurrentAudioObjectIndexInVisiblePlayList(), isUserAction);
        }
    }

    /**
     * Scrolls the playlist to the playing song.
     * 
     * @param audioObject
     *            Audio object which should have focus
     */
    private void scrollPlayList(int audioObject, boolean isUserAction) {
        if (!statePlaylist.isAutoScrollPlayListEnabled() && !isUserAction) {
            return;
        }

        // If visible playlist is not active then don't scroll
        if (!playListHandler.isActivePlayListVisible()) {
            return;
        }

        Logger.debug("Scrolling PlayList");

        // Get visible rectangle
        final Rectangle visibleRect = getVisibleRectangle(audioObject);

        // Apply scroll
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
                playListTable.scrollRectToVisible(visibleRect);
        	}
        });
    }

	/**
	 * @param audioObject
	 * @return
	 */
	private Rectangle getVisibleRectangle(int audioObject) {
		Rectangle visibleRect = (Rectangle) playListTable.getVisibleRect().clone();

        // Get cell height
        int heightOfRow = playListTable.getCellRect(audioObject, 0, true).height;

        // Do calculation
        if (visibleRect.height == 0) {
            // At the first scroll of play list visibleRect.height is 0
            // So selected audio object is not visible
            visibleRect.y = (audioObject + 1) * heightOfRow;
        } else {
            int newYPosition = (audioObject + 1) * heightOfRow - visibleRect.height / 2;
            visibleRect.y = newYPosition;
        }

        // Correct negative value of y
        visibleRect.y = visibleRect.y >= 0 ? visibleRect.y : 0;
		return visibleRect;
	}

    /**
     * Forces refresh of play list
     */
    public void refreshPlayList() {
    	SwingUtilities.invokeLater(new Runnable() {
    		@Override
    		public void run() {
    	        int[] selectedRows = playListTable.getSelectedRows();
    	        ((PlayListTableModel) playListTable.getModel()).refresh(TableModelEvent.UPDATE);
    	        // Select previous selected rows
    	        for (int selectedRow : selectedRows) {
    	        	playListTable.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
    	        }
    		}
    	});
    }
    
    /**
     * Soft refresh of play list, only repaints
     */
    void updatePlayList() {
    	playListTable.repaint();
    }
    
    /**
     * Clear play list selection
     */
    void clearSelection() {
    	playListTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Sets given playlist as visible
     * @param playList
     */
    void setVisiblePlayList(IPlayList playList) {
    	((PlayListTableModel) playListTable.getModel()).setVisiblePlayList(playList);
    }
    
    /**
     * Sets selected elements in table
     * @param from
     * @param to
     */
    void setSelectionInterval(int from, int to) {
    	playListTable.getSelectionModel().setSelectionInterval(from, to);
    }
    
    /**
     * Changes selection to given index
     * @param index
     */
    void changeSelectedAudioObjectToIndex(int index) {
    	playListTable.changeSelection(index, 0, false, false);
    }
    
    /**
     * Return selected rows
     * @return
     */
    int[] getSelectedRows() {
    	return playListTable.getSelectedRows();
    }

	/**
	 * Enable drag and drop in play list
	 */
	void enableDragAndDrop() {
    	playListPanel.enableDragAndDrop();
	}
	
	/**
	 * Repaint play list
	 */
	void repaint() {
		playListPanel.getSwingComponent().repaint();
	}
}
