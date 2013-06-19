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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.TransferHandler.TransferSupport;

import net.sourceforge.atunes.utils.Logger;

/**
 * Extracts data from a drap and drop operation
 * 
 * @author alex
 * 
 */
public class ExternalDragAndDropDataExtractor {

	/**
	 * Mime type of a list or URIs
	 */
	private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";

	/**
	 * Data flavor of a list of URIs dragged from outside application
	 */
	private DataFlavor uriListFlavor;

	/**
	 * @param support
	 * @return
	 */
	@SuppressWarnings("unchecked")
	List<File> getFilesDragged(final TransferSupport support) {
		List<File> files = null;
		try {
			// External drag and drop for Windows
			if (DragAndDropHelper.hasFileFlavor(support.getDataFlavors())) {
				files = (List<File>) support.getTransferable().getTransferData(
						DataFlavor.javaFileListFlavor);
				// External drag and drop for Linux
			} else if (hasURIListFlavor(support.getDataFlavors())) {
				files = textURIListToFileList((String) support
						.getTransferable().getTransferData(getUriListFlavor()));
			} else if (DragAndDropHelper.hasStringFlavor(support
					.getDataFlavors())) {
				String str = ((String) support.getTransferable()
						.getTransferData(DataFlavor.stringFlavor));
				Logger.info(str);
			}
		} catch (UnsupportedFlavorException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
		return files;
	}

	private List<File> textURIListToFileList(final String data) {
		List<File> list = new ArrayList<File>(1);
		for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st
				.hasMoreTokens();) {
			String s = st.nextToken();
			if (s.startsWith("#")) {
				// the line is a comment (as per the RFC 2483)
				continue;
			}
			try {
				URI uri = new URI(s);
				File file = new File(uri);
				list.add(file);
			} catch (URISyntaxException e) {
				Logger.error(e);
			} catch (IllegalArgumentException e) {
				Logger.error(e);
			}
		}
		return list;
	}

	private boolean hasURIListFlavor(final DataFlavor[] flavors) {
		for (DataFlavor flavor : flavors) {
			if (getUriListFlavor().equals(flavor)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Data flavor of a list of URIs dragged from outside application
	 * @throws ClassNotFoundException
	 */
	private DataFlavor getUriListFlavor() {
		if (uriListFlavor == null) {
			try {
				uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
			} catch (ClassNotFoundException e) {
				Logger.error(e);
			}
		}
		return uriListFlavor;
	}
}
