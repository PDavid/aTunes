/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddFavoriteRadioAction;
import net.sourceforge.atunes.kernel.actions.AddRadioAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RefreshRadioAction;
import net.sourceforge.atunes.kernel.actions.RemoveRadioAction;
import net.sourceforge.atunes.kernel.actions.RenameRadioAction;
import net.sourceforge.atunes.kernel.actions.RenameRadioLabelAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public final class RadioNavigationView extends NavigationView {

    /** The radio tree. */
    private JTree radioTree;

    /** The radio tree menu. */
    private JPopupMenu radioTreeMenu;

    /** The radio table menu. */
    private JPopupMenu radioTableMenu;

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.RADIO_LITTLE);
    }

    @Override
    public String getTitle() {
        return LanguageTool.getString("RADIO");
    }

    @Override
    public String getTooltip() {
        return LanguageTool.getString("RADIO_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (radioTree == null) {
            radioTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("RADIO"))));
            radioTree.setToggleClickCount(0);
            radioTree.setCellRenderer(getTreeRenderer());
            radioTree.setToolTipText(LanguageTool.getString("RADIO_VIEW_TOOLTIP"));
        }
        return radioTree;
    }

    @Override
    public boolean isAudioObjectsFromNodeNeedSort() {
        return false;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (radioTreeMenu == null) {
            radioTreeMenu = new JPopupMenu();
            radioTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            radioTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            radioTreeMenu.add(new JSeparator());
            radioTreeMenu.add(new JMenuItem(Actions.getAction(AddRadioAction.class)));
            radioTreeMenu.add(new JMenuItem(Actions.getAction(RefreshRadioAction.class)));
            radioTreeMenu.add(getMenuItemForAction(AddFavoriteRadioAction.class));
            radioTreeMenu.add(new JMenuItem(Actions.getAction(RenameRadioAction.class)));
            radioTreeMenu.add(new JMenuItem(Actions.getAction(RenameRadioLabelAction.class)));
            radioTreeMenu.add(getMenuItemForAction(RemoveRadioAction.class));
        }
        return radioTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (radioTableMenu == null) {
            radioTableMenu = new JPopupMenu();
            radioTableMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            radioTableMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            radioTableMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            radioTableMenu.add(new JMenuItem(Actions.getAction(PlayNowAction.class)));
            radioTableMenu.add(new JSeparator());
            radioTableMenu.add(new JMenuItem(Actions.getAction(ShowNavigatorTableItemInfoAction.class)));
            radioTableMenu.add(new JSeparator());
            radioTableMenu.add(getMenuItemForAction(AddFavoriteRadioAction.class));
            radioTableMenu.add(getMenuItemForAction(RemoveRadioAction.class));
        }
        return radioTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("SHOW_ALL_STATIONS", ApplicationState.getInstance().isShowAllRadioStations());
        data.put("RADIOS", RadioHandler.getInstance().getRadios());
        data.put("PRESET_RADIOS", RadioHandler.getInstance().getRadioPresets());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing " + this.getClass().getName());

        Map<String, ?> data = getViewData(viewMode);

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<TreeObject> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<TreeObject> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        root.setUserObject(LanguageTool.getString("RADIO"));
        root.removeAllChildren();
        addRadioNodes((List<Radio>) data.get("RADIOS"), (List<Radio>) data.get("PRESET_RADIOS"), root, treeFilter, (Boolean) data.get("SHOW_ALL_STATIONS"), objectsExpanded,
                objectsSelected, nodesToExpand, nodesToSelect);

        // Reload the tree to refresh content
        treeModel.reload();

        // Expand nodes
        expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(getTree(), nodesToSelect);
    }

    @Override
    public List<AudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                List<Radio> radios = RadioHandler.getInstance().getRadios();
                for (Radio r : radios) {
                    songs.add(r);
                }
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    Radio obj = (Radio) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.add(obj);
                }
            }
        } else {
            // A node in radio view can be a label or a radio
            Object obj = node.getUserObject();
            if (obj instanceof Radio) {
                Radio r = (Radio) node.getUserObject();
                songs.add(r);
            } else {
                // labels
                for (int i = 0; i < node.getChildCount(); i++) {
                    Radio r = (Radio) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
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
    private static void addRadioNodes(List<Radio> radios, List<Radio> presetRadios, DefaultMutableTreeNode root, String currentFilter, boolean showAllStations, List<TreeObject> objectsExpanded, List<TreeObject> objectsSelected, List<DefaultMutableTreeNode> nodesToExpand, List<DefaultMutableTreeNode> nodesToSelect) {
        if (radios == null) {
            return;
        }

        // Group radios by label
        Map<String, DefaultMutableTreeNode> radioGroups = new HashMap<String, DefaultMutableTreeNode>();
        Map<String, DefaultMutableTreeNode> radioGroupNoLabel = new HashMap<String, DefaultMutableTreeNode>();

        for (Radio r : radios) {
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
                    if (!presetRadios.isEmpty()) {
                        presetRadios.remove(r);
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

        if (presetRadios != null && showAllStations) {
            for (Radio r : presetRadios) {
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

                    // Insert radio node into label node
                    if (radioGroups.containsKey(r.getLabel()) && !r.isRemoved()) {
                        radioGroups.get(r.getLabel()).add(radioNode);
                    }
                    // Marked as removed, so remove for next start
                    else if (r.isRemoved()) {
                        RadioHandler.getInstance().removeRadio(r);
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

        // Sort and add labels 
        List<String> RadioLabels = RadioHandler.getInstance().sortRadioLabels();
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

    @Override
    public Class<?> getNavigatorTableColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Property.class;
        case 1:
            return String.class;
        default:
            return Long.class;
        }
    }

    @Override
    public int getNavigatorTableColumnCount() {
        return 2;
    }

    @Override
    public String getNavigatorTableColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "";
        case 1:
            return LanguageTool.getString("URL");
        default:
            return "";
        }
    }

    @Override
    public Object getNavigatorTableValueAt(AudioObject audioObject, int columnIndex) {
        if (audioObject instanceof Radio) {
            if (columnIndex == 0) {
                return Property.NO_PROPERTIES;
            } else if (columnIndex == 1) {
                return audioObject.getUrl();
            }
        }
        return "";
    }

    @Override
    public int getNavigatorTableMaxWidthForColumn(int columnIndex) {
        if (columnIndex == 0) {
            return 20;
        } else if (columnIndex == 2) {
            return 50;
        }
        return -1;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }

    @Override
    public List<AudioObject> filterNavigatorTable(List<AudioObject> audioObjects, String filter) {
        return null;
    }

    @Override
    public boolean isNavigatorTableFilterSupported() {
        return false;
    }

    @Override
    protected TreeCellRenderer getTreeRenderer() {
        return new SubstanceDefaultTreeCellRenderer() {
            private static final long serialVersionUID = 8184884292645176037L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                if (leaf || row == 0) {
                    icon.setIcon(ImageLoader.getImage(ImageLoader.RADIO_LITTLE));
                } else {
                    icon.setIcon(ImageLoader.getImage(ImageLoader.FOLDER));
                }
                return icon;
            }
        };
    }
}
