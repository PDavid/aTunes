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

import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.gui.views.CopyTransferHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.draganddrop.DragableArtist;
import net.sourceforge.atunes.model.IArtistInfo;

/**
 * Drap and drop from similar artists context table
 * 
 * @author alex
 * 
 */
public class SimilarArtistsDragAndDrop implements DragSourceListener,
		DragGestureListener {

	private final DragSource dragSource;

	private final ContextTable table;

	/**
	 * @param table
	 */
	public SimilarArtistsDragAndDrop(final ContextTable table) {
		this.table = table;
		this.dragSource = new DragSource();
		this.dragSource.createDefaultDragGestureRecognizer(this.table,
				DnDConstants.ACTION_COPY, this);
		this.table.setDragEnabled(true);
		this.table.setTransferHandler(new CopyTransferHandler());
	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dragExit(final DragSourceEvent dse) {
		// Nothing to do
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {
		int row = this.table.getSelectedRow();
		if (row != -1) {

			IArtistInfo artistInfo = ((SimilarArtistsTableModel) this.table
					.getModel()).getArtist(row);
			if (artistInfo.isAvailable()) {
				List<Object> itemsToDrag = new ArrayList<Object>();
				itemsToDrag.add(new DragableArtist(artistInfo));
				TransferableList<Object> items = new TransferableList<Object>(
						itemsToDrag);

				this.dragSource.startDrag(dge, DragSource.DefaultCopyDrop,
						items, this);
			}
		}
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
		// Nothing to do
	}
}
