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

package net.sourceforge.atunes.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.utils.Logger;

/**
 * List of objects to be transferred in a drag and drop operation
 * 
 * @author alex
 * 
 * @param <T>
 */
public final class TransferableList<T> implements Transferable {

    /** The Constant mimeType. */
    public static final String MIMETYPE = "aTunes/objects; class=java.io.InputStream";

    /** The list. */
    private final List<T> list;

    /**
     * Instantiates a new transferable list.
     * 
     * @param list
     *            the list
     */
    public TransferableList(final List<T> list) {
	this.list = list;
    }

    @Override
    public List<T> getTransferData(final DataFlavor flavor)
	    throws UnsupportedFlavorException, IOException {
	return list;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
	DataFlavor[] flavors = new DataFlavor[1];
	try {
	    flavors[0] = new DataFlavor(MIMETYPE);
	} catch (ClassNotFoundException e) {
	    Logger.error(e);
	}
	return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
	return flavor.getMimeType().equalsIgnoreCase(MIMETYPE);
    }
}
