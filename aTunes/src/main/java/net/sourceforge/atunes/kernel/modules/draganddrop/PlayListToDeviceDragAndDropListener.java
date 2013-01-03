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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.utils.Logger;

/**
 * The listener interface for receiving dragAndDrop events.
 */
public class PlayListToDeviceDragAndDropListener implements DropTargetListener {

    private IDeviceHandler deviceHandler;

    private INavigationView deviceNavigationView;

    /**
     * @param deviceHandler
     */
    public void setDeviceHandler(final IDeviceHandler deviceHandler) {
	this.deviceHandler = deviceHandler;
    }

    /**
     * @param deviceNavigationView
     */
    public void setDeviceNavigationView(
	    final INavigationView deviceNavigationView) {
	this.deviceNavigationView = deviceNavigationView;
    }

    /**
     * Initializes
     */
    public void initialize() {
	// Drop targets for drag and drops operations from playlist to device
	// tree
	new DropTarget(deviceNavigationView.getTreeScrollPane(), this);
	new DropTarget(deviceNavigationView.getTree().getSwingComponent(), this);
    }

    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
	// Nothing to do
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
	// Nothing to do
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
	// Nothing to do
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
	Transferable transferable = dtde.getTransferable();
	DataFlavor aTunesFlavorAccepted = new TransferableList<Object>(null)
		.getTransferDataFlavors()[0];
	if (transferable.isDataFlavorSupported(aTunesFlavorAccepted)) {
	    dtde.acceptDrop(DnDConstants.ACTION_COPY);
	    try {
		// Get all audiofiles dragged
		@SuppressWarnings("unchecked")
		List<PlayListDragableRow> listOfObjectsDragged = (List<PlayListDragableRow>) transferable
			.getTransferData(aTunesFlavorAccepted);

		// If device is connected, then copy files to device
		if (deviceHandler.isDeviceConnected()) {
		    // Don't copy files already in device
		    List<ILocalAudioObject> filesToCopy = new ArrayList<ILocalAudioObject>();
		    for (PlayListDragableRow f : listOfObjectsDragged) {
			// Only accept LocalAudioObject objects
			if (f.getRowContent() instanceof ILocalAudioObject
				&& !deviceHandler.isDevicePath(f
					.getRowContent().getUrl())) {
			    filesToCopy.add((ILocalAudioObject) f
				    .getRowContent());
			}
		    }
		    // Copy files
		    if (!filesToCopy.isEmpty()) {
			deviceHandler.copyFilesToDevice(filesToCopy);
		    }
		}
	    } catch (Exception e) {
		Logger.error(e);
	    }
	    dtde.getDropTargetContext().dropComplete(true);
	}
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
	// Nothing to do
    }
}
