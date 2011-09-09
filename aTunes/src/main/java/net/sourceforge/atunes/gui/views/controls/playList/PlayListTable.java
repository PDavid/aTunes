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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.awt.Paint;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.images.PlayListStateImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.AudioObjectsSource;
import net.sourceforge.atunes.gui.model.PlayListColumnModel;
import net.sourceforge.atunes.gui.model.TransferableList;
import net.sourceforge.atunes.gui.renderers.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.menus.PlayListMenu;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListDragableRow;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListTableModel;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The play list table.
 */
public final class PlayListTable extends JTable implements DragSourceListener, DragGestureListener, AudioObjectsSource {

    /**
     * The play state of the playlist.
     */
    public enum PlayState {

        STOPPED,

        PLAYING,

        PAUSED,

        /**
         * When it's not the active play list
         */
        NONE;

        public static ImageIcon getPlayStateIcon(Paint color, PlayState state) {
            switch (state) {
            case PLAYING:
                return PlayListStateImageIcon.getPlayIcon(color);
            case STOPPED:
                return PlayListStateImageIcon.getStopIcon(color);
            case PAUSED:
                return PlayListStateImageIcon.getPauseIcon(color);
            case NONE:
                return null;
            default:
                return null;
            }
        }
    }

    private static final long serialVersionUID = 9209069236823917569L;

    private PlayState playState = PlayState.STOPPED;
    private JPopupMenu menu;

    /**
     * Drag source for this play list to drag songs to device
     */
    private DragSource dragSource;

    /**
     * Instantiates a new play list table.
     */
    public PlayListTable(AbstractColumnSet columnSet) {
        super();
        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().decorateTable(this);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDropMode(DropMode.ON);

        // Set table model
        PlayListTableModel model = new PlayListTableModel(columnSet);
        setModel(model);

        // Set column model
        PlayListColumnModel columnModel = new PlayListColumnModel(this);
        setColumnModel(columnModel);

        // Set sorter
        new ColumnSetRowSorter(this, model, columnModel);

        // Bind column set popup menu
        new ColumnSetPopupMenu(this, columnModel);

        // Disable autoresize, as we will control it
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set renderers
        ColumnRenderers.addRenderers(this, columnModel);

        // Remove enter key event, which moves selection down
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

        // Remove F2 key event
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
        im.put(f2, "none");

        // Popup menu
        menu = new JPopupMenu();
        PlayListMenu.fillPopUpMenu(menu, this);
        GuiUtils.applyComponentOrientation(menu);

        // Create drag source and set listener
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);

        // Force minimum row height to 16 pixels to avoid icons height greater than row height
        if (getRowHeight() < 16) {
            setRowHeight(16);
        }
        
        setOpaque(false);
    }

    /**
     * Gets the menu.
     * 
     * @return the menu
     */
    public JPopupMenu getMenu() {
        return menu;
    }

    /**
     * Gets the play state.
     * 
     * @return the play state
     */
    public PlayState getPlayState() {
        return playState;
    }

    /**
     * Sets the play state.
     * 
     * @param playState
     *            the new play state
     */
    public void setPlayState(PlayState playState) {
        this.playState = playState;
        revalidate();
        repaint();
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        // Only allow drag events initiated with left mouse button
        InputEvent event = dge.getTriggerEvent();
        if (!(event instanceof MouseEvent) || !GuiUtils.isPrimaryMouseButton((MouseEvent)event)) {
            return;
        }

        // Get selected rows, add PlayListDragableRow objects to a list and start a drag event
        List<Object> itemsToDrag = new ArrayList<Object>();
        int[] selectedRows = getSelectedRows();
        List<AudioObject> selectedAudioObjects = PlayListHandler.getInstance().getSelectedAudioObjects();
        for (int i = 0; i < selectedAudioObjects.size(); i++) {
            itemsToDrag.add(new PlayListDragableRow(selectedAudioObjects.get(i), selectedRows[i]));
        }
        TransferableList<Object> items = new TransferableList<Object>(itemsToDrag);
        dragSource.startDrag(dge, DragSource.DefaultCopyDrop, items, this);
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public List<AudioObject> getSelectedAudioObjects() {
        return PlayListHandler.getInstance().getSelectedAudioObjects();
    }
}
