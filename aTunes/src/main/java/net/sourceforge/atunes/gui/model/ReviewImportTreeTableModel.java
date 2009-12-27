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
package net.sourceforge.atunes.gui.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.TagAttributesReviewed;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * The Class ReviewImportTreeTableModel.
 */
public final class ReviewImportTreeTableModel extends AbstractTreeTableModel {

    private static final long serialVersionUID = 1997644065009669746L;

    /** List of radio labels */
    private List<File> folders;

    /** AudioFiles to import */
    private List<AudioFile> audioFilesToImport;

    /** A hash map to store folders and its children folders */
    private HashMap<File, List<File>> foldersMap = new HashMap<File, List<File>>();

    /** JXTreeTable that uses this model */
    private JXTreeTable treeTable;

    /** Root node object */
    private static final String ROOT = "ROOT";

    /**
     * Object that contains all changes that must be applied to tags when
     * importing
     */
    private TagAttributesReviewed tagAttributesReviewed;

    /**
     * Constructor
     * 
     * @param folders
     * @param filesToImport
     * @param treeTable
     */
    public ReviewImportTreeTableModel(List<File> folders, List<AudioFile> filesToImport, JXTreeTable treeTable) {
        super(new DefaultMutableTreeTableNode(ROOT));
        this.folders = folders;
        this.audioFilesToImport = filesToImport;
        this.treeTable = treeTable;
        this.tagAttributesReviewed = new TagAttributesReviewed();
        Collections.sort(this.folders);
    }

    /**
     * Sets cell editors
     */
    public void setCellEditors() {
        for (int i = 0; i < this.tagAttributesReviewed.getTagAttributesCount(); i++) {
            TableCellEditor cellEditor = this.tagAttributesReviewed.getCellEditorForTagAttribute(i);
            if (cellEditor != null) {
                this.treeTable.getColumnExt(i + 1).setCellEditor(cellEditor);
            }
        }
    }

    /**
     * 
     * @param node
     * @return <code>true</code> if node is root
     */
    private boolean isRoot(DefaultMutableTreeTableNode node) {
        return node.getUserObject().equals(ROOT);
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (isRoot((DefaultMutableTreeTableNode) parent)) {
            return new DefaultMutableTreeTableNode(folders.get(index));
        }

        File folder = ((File) ((DefaultMutableTreeTableNode) parent).getUserObject()).getAbsoluteFile();
        if (foldersMap.containsKey(folder)) {
            return new DefaultMutableTreeTableNode(foldersMap.get(folder).get(index));
        }

        File[] childFiles = folder.listFiles();
        List<File> childFolders = new ArrayList<File>();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                childFolders.add(childFile);
            }
        }

        Collections.sort(childFolders);

        foldersMap.put(folder, childFolders);
        return new DefaultMutableTreeTableNode(childFolders.get(index));
    }

    @Override
    public int getChildCount(Object parent) {
        if (isRoot((DefaultMutableTreeTableNode) parent)) {
            return folders.size();
        }

        File folder = ((File) ((DefaultMutableTreeTableNode) parent).getUserObject()).getAbsoluteFile();
        if (foldersMap.containsKey(folder)) {
            return foldersMap.get(folder).size();
        }

        File[] childFiles = folder.listFiles();
        List<File> childFolders = new ArrayList<File>();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                childFolders.add(childFile);
            }
        }

        Collections.sort(childFolders);

        foldersMap.put(folder, childFolders);
        return childFolders.size();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        File folder = ((File) ((DefaultMutableTreeTableNode) parent).getUserObject()).getAbsoluteFile();
        File childFolder = ((File) ((DefaultMutableTreeTableNode) child).getUserObject()).getAbsoluteFile();
        if (foldersMap.containsKey(folder)) {
            return foldersMap.get(folder).indexOf(childFolder);
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return this.tagAttributesReviewed.getTagAttributesCount() + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return I18nUtils.getString("FOLDER");
        }

        return I18nUtils.getString(this.getTagAttributesReviewed().getTagAttributeName(column - 1));
    }

    /**
     * Returns value of a tag attribute for a given AudioFile at the given
     * column
     * 
     * @param column
     * @param audioFile
     * @return
     */
    private String getValueForColumn(int column, AudioFile audioFile) {
        return this.tagAttributesReviewed.getValueForTagAttribute(column - 1, audioFile);
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (isRoot((DefaultMutableTreeTableNode) node)) {
            return "";
        }

        File folder = ((File) ((DefaultMutableTreeTableNode) node).getUserObject()).getAbsoluteFile();
        if (column == 0) {
            if (folders.contains(folder)) {
                return folder.getAbsolutePath();
            }
            return folder.getName();
        }

        String change = this.tagAttributesReviewed.getChangeForAttributeAndFolder(column - 1, folder);
        if (change != null) {
            return change;
        }

        String value = "";
        for (AudioFile audioFile : audioFilesToImport) {
            if (audioFile.getFile().getParentFile().equals(folder)) {
                if (value.equals("")) {
                    value = getValueForColumn(column, audioFile);
                    if (value == null) {
                        value = "";
                    }
                } else {
                    if (!value.equals(getValueForColumn(column, audioFile))) {
                        value = "";
                        break;
                    }
                }
            }
        }
        return value;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return column != 0;
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
        super.setValueAt(value, node, column);
        File folder = ((File) ((DefaultMutableTreeTableNode) node).getUserObject()).getAbsoluteFile();
        recursiveFolderChange(column, folder, (String) value);
        // If folder has childs then update UI to show recursive changes
        if (!foldersMap.get(folder).isEmpty()) {
        	// Use repaint instead of updateUI since there are problems when using with combo box cell editors
            treeTable.repaint();
        }
    }

    public void setValueForColumn(int row, String tagAttributeName, String value) {
        int column = this.tagAttributesReviewed.getTagAttributeIndex(tagAttributeName);
        setValueAt(value, treeTable.getPathForRow(row).getLastPathComponent(), column + 1);
    }

    /**
     * Stores recursively changes of a tag attribute in a folder
     * 
     * @param column
     * @param folder
     * @param value
     */
    private void recursiveFolderChange(int column, File folder, String value) {
        this.tagAttributesReviewed.setTagAttributeForFolder(column - 1, folder, value);
        for (File childFolder : foldersMap.get(folder)) {
            recursiveFolderChange(column, childFolder, value);
        }
    }

    /**
     * @return the tagAttributesReviewed
     */
    public TagAttributesReviewed getTagAttributesReviewed() {
        return tagAttributesReviewed;
    }
}
