/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.misc.log.Logger;

public final class TransferableList<T> implements Transferable {

    /** The logger. */
    private Logger logger;

    /** The Constant mimeType. */
    public static final String mimeType = "aTunes/objects; class=java.io.InputStream";

    /** The list. */
    private List<T> list;

    /**
     * Instantiates a new transferable list.
     * 
     * @param list
     *            the list
     */
    public TransferableList(List<T> list) {
        this.list = list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer
     * .DataFlavor)
     */
    @Override
    public List<T> getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavors = new DataFlavor[1];
        try {
            flavors[0] = new DataFlavor(mimeType);
        } catch (ClassNotFoundException e) {
            getLogger().internalError(e);
        }
        return flavors;
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.
     * datatransfer.DataFlavor)
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.getMimeType().equalsIgnoreCase(mimeType);
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
