/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IState;

final class PlayListController extends AbstractSimpleController<PlayListPanel> {

    /** The visible rect. */
    private Rectangle visibleRect;
    
    private IFrame frame;

    /**
     * Instantiates a new play list controller.
     * 
     * @param panel
     * @param state
     */
    PlayListController(PlayListPanel panel, IState state, IFrame frame) {
        super(panel, state);
        this.frame = frame;
        addBindings();
        addStateBindings();
    }

    @Override
	public void addBindings() {
        final PlayListTable table = getComponentControlled().getPlayListTable();

        // Set key listener for table
        table.addKeyListener(new PlayListKeyListener(this));

        PlayListListener listener = new PlayListListener(table, this);

        table.addMouseListener(listener);

        table.getSelectionModel().addListSelectionListener(listener);
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
        PlayListTable table = frame.getPlayListTable();
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
            javax.swing.JTable aTable = getComponentControlled().getPlayListTable();
            aTable.getSelectionModel().clearSelection();
            PlayListHandler.getInstance().removeAudioObjects(rows);
            
            int rowCount = aTable.getRowCount();
            if(rowCount > 0) {
                int newIndex = arrMin(rows);

                if(newIndex >= rowCount)
                    newIndex = rowCount - 1;

                aTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
            }
        }
    }

    /**
     * Gets the main play list scroll pane.
     * 
     * @return the main play list scroll pane
     */
    JScrollPane getMainPlayListScrollPane() {
        return getComponentControlled().getPlayListTableScroll();
    }

    /**
     * Gets the main play list table.
     * 
     * @return the main play list table
     */
    PlayListTable getMainPlayListTable() {
        return getComponentControlled().getPlayListTable();
    }

    /**
     * Move down.
     */
    void moveDown() {
        int[] rows = getComponentControlled().getPlayListTable().getSelectedRows();
        if (rows.length > 0 && rows[rows.length - 1] < getComponentControlled().getPlayListTable().getRowCount() - 1) {
            PlayListHandler.getInstance().moveDown(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(rows[0] + 1, rows[rows.length - 1] + 1);
        }
    }

    /**
     * Move to bottom.
     */
    void moveToBottom() {
        int[] rows = getComponentControlled().getPlayListTable().getSelectedRows();
        if (rows.length > 0 && rows[rows.length - 1] < getComponentControlled().getPlayListTable().getRowCount() - 1) {
            PlayListHandler.getInstance().moveToBottom(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(getComponentControlled().getPlayListTable().getRowCount() - rows.length,
                    getComponentControlled().getPlayListTable().getRowCount() - 1);
        }
    }

    /**
     * Move to top.
     */
    void moveToTop() {
        int[] rows = getComponentControlled().getPlayListTable().getSelectedRows();
        if (rows.length > 0 && rows[0] > 0) {
            PlayListHandler.getInstance().moveToTop(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(0, rows.length - 1);
        }
    }

    /**
     * Move up.
     */
    void moveUp() {
        int[] rows = getComponentControlled().getPlayListTable().getSelectedRows();
        if (rows.length > 0 && rows[0] > 0) {
            PlayListHandler.getInstance().moveUp(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(rows[0] - 1, rows[rows.length - 1] - 1);
        }
    }

    /**
     * Play selected audio object.
     * 
     */
    void playSelectedAudioObject() {
        int audioObject = getComponentControlled().getPlayListTable().getSelectedRow();
        PlayListHandler.getInstance().setPositionToPlayInVisiblePlayList(audioObject);
        PlayerHandler.getInstance().playCurrentAudioObject(false);
    }

    /**
     * Scrolls to songs currently playing.
     */
    void scrollPlayList(boolean isUserAction) {
        // Sometimes scroll can be fired when application starts if window size is changed
        // In that cases call to scroll could thrown an exception is current play list is not yet initialized
        // So check first if the visible play list is initialized before scroll it
        if (PlayListHandler.getInstance().getCurrentPlayList(true) != null) {
            scrollPlayList(PlayListHandler.getInstance().getCurrentAudioObjectIndexInVisiblePlayList(), isUserAction);
        }
    }

    /**
     * Scrolls the playlist to the playing song.
     * 
     * @param audioObject
     *            Audio object which should have focus
     */
    private synchronized void scrollPlayList(int audioObject, boolean isUserAction) {
        if (!getState().isAutoScrollPlayListEnabled() && !isUserAction) {
            return;
        }

        // If visible playlist is not active then don't scroll
        if (!PlayListHandler.getInstance().isActivePlayListVisible()) {
            return;
        }

        Logger.debug("Scrolling PlayList");

        // Get visible rectangle
        visibleRect = (Rectangle) getComponentControlled().getPlayListTable().getVisibleRect().clone();

        // Get cell height
        int heightOfRow = getComponentControlled().getPlayListTable().getCellRect(audioObject, 0, true).height;

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

        // Apply scroll
        // We add a delay in order to reduce freezes
        Timer t = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getComponentControlled().getPlayListTable().scrollRectToVisible(visibleRect);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    void refreshPlayList() {
        int[] selectedRows = frame.getPlayListTable().getSelectedRows();
        ((PlayListTableModel) frame.getPlayListTable().getModel()).refresh(TableModelEvent.UPDATE);
        // Select previous selected rows
        for (int selectedRow : selectedRows) {
        	frame.getPlayListTable().getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
        }
    }

    void reapplyFilter() {
        if (PlayListHandler.getInstance().isFiltered()) {
            PlayListHandler.getInstance().setFilter(FilterHandler.getInstance().getFilter());
        }
    }

}
