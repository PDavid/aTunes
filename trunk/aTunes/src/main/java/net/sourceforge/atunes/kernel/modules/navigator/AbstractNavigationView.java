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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.kernel.actions.ActionWithColorMutableIcon;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITreeGeneratorFactory;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.Logger;

/**
 * Common code for navigation views
 * @author alex
 *
 */
public abstract class AbstractNavigationView implements INavigationView {

	/**
     * Scroll pane used for tree
     */
    private JScrollPane scrollPane;
    
    /**
     * Action associated to show this navigation view
     */
    private ActionWithColorMutableIcon action;
    
    private INavigationHandler navigationHandler;
    
    /**
     * Decorators used in view
     */
    private List<AbstractTreeCellDecorator<?, ?>> decorators;
    
    private ITreeGeneratorFactory treeGeneratorFactory;

	private ILookAndFeelManager lookAndFeelManager;
	
	private ITable navigationTable;
	
	private IStateNavigation stateNavigation;
	
	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}
	
	/**
	 * @param navigationTable
	 */
	public void setNavigationTable(ITable navigationTable) {
		this.navigationTable = navigationTable;
	}
	
    @Override
	public abstract String getTitle();

    @Override
	public abstract IColorMutableImageIcon getIcon();

    @Override
	public abstract String getTooltip();

    @Override
	public abstract JTree getTree();
    
    /**
     * Return decorators to be used in view
     * 
     * @return
     */
    protected final List<AbstractTreeCellDecorator<?, ?>> getTreeCellDecorators() {
    	return decorators;
    }
    
	/**
	 * @param decorators
	 */
	public void setDecorators(List<AbstractTreeCellDecorator<?, ?>> decorators) {
		this.decorators = decorators;
	}

    @Override
	public abstract JPopupMenu getTreePopupMenu();

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param treeGeneratorFactory
     */
    public void setTreeGeneratorFactory(ITreeGeneratorFactory treeGeneratorFactory) {
		this.treeGeneratorFactory = treeGeneratorFactory;
	}
    
    @Override
	public ITreeGeneratorFactory getTreeGeneratorFactory() {
		return treeGeneratorFactory;
	}
    
    @Override
	public JPopupMenu getTablePopupMenu() {
        // By default table popup is the same of tree
        return getTreePopupMenu();
    }

    @Override
	public final JScrollPane getTreeScrollPane() {
        if (scrollPane == null) {
            scrollPane = lookAndFeelManager.getCurrentLookAndFeel().getTreeScrollPane(getTree());
        }
        return scrollPane;
    }

    /**
     * Returns the data to be shown in the view. It depends on the view mode
     * 
     * @param viewMode
     * @return
     */
    protected abstract Map<String, ?> getViewData(ViewMode viewMode);

    @Override
	public void refreshView(ViewMode viewMode, String treeFilter) {
        // Get selected rows before refresh
        List<IAudioObject> selectedObjects = ((NavigationTableModel) navigationTable.getModel()).getAudioObjectsAt(navigationTable.getSelectedRows());

        // Call to refresh tree
        refreshTree(viewMode, treeFilter);

        // Set the same selected audio objects as before refreshing
        for (IAudioObject audioObject : selectedObjects) {
            int indexOfAudioObject = ((NavigationTableModel) navigationTable.getModel()).getAudioObjects().indexOf(audioObject);
            if (indexOfAudioObject != -1) {
            	navigationTable.addRowSelectionInterval(indexOfAudioObject, indexOfAudioObject);
            }
        }
    }

    /**
     * Refresh tree view
     * 
     * @param viewMode
     * @param treeFilter
     */
    protected abstract void refreshTree(ViewMode viewMode, String treeFilter);

    @Override
	public abstract List<? extends IAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter);

    @Override
	public abstract boolean isViewModeSupported();

    @Override
	public abstract boolean isUseDefaultNavigatorColumnSet();

    @Override
	public abstract IColumnSet getCustomColumnSet();

    @Override
	public final void updateTreePopupMenuWithTreeSelection(MouseEvent e) {
        List<DefaultMutableTreeNode> nodesSelected = new ArrayList<DefaultMutableTreeNode>();
        TreePath[] paths = getTree().getSelectionPaths();
        if (paths != null && paths.length > 0) {
            for (TreePath path : paths) {
                nodesSelected.add((DefaultMutableTreeNode) path.getLastPathComponent());
            }
        }
        updateTreePopupMenuItems(getTreePopupMenu(), getTree().isRowSelected(0), nodesSelected);
    }

    /**
     * Updates all actions of tree popup
     * 
     * @param menu
     * @param rootSelected
     * @param selection
     */
    private void updateTreePopupMenuItems(JPopupMenu menu, boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        for (Component c : menu.getComponents()) {
            Action a = null;
            if (c instanceof JMenuItem) {
                a = ((JMenuItem) c).getAction();
            }

            if (c instanceof JMenu) {
                updateTreeMenuItems((JMenu) c, rootSelected, selection);
            }

            if (a instanceof net.sourceforge.atunes.kernel.actions.CustomAbstractAction) {
                boolean enabled = ((net.sourceforge.atunes.kernel.actions.CustomAbstractAction) a).isEnabledForNavigationTreeSelection(rootSelected, selection);
                a.setEnabled(enabled);
            }
        }
    }

    /**
     * Updates all actions of tree menu
     * 
     * @param menu
     * @param rootSelected
     * @param selection
     */
    private void updateTreeMenuItems(JMenu menu, boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            // For some reason getItem can return null
            if (menuItem != null) {
                Action a = menuItem.getAction();

                if (menuItem instanceof JMenu) {
                    updateTreeMenuItems((JMenu) menuItem, rootSelected, selection);
                }

                if (a instanceof net.sourceforge.atunes.kernel.actions.CustomAbstractAction) {
                    boolean enabled = ((net.sourceforge.atunes.kernel.actions.CustomAbstractAction) a).isEnabledForNavigationTreeSelection(rootSelected, selection);
                    a.setEnabled(enabled);
                }
            }
        }
    }

    @Override
	public final void updateTablePopupMenuWithTableSelection(ITable table, MouseEvent e) {
        updateTablePopupMenuItems(getTablePopupMenu(), ((NavigationTableModel) navigationTable.getModel()).getAudioObjectsAt(table.getSelectedRows()));
    }

    /**
     * Updates all actions of table popup
     * 
     * @param menu
     * @param selection
     */
    private void updateTablePopupMenuItems(JPopupMenu menu, List<IAudioObject> selection) {
        for (Component c : menu.getComponents()) {
            Action a = null;
            if (c instanceof JMenuItem) {
                a = ((JMenuItem) c).getAction();
            }

            if (c instanceof JMenu) {
                updateTableMenuItems((JMenu) c, selection);
            }

            if (a instanceof net.sourceforge.atunes.kernel.actions.CustomAbstractAction) {
                boolean enabled = ((net.sourceforge.atunes.kernel.actions.CustomAbstractAction) a).isEnabledForNavigationTableSelection(selection);
                a.setEnabled(enabled);
            }
        }
    }

    /**
     * Updates all actions of table menu
     * 
     * @param menu
     * @param selection
     */
    private void updateTableMenuItems(JMenu menu, List<IAudioObject> selection) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem menuItem = menu.getItem(i);
            // For some reason getItem can return null
            if (menuItem != null) {
                Action a = menuItem.getAction();

                if (menuItem instanceof JMenu) {
                    updateTableMenuItems((JMenu) menuItem, selection);
                }

                if (a instanceof net.sourceforge.atunes.kernel.actions.CustomAbstractAction) {
                    boolean enabled = ((net.sourceforge.atunes.kernel.actions.CustomAbstractAction) a).isEnabledForNavigationTableSelection(selection);
                    a.setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Method to log debug messages
     * 
     * @param objects
     */
    protected final void debug(Object... objects) {
        Logger.debug(objects);
    }

    @Override
	public ViewMode getCurrentViewMode() {
        return stateNavigation.getViewMode();
    }

    /**
     * Return selected objects in this navigation view
     * 
     * @return
     */
    public List<IAudioObject> getSelectedAudioObjects() {
        List<IAudioObject> selectedInTable = ((NavigationTableModel) navigationTable.getModel()).getAudioObjectsAt(navigationTable.getSelectedRows());
        if (selectedInTable.isEmpty()) {
            TreePath[] paths = getTree().getSelectionPaths();
            List<IAudioObject> audioObjectsSelected = new ArrayList<IAudioObject>();
            if (paths != null) {
                for (TreePath path : paths) {
                	audioObjectsSelected.addAll(navigationHandler.getAudioObjectsForTreeNode(this.getClass(), (DefaultMutableTreeNode) path.getLastPathComponent()));
                }
            }
            return audioObjectsSelected;
        }
        return selectedInTable;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<ITreeObject<? extends IAudioObject>> getSelectedTreeObjects() {
        TreePath[] paths = getTree().getSelectionPaths();
        List<ITreeObject<? extends IAudioObject>> treeObjectsSelected = new ArrayList<ITreeObject<? extends IAudioObject>>();
        if (paths != null) {
            for (TreePath path : paths) {
            	treeObjectsSelected.add((ITreeObject<? extends IAudioObject>)((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject());
            }
        }
        return treeObjectsSelected;
    }

    /**
     * Returns all TreeObject instances selected in a tree
     * 
     * @param tree
     * @return
     */
    @SuppressWarnings("unchecked")
	protected static final List<ITreeObject<? extends IAudioObject>> getTreeObjectsSelected(JTree tree) {
        // Get objects selected before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsSelected = new ArrayList<ITreeObject<? extends IAudioObject>>();
        TreePath[] pathsSelected = tree.getSelectionPaths();

        // If any node was selected
        if (pathsSelected != null) {
            for (TreePath pathSelected : pathsSelected) {
                Object obj = ((DefaultMutableTreeNode) pathSelected.getLastPathComponent()).getUserObject();
                if (obj instanceof ITreeObject) {
                    objectsSelected.add((ITreeObject<? extends IAudioObject>) obj);
                }
            }
        }

        return objectsSelected;
    }

    /**
     * Returns all TreeObject instances expanded in a tree
     * 
     * @param tree
     * @param root
     * @return
     */
    @SuppressWarnings("unchecked")
	protected static final List<ITreeObject<? extends IAudioObject>> getTreeObjectsExpanded(JTree tree, DefaultMutableTreeNode root) {
        // Get objects expanded before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsExpanded = new ArrayList<ITreeObject<? extends IAudioObject>>();
        Enumeration<TreePath> enume = tree.getExpandedDescendants(new TreePath(root.getPath()));

        // If any node was expanded
        if (enume != null) {
            while (enume.hasMoreElements()) {
                TreePath p = enume.nextElement();
                Object obj = ((DefaultMutableTreeNode) p.getLastPathComponent()).getUserObject();
                if (obj instanceof ITreeObject) {
                    objectsExpanded.add((ITreeObject<? extends IAudioObject>) obj);
                }
            }
        }

        return objectsExpanded;
    }

    @Override
	public final ActionWithColorMutableIcon getActionToShowView() {
    	if (action == null) {
    		action = new ActionWithColorMutableIcon(getTitle()) {

    			private static final long serialVersionUID = 2895222205333520899L;

    			protected void executeAction() {
    				navigationHandler.setNavigationView(AbstractNavigationView.this.getClass().getName());
    			}
    			
    			@Override
    			public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
    				return new IColorMutableImageIcon() {
						
						@Override
						public ImageIcon getIcon(Color paint) {
							return AbstractNavigationView.this.getIcon().getIcon(paint);
						}
					};
    			}
    		};

    		action.putValue(Action.SHORT_DESCRIPTION, getTitle());
    	}
    	return action;
    }

    /**
     * Returns tree renderer used
     * 
     * @return
     */
    protected final TreeCellRenderer getTreeRenderer() {
        return lookAndFeelManager.getCurrentLookAndFeel().getTreeCellRenderer(new NavigationViewTreeCellRendererCode(getTreeCellDecorators()));
    }

    @Override
    public String toString() {
    	return getTitle();
    }

	@Override
	public void selectAudioObject(ViewMode currentViewMode, IAudioObject audioObject) {
		
	}

	@Override
	public void selectArtist(ViewMode currentViewMode, String artist) {
	}	
	
	/**
	 * @return look and feel manager
	 */
	protected ILookAndFeelManager getLookAndFeelManager() {
		return lookAndFeelManager;
	}
}
