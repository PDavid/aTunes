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

import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.utils.Logger;

final class DragAndDropHelper {
	
    /**
     * Data flavor of a list of objects dragged from inside application
     */
    private static DataFlavor internalDataFlavor;
    
    private DragAndDropHelper() {}
    
    /**
     * @return Data flavor of a list of objects dragged from inside application
     */
    static DataFlavor getInternalDataFlavor() {
    	if (internalDataFlavor == null) {
    		try {
				internalDataFlavor = new DataFlavor(TransferableList.MIMETYPE);
			} catch (ClassNotFoundException e) {
				Logger.error(e);
			}
    	}
    	return internalDataFlavor;
    }    

    /**
     * Check if an array of flavors contains one file flavor
     * @param flavors
     * @return
     */
    static boolean hasFileFlavor(DataFlavor[] flavors) {
        for (DataFlavor df : flavors) {
            if (DataFlavor.javaFileListFlavor.equals(df)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if an array of flavors contains one string flavor
     * @param flavors
     * @return
     */
    static boolean hasStringFlavor(DataFlavor[] flavors) {
        for (DataFlavor df : flavors) {
            if (DataFlavor.stringFlavor.equals(df)) {
                return true;
            }
        }
        return false;
    }
}
