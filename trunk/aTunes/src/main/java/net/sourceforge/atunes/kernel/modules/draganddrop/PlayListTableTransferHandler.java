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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.util.List;

import javax.swing.JTable;
import javax.swing.TransferHandler;

import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectLocator;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.utils.Logger;

/**
 * Some methods of this class about how to drag and drop from Gnome/KDE file
 * managers taken from:
 * 
 * http://www.davidgrant.ca/drag_drop_from_linux_kde_gnome_file_managers_konqueror_nautilus_to_java_applications
 * 
 */
public class PlayListTableTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 4366983690375897364L;

    private IFrame frame;
    
    private IPlayListHandler playListHandler;

    private INavigationHandler navigationHandler;
    
    private IPlayListTable playListTable;
    
    private ILocalAudioObjectFactory localAudioObjectFactory;
    
    private ILocalAudioObjectValidator localAudioObjectValidator;
    
    private IAudioObjectComparator audioObjectComparator;
    
    private ILocalAudioObjectLocator localAudioObjectLocator;
    
    private IPlayListIOService playListIOService;
    
    /**
     * @param playListTable
     * @param frame
     * @param playListHandler
     * @param navigationHandler
     * @param deviceHandler
     * @param localAudioObjectFactory
     * @param localAudioObjectValidator
     * @param audioObjectComparator
     * @param localAudioObjectLocator
     * @param playListIOService
     */
    public PlayListTableTransferHandler(IPlayListTable playListTable, IFrame frame, IPlayListHandler playListHandler, INavigationHandler navigationHandler, IDeviceHandler deviceHandler, ILocalAudioObjectFactory localAudioObjectFactory, ILocalAudioObjectValidator localAudioObjectValidator, IAudioObjectComparator audioObjectComparator, ILocalAudioObjectLocator localAudioObjectLocator, IPlayListIOService playListIOService) {
    	this.playListTable = playListTable;
    	this.frame = frame;
    	this.playListHandler = playListHandler;
    	this.navigationHandler = navigationHandler;
    	this.localAudioObjectFactory = localAudioObjectFactory;
    	this.localAudioObjectValidator = localAudioObjectValidator;
    	this.audioObjectComparator = audioObjectComparator;
    	this.localAudioObjectLocator = localAudioObjectLocator;
    	this.playListIOService = playListIOService;
        new PlayListToDeviceDragAndDropListener(navigationHandler, deviceHandler);
	}
    
    @Override
    public boolean canImport(TransferSupport support) {
        // Check if internal data flavor is supported
        if (support.getTransferable().isDataFlavorSupported(DragAndDropHelper.getInternalDataFlavor())) {
            try {
                List<?> listOfObjectsDragged = (List<?>) support.getTransferable().getTransferData(DragAndDropHelper.getInternalDataFlavor());
                if (listOfObjectsDragged == null || listOfObjectsDragged.isEmpty()) {
                    return false;
                }

                // Drag is made from another component...
                if (listOfObjectsDragged.get(0) instanceof PlayListDragableRow) {
                    try {
                        ((JTable.DropLocation) support.getDropLocation()).getRow();
                    } catch (ClassCastException e) {
                        // Drop is made at the top or bottom of JTable -> This is only allowed when dragging from another component
                        return false;
                    }
                }

                return true;
            } catch (Exception e) {
                Logger.error(e);
            }

            support.setShowDropLocation(true);
            return true;
        }

        if (DragAndDropHelper.hasFileFlavor(support.getDataFlavors())) {
            return true;
        }
        if (DragAndDropHelper.hasStringFlavor(support.getDataFlavors())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        if (support.getTransferable().isDataFlavorSupported(DragAndDropHelper.getInternalDataFlavor())) {
            return new InternalImportProcessor(frame, navigationHandler, playListTable, playListHandler, audioObjectComparator).processInternalImport(support);
        }

        return new ExternalImportProcessor(localAudioObjectFactory, localAudioObjectValidator, localAudioObjectLocator, playListIOService, playListTable, playListHandler, audioObjectComparator).processExternalImport(support);
    }
}
