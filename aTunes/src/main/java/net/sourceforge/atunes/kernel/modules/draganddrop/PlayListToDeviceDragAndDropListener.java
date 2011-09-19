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

import net.sourceforge.atunes.gui.model.TransferableList;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * The listener interface for receiving dragAndDrop events.
 */
public class PlayListToDeviceDragAndDropListener implements DropTargetListener {

    /**
     * Instantiates a new drag and drop listener.
     */
    public PlayListToDeviceDragAndDropListener(INavigationHandler navigationHandler) {
        // Drop targets for drag and drops operations from playlist to device tree
        new DropTarget(navigationHandler.getView(DeviceNavigationView.class).getTreeScrollPane(), this);
        new DropTarget(navigationHandler.getView(DeviceNavigationView.class).getTree(), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent
     * )
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent
     * )
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        Transferable transferable = dtde.getTransferable();
        DataFlavor aTunesFlavorAccepted = new TransferableList<Object>(null).getTransferDataFlavors()[0];
        if (transferable.isDataFlavorSupported(aTunesFlavorAccepted)) {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            try {
                // Get all audiofiles dragged
                @SuppressWarnings("unchecked")
                List<PlayListDragableRow> listOfObjectsDragged = (List<PlayListDragableRow>) transferable.getTransferData(aTunesFlavorAccepted);

                // If device is connected, then copy files to device
                if (DeviceHandler.getInstance().isDeviceConnected()) {
                    // Don't copy files already in device
                    List<ILocalAudioObject> filesToCopy = new ArrayList<ILocalAudioObject>();
                    for (PlayListDragableRow f : listOfObjectsDragged) {
                        // Only accept LocalAudioObject objects
                        if (f.getRowContent() instanceof ILocalAudioObject && !DeviceHandler.getInstance().isDevicePath(f.getRowContent().getUrl())) {
                            filesToCopy.add((ILocalAudioObject) f.getRowContent());
                        }
                    }
                    // Copy files
                    if (!filesToCopy.isEmpty()) {
                        DeviceHandler.getInstance().copyFilesToDevice(filesToCopy);
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }
            dtde.getDropTargetContext().dropComplete(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.
     * DropTargetDragEvent)
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // Nothing to do
    }
}
