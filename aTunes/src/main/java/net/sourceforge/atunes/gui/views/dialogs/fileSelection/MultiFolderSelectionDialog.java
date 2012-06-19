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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.AbstractTreeCellRendererCode;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IMultiFolderSelectionDialog;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITreeCellRendererCode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * The Class MultiFolderSelectionDialog. Allows the selection of the repository
 * folders.
 */
public final class MultiFolderSelectionDialog extends AbstractCustomDialog implements IMultiFolderSelectionDialog {

    /**
     * Width of a check box. This is used to calculate if user pressed mouse
     * button inside checkbox
     */
    private int checkBoxWidth = new JCheckBox().getPreferredSize().width;

    private final class SetTreeSwingWorker extends SwingWorker<CheckNode, Void> {
		private final class FileSystemTreeTreeWillExpandListener implements TreeWillExpandListener {
			
			private final class TreeWillExpandSwingWorker extends SwingWorker<List<CheckNode>, Void> {
				private final CheckNode selectedNode;

				private TreeWillExpandSwingWorker(CheckNode selectedNode) {
					this.selectedNode = selectedNode;
				}

				@Override
				protected List<CheckNode> doInBackground() {

				    List<CheckNode> result = new ArrayList<CheckNode>();

				    MultiFolderSelectionDialogDirectory dir = (MultiFolderSelectionDialogDirectory) selectedNode.getUserObject();
				    File[] files = fsView.getFiles(dir.getFile(), true);
				    Arrays.sort(files);
				    for (File f : files) {
				    	// Show only file system elements with path
				    	if (!"".equals(f.getPath().trim()) && !f.isFile()) {
				    		CheckNode treeNode2 = new CheckNode(new MultiFolderSelectionDialogDirectory(f, fsView.getSystemDisplayName(f)), fsView.getSystemIcon(f));
				    		result.add(treeNode2);
				    		File[] subfolders = f.listFiles();
				    		if (subfolders != null && subfolders.length > 0 && hasDirectories(subfolders, fsView)) {
				    			treeNode2.add(new DefaultMutableTreeNode(I18nUtils.getString("PLEASE_WAIT") + "..."));
				    		}
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
				        Logger.error(e);
				    } catch (ExecutionException e) {
				        Logger.error(e);
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
			    if (GuiUtils.isPrimaryMouseButton(e)) {
			        int x = e.getX();
			        int y = e.getY();
			        int row = fileSystemTree.getRowForLocation(x, y);
			        TreePath path = fileSystemTree.getPathForRow(row);

			        // If user pressed button out of tree do nothing
			        if (path == null) {
			        	return;
			        }
			        
			        // If user pressed button over text area, don't select node
			        if (x > fileSystemTree.getPathBounds(path).x + checkBoxWidth) {
			            return;
			        }

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

		@Override
		protected CheckNode doInBackground() {
		    File[] roots = fsView.getRoots();

		    CheckNode root = new CheckNode();

		    for (File f : roots) {
		        CheckNode treeNode = new CheckNode(new MultiFolderSelectionDialogDirectory(f, fsView.getSystemDisplayName(f)), fsView.getSystemIcon(f));
		        root.add(treeNode);
		        File[] files = fsView.getFiles(f, true);
		        Arrays.sort(files);
		        for (File f2 : files) {
		            File[] f2Childs = f2.listFiles();
		            if (f2Childs != null && f2Childs.length > 0) {
		                boolean hasDirs = hasDirectories(f2Childs, fsView);
		                CheckNode treeNode2 = new CheckNode(new MultiFolderSelectionDialogDirectory(f2, fsView.getSystemDisplayName(f2)), fsView.getSystemIcon(f2));
		                treeNode.add(treeNode2);
		                if (hasDirs) {
		                    treeNode2.add(new DefaultMutableTreeNode(I18nUtils.getString("PLEASE_WAIT") + "..."));
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
		    } catch (InterruptedException e) {
		        Logger.error(e);
		    } catch (ExecutionException e) {
		        Logger.error(e);
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
        private MultiFolderSelectionDialogDirectory dir;

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
            this.setDir((MultiFolderSelectionDialogDirectory) userObject);
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
        public final void setSelected(boolean isSelected) {
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

        /**
         * @return
         */
        public Icon getIcon() {
            return icon;
        }

        /**
         * @param icon
         */
        public final void setIcon(Icon icon) {
            this.icon = icon;
        }

        /**
         * @param dir
         */
        public final void setDir(MultiFolderSelectionDialogDirectory dir) {
            this.dir = dir;
        }

        /**
         * @return
         */
        public final MultiFolderSelectionDialogDirectory getDir() {
            return dir;
        }

    }

    /**
     * The Class CheckRenderer.
     */
    private class CheckRenderer extends DefaultTreeCellRenderer {

        private final class CheckRendererTreeCellRendererCode extends AbstractTreeCellRendererCode<JComponent, Object> {
        	
			@Override
			public JComponent getComponent(JComponent superComponent, JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
				JCheckBox check = new JCheckBox();
	            JLabel labelComponent = new JLabel();

			    String stringValue = value.toString();
			    setEnabled(tree.isEnabled());
			    if (value instanceof CheckNode) {
			    	check.setIcon(null);
			        check.setSelected(((CheckNode) value).isSelected());
			        check.setEnabled(((CheckNode) value).isEnabled());
			        labelComponent.setFont(tree.getFont());
			        labelComponent.setText(stringValue);
			        labelComponent.setForeground(superComponent.getForeground());
			        if (((CheckNode) value).getUserObject() instanceof MultiFolderSelectionDialogDirectory) {
			            MultiFolderSelectionDialogDirectory content = (MultiFolderSelectionDialogDirectory) ((CheckNode) value).getUserObject();
			            labelComponent.setIcon(((CheckNode) value).getIcon());
			            if (isInPathOfSelectedFolders(content.getFile()) || ((CheckNode) value).isSelected()) {
			                labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
			            }
			        }
			    } else if (value instanceof DefaultMutableTreeNode) {
			        check.setEnabled(false);
			        check.setSelected(false);
			        labelComponent.setText(stringValue);
			        labelComponent.setIcon(null);
			        labelComponent.setFont(tree.getFont());
			        labelComponent.setForeground(superComponent.getForeground());
			    }
			    
			    check.setOpaque(false);
			    labelComponent.setOpaque(false);
			    
			    JPanel panel = new JPanel(new BorderLayout());
			    panel.add(check, BorderLayout.WEST);
			    panel.add(labelComponent, BorderLayout.CENTER);
			    
	        	panel.setOpaque(isSelected);
	        	if (isSelected) {
	        		panel.setBackground(UIManager.getColor("Tree.selectionBackground"));
	        		panel.setForeground(UIManager.getColor("Tree.selectionForeground"));
	        		labelComponent.setForeground(UIManager.getColor("Tree.selectionForeground"));
	        	}

			    return panel;
			}
		}

		private static final long serialVersionUID = 5564069979708271654L;

        private transient ITreeCellRendererCode<JComponent, Object> rendererCode;
        
        private transient JLabel label;

        /**
         * Instantiates a new check renderer.
         */
        public CheckRenderer() {
        	label = new JLabel();
            rendererCode = new CheckRendererTreeCellRendererCode();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            return rendererCode.getComponent(label, tree, value, isSelected, expanded, leaf, row, hasFocus);
        }

    }

    private static final long serialVersionUID = -1612490779910952274L;

    /** The fs view. */
    private static FileSystemView fsView = FileSystemView.getFileSystemView();

    /** The file system tree. */
    private JTree fileSystemTree;

    /** The scroll pane. */
    private JScrollPane scrollPane;

    /** The ok button. */
    private JButton okButton;

    /** The text. */
    private JLabel text;

    /** The folders selected. */
    private List<File> selectedFolders;

    /** The canceled. */
    private boolean canceled = true;
    
    private IOSManager osManager;

    /**
     * Instantiates a new multi folder selection dialog.
     * 
     * @param frame
     */
    public MultiFolderSelectionDialog(IFrame frame) {
        super(frame, 460, 530, true, CloseAction.DISPOSE);
    }

    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    @Override
    public void initialize() {
        add(getContent(getLookAndFeel()));
        setResizable(false);
    }
    
    /**
     * Gets the content.
     * 
     * @param lookAndFeel
     * @return
     */
    private JPanel getContent(ILookAndFeel lookAndFeel) {
        JPanel panel = new JPanel(null);

        text = new JLabel();

        fileSystemTree = new JTree();
        fileSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        scrollPane = lookAndFeel.getTreeScrollPane(fileSystemTree);

        okButton = new JButton(I18nUtils.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canceled = false;
                dispose();
            }
        });
        JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
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

    @Override
	public List<File> getSelectedFolders() {
        return selectedFolders;
    }

    @Override
	public boolean isCancelled() {
        return canceled;
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
        String dirPath = dir.getAbsolutePath().concat(osManager.getFileSeparator());
        for (File folder : selectedFolders) {
            if (folder.getAbsolutePath().startsWith(dirPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
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
    boolean hasDirectories(File[] files, FileSystemView fsView) {
        for (File file : files) {
            if (file.isDirectory() && fsView.isTraversable(file)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSelectedFolders(List<File> selectedFolders) {
        if (selectedFolders == null) {
            this.selectedFolders = new ArrayList<File>();
        } else {
            this.selectedFolders = selectedFolders;
        }
    }
    
    @Override
	public void showDialog() {
        this.canceled = true;
        setTree();
        setVisible(true);
    }
    
    @Override
    public void hideDialog() {
    	setVisible(false);
    }
}