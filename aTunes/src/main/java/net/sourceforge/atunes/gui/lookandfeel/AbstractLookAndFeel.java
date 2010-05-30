/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public abstract class AbstractLookAndFeel {

    private static final class LookAndFeelListCellRenderer extends DefaultListCellRenderer {
        private final AbstractListCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 2572603555660744197L;

        private LookAndFeelListCellRenderer(AbstractListCellRendererCode code) {
            this.code = code;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return code.getComponent(c, list, value, index, isSelected, cellHasFocus);
        }
    }

    private static final class LookAndFeelTableHeaderCellRenderer extends DefaultTableCellRenderer {
        private final AbstractTableCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private LookAndFeelTableHeaderCellRenderer(AbstractTableCellRendererCode code) {
            this.code = code;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        }
    }

    private static final class LookAndFeelTableCellRenderer extends DefaultTableCellRenderer {
        private final AbstractTableCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private LookAndFeelTableCellRenderer(AbstractTableCellRendererCode code) {
            this.code = code;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        }
    }

    private static final class LookAndFeelTreeCellRenderer extends DefaultTreeCellRenderer {
        private final AbstractTreeCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 5424315832943108932L;

        private LookAndFeelTreeCellRenderer(AbstractTreeCellRendererCode code) {
            this.code = code;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            return code.getComponent(c, tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    /**
     * Returns name of the look and feel
     */
    public abstract String getName();

    /**
     * Returns description to show to user
     */
    public abstract String getDescription();

    /**
     * Steps needed to initialize look and feel
     */
    public abstract void initializeLookAndFeel();

    /**
     * Steps needed to set up look and feel
     * 
     * @param skin
     */
    public abstract void setLookAndFeel(String skin);

    /**
     * Returns default skin (if supported)
     * 
     * @return
     */
    public abstract String getDefaultSkin();

    /**
     * Returns list of available skins for this look and feel (if supported)
     * 
     * @return
     */
    public abstract List<String> getSkins();

    /**
     * Returns if dialogs must be undecorated
     * 
     * @return
     */
    public boolean isDialogUndecorated() {
        return false;
    }

    /**
     * Returns a new TreeCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    public TreeCellRenderer getTreeCellRenderer(final AbstractTreeCellRendererCode code) {
        return new LookAndFeelTreeCellRenderer(code);
    }

    /**
     * Returns a new TableCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    public TableCellRenderer getTableCellRenderer(final AbstractTableCellRendererCode code) {
        return new LookAndFeelTableCellRenderer(code);
    }

    /**
     * Returns a new TableCellRenderer executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    public TableCellRenderer getTableHeaderCellRenderer(final AbstractTableCellRendererCode code) {
        return new LookAndFeelTableHeaderCellRenderer(code);
    }

    /**
     * Returns a new ListCellRendeder executing given code (default
     * implementation)
     * 
     * @param code
     * @return
     */
    public ListCellRenderer getListCellRenderer(final AbstractListCellRendererCode code) {
        return new LookAndFeelListCellRenderer(code);
    }

    /**
     * Puts client properties in components
     * 
     * @param c
     */
    public void putClientProperties(JComponent c) {
        // No properties by default
    }

    /**
     * Returns if look and feel supports custom player controls (not by default)
     */
    public boolean isCustomPlayerControlsSupported() {
        return false;
    }

    /**
     * Returns shadow border used by look and feel (none by default)
     * 
     * @return
     */
    public Border getShadowBorder() {
        return null;
    }

    /**
     * Initializes needed properties for fonts depending on given base font and
     * look and feel (none by default)
     */
    public void initializeFonts(Font baseFont) {
        // Nothing to do
    }

    /**
     * Returns default font
     * 
     * @return
     */
    public Font getDefaultFont() {
        return UIManager.getFont("Label.font");
    }
    
    /**
     * Returns if look and feel supports tab close buttons (not by default)
     * @return
     */
    public boolean isTabCloseButtonsSupported() {
    	return false;
    }    
    
    /**
     * Adds tab close buttons to tabbed pane. When tabs are closed listener is called
     * @param tabbedPane
     * @param tabCloseListener
     */
    public void addTabCloseButtons(JTabbedPane tabbedPane, TabCloseListener tabCloseListener) {
    	// Does nothing by default
    }
}
