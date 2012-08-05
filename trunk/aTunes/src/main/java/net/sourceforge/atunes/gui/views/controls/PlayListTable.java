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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.gui.ColumnRenderers;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.PlayListColumnModel;
import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListDragableRow;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.PlayState;

/**
 * The play list table.
 */
public final class PlayListTable extends JTable implements IPlayListTable {

    private static final long serialVersionUID = 9209069236823917569L;

    private PlayState playState = PlayState.STOPPED;
    private JPopupMenu playListPopupMenu;

    /**
     * Drag source for this play list to drag songs to device
     */
    private DragSource dragSource;
    
    private IPlayListHandler playListHandler;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private AbstractColumnSetTableModel playListTableModel;
    
    private ITaskService taskService;
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    /**
     * @param playListTableModel
     */
    public void setPlayListTableModel(AbstractColumnSetTableModel playListTableModel) {
		this.playListTableModel = playListTableModel;
	}

    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param playListPopupMenu
     */
    public void setPlayListPopupMenu(JPopupMenu playListPopupMenu) {
		this.playListPopupMenu = playListPopupMenu;
	}
    
    /**
     * Initializes play list
     */
    public void initialize() {
        lookAndFeelManager.getCurrentLookAndFeel().decorateTable(this);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setDropMode(DropMode.ON);

        // Set table model
        setModel(playListTableModel);

        // Set column model
        PlayListColumnModel columnModel = new PlayListColumnModel(this, playListHandler, lookAndFeelManager.getCurrentLookAndFeel(), taskService);
        setColumnModel(columnModel);

        // Set sorter
        new ColumnSetRowSorter(this, playListTableModel, columnModel);

        // Bind column set popup menu
        new ColumnSetPopupMenu(this, columnModel);

        // Disable autoresize, as we will control it
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set renderers
        ColumnRenderers.addRenderers(this, columnModel, lookAndFeelManager.getCurrentLookAndFeel());

        // Remove enter key event, which moves selection down
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

        // Remove F2 key event
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
        im.put(f2, "none");

        // Create drag source and set listener
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);

        // Force minimum row height to 16 pixels to avoid icons height greater than row height
        if (getRowHeight() < 16) {
            setRowHeight(16);
        }
        
        setOpaque(false);
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.controls.playList.IPlayListTable#getMenu()
	 */
	@Override
	public JPopupMenu getMenu() {
        return playListPopupMenu;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.controls.playList.IPlayListTable#getPlayState()
	 */
	@Override
	public PlayState getPlayState() {
        return playState;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.controls.playList.IPlayListTable#setPlayState(net.sourceforge.atunes.model.PlayState)
	 */
	@Override
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
        List<IAudioObject> selectedAudioObjects = playListHandler.getSelectedAudioObjects();
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
    public List<IAudioObject> getSelectedAudioObjects() {
        return playListHandler.getSelectedAudioObjects();
    }
    
    @Override
    public JTable getSwingComponent() {
    	return this;
    }
   
}