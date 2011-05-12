/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class MultiFolderSelectionDialog. Allows the selection of the repository
 * folders.
 */
public final class MultiFolderSelectionDialog extends AbstractCustomModalDialog {

    /**
     * Width of a check box. This is used to calculate if user pressed mouse
     * button inside checkbox
     */
    private int checkBoxWidth = new JCheckBox().getPreferredSize().width;

    private final class SetTreeSwingWorker extends SwingWorker<CheckNode, Void> {
		private final class FileSystemTreeTreeWillExpandListener implements
				TreeWillExpandListener {
			private final class TreeWillExpandSwingWorker extends
					SwingWorker<List<CheckNode>, Void> {
				private final CheckNode selectedNode;

				private TreeWillExpandSwingWorker(CheckNode selectedNode) {
					this.selectedNode = selectedNode;
				}

				@Override
				protected List<CheckNode> doInBackground() throws Exception {

				    List<CheckNode> result = new ArrayList<CheckNode>();

				    Directory dir = (Directory) selectedNode.getUserObject();
				    File[] files = fsView.getFiles(dir.getFile(), true);
				    Arrays.sort(files);
				    for (File f : files) {
				    	// Show only file system elements with path
				    	if (!"".equals(f.getPath().trim())) {
				    		CheckNode treeNode2 = new CheckNode(new Directory(f, fsView.getSystemDisplayName(f)), fsView.getSystemIcon(f));
				    		result.add(treeNode2);
				    		treeNode2.add(new DefaultMutableTreeNode(I18nUtils.getString("PLEASE_WAIT") + "..."));
				    		treeNode2.setSelected(selectedNode.isSelected() || selectedFolders.contains(f));
				    		treeNode2.setEnabled(!selectedNode.isSelected());
				    	}
				    }
				    return result;
				}

				@Override
				protected void done() {
				    selectedNode.removeAllChildren();
				    try {
				        List<CheckNode> nodes = get();
				        for (CheckNode node : nodes) {
				            selectedNode.add(node);
				        }
				        ((DefaultTreeModel) fileSystemTree.getModel()).reload(selectedNode);
				    } catch (InterruptedException e) {
				        getLogger().internalError(e);
				    } catch (ExecutionException e) {
				        getLogger().internalError(e);
				    }
				}
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
			    // Nothing to do
			}

			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
			    fileSystemTree.setSelectionPath(event.getPath());
			    new TreeWillExpandSwingWorker((CheckNode) event.getPath().getLastPathComponent()).execute();
			}
		}

		private final class FileSystemTreeMouseAdapter extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getButton() == MouseEvent.BUTTON1) {
			        int x = e.getX();
			        int y = e.getY();
			        int row = fileSystemTree.getRowForLocation(x, y);
			        TreePath path = fileSystemTree.getPathForRow(row);

			        // If user pressed button over text area, don't select node
			        if (x > fileSystemTree.getPathBounds(path).x + checkBoxWidth) {
			            return;
			        }

			        if (path != null) {
			            //fileSystemTree.expandPath(path);
			            CheckNode node = (CheckNode) path.getLastPathComponent();

			            if (node.isEnabled()) {

			                boolean isSelected = !(node.isSelected());
			                node.setSelected(isSelected);

			                if (isSelected) {
			                    // Find if another child folder has been added before
			                    List<File> childFolders = new ArrayList<File>();
			                    for (File f : selectedFolders) {
			                        if (f.getAbsolutePath().startsWith(node.getDir().getFile().getAbsolutePath())) {
			                            childFolders.add(f);
			                        }
			                    }
			                    for (File f : childFolders) {
			                        selectedFolders.remove(f);
			                    }

			                    selectedFolders.add(node.getDir().getFile());
			                } else {
			                    selectedFolders.remove(node.getDir().getFile());
			                }

			                // I need revalidate if node is root.  but why?
			                if (row == 0) {
			                    fileSystemTree.revalidate();
			                    fileSystemTree.repaint();
			                }
			            }
			        }
			    }
			}
		}

		@Override
		protected CheckNode doInBackground() throws Exception {
		    File[] roots = fsView.getRoots();

		    CheckNode root = new CheckNode();

		    for (File f : roots) {
		        CheckNode treeNode = new CheckNode(new Directory(f, fsView.getSystemDisplayName(f)), fsView.getSystemIcon(f));
		        root.add(treeNode);
		        File[] files = fsView.getFiles(f, true);
		        Arrays.sort(files);
		        for (File f2 : files) {
		            File[] f2Childs = f2.listFiles();
		            if (f2Childs != null) {
		                boolean hasDirs = hasDirectories(f2Childs);
		                CheckNode treeNode2 = new CheckNode(new Directory(f2, fsView.getSystemDisplayName(f2)), fsView.getSystemIcon(f2));
		                treeNode.add(treeNode2);
		                if (hasDirs) {
		                    treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
		                }
		            }
		        }
		    }
		    return root;
		}

		@Override
		protected void done() {
		    try {
		        DefaultTreeModel model = new DefaultTreeModel(get());
		        fileSystemTree.setModel(model);
		        fileSystemTree.setRootVisible(false);
		        fileSystemTree.expandRow(0);
		        fileSystemTree.setSelectionRow(0);

		        fileSystemTree.addMouseListener(new FileSystemTreeMouseAdapter());
		        fileSystemTree.addTreeWillExpandListener(new FileSystemTreeTreeWillExpandListener());

		        fileSystemTree.revalidate();
		        fileSystemTree.repaint();
		    } catch (Exception e) {
		        getLogger().internalError(e);
		    } finally {
		        okButton.setEnabled(true);
		        // Show default cursor
		        MultiFolderSelectionDialog.this.setCursor(Cursor.getDefaultCursor());
		    }

		}
	}

	/**
     * The Class CheckNode.
     */
    private class CheckNode extends DefaultMutableTreeNode {

        private static final long serialVersionUID = 3563009061452848710L;

        /** The is selected. */
        private boolean isSelected;

        /** The enabled. */
        private boolean enabled = true;

        /** The dir. */
        private Directory dir;

        /** The icon. */
        private Icon icon;

        /**
         * Instantiates a new check node.
         */
        public CheckNode() {
            this(null, null);
        }

        /**
         * Instantiates a new check node.
         * 
         * @param userObject
         *            the user object
         */
        public CheckNode(Object userObject, Icon icon) {
            this(userObject, true, icon);
        }

        /**
         * Instantiates a new check node.
         * 
         * @param userObject
         *            the user object
         * @param allowsChildren
         *            the allows children
         */
        public CheckNode(Object userObject, boolean allowsChildren, Icon icon) {
            super(userObject, allowsChildren);
            this.setIcon(icon);
            this.setDir((Directory) userObject);
            if (this.getDir() != null && selectedFolders.contains(this.getDir().getFile())) {
                setSelected(true);
            }
        }

        /**
         * Checks if is enabled.
         * 
         * @return true, if is enabled
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Checks if is selected.
         * 
         * @return true, if is selected
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Sets the enabled.
         * 
         * @param enabled
         *            the new enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Sets the selected.
         * 
         * @param isSelected
         *            the new selected
         */
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;

            if (children != null) {
                Enumeration<?> enume = children.elements();
                while (enume.hasMoreElements()) {
                    Object nextElement = enume.nextElement();
                    if (nextElement instanceof CheckNode) {
                        CheckNode node = (CheckNode) nextElement;
                        node.setSelected(isSelected);
                        node.setEnabled(!isSelected);
                    }
                }
            }
            ((DefaultTreeModel) fileSystemTree.getModel()).nodeChanged(this);
        }

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }

        public void setDir(Directory dir) {
            this.dir = dir;
        }

        public Directory getDir() {
            return dir;
        }

    }

    /**
     * The Class CheckRenderer.
     */
    private class CheckRenderer extends DefaultTreeCellRenderer {

        private final class CheckRendererTreeCellRendererCode extends
				AbstractTreeCellRendererCode {
			@Override
			public Component getComponent(Component superComponent, JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
			    String stringValue = value.toString();
			    setEnabled(tree.isEnabled());
			    if (value instanceof CheckNode) {
			        check.setSelected(((CheckNode) value).isSelected());
			        check.setEnabled(((CheckNode) value).isEnabled());
			        label.setFont(tree.getFont());
			        label.setText(stringValue);
			        label.setForeground(superComponent.getForeground());
			        if (((CheckNode) value).getUserObject() instanceof Directory) {
			            Directory content = (Directory) ((CheckNode) value).getUserObject();
			            label.setIcon(((CheckNode) value).getIcon());
			            if (isInPathOfSelectedFolders(content.getFile()) || ((CheckNode) value).isSelected()) {
			                label.setFont(label.getFont().deriveFont(Font.BOLD));
			            }
			        }
			    } else if (value instanceof DefaultMutableTreeNode) {
			        check.setEnabled(false);
			        check.setSelected(false);
			        label.setText(stringValue);
			        label.setIcon(null);
			        label.setFont(tree.getFont());
			        label.setForeground(superComponent.getForeground());
			    }

			    return CheckRenderer.this;
			}
		}

		private static final long serialVersionUID = 5564069979708271654L;

        /** The check. */
        private JCheckBox check;

        /** The label. */
        private JLabel label;

        private AbstractTreeCellRendererCode rendererCode;

        /**
         * Instantiates a new check renderer.
         */
        public CheckRenderer() {
            setLayout(new FlowLayout());
            check = new JCheckBox();
            check.setOpaque(false);
            add(check);
            add(label = new JLabel());
            rendererCode = new CheckRendererTreeCellRendererCode();
        }

        // TODO this method should be aware of component orientation
        @Override
        public void doLayout() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            int y_check = 0;
            int y_label = 0;
            if (d_check.height < d_label.height) {
                y_check = (d_label.height - d_check.height) / 2;
            } else {
                y_label = (d_check.height - d_label.height) / 2;
            }
            check.setLocation(5, y_check);
            check.setBounds(5, y_check, d_check.width + 5, d_check.height);
            label.setLocation(d_check.width + 5, y_label);

            label.setBounds(d_check.width + 5, y_label, d_label.width + 350, d_label.height);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            return new Dimension(d_check.width + d_label.width + 10, (d_check.height < d_label.height ? d_label.height : d_check.height));
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component superComponent = super.getTreeCellRendererComponent(tree, "", isSelected, expanded, leaf, row, hasFocus);
            return rendererCode.getComponent(superComponent, tree, value, isSelected, expanded, leaf, row, hasFocus);
        }

    }

    /**
     * The Class Directory.
     */
    private static class Directory {

        /** The file. */
        private File file;
        /** The display name. */
        private String displayName;

        /**
         * Instantiates a new directory.
         * 
         * @param file
         *            the file
         * @param displayName
         *            the name that should be displayed
         */
        Directory(File file, String displayName) {
            this.setFile(file);
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

    }

    private static final long serialVersionUID = -1612490779910952274L;

    /** The Constant logger. */
    private Logger logger;

    /** The fs view. */
    private static FileSystemView fsView = FileSystemView.getFileSystemView();

    /** The file system tree. */
    private JTree fileSystemTree;

    /** The scroll pane. */
    private JScrollPane scrollPane;

    /** The ok button. */
    private JButton okButton;

    /** The cancel button. */
    private JButton cancelButton;

    /** The text. */
    private JLabel text;

    /** The folders selected. */
    private List<File> selectedFolders;

    /** The cancelled. */
    private boolean cancelled = true;

    /**
     * Instantiates a new multi folder selection dialog.
     * 
     * @param owner
     *            the owner
     */
    public MultiFolderSelectionDialog(JFrame owner) {
        super(owner, 460, 530, true);
        setContent(getContent());
        setResizable(false);
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        MultiFolderSelectionDialog dialog = new MultiFolderSelectionDialog(null);

        dialog.startDialog(null);

        if (!dialog.isCancelled()) {
            List<File> folders = dialog.getSelectedFolders();
            for (File f : folders) {
                System.out.println(f);
            }
        }
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(null);

        text = new JLabel();

        fileSystemTree = new JTree();
        fileSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        scrollPane = new JScrollPane();

        okButton = new CustomButton(null, I18nUtils.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = false;
                dispose();
            }
        });
        cancelButton = new CustomButton(null, I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        text.setSize(430, 20);
        text.setLocation(10, 10);
        panel.add(text);

        scrollPane.setSize(430, 410);
        scrollPane.setLocation(10, 40);
        panel.add(scrollPane);

        okButton.setSize(100, 25);
        okButton.setLocation(230, 460);
        panel.add(okButton);

        cancelButton.setSize(100, 25);
        cancelButton.setLocation(340, 460);
        panel.add(cancelButton);

        return panel;
    }

    /**
     * Gets the selected folders.
     * 
     * @return the selected folders
     */
    public List<File> getSelectedFolders() {
        return selectedFolders;
    }

    /**
     * Checks if is cancelled.
     * 
     * @return true, if is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Checks if is in path of selected folders.
     * 
     * @param dir
     *            the dir
     * 
     * @return true, if is in path of selected folders
     */
    boolean isInPathOfSelectedFolders(File dir) {
        String dirPath = dir.getAbsolutePath().concat(OsManager.getFileSeparator());
        for (File folder : selectedFolders) {
            if (folder.getAbsolutePath().startsWith(dirPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the text.
     * 
     * @param text
     *            the new text
     */
    public void setText(String text) {
        this.text.setText(text);
    }

    /**
     * Sets the tree.
     */
    private void setTree() {

        DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("PLEASE_WAIT") + "..."));
        fileSystemTree.setModel(model);
        fileSystemTree.setRootVisible(true);
        fileSystemTree.setCellRenderer(new CheckRenderer());
        scrollPane.setViewportView(fileSystemTree);
        scrollPane.setVisible(true);

        new SetTreeSwingWorker().execute();

        // Show wait cursor
        MultiFolderSelectionDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        okButton.setEnabled(false);
    }

    /**
     * Checks if an array of files contains at least one directory.
     * 
     * @param files
     *            The array of files that should be checked
     * @return if at least one directory is contained
     */
    boolean hasDirectories(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start dialog.
     * 
     * @param selectedFolders
     *            the selected folders
     */
    public void startDialog(List<File> selectedFolders) {
        this.cancelled = true;
        if (selectedFolders == null) {
            this.selectedFolders = new ArrayList<File>();
        } else {
            this.selectedFolders = selectedFolders;
        }
        setTree();
        setVisible(true);
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
