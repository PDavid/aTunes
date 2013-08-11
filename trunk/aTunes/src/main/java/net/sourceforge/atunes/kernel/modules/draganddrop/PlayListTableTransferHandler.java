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

import java.util.List;

import javax.swing.JTable;
import javax.swing.TransferHandler;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.Timer;

/**
 * Some methods of this class about how to drag and drop from Gnome/KDE file
 * managers taken from:
 * 
 * http://www.davidgrant.ca/
 * drag_drop_from_linux_kde_gnome_file_managers_konqueror_nautilus_to_java_applications
 * 
 */
public class PlayListTableTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 4366983690375897364L;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public boolean canImport(final TransferSupport support) {
		// Check if it's a drag and drop operation
		if (!support.isDrop()) {
			return false;
		}

		// Check playlist is dynamic
		if (this.beanFactory.getBean(IPlayListHandler.class)
				.getVisiblePlayList().isDynamic()) {
			Logger.debug("Can't drop in a dynamic playlist");
			return false;
		}

		// Check if internal data flavor is supported
		if (support.getTransferable().isDataFlavorSupported(
				DragAndDropHelper.getInternalDataFlavor())) {
			try {
				List<?> listOfObjectsDragged = (List<?>) support
						.getTransferable().getTransferData(
								DragAndDropHelper.getInternalDataFlavor());
				if (listOfObjectsDragged == null
						|| listOfObjectsDragged.isEmpty()) {
					return false;
				}

				// Drag is made from another component...
				if (listOfObjectsDragged.get(0) instanceof PlayListDragableRow) {
					try {
						((JTable.DropLocation) support.getDropLocation())
								.getRow();
					} catch (ClassCastException e) {
						// Drop is made at the top or bottom of JTable -> This
						// is only allowed when dragging from another component
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
	public boolean importData(final TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}

		if (support.getTransferable().isDataFlavorSupported(
				DragAndDropHelper.getInternalDataFlavor())) {
			Timer t = new Timer();
			t.start();
			boolean accepted = this.beanFactory.getBean(
					InternalImportProcessor.class).processInternalImport(
					support);
			Logger.debug("Internal drag and drop to table took ", t.stop(),
					" seconds");
			return accepted;
		} else {
			return this.beanFactory.getBean(ExternalImportProcessor.class)
					.processExternalImport(support);
		}
	}
}
