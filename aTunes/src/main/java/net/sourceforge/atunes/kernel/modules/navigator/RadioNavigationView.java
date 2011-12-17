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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AddFavoriteRadioAction;
import net.sourceforge.atunes.kernel.actions.AddRadioAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.EditRadioAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RefreshRadioAction;
import net.sourceforge.atunes.kernel.actions.RemoveRadioAction;
import net.sourceforge.atunes.kernel.actions.RenameRadioLabelAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public final class RadioNavigationView extends AbstractNavigationView {

    /** The radio tree. */
    private JTree radioTree;

    /** The radio tree menu. */
    private JPopupMenu radioTreeMenu;

    /** The radio table menu. */
    private JPopupMenu radioTableMenu;

    /** The column set */
    private IColumnSet radioNavigationColumnSet;

    private IRadioHandler radioHandler;
    
    private IIconFactory radioSmallIcon;
    
    /**
     * @param radioSmallIcon
     */
    public void setRadioSmallIcon(IIconFactory radioSmallIcon) {
		this.radioSmallIcon = radioSmallIcon;
	}
    
    /**
     * @param radioNavigationColumnSet
     */
    public void setRadioNavigationColumnSet(IColumnSet radioNavigationColumnSet) {
		this.radioNavigationColumnSet = radioNavigationColumnSet;
	}
    
    @Override
    public IColorMutableImageIcon getIcon() {
    	return radioSmallIcon.getColorMutableIcon();
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("RADIO");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("RADIO_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (radioTree == null) {
            radioTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("RADIO"))));
            radioTree.setToggleClickCount(0);
            radioTree.setCellRenderer(getTreeRenderer());
            radioTree.setToolTipText(I18nUtils.getString("RADIO_VIEW_TOOLTIP"));
        }
        return radioTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (radioTreeMenu == null) {
            radioTreeMenu = new JPopupMenu();
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromRadioNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            radioTreeMenu.add(addToPlayListAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromRadioNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            radioTreeMenu.add(setAsPlayListAction);
            
            radioTreeMenu.add(new JSeparator());
            radioTreeMenu.add(Context.getBean(AddRadioAction.class));
            radioTreeMenu.add(Context.getBean(RefreshRadioAction.class));
            AbstractActionOverSelectedObjects<IRadio> addFavoriteRadioAction = Context.getBean(AddFavoriteRadioAction.class);
            addFavoriteRadioAction.setAudioObjectsSource(this);
            radioTreeMenu.add(addFavoriteRadioAction);
            
            EditRadioAction editRadioAction = Context.getBean(EditRadioAction.class);
            editRadioAction.setAudioObjectsSource(this);
            radioTreeMenu.add(editRadioAction);
            
            radioTreeMenu.add(Context.getBean(RenameRadioLabelAction.class));
            
            RemoveRadioAction removeRadioAction = Context.getBean(RemoveRadioAction.class);
            removeRadioAction.setAudioObjectsSource(this);
            radioTreeMenu.add(removeRadioAction);
        }
        return radioTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (radioTableMenu == null) {
            radioTableMenu = new JPopupMenu();
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromRadioNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            radioTableMenu.add(addToPlayListAction);
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = Context.getBean("addToPlayListAfterCurrentAudioObjectFromRadioNavigationView", AddToPlayListAfterCurrentAudioObjectAction.class);
            addToPlayListAfterCurrentAudioObjectAction.setAudioObjectsSource(this);
            radioTableMenu.add(addToPlayListAfterCurrentAudioObjectAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromRadioNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            radioTableMenu.add(setAsPlayListAction);
            
            radioTableMenu.add(Context.getBean(PlayNowAction.class));
            radioTableMenu.add(new JSeparator());
            radioTableMenu.add(new JMenuItem(Context.getBean(ShowNavigatorTableItemInfoAction.class)));
            radioTableMenu.add(new JSeparator());
            AbstractActionOverSelectedObjects<IRadio> addFavoriteRadioAction = Context.getBean(AddFavoriteRadioAction.class);
            addFavoriteRadioAction.setAudioObjectsSource(this);
            radioTableMenu.add(addFavoriteRadioAction);
            
            EditRadioAction editRadioAction = Context.getBean(EditRadioAction.class);
            editRadioAction.setAudioObjectsSource(this);
            radioTableMenu.add(editRadioAction);
            
            RemoveRadioAction removeRadioAction = Context.getBean(RemoveRadioAction.class);
            removeRadioAction.setAudioObjectsSource(this);
            radioTableMenu.add(removeRadioAction);
        }
        return radioTableMenu;
    }
    
    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("SHOW_ALL_STATIONS", getState().isShowAllRadioStations());
        data.put("RADIOS", radioHandler.getRadios());
        data.put("PRESET_RADIOS", radioHandler.getRadioPresets());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        Map<String, ?> data = getViewData(viewMode);

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        root.setUserObject(I18nUtils.getString("RADIO"));
        root.removeAllChildren();
        addRadioNodes((List<IRadio>) data.get("RADIOS"), (List<IRadio>) data.get("PRESET_RADIOS"), root, treeFilter, (Boolean) data.get("SHOW_ALL_STATIONS"), objectsExpanded,
                objectsSelected, nodesToExpand, nodesToSelect);

        // Reload the tree to refresh content
        treeModel.reload();

        // Expand nodes
        NavigationViewHelper.expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        NavigationViewHelper.selectNodes(getTree(), nodesToSelect);
    }

    @Override
    public List<IAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<IAudioObject> songs = new ArrayList<IAudioObject>();
        if (node.isRoot()) {
            // Add all radios in child nodes
            for (int i = 0; i < node.getChildCount(); i++) {
                songs.addAll(getAudioObjectForTreeNode((DefaultMutableTreeNode) node.getChildAt(i), viewMode, treeFilter));
            }
        } else {
            // A node in radio view can be a label or a radio
            Object obj = node.getUserObject();
            if (obj instanceof IRadio) {
                IRadio r = (IRadio) node.getUserObject();
                songs.add(r);
            } else {
                // labels
                for (int i = 0; i < node.getChildCount(); i++) {
                    IRadio r = (IRadio) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.add(r);
                }
            }
        }
        return songs;
    }

    /**
     * Adds the radio nodes.
     * 
     * @param radios
     *            the radios
     * @param presetRadios
     *            the preset radios
     * @param root
     *            the root
     * @param currentFilter
     *            the current filter
     * @param showAllStations
     *            the show all stations
     */
    private void addRadioNodes(List<IRadio> radios, List<IRadio> presetRadios, DefaultMutableTreeNode root, String currentFilter, boolean showAllStations, List<ITreeObject<? extends IAudioObject>> objectsExpanded, List<ITreeObject<? extends IAudioObject>> objectsSelected, List<DefaultMutableTreeNode> nodesToExpand, List<DefaultMutableTreeNode> nodesToSelect) {
        if (radios == null) {
            return;
        }

        // Group radios by label
        Map<String, DefaultMutableTreeNode> radioGroups = new HashMap<String, DefaultMutableTreeNode>();
        Map<String, DefaultMutableTreeNode> radioGroupNoLabel = new HashMap<String, DefaultMutableTreeNode>();

        // Add radios
        addRadioNodes(radios, presetRadios, currentFilter, objectsExpanded, objectsSelected, nodesToExpand, nodesToSelect, radioGroups, radioGroupNoLabel);
        
        // Add presets
        if (showAllStations) {
        	addRadioNodes(presetRadios, null, currentFilter, objectsExpanded, objectsSelected, nodesToExpand, nodesToSelect, radioGroups, radioGroupNoLabel);
        }
        
        // Sort and add labels 
        List<String> RadioLabels = radioHandler.sortRadioLabels();
        for (String label : RadioLabels) {
            for (DefaultMutableTreeNode labelNode : radioGroups.values()) {
                if (labelNode.toString().equals(label)) {
                    root.add(labelNode);
                    break;
                }
            }
        }

        // Add radio nodes without labels
        for (DefaultMutableTreeNode radioNode : radioGroupNoLabel.values()) {
            root.add(radioNode);
        }
    }

    private void addRadioNodes(List<IRadio> radios,
    						   List<IRadio> presetRadios,
    						   String currentFilter, 
    						   List<ITreeObject<? extends IAudioObject>> objectsExpanded, 
    						   List<ITreeObject<? extends IAudioObject>> objectsSelected, 
    						   List<DefaultMutableTreeNode> nodesToExpand, 
    						   List<DefaultMutableTreeNode> nodesToSelect, 
    						   Map<String, DefaultMutableTreeNode> radioGroups, 
    						   Map<String, DefaultMutableTreeNode> radioGroupNoLabel) {
    	
        for (IRadio r : radios) {
            if (currentFilter == null || r.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                // Create radio node
                DefaultMutableTreeNode radioNode = new DefaultMutableTreeNode(r);
                // If node was selected before refreshing...
                if (objectsSelected.contains(radioNode.getUserObject())) {
                    nodesToSelect.add(radioNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(radioNode.getUserObject())) {
                    nodesToExpand.add(radioNode);
                }

                // Insert radio node into label node; "REMOVED" tag means we should not display this station
                if (radioGroups.containsKey(r.getLabel()) && !r.isRemoved()) {
                    radioGroups.get(r.getLabel()).add(radioNode);
                } else if (r.isRemoved()) {
                	if (presetRadios != null) {
                		if (!presetRadios.isEmpty()) {
                			presetRadios.remove(r);
                		}
                	} else {
                		radioHandler.removeRadio(r);
                	}
                } else {
                    DefaultMutableTreeNode labelNode;
                    if (r.getLabel() == null || r.getLabel().trim().equals("")) {
                        //labelNode = new DefaultMutableTreeNode(LanguageTool.getString("UNLABELED"));
                        radioGroupNoLabel.put(r.getName(), radioNode);
                    } else {
                        if (!r.isRemoved()) {
                            labelNode = new DefaultMutableTreeNode(r.getLabel());
                            labelNode.add(radioNode);
                            // If node was selected before refreshing...
                            if (objectsSelected.contains(labelNode.getUserObject())) {
                                nodesToSelect.add(labelNode);
                            }
                            // If node was expanded before refreshing...
                            if (objectsExpanded.contains(labelNode.getUserObject())) {
                                nodesToExpand.add(labelNode);
                            }
                            radioGroups.put(r.getLabel(), labelNode);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
        return false;
    }

    @Override
    public IColumnSet getCustomColumnSet() {
        return radioNavigationColumnSet;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }
    
    public void setRadioHandler(IRadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}

}
