/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.gui.views.controls.playList;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.AudioObjectsSource;
import net.sourceforge.atunes.gui.model.PlayListColumnModel;
import net.sourceforge.atunes.gui.model.TransferableList;
import net.sourceforge.atunes.gui.views.menus.PlayListMenu;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListDragableRow;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListTableModel;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The play list table.
 */
public class PlayListTable extends JTable implements DragSourceListener, DragGestureListener, AudioObjectsSource {

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

        public static ImageIcon getPlayStateIcon(PlayState state) {
            switch (state) {
            case PLAYING:
                return ImageLoader.getImage(ImageLoader.PLAY_TINY);
            case STOPPED:
                return ImageLoader.getImage(ImageLoader.STOP_TINY);
            case PAUSED:
                return ImageLoader.getImage(ImageLoader.PAUSE_TINY);
            case NONE:
                return ImageLoader.getImage(ImageLoader.EMPTY);
            default:
                return ImageLoader.getImage(ImageLoader.EMPTY);
            }
        }
    }

    private static final long serialVersionUID = 9209069236823917569L;

    private PlayState playState = PlayState.STOPPED;
    private JPopupMenu menu;
    JPopupMenu rightMenu;
    private JMenuItem arrangeColumns;
    List<PlayListColumnClickedListener> listeners = new ArrayList<PlayListColumnClickedListener>();

    /**
     * Drag source for this play list to drag songs to device
     */
    private DragSource dragSource;

    /**
     * Instantiates a new play list table.
     */
    public PlayListTable() {
        super();
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setShowGrid(false);
        setDropMode(DropMode.ON);

        // Add mouse click listener on table header
        getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Use left button to sort
                if (e.getButton() == MouseEvent.BUTTON1) {
                    PlayListColumnModel colModel = (PlayListColumnModel) getColumnModel();
                    int column = colModel.getColumnIndexAtPosition(e.getX());
                    if (column >= 0) {
                        Column columnClicked = ((PlayListColumnModel) getColumnModel()).getColumnObject(column);
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).columnClicked(columnClicked);
                        }
                    }
                }
                // Use right button to arrange columns
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    rightMenu.show(PlayListTable.this.getTableHeader(), e.getX(), e.getY());
                }
            }
        });

        // Set table model
        setModel(new PlayListTableModel());

        // Set column model
        setColumnModel(new PlayListColumnModel(this));

        // Disable autoresize, as we will control it
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set renderers
        PlayListRenderers.addRenderers(this);

        // Remove enter key event, which moves selection down
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

        // Remove F2 key event
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
        im.put(f2, "none");

        rightMenu = new JPopupMenu();
        arrangeColumns = new JMenuItem(I18nUtils.getString("ARRANGE_COLUMNS"));
        rightMenu.add(arrangeColumns);

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
    }

    /**
     * Adds the play list column clicked listener.
     * 
     * @param l
     *            the l
     */
    public void addPlayListColumnClickedListener(PlayListColumnClickedListener l) {
        listeners.add(l);
    }

    /**
     * Gets the arrange columns.
     * 
     * @return the arrangeColumns
     */
    public JMenuItem getArrangeColumns() {
        return arrangeColumns;
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
        if (!(event instanceof MouseEvent) || ((MouseEvent) event).getButton() != MouseEvent.BUTTON1) {
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
