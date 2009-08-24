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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.repository.HighlightFoldersByIncompleteTags;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.TagAttribute;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class NavigatorPanel extends PreferencesPanel {

    private static final long serialVersionUID = -4315748284461119970L;

    private JCheckBox showFavorites;
    JCheckBox showExtendedToolTip;
    JComboBox extendedToolTipDelay;
    private JCheckBox useSmartTagViewSorting;
    private JCheckBox useArtistNamesSorting;

    /**
     * Check box to set navigator tabs at left (selected) or top (unselected)
     */
    private JCheckBox showNavigatorTabsAtLeft;

    /**
     * Check box to set navigator tabs text (selected) or not (unselected)
     */
    private JCheckBox showNavigatorTabsText;

    /**
     * Check box to highlight folders with incomplete tags (selected) or not
     * (unselected)
     */
    JCheckBox highlightFoldersWithIncompleteBasicTags;

    /**
     * Table to select which tag attributes are used to highlight incomplete tag
     * folders
     */
    JTable highlighTagAttributesTable;

    /**
     * Scroll pane fot tag attributes
     */
    JScrollPane highlightTagAttributesScrollPane;

    /**
     * Table model to select tag attributes
     */
    TagAttributesTableModel tagAttributesTableModel;

    /**
     * The Class TagAttributesTableModel.
     */
    private static class TagAttributesTableModel implements TableModel {

        private static final long serialVersionUID = 5251001708812824836L;

        /** The tag attributes hash map. */
        private Map<TagAttribute, Boolean> tagAttributes;

        /** The listeners. */
        private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

        /**
         * Instantiates a new tag attributes table model
         */
        TagAttributesTableModel() {
            // Nothing to do
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.table.TableModel#addTableModelListener(javax.swing.event
         * .TableModelListener)
         */
        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return 2;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            return "";
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount() {
            if (this.tagAttributes != null) {
                return this.tagAttributes.size();
            }
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return tagAttributes.get(TagAttribute.values()[rowIndex]);
            }
            return LanguageTool.getString(TagAttribute.values()[rowIndex].name());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.table.TableModel#removeTableModelListener(javax.swing
         * .event.TableModelListener)
         */
        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        /**
         * Sets the columns.
         * 
         * @param columns
         *            the new columns
         */
        public void setTagAttributes(Map<TagAttribute, Boolean> attrs) {
            this.tagAttributes = attrs;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
         * int)
         */

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                tagAttributes.put(TagAttribute.values()[rowIndex], (Boolean) aValue);
            }
        }

        /**
         * @return the tagAttributes selected by user
         */
        public List<TagAttribute> getSelectedTagAttributes() {
            List<TagAttribute> result = new ArrayList<TagAttribute>();
            for (TagAttribute attr : tagAttributes.keySet()) {
                if (tagAttributes.get(attr)) {
                    result.add(attr);
                }
            }
            return result;
        }
    }

    /**
     * Instantiates a new navigator panel.
     */

    public NavigatorPanel() {
        super(LanguageTool.getString("NAVIGATOR"));
        showFavorites = new JCheckBox(LanguageTool.getString("SHOW_FAVORITES"));
        showExtendedToolTip = new JCheckBox(LanguageTool.getString("SHOW_EXTENDED_TOOLTIP"));
        final JLabel label = new JLabel(LanguageTool.getString("EXTENDED_TOOLTIP_DELAY"));
        extendedToolTipDelay = new JComboBox(new Integer[] { 1, 2, 3, 4, 5 });
        showExtendedToolTip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                label.setEnabled(showExtendedToolTip.isSelected());
                extendedToolTipDelay.setEnabled(showExtendedToolTip.isSelected());
            }
        });
        useSmartTagViewSorting = new JCheckBox(LanguageTool.getString("USE_SMART_TAG_VIEW_SORTING"));
        useArtistNamesSorting = new JCheckBox(LanguageTool.getString("USE_PERSON_NAMES_ARTIST_TAG_VIEW_SORTING"));
        showNavigatorTabsAtLeft = new JCheckBox(LanguageTool.getString("SHOW_NAVIGATION_TABS_AT_LEFT"));
        showNavigatorTabsText = new JCheckBox(LanguageTool.getString("SHOW_NAVIGATION_TABS_TEXT"));
        highlightFoldersWithIncompleteBasicTags = new JCheckBox(LanguageTool.getString("HIGHLIGHT_INCOMPLETE_TAG_FOLDERS"));
        highlightFoldersWithIncompleteBasicTags.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        highlightTagAttributesScrollPane.setEnabled(highlightFoldersWithIncompleteBasicTags.isSelected());
                        highlighTagAttributesTable.setEnabled(highlightFoldersWithIncompleteBasicTags.isSelected());
                    }
                });
            }
        });

        tagAttributesTableModel = new TagAttributesTableModel();
        tagAttributesTableModel.setTagAttributes(HighlightFoldersByIncompleteTags.getAllTagAttributes());
        highlighTagAttributesTable = new JTable(tagAttributesTableModel);
        highlighTagAttributesTable.setShowGrid(false);
        highlighTagAttributesTable.setTableHeader(null);
        highlighTagAttributesTable.getColumnModel().getColumn(0).setMaxWidth(20);
        highlighTagAttributesTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        highlighTagAttributesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        highlighTagAttributesTable.setDefaultRenderer(String.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 1111298953883261220L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

                GuiUtils.applyComponentOrientation((JLabel) c);

                return c;
            }
        });

        highlightTagAttributesScrollPane = new JScrollPane(highlighTagAttributesTable);
        highlightTagAttributesScrollPane.setMinimumSize(new Dimension(300, 150));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(showFavorites, c);
        c.gridy = 1;
        add(showExtendedToolTip, c);
        c.gridy = 2;
        c.insets = new Insets(0, 10, 0, 0);
        add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        add(extendedToolTipDelay, c);
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        c.gridwidth = 2;
        add(useSmartTagViewSorting, c);
        c.gridy = 4;
        add(useArtistNamesSorting, c);
        c.gridy = 5;
        add(showNavigatorTabsAtLeft, c);
        c.gridy = 6;
        add(showNavigatorTabsText, c);
        c.gridy = 7;
        add(highlightFoldersWithIncompleteBasicTags, c);
        c.gridy = 8;
        c.weighty = 1;
        c.weightx = 0;
        c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.NORTHWEST : GridBagConstraints.NORTHEAST;
        c.insets = new Insets(10, 20, 10, 10);
        add(highlightTagAttributesScrollPane, c);
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        state.setShowFavoritesInNavigator(showFavorites.isSelected());
        state.setShowExtendedTooltip(showExtendedToolTip.isSelected());
        state.setExtendedTooltipDelay((Integer) extendedToolTipDelay.getSelectedItem());
        state.setUseSmartTagViewSorting(useSmartTagViewSorting.isSelected());
        state.setShowNavigatorTabsAtLeft(showNavigatorTabsAtLeft.isSelected());
        state.setShowNavigatorTabsText(showNavigatorTabsText.isSelected());
        state.setHighlightIncompleteTagFolders(highlightFoldersWithIncompleteBasicTags.isSelected());
        state.setHighlightIncompleteTagFoldersAttributes(tagAttributesTableModel.getSelectedTagAttributes());
        state.setUsePersonNamesArtistTagViewSorting(useArtistNamesSorting.isSelected());

        return false;
    }

    /**
     * Sets the album tool tip delay.
     * 
     * @param time
     *            the new album tool tip delay
     */

    public void setAlbumToolTipDelay(int time) {
        extendedToolTipDelay.setSelectedItem(time);
    }

    /**
     * Sets the show album tool tip.
     * 
     * @param show
     *            the new show album tool tip
     */
    public void setShowAlbumToolTip(boolean show) {
        showExtendedToolTip.setSelected(show);
    }

    /**
     * Sets the show favorites.
     * 
     * @param show
     *            the new show favorites
     */
    private void setShowFavorites(boolean show) {
        showFavorites.setSelected(show);
    }

    /**
     * Sets the use smart tag view sorting.
     * 
     * @param use
     *            the new use smart tag view sorting
     */
    private void setUseSmartTagViewSorting(boolean use) {
        useSmartTagViewSorting.setSelected(use);
    }

    /**
     * Sets the use person names artist tag view sorting.
     * 
     * @param use
     *            the new se person names artist tag view sorting
     */
    private void setUsePersonNamesArtistTagViewSorting(boolean use) {
        useArtistNamesSorting.setSelected(use);
    }

    /**
     * Sets property to show navigator tabs at left
     * 
     * @param show
     */
    private void setShowNavigatorTabsAtLeft(boolean show) {
        showNavigatorTabsAtLeft.setSelected(show);
    }

    /**
     * Sets property to show navigator tabs text
     * 
     * @param show
     */
    private void setShowNavigatorTabsText(boolean show) {
        showNavigatorTabsText.setSelected(show);
    }

    /**
     * Sets property to highlight folder with incomplete tags
     * 
     * @param highlightFoldersWithIncompleteBasicTags
     *            the highlightFoldersWithIncompleteBasicTags to set
     */
    private void setHighlightFoldersWithIncompleteBasicTags(boolean highlightFoldersWithIncompleteBasicTags) {
        this.highlightFoldersWithIncompleteBasicTags.setSelected(highlightFoldersWithIncompleteBasicTags);
        highlightTagAttributesScrollPane.setEnabled(highlightFoldersWithIncompleteBasicTags);
        highlighTagAttributesTable.setEnabled(highlightFoldersWithIncompleteBasicTags);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setShowFavorites(state.isShowFavoritesInNavigator());
        setShowAlbumToolTip(state.isShowExtendedTooltip());
        setAlbumToolTipDelay(state.getExtendedTooltipDelay());
        setUseSmartTagViewSorting(state.isUseSmartTagViewSorting());
        setShowNavigatorTabsAtLeft(state.isShowNavigatorTabsAtLeft());
        setShowNavigatorTabsText(state.isShowNavigatorTabsText());
        setHighlightFoldersWithIncompleteBasicTags(state.isHighlightIncompleteTagFolders());
        setUsePersonNamesArtistTagViewSorting(state.isUsePersonNamesArtistTagViewSorting());
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.NAVIGATE);
    }

}
