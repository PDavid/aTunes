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
package net.sourceforge.atunes.kernel.controllers.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.model.NavigationTableColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationFilterPanel.NavigationFilterListener;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.internetsearch.Search;
import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.SortType;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;

public class NavigationController extends SimpleController<NavigationPanel> implements AudioFilesRemovedListener {

    public enum ViewMode {

        ARTIST, ALBUM, GENRE, FOLDER
    }

    static Logger logger = new Logger();

    /** The current extended tool tip content. */
    volatile Object currentExtendedToolTipContent;

    /** The album tool tip. */
    private ExtendedToolTip extendedToolTip;

    /** The popupmenu caller. */
    private JComponent popupMenuCaller;

    /** The timer. */
    private Timer timer = new Timer(0, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            getExtendedToolTip().setVisible(true);

            final Object currentObject = currentExtendedToolTipContent;
            SwingWorker<ImageIcon, Void> getAndSetImage = new SwingWorker<ImageIcon, Void>() {
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
                        logger.error(LogCategories.IMAGE, e);
                    } catch (ExecutionException e) {
                        logger.error(LogCategories.IMAGE, e);
                    }
                }
            };
            getAndSetImage.execute();

        }
    });

    /**
     * Instantiates a new navigation controller.
     * 
     * @param panel
     *            the panel
     */
    public NavigationController(NavigationPanel panel) {
        super(panel);
        addBindings();
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    protected void addBindings() {
        getComponentControlled().getNavigationTable().setModel(new NavigationTableModel());
        getComponentControlled().getNavigationTable().setColumnModel(new NavigationTableColumnModel());

        // Add tree selection listeners to all views
        for (NavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            view.getTree().addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    updateTableContent((JTree) e.getSource());
                }
            });
        }

        // Add tree mouse listeners to all views
        // Add tree tool tip listener to all views
        NavigationTreeMouseListener treeMouseListener = new NavigationTreeMouseListener(this);
        NavigationTreeToolTipListener tooltipListener = new NavigationTreeToolTipListener(this);
        for (NavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            view.getTree().addMouseListener(treeMouseListener);
            view.getTree().addMouseListener(tooltipListener);
            view.getTree().addMouseMotionListener(tooltipListener);
            view.getTreeScrollPane().addMouseWheelListener(tooltipListener);
        }

        getComponentControlled().getNavigationTable().addMouseListener(new NavigationTableMouseListener(this, getComponentControlled()));
        getComponentControlled().getTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int view = getComponentControlled().getTabbedPane().getSelectedIndex();
                // Maybe tabbed pane is empty so set navigation only if it contains tabs
                if (view != -1) {
                    if (view != NavigationHandler.getInstance().indexOf(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView()))) {
                        setNavigationView(NavigationHandler.getInstance().getNavigationViews().get(view).getClass().getName());
                    }
                }
            }
        });

        getComponentControlled().getTreeFilterPanel().addListener(new NavigationFilterListener() {
            @Override
            public void filterChanged(String newFilter) {
                if (newFilter == null) {
                    getComponentControlled().getTreeFilterPanel().setVisible(false);
                }
                NavigationHandler.getInstance().refreshCurrentView();
            }
        });

        getComponentControlled().getTableFilterPanel().addListener(new NavigationFilterListener() {
            @Override
            public void filterChanged(String newFilter) {
                if (newFilter == null) {
                    getComponentControlled().getTableFilterPanel().setVisible(false);
                }
                updateTableContent(NavigationHandler.getInstance().getCurrentView().getTree());
            }
        });
    }

    public NavigationPanel getNavigationPanel() {
        return getComponentControlled();
    }

    @Override
    protected void addStateBindings() {
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
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        return extendedToolTip;
    }

    /**
     * Get files of all selected elements in navigator.
     * 
     * @return the files selected in navigator
     */
    public List<AudioFile> getFilesSelectedInNavigator() {
        List<AudioObject> files = new ArrayList<AudioObject>();
        if (getPopupMenuCaller() instanceof JTable) {
            int[] rows = getComponentControlled().getNavigationTable().getSelectedRows();
            files.addAll(((NavigationTableModel) getComponentControlled().getNavigationTable().getModel()).getAudioObjectsAt(rows));
        } else if (getPopupMenuCaller() instanceof JTree) {
            TreePath[] paths = NavigationHandler.getInstance().getCurrentView().getTree().getSelectionPaths();
            for (TreePath path : paths) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (treeNode.getUserObject() instanceof TreeObject) {
                    files.addAll(ControllerProxy.getInstance().getNavigationController().getAudioObjectsForTreeNode(NavigationHandler.getInstance().getCurrentView().getClass(),
                            treeNode));
                }
            }
        }
        return AudioFile.getAudioFiles(files);
    }

    /**
     * Checks if a collection of files have the same parent file.
     * 
     * @param c
     *            collection of files
     * @return if a collection of files have the same parent file
     */
    public boolean sameParentFile(Collection<? extends AudioFile> c) {
        Set<File> set = new HashSet<File>();
        for (AudioFile af : c) {
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
     * Gets the song in navigation table.
     * 
     * @param row
     *            the row
     * 
     * @return the song in navigation table
     */
    public AudioObject getSongInNavigationTable(int row) {
        return ((NavigationTableModel) getComponentControlled().getNavigationTable().getModel()).getSongAt(row);
    }

    public List<AudioObject> getAudioObjectsForTreeNode(Class<? extends NavigationView> navigationViewClass, DefaultMutableTreeNode node) {
        List<AudioObject> audioObjects = NavigationHandler.getInstance().getView(navigationViewClass).getAudioObjectForTreeNode(node, ApplicationState.getInstance().getViewMode(),
                getComponentControlled().getTreeFilterPanel().getFilter());
        if (NavigationHandler.getInstance().getView(navigationViewClass).isAudioObjectsFromNodeNeedSort()) {
            return SortType.sort(audioObjects, ApplicationState.getInstance().getSortType());
        }
        return audioObjects;
    }

    /**
     * Notify device reload.
     */
    public void notifyDeviceReload() {
        NavigationHandler.getInstance().getView(DeviceNavigationView.class).refreshView(ApplicationState.getInstance().getViewMode(),
                getComponentControlled().getTreeFilterPanel().getFilter());
    }

    @Override
    public void notifyReload() {
        NavigationHandler.getInstance().refreshCurrentView();
    }

    /**
     * Refresh table.
     */
    public void refreshTable() {
        ((NavigationTableModel) getComponentControlled().getNavigationTable().getModel()).refresh();
    }

    /**
     * Sets the current album tool tip content.
     * 
     * @param currentAlbumToolTipContent
     *            the new current album tool tip content
     */
    public void setCurrentExtendedToolTipContent(Object currentAlbumToolTipContent) {
        this.currentExtendedToolTipContent = currentAlbumToolTipContent;
        getExtendedToolTip().setSizeToFitImage(currentAlbumToolTipContent instanceof TreeObject && ((TreeObject) currentAlbumToolTipContent).isExtendedToolTipImageSupported());
    }

    /**
     * Sets the navigation view.
     * 
     * @param view
     *            the new navigation view
     */
    public void setNavigationView(String view) {
        getLogger().debug(LogCategories.CONTROLLER, new String[] { view });

        Class<? extends NavigationView> navigationView = NavigationHandler.getInstance().getViewByName(view);
        if (navigationView == null) {
            navigationView = RepositoryNavigationView.class;
        }
        ApplicationState.getInstance().setNavigationView(navigationView.getName());

        int currentView = getComponentControlled().getTabbedPane().getSelectedIndex();
        int newView = NavigationHandler.getInstance().indexOf(navigationView);
        // If current view is equals to the new view then don't change tabbed pane selected index
        // This can happen when this method is called from a stateChanged method of tabbed pane listener when user
        // changes selected tab
        if (currentView != newView && newView < getComponentControlled().getTabbedPane().getTabCount()) {
            getComponentControlled().getTabbedPane().setSelectedIndex(newView);
        }

        boolean viewModeSupported = NavigationHandler.getInstance().getView(navigationView).isViewModeSupported();
        Actions.getAction(ShowAlbumsInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowArtistsInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowFoldersInNavigatorAction.class).setEnabled(viewModeSupported);
        Actions.getAction(ShowGenresInNavigatorAction.class).setEnabled(viewModeSupported);

        // Clear tree filter
        getComponentControlled().getTreeFilterPanel().setFilter(null);
        getComponentControlled().getTreeFilterPanel().setVisible(false);
        NavigationHandler.getInstance().refreshCurrentView();

        // Clear table filter
        getComponentControlled().getTableFilterPanel().setFilter(null);
        getComponentControlled().getTableFilterPanel().setVisible(false);

        JTree tree = NavigationHandler.getInstance().getCurrentView().getTree();

        if (tree.getSelectionPath() != null) {
            ((NavigationTableModel) getComponentControlled().getNavigationTable().getModel()).setSongs(getAudioObjectsForTreeNode(navigationView, (DefaultMutableTreeNode) (tree
                    .getSelectionPath().getLastPathComponent())));
        }
    }

    /**
     * Sort.
     * 
     * @param songs
     *            the songs
     * @param type
     *            the type
     * 
     * @return the list< audio object>
     */
    public List<AudioObject> sort(List<AudioObject> songs, SortType type) {
        return SortType.sort(songs, type);
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
        if (!ApplicationState.getInstance().isShowNavigationTable()) {
            return;
        }

        // Avoid events when changes on a tree different than the one which is visible
        if (tree != NavigationHandler.getInstance().getCurrentView().getTree()) {
            return;
        }

        TreePath[] paths = tree.getSelectionPaths();

        if (paths != null) {
            List<AudioObject> songs = new ArrayList<AudioObject>();
            for (TreePath element : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) (element.getLastPathComponent());
                songs.addAll(getAudioObjectsForTreeNode(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView()), node));
            }

            songs = filterNavigationTable(songs);

            ((NavigationTableModel) getComponentControlled().getNavigationTable().getModel()).setSongs(songs);
        }
    }

    /**
     * Returns a filtered list of audio objects using the current table filter
     * 
     * @param audioObjects
     * @return
     */
    private List<AudioObject> filterNavigationTable(List<AudioObject> audioObjects) {
        if (getComponentControlled().getTableFilterPanel().getFilter() == null) {
            return audioObjects;
        }

        if (!NavigationHandler.getInstance().getCurrentView().isNavigatorTableFilterSupported()) {
            return audioObjects;
        }

        return NavigationHandler.getInstance().getCurrentView().filterNavigatorTable(audioObjects, getComponentControlled().getTableFilterPanel().getFilter());
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
    public void audioFilesRemoved(List<AudioFile> audioFiles) {
        notifyReload();
    }
}
