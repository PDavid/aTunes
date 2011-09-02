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
package net.sourceforge.atunes.kernel.modules.context.similar;

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

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.model.TransferableList;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.draganddrop.DragableArtist;

/**
 * 
 * Table to display similar artist with drag and drop handler
 * @author encestre
 *
 */
public final class SimilarArtistsContextTable extends ContextTable implements DragSourceListener, DragGestureListener {

	private static final long serialVersionUID = 1978112401090380585L;
	private DragSource dragSource;

    /**
     * Instantiates a new drag source table.
     */
    public SimilarArtistsContextTable() {
        super();
        setShowGrid(false);
        setDragSource();
        setDragEnabled(true);

        setTransferHandler(new TransferHandler() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -3748127907759271591L;

			@Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY;
    }
        });
    }

    /**
     * Instantiates a new drag source table.
     * 
     * @param model
     *            the model
     */
    public SimilarArtistsContextTable(TableModel model) {
        this();
        setModel(model);
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        // Nothing to do
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        // Nothing to do
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
        // Nothing to do
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        int row = getSelectedRow();
        
        ArtistInfo artistInfo = ((SimilarArtistsTableModel) getModel()).getArtist(row);
        if (artistInfo.isAvailable()){
	        List<Object> itemsToDrag = new ArrayList<Object>();
	        itemsToDrag.add(new DragableArtist(artistInfo));
	        TransferableList<Object> items = new TransferableList<Object>(itemsToDrag);
	        
	        dragSource.startDrag(dge, DragSource.DefaultCopyDrop, items, this);
        }
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        // Nothing to do
    }

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
