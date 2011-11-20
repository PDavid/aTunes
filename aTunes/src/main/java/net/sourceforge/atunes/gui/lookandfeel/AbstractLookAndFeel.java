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

package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public abstract class AbstractLookAndFeel implements ILookAndFeel {

	/**
	 * Base font
	 */
	protected Font baseFont;
	
	protected IOSManager osManager;
	
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
        public JComponent getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        	JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        	if (code != null) {
        		c = code.getComponent(c, list, value, index, isSelected, cellHasFocus);
        	}
            c.setOpaque(isSelected);
            return c;
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
        public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
        public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        	if (code != null) {
        		c = code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        	}
            c.setOpaque(isSelected);
            return c;
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
        public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        	// Use custom JLabel component (if call super method, returned component can't be fully customized)
        	JComponent c = new JLabel(value.toString());
        	if (code != null) {
        		c = code.getComponent(c, tree, value,isSelected, expanded, leaf, row, hasFocus);
        	}
        	c.setOpaque(isSelected);
        	if (isSelected) {
        		c.setBackground(UIManager.getColor("Tree.selectionBackground"));
        		c.setForeground(UIManager.getColor("Tree.selectionForeground"));
        	}
            return c;
        }
    }

    @Override
	public abstract String getName();

    @Override
	public abstract String getDescription();

    @Override
	public abstract void initializeLookAndFeel();

    @Override
	public abstract void setLookAndFeel(String skin);

    @Override
	public abstract String getDefaultSkin();

    @Override
	public abstract List<String> getSkins();

    @Override
	public boolean isDialogUndecorated() {
        return false;
    }

    @Override
	public TreeCellRenderer getTreeCellRenderer(final AbstractTreeCellRendererCode code) {
        return new LookAndFeelTreeCellRenderer(code);
    }

    @Override
	public TableCellRenderer getTableCellRenderer(final AbstractTableCellRendererCode code) {
        return new LookAndFeelTableCellRenderer(code);
    }

    @Override
	public TableCellRenderer getTableHeaderCellRenderer(final AbstractTableCellRendererCode code) {
        return new LookAndFeelTableHeaderCellRenderer(code);
    }

    @Override
	public ListCellRenderer getListCellRenderer(final AbstractListCellRendererCode code) {
        return new LookAndFeelListCellRenderer(code);
    }

    @Override
	public boolean customComboBoxRenderersSupported() {
    	return true;
    }
    
    @Override
	public void putClientProperties(JComponent c) {
        // No properties by default
    }

    @Override
	public boolean isCustomPlayerControlsSupported() {
        return false;
    }

    @Override
	public Border getShadowBorder() {
        return null;
    }

    @Override
	public void initializeFonts(Font baseFont) {
    	this.baseFont = baseFont;
    	setUIFont(new FontUIResource(baseFont));
    }

    /**
     * Changes all components' font to a given one
     * @param f
     */
    protected void setUIFont (FontUIResource f){
    	Enumeration<Object> keys = UIManager.getDefaults().keys();
    	while (keys.hasMoreElements()) {
    		Object key = keys.nextElement();
    		Object value = UIManager.get (key);
    		if (value instanceof FontUIResource) {
    			UIManager.put (key, f);
    		}
    	}
    }    
    
    @Override
	public boolean allowsSkins() {
    	return getSkins() != null && !getSkins().isEmpty();
    }

	@Override
	public Color getPaintForSpecialControls() {
		return UIManager.getColor("Label.foreground"); 
	}
	
	@Override
	public Color getPaintForDisabledSpecialControls() {
		return UIManager.getColor("Label.foreground"); 
	}	

	@Override
	public Color getPaintForColorMutableIcon(Component c, boolean isSelected) {
		return isSelected ? UIManager.getColor("Tree.selectionForeground") : UIManager.getColor("Label.foreground");	
	}

	@Override
	public Dimension getPopUpButtonSize() {
		return new Dimension(20, 20);
	}

	
	/********************************************************* FONTS *******************************************************/
	
    @Override
	public Font getDefaultFont() {
        return this.baseFont;
    }
    
	@Override
	public abstract boolean supportsCustomFontSettings();
	
    @Override
	public final Font getAboutBigFont() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 8f);
    }

    @Override
	public Font getPlayListFont() {
        return getDefaultFont();
    }

    @Override
	public Font getPlayListSelectedItemFont() {
    	if (getPlayListFont() != null) {
    		return getPlayListFont().deriveFont(Font.BOLD);
    	}
    	return getDefaultFont();
    }

    @Override
	public final Font getContextInformationBigFont() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 8f);
    }

    @Override
	public final Font getPropertiesDialogBigFont() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 4f);
    }

    @Override
	public final Font getOsdLine1Font() {
        return getDefaultFont().deriveFont(Font.BOLD, getDefaultFont().getSize() + 4f);
    }

    @Override
	public final Font getOsdLine2Font() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 2f);
    }

    @Override
	public final Font getOsdLine3Font() {
        return getOsdLine2Font();
    }

    @Override
	public final Font getFullScreenLine1Font() {
        return  getDefaultFont().deriveFont(getDefaultFont().getSize() + 25f);
    }

    @Override
	public int getSplitPaneDividerSize() {
    	return new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT).getDividerSize();
    }
    
    @Override
	public JTable getTable() {
    	JTable table = new JTable();
    	table.setShowGrid(false);
    	return table;
    }
    
    @Override
	public void decorateTable(JTable table) {
    	table.setShowGrid(false);
    }
    
    @Override
	public JScrollPane getTableScrollPane(JTable table) {
    	return getScrollPane(table);
    }
    
    @Override
	public JScrollPane getTreeScrollPane(JTree tree) {
    	return getScrollPane(tree);
    }

    @Override
	public JList getList() {
    	return new JList();
    }
    
    @Override
	public JScrollPane getListScrollPane(JList list) {
    	return getScrollPane(list);
    }
    
    @Override
	public JScrollPane getScrollPane(Component component) {
    	return new JScrollPane(component);
    }

	/**
	 * @param osManager the osManager to set
	 */ 
    @Override
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public void setBaseFont(Font f) {
		this.baseFont = f;
	}
}
