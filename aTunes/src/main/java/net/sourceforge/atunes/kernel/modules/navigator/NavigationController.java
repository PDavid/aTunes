/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.renderers.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.kernel.IController;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowYearsInNavigatorAction;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.internetsearch.Search;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.TreeObject;

final class NavigationController implements AudioFilesRemovedListener, IController {

    private final class ExtendedToolTipActionListener implements ActionListener {
		private final class GetAndSetImageSwingWorker extends
				SwingWorker<ImageIcon, Void> {
			private final Object currentObject;

			private GetAndSetImageSwingWorker(Object currentObject) {
				this.currentObject = currentObject;
			}

			@Override
			protected ImageIcon doInBackground() throws Exception {
			    // Get image for albums
			    return ExtendedToolTip.getImage(currentObject);
			}

			@Override
			protected void done() {
			    try {
			        // Set image in tooltip when done (tooltip can be not visible then)
			        if (currentExtendedToolTipContent != null && currentExtendedToolTipContent.equals(currentObject)) {
			            getExtendedToolTip().setImage(get());
			        }
			    } catch (InterruptedException e) {
			        Logger.error(e);
			    } catch (ExecutionException e) {
			        Logger.error(e);
			    }
			}
		}

		@Override
        public void actionPerformed(ActionEvent arg0) {
            getExtendedToolTip().setVisible(true);

            final Object currentObject = currentExtendedToolTipContent;
            SwingWorker<ImageIcon, Void> getAndSetImage = new GetAndSetImageSwingWorker(currentObject);
            getAndSetImage.execute();

        }
	}

    private NavigationTreePanel navigationTreePanel;
    private NavigationTablePanel navigationTablePanel;

    /** The current extended tool tip content. */
    private volatile Object currentExtendedToolTipContent;

    /** The album tool tip. */
    private ExtendedToolTip extendedToolTip;

    /** The popupmenu caller. */
    private JComponent popupMenuCaller;

    /** The column model */
    private AbstractCommonColumnModel columnModel;

    private ColumnSetPopupMenu columnSetPopupMenu;

    /** The timer. */
    private Timer timer = new Timer(0, new ExtendedToolTipActionListener());

    /**
     * State of app
     */
    private IState state;
    
    private AbstractColumnSet navigatorColumnSet;
    
    /**
     * Instantiates a new navigation controller.
     * 
     * @param treePanel
     * @param tablePanel
     * @param state
     */
    NavigationController(NavigationTreePanel treePanel, NavigationTablePanel tablePanel, IState state) {
        this.navigationTreePanel = treePanel;
        this.navigationTablePanel = tablePanel;
        this.state = state;
        addBindings();
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
        this.navigatorColumnSet = (AbstractColumnSet) Context.getBean("navigatorColumnSet");
    }

    public NavigationTreePanel getNavigationTreePanel() {
        return navigationTreePanel;
    }

    public NavigationTablePanel getNavigationTablePanel() {
        return navigationTablePanel;
    }

    @Override
    public void addBindings() {
        NavigationTableModel model = new NavigationTableModel();
        navigationTablePanel.getNavigationTable().setModel(model);
        columnModel = new NavigationTableColumnModel(navigationTablePanel.getNavigationTable(), state);
        navigationTablePanel.getNavigationTable().setColumnModel(columnModel);
        ColumnRenderers.addRenderers(navigationTablePanel.getNavigationTable(), columnModel);

        new ColumnSetRowSorter(navigationTablePanel.getNavigationTable(), model, columnModel);

        // Bind column set popup menu
        columnSetPopupMenu = new ColumnSetPopupMenu(navigationTablePanel.getNavigationTable(), columnModel);

        // Add tree selection listeners to all views
        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            view.getTree().addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    updateTableContent((JTree) e.getSource());
                }
            });
        }

        // Add tree mouse listeners to all views
        // Add tree tool tip listener to all views
        NavigationTreeMouseListener treeMouseListener = new NavigationTreeMouseListener(this, state);
        NavigationTreeToolTipListener tooltipListener = new NavigationTreeToolTipListener(this, state);
        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            view.getTree().addMouseListener(treeMouseListener);
            view.getTree().addMouseListener(tooltipListener);
            view.getTree().addMouseMotionListener(tooltipListener);
            view.getTreeScrollPane().addMouseWheelListener(tooltipListener);
        }

        navigationTablePanel.getNavigationTable().addMouseListener(new NavigationTableMouseListener(this, navigationTablePanel));
        
        // Add combo listener
        navigationTreePanel.getTreeComboBox().addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				NavigationHandler.getInstance().setNavigationView(e.getItem().getClass().getName());
			}
		});
        
        if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().customComboBoxRenderersSupported()) {
        	navigationTreePanel.getTreeComboBox().setRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListCellRenderer(new AbstractListCellRendererCode() {

        		@Override
        		public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        			((JLabel)superComponent).setIcon(((AbstractNavigationView)value).getIcon().getIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForColorMutableIcon(superComponent, isSelected)));
        			((JLabel)superComponent).setText(((AbstractNavigationView)value).getTitle());
        			return superComponent;
        		}
        	}));
        }
    }

    @Override
    public void addStateBindings() {
    }

    /**
     * @return the timer
     */
    public Timer getToolTipTimer() {
        return timer;
    }

    /**
     * Gets the album tool tip.
     * 
     * @return the album tool tip
     */
    public ExtendedToolTip getExtendedToolTip() {
        if (extendedToolTip == null) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            extendedToolTip = new ExtendedToolTip();
            JDialog.setDefaultLookAndFeelDecorated(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isDialogUndecorated());
        }
        return extendedToolTip;
    }

    /**
     * Get files of all selected elements in navigator.
     * 
     * @return the files selected in navigator
     */
    public List<IAudioObject> getFilesSelectedInNavigator() {
        List<IAudioObject> files = new ArrayList<IAudioObject>();
        if (getPopupMenuCaller() instanceof JTable) {
            int[] rows = navigationTablePanel.getNavigationTable().getSelectedRows();
            files.addAll(((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).getAudioObjectsAt(rows));
        } else if (getPopupMenuCaller() instanceof JTree) {
            TreePath[] paths = NavigationHandler.getInstance().getCurrentView().getTree().getSelectionPaths();
            for (TreePath path : paths) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (treeNode.getUserObject() instanceof TreeObject) {
                    files.addAll(getAudioObjectsForTreeNode(NavigationHandler.getInstance().getCurrentView().getClass(),
                            treeNode));
                }
            }
        }
        return files;
    }

    /**
     * Checks if a collection of files have the same parent file.
     * 
     * @param c
     *            collection of files
     * @return if a collection of files have the same parent file
     */
    public boolean sameParentFile(Collection<? extends ILocalAudioObject> c) {
        Set<File> set = new HashSet<File>();
        for (ILocalAudioObject af : c) {
            set.add(af.getFile().getParentFile());
        }
        return set.size() == 1;
    }

    /**
     * Gets the last album tool tip content.
     * 
     * @return the last album tool tip content
     */
    public Object getCurrentExtendedToolTipContent() {
        return currentExtendedToolTipContent;
    }

    /**
     * Gets the audio object in navigation table.
     * 
     * @param row
     *            the row
     * 
     * @return the song in navigation table
     */
    public IAudioObject getAudioObjectInNavigationTable(int row) {
        return ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).getAudioObjectAt(row);
    }

    /**
     * Returns audio objects selected by the given node in the given navigation
     * view
     * 
     * @param navigationViewClass
     * @param node
     * @return
     */
    public List<? extends IAudioObject> getAudioObjectsForTreeNode(Class<? extends AbstractNavigationView> navigationViewClass, DefaultMutableTreeNode node) {
        List<? extends IAudioObject> audioObjects = NavigationHandler.getInstance().getView(navigationViewClass).getAudioObjectForTreeNode(node, state.getViewMode(),
                FilterHandler.getInstance().isFilterSelected(NavigationHandler.getInstance().getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);

        AbstractColumnSet columnSet = NavigationHandler.getInstance().getCurrentView().getCustomColumnSet();
        if (columnSet == null) {
            columnSet = navigatorColumnSet;
        }

        AbstractColumn columnSorted = columnSet.getSortedColumn();
        if (columnSorted != null) {
            Collections.sort(audioObjects, columnSorted.getComparator(false));
        }
        return audioObjects;
    }

    /**
     * Notify device reload.
     */
    public void notifyDeviceReload() {
        NavigationHandler.getInstance().getView(DeviceNavigationView.class).refreshView(state.getViewMode(),
                FilterHandler.getInstance().isFilterSelected(NavigationHandler.getInstance().getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
    }

    @Override
    public void notifyReload() {
    	// Calling inside invokeLater will cause ConcurrentModificationException
    	NavigationHandler.getInstance().refreshCurrentView();
    }

    /**
     * Refresh table.
     */
    public void refreshTable() {
        ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).refresh(TableModelEvent.UPDATE);
    }

    /**
     * Sets the current album tool tip content.
     * 
     * @param currentAlbumToolTipContent
     *            the new current album tool tip content
     */
    @SuppressWarnings("unchecked")
	public void setCurrentExtendedToolTipContent(Object currentAlbumToolTipContent) {
        this.currentExtendedToolTipContent = currentAlbumToolTipContent;
        getExtendedToolTip().setSizeToFitImage(currentAlbumToolTipContent instanceof TreeObject && ((TreeObject<? extends IAudioObject>) currentAlbumToolTipContent).isExtendedToolTipImageSupported());
    }

    /**
     * Sets the navigation view and optionally saves navigation view
     * 
     * @param view
     *            the new navigation view
     *            
     * @param saveNavigationView
     */
    public void setNavigationView(String view, boolean saveNavigationView) {
        Class<? extends AbstractNavigationView> navigationView = NavigationHandler.getInstance().getViewByName(view);
        if (navigationView == null) {
            navigationView = RepositoryNavigationView.class;
        }
        
        if (saveNavigationView) {
        	state.setNavigationView(navigationView.getName());
        }
        
        getNavigationTreePanel().showNavigationView(NavigationHandler.getInstance().getView(navigationView));

        boolean viewModeSupported = NavigationHandler.getInstance().getView(navigationView).isViewModeSupported();
        Actions.getAction(ShowAlbumsInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowArtistsInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowFoldersInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowGenresInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowYearsInNavigatorAction.class).setEnabled(viewModeSupported);

        // Change column set
        boolean useDefaultNavigatorColumns = NavigationHandler.getInstance().getView(navigationView).isUseDefaultNavigatorColumnSet();
        AbstractColumnSet columnSet = null;
        if (useDefaultNavigatorColumns) {
            columnSet = navigatorColumnSet;
        } else {
            columnSet = NavigationHandler.getInstance().getView(navigationView).getCustomColumnSet();
        }

        ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).setColumnSet(columnSet);
        ((NavigationTableColumnModel) navigationTablePanel.getNavigationTable().getColumnModel()).setColumnSet(columnSet);

        NavigationHandler.getInstance().refreshCurrentView();

        // Allow arrange columns if view uses default column set
        columnSetPopupMenu.enableArrangeColumns(useDefaultNavigatorColumns);

        JTree tree = NavigationHandler.getInstance().getCurrentView().getTree();

        if (tree.getSelectionPath() != null) {
            ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).setSongs(getAudioObjectsForTreeNode(navigationView, (DefaultMutableTreeNode) (tree
                    .getSelectionPath().getLastPathComponent())));
        }
    }

    /**
     * Sets the navigation view and saves state
     * @param view
     */
    public void setNavigationView(String view) {
    	setNavigationView(view, true);
    }
    
    /**
     * Updates table contents when user selects a tree node or the table filter
     * changes
     * 
     * @param tree
     *            the tree
     */
    public void updateTableContent(JTree tree) {
        // If navigation table is not shown then don't update it
        if (!state.isShowNavigationTable()) {
            return;
        }

        // Avoid events when changes on a tree different than the one which is visible
        if (tree != NavigationHandler.getInstance().getCurrentView().getTree()) {
            return;
        }

        TreePath[] paths = tree.getSelectionPaths();

        if (paths != null) {
            List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
            for (TreePath element : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) (element.getLastPathComponent());
                audioObjects.addAll(getAudioObjectsForTreeNode(NavigationHandler.getInstance().getViewByName(state.getNavigationView()), node));
            }

            // Filter objects
            audioObjects = filterNavigationTable(audioObjects);

            ((NavigationTableModel) navigationTablePanel.getNavigationTable().getModel()).setSongs(audioObjects);
        }
    }

    /**
     * Returns a filtered list of audio objects using the current table filter
     * 
     * @param audioObjects
     * @return
     */
    private List<IAudioObject> filterNavigationTable(List<IAudioObject> audioObjects) {
        if (!FilterHandler.getInstance().isFilterSelected(NavigationHandler.getInstance().getTableFilter())) {
            return audioObjects;
        }

        if (NavigationHandler.getInstance().getCurrentView().isUseDefaultNavigatorColumnSet()) {
            // Use column set filtering
            return navigatorColumnSet.filterAudioObjects(audioObjects, FilterHandler.getInstance().getFilter());
        } else {
            // Use custom filter
            return NavigationHandler.getInstance().getCurrentView().getCustomColumnSet().filterAudioObjects(audioObjects, FilterHandler.getInstance().getFilter());
        }
    }

    /**
     * Open search dialog.
     * 
     * @param dialog
     *            the dialog
     * @param setAsDefaultVisible
     *            the set as default visible
     * 
     * @return the search
     */
    public Search openSearchDialog(SearchDialog dialog, boolean setAsDefaultVisible) {
        dialog.setSetAsDefaultVisible(setAsDefaultVisible);
        dialog.setVisible(true);
        return dialog.getResult();
    }

    /**
     * @return the popupMenuCaller
     */
    public JComponent getPopupMenuCaller() {
        return popupMenuCaller;
    }

    /**
     * @param popupMenuCaller
     *            the popupMenuCaller to set
     */
    public void setPopupMenuCaller(JComponent popupMenuCaller) {
        this.popupMenuCaller = popupMenuCaller;
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        notifyReload();
    }
}
