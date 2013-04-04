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

import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.gui.views.CopyTransferHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ITable;

/**
 * Shows items of current selected elements in navigator tree
 * 
 * @author alex
 * 
 */
public final class NavigationTable extends JTable implements
		DragSourceListener, DragGestureListener, ITable {

	private static final long serialVersionUID = -607346309523708685L;
	private DragSource dragSource;

	/**
	 * Instantiates a new drag source table.
	 * 
	 * @param lookAndFeelManager
	 */
	public NavigationTable(final ILookAndFeelManager lookAndFeelManager) {
		super();
		lookAndFeelManager.getCurrentLookAndFeel().decorateTable(this);
		setDragSource();
		setTransferHandler(new CopyTransferHandler());
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
		int[] rows = getSelectedRows();
		List<Integer> rowsToDrag = new ArrayList<Integer>();
		for (int element : rows) {
			rowsToDrag.add(element);
		}
		TransferableList<Integer> items = new TransferableList<Integer>(
				rowsToDrag);
		this.dragSource.startDrag(dge, DragSource.DefaultCopyDrop, items, this);
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	/**
	 * Sets the drag source.
	 */
	private void setDragSource() {
		this.dragSource = new DragSource();
		this.dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY, this);
	}

	@Override
	public JTable getSwingComponent() {
		return this;
	}
}
