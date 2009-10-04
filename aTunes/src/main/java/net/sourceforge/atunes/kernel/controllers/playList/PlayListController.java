/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.controllers.playList;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.model.PlayListColumnModel;
import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumnClickedListener;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumnSelector;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.panels.PlayListControlsPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.kernel.controllers.model.Controller;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumns;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListTableModel;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;

public class PlayListController extends Controller<PlayListPanel> implements PlayListColumnClickedListener {

    /** The visible rect. */
    Rectangle visibleRect;

    /**
     * Instantiates a new play list controller.
     * 
     * @param panel
     *            the panel
     */
    public PlayListController(PlayListPanel panel) {
        super(panel);
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
        final PlayListTable table = getComponentControlled().getPlayListTable();
        table.addPlayListColumnClickedListener(this);

        // Set key listener for table
        table.addKeyListener(new PlayListKeyListener(this));

        PlayListListener listener = new PlayListListener(table, this);

        table.getArrangeColumns().addActionListener(listener);
        table.addMouseListener(listener);

        table.getSelectionModel().addListSelectionListener(listener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Arrange columns.
     */
    public void arrangeColumns() {
        // Show column selector
        PlayListColumnSelector selector = VisualHandler.getInstance().getPlayListColumnSelector();
        selector.setColumns(PlayListColumns.getColumnsForSelection());
        selector.setVisible(true);

        // Apply changes
        ((PlayListColumnModel) getComponentControlled().getPlayListTable().getColumnModel()).arrangeColumns(true);

    }

    /**
     * Notifies when user clicks on a column header.
     * 
     * @param columnClicked
     *            the column clicked
     */
    public void columnClicked(Column columnClicked) {
        getLogger().debug(LogCategories.CONTROLLER, new String[] { columnClicked.getColumnName() });

        // Sort play list
        PlayListHandler.getInstance().sortPlayList(columnClicked.getComparator());
    }

    /**
     * Delete selection.
     */
    public void deleteSelection() {
        getLogger().debug(LogCategories.CONTROLLER);

        PlayListTable table = VisualHandler.getInstance().getPlayListTable();
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
            getComponentControlled().getPlayListTable().getSelectionModel().clearSelection();
            PlayListHandler.getInstance().removeAudioObjects(rows);
        }
    }

    /**
     * Gets the main play list scroll pane.
     * 
     * @return the main play list scroll pane
     */
    public JScrollPane getMainPlayListScrollPane() {
        return getComponentControlled().getPlayListTableScroll();
    }

    /**
     * Gets the main play list table.
     * 
     * @return the main play list table
     */
    public PlayListTable getMainPlayListTable() {
        return getComponentControlled().getPlayListTable();
    }

    /**
     * Move down.
     */
    public void moveDown() {
        getLogger().debug(LogCategories.CONTROLLER);

        PlayListTable table = VisualHandler.getInstance().getPlayListTable();
        int[] rows = table.getSelectedRows();
        if (rows.length > 0 && rows[rows.length - 1] < table.getRowCount() - 1) {
            PlayListHandler.getInstance().moveDown(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(rows[0] + 1, rows[rows.length - 1] + 1);
        }
    }

    /**
     * Move to bottom.
     */
    public void moveToBottom() {
        getLogger().debug(LogCategories.CONTROLLER);

        PlayListTable table = VisualHandler.getInstance().getPlayListTable();
        int[] rows = table.getSelectedRows();
        if (rows.length > 0 && rows[rows.length - 1] < table.getRowCount() - 1) {
            PlayListHandler.getInstance().moveToBottom(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(getComponentControlled().getPlayListTable().getRowCount() - rows.length,
                    getComponentControlled().getPlayListTable().getRowCount() - 1);
        }
    }

    /**
     * Move to top.
     */
    public void moveToTop() {
        getLogger().debug(LogCategories.CONTROLLER);

        PlayListTable table = VisualHandler.getInstance().getPlayListTable();
        int[] rows = table.getSelectedRows();
        if (rows.length > 0 && rows[0] > 0) {
            PlayListHandler.getInstance().moveToTop(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(0, rows.length - 1);
        }
    }

    /**
     * Move up.
     */
    public void moveUp() {
        getLogger().debug(LogCategories.CONTROLLER);

        PlayListTable table = VisualHandler.getInstance().getPlayListTable();
        int[] rows = table.getSelectedRows();
        if (rows.length > 0 && rows[0] > 0) {
            PlayListHandler.getInstance().moveUp(rows);
            refreshPlayList();
            getComponentControlled().getPlayListTable().getSelectionModel().setSelectionInterval(rows[0] - 1, rows[rows.length - 1] - 1);
        }
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Play selected audio object.
     * 
     */
    public void playSelectedAudioObject() {
        int audioObject = getComponentControlled().getPlayListTable().getSelectedRow();
        PlayListHandler.getInstance().setPositionToPlayInVisiblePlayList(audioObject);
        PlayerHandler.getInstance().playCurrentAudioObject(false);
    }

    /**
     * Scrolls to songs currently playing.
     */
    public void scrollPlayList(boolean isUserAction) {
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
        if (!ApplicationState.getInstance().isAutoScrollPlayListEnabled() && !isUserAction) {
            return;
        }

        // If visible playlist is not active then don't scroll
        if (!PlayListHandler.getInstance().isActivePlayListVisible()) {
            return;
        }

        getLogger().debug(LogCategories.CONTROLLER, "Scrolling PlayList");

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

    public PlayListControlsPanel getPlayListControlsPanel() {
        return getComponentControlled().getPlayListControls();
    }

    public void refreshPlayList() {
        int[] selectedRows = VisualHandler.getInstance().getPlayListTable().getSelectedRows();
        ((PlayListTableModel) VisualHandler.getInstance().getPlayListTable().getModel()).refresh();
        // Select previous selected rows
        for (int i = 0; i < selectedRows.length; i++) {
            VisualHandler.getInstance().getPlayListTable().getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
        }
    }
}
