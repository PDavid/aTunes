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

package net.sourceforge.atunes.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * The Class ClipboardFacade.
 */
public final class ClipboardFacade implements ClipboardOwner {

    /**
     * Puts text on clipboard.
     * 
     * @param sText
     *            the s text
     */
    public void copyToClipboard(String sText) {
        // Get System Clipboard
        Clipboard objClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // Wrap text into a transferable object
        StringSelection objStringSelection = new StringSelection(sText);
        // Put text on clipboard
        objClipboard.setContents(objStringSelection, this);
    }

    /**
     * Gets text from clipboard
     * 
     * @return Text from clipboard
     */
    public String getClipboardContent() {
        // Get System Clipboard
        Clipboard objClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String content = "";
        try {
            Transferable transferable = objClipboard.getContents(null);
            boolean hasTransferableText = transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                content = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (IOException e) {
        	Logger.error(e);
            // Thrown if clipboard is not available: Will return empty string
        } catch (UnsupportedFlavorException e) {
        	Logger.error(e);
            // Thrown if clipboard is not available: Will return empty string
		}
        return content;
    }

    /**
     * Gets if clipboard contains text
     * 
     * @return <code>true</code> if clipboard contains text
     */
    public boolean clipboardContainsText() {
        return !getClipboardContent().equals("");
    }

    /**
     * Lost ownership listener.
     * 
     * @param arg0
     *            the arg0
     * @param arg1
     *            the arg1
     */
    @Override
    public void lostOwnership(Clipboard arg0, Transferable arg1) {
        // Nothing to do
    }

}
