/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.decorators.RadioTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.StringTreeCellDecorator;
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
import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSet;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

public final class RadioNavigationView extends NavigationView {

    private List<TreeCellDecorator> decorators;

    /** The radio tree. */
    private JTree radioTree;

    /** The radio tree menu. */
    private JPopupMenu radioTreeMenu;

    /** The radio table menu. */
    private JPopupMenu radioTableMenu;

    /** The column set */
    private ColumnSet columnSet;

    @Override
    public ImageIcon getIcon() {
        return Images.getImage(Images.RADIO_LITTLE);
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
        debug("Refreshing ", this.getClass().getName());

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

        root.setUserObject(I18nUtils.getString("RADIO"));
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
            // Add all radios in child nodes
            for (int i = 0; i < node.getChildCount(); i++) {
                songs.addAll(getAudioObjectForTreeNode((DefaultMutableTreeNode) node.getChildAt(i), viewMode, treeFilter));
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
    public boolean isUseDefaultNavigatorColumnSet() {
        return false;
    }

    @Override
    public ColumnSet getCustomColumnSet() {
        if (columnSet == null) {
            columnSet = new RadioNavigationColumnSet(this.getClass().getName());
        }
        return columnSet;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }

    @Override
    protected List<TreeCellDecorator> getTreeCellDecorators() {
        if (decorators == null) {
            decorators = new ArrayList<TreeCellDecorator>();
            decorators.add(new StringTreeCellDecorator());
            decorators.add(new RadioTreeCellDecorator());
        }
        return decorators;
    }

    private static final class RadioNavigationColumnSet extends CustomNavigatorColumnSet {

        private static final class UrlColumn extends Column {
            /**
			 * 
			 */
            private static final long serialVersionUID = -1615880013918017198L;

            private UrlColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return o1.getUrl().compareTo(o2.getUrl());
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return audioObject.getUrl();
            }
        }

        private static final class NameColumn extends Column {
            /**
			 * 
			 */
            private static final long serialVersionUID = 3613237620716484881L;

            private NameColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return ((Radio) audioObject).getName();
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return ((Radio) o1).getName().compareTo(((Radio) o2).getName());
            }
        }

        private static final class EmptyColumn extends Column {
            /**
			 * 
			 */
            private static final long serialVersionUID = 3613237620716484881L;

            private EmptyColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return 0;
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return Property.NO_PROPERTIES;
            }
        }

        public RadioNavigationColumnSet(String columnSetName) {
            super(columnSetName);
        }

        @Override
        protected List<Column> getAllowedColumns() {
            List<Column> columns = new ArrayList<Column>();

            Column property = new EmptyColumn("", Property.class);
            property.setVisible(true);
            property.setWidth(20);
            property.setResizable(false);
            columns.add(property);

            Column name = new NameColumn("NAME", String.class);
            name.setVisible(true);
            name.setWidth(150);
            name.setUsedForFilter(true);
            columns.add(name);

            Column url = new UrlColumn("URL", String.class);
            url.setVisible(true);
            url.setWidth(400);
            url.setUsedForFilter(true);
            columns.add(url);

            return columns;
        }

    }

}
