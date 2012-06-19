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

package net.sourceforge.atunes.gui.views.dialogs.fileSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.ICustomFileSelectionDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class FileSelectionDialog.
 */
public final class CustomFileSelectionDialog extends AbstractCustomDialog implements ICustomFileSelectionDialog {

    private final class OkButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		    selectedDir = null;
		    selectedFiles = null;
		    if (directoryOnly) {
		        if (fileSystemList.getSelectedValue() != null) {
		            selectedDir = new File(((File) fileSystemList.getSelectedValue()).getAbsolutePath());
		        } else {
		            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
		            selectedDir = ((CustomFileSelectionDialogDirectory) node.getUserObject()).getFile();
		        }
		    } else {
		        if (fileSystemList.getSelectedValues().length > 0) {
		            Object[] files = fileSystemList.getSelectedValues();
		            selectedFiles = new File[files.length];
		            System.arraycopy(files, 0, selectedFiles, 0, files.length);
		        } else {
		            selectedFiles = new File[1];
		            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
		            selectedFiles[0] = ((CustomFileSelectionDialogDirectory) node.getUserObject()).getFile();
		        }
		    }
		    canceled = false;
		    setVisible(false);
		}
	}

	private static final long serialVersionUID = -1612490779910952274L;

    /** The fs view. */
    static transient FileSystemView fsView = FileSystemView.getFileSystemView();

    /** The file system tree. */
    private JTree fileSystemTree;

    /** The file system list. */
    private JList fileSystemList;

    /** The selection. */
    private JLabel selection;

    /** The dir only. */
    private boolean directoryOnly;

    /** The canceled. */
    private boolean canceled = true;

    /** The selected dir. */
    private File selectedDir;

    /** The selected files. */
    private File[] selectedFiles;
    
    /**
     * Instantiates a new file selection dialog.
     * @param frame
     */
    public CustomFileSelectionDialog(IFrame frame) {
        super(frame, 660, 430);
    }
    
    @Override
    public void initialize() {
        add(getContent());
        setResizable(false);
    }
    
    @Override
    public void setDirectoryOnly(boolean dirOnly) {
		this.directoryOnly = dirOnly;
	}

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(null);

        fileSystemTree = new JTree();
        fileSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scroll1 = getLookAndFeel().getTreeScrollPane(fileSystemTree);

        fileSystemList = getLookAndFeel().getList();
        setListRenderer();
        JScrollPane scroll2 = getLookAndFeel().getListScrollPane(fileSystemList);

        selection = new JLabel();

        JButton okButton = new JButton(I18nUtils.getString("OK"));
        okButton.addActionListener(new OkButtonActionListener());
        JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        scroll1.setSize(220, 320);
        scroll1.setLocation(10, 10);
        panel.add(scroll1);

        scroll2.setSize(400, 320);
        scroll2.setLocation(240, 10);
        panel.add(scroll2);

        selection.setSize(600, 20);
        selection.setLocation(10, 335);
        panel.add(selection);

        okButton.setSize(100, 25);
        okButton.setLocation(430, 360);
        panel.add(okButton);

        cancelButton.setSize(100, 25);
        cancelButton.setLocation(540, 360);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Gets the files.
     * 
     * @param f
     *            the f
     * 
     * @return the files
     */
    File[] getFiles(File f) {
        File[] files = fsView.getFiles(f, true);
        List<File> list = new ArrayList<File>();
        for (File element : files) {
            if (!directoryOnly) {
                list.add(element);
            } else if (element.isDirectory()) {
                list.add(element);
            }
        }
        Collections.sort(list);
        return list.toArray(new File[list.size()]);
    }

    @Override
	public File getSelectedDir() {
        return selectedDir;
    }

    @Override
	public File[] getSelectedFiles() {
        return Arrays.copyOf(selectedFiles, selectedFiles.length);
    }

    @Override
	public boolean isCanceled() {
        return canceled;
    }

    /**
     * Sets the list renderer.
     */
    private void setListRenderer() {
        fileSystemList.setCellRenderer(getLookAndFeel().getListCellRenderer(new FileSystemListCellRendererCode()));
    }

    /**
     * Sets the selection text.
     * 
     * @param f
     *            the new selection text
     */
    void setSelectionText(File f) {
        String displayName;
        if (!fsView.isFileSystem(f)) {
            displayName = fsView.getSystemDisplayName(f);
        } else {
            displayName = f.getAbsolutePath();
        }
        selection.setText(displayName);
    }

    /**
     * Sets the tree.
     */

    private void setTree() {
        File[] roots = fsView.getRoots();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        for (File f : roots) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new CustomFileSelectionDialogDirectory(f));
            root.add(treeNode);
            File[] files = fsView.getFiles(f, true);
            Arrays.sort(files);
            for (File f2 : files) {
                if (fsView.isTraversable(f2)) {
                    DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(new CustomFileSelectionDialogDirectory(f2));
                    treeNode.add(treeNode2);
                    treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
                }
            }
        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        fileSystemTree.setModel(model);
        fileSystemTree.setRootVisible(false);
        fileSystemTree.expandRow(0);
        fileSystemTree.setSelectionRow(0);
        fileSystemList.setListData(getFiles(roots[0]));
        setSelectionText(roots[0]);
        setTreeRenderer();
        fileSystemTree.addTreeWillExpandListener(new FileSystemTreeTreeWillExpandListener(this));
        fileSystemTree.addTreeSelectionListener(new FileSystemTreeTreeSelectionListener(this));
        fileSystemList.addMouseListener(new FileSystemListMouseAdapter(this));
    }

    /**
     * Sets the tree renderer.
     */
    private void setTreeRenderer() {
        fileSystemTree.setCellRenderer(getLookAndFeel().getTreeCellRenderer(new FileSystemTreeCellRendererCode()));
    }

    @Override
	public void showDialog() {
        canceled = true;
        setTree();
        setVisible(true);
    }
    
    /**
     * @return file system list
     */
    JList getFileSystemList() {
		return fileSystemList;
	}
    
    /**
     * @return file system tree
     */
    JTree getFileSystemTree() {
		return fileSystemTree;
	}
    
    @Override
    public void hideDialog() {
    	setVisible(false);
    }
}
