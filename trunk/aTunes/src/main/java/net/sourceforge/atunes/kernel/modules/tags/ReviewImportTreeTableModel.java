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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * The Class ReviewImportTreeTableModel.
 */
public final class ReviewImportTreeTableModel extends AbstractTreeTableModel {

	/** List of radio labels */
	private final List<File> folders;

	/** AudioFiles to import */
	private final List<ILocalAudioObject> audioFilesToImport;

	/** A hash map to store folders and its children folders */
	private final Map<File, List<File>> foldersMap = new HashMap<File, List<File>>();

	/** JXTreeTable that uses this model */
	private final JXTreeTable treeTable;

	/** Root node object */
	private static final String ROOT = "ROOT";

	/**
	 * Object that contains all changes that must be applied to tags when
	 * importing
	 */
	private final ITagAttributesReviewed tagAttributesReviewed;

	private final IFileManager fileManager;

	/**
	 * Constructor
	 * 
	 * @param folders
	 * @param filesToImport
	 * @param treeTable
	 * @param tagAttributesReviewed
	 * @param fileManager
	 */
	public ReviewImportTreeTableModel(final List<File> folders,
			final List<ILocalAudioObject> filesToImport,
			final JXTreeTable treeTable,
			final TagAttributesReviewed tagAttributesReviewed,
			final IFileManager fileManager) {
		super(new DefaultMutableTreeTableNode(ROOT));
		this.folders = folders;
		this.audioFilesToImport = filesToImport;
		this.treeTable = treeTable;
		this.tagAttributesReviewed = tagAttributesReviewed;
		this.fileManager = fileManager;
		Collections.sort(this.folders);
	}

	/**
	 * Sets cell editors
	 */
	public void setCellEditors() {
		for (int i = 0; i < this.tagAttributesReviewed.getTagAttributesCount(); i++) {
			TableCellEditor cellEditor = this.tagAttributesReviewed
					.getCellEditorForTagAttribute(i);
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
	private boolean isRoot(final DefaultMutableTreeTableNode node) {
		return node.getUserObject().equals(ROOT);
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		if (isRoot((DefaultMutableTreeTableNode) parent)) {
			return new DefaultMutableTreeTableNode(this.folders.get(index));
		}

		File folder = ((File) ((DefaultMutableTreeTableNode) parent)
				.getUserObject()).getAbsoluteFile();
		if (this.foldersMap.containsKey(folder)) {
			return new DefaultMutableTreeTableNode(this.foldersMap.get(folder)
					.get(index));
		}

		File[] childFiles = folder.listFiles();
		List<File> childFolders = new ArrayList<File>();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				childFolders.add(childFile);
			}
		}

		Collections.sort(childFolders);

		this.foldersMap.put(folder, childFolders);
		return new DefaultMutableTreeTableNode(childFolders.get(index));
	}

	@Override
	public int getChildCount(final Object parent) {
		if (isRoot((DefaultMutableTreeTableNode) parent)) {
			return this.folders.size();
		}

		File folder = ((File) ((DefaultMutableTreeTableNode) parent)
				.getUserObject()).getAbsoluteFile();
		if (this.foldersMap.containsKey(folder)) {
			return this.foldersMap.get(folder).size();
		}

		File[] childFiles = folder.listFiles();
		List<File> childFolders = new ArrayList<File>();
		if (childFiles != null) {
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					childFolders.add(childFile);
				}
			}
			Collections.sort(childFolders);
		}

		this.foldersMap.put(folder, childFolders);
		return childFolders.size();
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		Object folderObject = ((DefaultMutableTreeTableNode) parent)
				.getUserObject();
		Object childFolderObject = ((DefaultMutableTreeTableNode) child)
				.getUserObject();

		if (folderObject instanceof File && childFolderObject instanceof File) {
			File folder = ((File) folderObject).getAbsoluteFile();
			File childFolder = ((File) childFolderObject).getAbsoluteFile();
			if (this.foldersMap.containsKey(folder)) {
				return this.foldersMap.get(folder).indexOf(childFolder);
			}
		}
		return 0;
	}

	@Override
	public int getColumnCount() {
		return this.tagAttributesReviewed.getTagAttributesCount() + 1;
	}

	@Override
	public String getColumnName(final int column) {
		if (column == 0) {
			return I18nUtils.getString("FOLDER");
		}

		return I18nUtils.getString(this.getTagAttributesReviewed()
				.getTagAttributeName(column - 1));
	}

	/**
	 * Returns value of a tag attribute for a given LocalAudioObject at the
	 * given column
	 * 
	 * @param column
	 * @param audioFile
	 * @return
	 */
	private String getValueForColumn(final int column,
			final ILocalAudioObject audioFile) {
		return this.tagAttributesReviewed.getValueForTagAttribute(column - 1,
				audioFile);
	}

	@Override
	public Object getValueAt(final Object node, final int column) {
		if (isRoot((DefaultMutableTreeTableNode) node)) {
			return "";
		}

		File folder = ((File) ((DefaultMutableTreeTableNode) node)
				.getUserObject()).getAbsoluteFile();
		if (column == 0) {
			if (this.folders.contains(folder)) {
				return net.sourceforge.atunes.utils.FileUtils.getPath(folder);
			}
			return folder.getName();
		}

		String change = this.tagAttributesReviewed
				.getChangeForAttributeAndFolder(column - 1, folder);
		if (change != null) {
			return change;
		}

		String value = "";
		for (ILocalAudioObject audioFile : this.audioFilesToImport) {
			if (this.fileManager.getFolder(audioFile).equals(folder)) {
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
	public boolean isCellEditable(final Object node, final int column) {
		return column != 0;
	}

	@Override
	public void setValueAt(final Object value, final Object node,
			final int column) {
		super.setValueAt(value, node, column);
		File folder = ((File) ((DefaultMutableTreeTableNode) node)
				.getUserObject()).getAbsoluteFile();
		recursiveFolderChange(column, folder, (String) value);
		// If folder has childs then update UI to show recursive changes
		if (!this.foldersMap.get(folder).isEmpty()) {
			// Use repaint instead of updateUI since there are problems when
			// using with combo box cell editors
			this.treeTable.repaint();
		}
	}

	/**
	 * Sets value of column for given row (a folder)
	 * 
	 * @param row
	 * @param tagAttributeName
	 * @param value
	 */
	public void setValueForColumn(final int row, final String tagAttributeName,
			final String value) {
		int column = this.tagAttributesReviewed
				.getTagAttributeIndex(tagAttributeName);
		setValueAt(value, this.treeTable.getPathForRow(row)
				.getLastPathComponent(), column + 1);
	}

	/**
	 * Stores recursively changes of a tag attribute in a folder
	 * 
	 * @param column
	 * @param folder
	 * @param value
	 */
	private void recursiveFolderChange(final int column, final File folder,
			final String value) {
		this.tagAttributesReviewed.setTagAttributeForFolder(column - 1, folder,
				value);
		for (File childFolder : this.foldersMap.get(folder)) {
			recursiveFolderChange(column, childFolder, value);
		}
	}

	/**
	 * @return the tagAttributesReviewed
	 */
	public ITagAttributesReviewed getTagAttributesReviewed() {
		return this.tagAttributesReviewed;
	}
}
