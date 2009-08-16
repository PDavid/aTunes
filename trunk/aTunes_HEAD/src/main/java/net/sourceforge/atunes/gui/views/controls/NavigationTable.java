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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.model.TransferableList;

/**
 * The Class DragSourceTable.
 */
public class NavigationTable extends JTable implements DragSourceListener, DragGestureListener {

    private static final long serialVersionUID = -607346309523708685L;

    /** The drag source. */
    private DragSource dragSource;

    /**
     * Instantiates a new drag source table.
     */
    public NavigationTable() {
        super();
        setShowGrid(false);
        setDragSource();
    }

    /**
     * Instantiates a new drag source table.
     * 
     * @param model
     *            the model
     */
    public NavigationTable(TableModel model) {
        this();
        setModel(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent
     * )
     */
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent
     * )
     */
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
     */
    @Override
    public void dragExit(DragSourceEvent dse) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.
     * DragGestureEvent)
     */

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        int[] rows = getSelectedRows();
        List<Integer> rowsToDrag = new ArrayList<Integer>();
        for (int element : rows) {
            rowsToDrag.add(element);
        }
        TransferableList<Integer> items = new TransferableList<Integer>(rowsToDrag);
        dragSource.startDrag(dge, DragSource.DefaultCopyDrop, items, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent
     * )
     */
    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.
     * DragSourceDragEvent)
     */
    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
        // Nothing to do
    }

    /**
     * Sets the drag source.
     */
    private void setDragSource() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }

}
