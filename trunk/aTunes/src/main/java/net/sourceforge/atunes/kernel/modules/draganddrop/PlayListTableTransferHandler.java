/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.model.TransferableList;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryLoader;
import net.sourceforge.atunes.kernel.modules.repository.SortType;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Some methods of this class about how to drag and drop from Gnome/KDE file
 * managers taken from:
 * 
 * http://www.davidgrant.ca/drag_drop_from_linux_kde_gnome_file_managers_konqueror_nautilus_to_java_applications
 * 
 */
public class PlayListTableTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 4366983690375897364L;

    /**
     * Internal logger of this class
     */
    private static Logger logger = new Logger();

    /**
     * Data flavor of a list of objects dragged from inside application
     */
    private static DataFlavor internalDataFlavor;

    /**
     * Mime type of a list or URIs
     */
    private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";

    /**
     * Data flavor of a list of URIs dragged from outside application
     */
    private static DataFlavor uriListFlavor;

    static {
        try {
            internalDataFlavor = new DataFlavor(TransferableList.mimeType);
            uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
        } catch (ClassNotFoundException e) {
            logger.error(LogCategories.DESKTOP, e);
        }
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // Check if internal data flavor is supported
        if (support.getTransferable().isDataFlavorSupported(internalDataFlavor)) {
            try {
                List<?> listOfObjectsDragged = (List<?>) support.getTransferable().getTransferData(internalDataFlavor);
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
                logger.error(LogCategories.DESKTOP, e);
            }

            support.setShowDropLocation(true);
            return true;
        }

        if (hasFileFlavor(support.getDataFlavors())) {
            return true;
        }
        if (hasStringFlavor(support.getDataFlavors())) {
            return true;
        }
        return false;
    }

    private static boolean hasFileFlavor(DataFlavor[] flavors) {
        for (DataFlavor df : flavors) {
            if (DataFlavor.javaFileListFlavor.equals(df)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStringFlavor(DataFlavor[] flavors) {
        for (DataFlavor df : flavors) {
            if (DataFlavor.stringFlavor.equals(df)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasURIListFlavor(DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (uriListFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        if (support.getTransferable().isDataFlavorSupported(internalDataFlavor)) {
            return processInternalImport(support);
        }

        return processExternalImport(support);
    }

    /**
     * Perform drop with data dragged from another component
     * 
     * @param support
     * @return
     */
    private static boolean processInternalImport(TransferSupport support) {
        try {
            List<AudioObject> audioObjectsToAdd = new ArrayList<AudioObject>();
            List<?> listOfObjectsDragged = (List<?>) support.getTransferable().getTransferData(internalDataFlavor);
            if (listOfObjectsDragged == null || listOfObjectsDragged.isEmpty()) {
                return false;
            }

            // DRAG AND DROP FROM PLAY LIST -> MOVE			
            if (listOfObjectsDragged.get(0) instanceof PlayListDragableRow) {
                @SuppressWarnings("unchecked")
                boolean moveRowsInPlayList = moveRowsInPlayList((List<PlayListDragableRow>) listOfObjectsDragged, ((JTable.DropLocation) support.getDropLocation()).getRow());
                return moveRowsInPlayList;
            }

            for (int i = 0; i < listOfObjectsDragged.size(); i++) {
                Object objectDragged = listOfObjectsDragged.get(i);
                // DRAG AND DROP FROM TREE
                if (objectDragged instanceof DefaultMutableTreeNode) {
                    List<AudioObject> objectsToImport = ControllerProxy.getInstance().getNavigationController().getAudioObjectsForTreeNode(
                            NavigationHandler.getInstance().getCurrentView().getClass(), (DefaultMutableTreeNode) objectDragged);
                    if (objectsToImport != null) {
                        audioObjectsToAdd.addAll(objectsToImport);
                    }
                } else if (objectDragged instanceof Integer) {
                    // DRAG AND DROP FROM TABLE
                    Integer row = (Integer) objectDragged;
                    audioObjectsToAdd.add(ControllerProxy.getInstance().getNavigationController().getSongInNavigationTable(row));
                }
            }

            int dropRow = GuiHandler.getInstance().getPlayListTable().rowAtPoint(support.getDropLocation().getDropPoint());

            if (!audioObjectsToAdd.isEmpty()) {
                List<AudioObject> songsSorted = SortType.sort(audioObjectsToAdd);
                PlayListHandler.getInstance().addToPlayList(dropRow, songsSorted, true);
                // Keep selected rows: if drop row is the bottom of play list (-1) then select last row
                if (dropRow == -1) {
                    dropRow = PlayListHandler.getInstance().getCurrentPlayList(true).size() - audioObjectsToAdd.size();
                }
                GuiHandler.getInstance().getPlayListTable().getSelectionModel().addSelectionInterval(dropRow, dropRow + audioObjectsToAdd.size() - 1);
            }
        } catch (Exception e) {
            logger.internalError(e);
        }

        return false;
    }

    /**
     * Move rows in play list
     * 
     * @param rowsDragged
     * @param targetRow
     * @return
     */
    private static boolean moveRowsInPlayList(List<PlayListDragableRow> rowsDragged, int targetRow) {
        if (rowsDragged == null || rowsDragged.isEmpty()) {
            return true;
        }

        // sort rows in reverse order if necessary: if target row index is greater than original row position we need to reverse rows to move them without change the order		
        final boolean needReverseRows = rowsDragged.get(0).getRowPosition() < targetRow;
        Collections.sort(rowsDragged, new Comparator<PlayListDragableRow>() {
            @Override
            public int compare(PlayListDragableRow o1, PlayListDragableRow o2) {
                return (needReverseRows ? -1 : 1) * Integer.valueOf(o1.getRowPosition()).compareTo(Integer.valueOf(o2.getRowPosition()));
            }
        });
        // get first row index
        int baseRow = (needReverseRows ? rowsDragged.get(rowsDragged.size() - 1) : rowsDragged.get(0)).getRowPosition();

        List<Integer> rowsToKeepSelected = new ArrayList<Integer>();

        boolean dropAtEnd = targetRow == PlayListHandler.getInstance().getCurrentPlayList(true).size() - 1;

        int rowMovedCounter = 0;
        // Move every row
        for (PlayListDragableRow rowDragged : rowsDragged) {
            // Calculate drop row, since targetRow is the row where to drop the first row (baseRow)
            int dropRow = targetRow + (rowDragged.getRowPosition() - baseRow);
            if (dropAtEnd) {
                dropRow = PlayListHandler.getInstance().getCurrentPlayList(true).size() - (rowMovedCounter + 1);
            }
            rowsToKeepSelected.add(dropRow);
            PlayListHandler.getInstance().moveRowTo(rowDragged.getRowPosition(), dropRow);
            rowMovedCounter++;
        }

        // Refresh play list after moving
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        // Set dragged rows as selected
        for (Integer rowToKeepSelected : rowsToKeepSelected) {
            GuiHandler.getInstance().getPlayListTable().getSelectionModel().addSelectionInterval(rowToKeepSelected, rowToKeepSelected);
        }

        return true;
    }

    private static List<File> textURIListToFileList(String data) {
        List<File> list = new ArrayList<File>(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
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
                logger.error(LogCategories.DESKTOP, e);
            } catch (IllegalArgumentException e) {
                logger.error(LogCategories.DESKTOP, e);
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static boolean processExternalImport(TransferSupport support) {
        List<File> files = null;
        try {
            // External drag and drop for Windows
            if (hasFileFlavor(support.getDataFlavors())) {
                files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                // External drag and drop for Linux
            } else if (hasURIListFlavor(support.getDataFlavors())) {
                files = textURIListToFileList((String) support.getTransferable().getTransferData(uriListFlavor));
            } else if (hasStringFlavor(support.getDataFlavors())) {
                String str = ((String) support.getTransferable().getTransferData(DataFlavor.stringFlavor));
                logger.info(LogCategories.DESKTOP, str);
            }
        } catch (UnsupportedFlavorException e) {
            logger.error(LogCategories.DESKTOP, e);
        } catch (IOException e) {
            logger.error(LogCategories.DESKTOP, e);
        }

        if (files != null && !files.isEmpty()) {
            List<AudioObject> filesToAdd = new ArrayList<AudioObject>();
            for (File f : files) {
                if (f.isDirectory()) {
                    filesToAdd.addAll(RepositoryLoader.getSongsForFolder(f, null));
                } else if (AudioFile.isValidAudioFile(f)) {
                    AudioFile song = new AudioFile(f.getAbsolutePath());
                    filesToAdd.add(song);
                } else if (f.getName().toLowerCase().endsWith("m3u")) {
                    filesToAdd.addAll(PlayListIO.getFilesFromList(f));
                }
            }
            int dropRow = GuiHandler.getInstance().getPlayListTable().rowAtPoint(support.getDropLocation().getDropPoint());

            if (!filesToAdd.isEmpty()) {
                List<AudioObject> songsSorted = SortType.sort(filesToAdd);
                PlayListHandler.getInstance().addToPlayList(dropRow, songsSorted, true);
                // Keep selected rows: if drop row is the bottom of play list (-1) then select last row
                if (dropRow == -1) {
                    dropRow = PlayListHandler.getInstance().getCurrentPlayList(true).size() - filesToAdd.size();
                }
                GuiHandler.getInstance().getPlayListTable().getSelectionModel().addSelectionInterval(dropRow, dropRow + filesToAdd.size() - 1);
            }
            return true;
        }
        return false;
    }

}
